/**
 * 
 */
package unamedGame;

import java.io.Serializable;

import unamedGame.entities.Player;

/**
 * @author c-e-r
 *
 */
public class Save implements Serializable {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1923887931459389527L;
	private Player player;

	public Save() {
		player = Player.getInstance();
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}
}
