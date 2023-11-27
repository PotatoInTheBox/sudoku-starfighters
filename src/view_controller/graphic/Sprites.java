package view_controller.graphic;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.HashMap;
import java.io.File;
import java.net.URI;

import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;

public class Sprites {

    private static HashMap<String, Image> imageMap = new HashMap<>();

    public static Image loadImage(String path) {

        // check if it already exists
        if (imageMap.containsKey(path)) {
            // if so we return the cached image
            return imageMap.get(path);
        }

        // if not we try to load it
        // either return the image or return null
        String imagePath = "resources/images/" + path;
        try {
            File file = new File(imagePath);
            URI uri = file.toURI();
            Image image = new Image(uri.toString());
            imageMap.put(path, image);
            return image;
        } catch (Exception e) {
            System.out.println("Couldn't find local image resource " + imagePath);
            return null;
        }

    }
}
