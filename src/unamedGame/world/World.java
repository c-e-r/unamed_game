/**
 * 
 */
package unamedGame.world;

import java.awt.Point;

/**
 * A class to store and manipulate the world
 * @author c-e-r
 *
 */
public class World {

	private static World instance = null;

	private WorldTile[][] world;

	/**
	 * Returns the World object and creates it if it dosn't already exist
	 * @return the World object
	 */
	public static World getInstance() {
		if (instance == null) {
			instance = new World();

		}
		return instance;
	}

	/**
	 * Creates the world and fills it with tiles
	 */
	private World() {
		world = new WorldTile[50][50];

		for (int i = 0; i < world.length; i++) {
			for (int j = 0; j < world[i].length; j++) {
				if (i % 2 == 0) {
					world[i][j] = new WorldTile("mountain", 4, 4, 'm', new Point(i, j));

				} else {
					world[i][j] = new WorldTile("forest", 2, 2, 'f', new Point(i, j));
				}
			}
		}
	}

	/**
	 * Returns true if the given location exists
	 * @param point the x y coordinates to check
	 * @return if the given point exists
	 */
	public boolean locationExists(Point point) {
		return point.x >= 0 && point.y >= 0 && point.x < world.length && point.y < world[1].length;

	}

	/**
	 * Returns a 2D array of world tiles representing the world
	 * @return a 2D array of world tiles representing the world
	 */
	public WorldTile[][] getWorld() {
		return world;
	}

}
