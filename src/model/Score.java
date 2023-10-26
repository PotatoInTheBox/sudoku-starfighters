package model;

public class Score
{
	private int score;
	
	public Score() {
		score = 0;
	}
	
	public int getScore(){
		return score;
	}
	
	public void changeScore(int change) {
		score = score + change;
	}
}
