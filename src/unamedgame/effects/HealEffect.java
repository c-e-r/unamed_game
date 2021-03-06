package unamedgame.effects;

import java.util.List;

import unamedgame.entities.Entity;
import unamedgame.entities.Player;
import unamedgame.ui.Window;
import unamedgame.util.Colors;

/**
 * An effect that heals health.
 * 
 * @author c-e-r
 * @version 0.0.1
 */
public class HealEffect extends Effect {

    private static final long serialVersionUID = 7183392219956845366L;
    private int magnitude;
    private int totalHeal;

    /**
     * Creates a healEffect from the given parameters.
     * 
     * @see Effect#Effect(List, String, int, int, int, String, String, boolean,
     *      String, String, String, String, String, String, String, String,
     *      String, String, String, String, String, String, String, String,
     *      String, int)
     * @param magnitude
     *            the amount of health the effect restores
     */
    public HealEffect(Effect effect,int magnitude) {
        super(effect);
        this.magnitude = magnitude;
    }

    /**
     * Heals health to the owner.
     */
    public void activate() {
        totalHeal = owner.restoreHealth(magnitude);
        if (owner instanceof Player) {
            printDescription(playerRepeatEffectDescription);
        } else {
            printDescription(repeatEffectDescription);
        }
    }

    /**
     * Heals health to the owner.
     */
    public void firstActivate() {
        totalHeal = owner.restoreHealth(magnitude);
        if (owner instanceof Player) {
            printDescription(playerEffectDescription);
        } else {
            printDescription(effectDescription);
        }
    }

    /**
     * Heals health to the player.
     */
    public void specialActivate() {
        totalHeal = owner.restoreHealth(magnitude);
        if (owner instanceof Player) {
            printDescription(playerSpecialEffectDescription);
        } else {
            printDescription(specialEffectDescription);
        }
    }

    /**
     * Not used in this effect.
     */
    public void applyEffect(Entity owner) {
    }

    @Override
    public void printDescription(String description) {
        String[] descriptionArray = description.split("#");

        for (String string : descriptionArray) {
            switch (string) {
            case "totalHeal":
                Window.appendText(Integer.toString(totalHeal), Colors.HEAL);
                break;
            default:
                if (!printSharedDescription(string)) {
                    Window.appendText(string);

                }
                break;
            }

        }
        Window.appendText("\n");

    }

    @Override
    public String getInfo() {
        return "Heal Effect";
    }

}
