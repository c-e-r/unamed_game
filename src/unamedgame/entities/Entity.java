/**
 * 
 */
package unamedgame.entities;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.ListIterator;
import java.util.Observable;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import unamedgame.Calculate;
import unamedgame.Dice;
import unamedgame.Game;
import unamedgame.effects.Effect;
import unamedgame.items.Item;
import unamedgame.skills.Skill;
import unamedgame.spells.Spell;
import unamedgame.time.Time;
import unamedgame.ui.Window;
import unamedgame.util.Colors;

/**
 * A class to act as a general living thing.
 * 
 * @author c-e-r
 * @version 0.0.1
 */
public class Entity extends Observable implements Serializable {

    private static final long serialVersionUID = -7904979206847101273L;

    private static final Logger LOG = LogManager.getLogger(Game.class);

    protected List<EntityListener> entityListeners = new ArrayList<EntityListener>();

    protected Item[] equipment;
    protected HashMap<String, Integer> flags;

    protected List<Item> inventory;
    protected int currency;

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

    protected double mentalChanceMult = 1;
    protected double mentalChanceMultBonus;
    protected double mentalChanceMultPenalty;
    protected double physicalChanceMult = 1;
    protected double physicalChanceMultBonus;
    protected double physicalChanceMultPenalty;

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
     * Restores health to the Entity.
     * 
     * @param heal
     *            the amount of health to restore
     * @return the amount of health restored
     */
    public int restoreHealth(int heal) {
        if (currentHealth + heal > maxHealth) {
            heal = maxHealth - currentHealth;
        }
        if (heal > 0) {
            triggerEffects("health_restored_before");
        }
        currentHealth += heal;
        checkHealth();
        if (heal > 0) {
            triggerEffects("health_restored_after");
        }
        return heal;
    }

    /**
     * Calculates derived stats.
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
     * Calculates the total armor of equipped items and returns it.
     * 
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
     * Returns the main hand weapon.
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
     * Returns the off-hand hand weapon.
     * 
     * @return the off-hand hand weapon
     */
    public Item getOffhandWeapon() {

        if (equipment[EquipmentIndex.LEFT_HELD.getValue()] != null) {
            return equipment[EquipmentIndex.LEFT_HELD.getValue()];
        }
        return null;
    }

    /**
     * Returns true if the enemy is wielding the same item in both hands.
     * 
     * @return if dual wielding
     */
    public boolean isWieldingTwoHanded() {
        return getMainWeapon() == getOffhandWeapon();
    }

    /**
     * Receive a standard attack from the attacker.
     * 
     * @param attacker
     */
    public void getAttacked(Entity attacker, boolean usingOffhandWeapon) {
        attacker.triggerEffects("attack");
        attacker.triggerEffects("all_attack");
        triggerEffects("attacked");
        triggerEffects("all_attacked");

        boolean attackHit = false;
        Item weapon;

        if (usingOffhandWeapon) {
            weapon = attacker.getOffhandWeapon();
        } else {
            weapon = attacker.getMainWeapon();
        }

        int weaponHitChance = 0;
        String weaponDamageType = "null";
        String weaponAttackHitDescription;
        String weaponAttackMissDescription;
        String playerWeaponAttackHitDescription;
        String playerWeaponAttackMissDescription;

        weapon.getWeaponBaseDamage();
        weaponHitChance = weapon.getWeaponHitChance();
        weapon.getWeaponVariableDamage();
        weaponDamageType = weapon.getDamageType();
        weaponAttackHitDescription = weapon.getAttackHitDescription();
        weaponAttackMissDescription = weapon.getAttackMissDescription();
        playerWeaponAttackHitDescription = weapon
                .getPlayerAttackHitDescription();
        playerWeaponAttackMissDescription = weapon
                .getPlayerAttackMissDescription();

        String description = null;

        int damage = 0;

        attackHit = Calculate.calculateAttackHitChance(attacker,
                weaponHitChance) >= this.getEffectiveDodge();
        if (attackHit) {
            damage = Calculate.calculateAttackDamage(attacker, weapon,
                    usingOffhandWeapon);
            damage = this.takeDamage(damage, weaponDamageType);

            if (attacker instanceof Player) {
                description = playerWeaponAttackHitDescription;
            } else {
                description = weaponAttackHitDescription;
            }

        } else {

            if (attacker instanceof Player) {
                description = playerWeaponAttackMissDescription;
            } else {
                description = weaponAttackMissDescription;
            }

        }
        // Replace the placeholder words with variables
        printAttackDescription(description, attacker, damage, weapon);

        if (attackHit) {
            attacker.triggerEffects("attack_hit");
            attacker.triggerEffects("all_attack_hit");
            triggerEffects("attacked_hit");
            triggerEffects("all_attacked_hit");
        } else {
            attacker.triggerEffects("attack_miss");
            attacker.triggerEffects("all_attack_miss");
            triggerEffects("attacked_miss");
            triggerEffects("all_attacked_miss");
        }


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
        if (attackHit) {
            attacker.triggerEffects("attack_after_hit");
            attacker.triggerEffects("all_attack_after_hit");
            triggerEffects("attacked_after_hit");
            triggerEffects("all_attacked_after_hit");
        }
        attacker.triggerEffects("attack_after");
        attacker.triggerEffects("all_attack_after");
        triggerEffects("attacked_after");
        triggerEffects("all_attacked_after");
    }

    /**
     * Use a skill on the given enemy.
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

    public void printAttackDescription(String description, Entity attacker,
            int damage, Item weapon) {

        for (String string : description.split("#")) {
            switch (string) {
            case "userName":
                Window.appendText(attacker.getUseName());
                break;
            case "userNameCapital":
                Window.appendText(
                        Game.capitalizeFirstLetter(attacker.getUseName()));
                break;
            case "targetName":
                Window.appendText(this.getUseName());
                break;
            case "targetNameCapital":
                Window.appendText(
                        Game.capitalizeFirstLetter(this.getUseName()));
                break;
            case "damage":
                if (damage == 0) {
                    Window.appendText(Integer.toString(damage),
                            Colors.DAMAGE_BLOCK);
                } else {
                    Window.appendText(Integer.toString(damage), Colors.DAMAGE);
                }
                break;
            case "weaponName":
                Window.appendText(weapon.getName());
                break;
            case "weaponNameCapital":
                Window.appendText(Game.capitalizeFirstLetter(weapon.getName()));
                break;
            default:
                Window.appendText(string);
                break;
            }
        }
        Window.appendText("\n");

    }

    /**
     * Use a spell on the given enemy.
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
     * Trigger self-destruct or special effects on effects watching this entity.
     * 
     * @param reason
     *            the reason effects are being triggered
     */
    public void triggerEffects(String reason) {
        for (Iterator<EntityListener> it = entityListeners.iterator(); it
                .hasNext();) {
            EntityListener listener = it.next();
            listener.entityEvent(reason);
            if (listener.getDelete()) {
                it.remove();
            }

        }
    }

