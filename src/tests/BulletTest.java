package tests;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import model.Bullet;
import model.Team;

public class BulletTest {
    @Test
    void testGetTeam() {
        // The bullet should request a team as a construct parameter.
        // Having a "neutral" or unexpected Team should be clearly stated.
        // Note: neutral bullets should not collide/harm anything.
        Bullet bullet = new Bullet(0, 0, 1, 1, 1, Team.INVADERS);
        assertEquals(Team.INVADERS, bullet.getTeam());
        assertNotEquals(Team.NEUTRAL, bullet.getTeam());
        assertEquals(1, bullet.getDy());
        bullet = new Bullet(0, 0, 1, 1, -1f, Team.PLAYER);
        assertEquals(Team.PLAYER, bullet.getTeam());
        assertNotEquals(Team.NEUTRAL, bullet.getTeam());
        assertEquals(-1, bullet.getDy());
    }

    // TODO there isn't a whole lot to test to be honest.
}
