/**
 * 
 */
package unamedGame.entities;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import unamedGame.Game;
import unamedGame.effects.*;
import unamedGame.items.Item;
import unamedGame.skills.Skill;
import unamedGame.spells.Spell;
import unamedGame.ui.Window;

/**
 * @author c-e-r
 *
 */
public class Enemy extends Entity {

	private String description;
	private String deathDescription;
	private String killDescription;
	private int attackChance;
	private int skillChance;
	private int spellChance;
	private int itemChance;
	private int buffChance;
	private int debuffChance;
	private int offensiveChance;
	private int healChance;

	/**
	 * Creates an enemy from an xml with the given filename
	 * 
	 * @param fileName
	 */
	public Enemy(String fileName) {
		itemSkills = new ArrayList<Skill>();
		equipment = new Item[7];
		inventory = new ArrayList<Item>();
		innateSkills = new ArrayList<Skill>();
		combinedSkills = new ArrayList<Skill>();
		permanantEffects = new ArrayList<Effect>();
		equipmentEffects = new ArrayList<Effect>();
		spells = new ArrayList<Spell>();
		knownSpells = new ArrayList<Spell>();
		itemSpells = new ArrayList<Spell>();
		effects = new ArrayList<Effect>();

		loadEnemyFromXML(fileName);
		calculateDerivedStats();
		reloadSkills();
		calculateChances();
		currentHealth = maxHealth;
	}

	/**
	 * Returns the description to be displayed when the Enemy is killed
	 * 
	 * @return the deathDescription
	 */
	public String getDeathDescription() {
		return deathDescription;
	}

	/**
	 * Returns the description to displayed when the Enemy kills the player
	 * 
	 * @return the killDescription
	 */
	public String getKillDescription() {
		return killDescription;

	}

	/**
	 * Returns the description of the enemy
	 * 
	 * @return the description
	 */
	public String getDescription() {
		return description;
	}

