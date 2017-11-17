/**
 * 
 */
package unamedgame.ui;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;

import javax.swing.JPanel;

import unamedgame.entities.Player;
import unamedgame.time.Time;
import unamedgame.util.CubePoint;
import unamedgame.world.World;
import unamedgame.world.WorldTile;

/**
 * A panel to display the map
 * 
 * @author c-e-r
 *
 */

public class MapPanel extends JPanel {
    private static final long serialVersionUID = 1L;
    private static final int PREFERRED_WIDTH = 450;
    private static final int PREFERRED_HEIGHT = 450;
    private int width;
    private int height;
    private Font font = new Font("monospaced", Font.PLAIN, 16);
    FontMetrics metrics;

    public MapPanel() {
        //setPreferredSize(new Dimension(PREFERRED_WIDTH, PREFERRED_HEIGHT));
    }

    /**
     * Paints the map panel
     */
    @Override
    public void paintComponent(Graphics g) {
        Graphics2D g2d = (Graphics2D) g;
        RenderingHints rh = new RenderingHints(
                RenderingHints.KEY_TEXT_ANTIALIASING,
                RenderingHints.VALUE_TEXT_ANTIALIAS_OFF);
        g2d.setRenderingHints(rh);
        height = (int) this.getSize().getHeight();
        width = (int) this.getSize().getWidth();
        int lowerBound = (height < width ? height : width);
        lowerBound = (int) (lowerBound * 0.8);
        Point origin = new Point(height / 2, width / 2);

        g2d.setStroke(new BasicStroke(4.0f, BasicStroke.CAP_SQUARE,
                BasicStroke.JOIN_MITER));
        g2d.setFont(font);
        metrics = g.getFontMetrics();

        drawRectangle(g2d, true, 0xFFFFFF, 1);
        int radius = lowerBound / 13;
        drawHexGridLoop(g2d, origin, 9, radius, 1, lowerBound);
        drawMovementOverlay(g2d, origin, lowerBound, 0x000000);
        drawDateOverlay(g2d, origin, lowerBound, 0x000000);
        drawCurrencyOverlay(g2d, origin, lowerBound, 0x000000);
    }

    /**
     * Draws a grid of hexagons based on the parameters given
     * 
     * @param g
     *            the graphics object
     * @param origin
     *            the origin point of the grid
     * @param size
     *            the size of the grid to draw
     * @param radius
     *            the radius of the hexes
     * @param padding
     *            the padding between hexes
     */
    private void drawHexGridLoop(Graphics g, Point origin, int size, int radius,
            int padding, int lowerBound) {
        double ang30 = Math.toRadians(30);
        double xOff = Math.cos(ang30) * (radius + padding);
        double yOff = Math.sin(ang30) * (radius + padding);
        int half = size / 2;

        Point playerPoint = Player.getInstance().getLocation();
        CubePoint playerCubePoint = CubePoint.pointToCubePoint(playerPoint);
        WorldTile[] tiles = new WorldTile[61];

        for (int i = 0; i < tiles.length; i++) {
            tiles[i] = getMapTile(playerCubePoint, i);

        }
        int i = 0;
        for (int row = 0; row < size; row++) {
            int cols = size - java.lang.Math.abs(row - half);

            for (int col = 0; col < cols; col++) {
                int yLbl = row < half ? col - row : col - half;
                int xLbl = row - half;
                int y = (int) (origin.x + xOff * (col * 2 + 1 - cols));
                int x = (int) (origin.y + yOff * (row - half) * 3);

                drawHex(g, xLbl, yLbl, x, y, radius, tiles[i], lowerBound);
                i++;
            }
        }
    }

    /**
     * Draws the background of the map
     * 
     * @param g
     *            the graphics object
     * @param filled
     *            if the rectangle should be filled
     * @param colorValue
     *            the color of the rectangle
     * @param lineThickness
     *            the thickness of the rectangle border
     */
    public void drawRectangle(Graphics2D g, boolean filled, int colorValue,
            int lineThickness) {
        // Store before changing.
        Stroke tmpS = g.getStroke();
        Color tmpC = g.getColor();

        g.setColor(new Color(colorValue));
        g.setStroke(new BasicStroke(lineThickness, BasicStroke.CAP_ROUND,
                BasicStroke.JOIN_ROUND));

        if (filled)
            g.fillRect(0, 0, width, height);
        else
            g.drawRect(0, 0, width, height);

        // Set values to previous when done.
        g.setColor(tmpC);
        g.setStroke(tmpS);
    }

