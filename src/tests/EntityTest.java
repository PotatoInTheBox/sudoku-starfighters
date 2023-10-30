package tests;

import static org.junit.Assert.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;

import model.Entity;
import model.Team;

public class EntityTest {
    @Test
    void testGettersSetters() {
        Entity newEntity = new Entity(1, 2, 10, 100) {
        };
        assertEquals(1f, newEntity.getX());
        assertEquals(2f, newEntity.getY());
        assertEquals(10f, newEntity.getWidth());
        assertEquals(100f, newEntity.getHeight());

        // try new values
        newEntity.setX(5);
        newEntity.setY(6);
        newEntity.setWidth(7);
        newEntity.setHeight(8);

        // "set" will exactly set the position
        assertEquals(5, newEntity.getX());
        assertEquals(6, newEntity.getY());
        assertEquals(7, newEntity.getWidth());
        assertEquals(8, newEntity.getHeight());

        // by default, an entity should be neutral
        assertEquals(Team.NEUTRAL, newEntity.getTeam());
        assertNotEquals(Team.PLAYER, newEntity.getTeam());
        assertNotEquals(Team.INVADERS, newEntity.getTeam());

        // however, it can always change if needed
        newEntity.setTeam(Team.PLAYER);
        assertEquals(Team.PLAYER, newEntity.getTeam());
        assertNotEquals(Team.NEUTRAL, newEntity.getTeam());
        assertNotEquals(Team.INVADERS, newEntity.getTeam());

    }

    @Test
    void testHasCollidedWith() {
        // Note up/down on y cords technically don't matter but
        // assume +y is going down and -y is going up.
        Entity myEntity;
        Entity otherEntity;

        // test no collision
        myEntity = new Entity(0, 0, 10, 10) {
        };
        otherEntity = new Entity(20, 20, 10, 10) {
        };
        assertFalse(myEntity.hasCollidedWith(otherEntity));

        // test topleft corner collision
        myEntity = new Entity(-5, -5, 10, 10) {
        };
        otherEntity = new Entity(0, 0, 10, 10) {
        };
        assertTrue(myEntity.hasCollidedWith(otherEntity));

        // test topright corner collision
        myEntity = new Entity(5, -5, 10, 10) {
        };
        otherEntity = new Entity(0, 0, 10, 10) {
        };
        assertTrue(myEntity.hasCollidedWith(otherEntity));

        // test bottomleft corner collision
        myEntity = new Entity(-5, 5, 10, 10) {
        };
        otherEntity = new Entity(0, 0, 10, 10) {
        };
        assertTrue(myEntity.hasCollidedWith(otherEntity));

        // test bottomright corner collision
        myEntity = new Entity(5, 5, 10, 10) {
        };
        otherEntity = new Entity(0, 0, 10, 10) {
        };
        assertTrue(myEntity.hasCollidedWith(otherEntity));

        // completely contained in other collision
        myEntity = new Entity(0, 0, 10, 10) {
        };
        otherEntity = new Entity(-5, -5, 20, 20) {
        };
        assertTrue(myEntity.hasCollidedWith(otherEntity));

        // completely containing other collision
        myEntity = new Entity(0, 0, 10, 10) {
        };
        otherEntity = new Entity(2, 2, 5, 5) {
        };
        assertTrue(myEntity.hasCollidedWith(otherEntity));

        // test nearby Y (no collision)
        myEntity = new Entity(0, 0, 10, 10) {
        };
        otherEntity = new Entity(20, 5, 10, 10) {
        };
        assertFalse(myEntity.hasCollidedWith(otherEntity));

        // test nearby X (no collision)
        myEntity = new Entity(0, 0, 10, 10) {
        };
        otherEntity = new Entity(5, 20, 10, 10) {
        };
        assertFalse(myEntity.hasCollidedWith(otherEntity));
    }

    @Test
    void moveTest() {
        Entity entity = new Entity(20, 0, 10, 10) {
        };
        entity.setDx(10);
        entity.setDy(2000);
        entity.move();
        assertEquals(30, entity.getX());
        assertEquals(2000, entity.getY());
        entity.move();
        assertEquals(40, entity.getX());
        assertEquals(4000, entity.getY());

        entity.setX(0);
        entity.setY(0);
        entity.setDx(-1);
        entity.setDy(-43);
        entity.move();
        assertEquals(-1, entity.getX());
        assertEquals(-43, entity.getY());
        entity.setDx(-2);
        entity.setDy(5);
        entity.move();
        assertEquals(-3, entity.getX());
        assertEquals(-38, entity.getY());
    }
}