    /**
     * Get attacked by a skill.
     * 
     * @param skill
     *            the skill to be hit by
     * @param attacker
     *            the attacker
     * @param usingOffhandWeapon
     *            if the attack is using the offhand weapon
     */
    public void getAttackedBySkill(Skill skill, Entity attacker,
            boolean usingOffhandWeapon) {
        attacker.triggerEffects("skill_attack");
        attacker.triggerEffects("all_attack");
        triggerEffects("skill_attacked");
        triggerEffects("all_attacked");
        Item weapon = attacker.getMainWeapon();
        boolean attackHit = true;

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

        weapon.getWeaponBaseDamage();
        weaponHitChance = weapon.getWeaponHitChance();
        weapon.getWeaponVariableDamage();
        weaponDamageType = weapon.getDamageType();

        String description = null;

        int damage = 0;
        // Prevent damage from going below 0

        if (skill.isAttack()) {
            attackHit = Calculate.calculateSkillAttackHitChance(attacker, skill,
                    weaponHitChance) >= this.getEffectiveDodge();
            if (attackHit) {
                damage = Calculate.calculateSkillAttackDamage(attacker, skill,
                        weapon, usingOffhandWeapon);
                damage = this.takeDamage(damage, weaponDamageType);

            }
        }
        if (attackHit) {
            if (attacker instanceof Player) {
                description = skill.getPlayerAttackDescription();
            } else {
                description = skill.getAttackDescription();
            }

        } else {

            if (attacker instanceof Player) {
                description = skill.getPlayerMissDescription();
            } else {
                description = skill.getMissDescription();
            }
        }

        // Replace keywords in description with variables
        printAttackDescription(description, attacker, damage, weapon);
        if (attackHit) {
            attacker.triggerEffects("skill_attack_hit");
            attacker.triggerEffects("all_attack_hit");
            triggerEffects("skill_attacked_hit");
            triggerEffects("all_attacked_hit");
        } else {
            attacker.triggerEffects("skill_attack_miss");
            attacker.triggerEffects("all_attack_miss");
            triggerEffects("skill_attacked_miss");
            triggerEffects("all_attacked_miss");
        }
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
        if (attackHit) {
            attacker.triggerEffects("skill_attack_after_hit");
            attacker.triggerEffects("all_attack_after_hit");
            triggerEffects("skill_attacked_after_hit");
            triggerEffects("all_attacked_after_hit");
        }
        attacker.triggerEffects("skill_attack_after");
        attacker.triggerEffects("all_attack_after");
        triggerEffects("skill_attacked_after");
        triggerEffects("all_attacked_after");

    }

    /**
     * Get attacked by a spell.
     * 
     * @param spell
     *            the spell to be hit by
     * @param attacker
     *            the attacker
     */
    public void getAttackedBySpell(Spell spell, Entity attacker) {
        attacker.triggerEffects("spell_attack");
        attacker.triggerEffects("all_attack");
        triggerEffects("spell_attacked");
        triggerEffects("all_attacked");
        Item spellFocus = attacker.getMainWeapon();

        int spellFocusHitChance = 0;

        if (spellFocus != null) {
            if (spellFocus.isSpellFocus()) {
                spellFocusHitChance = spellFocus.getWeaponHitChance();
            } else {
                spellFocus = null;
            }
        }

        boolean spellHit = true;

        // Get weapon information of weapon if a weapon exists. Otherwise get
        // innate
        // weapon stats

        String description = null;

        Dice.roll(Dice.HIT_DIE);

        if (spell.isAttack()) {
            spellHit = Calculate.calculateSpellAttackHitChance(attacker, spell,
                    spellFocusHitChance) >= this.getEffectiveDodge();
            spellHit = true;

        }
        if (spellHit) {
            
            if (attacker instanceof Player) {
                description = spell.getPlayerAttackDescription();
            } else {
                description = spell.getAttackDescription();
            }

        } else {
           
            if (attacker instanceof Player) {
                description = spell.getPlayerMissDescription();
            } else {
                description = spell.getMissDescription();
            }

        }
        // Replace keywords in description with variables
        printAttackDescription(description, attacker, 0, spellFocus);
        if(spellHit) {
            attacker.triggerEffects("spell_attack_hit");
            attacker.triggerEffects("all_attack_hit");
            triggerEffects("spell_attacked_hit");
            triggerEffects("all_attacked_hit");
        } else {
            attacker.triggerEffects("spell_attack_miss");
            attacker.triggerEffects("all_attack_miss");
            triggerEffects("spell_attacked_miss");
            triggerEffects("all_attacked_miss");
        }
        // Apply spell attack effects if weapon attack hit
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

        if (spellHit) {
            attacker.triggerEffects("spell_attack_after_hit");
            attacker.triggerEffects("all_attack_after_hit");
            triggerEffects("spell_attacked_after_hit");
            triggerEffects("all_attacked_after_hit");
        }
        attacker.triggerEffects("spell_attack_after_hit");
        attacker.triggerEffects("all_attack_after_hit");
        triggerEffects("spell_attacked_after");
        triggerEffects("all_attacked_after");
    }

    /**
     * Apply resistances to the given damage value based on the damage type.
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
     * Attack the given target.
     * 
     * @param target
     *            the target to attack
     */
    public void attack(Entity target) {
        int temp = getEffectiveSpeed();
        target.getAttacked(this, false);

        if (temp >= 10 && getOffhandWeapon() != null
                && !isWieldingTwoHanded()) {
            target.getAttacked(this, true);
        }
        if (temp >= 20) {
            target.getAttacked(this, false);
        }
        if (temp >= 30 && getOffhandWeapon() != null
                && !isWieldingTwoHanded()) {
            target.getAttacked(this, true);
        }
        if (temp >= 40) {
            target.getAttacked(this, false);
        }

    }

    /**
     * Apply effects of all permanent status effects.
     */
    public void applyPermanantStatusEffects() {
        for (Effect effect : permanantEffects) {
            effect.applyEffect(this);
        }
    }