    /**
     * Draws a hexagon
     * 
     * @param g
     *            the graphics object
     * @param posX
     *            the x coordinate of the hex in the grid
     * @param posY
     *            the y coordinate of the hex in the grid
     * @param x
     *            the x position to draw the hex at
     * @param y
     *            the y position to draw the hex at
     * @param r
     *            the radius of the hex
     * @param tile
     *            the WorldTile the hex represents
     */
    private void drawHex(Graphics g, int posX, int posY, int x, int y, int r,
            WorldTile tile, int lowerBound) {
        Graphics2D g2d = (Graphics2D) g;
        Font tmpf = g.getFont();

        Hexagon hex = new Hexagon(x, y, r);
        String text = Character.toString(tile.getCharacter());
        int w = metrics.stringWidth(text);
        int h = metrics.getHeight();

        String outlineColor = "0xf1fccf";
        if (tile.getCoordinates() != null && tile.getCoordinates()
                .equals(Player.getInstance().getLocation())) {
            outlineColor = "0xFF0000";
            g.drawString("1", x - w / 2 + 1, y + h / 2);

        }
        hex.draw(g2d, x, y, 0, tile.getColor(), true);
        hex.draw(g2d, x, y, 2, Color.decode(outlineColor), false);

        g.setFont(new Font("Courier New", Font.PLAIN, lowerBound / 30));

        g.setColor(new Color(0xFFFFFF));
        g.drawString(text, x - w / 2, y + h / 2 - 14);
        g.drawString(tile.getExploredOutOfMax(), x - w / 2 - 10, y + h / 2 + 2);

        g.setFont(tmpf);
    }

    /**
     * Draws the movement overlay on the map
     * 
     * @param g
     *            the graphics object
     * @param o
     *            the origin of the panel
     * @param l
     *            lower value of height or width or the panel
     * @param colorValue
     *            the color to use for the text
     */
    private void drawMovementOverlay(Graphics2D g, Point o, int l,
            int colorValue) {
        Color tmpC = g.getColor();
        Font tmpf = g.getFont();
        g.setColor(new Color(colorValue));
        g.setFont(new Font("Courier New", Font.PLAIN, l / 30));
        g.drawString("1", o.y - l / 13, o.x - l / 31);
        g.drawString("2", o.y - l / 90, o.x - l / 14);
        g.drawString("3", o.y + l / 17, o.x - l / 31);
        g.drawString("4", o.y + l / 17, o.x + l / 23);
        g.drawString("5", o.y - l / 90, o.x + l / 11);
        g.drawString("6", o.y - l / 13, o.x + l / 21);

        g.setFont(tmpf);
        g.setColor(tmpC);

    }

    /**
     * Draws the date overlay on the map
     * 
     * @param g
     *            the graphics object
     * @param o
     *            the origin of the panel
     * @param l
     *            lower value of height or width or the panel
     * @param colorValue
     *            the color to use for the text
     */
    private void drawDateOverlay(Graphics2D g, Point o, int l, int colorValue) {
        Color tmpC = g.getColor();
        Font tmpf = g.getFont();
        g.setColor(new Color(colorValue));
        g.setFont(new Font("Courier New", Font.PLAIN, l / 25));
        g.drawString(Time.getInstance().getDateTime(), 0 + 10, height - 10);
        g.setFont(tmpf);
        g.setColor(tmpC);

    }

    /**
     * Draws the currency overlay on the map
     * 
     * @param g
     *            the graphics object
     * @param o
     *            the origin of the panel
     * @param l
     *            lower value of height or width or the panel
     * @param colorValue
     *            the color to use for the text
     */
    private void drawCurrencyOverlay(Graphics2D g, Point o, int l,
            int colorValue) {
        Color tmpC = g.getColor();
        Font tmpf = g.getFont();
        g.setColor(new Color(colorValue));
        g.setFont(new Font("Courier New", Font.PLAIN, l / 25));
        FontMetrics metrics = g.getFontMetrics(g.getFont());
        int stringWidth = metrics
                .stringWidth(Player.getInstance().getCurrencyString());
        g.drawString(Player.getInstance().getCurrencyString(),
                width - stringWidth - 10, height - 10);
        g.setFont(tmpf);
        g.setColor(tmpC);

    }

    /**
     * Gets a tile neighboring the player based on the given direction
     * 
     * @param playerCubePoint
     *            the players position as a CubePoint
     * @param direction
     *            the direction of the neighbor to get
     * @return the neighboring tile
     */
    private static WorldTile getMapTile(CubePoint playerCubePoint,
            int direction) {
        Point point = CubePoint.cubePointToPoint(
                CubePoint.getCubeNeighbor(playerCubePoint, direction));

        return World.getInstance().getTile(point);

    }

}
