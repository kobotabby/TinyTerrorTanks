/**
 * @author Ethan Gan
 * Computer Science
 * 1/12/2024
 * GameFrame class controls the running TankRunner Game.
 */
package main;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Random;
import java.util.Scanner;

import javax.swing.*;

import entity.Player;
import entity.Projectile;
import entity.Ray;
import entity.Tank;
import entity.Wall;
import handlers.KeyHandler;
import handlers.MouseHandler;
import map.GameMap;
import map.Room;
import templates.Game;

////// REPAIR BROKEN LOAD STATES
/* GameFrame manages the majority of the game's logic and object interactions */
public class GameFrame extends Game {
	//SCREEN SETTINGS
	final int originalTileSize = 16; // fix game doesn't always boot up.
	final int SCALE = 3;
	final int TILE_SIZE = originalTileSize * SCALE; // 48 * 48 tile
	final int MAX_SCREEN_ROW = 20;
	final int MAX_SCREEN_COL = 20;  
	final int SCREEN_WIDTH = TILE_SIZE * MAX_SCREEN_ROW; // 48 * 16 = 768
	final int SCREEN_HEIGHT = TILE_SIZE * MAX_SCREEN_COL; //48 * 12 = 576
	
	private GameMap gameMap = new GameMap(MAX_SCREEN_ROW, MAX_SCREEN_COL, 8);	
//	private Room currentRoom = ;
	private double enemyMovementInterval; 
	private double delta;
	private long lastTime;
	private long currentTime;
	// decision count to show how many enemy movement decision periods have been made in a second for debugging/tuning
	private int decisionCount;
	private int timer;
	private double enemySpeedScale;
	private double enemyBulletSpeedScale;
	private double enemyFireRateScale;
	private double enemyDamageScale;
	private int numEnemies;
	private double numEnemyScale;
	public boolean gameOver = false;
	private JLabel scoreLabel;	
	private JLabel healthLabel;	
	private JLabel levelLabel;
	private int score;
	private int level;
	private int playerHealth;
	private int playerHeal;
	private double scoreMulti;
	Player player;
	private Tank enemy;
	private ArrayList<Projectile> projList = new ArrayList<Projectile>();
	private ArrayList<Projectile> enemyProjList = new ArrayList<Projectile>();
	private ArrayList<Tank> enemyList = new ArrayList<Tank>();
	private ArrayList<Wall> wallList = new ArrayList<Wall>();
	private ArrayList<Ray> rayList = new ArrayList<Ray>();
	private int enemyRaySteps = 4;
	private int[][] wallArr;
	private int[] anglesToCheck = {0, 45, 90, 135, 180, -90, -45, -135};

	KeyHandler keyH = new KeyHandler();
	public MouseHandler mouseH = new MouseHandler();

	
	// add flood fill for ai 
	// change room gen 
	public GameFrame() {
		// initialize all variables in the constructor for when the game is restarted as a new GameFrame object
		enemyMovementInterval = 1000000000/15; // one second
		delta = 0;
		lastTime = System.nanoTime();
		decisionCount = 0;
		timer = 0;
		enemySpeedScale = 1;
		enemyBulletSpeedScale = 1;
		enemyFireRateScale = 6;
		enemyDamageScale = 0.1;
		numEnemies = 1;
		numEnemyScale = 1;
		gameOver = false;
		score = 0;
		level = 0;
		playerHealth = 250;
		playerHeal = 250; // normal 15
		scoreMulti = 1;
		projList = new ArrayList<Projectile>();
		enemyProjList = new ArrayList<Projectile>();
		setEnemyList(new ArrayList<Tank>());
		wallList = new ArrayList<Wall>();
		rayList = new ArrayList<Ray>();
		enemyRaySteps = 3; // try lowering
		
		// game GUI
		scoreLabel = new JLabel("SCORE: 0");
		scoreLabel.setForeground(Color.BLACK);
		scoreLabel.setBounds(50, 0, 400, 48);
		scoreLabel.setFont(new Font("Sans-serif", Font.BOLD, 48));
		add(scoreLabel);

		healthLabel = new JLabel("HP: FULL");
		healthLabel.setForeground(Color.BLACK);
		healthLabel.setBounds(50, 910, 400, 48);
		healthLabel.setFont(new Font("Sans-serif", Font.BOLD, 48));
		add(healthLabel);

		levelLabel = new JLabel("LEVEL: 01");
		levelLabel.setForeground(Color.BLACK);
		levelLabel.setBounds(700, 910, 400, 48);
		levelLabel.setFont(new Font("Sans-serif", Font.BOLD, 48));
		add(levelLabel);

		System.out.println("GENERATING BOARD");
		drawRoomBoard(gameMap.getCurrentRoom());
		this.addKeyListener(keyH);
		this.addMouseListener(mouseH);

		// yield to actual screen size
		this.setSize(SCREEN_WIDTH+16, SCREEN_HEIGHT+62);		
		this.setBackground(Color.BLACK);
		this.addKeyListener(keyH);
		this.addMouseListener(mouseH);
		this.addMouseMotionListener(mouseH);
		this.setFocusable(true);
		this.setResizable(false);
		System.out.println("game started");
	}

