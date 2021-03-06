/**
 * 
 */
package unamedgame.items;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Element;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;

import unamedgame.Game;
import unamedgame.effects.Effect;
import unamedgame.entities.Entity;
import unamedgame.entities.Player;
import unamedgame.skills.Skill;
import unamedgame.spells.Spell;
import unamedgame.ui.Window;

/**
 * A class to store item information.
 * 
 * @author c-e-r
 * @version 0.0.1
 */
public class Item implements Serializable {

    private static final long serialVersionUID = -3840552122199961294L;

    private static final Logger LOG = LogManager.getLogger(Game.class);

    protected List<Skill> equipSkills;
    protected List<Skill> skills;
    protected List<Spell> spells;
    protected List<Spell> equipSpells;

    private String id;
    private String name;
    private double weight;
    private List<Effect> effects;
    private List<Effect> permanantEffects;
    private List<Effect> equipEffects;
    private List<Effect> attackEffects;
    private List<Effect> spellEffects;

    private int uses;
    private int maxUses;
    private boolean battleUse;
    private boolean fieldUse;
    private boolean offensive;
    private String description;
    private String useText;
    private String useOnText;
    private String playerUseText;
    private String playerUseOnText;
    private String attackHitDescription;
    private String attackMissDescription;
    private String playerAttackHitDescription;
    private String playerAttackMissDescription;
    private boolean equippable;
    private String equipSlot;
    private boolean equipped;
    private String damageType;
    private String itemType;

    private boolean spellFocus;
    private int spellFocusHitChance;
    private int spellFocusSpeed;

    private int weaponHitChance;
    private int weaponBaseDamage;
    private int weaponVariableDamage;
    private int weaponSpeed;
    private int weaponCritChance;
    private int weaponCritMult;

    private int piercingReduction;
    private int slashingReduction;
    private int bludgeoningReduction;
    private int fireReduction;
    private int coldReduction;
    private int electricityReduction;
    private int sacredReduction;
    private int profaneReduction;
    private int poisonReduction;

    private int strRequirement;

    private int speedPenalty;

    private int value;
    private boolean bound;
    private boolean perishable;

    /**
     * Builds an Item from the given filename.
     * 
     * @param itemName
     *            the filename of the item to create
     * @throws DocumentException
     *             if the item file cannot be found
     */
    private Item(String itemName) throws DocumentException {
        permanantEffects = new ArrayList<Effect>();
        equipEffects = new ArrayList<Effect>();
        attackEffects = new ArrayList<Effect>();
        effects = new ArrayList<Effect>();
        equipSkills = new ArrayList<Skill>();
        spells = new ArrayList<Spell>();
        skills = new ArrayList<Skill>();
        equipSpells = new ArrayList<Spell>();
        loadItemFromXML(itemName);

        if (uses == 0 && maxUses != 0) {
            uses = maxUses;
        }

    }

    /**
     * Creates an item from an xml element.
     * 
     * @param element
     *            the xml element to create an item from
     */
    public Item(Element element) {
        permanantEffects = new ArrayList<Effect>();
        equipEffects = new ArrayList<Effect>();
        attackEffects = new ArrayList<Effect>();
        effects = new ArrayList<Effect>();
        equipSkills = new ArrayList<Skill>();
        spells = new ArrayList<Spell>();
        skills = new ArrayList<Skill>();
        equipSpells = new ArrayList<Spell>();

        Iterator<Element> iterator = element.elementIterator();
        parseItemXML(iterator);

    }

    /**
     * Builds and returns an Item object.
     * 
     * @param itemName
     *            the itemName to build
     * @return the item that was built
     */
    public static Item buildItem(String itemName) {
        Item temp;
        try {
            temp = new Item(itemName);
            return temp;

        } catch (DocumentException e) {
            LOG.error("Error building item from xml file", e);
            e.printStackTrace();
            return null;

        }
    }

    /**
     * Loads the xml and calls the method to parse it.
     * 
     * @param itemName
     *            the item to load
     * @throws DocumentException
     *             if the file cannot be found or is incorrectly formatted
     */
    private void loadItemFromXML(String itemName) throws DocumentException {
        SAXReader reader = new SAXReader();
        id = itemName;
        File inputFile = new File("data/items/" + itemName + ".xml");
        Document document = reader.read(inputFile);

        Element root = document.getRootElement();
        Iterator<Element> iterator = root.elementIterator();
        parseItemXML(iterator);
    }

