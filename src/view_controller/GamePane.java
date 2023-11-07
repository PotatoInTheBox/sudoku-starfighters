package view_controller;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import model.Bullet;
import model.Game;

public class GamePane extends Pane {

    private Input input;
    public Game game;
    private Scene scene;
    private Graphics graphics;
    private Timer timer;
    private long lastTime = 0l;
    private long unprocessedTime = 0l;
    private final long TARGET_NANO_TIME = 16_666_666L; // (1/60)
    public FrameRateTracker frameRateTracker = new FrameRateTracker(200);

    public GamePane(Scene scene, Input input, double width, double height) {
        this.scene = scene;
        this.input = input;
        this.game = new Game((float) width, (float) height);
        this.graphics = new Graphics(this, width, height);
        this.timer = new Timer();

        input.onKeyDown(e -> {
            if (e.getCode().equals(KeyCode.Z)) {
                game.shootPlayerBullet();
            }
            if (e.getCode().equals(KeyCode.SPACE)) {
                game.setPlayerHit(false);
                unpauseGame();
            }
        });
        getChildren().add(graphics);
        game.startNewGame();
        unpauseGame();
    }

    public void pauseGame() {
        timer.stop();
    }

    public void unpauseGame() {
        lastTime = System.nanoTime();
        timer.start();
    }

    // update() is called every time javafx wants a new frame drawn
    private void update() {
        long currentTime = System.nanoTime();
        boolean hasUpdatedLogic = false;

        long deltaTime = currentTime - lastTime;
        lastTime = currentTime;
        unprocessedTime += deltaTime;

        while (unprocessedTime >= TARGET_NANO_TIME) {
            logicUpdate();
            frameRateTracker.logFrameUpdate();
            hasUpdatedLogic = true;
            unprocessedTime -= TARGET_NANO_TIME;
        }
        if (hasUpdatedLogic)
            frameUpdate(); // CAP FPS TO GAME LOGIC UPDATE (below 60hz is ok but not above it)
    }

    private void frameUpdate() {
        graphics.update();
    }

    private void logicUpdate() {
        game.movePlayer(input.getJoystickX(), input.getJoystickY());
        game.update();

        if (game.isPlayerHit()) {
            pauseGame();
            System.out.println("Player has been hit, the game has been paused"
                    + ", press space to override.");
        }
    }

    private class Timer extends AnimationTimer {
        @Override
        public void handle(long arg0) {
            update();
        }
    }
}
