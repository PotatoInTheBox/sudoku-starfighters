package model;

import view_controller.sound.SoundPlayer;
import view_controller.utils.Input;
import view_controller.utils.KeyBinding;

import java.util.Iterator;

/**
 * Player class is responsible for allowing the player to interact with the
 * game. The player should be at least be able to move around, shoot invaders,
 * and pick up coins.
 */
public class Player extends Entity {
	private final static int TURRET_COST = 3;
	private final static int SHOOT_COOLDOWN = 30;

	private final static int SHOOT_QUEUE_BUFFER = 8;
	public Collider collider;

	public Sprite sprite;
	private float speed = 3f;
	private boolean isInvincible = false;

	private int shootCooldown = 0;
	// private boolean isShootQueued = false;
	private int shootQueuedCountdown = -1;

	public int coins = 0;

	/**
	 * Create the player at a specified position. The size is automatically
	 * chosen at 10. Does not instantiate any children or create any handlers.
	 * 
	 * @param game to instantiate to
	 * @param x    absolute x to spawn at (centered)
	 * @param y    absolute y to spawn at (centered)
	 */
	public Player(Game game, float x, float y) {
		this(game, x, y, 10, 10);
	}

	/**
	 * Create the player at a specified location and size. The sprite and
	 * collider will be instantiated and added as children. The handlers are
	 * made upon creation of this instance.
	 * 
	 * @param game   to instantiate to
	 * @param x      absolute x to spawn at (children centered)
	 * @param y      absolute y to spawn at (children centered)
	 * @param width  to scale collider and sprite to
	 * @param height to scale collider and sprite to
	 */
	public Player(Game game, float x, float y, float width, float height) {
		super(game, x, y);
		collider = new Collider(game, 0, 0, width / 2, 2 * height / 3);
		collider.setCenter(x, y);
		sprite = new Sprite(game, 0, 0, width, height, "player_ship.png");
		sprite.setCenter(x, y);

		this.team = Team.PLAYER;

		collider.instantiate();
		sprite.instantiate();
		addChild(collider, sprite);
		createHandlers();
	}

	@Override
	public void update() {
		/// handle input
		float xDir = Input.getJoystickX();
		float yDir = Input.getJoystickY();
		if (xDir != 0) {
			move(xDir * speed, 0);
			for (Entity entity : game.getEntities()) {
				if (entity.getClass() == House.class) {
					House house = (House) entity;
					if (collider.hasCollidedWith(house.collider)) {
						move(-xDir * speed, 0);
						break;
					}
				}
			}
		}
		if (yDir != 0) {
			move(0, yDir * speed);
			for (Entity entity : game.getEntities()) {
				if (entity.getClass() == House.class) {
					House house = (House) entity;
					if (collider.hasCollidedWith(house.collider)) {
						move(0, -yDir * speed);
						break;
					}
				}
			}
		}

		// bound to map
		bindToGame();

		/// check if colliding with enemy
		if (!isInvincible && collidingWithEnemy()) {
			SoundPlayer.playSound("player_death.wav");
			game.loseGame();
		}

		/// check if colliding with coin
		Coin collidedCoin = collidedCoin();
		if (collidedCoin != null && collidedCoin.isAlive) {
			SoundPlayer.playSound("coin_pickup.wav");
			collidedCoin.delete();
			coins += 1;
		}

		/// bullet collision
		if (!isInvincible && collidingWithBullet()) {
			SoundPlayer.playSound("player_death.wav");
			game.loseLife();
		}

		if (shootQueuedCountdown > 0 && shootCooldown <= 0) {
			shootBullet(0, 0);
			shootQueuedCountdown = 0;
			shootCooldown = SHOOT_COOLDOWN;
		}

		if (shootQueuedCountdown > 0) {
			shootQueuedCountdown -= 1;
		}

		if (shootCooldown > 0) {
			shootCooldown -= 1;
		}
	}

