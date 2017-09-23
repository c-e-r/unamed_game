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

	private static Random rand = new Random();

	/**
	 * Returns a number between one and the specified value inclusive
	 * @param d the upper bound
	 * @return the random number
	 */
	public static int roll(int d) {
		d = rand.nextInt(d) +1;
		
		return d;
	}

}
