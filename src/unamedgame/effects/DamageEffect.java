package unamedgame.effects;

import java.util.List;

import unamedgame.entities.Entity;
import unamedgame.entities.Player;
import unamedgame.ui.Window;
import unamedgame.util.Colors;

/**
 * An effect that deals damage.
 * 
 * @author c-e-r
 * @version 0.0.1
 */
public class DamageEffect extends Effect {

    private static final long serialVersionUID = -6069813639114427398L;
    private int magnitude;
    private int totalDamage;
    private String damageType;

    /**
     * Creates a damageEffect from the given parameters.
     * 
     * @see Effect#Effect(List, String, int, int, int, String, String, boolean,
     *      String, String, String, String, String, String, String, String,
     *      String, String, String, String, String, String, String, String,
     *      String, int)
     * @param damageType
     *            the type of damage the effect deals
     * @param magnitude
     *            the amount of damage the effect deals
     */
    public DamageEffect(Effect effect, String damageType, int magnitude) {
        super(effect);
        this.damageType = damageType;
        this.magnitude = magnitude;
    }

    /**
     * Deals damage to the owner.
     */
    public void activate() {
        totalDamage = owner.takeDamage(magnitude, damageType);
        if (owner instanceof Player) {
            printDescription(playerRepeatEffectDescription);
        } else {
            printDescription(repeatEffectDescription);
        }
    }

    /**
     * Deals damage to the owner.
     */
    public void firstActivate() {
        totalDamage = owner.takeDamage(magnitude, damageType);
        if (owner instanceof Player) {
            printDescription(playerEffectDescription);
        } else {
            printDescription(effectDescription);
        }
    }

    /**
     * Deals Damage to the owner.
     */
    public void specialActivate() {
        totalDamage = owner.takeDamage(magnitude, damageType);
        if (owner instanceof Player) {
            printDescription(playerSpecialEffectDescription);
        } else {
            printDescription(specialEffectDescription);
        }
    }

    /**
     * Not used in this effect.
     * 
     * @param owner
     *            the owner of the effect
     */
    public void applyEffect(Entity owner) {
    }

    @Override
    public void printDescription(String description) {
        String[] descriptionArray = description.split("#");

        for (String string : descriptionArray) {
            switch (string) {
            case "damageType":
                Window.appendText(damageType);
                break;
            case "totalDamage":
                if (totalDamage == 0) {
                    Window.appendText(Integer.toString(totalDamage),
                            Colors.DAMAGE_BLOCK);

                } else {
                    Window.appendText(Integer.toString(totalDamage),
                            Colors.DAMAGE);

                }
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
        return "Damage Effect";
    }

}
