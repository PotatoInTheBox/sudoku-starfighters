package model;

public class InvaderGoal extends Entity {
    public Collider collider;
    public Sprite sprite;

    public InvaderGoal(Game game, float x, float y, float width, float height) {
        super(game, x, y);
        collider = new Collider(game, 0, 0, width, height);
        collider.setCenter(x, y);
        sprite = new Sprite(game, 0, 0, width, height);
        sprite.setCenter(x, y);

        addChild(collider, sprite);
        sprite.setImage("empty_pixel.png");
    }

    @Override
    public void update() {

    }
}
