/**
 * 
 */
package unamedgame.entities;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import unamedgame.Game;
import unamedgame.effects.Effect;
import unamedgame.items.Item;
import unamedgame.skills.Skill;
import unamedgame.spells.Spell;
import unamedgame.ui.Window;

/**
 * A class to act as an enemy entity.
 * 
 * @author c-e-r
 * @version 0.0.1
 */
public final class Enemy extends Entity {

    private static final long serialVersionUID = -6371773878242485513L;

    private static final Logger LOG = LogManager.getLogger(Game.class);

    private static final int EQUIP_LIMIT = 7;

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
    private int expValue;

    /**
     * Creates an enemy from an xml with the given filename.
     * 
     * @param fileName
     *            the filename of the enemy to be created
     * @throws DocumentException
     *             if file is not found or invalid
     */
    private Enemy(String fileName) throws DocumentException {
        itemSkills = new ArrayList<Skill>();
        equipment = new Item[EQUIP_LIMIT];
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
     * Builds and returns an enemy if it can be created.
     * 
     * @param enemyName
     *            the filename to create an enemy from
     * @return the enemy or null if the enemy could not be created
     */
    public static Enemy buildEnemy(String enemyName) {
        Enemy temp;
        try {
            temp = new Enemy(enemyName);
            return temp;

        } catch (DocumentException e) {
            LOG.error("Error building enemy from xml file", e);
            e.printStackTrace();
            return null;

        }
    }

    /**
     * Returns the description to be displayed when the Enemy is killed.
     * 
     * @return the deathDescription
     */
    public String getDeathDescription() {
        return deathDescription;
    }

    /**
     * Returns the description to displayed when the Enemy kills the player.
     * 
     * @return the killDescription
     */
    public String getKillDescription() {
        return killDescription;

    }

    /**
     * Returns the description of the enemy.
     * 
     * @return the description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Calculates the enemies chance to do various actions.
     */
    private void calculateChances() {
        spellChance += attackChance;
        skillChance += spellChance;
        itemChance += skillChance;
        debuffChance += offensiveChance;
        buffChance += debuffChance;
        healChance += buffChance;
    }

    /**
     * Prints the description for using the item depending on the user and
     * target and calls the items use method.
     * 
     * @param item
     *            the item to apply
     * @param user
     *            the character who used the item
     * @return item the item that had its effect applied
     */
    public Item applyItemEffects(Item item, Entity user) {
        String[] itemDescription;
        if (user instanceof Player) {
            if (user == this) {
                itemDescription = item.getPlayerUseText().split("#");

            } else {
                itemDescription = item.getPlayerUseOnText().split("#");

            }
        } else {
            if (user == this) {
                itemDescription = item.getUseText().split("#");

            } else {
                itemDescription = item.getUseOnText().split("#");

            }
        }

        for (String string : itemDescription) {
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
     * Loads the enemy xml file to build the enemy from.
     * 
     * @param fileName
     *            the name of the enemy xml file
     * @throws DocumentException if the file can not be loaded or is invalid
     */
    public void loadEnemyFromXML(String fileName) throws DocumentException {
        SAXReader reader = new SAXReader();

        File inputFile = new File("data/enemies/" + fileName + ".xml");
        Document document = reader.read(inputFile);

        Element root = document.getRootElement();
        Iterator<Element> iterator = root.elementIterator();
        parseEnemyXML(iterator);

    }

    /**
     * Parses the enemy xml.
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
                    bludgeoningResistance = Double
                            .parseDouble(element.getText());
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
                    electricityResistance = Double
                            .parseDouble(element.getText());
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
                Skill newSkill = Skill.buildSkill(element.getText());
                if (newSkill != null) {
                    addInnateSkill(newSkill);
                }
                break;
            case "knownSpell":
                Spell newSpell = Spell.buildSpell(element.getText());
                if (newSpell != null) {
                    addKnownSpell(newSpell);

                } else {
                    Window.appendToPane(Window.getInstance().getTextPane(),
                            "ERROR: Somthing went wrong while creating a spell. See game.log for more information.");
                }
                break;
            case "item":
                Item newItem = Item.buildItem(element.getText());
                if (newItem != null) {
                    addItemToInventory(newItem);
                    if (element.attributeValue("equip").equals("true")) {

                        int itemIndex = inventory.size() - 1;
                        Item item = inventory.get(itemIndex);
                        if (item.getEquipSlot().equals("hand")) {
                            if (equipment[EquipmentIndex.LEFT_HAND
                                    .getValue()] == null) {
                                equipHandItem(item, "left");
                            } else if (equipment[EquipmentIndex.RIGHT_HAND
                                    .getValue()] == null) {
                                equipHandItem(item, "right");
                            }
                        } else if (item.getEquipSlot().equals("held")) {
                            if (equipment[EquipmentIndex.LEFT_HELD
                                    .getValue()] == null) {
                                equipHeldItem(item, "left");

                            } else if (equipment[EquipmentIndex.RIGHT_HELD
                                    .getValue()] == null) {
                                equipHeldItem(item, "right");
                            }

                        } else {
                            equipInventoryItem(itemIndex);
                        }
                    }
                } else {
                    Window.appendToPane(Window.getInstance().getTextPane(),
                            "ERROR: Somthing went wrong when adding an item to the enemies inventory. See game.log for more information.");
                }
                reloadSkills();
                break;
            case "expValue":
                if (Game.isNumeric(element.getText())) {
                    expValue = Integer.parseInt(element.getText());
                }
                break;
            case "innateWeapon":
                innateWeapon = new Item(element);
                break;
            default:
                LOG.error("Error unrecognized element name: "
                        + element.getName());
                break;
            }
        }

    }

    /**
     * Equips the inventory item.
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

            break;

        default:
            break;
        }

    }

    /**
     * Equips an item to a hand slot.
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
                equipment[EquipmentIndex.LEFT_HAND.getValue()]
                        .setEquipped(false);
                equipment[EquipmentIndex.LEFT_HAND.getValue()] = toEquip;
                toEquip.setEquipped(true);
            }
        } else {
            if (equipment[EquipmentIndex.RIGHT_HAND.getValue()] == null) {
                equipment[EquipmentIndex.RIGHT_HAND.getValue()] = toEquip;
                toEquip.setEquipped(true);
            } else {
                equipment[EquipmentIndex.RIGHT_HAND.getValue()]
                        .setEquipped(false);
                equipment[EquipmentIndex.RIGHT_HAND.getValue()] = toEquip;
                toEquip.setEquipped(true);

            }
        }
    }

    /**
     * Equips an item to a held slot.
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
                equipment[EquipmentIndex.LEFT_HELD.getValue()]
                        .setEquipped(false);
                equipment[EquipmentIndex.LEFT_HELD.getValue()] = toEquip;
                toEquip.setEquipped(true);
            }
        } else {
            if (equipment[EquipmentIndex.RIGHT_HELD.getValue()] == null) {
                equipment[EquipmentIndex.RIGHT_HELD.getValue()] = toEquip;
                toEquip.setEquipped(true);
            } else {
                equipment[EquipmentIndex.RIGHT_HELD.getValue()]
                        .setEquipped(false);
                equipment[EquipmentIndex.RIGHT_HELD.getValue()] = toEquip;
                toEquip.setEquipped(true);

            }
        }
    }

    /**
     * Unequips the item associated with the index given if it is equipped.
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

        }

    }

    /**
     * Gets a list of the enemies skills for a certain type.
     * @param type the type to get skills for
     * @return the list of skills
     */
    public List<Skill> getSkillsOfType(String type) {
        List<Skill> temp = new ArrayList<Skill>();
        for (Skill skill : combinedSkills) {
            if (skill.getSkillType().equals(type)) {
                temp.add(skill);
            }
        }
        return temp;
    }
    /**
     * Gets a list of the enemies spells for a certain type.
     * @param type the type to get spells for
     * @return the list of spells
     */
    public List<Spell> getSpellsOfType(String type) {
        List<Spell> temp = new ArrayList<Spell>();
        for (Spell spell : spells) {
            if (spell.getSpellType().equals(type)) {
                temp.add(spell);
            }
        }
        return temp;
    }

    /**
     * Gets a list of the enemies items for a certain type.
     * @param type the type to get items for
     * @return the list of items
     */
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
     * Returns the chance for the enemy to attack.
     * @return the attackChance
     */
    public int getAttackChance() {
        return attackChance;
    }

    /**
     * Returns the chance for the enemy to use a skill.
     * @return the skillChance
     */
    public int getSkillChance() {
        return skillChance;
    }

    /**
     * Returns the chance for the enemy to use a spell.
     * @return the spellChance
     */
    public int getSpellChance() {
        return spellChance;
    }

    /**
     * Returns the chance for the enemy to use an item.
     * @return the itemChance
     */
    public int getItemChance() {
        return itemChance;
    }

    /**
     * Returns the chance for an enemy to use a buff skill, spell, or item.
     * @return the buffChance
     */
    public int getBuffChance() {
        return buffChance;
    }

    /**
     * Returns the chance for an enemy to use a debuff skill, spell, or item.
     * @return the debuffChance
     */
    public int getDebuffChance() {
        return debuffChance;
    }

    /**
     * Returns the chance for an enemy to use an offensive skill, spell, or item.
     * @return the offensiveChance
     */
    public int getOffensiveChance() {
        return offensiveChance;
    }

    /**
     * Returns the chance for an enemy to use a healing skill, spell, or item.
     * @return the healChance
     */
    public int getHealChance() {
        return healChance;
    }

    /**
     * Returns the amount of exp awarded when the enemy is defeated.
     * @return the expValue
     */
    public int getExpValue() {
        return expValue;
    }

}