    /**
     * Parses the item xml file setting variables based on the element names and
     * attributes.
     * 
     * @param iterator
     *            the element iterator to iterate through
     */
    private void parseItemXML(Iterator<Element> iterator) {
        while (iterator.hasNext()) {
            Element element = iterator.next();
            switch (element.getName()) {
            case "name":
                name = element.getText();
                break;
            case "weight":
                if (Game.isNumeric(element.getText())) {
                    weight = Integer.parseInt(element.getText());
                }
                break;
            case "uses":
                if (Game.isNumeric(element.getText())) {
                    uses = Integer.parseInt(element.getText());
                }
                break;
            case "maxUses":
                if (Game.isNumeric(element.getText())) {
                    maxUses = Integer.parseInt(element.getText());
                }
                break;
            case "description":
                description = element.getText();
                break;
            case "useText":
                useText = element.getText();
                break;
            case "useOnText":
                useOnText = element.getText();
                break;
            case "playerUseText":
                playerUseText = element.getText();
                break;
            case "playerUseOnText":
                playerUseOnText = element.getText();
                break;
            case "attackHitDescription":
                attackHitDescription = element.getText();
                break;
            case "attackMissDescription":
                attackMissDescription = element.getText();
                break;
            case "playerAttackHitDescription":
                playerAttackHitDescription = element.getText();
                break;
            case "playerAttackMissDescription":
                playerAttackMissDescription = element.getText();
                break;
            case "battleUse":
                battleUse = Boolean.parseBoolean(element.getText());
                break;
            case "fieldUse":
                fieldUse = Boolean.parseBoolean(element.getText());
                break;
            case "offensive":
                offensive = Boolean.parseBoolean(element.getText());
                break;
            case "equippable":
                equippable = Boolean.parseBoolean(element.getText());
                equipSlot = element.attributeValue("equipSlot");
                break;
            case "attackEffect":
                addAttackEffect(Effect.buildEffect(element));
                break;
            case "spellEffect":
                addSpellEffect(Effect.buildEffect(element));
                break;
            case "effect":

                addEffect(Effect.buildEffect(element));
                break;
            case "permanantEffect":

                addPermanantEffect(Effect.buildEffect(element));
                break;
            case "equipEffect":

                addEquipEffect(Effect.buildEffect(element));
                break;
            case "weaponHitChance":
                if (Game.isNumeric(element.getText())) {
                    weaponHitChance = Integer.parseInt(element.getText());
                }
                break;
            case "weaponBaseDamage":
                if (Game.isNumeric(element.getText())) {
                    weaponBaseDamage = Integer.parseInt(element.getText());
                }
                break;
            case "weaponVariableDamage":
                if (Game.isNumeric(element.getText())) {
                    weaponVariableDamage = Integer.parseInt(element.getText());
                }
                break;
            case "weaponSpeed":
                if (Game.isNumeric(element.getText())) {
                    weaponSpeed = Integer.parseInt(element.getText());
                }
                break;
            case "skill":
                Skill newSkill = Skill.buildSkill(element.getText());
                if (newSkill != null) {
                    addSkill(newSkill);
                } else {
                    Window.appendText(
                            "ERROR: Somthing went wrong while creating a skill. See game.log for more information.\n");
                }
                break;
            case "spell":
                Spell newSpell = Spell.buildSpell(element.getText());
                if (newSpell != null) {
                    addSpell(newSpell);
                } else {
                    Window.appendText(
                            "ERROR: Somthing went wrong while creating a spell. See game.log for more information.\n");
                }
                break;
            case "equipSkill":
                Skill newEquipSkill = Skill.buildSkill(element.getText());
                if (newEquipSkill != null) {
                    addEquipSkill(newEquipSkill);
                } else {
                    Window.appendText(
                            "ERROR: Somthing went wrong while creating a skill. See game.log for more information.\n");
                }
                break;
            case "equipSpell":
                Spell newEquipSpell = Spell.buildSpell(element.getText());
                if (newEquipSpell != null) {
                    addEquipSpell(newEquipSpell);
                } else {
                    Window.appendText(
                            "ERROR: Somthing went wrong while creating a spell. See game.log for more information.\n");
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
            case "speedPenalty":
                if (Game.isNumeric(element.getText())) {
                    speedPenalty = Integer.parseInt(element.getText());
                }
                break;
            case "strRequirement":
                if (Game.isNumeric(element.getText())) {
                    strRequirement = Integer.parseInt(element.getText());
                }
                break;
            case "damageType":
                damageType = element.getText();
                break;
            case "itemType":
                itemType = element.getText();
                break;
            case "value":
                if (Game.isNumeric(element.getText())) {
                    value = Integer.parseInt(element.getText());
                }
                break;
            case "bound":
                bound = Boolean.parseBoolean(element.getText());
            case "perishable":
                perishable = Boolean.parseBoolean(element.getText());
            default:
                LOG.error("Error unrecognized element name: "
                        + element.getName());
                break;
            }
        }

    }

    /**
     * Adds a Skill to the items equipSkills ArrayList.
     * 
     * @param skill
     *            the skill to add
     */
    public void addEquipSkill(Skill skill) {
        equipSkills.add(skill);
    }

    /**
     * Adds a Spell to the items spell's ArrayList.
     * 
     * @param spell
     *            the spell to add
     */
    public void addSpell(Spell spell) {
        spells.add(spell);
    }

    /**
     * Adds a Spell to the items equipSpells ArrayList.
     * 
     * @param spell
     *            the spell to add
     */
    public void addEquipSpell(Spell spell) {
        equipSpells.add(spell);
    }

    /**
     * Adds a skill to the items skill's ArrayList.
     * 
     * @param skill
     *            the skill to add
     */
    public void addSkill(Skill skill) {
        skills.add(skill);
    }

    /**
     * Adds an Effect to the items attackEffects ArrayList.
     * 
     * @param effect
     *            the effect to add
     */
    public void addAttackEffect(Effect effect) {
        attackEffects.add(effect);
    }

    /**
     * Adds an Effect to the items spellEffects ArrayList.
     * 
     * @param effect
     *            the effect to add
     */
    public void addSpellEffect(Effect effect) {
        spellEffects.add(effect);
    }

    /**
     * Adds an Effect to the items effects ArrayList.
     * 
     * @param effect
     *            the effect to add
     */
    public void addEffect(Effect effect) {
        effects.add(effect);
    }

    /**
     * Adds an Effect to the items equipEffects ArrayList.
     * 
     * @param effect
     *            the effect to add
     */
    private void addEquipEffect(Effect effect) {
        equipEffects.add(effect);
    }

    /**
     * Adds an Effect to the items permanantEffects ArrayList.
     * 
     * @param effect
     *            the effect to add
     */
    private void addPermanantEffect(Effect effect) {
        permanantEffects.add(effect);
    }

    /**
     * Uses an item adding its effects and permanent effects to the target
     * 
     * @param user
     *            the user of the item
     * @param target
     *            the target of the item
     */
    public void use(Entity user, Entity target) {

        if (effects.size() != 0) {
            for (Effect effect : effects) {
                if (effect != null) {
                    if (effect.isToSelf()) {
                        user.addEffect(effect, user);
                    } else {
                        target.addEffect(effect, user);
                    }
                }
            }
        }
        if (permanantEffects.size() != 0) {
            for (Effect permanantEffect : permanantEffects) {
                Player.getInstance().addPermanantEffect(permanantEffect);
            }
        }
        if (spells.size() != 0) {
            for (Spell spell : spells) {
                Player.getInstance().addKnownSpell(spell);
            }
        }
        if (skills.size() != 0) {
            for (Skill skill : skills) {
                Player.getInstance().addInnateSkill(skill);
            }
        }
        uses--;
        if (uses <= 0 && maxUses > 0 && user instanceof Player) {
            if (user instanceof Player) {
                Window.appendText(Game.capitalizeFirstLetter(getName())
                        + " has run out of uses\n");
            }
            if (perishable) {
                user.removeItemFromInventory(this);
            }
        }
    }

    public void removeUse() {
        if (uses > 0) {
            uses--;
        }
    }

    /**
     * Prints the inventory info string of the item.
     * 
     * @return the item info string
     */
    public String getItemInfo() {
        String usesString = Integer.toString(uses);
        String maxUsesString = Integer.toString(maxUses);
        if (maxUses == 0) {
            usesString = "-";
            maxUsesString = "-";
        }
        if (this.isEquipped()) {
            return String.format("%-17%5ds%5.1f%10s/%-6s", "[e] " + name, value,
                    weight, usesString, maxUsesString);

        }
        return String.format("%-17s%5d%5.1f%10s/%-6s", name, value, weight,
                usesString, maxUsesString);
    }

    /**
     * Returns the name of the item as a string.
     * 
     * @return the name of the item as a string
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the weight of the item as a double.
     * 
     * @return the weight of the item as a double
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Returns the number of remaining uses of the item.
     * 
     * @return the number of remaining uses of the item
     */
    public int getUses() {
        return uses;
    }

    /**
     * Returns the number of remaining uses of the item.
     * 
     * @return the number of remaining uses of the item
     */
    public int getMaxUses() {
        return maxUses;
    }

    /**
     * Returns the description of the item.
     * 
     * @return the description of the item
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns true if the item can be used in battle.
     * 
     * @return if the item can be used in battle
     */
    public boolean isBattleUse() {
        return battleUse;
    }

    /**
     * Returns the text to displayed when a non player entity uses the item on
     * itself.
     * 
     * @return the useText of the item
     */
    public String getUseText() {
        return useText;
    }

    /**
     * Returns true if the item is used against the opponent in battle instead
     * of on the used.
     * 
     * @return if the item is used offensively in battle
     */
    public boolean isOffensive() {
        return offensive;
    }

    /**
     * Returns true if the item can be used in the field.
     * 
     * @return if the item can be used in the field
     */
    public boolean isFieldUse() {
        return fieldUse;
    }

    /**
     * Returns the text to be displayed when a non player unity uses the item on
     * another entity.
     * 
     * @return the useOnText of the item
     */
    public String getUseOnText() {
        return useOnText;
    }

    /**
     * Returns the text to be displayed when a player uses the item on another
     * entity.
     * 
     * @return the playerUseOnText of the item
     */
    public String getPlayerUseOnText() {
        return playerUseOnText;
    }

    /**
     * Returns the variable weapon damage of the item.
     * 
     * @return the variable weapon damage of the item
     */
    public int getWeaponVariableDamage() {
        return weaponVariableDamage;
    }

    /**
     * Returns the items effects ArrayList.
     * 
     * @return the items effects ArrayList
     */
    public List<Effect> getEffects() {
        return effects;
    }

    /**
     * Returns the items permanantEffects ArrayList.
     * 
     * @return the items permanantEffects ArrayList
     */
    public List<Effect> getPermanantEffects() {
        return permanantEffects;
    }

    /**
     * Returns the items equipEffects ArrayList.
     * 
     * @return the items equipEffects ArrayList
     */
    public List<Effect> getEquipEffects() {
        return equipEffects;
    }

    /**
     * Returns the weapons hit chance modifier.
     * 
     * @return the weapons hit chance modifier
     */
    public int getWeaponHitChance() {
        return weaponHitChance;
    }

    /**
     * Returns the weapons damage modifier.
     * 
     * @return the weapons damage modifier
     */
    public int getWeaponBaseDamage() {
        return weaponBaseDamage;
    }

    /**
     * Returns the weapons speed modifier.
     * 
     * @return the weapons speed modifier
     */
    public int getWeaponSpeed() {
        return weaponSpeed;
    }

    /**
     * Returns the text to be displayed when the player uses the item on another
     * entity.
     * 
     * @return the items playerUseText
     */
    public String getPlayerUseText() {
        return playerUseText;
    }

    /**
     * Returns true if the item can be equipped.
     * 
     * @return if the item can be equipped
     */
    public boolean isEquippable() {
        return equippable;
    }

    /**
     * Returns the slot the item can be equipped in.
     * 
     * @return the slot the item can be equipped in
     */
    public String getEquipSlot() {
        return equipSlot;
    }

    /**
     * Returns true if the item is currently equipped by an Entity.
     * 
     * @return if the item is currently equipped by an entity
     */
    public boolean isEquipped() {
        return equipped;
    }

    /**
     * Sets if the weapon is equipped or not.
     * 
     * @param equipped
     *            value to set equipped to
     */
    public void setEquipped(boolean equipped) {
        this.equipped = equipped;
    }

    /**
     * Returns the list of skills granted by having the item equipped.
     * 
     * @return the items equipSkills ArrayList
     */
    public List<Skill> getEquipSkills() {
        return equipSkills;
    }

    /**
     * Returns the list of skills granted by using the item.
     * 
     * @return the items skills ArrayList
     */
    public List<Skill> getSkills() {
        return skills;
    }

    /**
     * Returns the list of spells granted by having the item equipped.
     * 
     * @return the items equipSpells ArrayList
     */
    public List<Spell> getEquipSpells() {
        return equipSpells;
    }

    /**
     * Returns the list of spells granted by using the item.
     * 
     * @return the items skills ArrayList
     */
    public List<Spell> getSpells() {
        return spells;
    }

    /**
     * Returns the list of effects imparted when an attack made with the item.
     * hits
     * 
     * @return the items attackEffects ArrayList
     */
    public List<Effect> getAttackEffects() {
        return attackEffects;
    }

    /**
     * Returns the list of effects imparted when a spell attack made with the.
     * item hits
     * 
     * @return the items attackEffects ArrayList
     */
    public List<Effect> getSpellEffects() {
        return spellEffects;
    }

    /**
     * Returns the damage type that the item deals with regular attacks.
     * 
     * @return the damageType of the item
     */
    public String getDamageType() {
        return damageType;
    }

    /**
     * Returns the text to be displayed when an entity hits with the item.
     * 
     * @return the items attackHitDescription
     */
    public String getAttackHitDescription() {
        return attackHitDescription;
    }

    /**
     * Returns the text to be displayed when an entity misses with the item.
     * 
     * @return the items attackMissDescription
     */
    public String getAttackMissDescription() {
        return attackMissDescription;
    }

    /**
     * Returns the text to be displayed when the player hits with the item.
     * 
     * @return the items playerAttackHitDescription
     */
    public String getPlayerAttackHitDescription() {
        return playerAttackHitDescription;
    }

    /**
     * Returns the text to be displayed when the player misses with the item.
     * 
     * @return the items playerAttackHitDescription
     */
    public String getPlayerAttackMissDescription() {
        return playerAttackMissDescription;
    }

    /**
     * Returns the piercing reduction.
     * 
     * @return the piercingReduction
     */
    public int getPiercingReduction() {
        return piercingReduction;
    }

    /**
     * Returns the slashing reduction.
     * 
     * @return the slashingReduction
     */
    public int getSlashingReduction() {
        return slashingReduction;
    }

    /**
     * Returns the bludgeoning reduction.
     * 
     * @return the bludgeoningReduction
     */
    public int getBludgeoningReduction() {
        return bludgeoningReduction;
    }

    /**
     * Returns the fire reduction.
     * 
     * @return the fireReduction
     */
    public int getFireReduction() {
        return fireReduction;
    }

    /**
     * Returns the cold reduction.
     * 
     * @return the coldReduction
     */
    public int getColdReduction() {
        return coldReduction;
    }

    /**
     * Returns the electricity reduction.
     * 
     * @return the electricityReduction
     */
    public int getElectricityReduction() {
        return electricityReduction;
    }

    /**
     * Returns the sacred reduction.
     * 
     * @return the sacredReduction
     */
    public int getSacredReduction() {
        return sacredReduction;
    }

    /**
     * Returns the profane reduction.
     * 
     * @return the profaneReduction
     */
    public int getProfaneReduction() {
        return profaneReduction;
    }

    /**
     * Returns the poison reduction.
     * 
     * @return the poisonReduction
     */
    public int getPoisonReduction() {
        return poisonReduction;
    }

    /**
     * Returns if its a spell focus.
     * 
     * @return the spellFocus
     */
    public boolean isSpellFocus() {
        return spellFocus;
    }

    /**
     * Returns the spell focus hit chance.
     * 
     * @return the spellFocusHitChance
     */
    public int getSpellFocusHitChance() {
        return spellFocusHitChance;
    }

    /**
     * Returns the spell focus speed bonus.
     * 
     * @return the spellFocusSpeed
     */
    public int getSpellFocusSpeed() {
        return spellFocusSpeed;
    }

    /**
     * Returns the speed penalty.
     * 
     * @return the speedPenalty
     */
    public int getSpeedPenalty() {
        return speedPenalty;
    }

    /**
     * Returns the strength requirement.
     * 
     * @return the strRequirement
     */
    public int getStrRequirement() {
        return strRequirement;
    }

    /**
     * Returns the item type.
     * 
     * @return the itemType
     */
    public String getItemType() {
        return itemType;
    }

    public int getValue() {
        return value * (uses / maxUses);
    }

    public String getId() {
        return id;
    }

    public boolean isBound() {
        return bound;
    }

    public int getWeaponCritChance() {
        return weaponCritChance;
    }
    public int getWeaponCritMult() {
        return weaponCritMult;
    }

    /*
     * (non-Javadoc)
     * 
     * @see java.lang.Object#toString()
     */
    @Override
    public String toString() {
        return "Item [name=" + name + "]";
    }

}
