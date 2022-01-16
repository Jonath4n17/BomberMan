package main;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;

public class Explosion implements Comparable <Explosion>{
	  private int xpos;
	  private int ypos;
	  private int gridNum;
	  private Rectangle rect;
	  private static Image img = Toolkit.getDefaultToolkit().getImage("Pics/Other/explosion.png");
	  private int timer = 80;
	  private boolean collision1Check = true;
	  
	  public Explosion(Rectangle rect) {
		  this.rect = rect;
		  this.xpos = (int) rect.getX();
		  this.ypos = (int) rect.getY();
		  this.gridNum = (xpos / 40) + 15 * (ypos / 40);
	  }
	  
	  public Explosion(int xpos, int ypos) {
		  this.xpos = xpos;
		  this.ypos = ypos;
		  this.gridNum = (xpos / 40) + 15 * (ypos / 40);
	  }
	  
	  //Getters
	  public boolean getCheck() {
		  return collision1Check;
	  }
	  
	  public int getXpos() {
		  return xpos;
	  }
	  
	  public int getYpos() {
		  return ypos;
	  }
	  
	  public int getTime() {
		  return timer;
	  }
	  
	  public Image getImg() {
		  return img;
	  }
	  
	  public Rectangle getRect() {
		  return rect;
	  }
	  //Setters
	  public void setRect (Rectangle rect) {
		  this.rect = rect;
	  }
	  
	  public void setCheck (boolean check) {
		  this.collision1Check = check;
	  }
	  
	  public void setTime(int time) {
		  this.timer = time;
	  }
	  
	  public void updateTimer() {
		  timer -= 1;
	  }
	  
	  public void setXpos(int xpos) {
		this.xpos = xpos;
	  }
	  
	  public void setYpos(int ypos) {
		 this.ypos = ypos;
	  }
	  
	  public int compareTo(Explosion expl) {
		  return this.gridNum - expl.gridNum;
	  }
}	

