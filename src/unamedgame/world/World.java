/**
 * 
 */
package unamedgame.world;

import java.awt.Color;
import java.awt.Point;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.Scanner;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import unamedgame.Game;
import unamedgame.ui.Window;

import java.lang.Integer;

/**
 * A class to store and manipulate the world
 * 
 * @author c-e-r
 * @version 0.0.1
 */
public class World {
    

    private static final Logger LOG = LogManager.getLogger(Game.class);

    private static World instance = null;
    private static HashMap<Integer, WorldTile> tileStorage = new HashMap<Integer, WorldTile>();

    private WorldTile[][] world;

    /**
     * Returns the World object and creates it if it dosn't already exist.
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
     * Creates the world and fills it with tiles.
     */
    private World() {
        world = new WorldTile[10][10];
        fillTileStorageFromFile();
        fillWorldFromIntGrid(readIntGridFromFile());
        addLocationsToTiles();
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
    
    public void addLocationsToTiles() {
        String[] tmp;
        Scanner scanner;
        try {
            scanner = new Scanner(
                    new File("data/map/locations.txt"));
            while (scanner.hasNextLine()) {
                tmp = scanner.nextLine().split("\\|");
                if(!tmp[0].substring(0, 1).equals("#")) {
                    int x = Integer.parseInt(tmp[0]);
                    int y = Integer.parseInt(tmp[1]);
                    String name = tmp[2];
                    String desc = tmp[3];
                    String eventFile = tmp[4];
                    
                    this.getTile(x, y).setLocation(new Location(name, desc, eventFile));
                    
                }
            }
            scanner.close();
        } catch (FileNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
       
    }

    /**
     * Returns true if the given location exists.
     * 
     * @param point
     *            the x y coordinates to check
     * @return if the given point exists
     */
    public boolean locationExists(Point point) {
        return point.x >= 0 && point.y >= 0 && point.x < world.length
                && point.y < world[1].length;

    }

    /**
     * Checks if the given location is within the bounds of the world.
     * 
     * @param x
     *            the x value
     * @param y
     *            the y value
     * @return if the location exists
     */
    public boolean locationExists(int x, int y) {
        return x >= 0 && y >= 0 && x < world.length && y < world[1].length;

    }

    /**
     * Gets the specified tile.
     * 
     * @param x
     *            the x value
     * @param y
     *            the y value
     * @return the tile at the specified location
     */
    public WorldTile getTile(int x, int y) {
        if (locationExists(x, y)) {
            return world[x][y];
        }
        return new WorldTile();

    }

    /**
     * Gets the specified tile.
     * 
     * @param point
     *            the x and y value of the tile to get
     * @return the tile at the specified location
     */
    public WorldTile getTile(Point point) {
        if (locationExists(point.x, point.y)) {
            return world[point.x][point.y];
        }
        return new WorldTile();

    }

    /**
     * Returns a 2D array of world tiles representing the world.
     * 
     * @return a 2D array of world tiles representing the world
     */
    public WorldTile[][] getWorld() {
        return world;
    }

    /**
     * Fills tile storage from tileData.properties.
     */
    private void fillTileStorageFromFile() {
        Properties properties = new Properties();

        try {
            properties.load(
                    new FileReader(new File("data/map/tileData.properties")));
            for (String key : properties.stringPropertyNames()) {
                String[] values = properties.getProperty(key).split("\\|");
                WorldTile tile = new WorldTile(values[0],
                        Integer.parseInt(values[1]), values[2].charAt(0),
                        Color.decode(values[3]), Double.parseDouble(values[4]));
                for (int i = 5; i < values.length; i++) {
                    tile.addEventFile(values[i]);
                }
                tileStorage.put(Integer.parseInt(key, 16), tile);

            }
        } catch (IOException e) {
            Window.appendText(
                    "ERROR: There was an error loading 'data/map/tileData.properties'. Please check that the file exists. See game.log for more information.\n");
            LOG.error(e);
            e.printStackTrace();
        } catch (NumberFormatException e) {
            Window.appendText(
                    "ERROR: There was an error loading 'data/map/tileData.properties'. Please check that the file is correctly formatted. See game.log for more information.\n");
            LOG.error(e);
            e.printStackTrace();
        }
    }

}
