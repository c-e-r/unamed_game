/**
 * 
 */
package unamedgame;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import unamedgame.entities.Player;
import unamedgame.time.Time;

/**
 * A class to store other objects for saving.
 * 
 * @author c-e-r
 * @version 0.0.1
 */
public class Save implements Serializable {

    private static final long serialVersionUID = 1923887931459389527L;
    private Player player;
    private Time time;
    private String playerName;
    private String saveNote;
    private LocalDateTime saveDateTime;
    private int playerLevel;

    /**
     * Creates a save.
     */
    Save() {
        player = Player.getInstance();
        time = Time.getInstance();
        saveDateTime = LocalDateTime.now();
        playerName = player.getName();
        playerLevel = player.getLevel();
    }

    /**
     * Creates a save with a save note.
     * 
     * @param saveNote
     *            the save notes
     */
    Save(String saveNote) {
        player = Player.getInstance();
        time = Time.getInstance();
        saveDateTime = LocalDateTime.now();
        playerName = player.getName();
        playerLevel = player.getLevel();
        if (saveNote != null) {
            this.saveNote = saveNote;
        }
        System.out.println(" :" + saveNote);
    }

    /**
     * Returns the saved player.
     * 
     * @return the player
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Returns the save time.
     * 
     * @return the time
     */
    public Time getTime() {
        return time;
    }

    /**
     * Returns the formatted save text.
     * 
     * @return the save text
     */
    public String getSaveText() {
        return String.format("%s LVL.%d %s ", playerName, playerLevel,
                saveDateTime
                        .format(DateTimeFormatter.ofPattern("h:mma  d/M/y")));
    }

    /**
     * Returns the save note.
     * 
     * @return the saveNote
     */
    public String getSaveNote() {
        return saveNote;
    }
}
