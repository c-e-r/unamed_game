package unamedGame.time;

import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;

/**
 * 
 */

/**
 * A class to observe time and allow other classes to do things when time passes
 * 
 * @author c-e-r
 *
 */
public class TimeObserver implements Observer, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5454029316475387373L;

	public void observe(Observable o) {
		o.addObserver(this);
	}

	@Override
	public void update(Observable o, Object arg) {
		int time = ((Time) o).getTime();
	}
}
