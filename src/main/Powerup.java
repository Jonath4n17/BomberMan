package main;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;

public class Powerup {
	  private int xpos;
	  private int ypos;
	  private Rectangle rect;
	  private static Image[] imgs = {Toolkit.getDefaultToolkit().getImage("Pics/Other/powerup1.png"), 
	  Toolkit.getDefaultToolkit().getImage("Pics/Other/powerup2.png")};
	  private int type;
	  
	  public Powerup(Rectangle rect, int type) {
		  this.rect = rect;
		  this.xpos = (int) rect.getX();
		  this.ypos = (int) rect.getY();
		  this.type = type;
	  }
	  
	  //Getters
	  public int getXpos() {
		  return xpos;
  	  }
	  
	  public static Image[] getImgArr() {
		  return imgs;
	  }
	  
	  public int getYpos() {
		  return ypos;
	  }
	
	  public Rectangle getRect() {
		  return rect;
	  }
	  
	  public Image getImg() {
		  return imgs[type - 1];
	  }
  
	  public int getType() {
		  return type;
	  }
	  
}
