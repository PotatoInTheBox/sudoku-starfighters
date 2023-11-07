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

    private GamePane gamePane;
    private Game game;
    private Canvas canvas;
    private GraphicsContext gc;

    private FrameRateTracker frameRateTracker = new FrameRateTracker(200);

    private Score score = new Score();

    Image playerSprite;
    Image[] invaderSprites;
    Image bulletSprite;

    public Graphics(GamePane gamePane, double width, double height) {
        this.gamePane = gamePane;
        this.game = gamePane.game;
        canvas = new Canvas(width, height);
        gc = canvas.getGraphicsContext2D();
        gc.setImageSmoothing(false);
        getChildren().add(canvas);
        loadSprites();
    }

    public void update() {
        drawRectangle(0, 0, canvas.getWidth(), canvas.getHeight(), Color.BLACK);
        drawAllSprites();
        //drawAllWireFrames();
        double fpsAvg = frameRateTracker.getAverageUpdate();
        double tpsAvg = gamePane.frameRateTracker.getAverageUpdate();
        String fpsAverageString = String.format("Average FPS/UPS: %8.4f / %8.4f", fpsAvg, tpsAvg);

        drawText(fpsAverageString, 10, 15);
        drawText("Score: " + Integer.toString(score.getScore()), 10, 30);
        drawText("Lives: " + Integer.toString(score.getLives()), 10, 45);

        frameRateTracker.logFrameUpdate();
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
        bulletSprite = getSpriteFromFile("./resources/images/bullet.png");
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
        for (Bullet bullet : game.getBullets()) {
            // reverse the bullet sprite if it is going down
            if (bullet.getDy() > 0f) {
                gc.drawImage(bulletSprite, bullet.getX(), bullet.getY() + bullet.getHeight(), bullet.getWidth(), -bullet.getHeight());
            } else {
                gc.drawImage(bulletSprite, bullet.getX(), bullet.getY(), bullet.getWidth(), bullet.getHeight());
            }

        }
    }

    private void drawAllWireFrames() {
        drawWireFrame(game.getPlayer(), Color.CYAN);
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