    /**
     * Apply effects of all equipment effects.
     */
    public void applyEquipmentEffects() {
        for (Effect effect : equipmentEffects) {
            effect.applyEffect(this);
        }
    }

    /**
     * Checks if an effect of that name already exists in the effects List.
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
     * Increases the given stat by the given modifier.
     * 
     * @param stat
     *            the stat to increases as a string
     * @param modifier
     *            the number to increase the stat by
     */
    public void increaseModifier(String stat, int modifier) {
        switch (stat) {
        case "vitality":
            triggerEffects("vitality_increased_before");
            vitalityBonus += modifier;
            triggerEffects("vitality_increased_after");
            break;
        case "strength":
            triggerEffects("strength_increased_before");
            strengthBonus += modifier;
            triggerEffects("strength_increased_after");
            break;
        case "dexterity":
            triggerEffects("dexterity_increased_before");
            dexterityBonus += modifier;
            triggerEffects("dexterity_increased_after");
            break;
        case "intellect":
            triggerEffects("intellect_increased_before");
            intellectBonus += modifier;
            triggerEffects("intellect_increased_after");
            break;
        case "spirit":
            triggerEffects("spirit_increased_before");
            spiritBonus += modifier;
            triggerEffects("spirit_increased_after");
            break;
        case "luck":
            triggerEffects("luck_increased_before");
            luckBonus += modifier;
            triggerEffects("luck_increased_after");
            break;
        case "health":
            maxHealthBonus += modifier;
            break;
        case "stamina":
            maxStaminaBonus += modifier;
            break;
        case "mana":
            maxManaBonus += modifier;
            break;
        case "speed":
            speedBonus += modifier;
            break;
        case "hit":
            triggerEffects("hit_increased_before");
            hitBonus += modifier;
            triggerEffects("hit_increased_after");
            break;
        case "dodge":
            triggerEffects("dodge_increased_before");
            dodgeBonus += modifier;
            triggerEffects("dodge_increased_after");
            break;
        case "slashingReduction":
            slashingReductionBonus += modifier;
            break;
        case "piercingReduction":
            piercingReductionBonus += modifier;
            break;
        case "bludgeoningReduction":
            bludgeoningReductionBonus += modifier;
            break;
        case "fireReduction":
            fireReductionBonus += modifier;
            break;
        case "coldReduction":
            coldReductionBonus += modifier;
            break;
        case "electricityReduction":
            electricityReductionBonus += modifier;
            break;
        case "sacredReduction":
            sacredReductionBonus += modifier;
            break;
        case "profaneReduction":
            profaneReductionBonus += modifier;
            break;
        case "poisonReduction":
            poisonReductionBonus += modifier;
            break;
        case "slashingResistance":
            slashingResistanceBonus += modifier;
            break;
        case "piercingResistance":
            piercingResistanceBonus += modifier;
            break;
        case "bludgeoningResistance":
            bludgeoningResistanceBonus += modifier;
            break;
        case "fireResistance":
            fireResistanceBonus += modifier;
            break;
        case "coldResitance":
            coldResistanceBonus += modifier;
            break;
        case "electricityResistance":
            electricityResistanceBonus += modifier;
            break;
        case "sacredResistance":
            sacredResistanceBonus += modifier;
            break;
        case "profaneResistance":
            profaneResistanceBonus += modifier;
            break;
        case "poisonResistance":
            poisonResistanceBonus += modifier;
            break;
        case "hitMult":
            hitMultBonus += modifier;
            break;
        case "damageMult":
            damageMultBonus += modifier;
            break;
        case "healMult":
            healMultBonus += modifier;
            break;
        case "carryCapacitys":
            carryCapacityBonus += modifier;
            break;
        case "mentalChanceMult":
            mentalChanceMultBonus += modifier;
            break;
        case "physicalChanceMult":
            physicalChanceMultBonus += modifier;
            break;
        case "mentalResistance":
            mentalResistanceBonus += modifier;
            break;
        case "physicalResistance":
            physicalResistanceBonus += modifier;
            break;
        default:
            break;
        }
    }

    /**
     * Decreases the given stat by the given modifier.
     * 
     * @param stat
     *            the stat to decrease as a string
     * @param modifier
     *            the number to decrease the stat by
     */
    public void decreaseModifier(String stat, int modifier) {
        switch (stat) {
        case "vitality":
            vitalityPenalty += modifier;
            break;
        case "strength":
            strengthPenalty += modifier;
            break;
        case "dexterity":
            dexterityPenalty += modifier;
            break;
        case "intellect":
            intellectPenalty += modifier;
            break;
        case "spirit":
            spiritPenalty += modifier;
            break;
        case "luck":
            luckPenalty += modifier;
            break;
        case "health":
            maxHealthPenalty += modifier;
            break;
        case "stamina":
            maxStaminaPenalty += modifier;
            break;
        case "mana":
            maxManaPenalty += modifier;
            break;
        case "speed":
            speedPenalty += modifier;
            break;
        case "hit":
            hitPenalty = modifier;
            break;
        case "dodge":
            dodgePenalty = modifier;
            break;
        case "slashingReduction":
            slashingReductionPenalty += modifier;
            break;
        case "piercingReduction":
            piercingReductionPenalty += modifier;
            break;
        case "bludgeoningReduction":
            bludgeoningReductionPenalty += modifier;
            break;
        case "fireReduction":
            fireReductionPenalty += modifier;
            break;
        case "coldReduction":
            coldReductionPenalty += modifier;
            break;
        case "electricityReduction":
            electricityReductionPenalty += modifier;
            break;
        case "sacredReduction":
            sacredReductionPenalty += modifier;
            break;
        case "profaneReduction":
            profaneReductionPenalty += modifier;
            break;
        case "poisonReduction":
            poisonReductionPenalty += modifier;
            break;
        case "slashingResistance":
            slashingResistancePenalty += modifier;
            break;
        case "piercingResistance":
            piercingResistancePenalty += modifier;
            break;
        case "bludgeoningResistance":
            bludgeoningResistancePenalty += modifier;
            break;
        case "fireResistance":
            fireResistancePenalty += modifier;
            break;
        case "coldResitance":
            coldResistancePenalty += modifier;
            break;
        case "electricityResistance":
            electricityResistancePenalty += modifier;
            break;
        case "sacredResistance":
            sacredResistancePenalty += modifier;
            break;
        case "profaneResistance":
            profaneResistancePenalty += modifier;
            break;
        case "poisonResistance":
            poisonResistancePenalty += modifier;
            break;
        case "hitMult":
            hitMultPenalty += modifier;
            break;
        case "damageMult":
            damageMultPenalty += modifier;
            break;
        case "healMult":
            healMultPenalty += modifier;
            break;
        case "carryCapacity":
            carryCapacityPenalty += modifier;
            break;
        case "mentalChanceMult":
            mentalChanceMultPenalty += modifier;
            break;
        case "physicalChanceMult":
            physicalChanceMultPenalty += modifier;
            break;
        case "mentalResistance":
            mentalResistancePenalty += modifier;
            break;
        case "physicalResistance":
            physicalResistancePenalty += modifier;
            break;
        default:
            break;
        }

    }

