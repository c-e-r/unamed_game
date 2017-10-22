/**
 * 
 */
package unamedGame.entities;

import java.awt.Point;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import unamedGame.Game;
import unamedGame.effects.Effect;
import unamedGame.items.Item;
import unamedGame.skills.Skill;
import unamedGame.spells.Spell;
import unamedGame.time.Time;
import unamedGame.ui.Window;
import unamedGame.util.CubePoint;
import unamedGame.world.World;

/**
 * @author c-e-r
 *
 */
public class Player extends Entity implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3011027798036421350L;
	private static Player instance = null;

	public static Player getInstance() {
		if (instance == null) {
			instance = new Player();

		}
		return instance;
	}

	public static void setInstance(Player instance) {
		Player.instance = instance;
	}

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

		Item newItem = Item.buildItem("default");
		if (newItem != null) {
			innateWeapon = newItem;
		}

		reloadSkills();

	}

	/**
	 * Moves the player in the given direction passing time and displaying a
	 * confirmation of direction moved
	 * 
	 * @param direction
	 */
	public void move(int direction) {
		if (Game.onMoveMenu) {
			Point tempLocation;
			tempLocation = CubePoint.cubePointToPoint(CubePoint.getMoveNeighbor(
					CubePoint.pointToCubePoint(location), direction));
			if (World.getInstance().locationExists(tempLocation)) {
				location = tempLocation;
				Time.getInstance().passTime(480);
			} else {
				direction = -1;
			}
			switch (direction) {
			case -1:
				Window.appendToPane(Window.getInstance().getTextPane(),
						"You can't go there.");
				break;
			case 0:
				Window.appendToPane(Window.getInstance().getTextPane(),
						"You travel northwest.");
				break;
			case 1:
				Window.appendToPane(Window.getInstance().getTextPane(),
						"You travel north.");
				break;
			case 2:
				Window.appendToPane(Window.getInstance().getTextPane(),
						"You travel northeast.");
				break;
			case 3:
				Window.appendToPane(Window.getInstance().getTextPane(),
						"You travel southeast.");
				break;
			case 4:
				Window.appendToPane(Window.getInstance().getTextPane(),
						"You travel south.");
				break;
			case 5:
				Window.appendToPane(Window.getInstance().getTextPane(),
						"You travel southwest.");
				break;

			default:
				break;
			}
		}
	}

	public void gainExp(int expGain) {

		int expToGain = 0;
		int expRemainder = 0;
		expGain *= expMult;
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

	private void increaseLevel() {
		level++;
		statPoints += 3;
		expToNextLevel += (level * level) * 100;

	}

	/**
	 * Returns a Point with x and y values of the players current location
	 * 
	 * @return
	 */
	public Point getLocation() {
		return location;
	}

	/**
	 * Sets the players current location to the x y coordinates of the point
	 * given
	 * 
	 * @param location
	 *            the location to set the player to
	 */
	public void setLocation(Point location) {
		this.location = location;
	}

	/**
	 * Prints the description for using the item depending on the user and
	 * target and calls the items use method
	 * 
	 * @param item
	 *            the item to apply
	 * @param user
	 *            the character who used the item
	 * @return
	 */
	public Item applyItemEffects(Item item, Entity user) {
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
				Window.addToPane(Window.getInstance().getTextPane(),
						user.getUseName());
				break;
			case "userNameCapital":
				Window.addToPane(Window.getInstance().getTextPane(),
						Game.capitalizeFirstLetter(user.getUseName()));
				break;
			case "targetName":
				Window.addToPane(Window.getInstance().getTextPane(),
						this.getUseName());
				break;
			case "targetNameCapital":
				Window.addToPane(Window.getInstance().getTextPane(),
						Game.capitalizeFirstLetter(this.getUseName()));
				break;
			default:
				Window.addToPane(Window.getInstance().getTextPane(), string);
				break;
			}
		}
		Window.appendToPane(Window.getInstance().getTextPane(), "");

		item.use(user, this);

		return item;
	}

	/**
	 * Equips the item at the item index removing other equipment if needed
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

				Window.appendToPane(Window.getInstance().getTextPane(),
						"You equipped your " + item.getName());
			} else {
				equipment[EquipmentIndex.HEAD.getValue()].setEquipped(false);
				removeEquipEffects(equipment[EquipmentIndex.HEAD.getValue()]
						.getEquipEffects());
				Window.addToPane(Window.getInstance().getTextPane(),
						"You removed your "
								+ equipment[EquipmentIndex.HEAD.getValue()]
										.getName());
				equipment[EquipmentIndex.HEAD.getValue()] = item;
				item.setEquipped(true);
				addEquipEffects(item.getEquipEffects());
				Window.appendToPane(Window.getInstance().getTextPane(),
						" and equipped your " + item.getName());
			}

			break;
		case "body":
			if (equipment[EquipmentIndex.BODY.getValue()] == null) {
				equipment[EquipmentIndex.BODY.getValue()] = item;
				item.setEquipped(true);
				addEquipEffects(item.getEquipEffects());

				Window.appendToPane(Window.getInstance().getTextPane(),
						"You equipped your " + item.getName());
			} else {
				equipment[EquipmentIndex.BODY.getValue()].setEquipped(false);
				removeEquipEffects(equipment[EquipmentIndex.BODY.getValue()]
						.getEquipEffects());

				Window.addToPane(Window.getInstance().getTextPane(),
						"You removed your "
								+ equipment[EquipmentIndex.BODY.getValue()]
										.getName());
				equipment[EquipmentIndex.BODY.getValue()] = item;
				item.setEquipped(true);
				addEquipEffects(item.getEquipEffects());

				Window.appendToPane(Window.getInstance().getTextPane(),
						" and equipped your " + item.getName());
			}

			break;
		case "feet":
			if (equipment[EquipmentIndex.FEET.getValue()] == null) {
				equipment[EquipmentIndex.FEET.getValue()] = item;
				item.setEquipped(true);
				addEquipEffects(item.getEquipEffects());

				Window.appendToPane(Window.getInstance().getTextPane(),
						"You equipped your " + item.getName());
			} else {
				equipment[EquipmentIndex.FEET.getValue()].setEquipped(false);
				removeEquipEffects(equipment[EquipmentIndex.FEET.getValue()]
						.getEquipEffects());

				Window.addToPane(Window.getInstance().getTextPane(),
						"You removed your "
								+ equipment[EquipmentIndex.FEET.getValue()]
										.getName());
				equipment[EquipmentIndex.FEET.getValue()] = item;
				item.setEquipped(true);
				addEquipEffects(item.getEquipEffects());

				Window.appendToPane(Window.getInstance().getTextPane(),
						" and equipped your " + item.getName());
			}

			break;
		case "neck":
			if (equipment[EquipmentIndex.NECK.getValue()] == null) {
				equipment[EquipmentIndex.NECK.getValue()] = item;
				item.setEquipped(true);
				addEquipEffects(item.getEquipEffects());

				Window.appendToPane(Window.getInstance().getTextPane(),
						"You equipped your " + item.getName());
			} else {
				equipment[EquipmentIndex.NECK.getValue()].setEquipped(false);
				removeEquipEffects(equipment[EquipmentIndex.NECK.getValue()]
						.getEquipEffects());

				Window.addToPane(Window.getInstance().getTextPane(),
						"You removed your "
								+ equipment[EquipmentIndex.NECK.getValue()]
										.getName());
				equipment[EquipmentIndex.NECK.getValue()] = item;
				item.setEquipped(true);
				addEquipEffects(item.getEquipEffects());

				Window.appendToPane(Window.getInstance().getTextPane(),
						" and equipped your " + item.getName());
			}

			break;

		default:
			break;
		}

	}

	/**
	 * Unequips the specified item if it is equipped
	 * 
	 * @param itemIndex
	 */
	public void unequipInventoryItem(int itemIndex) {
		Item toRemove = getInventoryItem(itemIndex);
		for (int i = 0; i < equipment.length; i++) {
			if (toRemove == equipment[i]) {
				equipment[i] = null;
				Player.getInstance()
						.removeEquipEffects(toRemove.getEquipEffects());

				toRemove.setEquipped(false);
				Window.appendToPane(Window.getInstance().getTextPane(),
						"You unequipped your " + toRemove.getName());
				reloadSkills();

			}
			;

		}

	}

	public void equipToHand(int hand, Item toEquip) {
		hand = hand == 1 ? EquipmentIndex.LEFT_HAND.getValue()
				: EquipmentIndex.RIGHT_HAND.getValue();
		if (equipment[hand] == null) {
			equipment[hand] = toEquip;
			System.out.println(equipment[hand].getName());
			toEquip.setEquipped(true);
			Player.getInstance().addEquipEffects(toEquip.getEquipEffects());

			Window.appendToPane(Window.getInstance().getTextPane(),
					"You equipped your " + toEquip.getName());
		} else {
			equipment[hand].setEquipped(false);
			Player.getInstance()
					.removeEquipEffects(equipment[hand].getEquipEffects());

			Window.addToPane(Window.getInstance().getTextPane(),
					"You removed your " + equipment[hand].getName());
			equipment[hand] = toEquip;
			toEquip.setEquipped(true);
			Player.getInstance().addEquipEffects(toEquip.getEquipEffects());

			Window.appendToPane(Window.getInstance().getTextPane(),
					" and equipped your " + toEquip.getName());
		}
	}

	public void equipToHeld(int hand, Item toEquip) {
		if (hand != 3) {
			hand = hand == 1 ? EquipmentIndex.LEFT_HELD.getValue()
					: EquipmentIndex.RIGHT_HELD.getValue();
			if (equipment[hand] == null) {
				equipment[hand] = toEquip;
				System.out.println(equipment[hand].getName());
				toEquip.setEquipped(true);
				Player.getInstance().addEquipEffects(toEquip.getEquipEffects());

				Window.appendToPane(Window.getInstance().getTextPane(),
						"You equipped your " + toEquip.getName());
			} else {
				equipment[hand].setEquipped(false);
				Player.getInstance()
						.removeEquipEffects(equipment[hand].getEquipEffects());

				Window.addToPane(Window.getInstance().getTextPane(),
						"You removed your " + equipment[hand].getName());
				equipment[hand] = toEquip;
				toEquip.setEquipped(true);
				Player.getInstance().addEquipEffects(toEquip.getEquipEffects());

				Window.appendToPane(Window.getInstance().getTextPane(),
						" and equipped your " + toEquip.getName());
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
			Window.appendToPane(Window.getInstance().getTextPane(),
					"You equipped your " + toEquip.getName()
							+ " in both hands");
		}
	}

	/**
	 * Add all equip effects from the given list to the equipmentEffects List
	 * 
	 * @param list
	 *            the List of effects to add
	 */
	public void addEquipEffects(List<Effect> list) {
		equipmentEffects.addAll(list);
		recalculateStats();
	}

	/**
	 * Remove all equip effects that are in the given list from the
	 * equipmentEffects List
	 * 
	 * @param list
	 *            the List of effects to remove
	 */
	public void removeEquipEffects(List<Effect> list) {
		equipmentEffects.removeAll(list);
		recalculateStats();
	}

	/**
	 * @return the statPoints
	 */
	public int getStatPoints() {
		return statPoints;
	}

	/**
	 * @param statPoints
	 *            the statPoints to set
	 */
	public void setStatPoints(int statPoints) {
		this.statPoints = statPoints;
	}

	/**
	 * @return the newVitality
	 */
	public int getNewVitality() {
		return newVitality;
	}

	/**
	 * @param newVitality
	 *            the newVitality to set
	 */
	public void setNewVitality(int newVitality) {
		this.newVitality = newVitality;
	}

	/**
	 * @return the newStrength
	 */
	public int getNewStrength() {
		return newStrength;
	}

	/**
	 * @param newStrength
	 *            the newStrength to set
	 */
	public void setNewStrength(int newStrength) {
		this.newStrength = newStrength;
	}

	/**
	 * @return the newDexterity
	 */
	public int getNewDexterity() {
		return newDexterity;
	}

	/**
	 * @param newDexterity
	 *            the newDexterity to set
	 */
	public void setNewDexterity(int newDexterity) {
		this.newDexterity = newDexterity;
	}

	/**
	 * @return the newIntellect
	 */
	public int getNewIntellect() {
		return newIntellect;
	}

	/**
	 * @param newIntellect
	 *            the newIntellect to set
	 */
	public void setNewIntellect(int newIntellect) {
		this.newIntellect = newIntellect;
	}

	/**
	 * @return the newSpirit
	 */
	public int getNewSpirit() {
		return newSpirit;
	}

	/**
	 * @param newSpirit
	 *            the newSpirit to set
	 */
	public void setNewSpirit(int newSpirit) {
		this.newSpirit = newSpirit;
	}

	/**
	 * @return the newLuck
	 */
	public int getNewLuck() {
		return newLuck;
	}

	/**
	 * @param newLuck
	 *            the newLuck to set
	 */
	public void setNewLuck(int newLuck) {
		this.newLuck = newLuck;
	}

	/**
	 * @return the newStatPoints
	 */
	public int getNewStatPoints() {
		return newStatPoints;
	}

	/**
	 * @param newStatPoints
	 *            the newStatPoints to set
	 */
	public void setNewStatPoints(int newStatPoints) {
		this.newStatPoints = newStatPoints;
	}

	/**
	 * @return the level
	 */
	public int getLevel() {
		return level;
	}

}
