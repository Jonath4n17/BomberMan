package main;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;

public class Bomb implements Comparable <Bomb>{
	  private int player;
	  private int xpos,ypos;
	  private int gridNum, gridNumX, gridNumY;
	  private Rectangle rect;
	  private int strength;
	  private int speed;
	  private int timer = 150;
	  private Image[] imgs = new Image[4];
	  private Image indicator;
	  private boolean collision1Check = true;
	  private int topCheck, bottomCheck, leftCheck, rightCheck;

	  public Bomb (Rectangle rect, int strength, int player) {
		this.player = player;
	    this.rect = rect;
		this.xpos = (int) rect.getX();
	    this.ypos = (int) rect.getY();
	    this.gridNum = (xpos / 40) + 15 * (ypos / 40);
	    this.strength = strength;
	    this.gridNumX = ypos/40;
	    this.gridNumY = xpos/40;
	    
	    for (int i = 0 ; i < imgs.length ; i++) {
	    	imgs[i] = Toolkit.getDefaultToolkit().getImage("Pics/Bombs/" + (i+1) + ".png");
	    }
	    indicator = Toolkit.getDefaultToolkit().getImage("Pics/Other/redIndicator.png");
	  }
	  
	  public Bomb(int xpos, int ypos) {
		  this.xpos = xpos;
		  this.ypos = ypos;
		  this.gridNum = (xpos / 40) + 15 * (ypos / 40);
	  }
	  
	  public void westCheck(int [][] board) {
		  for (int i = 1; i<=strength; i++) {
			  if (board[gridNumX][gridNumY-i] != 0) {
				  if (board[gridNumX][gridNumY-i] == 1) {
					  leftCheck = gridNumY-i+1;
					  break;
				  } else if (board[gridNumX][gridNumY-i] == 2) {
					  leftCheck = (gridNumY-i);
					  break;
				  }
			  } else {
				  leftCheck = (gridNumY-i);
			  }
		  }
	  }
	  
	  public void eastCheck(int [][] board) {
		  for (int i = 1; i <=strength; i++) {
			  if (board[gridNumX][gridNumY+i] != 0) {
				  if (board[gridNumX][gridNumY+i] == 1) {
					  rightCheck = gridNumY+i-1;
					  break;
				  } else if (board[gridNumX][gridNumY+i] == 2) {
					  rightCheck = (gridNumY+i);
					  break;
				  }
			  } else {
				  rightCheck = (gridNumY+i);
			  }
		  }
	  }
	  
	  public void northCheck(int [][] board) {
		  for (int i = 1; i<=strength; i++) {
			  
			  if (board[gridNumX-i][gridNumY] != 0) {
				  if (board[gridNumX-i][gridNumY] == 1) {
					  topCheck = gridNumX-i+1;
					  break;
				  } else if (board[gridNumX-i][gridNumY] == 2) {
					  topCheck = (gridNumX-i);
					  break;
				  }
			  } else {
				  topCheck = (gridNumX-i);
			  }
		  }
	  }
	  
	  public void southCheck(int [][] board) {
		  for (int i = 1; i<=strength; i++) {
			  if (board[gridNumX+i][gridNumY] != 0) {
				  if (board[gridNumX+i][gridNumY] == 1) {
					  bottomCheck = gridNumX+i-1;
					  break;
				  } else if (board[gridNumX+i][gridNumY] == 2) {
					  bottomCheck = (gridNumX+i);
					  break;
				  } 
			  } else {
				  bottomCheck = (gridNumX+i);
			  }
		  }
	  }
	  
	  //Getters
	  public int getPlayer() {
		  return player;
	  }
	  
	  public int getGridNumX() {
		  return gridNumX;
	  }
	  
	  public int getGridNumY() {
		  return gridNumY;
	  }
	  
	  public int getTopCheck() {
		  return topCheck;
	  }
	  
	  public int getBottomCheck() {
		  return bottomCheck;
	  }
	  
	  public int getLeftCheck() {
		  return leftCheck;
	  }
	  
	  public int getRightCheck() {
		  return rightCheck;
	  }
	  
	  public boolean getCheck() {
		  return collision1Check;
	  }
	  
	  public Rectangle getRect() {

		  return rect;
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
		  if (timer > 112)
			  return imgs[0];
		  else if (timer > 74)
			  return imgs[1];
		  else if (timer > 36)
			  return imgs[2];
		  return imgs[3];
	  }
	  
	  public Image getIndicator() {
		  return indicator;
	  }
		
	  //Setters
	  public void setCheck (boolean check) {
		  this.collision1Check = check;
	  }
	  public void setTime(int time) {
		  this.timer = time;
	  }
	  
	  public void updateTimer() {
		  timer -= 1;
	  }

	  public int compareTo(Bomb bmb) {
		  return this.gridNum - bmb.gridNum;
	  }
	}