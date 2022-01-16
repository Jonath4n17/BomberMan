package main;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GraphicsEnvironment;
import java.awt.Image;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.util.*;

import javax.sound.sampled.*;
import javax.swing.JFrame;
import javax.swing.JPanel;

public class Driver extends JPanel implements Runnable, KeyListener {
	//Sounds
	Clip powerupSound, powerupSound2, explosionSound, explosionSound2, hitSound, hitSound2;
	Clip placeSound, placeSound2;
	Clip background;
	//Player
	boolean up1, down1, left1, right1;
	boolean up2, down2, left2, right2;
	boolean place1, place2;
	Player p1, p2;
	int playerHeight = 29;
	int playerWidth = 29;
	
	//Basic Setup
    static JFrame frame;
	Graphics offScreenBuffer;
	Image offScreenImage;
	int FPS = 60;
	Thread thread;
	int screenWidth = 600;
	int screenHeight = 700;
	int[][] board = new int [15][15];
	Font customFont;
	Image heartFill, heartEmpty;
	
	int numLevels = 1;
	int selectedLevel;
	int numRows = 15;
	int numCols = 15;
	//Walls
    int wallWidth = 40;
    int wallHeight = 40;
    ArrayList <Wall> walls = new ArrayList <Wall>();
    //Players
    ArrayList <Player> players = new ArrayList <Player>();
    //Bombs
    ArrayList <Bomb> bombs = new ArrayList <Bomb>();
    int bombHeight = 30;
    int bombWidth = 30;
    Set <Explosion> explosions = new TreeSet<Explosion>();
    Iterator <Explosion> iter;
    ArrayList <Powerup> powerups = new ArrayList <Powerup>();
	
    //Score
	Map <Player, Integer> wallsDestroyed = new TreeMap<Player, Integer>();
    
	public Driver() {
		//sets up JPanel
		setPreferredSize(new Dimension(screenWidth, screenHeight));
		setVisible(true);
		
		//Starting the thread
		thread = new Thread(this);
		thread.start();
	}
	
