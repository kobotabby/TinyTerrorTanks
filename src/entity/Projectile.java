/**
 * @author Ethan Gan
 * Computer Science
 * 1/23/2023
 * Projectile class that manages projectiles that are shot from tanks in the GameFrame class.
 */
package entity;
import java.awt.Color;

import templates.GameObject;

public class Projectile extends GameObject {
	private double angle;
	private double speed;
	private int speedX;
	private int speedY;
	public double damage;
	private int size;
	private int numBounces = 0;
	private int maxBounces = 3;

	public Projectile(int x, int y, int size, double angle, double speed, int damage, Color color) {
		this.setSize(size, size);
		this.setColor(color);
		// System.out.println(getX() + ", " + getY());
		this.setX(x - size / 2);
		this.setY(y - size / 2);
		this.angle = angle;
		this.speed = speed;
		this.damage = damage;
		this.size = size;
		
		this.speedX = (int) ((this.speed * (float) Math.sin(Math.toRadians(angle))) + .5);
		this.speedY = (int) ((this.speed * (float) Math.cos(Math.toRadians(angle))) + .5);
	}

	/** act() method inherited from the GameObject class runs every game update */
	@Override
	public void act() {
		moveProjectile();
	}

	/** 
	 * Method bounces projectile motion by reversing its speed vectors depending on which direction it collides
	 * pre: o.collides(this)
	 * post: speedX or speedY changed
	 */
	public void bounce(GameObject o, int width) {
		int objectCenterX = o.getX();
		int objectCenterY = o.getY();
		int centerX = this.getX();
		int centerY = this.getY();		
		//check X movement bounce
		// check collision even if its in the object
		if (centerX + this.size + this.speedX > objectCenterX && centerX + this.speedX < objectCenterX + width && centerY + this.size > objectCenterY && centerY < objectCenterY + width) {
			this.speedY *= -1;
		}
		//check Y movement bounce 
		// check collision even if its in the object
		if (centerX + this.size> objectCenterX && centerX < objectCenterX + width && centerY + this.size + this.speedY > objectCenterY && centerY + this.speedY < objectCenterY + width) {
			this.speedY *= -1;
			this.speedX *= -1;
		}
		this.setNumBounces(this.getNumBounces() + 1);
	}

	/** 
	 * Method moves projectile by its component speed vectors
	 * pre: none
	 * post: projectile moved
	 */
	public void moveProjectile() {
		setX((getX() + speedX));
		setY((getY() + speedY));
	}

	/** 
	 * Method returns the number of bounces the projectile did
	 * pre: none
	 * post: number representing number of bounces
	 */
	public int getNumBounces() {
		return numBounces;
	}

	/** 
	 * Method sets the number of bounces the projectile did
	 * pre: none
	 * post: numBounces changed
	 */
	public void setNumBounces(int numBounces) {
		this.numBounces = numBounces;
	}

	/** 
	 * Method gets the max number of bounces the projectile can do
	 * pre: none
	 * post: maxBounces returned
	 */
	public int getMaxBounces() {
		return maxBounces;
	}

	/** 
	 * Method sets the max number of bounces the projectile can do
	 * pre: none
	 * post: maxBounces returned
	 */
	public void setMaxBounces(int maxBounces) {
		this.maxBounces = maxBounces;
	}
}