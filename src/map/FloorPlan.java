package map;

import java.util.Random;


public class FloorPlan {
	private int[][] floorPlan;
	private int numRooms;
	private int startRow, startCol;
	private Random rand = new Random();
	
	
	/** Constructor creates large empty array for potential room positions */
	public FloorPlan(int rooms) {
		numRooms = rooms;
		floorPlan = new int[rooms*2][rooms*2];
		startRow = rooms/2;
		startCol = rooms/2; 
		floorPlan[startRow][startCol] = rooms;
		generateFloorPlan();
	}
	
	public int[][] getFloorPlan() {
		return floorPlan;
	}
	
	/** Method generates a high level arrangement of the different numbered rooms.*/
	private void generateFloorPlan() {
		int req = numRooms-1;
		int loops = 0;
		// loop through different directions for each room to decide where the next room should be 
		int[][] directions = {{0,1}, {0,-1}, {1,0}, {-1,0}};
		int[] currentRoom = {startRow, startCol};
		// prevent infinite loops in the odd chance that a room ends up boxing itself up
		while(req > 0 && loops < 200) {
			loops ++;
			for (int[] dir : directions) {
				int row = currentRoom[0]+dir[0];
				int col = currentRoom[1]+dir[1];
				 		
				int r = rand.nextInt(10)+1;
				if (row >= 0 && row < floorPlan.length && col >= 0 && col < floorPlan[0].length) {
						
					if (floorPlan[row][col] == 0) {
						if (r >= 8) {
							floorPlan[row][col] = req;
							currentRoom[0] = row;
							currentRoom[1] = col;
							req--;
						} 	
					}
				}
			}
		}
		// if a room boxed itself generate again
		if (loops == 200) {
			floorPlan = new int[numRooms*2][numRooms*2];
			floorPlan[numRooms][numRooms] = numRooms;
			generateFloorPlan();
		}
	}
	// getter or setter
	public int getNumRooms() {
		return numRooms;
	}

}
