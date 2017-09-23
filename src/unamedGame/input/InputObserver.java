/**
 * 
 */
package unamedGame.input;

import java.util.EventListener;

/**
 * @author camer
 *
 */
public interface InputObserver extends EventListener {

	public void inputChanged(InputEvent evt);

}