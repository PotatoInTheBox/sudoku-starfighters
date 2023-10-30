package view_controller;

import javafx.scene.Scene;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.animation.AnimationTimer;
import model.Bullet;
import model.Entity;
import model.Game;

public class GamePane extends Pane {
    private Input input;
    private Game game;
    private Scene scene;
    private Graphics graphics;

    private long lastTime = 0l;

    public GamePane(Scene scene, double width, double height) {
        this.scene = scene;
        this.input = new Input(scene);
        this.game = new Game((float) width, (float) height);
        this.graphics = new Graphics(scene, width, height, game);
        getChildren().add(graphics);

        input.onKeyDown(e -> {
            if (e.getCode().equals(KeyCode.Z)) {
                Bullet bullet = game.getPlayer().shootBullet();
                game.bullets.add(bullet);
            }
        });

        Timer newTimer = new Timer();
        lastTime = System.currentTimeMillis();
        newTimer.start();
    }

    private void fixedUpdate() {
        game.movePlayer(input.getJoystickX(), input.getJoystickY());
        game.fixedUpdate();
        graphics.update();
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

    private class Timer extends AnimationTimer {
        @Override
        public void handle(long arg0) {
            update();
        }

    }
}
