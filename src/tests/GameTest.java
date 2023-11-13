package tests;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;

import model.Bullet;
import model.Entity;
import model.Game;
import model.Invader;
import model.Player;
import model.Team;

public class GameTest {
    @Test
    void testGetBullets() {
        Game game = new Game();
        List<Bullet> bullets = game.getBullets();
        bullets.add(new Bullet(0, 0, 1, null));
        bullets.add(new Bullet(0, 0, 1, null));
        Bullet uniqueBullet = new Bullet(0, 10, 1, null);
        bullets.add(uniqueBullet);
        assertEquals(3, bullets.size());
        bullets.remove(uniqueBullet);
        assertEquals(false, bullets.contains(uniqueBullet));
        assertEquals(2, bullets.size());
    }

    // @Test
    // void testGetInvaders() {

    // }

    @Test
    void testGetPlayer() {
        Game game = new Game();
        game.spawnPlayer(0, 0, 10, 10);
        assertNotEquals(null, game.getPlayer());
    }

    // @Test
    // void testStartGame() {
    // // game should do something, not specified yet...
    // }

    // normal updates are frame based and should not handle the game
    // state. Instead, it's useful for things like getting a keypress
    // @Test
    // void testUpdate() {

    // }

    // fixed update as the name implies should be ticking the game state
    // by one. This may involve checking collisions, moving entities,
    // and other deterministic logic.

    @Test
    void testFixedUpdate() {
        Game game = new Game();
        game.spawnPlayer(0, 0, 10, 10);
        // avoid accessing player for purposes like these (unless testing)
        Bullet bullet = game.getPlayer().shootBullet();
        game.addBullet(bullet);
        float playerX, bulletY;
        playerX = game.getPlayer().getX();
        bulletY = bullet.getY();

        game.getPlayer().moveHorizontal(1f); // move player
        game.update(); // let some time pass
        float newPlayerX, newBulletY;
        newPlayerX = game.getPlayer().getX();
        newBulletY = bullet.getY();
        // TODO add to make sure Invaders move too
        // Bullet should have moved, player should have moved
        // Assuming player moves horizontally and bullet moves vertically
        assertNotEquals(playerX, newPlayerX, 0.0001f);
        assertNotEquals(bulletY, newBulletY, 0.0001f);

        game.update(); // let some more time pass

        playerX = newPlayerX;
        bulletY = newBulletY;
        newPlayerX = game.getPlayer().getX();
        newBulletY = bullet.getY();

        assertEquals(playerX, newPlayerX, 0.0001f); // player has not moved
        assertNotEquals(bulletY, newBulletY, 0.0001f); // bullet did tho

        // TODO test invaders
    }

    @Test
    void testStartNewGame() {
        Game game = new Game();
        game.startNewRound();
        assertNotNull(game.getPlayer());
        assertTrue(game.getInvaders().size() >= 1); // at least 1 invader exist
    }

    @Test
    void testPlayerGettingHit() {

    }

    @Test
    void testInvaderGettingHit() {
        Game game = new Game(100, 100);
        game.spawnPlayer(0, 80, 20, 20);
        game.spawnInvader(0, 0, 20, 40);

        Bullet bullet = game.getPlayer().shootBullet(75);

        Invader invader = game.getInvaders().get(0);

        assertFalse(bullet.hasCollidedWith(invader));
        assertFalse(invader.hasCollidedWith(bullet));

        bullet.move();

        assertTrue(bullet.hasCollidedWith(invader));
        assertTrue(invader.hasCollidedWith(bullet));
    }

