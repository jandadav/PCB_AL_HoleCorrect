package com.dj;

import java.io.File;
import java.awt.geom.Point2D.Float;
import com.dj.readers.ExcellonDrill;
import com.dj.readers.GcMill;

public class AutoLevellerCorrect {

	public static void main(String[] args) throws Exception{

		// LOAD THE DRILL FILE *.DRL
		//ExcellonDrill drill = new ExcellonDrill(new File("C:\\Users\\David\\Documents\\MIDIBOX\\MBHP_DIO_ALTIUM\\GERB\\dupl\\cam.drl"));
		ExcellonDrill drill = new ExcellonDrill(new File("C:\\Users\\David\\Documents\\MIDIBOX\\STM32F4\\BREAKOUT_BOARD\\GERBER\\cam_44mm.drl"));
		
		// PROCESS THE MILL PROBE FILE
		//GcMill probe = new GcMill(new File("C:\\Users\\David\\Documents\\MIDIBOX\\MBHP_DIO_ALTIUM\\flatcam\\2017_01_prod\\ALGBOT"), drill, 39.75f);
		//GcMill probe = new GcMill(new File("C:\\Users\\David\\Documents\\MIDIBOX\\MBHP_DIO_ALTIUM\\flatcam\\2017_01_prod\\AL06_mill_bot2"), drill, 39.75f);
		GcMill probe = new GcMill(new File("C:\\Users\\David\\Documents\\MIDIBOX\\STM32F4\\BREAKOUT_BOARD\\FLATCAM\\ALtest_top_mill_al"), drill, null);
		//"C:\Users\David\Documents\MIDIBOX\STM32F4\BREAKOUT_BOARD\FLATCAM\ALtest_top_mill_al"
		Visualizer vis = new Visualizer();
		vis.addPointCloud(drill.getPointCloud());
		vis.addPointCloud(probe.getPointCloud());
		
		for(Float collisionPoint:probe.getCollisionCloud()){
			vis.addPoint(collisionPoint);
		}
		//vis.addPointCloud(probe.getCollisionCloud());
		
	}

}
