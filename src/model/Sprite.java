package model;

import javafx.scene.image.Image;
import view_controller.graphic.Sprites;

import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

public class Sprite extends Rect {

    private List<Image> sprites = new ArrayList<>();
    private int frame = 0;

    public Sprite(Game game, float x, float y, float width, float height, Image spriteImage) {
        super(game, x, y, width, height);
        if (spriteImage != null) {
            this.sprites.add(spriteImage);
        }

    }

    public Sprite(Game game, float x, float y, float width, float height) {
        this(game, x, y, width, height, ((Image) null));
    }

    public Sprite(Game game, float x, float y, float width, float height, String imagePath) {
        this(game, x, y, width, height, ((Image) null));
        Image spriteImage = Sprites.loadImage(imagePath);
        if (spriteImage != null) {
            this.sprites.add(spriteImage);
        }
    }

    public void nextFrame() {
        frame += 1;
    }

    public Image getImage() {
        return sprites.get(Math.floorMod(frame, sprites.size()));
    }

    public void addImage(Image... images) {
        for (Image image : images) {
            sprites.add(image);
        }
    }

    public void addImage(String... imagePaths) {
        for (String path : imagePaths) {
            addImage(Sprites.loadImage(path));
        }
    }

    public void clearImages() {
        sprites.clear();
    }

}
