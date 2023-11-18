package view_controller;

import java.util.ArrayList;
import java.util.List;

import javafx.animation.AnimationTimer;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import model.Bullet;
import model.Game;
import model.Team;
import view_controller.options.KeyBinding;
import view_controller.options.OptionsPane;

public class GamePane extends StackPane {

    private Input input;
    public Game game;
    public OptionsPane optionsPane;
    private GameOverPane gameOverPane;
    private Scene scene;
    private Graphics graphics;
    private Timer timer;
    private boolean isGamePaused = true;
    private long lastTime = 0l;
    private long unprocessedTime = 0l;
    private final long TARGET_NANO_TIME = 16_666_666L; // (1/60)
    public FrameRateTracker frameRateTracker = new FrameRateTracker(200);
    private boolean disabledInputValue = false;
    private boolean isRenderPaused = true;
    private boolean eventBlockedPause = false;

    public GamePane(Scene scene, Input input, OptionsPane optionsPane, double width, double height) {
        this.scene = scene;
        this.input = input;
        this.optionsPane = optionsPane;
        this.game = new Game((float) width, (float) height);
        this.graphics = new Graphics(this, width, height);
        this.gameOverPane = new GameOverPane();
        this.timer = new Timer();
        addButtonHandlers();
        SoundPlayer.loadAllSongs();
        this.getChildren().add(graphics);
        game.startNewRound();
        timer.start();
        unpauseGame();
    }

    private void addButtonHandlers() {
        input.onKeyDown(e -> {
            if (disabledInputValue) { // do not accept ANY inputs below
                return;
            }
            if (e.getCode().equals(input.getKeyFromType(KeyBinding.Type.FORCE_UNPAUSE))) {
                if (game.getLives() > 0) {
                    unpauseGame(); // force continue game
                }
            }
            if (e.getCode().equals(input.getKeyFromType(KeyBinding.Type.WIREFRAME))) {
                optionsPane.setWireframeEnabled(!optionsPane.isWireframeEnabled());
            }
            if (isGamePaused) { // do game inputs below
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
        isGamePaused = true;
    }

    public void unpauseGame() {
        // cannot unpause while the player is hit
        // cannot unpause if an event is blocking it
        if (!isGamePaused || eventBlockedPause || game.isPlayerHit()) {
            return; 
        }
        lastTime = System.nanoTime();
        isGamePaused = false;
    }

    public void pauseRender() {
        isRenderPaused = true;
    }

    public void unpauseRender() {
        isRenderPaused = false;
    }

    public boolean isGamePaused() {
        return isGamePaused;
    }

    public void promptGameOver() {
        this.getChildren().remove(gameOverPane);
        gameOverPane.showGameOver();
        this.getChildren().add(gameOverPane);
        gameOverPane.setOnSubmitButtonAction(event -> {
            if (!((TextField) event.getTarget()).getText().isBlank()) {
                game.getUser().setUsername(((TextField) event.getTarget()).getText());
                LeaderboardPane.topScores.add(game.getUser());
                LeaderboardPane.saveLeaderboard("saved_scores");
                gameOverPane.showSubmitted();
            }
        });
    }

    // update() is called every time javafx wants a new frame drawn
    private void update() {
        boolean hasUpdatedLogic = false;
        if (isGamePaused == false) {
            long currentTime = System.nanoTime();

            long deltaTime = currentTime - lastTime;
            lastTime = currentTime;
            unprocessedTime += deltaTime;

            while (unprocessedTime >= TARGET_NANO_TIME) {
                logicUpdate();
                frameRateTracker.logFrameUpdate();
                hasUpdatedLogic = true;
                unprocessedTime -= TARGET_NANO_TIME;
            }
        }

        boolean doNewFrame = true;
        if (!hasUpdatedLogic && optionsPane.isCapFpsEnabled())
            doNewFrame = false;

        if (doNewFrame)
            frameUpdate();
    }

    private void frameUpdate() {
        graphics.update();
    }

    private void logicUpdate() {
        game.movePlayer(input.getJoystickX(), input.getJoystickY());
        game.update();

        if (game.hasWon()) {
            pauseGame();
            winRound();
        } else if (game.isGameOver()) {
            pauseGame();
            loseGame();
        } else if (game.isPlayerHit()) {
            pauseGame();
            loseLife();
        }
    }

    private void loseGame() {
        // SoundPlayer.playSound("player_death.wav");
        SoundPlayer.playSound("game_over.mp3");
        //eventBlockedPause = true;
        promptGameOver();
    }

    private void loseLife() {
        SoundPlayer.playSound("player_death.wav");
        Thread thread = new Thread(() -> {
            eventBlockedPause = true;
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            game.startPlayerLife();
            eventBlockedPause = false;
            if (disabledInputValue == false) {
                unpauseGame();
            }
        });
        thread.start();
    }

    private void winRound() {
        Thread thread = new Thread(() -> {
            eventBlockedPause = true;
            try {
                Thread.sleep(1500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            game.increaseDifficulty();
            game.startNewRound();
            game.startPlayerLife();
            eventBlockedPause = false;
            if (disabledInputValue == false) {
                unpauseGame();
            }
        });
        thread.start();
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
