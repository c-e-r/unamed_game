/**
 * 
 */
package unamedGame.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import unamedGame.Calculate;
import unamedGame.Dice;
import unamedGame.Game;
import unamedGame.effects.Effect;
import unamedGame.items.Item;
import unamedGame.skills.Skill;
import unamedGame.spells.Spell;
import unamedGame.time.Time;
import unamedGame.ui.Window;
import unamedGame.util.Colors;

/**
 * @author c-e-r
 *
 */
public class Entity extends Observable {

	private static final Logger LOG = LogManager.getLogger(Game.class);

	protected Item[] equipment;

	protected List<Item> inventory;
	protected List<Effect> effects;

	protected List<Skill> innateSkills;
	protected List<Skill> itemSkills;
	protected List<Skill> combinedSkills;
	protected List<Effect> equipmentEffects;
	protected List<Effect> permanantEffects;
	protected List<Effect> attackEffects;
	protected List<Spell> spells;
	protected List<Spell> knownSpells;
	protected List<Spell> itemSpells;

	protected String name;
	protected String useName;

	protected int vitality;
	protected int vitalityBonus;
	protected int vitalityPenalty;
	protected int strength;
	protected int strengthBonus;
	protected int strengthPenalty;
	protected int dexterity;
	protected int dexterityBonus;
	protected int dexterityPenalty;
	protected int intellect;
	protected int intellectBonus;
	protected int intellectPenalty;
	protected int spirit;
	protected int spiritBonus;
	protected int spiritPenalty;
	protected int luck;
	protected int luckBonus;
	protected int luckPenalty;

	protected int maxHealth;
	protected int maxHealthBonus;
	protected int maxHealthPenalty;
	protected int maxStamina;
	protected int maxStaminaBonus;
	protected int maxStaminaPenalty;
	protected int maxMana;
	protected int maxManaBonus;
	protected int maxManaPenalty;

	protected int speed;
	protected int speedBonus;
	protected int speedPenalty;
	protected int dodge;
	protected int dodgeBonus;
	protected int dodgePenalty;
	protected int baseDodge = 20;

	protected int slashingReduction;
	protected double slashingResistance = 1;
	protected int piercingReduction;
	protected double piercingResistance = 1;
	protected int bludgeoningReduction;
	protected double bludgeoningResistance = 1;
	protected int fireReduction;
	protected double fireResistance = 1;
	protected int coldReduction;
	protected double coldResistance = 1;
	protected int electricityReduction;
	protected double electricityResistance = 1;
	protected int sacredReduction;
	protected double sacredResistance = 1;
	protected int profaneReduction;
	protected double profaneResistance = 1;
	protected int poisonReduction;
	protected double poisonResistance = 1;

	protected int slashingReductionBonus;
	protected int slashingReductionPenalty;
	protected double slashingResistanceBonus;
	protected double slashingResistancePenalty;
	protected int piercingReductionBonus;
	protected int piercingReductionPenalty;
	protected double piercingResistanceBonus;
	protected double piercingResistancePenalty;
	protected int bludgeoningReductionBonus;
	protected int bludgeoningReductionPenalty;
	protected double bludgeoningResistanceBonus;
	protected double bludgeoningResistancePenalty;
	protected int fireReductionBonus;
	protected int fireReductionPenalty;
	protected double fireResistanceBonus;
	protected double fireResistancePenalty;
	protected int coldReductionBonus;
	protected int coldReductionPenalty;
	protected double coldResistanceBonus;
	protected double coldResistancePenalty;
	protected int electricityReductionBonus;
	protected int electricityReductionPenalty;
	protected double electricityResistanceBonus;
	protected double electricityResistancePenalty;
	protected int sacredReductionBonus;
	protected int sacredReductionPenalty;
	protected double sacredResistanceBonus;
	protected double sacredResistancePenalty;
	protected int profaneReductionBonus;
	protected int profaneReductionPenalty;
	protected double profaneResistanceBonus;
	protected double profaneResistancePenalty;
	protected int poisonReductionBonus;
	protected int poisonReductionPenalty;
	protected double poisonResistanceBonus;
	protected double poisonResistancePenalty;

	protected int mentalResistance;
	protected int mentalResistanceBonus;
	protected int mentalResistancePenalty;
	protected int baseMentalResistance = 20;
	protected int physicalResistance;
	protected int physicalResistanceBonus;
	protected int physicalResistancePenalty;
	protected int basePhysicalResistance = 20;

	protected int mentalChanceMult = 1;
	protected int mentalChanceMultBonus;
	protected int mentalChanceMultPenalty;
	protected int physicalChanceMult = 1;
	protected int physicalChanceMultBonus;
	protected int physicalChanceMultPenalty;

	protected int hit;
	protected int hitBonus;
	protected int hitPenalty;
	protected double hitMult = 1;
	protected double hitMultBonus;
	protected double hitMultPenalty;
	protected int damageBonus;
	protected int damagePenalty;
	protected double damageMult = 1;
	protected double damageMultBonus;
	protected double damageMultPenalty;
	protected int healBonus;
	protected int healModPenalty;
	protected double healMult = 1;
	protected double healMultBonus;
	protected double healMultPenalty;
	protected int effectMod;
	protected int effectModPenalty;

	protected int carryCapacity;
	protected int carryCapacityBonus;
	protected int carryCapacityPenalty;

	protected int currentHealth;
	protected int currentStamina;
	protected int currentMana;

	protected Item innateWeapon;

	protected int equipSpeedPenalty;

	/**
	 * Restores health to the Entity
	 * 
	 * @param heal
	 *            the amount of health to restore
	 * @return the amount of health restored
	 */
	public int restoreHealth(int heal) {
		if (currentHealth + heal > maxHealth) {
			heal = maxHealth - currentHealth;
		}
		currentHealth += heal;
		checkHealth();
		return heal;
	}

	/**
	 * Calculates derived stats
	 */
	public void calculateDerivedStats() {
		maxHealth = vitality * 5;
		maxStamina = strength * 3 + vitality * 2 + dexterity * 2;
		maxMana = spirit * 4 + intellect * 3;
		speed = dexterity * 2;
		dodge = dexterity * 5 + intellect * 2 + baseDodge;
		mentalResistance = spirit * 2 + intellect * 1 + baseMentalResistance;
		physicalResistance = vitality * 2 + strength * 1
				+ basePhysicalResistance;
		hit = dexterity * 5 + intellect * 2;
		carryCapacity = strength * 5;

	}

	/**
	 * Calculates the total armor of equipped items and returns it
	 * 
	 * @return the total armor of all equipped items
	 */
	public void calculateEquipmentDefences() {

		for (int i = 0; i < equipment.length; i++) {
			Item item = equipment[i];
			if (item != null) {
				if (!(isWieldingTwoHanded() && i == 3)) {
					piercingReductionBonus += item.getPiercingReduction();
					slashingReductionBonus += item.getSlashingReduction();
					bludgeoningReductionBonus += item.getBludgeoningReduction();
					fireReductionBonus += item.getFireReduction();
					coldReductionBonus += item.getColdReduction();
					electricityReductionBonus += item.getElectricityReduction();
					sacredReductionBonus += item.getSacredReduction();
					profaneReductionBonus += item.getProfaneReduction();
					poisonReductionBonus += item.getPoisonReduction();
					equipSpeedPenalty += item.getSpeedPenalty();
				}
			}
		}
	}

