/**
 * 
 */
package unamedGame.input;

import java.util.EventListener;

import org.dom4j.DocumentException;

/**
 * @author c-e-r
 *
 */
public interface InputObserver extends EventListener {

	public void inputChanged(InputEvent evt);

}