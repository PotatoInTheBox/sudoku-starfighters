package model;

public class Player extends Entity {
	protected float speed;
	private static final float BULLET_SPEED = 4f;

	public Player(float x, float y, float width, float height, float speed) {
		super(x, y, width, height);
		this.speed = speed;
		this.team = Team.PLAYER;
	}

	public void moveHorizontal(float analogIput) {
		x += speed * analogIput;
	}

	// for testing purposes
	public void testMoveVertical(float analogIput) {
		y += speed * analogIput;
	}

	public boolean isHit() {
		return false;
	}

	public Bullet shootBullet() {
		Bullet newBullet = new Bullet(x + width/2, y + height/2, -BULLET_SPEED, team);
		return newBullet;
	}
}
