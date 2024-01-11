/**
 * @author Ethan Gan
 * Computer Science
 * 1/23/2023
 * MouseHandler class that manages reading mouse inputs.
 */
package handlers;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

/* Reads Mouse Input **/
public class MouseHandler implements MouseListener, MouseMotionListener {
	public boolean mouseClicked, mousePressed, mouseEntered, mouseExited, mouseDragged, mouseMoved, mouseReleased;
	public int mouseX, mouseY;
	// correct downwards offset
	private int offsetY = 50;
	private int offsetX;
	

	// override mouse listener functions
	/** 
	 * Method checks if the mouse was clicked
	 * pre: none
	 * post: mouseX and mouseY updates, mouse event registered as boolean
	 */
	@Override
	public void mouseClicked(MouseEvent e) {
//		System.out.println("Mouse Clicked: ("+e.getX()+", "+e.getY() +")");
		mouseX = e.getX();
		mouseY = e.getY() - offsetY;
		mouseClicked = true;
		mouseReleased = false;
	}
	
	/** 
	 * Method checks if the mouse was pressed
	 * pre: none
	 * post: mouseX and mouseY updated, mouse event registered as boolean
	 */
	@Override
	public void mousePressed(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY() - offsetY;
		mousePressed = true;
		mouseReleased = false;
	}
	
	/** 
	 * Method checks if the mouse was released
	 * pre: none
	 * post: mouseX and mouseY updated, mouse event registered as boolean
	 */
	@Override
	public void mouseReleased(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY() - offsetY;
		mousePressed = false;
		mouseReleased = true;
	}
	
	/** 
	 * Method checks if the mouse entered
	 * pre: none
	 * post: mouseX and mouseY updated, mouse event registered as boolean
	 */
	@Override
	public void mouseEntered(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY() - offsetY;
		mouseEntered = true;
		mouseReleased = false;
	}
	
	
	/** 
	 * Method checks if the mouse exited
	 * pre: none
	 * post: mouseX and mouseY updated, mouse event registered as boolean
	 */
	@Override
	public void mouseExited(MouseEvent e) {
		mouseX = e.getX();
		mouseY = e.getY() - offsetY;
		mouseExited = true;
		mouseReleased = false;
	}

	/** 
	 * Method checks if the mouse dragged
	 * pre: none
	 * post: mouseX and mouseY updated, mouse event registered as boolean
	 */
	@Override
	public void mouseDragged(MouseEvent e) {
		// TODO Auto-generated method stub
		mouseX = e.getX();
		mouseY = e.getY() - offsetY;
		mouseDragged = true;
		mouseReleased = false;
//		System.out.println("Mouse Dragged: ("+e.getX()+", "+e.getY() +")");
	}

	/** 
	 * Method checks if the mouse moved
	 * pre: none
	 * post: mouseX and mouseY updated, mouse event registered as boolean
	 */
	@Override
	public void mouseMoved(MouseEvent e) {
		// TODO Auto-generated method stub
		mouseX = e.getX();
		mouseY = e.getY() - offsetY;
		mouseMoved = true;
//		System.out.println("Mouse At: ("+e.getX()+", "+e.getY() +")");	
	}
}
