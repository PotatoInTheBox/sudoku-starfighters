package model;

/**
 * InvaderGoal only serves to be a line which seperates the invaders from the
 * player. The InvaderGoal is the representation of where the player cannot
 * cross and where the invaders will win if touched.
 */
public class InvaderGoal extends Entity {
    public Collider collider;
    public Sprite sprite;

    /**
     * Construct the InvaderGoal at the given location and size.
     * 
     * @param game   to instantiate to
     * @param x      absolute x to spawn at (children centered)
     * @param y      absolute y to spawn at (children centered)
     * @param width  to scale collider and sprite to
     * @param height to scale collider and sprite to
     */
    public InvaderGoal(Game game, float x, float y, float width, float height) {
        super(game, x, y);
        collider = new Collider(game, 0, 0, width, height);
        collider.setCenter(x, y);
        sprite = new Sprite(game, 0, 0, width, height);
        sprite.setCenter(x, y);

        collider.instantiate();
        sprite.instantiate();
        addChild(collider, sprite);
        sprite.setImage("empty_pixel.png");
    }
}
