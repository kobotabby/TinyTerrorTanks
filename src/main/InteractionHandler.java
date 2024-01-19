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
	private Player player;
//	private ArrayList<Tank> game.enemyList; 
//	private ArrayList<Wall> game.wallList; 
//	private ArrayList<Projectile> playerProjList; 
//	private ArrayList<Projectile> game.enemyProjList; 
	private GameFrame game;
	private int playerHealth;
	
	
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
					double angle = game.getAngleTo(enemy.getX(), enemy.getY(), player.getX(), player.getY());
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
					player.checkCollision(enemy, (int) enemy.getDamage());
					playerHealth = player.getHealth();
//					HealthLabel.setText("HP: " + Integer.toString(playerHealth));
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

		// in class it should already have decided its angle and make a move
//		// Enemy Movement Decision Making - Happens Every 2 Seconds
//		if(delta >= 1) { 
//			for (Tank enemy : game.enemyList) {
//				// MODE #1: Ray Based Movement - if the enemy is not touching a wall 
//				if (enemy.isCollidingWall() == false) {
//					// set a dictionary for the possible angles to check and a value representing the distance of the closest wall to the tank
//					Dictionary<Integer, Integer> angleValues = new Hashtable<Integer, Integer>();					
//					for (int rayAngle : anglesToCheck) {	
//						// populate dictionaries with the maximum value for the nearest wall to hit a ray
//						angleValues.put(rayAngle, enemyRaySteps);
//						// create rays which are groups of ray objects contained in the ray list
//						drawLine(rayAngle, 150, enemyRaySteps, enemy.getCenterX(), enemy.getCenterY());
//					}
//					// Ray Logic - determining angle values
//					for (int i=0; i< rayList.size(); i++) {
//						Ray ray = rayList.get(i);
//						// check ray to wall collisions
//						for (Wall wall : game.wallList) {
//							if (ray.collides(wall)) {
//								int rayAngle = ray.getAngle();
//								// ray generation tells us how far the wall was relative to the current tank
//								int generation = ray.getGeneration();
//								// comments for reading the angle values of the angle value dictionary
////								outputDictionaryValues(angleValues);
////								System.out.println("RAY ANGLE: " + rayAngle);
//								int currentAngleValue = (int) angleValues.get(rayAngle);
//								// if the generation is smaller than current angle, meaning this ray's generation is closer
//								if (generation < currentAngleValue) {
//									// replace angle value with the closest generation
//									angleValues.remove(rayAngle);
//									angleValues.put(rayAngle, generation);
//								}
//							}
//						}
//					}
//					// clear all rays 
//					for (int i=0; i< rayList.size(); i++) {
//						Ray ray = rayList.get(i);
//						remove(ray);
//						rayList.remove(i);
//					}		
//					// calculate the current tank's angle to player to later use as a weight
//					double angleAtPlayer = Math.atan2(player.getCenterX() - enemy.getCenterX(), player.getCenterY() - enemy.getCenterY()) * 180 / Math.PI;
//					// create an angle bias weight based on how close the angle in the angle values dictionary is to the actual angle to the player
//					double angleBias = .7;
//					// create a new weighted random class to store the different probabilities and later select a movement option
//					WeightedRandom<Integer> possibleMoves = new WeightedRandom<>();
//					// loop through all possible angles
//					for (int possibleAngle : anglesToCheck) {
//						// get range of movement from that angle using the closest ray generation stored in the angle value dictionary
//						int range = angleValues.get(possibleAngle);
//						// only consider angles when the range of the angle is greater than 2 and more than the wall being directly beside the tank
//						if (range >= 2) {
//							// weight the angle based on the distance to player and potential range of motion
//							// math.pow makes the distribution more skewed towards certain angles making the selection easier
//							double weight = Math.pow((Math.pow(range,3)*(angleBias/Math.abs(angleAtPlayer-possibleAngle))), 7); 
//							possibleMoves.addEntry(possibleAngle, weight);
//						}
//					}					
//					// choose an angle using the weighted random class
//					int angleChosen = possibleMoves.getRandom();
//					// if the angle chosen is not the default value of 999 meaning it is null set current tank speed accordingly
//					if (angleChosen != 999) {
//						enemy.setSpeedX((int) ((enemy.speed * (float) Math.sin(Math.toRadians(angleChosen))) + .5));
//						enemy.setSpeedY((int) ((enemy.speed * (float) Math.cos(Math.toRadians(angleChosen))) + .5));		
//					}
//				}
//				// MODE #2: Random Grid Based Movement - if the enemy is touching a wall to get unstuck 
//				else {
//					// find the closest grid coordinate to the current tank
//					int[] enemyGridCoord = getClosestGridCoords(enemy.getX(), enemy.getY());
//					int enemyCoordX = enemyGridCoord[0];
//					int enemyCoordY = enemyGridCoord[1];
//					ArrayList<int[]> possibleNextCoords = new ArrayList<int[]> ();
//					// use a surrounding tiles list to check for walls and possible moves
//					int[][] surroundingTiles = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1},{1, -1},{1, 0}, {1, 1}};
//					for (int[] dist : surroundingTiles) {
//						// try and catch for when a tile is called that is out of bounds
//						try {
//							if (wallArr[enemyCoordX+dist[0]][enemyCoordY+dist[1]] == 0) {
//								int[] coord = {enemyCoordX+dist[0], enemyCoordY+dist[1]};
//								possibleNextCoords.add(coord);
//							}												
//						} catch (ArrayIndexOutOfBoundsException e) {
//							// Print message when a tile is called that is out of bounds
////							System.out.println("Out of Bounds");
//						}
//					}
//					Random random = new Random();			
//					if (possibleNextCoords.size() > 1) {
//						// generate a random number to choose a possible grid coordinate
//						int randomSlot = random.nextInt(possibleNextCoords.size()-1);
//						int[] selectedCoord = possibleNextCoords.get(randomSlot);
//						// convert the grid coordinate to x and y coordinates
//						int targetX = selectedCoord[0] * TILE_SIZE;
//						int targetY = selectedCoord[1] * TILE_SIZE;
//						// set course for the target square using the found angle
//						double angle = getAngleTo(enemy.getX(), enemy.getY(), targetX, targetY);
//						enemy.setSpeedX((int) ((enemy.speed * (float) Math.sin(Math.toRadians(angle))) + .5));
//						enemy.setSpeedY((int) ((enemy.speed * (float) Math.cos(Math.toRadians(angle))) + .5));						
//					}
//				}
//			}
//			// reset delta
//			delta = 0;			
//			decisionCount++;
//		}
		
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
					if (player.checkCollision(bullet, (int) bullet.damage)) {
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
