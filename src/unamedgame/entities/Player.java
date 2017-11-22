/**
 * 
 */
package unamedgame.entities;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import unamedgame.Game;
import unamedgame.Quest;
import unamedgame.effects.Effect;
import unamedgame.items.Item;
import unamedgame.skills.Skill;
import unamedgame.spells.Spell;
import unamedgame.time.Time;
import unamedgame.ui.Window;
import unamedgame.util.CubePoint;
import unamedgame.world.World;
import unamedgame.world.WorldTile;

/**
 * A class to act as the player.
 * 
 * @author c-e-r
 * @version 0.0.1
 */
public final class Player extends Entity implements Serializable {

    private static final int BASE_TRAVEL_TIME = 2880;
    private static final long serialVersionUID = -3011027798036421350L;
    private static Player instance;

    /**
     * Returns the player.
     * 
     * @return the player
     */
    public static Player getInstance() {
        if (instance == null) {
            instance = new Player();

        }
        return instance;
    }

    /**
     * Sets the player.
     * 
     * @param instance
     *            the player to set
     */
    public static void setInstance(Player instance) {
        Player.instance = instance;
    }

    private HashMap<String, Quest> questLog;

    private Point location;
    private int level;
    private int exp;
    private int expMult = 1;
    private int expToNextLevel = 100;
    private int statPoints;

    private int newVitality;
    private int newStrength;
    private int newDexterity;
    private int newIntellect;
    private int newSpirit;
    private int newLuck;
    private int newStatPoints;

    private Player() {
        vitality = 3;
        strength = 3;
        dexterity = 3;
        intellect = 3;
        spirit = 3;
        luck = 3;
        expToNextLevel = 100;
        level = 1;

        calculateDerivedStats();

        currentHealth = maxHealth;
        currentStamina = maxStamina;
        currentMana = maxMana;
        location = new Point(4, 4);
        inventory = new ArrayList<Item>();
        flags = new HashMap<String, Integer>();
        permanantEffects = new ArrayList<Effect>();
        equipmentEffects = new ArrayList<Effect>();
        useName = "you";
        innateSkills = new ArrayList<Skill>();
        equipment = new Item[7];
        effects = new ArrayList<Effect>();
        itemSkills = new ArrayList<Skill>();
        combinedSkills = new ArrayList<Skill>();
        spells = new ArrayList<Spell>();
        knownSpells = new ArrayList<Spell>();
        itemSpells = new ArrayList<Spell>();
        questLog = new HashMap<>();

        Item newItem = Item.buildItem("default");
        if (newItem != null) {
            innateWeapon = newItem;
        }

        reloadSkills();

    }

    /**
     * Moves the player in the given direction passing time and displaying a
     * confirmation of direction moved.
     * 
     * @param direction
     *            the direction to move the player
     */
    public void move(int direction) {
        if (Game.isOnMoveMenu()) {
            Point tempLocation;
            int timeToPass = -1;
            tempLocation = CubePoint.cubePointToPoint(CubePoint.getMoveNeighbor(
                    CubePoint.pointToCubePoint(location), direction));
            if (World.getInstance().locationExists(tempLocation)) {
                WorldTile tile = World.getInstance().getTile(tempLocation);
                timeToPass = (int) (BASE_TRAVEL_TIME * tile.getTravelMult());
            }

            if (timeToPass >= 0) {
                location = tempLocation;
                Time.getInstance().passTime(timeToPass);
            } else {
                direction = -1;
            }
            double hoursTraveled = timeToPass / 360;
            int roundedHours = (int) Math.round(hoursTraveled);
            switch (direction) {
            case -1:
                Window.appendText("You can't go there.\n");
                break;
            case 0:
                Window.appendText("You travel northwest for " + roundedHours
                        + " hours.\n");
                break;
            case 1:
                Window.appendText(
                        "You travel north for " + roundedHours + " hours.\n");
                break;
            case 2:
                Window.appendText("You travel northeast for " + roundedHours
                        + " hours.\n");
                break;
            case 3:
                Window.appendText("You travel southeast for " + roundedHours
                        + " hours.\n");
                break;
            case 4:
                Window.appendText(
                        "You travel south for " + roundedHours + " hours.\n");
                break;
            case 5:
                Window.appendText("You travel southwest for " + roundedHours
                        + " hours.\n");
                break;

            default:
                break;
            }
        }
    }

