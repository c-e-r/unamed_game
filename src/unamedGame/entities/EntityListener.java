package unamedGame.entities;

import java.io.Serializable;

/**
 * 
 */

/**
 * A class to observe entities and allow other classes to do things when stuff
 * happens
 * 
 * @author c-e-r
 *
 */
public abstract class EntityListener implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 7751064600840910271L;
	private boolean delete;

	protected abstract void action(String event);

	public void setDelete() {
		delete = true;
	}

	public boolean getDelete() {
		return delete;
	}

	public void entityEvent(String event) {
		if (!delete) {
			action(event);
		}
	}
}
