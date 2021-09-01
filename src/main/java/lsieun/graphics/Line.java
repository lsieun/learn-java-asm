package lsieun.graphics;

import java.awt.*;
import java.awt.geom.Line2D;

public class Line implements Shape {
    private Color color = Color.BLACK;
    private double x1;
    private double y1;
    private double x2;
    private double y2;

    /**
     * Constructs a line with a given starting and ending location.
     *
     * @param x1 the x-coordinate of the starting point
     * @param y1 the y-coordinate of the starting point
     * @param x2 the x-coordinate of the ending point
     * @param y2 the y-coordinate of the ending point
     */
    public Line(double x1, double y1, double x2, double y2) {
        this.x1 = x1;
        this.x2 = x2;
        this.y1 = y1;
        this.y2 = y2;
    }

    /**
     * Gets the leftmost x-position of the line.
     *
     * @return the leftmost x-position
     */
    public int getX() {
        return (int) Math.round(Math.min(x1, x2));
    }

    /**
     * Gets the topmost y-position of the line.
     *
     * @return the topmost y-position
     */
    public int getY() {
        return (int) Math.round(Math.min(y1, y2));
    }

    /**
     * Gets the width of the bounding box.
     *
     * @return the width
     */
    public int getWidth() {
        return (int) Math.round(Math.abs(x2 - x1));
    }

    /**
     * Gets the height of the bounding box.
     *
     * @return the height
     */
    public int getHeight() {
        return (int) Math.round(Math.abs(y2 - y1));
    }

    /**
     * Moves this line by a given amount.
     *
     * @param dx the amount by which to move in x-direction
     * @param dy the amount by which to move in y-direction
     */
    public void translate(double dx, double dy) {
        x1 += dx;
        y1 += dy;
        x2 += dx;
        y2 += dy;
        Canvas.getInstance().repaint();
    }

    /**
     * Resizes this line both horizontally and vertically.
     *
     * @param dw the amount by which to resize the width on each side
     * @param dw the amount by which to resize the height on each side
     */
    public void grow(double dw, double dh) {
        if (x1 <= x2) {
            x1 -= dw;
            x2 += dw;
        }
        else {
            x1 += dw;
            x2 -= dw;
        }
        if (y1 <= y2) {
            y1 -= dh;
            y2 += dh;
        }
        else {
            y1 += dh;
            y2 -= dh;
        }

        Canvas.getInstance().repaint();
    }

    /**
     * Sets the color for drawing this line.
     *
     * @param newColor the new color
     */
    public void setColor(Color newColor) {
        color = newColor;
        Canvas.getInstance().repaint();
    }

    /**
     * Shows this line on the canvas.
     */
    public void draw() {
        Canvas.getInstance().show(this);
    }

    public String toString() {
        return "Line[x1=" + x1 + ",y1=" + y1 + ",x2=" + x2 + ",y2=" + y2 + "]";
    }

    public void paintShape(Graphics2D g2) {
        if (color != null) {
            g2.setColor(new java.awt.Color((int) color.getRed(), (int) color.getGreen(), (int) color.getBlue()));
            Line2D.Double line = new Line2D.Double(x1, y1, x2, y2);
            g2.draw(line);
        }
    }
}