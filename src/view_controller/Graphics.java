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
import model.Invader;
import model.InvaderType;
import model.Score;

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

    private Score score = new Score();

    Image playerSprite;
    Image[] invaderSprites;

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
        drawText(fpsAverageString, 10, 15);
        drawText("Score: " + Integer.toString(score.getScore()), 10, 30);
        drawText("Lives: " + Integer.toString(score.getLives()), 10, 45);
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
        playerSprite = getSpriteFromFile("./resources/images/player_ship.png");
        invaderSprites = new Image[6];
        invaderSprites[0] = getSpriteFromFile("./resources/images/enemy1_frame1.png");
        invaderSprites[1] = getSpriteFromFile("./resources/images/enemy1_frame2.png");
        invaderSprites[2] = getSpriteFromFile("./resources/images/enemy2_frame1.png");
        invaderSprites[3] = getSpriteFromFile("./resources/images/enemy2_frame2.png");
        invaderSprites[4] = getSpriteFromFile("./resources/images/enemy3_frame1.png");
        invaderSprites[5] = getSpriteFromFile("./resources/images/enemy3_frame2.png");
    }

    private Image getSpriteFromFile(String path) {
        FileInputStream playerImageFile;
        try {
            playerImageFile = new FileInputStream(path);
            Image sprite = new Image(playerImageFile);
            return sprite;
        } catch (FileNotFoundException e) {
            System.out.println("Could not find" + path);
            return null;
        }
    }

    private void drawAllSprites() {
        gc.drawImage(playerSprite, game.getPlayer().getX(), game.getPlayer().getY(), game.getPlayer().getWidth(),
                game.getPlayer().getHeight());
        for (Invader invader : game.getInvaders()) {
            Image invaderSprite = null;
            if (invader.getInvaderType() == InvaderType.ONION)
                invaderSprite = invaderSprites[0];
            else if (invader.getInvaderType() == InvaderType.SPIDER)
                invaderSprite = invaderSprites[1];
            else
                invaderSprite = invaderSprites[2]; // == InvaderType.MUSHROOM
            gc.drawImage(invaderSprite, invader.getX(), invader.getY(), invader.getWidth(), invader.getHeight());
        }
    }

    private void drawAllWireFrames() {
        drawWireFrame(game.getPlayer(), Color.RED);
        for (Entity e : game.getBullets()) {
            drawWireFrame(e, Color.YELLOW);
        }
        for (Entity e : game.getInvaders()) {
            drawWireFrame(e, Color.RED);
        }
    }

    private void drawText(String string, float x, float y) {
        gc.setLineWidth(1);
        gc.setStroke(Color.WHITE);
        gc.strokeText(string, x, y + 5);
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
