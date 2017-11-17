/**
 * 
 */
package unamedgame.time;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

/**
 * Keeps track of the games current time and contains methods for manipulating
 * that time.
 * 
 * @author c-e-r
 * @version 0.0.1
 */
public class Time extends Observable implements Serializable {

    private static final long serialVersionUID = -6602346108280243709L;
    private static Time instance;

    /**
     * Returns the Time object and creates it if it dosn't already exist.
     * 
     * @return the Time object
     */
    public static Time getInstance() {
        if (instance == null) {
            instance = new Time();

        }
        return instance;
    }

    /**
     * Sets the the Time object.
     * @param instance the Time to set
     */
    public static void setInstance(Time instance) {
        Time.instance = instance;
    }
    
    
    
    private List<TimeListener> timeListeners = new ArrayList<TimeListener>();
    private int time;

    /**
     * A constructor to initialize the time at 0.
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
     * returns the current time.
     * 
     * @return the current time
     */
    public int getTime() {
        return time;
    }
    
    public String getDateTime() {
        int tempTime = time;
        int days = time/8640;
        tempTime -= days*8640;
        int hours = tempTime/360;
        tempTime -= hours*360;
        int minutes = tempTime/6;
        tempTime = tempTime*6;
        return String.format("Day %d  %d:%02d", days+1, hours, minutes);
    }

    /**
     * Adds a time listener to this time object.
     * @param listener the listener to add
     */
    public void addTimeListener(TimeListener listener) {
        timeListeners.add(listener);
    }

    /**
     * Removes the given time listener from the time object.
     * @param listener the listener to remove
     */
    public void removeEntityListener(TimeListener listener) {
        timeListeners.remove(listener);
    }

}
