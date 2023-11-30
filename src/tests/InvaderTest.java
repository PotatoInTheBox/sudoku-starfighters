package tests;

import org.junit.jupiter.api.Test;

import model.Bullet;
import model.Game;
import model.Invader;
import model.Player;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class InvaderTest {

    @Test
    void testInvaderInitialization() {
        Game game = new Game();
        Invader invader = new Invader(game, 0, 0, 1, 1, 1);
        invader.delete();
    }
}