	public void calculateChances() {
		spellChance += attackChance;
		skillChance += spellChance;
		itemChance += skillChance;
		debuffChance += offensiveChance;
		buffChance += debuffChance;
		healChance += buffChance;
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
	 * Loads the enemy xml file to build the enemy from
	 * 
	 * @param fileName
	 *            the name of the enemy xml file
	 */
	public void loadEnemyFromXML(String fileName) {
		SAXReader reader = new SAXReader();
		try {
			File inputFile = new File("data/enemies/" + fileName + ".xml");
			Document document = reader.read(inputFile);

			Element root = document.getRootElement();
			Iterator<Element> iterator = root.elementIterator();
			parseEnemyXML(iterator);

		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Parses the enemy xml
	 * 
	 * @param iterator
	 *            the iterator for the enemy xml file
	 */
	private void parseEnemyXML(Iterator<Element> iterator) {
		while (iterator.hasNext()) {
			Element element = iterator.next();
			switch (element.getName()) {
			case "name":
				name = element.getText();
				break;
			case "useName":
				useName = element.getText();
				break;
			case "vitality":
				if (Game.isNumeric(element.getText())) {
					vitality = Integer.parseInt(element.getText());
				}
				break;
			case "strength":
				if (Game.isNumeric(element.getText())) {
					strength = Integer.parseInt(element.getText());
				}
				break;
			case "dexterity":
				if (Game.isNumeric(element.getText())) {
					dexterity = Integer.parseInt(element.getText());
				}
				break;
			case "intellect":
				if (Game.isNumeric(element.getText())) {
					intellect = Integer.parseInt(element.getText());
				}
				break;
			case "spirit":
				if (Game.isNumeric(element.getText())) {
					spirit = Integer.parseInt(element.getText());
				}
				break;
			case "luck":
				if (Game.isNumeric(element.getText())) {
					luck = Integer.parseInt(element.getText());
				}
				break;
			case "attackChance":
				if (Game.isNumeric(element.getText())) {
					attackChance = Integer.parseInt(element.getText());
				}
				break;
			case "spellChance":
				if (Game.isNumeric(element.getText())) {
					spellChance = Integer.parseInt(element.getText());
				}
				break;
			case "itemChance":
				if (Game.isNumeric(element.getText())) {
					itemChance = Integer.parseInt(element.getText());
				}
				break;
			case "skillChance":
				if (Game.isNumeric(element.getText())) {
					skillChance = Integer.parseInt(element.getText());
				}
				break;
			case "buffChance":
				if (Game.isNumeric(element.getText())) {
					buffChance = Integer.parseInt(element.getText());
				}
				break;
			case "debuffChance":
				if (Game.isNumeric(element.getText())) {
					debuffChance = Integer.parseInt(element.getText());
				}
				break;
			case "offensiveChance":
				if (Game.isNumeric(element.getText())) {
					offensiveChance = Integer.parseInt(element.getText());
				}
				break;
			case "healChance":
				if (Game.isNumeric(element.getText())) {
					healChance = Integer.parseInt(element.getText());
				}
				break;
			case "piercingReduction":
				if (Game.isNumeric(element.getText())) {
					piercingReduction = Integer.parseInt(element.getText());
				}
				break;
			case "slashingReduction":
				if (Game.isNumeric(element.getText())) {
					slashingReduction = Integer.parseInt(element.getText());
				}
				break;
			case "bludgeoningReduction":
				if (Game.isNumeric(element.getText())) {
					bludgeoningReduction = Integer.parseInt(element.getText());
				}
				break;
			case "fireReduction":
				if (Game.isNumeric(element.getText())) {
					fireReduction = Integer.parseInt(element.getText());
				}
				break;
			case "coldReduction":
				if (Game.isNumeric(element.getText())) {
					coldReduction = Integer.parseInt(element.getText());
				}
				break;
			case "electricityReduction":
				if (Game.isNumeric(element.getText())) {
					electricityReduction = Integer.parseInt(element.getText());
				}
				break;
			case "sacredReduction":
				if (Game.isNumeric(element.getText())) {
					sacredReduction = Integer.parseInt(element.getText());
				}
				break;
			case "profaneReduction":
				if (Game.isNumeric(element.getText())) {
					profaneReduction = Integer.parseInt(element.getText());
				}
				break;
			case "poisonReduction":
				if (Game.isNumeric(element.getText())) {
					poisonReduction = Integer.parseInt(element.getText());
				}
				break;
			case "piercingResistance":
				if (Game.isDoubleNumeric(element.getText())) {
					piercingResistance = Double.parseDouble(element.getText());
				}
				break;
			case "slashingResistance":
				if (Game.isDoubleNumeric(element.getText())) {
					slashingResistance = Double.parseDouble(element.getText());
				}
				break;
			case "bludgeoningResistance":
				if (Game.isDoubleNumeric(element.getText())) {
					bludgeoningResistance = Double.parseDouble(element.getText());
				}
				break;
			case "fireResistance":
				if (Game.isDoubleNumeric(element.getText())) {
					fireResistance = Double.parseDouble(element.getText());
				}
				break;
			case "coldResistance":
				if (Game.isDoubleNumeric(element.getText())) {
					coldResistance = Double.parseDouble(element.getText());
				}
				break;
			case "electricityResistance":
				if (Game.isDoubleNumeric(element.getText())) {
					electricityResistance = Double.parseDouble(element.getText());
				}
				break;
			case "sacredResistance":
				if (Game.isDoubleNumeric(element.getText())) {
					sacredResistance = Double.parseDouble(element.getText());
				}
				break;
			case "profaneReistance":
				if (Game.isDoubleNumeric(element.getText())) {
					profaneResistance = Double.parseDouble(element.getText());
				}
				break;
			case "poisonResistance":
				if (Game.isDoubleNumeric(element.getText())) {
					poisonResistance = Double.parseDouble(element.getText());
				}
				break;
			case "description":
				description = element.getText();
				break;
			case "deathDescription":
				deathDescription = element.getText();
				break;
			case "killDescription":
				killDescription = element.getText();
				break;
			case "innateSkill":
				addInnateSkill(new Skill(element.getText()));
				break;
			case "knownSpell":
				addKnownSpell(new Spell(element.getText()));
				break;
			case "innateWeaponDamage":
				if (Game.isNumeric(element.getText())) {
					innateWeaponDamage = Integer.parseInt(element.getText());
				}
				break;
			case "innateWeaponVariableDamage":
				if (Game.isNumeric(element.getText())) {
					innateWeaponVariableDamage = Integer.parseInt(element.getText());
				}
				break;
			case "innateWeaponHitChance":
				if (Game.isNumeric(element.getText())) {
					innateWeaponHitChance = Integer.parseInt(element.getText());
				}
				break;
			case "innateWeaponSpeed":
				if (Game.isNumeric(element.getText())) {
					innateWeaponSpeed = Integer.parseInt(element.getText());
				}
				break;
			case "innateWeaponDamageType":
				innateWeaponDamageType = element.getText();
				break;
			case "innateWeaponHitDescription":
				innateWeaponHitDescription = element.getText();
				break;
			case "innateWeaponMissDescription":
				innateWeaponMissDescription = element.getText();
				break;
			case "innatePlayerWeaponHitDescription":
				innatePlayerWeaponHitDescription = element.getText();
				break;
			case "innatePlayerWeaponMissDescription":
				innatePlayerWeaponMissDescription = element.getText();
				break;
			case "item":

				addItemToInventory(new Item(element.getText()));
				if (element.attributeValue("equip").equals("true")) {

					int itemIndex = inventory.size() - 1;
					Item item = inventory.get(itemIndex);
					if (item.getEquipSlot().equals("hand")) {
						if (equipment[EquipmentIndex.LEFT_HAND.getValue()] == null) {
							equipHandItem(item, "left");
						} else if (equipment[EquipmentIndex.RIGHT_HAND.getValue()] == null) {
							equipHandItem(item, "right");
						}
					} else if (item.getEquipSlot().equals("held")) {
						if (equipment[EquipmentIndex.LEFT_HELD.getValue()] == null) {
							equipHeldItem(item, "left");

						} else if (equipment[EquipmentIndex.RIGHT_HELD.getValue()] == null) {
							equipHeldItem(item, "right");
						}

					} else {
						equipInventoryItem(itemIndex);
					}
				}
				reloadSkills();
				break;
			default:
				System.out.println("Error unrecognized element name: " + element.getName());
				break;
			}
		}

	}

	/**
	 * Equips the inventory item
	 * 
	 * @param itemIndex
	 *            the index of the item to equip
	 */
	public void equipInventoryItem(int itemIndex) {
		Item item = getInventoryItem(itemIndex);
		switch (item.getEquipSlot()) {
		case "head":
			if (equipment[EquipmentIndex.HEAD.getValue()] == null) {
				equipment[EquipmentIndex.HEAD.getValue()] = item;
				item.setEquipped(true);
			} else {
				equipment[EquipmentIndex.HEAD.getValue()].setEquipped(false);
				equipment[EquipmentIndex.HEAD.getValue()] = item;
				item.setEquipped(true);
			}
			Game.inventory();

			break;
		case "body":
			if (equipment[EquipmentIndex.BODY.getValue()] == null) {
				equipment[EquipmentIndex.BODY.getValue()] = item;
				item.setEquipped(true);
			} else {
				equipment[EquipmentIndex.BODY.getValue()].setEquipped(false);
				equipment[EquipmentIndex.BODY.getValue()] = item;
				item.setEquipped(true);
			}
			Game.inventory();

			break;
		case "feet":
			if (equipment[EquipmentIndex.FEET.getValue()] == null) {
				equipment[EquipmentIndex.FEET.getValue()] = item;
				item.setEquipped(true);
			} else {
				equipment[EquipmentIndex.FEET.getValue()].setEquipped(false);
				equipment[EquipmentIndex.FEET.getValue()] = item;
				item.setEquipped(true);
			}
			Game.inventory();

			break;
		case "neck":
			if (equipment[EquipmentIndex.FEET.getValue()] == null) {
				equipment[EquipmentIndex.FEET.getValue()] = item;
				item.setEquipped(true);
			} else {
				equipment[EquipmentIndex.FEET.getValue()].setEquipped(false);
				equipment[EquipmentIndex.FEET.getValue()] = item;
				item.setEquipped(true);
			}
			Game.inventory();

			break;

		default:
			break;
		}

	}

	/**
	 * Equips an item to a hand slot
	 * 
	 * @param toEquip
	 *            the item to equip
	 * @param hand
	 *            the hand to equip the item in as a string
	 */
	public void equipHandItem(Item toEquip, String hand) {
		if (hand.equals("left")) {
			if (equipment[EquipmentIndex.LEFT_HAND.getValue()] == null) {
				equipment[EquipmentIndex.LEFT_HAND.getValue()] = toEquip;
				toEquip.setEquipped(true);
			} else {
				equipment[EquipmentIndex.LEFT_HAND.getValue()].setEquipped(false);
				equipment[EquipmentIndex.LEFT_HAND.getValue()] = toEquip;
				toEquip.setEquipped(true);
			}
		} else {
			if (equipment[EquipmentIndex.RIGHT_HAND.getValue()] == null) {
				equipment[EquipmentIndex.RIGHT_HAND.getValue()] = toEquip;
				toEquip.setEquipped(true);
			} else {
				equipment[EquipmentIndex.RIGHT_HAND.getValue()].setEquipped(false);
				equipment[EquipmentIndex.RIGHT_HAND.getValue()] = toEquip;
				toEquip.setEquipped(true);

			}
		}
	}

	/**
	 * Equips an item to a held slot
	 * 
	 * @param toEquip
	 *            the item to equip
	 * @param hand
	 *            the hand to equip the item in as a string
	 */
	public void equipHeldItem(Item toEquip, String hand) {
		if (hand.equals("left")) {
			if (equipment[EquipmentIndex.LEFT_HELD.getValue()] == null) {
				equipment[EquipmentIndex.LEFT_HELD.getValue()] = toEquip;
				toEquip.setEquipped(true);
			} else {
				equipment[EquipmentIndex.LEFT_HELD.getValue()].setEquipped(false);
				equipment[EquipmentIndex.LEFT_HELD.getValue()] = toEquip;
				toEquip.setEquipped(true);
			}
		} else {
			if (equipment[EquipmentIndex.RIGHT_HELD.getValue()] == null) {
				equipment[EquipmentIndex.RIGHT_HELD.getValue()] = toEquip;
				toEquip.setEquipped(true);
			} else {
				equipment[EquipmentIndex.RIGHT_HELD.getValue()].setEquipped(false);
				equipment[EquipmentIndex.RIGHT_HELD.getValue()] = toEquip;
				toEquip.setEquipped(true);

			}
		}
	}

	/**
	 * Unequips the item associated with the index given if it is equipped
	 * 
	 * @param itemIndex
	 *            the index of the item to unequip
	 */
	public void unequipInventoryItem(int itemIndex) {
		Item toRemove = getInventoryItem(itemIndex);
		for (int i = 0; i < equipment.length; i++) {
			if (toRemove == equipment[i]) {
				equipment[i] = null;
				toRemove.setEquipped(false);
			}
			;

		}

	}

	public List<Skill> getSkillsOfType(String type) {
		List<Skill> temp = new ArrayList<Skill>();
		for (Skill skill : combinedSkills) {
			if (skill.getSkillType().equals(type)) {
				temp.add(skill);
			}
		}
		return temp;
	}

	public List<Spell> getSpellsOfType(String type) {
		List<Spell> temp = new ArrayList<Spell>();
		for (Spell spell : spells) {
			if (spell.getSpellType().equals(type)) {
				temp.add(spell);
			}
		}
		return temp;
	}

	public List<Item> getItemsOfType(String type) {
		List<Item> temp = new ArrayList<Item>();
		for (Item item : inventory) {
			if (item.getItemType().equals(type)) {
				temp.add(item);
			}
		}
		return temp;
	}

	/**
	 * @return the attackChance
	 */
	public int getAttackChance() {
		return attackChance;
	}

	/**
	 * @return the skillChance
	 */
	public int getSkillChance() {
		return skillChance;
	}

	/**
	 * @return the spellChance
	 */
	public int getSpellChance() {
		return spellChance;
	}

	/**
	 * @return the itemChance
	 */
	public int getItemChance() {
		return itemChance;
	}

	/**
	 * @return the buffChance
	 */
	public int getBuffChance() {
		return buffChance;
	}

	/**
	 * @return the debuffChance
	 */
	public int getDebuffChance() {
		return debuffChance;
	}

	/**
	 * @return the offensiveChance
	 */
	public int getOffensiveChance() {
		return offensiveChance;
	}

	/**
	 * @return the healChance
	 */
	public int getHealChance() {
		return healChance;
	}

}
