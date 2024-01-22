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

import ai.PathFinder;
import entity.Player;
import entity.Projectile;
import entity.Ray;
import entity.Tank;
import entity.Wall;
import handlers.KeyHandler;
import handlers.MouseHandler;
import labels.TimerLabel;
import map.GameMap;
import map.RoomPlan;
import templates.Game;

////// REPAIR BROKEN LOAD STATES
/* GameFrame manages the majority of the game's logic and object interactions */
public class GameFrame extends Game {
	//SCREEN SETTINGS
	
	final int originalTileSize = 16; // fix game doesn't always boot up.
	final int SCALE = 3;
	public final int TILE_SIZE = originalTileSize * SCALE; // 48 * 48 tile
	public final int MAX_SCREEN_ROW = 20;
	public final int MAX_SCREEN_COL = 20;  
	final int SCREEN_WIDTH = TILE_SIZE * MAX_SCREEN_ROW; // 48 * 16 = 768
	final int SCREEN_HEIGHT = TILE_SIZE * MAX_SCREEN_COL; //48 * 12 = 576
	
	private GameMap gameMap = new GameMap(MAX_SCREEN_ROW, MAX_SCREEN_COL, 25);	
//	private Room currentRoom = ;
	private double enemyMovementInterval; 
	private double delta;
	private long lastTime;
	private long currentTime;
	// decision count to show how many enemy movement decision periods have been made in a second for debugging/tuning
	private int decisionCount;
	private int timer;
//	private double enemySpeedScale; // should be static variables in tank
//	private double enemyBulletSpeedScale;
//	private double enemyFireRateScale;
//	private double enemyDamageScale;
//	private int numEnemies;
//	private double numEnemyScale;
	public boolean gameOver = false;
	public JLabel scoreLabel;
	public JLabel healthLabel;	
	public JLabel levelLabel;
	public JLabel timeLabel; // treat as normal label
	TimerLabel gameTime;
	
	int score;
	private int level;
	int playerHealth;
	private int playerHeal;
	private double scoreMulti;
	Player player;
	InteractionHandler ih;
	public PathFinder pFinder = new PathFinder(this);
	private boolean firstTime;
	
	
//	private Tank enemy;
	
	// create tile array if something is not in the tile array collisions do nothing
	private ArrayList<Projectile> projList = new ArrayList<Projectile>();
	ArrayList<Projectile> enemyProjList = new ArrayList<Projectile>();
	ArrayList<Tank> enemyList = new ArrayList<Tank>();
	ArrayList<Wall> wallList = new ArrayList<Wall>();
	public ArrayList<Ray> rayList = new ArrayList<Ray>(); // CHANGE LATTER
	// create wall array
	private int enemyRaySteps = 4;
	private int[][] wallArr;
	private int[] anglesToCheck = {0, 45, 90, 135, 180, -90, -45, -135};

	KeyHandler keyH = new KeyHandler();
	public MouseHandler mouseH = new MouseHandler();