    /**
     * Player gains the specified amount of exp. Leveling up if enough exp is
     * gained.
     * 
     * @param expGain
     *            the exp to gain
     */
    public void gainExp(int expGain) {

        int expToGain = 0;
        int expRemainder = 0;
        expGain *= expMult;
        Window.appendText("You gained " + expGain + " exp.\n");
        while (expGain > 0) {
            if (exp + expGain > expToNextLevel) {
                expRemainder = exp + expGain - expToNextLevel;
                expToGain = expGain - expRemainder;
                expGain = expRemainder;

            } else {
                expToGain = expGain;
                expGain = 0;
            }
            exp += expToGain;
            if (exp >= expToNextLevel) {
                increaseLevel();
            }
        }
    }

    /**
     * Increase the players level and all things that come with that.
     */
    private void increaseLevel() {
        level++;
        statPoints += 3;
        expToNextLevel += (level * level) * 100;

    }

    /**
     * Returns a Point with x and y values of the players current location.
     * 
     * @return the location as a Point
     */
    public Point getLocation() {
        return location;
    }

    /**
     * Sets the players current location to the x y coordinates of the point
     * given.
     * 
     * @param location
     *            the location to set the player to
     */
    public void setLocation(Point location) {
        this.location = location;
    }

    /**
     * Prints the description for using the item depending on the user and
     * target and calls the items use method.
     * 
     * @param item
     *            the item to apply
     * @param user
     *            the character who used the item
     * @return the item used
     */
    public Item applyItemEffects(Item item, Entity user) {
        triggerEffects("item_used_on_before");
        user.triggerEffects("item_used_before");

        String[] description;
        if (user instanceof Player) {
            if (user == this) {
                description = item.getPlayerUseText().split("#");

            } else {
                description = item.getPlayerUseOnText().split("#");

            }
        } else {
            if (user == this) {
                description = item.getUseText().split("#");

            } else {
                description = item.getUseOnText().split("#");

            }
        }

        for (String string : description) {
            switch (string) {
            case "userName":
                Window.appendText(user.getUseName());
                break;
            case "userNameCapital":
                Window.appendText(
                        Game.capitalizeFirstLetter(user.getUseName()));
                break;
            case "targetName":
                Window.appendText(this.getUseName());
                break;
            case "targetNameCapital":
                Window.appendText(
                        Game.capitalizeFirstLetter(this.getUseName()));
                break;
            default:
                Window.appendText(string);
                break;
            }
        }
        Window.appendText("\n");

        item.use(user, this);
        triggerEffects("item_used_on_after");
        user.triggerEffects("item_used_after");

        return item;
    }

    /**
     * Equips the item at the item index removing other equipment if needed.
     * 
     * @param itemIndex
     *            the inventory index of the item to equip
     */
    public void equipInventoryItem(int itemIndex) {
        Item item = getInventoryItem(itemIndex);
        switch (item.getEquipSlot()) {
        case "head":
            if (equipment[EquipmentIndex.HEAD.getValue()] == null) {
                equipment[EquipmentIndex.HEAD.getValue()] = item;
                item.setEquipped(true);
                addEquipEffects(item.getEquipEffects());

                Window.appendText("You equipped your " + item.getName() + "\n");
            } else {
                equipment[EquipmentIndex.HEAD.getValue()].setEquipped(false);
                removeEquipEffects(equipment[EquipmentIndex.HEAD.getValue()]
                        .getEquipEffects());
                Window.appendText("You removed your "
                        + equipment[EquipmentIndex.HEAD.getValue()].getName());
                equipment[EquipmentIndex.HEAD.getValue()] = item;
                item.setEquipped(true);
                addEquipEffects(item.getEquipEffects());
                Window.appendText(
                        " and equipped your " + item.getName() + "\n");
            }

            break;
        case "body":
            if (equipment[EquipmentIndex.BODY.getValue()] == null) {
                equipment[EquipmentIndex.BODY.getValue()] = item;
                item.setEquipped(true);
                addEquipEffects(item.getEquipEffects());

                Window.appendText("You equipped your " + item.getName() + "\n");
            } else {
                equipment[EquipmentIndex.BODY.getValue()].setEquipped(false);
                removeEquipEffects(equipment[EquipmentIndex.BODY.getValue()]
                        .getEquipEffects());

                Window.appendText("You removed your "
                        + equipment[EquipmentIndex.BODY.getValue()].getName());
                equipment[EquipmentIndex.BODY.getValue()] = item;
                item.setEquipped(true);
                addEquipEffects(item.getEquipEffects());

                Window.appendText(
                        " and equipped your " + item.getName() + "\n");
            }

            break;
        case "feet":
            if (equipment[EquipmentIndex.FEET.getValue()] == null) {
                equipment[EquipmentIndex.FEET.getValue()] = item;
                item.setEquipped(true);
                addEquipEffects(item.getEquipEffects());

                Window.appendText("You equipped your " + item.getName() + "\n");
            } else {
                equipment[EquipmentIndex.FEET.getValue()].setEquipped(false);
                removeEquipEffects(equipment[EquipmentIndex.FEET.getValue()]
                        .getEquipEffects());

                Window.appendText("You removed your "
                        + equipment[EquipmentIndex.FEET.getValue()].getName());
                equipment[EquipmentIndex.FEET.getValue()] = item;
                item.setEquipped(true);
                addEquipEffects(item.getEquipEffects());

                Window.appendText(
                        " and equipped your " + item.getName() + "\n");
            }

            break;
        case "neck":
            if (equipment[EquipmentIndex.NECK.getValue()] == null) {
                equipment[EquipmentIndex.NECK.getValue()] = item;
                item.setEquipped(true);
                addEquipEffects(item.getEquipEffects());

                Window.appendText("You equipped your " + item.getName() + "\n");
            } else {
                equipment[EquipmentIndex.NECK.getValue()].setEquipped(false);
                removeEquipEffects(equipment[EquipmentIndex.NECK.getValue()]
                        .getEquipEffects());

                Window.appendText("You removed your "
                        + equipment[EquipmentIndex.NECK.getValue()].getName());
                equipment[EquipmentIndex.NECK.getValue()] = item;
                item.setEquipped(true);
                addEquipEffects(item.getEquipEffects());

                Window.appendText(
                        " and equipped your " + item.getName() + "\n");
            }

            break;

        default:
            break;
        }

    }

