package tests;

import org.junit.jupiter.api.Test;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import model.Bullet;
import model.Game;
import model.Player;
import model.Team;

public class PlayerTest {
    // @Test
    // void testIsHit() {

    // }

    // Player has a seperate move than Entity (no dx/dy)
    // This is because the character is not really in motion.
    // Rather, it is being controlled by the joystick.
    // move() is not needed because the player does not go in motion.
    @Test
    void testPlayerInitialization() {
        Game game = new Game();
        Player player = new Player(game, 0, 0);
         
        assertEquals(Team.PLAYER, player.getTeam());
    }
}