	/**
	 * Returns the main hand weapon
	 * 
	 * @return the main hand weapon
	 */
	public Item getMainWeapon() {

		if (equipment[EquipmentIndex.RIGHT_HELD.getValue()] != null) {
			return equipment[EquipmentIndex.RIGHT_HELD.getValue()];

		} else if (innateWeapon != null) {

			return innateWeapon;
		}
		return null;
	}

	/**
	 * Returns the off-hand hand weapon
	 * 
	 * @return the off-hand hand weapon
	 */
	public Item getOffhandWeapon() {

		if (equipment[EquipmentIndex.LEFT_HELD.getValue()] != null) {
			return equipment[EquipmentIndex.LEFT_HELD.getValue()];
		}
		return null;
	}

	public boolean isWieldingTwoHanded() {
		return getMainWeapon() == getOffhandWeapon();
	}

	/**
	 * Receive a standard attack from the attacker
	 * 
	 * @param attacker
	 */
	public void getAttacked(Entity attacker, boolean usingOffhandWeapon) {
		boolean attackHit = false;
		Item weapon;

		if (usingOffhandWeapon) {
			weapon = attacker.getOffhandWeapon();
		} else {
			weapon = attacker.getMainWeapon();
		}

		triggerEffects("attacked");

		int weaponBaseDamage = 0;
		int weaponVariableDamage = 1;
		int weaponHitChance = 0;
		String weaponDamageType = "null";
		String weaponAttackHitDescription;
		String weaponAttackMissDescription;
		String playerWeaponAttackHitDescription;
		String playerWeaponAttackMissDescription;

		weaponBaseDamage = weapon.getWeaponBaseDamage();
		weaponHitChance = weapon.getWeaponHitChance();
		weaponVariableDamage = weapon.getWeaponVariableDamage();
		weaponDamageType = weapon.getDamageType();
		weaponAttackHitDescription = weapon.getAttackHitDescription();
		weaponAttackMissDescription = weapon.getAttackMissDescription();
		playerWeaponAttackHitDescription = weapon
				.getPlayerAttackHitDescription();
		playerWeaponAttackMissDescription = weapon
				.getPlayerAttackMissDescription();

		String[] description = null;

		int damage = Calculate.calculateAttackDamage(attacker, weapon);
		// Prevent damage from going below 0
		if (damage < 0) {
			damage = 0;
		}

		attackHit = Calculate.calculateAttackHitChance(attacker,
				weaponHitChance) >= this.getEffectiveDodge();
		if (attackHit) {
			damage = this.takeDamage(damage, weaponDamageType);
			if (attacker instanceof Player) {
				description = playerWeaponAttackHitDescription.split("#");
			} else {
				description = weaponAttackHitDescription.split("#");
			}

		} else {
			if (attacker instanceof Player) {
				description = playerWeaponAttackMissDescription.split("#");
			} else {
				description = weaponAttackMissDescription.split("#");
			}

		}
		// Replace the placeholder words with variables
		for (String string : description) {
			switch (string) {
			case "userName":
				Window.addToPane(Window.getInstance().getTextPane(),
						attacker.getUseName());
				break;
			case "userNameCapital":
				Window.addToPane(Window.getInstance().getTextPane(),
						Game.capitalizeFirstLetter(attacker.getUseName()));
				break;
			case "targetName":
				Window.addToPane(Window.getInstance().getTextPane(),
						this.getUseName());
				break;
			case "targetNameCapital":
				Window.addToPane(Window.getInstance().getTextPane(),
						Game.capitalizeFirstLetter(this.getUseName()));
				break;
			case "damage":
				if (damage == 0) {
					Window.addToPane(Window.getInstance().getTextPane(),
							Integer.toString(damage), Colors.DAMAGE_BLOCK);
				} else {
					Window.addToPane(Window.getInstance().getTextPane(),
							Integer.toString(damage), Colors.DAMAGE);
				}
				break;
			case "weaponName":
				Window.addToPane(Window.getInstance().getTextPane(),
						Game.capitalizeFirstLetter(weapon.getName()));
				break;
			default:
				Window.addToPane(Window.getInstance().getTextPane(), string);
				break;
			}
		}
		Window.appendToPane(Window.getInstance().getTextPane(), "");

		// Apply attack effects if the attack hit
		if (weapon != null && attackHit) {
			for (Effect effect : weapon.getAttackEffects()) {
				if (effect != null) {
					if (effect.isToSelf()) {
						attacker.addEffect(effect, attacker);
					} else {
						addEffect(effect, attacker);
					}
				}
			}
		}
	}

	/**
	 * Use a skill on the given enemy
	 * 
	 * @param skillIndex
	 *            the index of the skill to use
	 * @param target
	 *            the target of the skill
	 */
	public void useSkill(int skillIndex, Entity target) {
		Skill skill = combinedSkills.get(skillIndex);
		target.getAttackedBySkill(skill, this, skill.isOffhandSkill());
	}

	/**
	 * Use a spell on the given enemy
	 * 
	 * @param spellIndex
	 *            the index of the spell to use
	 * @param target
	 *            the target of the spell
	 */
	public void useSpell(int spellIndex, Entity target) {
		target.getAttackedBySpell(spells.get(spellIndex), this);
	}

	/**
	 * Trigger self-destruct or special effects on effects watching this entity
	 * 
	 * @param reason
	 *            the reason effects are being triggered
	 */
	public void triggerEffects(String reason) {
		setChanged();
		notifyObservers((String) reason);
	}

