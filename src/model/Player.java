package model;

import javafx.scene.input.KeyCode;
import view_controller.sound.SoundPlayer;
import view_controller.utils.Input;

import java.util.Iterator;

public class Player extends Entity {
	protected float speed;
	public Collider collider;
	public Sprite sprite;

	public Player(Game game, float x, float y) {
		this(game, x, y, 10, 10, 0.5f);
	}

	public Player(Game game, float x, float y, float width, float height, float speed) {
		super(game, x, y);
		collider = new Collider(game, x + (-width / 2), y + (-height / 2), width, height);
		sprite = new Sprite(game, x + (-width / 2), y + (-height / 2), width, height, null);

		this.speed = speed;
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
		if (collidingWithEnemy()) {
			SoundPlayer.playSound("player_death.wav", false);
			game.loseGame();
		}

		/// bullet collision
		if (collidingWithBullet()) {
			SoundPlayer.playSound("player_death.wav", false);
			game.loseLife();
		}
	}

	private void createHandlers() {
		// create button press handlers (eg. shoot weapon)
		onKeyDown(e -> {
			if (e.getCode() == KeyCode.A) {
				exShootBullet();
			}
		});
	}

	private void exShootBullet() {
		// shoot bullet if this player can shoot
		SoundPlayer.playSound("player_shoot.wav", false);
		Bullet bullet = new Bullet(game, getX(), getY(), Bullet.BULLET_PLAYER_SPEED, team);
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

	/**
	 * Moves the player left and right
	 * 
	 * @param analogInput The directional input of the user
	 */
	public void moveHorizontal(float analogInput) {
		x += speed * analogInput;
	}

	/**
	 * Moves the player up and down for testing
	 * 
	 * @param analogInput The directional input of the user
	 */
	public void moveVertical(float analogInput) {
		y += speed * analogInput;
	}

	/**
	 * Specifies if the player is hit
	 * 
	 * @return If the player is hit
	 */
	public boolean isHit() {
		return false;
	}

	/**
	 * Shoots a bullet at a specific speed
	 * 
	 * @param speed The speed at which to fire the bullet
	 * @return The created bullet
	 */
	public Bullet shootBullet(float speed) {
		Bullet newBullet = new Bullet(game, this.getX(), this.getY(), -speed, team);
		return newBullet;
	}
}