	/** setup() method sets a delay for the act method */
	@Override
	public void setup() {
		// slow down the timer to 60 fps
		setDelay(16);
	} 

	/** act() method contains core game logic and is called every game update */
	@Override
	public void act() {
		// Player Movement
		// check walls the player currently collides with
		int originalX = player.getX();
		int originalY = player.getY();
		// initialize boolean variables for vertical and horizontal collisions
		boolean wallVertical = false;
		boolean wallHorizontal = false;
		// Listen for WASD Key Presses
		if(WKeyPressed()){
			player.moveUp();
		}
		if(AKeyPressed()){
			player.moveLeft();
		}
		if(SKeyPressed()){
			player.moveDown();
		}
		if(DKeyPressed()){
			player.moveRight();
		}
		// check if the player has made a valid move
		for (Wall wall : wallList) {
			if (player.collides(wall)) {
				int objectCenterX = wall.getX();
				int objectCenterY = wall.getY();
				int centerX = player.getX();
				int centerY = player.getY();
				// collision code accounts for how far the player might be into a wall based on its speed repurposed from: https://happycoding.io/tutorials/processing/collision-detection
				//check horizontal collision 
				if (centerX + TILE_SIZE + player.speed > objectCenterX && centerX + player.speed < objectCenterX + TILE_SIZE && centerY + TILE_SIZE > objectCenterY && centerY < objectCenterY + TILE_SIZE) {
					wallVertical = true;
				}
				//check vertical collision
				if (centerX + TILE_SIZE> objectCenterX && centerX< objectCenterX + TILE_SIZE && centerY + TILE_SIZE + player.speed > objectCenterY && centerY + player.speed < objectCenterY + TILE_SIZE) {
					wallHorizontal = true;
				}
			}
		}
		// If the player's move is illegal (hits a wall) move the player back to its original position in that axis.
		if ((wallHorizontal) || (wallVertical)) {
			player.setX(originalX);
			player.setY(originalY);
		}
		
		// Enemy Spawn Testing 
		if (keyH.spacePressed) {
			generateNewEnemies(1);
		}
		
		
		// Player Shooting
		if (((mouseH.mousePressed)) && player.shotReady()) {
			// find mouse shooting angle			
			double angle = getAngleTo(player.getCenterX(), player.getCenterY(), mouseH.mouseX, mouseH.mouseY);
			// create new projectile for the player and add it to the projectile list
			Projectile bullet = new Projectile(player.getCenterX(), player.getCenterY(), 10, angle, 9, player.getDamage(), Color.YELLOW);
			getProjList().add(bullet);
			add(bullet);
			player.shootProjectile(angle);
		}
		
		
		// Projectile Logic
		for (int i=0; i< getProjList().size(); i++) {
			//	System.out.println(projList.size());
			if (getProjList().size() != 0) {
				Projectile bullet = getProjList().get(i);
				// continuously check if the bullet has been deleted because it is iterating through the projectile list and removing bullets at the same time
				if(bullet != null){
					// check bullets that have gone out of bounds
					if (bullet.getX()>= getSize().getWidth() || bullet.getY ()>= getSize().getHeight()) {
						remove(bullet);
						getProjList().remove(i);
					}
					// check bullets that hit enemy tanks
					for (Tank enemy : getEnemyList()) {
						if (enemy.checkCollision(bullet, (int) bullet.damage)) {
							remove(bullet);
							getProjList().remove(i);
							break;
						}
					}
					// check wall collisions
					for (Wall wall : wallList) {
						if (bullet.collides(wall)) {
							bullet.bounce(wall, TILE_SIZE);
						}
					}
					// check bullets that have reached their max bounces
					if(bullet != null){
						if (bullet.getNumBounces() >= bullet.getMaxBounces()) {
							getProjList().remove(i);
							remove(bullet);
						}
					}
				}	
			} else {
				break;
			}			   
		}
		// Enemy Projectile Logic - same as above logic but for enemy projectiles attacking the player
		for (int i=0; i< getEnemyProjList().size(); i++) {
			if (getEnemyProjList().size() != 0) {
				Projectile bullet = getEnemyProjList().get(i);
				if(bullet != null){
					if (bullet.getX()>= getSize().getWidth() || bullet.getY ()>= getSize().getHeight()) {
						remove(bullet);
						getEnemyProjList().remove(i);
					}	
					if (player.checkCollision(bullet, (int) bullet.damage)) {
						remove(bullet);
						getEnemyProjList().remove(i);
						break;
					}
				}
				if(bullet != null){
					for (Wall wall : wallList) {
						if (bullet.collides(wall)) {
							getEnemyProjList().remove(i);
							remove(bullet);
							break;
						}
					}						
				}
				if(bullet != null){
					if (bullet.getNumBounces() >= bullet.getMaxBounces()) {
						getEnemyProjList().remove(i);
						remove(bullet);
					}
				}
			}	
		}
		// update screen with removed bullets
		repaint();		
		// Enemy Logic SHOULD BE MOVED TO ENEMY TANK CLASS THAT EXTENDS TANK
				for (int n=0; n< getEnemyList().size(); n++) {
					if (getEnemyList().size() != 0) {
						enemy = getEnemyList().get(n);
						if(enemy != null){
							enemy.setCollidingWall(false);
							// Enemy Shooting
							enemy.setInSight(true);
							drawLine(4, 40, enemy.getX(), enemy.getY(), player.getX(), player.getY());
							
							for (int i=0; i< rayList.size(); i++) {
								Ray ray = rayList.get(i);
								for (Wall wall : wallList) {
									if (ray.collides(wall)) {
										enemy.setInSight(false);
										break;
									}
								}
							}
							// clear all rays by looping through ray list
							for (int i=0; i< rayList.size(); i++) {
								Ray ray = rayList.get(i);
								remove(ray);
							}	
							// clear ray associations in ray list
							rayList.clear();
							// check if player is in sight of tank
							double angle = getAngleTo(enemy.getX(), enemy.getY(), player.getX(), player.getY());
							enemy.moveTurret(angle);
							if (enemy.shotReady()) {
								enemy.shootProjectile();
							}
							// Enemy Movement
							// store original x and y locations to revert to if the enemy moves into a wall
							originalX = enemy.getX();
							originalY = enemy.getY();
							// move enemy based on speed
							enemy.setX(enemy.getX() + (int)enemy.getSpeedX());	
							enemy.setY(enemy.getY() + (int)enemy.getSpeedY());	

							for (Wall wall : wallList) {
								if (enemy.collides(wall)) {
									enemy.setCollidingWall(true);
									int objectCenterX = wall.getX();
									int objectCenterY = wall.getY();
									int centerX = enemy.getX();
									int centerY = enemy.getY();
									//check vertical
									if (centerX + TILE_SIZE + enemy.speed > objectCenterX && centerX + enemy.speed < objectCenterX + TILE_SIZE && centerY + TILE_SIZE > objectCenterY && centerY < objectCenterY + TILE_SIZE) {
										wallVertical = true;
									}
									//check horizontal
									if (centerX + TILE_SIZE> objectCenterX && centerX < objectCenterX + TILE_SIZE && centerY + TILE_SIZE + enemy.speed > objectCenterY && centerY + enemy.speed < objectCenterY + TILE_SIZE) {
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
							healthLabel.setText("HP: " + Integer.toString(playerHealth));

							if (enemy.getHealth() <= 0) {
								remove(enemy);
								getEnemyList().remove(enemy);
								score += 50*scoreMulti;
								scoreLabel.setText("SCORE: " + Integer.toString(score));
							}
						}
					}
					else {
						break;
					}
				}
		// check if player has died
		if (player.getHealth() <= 0) {
			// update player health label
			healthLabel.setText("HP: " + 0);
			// stop game and display end dialog
			this.stopGame();
			this.checkHighScore(score);
			this.gameOver();
		}
		
		// enter the next level if all enemies have been killed
//		if (getEnemyList().size() == 0) {
//			clearEntities();
//			generateNewBoard(numEnemies);
//		}
		
		checkRoomChange();

		// updates per second style timer for enemy movement
		currentTime = System.nanoTime();
		delta += (currentTime - lastTime) / enemyMovementInterval; // how much time left until the next draw interval 
		timer += (currentTime - lastTime); // update timer that keeps tracks of when a second passes by the time elapsed
		lastTime = currentTime; // update last time

		// Enemy Movement Decision Making - Happens Every 2 Seconds
		if(delta >= 1) { 
			for (Tank enemy : getEnemyList()) {
				// MODE #1: Ray Based Movement - if the enemy is not touching a wall 
				if (enemy.isCollidingWall() == false) {
					// set a dictionary for the possible angles to check and a value representing the distance of the closest wall to the tank
					Dictionary<Integer, Integer> angleValues = new Hashtable<Integer, Integer>();					
					for (int rayAngle : anglesToCheck) {	
						// populate dictionaries with the maximum value for the nearest wall to hit a ray
						angleValues.put(rayAngle, enemyRaySteps);
						// create rays which are groups of ray objects contained in the ray list
						drawLine(rayAngle, 150, enemyRaySteps, enemy.getCenterX(), enemy.getCenterY());
					}
					// Ray Logic - determining angle values
					for (int i=0; i< rayList.size(); i++) {
						Ray ray = rayList.get(i);
						// check ray to wall collisions
						for (Wall wall : wallList) {
							if (ray.collides(wall)) {
								int rayAngle = ray.getAngle();
								// ray generation tells us how far the wall was relative to the current tank
								int generation = ray.getGeneration();
								// comments for reading the angle values of the angle value dictionary
//								outputDictionaryValues(angleValues);
//								System.out.println("RAY ANGLE: " + rayAngle);
								int currentAngleValue = (int) angleValues.get(rayAngle);
								// if the generation is smaller than current angle, meaning this ray's generation is closer
								if (generation < currentAngleValue) {
									// replace angle value with the closest generation
									angleValues.remove(rayAngle);
									angleValues.put(rayAngle, generation);
								}
							}
						}
					}
					// clear all rays 
					for (int i=0; i< rayList.size(); i++) {
						Ray ray = rayList.get(i);
						remove(ray);
						rayList.remove(i);
					}		
					// calculate the current tank's angle to player to later use as a weight
					double angleAtPlayer = Math.atan2(player.getCenterX() - enemy.getCenterX(), player.getCenterY() - enemy.getCenterY()) * 180 / Math.PI;
					// create an angle bias weight based on how close the angle in the angle values dictionary is to the actual angle to the player
					double angleBias = .7;
					// create a new weighted random class to store the different probabilities and later select a movement option
					WeightedRandom<Integer> possibleMoves = new WeightedRandom<>();
					// loop through all possible angles
					for (int possibleAngle : anglesToCheck) {
						// get range of movement from that angle using the closest ray generation stored in the angle value dictionary
						int range = angleValues.get(possibleAngle);
						// only consider angles when the range of the angle is greater than 2 and more than the wall being directly beside the tank
						if (range >= 2) {
							// weight the angle based on the distance to player and potential range of motion
							// math.pow makes the distribution more skewed towards certain angles making the selection easier
							double weight = Math.pow((Math.pow(range,3)*(angleBias/Math.abs(angleAtPlayer-possibleAngle))), 7); 
							possibleMoves.addEntry(possibleAngle, weight);
						}
					}					
					// choose an angle using the weighted random class
					int angleChosen = possibleMoves.getRandom();
					// if the angle chosen is not the default value of 999 meaning it is null set current tank speed accordingly
					if (angleChosen != 999) {
						enemy.setSpeedX((int) ((enemy.speed * (float) Math.sin(Math.toRadians(angleChosen))) + .5));
						enemy.setSpeedY((int) ((enemy.speed * (float) Math.cos(Math.toRadians(angleChosen))) + .5));		
					}
				}
				// MODE #2: Random Grid Based Movement - if the enemy is touching a wall to get unstuck 
				else {
					// find the closest grid coordinate to the current tank
					int[] enemyGridCoord = getClosestGridCoords(enemy.getX(), enemy.getY());
					int enemyCoordX = enemyGridCoord[0];
					int enemyCoordY = enemyGridCoord[1];
					ArrayList<int[]> possibleNextCoords = new ArrayList<int[]> ();
					// use a surrounding tiles list to check for walls and possible moves
					int[][] surroundingTiles = {{-1, -1}, {-1, 0}, {-1, 1}, {0, -1}, {0, 1},{1, -1},{1, 0}, {1, 1}};
					for (int[] dist : surroundingTiles) {
						// try and catch for when a tile is called that is out of bounds
						try {
							if (wallArr[enemyCoordX+dist[0]][enemyCoordY+dist[1]] == 0) {
								int[] coord = {enemyCoordX+dist[0], enemyCoordY+dist[1]};
								possibleNextCoords.add(coord);
							}												
						} catch (ArrayIndexOutOfBoundsException e) {
							// Print message when a tile is called that is out of bounds
//							System.out.println("Out of Bounds");
						}
					}
					Random random = new Random();			
					if (possibleNextCoords.size() > 1) {
						// generate a random number to choose a possible grid coordinate
						int randomSlot = random.nextInt(possibleNextCoords.size()-1);
						int[] selectedCoord = possibleNextCoords.get(randomSlot);
						// convert the grid coordinate to x and y coordinates
						int targetX = selectedCoord[0] * TILE_SIZE;
						int targetY = selectedCoord[1] * TILE_SIZE;
						// set course for the target square using the found angle
						double angle = getAngleTo(enemy.getX(), enemy.getY(), targetX, targetY);
						enemy.setSpeedX((int) ((enemy.speed * (float) Math.sin(Math.toRadians(angle))) + .5));
						enemy.setSpeedY((int) ((enemy.speed * (float) Math.cos(Math.toRadians(angle))) + .5));						
					}
				}
			}
			// reset delta
			delta = 0;			
			decisionCount++;
		}

		if(timer >= 1000000000) {
			// Print out how many movement decision periods have been made in the second for tuning and debugging
//			System.out.println("Movement Decision Periods This Second: " + drawCount);
			decisionCount = 0;
			timer = 0;
		}
	}
	
//	/** 
//	 * Method adds objects dictated from the Map object's board array to the JFrame.
//	 * pre: board.length != 0, board[0].length != 0, numEnemies >= 0
//	 * post: items of board array should appear on screen including wall and tank objects
//	 */
//	public void generateNewBoard(int numEnemies) {
//		// have a generate new map with the player's position in mind 
//		int[][] boardArr = board.getCurrentRoom().generateNewMap(numEnemies);
//		wallArr = board.getWallArray();
//
//		for (int row=0; row<=boardArr.length-1; row++){
//			for (int col=0; col<=boardArr[0].length-1; col++){
//				// wall=1
//				if (boardArr[row][col] == 1) {
//					Wall wall = new Wall(row*TILE_SIZE, col*TILE_SIZE, TILE_SIZE, TILE_SIZE);
//					wallList.add(wall);
//					add(wall);
//				}
//				// player=2
//				if (boardArr[row][col] == 2) {
//					player = new Player(row*TILE_SIZE+TILE_SIZE/2, col*TILE_SIZE+TILE_SIZE/2, TILE_SIZE, TILE_SIZE, this);
//					player.setHealth(playerHealth+playerHeal);
//					add(player);
//				}
//				// enemy=3
////				if (boardArr[row][col] == 3) {
////					enemy = new Tank(row*TILE_SIZE+TILE_SIZE/2, col*TILE_SIZE+TILE_SIZE/2, TILE_SIZE, TILE_SIZE, this);
////					add(enemy);
////					getEnemyList().add(enemy);
////				}			
//			}			
//		}
//		levelUp();
//	}
//	
	public void drawRoomBoard(Room room) {
		// have a generate new map with the player's position in mind 
		int[][] boardArr = room.getArray();
		wallArr = room.getWallArray();

		System.out.println("Stuff");
		for (int row=0; row<=boardArr.length-1; row++){
			for (int col=0; col<=boardArr[0].length-1; col++){
				// wall=1
				if (boardArr[row][col] == 1) {
					Wall wall = new Wall(row*TILE_SIZE, col*TILE_SIZE, TILE_SIZE, TILE_SIZE);
					wallList.add(wall);
					add(wall);
				}
				// player=2
				if (boardArr[row][col] == 2) {
					player = new Player(row*TILE_SIZE+TILE_SIZE/2, col*TILE_SIZE+TILE_SIZE/2, TILE_SIZE, TILE_SIZE, this);
					player.setHealth(playerHealth+playerHeal);
					add(player);
				}
				// enemy=3
				if (boardArr[row][col] == 3) {
					enemy = new Tank(row*TILE_SIZE+TILE_SIZE/2, col*TILE_SIZE+TILE_SIZE/2, TILE_SIZE, TILE_SIZE, this);
					add(enemy);
					getEnemyList().add(enemy);
				}			
			}			
		}
		levelUp();
	}

	public void checkRoomChange() {
		if (player.getY() < TILE_SIZE/2) {
			gameMap.getTopRoom();
			clearEntities();
			drawRoomBoard(gameMap.getCurrentRoom()); // should be no args
			player = new Player(MAX_SCREEN_ROW/2*TILE_SIZE+TILE_SIZE/2,MAX_SCREEN_COL*TILE_SIZE-TILE_SIZE/2, TILE_SIZE, TILE_SIZE, this);
			player.setHealth(playerHealth+playerHeal);
			add(player);
			// should also accommodate enemy spawns and how many to spawn
		}
		if (player.getY() > TILE_SIZE*MAX_SCREEN_COL-TILE_SIZE/2) {
//			gameMap.getLeftRoom();
			gameMap.getBottomRoom();
			clearEntities();
			drawRoomBoard(gameMap.getCurrentRoom());
			player = new Player( MAX_SCREEN_ROW/2*TILE_SIZE+TILE_SIZE/2, TILE_SIZE+TILE_SIZE/2, TILE_SIZE, TILE_SIZE, this);
			
			player.setHealth(playerHealth+playerHeal);
			add(player);
			
		}
		if (player.getX() < TILE_SIZE/2) { // fix labels for rows and columns
			gameMap.getLeftRoom();
			clearEntities();
			drawRoomBoard(gameMap.getCurrentRoom());
			player = new Player(MAX_SCREEN_ROW*TILE_SIZE-TILE_SIZE/2, MAX_SCREEN_COL/2*TILE_SIZE+TILE_SIZE/2, TILE_SIZE, TILE_SIZE, this);
			
			player.setHealth(playerHealth+playerHeal);
			add(player);
		}
		if (player.getX() > TILE_SIZE*MAX_SCREEN_COL-TILE_SIZE/2) {
			gameMap.getRightRoom();
			clearEntities();
			drawRoomBoard(gameMap.getCurrentRoom());
			player = new Player(1*TILE_SIZE+TILE_SIZE/2, MAX_SCREEN_COL/2*TILE_SIZE+TILE_SIZE/2, TILE_SIZE, TILE_SIZE, this);
			player.setHealth(playerHealth+playerHeal);
			add(player);
		}
	}
	
	/** 
	 * Method generates new enemies at new spawns keeping the existing map layout
	 * pre: board.length != 0, board[0].length != 0, numEnemies > 0
	 * post: new enemies should be made
	 */
	public void generateNewEnemies(int numEnemies) {
		//	spawn new enemies in new enemy spawn locations
		int[][] boardArr = gameMap.getCurrentRoom().generateNewEnemySpawns(numEnemies);

		for (int row=0; row<=boardArr.length-1; row++){
			for (int col=0; col<=boardArr[0].length-1; col++){
				if (boardArr[row][col] == 3) {
					enemy = new Tank(row*TILE_SIZE+TILE_SIZE/2, col*TILE_SIZE+TILE_SIZE/2, TILE_SIZE, TILE_SIZE, this);
					add(enemy);
					getEnemyList().add(enemy);
				}			
			}			
		}
	}
	
	/** 
	 * Method clears all GameObjects except for the player from the screen and resets their respective lists
	 * pre: player != null
	 * post: a screen with no walls or enemies retaining the GUI labels and player, emptied entity lists
	 */
	public void clearEntities() {
		//	clear all entities
		this.getContentPane().removeAll();
		this.repaint();
		setProjList(new ArrayList<Projectile>());
		setEnemyProjList(new ArrayList<Projectile>());
		setEnemyList(new ArrayList<Tank>());
		wallList = new ArrayList<Wall>();
		this.add(scoreLabel);
		this.add(healthLabel);
		player.setHealth(playerHealth);
		this.add(levelLabel);
	}

	/** 
	 * Method draws a line of invisible ray objects and adds them to the ray list using an angle, distance, steps and a start point
	 * pre: steps > 0, distance > 0
	 * post: a line drawn with ray objects
	 */
	public void drawLine(double angle, int distance, int steps, int startX, int startY) {		
		double stepDistance = distance/steps;

		int endX = (int) (startX+((distance * (float) Math.sin(Math.toRadians(angle))) + .5));
		int endY = (int) (startY+((distance * (float) Math.cos(Math.toRadians(angle))) + .5));

		// i = 2 skips the rays at the origin
		for (int i = 2; i<steps; i++) {
			int rayX = (int) (startX+((stepDistance * i * (float) Math.sin(Math.toRadians(angle))) + .5));
			int rayY = (int) (startY+((stepDistance * i * (float) Math.cos(Math.toRadians(angle))) + .5));

			Ray fillerRay = new Ray(rayX, rayY, angle, i, this);
			rayList.add(fillerRay);
			add(fillerRay);
		}

		Ray rayEnd = new Ray(endX, endY, angle, steps, this);
		rayList.add(rayEnd);
		add(rayEnd);

//		for (Ray ray : rayList) {
//			ray.setVisible(false);
//		}		
		repaint();
	}
	
	/** 
	 * Overloaded method draws a line of invisible ray objects and adds them to the ray list using steps and a start and end point
	 * pre: steps > 0, distance > 0
	 * post: a line drawn with ray objects
	 */
	// max length is there to differentiate the methods during calls
	public void drawLine(int steps, int maxLength, int startX, int startY, int endX, int endY) {		
		double angle = getAngleTo(startX, startY, endX, endY);
		double distance =  Math.sqrt(Math.pow(endX-startX, 2)+ Math.pow(endY-startY, 2));
		double stepDistance = distance/steps;

		// i = 1 skips the ray at the origin
		for (int i = 2; i<steps; i++) {
			//			System.out.println("drawing radial rays...");
			int rayX = (int) (startX+((stepDistance * i * (float) Math.sin(Math.toRadians(angle))) + .5));
			int rayY = (int) (startY+((stepDistance * i * (float) Math.cos(Math.toRadians(angle))) + .5));

			Ray fillerRay = new Ray(rayX, rayY, angle, i, this);
			rayList.add(fillerRay);
			add(fillerRay);
		}

		Ray rayEnd = new Ray(endX, endY, angle, steps, this);
		rayList.add(rayEnd);
		add(rayEnd);


		for (Ray ray : rayList) {
			ray.setVisible(false);
		}		
		repaint();
	}

	/** 
	 * Method returns the closest grid coords to an x,y coordinate using the game's tile size
	 * pre: TILE_SIZE > 0
	 * post: a 2 length 2d list including the x and y coordinate of the closest grid coordinate
	 */
	public int[] getClosestGridCoords(int x, int y) {
		int gridX = x/TILE_SIZE;
		int gridY = y/TILE_SIZE;
		int[] gridCoord = {gridX, gridY};
		return gridCoord;		
	}
	

	public double getAngleTo(int startX, int startY, int endX, int endY) {
		double angle = (Math.atan2(endX - startX, endY - startY) * 180) / Math.PI;
		return angle;
	}

	public void levelUp() {
		level += 1;
		
		if (level >8) {
			enemySpeedScale *= 1.01;
			enemyBulletSpeedScale *= 1.10;	
		}
		
		scoreMulti *= 1.05;
		enemyFireRateScale *= 1.05;
		enemyDamageScale *= 1.15;
		numEnemyScale *= 1.30;
		numEnemies = (int) numEnemyScale;
		
		for (Tank enemy : getEnemyList()) {
			enemy.speed *= enemySpeedScale;
			enemy.setShotDelay((int) (enemy.getShotDelay() / enemyFireRateScale));
			enemy.setProjectileSpeed((int) (enemy.getProjectileSpeed() * enemyBulletSpeedScale));
			enemy.setDamage((int) (enemy.getDamage() * enemyDamageScale));
		}
		
//		levelLabel.setText("LVL: " + Integer.toString(level));
		levelLabel.setText("ROOM: " + Integer.toString(gameMap.getCurrentRoom().getRoomID()));
	}

	/** 
	 * Method prints out the keys and values of a dictionary, source: https://www.javatpoint.com/dictionary-class-in-java 
	 * pre: dict.size() > 0
	 * post: keys and values of a dictionary printed
	 */
	public void outputDictionaryValues(Dictionary dict){
		//loop iterate over the values stored in the dictionary  
		for(Enumeration enm = dict.elements(); enm.hasMoreElements();)  
		{  
			//prints the value      
			System.out.println(enm.nextElement());  
		}  
		System.out.println("\nDictionary keys are: \n");  
		//loop iterate over the keys stored in the dictionary  
		for(Enumeration enm = dict.keys(); enm.hasMoreElements();)  
		{  
			//prints the keys      
			System.out.println(enm.nextElement()); 

		} 

		System.out.print("DICTIONARY PRINTING FINISHED");
	}
	
	/** 
	 * Method checks if the player's score is valid for a top 10 high score entry by comparing the 10th position's score in the high score list
	 * pre: none
	 * post: nothing happens if the player's score is invalid for a high score. Displays input prompt for user's name if the user has a valid highscore and adds it to the highscore text file in form of username separated by a space and score
	 */
	public void checkHighScore(int score) {
		// check if score is valid for a high score entry
		int minPosition = 10;
		ArrayList<String[]> scores = getHighScores();
		int minScore;
		// if the high score list is less than 10, the user gets a free entry as long as their score is not zero
		if (scores.size() <= 10) {			
			minScore = 0;			
		} else {
			minScore = Integer.parseInt(scores.get(minPosition-1)[1]);
		}
//		System.out.println("MINIMUM HIGH SCORE:" + minScore);
		// if the score is greater than the minimum required score create a new high score entry
		if (score > minScore) {
			// initialize user name string outside of try and catch
			String username;
			try {
				username = JOptionPane.showInputDialog(this, "Please enter username with no spaces:", "CONGRATULATIONS NEW HIGHSCORE!", JOptionPane.QUESTION_MESSAGE);				
				// remove spaces from the name to remove complications when reading from text file
				username = username.replaceAll("\\s", "");
			} catch (NullPointerException e) {
				// if the user did not input a valid user name give the entry a default name of "noname"
				username = "noname";
			}				
			String[] newEntry = {username, Integer.toString(score)};
			scores.add(newEntry);
			addHighScores(scores);
		}
	}

	/** 
	 * Method sorts the score list and adds the players score in accordingly, then adds the score list to the high score text file.
	 * pre: a 2d scores list with names and respective scores separated
	 * post: the high score text file is updated
	 */
	public void addHighScores(ArrayList<String[]> scores) {
		//sort scores descending order
		Collections.sort(scores, new Comparator<String[]>() {
			@Override
			public int compare(String[] entry1, String[] entry2) {
				int value1 = Integer.parseInt(entry1[1]);
				int value2 = Integer.parseInt(entry2[1]);
				return value2-value1;
			}
		});
		
		String fileContent = "";
		for (String[] entry : scores) {
			fileContent = fileContent.concat(entry[0] + " " + entry[1] + "\n");
		}
		
		FileWriter writer;
		try {
			writer = new FileWriter("highscores.txt");
			writer.write(fileContent);
			writer.close();
			
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	/** 
	 * Method gets the high scores from the text file on to a 2d array.
	 * pre: scores in the text file are recorded string user name with no spaces separated by a space and score.
	 * post: an array list of the different high score entries
	 */
	public ArrayList<String[]> getHighScores() {
		File file = new File("highscores.txt");
		Scanner input = null;
		ArrayList<String[]> scores = new ArrayList<String[]>();
		
		try {
			input = new Scanner(file);
			String[] entry = new String[2];
			while (input.hasNextLine()) {
				String message = input.nextLine();
//				System.out.println(message);
				entry = message.split(" ");
				scores.add(entry);
//				System.out.println(entry[0] + " " + entry[1]);
			}
		} catch (FileNotFoundException e) {
			System.out.println("File not found.");
		}
		return scores;
	}
	
	/** 
	 * Method calls the game over dialogue.
	 * pre: game stopped
	 * post: game over dialogue displayed
	 */
	public void gameOver() {
		_WinDialog d = new _WinDialog(this, "You Died.");
		d.setVisible(true);
	}

	/** 
	 * Method displays game over / win dialog.
	 * pre: game stopped
	 * post: game over dialogue displayed in the center of the JFrame
	 */
	public class _WinDialog extends JDialog {
		JButton ok = new JButton("Back to Main Menu.");
		_WinDialog(GameFrame owner, String title) {
			super(owner, title);
			Rectangle r = owner.getBounds();
			setSize(200, 100);
			setLocation(r.x + r.width / 2 - 100, r.y + r.height / 2 - 50);
			getContentPane().add(ok);
			ok.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					_WinDialog.this.setVisible(false);
					owner.gameOver = true;
				}
			});
		}		
	}	
	
	// getters and setters
	public ArrayList<Projectile> getProjList() {
		return projList;
	}

	public void setProjList(ArrayList<Projectile> projList) {
		this.projList = projList;
	}

	public ArrayList<Projectile> getEnemyProjList() {
		return enemyProjList;
	}

	public void setEnemyProjList(ArrayList<Projectile> enemyProjList) {
		this.enemyProjList = enemyProjList;
	}	
	
	public ArrayList<Tank> getEnemyList() {
		return enemyList;
	}

	public void setEnemyList(ArrayList<Tank> enemyList) {
		this.enemyList = enemyList;
	}
}
