package model;

public class Player extends Entity {
	protected float speed;
	private static final float BULLET_SPEED = 4f;

	public Player(float x, float y, float width, float height, float speed) {
		super(x, y, width, height);
		this.speed = speed;
		this.team = Team.PLAYER;
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
	public void testMoveVertical(float analogInput) {
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
	 * Shoots a bullet at default speed
	 * 
	 * @return The created bullet
	 */
	public Bullet shootBullet() {
		Bullet newBullet = new Bullet(x + width / 2, y + height / 2, -BULLET_SPEED, team);
		return newBullet;
	}

	/**
	 * Shoots a bullet at a specific speed
	 * 
	 * @param speed The speed at which to fire the bullet
	 * @return The created bullet
	 */
	public Bullet shootBullet(float speed) {
		Bullet newBullet = new Bullet(x + width / 2, y + height / 2, -speed, team);
		return newBullet;
	}
}
