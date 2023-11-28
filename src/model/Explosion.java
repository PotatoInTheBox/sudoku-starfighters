package model;

public class Explosion extends Entity {
    public Collider collider;
	public Sprite sprite;

    private int timeToLive = 30;

    public Explosion(Game game, float x, float y, float width, float height) {
        super(game, x, y);
        collider = new Collider(game, width, height);
		collider.setCenter(x, y);
		sprite = new Sprite(game, 0, 0, width, height, "destruction_frame.png");
        sprite.setCenter(x, y);
        addChild(collider, sprite);
    }

    @Override
    public void update() {
        timeToLive -= 1;
        if (timeToLive <= 0){
            delete();
        }
    }
}
