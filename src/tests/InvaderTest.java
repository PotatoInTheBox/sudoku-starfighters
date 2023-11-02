package tests;

import org.junit.jupiter.api.Test;

import model.Bullet;
import model.Invader;
import model.Player;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InvaderTest {

    @Test
    void testShoot() {
        Invader invader = new Invader(0, 0, 10, 10, 10);
        Bullet bullet = invader.shootBullet();
        assertEquals(5f, bullet.getX(), 6f);
        bullet.move();
        assertTrue(bullet.getY() > invader.getY() + invader.getHeight() / 2);

    }
}
