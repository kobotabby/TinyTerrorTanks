package ai;

import java.util.ArrayList;

import javax.swing.text.html.parser.Entity;

import main.GameFrame;

/**
 * @author Ethan Gan
 * Computer Science
 * 1/12/2024
 * Implementation of the A* pathfinding algorithm based on RyiSnow's tutorial. Works by calculating the costs of various nodes to determine which paths have costs that are the closest to both the starting and ending position.
 */
public class PathFinder {
	GameFrame game;
	Node[][] node;
	ArrayList<Node> openList = new ArrayList<>();
	public ArrayList<Node> pathList = new ArrayList<>();
	Node startNode, goalNode, currentNode;
	int step = 0;
	private boolean goalReached;
	private int maxCol;
	private int maxRow;
	
	public PathFinder(GameFrame gp) {
		this.game = gp;
		maxCol = gp.MAX_SCREEN_COL;
		maxRow = gp.MAX_SCREEN_ROW;
		instantiateNodes();
	}
	
	/** Method creates nodes based on the Room plan of the GameFrame */
	public void instantiateNodes() {
		node = new Node[game.MAX_SCREEN_ROW][game.MAX_SCREEN_COL];
		
		int col = 0;
		int row = 0;
		
		while(col < game.MAX_SCREEN_COL && row < game.MAX_SCREEN_ROW) {
			node[col][row] = new Node(col, row);
			col++;
			if (col == game.MAX_SCREEN_COL) {
				col = 0;
				row++;
			}
		}
	}
	/** Method resets node list and path list */
	public void resetNodes() {
		int col = 0;
		int row = 0;
		while(col < game.MAX_SCREEN_COL && row< game.MAX_SCREEN_ROW) {
			// Reset open, checked and solid state
			node[col][row].open = false;
			node[col][row].checked = false;
			node[col][row].solid = false;
			
			col++;
			if (col == game.MAX_SCREEN_COL) {
				col = 0;
				row++;
			}
		}
		openList.clear();
		pathList.clear();
		goalReached = false; 
		step = 0;
		
	}
	/** Method sets nodes to a list and flags unreachable nodes as solid such as walls. */
	public void setNodes(int startCol, int startRow, int goalCol, int goalRow) {
		resetNodes();
		
		// set start and goal node - subtract 1 as arrays start from 0 to 19
		startNode = node[startCol-1][startRow-1];
		currentNode = startNode;
		goalNode = node[goalCol][goalRow];
		openList.add(currentNode);
		
		int col = 0;
		int row = 0;
		
		while(col<game.MAX_SCREEN_COL && row<game.MAX_SCREEN_ROW) {
//			System.out.println("THINKING HARDERerer ");
			int tileNum = game.getGameMap().getCurrentRoom().getArray()[col][row];
			if (tileNum == 1) { // if it is a wall
				node[col][row].solid = true;
			}
			getCost(node[col][row]);
			
			col++;
			if(col == game.MAX_SCREEN_COL) {
				col = 0;
				row++;
			}
		}

	}
	/** Method initializes node costs. */
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
	/** Method implements A* algorithm by comparing the fCosts and later gCosts of the nodes to find the best Nodes to go to. */
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

			// FIND THE BEST NODE using fCost first
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
			
            // if there are no nodes in open list, end the loop
            if(openList.size() == 0)
            {
                break;
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
	
	/** Method creates a new coordinate path based on the nodes of trackPath. */
	public ArrayList<int[]> createCoordinatePath(int startCol, int startRow, int goalCol, int goalRow) {
		this.setNodes(startCol, startRow, goalCol, goalRow);
		boolean goalReached = this.search();
		if (goalReached) {
		    ArrayList<Node> pathList = this.trackPath();
		    ArrayList<int[]> coordList = this.convertPathToCoordinates(pathList);
		    return coordList;
		}
		return new ArrayList<int[]>();
	}
	/** Method converts node list into a list of coordinates. */
	public ArrayList<int[]> convertPathToCoordinates(ArrayList<Node> pathList) {
	    ArrayList<int[]> coordList = new ArrayList<>();
	    for (Node node : pathList) {
	        int[] coord = {node.col, node.row};
	        coordList.add(coord);
	    }
	    return coordList;
	}
	/** Method opens nodes for decision to evaluate for node cost. */
	private void openNode(Node node) {
		if(node.open == false && node.checked == false && node.solid == false) {
			node.setAsOpen();
			node.parent = currentNode;
			openList.add(node);
			
		}
	}
	/** Method backtracks from current node its optimal path and returns an array of nodes. */
    public ArrayList<Node> trackPath()
    {
    	// clear path list
        pathList.clear();
    	Node current = goalNode;
        while(current != startNode)
        {
            pathList.add(0,current); 
            current = current.parent;
        }
//        System.out.println(pathList);
        return pathList;
    }
	
}
