package model;


public abstract class Entity {
	protected float x, y, width, height;
	protected float dx = 0f;
	protected float dy = 0f;
	protected Team team = Team.NEUTRAL;

	public Entity(float x, float y, float width, float height) {
		this.x = x;
		this.y = y;
		this.width = width;
		this.height = height;
	}

	public void setX(float positionX) {
		this.x = positionX;
	}

	public void setY(float positionY) {
		this.y = positionY;
	}

	public float getX() {
		return x;
	}

	public float getY() {
		return y;
	}

	public void setWidth(float sizeX) {
		this.width = sizeX;
	}

	public void setHeight(float sizeY) {
		this.height = sizeY;
	}

	public float getWidth() {
		return width;
	}

	public float getHeight() {
		return height;
	}

	public void setDx(float newHorizontalSpeed) {
		this.dx = newHorizontalSpeed;
	}

	public void setDy(float newVerticalSpeed) {
		this.dy = newVerticalSpeed;
	}

	public float getDx() {
		return dx;
	}

	public float getDy() {
		return dy;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public Team getTeam() {
		return team;
	}

	public void move() {
		x += dx;
		y += dy;
	}

	public boolean hasCollidedWith(Entity other) {
		if (this.x < other.x + other.width &&
				this.x + this.width > other.x &&
				this.y < other.y + other.height &&
				this.y + this.height > other.y) {
			return true;
		} else
			return false;
	}

	public boolean isOutOfBounds(float boundsX, float boundsY, float boundsWidth, float boundsHeight) {
		// out of bounds means if any part of the collider goes outside the bounds
		if (this.x + width > boundsX + boundsWidth ||
				this.x < boundsX ||
				this.y + height > boundsY + boundsHeight ||
				this.y < boundsY) {
			return true;
		} else
			return false;
	}
}
