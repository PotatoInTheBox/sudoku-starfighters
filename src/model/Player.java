package model;

public class Player extends Entity
{
	protected float speed;
	
	public Player(float _speed) {
		speed = _speed;
	}
	
	public void moveLeft() {
		positionX -= speed;
	}
	
	public void moveRight() {
		positionX += speed;
	}
	
	public boolean isHit() {
		return false;
	}
	
	public void shootBullet() {
		
	}
}
