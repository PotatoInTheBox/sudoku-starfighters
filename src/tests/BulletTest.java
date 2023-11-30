package tests;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import model.Bullet;
import model.Game;
import model.Team;

public class BulletTest {
    @Test
    void testBulletInitialization() {
        // The bullet should request a team as a construct parameter.
        // Having a "neutral" or unexpected Team should be clearly stated.
        // Note: neutral bullets should not collide/harm anything.
        Game game = new Game();
        Bullet bullet = new Bullet(game, 0f, 0f, -1f, Team.NEUTRAL);
        assertEquals(0, bullet.getY());
        bullet.update();
        assertTrue(bullet.getY() < 0);
    }

    // TODO there isn't a whole lot to test to be honest.
}
