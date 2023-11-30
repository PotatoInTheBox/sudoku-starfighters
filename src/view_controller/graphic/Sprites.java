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
import javafx.scene.paint.Color;

/**
 * Sprites is responsible for keeping a cache of all the images for the game.
 * It helps with loading the sprites from a file or retrieving the already
 * loaded sprite. It also has the ability to change the colors of the sprite and
 * cache that result too.
 */
public class Sprites {

    private static HashMap<String, Image> imageMap = new HashMap<>();
    private static HashMap<Image, Image> coloredImageMap = new HashMap<>();
    private static HashMap<Color, Image> colorToImageAssociation = new HashMap<>();

    /**
     * Loads an image
     * 
     * @param path The file path to load the image from
     * @return The loaded image
     */
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

    /**
     * Gets a colored image
     * 
     * @param image The image to get
     * @param color The color to change it to
     * @return The colored image
     */
    public static Image getColoredImage(Image image, Color color) {
        if (coloredImageMap.containsKey(image) &&
                colorToImageAssociation.containsKey(color) &&
                colorToImageAssociation.get(color) == coloredImageMap.get(image)) {
            return coloredImageMap.get(image);
        }
        Image newImage = reColor(image, Color.WHITE, color);
        coloredImageMap.put(image, newImage);
        colorToImageAssociation.put(color, newImage);
        return newImage;
    }

    /**
     * Create a new image with replaced colors from a given image and target
     * colors.
     * 
     * StackOverflow snippet changed to fix bugs and work with the project needs.
     * 
     * @author Wolfgang Fahl https://stackoverflow.com/a/51726678
     *         Author's notes: "reColor the given InputImage to the given color
     *         inspired by https://stackoverflow.com/a/12945629/1497139"
     * @param inputImage to generate from
     * @param oldColor   to replace
     * @param newColor   to set replaced color to
     * @return Image with the newly generated pixel colors
     */
    public static Image reColor(Image inputImage, Color oldColor, Color newColor) {
        int W = (int) inputImage.getWidth();
        int H = (int) inputImage.getHeight();
        WritableImage outputImage = new WritableImage(W, H);
        PixelReader reader = inputImage.getPixelReader();
        PixelWriter writer = outputImage.getPixelWriter();
        int ob = (int) (oldColor.getBlue() * 255);
        int or = (int) (oldColor.getRed() * 255);
        int og = (int) (oldColor.getGreen() * 255);
        int nb = (int) (newColor.getBlue() * 255);
        int nr = (int) (newColor.getRed() * 255);
        int ng = (int) (newColor.getGreen() * 255);
        for (int y = 0; y < H; y++) {
            for (int x = 0; x < W; x++) {
                int argb = reader.getArgb(x, y);
                int a = (argb >> 24) & 0xFF;
                int r = (argb >> 16) & 0xFF;
                int g = (argb >> 8) & 0xFF;
                int b = argb & 0xFF;
                if (g == og && r == or && b == ob) {
                    r = nr;
                    g = ng;
                    b = nb;
                }

                argb = (a << 24) | (r << 16) | (g << 8) | b;
                writer.setArgb(x, y, argb);
            }
        }
        return outputImage;
    }
}
