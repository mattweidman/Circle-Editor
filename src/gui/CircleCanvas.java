package gui;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.JPanel;

public class CircleCanvas extends JPanel implements MouseListener, MouseMotionListener {
	
	GUI gui;
	
	private Tool tool = Tool.PAN;
	
	private MouseEvent lastClick = null;
	
	private BufferedImage bgImage = null;
	private int translateX = 0;
	private int translateY = 0;
	private double scale = 1;
	
	private ArrayList<Circle> circles;
	
	private Circle circleDragged = null;
	private Circle circleResized = null;
	
	public CircleCanvas(GUI g) {
		super();
		addMouseListener(this);
		addMouseMotionListener(this);
		
		gui = g;
	}
	
	public ArrayList<Circle> getCircles() {
		return circles;
	}
	
	public void setPicture(BufferedImage image) {
		bgImage = image;
		scale = 1;
		translateX = 0;
		translateY = 0;
		repaint();
	}
	
	public void setCircles(ArrayList<Circle> circles) {
		this.circles = circles;
		repaint();
	}
	
	public void scaleImage(double imScale) {
		scale *= imScale;
		repaint();
	}
	
	public void setTool(Tool t) {
		tool = t;
	}
	
	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);
		Graphics2D g2d = (Graphics2D) g;

		int cx = gui.getWidth() / 2;
		int cy = gui.getHeight() / 2;
		int imageX = (int)(cx - (double)(cx - translateX)*scale);
		int imageY = (int)(cy - (double)(cy - translateY)*scale);
		
		if (bgImage != null) {
			g2d.drawImage(bgImage, imageX, imageY, 
					(int)(bgImage.getWidth()*scale), 
					(int)(bgImage.getHeight()*scale), null);
		}
		
		if (circles != null) {
			for (Circle circle : circles) {
				circle.draw(imageX, imageY, scale, g2d);
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent arg0) {
		if (tool == Tool.PAN || tool == Tool.MOVE || 
				tool == Tool.RESIZE || tool == Tool.ADD) {
			
			int x1 = lastClick.getX();
			int y1 = lastClick.getY();
			int x2 = arg0.getX();
			int y2 = arg0.getY();
			
			if (tool == Tool.PAN) {
				int dx = x2 - x1;
				int dy = y2 - y1;
				translateX += (int)((double)dx / scale);
				translateY += (int)((double)dy / scale);
				lastClick = arg0;
				repaint();
			}
			
			if (tool == Tool.MOVE && circleDragged != null) {
				int dx = x2 - x1;
				int dy = y2 - y1;
				circleDragged.moveTransformed(dx, dy, scale);
				lastClick = arg0;
				repaint();
			}
			
			if ((tool == Tool.RESIZE || tool == Tool.ADD) && circleResized != null) {
				int cx = gui.getWidth() / 2;
				int cy = gui.getHeight() / 2;
				int imageX = (int)(cx - (double)(cx - translateX)*scale);
				int imageY = (int)(cy - (double)(cy - translateY)*scale);
				int circleX = circleResized.getTransformedCenterX(imageX, scale);
				int circleY = circleResized.getTransformedCenterY(imageY, scale);
				int dx2 = x2 - circleX;
				int dy2 = y2 - circleY;
				double r2 = Math.sqrt(dx2*dx2 + dy2*dy2);
				circleResized.resize(r2, scale);
				repaint();
			}
		}
	}

	@Override
	public void mouseMoved(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseClicked(MouseEvent arg0) {
		if (tool == Tool.REMOVE) {
			
			int cx = gui.getWidth() / 2;
			int cy = gui.getHeight() / 2;
			int imageX = (int)(cx - (double)(cx - translateX)*scale);
			int imageY = (int)(cy - (double)(cy - translateY)*scale);
			
			for (Circle circle : circles) {
				if (circle.mouseContained(imageX, imageY, scale, arg0)) {
					circles.remove(circle);
					gui.refreshTitle(false);
					break;
				}
			}
			
			repaint();
		}
	}

	@Override
	public void mouseEntered(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mouseExited(MouseEvent arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void mousePressed(MouseEvent arg0) {
		lastClick = arg0;
		
		if (tool == Tool.MOVE || tool == Tool.RESIZE || tool == Tool.ADD) {
			
			int cx = gui.getWidth() / 2;
			int cy = gui.getHeight() / 2;
			int imageX = (int)(cx - (double)(cx - translateX)*scale);
			int imageY = (int)(cy - (double)(cy - translateY)*scale);
			
			if (tool == Tool.ADD) {
				int newX = (int) ((arg0.getX() - imageX) / scale);
				int newY = (int) ((arg0.getY() - imageY) / scale);
				circleResized = new Circle(newX, newY, 1);
				circles.add(circleResized);
				return;
			}
			
			for (Circle circle : circles) {
				if (circle.mouseContained(imageX, imageY, scale, arg0)) {
					if (tool == Tool.MOVE) circleDragged = circle;
					else if (tool == Tool.RESIZE) circleResized = circle;
					gui.refreshTitle(false);
					break;
				}
			}
		}
	}

	@Override
	public void mouseReleased(MouseEvent arg0) {
		circleDragged = null;
		circleResized = null;
	}

}