    /**
     * Check if current health is above the maximum health and sets it to the
     * maximum if it is.
     */
    public void checkHealth() {
        if (currentHealth > maxHealth) {
            currentHealth = maxHealth;
        }
    }

    /**
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
     * bonus.
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
        int speedDie = Dice.roll(Dice.SPEED_DIE);
        LOG.debug(
                "speed + speedBonus - equipSpeedPenalty + actionBonus + speedDie[D10]");
        StringBuilder builder = new StringBuilder();
        builder.append("Speed roll: ");
        builder.append(speed);
        builder.append(" + ");
        builder.append(speedBonus);
        builder.append(" - ");
        builder.append(speedPenalty);
        builder.append(" + ");
        builder.append(tempSpeedBonus);
        builder.append(" + ");
        builder.append(speedDie);
        builder.append("[1-");
        builder.append(Dice.SPEED_DIE);
        builder.append("]");
        LOG.debug(builder.toString());
        return speed + speedBonus - equipSpeedPenalty + tempSpeedBonus
                + speedDie;
    }

    /**
     * Resets all modifiers, reapplies effects, and recalculates derived stats.
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
     * applies all effects to the player.
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
     * the entity's effects list.
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
     * Removes an effect from the permanant effects array list.
     * 
     * @param effect
     *            the effect to remove
     */
    public void removePermanantEffect(Effect effect) {
        permanantEffects.remove(effect);
        recalculateStats();
    }

    /**
     * Add an effect to the permanent effects array list.
     * 
     * @param effect
     */
    public void addPermanantEffect(Effect effect) {
        permanantEffects.add(effect);
        recalculateStats();
    }

    /**
     * Take an amount of damage of the specified type.
     * 
     * @param damage
     *            the amount of damage to take
     * @param damageType
     *            the type of damage to take
     * @return the amount of damage taken
     */
    public int takeDamage(int damage, String damageType) {
        damage = applyResistances(damage, damageType);
        triggerEffects("damage_taken_before");
        switch (damageType) {
        case "slashing":
            triggerEffects("slashing_damage_taken_before");
            break;
        case "piercing":
            triggerEffects("piercing_damage_taken_before");
            break;
        case "bludgeoning":
            triggerEffects("bludgeoning_damage_taken_before");
            break;
        case "fire":
            triggerEffects("fire_damage_taken_before");
            break;
        case "cold":
            triggerEffects("cold_damage_taken_before");
            break;
        case "lightning":
            triggerEffects("lightning_damage_taken_before");
            break;
        case "sacred":
            triggerEffects("sacred_damage_taken_before");
            break;
        case "profane":
            triggerEffects("profane_damage_taken_before");
            break;
        case "poison":
            triggerEffects("poison_damage_taken_before");
            break;
        case "unresistable":
            triggerEffects("unresistable_damage_taken_before");
            break;
        }
        if (damage < 0) {
            damage = 0;
        }
        currentHealth -= damage;
        LOG.debug(damage + " taken by " + name);

        triggerEffects("damage_taken_after");
        switch (damageType) {
        case "slashing":
            triggerEffects("slashing_damage_taken_after");
            break;
        case "piercing":
            triggerEffects("piercing_damage_taken_after");
            break;
        case "bludgeoning":
            triggerEffects("bludgeoning_damage_taken_after");
            break;
        case "fire":
            triggerEffects("fire_damage_taken_after");
            break;
        case "cold":
            triggerEffects("cold_damage_taken_after");
            break;
        case "lightning":
            triggerEffects("lightning_damage_taken_after");
            break;
        case "sacred":
            triggerEffects("sacred_damage_taken_after");
            break;
        case "profane":
            triggerEffects("profane_damage_taken_after");
            break;
        case "poison":
            triggerEffects("poison_damage_taken_after");
            break;
        case "unresistable":
            triggerEffects("unresistable_damage_taken_after");
            break;
        }
        return damage;
    }

    /**
     * Returns true if current health is less than or equal to zero.
     * 
     * @return if the entity is dead
     */
    public boolean isDead() {
        return currentHealth <= 0;
    }

    /**
     * Returns the current health.
     * 
     * @return the current health
     */
    public int getCurrentHealth() {
        return currentHealth;
    }

    /**
     * recreates the combined skills arrayList from the item and innate skills
     * ArrayLists.
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
     * recreates the spells arrayList from the item and known spells ArrayLists.
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
     * Adds an effect to the effects arrayList.
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
                    if (effect2 != null) {
                        addEffect(effect2, creator);
                    }
                }
            }
        } else {
            updateEffectDuration(effect);

        }

    }

    /**
     * Increases the duration of effects of the same name as the given effect if
     * its lower than the given effect.
     * 
     * @param effect
     *            the effect to update the duration of
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
                        Window.appendText(Game.capitalizeFirstLetter(
                                "You are already under the effects of "
                                        + effect2.getName())
                                + ", duration increased\n");

                    } else {
                        Window.appendText(Game.capitalizeFirstLetter(
                                Game.capitalizeFirstLetter(useName)
                                        + " is already under the effects of "
                                        + effect2.getName())
                                + ", duration increased\n");

                    }

                }
            }
        } else {
            if (this instanceof Player) {
                Window.appendText(Game.capitalizeFirstLetter(
                        "You are already under the effects of "
                                + effect.getName())
                        + "\n");

            } else {
                Window.appendText(Game.capitalizeFirstLetter(
                        Game.capitalizeFirstLetter(useName)
                                + " is already under the effects of "
                                + effect.getName())
                        + "\n");

            }

        }
    }

    /**
     * removes an effect from the effects list.
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
     * adds an item to the inventory.
     * 
     * @param item
     *            the item to add
     * @return the item added
     */
    public Item addItemToInventory(Item item) {
        triggerEffects("item_gained_before");
        inventory.add(item);
        triggerEffects("item_gained_after");
        return item;
    }

