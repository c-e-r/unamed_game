/**
 * 
 */
package unamedgame.spells;

import java.io.File;
import java.io.Serializable;
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
import unamedgame.ui.Window;

/**
 * 
 * 
 * @author c-e-r
 *
 */
public class Spell implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private static final Logger LOG = LogManager.getLogger(Game.class);

    private List<Spell> hitSpells;
    private List<Spell> alwaysSpells;
    private List<Spell> missSpells;
    private List<Effect> hitEffects;
    private List<Effect> alwaysEffects;
    private List<Effect> missEffects;

    private String name;
    private String id;
    private boolean isAttack;
    private String spellType;
    private String description;
    private String attackDescription;
    private String playerAttackDescription;
    private String missDescription;
    private String playerMissDescription;

    private int attackHitBonus;
    private int attackSpeedBonus;
    private int manaCost;

    /**
     * Create a spell from an xml file
     * 
     * @param filename
     *            the filename of the spell to create
     * @throws DocumentException
     */
    private Spell(String filename) throws DocumentException {
        loadSpellFromXML(filename);
    }

    public static Spell buildSpell(String spellName) {
        Spell temp;
        try {
            temp = new Spell(spellName);
            return temp;
        } catch (DocumentException e) {
            LOG.error("Error building item from xml file", e);
            e.printStackTrace();
            return null;

        }
    }

    /**
     * Load a spells information from an xml file
     * 
     * @param filename
     * @throws DocumentException
     */
    public void loadSpellFromXML(String filename) throws DocumentException {
        SAXReader reader = new SAXReader();
        id = filename;
        File inputFile = new File("data/spells/" + filename + ".xml");
        Document document = reader.read(inputFile);

        Element root = document.getRootElement();
        buildSpell(root);

    }

    /**
     * Sets a spells variables based on xml data
     * 
     * @param element
     *            the element to retrieve spell data from
     */
    public void buildSpell(Element element) {
        name = element.attributeValue("name");
        description = element.attributeValue("description");
        attackDescription = element.attributeValue("attackDescription");
        playerAttackDescription = element
                .attributeValue("playerAttackDescription");
        missDescription = element.attributeValue("missDescription");
        playerMissDescription = element.attributeValue("playerMissDescription");
        spellType = element.attributeValue("spellType");

        if (element.attributeValue("manaCost") != null) {
            manaCost = Integer.parseInt(element.attributeValue("manaCost"));
        }

        isAttack = Boolean.parseBoolean(element.attributeValue("isAttack"));

        if (element.attributeValue("attackHitBonus") != null) {
            attackHitBonus = Integer
                    .parseInt(element.attributeValue("attackHitBonus"));
        }
        if (element.attributeValue("attackSpeedBonus") != null) {
            attackSpeedBonus = Integer
                    .parseInt(element.attributeValue("attackSpeedBonus"));
        }

        hitEffects = new ArrayList<Effect>();
        missEffects = new ArrayList<Effect>();
        alwaysEffects = new ArrayList<Effect>();

        hitSpells = new ArrayList<Spell>();
        missSpells = new ArrayList<Spell>();
        alwaysSpells = new ArrayList<Spell>();

        Iterator<Element> iterator = element.elementIterator();
        while (iterator.hasNext()) {
            Element temp = iterator.next();
            switch (temp.getName()) {
            case "hitEffect":
                addHitEffect(Effect.buildEffect(temp));
                break;
            case "missEffect":
                addMissEffect(Effect.buildEffect(temp));
                break;
            case "alwaysEffect":
                addAlwaysEffect(Effect.buildEffect(temp));
                break;
            case "hitSpell":
                Spell newHitSpell = Spell.buildSpell(temp.getTextTrim());
                if (newHitSpell != null) {
                    addHitSpell(newHitSpell);
                } else {
                    Window.appendText(
                            "ERROR: Somthing went wrong while creating a spell. See game.log for more information.\n");
                }
                break;
            case "missSpell":
                Spell newMissSpell = Spell.buildSpell(temp.getTextTrim());
                if (newMissSpell != null) {
                    addMissSpell(newMissSpell);
                } else {
                    Window.appendText(
                            "ERROR: Somthing went wrong while creating a spell. See game.log for more information.\n");
                }
                break;
            case "alwaysSpell":
                Spell newAlwaysSpell = Spell.buildSpell(temp.getTextTrim());
                if (newAlwaysSpell != null) {
                    addAlwaysSpell(newAlwaysSpell);
                } else {
                    Window.appendText(
                            "ERROR: Somthing went wrong while creating a spell. See game.log for more information.\n");
                }
                break;
            default:
                break;
            }

        }

    }

    /**
     * Adds a spell to the spell's hitSpell ArrayList
     * 
     * @param spell
     *            the spell's to add to hitSpells
     */
    public void addHitSpell(Spell spell) {
        hitSpells.add(spell);
    }

    /**
     * Adds a spell to the spell's missSpells ArrayList
     * 
     * @param spell
     *            the spell's to add to missSpells
     */
    public void addMissSpell(Spell spell) {
        missSpells.add(spell);
    }

    /**
     * Adds a spell to the spell's alwaysSpells ArrayList
     * 
     * @param spell
     *            the spell's to add to alwaysSpells
     */
    public void addAlwaysSpell(Spell spell) {
        alwaysSpells.add(spell);
    }

    /**
     * Adds an effect to the spell's hitEffects ArrayList
     * 
     * @param effect
     *            the spell's to add to hitEffects
     */
    public void addHitEffect(Effect effect) {
        hitEffects.add(effect);
    }

    /**
     * Adds an effect to the spell's missEffects ArrayList
     * 
     * @param effect
     *            the spell's to add to missEffects
     */
    public void addMissEffect(Effect effect) {
        missEffects.add(effect);
    }

    /**
     * Adds an effect to the spell's alwaysEffects ArrayList
     * 
     * @param effect
     *            the spell's to add to alwaysEffects
     */
    public void addAlwaysEffect(Effect effect) {
        alwaysEffects.add(effect);
    }

    /**
     * Returns true if the spell's should make an attack roll
     * 
     * @return if the spell's is an attack skill
     */
    public boolean isAttack() {
        return isAttack;
    }

    /**
     * Returns a description of the spell as a string
     * 
     * @return the description of the spell
     */
    public String getDescription() {
        return description;
    }

    /**
     * Returns the description to be displayed if the spell is used by a
     * non-player and hits
     * 
     * @return the spell's attackDescription
     */
    public String getAttackDescription() {
        return attackDescription;
    }

    /**
     * Returns the description to be displayed if the spell is used by a player
     * and hits
     * 
     * @return the spell's playerAttackDescription
     */
    public String getPlayerAttackDescription() {
        return playerAttackDescription;
    }

    /**
     * Returns the spell's hit modifier
     * 
     * @return the attackHitBonus
     */
    public int getAttackHitBonus() {
        return attackHitBonus;
    }

    /**
     * Returns the spell's speed bonus
     * 
     * @return the attackSpeedBonus
     */
    public int getAttackSpeedBonus() {
        return attackSpeedBonus;
    }

    /**
     * Returns the spell's name
     * 
     * @return the spell's name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns the description to be displayed if a non-player Entity uses the
     * spell and misses
     * 
     * @return the spell's missDescription
     */
    public String getMissDescription() {
        return missDescription;
    }

    /**
     * Returns the description to be displayed if the player uses the spell and
     * misses
     * 
     * @return the spell's missDescription
     */
    public String getPlayerMissDescription() {
        return playerMissDescription;
    }

    /**
     * Returns the stamina cost of the spell
     * 
     * @return the spell's staminaCost
     */
    public int getManaCost() {
        return manaCost;
    }

    /**
     * Returns the spell's hitEffects ArrayList
     * 
     * @return the spell's hitEffects ArrayList
     */
    public List<Effect> getHitEffects() {
        return hitEffects;
    }

    /**
     * Returns the spell's alwaysEffects ArrayList
     * 
     * @return the spell's alwaysEffects ArrayList
     */
    public List<Effect> getAlwaysEffects() {
        return alwaysEffects;
    }

    /**
     * Returns the spell's missEffects ArrayList
     * 
     * @return the spell's missEffects ArrayList
     */
    public List<Effect> getMissEffects() {
        return missEffects;
    }

    /**
     * Returns the spell's hitSpells ArrayList
     * 
     * @return the spell's hitSpells ArrayList
     */
    public List<Spell> getHitSpells() {
        return hitSpells;
    }

    /**
     * Returns the spell's alwaysSpells ArrayList
     * 
     * @return the spell's alwaysSpells ArrayList
     */
    public List<Spell> getAlwaysSpells() {
        return alwaysSpells;
    }

    /**
     * Returns the spell's alwaysSpells ArrayList
     * 
     * @return the spell's alwaysSpells ArrayList
     */
    public List<Spell> getMissSpells() {
        return missSpells;
    }

    /**
     * @return the spellType
     */
    public String getSpellType() {
        return spellType;
    }
    
    public String getId() {
        return id;
    }

}
