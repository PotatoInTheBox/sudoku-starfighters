package model;

public class Bullet extends Entity
{
	protected float speed;
	protected float directionY;
	
	public Bullet(float _speed) {
		speed = _speed;
	}
	
	public void move() {
		positionY += speed;
	}
}