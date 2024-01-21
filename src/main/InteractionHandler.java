package main;

import java.util.ArrayList;

import entity.Player;
import entity.Projectile;
import entity.Ray;
import entity.Tank;
import entity.Wall;
import labels.HealthLabel;
import labels.ScoreLabel;

// enemy movement class with enemy and player coord with game board
public class InteractionHandler {
//	private Player player;
//	private ArrayList<Tank> game.enemyList; 
//	private ArrayList<Wall> game.wallList; 
//	private ArrayList<Projectile> playerProjList; 
//	private ArrayList<Projectile> game.enemyProjList; 
	private GameFrame game;
//	private int playerHealth;
	
	
	public InteractionHandler(GameFrame g) {
		game = g;
	
	}
	
	
	public void handleBullets() {
		
	}
	
	public void handleEnemies() {
		// Enemy Logic SHOULD BE MOVED TO ENEMY TANK CLASS THAT EXTENDS TANK
		for (int n=0; n< game.enemyList.size(); n++) {
			if (game.enemyList.size() != 0) {
				Tank enemy = game.enemyList.get(n);
				if(enemy != null){
					enemy.setCollidingWall(false);
					// Enemy Shooting
					enemy.setInSight(true);
//					drawLine(4, 40, enemy.getX(), enemy.getY(), player.getX(), player.getY());
//					
//					for (int i=0; i< rayList.size(); i++) {
//						Ray ray = rayList.get(i);
//						for (Wall wall : game.wallList) {
//							if (ray.collides(wall)) {
//								enemy.setInSight(false);
//								break;
//							}
//						}
//					}
					// clear all rays by looping through ray list
					for (int i=0; i< game.rayList.size(); i++) {
						Ray ray = game.rayList.get(i);
						game.remove(ray);
					}	
					// clear ray associations in ray list
					game.rayList.clear();
					// check if player is in sight of tank
					double angle = game.getAngleTo(enemy.getX(), enemy.getY(), game.player.getX(), game.player.getY());
					enemy.moveTurret(angle);
					if (enemy.shotReady()) {
						enemy.shootProjectile();
					}
					// Enemy Movement
					// store original x and y locations to revert to if the enemy moves into a wall
					int originalX = enemy.getX();
					int originalY = enemy.getY();
					// move enemy based on speed
					enemy.setX(enemy.getX() + (int)enemy.getSpeedX());	
					enemy.setY(enemy.getY() + (int)enemy.getSpeedY());	

					for (Wall wall : game.wallList) {
						if (enemy.collides(wall)) {
							enemy.setCollidingWall(true);
							int objectCenterX = wall.getX();
							int objectCenterY = wall.getY();
							int centerX = enemy.getX();
							int centerY = enemy.getY();
							boolean wallVertical = false;
							//check vertical
							if (centerX + game.TILE_SIZE + enemy.speed > objectCenterX && centerX + enemy.speed < objectCenterX + game.TILE_SIZE && centerY + game.TILE_SIZE > objectCenterY && centerY < objectCenterY + game.TILE_SIZE) {
								wallVertical = true;
							}
							boolean wallHorizontal = false;
							//check horizontal
							if (centerX + game.TILE_SIZE> objectCenterX && centerX < objectCenterX + game.TILE_SIZE && centerY + game.TILE_SIZE + enemy.speed > objectCenterY && centerY + enemy.speed < objectCenterY + game.TILE_SIZE) {
								wallHorizontal = true;
							}
							// If the enemy's move is illegal (hits a wall) move the enemy back to its original position in that axis.
							if (wallHorizontal) {
								enemy.setX(originalX);
							}
							if (wallVertical) {
								enemy.setY(originalY);	
							}

						}
					}
					// decrease player health if they collide
					game.player.checkCollision(enemy, (int) enemy.getDamage());
					game.playerHealth = game.player.getHealth();
//					HealthLabel.setText("HP: " + Integer.toString(game.playerHealth));
//
//					if (enemy.getHealth() <= 0) {
//						game.remove(enemy);
//						game.enemyList.remove(enemy);
//						score += 50*scoreMulti;
//						ScoreLabel.setText("SCORE: " + Integer.toString(score));
//					}
				}
			}
			else {
				break;
			}
		}
	}
	
	public void handleEnemyMovement () {

		
	}
	
	public void handleCollisionDetection() {
		
		
	}
	

	
	public void handleProjectiles() {
		// Projectile Logic
		for (int i=0; i< game.getProjList().size(); i++) {
			//	System.out.println(projList.size());
			if (game.getProjList().size() != 0) {
				Projectile bullet = game.getProjList().get(i);
				// continuously check if the bullet has been deleted because it is iterating through the projectile list and removing bullets at the same time
				if(bullet != null){
					// check bullets that have gone out of bounds
					if (bullet.getX()>= game.getSize().getWidth() || bullet.getY ()>= game.getSize().getHeight()) {
						game.remove(bullet); // BULLET SHOULD HAVE A FUNCTION TO DELETE ITSELF
						game.getProjList().remove(i);
					}
					// check bullets that hit enemy tanks
					for (Tank enemy : game.enemyList) {
						if (enemy.checkCollision(bullet, (int) bullet.damage)) {
							game.remove(bullet);
							game.getProjList().remove(i);
							break;
						}
					}
					// check wall collisions
					for (Wall wall : game.wallList) {
						if (bullet.collides(wall)) {
							bullet.bounce(wall, game.TILE_SIZE);
						}
					}
					// check bullets that have reached their max bounces
					if(bullet != null){
						if (bullet.getNumBounces() >= bullet.getMaxBounces()) {
							game.getProjList().remove(i);
							game.remove(bullet);
						}
					}
				}	
			} else {
				break;
			}			   
		}
		// Enemy Projectile Logic - same as above logic but for enemy projectiles attacking the player
		for (int i=0; i< game.enemyProjList.size(); i++) {
			if (game.enemyProjList.size() != 0) {
				Projectile bullet = game.enemyProjList.get(i);
				if(bullet != null){
					if (bullet.getX()>= game.getSize().getWidth() || bullet.getY ()>= game.getSize().getHeight()) {
						game.remove(bullet);
						game.enemyProjList.remove(i);
					}	
					if (game.player.checkCollision(bullet, (int) bullet.damage)) {
						game.remove(bullet);
						game.enemyProjList.remove(i);
						break;
					}
				}
				if(bullet != null){
					for (Wall wall : game.wallList) {
						if (bullet.collides(wall)) {
							game.enemyProjList.remove(i);
							game.remove(bullet);
							break;
						}
					}		
				}
				if(bullet != null){
					if (bullet.getNumBounces() >= bullet.getMaxBounces()) {
						game.enemyProjList.remove(i);
						game.remove(bullet);
					}
				}
			}	
		}
	}

}
