package com.dj.readers;

import java.awt.geom.Point2D.Float;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.swing.JFrame;

import org.apache.commons.lang3.StringUtils;

public class GcMill {

	private Set<Float> pointCloud = new HashSet<>();
	
	List<Float> collisionCloud = new ArrayList<>();
	
	private float minDistance = 0.7f;
	
	public GcMill(File file, ExcellonDrill drill, java.lang.Float mirrorAxisXCoord) throws IOException {
		InputStream is = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		
		List<Float> drillCloud = null;
		if(mirrorAxisXCoord !=null) {
			drillCloud = drill.getPointCloudXFlipped(mirrorAxisXCoord);
			System.out.println("Working on flipped drill file with board flip axis at X: "+mirrorAxisXCoord);
		} else {
			drillCloud = drill.getPointCloud();
			System.out.println("Working on the same side as drill file drilled from");
		}
		
		/*
		System.out.println("DRILLCLOUD");
		for(Float point:drillCloud){
			System.out.println(String.format("%1f %2f", point.x, point.y));
		}
		System.out.println("/DRILLCLOUD");
		*/
		
		String fileWriteName = file.getAbsolutePath()+"cor";
		FileWriter writer = new FileWriter(fileWriteName);
		
		boolean firstPoint = true;
		String line = null;
		Float lastPoint = null;
		int lineNo = 0;
		int counter = 0;
		boolean probeBlock = false;
		StringBuilder messageLog = new StringBuilder("ActionReport\n");
		while( (line = reader.readLine()) != null ){
			lineNo++;
			
			if(line.equals("(begin initial probe and set Z to 0)")){
				probeBlock = true;
			}
			
			if(line.equals("(Set S value to ensure Speed has a value otherwise the spindle will not start on an M3 command)")){
				probeBlock = false;
			}
			
			if(probeBlock){
				if(line.startsWith("G0 X")){
					counter++;
					//System.out.println(String.format("%1$03d %2$s", lineNo, line));
					
					String xCoord = null;
					String yCoord = null;
					
					
					xCoord = StringUtils.substringBetween(line, "X", "Y").trim();
					
					if(line.contains("Z")){
						yCoord = StringUtils.substringBetween(line, "Y", "Z").trim();
					} else {
						yCoord = line.substring(line.indexOf("Y")+1, line.length());
					}
					
					
					
					Float point = new Float(java.lang.Float.valueOf(xCoord), java.lang.Float.valueOf(yCoord));
					
					//System.out.println(String.format("%1d. %2s", counter, point.toString()));
										
					
					List<Float> collisions = checkCollisions(point, drillCloud, minDistance);
					
					if(!collisions.isEmpty()){
						messageLog.append(String.format("Probe %1s on line %2d collision with points %3s\n", point, lineNo, collisions.toString()));
						
						Float originalPoint = new Float(point.x, point.y);
						
						//8-direction points around the original point, checked for collision
						
						
						for (int phi=0; phi<360; phi+=15) {
							float dist = 0.9f;
							
							Float testPoint = new Float();
							testPoint.x = originalPoint.x +  (dist* ((float)Math.cos(Math.toRadians(phi))));
							testPoint.y = originalPoint.y +  (dist* ((float)Math.sin(Math.toRadians(phi))));
									//new Float(originalPoint.x + (dist*Math.sin(phi)), originalPoint.y+ (dist*Math.cos(phi)));
							
							messageLog.append("phi: "+phi+" deltaX: "+ dist* ((float)Math.cos(Math.toRadians(phi)))+" deltaY: "+dist* ((float)Math.sin(Math.toRadians(phi)))+"\n");
							
							List<Float> collisions2 = checkCollisions(testPoint, drillCloud, minDistance);
							if(collisions2.isEmpty()){
								point = testPoint;
								break;
							}
						}
						
						messageLog.append(lineNo).append(": ").append(line).append(" > ");

						line = StringUtils.replace(line, "X"+xCoord, "X"+String.valueOf(point.x));
						line = StringUtils.replace(line, "Y"+yCoord, "Y"+String.valueOf(point.y));
						
						float movedDist = (float) originalPoint.distance(point);
						messageLog.append(line);
						if(movedDist>1.2f){
							messageLog.append(" DISTANCE TOO LARGE: ").append(movedDist).append("\n");
						} else {
							messageLog.append(" Distance: ").append(movedDist).append("\n");
						}
					}
					
					
					pointCloud.add(point);
					
				}
			}
			
			writer.write(line);
			writer.write("\r\n");
		}
		
		
		reader.close();
		writer.close();
		
		float xMin = 999999;
		float xMax = 0;
		float yMin = 999999;
		float yMax = 0;
		
		for(Float point:pointCloud){
			if(point.x > xMax){
				xMax = point.x;
			}
			if(point.x < xMin){
				xMin = point.x;
			}
			if(point.y > yMax){
				yMax = point.y;
			}
			if(point.y < yMin){
				yMin = point.y;
			}
		}
		System.out.println(String.format("G-Code probe parsed. Number of points: %1d. Bounds (%2f;%3f) to (%4f;%5f)", pointCloud.size(), xMin, yMin, xMax, yMax));

		messageLog.append("\nCOLLISIONS AFTER SOLVER:\n");
		
		for(Float point:pointCloud){
			
			List<Float> collisions = checkCollisions(point, drillCloud, minDistance);
			if(!collisions.isEmpty()){
				messageLog.append(String.format("ERROR: %1s collision with %2s\n", point, collisions.toString()));
				collisionCloud.add(point);
			}
		}
		messageLog.append("END COLLISIONS AFTER SOLVER:\n");
		
		System.out.println(messageLog.toString());
		
		/*
		System.out.println("POINTCLOUD");
		for(Float point:pointCloud){
			System.out.println(String.format("%1f %2f", point.x, point.y));
		}
		System.out.println("/POINTCLOUD");
		*/
	}
	
	private List<Float> checkCollisions(Float point, List<Float> cloud, java.lang.Float distTolerance){
		Set<Float> result = new HashSet<>(); 
		for(Float cloudPoint: cloud) {
			double dist = point.distance(cloudPoint);
			if(dist<distTolerance){
				result.add(cloudPoint);
			}
		}
		return new ArrayList(result);
	}
	
	public List<Float> getPointCloud() {
		List<Float> pointCloudClone = new ArrayList<>();
		for(Float point:pointCloud){
			pointCloudClone.add(new Float(point.x, point.y));
		}
		return pointCloudClone;
	}
	
	public List<Float> getCollisionCloud() {
		List<Float> collisionCloudClone = new ArrayList<>();
		for(Float point:collisionCloud){
			collisionCloudClone.add(new Float(point.x, point.y));
		}
		return collisionCloudClone;
	}
}