    /**
     * adds an effect to the attack effect list.
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
     * Add all equip effects from the given list to the equipmentEffects List.
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
     * equipmentEffects List.
     * 
     * @param list
     *            the List of effects to remove
     */
    public void removeEquipEffects(List<Effect> list) {
        equipmentEffects.removeAll(list);
        recalculateStats();
    }

    /**
     * Returns the item description for the given item index from inventory.
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
     * Returns grammatically correct noun for description display.
     * 
     * @return the use name
     */
    public String getUseName() {
        return useName;
    }

    /**
     * Returns a list of the entity's inventory.
     * 
     * @return the inventory List
     */
    public List<Item> getInventory() {
        return inventory;
    }

    /**
     * Returns the specified inventory item.
     * 
     * @param itemIndex
     *            the index of the item to return
     * @return the specified item
     */
    public Item getInventoryItem(int itemIndex) {
        return inventory.get(itemIndex);
    }

    /**
     * Removes the specified item from inventory.
     * 
     * @param itemIndex
     *            the index of the item to remove
     * @return the item that was removed
     */
    public Item removeItemFromInventory(int itemIndex) {
        return inventory.remove(itemIndex);
    }

    public void removeItemFromInventory(Item item) {
        inventory.remove(item);
    }

    /**
     * Returns the name of the entity.
     * 
     * @return the name of the entity
     */
    public String getName() {
        return name;
    }

    /**
     * Adds the given skill to the entity's innate skill list.
     * 
     * @param skill
     *            the skill to add
     */
    public void addInnateSkill(Skill skill) {
        innateSkills.add(skill);
        reloadSkills();
    }

    /**
     * Adds the given spell to the entity's known spells list.
     * 
     * @param spell
     *            the spell to add
     */
    public void addKnownSpell(Spell spell) {
        knownSpells.add(spell);
        reloadSpells();
    }

    /**
     * Returns the entity's equipment array.
     * 
     * @return the equipment array
     */
    public Item[] getEquipment() {
        return equipment;
    }

    /**
     * Returns the entity's innate skills List.
     * 
     * @return the innateSkills List
     */
    public List<Skill> getInnateSkills() {
        return innateSkills;
    }

    /**
     * Returns the entity's combined skills List.
     * 
     * @return the combinedSkills list
     */
    public List<Skill> getCombinedSkills() {
        reloadSkills();
        return combinedSkills;
    }

    /**
     * Returns the entity's item skills List.
     * 
     * @return the itemSkills List
     */
    public List<Skill> getItemSkills() {
        return itemSkills;
    }

    /**
     * Returns the entity's item spells List.
     * 
     * @return the itemSpells List
     */
    public List<Spell> getItemSpells() {
        return itemSpells;
    }

    /**
     * Returns the entity's spells List.
     * 
     * @return the spells List
     */
    public List<Spell> getSpells() {
        return spells;
    }

    /**
     * Returns the entity's knownSpells List.
     * 
     * @return the knownSpells List
     */
    public List<Spell> getKnownSpells() {
        return knownSpells;
    }

    /**
     * Returns the entity's vitality stat.
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
     * Returns the entity's strength stat.
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
     * Returns the entity's dexterity stat.
     * 
     * @return the dexterity stat
     */
    public int getDexterity() {
        return dexterity;
    }

    /**
     * Returns the entity's effective dexterity stat dexterity + dexterityMod.
     * 
     * @return the effective strength
     */
    public int getEffectiveDexterity() {
        return dexterity + dexterityBonus - dexterityPenalty;
    }

    /**
     * Returns the entity's intellect stat.
     * 
     * @return the intellect stat
     */
    public int getIntellect() {
        return intellect;
    }

    /**
     * Returns the entity's effective intellect stat intellect + intellectMod.
     * 
     * @return the effective intellect
     */
    public int getEffectiveIntellect() {
        return intellect + intellectBonus - intellectPenalty;
    }

    /**
     * Returns the entity's spirit stat.
     * 
     * @return the spirit stat
     */
    public int getSpirit() {
        return spirit;
    }

    /**
     * Returns the entity's effective spirit stat spirit + spiritMod.
     * 
     * @return the effective spirit
     */
    public int getEffectiveSpirit() {
        return spirit + spiritBonus - spiritPenalty;
    }

    /**
     * Returns the entity's luck stat.
     * 
     * @return the luck stat
     */
    public int getLuck() {
        return luck;
    }

    /**
     * Returns the entity's effective luck stat luck + luckMod.
     * 
     * @return the effective luck
     */
    public int getEffectiveLuck() {
        return luck + luckBonus - luckPenalty;
    }

    /**
     * Returns the entity's maximum stamina.
     * 
     * @return the maxStamina
     */
    public int getMaxStamina() {
        return maxStamina;
    }

    /**
     * Returns the entity's effective maximum stamina maxStamina +
     * maxStaminaMod.
     * 
     * @return the effective maximum stamina
     */
    public int getEffectiveMaxStamina() {
        return maxStamina + maxStaminaBonus - maxStaminaPenalty;
    }

    /**
     * Returns the entity's maximum mana.
     * 
     * @return the maxMana
     */
    public int getMaxMana() {
        return maxMana;
    }

    /**
     * Returns the entity's effective maximum mana maxMana + maxManaMod.
     * 
     * @return the effective maximum mana
     */
    public int getEffectiveMaxMana() {
        return maxMana + maxManaBonus - maxManaPenalty;
    }

    /**
     * Returns the entity's dodge stat.
     * 
     * @return the dodge stat
     */
    public int getDodge() {
        return dodge;
    }

    /**
     * Returns the entity's effective dodge stat dodge + dodgeMod.
     * 
     * @return the effective dodge stat
     */
    public int getEffectiveDodge() {
        return dodge + dodgeBonus - dodgePenalty;
    }

    /**
     * Returns the entity's hit stat.
     * 
     * @return the hit stat
     */
    public int getHit() {
        return hit;
    }

    /**
     * Returns the entity's effective hit rate (hit + hitMod) * hitMult.
     * 
     * @return the effective hit rate
     */
    public int getEffectiveHit() {
        return (int) ((hit + hitBonus - hitPenalty) * hitMult + hitMultBonus
                - hitMultPenalty);
    }

