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
	private Room currentRoom;
	// manage current room and manage possible directions
	Map<Integer, int[]> roomNeighborDict = new HashMap<>();
	private Room[] roomArr;
	
//	create a new list of room objects
	public GameMap(int rows, int cols, int rooms){
		this.board = new int[ROOM_WIDTH][ROOM_HEIGHT];
		System.out.println("BOARD HAS BEEN MADE");
		floorPlan = new FloorPlan(rooms);
		rawFloorArr = floorPlan.getFloorPlan();
		roomArr = new Room[rooms];
		populateRooms();
	}
	
	public void populateRooms() {
		// use bfs to populate rooms
		// check entrance and exits
		
		ArrayList<int[]> queue = new ArrayList<int[]>();
		
		int[][] visited = new int[rawFloorArr.length][rawFloorArr[0].length];
		 // could replace to floor plan.size
		for (int id=1; id<=floorPlan.getNumRooms(); id++) {
			System.out.println("Now Serving: " + id);
			int[] currCoord = findRoomPosition(id);

			roomArr[id-1] = new Room(ROOM_WIDTH, ROOM_HEIGHT, id);
			int[][] directions = {{0,1}, {0,-1}, {1,0}, {-1,0}};
			// keep track of neighbors right, left, down, and up

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
		printDict(roomNeighborDict);
		
		currentRoom = roomArr[0];
	}
	
	public Room getCurrentRoom() {
		return currentRoom;
	}

	// the player also does not spawn in on restart
	public Room getRightRoom() {
		printDict(roomNeighborDict);
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

	
	public Room getLeftRoom() {
		printDict(roomNeighborDict);
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
	
	public Room getBottomRoom() { // CORRECT
		printDict(roomNeighborDict);
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

	
	public Room getTopRoom() {
		printDict(roomNeighborDict);
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
	
	private void printDict(Map<Integer, int[]> dictionary) {
        System.out.println("\nDictionary Entries:");
        for (Map.Entry<Integer, int[]> entry : dictionary.entrySet()) {
            int key = entry.getKey();
            int[] arrayValues = entry.getValue();

            System.out.print("Key " + key + ": ");
            for (int value : arrayValues) {
                System.out.print(value + " ");
            }
            System.out.println();
        }
	}
	
}
