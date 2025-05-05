package main;
import java.util.Timer;

import javax.swing.JLabel;

/**
 * @author Ethan Gan
 * Computer Science
 * 1/22/2024
 * TimerClock class used to display the in game time and record high scores.
 */
public class TimerClock extends JLabel{
	private double startTime = 0; //milliseconds
	private double elapsedTime = 0; //milliseconds
	private String timeString = "00:00";
	private boolean frozen = false; 
	
	/** Constructor for TimerClock class */
	public TimerClock() {
		startTime = System.currentTimeMillis();
	}

	/** 
	 * Method describes if a second has passed and the timerClock string needs to be updated
	 * pre: none
	 * post: If the timeString is different from the formatted time, the updateTime() method is called and returns true. Otherwise, it returns false.
	 */
	public boolean secondPassed() {
		if (timeString != getFormattedTime()) {
			if (!frozen) {
				updateTime();					
			}
			return true;
		}
		return false;
	}
	
	/** 
	 * Creates a string to display on the timerLabel
	 * pre: none
	 * post: Returns a string describing the elapsed time in the form of Minutes:Seconds.
	 */
	public String getFormattedTime() {
//		System.out.println(elapsedTime);
		timeString = String.format("%02d:%02d", (int)(elapsedTime/60000), (int)(elapsedTime%60000)/1000);
		return timeString;
	}
	
	
	/** 
	 * Updates time stored in the TimerClock object. 
	 * pre: none
	 * post: The elapsedTime is updated by subtracting the startTime from the current system time.
	 */
	private void updateTime() {
		elapsedTime = System.currentTimeMillis()-startTime;
	}

	/** 
	 * Method resets TimerClock object.
	 * pre: none
	 * post: resets time value by moving the starting time to the current time and resets the string.
	 */
	public void reset() {
		startTime = System.currentTimeMillis(); //milliseconds
		timeString = "00:00";
	}
	
	/**
	 * Method switches on the frozen flag to stop updating the timer:
	 * pre: none
	 * post: The frozen flag is set to true.
	 */
	public void freeze() {
		frozen = true;
	}
	
	/** Method returns the number of seconds elapsed */
	public int getSeconds() {
		return (int) (elapsedTime/1000);
	}
	
	/** Converts the current elapsed time to text */
	public String toString() {
		String output;
		if (elapsedTime/60000 > 1) {
			output = String.format("%.0f minutes and %.0f seconds", elapsedTime/60000, (elapsedTime%60000)/1000);
		} else {
			output = String.format("%.0f seconds", (elapsedTime%60000)/1000);			
		}
		
		return output;
		
	}
}
