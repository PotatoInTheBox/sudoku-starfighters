package model;

public class House extends Entity{

	private int hits;
	
	public House(float x, float y, float width, float height) {
		super(x, y, width, height);
		this.team = Team.NEUTRAL;
		hits = 0;
	}
	
	public int getHits() {
		return hits;
	}
	
	public void hit() {
		hits += 1;
	}
	
	public boolean checkAlive() {
		if(hits >= 4) {
			return false;
		}
		return true;
	}
}
