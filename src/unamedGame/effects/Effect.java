/**
 * 
 */
package unamedGame.effects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Random;

import org.dom4j.Element;

import unamedGame.Game;
import unamedGame.entities.*;
import unamedGame.time.Time;
import unamedGame.time.TimeObserver;
import unamedGame.ui.Window;

/**
 * A parent class to base all other effects off of
 * 
 * @author c-e-r
 *
 */
public abstract class Effect {

	protected List<Effect> effects;
	protected String name;
	protected int startTime;
	protected int endTime;
	protected int increment;
	protected int activateCount;
	protected String playerRepeatEffectDescription;
	protected String playerEffectDescription;
	protected String repeatEffectDescription;
	protected String effectDescription;
	protected String resistEffectDescription;
	protected String playerResistEffectDescription;
	protected String resistRepeatEffectDescription;
	protected String playerResistRepeatEffectDescription;
	protected int baseAccuracy;
	protected int creatorEffectMult = 1;
	protected int accuracy;
	protected String resistType;
	protected String repeatType;
	protected Entity owner;
	protected Effect parent;
	protected boolean toSelf;
	protected int duration;
	protected boolean active = false;
	int maxActivateCount;

	/**
	 * Creates an Effect from the given parameters
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
	 */
	public Effect(List<Effect> effects, String name, int duration, int increment, int baseAccuracy, String resistType,
			String repeatType, boolean toSelf, String playerEffectDescription, String playerRepeatEffectDescription,
			String effectDescription, String repeatEffectDescription, String resistEffectDescription,
			String playerResistEffectDescription, String resistRepeatEffectDescription,
			String playerResistRepeatEffectDescription) {
		this.effects = effects;
		this.duration = duration;
		this.name = name;
		this.increment = increment;
		this.toSelf = toSelf;
		this.playerEffectDescription = playerEffectDescription;
		this.playerRepeatEffectDescription = playerRepeatEffectDescription;
		this.repeatEffectDescription = repeatEffectDescription;
		this.effectDescription = effectDescription;
		this.baseAccuracy = baseAccuracy;
		this.resistType = resistType;
		if (this.resistType == null) {
			this.resistType = "none";
		}
		this.repeatType = repeatType;
		if (this.repeatType == null) {
			this.repeatType = "noRepeat";
		}

		for (Effect effect : effects) {
			effect.parent = this;
		}

		this.resistEffectDescription = resistEffectDescription;
		this.playerResistEffectDescription = playerResistEffectDescription;
		this.resistRepeatEffectDescription = resistRepeatEffectDescription;
		this.playerResistRepeatEffectDescription = playerResistRepeatEffectDescription;

		if (increment != 0) {
			maxActivateCount = duration / increment;

		}

	}

	/**
	 * Checks if the effect hits
	 * 
	 * @return if the effect hits
	 */
	protected boolean checkResistance() {
		if (baseAccuracy == -1) {
			return true;
		}
		Random rand = new Random();
		int target = accuracy;
		System.out.println(target);
		switch (resistType) {
		case "physical":
			target -= owner.getEffectivePhysicalResistance();
			break;
		case "mental":
			target -= owner.getEffectiveMentalResistance();
			break;

		default:
			creatorEffectMult = 1;
			break;
		}
		int temp = rand.nextInt(100);
		System.out.println(temp + " < " + target);
		return temp < target;

	}

	/**
	 * Calculates the effects accuracy based on base accuracy and the creators stats
	 * 
	 * @param creator
	 *            the creator of the effect
	 */
	private void calculateAccuracy(Entity creator) {
		switch (resistType) {
		case "physical":
			creatorEffectMult = creator.getPhysicalChanceMult();
			break;
		case "mental":
			creatorEffectMult = creator.getPhysicalChanceMult();

			break;
		default:
			creatorEffectMult = 1;
			break;
		}
		accuracy = baseAccuracy * creatorEffectMult;
	}

