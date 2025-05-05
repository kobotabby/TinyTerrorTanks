package main;

import java.util.ArrayList;

import entity.Player;
import entity.Projectile;
import entity.Tank;
import entity.Wall;
/**
 * @author Ethan Gan
 * Computer Science
 * 1/22/2024
 * InteractionHandler class helps increase readability in the GameFrame class by handling all moving objects with exception to the player.
 */
public class InteractionHandler {

	private GameFrame game;
	
	public InteractionHandler(GameFrame g) {
		game = g;
	}
	
	/** Method handles enemy control in the core game loop - OLD CODE */
	public void handleEnemies() {
		// Enemy Logic SHOULD BE MOVED TO ENEMY TANK CLASS THAT EXTENDS TANK
		for (int n=0; n< game.enemyList.size(); n++) {
			if (game.enemyList.size() != 0) {
				Tank enemy = game.enemyList.get(n);
				if(enemy != null){
					enemy.setCollidingWall(false);
					// Enemy Shooting
					enemy.setInSight(true);

					double angle = game.getAngleTo(enemy.getX(), enemy.getY(), game.getPlayer().getX(), game.getPlayer().getY());
					enemy.moveTurret(angle);
					if (enemy.shotReady()) {
						enemy.shootProjectile();
					}

					// decrease player health if they collide
					game.getPlayer().checkCollision(enemy, (int) enemy.getDamage());
					game.setStoredPlayerHealth(game.getPlayer().getHealth());
					game.healthLabel.setText("HP: " + Integer.toString(game.getStoredPlayerHealth()));
//
					if (enemy.getHealth() <= 0) {
//						System.out.println("DIED DIED DIED DIED");
//						System.out.println(game.player.getHealth());
//						System.out.println(game.player.getHealth()+game.getPlayerHeal());
						game.remove(enemy);
						game.enemyList.remove(enemy);
						// update stored player health
						game.setStoredPlayerHealth(game.getPlayer().getHealth()+game.getPlayerHeal());
						// update actual player health
						game.getPlayer().setHealth(game.getStoredPlayerHealth());
//						System.out.println(game.player.getHealth());
						game.healthLabel.setText("HP: " + Integer.toString(game.getStoredPlayerHealth()));
						game.setScore((int)(game.getScore() + 50*game.getScoreMulti()));
						game.scoreLabel.setText("SCORE: " + Integer.toString(game.getScore()));
					}
				}
			}
			else {
				break;
			}
		}
	}
	/** Method handles projectile interactions in the core game loop - OLD CODE */
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
					if (game.getPlayer().checkCollision(bullet, (int) bullet.damage)) {
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
