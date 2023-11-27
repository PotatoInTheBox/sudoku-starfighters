package view_controller.panel;

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
import model.Entity;
import model.Game;
import model.Team;
import view_controller.graphic.Graphics;
import view_controller.sound.SoundPlayer;
import view_controller.utils.FrameRateTracker;
import view_controller.utils.Input;
import view_controller.utils.KeyBinding;

public class GamePane extends StackPane {

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
    private boolean isRenderPaused = false;
    private boolean eventBlockedPause = false;

    public GamePane(Scene scene, OptionsPane optionsPane, double width, double height) {
        this.scene = scene;
        this.optionsPane = optionsPane;
        this.game = new Game((float) width, (float) height);
        this.graphics = new Graphics(this, width, height);
        this.gameOverPane = new GameOverPane();
        this.timer = new Timer();
        addButtonHandlers();
        SoundPlayer.loadAllSongs();
        this.getChildren().add(graphics);
        game.startGame();
        timer.start();
        unpauseGame();
    }

    /**
     * Adds key handlers for controls and button presses
     */
    private void addButtonHandlers() {
        Input.onKeyDown(e -> {
            if (disabledInputValue) { // do not accept ANY inputs below
                return;
            }
            if (e.getCode().equals(Input.getKeyFromType(KeyBinding.Type.FORCE_UNPAUSE))) {
                if (game.getLives() > 0) {
                    unpauseGame(); // force continue game
                }
            }
            if (e.getCode().equals(Input.getKeyFromType(KeyBinding.Type.WIREFRAME))) {
                optionsPane.setWireframeEnabled(!optionsPane.isWireframeEnabled());
            }
            if (isGamePaused) { // do game inputs below
                return;
            }

        });
    }

    /**
     * Pauses the game
     */
    public void pauseGame() {
        isGamePaused = true;
    }

    /**
     * Resumes the Game
     */
    public void unpauseGame() {
        // cannot unpause while the player is hit
        // cannot unpause if an event is blocking it
        if (!isGamePaused || eventBlockedPause || game.isPlayerHit()) {
            return;
        }
        lastTime = System.nanoTime();
        isGamePaused = false;
    }

    /**
     * Pauses Rendering
     */
    public void pauseRender() {
        isRenderPaused = true;
    }

    /**
     * Resumes rendering
     */
    public void unpauseRender() {
        isRenderPaused = false;
    }

    /**
     * Specifies if the game is paused
     * 
     * @return True if game is paused
     */
    public boolean isGamePaused() {
        return isGamePaused;
    }

    /**
     * Prompt for game over
     */
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

    /**
     * Called every time there is a new frame drawn
     */
    private void update() {
        // boolean hasUpdatedLogic = false;
        long currentTime = System.nanoTime();

        long deltaTime = currentTime - lastTime;
        lastTime = currentTime;
        unprocessedTime += deltaTime;

        // if we are behind by >6 frames, start dropping them
        if (unprocessedTime > 100_000_000) {
            unprocessedTime = 100_000_000;
        }

        while (unprocessedTime >= TARGET_NANO_TIME) {

            if (isGamePaused == false) {
                logicUpdate();
                frameRateTracker.logFrameUpdate();
            }
            // hasUpdatedLogic = true;

            if (!isRenderPaused && optionsPane.isCapFpsEnabled())
                frameUpdate();
            unprocessedTime -= TARGET_NANO_TIME;
        }

        // boolean doNewFrame = true;
        // if (!hasUpdatedLogic && optionsPane.isCapFpsEnabled())
        // doNewFrame = false;

        if (optionsPane.isCapFpsEnabled() == false)
            frameUpdate();
    }

    /**
     * Calls graphics update
     */
    private void frameUpdate() {
        graphics.update();
    }

    /**
     * Calls logic update
     */
    private void logicUpdate() {
        // game.movePlayer(Input.getJoystickX(), Input.getJoystickY());

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

    /**
     * Activates when game is lost
     */
    private void loseGame() {
        // SoundPlayer.playSound("player_death.wav");
        SoundPlayer.playSound("game_over.mp3", false);
        // eventBlockedPause = true;
        promptGameOver();
    }

    /**
     * Activates when a life is lost
     */
    private void loseLife() {
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

    /**
     * Activates when the round is won
     */
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

    /**
     * Gets the active player bullets
     * 
     * @return The number of player bullets
     */
    private int getActivePlayerBulletCount() {
        int playerBulletCount = 0;
        // for (Bullet bullet : game.getBullets()) {
        // if (bullet.getTeam() == Team.PLAYER) {
        // playerBulletCount += 1;
        // }
        // }
        return playerBulletCount;
    }

    /**
     * Disables player input
     * 
     * @param value True if input is to be disabled
     */
    public void disabledInput(boolean value) {
        this.disabledInputValue = value;
    }

    public void delete() {
        pauseGame();
        timer.stop();
        game.delete();
    }
}
