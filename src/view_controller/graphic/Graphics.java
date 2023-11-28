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

    private int bulletAnimationCounter = 0;
    private boolean bulletTickedThisFrame = false;

    private FrameRateTracker frameRateTracker = new FrameRateTracker(200);

    private ArrayList<DestructionEntity> destructionEntities = new ArrayList<>();

    Image playerSprite;
    Image[] invaderSprites;
    Image[] bulletSprites;
    Image destructionSprite;

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

        loadSprites();

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
        drawRectangle(0, 0, canvas.getWidth(), canvas.getHeight(), Color.BLACK);

        long currentTime = System.currentTimeMillis();
        long truncatedTime = currentTime / 1000;
        int valueToPass = (truncatedTime % 2 == 0) ? 1 : 0;
        if ((currentTime / 16) % 2 == 0) {
            if (bulletTickedThisFrame == false) {
                bulletAnimationCounter += 1;
                bulletTickedThisFrame = true;
            }
        } else {
            bulletTickedThisFrame = false;
        }

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

    private void loadSprites() {
        playerSprite = getSpriteFromFile("./resources/images/player_ship.png");
        invaderSprites = new Image[6];
        invaderSprites[0] = getSpriteFromFile("./resources/images/enemy1_frame1.png");
        invaderSprites[1] = getSpriteFromFile("./resources/images/enemy1_frame2.png");
        invaderSprites[0] = debugTempReColor(invaderSprites[0], Color.WHITE, Color.web("#ffff73"));
        invaderSprites[1] = debugTempReColor(invaderSprites[1], Color.WHITE, Color.web("#ffff73"));
        invaderSprites[2] = getSpriteFromFile("./resources/images/enemy2_frame1.png");
        invaderSprites[3] = getSpriteFromFile("./resources/images/enemy2_frame2.png");
        invaderSprites[2] = debugTempReColor(invaderSprites[2], Color.WHITE, Color.web("#da73ff"));
        invaderSprites[3] = debugTempReColor(invaderSprites[3], Color.WHITE, Color.web("#da73ff"));
        invaderSprites[4] = getSpriteFromFile("./resources/images/enemy3_frame1.png");
        invaderSprites[5] = getSpriteFromFile("./resources/images/enemy3_frame2.png");
        invaderSprites[4] = debugTempReColor(invaderSprites[4], Color.WHITE, Color.web("#ff7373"));
        invaderSprites[5] = debugTempReColor(invaderSprites[5], Color.WHITE, Color.web("#ff7373"));
        Image bulletSprite = getSpriteFromFile("./resources/images/bullet4.png");
        bulletSprites = generateBulletAnimation(bulletSprite);
        destructionSprite = getSpriteFromFile("./resources/images/destruction_frame.png");
    }

    public static Image[] generateBulletAnimation(Image inputImage) {
        int W = (int) inputImage.getWidth();
        int H = (int) inputImage.getHeight();
        final int FRAME_COUNT = H;
        Image[] bulletSprites = new Image[FRAME_COUNT];
        bulletSprites[0] = inputImage;
        PixelReader reader = inputImage.getPixelReader();
        for (int i = 1; i < FRAME_COUNT; i++) {
            WritableImage outputImage = new WritableImage(W, H);
            PixelWriter writer = outputImage.getPixelWriter();
            for (int y = 0; y < H; y++) {
                for (int x = 0; x < W; x++) {
                    int argb = reader.getArgb(x, (y + i) % H);
                    writer.setArgb(x, y, argb);
                }
            }
            bulletSprites[i] = outputImage;
        }
        return bulletSprites;
    }

    /**
     * reColor the given InputImage to the given color
     * inspired by https://stackoverflow.com/a/12945629/1497139
     * 
     * @author Wolfgang Fahl https://stackoverflow.com/a/51726678
     * 
     * @param inputImage
     * @param oldColor
     * @param newColor
     * @return reColored Image
     * 
     */
    public static Image debugTempReColor(Image inputImage, Color oldColor, Color newColor) {
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

        drawPlayer();
        drawInvaders(animFrame);
        drawBullets();
        drawDestroyed();
    }

    private void drawDestroyed() {
        // for (Entity e : game.markedForRemoval) {
        // destructionEntities.add(new DestructionEntity(new Point2D(e.getX(),
        // e.getY()),
        // new Point2D(10, 10)));
        // }

        ArrayList<DestructionEntity> toRemove = new ArrayList<>();

        for (DestructionEntity e : destructionEntities) {
            drawSprite(destructionSprite, e.getOriginalPos(),
                    e.getEndPos());
            if (e.update()) {
                toRemove.add(e);
            }
        }

        for (DestructionEntity e : toRemove) {
            destructionEntities.remove(e);
        }
    }

    private void drawPlayer() {
        double targetWidth = 40;
        double targetHeight = 40;
        double widthMult = targetWidth / game.getPlayer().collider.getWidth();
        double heightMult = targetHeight / game.getPlayer().collider.getHeight();
        double x = game.getPlayer().getX() - game.getPlayer().collider.getWidth() * (widthMult - 1) / 2;
        double y = game.getPlayer().getY() - game.getPlayer().collider.getHeight() * (heightMult - 1) / 2;
        double w = game.getPlayer().collider.getWidth() * widthMult;
        double h = game.getPlayer().collider.getHeight() * heightMult;

        drawSprite(playerSprite, new Point2D(x, y), new Point2D(w, h));
    }

    private void drawBullets() {
        int bulletSpriteIndex = Integer.remainderUnsigned(bulletAnimationCounter, bulletSprites.length);
        double widthMult = 1.4;
        double heightMult = 1.4;
        Iterator<Bullet> bullets = game.getBullets();
        while (bullets.hasNext()) {
            Bullet bullet = bullets.next();
            double x = bullet.getX() - bullet.collider.getWidth() * (widthMult - 1) / 2;
            double y = bullet.getY() - bullet.collider.getHeight() * (heightMult - 1) / 2;
            double w = bullet.collider.getWidth() * widthMult;
            double h = bullet.collider.getHeight() * heightMult;
            // reverse the bullet sprite if it is going down
            if (bullet.getDy() > 0f) {
                drawSprite(bulletSprites[bulletSpriteIndex],
                        new Point2D(x, y + h),
                        new Point2D(w, -h));
            } else {
                drawSprite(bulletSprites[bulletSpriteIndex],
                        new Point2D(x, y),
                        new Point2D(w, h));
            }
        }
    }

    private void drawInvaders(int animFrame) {
        Iterator<Invader> invaders = game.getInvaders();
        while (invaders.hasNext()) {
            Invader invader = invaders.next();
            Image invaderSprite = null;
            if (invader.getInvaderType() == InvaderType.ONION)
                invaderSprite = invaderSprites[0 + animFrame];
            else if (invader.getInvaderType() == InvaderType.SPIDER)
                invaderSprite = invaderSprites[2 + animFrame];
            else
                invaderSprite = invaderSprites[4 + animFrame]; // == InvaderType.MUSHROOM
            drawSprite(invaderSprite, new Point2D(invader.getX(), invader.getY()),
                    new Point2D(invader.collider.getWidth(), invader.collider.getHeight()));
        }
    }

    private void drawSprite(Image image, Point2D startPoint, Point2D size) {
        Point2D mappedStartPoint = mapGamePointOntoGraphics(startPoint);
        Point2D mappedEndPoint = mapGamePointOntoGraphics(size);
        gc.drawImage(image, mappedStartPoint.getX(), mappedStartPoint.getY(),
                mappedEndPoint.getX(), mappedEndPoint.getY());
    }

    private void drawAllWireFrames() {
        // drawWireFrame(game.getPlayer());

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
        }

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
