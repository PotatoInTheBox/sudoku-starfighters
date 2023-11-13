package tests;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import model.Bullet;
import model.Player;

public class PlayerTest {
    // @Test
    // void testIsHit() {

    // }

    // Player has a seperate move than Entity (no dx/dy)
    // This is because the character is not really in motion.
    // Rather, it is being controlled by the joystick.
    // move() is not needed because the player does not go in motion.
    @Test
    void testMovePlayer() {
        Player player = new Player(0, 0, 10, 10, 10);
        player.moveHorizontal(1f);
        player.testMoveVertical(0.5f);
        // with a speed of 10 it should have gone 10 right, 5 down
        assertEquals(10, player.getX());
        assertEquals(5, player.getY());
        player.moveHorizontal(-0.5f);
        player.testMoveVertical(-1f);
        assertEquals(5, player.getX());
        assertEquals(-5, player.getY());
    }

    @Test
    void testShootBullet() {
        Player player = new Player(0, 0, 10, 10, 10);
        Bullet bullet = player.shootBullet();
        // location doesn't really matter, however, it should be near the
        // player and above it.
        assertEquals(5f, bullet.getX(), 6f);
        // after the bullet has moved it should always be in front of the
        // character (even if by a single pixel)
        bullet.move();
        assertTrue(bullet.getY() < player.getY() + player.getHeight() / 2);

        // no assumptions can be made about the speed and exact position
        // of the spawned bullet.
    }

}
