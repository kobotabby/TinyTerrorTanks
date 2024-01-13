/**
 * @author Ethan Gan
 * Computer Science
 * 1/23/2023
 * Player class extends Tank class and is the tank the player controls in the TankRunner game.
 */
package entity;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import handlers.KeyHandler;
import handlers.MouseHandler;
import main.GameFrame;

public class Player extends Tank  {	
	public int speed = 48; // speed normally 4
	private int health; 
	private int damage = 100; 
	private int centerX; 
	private int centerY; 
	private int width;
	private int height;	
	private int turretAngle;
	private int shotDelay = 4;	//normally 4
	private int shotTimer = 0;
	private double drawInterval = 1000000000/12; // in seconds nanoseconds divided by needed frames per second
	private double delta = 0;
	private long lastTime = System.nanoTime();
	private long currentTime;
	private int timer = 0;
	private Color tankColor = Color.YELLOW;
	private Color turretColor = Color.BLACK;
	private int turretLength = 20;
	private int turretMargin = 10; // shrinks turret radius
	private GameFrame frame;
	
	public Player(int x, int y, int width, int height, GameFrame g) {
		super(x, y, width, height, g);
		this.setColor(tankColor); 
		super.speed = speed;
		this.width = width;
		this.height = height;
		this.setDamage(10);
		frame = g;
	}	
	
	/** paint() method paints the tank's graphics by overriding the game object paint method to include the tank turret */
	public void paint(Graphics g) {
		Rectangle r = getBounds();
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(c);
		g2.fillRect(0, 0, (int)r.getWidth(), (int)r.getHeight());
		
		g2.setColor(turretColor);
		g2.fillOval(0+turretMargin, 0+turretMargin, (int)r.getWidth()-turretMargin*2, (int)r.getHeight()-turretMargin*2);
		int turretEndX = (int)r.getWidth()/2 + (int) ((turretLength * (float) Math.sin(Math.toRadians(this.turretAngle))) + .5);
		int turretEndY = (int)r.getHeight()/2 + (int) ((turretLength * (float) Math.cos(Math.toRadians(this.turretAngle))) + .5);
		g2.setStroke(new BasicStroke(10));
		g2.drawLine((int)r.getWidth()/2, (int)r.getHeight()/2, turretEndX, turretEndY);
	}
	
	/** act() method inherited from the GameObject class runs every game update */
	@Override
	public void act() {
		// update turret angle based on the current mouse position
		this.turretAngle = (int)getAngleTo(this.centerX, this.centerY, frame.mouseH.mouseX, frame.mouseH.mouseY);
		// update shot timer
		if (shotTimer > 0) {
//			System.out.println("Shot timer: " + shotTimer);
			shotTimer--;
		}
		
		// update center position variables
		this.centerX = getX() + this.width/2;
		this.centerY = getY() + this.height/2;
		
		// update tank color after getting hit using the delta method
		currentTime = System.nanoTime();
		delta += (currentTime - lastTime) / drawInterval; // how much time left until the next draw interval 
		timer += (currentTime - lastTime);
		lastTime = currentTime;
		// if delta is 1 update and draw to screen
		if(delta >= 1) {
			this.setColor(tankColor);
			delta--;
		}
		if(timer >= 1000000000) {
			timer = 0;
		}
	}
	
	/** 
	 * Method gets angle from a start to end point
	 * pre: the x and y values of two different points
	 * post: an angle returned
	 */
	private double getAngleTo(int startX, int startY, int endX, int endY) {
		double angle = (Math.atan2(endX - startX, endY - startY) * 180) / Math.PI;
		return angle;
	}
	
	/** 
	 * Method reduces player health
	 * pre: damage > 0
	 * post: player's health decreased
	 */
	// override tank function with new player health value
	public void reduceHealth(int damage) {
		this.health -= damage;
	}

	/** 
	 * Method checks if the player is ready to shoot again
	 * pre: shotTimer >= 0
	 * post: true if the player can shoot again or else false
	 */
	public boolean shotReady() {
		if (shotTimer == 0) {
			return true;
		}
		else {
			return false;
		}
	}

	/** 
	 * Method manages reseting the shot timer and moving the turret after a bullet is shot
	 * pre: none
	 * post: turretAngle updated and shot timer reset
	 */
	public void shootProjectile(double angle) {
		shotTimer = shotDelay;
		this.turretAngle = (int) angle;
	}
	
	/** 
	 * Method moves player upwards based on speed
	 * pre: none
	 * post: player moved up
	 */
	public void moveUp(){
		setY(getY()-this.speed);	
	}
	
	/** 
	 * Method moves player left based on speed
	 * pre: none
	 * post: player moved left
	 */
	public void moveLeft(){
		setX(getX()-this.speed);	
	}
	
	/** 
	 * Method moves player down based on speed
	 * pre: none
	 * post: player moved down
	 */
	public void moveDown(){
		setY(getY()+this.speed);	
	}
	
	/** 
	 * Method moves player right based on speed
	 * pre: none
	 * post: player moved right
	 */
	public void moveRight(){
		setX(getX()+this.speed);	
	}
	
	// getters and setters
	public int getCenterX() {
		return centerX;
	}

	public void setCenterX(int centerX) {
		this.centerX = centerX;
	}

	public int getCenterY() {
		return centerY;
	}

	public void setCenterY(int centerY) {
		this.centerY = centerY;
	}

	public void setHealth(int health) {
		this.health = health;
	}
	
	public int getHealth() {
		return this.health;
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}
}
