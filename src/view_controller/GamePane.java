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
    private Game game;
    private Scene scene;
    private Graphics graphics;
    private Timer timer;
    private long lastTime = 0l;

    public GamePane(Scene scene, Input input, double width, double height) {
        this.scene = scene;
        this.input = input;
        this.game = new Game((float) width, (float) height);
        this.graphics = new Graphics(scene, width, height, game);
        this.timer = new Timer();

        input.onKeyDown(e -> {
            if (e.getCode().equals(KeyCode.Z)) {
                Bullet bullet = game.getPlayer().shootBullet();
                game.bullets.add(bullet);
            }
        });
        getChildren().add(graphics);
        unpauseGame();
    }

    public void pauseGame() {
        timer.stop();
    }

    public void unpauseGame() {
        lastTime = System.currentTimeMillis();
        timer.start();
    }

    private void update() {
        // if (e.getCode().equals(KeyCode.Z)) {
        // Bullet bullet = game.getPlayer().shootBullet();
        // game.bullets.add(bullet);
        // }

        long currentTime = System.currentTimeMillis();
        if (currentTime - lastTime > 20 * 5) {
            // too many dropped frames, slow down the game and don't speed up
            lastTime = currentTime;
        }
        if (currentTime - lastTime > 20) {
            lastTime += 20;
            fixedUpdate();
        }
    }

    private void fixedUpdate() {
        game.movePlayer(input.getJoystickX(), input.getJoystickY());
        game.fixedUpdate();
        graphics.update();
    }

    private class Timer extends AnimationTimer {
        @Override
        public void handle(long arg0) {
            update();
        }
    }
}
