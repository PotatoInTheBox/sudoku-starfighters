package model;

public abstract class Entity
{
	protected float positionX;
	protected float positionY;
	protected float sizeX;
	protected float sizeY;
	
	public Entity()
	{
		positionX = 0;
		positionY = 0;
		sizeX = 20;
		sizeY = 20;
	}
	
	public Entity(float xPos, float yPos, float xSize, float ySize)
	{
		positionX = xPos;
		positionY = yPos;
		sizeX = xSize;
		sizeY = ySize;
	}
	
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
