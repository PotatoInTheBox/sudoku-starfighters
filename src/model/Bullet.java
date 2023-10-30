package model;

public class Bullet extends Entity {

	public Bullet(float positionX, float positionY, float dy, Team team) {
		this(positionX, positionY, 3f, 5f, dy, team);
	}

	public Bullet(float positionX, float positionY, float sizeX, float sizeY, float dy, Team team) {
		super(positionX, positionY, sizeX, sizeY);
		this.team = team;
		this.dy = dy; // must explicitly be given a speed
	}

}