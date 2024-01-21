 package map;

import java.util.Random;

public class RoomPlan {
	private int[][] templateBoard;
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
	public RoomPlan(int rows, int cols, int id) {
		roomID = id;
		templateBoard = new int[rows][cols];
		templateBoard = generateNewMap(0);
		System.out.println("BOARD HAS BEEN MADE");
	}

	/** 
	 * Method generates a new board array filled with a specified amount of enemies, random walls and a player spawn.
	 * pre: numEnemies >= 0, board.length != 0, board[0].length != 0
	 * post: a board array of dimensions rows by cols returned
	 */
	public int[][] generateNewMap(int numEnemies) {
	    int enemyQueue = numEnemies;
	    for (int i = 0; i < this.templateBoard.length; i++) {
	        for (int j = 0; j < this.templateBoard[i].length; j++) {
	            this.templateBoard[i][j] = BLANK_SQUARE;
	        }
	    }
	    // generate outer walls of the game board
	    this.templateBoard = generateSimpleRectangle();

	    // randomize player spawns
	    int playerQueue = 1;
	    // try random locations for the player
	    while (playerQueue > 0) {
	        int locX = r.nextInt(this.templateBoard[0].length);
	        int locY = r.nextInt(this.templateBoard.length);
	        // check if the location is a blank square
	        if (locX >= 0 && locX < this.templateBoard[0].length && locY >= 0 && locY < this.templateBoard.length) {
	            if (this.templateBoard[locY][locX] == BLANK_SQUARE) {
	                this.templateBoard[locY][locX] = PLAYER_SQUARE;
	                // player spawn area square where no enemies are allowed in
	                int spawnArea = 4;
	                for (int x = Math.max(0, locX - spawnArea); x <= Math.min(this.templateBoard[0].length - 1, locX + spawnArea); x++) {
	                    for (int y = Math.max(0, locY - spawnArea); y <= Math.min(this.templateBoard.length - 1, locY + spawnArea); y++) {
	                        if (x >= 0 && x < this.templateBoard[0].length && y >= 0 && y < this.templateBoard.length) {
	                            if (this.templateBoard[y][x] == BLANK_SQUARE) {
	                                this.templateBoard[y][x] = PLAYER_SPAWN_AREA_SQUARE;
	                            }
	                        }
	                    }
	                }
	                // player protected area to avoid trapped spawns
	                int protectedArea = 2;
	                for (int x = Math.max(0, locX - protectedArea); x <= Math.min(this.templateBoard[0].length - 1, locX + protectedArea); x++) {
	                    for (int y = Math.max(0, locY - protectedArea); y <= Math.min(this.templateBoard.length - 1, locY + protectedArea); y++) {
	                        if (x >= 0 && x < this.templateBoard[0].length && y >= 0 && y < this.templateBoard.length) {
	                            if (this.templateBoard[y][x] == PLAYER_SPAWN_AREA_SQUARE) {
	                                this.templateBoard[y][x] = ENTITY_AREA;
	                            }
	                        }
	                    }
	                }
	                playerQueue--;
	            }
	        }
	    }
	    generateNewEnemySpawns(numEnemies);
	    generateRandomWalls();
//	    PowerPrint.showMatrix(board);
	    return this.templateBoard;
	}

	/** 
	 * Method removes old enemy spawns and generates new enemy spawn points onto the board array.
	 * pre: numEnemies >= 0, board.length != 0, board[0].length != 0
	 * post: a new board array is returned
	 */
	public int[][] generateNewEnemySpawns(int numEnemies){
		for (int row=0; row<=this.templateBoard.length-1; row++){
			for (int col=0; col<=this.templateBoard[0].length-1; col++){
				if (this.templateBoard[row][col] == ENEMY_SQUARE) {
//					System.out.println("current tile:" + this.board[locY][locX]);
					this.templateBoard[row][col] = BLANK_SQUARE;
				}
			}
		}
		int enemyQueue = numEnemies;
		// randomize enemy spawns
		while (enemyQueue > 0) {
		    // generate a random enemy location and check if the square is open
		    int locX = r.nextInt(this.templateBoard[0].length);
		    int locY = r.nextInt(this.templateBoard.length);
		    if (locX >= 0 && locX < this.templateBoard[0].length && locY >= 0 && locY < this.templateBoard.length) {
		        if (this.templateBoard[locY][locX] == BLANK_SQUARE) {
		            // surround the enemy with protected area to avoid trapped spawns
		            int protectedArea = 1;
		            for (int x = Math.max(0, locX - protectedArea); x <= Math.min(this.templateBoard[0].length - 1, locX + protectedArea); x++) {
		                for (int y = Math.max(0, locY - protectedArea); y <= Math.min(this.templateBoard.length - 1, locY + protectedArea); y++) {
		                    if (this.templateBoard[y][x] == BLANK_SQUARE) {
		                        this.templateBoard[y][x] = ENTITY_AREA;
		                    }
		                }
		            }
		            this.templateBoard[locY][locX] = ENEMY_SQUARE;
		            enemyQueue--;
		        }
		    }
		}
//		showMatrix(this.board);
		return templateBoard;
	}
	
