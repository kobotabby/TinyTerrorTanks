
package entity;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.util.ArrayList;

import javax.swing.ImageIcon;
import javax.swing.JLabel;

import main.GameFrame;
import templates.GameObject;

/**
 * @author Ethan Gan
 * Computer Science
 * 1/23/2023 - MOSTLY OLD CODE
 * UPDATED 1/22/2024: Added pathfinding controls
 * Tank class creates little tanks on to a GameFrame object.
 */
public class Tank extends GameObject{
	public boolean onPath = true;
	private int tileRow;
	private int tileCol;
	
    public int speed = 4;
    private int damage = 15; // normally 15
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
	private boolean colliding = false;
	ArrayList<int[]> path;
	
	// movement target
	private int targetX;
	private int targetY;

	
	public Tank(int x, int y, int width, int height, GameFrame g) {
		this.setSize((int)(width), (int)(height));
		this.setColor(Color.BLUE);  
		this.setX(x - width/2);
		this.setY(y - height/2);
		this.width = width;
		this.height = height;
		game = g;		
		tileRow = this.getCoordinates()[0];
		tileCol = this.getCoordinates()[1];
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
	
	public void searchPath(int goalCol, int goalRow) {
		int startCol = (this.getX() + width)/game.TILE_SIZE;
		int startRow = (this.getY()+ height)/game.TILE_SIZE;
		
		game.pFinder.setNodes(startCol, startRow, goalCol, goalRow);

	}

	public int[] getCoordinates() {
		tileRow = this.getX()/game.TILE_SIZE;
		tileCol = this.getY()/game.TILE_SIZE;
//		System.out.println("TILE X " + tileX);
//		System.out.println("TILE Y " + tileY);
		int[] coord = {tileRow, tileCol};
		return coord;
	}
	
	/** act() method inherited from the GameObject class runs every game update */
	@Override
	public void act() {		
//		getCoordinates();
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
		
		int startCol = (this.getX() + width)/game.TILE_SIZE;
		int startRow = (this.getY()+ height)/game.TILE_SIZE;
		
	    if ((this.getX() == targetX && this.getY() == targetY) || (targetX == 0 && targetY == 0 ) ) {
	        // Generate new target coordinates
	        if (path == null || path.isEmpty()) {
	            int goalCol = game.getPlayer().getCoordinates()[0];
	            int goalRow = game.getPlayer().getCoordinates()[1];
	            
	            path = game.pFinder.createCoordinatePath(startCol, startRow, goalCol, goalRow);
//	            System.out.println(path);
	        }

	        // Set the next target coordinates from the path
	        if (!path.isEmpty()) {
	            // prevent spasms where the next move locks the tank in place
	        	int[] nextTarget = path.remove(0);
	        	if (nextTarget[0] != targetX && nextTarget[1] != targetY ) {
		            targetX = nextTarget[0] * game.TILE_SIZE;
		            targetY = nextTarget[1] * game.TILE_SIZE;
	        	} 
	        }
	    }

	    // Move the tank towards the target coordinates
	    if (targetX != 0 && targetY != 0) {
	    	
		    moveTowardsPt(targetX, targetY);
	    }
	    
	    // Visualize the target coordinates by placing a new projectile with no speed
//	    if (targetX != 0 && targetY != 0) {
//	        Projectile targetMarker = new Projectile(targetX, targetY, 10, 0, 0, 0, Color.GREEN);
//	        game.getProjList().add(targetMarker);
//	        game.add(targetMarker);
//	    }
//	    System.out.println(targetX + " " + targetY);
	}
	
	
	
	/** 
	 * Method checks if the tank is ready to shoot again
	 * pre: shotTimer >= 0
	 * post: true if the tank can shoot again or else false
	 */
	public boolean shotReady() {
		if (shotTimer == 0 && inSight) {

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
			colliding = true;
			return true;
		} else {
			return false;
		}

	}
	/** 
	 * Method checks if the tank has hit a game object
	 * pre: o != null
	 * post: tank health reduced and true returned if tank is hit, else false
	 */
	public boolean checkCollision(GameObject o) {
//		System.out.println("Checking Collision...");
		if (collides(o)) {
			this.setColor(Color.MAGENTA);
			colliding = true;
			return true;
		} else {
			return false;
		}

	}
	
	
	/** 
	 * Method moves tank health independently of collision.
	 * pre: health > 0
	 * post: tank health reduced 
	 */
	public void reduceHealth(int damage) {
		this.health -= damage;
	}
	

	public void setTarget(int x, int y) {
		targetX = x;
		targetY = y;
	}
	/** 
	 * Method moves the tank towards a point by finding the distance to the target point and necessary component vectors to reach there.
	 * pre: none
	 * post: tank object moved towards point
	 */
	public void moveTowardsPt(int coordx, int coordy) {		
		// finds the component distances to a target and finds the necessary x and y speeds based on the current speed variable
		double distX = coordx-this.getX();
		double distY = coordy-this.getY();
		// current distance for debugging
//		System.out.println("distX: " + distX);
//		System.out.println("distY: " + distY);
		double hypoteneuse = Math.sqrt(Math.pow(distX, 2) + Math.pow(distY, 2));
		double scale = this.speed/hypoteneuse;
		double speedX = distX*scale;
		double speedY = distY*scale;
		
		
		setX(getX() + (int)speedX);	
		setY(getY() + (int)speedY);	
		
		// get unstuck when approaching a wall
		if (collidingWall) {
			
			// stuck factor is the amount of movement to get unstuck
			double struggleFactor = 1.2;
			//	Try moving in all four directions
	        setX(getX() - (int)( speedX*struggleFactor));
	        setY(getY() - (int)( speedY*struggleFactor));
	        setX(getX() + (int)(speedX*struggleFactor));	
			setY(getY() + (int)(speedY*struggleFactor));	

	        
	        setX(getX() + (int) (speedY*struggleFactor));
	        setY(getY() - (int) (speedX*struggleFactor));

	        setX(getX() - (int) (speedY*struggleFactor));
	        setY(getY() + (int) (speedX*struggleFactor));

		}
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

	public void setInSight(boolean b) {
		inSight = b;
	}
}