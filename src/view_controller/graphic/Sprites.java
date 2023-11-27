package view_controller.graphic;

import java.util.ArrayList;
import java.util.List;

import javafx.scene.image.Image;

public class Sprites {

    protected List<Image> images = new ArrayList<>();
    protected int imageFrame = 0;

    public Sprites() {
        super();
    }

    // public static enum Type {
    // PLAYER,
    // ENEMY1,
    // ENEMY2,
    // ENEMY3,
    // BULLET
    // }

    public void addImage(Image image) {
        images.add(image);
    }

    public void addImage(int insertIndex, Image image) {
        images.add(insertIndex, image);
        if (insertIndex <= imageFrame && images.size() > 0) {
            imageFrame += 1;
        }
    }

    public Image getCurrentFrameImage() {
        return images.get(imageFrame);
    }

    public void nextKeyframe() {
        imageFrame += 1;
    }
}