	/**
	 * Get attacked by a skill
	 * 
	 * @param skill
	 *            the skill to be hit by
	 * @param attacker
	 *            the attacker
	 */
	public void getAttackedBySkill(Skill skill, Entity attacker,
			boolean usingOffhandWeapon) {
		Item weapon = attacker.getMainWeapon();
		boolean attackHit = true;

		int weaponBaseDamage = 0;
		int weaponVariableDamage = 1;
		int weaponHitChance = 0;
		String weaponDamageType = "null";

		// Get weapon information of weapon if a weapon exists. Otherwise get
		// innate
		// weapon stats
		if (usingOffhandWeapon) {
			weapon = attacker.getOffhandWeapon();
		} else {
			weapon = attacker.getMainWeapon();
		}

		weaponBaseDamage = weapon.getWeaponBaseDamage();
		weaponHitChance = weapon.getWeaponHitChance();
		weaponVariableDamage = weapon.getWeaponVariableDamage();
		weaponDamageType = weapon.getDamageType();

		String[] description = null;

		int damage = Calculate.calculateSkillAttackDamage(attacker, skill,
				weaponBaseDamage, weaponVariableDamage);
		// Prevent damage from going below 0
		if (damage < 0) {
			damage = 0;
		}
		if (skill.isAttack()) {
			attackHit = Calculate.calculateSkillAttackHitChance(attacker, skill,
					weaponHitChance) >= this.getEffectiveDodge();
			if (attackHit) {
				damage = this.takeDamage(damage, weaponDamageType);
			}
		}
		if (attackHit) {
			if (attacker instanceof Player) {
				description = skill.getPlayerAttackDescription().split("#");
			} else {
				description = skill.getAttackDescription().split("#");
			}

		} else {

			if (attacker instanceof Player) {
				description = skill.getPlayerMissDescription().split("#");
			} else {
				description = skill.getMissDescription().split("#");
			}
		}

		// Replace keywords in description with variables
		for (String string : description) {
			switch (string) {
			case "userName":
				Window.addToPane(Window.getInstance().getTextPane(),
						attacker.getUseName());
				break;
			case "userNameCapital":
				Window.addToPane(Window.getInstance().getTextPane(),
						Game.capitalizeFirstLetter(attacker.getUseName()));
				break;
			case "targetName":
				Window.addToPane(Window.getInstance().getTextPane(),
						this.getUseName());
				break;
			case "targetNameCapital":
				Window.addToPane(Window.getInstance().getTextPane(),
						Game.capitalizeFirstLetter(this.getUseName()));
				break;
			case "damage":
				if (damage == 0) {
					Window.addToPane(Window.getInstance().getTextPane(),
							Integer.toString(damage), Colors.DAMAGE_BLOCK);
				} else {
					Window.addToPane(Window.getInstance().getTextPane(),
							Integer.toString(damage), Colors.DAMAGE);
				}
				break;
			case "weaponName":
				Window.addToPane(Window.getInstance().getTextPane(),
						Game.capitalizeFirstLetter(weapon.getName()));
				break;
			default:
				Window.addToPane(Window.getInstance().getTextPane(), string);
				break;

			}

		}
		Window.appendToPane(Window.getInstance().getTextPane(), "");
		// Apply weapon attack effects if weapon attack hit
		if (weapon != null && attackHit) {
			for (Effect effect : weapon.getAttackEffects()) {
				if (effect != null) {
					if (effect.isToSelf()) {
						attacker.addEffect(effect, attacker);
					} else {
						addEffect(effect, attacker);
					}
				}
			}
		}
		// Apply skills effects that happen on hit
		if (attackHit) {
			for (Effect effect : skill.getHitEffects()) {
				if (effect != null) {
					if (effect.isToSelf()) {
						attacker.addEffect(effect, attacker);
					} else {
						addEffect(effect, attacker);
					}
				}

			}
		} else { // Apply skills effects that happen on miss
			for (Effect effect : skill.getMissEffects()) {
				if (effect != null) {
					if (effect.isToSelf()) {
						attacker.addEffect(effect, attacker);
					} else {
						addEffect(effect, attacker);
					}
				}

			}
		}
		// Apply skills effects that happen always
		for (Effect effect : skill.getAlwaysEffects()) {
			if (effect != null) {
				if (effect.isToSelf()) {
					attacker.addEffect(effect, attacker);
				} else {
					addEffect(effect, attacker);
				}
			}

		}
		// Apply skills child skills that happen on hit
		if (attackHit) {
			for (Skill subSkill : skill.getHitSkills()) {
				if (skill != null) {
					getAttackedBySkill(subSkill, attacker, false);
				}

			}
		} else { // Apply skills child skills that happen on miss
			for (Skill subSkill : skill.getMissSkills()) {
				if (skill != null) {
					getAttackedBySkill(subSkill, attacker, false);
				}

			}
		}
		// Apply skills child skills that happen always
		for (Skill subSkill : skill.getAlwaysSkills()) {
			if (skill != null) {
				getAttackedBySkill(subSkill, attacker, false);
			}

		}

	}

	/**
	 * Get attacked by a spell
	 * 
	 * @param spell
	 *            the spell to be hit by
	 * @param attacker
	 *            the attacker
	 */
	public void getAttackedBySpell(Spell spell, Entity attacker) {
		Item spellFocus = attacker.getMainWeapon();

		int spellFocusHitChance = 0;

		if (spellFocus != null) {
			if (spellFocus.isSpellFocus()) {
				spellFocusHitChance = spellFocus.getWeaponHitChance();
			} else {
				spellFocus = null;
			}
		}

		boolean spellHit = false;

		// Get weapon information of weapon if a weapon exists. Otherwise get
		// innate
		// weapon stats

		String[] description = null;

		int temp = Dice.roll(Dice.HIT_DIE);

		if (spell.isAttack()) {
			if (attacker.getEffectiveHit() + spellFocusHitChance
					+ spell.getAttackHitBonus()
					+ temp >= this.getEffectiveDodge()) {
				spellHit = true;

				if (attacker instanceof Player) {
					description = spell.getPlayerAttackDescription().split("#");
				} else {
					description = spell.getAttackDescription().split("#");
				}

			} else {
				if (attacker instanceof Player) {
					description = spell.getPlayerMissDescription().split("#");
				} else {
					description = spell.getMissDescription().split("#");
				}

			}
		} else {
			if (attacker instanceof Player) {
				description = spell.getPlayerAttackDescription().split("#");

			} else {
				description = spell.getAttackDescription().split("#");
			}
		}
		// Replace keywords in description with variables
		for (String string : description) {
			switch (string) {
			case "userName":
				Window.addToPane(Window.getInstance().getTextPane(),
						attacker.getUseName());
				break;
			case "userNameCapital":
				Window.addToPane(Window.getInstance().getTextPane(),
						Game.capitalizeFirstLetter(attacker.getUseName()));
				break;
			case "targetName":
				Window.addToPane(Window.getInstance().getTextPane(),
						this.getUseName());
				break;
			case "targetNameCapital":
				Window.addToPane(Window.getInstance().getTextPane(),
						Game.capitalizeFirstLetter(this.getUseName()));
				break;
			case "spellFocusName":
				Window.addToPane(Window.getInstance().getTextPane(),
						Game.capitalizeFirstLetter(spellFocus.getName()));
				break;
			default:
				Window.addToPane(Window.getInstance().getTextPane(), string);
				break;

			}

		}
		Window.appendToPane(Window.getInstance().getTextPane(), "");
		// Apply weapon attack effects if weapon attack hit
		if (spellFocus != null && spellHit) {
			for (Effect effect : spellFocus.getSpellEffects()) {
				if (effect != null) {
					if (effect.isToSelf()) {
						attacker.addEffect(effect, attacker);
					} else {
						addEffect(effect, attacker);
					}
				}
			}
		}
		// Apply skills effects that happen on hit
		if (spellHit) {
			for (Effect effect : spell.getHitEffects()) {
				if (effect != null) {
					if (effect.isToSelf()) {
						attacker.addEffect(effect, attacker);
					} else {
						addEffect(effect, attacker);
					}
				}

			}
		} else { // Apply skills effects that happen on miss
			for (Effect effect : spell.getMissEffects()) {
				if (effect != null) {
					if (effect.isToSelf()) {
						attacker.addEffect(effect, attacker);
					} else {
						addEffect(effect, attacker);
					}
				}

			}
		}
		// Apply skills effects that happen always
		for (Effect effect : spell.getAlwaysEffects()) {
			if (effect != null) {
				if (effect.isToSelf()) {
					attacker.addEffect(effect, attacker);
				} else {
					addEffect(effect, attacker);
				}
			}

		}
		// Apply skills child skills that happen on hit
		if (spellHit) {
			for (Spell subSpell : spell.getHitSpells()) {
				if (spell != null) {
					getAttackedBySpell(subSpell, attacker);
				}

			}
		} else { // Apply skills child skills that happen on miss
			for (Spell subSpell : spell.getMissSpells()) {
				if (spell != null) {
					getAttackedBySpell(subSpell, attacker);
				}

			}
		}
		// Apply skills child skills that happen always
		for (Spell subSpell : spell.getAlwaysSpells()) {
			if (spell != null) {
				getAttackedBySpell(subSpell, attacker);
			}

		}

	}

