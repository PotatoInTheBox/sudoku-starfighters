package model;

/**
 * House represents an obstruction to the invaders (and even the player). The
 * house has a set hp value and will graphically look more damaged as it loses
 * hp.
 */
public class House extends Entity {

	public Collider collider;
	public Sprite sprite;

	private static final int MAX_HP = 4;
	private int hp;
	private int maxHp;

	/**
	 * Construct house.
	 * 
	 * @param game   to instantiate to
	 * @param x      absolute x to spawn at (children centered)
	 * @param y      absolute y to spawn at (children centered)
	 * @param width  to scale collider and sprite to
	 * @param height to scale collider and sprite to
	 */
	public House(Game game, float x, float y, float width, float height) {
		super(game, x, y);
		collider = new Collider(game, 0, 0, width, height);
		collider.setCenter(x, y);
		sprite = new Sprite(game, 0, 0, width, height);
		sprite.setCenter(x, y);

		this.team = Team.PLAYER;
		this.team = Team.NEUTRAL;
		hp = MAX_HP;
		maxHp = MAX_HP;

		collider.instantiate();
		sprite.instantiate();
		addChild(collider, sprite);
		chooseCurrentSprite();
	}

	@Override
	public void update() {
		for (Entity entity : game.getEntities()) {
			if (entity.getClass() == Bullet.class) {
				Bullet bullet = (Bullet) entity;
				if (bullet.team == Team.PLAYER || bullet.team == Team.INVADERS) {
					if (bullet.collider.hasCollidedWith(collider)) {
						damage(1);
						bullet.delete();
					}
				}
			}
		}
	}

	/**
	 * Damage the house by a given amount. Subtracting the hp.
	 * 
	 * @param amount to decrease by
	 */
	public void damage(int amount) {
		hp -= 1;
		if (hp <= 0) {
			delete();
		}
		chooseCurrentSprite();
	}

	/**
	 * Load the correct sprite depending on the current hp value.
	 */
	private void chooseCurrentSprite() {
		int imageCount = 4;
		int hpSelector = (hp * imageCount) / maxHp;
		if (hpSelector >= 4) {
			sprite.setImage("house_damage_level_1.png");
		} else if (hpSelector == 3) {
			sprite.setImage("house_damage_level_2.png");
		} else if (hpSelector == 2) {
			sprite.setImage("house_damage_level_3.png");
		} else {
			sprite.setImage("house_damage_level_4.png");
		}
	}
}
