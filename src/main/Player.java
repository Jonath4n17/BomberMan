package main;

import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;

public class Player implements Comparable <Player>{
	private boolean invincible = false;
	private int flashTimer;
	private int speed = 4;
	private int wallsDestroyed = 0;
	private int xpos;
	private int ypos;
	private int strength = 1;
	private int numBombs = 1;
	private int numLives = 3;
	private int playerNum;
	private Rectangle rect;
	private Image[] imgs = new Image[4];
	private Image[] imgsFlash = new Image[4];
	private boolean alive = true;
	private int lastDir = 0;
	
	public Player(Rectangle rect, int playerNum) {
		this.playerNum = playerNum;
		this.rect = rect;
		this.xpos = (int) rect.getX();
		this.ypos = (int) rect.getY();
		
		if (playerNum == 1) {
			for (int i = 0 ; i < 4 ; i++ ) {
				imgs[i] = Toolkit.getDefaultToolkit().getImage("Pics/Player/p1_" + (i+1) + ".png");
				imgsFlash[i] = Toolkit.getDefaultToolkit().getImage("Pics/PlayerFlash/p1_" + (i+1) + ".png");
			}
		}
		else if (playerNum == 2) {
			for (int i = 0 ; i < 4 ; i++) {
				imgs[i] = Toolkit.getDefaultToolkit().getImage("Pics/Player/p2_" + (i+1) + ".png");
				imgsFlash[i] = Toolkit.getDefaultToolkit().getImage("Pics/PlayerFlash/p2_" + (i+1) + ".png");
			}
		}
	}
	
	//Getters
	public int getWallsDestroyed() {
		return wallsDestroyed;
	}
	
	public Image[] getImgArr() {
		return imgs;
	}
	
	public int getFlashTimer() {
		return flashTimer;
	}
	
	public int getLives() {
		return numLives;
	}
	
	public int getXpos() {
		return xpos;
	}
	
	public int getYpos() {
		return ypos;
	}
	
	public Rectangle getRect() {
		return rect;
	}
	
	public int getNumBombs() {
		return numBombs;
	}
	
	public boolean getAlive() {
		return alive;
	}
	
	public Image getImg() {
		if (invincible) {
			if (flashTimer % 20 <= 10) {
				return imgsFlash[lastDir];
			}
			else if (flashTimer % 20 > 10) {
				return imgs[lastDir];
			}
		}
		return imgs[lastDir];
	}
	
	public int getStrength() {
		return strength;
	}
	
	public boolean getInvincible() {
		return invincible;
	}
	
	//Setters
	public void setRect(Rectangle rect) {
		this.rect = rect;
	}
	
	public void setLives(int numLives) {
		this.numLives = numLives;
	}
	
	public void setStrength(int strength) {
		this.strength = strength;
	}
	
	public void setNumBombs(int numBombs) {
		this.numBombs = numBombs;
	}
	
	public void setAlive(boolean vitality) {
		this.alive = vitality;
	}
	
	public void setInvincible(boolean invincible) {
		this.invincible = invincible;
	}
	
	public void updateWallsDestroyed() {
		wallsDestroyed++;
	}
	
	public void flashTimer() {
		flashTimer--;
	}
	
	public void resetTimer() {
		flashTimer = 70;
	}
	
	public Bomb placeBomb() {
		int bombx = ((xpos + 20) / 40) * 40 + 5;
		int bomby = ((ypos + 20) / 40) * 40 + 5;
		return new Bomb(new Rectangle(bombx, bomby, 30, 30), strength, playerNum);
		
	}
	
	public void move(boolean left, boolean right, boolean up, boolean down) {
		if(left) {
			rect.x -= speed;
			lastDir = 3;
		}
		else if(right) {
			rect.x += speed;
			lastDir = 1;
		}
		if(up) {
			rect.y += -speed;
			lastDir = 0;
		}
		else if(down) {
			rect.y += speed;
			lastDir = 2;
		}
		xpos = (int) rect.getX();
		ypos = (int) rect.getY();
	}
	
	public void checkCollision(Rectangle wall) {
		//check if rect touches wall
		if(rect.intersects(wall)) {
			//stop the rect from moving
			double left1 = rect.getX();
			double right1 = rect.getX() + rect.getWidth();
			double top1 = rect.getY();
			double bottom1 = rect.getY() + rect.getHeight();
			double left2 = wall.getX();
			double right2 = wall.getX() + wall.getWidth();
			double top2 = wall.getY();
			double bottom2 = wall.getY() + wall.getHeight();
			
			if(right1 > left2 && 
			   left1 < left2 && 
			   right1 - left2 < bottom1 - top2 && 
			   right1 - left2 < bottom2 - top1)
	        {
	            //rect collides from left side of the wall
				rect.x = wall.x - rect.width;
	        }
	        else if(left1 < right2 &&
	        		right1 > right2 && 
	        		right2 - left1 < bottom1 - top2 && 
	        		right2 - left1 < bottom2 - top1)
	        {
	            //rect collides from right side of the wall
	        	rect.x = wall.x + wall.width;
	        }
	        else if(bottom1 > top2 && top1 < top2)
	        {
	            //rect collides from top side of the wall
	        	rect.y = wall.y - rect.height;
	        }
	        else if(top1 < bottom2 && bottom1 > bottom2)
	        {
	            //rect collides from bottom side of the wall
	        	rect.y = wall.y + wall.height;
	        }
			xpos = (int) rect.getX();
			ypos = (int) rect.getY();
		}
	}
	
	public boolean equals(Object o) {
		Player p = (Player) o;
		return this.playerNum == p.playerNum;
	}
	
	public int compareTo(Player p) {
		return this.playerNum - p.playerNum;
	}
	
	public String toString() {
		return "Player " + this.playerNum + ": ";
	}
}