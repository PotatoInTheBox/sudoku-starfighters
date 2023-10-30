package model;

import java.util.ArrayList;



public class Game
{
	boolean isGameOver;
	InvadersGroup invaders;
	ArrayList<Bullet> allBullets;
	Player player;
	Score score;
	
	public Game()
	{
		isGameOver = false;
//		invaders = new InvadersGroup();
		allBullets = new ArrayList<Bullet>();
		player = new Player(10);
		score = new Score();
		
	}
	
	public Score getScore()
	{
		return score;
	}
	
	public Player getPlayer()
	{
		return player;
	}
	
	public InvadersGroup getInvaders()
	{
		return invaders;
	}
	
	public ArrayList<Bullet> getBullets()
	{
		return allBullets;
	}
	
	public boolean getGameOver()
	{
		return isGameOver;
	}
	
	public void update()
	{
		moveInvaders();
		collisionCheck();
	}
	
	public void endGame()
	{
		isGameOver = true;
	}
	
	private void collisionCheck()
	{
		
	}
	
	private void moveInvaders()
	{
		
	}
}
