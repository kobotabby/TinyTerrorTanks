package entity;

import main.GameFrame;

public class SmartTank extends Tank{
	int targetX = 0;
	int targetY = 0;
	
	int[] targetDest = {targetX, targetY};
	
	// find a way to move to a point
	// implement a star
	
	public SmartTank(int x, int y, int width, int height, GameFrame g) {
		// TODO Auto-generated constructor stub
		super(height, height, height, height, g);
	}
	
	public void moveTowardsPt(int tileRow, int tileCol) {
		
	}
	
//	public void moveTowardsPt(int tileRow, int tileCol) {
//		
//	}

}
