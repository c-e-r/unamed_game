/**
 * 
 */
package unamedGame.entities;

import java.util.ArrayList;
import java.util.List;
import java.util.Observable;

import unamedGame.Dice;
import unamedGame.Game;
import unamedGame.effects.Effect;
import unamedGame.items.Item;
import unamedGame.skills.Skill;
import unamedGame.spells.Spell;
import unamedGame.ui.Window;
import unamedGame.util.Colors;

/**
 * @author c-e-r
 *
 */
public class Entity extends Observable {

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
	protected int vitalityMod;
	protected int strength;
	protected int strengthMod;
	protected int dexterity;
	protected int dexterityMod;
	protected int intellect;
	protected int intellectMod;
	protected int spirit;
	protected int spiritMod;
	protected int luck;
	protected int luckMod;

	protected int maxHealth;
	protected int maxHealthMod;
	protected int maxStamina;
	protected int maxStaminaMod;
	protected int maxMana;
	protected int maxManaMod;

	protected int speed;
	protected int speedMod;
	protected int dodge;
	protected int dodgeMod;
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

	protected int slashingReductionMod;
	protected double slashingResistanceMod;
	protected int piercingReductionMod;
	protected double piercingResistanceMod;
	protected int bludgeoningReductionMod;
	protected double bludgeoningResistanceMod;
	protected int fireReductionMod;
	protected double fireResistanceMod;
	protected int coldReductionMod;
	protected double coldResistanceMod;
	protected int electricityReductionMod;
	protected double electricityResistanceMod;
	protected int sacredReductionMod;
	protected double sacredResistanceMod;
	protected int profaneReductionMod;
	protected double profaneResistanceMod;
	protected int poisonReductionMod;
	protected double poisonResistanceMod;

	protected int mentalResistance;
	protected int mentalResistanceMod;
	protected int baseMentalResistance = 20;
	protected int physicalResistance;
	protected int physicalResistanceMod;
	protected int basePhysicalResistance = 20;

	protected int mentalChanceMult = 1;
	protected int physicalChanceMult = 1;

	protected int hit;
	protected int hitMod;
	protected double hitMult = 1;
	protected int damageMod;
	protected double damageMult = 1;
	protected int healMod;
	protected double healMult = 1;
	protected int effectMod;
	protected double effectMult = 1;

	protected int carryCapacity;
	protected int carryCapacityMod;

	protected int currentHealth;
	protected int currentStamina;
	protected int currentMana;

	protected int innateWeaponDamage;
	protected int innateWeaponVariableDamage = 1;
	protected int innateWeaponHitChance;
	protected int innateWeaponSpeed;
	protected String innateWeaponDamageType;

