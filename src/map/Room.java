 package map;

import java.util.Random;

public class Room {
	private int[][] board;
	// should create an array of tiles
	// tile assignments values within the board array
	private final int BLANK_SQUARE = 0;
	private final int WALL_SQUARE = 1;
	private final int PLAYER_SQUARE = 2;
	private final int ENEMY_SQUARE = 3;
	private final int PLAYER_SPAWN_AREA_SQUARE = 4;
	private final int ENTITY_AREA = 5;
	private final int ENTRANCE_SQUARE = 6;
	
	
	// create a new random class to use for board generation
	private Random r = new Random();
	private int roomID = 0;
	
	// create empty board with constructor
	public Room(int rows, int cols, int id) {
		roomID = id;
		board = new int[rows][cols];
		board = generateNewMap(0);
		System.out.println("BOARD HAS BEEN MADE");
	}

	/** 
	 * Method generates a new board array filled with a specified amount of enemies, random walls and a player spawn.
	 * pre: numEnemies >= 0, board.length != 0, board[0].length != 0
	 * post: a board array of dimensions rows by cols returned
	 */
	public int[][] generateNewMap(int numEnemies){
		int enemyQueue = numEnemies;
		for (int i = 0; i < this.board.length; i++) {
		    for (int j = 0; j < this.board[i].length; j++) {
		        this.board[i][j] = BLANK_SQUARE;
		    }
		}
		// generate outer walls of the game board
		this.board = generateSimpleRectangle();

		// randomize player spawns
		int playerQueue = 1;
		// try random locations for the player
		while (playerQueue > 0) {
			int locX = r.nextInt(this.board[0].length);
			int locY = r.nextInt(this.board.length);
			// check if the location is a blank square
			if (this.board[locY][locX] == BLANK_SQUARE) {
				this.board[locY][locX] = PLAYER_SQUARE;
				// player spawn area square where no enemies are allowed in
				int spawnArea = 4;
				for (int x=locX-spawnArea; x<= locX+spawnArea; x++) {
					try {
						for (int y=locY-spawnArea; y<=locY+spawnArea; y++) {
							try {
								if (this.board[y][x] == BLANK_SQUARE) {
									this.board[y][x] = PLAYER_SPAWN_AREA_SQUARE;
								}														
							} catch (ArrayIndexOutOfBoundsException e) {
								// Print message when a tile is called that is out of bounds
//								System.out.println("Out of Bounds");
							}								
						}
					} catch (ArrayIndexOutOfBoundsException e) {
						// Print message when a tile is called that is out of bounds
//						System.out.println("Out of Bounds");
					}
				}
				// player protected area to avoid trapped spawns
				int protectedArea = 2;
				for (int x=locX-protectedArea; x<= locX+protectedArea; x++) {
					try {
						for (int y=locY-protectedArea; y<=locY+protectedArea; y++) {
							try {
								if (this.board[y][x] == PLAYER_SPAWN_AREA_SQUARE) {
									this.board[y][x] = ENTITY_AREA;
								}														
							} catch (ArrayIndexOutOfBoundsException e) {
								// Print message when a tile is called that is out of bounds
//								System.out.println("Out of Bounds");
							}								
						}
					} catch (ArrayIndexOutOfBoundsException e) {
						// Print message when a tile is called that is out of bounds
//						System.out.println("Out of Bounds");
					}
				}
				playerQueue--;
			}
		}
		generateNewEnemySpawns(numEnemies);		
		generateRandomWalls();
//		showMatrix(this.board);	
		return this.board;
	}

	/** 
	 * Method removes old enemy spawns and generates new enemy spawn points onto the board array.
	 * pre: numEnemies >= 0, board.length != 0, board[0].length != 0
	 * post: a new board array is returned
	 */
	public int[][] generateNewEnemySpawns(int numEnemies){
		for (int row=0; row<=this.board.length-1; row++){
			for (int col=0; col<=this.board[0].length-1; col++){
				if (this.board[row][col] == ENEMY_SQUARE) {
//					System.out.println("current tile:" + this.board[locY][locX]);
					this.board[row][col] = BLANK_SQUARE;
				}
			}
		}
		int enemyQueue = numEnemies;
		// randomize enemy spawns
		while (enemyQueue > 0) {
			// generate a random enemy location and check if the square is open
			int locX = r.nextInt(this.board[0].length);
			int locY = r.nextInt(this.board.length);
			if (this.board[locY][locX] == BLANK_SQUARE) {
				// surround the enemy with protected area to avoid trapped spawns
				int protectedArea = 1;
				for (int x=locX-protectedArea; x<= locX+protectedArea; x++) {
					try {
						for (int y=locY-protectedArea; y<=locY+protectedArea; y++) {
							try {
								if (this.board[y][x] == BLANK_SQUARE) {
									this.board[y][x] = ENTITY_AREA;
								}														
							} catch (ArrayIndexOutOfBoundsException e) {
								// Print message when a tile is called that is out of bounds
//								System.out.println("Out of Bounds");
							}								
						}
					} catch (ArrayIndexOutOfBoundsException e) {
						// Print message when a tile is called that is out of bounds
//						System.out.println("Out of Bounds");
					}
				}
				this.board[locY][locX] = ENEMY_SQUARE;
				enemyQueue--;
			}
		}
//		showMatrix(this.board);
		return board;
	}
	
