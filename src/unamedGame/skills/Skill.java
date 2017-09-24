/**
 * 
 */
package unamedGame.skills;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.io.SAXReader;

import unamedGame.effects.Effect;

/**
 * 
 * 
 * @author c-e-r
 *
 */
public class Skill {

	private List<Skill> hitSkills;
	private List<Skill> alwaysSkills;
	private List<Skill> missSkills;
	private List<Effect> hitEffects;
	private List<Effect> alwaysEffects;
	private List<Effect> missEffects;

	private String name;
	private boolean isAttack;
	private String skillType;
	private String damageType;
	private String description;
	private String attackDescription;
	private String playerAttackDescription;
	private String missDescription;
	private String playerMissDescription;
	private double attackDamageMult;
	private int attackDamageBonus;
	private int attackVariableDamageBonus;
	private int attackHitBonus;
	private int attackSpeedBonus;
	private int staminaCost;

	/**
	 * Create a skill from an xml file
	 * 
	 * @param filename
	 *            the filename of the skill to create
	 */
	public Skill(String filename) {
		loadSkillFromXML(filename);
	}

	/**
	 * Load a skills information from an xml file
	 * 
	 * @param filename
	 */
	public void loadSkillFromXML(String filename) {
		SAXReader reader = new SAXReader();
		try {
			File inputFile = new File("data/skills/" + filename + ".xml");
			Document document = reader.read(inputFile);

			Element root = document.getRootElement();
			buildSkill(root);
		} catch (DocumentException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/**
	 * Sets a skills variables based on xml data
	 * 
	 * @param element
	 *            the element to retrieve skill data from
	 */
	public void buildSkill(Element element) {
		name = element.attributeValue("name");
		damageType = element.attributeValue("damageType");
		description = element.attributeValue("description");
		attackDescription = element.attributeValue("attackDescription");
		playerAttackDescription = element.attributeValue("playerAttackDescription");
		missDescription = element.attributeValue("missDescription");
		playerMissDescription = element.attributeValue("playerMissDescription");
		skillType = element.attributeValue("skillType");

		if (element.attributeValue("staminaCost") != null) {
			staminaCost = Integer.parseInt(element.attributeValue("staminaCost"));
		}

		isAttack = Boolean.parseBoolean(element.attributeValue("isAttack"));

		if (element.attributeValue("attackDamageMult") != null) {
			attackDamageMult = Double.parseDouble(element.attributeValue("attackDamageMult"));
			if (attackDamageMult == 0)
				attackDamageMult = 1;
		}

		if (element.attributeValue("attackDamageBonus") != null) {
			attackDamageBonus = Integer.parseInt(element.attributeValue("attackDamageBonus"));
		}

		if (element.attributeValue("attackVariableDamageBonus") != null) {
			attackVariableDamageBonus = Integer.parseInt(element.attributeValue("attackVariableDamageBonus"));
		}
		if (element.attributeValue("attackHitBonus") != null) {
			attackHitBonus = Integer.parseInt(element.attributeValue("attackHitBonus"));
		}
		if (element.attributeValue("attackDamageBonus") != null) {
			attackSpeedBonus = Integer.parseInt(element.attributeValue("attackSpeedBonus"));
		}

		hitEffects = new ArrayList<Effect>();
		missEffects = new ArrayList<Effect>();
		alwaysEffects = new ArrayList<Effect>();

		hitSkills = new ArrayList<Skill>();
		missSkills = new ArrayList<Skill>();
		alwaysSkills = new ArrayList<Skill>();

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
			case "hitSkill":
				addHitSkill(new Skill(temp.getTextTrim()));
				break;
			case "missSkill":
				addMissSkill(new Skill(temp.getTextTrim()));
				break;
			case "alwaysSkill":
				addAlwaysSkill(new Skill(temp.getTextTrim()));
				break;
			default:
				break;
			}

		}

	}

	/**
	 * Adds a skill to the skills hitSkills ArrayList
	 * 
	 * @param skill
	 *            the skills to add to hitSkills
	 */
	public void addHitSkill(Skill skill) {
		hitSkills.add(skill);
	}

	/**
	 * Adds a skill to the skills missSkills ArrayList
	 * 
	 * @param skill
	 *            the skills to add to missSkills
	 */
	public void addMissSkill(Skill skill) {
		missSkills.add(skill);
	}

	/**
	 * Adds a skill to the skills alwaysSkills ArrayList
	 * 
	 * @param skill
	 *            the skills to add to alwaysSkills
	 */
	public void addAlwaysSkill(Skill skill) {
		alwaysSkills.add(skill);
	}

	/**
	 * Adds an effect to the skills hitEffects ArrayList
	 * 
	 * @param effect
	 *            the skills to add to hitEffects
	 */
	public void addHitEffect(Effect effect) {
		hitEffects.add(effect);
	}

	/**
	 * Adds an effect to the skills missEffects ArrayList
	 * 
	 * @param effect
	 *            the skills to add to missEffects
	 */
	public void addMissEffect(Effect effect) {
		missEffects.add(effect);
	}

