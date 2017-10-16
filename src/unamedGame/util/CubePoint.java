package unamedGame.util;

import java.awt.Point;

/**
 * A class to store cube points for finding hex grid neighbors
 * 
 * @author c-e-r
 *
 */
public class CubePoint {

	public int x;
	public int y;
	public int z;

	/**
	 * An array to store the cube points of various nearby hexes
	 */
	public static final CubePoint[] CUBE_DIRECTIONS_7 = {
			new CubePoint(-3, 3, 0), new CubePoint(-3, 2, 1),
			new CubePoint(-3, 1, 2), new CubePoint(-3, 0, 3),
			new CubePoint(-2, 3, -1), new CubePoint(-2, 2, 0),
			new CubePoint(-2, 1, 1), new CubePoint(-2, 0, 2),
			new CubePoint(-2, -1, 3), new CubePoint(-1, 3, -2),
			new CubePoint(-1, 2, -1), new CubePoint(-1, 1, 0),
			new CubePoint(-1, 0, 1), new CubePoint(-1, -1, 2),
			new CubePoint(-1, -2, 3), new CubePoint(0, 3, -3),
			new CubePoint(0, 2, -2), new CubePoint(0, 1, -1),
			new CubePoint(0, 0, 0), new CubePoint(0, -1, 1),
			new CubePoint(0, -2, 2), new CubePoint(0, -3, 3),
			new CubePoint(1, 2, -3), new CubePoint(1, 1, -2),
			new CubePoint(1, 0, -1), new CubePoint(1, -1, 0),
			new CubePoint(1, -2, 1), new CubePoint(1, -3, 2),
			new CubePoint(2, 1, -3), new CubePoint(2, 0, -2),
			new CubePoint(2, -1, -1), new CubePoint(2, -2, 0),
			new CubePoint(2, -3, 1), new CubePoint(3, 0, -3),
			new CubePoint(3, -1, -2), new CubePoint(3, -2, -1),
			new CubePoint(3, -3, 0) };
	/**
	 * An array to store the cube points of neighboring hexes
	 */
	public static final CubePoint[] MOVEMENT_DIRECTIONS = {
			new CubePoint(-1, 1, 0), new CubePoint(0, 1, -1),
			new CubePoint(1, 0, -1), new CubePoint(1, -1, 0),
			new CubePoint(0, -1, 1), new CubePoint(-1, 0, 1) };

	/**
	 * Create a cubepoint with x, y, and z coordinates
	 * 
	 * @param x
	 * @param y
	 * @param z
	 */
	public CubePoint(int x, int y, int z) {
		this.x = x;
		this.y = y;
		this.z = z;

	}

	/**
	 * Converts a CubePoint to a Point
	 * 
	 * @param cube
	 *            the cube point to convert
	 * @return the converted point
	 */
	public static Point cubePointToPoint(CubePoint cube) {
		int col = cube.x;
		int row = cube.z + (cube.x - (cube.x & 1)) / 2;
		return new Point(col, row);
	}

	/**
	 * Converts a Point to a CubePoint
	 * 
	 * @param point
	 *            the point to convert
	 * @return the converted cubepoint
	 */
	public static CubePoint pointToCubePoint(Point point) {
		int x = point.x;
		int z = point.y - (point.x - (point.x & 1)) / 2;
		int y = -x - z;
		return new CubePoint(x, y, z);

	}

	/**
	 * Returns a CubePoint for the given direction.
	 * 
	 * @param direction
	 *            the direction as an int
	 * @return a CubePoint for the given direction
	 */
	private static CubePoint getCubeDirection(int direction) {
		return CUBE_DIRECTIONS_7[direction];
	}

	/**
	 * Returns a CubePoint for the given direction.
	 * 
	 * @param direction
	 *            the direction as an int
	 * @return a CubePoint for the given direction
	 */
	private static CubePoint getMoveDirection(int direction) {
		return MOVEMENT_DIRECTIONS[direction];
	}

	/**
	 * Given a cube point and a direction return the neighboring cubepoint. See
	 * grid.txt for more information on what number refers to which direction
	 * 
	 * @param cube
	 *            the CubePoint to find neighbors of
	 * @param direction
	 *            the direction to get the neighbor from
	 * @return the neighbor CubePoint
	 */
	public static CubePoint getCubeNeighbor(CubePoint cube, int direction) {
		return cubeAdd(cube, getCubeDirection(direction));
	}

	/**
	 * Given a cube point and a direction return the neighboring cubepoint. See
	 * grid.txt for more information on what number refers to which direction
	 * 
	 * @param cube
	 *            the CubePoint to find neighbors of
	 * @param direction
	 *            the direction to get the neighbor from
	 * @return the neighbor CubePoint
	 */
	public static CubePoint getMoveNeighbor(CubePoint cube, int direction) {
		return cubeAdd(cube, getMoveDirection(direction));
	}

	/*
	 * Add two cube points together
	 */
	private static CubePoint cubeAdd(CubePoint cube1, CubePoint cube2) {
		int x = cube1.x + cube2.x;
		int y = cube1.y + cube2.y;
		int z = cube1.z + cube2.z;

		return new CubePoint(x, y, z);
	}

	@Override
	public String toString() {
		return "CubePoint [x=" + x + ", y=" + y + ", z=" + z + "]";
	}
}