    /**
     * Returns the entity's damage stat.
     * 
     * @return the damage stat
     */
    public int getDamageBonus() {
        return damageBonus;
    }

    /**
     * Return the enemies effective damage bonus.
     * 
     * @return the effective damage bonus
     */
    public int getEffectiveDamageBonus() {
        return damageBonus - damagePenalty;
    }

    /**
     * Return the enemies effective damage bonus.
     * 
     * @return the effective damage bonus
     */
    public double getEffectiveDamageMult() {
        return damageMult - damageMultPenalty;
    }

    /**
     * Returns the entity's damage multiplier.
     * 
     * @return the damageMult
     */
    public double getDamageMult() {
        return damageMult;
    }

    /**
     * Returns the entity's heal modifier.
     * 
     * @return the healMod
     */
    public int getHealBonus() {
        return healBonus;
    }

    /**
     * Returns the entity's heal multiplier.
     * 
     * @return the healMult
     */
    public double getHealMult() {
        return healMult;
    }

    /**
     * Returns the entity's carry capacity.
     * 
     * @return the carryCapacity
     */
    public int getCarryCapacity() {
        return carryCapacity;
    }

    /**
     * Returns the entity's carry capacity modifier.
     * 
     * @return the carryCapacityMod
     */
    public int getCarryCapacityBonus() {
        return carryCapacityBonus;
    }

    /**
     * Returns the effective carry capacity.
     * 
     * @return the effective carry capacity
     */
    public int getEffectiveCarryCapacity() {
        return carryCapacity + carryCapacityBonus - carryCapacityPenalty;
    }

    /**
     * Returns the entity's current stamina.
     * 
     * @return the currentStamina
     */
    public int getCurrentStamina() {
        return currentStamina;
    }

    /**
     * Returns the entity's current mana.
     * 
     * @return the currentMana
     */
    public int getCurrentMana() {
        return currentMana;
    }

    /**
     * Returns the entity's equipment effects List.
     * 
     * @return the equipmentEffects List
     */
    public List<Effect> getEquipmentEffects() {
        return equipmentEffects;
    }

    /**
     * Returns the entity's effective mental resistance.
     * 
     * @return the effective mental distance
     */
    public int getEffectiveMentalResistance() {
        return mentalResistance + mentalResistanceBonus
                - mentalResistancePenalty;
    }

    /**
     * Returns the entity's effective physical resistance.
     * 
     * @return the effective physical resistance
     */
    public int getEffectivePhysicalResistance() {
        return physicalResistance + physicalResistanceBonus
                - physicalResistancePenalty;
    }

    /**
     * Returns the entity's mental effect chance multiplier.
     * 
     * @return the mentalChanceMult
     */
    public double getMentalChanceMult() {
        return mentalChanceMult;
    }

    /**
     * Returns the entity's effective physical chance multiplier.
     * 
     * @return the effective physical chance multiplier
     */
    public double getEffectivePhysicalChanceMult() {
        return physicalChanceMult + physicalChanceMultBonus
                - physicalChanceMultPenalty;
    }

    /**
     * Returns the entity's physical effect chance multiplier.
     * 
     * @return the physicalChanceMult
     */
    public double getPhysicalChanceMult() {
        return physicalChanceMult;
    }

    /**
     * Returns the entity's effective mental chance multiplier.
     * 
     * @return the effective mental chance multiplier
     */
    public double getEffectiveMentalChanceMult() {
        return mentalChanceMult + mentalChanceMultBonus
                - mentalChanceMultPenalty;
    }

    /**
     * Sets the vitality.
     * 
     * @param vitality
     *            the vitality to set
     */
    public void setVitality(int vitality) {
        this.vitality = vitality;
    }

    /**
     * Sets the strength.
     * 
     * @param strength
     *            the strength to set
     */
    public void setStrength(int strength) {
        this.strength = strength;
    }

    /**
     * Sets the dexterity.
     * 
     * @param dexterity
     *            the dexterity to set
     */
    public void setDexterity(int dexterity) {
        this.dexterity = dexterity;
    }

    /**
     * Sets the intellect.
     * 
     * @param intellect
     *            the intellect to set
     */
    public void setIntellect(int intellect) {
        this.intellect = intellect;
    }

    /**
     * Sets the spirit.
     * 
     * @param spirit
     *            the spirit to set
     */
    public void setSpirit(int spirit) {
        this.spirit = spirit;
    }

    /**
     * Sets the luck.
     * 
     * @param luck
     *            the luck to set
     */
    public void setLuck(int luck) {
        this.luck = luck;
    }

    /**
     * Returns the entities effective slashing reduction.
     * 
     * @return the effective slashing reduction
     */
    public int getEffectiveSlashingReduction() {
        return slashingReduction + slashingReductionBonus
                - slashingReductionPenalty;
    }

    /**
     * Returns the entities effective piercing reduction.
     * 
     * @return the effective pierceing reduction
     */
    public int getEffectivePiercingReduction() {
        return piercingReduction + piercingReductionBonus
                - piercingReductionPenalty;
    }

    /**
     * Returns the entities effective bludgeoning reduction.
     * 
     * @return the effective bludgeoning reduction
     */
    public int getEffectiveBludgeoningReduction() {
        return bludgeoningReduction + bludgeoningReductionBonus
                - bludgeoningReductionPenalty;
    }

    /**
     * Returns the entities effective fire reduction.
     * 
     * @return the effective fire reduction
     */
    public int getEffectiveFireReduction() {
        return fireReduction + fireReductionBonus - fireReductionPenalty;
    }

    /**
     * Returns the entities effective cold reduction.
     * 
     * @return the effective cold reduction
     */
    public int getEffectiveColdReduction() {
        return coldReduction + coldReductionBonus - coldReductionPenalty;
    }

    /**
     * Returns the entities effective electricity reduction.
     * 
     * @return the effective slash reduction
     */
    public int getEffectiveElectricityReduction() {
        return electricityReduction + electricityReductionBonus
                - electricityReductionPenalty;
    }

    /**
     * Returns the entities effective sacred reduction.
     * 
     * @return the effective sacred reduction
     */
    public int getEffectiveSacredReduction() {
        return sacredReduction + sacredReductionBonus - sacredReductionPenalty;
    }

    /**
     * Returns the entities effective profane reduction.
     * 
     * @return the effective profane reduction
     */
    public int getEffectiveProfaneReduction() {
        return profaneReduction + profaneReductionBonus
                - profaneReductionPenalty;
    }

