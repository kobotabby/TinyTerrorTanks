
package entity;
import java.awt.Color;

import templates.GameObject;

/**
 * @author Ethan Gan
 * Computer Science
 * 1/23/2023
 * UPDATED 1/22/2024: Added overloaded constructor for custom colours
 * Wall class for the TankRunner's walls.
 */
public class Wall extends GameObject {
	public Wall(int x, int y, int width, int height) {
		this.setSize(width, height);
		this.setColor(Color.WHITE);  
		this.setX(x);
		this.setY(y);
	}	
	
	// special constructor for end goal walls
	public Wall(int x, int y, int width, int height, Color color) {
		this.setSize(width, height);
		this.setColor(color);  
		this.setX(x);
		this.setY(y);
	}
	
	/** act() method inherited from the GameObject class runs every game update */
	@Override
	public void act() {
	}
}
