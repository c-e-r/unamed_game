/**
 * 
 */
package unamedGame.entities;

import java.awt.Point;
import java.util.ArrayList;
import java.util.List;
import unamedGame.Game;
import unamedGame.effects.Effect;
import unamedGame.input.InputEvent;
import unamedGame.input.InputObserver;
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
public class Player extends Entity {

	private static Player instance = null;

	public static Player getInstance() {
		if (instance == null) {
			instance = new Player();

		}
		return instance;
	}

	private Point location;

	private Player() {
		vitality = 3;
		strength = 3;
		dexterity = 3;
		intellect = 3;
		spirit = 3;
		luck = 3;

		calculateDerivedStats();

		currentHealth = maxHealth;
		currentStamina = maxStamina;
		currentMana = maxMana;
		location = new Point(2, 2);
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

		innateWeaponDamageType = "bludgeoning";
		innateWeaponHitDescription = "#userName# throws a punch at you dealing #damage# damage";
		innateWeaponMissDescription = "#userName# throws a punch at you but you avoid the attack";
		innatePlayerWeaponHitDescription = "You throw a punch at #targetName# dealing #damage# damage";
		innatePlayerWeaponMissDescription = "You throw a punch at #targetName# but miss because your bad";

		reloadSkills();

	}

	/**
	 * Moves the player in the given direction passing time and displaying a
	 * confimation of direction moved
	 * 
	 * @param direction
	 */
	public void move(int direction) {
		Point tempLocation;
		tempLocation = CubePoint
				.cubePointToPoint(CubePoint.getMoveNeighbor(CubePoint.pointToCubePoint(location), direction));
		if (World.getInstance().locationExists(tempLocation)) {
			location = tempLocation;
			Time.getInstance().passTime(480);
		} else {
			direction = -1;
		}
		switch (direction) {
		case -1:
			Window.appendToPane(Window.getInstance().getTextPane(), "You can't go there.");
			break;
		case 0:
			Window.appendToPane(Window.getInstance().getTextPane(), "You travel northwest.");
			break;
		case 1:
			Window.appendToPane(Window.getInstance().getTextPane(), "You travel north.");
			break;
		case 2:
			Window.appendToPane(Window.getInstance().getTextPane(), "You travel southeast.");
			break;
		case 3:
			Window.appendToPane(Window.getInstance().getTextPane(), "You travel northeast.");
			break;
		case 4:
			Window.appendToPane(Window.getInstance().getTextPane(), "You travel south.");
			break;
		case 5:
			Window.appendToPane(Window.getInstance().getTextPane(), "You travel southwest.");
			break;

		default:
			break;
		}

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
	 * Sets the players current location to the x y coordinates of the point given
	 * 
	 * @param location
	 *            the location to set the player to
	 */
	public void setLocation(Point location) {
		this.location = location;
	}

	/**
	 * Prints the description for using the item depending on the user and target
	 * and calls the items use method
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
				Window.addToPane(Window.getInstance().getTextPane(), user.getUseName());
				break;
			case "userNameCapital":
				Window.addToPane(Window.getInstance().getTextPane(), Game.capitalizeFirstLetter(user.getUseName()));
				break;
			case "targetName":
				Window.addToPane(Window.getInstance().getTextPane(), this.getUseName());
				break;
			case "targetNameCapital":
				Window.addToPane(Window.getInstance().getTextPane(), Game.capitalizeFirstLetter(this.getUseName()));
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

				Window.appendToPane(Window.getInstance().getTextPane(), "You equipped your " + item.getName());
			} else {
				equipment[EquipmentIndex.HEAD.getValue()].setEquipped(false);
				removeEquipEffects(equipment[EquipmentIndex.HEAD.getValue()].getEquipEffects());
				Window.addToPane(Window.getInstance().getTextPane(),
						"You removed your " + equipment[EquipmentIndex.HEAD.getValue()].getName());
				equipment[EquipmentIndex.HEAD.getValue()] = item;
				item.setEquipped(true);
				addEquipEffects(item.getEquipEffects());
				Window.appendToPane(Window.getInstance().getTextPane(), " and equipped your " + item.getName());
			}
			Game.inventory();

			break;
		case "body":
			if (equipment[EquipmentIndex.BODY.getValue()] == null) {
				equipment[EquipmentIndex.BODY.getValue()] = item;
				item.setEquipped(true);
				addEquipEffects(item.getEquipEffects());

				Window.appendToPane(Window.getInstance().getTextPane(), "You equipped your " + item.getName());
			} else {
				equipment[EquipmentIndex.BODY.getValue()].setEquipped(false);
				removeEquipEffects(equipment[EquipmentIndex.BODY.getValue()].getEquipEffects());

				Window.addToPane(Window.getInstance().getTextPane(),
						"You removed your " + equipment[EquipmentIndex.BODY.getValue()].getName());
				equipment[EquipmentIndex.BODY.getValue()] = item;
				item.setEquipped(true);
				addEquipEffects(item.getEquipEffects());

				Window.appendToPane(Window.getInstance().getTextPane(), " and equipped your " + item.getName());
			}
			Game.inventory();

			break;
		case "hand":
			getHandChoice(item);
			break;
		case "held":
			getHeldChoice(item);
			break;
		case "feet":
			if (equipment[EquipmentIndex.FEET.getValue()] == null) {
				equipment[EquipmentIndex.FEET.getValue()] = item;
				item.setEquipped(true);
				addEquipEffects(item.getEquipEffects());

				Window.appendToPane(Window.getInstance().getTextPane(), "You equipped your " + item.getName());
			} else {
				equipment[EquipmentIndex.FEET.getValue()].setEquipped(false);
				removeEquipEffects(equipment[EquipmentIndex.FEET.getValue()].getEquipEffects());

				Window.addToPane(Window.getInstance().getTextPane(),
						"You removed your " + equipment[EquipmentIndex.FEET.getValue()].getName());
				equipment[EquipmentIndex.FEET.getValue()] = item;
				item.setEquipped(true);
				addEquipEffects(item.getEquipEffects());

				Window.appendToPane(Window.getInstance().getTextPane(), " and equipped your " + item.getName());
			}
			Game.inventory();

			break;
		case "neck":
			if (equipment[EquipmentIndex.NECK.getValue()] == null) {
				equipment[EquipmentIndex.NECK.getValue()] = item;
				item.setEquipped(true);
				addEquipEffects(item.getEquipEffects());

				Window.appendToPane(Window.getInstance().getTextPane(), "You equipped your " + item.getName());
			} else {
				equipment[EquipmentIndex.NECK.getValue()].setEquipped(false);
				removeEquipEffects(equipment[EquipmentIndex.NECK.getValue()].getEquipEffects());

				Window.addToPane(Window.getInstance().getTextPane(),
						"You removed your " + equipment[EquipmentIndex.NECK.getValue()].getName());
				equipment[EquipmentIndex.NECK.getValue()] = item;
				item.setEquipped(true);
				addEquipEffects(item.getEquipEffects());

				Window.appendToPane(Window.getInstance().getTextPane(), " and equipped your " + item.getName());
			}
			Game.inventory();

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
				Player.getInstance().removeEquipEffects(toRemove.getEquipEffects());

				toRemove.setEquipped(false);
				Window.appendToPane(Window.getInstance().getTextPane(), "You unequipped your " + toRemove.getName());
				reloadSkills();

			}
			;

		}

	}

	/**
	 * Prompts the player to select which hand to equip the given item in and equips it
	 * @param toEquip the item to equip
	 */
	public void getHandChoice(Item toEquip) {
		Window.clearPane(Window.getInstance().getSidePane());
		Window.addToPane(Window.getInstance().getSidePane(), "0: Back\n1: Left");
		if (equipment[EquipmentIndex.LEFT_HAND.getValue()] != null) {
			Window.addToPane(Window.getInstance().getSidePane(),
					" - " + equipment[EquipmentIndex.LEFT_HAND.getValue()].getName());
		}
		Window.addToPane(Window.getInstance().getSidePane(), "\n2: Right");
		if (equipment[EquipmentIndex.RIGHT_HAND.getValue()] != null) {
			Window.addToPane(Window.getInstance().getSidePane(),
					" - " + equipment[EquipmentIndex.RIGHT_HAND.getValue()].getName());
		}
		Window.appendToPane(Window.getInstance().getTextPane(), "Equip to which hand?");
		Window.getInstance().addInputObsever(new InputObserver() {
			@Override
			public void inputChanged(InputEvent evt) {
				if (Game.isNumeric(evt.getText())) {
					int hand = Integer.parseInt(evt.getText());
					if (hand == 0) {
						Window.getInstance().removeInputObsever(this);
						Game.itemChoiceMenu(inventory.indexOf(toEquip));
					} else if (hand == 1) {
						if (equipment[EquipmentIndex.LEFT_HAND.getValue()] == null) {
							equipment[EquipmentIndex.LEFT_HAND.getValue()] = toEquip;
							toEquip.setEquipped(true);
							Player.getInstance().addEquipEffects(toEquip.getEquipEffects());

							Window.appendToPane(Window.getInstance().getTextPane(),
									"You equipped your " + toEquip.getName());
						} else {
							equipment[EquipmentIndex.LEFT_HAND.getValue()].setEquipped(false);
							Player.getInstance().removeEquipEffects(
									equipment[EquipmentIndex.RIGHT_HAND.getValue()].getEquipEffects());

							Window.addToPane(Window.getInstance().getTextPane(),
									"You removed your " + equipment[EquipmentIndex.LEFT_HAND.getValue()].getName());
							equipment[EquipmentIndex.LEFT_HAND.getValue()] = toEquip;
							toEquip.setEquipped(true);
							Player.getInstance().addEquipEffects(toEquip.getEquipEffects());

							Window.appendToPane(Window.getInstance().getTextPane(),
									" and equipped your " + toEquip.getName());
						}
						Window.getInstance().removeInputObsever(this);
						Game.inventory();

					} else if (hand == 2) {
						if (equipment[EquipmentIndex.RIGHT_HAND.getValue()] == null) {
							equipment[EquipmentIndex.RIGHT_HAND.getValue()] = toEquip;
							toEquip.setEquipped(true);
							Player.getInstance().addEquipEffects(toEquip.getEquipEffects());

							Window.appendToPane(Window.getInstance().getTextPane(),
									"You equipped your " + toEquip.getName());
						} else {
							equipment[EquipmentIndex.RIGHT_HAND.getValue()].setEquipped(false);
							Player.getInstance().removeEquipEffects(
									equipment[EquipmentIndex.RIGHT_HAND.getValue()].getEquipEffects());

							Window.addToPane(Window.getInstance().getTextPane(),
									"You removed your " + equipment[EquipmentIndex.RIGHT_HAND.getValue()].getName());
							equipment[EquipmentIndex.RIGHT_HAND.getValue()] = toEquip;
							toEquip.setEquipped(true);
							Player.getInstance().addEquipEffects(toEquip.getEquipEffects());

							Window.appendToPane(Window.getInstance().getTextPane(),
									" and equipped your " + toEquip.getName());

						}
						Window.getInstance().removeInputObsever(this);
						Game.inventory();
					}
				}
			}
		});

	}

	/**
	 * Prompts the player to select which hand to hold the given item in and equips it
	 * @param toEquip the item to equip
	 */
	public void getHeldChoice(Item toEquip) {
		Window.clearPane(Window.getInstance().getSidePane());
		Window.addToPane(Window.getInstance().getSidePane(), "0: Back\n1: Left");
		if (equipment[EquipmentIndex.LEFT_HELD.getValue()] != null) {
			Window.addToPane(Window.getInstance().getSidePane(),
					" - " + equipment[EquipmentIndex.LEFT_HELD.getValue()].getName());
		}
		Window.addToPane(Window.getInstance().getSidePane(), "\n2: Right");
		if (equipment[EquipmentIndex.RIGHT_HELD.getValue()] != null) {
			Window.addToPane(Window.getInstance().getSidePane(),
					" - " + equipment[EquipmentIndex.RIGHT_HELD.getValue()].getName());
		}
		Window.appendToPane(Window.getInstance().getTextPane(), "Hold in which hand?");
		Window.getInstance().addInputObsever(new InputObserver() {
			@Override
			public void inputChanged(InputEvent evt) {
				if (Game.isNumeric(evt.getText())) {
					int hand = Integer.parseInt(evt.getText());
					if (hand == 0) {
						Window.getInstance().removeInputObsever(this);
						Game.itemChoiceMenu(inventory.indexOf(toEquip));
					} else if (hand == 1) {
						if (equipment[EquipmentIndex.LEFT_HELD.getValue()] == null) {
							equipment[EquipmentIndex.LEFT_HELD.getValue()] = toEquip;
							toEquip.setEquipped(true);
							Player.getInstance().addEquipEffects(toEquip.getEquipEffects());
							Window.appendToPane(Window.getInstance().getTextPane(),
									"You equipped your " + toEquip.getName());
						} else {
							equipment[EquipmentIndex.LEFT_HELD.getValue()].setEquipped(false);
							Player.getInstance().removeEquipEffects(
									equipment[EquipmentIndex.LEFT_HELD.getValue()].getEquipEffects());
							Window.addToPane(Window.getInstance().getTextPane(),
									"You removed your " + equipment[EquipmentIndex.LEFT_HELD.getValue()].getName());
							equipment[EquipmentIndex.LEFT_HELD.getValue()] = toEquip;
							toEquip.setEquipped(true);
							Player.getInstance().addEquipEffects(toEquip.getEquipEffects());

							Window.appendToPane(Window.getInstance().getTextPane(),
									" and equipped your " + toEquip.getName() + " in your left hand");
						}
						Window.getInstance().removeInputObsever(this);
						Game.inventory();

					} else if (hand == 2) {
						if (equipment[EquipmentIndex.RIGHT_HELD.getValue()] == null) {
							equipment[EquipmentIndex.RIGHT_HELD.getValue()] = toEquip;
							toEquip.setEquipped(true);
							Player.getInstance().addEquipEffects(toEquip.getEquipEffects());

							Window.appendToPane(Window.getInstance().getTextPane(),
									"You equipped your " + toEquip.getName() + " in your right hand");
						} else {
							equipment[EquipmentIndex.RIGHT_HELD.getValue()].setEquipped(false);
							Player.getInstance().removeEquipEffects(
									equipment[EquipmentIndex.RIGHT_HELD.getValue()].getEquipEffects());
							Window.addToPane(Window.getInstance().getTextPane(),
									"You removed your " + equipment[EquipmentIndex.RIGHT_HELD.getValue()].getName());
							equipment[EquipmentIndex.RIGHT_HELD.getValue()] = toEquip;
							toEquip.setEquipped(true);
							Player.getInstance().addEquipEffects(toEquip.getEquipEffects());

							Window.appendToPane(Window.getInstance().getTextPane(),
									" and equipped your " + toEquip.getName());

						}
						Window.getInstance().removeInputObsever(this);
						Game.inventory();
					}
				}

			}
		});

	}

	/**
	 * Add all equip effects from the given list to the equipmentEffects List
	 * @param list the List of effects to add
	 */
	public void addEquipEffects(List<Effect> list) {
		equipmentEffects.addAll(list);
		recalculateStats();
	}

	/**
	 * Remove all equip effects that are in the given list from the equipmentEffects List
	 * @param list the List of effects to remove
	 */
	public void removeEquipEffects(List<Effect> list) {
		equipmentEffects.removeAll(list);
		recalculateStats();
	}

}