    /**
     * Returns the entities effective poison reduction.
     * 
     * @return the effective poison reduction
     */
    public int getEffectivePoisonReduction() {
        return poisonReduction + poisonReductionBonus - poisonReductionPenalty;
    }

    /**
     * Returns the entities effective slashing resistance.
     * 
     * @return the effective slashing resistance
     */
    public double getEffectiveSlashingResistance() {
        return slashingResistance + slashingResistanceBonus
                - slashingResistancePenalty;
    }

    /**
     * Returns the entities effective piercing resistance.
     * 
     * @return the effective piercing resistance
     */
    public double getEffectivePiercingResistance() {
        return piercingResistance + piercingResistanceBonus
                - piercingResistancePenalty;
    }

    /**
     * Returns the entities effective bludgeoning resistance.
     * 
     * @return the effective bludgeoning resistance
     */
    public double getEffectiveBludgeoningResistance() {
        return bludgeoningResistance + bludgeoningResistanceBonus
                - bludgeoningResistancePenalty;
    }

    /**
     * Returns the entities effective fire resistance.
     * 
     * @return the effective fire resistance
     */
    public double getEffectiveFireResistance() {
        return fireResistance + fireResistanceBonus - fireResistancePenalty;
    }

    /**
     * Returns the entities effective cold resistance.
     * 
     * @return the effective cold resistance
     */
    public double getEffectiveColdResistance() {
        return coldResistance + coldResistanceBonus - coldResistancePenalty;
    }

    /**
     * Returns the entities effective electricity resistance.
     * 
     * @return the effective electricity resistance
     */
    public double getEffectiveElectricityResistance() {
        return electricityResistance + electricityResistanceBonus
                - electricityResistancePenalty;
    }

    /**
     * Returns the entities effective sacred resistance.
     * 
     * @return the effective sacred resistance
     */
    public double getEffectiveSacredResistance() {
        return sacredResistance + sacredResistanceBonus
                - sacredResistancePenalty;
    }

    /**
     * Returns the entities effective piercing resistance.
     * 
     * @return the effective piercing resistance
     */
    public double getEffectiveProfaneResistance() {
        return profaneResistance + profaneResistanceBonus
                - profaneResistancePenalty;
    }

    /**
     * Returns the entities effective poison resistance.
     * 
     * @return the effective poison resistance
     */
    public double getEffectivePoisonResistance() {
        return poisonResistance + poisonResistanceBonus
                - poisonResistancePenalty;
    }

    /**
     * Returns the effects list.
     * 
     * @return the effects
     */
    public List<Effect> getEffects() {
        return effects;
    }

    /**
     * Returns the amount of currency carried by the entity
     * 
     * @return the currency
     */
    public int getCurrency() {
        return currency;

    }

    /**
     * An enumerator to store equipment array indexes.
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
     * Returns true if the entity is at full health.
     * 
     * @return if the entity is at full health
     */
    public boolean isAtFullHealth() {
        return currentHealth == getEffectiveMaxHealth();
    }

    /**
     * Returns a string of the entity's stats.
     * 
     * @return returns the status string
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

    /**
     * Checks the specified stat against the given value with the given
     * operator.
     * 
     * @param statName
     *            the stat name
     * @param operator
     *            the operator to use
     * @param value
     *            the value to compare to
     * @param roll
     *            the roll
     * @return if the check passed or failed
     */
    public boolean checkStat(String statName, String operator, double value,
            int roll) {
        double stat = getStat(statName);
        switch (operator) {
        case "=":
            return stat + Dice.roll(roll) == value;
        case "!=":
            return stat + Dice.roll(roll) != value;
        case "<":
            return stat + Dice.roll(roll) < value;
        case ">":
            return stat + Dice.roll(roll) > value;
        case "<=":
            return stat + Dice.roll(roll) <= value;
        case ">=":
            return stat + Dice.roll(roll) >= value;
        default:
            return false;
        }
    }

    /**
     * Checks the specified stat against the given value with the given
     * operator.
     * 
     * @param flagName
     *            the name of the flag
     * @param operator
     *            the operator to use
     * @param value
     *            the value to compare to
     * @param roll
     *            the roll
     * @return if the check passed or failed
     */
    public boolean checkFlag(String flagName, String operator, int value,
            int roll) {
        int flagValue = getFlagValue(flagName);
        switch (operator) {
        case "=":
            return flagValue + Dice.roll(roll) == value;
        case "!=":
            return flagValue + Dice.roll(roll) != value;
        case "<":
            return flagValue + Dice.roll(roll) < value;
        case ">":
            return flagValue + Dice.roll(roll) > value;
        case "<=":
            return flagValue + Dice.roll(roll) <= value;
        case ">=":
            return flagValue + Dice.roll(roll) >= value;
        default:
            return false;
        }
    }

    /**
     * Gets the stat from the given string.
     * 
     * @param statName
     * @return the stat as a double
     */
    public double getStat(String statName) {
        switch (statName) {
        case "vitality":
            return getEffectiveVitality();
        case "strength":
            return getEffectiveStrength();
        case "dexterity":
            return getEffectiveDexterity();
        case "intellect":
            return getEffectiveIntellect();
        case "spirit":
            return getEffectiveSpirit();
        case "luck":
            return getEffectiveLuck();
        case "currentHealth":
            return getCurrentHealth();
        case "maxHealth":
            return getEffectiveMaxHealth();
        case "stamina":
            return getCurrentStamina();
        case "maxStamina":
            return getEffectiveMaxStamina();
        case "speed":
            return getEffectiveSpeed();
        case "dodge":
            return getEffectiveDodge();

        case "slashingReduction":
            return getEffectiveSlashingReduction();
        case "piercingReduction":
            return getEffectivePiercingReduction();
        case "bludgeoningReduciton":
            return getEffectiveBludgeoningReduction();
        case "fireReduction":
            return getEffectiveFireReduction();
        case "coldReduction":
            return getEffectiveColdReduction();
        case "electricityReduction":
            return getEffectiveElectricityReduction();
        case "sacredReduction":
            return getEffectiveSacredReduction();
        case "profaneReduction":
            return getEffectiveProfaneReduction();
        case "poisonReduction":
            return getEffectivePoisonReduction();

        case "slashingResistance":
            return getEffectiveSlashingResistance();
        case "piercingResistance":
            return getEffectivePiercingResistance();
        case "bludgeoningResistance":
            return getEffectiveBludgeoningResistance();
        case "fireResistance":
            return getEffectiveFireResistance();
        case "coldResistance":
            return getEffectiveColdResistance();
        case "electricityResistance":
            return getEffectiveElectricityResistance();
        case "sacredResistance":
            return getEffectiveSacredResistance();
        case "profaneResistance":
            return getEffectiveProfaneResistance();
        case "poisonResistance":
            return getEffectivePoisonResistance();

        case "mentalResistance":
            return getEffectiveMentalResistance();
        case "physicalResistance":
            return getEffectivePhysicalResistance();
        case "mentalChance":
            return getEffectiveMentalChanceMult();
        case "physicalChance":
            return getEffectivePhysicalChanceMult();
        case "hit":
            return getEffectiveHit();
        case "damageBonus":
            return getEffectiveDamageBonus();
        case "damageMult":
            return getEffectiveDamageMult();
        case "healBonus":
            return getHealBonus();
        case "healMult":
            return getHealMult();

        case "carryCapacity":
            return getEffectiveCarryCapacity();
        default:
            break;
        }
        return baseDodge;

    }

