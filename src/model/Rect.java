package model;

/**
 * Rect types of Entity allow for representing an entity as a rectangle instead
 * of just a point. This means that the extending object can have access to
 * rectangle properties such as widths, heights, and centers. This is useful
 * for colliders and sprites as both can be modelled using rectangles.
 */
public class Rect extends Entity {
    protected float width, height;

    /**
     * Construct the rectangle with the given position and size.
     * 
     * @param game   to instantiate to
     * @param x      absolute x to spawn at
     * @param y      absolute y to spawn at
     * @param width  to scale collider to
     * @param height to scale collider to
     */
    public Rect(Game game, float x, float y, float width, float height) {
        super(game, x, y);
        this.width = width;
        this.height = height;
    }

    /**
     * Set the width to a new width.
     * 
     * @param width new width
     */
    public void setWidth(float width) {
        this.width = width;
    }

    /**
     * Set the height to a new height.
     * 
     * @param height new height
     */
    public void setHeight(float height) {
        this.height = height;
    }

    /**
     * Get the width of the rectangle.
     *
     * @return the width of the rectangle
     */
    public float getWidth() {
        return width;
    }

    /**
     * Get the height of the rectangle.
     *
     * @return the height of the rectangle
     */
    public float getHeight() {
        return height;
    }

    /**
     * Get the x-coordinate of the center of the rectangle.
     *
     * @return the x-coordinate of the center
     */
    public float getCenterX() {
        return getX() + width / 2;
    }

    /**
     * Get the y-coordinate of the center of the rectangle.
     *
     * @return the y-coordinate of the center
     */
    public float getCenterY() {
        return getY() + height / 2;
    }

    /**
     * Set the x-coordinate of the center of the rectangle.
     *
     * @param x the new x-coordinate of the center
     */
    public void setCenterX(float x) {
        this.setX(x - width / 2);
    }

    /**
     * Set the y-coordinate of the center of the rectangle.
     *
     * @param y the new y-coordinate of the center
     */
    public void setCenterY(float y) {
        this.setY(y - height / 2);
    }

    /**
     * Set the center of the rectangle to the specified coordinates.
     *
     * @param x the new x-coordinate of the center
     * @param y the new y-coordinate of the center
     */
    public void setCenter(float x, float y) {
        setCenterX(x);
        setCenterY(y);
    }
}
