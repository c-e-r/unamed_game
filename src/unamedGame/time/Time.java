/**
 * 
 */
package unamedGame.time;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

import javax.swing.event.EventListenerList;

import unamedGame.entities.EntityListener;
import unamedGame.entities.Player;

/**
 * Keeps track of the games current time and contains methods for manipulating
 * that time.
 * 
 * @author c-e-r
 *
 */
public class Time extends Observable implements Serializable {

	private static Time instance = null;

	/**
	 * Returns the Time object and creates it if it dosn't already exist
	 * 
	 * @return the Time object
	 */
	public static Time getInstance() {
		if (instance == null) {
			instance = new Time();

		}
		return instance;
	}
	
	public static void setInstance(Time instance) {
		Time.instance = instance;
	}


	private int time;
	private List<TimeListener> timeListeners = new ArrayList<TimeListener>();

	/**
	 * A constructor to initialize the time at 0
	 */
	public Time() {
		time = 0;
	}

	/**
	 * Passes the given number of turns
	 * 
	 * @param time
	 *            the number of turns to pass
	 */
	public void passTime(int time) {
		for (int i = 0; i < time; i++) {
			this.time++;

			for (Iterator<TimeListener> it = timeListeners.iterator(); it
					.hasNext();) {
				TimeListener listener = it.next();
				listener.entityEvent();
				if (listener.getDelete()) {
					it.remove();
				}
			}
		}

	}

	/**
	 * returns the current time
	 * 
	 * @return
	 */
	public int getTime() {
		return time;
	}

	public void addTimeListener(TimeListener listener) {
		timeListeners.add(listener);
	}

	public void removeEntityListener(TimeListener listener) {
		timeListeners.remove(listener);
	}

}
