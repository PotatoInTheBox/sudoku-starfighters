package model;

import javafx.scene.image.Image;
import javafx.scene.paint.Color;
import view_controller.graphic.Sprites;

import java.util.List;
import java.util.ArrayList;
import java.lang.Math;

/**
 * Sprite is a rectangle entity that contains one or many images which it can
 * cycle through and return when the rendering loop needs it.
 */
public class Sprite extends Rect {

    private List<Image> sprites = new ArrayList<>();
    private int frame = 0;

    /**
     * Construct a new sprite.
     * 
     * @param game        to instantiate to
     * @param x           absolute x to spawn at (centered)
     * @param y           absolute y to spawn at (centered)
     * @param width       to scale collider and sprite to
     * @param height      to scale collider and sprite to
     * @param spriteImage to set initial image to
     */
    public Sprite(Game game, float x, float y, float width, float height, Image spriteImage) {
        super(game, x, y, width, height);
        if (spriteImage != null) {
            this.sprites.add(spriteImage);
        }

    }

    /**
     * Construct a new sprite (and set the initial image to null).
     * 
     * @param game   to instantiate to
     * @param x      absolute x to spawn at (centered)
     * @param y      absolute y to spawn at (centered)
     * @param width  to scale collider and sprite to
     * @param height to scale collider and sprite to
     */
    public Sprite(Game game, float x, float y, float width, float height) {
        this(game, x, y, width, height, ((Image) null));
    }

    /**
     * Construct a new sprite, but have the sprite fetch the Image from a path.
     * 
     * @param game      to instantiate to
     * @param x         absolute x to spawn at (centered)
     * @param y         absolute y to spawn at (centered)
     * @param width     to scale collider and sprite to
     * @param height    to scale collider and sprite to
     * @param imagePath a path to fetch the initial image to
     */
    public Sprite(Game game, float x, float y, float width, float height, String imagePath) {
        this(game, x, y, width, height, ((Image) null));
        Image spriteImage = Sprites.loadImage(imagePath);
        if (spriteImage != null) {
            this.sprites.add(spriteImage);
        }
    }

    /**
     * Cycle to the next frame in the image list
     */
    public void nextFrame() {
        frame += 1;
    }

    /**
     * Get the current image/frame selected. (null if none exist)
     * 
     * @return the current image of the sprite
     */
    public Image getImage() {
        if (sprites.size() == 0) {
            return null;
        }
        return sprites.get(Math.floorMod(frame, sprites.size()));
    }

    /**
     * Add image or images to the sprite image list.
     * 
     * @param images to add
     */
    public void addImage(Image... images) {
        for (Image image : images) {
            sprites.add(image);
        }
    }

    /**
     * Add image path or paths to the sprite image list.
     * 
     * @param imagePaths to add
     */
    public void addImage(String... imagePaths) {
        for (String path : imagePaths) {
            addImage(Sprites.loadImage(path));
        }
    }

    /**
     * Add image or images to the sprite image list. It will clear any images
     * that were previously in the image list.
     * 
     * @param images to add
     */
    public void setImage(Image... images) {
        sprites.clear();
        addImage(images);
    }

    /**
     * Add image path or paths to the sprite image list. It will clear any images
     * that were previously in the image list.
     * 
     * @param imagePaths to add
     */
    public void setImage(String... imagePaths) {
        sprites.clear();
        addImage(imagePaths);
    }

    /**
     * Clears all images in the image list.
     */
    public void clearImages() {
        sprites.clear();
    }

    /**
     * Sets a given color to the image. Only pure white pixels will be changed.
     * 
     * @param color to set all images in image list to
     */
    public void setColor(Color color) {
        for (int i = 0; i < sprites.size(); i++) {
            sprites.set(i, Sprites.getColoredImage(sprites.get(i), color));
        }
    }

}
