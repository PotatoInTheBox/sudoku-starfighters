package model;

import java.util.Random;

import view_controller.sound.SoundPlayer;

import java.util.Iterator;
import java.lang.Math;

/**
 * Turrets are objects similar to the player and invaders. With shooting
 * capabilities and getting hit capabilities. The turret is able to aim and
 * shoot directly at invaders from a fixed position. It comes with hp and
 * a healthbar.
 */
public class Turret extends Entity {

	public Collider collider;
	public Sprite sprite;

	private Sprite[] barrelSprites = new Sprite[8];
	private float barrelLength = 40f;
	private HealthBar healthBar;

	public Invader currentTarget = null;

	private int hp;
	private int maxHp = 3;

	public int shootTimer = 0;

	/**
	 * Construct a turret with a given position and size.
	 * 
	 * @param game   to instantiate to
	 * @param x      absolute x to spawn at (centered)
	 * @param y      absolute y to spawn at (centered)
	 * @param width  to scale collider and sprite to
	 * @param height to scale collider and sprite to
	 */
	public Turret(Game game, float x, float y, float width, float height) {
		super(game, x, y);
		collider = new Collider(game, 0, 0, width, height);
		collider.setCenter(x, y);
		sprite = new Sprite(game, 0, 0, width, height);
		sprite.setCenter(x, y);
		this.team = Team.PLAYER;
		sprite.setImage("player_ship.png");
		for (int i = 0; i < barrelSprites.length; i++) {
			barrelSprites[i] = new Sprite(game, 0, 0, width / 3, height / 3);
			barrelSprites[i].setImage("empty_pixel.png");
		}
		hp = maxHp;
		healthBar = new HealthBar(game, x, y + height, width, 15, maxHp);
		collider.instantiate();
		sprite.instantiate();
		healthBar.instantiate();
		addChild(collider, healthBar, sprite);
		instantiate(game, barrelSprites);
		addChild(barrelSprites);
		aimBarrel(0, -1); // aim up initially
		currentTarget = getNextTarget();
		shootTimer = 120;

	}

	@Override
	public void update() {
		// check if hit
		for (Entity entity : game.getEntities()) {
			if (entity.getClass() == Bullet.class) {
				Bullet bullet = (Bullet) entity;
				if (bullet.team == Team.INVADERS) {
					if (bullet.collider.hasCollidedWith(collider)) {
						damage(1);
						healthBar.setHp(hp);
						bullet.delete();
					}
				}
			}
		}

		// aim barrel towards target enemy (if about to shoot)
		if (shootTimer < 60 && currentTarget != null && currentTarget.isAlive) {
			float xOffset = currentTarget.getX() - getX();
			float yOffset = currentTarget.getY() - getY();
			float dist = (float) Math.sqrt(xOffset * xOffset + yOffset * yOffset);
			aimBarrel(xOffset / dist, yOffset / dist);
		}

		// check if shoot timer is ready
		if (shootTimer <= 0) {
			if (currentTarget != null && currentTarget.isAlive) {
				float xOffset = currentTarget.getX() - getX();
				float yOffset = currentTarget.getY() - getY();
				float dist = (float) Math.sqrt(xOffset * xOffset + yOffset * yOffset);
				shootBullet(0, 0, xOffset / dist, yOffset / dist);
			}
			currentTarget = getNextTarget();
		}

		if (shootTimer <= 0) {
			shootTimer = 120;
		}
		shootTimer -= 1;
	}

	/**
	 * Indicates that the turret has taken damage. Deletes the turret when the
	 * hp has run out.
	 * 
	 * @param amount The amount of damage taken.
	 */
	public void damage(int amount) {
		hp -= 1;
		if (hp <= 0) {
			delete();
		}
	}

	/**
	 * Aims the barrel at a given normalized direction
	 * 
	 * @param dx value to aim at
	 * @param dy value to aim at
	 */
	private void aimBarrel(float dx, float dy) {
		for (int i = 0; i < barrelSprites.length; i++) {
			float segLen = (i + 5) / ((float) barrelSprites.length + 5);
			barrelSprites[i].setCenter(getX() + segLen * dx * barrelLength, getY() + segLen * dy * barrelLength);
		}
	}

	/**
	 * Shoot a bullet with given offset at a given normalized direction.
	 * 
	 * @param xOffset to start from relative to the turret
	 * @param yOffset to start from relative to the turret
	 * @param dx      value to aim at
	 * @param dy      value to aim at
	 */
	private void shootBullet(float xOffset, float yOffset, float dx, float dy) {
		SoundPlayer.playSound("player_shoot.wav");
		Bullet bullet = new Bullet(game, getX() + xOffset, getY() + yOffset, Bullet.BULLET_PLAYER_SPEED, team);
		bullet.dx = dx;
		bullet.dy = dy;
		bullet.instantiate();
	}

	/**
	 * Randomly choose the next available target.
	 * 
	 * @return a new target to shoot at
	 */
	private Invader getNextTarget() {
		int invaderCount = 0;
		Iterator<Invader> invaders = game.getInvaders();
		if (invaders.hasNext() == false) {
			return null;
		}
		while (invaders.hasNext()) {
			invaderCount += 1;
			invaders.next();
		}
		Random random = new Random();
		int selectInvader = random.nextInt(invaderCount);
		invaders = game.getInvaders();
		invaderCount = 0;
		Invader invader = null;
		while (invaders.hasNext()) {
			invader = invaders.next();
			if (selectInvader == invaderCount) {
				break;
			}
			invaderCount += 1;
		}
		return invader;
	}
}
