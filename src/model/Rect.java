package model;

public class Rect extends Entity {
    protected float width, height;

    public Rect(Game game, float x, float y, float width, float height) {
        super(game, x, y);
        this.width = width;
        this.height = height;
    }

    public void setWidth(float sizeX) {
        this.width = sizeX;
    }

    public void setHeight(float sizeY) {
        this.height = sizeY;
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    public float getCenterX() {
        return x + width / 2;
    }

    public float getCenterY() {
        return y + height / 2;
    }

    public void setCenterX(float x) {
        this.setX(x - width / 2);
        //this.x = x - width / 2;
    }

    public void setCenterY(float y) {
        this.setY(y - height / 2);
        //this.y = y - height / 2;
    }

    public void setCenter(float x, float y) {
        setCenterX(x);
        setCenterY(y);
    }
}