    /**
     * Unequips the specified item if it is equipped.
     * 
     * @param itemIndex
     *            the index of the item
     */
    public void unequipInventoryItem(int itemIndex) {
        Item toRemove = getInventoryItem(itemIndex);
        for (int i = 0; i < equipment.length; i++) {
            if (toRemove == equipment[i]) {
                equipment[i] = null;
                Player.getInstance()
                        .removeEquipEffects(toRemove.getEquipEffects());

                toRemove.setEquipped(false);
                Window.appendText(
                        "You unequipped your " + toRemove.getName() + "\n");
                reloadSkills();

            }

        }

    }

    /**
     * Equip the specified item to the specified hand.
     * 
     * @param hand
     *            the hand to equip to
     * @param toEquip
     *            the item to equip
     */
    public void equipToHand(int hand, Item toEquip) {
        hand = hand == 1 ? EquipmentIndex.LEFT_HAND.getValue()
                : EquipmentIndex.RIGHT_HAND.getValue();
        if (equipment[hand] == null) {
            equipment[hand] = toEquip;
            toEquip.setEquipped(true);
            Player.getInstance().addEquipEffects(toEquip.getEquipEffects());

            Window.appendText("You equipped your " + toEquip.getName() + "\n");
        } else {
            equipment[hand].setEquipped(false);
            Player.getInstance()
                    .removeEquipEffects(equipment[hand].getEquipEffects());

            Window.appendText("You removed your " + equipment[hand].getName());
            equipment[hand] = toEquip;
            toEquip.setEquipped(true);
            Player.getInstance().addEquipEffects(toEquip.getEquipEffects());

            Window.appendText(" and equipped your " + toEquip.getName() + "\n");
        }
    }

    /**
     * Hold the specified item in the specified hand.
     * 
     * @param hand
     *            to hold the item in
     * @param toEquip
     *            the item to hold
     */
    public void equipToHeld(int hand, Item toEquip) {
        if (hand != 3) {
            hand = hand == 1 ? EquipmentIndex.LEFT_HELD.getValue()
                    : EquipmentIndex.RIGHT_HELD.getValue();
            if (equipment[hand] == null) {
                equipment[hand] = toEquip;
                toEquip.setEquipped(true);
                Player.getInstance().addEquipEffects(toEquip.getEquipEffects());

                Window.appendText(
                        "You equipped your " + toEquip.getName() + "\n");
            } else {
                equipment[hand].setEquipped(false);
                Player.getInstance()
                        .removeEquipEffects(equipment[hand].getEquipEffects());

                Window.appendText(
                        "You removed your " + equipment[hand].getName());
                equipment[hand] = toEquip;
                toEquip.setEquipped(true);
                Player.getInstance().addEquipEffects(toEquip.getEquipEffects());

                Window.appendText(
                        " and equipped your " + toEquip.getName() + "\n");
            }
        } else {
            if (equipment[EquipmentIndex.RIGHT_HELD.getValue()] != null) {
                equipment[EquipmentIndex.RIGHT_HELD.getValue()]
                        .setEquipped(false);
                Player.getInstance().removeEquipEffects(
                        equipment[EquipmentIndex.RIGHT_HELD.getValue()]
                                .getEquipEffects());
            }
            if (equipment[EquipmentIndex.LEFT_HELD.getValue()] != null) {
                equipment[EquipmentIndex.LEFT_HELD.getValue()]
                        .setEquipped(false);
                Player.getInstance().removeEquipEffects(
                        equipment[EquipmentIndex.LEFT_HELD.getValue()]
                                .getEquipEffects());
            }

            equipment[EquipmentIndex.RIGHT_HELD.getValue()] = toEquip;
            equipment[EquipmentIndex.LEFT_HELD.getValue()] = toEquip;
            toEquip.setEquipped(true);

            Player.getInstance().addEquipEffects(toEquip.getEquipEffects());
            Window.appendText("You equipped your " + toEquip.getName()
                    + " in both hands");
        }
    }

