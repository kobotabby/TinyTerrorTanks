/**
 * @author Ethan Gan
 * Computer Science
 * 1/23/2023
 * KeyHandler class that manages reading key inputs.
 */
package handlers;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

/* Reads Key Input **/
public class KeyHandler implements KeyListener {
	public boolean upPressed, downPressed, leftPressed, rightPressed, spacePressed;
	
	/** 
	 * Method checks if a key was pressed and modifies the boolean variables as represented
	 * pre: none
	 * post: key registered as boolean
	 */
	@Override
	public void keyPressed(KeyEvent e) {
		int code = e.getKeyCode();
		
		if(code == KeyEvent.VK_W) {
			upPressed = true;
		}
		if(code == KeyEvent.VK_S) {
			downPressed = true;
		}
		if(code == KeyEvent.VK_A) {
			leftPressed = true;
		}
		if(code == KeyEvent.VK_D) {
			rightPressed = true;
		}
		if(code == KeyEvent.VK_SPACE) {
			spacePressed = true;
		}
	}
	
	/** 
	 * Method checks if a key was released and modifies the boolean variables as represented
	 * pre: none
	 * post: key registered as boolean
	 */
	@Override
	public void keyReleased(KeyEvent e) {
		// TODO Auto-generated method stub
		int code = e.getKeyCode();

		if(code == KeyEvent.VK_W) {
			upPressed = false;
		}
		if(code == KeyEvent.VK_S) {
			downPressed = false;
		}
		if(code == KeyEvent.VK_A) {
			leftPressed = false;
		}
		if(code == KeyEvent.VK_D) {
			rightPressed = false;
		}
		if(code == KeyEvent.VK_SPACE) {
			spacePressed = false;
		}
	}

	/** 
	 * Unused method checks if a key was typed 
	 * pre: none
	 * post: none
	 */
	@Override
	public void keyTyped(KeyEvent e) {
	}

}
