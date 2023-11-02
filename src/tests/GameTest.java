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
        game.fixedUpdate(); // let some time pass
        float newPlayerX, newBulletY;
        newPlayerX = game.getPlayer().getX();
        newBulletY = bullet.getY();
        // TODO add to make sure Invaders move too
        // Bullet should have moved, player should have moved
        // Assuming player moves horizontally and bullet moves vertically
        assertNotEquals(playerX, newPlayerX, 0.0001f);
        assertNotEquals(bulletY, newBulletY, 0.0001f);

        game.fixedUpdate(); // let some more time pass

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
        game.startNewGame();
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
        Invader invader2 = game.getInvaders().get(0);
        game.spawnInvader(0, 40, 30, 40);
        Invader invader1 = game.getInvaders().get(1);

        Bullet newBullet1 = game.getPlayer().shootBullet(30.01f);
        game.addBullet(newBullet1);

        List<Entity> markedForRemoval = game.getMarkedForRemovalEntities();

        assertTrue(game.getInvaders().contains(invader1));
        assertTrue(game.getInvaders().contains(invader2));
        assertFalse(markedForRemoval.contains(invader1));
        assertFalse(markedForRemoval.contains(invader2));
        assertFalse(markedForRemoval.contains(player)); // player isn't hit by own bullet ofc

        game.fixedUpdate();
        markedForRemoval = game.getMarkedForRemovalEntities();

        assertTrue(game.getInvaders().contains(invader1));
        assertTrue(game.getInvaders().contains(invader2));
        assertTrue(markedForRemoval.contains(invader1));
        assertFalse(markedForRemoval.contains(invader2));
        assertFalse(markedForRemoval.contains(player));

        game.fixedUpdate();
        markedForRemoval = game.getMarkedForRemovalEntities();

        Bullet newBullet2 = game.getPlayer().shootBullet(30.01f);
        game.addBullet(newBullet2);

        assertFalse(game.getInvaders().contains(invader1));
        assertTrue(game.getInvaders().contains(invader2));
        assertFalse(markedForRemoval.contains(invader1));
        assertFalse(markedForRemoval.contains(invader2));
        assertFalse(markedForRemoval.contains(player));

        game.fixedUpdate();
        markedForRemoval = game.getMarkedForRemovalEntities();

        assertFalse(game.getInvaders().contains(invader1));
        assertTrue(game.getInvaders().contains(invader2));
        assertFalse(markedForRemoval.contains(invader1));
        assertFalse(markedForRemoval.contains(invader2));
        assertFalse(markedForRemoval.contains(player));

        game.fixedUpdate();
        markedForRemoval = game.getMarkedForRemovalEntities();

        assertFalse(game.getInvaders().contains(invader1));
        assertTrue(game.getInvaders().contains(invader2));
        assertFalse(markedForRemoval.contains(invader1));
        assertTrue(markedForRemoval.contains(invader2));
        assertFalse(markedForRemoval.contains(player));

        game.fixedUpdate();
        markedForRemoval = game.getMarkedForRemovalEntities();

        assertFalse(game.getInvaders().contains(invader1));
        assertFalse(game.getInvaders().contains(invader2));
        assertFalse(markedForRemoval.contains(invader1));
        assertFalse(markedForRemoval.contains(invader2));
        assertFalse(markedForRemoval.contains(player));
    }
}
