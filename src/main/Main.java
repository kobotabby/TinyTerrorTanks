/**
 * @author Ethan Gan
 * Computer Science
 * 1/23/2023
 * Main class controls running the TankRunner GameFrame and main menu GUI.
 */
package main;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.TimerTask;

import javax.swing.JOptionPane;
import javax.swing.Timer;

/* 
	SOURCES------------------------------
	MOUSE LISTENER AND KEY LISTENER SETUP:
	- https://www.youtube.com/watch?v=om59cwR7psI
	- https://www.tutorialspoint.com/swing/swing_mouse_listener.htm
	SHOOTING:
	- https://stackoverflow.com/questions/59903415/how-do-i-calculate-the-angle-between-two-points
	GET WINDOW SIZE:
	- https://stackoverflow.com/questions/13474795/get-the-real-size-of-a-jframe-content
	JDIALOG:
	- https://www.geeksforgeeks.org/java-swing-jdialog-examples/
	COLLISION DETECTION:
	- https://happycoding.io/tutorials/processing/collision-detection
	DICTIONARIES:
	- https://www.javatpoint.com/dictionary-class-in-java 	
	Weighted Random Bag:
	- https://gamedev.stackexchange.com/questions/162976/how-do-i-create-a-weighted-collection-and-then-pick-a-random-element-from-it
	COMPARISONS / SORT:
	- https://www.w3schools.com/java/ref_string_compareto.asp 
	- https://www.geeksforgeeks.org/collections-sort-java-examples/
	READING TEXTFILES:
	- https://stackoverflow.com/questions/19871955/java-io-filenotfoundexception-the-system-cannot-find-the-file-specified
*/

/** Main class manages the active JFrame and main menu GUI */
public class Main {
	private static boolean firstTime = true;
	private static GameFrame frame;
	
	public static void main(String[] args) throws IOException {
		System.out.println("RUNNING");
		frame = new GameFrame();

		// loop and check game over state every second
		Timer timer = new Timer(1000, e -> {
			// print statement to debug if the timer loop is running properly
//			System.out.println("Game State Registered");
			// check if it is a first time run in a play session 
			if (firstTime) {
				// check if the high score file has been made		
				File file = new File("highscores.txt");
				try {
					Scanner input = new Scanner(file);
				} catch (FileNotFoundException e2) {
					// if the high score file has not been made yet make a new high score file	
					try {
						file.createNewFile();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
				}
				firstTime = false;
				createNewGameFrame();
			} else {
				if (frame.gameOver) {
					// delete current game frame and create a new one
					frame.dispose();
					createNewGameFrame();								
				}
			}
		});
		timer.start();		
	}
	/** Method creates new game process */
	public static void createNewGameFrame() {
		System.out.println("RUNNING");
		frame = new GameFrame();
		// auto center window
		frame.setLocationRelativeTo(null);
		frame.setVisible(true);
		frame.initComponents();		
		frame.stopGame();
		// display main menu GUI
		displayMainMenu();
	}
	public static void displayMainMenu() {
		// main menu GUI options

		Object[] options = {"Play", "Controls", "Highscores", "Quit"};
				// loop the main menu screen when the player has not yet started to play the game or exit
				boolean choiceOpen = true;
				while (true) {
					System.out.println("RUNNING");
					choiceOpen = false;
					// Code for main menu GUI
					int choice = JOptionPane.showOptionDialog(frame,
							"Blast through levels of tanks as they get more numbered, dangerous and quick!",
							"Welcome to the TankRunner Menu!",
							JOptionPane.DEFAULT_OPTION,
							JOptionPane.QUESTION_MESSAGE,
							null,
							options,
							options[2]);

					switch (choice) {
					case 0:
						// Code to start game   
						frame.startGame();
						choiceOpen = false;
						break;
					case 1:
						// Code to display controls
						JOptionPane.showMessageDialog(frame, "<HTML>Shoot Tanks! Use WASD keys to move and SPACE or MOUSE1 to shoot-em up! <br> You can use smart angles to outrange your enemies with bouncing bullets. <br> Get as much score as possible, but don't die!<!HTML>", "TankRunner Menu - Controls", JOptionPane.INFORMATION_MESSAGE);
						choiceOpen = true;
						break;
					case 2:
						// Code to display high scores
						ArrayList<String[]> scores = frame.getHighScores();
						String[] highScores; 		
						if (scores.size() == 0) {
							// if there are no high scores stored display a message to prompt the user to get a high score
							highScores = new String[1];
							highScores[0] = "There are no high scores at the moment, be the first!";
						} else if (scores.size() <= 10) {
							// if the high scores document has not been fully filled display current entries
							highScores = new String[scores.size()];
							for (int i=0; i<scores.size(); i++) {
								String[] entry = scores.get(i);
								String entryLabel = (i+1) + ". " + entry[0] + " - " + entry[1] + " points"; 
								highScores[i] = entryLabel;
							}			
						}
						else {
							// display the top 10 if the high scores document has over 10 entries
							highScores = new String[10];
							for (int i=0; i<10; i++) {
								String[] entry = scores.get(i);
								String entryLabel = (i+1) + ". " + entry[0] + " - " + entry[1] + " points"; 
								highScores[i] = entryLabel;
							}
						}
						JOptionPane.showMessageDialog(frame, highScores, "TankRunner Menu - High Scores", JOptionPane.INFORMATION_MESSAGE);
						choiceOpen = true;
						break;
					case 3:
						// Code to quit the game
						System.exit(0);
						break;
					default:
						// Code to handle tab closing
						frame.startGame();
						choiceOpen = false;
						break;
					}
					// if the player has chosen an option
					if (!choiceOpen) {
						break;
					}
				}
	}
}