	/**
	 * Apply resistances to the given damage value based on the damage type
	 * 
	 * @param damage
	 *            the damage to apply resistances to
	 * @param damageType
	 *            the type of damage
	 * @return the damage after resistances are applied
	 */
	public int applyResistances(int damage, String damageType) {
		switch (damageType) {
		case "slashing":
			return (int) ((damage - slashingReduction - slashingReductionBonus)
					* (slashingResistance + slashingResistanceBonus));
		case "piercing":
			return (int) ((damage - piercingReduction - piercingReductionBonus)
					* (piercingResistance + piercingResistanceBonus));
		case "bludgeoning":
			return (int) ((damage - bludgeoningReduction
					- bludgeoningReductionBonus)
					* (bludgeoningResistance + bludgeoningResistanceBonus));
		case "fire":
			return (int) ((damage - fireReduction - fireReductionBonus)
					* (fireResistance + fireResistanceBonus));
		case "cold":
			return (int) ((damage - coldReduction - coldReductionBonus)
					* (coldResistance + coldResistanceBonus));
		case "electricity":
			return (int) ((damage - electricityReduction
					- electricityReductionBonus)
					* (electricityResistance + electricityResistanceBonus));
		case "sacred":
			return (int) ((damage - sacredReduction - sacredReductionBonus)
					* (sacredResistance + sacredResistanceBonus));
		case "profane":
			return (int) ((damage - profaneReduction - profaneReductionBonus)
					* (profaneResistance + profaneResistanceBonus));
		case "poison":
			return (int) ((damage - poisonReduction - poisonReductionBonus)
					* (poisonResistance + poisonResistanceBonus));
		case "unresistable":
			return damage;

		default:
			return 0;
		}
	}

	/**
	 * Attack the given target
	 * 
	 * @param target
	 *            the target to attack
	 */
	public void attack(Entity target) {
		int temp = getEffectiveSpeed();
		target.getAttacked(this, false);

		if (temp >= 10 && getOffhandWeapon() != null) {
			target.getAttacked(this, true);
		}
		if (temp >= 20) {
			target.getAttacked(this, false);
		}
		if (temp >= 30 && getOffhandWeapon() != null) {
			target.getAttacked(this, true);
		}
		if (temp >= 40) {
			target.getAttacked(this, false);
		}

	}

	/**
	 * Apply effects of all permanent status effects
	 */
	public void applyPermanantStatusEffects() {
		for (Effect effect : permanantEffects) {
			effect.applyEffect(this);
		}
	}

	/**
	 * Apply effects of all equipment effects
	 */
	public void applyEquipmentEffects() {
		for (Effect effect : equipmentEffects) {
			effect.applyEffect(this);
		}
	}

