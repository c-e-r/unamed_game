/**
 * 
 */
package unamedgame.effects;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Element;

import unamedgame.Dice;
import unamedgame.Game;
import unamedgame.entities.*;
import unamedgame.time.Time;
import unamedgame.time.TimeListener;
import unamedgame.ui.Window;

/**
 * A parent class to base all other effects off of.
 * 
 * @author c-e-r
 * @version 0.0.1
 */
public class Effect implements Serializable {

    private static final long serialVersionUID = 1153105558946744535L;

    protected static final Logger LOG = LogManager.getLogger(Game.class);

    protected List<Effect> effects;
    protected String id;
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
    protected double creatorEffectMult = 1;
    protected int accuracy;
    protected String resistType;
    protected String repeatType;
    protected Entity owner;
    protected Entity creator;
    protected Effect parent;
    protected boolean toSelf;
    protected int duration;
    protected boolean active = false;
    protected ArrayList<String> selfDestructTriggers;
    protected String selfDestructDescription;
    protected String playerSelfDestructDescription;
    protected ArrayList<String> specialEffectTriggers;
    protected String specialEffectDescription;
    protected String playerSpecialEffectDescription;
    protected String specialResistEffectDescription;
    protected String playerSpecialResistEffectDescription;
    protected String specialResistType;
    protected int specialAccuracyBonus;
    protected int specialAccuracy;
    protected EntityListener selfDestructionListener;
    protected EntityListener specialEffectListener;
    protected TimeListener timeListener;

    int maxActivateCount;
    protected boolean destroyed;

