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

	}
	public int getNumRooms() {
		return numRooms;
	}

}
