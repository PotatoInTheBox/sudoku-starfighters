package model;

import java.io.Serializable;

public class Score implements Serializable {
    private static final long serialVersionUID = 1L;

	private int score;
	private int lives;
	private String username;

	public Score() {
		score = 0;
		lives = 3;
		username = "";
	}

	public int getScore() {
		return score;
	}

	public int getLives() {
		return lives;
	}

	public void setLives(int lives) {
		this.lives = lives;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String user) {
		username = user;
	}

	public void changeScore(int change) {
		score = score + change;
	}

	public void changeScore(Invader invader) {
		score = score + invader.getScoreChange();
	}

	public void changeLives() {
		lives -= 1;
	}
}
