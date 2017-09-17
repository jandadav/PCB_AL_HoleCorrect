package com.dj;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Point2D.Float;
import java.util.ArrayList;
import java.util.List;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JWindow;

public class Visualizer {
	private JFrame frame = new JFrame();
	
	private List<Float> privatePointCloud = new ArrayList<>();
	private List<List<Float>> pointCloudList = new ArrayList<List<Float>>();
	private List<Color> colorList = new ArrayList<>();
	private MyPanel panel = new MyPanel();
	private String coord = "";
	
	float[] bounds = {999999f, 999999f, -99999f, -99999f};
	
	public Visualizer() {
		//frame.setExtendedState(JFrame.MAXIMIZED_BOTH); 
		//frame.setUndecorated(true);
		frame.setVisible(true);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setBounds(200, 100, 1000, 800);
		frame.add(panel);
		
		
		colorList.add(new Color(255,105,0));
		colorList.add(new Color(64,196,255));
		colorList.add(new Color(213,0,0));
		colorList.add(new Color(224,224,224));
		colorList.add(new Color(85,139,47));
		
		
		
	};
	
	
	public void addPoint(Float point) {
		this.privatePointCloud.add(point);
		bounds = CloudUtil.addBounds(bounds, point);
		
		//panel.zoom = Math.min(bounds[2]/panel.getWidth(), panel.getHeight()/bounds[3]);
		panel.zoom = Math.min(panel.getWidth()/((bounds[2]*10)+20),panel.getHeight()/((bounds[3]*10)+20));
		panel.repaint();
	};
	
	public void addPointCloud(List<Float> pointCloud) {
		this.pointCloudList.add(pointCloud);
		bounds = CloudUtil.addBounds(bounds, CloudUtil.getBounds(pointCloud));
		
		//panel.zoom = Math.min(panel.getWidth()/bounds[2], panel.getHeight()/bounds[3]);
		panel.zoom = Math.min(panel.getWidth()/((bounds[2]*10)+20),panel.getHeight()/((bounds[3]*10)+20));
		panel.repaint();
	};

	
	public void clear(){
		
	};
	
	
	private class MyPanel extends JPanel {
		
		private double zoom = 1d;
		
		public MyPanel() {
			setBackground(new Color(50, 050, 52));
			setBounds(0, 0, 50, 50);
			
			
			
			addMouseWheelListener(new MouseWheelListener() {
				
				@Override
				public void mouseWheelMoved(MouseWheelEvent e) {
					if (e.getPreciseWheelRotation() < 0) {
                        zoom -= 0.1;
                    } else {
                        zoom += 0.1;
                    }
//                  zoom += e.getPreciseWheelRotation();
                    if (zoom < 0.01) {
                        zoom = 0.01;
                    }

                    repaint();
					
				}
			});
			
			addMouseMotionListener(new MouseMotionListener() {
				
				@Override
				public void mouseMoved(MouseEvent e) {
					/*e.getX();
					e.getY();*/
					coord = "X:"+String.valueOf(e.getX())+" Y:"+String.valueOf(e.getY());
					//repaint();
				}
				
				@Override
				public void mouseDragged(MouseEvent e) {
					repaint();
					
				}
			} 
			);
		}
		
		public void paintComponent(Graphics g) {
			super.paintComponent(g);

			Graphics2D g2d = (Graphics2D) g.create();
			RenderingHints rh = new RenderingHints(
		             RenderingHints.KEY_ANTIALIASING,
		             RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHints(rh);
		    
			AffineTransform at = new AffineTransform().getTranslateInstance(10, this.getHeight()-10);
            //at.translate(anchorx, anchory);
			
			
			
            at.scale(zoom, -zoom);
            
            
            
            
            g2d.setTransform(at);
            
            
          //DRAW GRID
			g2d.drawRect(Math.round(bounds[0]*10), Math.round(bounds[1]*10), Math.round(bounds[2]*10), Math.round(bounds[3]*10));
			int step = 100; // 10 mm grid
			g2d.setColor(new Color(60,60,60));
			//x
			int x = 0;
			while(x<bounds[2]*10) {
				g2d.drawLine(x, 0, x, Math.round(bounds[3]*10));
				x+=step;
			}
			//y
			int y = 0;
			while(y<bounds[3]*10) {
				g2d.drawLine(0, y, Math.round(bounds[2]*10), y);
				y+=step;
			}
			step = 500; // 10 mm grid
			g2d.setColor(new Color(160,160,160));
			//x
			x = 0;
			while(x<bounds[2]*10) {
				g2d.drawLine(x, 0, x, Math.round(bounds[3]*10));
				x+=step;
			}
			//y
			y = 0;
			while(y<bounds[3]*10) {
				g2d.drawLine(0, y, Math.round(bounds[2]*10), y);
				y+=step;
			}

			//DRAW HOLES
			int radius = 20;
			
			g2d.setColor(new Color(255,0,0));
			for(Float point:privatePointCloud){
				//g.fillRect(Math.round(point.x*10), Math.round(point.y*10), 5, 5);
				g2d.fillOval(Math.round(point.x*10)-radius, Math.round(point.y*10)-radius, 2*radius, 2*radius);
			}
			radius = 5;
			int index = 0; 
			for(List<Float> list:pointCloudList){
				g2d.setColor(colorList.get(index));
				for(Float point:list){
					//g.fillRect(Math.round(point.x*10), Math.round(point.y*10), 5, 5);
					g2d.fillOval(Math.round(point.x*10)-radius, Math.round(point.y*10)-radius, 2*radius, 2*radius);
				}
				index++;
			}
			
			//g2d.dispose();
			
			/*Graphics2D g2te = (Graphics2D) g.create();
			g2te.setColor(new Color(255,255,255));
			g2te.drawString(coord, 50, 50);*/
			
			

		}
		
		
		
	}
}
