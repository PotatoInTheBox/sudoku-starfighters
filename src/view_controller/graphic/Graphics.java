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
import javafx.scene.effect.Bloom;
import javafx.scene.image.Image;
import javafx.scene.image.PixelReader;
import javafx.scene.image.PixelWriter;
import javafx.scene.image.WritableImage;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.Priority;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import model.Bullet;
import model.Collider;
import model.Entity;
import model.Game;
import model.House;
import model.Invader;
import model.InvaderCluster;
import model.InvaderType;
import model.Player;
import model.Score;
import model.Sprite;
import model.Turret;
import view_controller.panel.GamePane;
import view_controller.panel.OptionsPane;
import view_controller.utils.FrameRateTracker;

import java.util.ConcurrentModificationException;

/**
 * Graphics is responsible for drawing all the sprites from Game over to a
 * canvas so the player can see the game.
 */
public class Graphics extends VBox {

    private GamePane gamePane;
    public Game game;
    private OptionsPane optionsPane;
    private Canvas canvas;
    private GraphicsContext gc;

    private FrameRateTracker frameRateTracker = new FrameRateTracker(200);

    Image playerSprite;
    Image[] invaderSprites;
    Image[] bulletSprites;
    Image[] houseSprites;
    Image destructionSprite;
    Image bulletSprite;

    /**
     * Construct graphics with a canvas to begin drawing at. The gamepane
     * provided is needed to access the game. The graphics will access game to
     * draw on every .update() call from gamepane.
     * 
     * @param gamePane to get information about game and pane from
     * @param width    of the graphics pane/canvas
     * @param height   of the graphics pane/canvas
     */
    public Graphics(GamePane gamePane, double width, double height) {
        this.gamePane = gamePane;
        this.game = gamePane.game;
        this.optionsPane = gamePane.optionsPane;
        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();
        gc.setImageSmoothing(false);

        Pane dummyResizePane = new Pane(canvas);
        setVgrow(dummyResizePane, Priority.ALWAYS);
        setAlignment(Pos.CENTER);
        this.getChildren().add(dummyResizePane);

        // add event for resizing
        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> {
            // restrict the game to 1:1 screen ratio
            double smallestLen = Math.min(this.getWidth(), this.getHeight());
            canvas.setTranslateX(Math.max((this.getWidth() - this.getHeight()) / 2, 0));
            canvas.setTranslateY(Math.max((this.getHeight() - this.getWidth()) / 2, 0));
            canvas.setWidth(smallestLen);
            canvas.setHeight(smallestLen);
        };
        this.widthProperty().addListener(stageSizeListener);
        this.heightProperty().addListener(stageSizeListener);
    }

    /**
     * Updates all the graphics on a frame by frame basis
     */
    public void update() {
        // clear screen
        drawRectangle(0, 0, canvas.getWidth(), canvas.getHeight(), Color.BLACK);

        // double fpsAvg = frameRateTracker.getAverageUpdate();
        // double tpsAvg = gamePane.frameRateTracker.getAverageUpdate();
        // String fpsAverageString = String.format("Average FPS/UPS: %8.4f / %8.4f",
        // fpsAvg, tpsAvg);

        // drawText(fpsAverageString, 10, 15);
        drawText(Integer.toString(game.getScore()), (float) canvas.getWidth() / 2, (float) canvas.getHeight() / 3, 75,
                0.2f);
        for (int i = 0; i < game.getLives(); i++) {
            drawSprite(Sprites.loadImage("heart.png"), new Point2D(500 + 30 * i, 565), new Point2D(25, 25));
        }
        // drawText("Lives: " + Integer.toString(game.getLives()), 10, 45);
        if (game.getPlayer() != null) {
            drawText("Coins: " + Integer.toString(game.getPlayer().coins), 45, (float)canvas.getHeight()-20, 20, 0.7f);
        }

        try {
            drawSprites();
            if (optionsPane.isWireframeEnabled())
                drawAllWireFrames();
        } catch (ConcurrentModificationException e) {
            // System.err.println(e);
        }

        frameRateTracker.logFrameUpdate();
    }

    private void drawSprites() {
        for (Entity entity : game.getEntities()) {
            if (entity.getClass() == Sprite.class) {
                Sprite sprite = (Sprite) entity;
                float spriteX = sprite.getX();
                float spriteY = sprite.getY();
                if (sprite.getParent() == null) {
                    System.out.println("Orphan sprite " + sprite);
                }
                drawSprite(sprite.getImage(), new Point2D(spriteX, spriteY),
                        new Point2D(sprite.getWidth(), sprite.getHeight()));
            }
        }
    }

    // /**
    // * Gets a sprite from a file
    // * @param path The path to the Image
    // * @return The final Image
    // */
    // private Image getSpriteFromFile(String path) {
    // FileInputStream playerImageFile;
    // try {
    // playerImageFile = new FileInputStream(path);
    // Image sprite = new Image(playerImageFile);
    // return sprite;
    // } catch (FileNotFoundException e) {
    // System.out.println("Could not find" + path);
    // return null;
    // }
    // }