    /**
     * Creates an Effect from the given parameters.
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
     *            the description to be displayed when the player gains the
     *            effect
     * @param playerRepeatEffectDescription
     *            the description to be displayed when the effect triggers on
     *            the player
     * @param effectDescription
     *            the description to be displayed when a non-player Entity gains
     *            the effect
     * @param repeatEffectDescription
     *            the description to be displayed when the effect triggers on a
     *            non-player entity
     * @param resistEffectDescription
     *            the description to be displayed when a non-player entity
     *            resist the effect when they gain it
     * @param playerResistEffectDescription
     *            the description to be displayed when the player resists the
     *            effect when they gain it
     * @param resistRepeatEffectDescription
     *            the description to be displayed when a non-player Entity
     *            resists the effect when it triggers
     * @param playerResistRepeatEffectDescription
     *            the description to be displayed when a player resists the
     *            effect when it triggers
     * @param selfDestructTrigger
     *            the condition under which the effect will destroy itself
     * @param selfDestructDescription
     *            the description to be displayed when the effect destroys
     *            itself while on a non-player entity
     * @param playerSelfDestructDescription
     *            the description to be displayed when the effect destroys
     *            itself while on the player
     * @param specialEffectTrigger
     *            the condition under which specialActivate will trigger
     * @param specialEffectDescription
     *            the description to be displayed when the special effect is
     *            activates while on a non-player entity
     * @param playerSpecialEffectDescription
     *            the description to be displayed when the special effect
     *            activates while on the player
     * @param specialResistEffectDescription
     *            the description to be displayed when a non-player entity
     *            resists the special effect
     * @param playerSpecialResistEffectDescription
     *            the description to be displayed when the player reists a
     *            special effect
     * @param specialResistType
     *            the resistType of the special effect
     * @param specialAccuracyBonus
     *            the accuracy bonus of the special effect
     */
    private Effect(List<Effect> effects, String id, String name, int duration,
            int increment, int baseAccuracy, String resistType,
            String repeatType, boolean toSelf, String playerEffectDescription,
            String playerRepeatEffectDescription, String effectDescription,
            String repeatEffectDescription, String resistEffectDescription,
            String playerResistEffectDescription,
            String resistRepeatEffectDescription,
            String playerResistRepeatEffectDescription,
            ArrayList<String> selfDestructTrigger,
            String selfDestructDescription,
            String playerSelfDestructDescription,
            ArrayList<String> specialEffectTrigger,
            String specialEffectDescription,
            String playerSpecialEffectDescription,
            String specialResistEffectDescription,
            String playerSpecialResistEffectDescription,
            String specialResistType, int specialAccuracyBonus) {
        this.effects = effects;
        this.duration = duration;
        this.id = id;
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
        this.selfDestructTriggers = selfDestructTrigger;
        this.selfDestructDescription = selfDestructDescription;
        this.playerSelfDestructDescription = playerSelfDestructDescription;
        this.specialEffectTriggers = specialEffectTrigger;
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

    public Effect(Effect effect) {
        this.effects = effect.effects;
        for (Effect childEffect : effects) {
            childEffect.parent = this;
        }
        this.id = effect.id;
        this.duration = effect.duration;
        this.name = effect.name;
        this.increment = effect.increment;
        this.toSelf = effect.toSelf;
        this.playerEffectDescription = effect.playerEffectDescription;
        this.playerRepeatEffectDescription = effect.playerRepeatEffectDescription;
        this.repeatEffectDescription = effect.repeatEffectDescription;
        this.effectDescription = effect.effectDescription;
        this.baseAccuracy = effect.baseAccuracy;
        this.resistType = effect.resistType;
        this.repeatType = effect.repeatType;
        this.resistEffectDescription = effect.resistEffectDescription;
        this.playerResistEffectDescription = effect.playerResistEffectDescription;
        this.resistRepeatEffectDescription = effect.resistRepeatEffectDescription;
        this.playerResistRepeatEffectDescription = effect.playerResistRepeatEffectDescription;
        this.selfDestructTriggers = effect.selfDestructTriggers;
        this.selfDestructDescription = effect.selfDestructDescription;
        this.playerSelfDestructDescription = effect.playerSelfDestructDescription;
        this.specialEffectTriggers = effect.specialEffectTriggers;
        this.specialEffectDescription = effect.specialEffectDescription;
        this.playerSpecialEffectDescription = effect.playerSpecialEffectDescription;
        this.specialResistEffectDescription = effect.specialResistEffectDescription;
        this.playerSpecialResistEffectDescription = effect.playerSpecialResistEffectDescription;
        this.specialAccuracyBonus = effect.specialAccuracyBonus;
        this.specialResistType = effect.specialResistType;

        if (increment != 0) {
            maxActivateCount = duration / increment;
        }
    }

    /**
     * Checks if the effect hits.
     * 
     * @return if the effect hits
     */
    protected boolean checkResistance() {
        if (resistType.equals("none")) {
            return true;
        }
        int target = accuracy;
        int resistance = 0;
        switch (resistType) {
        case "physical":
            resistance = owner.getEffectivePhysicalResistance();
            break;
        case "mental":
            resistance = owner.getEffectiveMentalResistance();
            break;
        default:
            break;

        }
        target -= resistance;
        int temp = Dice.roll(Dice.RESISTANCE_DIE);
        LOG.debug("d100 < effectAcuracy - targetResistance");
        LOG.debug("Special effect roll: " + temp + "[1-" + Dice.RESISTANCE_DIE
                + "] < " + accuracy + " - " + resistance);
        return temp < target;

    }

    /**
     * Checks if the special effect hits.
     * 
     * @return if the special effect hits
     */
    protected boolean checkSpecialResistance() {
        if (specialResistType.equals("none")) {
            return true;
        }
        int target = specialAccuracy;
        int resistance = 0;
        switch (resistType) {
        case "physical":
            resistance = owner.getEffectivePhysicalResistance();
            break;
        case "mental":
            resistance = owner.getEffectiveMentalResistance();
            break;
        default:
            break;

        }
        target -= resistance;
        int temp = Dice.roll(Dice.RESISTANCE_DIE);
        LOG.debug("d100 < specialEffectAccuracy - targetResistance");
        LOG.debug("Special effect roll: " + temp + "[1-" + Dice.RESISTANCE_DIE
                + "] < " + specialAccuracy + " - " + resistance);
        return temp < target;

    }

    /**
     * Calculates the effects accuracy based on base accuracy and the creators
     * stats.
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
        accuracy = (int) (baseAccuracy * creatorEffectMult);
        specialAccuracy = (int) (baseAccuracy
                + specialAccuracyBonus * creatorEffectMult);
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
        this.creator = creator;
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
                if (selfDestructTriggers.size() != 0) {
                    setSelfDestructionObserver();
                }
                if (specialEffectTriggers.size() != 0) {
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
     * Does somthing based on effect implementation.
     */
    public void activate() {

    }

    /**
     * Does something based on effect implementation.
     */
    public void firstActivate() {

    }

    /**
     * Prints the description given to it replacing placeholder words with
     * values.
     * 
     * @param description
     *            the description to print
     */
    public void printDescription(String description) {

    }

    /**
     * Checks if string is a keyword. If it is it prints it and returns true, if
     * not it return false
     * 
     * @param string
     *            the string to check for replacement
     * @return if the word was printed to the window
     */
    protected boolean printSharedDescription(String string) {

        switch (string) {
        case "targetName":
            Window.appendText(owner.getUseName());
            break;
        case "targetNameCapital":
            Window.appendText(Game.capitalizeFirstLetter(owner.getUseName()));
            break;
        default:
            return false;
        }

        return true;

    }

    /**
     * Does somthing based on effect implementation.
     * 
     * @param owner
     *            the owner of the effect
     */
    public void applyEffect(Entity owner) {

    }

    /**
     * Set an observer on the owner to destroy itself when a condition is met.
     */
    private void setSelfDestructionObserver() {
        Effect effect = this;

        selfDestructionListener = new EntityListener() {
            /**
             * 
             */
            private static final long serialVersionUID = -8406189170907410554L;

            @Override
            public void action(String event) {
                if (selfDestructTriggers.contains(event)) {
                    effect.selfDestruct();
                    setDelete();
                }
            }
        };
        owner.addEntityListener(selfDestructionListener);
    }

    /**
     * Destroy the effect and print its self destruct text.
     */
    protected void selfDestruct() {
        owner.removeEffect(this);
        specialEffectListener.setDelete();
        timeListener.setDelete();
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
                Window.appendText(string);
            }
        }
        Window.appendText("\n");
    }

    /**
     * Set an observer on the owner to trigger specialActivate when a condition
     * is met.
     */
    private void setSpecialEffectObserver() {
        Effect effect = this;

        specialEffectListener = new EntityListener() {
            /**
             * 
             */
            private static final long serialVersionUID = 1389343275668841811L;

            @Override
            public void action(String event) {
                if (specialEffectTriggers.contains(event)) {
                    if (checkSpecialResistance()) {
                        effect.specialActivate();
                    } else {
                        if (owner instanceof Player) {
                            printDescription(
                                    playerSpecialResistEffectDescription);

                        } else {
                            printDescription(specialResistEffectDescription);
                        }
                    }
                }
            }
        };
        owner.addEntityListener(specialEffectListener);
    }

    /**
     * Implementation depends on specific effect.
     */
    protected void specialActivate() {

    }

    /**
     * Sets a TimeObserver for the effect.
     */
    private void setTimeObserver() {
        Effect effect = this;

        timeListener = new TimeListener() {
            /**
             * 
             */
            private static final long serialVersionUID = 6561115758201409530L;

            @Override
            public void action() {
                if (increment > 0) {
                    int newActivateCount;
                    if (Time.getInstance().getTime() > endTime
                            && endTime != 0) {
                        newActivateCount = (endTime - startTime) / increment;

                    } else {
                        newActivateCount = (Time.getInstance().getTime()
                                - startTime) / increment;
                    }
                    if (duration == -1 || (activateCount < maxActivateCount
                            && activateCount < newActivateCount)) {
                        while (activateCount < newActivateCount) {
                            activateCount++;
                            if (checkResistance()) {
                                active = true;
                                effect.activate();
                            } else {
                                if (repeatType.equals("untilResist")) {
                                    owner.removeEffect(effect);
                                    setDelete();
                                }
                                active = false;
                                if (owner instanceof Player) {
                                    printDescription(
                                            playerResistRepeatEffectDescription);

                                } else {
                                    printDescription(
                                            resistRepeatEffectDescription);
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

        };
        Time.getInstance().addTimeListener(timeListener);

    }

    /**
     * Builds an Effect from the given XML element and returns it.
     * 
     * @param element
     *            the element to build an effect out of
     * @return the built effect
     */
    public static Effect buildEffect(Element element) {

        int baseAccuracy;
        if (element.attributeValue("baseAccuracy") != null) {
            baseAccuracy = Integer
                    .parseInt(element.attributeValue("baseAccuracy"));
        } else {
            baseAccuracy = 0;
        }

        int specialAccuracyBonus;

        if (element.attributeValue("specialAccuracyBonus") != null) {
            specialAccuracyBonus = Integer
                    .parseInt(element.attributeValue("specialAccuracyBonus"));
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

        ArrayList<String> specialEffectTriggers = new ArrayList<String>();
        String tmp = element.attributeValue("specialEffectTriggers");
        if (tmp != null && !tmp.equals("")) {
            for (String string : tmp.split("\\|")) {
                specialEffectTriggers.add(string);
            }
        }

        ArrayList<String> selfDestructTriggers = new ArrayList<String>();
        tmp = element.attributeValue("selfDestructTriggers");
        if (tmp != null && !tmp.equals("")) {
            for (String string : tmp.split("\\|")) {
                selfDestructTriggers.add(string);
            }
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

        Effect effect = new Effect(effects, element.attributeValue("id"),
                element.attributeValue("name"), duration, increment,
                baseAccuracy, element.attributeValue("resistType"),
                element.attributeValue("repeatType"),
                Boolean.parseBoolean(element.attributeValue("toSelf")),
                element.attributeValue("playerEffectDescription"),
                element.attributeValue("playerRepeatEffectDescription"),
                element.attributeValue("effectDescription"),
                element.attributeValue("repeatEffectDescription"),
                element.attributeValue("resistEffectDescription"),
                element.attributeValue("playerResistEffectDescription"),
                element.attributeValue("resistRepeatEffectDescription"),
                element.attributeValue("repeatResistRepeatEffectDescription"),
                selfDestructTriggers,
                element.attributeValue("selfDestructDescription"),
                element.attributeValue("playerSelfDestructDescription"),
                specialEffectTriggers,
                element.attributeValue("specialEffectDescription"),
                element.attributeValue("playerSpecialEffectDescription"),
                element.attributeValue("specialResistEffectDescription"),
                element.attributeValue("playerSpecialResistEffectDescription"),
                element.attributeValue("specialResistType"),
                specialAccuracyBonus);

        switch (element.attributeValue("type")) {

        case "increaseStat":
            return new StatIncreaseEffect(effect,
                    element.attributeValue("stat"),
                    Integer.parseInt(element.attributeValue("magnitude")));
        case "heal":
            return new HealEffect(effect,
                    Integer.parseInt(element.attributeValue("magnitude")));
        case "damage":
            int critChance = 0;
            if (Game.isNumeric(element.attributeValue("critChance"))) {
                critChance = Integer
                        .parseInt(element.attributeValue("critChance"));
            }
            int base = 0;
            if (Game.isNumeric(element.attributeValue("critChance"))) {
                base = Integer.parseInt(element.attributeValue("critChance"));
            }
            double critMult = 0;
            if (Game.isDoubleNumeric(element.attributeValue("critMult"))) {
                critMult = Double
                        .parseDouble(element.attributeValue("critMult"));
            }
            double scaling = 0;
            if (Game.isDoubleNumeric(element.attributeValue("scaling"))) {
                scaling = Double.parseDouble(element.attributeValue("scaling"));
            }
            double percentRoll = 0;
            if (Game.isDoubleNumeric(element.attributeValue("percentRoll"))) {
                percentRoll = Double
                        .parseDouble(element.attributeValue("percentRoll"));
            }
            return new DamageEffect(effect,
                    element.attributeValue("damageType"), base,
                    element.attributeValue("stat"), scaling, critChance,
                    critMult, percentRoll);
        default:

            return null;

        }
    }

    /**
     * Returns the effects increment.
     * 
     * @return the effects increment
     */
    public int getIncrement() {
        return increment;
    }

    /**
     * Returns the effects duration.
     * 
     * @return the effects duration
     */
    public int getDuration() {
        return duration;
    }

    /**
     * Returns true if the effect always applies to the user.
     * 
     * @return if the effect always applies to the user
     */
    public boolean isToSelf() {
        return toSelf;
    }

    /**
     * Returns the effects effects ArrayList.
     * 
     * @return the list of effects
     */
    public List<Effect> getEffects() {
        return effects;
    }

    /**
     * Returns the effects repeat type.
     * 
     * @return the the effects repeatType
     */
    public String getRepeatType() {
        return repeatType;
    }

    /**
     * Returns the effects name.
     * 
     * @return the name of the effect
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the effects end time.
     * 
     * @return the endTime
     */
    public int getEndTime() {
        return endTime;
    }

    /**
     * Sets the effects end time.
     * 
     * @param endTime
     *            the endTime to set
     */
    public void setEndTime(int endTime) {
        this.endTime = endTime;
    }

    public String getId() {
        return id;
    }

    @Override
    public String toString() {
        return "LongEffect [startTime=" + startTime + ", endTime=" + endTime
                + ", increment=" + increment + ", activateCount="
                + activateCount + ", playerEffectDescription="
                + playerRepeatEffectDescription + "]";
    }

    /**
     * Returns a description of the effect.
     * 
     * @return a description of the effect as a string
     */
    public String getInfo() {
        return null;

    }
}
