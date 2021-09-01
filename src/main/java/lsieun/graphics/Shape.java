package lsieun.graphics;

import java.awt.*;

/**
 * A shape that can be drawn on a canvas.
 */
public interface Shape {
    /**
     * Gets the leftmost x-position of the shape.
     *
     * @return the leftmost x-position
     */
    int getX();

    /**
     * Gets the topmost y-position of the shape.
     *
     * @return the topmost y-position
     */
    int getY();

    /**
     * Gets the width of the shape.
     *
     * @return the width
     */
    int getWidth();

    /**
     * Gets the height of the shape.
     *
     * @return the height
     */
    int getHeight();

    /**
     * Paints the shape
     *
     * @param g2 the graphics object
     */
    void paintShape(Graphics2D g2);
}