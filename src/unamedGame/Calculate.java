/**
 * 
 */
package unamedGame;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import unamedGame.entities.Entity;
import unamedGame.items.Item;
import unamedGame.skills.Skill;

/**
 * @author c-e-r
 *
 */
public class Calculate {

	private static final Logger LOG = LogManager.getLogger(Game.class);

	public static int calculateAttackDamage(Entity attacker, Item weapon) {

		double twoHandedMultiplier = 1;
		double strRequirementMod = 1;
		if (attacker.isWieldingTwoHanded()) {
			twoHandedMultiplier = 1.5;
		}
		if (attacker.getEffectiveStrength() < weapon.getStrRequirement()) {
			strRequirementMod = 0.5;
		}
		int damage = (int) (((((attacker.getEffectiveStrength()
				+ weapon.getWeaponBaseDamage()
				+ Dice.roll(weapon.getWeaponVariableDamage())
				+ attacker.getEffectiveDamageBonus())
				* attacker.getEffectiveDamageMult())) * twoHandedMultiplier)
				* strRequirementMod);
		LOG.debug(damage + " damage calculated");
		return damage;
	}

	public static int calculateSkillAttackDamage(Entity attacker, Skill skill,
			int weaponBaseDamage, int weaponVariableDamage) {

		int damage = (int) (((attacker.getEffectiveStrength() + weaponBaseDamage
				+ Dice.roll(weaponVariableDamage)
				+ attacker.getEffectiveDamageBonus()
				+ skill.getAttackDamageBonus()
				+ Dice.roll(skill.getAttackVariableDamageBonus()))
				* (attacker.getEffectiveDamageMult()
						+ skill.getAttackDamageMult())));
		LOG.debug(damage + " damage calculated");

		return damage;

	}

	public static int calculateAttackHitChance(Entity attacker,
			int weaponHitChance) {

		int hit = attacker.getEffectiveHit() + weaponHitChance
				+ Dice.roll(Dice.HIT_DIE);
		LOG.debug(hit + " hit calcualted");
		return hit;
	}

	public static int calculateSkillAttackHitChance(Entity attacker,
			Skill skill, int weaponHitChance) {

		int hit = attacker.getEffectiveHit() + weaponHitChance
				+ skill.getAttackHitBonus() + Dice.roll(Dice.HIT_DIE);
		LOG.debug(hit + " hit calcualted");

		return hit;

	}

}