	/** 
	 * Method generates random walls onto the board array.
	 * pre: board.length != 0, board[0].length != 0
	 * post: the private board array has been modified to include random walls
	 */
	private int[][] generateRandomWalls(){
		int wallPercentage = 12;
		int wallQueue = (templateBoard.length * templateBoard[0].length)*wallPercentage/100;
		
		while (wallQueue > BLANK_SQUARE) {

			int locX = r.nextInt(this.templateBoard[0].length);
			int locY = r.nextInt(this.templateBoard.length);

			if (this.templateBoard[locY][locX] == BLANK_SQUARE || this.templateBoard[locY][locX] == PLAYER_SPAWN_AREA_SQUARE) {
				this.templateBoard[locY][locX] = WALL_SQUARE;
				wallQueue--;
			}
		}
		return templateBoard;
	}

	/** 
	 * Method generates a checkerboard array pattern for the board array to showcase the tiles displayed on screen.
	 * pre: board.length != 0, board[0].length != 0
	 * post: board is edited with checker pattern and returned
	 */
	private int[][] generateCheckerBoard() {
		for (int row=0; row<=this.templateBoard.length-1; row++){
			for (int col=0; col<=this.templateBoard[0].length-1; col++){
				System.out.println(row + ", " + col);
				if ((row % 2 == 0) && (col % 2 == 0)) {
					this.templateBoard[row][col] = WALL_SQUARE;
				}
				else if ((row % 2 != 0) && (col % 2 != 0)) {
					this.templateBoard[row][col] = WALL_SQUARE;
				}
			}			
		}
		return templateBoard;
	}
	
	/** 
	 * Method creates a wall array using the board array's locations of walls.
	 * pre: board.length != 0, board[0].length != 0
	 * post: wall array of board array's dimensions returned with 1s and 0s
	 */
	public int[][] getWallArray() {
		int[][] wallArray = new int[templateBoard.length][templateBoard[0].length];
		
		for (int row=0; row<=this.templateBoard.length-1; row++){
			for (int col=0; col<=this.templateBoard[0].length-1; col++){
				if (this.templateBoard[row][col] == WALL_SQUARE) {
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
		for (int row=0; row<=templateBoard.length-1; row++){
			templateBoard[row][0] = WALL_SQUARE;
		}
		// Set walls on the right side of the rectangle
		for (int row=0; row<=templateBoard.length-1; row++){
			templateBoard[row][templateBoard[0].length-1] = WALL_SQUARE;
		}
		// Set walls on the top side of the rectangle
		for (int col=0; col<=templateBoard[0].length-1; col++){
			templateBoard[0][col] = WALL_SQUARE;
		}
		// Set walls on the bottom side of the rectangle
		for (int col=0; col<=templateBoard[0].length-1; col++){
			templateBoard[templateBoard.length-1][col] = WALL_SQUARE;
		}
		return templateBoard;
	}
	public void createLeftEntrance(){
		for (int row=8; row<=templateBoard.length-1-8; row++){
			templateBoard[row][0] = ENTRANCE_SQUARE;
		}
		for (int row=8; row<=templateBoard.length-1-8; row++){
			templateBoard[row][1] = BLANK_SQUARE;
		}
	}
	public void createRightEntrance(){
		for (int row=8; row<=templateBoard.length-1-8; row++){
			templateBoard[row][templateBoard[0].length-1] = ENTRANCE_SQUARE;
		}
		for (int row=8; row<=templateBoard.length-1-8; row++){
			templateBoard[row][templateBoard[0].length-2] = BLANK_SQUARE;
		}
	}	
	public void createTopEntrance(){
		for (int col=8; col<=templateBoard[0].length-1-8; col++){
			templateBoard[0][col] = ENTRANCE_SQUARE;
		}
		for (int col=8; col<=templateBoard[0].length-1-8; col++){
			templateBoard[1][col] = BLANK_SQUARE;
		}
	}
	public void createBottomEntrance(){
		for (int col=8; col<=templateBoard[0].length-1-8; col++){
			templateBoard[templateBoard.length-1][col] = ENTRANCE_SQUARE;
		}
		for (int col=8; col<=templateBoard[0].length-1-8; col++){
			templateBoard[templateBoard.length-2][col] = BLANK_SQUARE;
		}
	}
	public int[][] getArray(){
		return templateBoard;
	}

	public int getRoomID() {
		return roomID;
	}



	
	
}