	public GameFrame() {
		
		// pre initialize player to prevent null pointers
		player = new Player(0*TILE_SIZE+TILE_SIZE/2, 0*TILE_SIZE+TILE_SIZE/2, TILE_SIZE, TILE_SIZE, this);
		player.setHealth(playerHealth);
		add(player);
		
		
		// initialize all variables in the constructor for when the game is restarted as a new GameFrame object
		enemyMovementInterval = 1000000000/15; // one second
		delta = 0;
		lastTime = System.nanoTime();
		decisionCount = 0;
		timer = 0;
//		enemySpeedScale = 1;
//		enemyBulletSpeedScale = 1;
//		enemyFireRateScale = 6;
//		enemyDamageScale = 0.1;
//		numEnemies = 1;
//		numEnemyScale = 1;
		gameOver = false;
		setScore(0);
		level = 0;
		playerHealth = 250;
		setPlayerHeal(15); // normal 15
		setScoreMulti(1);
		projList = new ArrayList<Projectile>();
		enemyProjList = new ArrayList<Projectile>();
		setEnemyList(new ArrayList<Tank>());
		wallList = new ArrayList<Wall>();
		rayList = new ArrayList<Ray>();
		enemyRaySteps = 3; // try lowering
		firstTime = true;
		
		// game GUI
		scoreLabel = new JLabel("SCORE: 0");
		scoreLabel.setForeground(Color.BLACK);
//		scoreLabel.setBounds(50, 0, 400, 48);
		scoreLabel.setFont(new Font("Sans-serif", Font.BOLD, 48));
		add(scoreLabel);

		healthLabel = new JLabel("HP: " + playerHealth);
		healthLabel.setForeground(Color.BLACK);
		healthLabel.setBounds(50, 910, 400, 48);
		healthLabel.setFont(new Font("Sans-serif", Font.BOLD, 48));
		add(healthLabel);

		levelLabel = new JLabel("LEVEL: 01");
		levelLabel.setForeground(Color.BLACK);
		levelLabel.setBounds(700, 910, 400, 48);
		levelLabel.setFont(new Font("Sans-serif", Font.BOLD, 48));
		add(levelLabel);
		
		timeLabel = new JLabel("RUN: 00:00");
		timeLabel.setForeground(Color.BLACK);
		timeLabel.setBounds(50, 0, 400, 48);
		timeLabel.setFont(new Font("Sans-serif", Font.BOLD, 48));
		add(timeLabel);
		
		gameTime = new TimerLabel();

				
		this.addKeyListener(keyH);
		this.addMouseListener(mouseH);

		// yield to actual screen size
		this.setSize(SCREEN_WIDTH+16, SCREEN_HEIGHT+62);		
		this.setBackground(Color.BLACK);
		this.addKeyListener(keyH);
		this.addMouseListener(mouseH);
		this.addMouseMotionListener(mouseH);
		this.setFocusable(true);
		this.setResizable(true);
		System.out.println("game started");
		
	}

	/** setup() method sets a delay for the act method */
	@Override
	public void setup() {
		// slow down the timer to 60 fps
		System.out.println("GENERATING BOARD");
		drawRoomBoard(getGameMap().getCurrentRoom());
		ih = new InteractionHandler(this);
		setDelay(16);
		gameTime.reset();
	} 

