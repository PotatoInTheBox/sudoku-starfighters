package tests;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import java.util.List;

import model.Bullet;
import model.Game;

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
        assertNotEquals(null, game.getPlayer()); // should always exist
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

    }
}
