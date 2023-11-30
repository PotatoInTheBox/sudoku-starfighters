package tests;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import model.Entity;
import model.Game;
import model.Team;

public class EntityTest {
    @Test
    void testEntityInitialization() {
        Game game = new Game();
        Entity entity = new Entity(game, 0, 0) {

        };

        entity.move(-1, -1);

        assertTrue(entity.getX() < 0);
        assertTrue(entity.getY() < 0);

        entity.move(-1, -1);

        assertTrue(entity.getX() < 0);
        assertTrue(entity.getY() < 0);
    }
}
