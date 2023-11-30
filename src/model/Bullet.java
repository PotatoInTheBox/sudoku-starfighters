package model;

/**
 * Bullet class is capable of representing a projectile. Bullets will travel
 * according to their dx/dy values on every update tick. The bullet will
 * dissapear once it goes off-screen.
 */
public class Bullet extends Entity {
	public final static float BULLET_BASE_SPEED = 2f;
	public final static float BULLET_INVADER_SPEED = 1f * BULLET_BASE_SPEED;

	public final static float BULLET_PLAYER_SPEED = 1.5f * BULLET_BASE_SPEED;
	public Collider collider;
	public Sprite sprite;

	/**
	 * Create a bullet. The x and y values are absolute. dy can be used
	 * as just direction or as a speed multiplier (bullets automatically change
	 * speed depending on the team).
	 * 
	 * @param game to instantiate children nodes to.
	 * @param x    absolute x position to place the bullet object.
	 * @param y    absolute y position to place the bullet object.
	 * @param dy   direction/magnitude the bullet should travel along the y-axis.
	 * @param team team this bullet belongs to (determines speed and collision
	 *             logic).
	 */
	public Bullet(Game game, float x, float y, float dy, Team team) {
		this(game, x, y, 5f, 10f, dy, team);
	}

	/**
	 * Create a bullet. dy can be used
	 * as just direction or as a speed multiplier (bullets automatically change
	 * speed depending on the team). Coordinates are set to 0,0 when created.
	 * 
	 * @param game to instantiate children nodes to.
	 * @param dy   direction/magnitude the bullet should travel along the y-axis.
	 * @param team team this bullet belongs to (determines speed and collision
	 *             logic).
	 */
	public Bullet(Game game, float dy, Team team) {
		this(game, 0, 0, 5f, 10f, dy, team);
	}

	/**
	 * Create a bullet. The x and y values are absolute. dy can be used
	 * as just direction or as a speed multiplier (bullets automatically change
	 * speed depending on the team).
	 * 
	 * @param game   to instantiate children nodes to.
	 * @param x      absolute x position to place the bullet object (children
	 *               centered).
	 * @param y      absolute y position to place the bullet object (children
	 *               centered).
	 * @param width  for the bullet size.
	 * @param height for the bullet size.
	 * @param dy     direction/magnitude the bullet should travel along the y-axis.
	 * @param team   team this bullet belongs to (determines speed and collision
	 *               logic).
	 */
	public Bullet(Game game, float x, float y, float width, float height, float dy, Team team) {
		super(game, x, y);
		collider = new Collider(game, width, height);
		collider.setCenter(x, y);
		sprite = new Sprite(game, 0, 0, width, height, "bullet.png");
		sprite.setCenter(x, y);

		collider.instantiate();
		sprite.instantiate();
		addChild(collider, sprite);

		this.team = team;
		this.dy = dy;
	}

	@Override
	public void update() {
		if (team == Team.PLAYER) {
			move(dx * BULLET_PLAYER_SPEED, dy * BULLET_PLAYER_SPEED);
		} else if (team == Team.INVADERS) {
			move(dx * BULLET_INVADER_SPEED, dy * BULLET_INVADER_SPEED);
		} else {
			move(dx * BULLET_BASE_SPEED, dy * BULLET_BASE_SPEED);
		}
		if (collider.isOutOfBounds(0, 0, game.getWidth(), game.getHeight())) {
			Explosion explosion = new Explosion(game, getX(), getY(), sprite.getWidth(), sprite.getHeight());
			instantiate(game, explosion);
			delete();
		}
		for (Entity entity : game.getEntities()) {
			if (entity.getClass() == Bullet.class) {
				Bullet otherBullet = (Bullet) entity;
				if (collider.hasCollidedWith(otherBullet.collider)) {
					if (team == Team.PLAYER && otherBullet.team == Team.INVADERS ||
							team == Team.INVADERS && otherBullet.team == Team.PLAYER) {
						Explosion explosion = new Explosion(game, getX(), getY(), sprite.getWidth(),
								sprite.getHeight());
						instantiate(game, explosion);
						Explosion otherExplosion = new Explosion(game, otherBullet.getX(), otherBullet.getY(),
								otherBullet.sprite.getWidth(), otherBullet.sprite.getHeight());
						instantiate(game, otherExplosion);
						delete();
						otherBullet.delete();

					}

				}
			}
		}
	}

}