	/**
	 * Instantiates the effect.
	 * 
	 * @param owner
	 *            the owner of the effect
	 * @param creator
	 *            the creator of the effect
	 */
	public void instantiate(Entity owner, Entity creator) {
		this.owner = owner;
		calculateAccuracy(creator);

		if (checkResistance()) {
			startTime = Time.getInstance().getTime();
			if (duration > 0) {
				endTime = startTime + duration;
				setTimeObserver();
			} else if (duration == -1 && repeatType.equals("noRepeat")) {
				endTime = 0;
				setTimeObserver();
			}
			active = true;
			firstActivate();
		} else {
			printDescription();
		}

	}

	/**
	 * Does somthing based on effect implementation
	 */
	public abstract void activate();

	/**
	 * Does somthing based on effect implementation
	 */
	public abstract void firstActivate();

	/**
	 * Prints the effects description
	 */
	public abstract void printDescription();

	/**
	 * Checks if string is a keyword. If it is it prints it and returns true, if not
	 * it return false
	 * 
	 * @param string
	 *            the string to check for replacement
	 * @return
	 */
	protected boolean printSharedDescription(String string) {

		switch (string) {
		case "targetName":
			Window.addToPane(Window.getInstance().getTextPane(), owner.getUseName());
			break;
		case "targetNameCapital":
			System.out.println(owner == null);
			Window.addToPane(Window.getInstance().getTextPane(), Game.capitalizeFirstLetter(owner.getUseName()));
			break;
		default:
			return false;
		}

		return true;

	}

	/**
	 * Does somthing based on effect implementation
	 * 
	 * @param owner
	 */
	public abstract void applyEffect(Entity owner);

	/**
	 * Sets a TimeObserver for the effect.
	 */
	private void setTimeObserver() {
		Effect effect = this;

		if (startTime != endTime) {
			System.out.println("TIME OBSERVER ADDED: " + increment);
			Time.getInstance().addObserver(new TimeObserver() {
				@Override
				public void update(Observable o, Object arg) {
					System.out.println("OBSERVER ACTIVATED");
					if (increment > 0) {
						System.out.println("increment > 0");
						System.out.println(this);
						int newActivateCount;
						if (Time.getInstance().getTime() > endTime) {
							newActivateCount = (endTime - startTime) / increment;

						} else {
							newActivateCount = (Time.getInstance().getTime() - startTime) / increment;
						}
						if (duration == -1 || (activateCount < maxActivateCount && activateCount < newActivateCount)) {
							while (activateCount < newActivateCount) {
								System.out.println("EFFECT ACTIVATED");
								activateCount++;
								if (checkResistance()) {
									active = true;
									effect.activate();
								} else {
									if (repeatType.equals("untilResist")) {
										owner.removeEffect(effect);
										Time.getInstance().deleteObserver(this);
									}
									active = false;
									printDescription();

								}

							}
						}
					}
					if (Time.getInstance().getTime() >= endTime) {
						owner.removeEffect(effect);
						Time.getInstance().deleteObserver(this);
					}
					Player.getInstance().recalculateStats();

				}
			});
		}

	}

