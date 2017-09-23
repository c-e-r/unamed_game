/**
 * 
 */
package unamedGame.world;

import java.awt.Point;

/**
 * @author c-e-r
 *
 */
public class WorldTile {

	private String tileType;
	private int maxExplore;
	private int currentExplore;
	private char character;
	private Point coordinates;

	/**
	 * A default Constructor
	 */
	public WorldTile() {
		tileType = "impassable";
		maxExplore = 0;
		currentExplore = 0;
		character = '-';
	}

	/**
	 * Creates a worldtile from the given parameters
	 * @param tileType the type of tile
	 * @param maxExplore the maximum amount of times the tile can be explored
	 * @param currentExplore the number of remaining times the tile can be explored
	 * @param character the character representing the tiles type
	 * @param coordinates the x y coordinates of the tile
	 */
	public WorldTile(String tileType, int maxExplore, int currentExplore, char character, Point coordinates) {
		this.tileType = tileType;
		this.maxExplore = maxExplore;
		this.currentExplore = currentExplore;
		this.character = character;
		this.coordinates = coordinates;

	}

	/**
	 * Returns a string showing the number of remaining explorations out of the maximum
	 * @return a string showing the number of remaining explorations out of the maximum
	 */
	public String getExploredOutOfMax() {
		if (maxExplore != 0) {
			return currentExplore + "/" + maxExplore;
		}
		return "";
	}
	/**
	 * Returns the tiles type
	 * @return the tiles tileType
	 */
	public String getTileType() {
		return tileType;
	}

	/**
	 * Returns the maximum number of times the tile can be explored
	 * @return the maximum number of times the tile can be explored
	 */
	public int getMaxExplore() {
		return maxExplore;
	}
	/**
	 * Returns the number of remaining times the tile can be explored
	 * @return the number of remaining times the tile can be explored
	 */
	public int getCurrentExplore() {
		return currentExplore;
	}

	/**
	 * Sets the number of times the tiles has left to be explored
	 * @param currentExplore the number of explorations remaning to set
	 */
	public void setCurrentExplore(int currentExplore) {
		this.currentExplore = currentExplore;
	}
	/**
	 * Returns the character that represents the tile type
	 * @return the chracter taht represents the tile type
	 */
	public char getCharacter() {
		return character;
	}

	
	/**
	 * Returns the tiles coordinates as a Point
	 * @return the tiles coordinates as a Point
	 */
	public Point getCoordinates() {
		return coordinates;
	}

	@Override
	public String toString() {
		return "WorldTile [tileType=" + tileType + ", maxExplore=" + maxExplore + ", currentExplore=" + currentExplore
				+ ", character=" + character + "]";
	}

}
