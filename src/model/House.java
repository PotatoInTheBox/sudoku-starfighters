package model;

public class House extends Entity{

	public int Health;
	
	public House(float x, float y, float width, float height) {
		super(x, y, width, height);
		this.team = Team.NEUTRAL;
		Health = 5;
	}
	
}
