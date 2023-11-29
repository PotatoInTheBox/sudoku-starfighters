package model;

public class HealthBar extends Rect {

    public Sprite[] sprites;

    private int hp;
    private int maxHp;

    public HealthBar(Game game, float x, float y, float width, float height, int maxHp) {
        super(game, 0, y, width, height);
        setCenterX(x);
        sprites = new Sprite[maxHp];
        this.maxHp = maxHp;
        this.hp = maxHp;

        createHealthSprites();
        updateSprites();
        addChild(sprites);
    }

    @Override
    public void update() {

    }

    public void setHp(int newHp) {
        if (newHp == hp) {
            return;
        }
        hp = newHp;
        updateSprites();
    }

    private void updateSprites(){
        for (int i = 0; i < sprites.length; i++) {
            if (hp - 1 >= i) {
                sprites[i].setImage("player_ship.png");
            } else {
                sprites[i].clearImages();
            }
        }
    }

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
