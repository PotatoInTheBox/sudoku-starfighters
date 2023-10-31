package model;

import java.util.ArrayList;
import java.util.List;

public class Game {

    public Player player;
    public ArrayList<Invader> invaders = new ArrayList<Invader>();
    public ArrayList<Bullet> bullets = new ArrayList<Bullet>();

    private float width;
    private float height;

    public Game() {
        this(100, 100);
    }

    public Game(float width, float height) {
        this.width = width;
        this.height = height;
        startGame();
    }

    public void startGame() {
        this.player = new Player(50f, 50f, 40f, 40f, 10f);
    }

    // This update is primarily for user input, game logic shouldn't go here
    // (can't guarentee that it will run at a constant rate)
    // TODO figure out a use for frame update loop.
    public void update() {

    }

    public void addBullet(Bullet bullet) {
        bullets.add(bullet);
    }

    // for rendering only
    public Player getPlayer() {
        return player;
    }

    // for rendering only
    public List<Invader> getInvaders() {
        return invaders;
    }

    // for rendering only
    public List<Bullet> getBullets() {
        return bullets;
    }

    public void movePlayer(float analogX, float analogY) {
        player.moveHorizontal(analogX);
        player.testMoveVertical(analogY);
    }

    // Game logic here, this will run at a constant rate.
    // CURRENTLY SET TO 50hz
    public void fixedUpdate() {
        for (Entity e : bullets) {
            e.move();
        }
        if (player.isOutOfBounds(0f, 0f, (float) width, (float) height)) {
            player.setX((float) width / 2);
            player.setY((float) height / 2);
        }
    }
}