	@Override
	public void run() {
		initialize();
		while(true) {
			this.repaint();
			update();
			deathCheck();			
			try {
				Thread.sleep(1000/FPS);
			}catch(Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void initialize() {
		readInLevels();
		p1 = new Player(new Rectangle(40, 40, playerWidth, playerHeight), 1);
		p2 = new Player(new Rectangle(520, 520, playerWidth, playerHeight), 2);
		heartFill = Toolkit.getDefaultToolkit().getImage("Pics/Other/heart1.png");
		heartEmpty = Toolkit.getDefaultToolkit().getImage("Pics/Other/heart2.png");
		AudioInputStream audioInputStream;
	    try {
	    	audioInputStream = AudioSystem.getAudioInputStream(new File("Sounds/background.wav").getAbsoluteFile());
	        background = AudioSystem.getClip();
	        background.open(audioInputStream);
	    	
			audioInputStream = AudioSystem.getAudioInputStream(new File("Sounds/powerup.wav").getAbsoluteFile());
	        powerupSound = AudioSystem.getClip();
	        powerupSound.open(audioInputStream);
	        powerupSound2 = AudioSystem.getClip();
	        powerupSound2.open(audioInputStream);
	        
	        audioInputStream = AudioSystem.getAudioInputStream(new File("Sounds/explosion.wav").getAbsoluteFile());
	        explosionSound = AudioSystem.getClip();
	        explosionSound.open(audioInputStream);
	        explosionSound2 = AudioSystem.getClip();
	        explosionSound2.open(audioInputStream);
	        
	        audioInputStream = AudioSystem.getAudioInputStream(new File("Sounds/hit.wav").getAbsoluteFile());
	        hitSound = AudioSystem.getClip();
	        hitSound.open(audioInputStream);
	        hitSound2 = AudioSystem.getClip();
	        hitSound2.open(audioInputStream);	 
	        
	        audioInputStream = AudioSystem.getAudioInputStream(new File("Sounds/place.wav").getAbsoluteFile());
	        placeSound = AudioSystem.getClip();
	        placeSound.open(audioInputStream);
	        placeSound2 = AudioSystem.getClip();
	        placeSound2.open(audioInputStream);
	        
		} catch (UnsupportedAudioFileException | IOException e1) {
			e1.printStackTrace();
		} catch (LineUnavailableException e) {
			e.printStackTrace();
		}
		background.loop(background.LOOP_CONTINUOUSLY);
		try {
		    //create the font to use. Specify the size!
		    customFont = Font.createFont(Font.TRUETYPE_FONT, new File("Fonts/bomberman.ttf")).deriveFont(12f);
		    customFont = customFont.deriveFont(36.0f);
		    GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
		    //register the font
		    ge.registerFont(customFont);
		} catch (IOException e) {
		    e.printStackTrace();
		} catch(FontFormatException e) {
		    e.printStackTrace();
		}
	}
	
	public void update() {
		p1.move(left1, right1, up1, down1);
		p2.move(left2, right2, up2, down2);
		for (int i = 0 ; i < walls.size() ; i++) {
			p1.checkCollision(walls.get(i).getRect());
			p2.checkCollision(walls.get(i).getRect());
		}
		for (int i = 0 ; i < powerups.size() ; i++) {
			if (p1.getRect().intersects(powerups.get(i).getRect())) {
				if (powerups.get(i).getType() == 1) {
					p1.setNumBombs(p1.getNumBombs() + 1);
				}
				else if (powerups.get(i).getType() == 2) {
					p1.setStrength(p1.getStrength() + 1);
				}
				playSound(powerupSound, powerupSound2);
				powerups.remove(i);
				continue;
			}
			if (p2.getRect().intersects(powerups.get(i).getRect())) {
				if (powerups.get(i).getType() == 1) {
					p2.setNumBombs(p2.getNumBombs() + 1);
				}
				else if (powerups.get(i).getType() == 2) {
					p2.setStrength(p2.getStrength() + 1);
				}
				playSound(powerupSound, powerupSound2);
				powerups.remove(i);
				continue;
			}
			
		}
		for (int i = 0; i<bombs.size(); i++) {
			if (bombs.get(i).getCheck()==false) {
				p1.checkCollision(bombs.get(i).getRect());
				p2.checkCollision(bombs.get(i).getRect());
			} else {
				if (p1.getRect().intersects(bombs.get(i).getRect()) == false && bombs.get(i).getPlayer() == 1) {
					bombs.get(i).setCheck(false);
				}
				if (p2.getRect().intersects(bombs.get(i).getRect()) == false && bombs.get(i).getPlayer() == 2) {
					bombs.get(i).setCheck(false);
				} 
			}
		}
		if (place1 && p1.getNumBombs() > 0) {
			Collections.sort(bombs);
			int index = Collections.binarySearch(bombs, new Bomb(((p1.getXpos() + 20) / 40) * 40 + 5, ((p1.getYpos() + 20) / 40) * 40 + 5));
			if (index < 0) {
				bombs.add(p1.placeBomb());
				playSound(placeSound, placeSound2);
				p1.setNumBombs(p1.getNumBombs() - 1);
			}
			Collections.sort(bombs);
			index = Collections.binarySearch(bombs, new Bomb(((p1.getXpos() + 20) / 40) * 40 + 5, ((p1.getYpos() + 20) / 40) * 40 + 5));
			bombs.get(index).northCheck(board);
			bombs.get(index).southCheck(board);
			bombs.get(index).westCheck(board);
			bombs.get(index).eastCheck(board);
		}
		if (place2 && p2.getNumBombs() > 0) {
			Collections.sort(bombs);
			int index = Collections.binarySearch(bombs, new Bomb(((p2.getXpos() + 20) / 40) * 40 + 5, ((p2.getYpos() + 20) / 40) * 40 + 5));
			if (index < 0) {
				bombs.add(p2.placeBomb());
				playSound(placeSound, placeSound2);
				p2.setNumBombs(p2.getNumBombs() - 1);
			}
			Collections.sort(bombs);
			index = Collections.binarySearch(bombs, new Bomb(((p2.getXpos() + 20) / 40) * 40 + 5, ((p2.getYpos() + 20) / 40) * 40 + 5));
			bombs.get(index).northCheck(board);
			bombs.get(index).southCheck(board);
			bombs.get(index).westCheck(board);
			bombs.get(index).eastCheck(board);
		}
		wallsDestroyed.put(p1, p1.getWallsDestroyed());
		wallsDestroyed.put(p2, p2.getWallsDestroyed());
	}
	
	public void paintComponent(Graphics g) {
		if (offScreenBuffer == null)
		{
		    offScreenImage = createImage (this.getWidth (), this.getHeight ());
		    offScreenBuffer = offScreenImage.getGraphics ();
		}
		offScreenBuffer.clearRect (0, 0, this.getWidth (), this.getHeight ());
		
		super.paintComponent(g);

		//white background
		offScreenBuffer.setColor(Color.GRAY);
		offScreenBuffer.fillRect(0, 0, screenWidth, screenHeight);
		
		//draw walls
		for (int i = 0 ; i < walls.size() ; i++) {
			offScreenBuffer.drawImage(walls.get(i).getImg(), walls.get(i).getXpos(), walls.get(i).getYpos(), this);
		}
		//draw powerups
		for (int i = 0 ; i < powerups.size() ; i++) {
			offScreenBuffer.drawImage(powerups.get(i).getImg(), powerups.get(i).getXpos(), powerups.get(i).getYpos(), this);
		}
		//draw bombs + adjust timers
		for (int i = 0; i < bombs.size() ; i++) {
			if (bombs.get(i).getTime() == 0) {
				createExplosions(bombs.get(i));
				
				playSound(explosionSound, explosionSound2);
				
				if (bombs.get(i).getPlayer() == 1)
					p1.setNumBombs(p1.getNumBombs() + 1);
				else if (bombs.get(i).getPlayer() == 2)
					p2.setNumBombs(p2.getNumBombs() + 1);
				bombs.remove(i);
				continue;
			}
			offScreenBuffer.drawImage(bombs.get(i).getImg(), bombs.get(i).getXpos(), bombs.get(i).getYpos(), this);
			drawIndicators(bombs.get(i));
			bombs.get(i).updateTimer();
		}
		//draw explosions + adjust timers
		iter = explosions.iterator();
		while (iter.hasNext()) {
			Explosion temp = iter.next();
			if (temp.getTime() == 0) {
				iter.remove();
				continue;
			}
			offScreenBuffer.drawImage(temp.getImg(), temp.getXpos(), temp.getYpos(), this);
			temp.updateTimer();
		}
//		for (int i = 0 ; i < explosions.size() ; i++) {
//			if (explosions.get(i).getTime() == 0) {
//				explosions.remove(i);
//				continue;
//			}
//			offScreenBuffer.drawImage(explosions.get(i).getImg(), explosions.get(i).getXpos(), explosions.get(i).getYpos(), this);
//			explosions.get(i).updateTimer();
//		}		
		
		if (p1.getFlashTimer() == 0 && p1.getInvincible()) {
			p1.setInvincible(false);
		}
		else {
			p1.flashTimer();
		}
		if (p2.getFlashTimer() == 0 && p2.getInvincible()) {
			p2.setInvincible(false);
		}
		else {
			p2.flashTimer();
		}
		
		if (p1.getAlive()==true) {
			offScreenBuffer.drawImage(p1.getImg(), p1.getXpos(), p1.getYpos(), this);
		}
		if (p2.getAlive()==true) {
			offScreenBuffer.drawImage(p2.getImg(), p2.getXpos(), p2.getYpos(), this);
		}
		
		//Draw bottom bar
		offScreenBuffer.setColor(Color.BLACK);
		offScreenBuffer.setFont(customFont); 
//		offScreenBuffer.drawString("P1", 30, 640);
		offScreenBuffer.drawImage(p1.getImgArr()[0], 30, 612, this);
		offScreenBuffer.drawImage(Powerup.getImgArr()[0], 90, 605, this);
		offScreenBuffer.drawString(": " + p1.getNumBombs(), 130, 640);
		offScreenBuffer.drawImage(Powerup.getImgArr()[1], 190, 605, this);
		offScreenBuffer.drawString(": " + p1.getStrength(), 230, 640);
		
		if (p1.getLives() >= 1)
			offScreenBuffer.drawImage(heartFill, 60, 650, this);
		else if (p1.getLives() == 0)
			offScreenBuffer.drawImage(heartEmpty, 60, 650, this);
		if (p1.getLives() >= 2)
			offScreenBuffer.drawImage(heartFill, 110, 650, this);
		else if (p1.getLives() < 2)
			offScreenBuffer.drawImage(heartEmpty, 110, 650, this);
		if (p1.getLives() == 3)
			offScreenBuffer.drawImage(heartFill, 160, 650, this);
		else if (p1.getLives() < 3)
			offScreenBuffer.drawImage(heartEmpty, 160, 650, this);
		
//		offScreenBuffer.drawString("P2", 330, 640);
		offScreenBuffer.drawImage(p2.getImgArr()[0], 330, 612, this);
		offScreenBuffer.drawImage(Powerup.getImgArr()[0], 390, 605, this);
		offScreenBuffer.drawString(": " + p2.getNumBombs(), 430, 640);
		offScreenBuffer.drawImage(Powerup.getImgArr()[1], 490, 605, this);
		offScreenBuffer.drawString(": " + p2.getStrength(), 530, 640);

		if (p2.getLives() >= 1)
			offScreenBuffer.drawImage(heartFill, 360, 650, this);
		else if (p2.getLives() == 0)
			offScreenBuffer.drawImage(heartEmpty, 360, 650, this);
		if (p2.getLives() >= 2)
			offScreenBuffer.drawImage(heartFill, 410, 650, this);
		else if (p2.getLives() < 2)
			offScreenBuffer.drawImage(heartEmpty, 410, 650, this);
		if (p2.getLives() == 3)
			offScreenBuffer.drawImage(heartFill, 460, 650, this);
		else if (p2.getLives() < 3)
			offScreenBuffer.drawImage(heartEmpty, 460, 650, this);
		
		g.drawImage(offScreenImage, 0, 0, this);
	}
	
	public void deathCheck() {
		if (!p1.getInvincible()) {
			iter = explosions.iterator();
			while(iter.hasNext()) {
				Explosion temp = iter.next();
				if (p1.getRect().intersects(temp.getRect())) {
					if (p1.getLives() > 1) {
					p1.setLives(p1.getLives()-1);
					p1.setInvincible(true);
					p1.resetTimer();
					playSound(hitSound, hitSound2);
					temp.getRect().setLocation(700,700);						
				} else if (p1.getLives() == 1) {
					p1.setLives(0);
					p1.setAlive(false);
				}
				}
			}
//			for (int i = 0; i < explosions.size(); i++) {
//				if (p1.getRect().intersects(explosions.get(i).getRect())) {
//					if (p1.getLives() > 1) {
//						p1.setLives(p1.getLives()-1);
//						p1.setInvincible(true);
//						p1.resetTimer();
//						playSound(hitSound, hitSound2);
//						explosions.get(i).getRect().setLocation(700,700);						
//					} else if (p1.getLives() == 1) {
//						p1.setLives(0);
//						p1.setAlive(false);
//					}
//				}
//			}
		}
		if (!p2.getInvincible()) {
			while(iter.hasNext()) {
				Explosion temp = iter.next();
				if (p2.getRect().intersects(temp.getRect())) {
					if (p2.getLives() > 1) {
					p2.setLives(p2.getLives()-1);
					p2.setInvincible(true);
					p2.resetTimer();
					playSound(hitSound, hitSound2);
					temp.getRect().setLocation(700,700);						
				} else if (p2.getLives() == 1) {
					p2.setLives(0);
					p2.setAlive(false);
				}
				}
			}
//			for (int i = 0; i < explosions.size(); i++) {
//				if (p2.getRect().intersects(explosions.get(i).getRect())) {
//					if (p2.getLives() > 1) {
//						p2.setLives(p2.getLives()-1);
//						p2.setInvincible(true);
//						p2.resetTimer();
//						playSound(hitSound, hitSound2);
//						explosions.get(i).getRect().setLocation(700,700);
//					} else if (p2.getLives() == 1) {
//						p2.setLives(0);
//						p2.setAlive(false);
//					}
//				}
//			}
		}
	}
	
	public void playSound(Clip sound, Clip sound2) {
		if (sound.isActive()) {
			sound2.setFramePosition(0);
			sound2.start();
		}
		else {
			sound.setFramePosition(0);
			sound.start();
		}
	}
	
	public void createExplosions(Bomb bmb) {
		int x;
		int y;
		int index;
		Powerup drop;
		Collections.sort(walls);
		//North
		for (int j = bmb.getGridNumX() - 1 ; j >= bmb.getTopCheck() ; j--) {
			x = bmb.getXpos() - 5;
			y = j * 40;
			index = Collections.binarySearch(walls, new Wall(x, y));
			if (index >= 0) {
				board[j][x/40] = 0;
				drop = walls.get(index).drop();
				if (drop != null) {
					powerups.add(drop);
				}
				if (bmb.getPlayer() == 1)
					p1.updateWallsDestroyed();
				else
					p2.updateWallsDestroyed();
				walls.remove(index);
			}
//			if (Collections.binarySearch(explosions, new Explosion(x, y)) < 0) {
				explosions.add(new Explosion(new Rectangle(x, y, 30, 30)));
//				Collections.sort(explosions);
//			}
		}
		//South
		for (int j  = bmb.getGridNumX() + 1 ; j <= bmb.getBottomCheck() ; j++) {
			x = bmb.getXpos() - 5;
			y = j * 40;
			index = Collections.binarySearch(walls, new Wall(x, y));
			if (index >= 0) {
				board[j][x/40] = 0;
				drop = walls.get(index).drop();
				if (drop != null) {
					powerups.add(drop);
				}
				if (bmb.getPlayer() == 1)
					p1.updateWallsDestroyed();
				else
					p2.updateWallsDestroyed();
				walls.remove(index);
			}
//			if (Collections.binarySearch(explosions, new Explosion(x, y)) < 0) {
				explosions.add(new Explosion(new Rectangle(x, y, 30, 30)));
//				Collections.sort(explosions);
//			}
		}
		// West
		for (int j = bmb.getGridNumY() - 1 ; j >= bmb.getLeftCheck() ; j--) {
			x = j * 40;
			y = bmb.getYpos() - 5;
			index = Collections.binarySearch(walls, new Wall(x, y));
			if (index >= 0) {
				board[y/40][j] = 0;
				drop = walls.get(index).drop();
				if (drop != null) {
					powerups.add(drop);
				}
				if (bmb.getPlayer() == 1)
					p1.updateWallsDestroyed();
				else
					p2.updateWallsDestroyed();
				walls.remove(index);
			}
//			if (Collections.binarySearch(explosions, new Explosion(x, y)) < 0) {
				explosions.add(new Explosion(new Rectangle(x, y, 30, 30)));
//				Collections.sort(explosions);
//			}
		}
		//East
		for (int j = bmb.getGridNumY() + 1 ; j <= bmb.getRightCheck() ; j++) {
			x = j * 40;
			y = bmb.getYpos() - 5;
			index = Collections.binarySearch(walls, new Wall(x, y));
			if (index >= 0) {
				board[y/40][j] = 0;
				drop = walls.get(index).drop();
				if (drop != null) {
					powerups.add(drop);
				}
				if (bmb.getPlayer() == 1)
					p1.updateWallsDestroyed();
				else
					p2.updateWallsDestroyed();
				walls.remove(index);
			}
//			if (Collections.binarySearch(explosions, new Explosion(x, y)) < 0) {
				explosions.add(new Explosion(new Rectangle(x, y, 30, 30)));
//				Collections.sort(explosions);
//			}
		}
//		if (Collections.binarySearch(explosions, new Explosion(bmb.getXpos() - 5, bmb.getYpos() - 5)) < 0) {
			explosions.add(new Explosion(new Rectangle(bmb.getXpos() - 5, bmb.getYpos() - 5, 30, 30)));
//		}
	}
	
	public void drawIndicators(Bomb bmb) {
        //North
        for (int j = bmb.getGridNumX() - 1 ; j >= bmb.getTopCheck() ; j--) {
            int x = bmb.getXpos() - 5;
            int y = j * 40;
            offScreenBuffer.drawImage(bmb.getIndicator(), x, y, this);
        }
        //South
        for (int j  = bmb.getGridNumX() + 1 ; j <= bmb.getBottomCheck() ; j++) {
            int x = bmb.getXpos() - 5;
            int y = j * 40;
            offScreenBuffer.drawImage(bmb.getIndicator(), x, y, this);
        }
        // West
        for (int j = bmb.getGridNumY() - 1 ; j >= bmb.getLeftCheck() ; j--) {
            int x = j * 40;
            int y = bmb.getYpos() - 5;
            offScreenBuffer.drawImage(bmb.getIndicator(), x, y, this);
        }
        //East
        for (int j = bmb.getGridNumY() + 1 ; j <= bmb.getRightCheck() ; j++) {
            int x = j * 40;
            int y = bmb.getYpos() - 5;
            offScreenBuffer.drawImage(bmb.getIndicator(), x, y, this);
        }
        offScreenBuffer.drawImage(bmb.getIndicator(), bmb.getXpos() - 5, bmb.getYpos() - 5, this);
    }
	
	public void readInLevels() {
		String line;
		int x = 0;
		for (int i = 1 ; i < numLevels + 1 ; i++) {
			try {
				BufferedReader inFile = new BufferedReader(new FileReader("Levels/L" + i + ".txt"));
				line = "";
				while (line != null) {
					line = inFile.readLine();
					if (line == null)
						break;
					for (int y = 0 ; y < 15 ; y++) {
						
						int wallType = Integer.parseInt(line.substring(y, y + 1));
						if (wallType == 1) {
							board[x][y] = 1;
							walls.add(new Wall(false, new Rectangle(y * wallWidth, x * wallHeight, wallWidth, wallHeight)));
						}
						else if (wallType == 2) {
							board[x][y] = 2;
							walls.add(new Wall(true, new Rectangle(y * wallWidth, x * wallHeight, wallWidth, wallHeight)));
						} else {
							board[x][y] = 0;
						}
					}
					x++;
				}
				inFile.close();
			}
			catch (FileNotFoundException e) {
				System.out.println("File not found");
			}
			catch (IOException e) {
			}
		}
	}
	
	@Override
	public void keyTyped(KeyEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void keyPressed(KeyEvent e) {
		int key = e.getKeyCode();
		//P1
		if(key == KeyEvent.VK_A) {
			left1 = true;
			right1 = false;
		}else if(key == KeyEvent.VK_D) {
			right1 = true;
			left1 = false;
		}else if(key == KeyEvent.VK_W) {
			up1 = true;
			down1 = false;
		}else if(key == KeyEvent.VK_S) {
			down1 = true;
			up1 = false;
		}
		else if (key == KeyEvent.VK_SPACE) {
			place1 = true;
		}
		//P2
		else if (key == KeyEvent.VK_LEFT) {
			left2 = true;
			right2 = false;
		}
		else if (key == KeyEvent.VK_RIGHT) {
			right2 = true;
			left2 = false;
		}
		else if (key == KeyEvent.VK_UP) {
			up2 = true;
			down2 = false;
		}
		else if (key == KeyEvent.VK_DOWN) {
			down2 = true;
			up2 = false;
		}
		else if (key == KeyEvent.VK_SLASH) {
			place2 = true;
		}
	}

	@Override
	public void keyReleased(KeyEvent e) {
		int key = e.getKeyCode();
		//P1
		if(key == KeyEvent.VK_A) {
			left1 = false;
		}else if(key == KeyEvent.VK_D) {
			right1 = false;
		}else if(key == KeyEvent.VK_W) {
			up1 = false;
		}else if(key == KeyEvent.VK_S) {
			down1 = false;
		}
		else if (key == KeyEvent.VK_SPACE) {
			place1 = false;
		}
		//P2
		else if (key == KeyEvent.VK_LEFT) {
			left2 = false;
		}
		else if (key == KeyEvent.VK_RIGHT) {
			right2 = false;
		}
		else if (key == KeyEvent.VK_UP) {
			up2 = false;
		}
		else if (key == KeyEvent.VK_DOWN) {
			down2 = false;	
		}
		else if (key == KeyEvent.VK_SLASH) {
			place2 = false;
		}
	}
	
	
	
	public static void main(String[] args) {
		JFrame frame = new JFrame ("BomberMan");
		Driver myPanel = new Driver ();
		frame.add(myPanel);
		frame.addKeyListener(myPanel);
		frame.setVisible(true);
		frame.pack();
		frame.setLocationRelativeTo(null);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setResizable(false);
	}


}