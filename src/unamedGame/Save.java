/**
 * 
 */
package unamedGame;

import java.io.Serializable;

import unamedGame.entities.Player;
import unamedGame.time.Time;

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
	private Time time;

	public Save() {
		player = Player.getInstance();
		time = Time.getInstance();
	}

	/**
	 * @return the player
	 */
	public Player getPlayer() {
		return player;
	}

	/**
	 * @return the time
	 */
	public Time getTime() {
		return time;
	}

}
