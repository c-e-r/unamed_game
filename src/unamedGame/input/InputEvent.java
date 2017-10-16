/**
 * 
 */
package unamedGame.input;

import java.util.EventObject;

import unamedGame.Game;
import unamedGame.ui.Window;
import unamedGame.util.Colors;

public class InputEvent extends EventObject {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6677771066487284367L;
	private final String text;

	/**
	 * An event to get player entered text. Also put entered text into text
	 * field and clears the text field.
	 * 
	 * @param source
	 * @param text
	 */
	public InputEvent(Object source, String text) {
		super(source);
		this.text = text;
		Game.clearTextField();
		Window.appendToPane(Window.getInstance().getTextPane(), text,
				Colors.PLAYER_TEXT);
		int len = Window.getInstance().getTextPane().getDocument().getLength();
		Window.getInstance().getTextPane().setCaretPosition(len);
	}

	/**
	 * Returns the text the player entered
	 * 
	 * @return
	 */
	public String getText() {
		return text;
	}

}
