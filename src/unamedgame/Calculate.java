/**
 * 
 */
package unamedgame;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import unamedgame.entities.Entity;
import unamedgame.items.Item;
import unamedgame.skills.Skill;
import unamedgame.spells.Spell;

/**
 * A class to handle various calculations.
 * 
 * @author c-e-r
 * @version 0.0.1
 *
 */
public class Calculate {

    private static final Logger LOG = LogManager.getLogger(Game.class);

    private static final double OFF_HAND_MULTIPLIER = 0.5;
    private static final double TWO_HAND_MULTIPLER = 1.5;
    private static final double STRENGTH_REQUIREMENT_FAILED = 0.5;

    /**
     * Calculates the damage dealt by an attack.
     * 
     * @param attacker
     *            the entity initiating the attack
     * @param weapon
     *            the weapon being used for the attack
     * @param offHandAttack
     *            if the attack is an offhand attack
     * @return an array with the damage as the first value and if the attack crit as the second
     */
    public static int[] calculateAttackDamage(Entity attacker, Item weapon,
            boolean offHandAttack) {

        double handMultiplier = 1;
        double strRequirementMod = 1;
        double critMult = 1;
        int crit = 0;
        if (Dice.roll(Dice.CRIT_DIE) <= attacker.getEffectiveCritChance()
                + weapon.getWeaponCritChance()) {
            critMult = attacker.getEffectiveCritMult()
                    + weapon.getWeaponCritMult();
            crit = 1;
        }
        if (offHandAttack) {
            handMultiplier = OFF_HAND_MULTIPLIER;
        }
        if (attacker.isWieldingTwoHanded()) {
            handMultiplier = TWO_HAND_MULTIPLER;
        }
        if (attacker.getEffectiveStrength() * handMultiplier < weapon
                .getStrRequirement()) {
            strRequirementMod = STRENGTH_REQUIREMENT_FAILED;
        }
        int dieRoll = Dice.roll(weapon.getWeaponVariableDamage());

        int damage = (int) ((((((attacker.getEffectiveStrength()
                + weapon.getWeaponBaseDamage() + dieRoll
                + attacker.getEffectiveDamageBonus())
                * attacker.getEffectiveDamageMult())) * handMultiplier)
                * strRequirementMod) * critMult);

        LOG.debug(
                "((((strength + weaponBase + weaponVariable + damageBonus) * damageMultiplier) * handMultipler) * strengthRequirementMod) * critMultiplier");
        LOG.debug(String.format(
                "Damage roll: (((%d + %d + %d[1-%d] + %d) * %.2f) * %.2f) * %.2f) *%.2f = %d",
                attacker.getEffectiveStrength(), weapon.getWeaponBaseDamage(),
                dieRoll, weapon.getWeaponVariableDamage(),
                attacker.getDamageBonus(), attacker.getEffectiveDamageMult(),
                handMultiplier, strRequirementMod, critMult, damage));

        if (damage < 0) {
            damage = 0;
        }
        return new int[] { damage, crit };
    }

