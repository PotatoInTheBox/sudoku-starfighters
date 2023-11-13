package view_controller;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import model.Bullet;
import model.Game;
import model.Team;
import view_controller.options.KeyBinding;
import view_controller.options.OptionsPane;

public class GamePane extends StackPane {

    private Input input;
    public Game game;
    public OptionsPane optionsPane;
    private Scene scene;
    private Graphics graphics;
    private Timer timer;
    private boolean isPaused;
    private long lastTime = 0l;
    private long unprocessedTime = 0l;
    private final long TARGET_NANO_TIME = 16_666_666L; // (1/60)
    public FrameRateTracker frameRateTracker = new FrameRateTracker(200);
    private boolean disabledInputValue = false;

    public GamePane(Scene scene, Input input, OptionsPane optionsPane, double width, double height) {
        this.scene = scene;
        this.input = input;
        this.optionsPane = optionsPane;
        this.game = new Game((float) width, (float) height);
        this.graphics = new Graphics(this, width, height);
        this.timer = new Timer();
        addButtonHandlers();
        this.getChildren().add(graphics);
        game.startNewGame();
        unpauseGame();
    }

    private void addButtonHandlers() {
        input.onKeyDown(e -> {
            if (disabledInputValue){ // do not accept ANY inputs below
                return;
            }
            if (e.getCode().equals(input.getKeyFromType(KeyBinding.Type.FORCE_UNPAUSE))) {
                if (game.getLives() > 0) {
                    game.setPlayerHit(false);
                    unpauseGame(); // force continue game
                }
            }
            if (e.getCode().equals(input.getKeyFromType(KeyBinding.Type.WIREFRAME))){
                optionsPane.setWireframeEnabled(!optionsPane.isWireframeEnabled());
            }
            if (isPaused) { // do game inputs below
                return;
            }
            if (e.getCode().equals(input.getKeyFromType(KeyBinding.Type.FIRE))) {
                if (getActivePlayerBulletCount() < 1) {
                    game.shootPlayerBullet(); // better make that shot count xd
                }
            }
            if (e.getCode().equals(input.getKeyFromType(KeyBinding.Type.RAPID_FIRE))) {
                game.shootPlayerBullet(); // force bullet shoot anyways
            }
            
        });
    }

    public void pauseGame() {
        timer.stop();
        isPaused = true;
    }

    public void unpauseGame() {
        if (game.isPlayerHit()) {
            return; // cannot unpause while the player is hit
        }
        lastTime = System.nanoTime();
        isPaused = false;
        timer.start();
    }

    public boolean isPaused() {
        return isPaused;
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

    private int getActivePlayerBulletCount() {
        int playerBulletCount = 0;
        for (Bullet bullet : game.getBullets()) {
            if (bullet.getTeam() == Team.PLAYER) {
                playerBulletCount += 1;
            }
        }
        return playerBulletCount;
    }

    public void disabledInput(boolean value) {
        this.disabledInputValue = value;
    }
}
