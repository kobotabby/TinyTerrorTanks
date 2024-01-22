/**
 * @author Ethan Gan
 * Computer Science
 * 1/23/2023
 * Ray class that manages drawing single line nodes for deciding enemy movement
 */
package entity;
import java.awt.Color;

import main.GameFrame;
import templates.GameObject;


/// hopefully completely remove this class
/** Ray object class used for draw lines for deciding enemy movement */
public class Ray extends GameObject{

	private double angle = 0;
	private int size = 20;
	private int generation = 0;

	public Ray(int x, int y, double angle, int generation, GameFrame game) {
		this.setSize(size, size);
		this.setColor(Color.CYAN);
		this.setX(x - size / 2);
		this.setY(y - size / 2);
		this.angle = angle;
		this.generation = generation;
	}
	public Ray(int x, int y, GameFrame game) {
		this.setSize(size, size);
		this.setColor(Color.CYAN);
		this.setX(x - size / 2);
		this.setY(y - size / 2);

	}
	
	/** act() method inherited from the GameObject class runs every game update */
	@Override
	public void act() {	
	}

	/** 
	 * Method returns the generation number of the ray
	 * pre: generation != null
	 * post: generation number returned
	 */
	public int getGeneration() {
		return this.generation;
	}
	
	/** 
	 * Method returns the angle of the ray
	 * pre: angle != null
	 * post: angle returned
	 */
	public int getAngle() {
		return (int) this.angle;
	}
}

