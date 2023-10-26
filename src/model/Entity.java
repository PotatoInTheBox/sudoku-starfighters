package model;

public abstract class Entity
{
	protected float positionX;
	protected float positionY;
	protected float sizeX;
	protected float sizeY;
	
	public float getPositionX() {
		return positionX;
	}
	
	public float getPositionY() {
		return positionY;
	}
	
	public float getSizeX() {
		return sizeX;
	}
	
	public float getSizeY() {
		return sizeY;
	}

	public boolean hasCollidedWith(Entity other) {
		return false;
	}
}
