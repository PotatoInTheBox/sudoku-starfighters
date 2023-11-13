package view_controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

import javafx.animation.AnimationTimer;
import javafx.beans.value.ChangeListener;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
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

    private VBox centeringContainer = new VBox();

    private FrameRateTracker frameRateTracker = new FrameRateTracker(200);

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
        ChangeListener<Number> stageSizeListener = (observable, oldValue, newValue) -> {
            System.out.println("Height: " + gamePane.getHeight() + " Width: " + gamePane.getWidth());
            canvas.setWidth(gamePane.getWidth());
            canvas.setHeight(gamePane.getHeight());
        };
        gamePane.widthProperty().addListener(stageSizeListener);
        gamePane.heightProperty().addListener(stageSizeListener);
    }

    public void update() {
        drawRectangle(0, 0, canvas.getWidth(), canvas.getHeight(), Color.BLACK);

        long currentTime = System.currentTimeMillis();
        long truncatedTime = currentTime / 1000;
        int valueToPass = (truncatedTime % 2 == 0) ? 1 : 0;
        drawAllSprites(valueToPass);

        // drawAllWireFrames();
        double fpsAvg = frameRateTracker.getAverageUpdate();
        double tpsAvg = gamePane.frameRateTracker.getAverageUpdate();
        String fpsAverageString = String.format("Average FPS/UPS: %8.4f / %8.4f", fpsAvg, tpsAvg);

        drawText(fpsAverageString, 10, 15);
        drawText("Score: " + Integer.toString(game.getScore()), 10, 30);
        drawText("Lives: " + Integer.toString(game.getLives()), 10, 45);

        if (game.getLives() <= 0) {
            drawRectangle(165, 270, 250, 180, Color.BLACK);
            drawText("GAME OVER", 250, 300);
            drawText("ENTER NAME FOR LEADERBOARD", 200, 325);
            TextArea inputBox = new TextArea();
            Button submitButton = new Button("SUBMIT");
            centeringContainer.setAlignment(Pos.CENTER);
            centeringContainer.setPadding(new Insets(350, 10, 10, 190));
            centeringContainer.setSpacing(10);
            inputBox.setPrefSize(200, 20);
            centeringContainer.getChildren().addAll(inputBox, submitButton);
            gamePane.getChildren().add(centeringContainer);
            submitButton.setOnAction(event -> {
                if (!inputBox.getText().isBlank()) {
                    game.getUser().setUsername(inputBox.getText());
                    LeaderboardPane.topScores.add(game.getUser());
                    centeringContainer.getChildren().clear();
                    drawText("PRESS ESC TO RETURN TO MENU", 200, 350);
                }
            });
        }

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

    private void drawAllSprites(int animFrame) {
        drawSprite(playerSprite, new Point2D(game.getPlayer().getX(), game.getPlayer().getY()),
                new Point2D(game.getPlayer().getWidth(), game.getPlayer().getHeight()));
        for (Invader invader : game.getInvaders()) {
            Image invaderSprite = null;
            if (invader.getInvaderType() == InvaderType.ONION)
                invaderSprite = invaderSprites[0 + animFrame];
            else if (invader.getInvaderType() == InvaderType.SPIDER)
                invaderSprite = invaderSprites[2 + animFrame];
            else
                invaderSprite = invaderSprites[4 + animFrame]; // == InvaderType.MUSHROOM
            drawSprite(invaderSprite, new Point2D(invader.getX(), invader.getY()),
                    new Point2D(invader.getWidth(), invader.getHeight()));
        }
        for (Bullet bullet : game.getBullets()) {
            // reverse the bullet sprite if it is going down
            if (bullet.getDy() > 0f) {
                drawSprite(bulletSprite, new Point2D(bullet.getX(), bullet.getY() + bullet.getHeight()),
                        new Point2D(bullet.getWidth(), -bullet.getHeight()));
            } else {
                drawSprite(bulletSprite, new Point2D(bullet.getX(), bullet.getY()),
                        new Point2D(bullet.getWidth(), bullet.getHeight()));
            }

        }
    }

    private void drawSprite(Image image, Point2D startPoint, Point2D endPoint) {
        Point2D mappedStartPoint = mapGamePointOntoGraphics(startPoint);
        Point2D mappedEndPoint = mapGamePointOntoGraphics(endPoint);
        gc.drawImage(image, mappedStartPoint.getX(), mappedStartPoint.getY(),
                mappedEndPoint.getX(), mappedEndPoint.getY());
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
        drawWireframe(new Point2D(entity.getX(), entity.getY()),
                new Point2D(entity.getWidth(), entity.getHeight()), color);
    }

    private void drawWireframe(Point2D startPoint, Point2D endPoint, Color color) {
        Point2D mappedStartPoint = mapGamePointOntoGraphics(startPoint);
        Point2D mappedEndPoint = mapGamePointOntoGraphics(endPoint);
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