    /**
     * Calculates the damage deal by the attack portion of a skill.
     * 
     * @param attacker
     *            the entity initiating the attack
     * @param skill
     *            the skill being used by the attacker
     * @param weapon
     *            the weapon being used by the attacker
     * @param offHandAttack
     *            if the attack is an offhand attack
     * @return an array with the damage as the first value and if the attack crit as the second
     */
    public static int[] calculateSkillAttackDamage(Entity attacker, Skill skill,
            Item weapon, boolean offHandAttack) {

        double handMultiplier = 1;
        double strRequirementMod = 1;
        double critMult = 1;
        int crit = 0;
        if (Dice.roll(Dice.CRIT_DIE) <= attacker.getEffectiveCritChance()
                + weapon.getWeaponCritChance()) {
            critMult = attacker.getEffectiveCritMult()
                    + weapon.getWeaponCritMult();
            crit = 1;
        }
        if (offHandAttack) {
            handMultiplier = OFF_HAND_MULTIPLIER;
        }
        if (attacker.isWieldingTwoHanded()) {
            handMultiplier = TWO_HAND_MULTIPLER;
        }
        if (attacker.getEffectiveStrength() * handMultiplier < weapon
                .getStrRequirement()) {
            strRequirementMod = STRENGTH_REQUIREMENT_FAILED;
        }

        int weaponDieRoll = Dice.roll(weapon.getWeaponVariableDamage());
        int skillDieRoll = Dice.roll(skill.getAttackVariableDamageBonus());

        int damage = (int) ((((attacker.getEffectiveStrength()
                + weapon.getWeaponBaseDamage() + weaponDieRoll
                + attacker.getEffectiveDamageBonus()
                + skill.getAttackDamageBonus() + skillDieRoll)
                * (attacker.getEffectiveDamageMult()
                        + skill.getAttackDamageMult())
                * handMultiplier) * strRequirementMod) * critMult);

        LOG.debug(
                "((((strength + weaponBase + weaponVariable + damageBonus + skillBase + skillVariable) * (damageMultiplier + skillDamageMultiplier)) * handMultipler) * strengthRequirementMod) * critMultiplier");
        LOG.debug(String.format(
                "Damage roll: ((((%d + %d + %d[1-%d] + %d + %d + %d[1-%d]) * (%.2f + %.2f)) * %.2f) * %.2f) * %.2f= %d",
                attacker.getEffectiveStrength(), weapon.getWeaponBaseDamage(),
                weaponDieRoll, weapon.getWeaponVariableDamage(),
                attacker.getDamageBonus(), skill.getAttackDamageBonus(),
                skillDieRoll, skill.getAttackVariableDamageBonus(),
                attacker.getEffectiveDamageMult(), skill.getAttackDamageMult(),
                handMultiplier, strRequirementMod, critMult, damage));

        if (damage < 0) {
            damage = 0;
        }
        return new int[] { damage, crit };

    }

    /**
     * Calculates the hit roll for an attack.
     * 
     * @param attacker
     *            the entity initiating the attack
     * @param weaponHitChance
     *            the hit chance bonus of the weapon being used to attack
     * @return the hit total
     */
    public static int calculateAttackHitChance(Entity attacker,
            int weaponHitChance) {
        int die = Dice.roll(Dice.HIT_DIE);
        int hit = attacker.getEffectiveHit() + weaponHitChance + die;
        LOG.debug("hitChance + weaponHitChance + dieRoll[1-100]");
        LOG.debug(String.format("Attack Hit Roll: %d + %d + %d[1-%d] = %d",
                attacker.getEffectiveHit(), weaponHitChance, die, Dice.HIT_DIE,
                hit));
        return hit;
    }

    /**
     * Calculates the hit roll for a skill attack.
     * 
     * @param attacker
     *            the entity initiating the attack
     * @param skill
     *            the skill being used
     * @param weaponHitChance
     *            the hit chance bonus of the weapon being used to attack
     * @return the hit total
     */
    public static int calculateSkillAttackHitChance(Entity attacker,
            Skill skill, int weaponHitChance) {
        int die = Dice.roll(Dice.HIT_DIE);
        int hit = attacker.getEffectiveHit() + weaponHitChance
                + skill.getAttackHitBonus() + die;
        LOG.debug(
                "hitChance + weaponHitChance + skillHitBonus + dieRoll[1-100]");
        LOG.debug(String.format("Skill Hit Roll: %d + %d + %d + %d[1-%d] = %d",
                attacker.getEffectiveHit(), weaponHitChance,
                skill.getAttackHitBonus(), die, Dice.HIT_DIE, hit));
        return hit;
    }

    /**
     * Calculates the hit roll for a spell attack.
     * 
     * @param attacker
     *            the entity initiating the attack
     * @param spell
     *            the spell being cast
     * @param spellFocusHitChance
     *            the hit chance granted by the spell focus being used to attack
     * @return the hit total
     */
    public static int calculateSpellAttackHitChance(Entity attacker,
            Spell spell, int spellFocusHitChance) {
        int die = Dice.roll(Dice.HIT_DIE);
        int hit = attacker.getEffectiveHit() + spellFocusHitChance
                + spell.getAttackHitBonus() + die;

        LOG.debug(
                "hitChance + spellFocusHitBonus + spellHitBonus + dieRoll[1-100]");
        LOG.debug(String.format("Spell Hit Roll: %d + %d + %d + %d[1-%d] = %d",
                attacker.getEffectiveHit(), spellFocusHitChance,
                spell.getAttackHitBonus(), die, Dice.HIT_DIE, hit));
        return hit;

    }

}
