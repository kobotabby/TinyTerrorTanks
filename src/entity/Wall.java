/**
 * @author Ethan Gan
 * Computer Science
 * 1/23/2023
 * Wall class for the TankRunner's walls.
 */
package entity;
import java.awt.Color;

import templates.GameObject;

/** Square Wall object */
public class Wall extends GameObject {
	public Wall(int x, int y, int width, int height) {
		this.setSize(width, height);
		this.setColor(Color.WHITE);  
		this.setX(x);
		this.setY(y);
	}

	/** act() method inherited from the GameObject class runs every game update */
	@Override
	public void act() {
	}
}