	/**
	 * Adds an effect to the skills alwaysEffects ArrayList
	 * 
	 * @param effect
	 *            the skills to add to alwaysEffects
	 */
	public void addAlwaysEffect(Effect effect) {
		alwaysEffects.add(effect);
	}

	/**
	 * Returns true if the skill should make an attack roll
	 * 
	 * @return if the skill is an attack skill
	 */
	public boolean isAttack() {
		return isAttack;
	}

	/**
	 * Returns the damage type of the skills regular attack
	 * 
	 * @return the damageType of the skill
	 */
	public String getDamageType() {
		return damageType;
	}

	/**
	 * Returns a description of the skill as a string
	 * 
	 * @return the description of the skill
	 */
	public String getDescription() {
		return description;
	}

	/**
	 * Returns the description to be displayed if the skill is used by a non-player
	 * and hits
	 * 
	 * @return the skills attackDescription
	 */
	public String getAttackDescription() {
		return attackDescription;
	}

	/**
	 * Returns the description to be displayed if the skill is used by a player and
	 * hits
	 * 
	 * @return the skills playerAttackDescription
	 */
	public String getPlayerAttackDescription() {
		return playerAttackDescription;
	}

	/**
	 * Returns the skills damage modifier
	 * 
	 * @return the attackDamageBonus
	 */
	public double getAttackDamageMult() {
		return attackDamageMult;
	}

	/**
	 * @return the attackDamageBonus
	 */
	public int getAttackDamageBonus() {
		return attackDamageBonus;
	}

	/**
	 * @return the attackVariableDamageBonus
	 */
	public int getAttackVariableDamageBonus() {
		return attackVariableDamageBonus;
	}

	/**
	 * Returns the skills hit modifier
	 * 
	 * @return the attackHitBonus
	 */
	public int getAttackHitBonus() {
		return attackHitBonus;
	}

	/**
	 * Returns the skills speed bonus
	 * 
	 * @return the attackSpeedBonus
	 */
	public int getAttackSpeedBonus() {
		return attackSpeedBonus;
	}

	/**
	 * Returns the skills name
	 * 
	 * @return the skills name
	 */
	public String getName() {
		return name;
	}

	/**
	 * Returns the description to be displayed if a non-player Entity uses the skill
	 * and misses
	 * 
	 * @return the skills missDescription
	 */
	public String getMissDescription() {
		return missDescription;
	}

	/**
	 * Returns the description to be displayed if the player uses the skill and
	 * misses
	 * 
	 * @return the skills missDescription
	 */
	public String getPlayerMissDescription() {
		return playerMissDescription;
	}

	/**
	 * Returns the stamina cost of the skill
	 * 
	 * @return the skills staminaCost
	 */
	public int getStaminaCost() {
		return staminaCost;
	}

	/**
	 * Returns the skills hitEffects ArrayList
	 * 
	 * @return the skills hitEffects ArrayList
	 */
	public List<Effect> getHitEffects() {
		return hitEffects;
	}

	/**
	 * Returns the skills alwaysEffects ArrayList
	 * 
	 * @return the skills alwaysEffects ArrayList
	 */
	public List<Effect> getAlwaysEffects() {
		return alwaysEffects;
	}

	/**
	 * Returns the skills missEffects ArrayList
	 * 
	 * @return the skills missEffects ArrayList
	 */
	public List<Effect> getMissEffects() {
		return missEffects;
	}

	/**
	 * Returns the skills hitSkills ArrayList
	 * 
	 * @return the skills hitSkills ArrayList
	 */
	public List<Skill> getHitSkills() {
		return hitSkills;
	}

	/**
	 * Returns the skills alwaysSkills ArrayList
	 * 
	 * @return the skills alwaysSkills ArrayList
	 */
	public List<Skill> getAlwaysSkills() {
		return alwaysSkills;
	}

	/**
	 * Returns the skills alwaysSkills ArrayList
	 * 
	 * @return the skills alwaysSkills ArrayList
	 */
	public List<Skill> getMissSkills() {
		return missSkills;
	}
	
	/**
	 * @return the skillType
	 */
	public String getSkillType() {
		return skillType;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "Skill [hitSkills=" + hitSkills + ", alwaysSkills=" + alwaysSkills + ", missSkills=" + missSkills
				+ ", hitEffects=" + hitEffects + ", alwaysEffects=" + alwaysEffects + ", missEffects=" + missEffects
				+ ", name=" + name + ", isAttack=" + isAttack + ", damageType=" + damageType + ", description="
				+ description + ", attackDescription=" + attackDescription + ", playerAttackDescription="
				+ playerAttackDescription + ", missDescription=" + missDescription + ", playerMissDescription="
				+ playerMissDescription + ", attackDamageBounus=" + attackDamageMult + ", attackHitBonus="
				+ attackHitBonus + ", attackSpeedBonus=" + attackSpeedBonus + ", staminaCost=" + staminaCost + "]";
	}

}
