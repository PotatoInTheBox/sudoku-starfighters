package model;

import javafx.scene.image.Image;

public class Sprite extends Rect {

    private Image spriteImage = null;

    public Sprite(Game game, float x, float y, float width, float height, Image spriteImage) {
        super(game, x, y, width, height);
        this.spriteImage = spriteImage;
    }

    public Image getImage() {
        return spriteImage;
    }

}
