/**
 * @author Ethan Gan
 * Computer Science
 * 1/12/2024
 * Map class that manages generating a board array to be used as a template for the GameFrame class .
 */
package map;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;


// ADD LEVEL COUNTDOWN TO prevent LAG
/* Map class manages generating a board array to be used as a template for the GameFrame class */
public class GameMap {
	// tile assignments values within the board array
	private final int BLANK_SQUARE = 0;
	private final int WALL_SQUARE = 1;
	private final int PLAYER_SQUARE = 2;
	private final int ENEMY_SQUARE = 3;
	private final int PLAYER_SPAWN_AREA_SQUARE = 4;
	private final int ENTITY_AREA = 5;
	
	// hard set room width and height to 20 to standardize rooms (has duplicate code in gameframe)
	private final int ROOM_WIDTH = 20;
	private final int ROOM_HEIGHT = 20;
	
	// create a new random class to use for board generation
	private Random r = new Random();
	// create empty board with constructor
	private int[][] board;
	
	private FloorPlan floorPlan;
	private int[][] rawFloorArr; 
	private RoomPlan currentRoom;
	// manage current room and manage possible directions
	Map<Integer, int[]> roomNeighborDict = new HashMap<>();
	private RoomPlan[] roomArr;
	
	/** Method creates a new list of room objects */
	public GameMap(int rows, int cols, int rooms){
		this.board = new int[ROOM_WIDTH][ROOM_HEIGHT];
		System.out.println("BIG BOARD HAS STARTED");
		floorPlan = new FloorPlan(rooms);
		System.out.println("Floor Plan has been MADE");
		rawFloorArr = floorPlan.getFloorPlan();
		System.out.println("FLOOR ARRAY HAS BEEN MADE");
		roomArr = new RoomPlan[rooms];
		populateRooms();
	}
	
	/** Method generates the individual boards of each room and populates the larger room array. */
	public void populateRooms() {
		// check entrance and exits by checking neighbors of the room 
		ArrayList<int[]> queue = new ArrayList<int[]>();
		
		int[][] visited = new int[rawFloorArr.length][rawFloorArr[0].length];
		 // could replace to floor plan.size
		for (int id=1; id<=floorPlan.getNumRooms(); id++) {
			System.out.println("Now Serving: " + id);
			int[] currCoord = findRoomPosition(id);

			roomArr[id-1] = new RoomPlan(ROOM_WIDTH, ROOM_HEIGHT, id);
			int[][] directions = {{0,1}, {0,-1}, {1,0}, {-1,0}};
			// keep track of neighbors bottom, up, right, and left
			// check for neighbors and make necessary entryways
			int[] neighborArray = {0, 0, 0, 0};
			for (int j=0; j<directions.length; j++) {
				int row = currCoord[0]+directions[j][0];
				int col = currCoord[1]+directions[j][1];
			    if (row >= 0 && row < rawFloorArr.length && col >= 0 && col < rawFloorArr[0].length) {
					if (rawFloorArr[row][col] != 0) {
						neighborArray[j] = rawFloorArr[row][col];
						createEntrance(id,j);
					}
			    }
			}
			roomNeighborDict.put(id, neighborArray);
			showMatrix(roomArr[id-1].getArray());
		}
		
		currentRoom = roomArr[0];
	}
	
	public RoomPlan getCurrentRoom() {
		return currentRoom;
	}

	/** Update currently drawn room to match the direction the player went. If there is no room the player will loop back into the same room. */
	public RoomPlan enterRightRoom() {
//		printDict(roomNeighborDict);
		int targetID = roomNeighborDict.get(currentRoom.getRoomID())[2];
		System.out.println("YOU TRIED TO GO RIGHT : " + targetID + "\t LEAVING : " + currentRoom.getRoomID());
		if (targetID != 0) {
			currentRoom = roomArr[targetID-1];
//			showMatrix(currentRoom.getArray());
			showMatrix(rawFloorArr);
			System.out.println("YOU ARE NOW VISITING : ROOM # " + currentRoom.getRoomID());
		}
		
		return currentRoom;
	}

