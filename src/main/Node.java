package main;
import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;

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
	
	public void setAsStart() {
		setBackground(Color.blue);
		setForeground(Color.white);
		setText("Start");
		start = true;
	}
	
	public void setAsGoal() {
		setBackground(Color.yellow);
		setForeground(Color.black);
		setText("Goal");
		goal = true;
	}
	public void setAsSolid() {
		setBackground(Color.black);
		setForeground(Color.black);
		solid = true;
	}
	public void setAsOpen() {
		open = true;
	}
	public void setAsChecked() {
		if (start == false && goal == false) {
			setBackground(Color.orange);
			setForeground(Color.black);
		}
		checked = true; 
	}
	public void setAsPath() {
		setBackground(Color.green);
		setForeground(Color.black);
	}
	
	@Override
	public void actionPerformed(ActionEvent e) {
		// TODO Auto-generated method stub
		setBackground(Color.orange);
	}
	
}

