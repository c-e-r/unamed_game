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
public abstract class TimeListener implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = -5454029316475387373L;
	private boolean delete;

	protected abstract void action();

	public void setDelete() {
		delete = true;
	}

	public boolean getDelete() {
		return delete;
	}

	public void entityEvent() {
		if (!delete) {
			action();
		}
	}
}