    public void addQuest(String questId, String name) {
        questLog.put(questId, new Quest(name));
    }

    public void updateQuest(String questId, String text) {
        questLog.get(questId).appendText(text);
    }

    public String getCurrencyString() {
        return String.format("%7dŁ", currency);
    }

    /**
     * Returns the players stat points.
     * 
     * @return the statPoints
     */
    public int getStatPoints() {
        return statPoints;
    }

    /**
     * Sets the players stat points.
     * 
     * @param statPoints
     *            the statPoints to set
     */
    public void setStatPoints(int statPoints) {
        this.statPoints = statPoints;
    }

    /**
     * Returns the players newVitality.
     * 
     * @return the newVitality
     */
    public int getNewVitality() {
        return newVitality;
    }

    /**
     * Sets the players newVitality.
     * 
     * @param newVitality
     *            the newVitality to set
     */
    public void setNewVitality(int newVitality) {
        this.newVitality = newVitality;
    }

    /**
     * Retuens the platers newStrength.
     * 
     * @return the newStrength
     */
    public int getNewStrength() {
        return newStrength;
    }

    /**
     * Sets the players newStrength.
     * 
     * @param newStrength
     *            the newStrength to set
     */
    public void setNewStrength(int newStrength) {
        this.newStrength = newStrength;
    }

    /**
     * Returns the players newDexterity.
     * 
     * @return the newDexterity
     */
    public int getNewDexterity() {
        return newDexterity;
    }

    /**
     * Sets the players newDexterity.
     * 
     * @param newDexterity
     *            the newDexterity to set
     */
    public void setNewDexterity(int newDexterity) {
        this.newDexterity = newDexterity;
    }

    /**
     * Returns the players new intellect.
     * 
     * @return the newIntellect
     */
    public int getNewIntellect() {
        return newIntellect;
    }

    /**
     * Sets the players newIntellect.
     * 
     * @param newIntellect
     *            the newIntellect to set
     */
    public void setNewIntellect(int newIntellect) {
        this.newIntellect = newIntellect;
    }

    /**
     * Returns the players newSpirit.
     * 
     * @return the newSpirit
     */
    public int getNewSpirit() {
        return newSpirit;
    }

    /**
     * Sets the players newSpirit.
     * 
     * @param newSpirit
     *            the newSpirit to set
     */
    public void setNewSpirit(int newSpirit) {
        this.newSpirit = newSpirit;
    }

    /**
     * Returns the players newLuck.
     * 
     * @return the newLuck
     */
    public int getNewLuck() {
        return newLuck;
    }

    /**
     * Sets the players newLuck.
     * 
     * @param newLuck
     *            the newLuck to set
     */
    public void setNewLuck(int newLuck) {
        this.newLuck = newLuck;
    }

    /**
     * Returns the players newStatPoints.
     * 
     * @return the newStatPoints
     */
    public int getNewStatPoints() {
        return newStatPoints;
    }

    /**
     * Sets the players newstatPoints.
     * 
     * @param newStatPoints
     *            the newStatPoints to set
     */
    public void setNewStatPoints(int newStatPoints) {
        this.newStatPoints = newStatPoints;
    }

    /**
     * Returns the quest log
     * 
     * @return the questLog
     */
    public HashMap<String, Quest> getQuestLog() {
        return questLog;
    }

    /**
     * Returns the players level.
     * 
     * @return the level
     */
    public int getLevel() {
        return level;
    }

}
