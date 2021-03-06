package unamedgame.effects;

import java.util.List;

import unamedgame.entities.Entity;
import unamedgame.entities.Player;
import unamedgame.time.Time;
import unamedgame.ui.Window;
import unamedgame.util.Colors;

/**
 * An Effect that increases one of the stats of an entity.
 * 
 * @author c-e-r
 * @version 0.0.1
 */
public class StatIncreaseEffect extends Effect {

    private static final long serialVersionUID = 1217888167733580084L;
    private int magnitude;
    private String stat;

    /**
     * Creates a StatIncreaseEffect from the given parameters.
     * 
     * @see Effect#Effect(List, String, int, int, int, String, String, boolean,
     *      String, String, String, String, String, String, String, String,
     *      String, String, String, String, String, String, String, String,
     *      String, int)
     * @param stat
     *            the name of the stat to increase as a string
     * @param magnitude
     *            the amount to increase the given stat by
     */
    public StatIncreaseEffect(Effect effect, String stat, int magnitude) {
        super(effect);
        this.stat = stat;
        this.magnitude = magnitude;
    }

    /**
     * Unused in this Effect.
     */
    public void activate() {
    }

    /**
     * Increases stat of owner.
     */
    public void firstActivate() {
        applyEffect(owner);
        owner.recalculateStats();
        if (owner instanceof Player) {
            printDescription(playerEffectDescription);
        } else {
            printDescription(effectDescription);
        }
    }

    /**
     * Unused in this effect.
     */
    public void specialActivate() {

    }

    /**
     * Increases stat of owner.
     */
    public void applyEffect(Entity owner) {
        owner.increaseModifier(stat, magnitude);
    }

    @Override
    public void printDescription(String description) {
        String[] descriptionArray = description.split("#");

        for (String string : descriptionArray) {
            switch (string) {
            case "stat":
                Window.appendText(stat);
                break;
            case "magnitude":
                Window.appendText(Integer.toString(magnitude),
                        Colors.GAIN_STAT);
                break;
            case "time":
                Window.appendText(Integer.toString(duration), Colors.TURNS);
                break;
            default:
                Window.appendText(string);

                break;
            }

        }
        Window.appendText("\n");

    }

    public String getInfo() {
        return name + ": " + stat + " increased by " + magnitude + ". "
                + ((endTime - Time.getInstance().getTime()) / 60)
                + " hour(s) remaining.";

    }

    @Override
    public String toString() {
        return "StatIncreaseLongEffect [magnitude=" + magnitude + ", stat="
                + stat + "]";
    }

}
