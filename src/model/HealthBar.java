package model;

/**
 * HealthBar represents a health bar. It can be given a maxHp which it will use
 * to hold the maximum health which is also set as the current health. The
 * HealthBar will represent health by using heart sprites within its width and
 * height constraints. Each unit of hp is an individual heart sprite that gets
 * drawn.
 */
public class HealthBar extends Rect {

    public Sprite[] sprites;

    private int hp;
    private int maxHp;

    /**
     * Constructs the HealthBar
     * 
     * @param game   to instantiate to
     * @param x      absolute x to spawn at (children centered)
     * @param y      absolute y to spawn at
     * @param width  to scale collider and sprite to
     * @param height to scale collider and sprite to
     * @param maxHp  to set the max hp, current hp, and heart sprite count to
     */
    public HealthBar(Game game, float x, float y, float width, float height, int maxHp) {
        super(game, 0, y, width, height);
        setCenterX(x);
        sprites = new Sprite[maxHp];
        this.maxHp = maxHp;
        this.hp = maxHp;

        createHealthSprites();
        updateSprites();

        instantiate(game, sprites);
        addChild(sprites);
    }

    /**
     * Sets the HP of the health bar
     * 
     * @param newHp The HP to set to
     */
    public void setHp(int newHp) {
        if (newHp == hp) {
            return;
        }
        hp = newHp;
        updateSprites();
    }

    /**
     * Updates the sprite images for the health of the turret
     */
    private void updateSprites() {
        for (int i = 0; i < sprites.length; i++) {
            if (hp - 1 >= i) {
                sprites[i].setImage("heart.png");
            } else {
                sprites[i].clearImages();
            }
        }
    }

    /**
     * Creates the sprites for the health bar
     */
    private void createHealthSprites() {
        float spriteWidth = width / maxHp;
        float spriteHeight = height;
        for (int i = 0; i < maxHp; i++) {
            float spawnX = i * width / maxHp + getX() + spriteWidth / 2;
            sprites[i] = new Sprite(game, 0, 0, spriteWidth, spriteHeight);
            sprites[i].setCenterX(spawnX);
            sprites[i].setY(getY());
        }
    }
}
