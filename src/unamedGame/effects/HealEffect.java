package unamedGame.effects;

import java.util.List;

import unamedGame.entities.Entity;
import unamedGame.entities.Player;
import unamedGame.time.Time;
import unamedGame.ui.Window;
import unamedGame.util.Colors;

/**
 * An effect that heals health
 * 
 * @author camer
 *
 */
public class HealEffect extends Effect {

	private int magnitude;
	private int totalHeal;

	/**
	 * Creates a healEffect from the given parameters
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
	 * @param magnitude
	 *            the amount of health the effect restores
	 */
	public HealEffect(List<Effect> effects, String name, int duration, int increment, int baseAccuracy,
			String resistType, String repeatType, boolean toSelf, String playerEffectDescription,
			String playerRepeatEffectDescription, String effectDescription, String repeatEffectDescription,
			String resistEffectDescription, String playerResistEffectDescription, String resistRepeatEffectDescription,
			String playerResistRepeatEffectDescription, int magnitude) {
		super(effects, name, duration, increment, baseAccuracy, resistType, repeatType, toSelf, playerEffectDescription,
				playerRepeatEffectDescription, effectDescription, repeatEffectDescription, resistEffectDescription,
				playerResistEffectDescription, resistRepeatEffectDescription, playerResistRepeatEffectDescription);
		this.magnitude = magnitude;
	}

	/**
	 * Heals health to the owner
	 */
	public void activate() {

		totalHeal = owner.restoreHealth(magnitude);

		printDescription();
		System.out.println("activate count: " + activateCount);
	}

	/**
	 * Heals health to the owner
	 */
	public void firstActivate() {
		applyEffect(owner);
		totalHeal = owner.restoreHealth(magnitude);

		printDescription();

	}

	/**
	 * Not used in this effect
	 */
	public void applyEffect(Entity owner) {
	}

	@Override
	public void printDescription() {
		String[] description;
		if (active) {
			if (activateCount == 0) {
				if (owner instanceof Player) {
					description = playerEffectDescription.split("#");
				} else {
					description = effectDescription.split("#");
				}
			} else {
				if (owner instanceof Player) {

					description = playerRepeatEffectDescription.split("#");
				} else {
					description = repeatEffectDescription.split("#");
				}
			}
		} else {
			if (activateCount == 0) {
				if (owner instanceof Player) {
					description = playerResistEffectDescription.split("#");
				} else {
					description = resistEffectDescription.split("#");
				}
			} else {
				if (owner instanceof Player) {
					System.out.println(playerResistRepeatEffectDescription);
					description = playerResistRepeatEffectDescription.split("#");
				} else {
					description = resistRepeatEffectDescription.split("#");
				}
			}
		}

		StringBuilder builder = new StringBuilder();

		for (String string : description) {
			switch (string) {
			case "totalHeal":
				Window.addToPane(Window.getInstance().getTextPane(), Integer.toString(totalHeal), Colors.HEAL);
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
		return "Heal Effect";
	}

}
