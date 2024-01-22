package labels;
import java.util.Timer;

import javax.swing.JLabel;

public class TimerLabel extends JLabel{
	public double startTime = 0; //milliseconds
//	public double currentTime = 0;  //milliseconds
	public double elapsedTime = 0; //milliseconds
	public String timeString = "00:00";
	
	public String maxTimeString = "99:99";
	
	public TimerLabel() {
		startTime = System.currentTimeMillis();
	}
	
	public boolean secondPassed() {
		if (timeString != getFormattedTime())
		return true;
		return false;
	}
	
	public String getFormattedTime() {
		updateTime();
		timeString = String.format("%02.0f:%02.0f", elapsedTime/60000, (elapsedTime%60000)/1000);
//		System.out.println(timeString);
		return timeString;
	}
	public void updateTime() {
		elapsedTime = System.currentTimeMillis()-startTime;
	}

	public int compareTo(TimerLabel timer2) {
		return 0;
	}
	
	public void reset() {
		startTime = System.currentTimeMillis(); //milliseconds
		timeString = "00:00:000";
	}
	
//	public String deductTime(int seconds) {
//		startTime = startTime + seconds;
//		timeString = String.format("RUN: %02.0f:%02.0f", elapsedTime/60000, (elapsedTime%60000)/1000);
//		return timeString;
//	}
}
