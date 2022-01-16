package main;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;

public class Wall implements Comparable <Wall>{
	private Image[] imgs = new Image[2];
	private boolean canBreak;
	private int xpos;
	private int ypos;
	private int gridNum;
	private Rectangle rect;
	
	public Wall(boolean canBreak, Rectangle rect) {
		this.canBreak = canBreak;
		this.rect = rect;
		this.xpos = (int) rect.getX();
		this.ypos = (int) rect.getY();
		this.gridNum = (xpos / 40) + 15 * (ypos / 40);
		
	    imgs[0] = Toolkit.getDefaultToolkit().getImage("Pics/Walls/metal.png");
	    imgs[1] = Toolkit.getDefaultToolkit().getImage("Pics/Walls/wood.png");
	}
	
	public Wall(int xpos, int ypos) {
		  this.xpos = xpos;
		  this.ypos = ypos;
		  this.gridNum = (xpos / 40) + 15 * (ypos / 40);
	}
	
	//Getters
	public boolean getCanBreak() {
		return canBreak;
	}
	
	public int getXpos() {
		return xpos;
	}
	
	public int getYpos() {
		return ypos;
	}
	
	public int getGridNum() {
		return gridNum;
	}
	
	public Rectangle getRect() {
		return rect;
	}
	
	public Image getImg() {
		if (canBreak)
			return imgs[1];
		return imgs[0];
	}
	
	public String toString() {
		return "Pos: " + xpos + ", " + ypos;
	}
	
	public int compareTo(Wall wall) {
		return this.gridNum - wall.gridNum;
	}
	
	public Powerup drop() {
		int randomNum = (int) ((Math.random() * (11 - 1)) + 1);
		if (randomNum <= 4) {
			int powerupType = (int) ((Math.random() * (11-1)) + 1);
			if (powerupType <= 5) {
				return new Powerup(new Rectangle(xpos, ypos, 40, 40), 1);
			}
			else if (powerupType > 5) {
				return new Powerup(new Rectangle(xpos, ypos, 40, 40), 2);
			}
		}
		return null;
	}
	
}
