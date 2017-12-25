package unamedgame.effects;

import java.util.List;

import unamedgame.Dice;
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
    private int base;
    private String stat;
    private double scaling;
    private int critChance;
    private double critMult;
    private int totalDamage;
    private double percentRoll;
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
    public DamageEffect(Effect effect, String damageType, int base, String stat,
            double scaling, int critChance, double critMult,
            double percentRoll) {
        super(effect);
        this.damageType = damageType;
        this.base = base;
        this.stat = stat;
        this.critChance = critChance;
        this.critMult = critMult;
        this.percentRoll = percentRoll;
    }

    /**
     * Deals damage to the owner.
     */
    public void activate() {
        int statValue = (int) creator.getStat(stat);
        double scaled = scaling*statValue;
        scaled = scaled == 0? 1 :scaled;
        double roll = Dice.rollPercent(percentRoll);
        int damage;
        damage = (int) ((base * scaled)
                * roll);
        LOG.debug(String.format("(%d * %2f (%2f * %d) * %2f ", base, scaled,scaling,  statValue, roll));
        totalDamage = owner.takeDamage(damage, damageType);
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
        int statValue = (int) creator.getStat(stat);
        double scaled = scaling*statValue;
        scaled = scaled == 0? 1 :scaled;
        double roll = Dice.rollPercent(percentRoll);
        int damage;
        damage = (int) ((base * scaled)
                * roll);
        LOG.debug(String.format("(%d * %2f (%2f * %d) * %2f ", base, scaled,scaling,  statValue, roll));
        totalDamage = owner.takeDamage(damage, damageType);
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
        int statValue = (int) creator.getStat(stat);
        double scaled = scaling*statValue;
        scaled = scaled == 0? 1 :scaled;
        double roll = Dice.rollPercent(percentRoll);
        int damage;
        damage = (int) ((base * scaled)
                * roll);
        LOG.debug(String.format("(%d * %2f{%2f * %d} * %2f ", base, scaled,scaling,  statValue, roll));
        totalDamage = owner.takeDamage(damage, damageType);
        if (owner instanceof Player) {
            printDescription(playerRepeatEffectDescription);
        } else {
            printDescription(repeatEffectDescription);
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
