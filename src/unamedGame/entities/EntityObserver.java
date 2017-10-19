package unamedGame.entities;

import java.io.Serializable;
import java.util.Observable;
import java.util.Observer;

/**
 * 
 */

/**
 * A class to observe entities and allow other classes to do things when stuff happens
 * @author c-e-r
 *
 */
public class EntityObserver implements Observer, Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 9153513366467829508L;

	public void observe(Observable o) {
	    o.addObserver(this);
	  }

	  @Override
	  public void update(Observable o, Object arg) {
	  }
}
