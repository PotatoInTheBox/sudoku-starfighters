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
			setX(getX() + xDir * speed);
		}
		if (yDir != 0) {
			setY(getY() + yDir * speed);
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
		if (collidedCoin != null && collidedCoin.isAlive){
			collidedCoin.delete();
			coins += 1;
		}

		/// bullet collision
		if (!isInvincible && collidingWithBullet()) {
			SoundPlayer.playSound("player_death.wav");
			game.loseLife();
		}
	}

	private void createHandlers() {
		// create button press handlers (eg. shoot weapon)
		onKeyDown(e -> {
			if (e.getCode().equals(Input.getKeyFromType(KeyBinding.Type.FIRE))) {
				// if (getActivePlayerBulletCount() < 1) {
				// game.shootPlayerBullet(); // better make that shot count xd
				// }
				shootBullet(0, 0);
			}
			if (e.getCode().equals(Input.getKeyFromType(KeyBinding.Type.RAPID_FIRE))) {
				shootBullet(0, 0);
			}
			if (e.getCode().equals(Input.getKeyFromType(KeyBinding.Type.SHOOT_MANY))) {
				for (int i = 0; i < 50; i++) {
					shootBullet(i * 4 - 25 * 4, 0);
				}
			}
			if (e.getCode().equals(Input.getKeyFromType(KeyBinding.Type.GHOST))) {
				isInvincible = !isInvincible;
			}
			if (e.getCode().equals(Input.getKeyFromType(KeyBinding.Type.SPAWN_TURRET))) {
				Turret turret = new Turret(game, getX(), getY(), 20, 20);
				instantiate(game, turret);
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
		if (collider.isOutOfBounds(0f, 0f, game.getWidth(), game.getHeight())) {
			if (collider.getX() < 0) {
				setX(getX() - collider.getX());
			}
			if (collider.getX() + collider.getWidth() > game.getWidth()) {
				setX(game.getWidth() - collider.getWidth() + getX() - collider.getX());
			}
			if (collider.getY() < 0) {
				setY(getY() - collider.getY());
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
