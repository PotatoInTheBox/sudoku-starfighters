package model;

public class Coin extends Entity {
    public Collider collider;
    public Sprite sprite;

    public final static float COIN_SPEED = 1.4f;

    public Coin(Game game, float x, float y, float width, float height) {
        super(game, x, y);
        collider = new Collider(game, width, height);
        collider.setCenter(x, y);
        sprite = new Sprite(game, 0, 0, width, height, "temp_coin.png");
        sprite.setCenter(x, y);

        addChild(collider, sprite);
    }

    @Override
    public void update() {
        move(0, COIN_SPEED);
        if (collider.isOutOfBounds(0, 0, game.getWidth(), game.getHeight())) {
            delete();
        }
    }
}
