/**
 * 
 */
package unamedGame.world;

import java.awt.Color;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Properties;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import unamedGame.Game;
import unamedGame.ui.Window;

import java.lang.Integer;

/**
 * A class to store and manipulate the world
 * 
 * @author c-e-r
 *
 */
public class World {

	private static final Logger LOG = LogManager.getLogger(Game.class);

	private static World instance = null;
	private static HashMap<Integer, WorldTile> tileStorage = new HashMap<Integer, WorldTile>();

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
		fillTileStorageFromFile();
		fillWorldFromIntGrid(readIntGridFromFile());

	}

	private void fillWorldFromIntGrid(int[][] intGrid) {
		for (int i = 0; i < intGrid.length; i++) {
			for (int j = 0; j < intGrid[i].length; j++) {
				world[i][j] = new WorldTile(tileStorage.get(intGrid[i][j]),
						new Point(i, j));
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
					if (j < intGrid.length && i < intGrid[j].length) {
						intGrid[j][i] = Integer.parseInt(splitLine[j], 16);

					}
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

	private void fillTileStorageFromFile() {
		Properties properties = new Properties();

		try {
			properties.load(
					new FileReader(new File("data/map/tileData.properties")));
			for (String key : properties.stringPropertyNames()) {
				String[] values = properties.getProperty(key).split("\\|");
				WorldTile tile = new WorldTile(values[0],
						Integer.parseInt(values[1]), values[2].charAt(0),
						Color.decode(values[3]));
				for (int i = 4; i < values.length; i++) {
					tile.addEventFile(values[i]);
				}
				tileStorage.put(Integer.parseInt(key, 16), tile);

			}
		} catch (IOException e) {
			Window.appendToPane(Window.getInstance().getTextPane(),
					"ERROR: There was an error loading 'data/map/tileData.properties'. Please check that the file exists. See game.log for more information.");
			LOG.error(e);
			e.printStackTrace();
		} catch (NumberFormatException e) {
			Window.appendToPane(Window.getInstance().getTextPane(),
					"ERROR: There was an error loading 'data/map/tileData.properties'. Please check that the file is correctly formatted. See game.log for more information.");
			LOG.error(e);
			e.printStackTrace();
		}
	}

}
