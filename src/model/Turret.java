package model;

import java.util.ArrayList;
import java.util.Random;

public class Turret extends Entity {

	public Invader currentTarget;
	public float gunLineX = this.getCenterX();
	public float gunLineY = this.getCenterY() - 30;
	
	private float timer = 0;
	private float health = 3;
	
	public Turret(float x, float y, float width, float height) {
		super(x, y, width, height);
		this.team = Team.NEUTRAL;
	}
	
	/**
	 * Sets a target for the turret
	 * @param toTarget The Invader that is targeted
	 */
	public void setTarget(Invader toTarget) {
		currentTarget = toTarget;
	}
	
	/**
	 * Finds a pseudo random target
	 * @param invaders The list of all invaders that could be targeted
	 */
	public void setTarget(ArrayList<Invader> invaders) {
		Random r = new Random();
		currentTarget = invaders.get(r.nextInt(invaders.size()));
	}
	
	/**
	 * Shoots a bullet
	 * @return The shot bullet
	 */
	public Bullet shootBullet() {
		float[] change = determineDxDy();
		float dx = change[0];
		float dy = change[1];
		
	    Bullet newBullet = new Bullet(x, y, dx, dy, team);
	    return newBullet;
	}
	
	/**
	 * Determines whether to shoot or not
	 * @param invaders The list of invaders
	 * @return If the turret should shoot
	 */
	public boolean update(ArrayList<Invader> invaders) {
		timer = (timer + 1) % 320;
		
		if (timer == 319) {
			setTarget(invaders);
			lookAtTarget();
			return true;
		}
		return false;
	}
	
	/**
	 * Updates the health of the turret
	 * @return The current health
	 */
	public float updateHealth() {
		return --health;
	}
	
	/**
	 * Turns the turret to look at a target
	 */
	private void lookAtTarget() {
		float targetXPos = currentTarget.x;
		float targetYPos = currentTarget.y;
		float gunLength = 30;
		
		double xVal;
		double yVal;
		
		// Distance Formula
		double ratioOfDistance = Math.pow(targetXPos - this.getCenterX(), 2) + Math.pow(targetYPos - this.getCenterY(), 2);
		ratioOfDistance = Math.sqrt(ratioOfDistance);
		ratioOfDistance = gunLength/ratioOfDistance;
		
		xVal = (1-ratioOfDistance) * this.getCenterX() + ratioOfDistance * targetXPos;
		yVal = (1-ratioOfDistance) * this.getCenterY() + ratioOfDistance * targetYPos;
		
		gunLineX = (float) xVal;
		gunLineY = (float) yVal;
	}
	
	/**
	 * Determines the DX and DY values
	 * @return A Float Array with the values
	 */
	private float[] determineDxDy() {
		float targetXPos = currentTarget.x;
		float targetYPos = currentTarget.y;
		float distance = (float) 2.5;
		
		double xVal;
		double yVal;
		
		// Distance Formula
		double ratioOfDistance = Math.pow(targetXPos - this.getCenterX(), 2) + Math.pow(targetYPos - this.getCenterY(), 2);
		ratioOfDistance = Math.sqrt(ratioOfDistance);
		ratioOfDistance = distance/ratioOfDistance;
		
		xVal = (1-ratioOfDistance) * this.getCenterX() + ratioOfDistance * targetXPos;
		yVal = (1-ratioOfDistance) * this.getCenterY() + ratioOfDistance * targetYPos;
		
		float[] change = new float[2];
		change[0] = (float) xVal - this.getCenterX();
		change[1] = (float) yVal - this.getCenterY();
		
		return change;
	}
	
}
