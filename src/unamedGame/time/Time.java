/**
 * 
 */
package unamedGame.time;

import java.util.Observable;

import javax.swing.event.EventListenerList;

import unamedGame.entities.Player;

/**
 * Keeps track of the games current time and contains methods for manipulating that time.
 * 
 * @author c-e-r
 *
 */
public class Time extends Observable {
	
	private static Time instance = null;

	/**
	 * Returns the Time object and creates it if it dosn't already exist
	 * @return the Time object
	 */
	public static Time getInstance() {
		if (instance == null) {
			instance = new Time();

		}
		return instance;
	}

	private  int time;
	private EventListenerList timeList = new EventListenerList();

	/**
	 * A constructor to initialize the time at 0
	 */
	public Time() {
		time = 0;
	}

	/**
	 * Passes the given number of turns
	 * @param time the number of turns to pass
	 */
	public  void passTime(int time) {
		for (int i = 0; i < time; i++) {
			this.time++;
			setChanged();
			notifyObservers();
		}
		
	}
	
	/**
	 * returns the current time
	 * @return
	 */
	public int getTime() {
		return time;
	}
	
	
}
