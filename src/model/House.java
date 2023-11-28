package model;

import javafx.scene.image.Image;

public class House extends Entity {

	public Collider collider;
	public Sprite sprite;

	private int hp;
	private int maxHp;

	public House(Game game, float x, float y, float width, float height) {
		super(game, x, y);
		collider = new Collider(game, 0, 0, width, height);
		collider.setCenter(x, y);
		sprite = new Sprite(game, 0, 0, width, height);
		sprite.setCenter(x, y);

		this.team = Team.PLAYER;
		this.team = Team.NEUTRAL;
		hp = 4;
		maxHp = hp;

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

	public void damage(int amount) {
		hp -= 1;
		if (hp <= 0) {
			delete();
		}
		chooseCurrentSprite();
	}

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