	protected String innateWeaponHitDescription;
	protected String innateWeaponMissDescription;
	protected String innatePlayerWeaponHitDescription;
	protected String innatePlayerWeaponMissDescription;

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
		physicalResistance = vitality * 2 + strength * 1 + basePhysicalResistance;
		hit = dexterity * 5 + intellect * 2;
		carryCapacity = strength * 5;

	}

	/**
	 * Calculates the total armor of equipped items and returns it
	 * 
	 * @return the total armor of all equipped items
	 */
	public void calculateEquipmentDefences() {

		for (Item item : equipment) {
			if (item != null) {
				piercingReductionMod += item.getPiercingReduction();
				slashingReductionMod += item.getSlashingReduction();
				bludgeoningReductionMod += item.getBludgeoningReduction();
				fireReductionMod += item.getFireReduction();
				coldReductionMod += item.getColdReduction();
				electricityReductionMod += item.getElectricityReduction();
				sacredReductionMod += item.getSacredReduction();
				profaneReductionMod += item.getProfaneReduction();
				poisonReductionMod += item.getPoisonReduction();
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

		}
		return null;
	}

	/**
	 * Recieve a standard attack from the attacker
	 * 
	 * @param attacker
	 */
	public void getAttacked(Entity attacker) {
		boolean attackHit = false;
		Item weapon = attacker.getMainWeapon();

		triggerEffects("attacked");

		int weaponBaseDamage = 0;
		int weaponVariableDamage = 1;
		int weaponHitChance = 0;
		String weaponDamageType = "null";
		String weaponAttackHitDescription;
		String weaponAttackMissDescription;
		String playerWeaponAttackHitDescription;
		String playerWeaponAttackMissDescription;

		// Set weapon variables to weapon if it exists and innate weapon if not
		if (weapon == null) {
			weaponBaseDamage = attacker.innateWeaponDamage;
			weaponHitChance = attacker.innateWeaponHitChance;
			weaponVariableDamage = attacker.innateWeaponVariableDamage;
			weaponDamageType = attacker.innateWeaponDamageType;
			weaponAttackHitDescription = attacker.innateWeaponHitDescription;
			weaponAttackMissDescription = attacker.innateWeaponMissDescription;
			playerWeaponAttackHitDescription = attacker.innatePlayerWeaponHitDescription;
			playerWeaponAttackMissDescription = attacker.innatePlayerWeaponMissDescription;

		} else {
			weaponBaseDamage = weapon.getWeaponBaseDamage();
			weaponHitChance = weapon.getWeaponHitChance();
			weaponVariableDamage = weapon.getWeaponVariableDamage();
			weaponDamageType = weapon.getDamageType();
			weaponAttackHitDescription = weapon.getAttackHitDescription();
			weaponAttackMissDescription = weapon.getAttackMissDescription();
			playerWeaponAttackHitDescription = weapon.getPlayerAttackHitDescription();
			playerWeaponAttackMissDescription = weapon.getPlayerAttackMissDescription();
		}
		String[] description = null;

		int damage = (int) (((attacker.getEffectiveStrength() + weaponBaseDamage + Dice.roll(weaponVariableDamage)
				+ attacker.getDamageMod()) * attacker.getDamageMult()));
		// Prevent damage from going below 0
		if (damage < 0) {
			damage = 0;
		}

		int temp = Dice.roll(Dice.HIT_DIE);

		if (attacker.getEffectiveHit() + weaponHitChance + temp >= this.getEffectiveDodge()) {
			attackHit = true;
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
				Window.addToPane(Window.getInstance().getTextPane(), attacker.getUseName());
				break;
			case "userNameCapital":
				Window.addToPane(Window.getInstance().getTextPane(), Game.capitalizeFirstLetter(attacker.getUseName()));
				break;
			case "targetName":
				Window.addToPane(Window.getInstance().getTextPane(), this.getUseName());
				break;
			case "targetNameCapital":
				Window.addToPane(Window.getInstance().getTextPane(), Game.capitalizeFirstLetter(this.getUseName()));
				break;
			case "damage":
				if (damage == 0) {
					Window.addToPane(Window.getInstance().getTextPane(), Integer.toString(damage), Colors.DAMAGE_BLOCK);
				} else {
					Window.addToPane(Window.getInstance().getTextPane(), Integer.toString(damage), Colors.DAMAGE);
				}
				break;
			case "weaponName":
				Window.addToPane(Window.getInstance().getTextPane(), Game.capitalizeFirstLetter(weapon.getName()));
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
		target.getAttackedBySkill(combinedSkills.get(skillIndex), this);
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
	public void getAttackedBySkill(Skill skill, Entity attacker) {
		Item weapon = attacker.getMainWeapon();
		boolean attackHit = false;

		int weaponBaseDamage = 0;
		int weaponVariableDamage = 1;
		int weaponHitChance = 0;
		String weaponDamageType = "null";

		// Get weapon information of weapon if a weapon exists. Otherwise get innate
		// weapon stats
		if (weapon == null) {
			weaponBaseDamage = attacker.innateWeaponDamage;
			weaponHitChance = attacker.innateWeaponHitChance;
			weaponVariableDamage = attacker.innateWeaponVariableDamage;
			weaponDamageType = attacker.innateWeaponDamageType;

		} else {
			weaponBaseDamage = weapon.getWeaponBaseDamage();
			weaponHitChance = weapon.getWeaponHitChance();
			weaponVariableDamage = weapon.getWeaponVariableDamage();
			weaponDamageType = weapon.getDamageType();
		}
		String[] description = null;

		int damage = (int) (((attacker.getEffectiveStrength() + weaponBaseDamage + Dice.roll(weaponVariableDamage)
				+ attacker.getDamageMod() + skill.getAttackDamageBonus()
				+ Dice.roll(skill.getAttackVariableDamageBonus()))
				* (attacker.getDamageMult() + skill.getAttackDamageMult())));
		// Prevent damage from going below 0
		if (damage < 0) {
			damage = 0;
		}

		int temp = Dice.roll(Dice.HIT_DIE);

		if (skill.isAttack()) {
			if (attacker.getEffectiveHit() + weaponHitChance + skill.getAttackHitBonus() + temp >= this
					.getEffectiveDodge()) {
				attackHit = true;
				damage = this.takeDamage(damage, weaponDamageType);

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
		} else {
			if (attacker instanceof Player) {
				description = skill.getPlayerAttackDescription().split("#");

			} else {
				description = skill.getAttackDescription().split("#");
			}
		}
		// Replace keywords in description with variables
		for (String string : description) {
			switch (string) {
			case "userName":
				Window.addToPane(Window.getInstance().getTextPane(), attacker.getUseName());
				break;
			case "userNameCapital":
				Window.addToPane(Window.getInstance().getTextPane(), Game.capitalizeFirstLetter(attacker.getUseName()));
				break;
			case "targetName":
				Window.addToPane(Window.getInstance().getTextPane(), this.getUseName());
				break;
			case "targetNameCapital":
				Window.addToPane(Window.getInstance().getTextPane(), Game.capitalizeFirstLetter(this.getUseName()));
				break;
			case "damage":
				if (damage == 0) {
					Window.addToPane(Window.getInstance().getTextPane(), Integer.toString(damage), Colors.DAMAGE_BLOCK);
				} else {
					Window.addToPane(Window.getInstance().getTextPane(), Integer.toString(damage), Colors.DAMAGE);
				}
				break;
			case "weaponName":
				Window.addToPane(Window.getInstance().getTextPane(), Game.capitalizeFirstLetter(weapon.getName()));
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
					getAttackedBySkill(subSkill, attacker);
				}

			}
		} else { // Apply skills child skills that happen on miss
			for (Skill subSkill : skill.getMissSkills()) {
				if (skill != null) {
					getAttackedBySkill(subSkill, attacker);
				}

			}
		}
		// Apply skills child skills that happen always
		for (Skill subSkill : skill.getAlwaysSkills()) {
			if (skill != null) {
				getAttackedBySkill(subSkill, attacker);
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

		// Get weapon information of weapon if a weapon exists. Otherwise get innate
		// weapon stats

		String[] description = null;

		int temp = Dice.roll(Dice.HIT_DIE);

		if (spell.isAttack()) {
			if (attacker.getEffectiveHit() + spellFocusHitChance + spell.getAttackHitBonus() + temp >= this
					.getEffectiveDodge()) {
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
				Window.addToPane(Window.getInstance().getTextPane(), attacker.getUseName());
				break;
			case "userNameCapital":
				Window.addToPane(Window.getInstance().getTextPane(), Game.capitalizeFirstLetter(attacker.getUseName()));
				break;
			case "targetName":
				Window.addToPane(Window.getInstance().getTextPane(), this.getUseName());
				break;
			case "targetNameCapital":
				Window.addToPane(Window.getInstance().getTextPane(), Game.capitalizeFirstLetter(this.getUseName()));
				break;
			case "spellFocusName":
				Window.addToPane(Window.getInstance().getTextPane(), Game.capitalizeFirstLetter(spellFocus.getName()));
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
			return (int) ((damage - slashingReduction - slashingReductionMod) * (slashingResistance + slashingResistanceMod));
		case "piercing":
			return (int) ((damage - piercingReduction - piercingReductionMod) * (piercingResistance + piercingResistanceMod));
		case "bludgeoning":
			return (int) ((damage - bludgeoningReduction - bludgeoningReductionMod) * (bludgeoningResistance + bludgeoningResistanceMod));
		case "fire":
			return (int) ((damage - fireReduction - fireReductionMod) * (fireResistance + fireResistanceMod));
		case "cold":
			return (int) ((damage - coldReduction - coldReductionMod) * (coldResistance + coldResistanceMod));
		case "electricity":
			return (int) ((damage - electricityReduction - electricityReductionMod) * (electricityResistance + electricityResistanceMod));
		case "sacred":
			return (int) ((damage - sacredReduction - sacredReductionMod) * (sacredResistance + sacredResistanceMod));
		case "profane":
			return (int) ((damage - profaneReduction - profaneReductionMod) * (profaneResistance + profaneResistanceMod));
		case "poison":
			return (int) ((damage - poisonReduction - poisonReductionMod) * (poisonResistance + poisonResistanceMod));
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
		target.getAttacked(this);

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
			if (modifier > vitalityMod) {
				vitalityMod = modifier;
			}
			break;
		case "strength":
			if (modifier > strengthMod) {
				strengthMod = modifier;
			}
			break;
		case "dexterity":
			if (modifier > dexterityMod) {
				dexterityMod = modifier;
			}
			break;
		case "intellect":
			if (modifier > intellectMod) {
				intellectMod = modifier;
			}
			break;
		case "spirit":
			if (modifier > spiritMod) {
				spiritMod = modifier;
			}
			break;
		case "luck":
			if (modifier > luckMod) {
				luckMod = modifier;
			}
			break;
		case "health":
			if (modifier > maxHealthMod) {
				maxHealthMod += modifier;
			}
			break;
		case "speed":
			if (modifier > speedMod) {
				speedMod = modifier;
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
		case "health":
			maxHealthMod -= modifier;
			break;
		case "speed":
			speedMod -= modifier;
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
		vitalityMod = 0;
		strengthMod = 0;
		dexterityMod = 0;
		intellectMod = 0;
		spiritMod = 0;
		luckMod = 0;
		maxHealthMod = 0;
		maxStaminaMod = 0;
		maxManaMod = 0;
		speedMod = 0;
		hitMod = 0;
		dodgeMod = 0;

		slashingReductionMod = 0;
		slashingResistanceMod = 0;
		piercingReductionMod = 0;
		piercingResistanceMod = 0;
		bludgeoningReductionMod = 0;
		bludgeoningResistanceMod = 0;
		fireReductionMod = 0;
		fireResistanceMod = 0;
		coldReductionMod = 0;
		coldResistanceMod = 0;
		electricityReductionMod = 0;
		electricityResistanceMod = 0;
		sacredReductionMod = 0;
		sacredResistanceMod = 0;
		profaneReductionMod = 0;
		profaneResistanceMod = 0;
		poisonReductionMod = 0;
		poisonResistanceMod = 0;

	}

	/**
	 * Returns effective speed + a roll of the SPEED_DIE + the skill or speed bonus
	 * 
	 * @param command
	 *            the command. 2 for skill and 3 for spell
	 * @param index
	 *            the index of the skill or spell
	 * @return the speed to check
	 */

	public int speedCheck(int command, int index) {

		int speedBonus = 0;
		switch (command) {
		case 2:
			speedBonus = combinedSkills.get(index).getAttackSpeedBonus();
			break;
		case 3:
			speedBonus = spells.get(index).getAttackSpeedBonus();
			break;
		default:
			break;
		}

		return speed + speedMod + speedBonus + Dice.roll(Dice.SPEED_DIE);
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
			if (!effect.getRepeatType().equals("noRepeat")) {
				effect.applyEffect(this);
			}
		}
	}

	/**
	 * Returns true if there is an effect of the same name as the one given in the
	 * entity's effects list
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
		if(damage < 0 ) {
			damage = 0;
		}
		currentHealth -= damage;
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
		return maxHealth + maxHealthMod;
	}

	/**
	 * Returns the effective speed. speed + speedMod
	 * 
	 * @return the effective speed
	 */
	public int getEffectiveSpeed() {
		return speed + speedMod;
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
		return vitality + vitalityMod;
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
		return strength + strengthMod;
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
		return dexterity + dexterityMod;
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
		return intellect + intellectMod;
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
		return spirit + spiritMod;
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
		return luck + luckMod;
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
		return maxStamina + maxStaminaMod;
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
		return maxMana + maxManaMod;
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
		return dodge + dodgeMod;
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
		return (int) ((hit + hitMod) * hitMult);
	}

	/**
	 * Returns the entity's damage stat
	 * 
	 * @return the damage stat
	 */
	public int getDamageMod() {
		return damageMod;
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
	public int getHealMod() {
		return healMod;
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
	public int getCarryCapacityMod() {
		return carryCapacityMod;
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
	 * Returns the entity's innate weapon damage
	 * 
	 * @return the innateWeaponDamage
	 */
	public int getInnateWeaponDamage() {
		return innateWeaponDamage;
	}

	/**
	 * Returns the entity's innate weapon hit change
	 * 
	 * @return
	 */
	public int getInnateWeaponHitChance() {
		return innateWeaponHitChance;
	}

	/**
	 * Returns the entity's innate weapon speed
	 * 
	 * @return the inanteWeaponSpeed
	 */
	public int getInnateWeaponSpeed() {
		return innateWeaponSpeed;
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
		return mentalResistance + mentalResistanceMod;
	}

	/**
	 * Returns the entity's effective physical resistance
	 * 
	 * @return the effective physical resistance
	 */
	public int getEffectivePhysicalResistance() {
		return physicalResistance + physicalResistanceMod;
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
	 * Returns the entity's physical effect chance multiplier
	 * 
	 * @return the physicalChanceMult
	 */
	public int getPhysicalChanceMult() {
		return physicalChanceMult;
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
		HEAD(0), BODY(1), LEFT_HELD(2), RIGHT_HELD(3), LEFT_HAND(4), RIGHT_HAND(5), NECK(6), FEET(7);

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
