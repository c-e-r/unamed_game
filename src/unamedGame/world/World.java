/**
 * 
 */
package unamedGame.world;

import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.Scanner;

/**
 * A class to store and manipulate the world
 * 
 * @author c-e-r
 *
 */
public class World {

	private static World instance = null;

	private WorldTile[][] world;

	/**
	 * Returns the World object and creates it if it dosn't already exist
	 * 
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
		world = new WorldTile[10][10];
		fillWorldFromIntGrid(readIntGridFromFile());

	}

	private void fillWorldFromIntGrid(int[][] intGrid) {
		for (int i = 0; i < intGrid.length; i++) {
			for (int j = 0; j < intGrid[i].length; j++) {
				switch (intGrid[i][j]) {
				case 0:
					world[i][j] = new WorldTile("ocean", 0, 0, 'o',
							new Point(i, j));
					break;
				case 1:
					world[i][j] = new WorldTile("plains", 2, 2, 'p',
							new Point(i, j));
					break;
				case 2:
					world[i][j] = new WorldTile("forest", 3, 3, 'f',
							new Point(i, j));
					break;
				case 3:
					world[i][j] = new WorldTile("mountain", 4, 4, 'm',
							new Point(i, j));
					break;
				
				default:
					break;
				}
			}

		}
	}

	private int[][] readIntGridFromFile() {
		int[][] intGrid = new int[10][10];
		String line;
		int i = 0;
		try {
			BufferedReader reader = new BufferedReader(
					new FileReader(new File("data/map/map.csv")));
			while ((line = reader.readLine()) != null) {
				String[] splitLine = line.split(",");
				for (int j = 0; j < splitLine.length; j++) {
					intGrid[j][i] = Integer.parseInt(splitLine[j]);
				}
				i++;
			}
			reader.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		
		return intGrid;
	}

	/**
	 * Returns true if the given location exists
	 * 
	 * @param point
	 *            the x y coordinates to check
	 * @return if the given point exists
	 */
	public boolean locationExists(Point point) {
		return point.x >= 0 && point.y >= 0 && point.x < world.length
				&& point.y < world[1].length;

	}

	public boolean locationExists(int x, int y) {
		return x >= 0 && y >= 0 && x < world.length && y < world[1].length;

	}

	public WorldTile getTile(int x, int y) {
		if (locationExists(x, y)) {
			return world[x][y];
		}
		return new WorldTile();

	}

	public WorldTile getTile(Point point) {
		if (locationExists(point.x, point.y)) {
			return world[point.x][point.y];
		}
		return new WorldTile();

	}

	/**
	 * Returns a 2D array of world tiles representing the world
	 * 
	 * @return a 2D array of world tiles representing the world
	 */
	public WorldTile[][] getWorld() {
		return world;
	}

}
