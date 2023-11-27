package model;

public class Bullet extends Entity {

	public Bullet(float positionX, float positionY, float dy, Team team) {
		this(positionX, positionY, 5f, 10f, dy, team);
	}
	
	// Used with turrets
	public Bullet(float positionX, float positionY, float dx, float dy, Team team) {
		super(positionX, positionY, 5f, 10f);
		this.team = team;
		this.dx = dx;
		this.dy = dy;
	}

	public Bullet(float positionX, float positionY, float sizeX, float sizeY, float dy, Team team) {
		super(positionX, positionY, sizeX, sizeY);
		this.team = team;
		this.dy = dy; // must explicitly be given a speed
	}

}