    /**
     * Draw sprite from game onto screen.
     * 
     * @param image      sprite image to draw
     * @param startPoint point to draw (from game)
     * @param size       size to draw at (from game)
     */
    private void drawSprite(Image image, Point2D startPoint, Point2D size) {
        Point2D mappedStartPoint = mapGamePointOntoGraphics(startPoint);
        Point2D mappedEndPoint = mapGamePointOntoGraphics(size);
        gc.drawImage(image, mappedStartPoint.getX(), mappedStartPoint.getY(),
                mappedEndPoint.getX(), mappedEndPoint.getY());
    }

    /**
     * Draw the wire frames for all entities
     */
    private void drawAllWireFrames() {
        for (Entity e : game.getEntities()) {
            drawWireFrame(e);
        }

    }

    // /*
    // * Draws text
    // */
    // private void drawText(String string, float x, float y) {
    // gc.setLineWidth(1);
    // gc.setStroke(Color.WHITE);
    // gc.strokeText(string, x, y + 5);
    // }

    /**
     * Draw text at a given position and size.
     * 
     * @param string  text to use
     * @param x       position to draw at (relative to pane)
     * @param y       position to draw at (relative to pane)
     * @param width   of the text
     * @param opacity of the text
     */
    private void drawText(String string, float x, float y, float width, float opacity) {
        gc.setLineWidth(1);
        gc.setStroke(Color.WHITE);

        gc.setGlobalAlpha(opacity);
        gc.setFont(Font.font(width));
        gc.setTextAlign(TextAlignment.CENTER);

        gc.strokeText(string, x, y + 5);

        gc.setGlobalAlpha(1);
        gc.setTextAlign(TextAlignment.LEFT);
        gc.setFont(Font.font(12));
    }

    /**
     * Draw wireframe as debug (only targets colliders instead of all rectangles)
     * 
     * @param entity to draw wireframe for (will only draw if it is a collider)
     */
    private void drawWireFrame(Entity entity) {
        for (Entity subEntity : entity.getChildren()) {
            if (subEntity.getClass() == Collider.class) {
                Collider collider = (Collider) subEntity;
                drawWireframe(new Point2D(collider.getX(), collider.getY()),
                        new Point2D(collider.getWidth(), collider.getHeight()), Color.WHITESMOKE);
            }
            drawCircleWireframe(new Point2D(subEntity.getX(), subEntity.getY()),
                    new Point2D(5, 5), Color.CYAN);
        }

    }

    /**
     * Draws a wire frame
     * 
     * @param startPoint Start of the wire frame
     * @param size       The size of the lines to draw
     * @param color      The color of the frame
     */
    private void drawWireframe(Point2D startPoint, Point2D size, Color color) {
        Point2D mappedStartPoint = mapGamePointOntoGraphics(startPoint);
        Point2D mappedEndPoint = mapGamePointOntoGraphics(size);
        double x = mappedStartPoint.getX();
        double y = mappedStartPoint.getY();
        double w = mappedEndPoint.getX();
        double h = mappedEndPoint.getY();
        gc.setStroke(color);
        gc.setLineWidth(2);
        gc.strokeLine(x, y, x, y + h); // left
        gc.strokeLine(x + w, y, x + w, y + h); // right
        gc.strokeLine(x, y, x + w, y); // top
        gc.strokeLine(x, y + h, x + w, y + h); // down
    }

    /**
     * Draw a circle wire frame
     * 
     * @param startPoint Start of the wire frame
     * @param size       The size of the lines to draw
     * @param color      The color of the frame
     */
    private void drawCircleWireframe(Point2D startPoint, Point2D size, Color color) {
        Point2D mappedStartPoint = mapGamePointOntoGraphics(startPoint);
        Point2D mappedEndPoint = mapGamePointOntoGraphics(size);
        double x = mappedStartPoint.getX();
        double y = mappedStartPoint.getY();
        double w = mappedEndPoint.getX();
        double h = mappedEndPoint.getY();
        gc.setStroke(color);
        gc.setLineWidth(2);
        gc.strokeOval(x, y, w, h);
    }

    /**
     * Draws a rectangle
     * 
     * @param x      The x coordinate
     * @param y      The y coordinate
     * @param width  The width of the rectangle
     * @param height The height of the rectangle
     * @param color  The color of the rectangle
     */
    private void drawRectangle(double x, double y, double width, double height, Color color) {
        gc.setFill(color);
        gc.fillRect(x, y, width, height);
    }

    /**
     * Maps a game point to the graphics
     * 
     * @param point The point to map to
     * @return The point on the graphics
     */
    private Point2D mapGamePointOntoGraphics(Point2D point) {
        double graphicsWidth = canvas.getWidth();
        double graphicsHeight = canvas.getHeight();
        double gameWidth = game.getWidth();
        double gameHeight = game.getHeight();

        double scaleX = graphicsWidth / gameWidth;
        double scaleY = graphicsHeight / gameHeight;

        return new Point2D(point.getX() * scaleX, point.getY() * scaleY);
    }

    /**
     * Clears the canvas
     */
    private void clearCanvas() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

}
