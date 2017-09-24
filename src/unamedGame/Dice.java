/**
 * 
 */
package unamedGame;

import java.util.Random;

/**
 * @author c-e-r
 *
 */
public class Dice {
	

	public static final int HIT_DIE = 100;
	public static final int RESISTANCE_DIE = 100;
	public static final int SPEED_DIE = 10;

	private static Random rand = new Random();

	/**
	 * Returns a number between one and the specified value inclusive
	 * 
	 * @param d
	 *            the upper bound
	 * @return the random number
	 */
	public static int roll(int d) {
		if (d == 0) {
		return 0;
		}
		d = rand.nextInt(d) + 1;

		return d;
	}

}
