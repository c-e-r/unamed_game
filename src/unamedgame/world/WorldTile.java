/**
 * 
 */
package unamedgame.world;

import java.awt.Color;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * A class to store tile information.
 * 
 * @author c-e-r
 * @version 0.0.1
 */
public class WorldTile {

    private String tileType;
    private int maxExplore;
    private int currentExplore;
    private char character;
    private Point coordinates;
    private List<String> eventFiles;
    private Color color;
    private Location location;
    private double travelMult;

    /**
     * A default Constructor.
     */
    public WorldTile() {
        tileType = "impassable";
        maxExplore = 0;
        currentExplore = 0;
        character = '-';
    }

    /**
     * Creates a worldtile from the given parameters.
     * 
     * @param tileType
     *            the type of tile
     * @param maxExplore
     *            the maximum amount of times the tile can be explored
     * @param currentExplore
     *            the number of remaining times the tile can be explored
     * @param character
     *            the character representing the tiles type
     * @param coordinates
     *            the x y coordinates of the tile
     */
    public WorldTile(String tileType, int maxExplore, int currentExplore,
            char character, Point coordinates) {
        this.tileType = tileType;
        this.maxExplore = maxExplore;
        this.currentExplore = currentExplore;
        this.character = character;
        this.coordinates = coordinates;

    }

    /**
     * Creates a worldtile from the given parameters.
     * 
     * @param tileType
     *            the type of tile
     * @param maxExplore
     *            the max times the tile can be explored
     * @param character
     *            the character that represents the tile
     * @param color
     *            the tiles color as a Color
     * @param eventFiles
     *            the names of the tiles event files
     */
    public WorldTile(String tileType, int maxExplore, char character,
            Color color,double travelMult,  String... eventFiles) {
        this.tileType = tileType;
        this.maxExplore = maxExplore;
        this.character = character;
        this.eventFiles = new ArrayList<String>();
        this.color = color;
        this.travelMult = travelMult;
        for (String fileName : eventFiles) {
            this.eventFiles.add(fileName);
        }
    }

    /**
     * Creates a new world tiles from a world tile and a point.
     * 
     * @param worldTile
     *            the world tile to copy
     * @param point
     *            the location of the tile
     */
    public WorldTile(WorldTile worldTile, Point point) {
        this.tileType = worldTile.tileType;
        this.maxExplore = worldTile.maxExplore;
        this.currentExplore = this.maxExplore;
        this.character = worldTile.character;
        this.eventFiles = worldTile.eventFiles;
        this.color = worldTile.color;
    }

    /**
     * Adds an event file to the tile.
     * 
     * @param filename
     *            the filename of the event file
     */
    public void addEventFile(String filename) {
        eventFiles.add(filename);
    }

    
    public void setLocation(Location location) {
        this.location = location;
    }
    
    public Location getLocation() {
        return location;
    }
    /**
     * Returns a string showing the number of remaining explorations out of the
     * maximum.
     * 
     * @return a string showing the number of remaining explorations out of the
     *         maximum
     */
    public String getExploredOutOfMax() {
        if (maxExplore != 0) {
            return currentExplore + "/" + maxExplore;
        }
        return "";
    }

    /**
     * Returns the tiles type.
     * 
     * @return the tiles tileType
     */
    public String getTileType() {
        return tileType;
    }

    /**
     * Returns the maximum number of times the tile can be explored.
     * 
     * @return the maximum number of times the tile can be explored
     */
    public int getMaxExplore() {
        return maxExplore;
    }

    /**
     * Returns the number of remaining times the tile can be explored.
     * 
     * @return the number of remaining times the tile can be explored
     */
    public int getCurrentExplore() {
        return currentExplore;
    }

    /**
     * Sets the number of times the tiles has left to be explored.
     * 
     * @param currentExplore
     *            the number of explorations remaning to set
     */
    public void setCurrentExplore(int currentExplore) {
        this.currentExplore = currentExplore;
    }

    /**
     * Returns the character that represents the tile type.
     * 
     * @return the character that represents the tile type
     */
    public char getCharacter() {
        return character;
    }

    /**
     * Returns the tiles coordinates as a Point.
     * 
     * @return the tiles coordinates as a Point
     */
    public Point getCoordinates() {
        return coordinates;
    }

    /**
     * Return the tiles event files.
     * 
     * @return the eventFiles
     */
    public List<String> getEventFiles() {
        return eventFiles;
    }

    /**
     * Return the tiles color.
     * 
     * @return the color
     */
    public Color getColor() {
        return color;
    }

    @Override
    public String toString() {
        return "WorldTile [tileType=" + tileType + ", maxExplore=" + maxExplore
                + ", currentExplore=" + currentExplore + ", character="
                + character + "]";
    }

}
