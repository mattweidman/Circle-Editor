package gui;

import java.awt.Graphics2D;
import java.awt.event.MouseEvent;

public class Circle {
	
	double cx, cy, r;
	
	private static final int CENTER_SIZE = 4;
	
	public Circle(int x, int y, int r) {
		cx = x;
		cy = y;
		this.r = r;
	}
	
	public void draw(int imageX, int imageY, double scale, Graphics2D g2d) {
		g2d.drawOval(imageX + (int)((cx - r)*scale), 
				imageY + (int)((cy - r)*scale), 
				(int)(r*2*scale), 
				(int)(r*2*scale));
		
		g2d.fillOval(imageX + (int)((cx - CENTER_SIZE)*scale), 
				imageY + (int)((cy - CENTER_SIZE)*scale), 
				(int)(CENTER_SIZE*2*scale), 
				(int)(CENTER_SIZE*2*scale));
	}
	
	public double getCenterX() {
		return cx;
	}
	
	public int getTransformedCenterX(int imageX, double scale) {
		return imageX + (int) (cx*scale);
	}
	
	public double getCenterY() {
		return cy;
	}
	
	public int getTransformedCenterY(int imageY, double scale) {
		return imageY + (int) (cy*scale);
	}
	
	public double getRadius() {
		return r;
	}
	
	public int getTransformedRadius(double scale) {
		return (int)(r*scale);
	}
	
	public boolean mouseContained(int imageX, int imageY, double scale, MouseEvent me) {
		int tx = getTransformedCenterX(imageX, scale);
		int ty = getTransformedCenterY(imageY, scale);
		int tr = getTransformedRadius(scale);
		int dx = me.getX() - tx;
		int dy = me.getY() - ty;
		int distSquared = dx*dx + dy*dy;
		return distSquared < tr*tr;
	}
	
	public void move(double dx, double dy) {
		cx += dx;
		cy += dy;
	}
	
	public void moveTransformed(int dx, int dy, double scale) {
		move((double)dx / scale, (double)dy / scale);
	}
	
	public void resize(double newR, double scale) {
		r = Math.abs(newR / scale);
	}

}
