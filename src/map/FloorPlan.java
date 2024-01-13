package map;

import java.util.Random;


public class FloorPlan {
	private int[][] floorPlan;
	private int numRooms;
	private int startRow, startCol;
	private Random rand = new Random();
	
	
	
	public FloorPlan(int rooms) {
		numRooms = rooms;
		floorPlan = new int[(rooms*3)/2][(rooms*3)/2];
		// start at middle left
		startRow = rooms/2;
		startCol = rooms/2; 
		floorPlan[startRow][startCol] = rooms;
		generateFloorPlan();
	}
	
	public int[][] getFloorPlan() {
		return floorPlan;
	}
	
	private void generateFloorPlan() {
		int req = numRooms-1;
		
		int[][] directions = {{0,1}, {0,-1}, {1,0}, {-1,0}};
		int[] currentRoom = {startRow, startCol};
		while(req > 0) {
			// i want to roll a 0.75 chance for each direction
			// add another random to help centralize generations
			// add a random number of bonds to fulfill for each node
			for (int[] dir : directions) {
				int row = currentRoom[0]+dir[0];
				int col = currentRoom[1]+dir[1];
				
				int r = rand.nextInt(10)+1;
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
	public int getNumRooms() {
		return numRooms;
	}

}
