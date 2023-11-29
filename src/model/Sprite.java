package model;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
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
        if (sprites.size() == 0){
            return null;
        }
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

    public void setImage(Image... images) {
        sprites.clear();
        addImage(images);
    }

    public void setImage(String... imagePaths) {
        sprites.clear();
        addImage(imagePaths);
    }

    public void clearImages() {
        sprites.clear();
    }

    public void setColor(Color color) {
        for (int i = 0; i < sprites.size(); i++) {
            sprites.set(i, Sprites.getColoredImage(sprites.get(i), color));
        }
    }

}