	/** Update currently drawn room to match the direction the player went. If there is no room the player will loop back into the same room. */	
	public RoomPlan enterLeftRoom() {
//		printDict(roomNeighborDict);
		int targetID = roomNeighborDict.get(currentRoom.getRoomID())[3];
		System.out.println("YOU TRIED TO GO LEFT : " + targetID + "\t LEAVING : " + currentRoom.getRoomID());
		if (targetID != 0) {
			currentRoom = roomArr[targetID-1];
//			showMatrix(currentRoom.getArray());
			showMatrix(rawFloorArr);
			System.out.println("YOU ARE NOW VISITING : ROOM # " + currentRoom.getRoomID());
		}
		
		return currentRoom;
	}
	
	/** Update currently drawn room to match the direction the player went. If there is no room the player will loop back into the same room. */
	public RoomPlan enterBottomRoom() {
//		printDict(roomNeighborDict);
		int targetID = roomNeighborDict.get(currentRoom.getRoomID())[0];
		System.out.println("YOU TRIED TO GO DOWN : " + targetID + "\t LEAVING : " + currentRoom.getRoomID());
		if (targetID != 0) {
			currentRoom = roomArr[targetID-1];
//			showMatrix(currentRoom.getArray());
			showMatrix(rawFloorArr);
			System.out.println("YOU ARE NOW VISITING : ROOM # " + currentRoom.getRoomID());
		}
		
		return currentRoom;
	}

	/** Update currently drawn room to match the direction the player went. If there is no room the player will loop back into the same room. */
	public RoomPlan enterTopRoom() {
//		printDict(roomNeighborDict);
		int targetID = roomNeighborDict.get(currentRoom.getRoomID())[1];
		System.out.println("YOU TRIED TO GO UP : " + targetID + "\t LEAVING : " + currentRoom.getRoomID());
		if (targetID != 0) {
			currentRoom = roomArr[targetID-1];
//			showMatrix(currentRoom.getArray());
			showMatrix(rawFloorArr);
			System.out.println("YOU ARE NOW VISITING : ROOM # " + currentRoom.getRoomID());
		}
		
		return currentRoom;
	}


	

	/** Method handles the different directions to create entrances. */
	public void createEntrance(int roomID, int direction) {
		switch(direction) {
		case 0: 
			roomArr[roomID-1].createRightEntrance(); break;
		case 1: 
			roomArr[roomID-1].createLeftEntrance(); break;
		case 2: 
			roomArr[roomID-1].createBottomEntrance(); break;
		case 3: 
			roomArr[roomID-1].createTopEntrance(); break;
		}
	}

	/** Method finds the rol and col of a room in the greater floor array and returns the coord. */
	public int[] findRoomPosition(int roomID) {
		for (int row=0; row<rawFloorArr.length; row++) {
			for (int col=0; col<rawFloorArr[0].length; col++) {
				if (rawFloorArr[row][col] == roomID) {
					int[] coord = {row, col};
					return coord;
				}
			}
		}
		int[] coord = {-1, -1};
		return coord;
	}
	
	


	/**
	 *	Displays a 2d matrix. For debugging map generation. Repurposed from my array unit submission
	 *	pre: one 2d array
	 *	post: Each row of a 2d array printed on new lines with enclosing square brackets and separating commas.
	 */
	public static void showMatrix(int[][] matrix) {
		// loop through the matrix's rows and print the items separated by commas with enclosing square brackets 
		for (int x = 0; x < matrix.length; x++) {
			System.out.print("[");
			System.out.print(matrix[x][0]);
			for (int y = 1; y < matrix[x].length; y++) {
				System.out.print(" " + matrix[x][y]);
			}
			System.out.println("]");
		}
	}
	
}