    @Test
    void testInvadersHit() {
        Game game = new Game(100, 100);
        game.spawnPlayer(0, 80, 20, 20);
        Player player = game.getPlayer();
        game.spawnInvader(0, 0, 20, 40);
        Invader invader1 = game.getInvaders().get(0);
        game.spawnInvader(0, 40, 30, 40);
        Invader invader2 = game.getInvaders().get(1);

        Bullet newBullet1 = game.getPlayer().shootBullet(30.01f);
        game.addBullet(newBullet1);

        List<Entity> markedForRemoval = game.getMarkedForRemovalEntities();

        assertTrue(game.getInvaders().contains(invader2));
        assertTrue(game.getInvaders().contains(invader1));
        assertFalse(markedForRemoval.contains(invader2));
        assertFalse(markedForRemoval.contains(invader1));
        assertFalse(markedForRemoval.contains(player)); // player isn't hit by own bullet ofc

        game.update();
        markedForRemoval = game.getMarkedForRemovalEntities();

        assertTrue(game.getInvaders().contains(invader2));
        assertTrue(game.getInvaders().contains(invader1));
        assertTrue(markedForRemoval.contains(invader2));
        assertFalse(markedForRemoval.contains(invader1));
        assertFalse(markedForRemoval.contains(player));

        game.update();
        markedForRemoval = game.getMarkedForRemovalEntities();

        Bullet newBullet2 = game.getPlayer().shootBullet(30.01f);
        game.addBullet(newBullet2);

        assertFalse(game.getInvaders().contains(invader2));
        assertTrue(game.getInvaders().contains(invader1));
        assertFalse(markedForRemoval.contains(invader2));
        assertFalse(markedForRemoval.contains(invader1));
        assertFalse(markedForRemoval.contains(player));

        game.update();
        markedForRemoval = game.getMarkedForRemovalEntities();

        assertFalse(game.getInvaders().contains(invader2));
        assertTrue(game.getInvaders().contains(invader1));
        assertFalse(markedForRemoval.contains(invader2));
        assertFalse(markedForRemoval.contains(invader1));
        assertFalse(markedForRemoval.contains(player));

        game.update();
        markedForRemoval = game.getMarkedForRemovalEntities();

        assertFalse(game.getInvaders().contains(invader2));
        assertTrue(game.getInvaders().contains(invader1));
        assertFalse(markedForRemoval.contains(invader2));
        assertTrue(markedForRemoval.contains(invader1));
        assertFalse(markedForRemoval.contains(player));

        game.update();
        markedForRemoval = game.getMarkedForRemovalEntities();

        assertFalse(game.getInvaders().contains(invader2));
        assertFalse(game.getInvaders().contains(invader1));
        assertFalse(markedForRemoval.contains(invader2));
        assertFalse(markedForRemoval.contains(invader1));
        assertFalse(markedForRemoval.contains(player));
    }

    @Test
    void testMoveInvadersLeftRightDown() {
        float gameWidth = 100;
        float gameHeight = 100;
        float invaderWidth = 20f;
        Game game = new Game(gameWidth, gameHeight);
        // although the player is not used, it's expected to exist
        game.spawnPlayer(80, 80, 20, 20);

        float direction = game.getInvaderDirection();
        assertTrue(direction == 1f || direction == -1f,
                "Direction should be exactly 1 or -1");
        float startX = direction > 0f ? gameWidth - 0.00001f - invaderWidth : invaderWidth + 0.00001f;

        game.spawnInvader(startX, 0, invaderWidth, 40);
        Invader invader1 = game.getInvaders().get(0);
        game.spawnInvader(startX - invaderWidth, 40, invaderWidth, 40);
        Invader invader2 = game.getInvaders().get(1);

        // invaders are not in motion by default...
        game.applyInvaderMotion();

        float lastY1 = invader1.getY();
        float lastX1 = invader1.getX();
        float lastY2 = invader2.getY();
        float lastX2 = invader2.getX();
        float dy1, dx1, dy2, dx2;

        game.update();

        dy1 = invader1.getY() - lastY1;
        dy2 = invader2.getY() - lastY2;
        // invaders should have moved down
        assertTrue(dy1 > 0);
        assertTrue(dy2 > 0);
        // (x is allowed to move so it won't be checked)

        lastY1 = invader1.getY();
        lastX1 = invader1.getX();
        lastY2 = invader2.getY();
        lastX2 = invader2.getX();
        game.update();
        dy1 = invader1.getY() - lastY1;
        dy2 = invader2.getY() - lastY2;
        dx1 = invader1.getX() - lastX1;
        dx2 = invader2.getX() - lastX2;

        // invaders then should have only moved on X
        assertTrue(dy1 == 0);
        assertTrue(dy2 == 0);
        assertTrue(dx1 != 0);
        assertTrue(dx2 != 0);

    }

    @Test
    void testPlayerLives() {
        Game game = new Game(100, 100);
        game.spawnPlayer(80, 80, 20, 20);

        game.spawnInvader(90, 90, 10, 10);
        Invader invader1 = game.getInvaders().get(0);
        Bullet bullet = invader1.shootBullet();
        game.addBullet(bullet);

        game.update();

        assertTrue(game.isPlayerHit(),
                "Player should have been hit from bullet!");
    }
}
