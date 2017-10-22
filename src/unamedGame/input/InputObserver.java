/**
 * 
 */
package unamedGame.input;

import java.util.EventListener;

/**
 * @author c-e-r
 *
 */
public interface InputObserver extends EventListener {

	public void inputChanged(InputEvent evt);

}