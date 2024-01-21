package main;

import java.util.ArrayList;

import javax.swing.text.html.parser.Entity;


public class PathFinder {
	GameFrame gp;
	Node[][] node;
	ArrayList<Node> openList = new ArrayList<>();
	public ArrayList<Node> pathList = new ArrayList<>();
	Node startNode, goalNode, currentNode;
	int step = 0;
	private boolean goalReached;
	private int maxCol;
	private int maxRow;
	
	public PathFinder(GameFrame gp) {
		this.gp = gp;
		maxCol = gp.MAX_SCREEN_COL;
		maxRow = gp.MAX_SCREEN_ROW;
		instantiateNodes();
	}
	
	public void instantiateNodes() {
		node = new Node[gp.MAX_SCREEN_ROW][gp.MAX_SCREEN_COL];
		
		int col = 0;
		int row = 0;
		
		while(col < gp.MAX_SCREEN_COL && row < gp.MAX_SCREEN_ROW) {
			node[col][row] = new Node(col, row);
			col++;
			if ( col == gp.MAX_SCREEN_COL) {
				col = 0;
				row++;
			}
		}
	}
	public void resetNodes() {
		int col = 0;
		int row = 0;
		while(col < gp.MAX_SCREEN_COL && row< gp.MAX_SCREEN_ROW) {
			// Reset open, checked and solid state
			node[col][row].open = false;
			node[col][row].checked = false;
			node[col][row].solid = false;
			
			col++;
			if (col == gp.MAX_SCREEN_COL) {
				col = 0;
				row++;
			}
		}
		openList.clear();
		pathList.clear();
		goalReached = false; 
		step = 0;
		
	}
	public void setNodes(int startCol, int startRow, int goalCol, int goalRow) {
		resetNodes();
		
		startNode = node[startCol][startRow];
		currentNode = startNode;
		goalNode = node[goalCol][goalRow];
		openList.add(currentNode);
		
		int col = 0;
		int row = 0;
		
		while(col<gp.MAX_SCREEN_COL && row<gp.MAX_SCREEN_ROW) {
			int tileNum = gp.getGameMap().getCurrentRoom().getArray()[col][row];
			if (tileNum == 1) { // if it is a wall
				node[col][row].solid = true;
			}
		}
		getCost(node[col][row]);
		
		col++;
		if(col == gp.MAX_SCREEN_COL) {
			col = 0;
			row++;
		}
	}
	private void getCost(Node node) {
		// GET G COST (The distance from the start node)
		int xDistance = Math.abs(node.col - startNode.col);
		int yDistance = Math.abs(node.row - startNode.row);
		node.gCost = xDistance + yDistance;
		
		// GET H COST (The distance from the goal node)
		xDistance = Math.abs(node.col - goalNode.col);
		yDistance = Math.abs(node.row - goalNode.row);
		node.hCost = xDistance + yDistance;
		
		// GET F COST (The total cost)
		node.fCost = node.gCost + node.hCost;
	}
	
	public boolean search() {
		int step = 0;
		while (goalReached == false && step < 300) {
			step++;
			int col = currentNode.col;
			int row = currentNode.row;
			
			currentNode.setAsChecked();
			openList.remove(currentNode);
			
			// OPEN THE UP NODE
			
			if (row -1 >= 0) {
				openNode(node[col][row-1]);				
			}
			if (col -1 >= 0) {
				openNode(node[col-1][row]);			
			}
			if (row +1 < maxRow) {
				openNode(node[col][row+1]);
			}
			if (col +1 < maxCol) {
				openNode(node[col+1][row]);			
			}

			// FIND THE BEST NODE
			
			int bestNodeIndex = 0;
			int bestNodefCost = 999;
			
			for (int i=0; i<openList.size(); i++) {
				
				if(openList.get(i).fCost < bestNodefCost) {
					bestNodeIndex = i;
					bestNodefCost = openList.get(i).fCost;
				}
				// if F cost is equal, check the G cost
				else if (openList.get(i).fCost == bestNodefCost) {
					if(openList.get(i).gCost < openList.get(bestNodeIndex).gCost) {
						bestNodeIndex = i;
					}
				}
				
			}
			// after the loop, we get the best node which is our next step
			currentNode = openList.get(bestNodeIndex);
			
			if (currentNode == goalNode) {
				goalReached=true;
				trackPath();
			}

		}
		return goalReached;
	}
	
	private void openNode(Node node) {
		if(node.open == false && node.checked == false && node.solid == false) {
			node.setAsOpen();
			node.parent = currentNode;
			openList.add(node);
			
		}
	}
	private void trackPath() {
		Node current = goalNode;
		while(current != startNode) {
			current = current.parent;
			if(current != startNode) {
				current.setAsPath();
			}
		}
	}
	
}
