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
    void testGameInitialization() {
        Game game = new Game();
        game.addEntity(new Entity(game, 0, 0) {
            
        });
        assertEquals(1, game.getEntities().size());
    }
}
