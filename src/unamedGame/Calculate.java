/**
 * 
 */
package unamedGame;

import unamedGame.entities.Entity;
import unamedGame.skills.Skill;

/**
 * @author c-e-r
 *
 */
public class Calculate {

	public static int calculateAttackDamage(Entity attacker, int weaponBaseDamage, int weaponVariableDamage) {
		double twoHandedMultiplier = 1;
		if (attacker.isWieldingTwoHanded()) {
			twoHandedMultiplier = 1.5;
		}

		return (int) ((((attacker.getEffectiveStrength() + weaponBaseDamage + Dice.roll(weaponVariableDamage)
				+ attacker.getEffectiveDamageBonus()) * attacker.getEffectiveDamageMult())) * twoHandedMultiplier);
	}

	public static int calculateSkillAttackDamage(Entity attacker, Skill skill, int weaponBaseDamage,
			int weaponVariableDamage) {

		return (int) (((attacker.getEffectiveStrength() + weaponBaseDamage + Dice.roll(weaponVariableDamage)
				+ attacker.getEffectiveDamageBonus() + skill.getAttackDamageBonus()
				+ Dice.roll(skill.getAttackVariableDamageBonus()))
				* (attacker.getEffectiveDamageMult() + skill.getAttackDamageMult())));
	}

	public static int calculateAttackHitChance(Entity attacker, int weaponHitChance) {
		return attacker.getEffectiveHit() + weaponHitChance + Dice.roll(Dice.HIT_DIE);

	}

	public static int calculateSkillAttackHitChance(Entity attacker, Skill skill, int weaponHitChance) {
		return attacker.getEffectiveHit() + weaponHitChance + skill.getAttackHitBonus() + Dice.roll(Dice.HIT_DIE);

	}

}
