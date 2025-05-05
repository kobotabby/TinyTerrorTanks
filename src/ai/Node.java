package ai;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

/**
 * @author Ethan Gan
 * Computer Science
 * 1/12/2024
 * Node class for the A* pathfinding algorithm that checks the cost of different nodes to determine which to visit.
 */
public class Node extends JButton implements ActionListener {
	Node parent;
	public int col;
	public int row;
	int gCost; //the distance between the current node and the start node
	int hCost; // the distance from the current node and the goal node
	int fCost; // sum of G cost + H cost
	boolean start;
	boolean goal;
	boolean solid;
	boolean open;
	boolean checked;
	
	public Node(int col, int row) {
		this.col = col;
		this.row = row;
		
		setBackground(Color.white);
		setForeground(Color.black);
		addActionListener(this);
	}
	// straight forward functions to enable and disable flags 
	public void setAsStart() {
		start = true;
	}
	
	public void setAsGoal() {
		goal = true;
	}
	public void setAsSolid() {
		solid = true;
	}
	public void setAsOpen() {
		open = true;
	}
	public void setAsChecked() {
		checked = true; 
	}

	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		setBackground(Color.orange);
	}
	
}

