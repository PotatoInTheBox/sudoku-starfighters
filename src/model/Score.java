package model;

import java.io.Serializable;

public class Score implements Serializable {
    private static final long serialVersionUID = 1L;

	private int score;
	private int lives;
	private int coins;
	private String username;

	public Score() {
		score = 0;
		lives = 3;
		coins = 0;
		username = "";
	}

	/**
	 * Gets player score
	 * @return Players current score
	 */
	public int getScore() {
		return score;
	}

	/**
	 * Gets player lives
	 * @return Players current lives
	 */
	public int getLives() {
		return lives;
	}
	
	/**
	 * Gets player coins
	 * @return Players current coins
	 */
	public int getCoins() {
		return coins;
	}

	/**
	 * Sets the player's lives
	 * @param lives The amount of lives to set the player to
	 */
	public void setLives(int lives) {
		this.lives = lives;
	}
	
	/**
	 * Changes the amount of coins the player has
	 * @param change The amount to change by
	 */
	public void changeCoins(int change) {
		coins = coins + change;
	}

	/**
	 * Gets the user's user name
	 * @return The current user's name
	 */
	public String getUsername() {
		return username;
	}

	/**
	 * Sets the user's name for the leader board
	 * @param user The user's name
	 */
	public void setUsername(String user) {
		username = user;
	}

	/**
	 * Changes the Score
	 * @param change The score amount to change by
	 */
	public void changeScore(int change) {
		score = score + change;
	}

	/**
	 * Changes the Score
	 * @param invader The invader which contains the amount of points to change by
	 */
	public void changeScore(Invader invader) {
		score = score + invader.getScoreChange();
	}

	/**
	 * Decrements lives by 1
	 */
	public void changeLives() {
		lives -= 1;
	}
}