	/**
	 * Create all the button handlers needed for each player button press.
	 */
	private void createHandlers() {
		// create button press handlers (eg. shoot weapon)
		onKeyDown(e -> {
			if (e.getCode().equals(Input.getKeyFromType(KeyBinding.Type.FIRE))) {
				shootQueuedCountdown = SHOOT_QUEUE_BUFFER;
				if (shootCooldown <= 0) {
					shootBullet(0, 0);
					shootQueuedCountdown = 0;
					shootCooldown = SHOOT_COOLDOWN;
				}
			}
			// if (e.getCode().equals(Input.getKeyFromType(KeyBinding.Type.SHOOT_MANY))) {
			// for (int i = 0; i < 50; i++) {
			// shootBullet(i * 4 - 25 * 4, 0);
			// }
			// }
			if (e.getCode().equals(Input.getKeyFromType(KeyBinding.Type.SPAWN_TURRET))) {
				if (coins < TURRET_COST) {
					System.out.println("Could not spawn turret, not enough coins.");
					return;
				}
				Collider tempCollider = new Collider(game, getX(), game.yTurretSpawnLine, 20, 20);
				for (Entity entity : game.getEntities()) {
					if (entity.getClass() == Turret.class) {
						if (tempCollider.hasCollidedWith(((Turret) entity).collider)) {
							return;
						}
					}
				}
				Turret turret = new Turret(game, getX(), game.yTurretSpawnLine, 30, 20);
				instantiate(game, turret);
				coins -= TURRET_COST;
			}
		});
	}

	/**
	 * Shoot a bullet going up with sound effect of shooting.
	 * 
	 * @param xOffset to displace the bullet by (from the position of the player)
	 * @param yOffset to displace the bullet by (from the position of the player)
	 */
	private void shootBullet(float xOffset, float yOffset) {
		// shoot bullet if this player can shoot
		SoundPlayer.playSound("player_shoot.wav");
		Bullet bullet = new Bullet(game, getX() + xOffset, getY() + yOffset, -1, team);
		bullet.instantiate();
	}

	/**
	 * Push player back into the bounds of the game. The bounds include the
	 * visible game but not the invader section of the game.
	 */
	private void bindToGame() {
		float boundsHeight = game.getHeight() - game.yInvaderGoal;
		if (collider.isOutOfBounds(0f, game.yInvaderGoal, game.getWidth(), boundsHeight)) {
			if (collider.getX() < 0) {
				setX(getX() - collider.getX());
			}
			if (collider.getX() + collider.getWidth() > game.getWidth()) {
				setX(game.getWidth() - collider.getWidth() + getX() - collider.getX());
			}
			if (collider.getY() < game.yInvaderGoal) {
				setY(getY() - collider.getY() + game.yInvaderGoal);
			}
			if (collider.getY() + collider.getHeight() > game.getHeight()) {
				setY(game.getHeight() - collider.getHeight() + getY() - collider.getY());
			}
		}
	}

	/**
	 * Check if the player is colliding with any enemy in the game.
	 * 
	 * @return true if colliding with any enemy, false if there is no collision
	 *         anywhere with the invaders.
	 */
	private boolean collidingWithEnemy() {
		Iterator<Invader> invaders = game.getInvaders();
		while (invaders.hasNext()) {
			Invader invader = invaders.next();
			if (collider.hasCollidedWith(invader.collider)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if the player is colliding with an enemy bullet.
	 * 
	 * @return true is colliding with any bullet, false if not colliding with any
	 *         bullet
	 */
	private boolean collidingWithBullet() {
		Iterator<Bullet> bullets = game.getBullets();
		while (bullets.hasNext()) {
			Bullet bullet = bullets.next();
			if (bullet.getTeam() == Team.INVADERS && collider.hasCollidedWith(bullet.collider)) {
				return true;
			}
		}
		return false;
	}

	/**
	 * Check if the player collided with a coin. If so then return that coin.
	 * 
	 * @return the coin it collided with, null if no coin was collided with.
	 */
	private Coin collidedCoin() {
		for (Entity entity : game.getEntities()) {
			if (entity.getClass() == Coin.class) {
				if (collider.hasCollidedWith(((Coin) entity).collider)) {
					return (Coin) entity;
				}
			}
		}
		return null;
	}

}
