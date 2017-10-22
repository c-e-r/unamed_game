/**
 * 
 */
package unamedGame;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import unamedGame.entities.Entity;
import unamedGame.items.Item;
import unamedGame.skills.Skill;
import unamedGame.spells.Spell;

/**
 * @author c-e-r
 *
 */
public class Calculate {

	private static final Logger LOG = LogManager.getLogger(Game.class);

	public static int calculateAttackDamage(Entity attacker, Item weapon,
			boolean offHandAttack) {

		double handMultiplier = 1;
		double strRequirementMod = 1;
		if (offHandAttack) {
			handMultiplier = 0.5;
		}
		if (attacker.isWieldingTwoHanded()) {
			handMultiplier = 1.5;
		}
		if (attacker.getEffectiveStrength() < weapon.getStrRequirement()) {
			strRequirementMod = 0.5;
		}
		int dieRoll = Dice.roll(weapon.getWeaponVariableDamage());

		int damage = (int) (((((attacker.getEffectiveStrength()
				+ weapon.getWeaponBaseDamage() + dieRoll
				+ attacker.getEffectiveDamageBonus())
				* attacker.getEffectiveDamageMult())) * handMultiplier)
				* strRequirementMod);

		LOG.debug(
				"(((strength + weaponBase + weaponVariable + damageBonus) * damageMultiplier) * handMultipler) * strengthRequirementMod");
		LOG.debug(String.format(
				"Damage roll: (((%d + %d + %d[1-%d] + %d) * %.2f) * %.2f) * %.2f = %d",
				attacker.getEffectiveStrength(), weapon.getWeaponBaseDamage(),
				dieRoll, weapon.getWeaponVariableDamage(),
				attacker.getDamageBonus(), attacker.getEffectiveDamageMult(),
				handMultiplier, strRequirementMod, damage));

		if (damage < 0) {
			return 0;
		}
		return damage;
	}

	public static int calculateSkillAttackDamage(Entity attacker, Skill skill,
			Item weapon, boolean offHandAttack) {

		double handMultiplier = 1;
		double strRequirementMod = 1;
		if (offHandAttack) {
			handMultiplier = 0.5;
		}
		if (attacker.isWieldingTwoHanded()) {
			handMultiplier = 1.5;
		}
		if (attacker.getEffectiveStrength() < weapon.getStrRequirement()) {
			strRequirementMod = 0.5;
		}

		int weaponDieRoll = Dice.roll(weapon.getWeaponVariableDamage());
		int skillDieRoll = Dice.roll(skill.getAttackVariableDamageBonus());

		int damage = (int) (((attacker.getEffectiveStrength()
				+ weapon.getWeaponBaseDamage() + weaponDieRoll
				+ attacker.getEffectiveDamageBonus()
				+ skill.getAttackDamageBonus() + skillDieRoll)
				* (attacker.getEffectiveDamageMult()
						+ skill.getAttackDamageMult())
				* handMultiplier) * strRequirementMod);

		LOG.debug(
				"(((strength + weaponBase + weaponVariable + damageBonus + skillBase + skillVariable) * (damageMultiplier + skillDamageMultiplier)) * handMultipler) * strengthRequirementMod");
		LOG.debug(String.format(
				"Damage roll: (((%d + %d + %d[1-%d] + %d + %d + %d[1-%d]) * (%.2f + %.2f)) * %.2f) * %.2f = %d",
				attacker.getEffectiveStrength(), weapon.getWeaponBaseDamage(),
				weaponDieRoll, weapon.getWeaponVariableDamage(),
				attacker.getDamageBonus(), skill.getAttackDamageBonus(),
				skillDieRoll, skill.getAttackVariableDamageBonus(),
				attacker.getEffectiveDamageMult(), skill.getAttackDamageMult(),
				handMultiplier, strRequirementMod, damage));
		;

		if (damage < 0) {
			return 0;
		}
		return damage;

	}

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
