package model;

/**
 * The Collider allows for performing collision detection. A seperate entity,
 * Collider, can be made with the dimensions needed for collision. This means
 * that other classes such as Sprite can operate independently without being
 * tied to how collisions work.
 */
public class Collider extends Rect {

    /**
     * Collider constructor creates a collider with the given dimensions.
     * 
     * @param game   to instantiate children nodes to.
     * @param x      absolute x position to place the collider object.
     * @param y      absolute y position to place the collider object.
     * @param width  for the collider size.
     * @param height for the collider size.
     */
    public Collider(Game game, float x, float y, float width, float height) {
        super(game, x, y, width, height);
    }

    /**
     * Collider constructor creates a collider with the given dimensions. It is
     * set to coordinates 0,0 when created.
     * 
     * @param game   to instantiate children nodes to.
     * @param width  for the collider size.
     * @param height for the collider size.
     */
    public Collider(Game game, float width, float height) {
        super(game, 0, 0, width, height);
    }

    /**
     * Return true/false whether the current collider is currently touching
     * another collider.
     * 
     * @param other collider to check for.
     * @return true if touching, false if not touching
     */
    public boolean hasCollidedWith(Collider other) {
        float myX = getX();
        float myY = getY();
        float otherX = other.getX();
        float otherY = other.getY();
        if (myX < otherX + other.width &&
                myX + this.width > otherX &&
                myY < otherY + other.height &&
                myY + this.height > otherY) {
            return true;
        } else
            return false;
    }

    /**
     * Check if the collider is bounded within a given area.
     * 
     * @param boundsX      absolute x position (top left)
     * @param boundsY      absolute y position (top left)
     * @param boundsWidth  bounds width (growing to bottom right)
     * @param boundsHeight bounds height (growing to bottom right)
     * @return true if bounded, false if not bounded.
     */
    public boolean isOutOfBounds(float boundsX, float boundsY, float boundsWidth, float boundsHeight) {
        // out of bounds means if any part of the collider goes outside the bounds
        float myX = getX();
        float myY = getY();
        if (myX + width > boundsX + boundsWidth ||
                myX < boundsX ||
                myY + height > boundsY + boundsHeight ||
                myY < boundsY) {
            return true;
        } else
            return false;
    }
}
