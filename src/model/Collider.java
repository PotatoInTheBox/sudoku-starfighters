package model;

public class Collider extends Rect {

    public Collider(Game game, float x, float y, float width, float height) {
        super(game, x, y, width, height);
    }

    public Collider(Game game, float width, float height) {
        super(game, 0, 0, width, height);
    }

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