	/** act() method contains core game logic and is called every game update */
	@Override
	public void act() {
		
		if (gameTime.secondPassed()) {
			timeLabel.setText("RUN: " + gameTime.getFormattedTime());			
		}
		// Player Movement
		// check walls the player currently collides with
		int originalX = getPlayer().getX();
		int originalY = getPlayer().getY();
		// initialize boolean variables for vertical and horizontal collisions
		boolean wallVertical = false;
		boolean wallHorizontal = false;
		// Listen for WASD Key Presses
		if(WKeyPressed()){
			getPlayer().moveUp();
		}
		if(AKeyPressed()){
			getPlayer().moveLeft();
		}
		if(SKeyPressed()){
			getPlayer().moveDown();
		}
		if(DKeyPressed()){
			getPlayer().moveRight();
		}
		// check if the player has made a valid move
		for (Wall wall : wallList) {
			if (getPlayer().collides(wall)) {
				int objectCenterX = wall.getX();
				int objectCenterY = wall.getY();
				int centerX = getPlayer().getX();
				int centerY = getPlayer().getY();
				// collision code accounts for how far the player might be into a wall based on its speed repurposed from: https://happycoding.io/tutorials/processing/collision-detection
				//check horizontal collision 
				if (centerX + TILE_SIZE + getPlayer().speed > objectCenterX && centerX + getPlayer().speed < objectCenterX + TILE_SIZE && centerY + TILE_SIZE > objectCenterY && centerY < objectCenterY + TILE_SIZE) {
					wallVertical = true;
				}
				//check vertical collision
				if (centerX + TILE_SIZE> objectCenterX && centerX< objectCenterX + TILE_SIZE && centerY + TILE_SIZE + getPlayer().speed > objectCenterY && centerY + getPlayer().speed < objectCenterY + TILE_SIZE) {
					wallHorizontal = true;
				}
			}
		}
		// If the player's move is illegal (hits a wall) move the player back to its original position in that axis.
		if ((wallHorizontal) || (wallVertical)) {
			getPlayer().setX(originalX);
			getPlayer().setY(originalY);
		}
		
		// Enemy Spawn Testing 
		if (keyH.spacePressed) {
			generateNewEnemies(1);
		}
		
		
		// Player Shooting
		if (((mouseH.mousePressed)) && getPlayer().shotReady()) {
			// find mouse shooting angle			
			double angle = getAngleTo(getPlayer().getCenterX(), getPlayer().getCenterY(), mouseH.mouseX, mouseH.mouseY);
			// create new projectile for the player and add it to the projectile list
			Projectile bullet = new Projectile(getPlayer().getCenterX(), getPlayer().getCenterY(), 10, angle, 20, getPlayer().getDamage(), Color.YELLOW); // WHY DOESNT THE PLAYER GET MODULAR BULLET SPEED
			getProjList().add(bullet);
			add(bullet);
			getPlayer().shootProjectile(angle);
//			for (Tank enemy : enemyList) {
//				enemy.setTarget(mouseH.mouseX/TILE_SIZE*TILE_SIZE, mouseH.mouseY/TILE_SIZE*TILE_SIZE);
//			}
			 /////////////////////////////////////////////////
		}
		
		ih.handleEnemies();
		ih.handleProjectiles();
		
		// update screen with removed bullets
		repaint();		

		// check if player has died
		if (getPlayer().getHealth() <= 0) {
			// update player health label
			healthLabel.setText("HP: " + 0);
			// stop game and display end dialog
			this.stopGame();
			this.checkHighScore(getScore());
			this.gameOver();
		}
		
		
		
		// enter the next level if all enemies have been killed
//		if (enemyList.size() == 0) {
//			clearEntities();
//			generateNewBoard(numEnemies);
//		}
		
		checkRoomChange();

		// updates per second style timer for enemy movement
		currentTime = System.nanoTime();
		delta += (currentTime - lastTime) / enemyMovementInterval; // how much time left until the next draw interval 
		timer += (currentTime - lastTime); // update timer that keeps tracks of when a second passes by the time elapsed
		lastTime = currentTime; // update last time
		
		// 
		

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
	public void drawRoomBoard(RoomPlan room) {
		
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
				// Only for first time generations
				if (boardArr[row][col] == 2 && firstTime) {
					player.setX(row*TILE_SIZE);
					player.setY(col*TILE_SIZE);
					player.setHealth(playerHealth);
					firstTime = false;
				}
				// enemy=3
				if (boardArr[row][col] == 3) {
					Tank enemy = new Tank(row*TILE_SIZE+TILE_SIZE/2, col*TILE_SIZE+TILE_SIZE/2, TILE_SIZE, TILE_SIZE, this);
					add(enemy);
					enemyList.add(enemy);
				}			
			}			
		}
		levelUp();
	}

	public void checkRoomChange() {
		if (getPlayer().getY() < TILE_SIZE/2) {
			getGameMap().enterTopRoom();
			clearEntities();
			drawRoomBoard(getGameMap().getCurrentRoom()); // should be no args
			setPlayer(new Player(MAX_SCREEN_ROW/2*TILE_SIZE+TILE_SIZE/2,MAX_SCREEN_COL*TILE_SIZE-TILE_SIZE/2, TILE_SIZE, TILE_SIZE, this));
			getPlayer().setHealth(playerHealth);
			add(getPlayer());
			// should also accommodate enemy spawns and how many to spawn
		}
		if (getPlayer().getY() > TILE_SIZE*MAX_SCREEN_COL-TILE_SIZE/2) {
//			gameMap.getLeftRoom();
			getGameMap().enterBottomRoom();
			clearEntities();
			drawRoomBoard(getGameMap().getCurrentRoom());
			setPlayer(new Player( MAX_SCREEN_ROW/2*TILE_SIZE+TILE_SIZE/2, TILE_SIZE+TILE_SIZE/2, TILE_SIZE, TILE_SIZE, this));
			
			getPlayer().setHealth(playerHealth+getPlayerHeal());
			add(getPlayer());
			
		}
		if (getPlayer().getX() < TILE_SIZE/2) { // fix labels for rows and columns
			getGameMap().enterLeftRoom();
			clearEntities();
			drawRoomBoard(getGameMap().getCurrentRoom());
			setPlayer(new Player(MAX_SCREEN_ROW*TILE_SIZE-TILE_SIZE/2, MAX_SCREEN_COL/2*TILE_SIZE+TILE_SIZE/2, TILE_SIZE, TILE_SIZE, this));
			
			getPlayer().setHealth(playerHealth+getPlayerHeal());
			add(getPlayer());
		}
		if (getPlayer().getX() > TILE_SIZE*MAX_SCREEN_COL-TILE_SIZE/2) {
			getGameMap().enterRightRoom();
			clearEntities();
			drawRoomBoard(getGameMap().getCurrentRoom());
			setPlayer(new Player(1*TILE_SIZE+TILE_SIZE/2, MAX_SCREEN_COL/2*TILE_SIZE+TILE_SIZE/2, TILE_SIZE, TILE_SIZE, this));
			getPlayer().setHealth(playerHealth+getPlayerHeal());
			add(getPlayer());
		}
	}
	
	/** 
	 * Method generates new enemies at new spawns keeping the existing map layout
	 * pre: board.length != 0, board[0].length != 0, numEnemies > 0
	 * post: new enemies should be made
	 */
	public void generateNewEnemies(int numEnemies) {
		//	spawn new enemies in new enemy spawn locations
		int[][] boardArr = getGameMap().getCurrentRoom().generateNewEnemySpawns(numEnemies);

		for (int row=0; row<=boardArr.length-1; row++){
			for (int col=0; col<=boardArr[0].length-1; col++){
				if (boardArr[row][col] == 3) {
					Tank enemy = new Tank(row*TILE_SIZE+TILE_SIZE/2, col*TILE_SIZE+TILE_SIZE/2, TILE_SIZE, TILE_SIZE, this);
					add(enemy);
					enemyList.add(enemy);
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
		getPlayer().setHealth(playerHealth);
		this.add(levelLabel);
		this.add(timeLabel);
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
			fillerRay.setVisible(false);
			
			rayList.add(fillerRay);
			add(fillerRay);
		}

		Ray rayEnd = new Ray(endX, endY, angle, steps, this);
		rayEnd.setVisible(true);
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
//		level += 1;
//		
//		if (level >8) {
//			enemySpeedScale *= 1.01;
//			enemyBulletSpeedScale *= 1.10;	
//		}
//		
//		scoreMulti *= 1.05;
//		enemyFireRateScale *= 1.05;
//		enemyDamageScale *= 1.15;
//		numEnemyScale *= 1.30;
//		numEnemies = (int) numEnemyScale;
//		
//		for (Tank enemy : enemyList) {
//			enemy.speed *= enemySpeedScale;
//			enemy.setShotDelay((int) (enemy.getShotDelay() / enemyFireRateScale));
//			enemy.setProjectileSpeed((int) (enemy.getProjectileSpeed() * enemyBulletSpeedScale));
//			enemy.setDamage((int) (enemy.getDamage() * enemyDamageScale));
//		}
		
//		levelLabel.setText("LVL: " + Integer.toString(level));
		levelLabel.setText("ROOM: " + Integer.toString(getGameMap().getCurrentRoom().getRoomID()));
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
	 * Method calls the game over dialogue.
	 * pre: game stopped
	 * post: game over dialogue displayed
	 */
	public void gameWon() {
		_WinDialog d = new _WinDialog(this, "You Escaped.");
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
	

	public void setEnemyList(ArrayList<Tank> enemyList) {
		this.enemyList = enemyList;
	}

	public GameMap getGameMap() {
		return gameMap;
	}

	public void setGameMap(GameMap gameMap) {
		this.gameMap = gameMap;
	}

	public Player getPlayer() {
		return player;
	}

	public void setPlayer(Player player) {
		this.player = player;
	}

	public int getScore() {
		return score;
	}

	public void setScore(int score) {
		this.score = score;
	}

	public double getScoreMulti() {
		return scoreMulti;
	}

	public void setScoreMulti(double scoreMulti) {
		this.scoreMulti = scoreMulti;
	}

	public int getPlayerHeal() {
		return playerHeal;
	}

	public void setPlayerHeal(int playerHeal) {
		this.playerHeal = playerHeal;
	}
}
