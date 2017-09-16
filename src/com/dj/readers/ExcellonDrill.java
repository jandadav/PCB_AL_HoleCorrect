package com.dj.readers;

import java.awt.geom.Point2D.Float;
import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

public class ExcellonDrill {
	private List<Float> pointCloud = new ArrayList<>();
	
	public ExcellonDrill(File file) throws IOException {
		
		
		InputStream is = new FileInputStream(file);
		BufferedReader reader = new BufferedReader(new InputStreamReader(is));
		boolean firstPoint = true;
		String line = null;
		Float lastPoint = null;
		int lineNo = 0;
		while( (line = reader.readLine()) != null ){
			lineNo++;
			
			
			if(line.startsWith("X")||line.startsWith("Y")) {
				String xCoord=null;
				String yCoord=null;
				int xIndex = line.indexOf("X");
				int yIndex = line.indexOf("Y");
				
				if(xIndex>-1){
					if(yIndex>-1){
						xCoord = line.substring(xIndex+1, yIndex);
					}else{
						xCoord = line.substring(xIndex+1, line.length());
					}
				}
				
				if(yIndex>-1){
					yCoord = line.substring(yIndex+1, line.length());
				}
				//System.out.println(line);
				
				//System.out.println(String.format("Line %1$d X:%2$-10s Y:%3$-10s", lineNo, xCoord , yCoord));
				
				int decimalPos = 4;
				if(xCoord!=null){
					xCoord = StringUtils.rightPad(xCoord, 8, "0").substring(0,decimalPos)+"."+StringUtils.rightPad(xCoord, 8, "0").substring(decimalPos,8);
				}
				if(yCoord!=null){
					yCoord = StringUtils.rightPad(yCoord, 8, "0").substring(0,decimalPos)+"."+StringUtils.rightPad(yCoord, 8, "0").substring(decimalPos,8);
				}
				
				//System.out.println(String.format("Line %1$d X:%2$-10s Y:%3$-10s", lineNo, xCoord , yCoord));
				
				
				
				java.lang.Float xCoordF = xCoord==null?null:java.lang.Float.valueOf(xCoord);
				java.lang.Float yCoordF = yCoord==null?null:java.lang.Float.valueOf(yCoord);
				
				if(firstPoint){
					lastPoint = new Float(xCoordF, yCoordF);
					firstPoint = false;
				}
				
				if(xCoordF==null){
					xCoordF = lastPoint.x;
				}
				if(yCoordF==null){
					yCoordF = lastPoint.y;
				}
				
				Float point = new Float(xCoordF, yCoordF);
				pointCloud.add(point);
				lastPoint = point;
				
				//System.out.println(point.toString());
				
				
			} else {
				//System.out.println("ignore");
			}
			
			
			
		}
		
		reader.close();
		
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
		System.out.println(String.format("Excellon parsed. Number of points: %1d. Bounds (%2f;%3f) to (%4f;%5f)", pointCloud.size(), xMin, yMin, xMax, yMax));
		
		
	}

	public List<Float> getPointCloud() {
		List<Float> pointCloudClone = new ArrayList<>();
		for(Float point:pointCloud){
			pointCloudClone.add(new Float(point.x, point.y));
		}
		return pointCloudClone;
	}
	
	public List<Float> getPointCloudXFlipped(float mirrorAxisXCoord) {
		List<Float> pointCloudClone = new ArrayList<>();
		for(Float point:pointCloud){
			pointCloudClone.add(new Float(mirrorAxisXCoord + (mirrorAxisXCoord - point.x), point.y));
		}
		return pointCloudClone;
	}
	/*
	public void offsetDrill(float offsetX, float offsetY){
		for(Float point:pointCloud){
			pointCloudClone.add(new Float(mirrorAxisXCoord + (mirrorAxisXCoord - point.x), point.y));
		}
	}*/
}
