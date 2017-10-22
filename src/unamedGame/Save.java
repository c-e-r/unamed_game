/**
 * 
 */
package unamedGame;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

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
	private String playerName;
	private String saveNote;
	private LocalDateTime saveDateTime;
	private int playerLevel;

	Save() {
		player = Player.getInstance();
		time = Time.getInstance();
		saveDateTime = LocalDateTime.now();
		playerName = player.getName();
		playerLevel = player.getLevel();
	}

	Save(String saveNote) {
		player = Player.getInstance();
		time = Time.getInstance();
		saveDateTime = LocalDateTime.now();
		playerName = player.getName();
		playerLevel = player.getLevel();
		if (saveNote != null)
			this.saveNote = saveNote;
		System.out.println(" :" + saveNote);
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

	public String getSaveText() {
		return String.format("%s LVL.%d %s ", playerName, playerLevel,
				saveDateTime
						.format(DateTimeFormatter.ofPattern("h:mma  d/M/y")));
	}

	public String getSaveNote() {
		return saveNote;
	}
}