	/**
	 * Builds an Effect from the given XML element and returns it
	 * 
	 * @param element
	 * @return
	 */
	public static Effect buildEffect(Element element) {
		System.out.println(element.getName());

		int baseAccuracy;
		if (element.attributeValue("baseAccuracy") != null) {
			baseAccuracy = Integer.parseInt(element.attributeValue("baseAccuracy"));
		} else {
			baseAccuracy = -1;
		}

		int duration;
		if (element.attributeValue("length") != null) {
			duration = Integer.parseInt(element.attributeValue("length"));
		} else {
			duration = 0;
		}

		int increment;
		if (element.attributeValue("increment") != null) {
			increment = Integer.parseInt(element.attributeValue("increment"));
		} else {
			increment = 0;
		}

		List<Effect> effects = new ArrayList<Effect>();
		Iterator<Element> iterator = element.elementIterator();
		while (iterator.hasNext()) {
			Element temp = iterator.next();
			switch (temp.getName()) {
			case "effect":
				effects.add(Effect.buildEffect(temp));

				break;

			default:
				break;
			}

		}

		switch (element.getTextTrim()) {

		case "increaseStat":
			return new StatIncreaseEffect(effects, element.attributeValue("name"), duration, baseAccuracy,
					element.attributeValue("resistType"), element.attributeValue("repeatType"),
					Boolean.parseBoolean(element.attributeValue("toSelf")),
					element.attributeValue("playerEffectDescription"),
					element.attributeValue("playerRepeatEffectDescription"),
					element.attributeValue("effectDescription"), element.attributeValue("repeatEffectDescription"),
					element.attributeValue("resistEffectDescription"),
					element.attributeValue("playerResistEffectDescription"),
					element.attributeValue("resistRepeatEffectDescription"),
					element.attributeValue("repeatResistRepeatEffectDescription"), element.attributeValue("stat"),
					Integer.parseInt(element.attributeValue("magnitude")));
		case "heal":
			return new HealEffect(effects, element.attributeValue("name"), duration, increment, baseAccuracy,
					element.attributeValue("resistType"), element.attributeValue("repeatType"),
					Boolean.parseBoolean(element.attributeValue("toSelf")),
					element.attributeValue("playerEffectDescription"),
					element.attributeValue("playerRepeatEffectDescription"),
					element.attributeValue("effectDescription"), element.attributeValue("repeatEffectDescription"),
					element.attributeValue("resistEffectDescription"),
					element.attributeValue("playerResistEffectDescription"),
					element.attributeValue("resistRepeatEffectDescription"),
					element.attributeValue("repeatResistRepeatEffectDescription"),
					Integer.parseInt(element.attributeValue("magnitude")));
		case "damage":
			return new DamageEffect(effects, element.attributeValue("name"), duration, increment, baseAccuracy,
					element.attributeValue("resistType"), element.attributeValue("repeatType"),
					Boolean.parseBoolean(element.attributeValue("toSelf")),
					element.attributeValue("playerEffectDescription"),
					element.attributeValue("playerRepeatEffectDescription"),
					element.attributeValue("effectDescription"), element.attributeValue("repeatEffectDescription"),
					element.attributeValue("resistEffectDescription"),
					element.attributeValue("playerResistEffectDescription"),
					element.attributeValue("resistRepeatEffectDescription"),
					element.attributeValue("playerResistRepeatEffectDescription"), element.attributeValue("damageType"),
					Integer.parseInt(element.attributeValue("magnitude")));
		default:

			return null;

		}
	}

	/**
	 * Returns the effects increment
	 * 
	 * @return the effects increment
	 */
	public int getIncrement() {
		return increment;
	}

	/**
	 * Returns the effects duration
	 * 
	 * @return the effects duration
	 */
	public int getDuration() {
		return duration;
	}

	/**
	 * Returns true if the effect always applies to the user
	 * 
	 * @return if the effect always applies to the user
	 */
	public boolean isToSelf() {
		return toSelf;
	}

	/**
	 * Returns the effects effects ArrayList
	 * 
	 * @return
	 */
	public List<Effect> getEffects() {
		return effects;
	}

	/**
	 * Returns the effects repeat type
	 * 
	 * @return the the effects repeatType
	 */
	public String getRepeatType() {
		return repeatType;
	}

	@Override
	public String toString() {
		return "LongEffect [startTime=" + startTime + ", endTime=" + endTime + ", increment=" + increment
				+ ", activateCount=" + activateCount + ", playerEffectDescription=" + playerRepeatEffectDescription
				+ "]";
	}

	/**
	 * Returns a description of the effect
	 * 
	 * @return
	 */
	public abstract String getInfo();
}
