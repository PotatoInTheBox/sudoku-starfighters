package model;

public class Bullet extends Entity {
	public Collider collider;
	public Sprite sprite;

	public final static float BULLET_BASE_SPEED = 2f;
	public final static float BULLET_INVADER_SPEED = 1f * BULLET_BASE_SPEED;
	public final static float BULLET_PLAYER_SPEED = 1.5f * BULLET_BASE_SPEED;

	public Bullet(Game game, float x, float y, float dy, Team team) {
		this(game, x, y, 5f, 10f, dy, team);
	}

	public Bullet(Game game, float dy, Team team) {
		this(game, 0, 0, 5f, 10f, dy, team);
	}

	public Bullet(Game game, float x, float y, float width, float height, float dy, Team team) {
		super(game, x, y);
		collider = new Collider(game, width, height);
		collider.setCenter(x, y);
		sprite = new Sprite(game, 0, 0, width, height, "bullet.png");
		sprite.setCenter(x, y);
		
		addChild(collider, sprite);

		this.team = team;
		this.dy = dy; // must explicitly be given a speed
	}

	@Override
	public void update() {
		if (team == Team.PLAYER){
			move(0, -BULLET_PLAYER_SPEED);
		}
		if (team == Team.INVADERS){
			move(0, BULLET_INVADER_SPEED);
		}
		if (collider.isOutOfBounds(0, 0, game.getWidth(), game.getHeight())){
			Explosion explosion = new Explosion(game, getX(), getY(), sprite.getWidth(), sprite.getHeight());
			instantiate(game, explosion);
			delete();
		}
	}

}