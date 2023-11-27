package model;

import java.util.ArrayList;
import java.util.Random;

public class Turret extends Entity {

	public Invader currentTarget;
	public float gunLineX = this.getCenterX();
	public float gunLineY = this.getCenterY() - 30;
	
	public float timer = 0;

	
	public Turret(float x, float y, float width, float height) {
		super(x, y, width, height);
		this.team = Team.PLAYER;
	}
	
	public void setTarget(Invader toTarget) {
		currentTarget = toTarget;
	}
	
	// Psuedo Random Target
	public void setTarget(ArrayList<Invader> invaders) {
		Random r = new Random();
		currentTarget = invaders.get(r.nextInt(invaders.size()));
	}
	
	public Bullet shootBullet() {
		float[] change = determineDxDy();
		float dx = change[0];
		float dy = change[1];
		
	    Bullet newBullet = new Bullet(x + width / 2, y + height / 2, dx, dy, team);
	    return newBullet;
	}
	
	public void update(ArrayList<Invader> invaders) {
		timer = (timer + 1) % 60;
		
		if (timer == 59) {
			setTarget(invaders);
			lookAtTarget();
		}
	}
	
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
	
	private float[] determineDxDy() {
		float targetXPos = currentTarget.x;
		float targetYPos = currentTarget.y;
		
		float slope = (targetYPos - this.getCenterY()) / (targetXPos - this.getCenterX());
		
		float[] change = new float[2];
		
		
		
		return change;
	}
	

}
