package model;

public class Score
{
	private int score;
	private int lives;
	
	public Score() {
		score = 0;
		lives = 3;
	}
	
	public int getScore(){
		return score;
	}
	
	public int getLives() {
		return lives;
	}
	
	public void changeScore(int change) {
		score = score + change;
	}
}
