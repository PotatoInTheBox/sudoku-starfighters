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
import model.Bullet;
import model.Collider;
import model.Entity;
import model.Game;
import model.Invader;
import model.InvaderCluster;
import model.InvaderType;
import model.Player;
import model.Score;
import model.Sprite;
import view_controller.panel.GamePane;
import view_controller.panel.OptionsPane;
import view_controller.utils.FrameRateTracker;

import java.util.ConcurrentModificationException;

public class Graphics extends VBox {

    private GamePane gamePane;
    public Game game;
    private OptionsPane optionsPane;
    private Canvas canvas;
    private GraphicsContext gc;

    private FrameRateTracker frameRateTracker = new FrameRateTracker(200);

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

    public void update() {
        // clear screen
        drawRectangle(0, 0, canvas.getWidth(), canvas.getHeight(), Color.BLACK);

        try {
            drawSprites();
            if (optionsPane.isWireframeEnabled())
                drawAllWireFrames();
        } catch (ConcurrentModificationException e) {
            // TODO concurrent modification sometimes happens,
            // unsure why...
            System.err.println(e);
        }

        double fpsAvg = frameRateTracker.getAverageUpdate();
        double tpsAvg = gamePane.frameRateTracker.getAverageUpdate();
        String fpsAverageString = String.format("Average FPS/UPS: %8.4f / %8.4f", fpsAvg, tpsAvg);

        drawText(fpsAverageString, 10, 15);
        drawText("Score: " + Integer.toString(game.getScore()), 10, 30);
        drawText("Lives: " + Integer.toString(game.getLives()), 10, 45);

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

    private void drawSprite(Image image, Point2D startPoint, Point2D size) {
        Point2D mappedStartPoint = mapGamePointOntoGraphics(startPoint);
        Point2D mappedEndPoint = mapGamePointOntoGraphics(size);
        gc.drawImage(image, mappedStartPoint.getX(), mappedStartPoint.getY(),
                mappedEndPoint.getX(), mappedEndPoint.getY());
    }

    private void drawAllWireFrames() {
        for (Entity e : game.getEntities()) {
            drawWireFrame(e);
        }

    }

    private void drawText(String string, float x, float y) {
        gc.setLineWidth(1);
        gc.setStroke(Color.WHITE);
        gc.strokeText(string, x, y + 5);
    }

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

    private void drawRectangle(double x, double y, double width, double height, Color color) {
        gc.setFill(color);
        gc.fillRect(x, y, width, height);
    }

    private Point2D mapGamePointOntoGraphics(Point2D point) {
        double graphicsWidth = canvas.getWidth();
        double graphicsHeight = canvas.getHeight();
        double gameWidth = game.getWidth();
        double gameHeight = game.getHeight();

        double scaleX = graphicsWidth / gameWidth;
        double scaleY = graphicsHeight / gameHeight;

        return new Point2D(point.getX() * scaleX, point.getY() * scaleY);
    }

    private void clearCanvas() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }

}

class DestructionEntity {
    private Point2D originalPos;
    private Point2D endPos;
    private int framesActive = 20;

    public DestructionEntity(Point2D originalPos, Point2D endPos) {
        this.originalPos = originalPos;
        this.endPos = endPos;
    }

    public Point2D getOriginalPos() {
        return originalPos;
    }

    public Point2D getEndPos() {
        return endPos;
    }

    public boolean update() {
        if (framesActive == 0) {
            return true;
        }

        framesActive--;
        return false;
    }

}