	/**
	 * Checks if an effect of that name already exists in the effects List
	 * 
	 * @param effect
	 *            the effect to check
	 * @return if an effect of the name exists
	 */
	public boolean ifEffectExists(Effect effect) {
		for (Effect effect2 : effects) {
			if (effect.getName().equals(effect2.getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Increases the given stat by the given modifier
	 * 
	 * @param stat
	 *            the stat to increases as a string
	 * @param modifier
	 *            the number to increase the stat by
	 */
	public void increaseModifier(String stat, int modifier) {
		switch (stat) {
		case "vitality":
			if (modifier > vitalityBonus) {
				vitalityBonus = modifier;
			}
			break;
		case "strength":
			if (modifier > strengthBonus) {
				strengthBonus = modifier;
			}
			break;
		case "dexterity":
			if (modifier > dexterityBonus) {
				dexterityBonus = modifier;
			}
			break;
		case "intellect":
			if (modifier > intellectBonus) {
				intellectBonus = modifier;
			}
			break;
		case "spirit":
			if (modifier > spiritBonus) {
				spiritBonus = modifier;
			}
			break;
		case "luck":
			if (modifier > luckBonus) {
				luckBonus = modifier;
			}
			break;
		case "health":
			if (modifier > maxHealthBonus) {
				maxHealthBonus = modifier;
			}
			break;
		case "stamina":
			if (modifier > maxStaminaBonus) {
				maxStaminaBonus = modifier;
			}
			break;
		case "mana":
			if (modifier > maxManaBonus) {
				maxManaBonus = modifier;
			}
			break;
		case "speed":
			if (modifier > speedBonus) {
				speedBonus = modifier;
			}
			break;
		case "hit":
			if (modifier > hitBonus) {
				hitBonus = modifier;
			}
			break;
		case "dodge":
			if (modifier > dodgeBonus) {
				dodgeBonus = modifier;
			}
			break;
		case "slashingReduction":
			if (modifier > slashingReductionBonus) {
				slashingReductionBonus = modifier;
			}
			break;
		case "piercingReduction":
			if (modifier > piercingReductionBonus) {
				piercingReductionBonus = modifier;
			}
			break;
		case "bludgeoningReduction":
			if (modifier > bludgeoningReductionBonus) {
				bludgeoningReductionBonus = modifier;
			}
			break;
		case "fireReduction":
			if (modifier > fireReductionBonus) {
				fireReductionBonus = modifier;
			}
			break;
		case "coldReduction":
			if (modifier > coldReductionBonus) {
				coldReductionBonus = modifier;
			}
			break;
		case "electricityReduction":
			if (modifier > electricityReductionBonus) {
				electricityReductionBonus = modifier;
			}
			break;
		case "sacredReduction":
			if (modifier > sacredReductionBonus) {
				sacredReductionBonus = modifier;
			}
			break;
		case "profaneReduction":
			if (modifier > profaneReductionBonus) {
				profaneReductionBonus = modifier;
			}
			break;
		case "poisonReduction":
			if (modifier > poisonReductionBonus) {
				poisonReductionBonus = modifier;
			}
			break;
		case "slashingResistance":
			if (modifier > slashingResistanceBonus) {
				slashingResistanceBonus = modifier;
			}
			break;
		case "piercingResistance":
			if (modifier > piercingResistanceBonus) {
				piercingResistanceBonus = modifier;
			}
			break;
		case "bludgeoningResistance":
			if (modifier > bludgeoningResistanceBonus) {
				bludgeoningResistanceBonus = modifier;
			}
			break;
		case "fireResistance":
			if (modifier > fireResistanceBonus) {
				fireResistanceBonus = modifier;
			}
			break;
		case "coldResitance":
			if (modifier > coldResistanceBonus) {
				coldResistanceBonus = modifier;
			}
			break;
		case "electricityResistance":
			if (modifier > electricityResistanceBonus) {
				electricityResistanceBonus = modifier;
			}
			break;
		case "sacredResistance":
			if (modifier > sacredResistanceBonus) {
				sacredResistanceBonus = modifier;
			}
			break;
		case "profaneResistance":
			if (modifier > profaneResistanceBonus) {
				profaneResistanceBonus = modifier;
			}
			break;
		case "poisonResistance":
			if (modifier > poisonResistanceBonus) {
				poisonResistanceBonus = modifier;
			}
			break;
		case "hitMult":
			if (modifier > hitMultBonus) {
				hitMultBonus = modifier;
			}
			break;
		case "damageMult":
			if (modifier > damageMultBonus) {
				damageMultBonus = modifier;
			}
			break;
		case "healMult":
			if (modifier > healMultBonus) {
				healMultBonus = modifier;
			}
			break;
		case "carryCapacitys":
			if (modifier > carryCapacityBonus) {
				carryCapacityBonus = modifier;
			}
			break;
		case "mentalChanceMult":
			if (modifier > mentalChanceMultBonus) {
				mentalChanceMultBonus = modifier;
			}
			break;
		case "physicalChanceMult":
			if (modifier > physicalChanceMultBonus) {
				physicalChanceMultBonus = modifier;
			}
			break;
		case "mentalResistance":
			if (modifier > mentalResistanceBonus) {
				mentalResistanceBonus = modifier;
			}
			break;
		case "physicalResistance":
			if (modifier > physicalResistanceBonus) {
				physicalResistanceBonus = modifier;
			}
			break;
		default:
			break;
		}
	}

	/**
	 * Decreases the given stat by the given modifier
	 * 
	 * @param stat
	 *            the stat to decrease as a string
	 * @param modifier
	 *            the number to decrease the stat by
	 */
	public void decreaseModifier(String stat, int modifier) {
		switch (stat) {
		case "vitality":
			if (modifier > vitalityPenalty) {
				vitalityPenalty = modifier;
			}
			break;
		case "strength":
			if (modifier > strengthPenalty) {
				strengthPenalty = modifier;
			}
			break;
		case "dexterity":
			if (modifier > dexterityPenalty) {
				dexterityPenalty = modifier;
			}
			break;
		case "intellect":
			if (modifier > intellectPenalty) {
				intellectPenalty = modifier;
			}
			break;
		case "spirit":
			if (modifier > spiritPenalty) {
				spiritPenalty = modifier;
			}
			break;
		case "luck":
			if (modifier > luckPenalty) {
				luckPenalty = modifier;
			}
			break;
		case "health":
			if (modifier > maxHealthPenalty) {
				maxHealthPenalty += modifier;
			}
			break;
		case "stamina":
			if (modifier > maxStaminaPenalty) {
				maxStaminaPenalty += modifier;
			}
			break;
		case "mana":
			if (modifier > maxManaPenalty) {
				maxManaPenalty += modifier;
			}
			break;
		case "speed":
			if (modifier > speedPenalty) {
				speedPenalty = modifier;
			}
			break;
		case "hit":
			if (modifier > hitPenalty) {
				hitPenalty = modifier;
			}
			break;
		case "dodge":
			if (modifier > dodgePenalty) {
				dodgePenalty = modifier;
			}
			break;
		case "slashingReduction":
			if (modifier > slashingReductionPenalty) {
				slashingReductionPenalty = modifier;
			}
			break;
		case "piercingReduction":
			if (modifier > piercingReductionPenalty) {
				piercingReductionPenalty = modifier;
			}
			break;
		case "bludgeoningReduction":
			if (modifier > bludgeoningReductionPenalty) {
				bludgeoningReductionPenalty = modifier;
			}
			break;
		case "fireReduction":
			if (modifier > fireReductionPenalty) {
				fireReductionPenalty = modifier;
			}
			break;
		case "coldReduction":
			if (modifier > coldReductionPenalty) {
				coldReductionPenalty = modifier;
			}
			break;
		case "electricityReduction":
			if (modifier > electricityReductionPenalty) {
				electricityReductionPenalty = modifier;
			}
			break;
		case "sacredReduction":
			if (modifier > sacredReductionPenalty) {
				sacredReductionPenalty = modifier;
			}
			break;
		case "profaneReduction":
			if (modifier > profaneReductionPenalty) {
				profaneReductionPenalty = modifier;
			}
			break;
		case "poisonReduction":
			if (modifier > poisonReductionPenalty) {
				poisonReductionPenalty = modifier;
			}
			break;
		case "slashingResistance":
			if (modifier > slashingResistancePenalty) {
				slashingResistancePenalty = modifier;
			}
			break;
		case "piercingResistance":
			if (modifier > piercingResistancePenalty) {
				piercingResistancePenalty = modifier;
			}
			break;
		case "bludgeoningResistance":
			if (modifier > bludgeoningResistanceBonus) {
				bludgeoningResistancePenalty = modifier;
			}
			break;
		case "fireResistance":
			if (modifier > fireResistancePenalty) {
				fireResistancePenalty = modifier;
			}
			break;
		case "coldResitance":
			if (modifier > coldResistancePenalty) {
				coldResistancePenalty = modifier;
			}
			break;
		case "electricityResistance":
			if (modifier > electricityResistancePenalty) {
				electricityResistancePenalty = modifier;
			}
			break;
		case "sacredResistance":
			if (modifier > sacredResistancePenalty) {
				sacredResistancePenalty = modifier;
			}
			break;
		case "profaneResistance":
			if (modifier > profaneResistancePenalty) {
				profaneResistancePenalty = modifier;
			}
			break;
		case "poisonResistance":
			if (modifier > poisonResistancePenalty) {
				poisonResistancePenalty = modifier;
			}
			break;
		case "hitMult":
			if (modifier > hitMultPenalty) {
				hitMultPenalty = modifier;
			}
			break;
		case "damageMult":
			if (modifier > damageMultPenalty) {
				damageMultPenalty = modifier;
			}
			break;
		case "healMult":
			if (modifier > healMultPenalty) {
				healMultPenalty = modifier;
			}
			break;
		case "carryCapacitys":
			if (modifier > carryCapacityPenalty) {
				carryCapacityPenalty = modifier;
			}
			break;
		case "mentalChanceMult":
			if (modifier > mentalChanceMultPenalty) {
				mentalChanceMultPenalty = modifier;
			}
			break;
		case "physicalChanceMult":
			if (modifier > physicalChanceMultPenalty) {
				physicalChanceMultPenalty = modifier;
			}
			break;
		case "mentalResistance":
			if (modifier > mentalResistancePenalty) {
				mentalResistancePenalty = modifier;
			}
			break;
		case "physicalResistance":
			if (modifier > physicalResistancePenalty) {
				physicalResistancePenalty = modifier;
			}
			break;
		default:
			break;
		}
	}

	/**
	 * Check if current health is above the maximum health and sets it to the
	 * maximum if it is
	 */
	public void checkHealth() {
		if (currentHealth > maxHealth) {
			currentHealth = maxHealth;
		}
	}

	/*
	 * Reset all modifier to 0
	 */
	private void resetModifiers() {
		vitalityBonus = 0;
		strengthBonus = 0;
		dexterityBonus = 0;
		intellectBonus = 0;
		spiritBonus = 0;
		luckBonus = 0;
		maxHealthBonus = 0;
		maxStaminaBonus = 0;
		maxManaBonus = 0;
		speedBonus = 0;
		hitBonus = 0;
		dodgeBonus = 0;

		vitalityPenalty = 0;
		strengthPenalty = 0;
		dexterityPenalty = 0;
		intellectPenalty = 0;
		spiritPenalty = 0;
		luckPenalty = 0;
		maxHealthPenalty = 0;
		maxStaminaPenalty = 0;
		maxManaPenalty = 0;
		speedPenalty = 0;
		hitPenalty = 0;
		dodgePenalty = 0;

		slashingReductionBonus = 0;
		slashingResistanceBonus = 0;
		piercingReductionBonus = 0;
		piercingResistanceBonus = 0;
		bludgeoningReductionBonus = 0;
		bludgeoningResistanceBonus = 0;
		fireReductionBonus = 0;
		fireResistanceBonus = 0;
		coldReductionBonus = 0;
		coldResistanceBonus = 0;
		electricityReductionBonus = 0;
		electricityResistanceBonus = 0;
		sacredReductionBonus = 0;
		sacredResistanceBonus = 0;
		profaneReductionBonus = 0;
		profaneResistanceBonus = 0;
		poisonReductionBonus = 0;
		poisonResistanceBonus = 0;

		slashingReductionPenalty = 0;
		slashingResistancePenalty = 0;
		piercingReductionPenalty = 0;
		piercingResistancePenalty = 0;
		bludgeoningReductionPenalty = 0;
		bludgeoningResistancePenalty = 0;
		fireReductionPenalty = 0;
		fireResistancePenalty = 0;
		coldReductionPenalty = 0;
		coldResistancePenalty = 0;
		electricityReductionPenalty = 0;
		electricityResistancePenalty = 0;
		sacredReductionPenalty = 0;
		sacredResistancePenalty = 0;
		profaneReductionPenalty = 0;
		profaneResistancePenalty = 0;
		poisonReductionPenalty = 0;
		poisonResistancePenalty = 0;

		hitMultBonus = 0;
		hitMultPenalty = 0;
		damageMultBonus = 0;
		damageMultPenalty = 0;
		healMultBonus = 0;
		healMultPenalty = 0;

		carryCapacityBonus = 0;
		carryCapacityPenalty = 0;
		mentalChanceMultBonus = 0;
		mentalChanceMultPenalty = 0;
		physicalChanceMultBonus = 0;
		physicalChanceMultPenalty = 0;

		mentalResistanceBonus = 0;
		mentalResistancePenalty = 0;
		physicalResistanceBonus = 0;
		physicalResistancePenalty = 0;

		equipSpeedPenalty = 0;

	}

	/**
	 * Returns effective speed + a roll of the SPEED_DIE + the skill or speed
	 * bonus
	 * 
	 * @param command
	 *            the command. 2 for skill and 3 for spell
	 * @param index
	 *            the index of the skill or spell
	 * @return the speed to check
	 */

	public int speedCheck(int command, int index) {

		int tempSpeedBonus = 0;
		switch (command) {
		case 2:
			tempSpeedBonus = combinedSkills.get(index).getAttackSpeedBonus();
			break;
		case 3:
			tempSpeedBonus = spells.get(index).getAttackSpeedBonus();
			break;
		default:
			break;
		}

		return speed + speedBonus - equipSpeedPenalty + tempSpeedBonus
				+ Dice.roll(Dice.SPEED_DIE);
	}

	/**
	 * Resets all modifiers, reapplies effects, and recalculates derived stats
	 */
	public void recalculateStats() {
		resetModifiers();
		calculateEquipmentDefences();
		applyEffects();
		applyEquipmentEffects();
		calculateDerivedStats();
		reloadSkills();
		reloadSpells();
		checkHealth();
	}

	/**
	 * applies all effects to the player
	 */
	public void applyEffects() {
		for (Effect effect : effects) {
			if (effect.getRepeatType().equals("noRepeat")) {
				effect.applyEffect(this);
			}
		}
	}

	/**
	 * Returns true if there is an effect of the same name as the one given in
	 * the entity's effects list
	 * 
	 * @param effect
	 *            the effect to compare
	 * @return if an effect of the same name exists
	 */
	public boolean checkIfEffectExists(Effect effect) {
		for (Effect effect2 : effects) {
			if (effect2.getName().equals(effect.getName())) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Removes an effect from the permanant effects array list
	 * 
	 * @param effect
	 *            the effect to remove
	 */
	public void removePermanantEffect(Effect effect) {
		permanantEffects.remove(effect);
		recalculateStats();
	}

	/**
	 * Add an effect to the permanent effects array list
	 * 
	 * @param effect
	 */
	public void addPermanantEffect(Effect effect) {
		permanantEffects.add(effect);
		recalculateStats();
	}

	/**
	 * Take an amount of damage of the specified type
	 * 
	 * @param damage
	 *            the amount of damage to take
	 * @param type
	 *            the type of damage to take
	 * @return the amount of damage taken
	 */
	public int takeDamage(int damage, String damageType) {
		damage = applyResistances(damage, damageType);
		if (damage < 0) {
			damage = 0;
		}
		currentHealth -= damage;
		LOG.debug(damage + " taken by " + name);
		return damage;
	}

	/**
	 * Returns true if current health is less than or equal to zero
	 * 
	 * @return
	 */
	public boolean isDead() {
		return currentHealth <= 0;
	}

	/**
	 * Returns the current health
	 * 
	 * @return the current health
	 */
	public int getCurrentHealth() {
		return currentHealth;
	}

	/**
	 * recreates the combined skills arrayList from the item and innate skills
	 * ArrayLists
	 */
	public void reloadSkills() {
		itemSkills = new ArrayList<Skill>();

		for (int i = 0; i < equipment.length; i++) {
			Item item = equipment[i];
			if (item != null) {
				itemSkills.addAll(item.getEquipSkills());
			}
		}

		combinedSkills = new ArrayList<Skill>();
		combinedSkills.addAll(itemSkills);
		combinedSkills.addAll(innateSkills);
	}

	/**
	 * recreates the spells arrayList from the item and known spells ArrayLists
	 */
	public void reloadSpells() {
		itemSpells = new ArrayList<Spell>();

		for (int i = 0; i < equipment.length; i++) {
			Item item = equipment[i];
			if (item != null) {
				itemSpells.addAll(item.getEquipSpells());
			}
		}

		spells = new ArrayList<Spell>();
		spells.addAll(itemSpells);
		spells.addAll(knownSpells);
	}

	/**
	 * Adds an effect to the effects arrayList
	 * 
	 * @param effect
	 *            the effect to add
	 * @param creator
	 *            the creator of the effect
	 */
	public void addEffect(Effect effect, Entity creator) {
		if (!ifEffectExists(effect)) {
			if (effect.getDuration() != 0) {
				effects.add(effect);
			}
			effect.instantiate(this, creator);
			recalculateStats();

			if (effect.getEffects().size() > 0) {
				for (Effect effect2 : effect.getEffects()) {
					if (effect2 != null)
						addEffect(effect2, creator);
				}
			}
		} else {
			updateEffectDuration(effect);

		}

	}

	/*
	 * Increases the duration of effects of the same name as the given effect if
	 * its lower than the given effect.
	 */
	private void updateEffectDuration(Effect effect) {
		if (effect.getDuration() != -1) {

			for (Effect effect2 : effects) {

				if (effect.getName().equals(effect2.getName())
						&& effect.getDuration()
								+ Time.getInstance().getTime() > effect2
										.getEndTime()) {
					effect2.setEndTime(effect.getDuration()
							+ Time.getInstance().getTime());
					if (this instanceof Player) {
						Window.appendToPane(Window.getInstance().getTextPane(),
								Game.capitalizeFirstLetter(
										"You are already under the effects of "
												+ effect2.getName())
										+ ", duration increased");

					} else {
						Window.appendToPane(Window.getInstance().getTextPane(),
								Game.capitalizeFirstLetter(Game
										.capitalizeFirstLetter(useName)
										+ " is already under the effects of "
										+ effect2.getName())
										+ ", duration increased");

					}

				}
			}
		} else {
			if (this instanceof Player) {
				Window.appendToPane(Window.getInstance().getTextPane(),
						Game.capitalizeFirstLetter(
								"You are already under the effects of "
										+ effect.getName()));

			} else {
				Window.appendToPane(Window.getInstance().getTextPane(),
						Game.capitalizeFirstLetter(
								Game.capitalizeFirstLetter(useName)
										+ " is already under the effects of "
										+ effect.getName()));

			}

		}
	}

	/**
	 * removes an effect from the effects list
	 * 
	 * @param effect
	 *            the effect to remove
	 */
	public void removeEffect(Effect effect) {
		if (effects.contains(effect)) {
			effects.remove(effect);
		}
	}

	/**
	 * adds an item to the inventory
	 * 
	 * @param item
	 *            the item to add
	 * @return the item added
	 */
	public Item addItemToInventory(Item item) {
		inventory.add(item);
		return item;
	}

	/**
	 * adds an effect to the attack effect list
	 * 
	 * @param effect
	 *            the effect to add
	 * @return the effect added
	 */
	public Effect addAttackEffect(Effect effect) {
		attackEffects.add(effect);
		return effect;
	}

	/**
	 * Returns the item description for the given item index from inventory
	 * 
	 * @param itemIndex
	 *            the index of the inventory item to get a description of
	 * @return the item description
	 */
	public String getItemDescription(int itemIndex) {
		return inventory.get(itemIndex).getDescription();
	}

	/**
	 * Returns effective max health. maxHealth + maxHealthMod
	 * 
	 * @return the effective max health
	 */
	public int getEffectiveMaxHealth() {
		return maxHealth + maxHealthBonus - maxHealthPenalty;
	}

	/**
	 * Returns the effective speed. speed + speedMod
	 * 
	 * @return the effective speed
	 */
	public int getEffectiveSpeed() {
		return speed + speedBonus - speedPenalty - equipSpeedPenalty;
	}

	/**
	 * Returns grammatically correct noun for description display
	 * 
	 * @return the use name
	 */
	public String getUseName() {
		return useName;
	}

	/**
	 * Returns a list of the entity's inventory
	 * 
	 * @return the inventory List
	 */
	public List<Item> getInventory() {
		return inventory;
	}

	/**
	 * Returns the specified inventory item
	 * 
	 * @param itemIndex
	 *            the index of the item to return
	 * @return the specified item
	 */
	public Item getInventoryItem(int itemIndex) {
		return inventory.get(itemIndex);
	}

	/**
	 * Removes the specified item from inventory
	 * 
	 * @param itemIndex
	 *            the index of the item to remove
	 * @return the item that was removed
	 */
	public Item removeItemFromInventory(int itemIndex) {

		return inventory.remove(itemIndex);
	}

	/**
	 * Returns the name of the entity
	 * 
	 * @return the name of the entity
	 */
	public String getName() {
		return name;
	}

	/**
	 * Adds the given skill to the entity's innate skill list
	 * 
	 * @param skill
	 *            the skill to add
	 */
	public void addInnateSkill(Skill skill) {
		innateSkills.add(skill);
		reloadSkills();
	}

	/**
	 * Adds the given spell to the entity's known spells list
	 * 
	 * @param spell
	 *            the spell to add
	 */
	public void addKnownSpell(Spell spell) {
		knownSpells.add(spell);
		reloadSpells();
	}

	/**
	 * Returns the entity's equipment array
	 * 
	 * @return the equipment array
	 */
	public Item[] getEquipment() {
		return equipment;
	}

	/**
	 * Returns the entity's innate skills List
	 * 
	 * @return the innateSkills List
	 */
	public List<Skill> getInnateSkills() {
		return innateSkills;
	}

	/**
	 * Returns the entity's combined skills List
	 * 
	 * @return the combinedSkills list
	 */
	public List<Skill> getCombinedSkills() {
		reloadSkills();
		return combinedSkills;
	}

	/**
	 * Returns the entity's item skills List
	 * 
	 * @return the itemSkills List
	 */
	public List<Skill> getItemSkills() {
		return itemSkills;
	}

	/**
	 * Returns the entity's item spells List
	 * 
	 * @return the itemSpells List
	 */
	public List<Spell> getItemSpells() {
		return itemSpells;
	}

	/**
	 * Returns the entity's spells List
	 * 
	 * @return the spells List
	 */
	public List<Spell> getSpells() {
		return spells;
	}

	/**
	 * Returns the entity's knownSpells List
	 * 
	 * @return the knownSpells List
	 */
	public List<Spell> getKnownSpells() {
		return knownSpells;
	}

	/**
	 * Returns the entity's vitality stat
	 * 
	 * @return the vitality stat
	 */
	public int getVitality() {
		return vitality;
	}

	/**
	 * Returns the entity's effective vitality stat. vitality + vitalityMod
	 * 
	 * @return the effective vitality
	 */
	public int getEffectiveVitality() {
		return vitality + vitalityBonus - vitalityPenalty;
	}

	/**
	 * Returns the entity's strength stat
	 * 
	 * @return the strength stat
	 */
	public int getStrength() {
		return strength;
	}

	/**
	 * Returns the entity's effective strength stat. strength + strengthMod
	 * 
	 * @return the effective strength
	 */
	public int getEffectiveStrength() {
		return strength + strengthBonus - strengthPenalty;
	}

	/**
	 * Returns the entity's dexterity stat
	 * 
	 * @return the dexterity stat
	 */
	public int getDexterity() {
		return dexterity;
	}

	/**
	 * Returns the entity's effective dexterity stat dexterity + dexterityMod
	 * 
	 * @return the effective strength
	 */
	public int getEffectiveDexterity() {
		return dexterity + dexterityBonus - dexterityPenalty;
	}

	/**
	 * Returns the entity's intellect stat
	 * 
	 * @return the intellect stat
	 */
	public int getIntellect() {
		return intellect;
	}

	/**
	 * Returns the entity's effective intellect stat intellect + intellectMod
	 * 
	 * @return the effective intellect
	 */
	public int getEffectiveIntellect() {
		return intellect + intellectBonus - intellectPenalty;
	}

	/**
	 * Returns the entity's spirit stat
	 * 
	 * @return the spirit stat
	 */
	public int getSpirit() {
		return spirit;
	}

	/**
	 * Returns the entity's effective spirit stat spirit + spiritMod
	 * 
	 * @return the effective spirit
	 */
	public int getEffectiveSpirit() {
		return spirit + spiritBonus - spiritPenalty;
	}

	/**
	 * Returns the entity's luck stat
	 * 
	 * @return the luck stat
	 */
	public int getLuck() {
		return luck;
	}

	/**
	 * Returns the entity's effective luck stat luck + luckMod
	 * 
	 * @return the effective luck
	 */
	public int getEffectiveLuck() {
		return luck + luckBonus - luckPenalty;
	}

	/**
	 * Returns the entity's maximum stamina
	 * 
	 * @return the maxStamina
	 */
	public int getMaxStamina() {
		return maxStamina;
	}

	/**
	 * Returns the entity's effective maximum stamina maxStamina + maxStaminaMod
	 * 
	 * @return the effective maximum stamina
	 */
	public int getEffectiveStamina() {
		return maxStamina + maxStaminaBonus - maxStaminaPenalty;
	}

	/**
	 * Returns the entity's maximum mana
	 * 
	 * @return the maxMana
	 */
	public int getMaxMana() {
		return maxMana;
	}

	/**
	 * Returns the entity's effective maximum mana maxMana + maxManaMod
	 * 
	 * @return the effective maximum mana
	 */
	public int getEffectiveMana() {
		return maxMana + maxManaBonus - maxManaPenalty;
	}

	/**
	 * Returns the entity's dodge stat
	 * 
	 * @return the dodge stat
	 */
	public int getDodge() {
		return dodge;
	}

	/**
	 * Returns the entity's effective dodge stat dodge + dodgeMod
	 * 
	 * @return the effective dodge stat
	 */
	public int getEffectiveDodge() {
		return dodge + dodgeBonus - dodgePenalty;
	}

	/**
	 * Returns the entity's hit stat
	 * 
	 * @return the hit stat
	 */
	public int getHit() {
		return hit;
	}

	/**
	 * Returns the entity's effective hit rate (hit + hitMod) * hitMult
	 * 
	 * @return
	 */
	public int getEffectiveHit() {
		return (int) ((hit + hitBonus - hitPenalty) * hitMult + hitMultBonus
				- hitMultPenalty);
	}

	/**
	 * Returns the entity's damage stat
	 * 
	 * @return the damage stat
	 */
	public int getDamageBonus() {
		return damageBonus;
	}

	/**
	 * Return the enemies effective damage bonus
	 * 
	 * @return the effective damage bonus
	 */
	public int getEffectiveDamageBonus() {
		return damageBonus - damagePenalty;
	}

	/**
	 * Return the enemies effective damage bonus
	 * 
	 * @return the effective damage bonus
	 */
	public double getEffectiveDamageMult() {
		return damageMult - damageMultPenalty;
	}

	/**
	 * Returns the entity's damage multiplier
	 * 
	 * @return the damageMult
	 */
	public double getDamageMult() {
		return damageMult;
	}

	/**
	 * Returns the entity's heal modifier
	 * 
	 * @return the healMod
	 */
	public int getHealBonus() {
		return healBonus;
	}

	/**
	 * Returns the entity's heal multiplier
	 * 
	 * @return the healMult
	 */
	public double getHealMult() {
		return healMult;
	}

	/**
	 * Returns the entity's carry capacity
	 * 
	 * @return the carryCapacity
	 */
	public int getCarryCapacity() {
		return carryCapacity;
	}

	/**
	 * Returns the entity's carry capacity modifier
	 * 
	 * @return the carryCapacityMod
	 */
	public int getCarryCapacityBonus() {
		return carryCapacityBonus;
	}

	/**
	 * Returns the entity's current stamina
	 * 
	 * @return the currentStamina
	 */
	public int getCurrentStamina() {
		return currentStamina;
	}

	/**
	 * Returns the entity's current mana
	 * 
	 * @return the currentMana
	 */
	public int getCurrentMana() {
		return currentMana;
	}

	/**
	 * Returns the entity's equipment effects List
	 * 
	 * @return the equipmentEffects List
	 */
	public List<Effect> getEquipmentEffects() {
		return equipmentEffects;
	}

	/**
	 * Returns the entity's effective mental resistance
	 * 
	 * @return the effective mental distance
	 */
	public int getEffectiveMentalResistance() {
		return mentalResistance + mentalResistanceBonus
				- mentalResistancePenalty;
	}

	/**
	 * Returns the entity's effective physical resistance
	 * 
	 * @return the effective physical resistance
	 */
	public int getEffectivePhysicalResistance() {
		return physicalResistance + physicalResistanceBonus
				- physicalResistancePenalty;
	}

	/**
	 * Returns the entity's mental effect chance multiplier
	 * 
	 * @return the mentalChanceMult
	 */
	public int getMentalChanceMult() {
		return mentalChanceMult;
	}

	/**
	 * Returns the entity's effective physical chance multiplier
	 * 
	 * @return the effective physical chance multiplier
	 */
	public int getEffectivePhysicalChanceMult() {
		return physicalChanceMult + physicalChanceMultBonus
				- physicalChanceMultPenalty;
	}

	/**
	 * Returns the entity's physical effect chance multiplier
	 * 
	 * @return the physicalChanceMult
	 */
	public int getPhysicalChanceMult() {
		return physicalChanceMult;
	}

	/**
	 * Returns the entity's effective mental chance multiplier
	 * 
	 * @return the effective mental chance multiplier
	 */
	public int getEffectiveMentalChanceMult() {
		return mentalChanceMult + mentalChanceMultBonus
				- mentalChanceMultPenalty;
	}

	/**
	 * @param vitality
	 *            the vitality to set
	 */
	public void setVitality(int vitality) {
		this.vitality = vitality;
	}

	/**
	 * @param strength
	 *            the strength to set
	 */
	public void setStrength(int strength) {
		this.strength = strength;
	}

	/**
	 * @param dexterity
	 *            the dexterity to set
	 */
	public void setDexterity(int dexterity) {
		this.dexterity = dexterity;
	}

	/**
	 * @param intellect
	 *            the intellect to set
	 */
	public void setIntellect(int intellect) {
		this.intellect = intellect;
	}

	/**
	 * @param spirit
	 *            the spirit to set
	 */
	public void setSpirit(int spirit) {
		this.spirit = spirit;
	}

	/**
	 * @param luck
	 *            the luck to set
	 */
	public void setLuck(int luck) {
		this.luck = luck;
	}

	/**
	 * @return the effects
	 */
	public List<Effect> getEffects() {
		return effects;
	}

	/**
	 * An enumerator to store equipment array indexes
	 * 
	 * @author c-e-r
	 *
	 */
	public enum EquipmentIndex {
		HEAD(0), BODY(1), LEFT_HELD(2), RIGHT_HELD(3), LEFT_HAND(4), RIGHT_HAND(
				5), NECK(6), FEET(7);

		private final int index;

		EquipmentIndex(int index) {
			this.index = index;
		}

		public int getValue() {
			return index;
		}

	}

	/**
	 * Returns true if the entity is at full health
	 * 
	 * @return if the entity is at full health
	 */
	public boolean isAtFullHealth() {
		return currentHealth == getEffectiveMaxHealth();
	}

	/**
	 * Returns a string of the entity's stats
	 * 
	 * @return
	 */
	public String getStatus() {
		StringBuilder builder = new StringBuilder();
		builder.append("You\n");
		builder.append(currentHealth);
		builder.append("/");
		builder.append(maxHealth);
		builder.append("\n");
		builder.append(currentStamina);
		builder.append("/");
		builder.append(maxStamina);
		builder.append("\n");
		builder.append(currentMana);
		builder.append("/");
		builder.append(maxMana);
		builder.append("\n");
		builder.append("Vitality: ");
		builder.append(getEffectiveVitality());
		builder.append("\n");
		builder.append("Strength: ");
		builder.append(getEffectiveStrength());
		builder.append("\n");
		builder.append("Dexterity: ");
		builder.append(getEffectiveDexterity());
		builder.append("\n");
		builder.append("Intellect: ");
		builder.append(getEffectiveIntellect());
		builder.append("\n");
		builder.append("Spirit: ");
		builder.append(getEffectiveSpirit());
		builder.append("\n");
		builder.append("Luck: ");
		builder.append(getEffectiveLuck());
		builder.append("\n");
		builder.append("Hit: ");
		builder.append(getEffectiveHit());
		builder.append("\n");
		builder.append("Dodge: ");
		builder.append(getEffectiveDodge());
		builder.append("\n");
		builder.append("Speed: ");
		builder.append(getEffectiveSpeed());
		builder.append("\n");

		builder.append("\n");

		builder.append("Status Effects:\n");
		for (Effect effect : effects) {
			builder.append(effect.getInfo());
			builder.append("\n");

		}
		builder.append("\n");

		for (Effect permanantEffect : permanantEffects) {
			builder.append(permanantEffect.getInfo());
			builder.append("\n");

		}

		return builder.toString();
	}
}
