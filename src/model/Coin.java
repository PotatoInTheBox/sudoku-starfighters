package model;

/**
 * Coin class is a game object that the player can touch to pick up. It only
 * goes down and dissapears once off-screen.
 */
public class Coin extends Entity {
    public Collider collider;
    public Sprite sprite;

    public final static float COIN_SPEED = 1.4f;
    private int coinFrameCountDown = 0;
    private final static int COIN_FRAME_DELAY = 30;

    /**
     * Coin constructor creates a coin with the given dimensions.
     * 
     * @param game   to instantiate children nodes to.
     * @param x      absolute x position to place the coin object.
     * @param y      absolute y position to place the coin object.
     * @param width  for the coin size.
     * @param height for the coin size.
     */
    public Coin(Game game, float x, float y, float width, float height) {
        super(game, x, y);
        collider = new Collider(game, width, height);
        collider.setCenter(x, y);
        sprite = new Sprite(game, 0, 0, width, height);
        sprite.addImage("coin_frame1.png", "coin_frame2.png", "coin_frame3.png", "coin_frame4.png");
        sprite.setCenter(x, y);

        addChild(collider, sprite);
    }

    @Override
    public void update() {
        move(0, COIN_SPEED);
        if (collider.isOutOfBounds(0, 0, game.getWidth(), game.getHeight())) {
            delete();
        }

        if (coinFrameCountDown <= 0){
            sprite.nextFrame();
            coinFrameCountDown = COIN_FRAME_DELAY;
        }

        coinFrameCountDown -= 1;
    }
}
