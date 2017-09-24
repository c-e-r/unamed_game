package unamedGame.effects;

import java.util.List;

import unamedGame.entities.Entity;
import unamedGame.entities.Player;
import unamedGame.ui.Window;
import unamedGame.util.Colors;

/**
 * An effect that deals damage
 * 
 * @author c-e-r
 *
 */
public class DamageEffect extends Effect {

	private int magnitude;
	private int totalDamage;
	private String damageType;

	/**
	 * Creates a damageEffect from the given parameters
	 * 
	 * @param effects
	 *            the effects sub effects
	 * @param name
	 *            the name of the effect
	 * @param duration
	 *            the duration of the effect in turns
	 * @param increment
	 *            how often the effect will activate in turns
	 * @param baseAccuracy
	 *            the base chance for the effect to happen
	 * @param resistType
	 *            the stat that resists the effect
	 * @param repeatType
	 *            if and how the effect will repeat
	 * @param toSelf
	 *            if the effect will always apply to the user
	 * @param playerEffectDescription
	 *            the description to be displayed when the player gains the effect
	 * @param playerRepeatEffectDescription
	 *            the description to be displayed when the effect triggers on the
	 *            player
	 * @param effectDescription
	 *            the description to be displayed when a non-player Entity gains the
	 *            effect
	 * @param repeatEffectDescription
	 *            the description to be displayed when the effect triggers on a
	 *            non-player entity
	 * @param resistEffectDescription
	 *            the description to be displayed when a non-player entity resist
	 *            the effect when they gain it
	 * @param playerResistEffectDescription
	 *            the description to be displayed when the player resists the effect
	 *            when they gain it
	 * @param resistRepeatEffectDescription
	 *            the description to be displayed when a non-player Entity resists
	 *            the effect when it triggers
	 * @param playerResistRepeatEffectDescription
	 *            the description to be displayed when a player resists the effect
	 *            when it triggers
	 * @param damageType
	 *            the type of damage the effect deals
	 * @param magnitude
	 *            the amount of damage the effect deals
	 */
	public DamageEffect(List<Effect> effects, String name, int duration, int increment, int baseAccuracy,
			String resistType, String repeatType, boolean toSelf, String playerEffectDescription,
			String playerRepeatEffectDescription, String effectDescription, String repeatEffectDescription,
			String resistEffectDescription, String playerResistEffectDescription, String resistRepeatEffectDescription,
			String playerResistRepeatEffectDescription, String selfDestructTrigger, String selfDestructDescription,
			String playerSelfDestructDescription, String specialEffectTrigger, String specialEffectDescription,
			String playerSpecialEffectDescription, String specialResistEffectDescription,
			String playerSpecialResistEffectDescription, String specialResistType, int specialAccuracyBonus,
			String damageType, int magnitude) {
		super(effects, name, duration, increment, baseAccuracy, resistType, repeatType, toSelf, playerEffectDescription,
				playerRepeatEffectDescription, effectDescription, repeatEffectDescription, resistEffectDescription,
				playerResistEffectDescription, resistRepeatEffectDescription, playerResistRepeatEffectDescription,
				selfDestructTrigger, selfDestructDescription, playerSelfDestructDescription, specialEffectTrigger,
				specialEffectDescription, playerSpecialEffectDescription, specialResistEffectDescription,
				playerSpecialResistEffectDescription, specialResistType, specialAccuracyBonus);
		this.damageType = damageType;
		this.magnitude = magnitude;
	}

	/**
	 * Deals damage to the owner
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
	 * Deals damage to the owner
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
	 * Deals Damage to the owner
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
	 * Not used in this effect
	 */
	public void applyEffect(Entity owner) {
	}

	@Override
	public void printDescription(String description) {
		String[] descriptionArray = description.split("#");

		for (String string : descriptionArray) {
			switch (string) {
			case "damageType":
				Window.addToPane(Window.getInstance().getTextPane(), damageType);
				break;
			case "totalDamage":
				Window.addToPane(Window.getInstance().getTextPane(), Integer.toString(totalDamage), Colors.DAMAGE);
				break;
			default:
				if (!printSharedDescription(string)) {
					Window.addToPane(Window.getInstance().getTextPane(), string);

				}
				break;
			}

		}
		Window.appendToPane(Window.getInstance().getTextPane(), "");

	}

	@Override
	public String getInfo() {
		return "Damage Effect";
	}

}
