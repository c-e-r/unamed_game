package unamedGame.time;

import java.util.Observable;
import java.util.Observer;

/**
 * 
 */

/**
 * A class to observe time and allow other classes to do things when time passes
 * @author camer
 *
 */
public class TimeObserver implements Observer {
	public void observe(Observable o) {
	    o.addObserver(this);
	  }

	  @Override
	  public void update(Observable o, Object arg) {
	    int time = ((Time) o).getTime();
	    System.out.println( "The time is: " +time);
	  }
}