	/** 
	 * Method generates random walls onto the board array.
	 * pre: board.length != 0, board[0].length != 0
	 * post: the private board array has been modified to include random walls
	 */
	private int[][] generateRandomWalls(){
		int wallPercentage = 12;
		int wallQueue = (board.length * board[0].length)*wallPercentage/100;
		
		while (wallQueue > BLANK_SQUARE) {

			int locX = r.nextInt(this.board[0].length);
			int locY = r.nextInt(this.board.length);

			if (this.board[locY][locX] == BLANK_SQUARE || this.board[locY][locX] == PLAYER_SPAWN_AREA_SQUARE) {
				this.board[locY][locX] = WALL_SQUARE;
				wallQueue--;
			}
		}
		return board;
	}

	/** 
	 * Method generates a checkerboard array pattern for the board array to showcase the tiles displayed on screen.
	 * pre: board.length != 0, board[0].length != 0
	 * post: board is edited with checker pattern and returned
	 */
	private int[][] generateCheckerBoard() {
		for (int row=0; row<=this.board.length-1; row++){
			for (int col=0; col<=this.board[0].length-1; col++){
				System.out.println(row + ", " + col);
				if ((row % 2 == 0) && (col % 2 == 0)) {
					this.board[row][col] = WALL_SQUARE;
				}
				else if ((row % 2 != 0) && (col % 2 != 0)) {
					this.board[row][col] = WALL_SQUARE;
				}
			}			
		}
		return board;
	}
	
	/** 
	 * Method creates a wall array using the board array's locations of walls.
	 * pre: board.length != 0, board[0].length != 0
	 * post: wall array of board array's dimensions returned with 1s and 0s
	 */
	public int[][] getWallArray() {
		int[][] wallArray = new int[board.length][board[0].length];
		
		for (int row=0; row<=this.board.length-1; row++){
			for (int col=0; col<=this.board[0].length-1; col++){
				if (this.board[row][col] == WALL_SQUARE) {
					wallArray[row][col] = WALL_SQUARE;
				}
			}
		}
		return wallArray;
	}
	
	/** 
	 * Method generates walls to form a simple rectangular perimeter around the specified board dimensions.
	 * pre: board.length != 0, board[0].length != 0
	 * post: the private board array has been modified to include random walls
	 */
	private int[][] generateSimpleRectangle(){		
//		System.out.println("creating rectangle");
		// Set walls on the left side of the rectangle
		for (int row=0; row<=board.length-1; row++){
			board[row][0] = WALL_SQUARE;
		}
		// Set walls on the right side of the rectangle
		for (int row=0; row<=board.length-1; row++){
			board[row][board[0].length-1] = WALL_SQUARE;
		}
		// Set walls on the top side of the rectangle
		for (int col=0; col<=board[0].length-1; col++){
			board[0][col] = WALL_SQUARE;
		}
		// Set walls on the bottom side of the rectangle
		for (int col=0; col<=board[0].length-1; col++){
			board[board.length-1][col] = WALL_SQUARE;
		}
		return board;
	}
	public void createLeftEntrance(){
		for (int row=8; row<=board.length-1-8; row++){
			board[row][0] = ENTRANCE_SQUARE;
		}
		for (int row=8; row<=board.length-1-8; row++){
			board[row][1] = BLANK_SQUARE;
		}
	}
	public void createRightEntrance(){
		for (int row=8; row<=board.length-1-8; row++){
			board[row][board[0].length-1] = ENTRANCE_SQUARE;
		}
		for (int row=8; row<=board.length-1-8; row++){
			board[row][board[0].length-2] = BLANK_SQUARE;
		}
	}	
	public void createTopEntrance(){
		for (int col=8; col<=board[0].length-1-8; col++){
			board[0][col] = ENTRANCE_SQUARE;
		}
		for (int col=8; col<=board[0].length-1-8; col++){
			board[1][col] = BLANK_SQUARE;
		}
	}
	public void createBottomEntrance(){
		for (int col=8; col<=board[0].length-1-8; col++){
			board[board.length-1][col] = ENTRANCE_SQUARE;
		}
		for (int col=8; col<=board[0].length-1-8; col++){
			board[board.length-2][col] = BLANK_SQUARE;
		}
	}
	public int[][] getArray(){
		return board;
	}

	public int getRoomID() {
		return roomID;
	}



	
	
}
