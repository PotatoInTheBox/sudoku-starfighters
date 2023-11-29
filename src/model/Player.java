package model;

import javafx.scene.input.KeyCode;
import view_controller.sound.SoundPlayer;
import view_controller.utils.Input;
import view_controller.utils.KeyBinding;

import java.util.Iterator;

public class Player extends Entity {
	public Collider collider;
	public Sprite sprite;

	private float speed = 3f;
	private boolean isInvincible = false;

	private final static int TURRET_COST = 3;
	private final static int SHOOT_COOLDOWN = 30;
	private final static int SHOOT_QUEUE_BUFFER = 8;

	private int shootCooldown = 0;
	// private boolean isShootQueued = false;
	private int shootQueuedCountdown = -1;

	public int coins = 0;

	public Player(Game game, float x, float y) {
		this(game, x, y, 10, 10);
	}

	public Player(Game game, float x, float y, float width, float height) {
		super(game, x, y);
		collider = new Collider(game, 0, 0, width / 2, 2 * height / 3);
		collider.setCenter(x, y);
		sprite = new Sprite(game, 0, 0, width, height, "player_ship.png");
		sprite.setCenter(x, y);

		this.team = Team.PLAYER;

		addChild(collider, sprite);
		createHandlers();
	}

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
			// 	for (int i = 0; i < 50; i++) {
			// 		shootBullet(i * 4 - 25 * 4, 0);
			// 	}
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

	private void shootBullet(float xOffset, float yOffset) {
		// shoot bullet if this player can shoot
		SoundPlayer.playSound("player_shoot.wav");
		Bullet bullet = new Bullet(game, getX() + xOffset, getY() + yOffset, -1, team);
		instantiate(bullet);
	}

	/**
	 * Push player back into the bounds of the game
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
