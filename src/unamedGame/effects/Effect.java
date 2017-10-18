/**
 * 
 */
package unamedGame.effects;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;

import org.dom4j.Element;

import unamedGame.Dice;
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
	protected String selfDestructTrigger;
	protected String selfDestructDescription;
	protected String playerSelfDestructDescription;
	protected String specialEffectTrigger;
	protected String specialEffectDescription;
	protected String playerSpecialEffectDescription;
	protected String specialResistEffectDescription;
	protected String playerSpecialResistEffectDescription;
	protected String specialResistType;
	protected int specialAccuracyBonus;
	protected int specialAccuracy;
	protected EntityObserver selfDestructionObserver;
	protected EntityObserver specialEffectObserver;
	protected TimeObserver timeObserver;

	int maxActivateCount;
	protected boolean destroyed;

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
	 * @param selfDestructTrigger
	 *            the condition under which the effect will destroy itself
	 * @param selfDestructDescription
	 *            the description to be displayed when the effect destroys itself
	 *            while on a non-player entity
	 * @param playerSelfDestructDescription
	 *            the description to be displayed when the effect destroys itself
	 *            while on the player
	 * @param specialEffectTrigger
	 *            the condition under which specialActivate will trigger
	 * @param specialEffectDescription
	 *            the description to be displayed when the special effect is
	 *            activates while on a non-player entity
	 * @param playerSpecialEffectDescription
	 *            the description to be displayed when the special effect activates
	 *            while on the player
	 * @param specialResistEffectDescription
	 *            the description to be displayed when a non-player entity resists
	 *            the special effect
	 * @param playerSpecialResistEffectDescription
	 *            the description to be displayed when the player reists a special
	 *            effect
	 * @param specialResistType
	 *            the resistType of the special effect
	 * @param specialAccuracyBonus
	 *            the accuracy bonus of the special effect
	 */
	public Effect(List<Effect> effects, String name, int duration, int increment, int baseAccuracy, String resistType,
			String repeatType, boolean toSelf, String playerEffectDescription, String playerRepeatEffectDescription,
			String effectDescription, String repeatEffectDescription, String resistEffectDescription,
			String playerResistEffectDescription, String resistRepeatEffectDescription,
			String playerResistRepeatEffectDescription, String selfDestructTrigger, String selfDestructDescription,
			String playerSelfDestructDescription, String specialEffectTrigger, String specialEffectDescription,
			String playerSpecialEffectDescription, String specialResistEffectDescription,
			String playerSpecialResistEffectDescription, String specialResistType, int specialAccuracyBonus) {
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
		this.selfDestructTrigger = selfDestructTrigger;
		this.selfDestructDescription = selfDestructDescription;
		this.playerSelfDestructDescription = playerSelfDestructDescription;
		this.specialEffectTrigger = specialEffectTrigger;
		this.specialEffectDescription = specialEffectDescription;
		this.playerSpecialEffectDescription = playerSpecialEffectDescription;
		this.specialResistEffectDescription = specialResistEffectDescription;
		this.playerSpecialResistEffectDescription = playerSpecialResistEffectDescription;
		this.specialAccuracyBonus = specialAccuracyBonus;
		this.specialResistType = specialResistType;
		if (this.specialResistType == null) {
			this.specialResistType = "none";
		}

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
		if (resistType.equals("none")) {
			return true;
		}
		int target = accuracy;
		switch (resistType) {
		case "physical":
			target -= owner.getEffectivePhysicalResistance();
			break;
		case "mental":
			target -= owner.getEffectiveMentalResistance();
			break;

		}
		int temp = Dice.roll(Dice.RESISTANCE_DIE);
		return temp < target;

	}

	/**
	 * Checks if the special effect hits
	 * 
	 * @return if the special effect hits
	 */
	protected boolean checkSpecialResistance() {
		if (specialResistType.equals("none")) {
			return true;
		}
		int target = specialAccuracy;
		switch (resistType) {
		case "physical":
			target -= owner.getEffectivePhysicalResistance();
			break;
		case "mental":
			target -= owner.getEffectiveMentalResistance();
			break;

		}
		int temp = Dice.roll(Dice.RESISTANCE_DIE);
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
		specialAccuracy = baseAccuracy + specialAccuracyBonus * creatorEffectMult;
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

			} else if (duration == -1 && !repeatType.equals("noRepeat")) {
				endTime = 0;
				setTimeObserver();

			}
			if (duration > 0 || duration == -1) {
				if (selfDestructTrigger != null) {
					setSelfDestructionObserver();
				}
				if (specialEffectTrigger != null) {
					setSpecialEffectObserver();
				}
			}

			active = true;
			firstActivate();
		} else {
			active = false;
			if (owner instanceof Player) {
				printDescription(playerResistEffectDescription);
			} else {
				printDescription(resistEffectDescription);
			}
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
	 * Prints the description given to it replacing placeholder words with values
	 * 
	 * @param description
	 *            the description to print
	 */
	public abstract void printDescription(String description);

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
	 * Set an observer on the owner to destroy itself when a condition is met
	 */
	private void setSelfDestructionObserver() {
		Effect effect = this;

		owner.addObserver(selfDestructionObserver = new EntityObserver() {
			@Override
			public void update(Observable o, Object arg) {
				super.update(o, arg);
				if (selfDestructTrigger.equals((String) arg)) {
					effect.selfDestruct();
					owner.deleteObserver(this);
				}
			}
		});
	}

	/**
	 * Destroy the effect and print its self destruct text
	 */
	protected void selfDestruct() {
		owner.removeEffect(this);
		owner.deleteObserver(specialEffectObserver);
		owner.deleteObservers();

		String[] description;

		destroyed = true;
		if (owner instanceof Player) {
			description = playerSelfDestructDescription.split("#");
		} else {
			description = selfDestructDescription.split("#");
		}
		for (String string : description) {
			if (!printSharedDescription(string)) {
				Window.addToPane(Window.getInstance().getTextPane(), string);
			}
		}
		Window.appendToPane(Window.getInstance().getTextPane(), "");
	}

	/**
	 * Set an observer on the owner to trigger specialActivate when a condition is
	 * met
	 */
	private void setSpecialEffectObserver() {
		Effect effect = this;

		owner.addObserver(new EntityObserver() {
			@Override
			public void update(Observable o, Object arg) {
				super.update(o, arg);
				if (specialEffectTrigger.equals((String) arg)) {
					if (checkSpecialResistance()) {
						effect.specialActivate();
					} else {
						if (owner instanceof Player) {
							printDescription(playerSpecialResistEffectDescription);

						} else {
							printDescription(specialResistEffectDescription);
						}
					}
				}
			}
		});
	}

	/**
	 * Implementation depends on specific effect
	 */
	protected abstract void specialActivate();

	/**
	 * Sets a TimeObserver for the effect.
	 */
	private void setTimeObserver() {
		Effect effect = this;

		Time.getInstance().addObserver(timeObserver = new TimeObserver() {
			@Override
			public void update(Observable o, Object arg) {
				if (destroyed) {
					Time.getInstance().deleteObserver(this);

				} else {

					if (increment > 0) {
						int newActivateCount;
						if (Time.getInstance().getTime() > endTime && endTime != 0) {
							newActivateCount = (endTime - startTime) / increment;

						} else {
							newActivateCount = (Time.getInstance().getTime() - startTime) / increment;
						}
						if (duration == -1 || (activateCount < maxActivateCount && activateCount < newActivateCount)) {
							while (activateCount < newActivateCount) {
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
									if (owner instanceof Player) {
										printDescription(playerResistRepeatEffectDescription);

									} else {
										printDescription(resistRepeatEffectDescription);
									}

								}

							}
						}
					}
					if (Time.getInstance().getTime() >= endTime) {
						owner.removeEffect(effect);
					}
					Player.getInstance().recalculateStats();

				}

			}
		});

	}

	/**
	 * Builds an Effect from the given XML element and returns it
	 * 
	 * @param element
	 * @return
	 */
	public static Effect buildEffect(Element element) {

		int baseAccuracy;
		if (element.attributeValue("baseAccuracy") != null) {
			baseAccuracy = Integer.parseInt(element.attributeValue("baseAccuracy"));
		} else {
			baseAccuracy = 0;
		}

		int specialAccuracyBonus;

		if (element.attributeValue("specialAccuracyBonus") != null) {
			specialAccuracyBonus = Integer.parseInt(element.attributeValue("specialAccuracyBonus"));
		} else {
			specialAccuracyBonus = 0;
		}

		int duration;
		if (element.attributeValue("duration") != null) {
			duration = Integer.parseInt(element.attributeValue("duration"));
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
					element.attributeValue("repeatResistRepeatEffectDescription"),
					element.attributeValue("selfDestructTrigger"), element.attributeValue("selfDestructDescription"),
					element.attributeValue("playerSelfDestructDescription"),
					element.attributeValue("specialEffectTrigger"), element.attributeValue("specialEffectDescription"),
					element.attributeValue("playerSpecialEffectDescription"),
					element.attributeValue("specialResistEffectDescription"),
					element.attributeValue("playerSpecialResistEffectDescription"),
					element.attributeValue("specialResistType"), specialAccuracyBonus, element.attributeValue("stat"),
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
					element.attributeValue("selfDestructTrigger"), element.attributeValue("selfDestructDescription"),
					element.attributeValue("playerSelfDestructDescription"),
					element.attributeValue("specialEffectTrigger"), element.attributeValue("specialEffectDescription"),
					element.attributeValue("playerSpecialEffectDescription"),
					element.attributeValue("specialResistEffectDescription"),
					element.attributeValue("playerSpecialResistEffectDescription"),
					element.attributeValue("specialResistType"), specialAccuracyBonus,
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
					element.attributeValue("playerResistRepeatEffectDescription"),
					element.attributeValue("selfDestructTrigger"), element.attributeValue("selfDestructDescription"),
					element.attributeValue("playerSelfDestructDescription"),
					element.attributeValue("specialEffectTrigger"), element.attributeValue("specialEffectDescription"),
					element.attributeValue("playerSpecialEffectDescription"),
					element.attributeValue("specialResistEffectDescription"),
					element.attributeValue("playerSpecialResistEffectDescription"),
					element.attributeValue("specialResistType"), specialAccuracyBonus,
					element.attributeValue("damageType"), Integer.parseInt(element.attributeValue("magnitude")));
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

	/**
	 * Returns the effects name
	 * 
	 * @return the name of the effect
	 */
	public String getName() {
		return name;
	}

	/**
	 * @return the endTime
	 */
	public int getEndTime() {
		return endTime;
	}

	/**
	 * @param endTime the endTime to set
	 */
	public void setEndTime(int endTime) {
		this.endTime = endTime;
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
