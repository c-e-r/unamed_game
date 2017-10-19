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
		StringBuilder builder = new StringBuilder();
		builder.append("Damage roll: ");
		builder.append("((( ");
		builder.append(attacker.getEffectiveStrength());
		builder.append(" + ");
		builder.append(weapon.getWeaponBaseDamage());
		builder.append(" + ");
		builder.append(dieRoll);
		builder.append("[1-");

		builder.append(weapon.getWeaponVariableDamage());
		builder.append("] + ");
		builder.append(attacker.getDamageBonus());
		builder.append(") * ");
		builder.append(attacker.getEffectiveDamageMult());
		builder.append(") * ");
		builder.append(handMultiplier);
		builder.append(") * ");
		builder.append(strRequirementMod);
		builder.append(" = ");
		builder.append(damage);
		LOG.debug(builder.toString());

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
				"(((strength + weaponBase + weaponVariable + damageBonus + skillBase + skillVariable) * damageMultiplier + skillDamageMultiplier) * handMultipler) * strengthRequirementMod");
		StringBuilder builder = new StringBuilder();
		builder.append("Damage roll: ");
		builder.append("((( ");
		builder.append(attacker.getEffectiveStrength());
		builder.append(" + ");
		builder.append(weapon.getWeaponBaseDamage());
		builder.append(" + ");
		builder.append(weaponDieRoll);
		builder.append("[1-");
		builder.append(weapon.getWeaponVariableDamage());
		builder.append("] + ");
		builder.append(attacker.getDamageBonus());
		builder.append(" + ");
		builder.append(skill.getAttackDamageBonus());
		builder.append(" + ");
		builder.append(skillDieRoll);
		builder.append("[1-");
		builder.append(skill.getAttackVariableDamageBonus());
		builder.append("]");
		builder.append(") * ");
		builder.append(attacker.getEffectiveDamageMult());
		builder.append(") * ");
		builder.append(handMultiplier);
		builder.append(") * ");
		builder.append(strRequirementMod);
		builder.append(" = ");
		builder.append(damage);
		LOG.debug(builder.toString());

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
		LOG.debug("Attack Hit Roll: " + attacker.getEffectiveHit() + " + "
				+ weaponHitChance + " + " + die + "[1-" + Dice.HIT_DIE + "] = "
				+ hit);
		return hit;
	}

	public static int calculateSkillAttackHitChance(Entity attacker,
			Skill skill, int weaponHitChance) {
		int die = Dice.roll(Dice.HIT_DIE);
		int hit = attacker.getEffectiveHit() + weaponHitChance
				+ skill.getAttackHitBonus() + die;
		LOG.debug(
				"hitChance + weaponHitChance + skillHitBonus + dieRoll[1-100]");
		LOG.debug("Skill Hit Roll: " + attacker.getEffectiveHit() + " + "
				+ weaponHitChance + " + " + skill.getAttackHitBonus() + " + "
				+ die + "[1-" + Dice.HIT_DIE + "] = " + hit);
		return hit;

	}

	public static int calculateSpellAttackHitChance(Entity attacker,
			Spell spell, int spellFocusHitChance) {
		int die = Dice.roll(Dice.HIT_DIE);
		int hit = attacker.getEffectiveHit() + spellFocusHitChance
				+ spell.getAttackHitBonus() + die;

		LOG.debug(
				"hitChance + weaponHitChance + skillHitBonus + dieRoll[1-100]");
		LOG.debug("Spell Hit Roll: " + attacker.getEffectiveHit() + " + "
				+ spellFocusHitChance + " + " + spell.getAttackHitBonus() + " + "
				+ die + "[1-" + Dice.HIT_DIE + "] = " + hit);
		return hit;

	}

}
