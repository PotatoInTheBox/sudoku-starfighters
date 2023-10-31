package view_controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javafx.animation.AnimationTimer;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import model.Bullet;
import model.Entity;
import model.Game;

public class Graphics extends Pane {

    private Game game;
    private Scene scene;
    private Canvas canvas;
    private GraphicsContext gc;

    private long lastFpsTime = 0l;
    private String fpsAverageString = "";
    private int fpsCounter = 0;
    private final int MAX_FRAME_COUNT = 60;
    private long[] fpsRecord = new long[MAX_FRAME_COUNT];

    Image playerSprite;

    public Graphics(Scene scene, double width, double height, Game game) {
        this.scene = scene;
        this.game = game;
        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();
        getChildren().add(canvas);
        loadSprites();
    }

    public void update() {
        drawRectangle(0, 0, canvas.getWidth(), canvas.getHeight(), Color.BLACK);
        drawAllSprites();
        drawAllWireFrames();
        updateFps();
        drawText(fpsAverageString, 10, 10);
    }

    private void updateFps() {
        if (fpsCounter >= MAX_FRAME_COUNT) {
            long total = 0l;
            for (int i = 0; i < MAX_FRAME_COUNT; i++) {
                total += fpsRecord[i];
            }
            double average = 1_000 / (total / 1_000d / (double) MAX_FRAME_COUNT);
            fpsAverageString = String.format("FPS: %.4f", average);
            fpsCounter = 0;
        }
        long thisTime = System.nanoTime();
        fpsRecord[fpsCounter] = (thisTime - lastFpsTime) / 1_000;
        lastFpsTime = thisTime;
        fpsCounter++;
    }

    private void loadSprites() {
        FileInputStream playerImageFile;
        try {
            playerImageFile = new FileInputStream("./resources/images/player_ship_scaled.png");
            playerSprite = new Image(playerImageFile);
        } catch (FileNotFoundException e) {
            System.out.println("Could not find ./resources/images/player_ship_scaled.png");
        }
        
    }

    private void drawAllSprites() {
        gc.drawImage(playerSprite, game.getPlayer().getX(), game.getPlayer().getY(), game.getPlayer().getWidth(), game.getPlayer().getHeight());
    }

    private void drawAllWireFrames() {
        drawWireFrame(game.getPlayer(), Color.RED);
        for (Entity e : game.bullets) {
            drawWireFrame(e, Color.YELLOW);
        }
    }

    private void drawText(String string, float x, float y) {
        gc.setLineWidth(1);
        gc.setStroke(Color.WHITE);
        gc.strokeText(string, x, y);
    }

    private void drawWireFrame(Entity entity, Color color) {
        drawWireframe(entity.getX(), entity.getY(),
                entity.getWidth(), entity.getHeight(), color);
    }

    private void drawWireframe(float x, float y, float w, float h, Color color) {
        gc.setStroke(color);
        gc.setLineWidth(2);
        gc.strokeLine(x, y, x, y + h); // left
        gc.strokeLine(x + w, y, x + w, y + h); // right
        gc.strokeLine(x, y, x + w, y); // top
        gc.strokeLine(x, y + h, x + w, y + h); // down
    }

    private void drawRectangle(double x, double y, double width, double height, Color color) {
        gc.setFill(color);
        gc.fillRect(x, y, width, height);
    }

    private void clearCanvas() {
        gc.clearRect(0, 0, canvas.getWidth(), canvas.getHeight());
    }
}
