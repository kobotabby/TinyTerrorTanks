/**
 * @author Ethan Gan
 * Computer Science
 * 1/23/2023
 * Tank class creates little tanks on to a GameFrame object.
 */
package entity;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import main.GameFrame;
import templates.GameObject;


/// figure out moving to point in grid
/** Tank object */
public class Tank extends GameObject{
	
    public int speed = 4;
	private int movingAngle = -1;
    private double speedX = 0;
    private double speedY = 0;
    private int damage = 15;
    private int health = 100; 
    private int centerX; 
	private int centerY; 
	private int width;
	private int height;
	private boolean collidingWall = false;
    private int shotDelay = 10;
    private int projectileSpeed = 7;
    private int shotTimer = 0;
    private double turretAngle = 90;
	private double drawInterval = 1000000000/12; // for tank hit animation
	private double delta = 0;
	private long lastTime = System.nanoTime();
	private long currentTime;
	private int timer = 0;
	private boolean inSight = false;
	public int closestAngleToPlayer = 0;
	public int closestAngleToPlayerValue = 0;
	private int turretLength = 20;
	private int turretMargin = 10; // shrinks turret radius
	private GameFrame game;	
	
	
	
	
	// PREDICT MOVEMENT USING MOVEMENT VECTORS
	
	public Tank(int x, int y, int width, int height, GameFrame g) {
		this.setSize((int)(width*0.9), (int)(height*0.9));
		this.setColor(Color.BLUE);  
		this.setX(x - width/2);
		this.setY(y - height/2);
		this.width = width;
		this.height = height;
		game = g;
	}

	/** paint() method paints the tank's graphics by overriding the game object paint method to include the tank turret */
	public void paint(Graphics g) {
		Rectangle r = getBounds();
		Graphics2D g2 = (Graphics2D) g;
		g2.setColor(c);
		g2.fillRect(0, 0, (int)r.getWidth(), (int)r.getHeight());
		
		// drawing tank turret
		g2.setColor(Color.WHITE);
		g2.fillOval(0+turretMargin, 0+turretMargin, (int)r.getWidth()-turretMargin*2, (int)r.getHeight()-turretMargin*2);
		int turretEndX = (int)r.getWidth()/2 + (int) ((turretLength * (float) Math.sin(Math.toRadians(this.turretAngle))) + .5);
		int turretEndY = (int)r.getHeight()/2 + (int) ((turretLength * (float) Math.cos(Math.toRadians(this.turretAngle))) + .5);
		g2.setStroke(new BasicStroke(10));
		g2.drawLine((int)r.getWidth()/2, (int)r.getHeight()/2, turretEndX, turretEndY);
	}
	
	/** act() method inherited from the GameObject class runs every game update */
	@Override
	public void act() {		
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
			this.setColor(Color.BLUE);
			delta--;
		}
		if(timer >= 1000000000) {
			timer = 0;
		}
	}
	
	/** 
	 * Method checks if the tank is ready to shoot again
	 * pre: shotTimer >= 0
	 * post: true if the tank can shoot again or else false
	 */
	public boolean shotReady() {
		if (shotTimer == 0 && this.isInSight()) {
			return true;
		}
		else {
			return false;
		}
	}
	
	/** 
	 * Method creates a new projectile from the enemy resets the shot timer
	 * pre: none
	 * post: new projectile is spawned according to the tanks turret angle and shotTimer reset
	 */
	public void shootProjectile() {
		shotTimer = getShotDelay();
		Projectile bullet = new Projectile(this.getCenterX(), this.getCenterY(), 10, turretAngle, getProjectileSpeed(), getDamage(), Color.magenta);
		game.getEnemyProjList().add(bullet);
		game.add(bullet);
		
	}
	
	/** 
	 * Method updates the tank's turret angle
	 * pre: none
	 * post: turret angle updated
	 */
	public void moveTurret(double angle) {
		this.turretAngle = angle;
	}
	
	/** 
	 * Method checks if the tank has hit a game object
	 * pre: o != null
	 * post: tank health reduced and true returned if tank is hit, else false
	 */
	public boolean checkCollision(GameObject o, int damage) {
//		System.out.println("Checking Collision...");
		if (collides(o)) {
			reduceHealth(damage);
			this.setColor(Color.RED);
			return true;
		} else {
			return false;
		}

	}
	
	/** 
	 * Method reduces tank health independently of collision.
	 * pre: health > 0
	 * post: tank health reduced 
	 */
	public void reduceHealth(int damage) {
		this.health -= damage;
	}
	
	/** 
	 * Method moves the tank towards a point by finding the distance to the target point and necessary component vectors to reach there.
	 * pre: none
	 * post: tank object moved towards point
	 */
	public void moveTowardsPt(int coordx, int coordy) {		
		// finds the component distances to a target and finds the necessary x and y speeds based on the current speed variable
		double distX = coordx-this.centerX;
		double distY = coordy-this.centerY;
		// current distance for debugging
//		System.out.println("distX: " + distX);
//		System.out.println("distY: " + distY);
		double hypoteneuse = Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2));
		double scale = this.speed/hypoteneuse;
		this.setSpeedX(distX*scale);
		this.setSpeedY(distY*scale);
		setX(getX() + (int)this.getSpeedX());	
		setY(getY() + (int)this.getSpeedY());	
	}
	// getters and setters
	public int getHealth() {
		return this.health;
	}
	
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

	public boolean isCollidingWall() {
		return collidingWall;
	}

	public void setCollidingWall(boolean collidingWall) {
		this.collidingWall = collidingWall;
	}

	public boolean isInSight() {
		return inSight;
	}

	public void setInSight(boolean inSight) {
		this.inSight = inSight;
	}

	public int getDamage() {
		return damage;
	}

	public void setDamage(int damage) {
		this.damage = damage;
	}

	public int getProjectileSpeed() {
		return projectileSpeed;
	}

	public void setProjectileSpeed(int projectileSpeed) {
		this.projectileSpeed = projectileSpeed;
	}

	public int getShotDelay() {
		return shotDelay;
	}

	public void setShotDelay(int shotDelay) {
		this.shotDelay = shotDelay;
	}

	public double getSpeedX() {
		return speedX;
	}

	public void setSpeedX(double speedX) {
		this.speedX = speedX;
	}

	public double getSpeedY() {
		return speedY;
	}

	public void setSpeedY(double speedY) {
		this.speedY = speedY;
	}
}