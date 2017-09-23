package unamedGame.effects;

import java.util.List;

import unamedGame.entities.Entity;
import unamedGame.entities.Player;
import unamedGame.time.Time;
import unamedGame.ui.Window;
import unamedGame.util.Colors;

/**
 * An Effect that increases one of the stats of an entity
 * @author c-e-r
 *
 */
public class StatIncreaseEffect extends Effect {

	private int magnitude;
	private String stat;

	/**
	 * Creates a StatIncreaseEffect from the given parameters
	 * @param effects
	 *            the effects sub effects
	 * @param name
	 *            the name of the effect
	 * @param duration
	 *            the duration of the effect in turns
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
	 * @param stat the name of the stat to increase as a string
	 * @param magnitude the amount to increase the given stat by
	 */
	public StatIncreaseEffect(List<Effect> effects,String name, int duration, int baseAccuracy, String resistType, String repeatType,
			boolean toSelf, String playerEffectDescription, String playerRepeatEffectDescription,
			String effectDescription, String repeatEffectDescription, String resistEffectDescription,
			String playerResistEffectDescription, String resistRepeatEffectDescription,
			String playerResistRepeatEffectDescription, String stat, int magnitude) {
		super(effects,name, duration, 0, baseAccuracy, resistType, repeatType, toSelf, playerEffectDescription,
				playerRepeatEffectDescription, effectDescription, repeatEffectDescription, resistEffectDescription,
				playerResistEffectDescription, resistRepeatEffectDescription, playerResistRepeatEffectDescription);
		this.stat = stat;
		this.magnitude = magnitude;
	}

	/**
	 * Unused in this Effect.
	 */
	public void activate() {
	}

	/**
	 * Increases stat of owner
	 */
	public void firstActivate() {
		applyEffect(owner);
		owner.recalculateStats();
		printDescription();

	}

	/**
	 * Increases stat of owner
	 */
	public void applyEffect(Entity owner) {
		owner.increaseModifier(stat, magnitude);
	}


	@Override
	public void printDescription() {
		String[] description = null;

		description = playerEffectDescription.split("#");

		StringBuilder builder = new StringBuilder();

		for (String string : description) {
			switch (string) {
			case "stat":
				Window.addToPane(Window.getInstance().getTextPane(), stat);
				break;
			case "magnitude":
				Window.addToPane(Window.getInstance().getTextPane(), Integer.toString(magnitude), Colors.GAIN_STAT);
				break;
			case "time":
				Window.addToPane(Window.getInstance().getTextPane(), Integer.toString((duration) / 60), Colors.TURNS);
				break;
			default:
				Window.addToPane(Window.getInstance().getTextPane(), string);

				break;
			}

		}
		Window.appendToPane(Window.getInstance().getTextPane(), "");

	}

	public String getInfo() {
		return name + ": " + stat + " increased by " + magnitude + ". "
				+ ((endTime - Time.getInstance().getTime()) / 60) + " hour(s) remaining.";

	}

	@Override
	public String toString() {
		return "StatIncreaseLongEffect [magnitude=" + magnitude + ", stat=" + stat + "]";
	}

}
