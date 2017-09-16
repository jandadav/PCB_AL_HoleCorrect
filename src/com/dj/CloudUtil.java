package com.dj;

import java.awt.geom.Point2D.Float;
import java.util.List;

public class CloudUtil {
	public static float[] getBounds(List<Float> cloud){
		float xMin = 999999;
		float xMax = 0;
		float yMin = 999999;
		float yMax = 0;
		
		for(Float point:cloud){
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
		
		float[] result = {xMin, yMin, xMax, yMax};
		return result;
	}
	
	
	public static float[] addBounds(float[] bounds1, float[] bounds2){
		
		if(bounds1.length!=4 || bounds2.length!=4) throw new IllegalArgumentException("Not 4 element bounds array");
		
				
		float[] result = {Math.min(bounds1[0], bounds2[0]), Math.min(bounds1[1], bounds2[1]), Math.max(bounds1[2], bounds2[2]), Math.max(bounds1[3], bounds2[3])};
		return result;
	}
	
	public static float[] addBounds(float[] bounds1, Float point){
		
		if(bounds1.length!=4 || point ==null) throw new IllegalArgumentException("Not 4 element bounds array or empty");
		
				
		float[] result = {Math.min(bounds1[0], point.y), Math.min(bounds1[1], point.y), Math.max(bounds1[2], point.x), Math.max(bounds1[3], point.y)};
		return result;
	}
}