    /**
     * Gets the specified flags value.
     * 
     * @param flag
     *            the flag to get
     * @return the value of the flag
     */
    public int getFlagValue(String flag) {
        if (flags.get(flag) != null) {
            return flags.get(flag);
        }
        return 0;
    }

    /**
     * Sets a flag with the specified operator and value
     * 
     * @param flag
     *            the flag to set
     * @param operator
     *            the operator to use
     * @param value
     *            the value to modify by
     */
    public void setFlag(String flag, String operator, int value) {
        switch (operator) {
        case "=":
            setFlagValue(flag, value);
            break;
        case "+":
            setFlagValue(flag, flags.get(flag) - value);
            break;
        case "-":
            setFlagValue(flag, flags.get(flag) + value);
            break;
        case "*":
            setFlagValue(flag, flags.get(flag) * value);
            break;
        case "/":
            setFlagValue(flag, flags.get(flag) / value);
            break;
        default:
        }
    }

    /**
     * Check if an item is equipped.
     * 
     * @param itemId
     *            the name of the item to check for
     * @return if the item is equipped
     */
    public boolean checkIfEquipped(String itemId) {
        for (Item item : equipment) {
            if (item != null) {
                if (item.getId().equals(itemId)) {
                    return true;
                }
            }

        }
        return false;
    }

    /**
     * Check if an item is in the players inventory.
     * 
     * @param itemId
     *            the name of the item to check for
     * @return if the item is in the players inventory
     */
    public boolean checkIfInInventory(String itemId) {
        for (Item item : inventory) {
            if (item.getId().equals(itemId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Check if an item is in the players inventory and has at least one use
     * left
     * 
     * @param itemId
     *            the name of the item to check for
     * @return if the item is in the players inventory
     */
    public boolean checkIfItemUses(String itemId, String operator, int value) {
        for (Item item : inventory) {
            if (item.getId().equals(itemId)) {
                switch (operator) {
                case "=":
                    if (item.getUses() == value)
                        return true;
                    break;
                case "<":
                    if (item.getUses() < value)
                        return true;
                    break;
                case ">":
                    if (item.getUses() > value)
                        return true;
                    break;
                case "<=":
                    if (item.getUses() <= value)
                        return true;
                    break;
                case ">=":
                    if (item.getUses() >= value)
                        return true;
                    break;
                default:
                }
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the player is under the given effect.
     * 
     * @param effectName
     *            the name of the effect to look for
     * @return if the player is under that effect.
     */
    public boolean checkIfEffected(String effectName) {
        for (Effect effect : effects) {
            if (effect.getId().equals(effectName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the player knows the given spell.
     * 
     * @param spellName
     *            the name of the spell to look for
     * @return if the player knows the spell
     */
    public boolean checkIfSpell(String spellName) {
        reloadSpells();
        for (Spell spell : spells) {
            if (spell.getId().equals(spellName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Checks if the player knows the given skill.
     * 
     * @param skillName
     *            the name of the skill to check for
     * @return if the player has the skill
     */
    public boolean checkIfSkill(String skillName) {
        reloadSkills();
        for (Skill skill : combinedSkills) {
            if (skill.getId().equals(skillName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Sets a flags value.
     * 
     * @param flag
     *            the flag to set
     * @param value
     *            the value to set
     */
    public void setFlagValue(String flag, int value) {
        flags.put(flag, value);
    }

    public boolean removeItemFromInventory(String itemName) {
        ListIterator<Item> itr = inventory.listIterator();
        while (itr.hasNext()) {
            if (itr.next().getName().equals(itemName)) {
                itr.remove();
                return true;
            }
        }
        return false;
    }

    public boolean removeItemUse(String itemName) {
        ListIterator<Item> itr = inventory.listIterator();
        Item item;
        while (itr.hasNext()) {
            if ((item = itr.next()).getName().equals(itemName)) {
                item.removeUse();
                return true;
            }
        }
        return false;
    }

    public boolean removeSkillFromInnateSkills(String skillName) {
        ListIterator<Skill> itr = innateSkills.listIterator();
        while (itr.hasNext()) {
            if (itr.next().getName().equals(skillName)) {
                itr.remove();
                return true;
            }
        }
        return false;
    }

    public boolean removeSpellFromKnownSpells(String spellName) {
        ListIterator<Spell> itr = knownSpells.listIterator();
        while (itr.hasNext()) {
            if (itr.next().getName().equals(spellName)) {
                itr.remove();
                return true;
            }
        }
        return false;
    }

    public void addCurrency(int amount) {
        triggerEffects("currency_gained_before");
        currency += amount;
        triggerEffects("currency_gained_after");
    }

    public void removeCurrency(int amount) {
        triggerEffects("currency_removed_before");
        currency -= amount;
        triggerEffects("currency_removed_after");
    }

    public boolean canAfford(int cost) {
        return currency >= cost;
    }

    /**
     * Adds an entity listener to the entity.
     * 
     * @param listener
     *            the listener to add
     */
    public void addEntityListener(EntityListener listener) {
        entityListeners.add(listener);
    }

    /**
     * Removes an entity listener from the entity.
     * 
     * @param listener
     *            the listener to remove
     */
    public void removeEntityListener(EntityListener listener) {
        entityListeners.remove(listener);
    }
}
