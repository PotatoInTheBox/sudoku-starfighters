package model;

public class House extends Entity{

	private int hits;
	
	public House(float x, float y, float width, float height) {
		super(x, y, width, height);
		this.team = Team.NEUTRAL;
		hits = 0;
	}
	
	/**
	 * Gets the amount of hits a house has taken
	 * @return The number of hits
	 */
	public int getHits() {
		return hits;
	}
	
	/**
	 * Registers a hit on the house
	 */
	public void hit() {
		hits += 1;
	}
	
	/**
	 * Checks if the house is alive
	 * @return If hits is less than 4
	 */
	public boolean checkAlive() {
		if(hits >= 4) {
			return false;
		}
		return true;
	}
}
