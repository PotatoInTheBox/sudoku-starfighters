package model;

/**
 * Explosion class represents an explosion in the game. The explosion simply
 * plays an explosion sprite (for when invaders/bullets are destroyed). The
 * Explosion class doesn't really need a hitbox but it has one anyways (maybe
 * other classes may want to interact with the explosion?).
 */
public class Explosion extends Entity {
    public Collider collider;
    public Sprite sprite;

    private int timeToLive = 30;

    /**
     * Construct the explosion with given parameters.
     * 
     * @param game   to instantiate to
     * @param x      absolute x to spawn at (children centered)
     * @param y      absolute y to spawn at (children centered)
     * @param width  to scale collider and sprite to
     * @param height to scale collider and sprite to
     */
    public Explosion(Game game, float x, float y, float width, float height) {
        super(game, x, y);
        collider = new Collider(game, width, height);
        collider.setCenter(x, y);
        sprite = new Sprite(game, 0, 0, width, height, "destruction_frame.png");
        sprite.setCenter(x, y);

        collider.instantiate();
        sprite.instantiate();
        addChild(collider, sprite);
    }

    @Override
    public void update() {
        timeToLive -= 1;
        if (timeToLive <= 0) {
            delete();
        }
    }
}
