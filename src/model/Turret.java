package model;

import java.util.ArrayList;
import java.util.Random;

import view_controller.sound.SoundPlayer;

import java.util.Iterator;
import java.lang.Math;

public class Turret extends Entity {

	public Collider collider;
	public Sprite sprite;

	public Invader currentTarget = null;

	public int shootTimer = 0;

	public Turret(Game game, float x, float y, float width, float height) {
		super(game, x, y);
		collider = new Collider(game, 0, 0, width, height);
		collider.setCenter(x, y);
		sprite = new Sprite(game, 0, 0, width, height);
		sprite.setCenter(x, y);
		this.team = Team.PLAYER;
		addChild(collider, sprite);
		sprite.setImage("player_ship.png");
		currentTarget = getNextTarget();
		shootTimer = 120;
	}

	@Override
	public void update() {
		// check if hit
		for (Entity entity : game.getEntities()) {
			if (entity.getClass() == Bullet.class) {
				Bullet bullet = (Bullet) entity;
				if (bullet.team == Team.INVADERS) {
					if (bullet.collider.hasCollidedWith(collider)) {
						bullet.delete();
						delete();
					}
				}
			}
		}

		// aim barrel towards target enemy

		// check if shoot timer is ready
		if (shootTimer <= 0) {
			if (currentTarget != null && currentTarget.isAlive) {
				float xOffset = currentTarget.getX() - getX();
				float yOffset = currentTarget.getY() - getY();
				float dist = (float) Math.sqrt(xOffset * xOffset + yOffset * yOffset);
				shootBullet(0, 0, xOffset/dist, yOffset/dist);
			}
			currentTarget = getNextTarget();

		}

		if (shootTimer <= 0) {
			shootTimer = 120;
		}
		shootTimer -= 1;
	}

	private void aimBarrel(float dx, float dy) {

	}

	private void shootBullet(float xOffset, float yOffset, float dx, float dy) {
		SoundPlayer.playSound("player_shoot.wav");
		Bullet bullet = new Bullet(game, getX() + xOffset, getY() + yOffset, Bullet.BULLET_PLAYER_SPEED, team);
		bullet.dx = dx;
		bullet.dy = dy;
		instantiate(bullet);
	}

	private Invader getNextTarget() {
		int invaderCount = 0;
		Iterator<Invader> invaders = game.getInvaders();
		if (invaders.hasNext() == false) {
			return null;
		}
		while (invaders.hasNext()) {
			invaderCount += 1;
			invaders.next();
		}
		Random random = new Random();
		int selectInvader = random.nextInt(invaderCount);
		invaders = game.getInvaders();
		invaderCount = 0;
		Invader invader = null;
		while (invaders.hasNext()) {
			invader = invaders.next();
			if (selectInvader == invaderCount) {
				break;
			}
			invaderCount += 1;
		}
		return invader;
	}

	public void setTarget(Invader toTarget) {
		currentTarget = toTarget;
	}

	// Psuedo Random Target
	public void setTarget(ArrayList<Invader> invaders) {
		Random r = new Random();
		currentTarget = invaders.get(r.nextInt(invaders.size()));
	}

	// public Bullet shootBullet() {
	// float[] change = determineDxDy();
	// float dx = change[0];
	// float dy = change[1];

	// // Bullet newBullet = new Bullet(x, y, dx, dy, team);
	// return null;
	// }

	// Return val determines whether to shoot or not
	// public boolean update(ArrayList<Invader> invaders) {
	// timer = (timer + 1) % 360;

	// if (timer == 359) {
	// setTarget(invaders);
	// lookAtTarget();
	// return true;
	// }
	// return false;
	// }

	// private void lookAtTarget() {
	// float targetXPos = currentTarget.x;
	// float targetYPos = currentTarget.y;
	// float gunLength = 30;

	// double xVal;
	// double yVal;

	// // Distance Formula
	// double ratioOfDistance = Math.pow(targetXPos - this.getCenterX(), 2)
	// + Math.pow(targetYPos - this.getCenterY(), 2);
	// ratioOfDistance = Math.sqrt(ratioOfDistance);
	// ratioOfDistance = gunLength / ratioOfDistance;

	// xVal = (1 - ratioOfDistance) * this.getCenterX() + ratioOfDistance *
	// targetXPos;
	// yVal = (1 - ratioOfDistance) * this.getCenterY() + ratioOfDistance *
	// targetYPos;

	// gunLineX = (float) xVal;
	// gunLineY = (float) yVal;
	// }

	// private float[] determineDxDy() {
	// float targetXPos = currentTarget.x;
	// float targetYPos = currentTarget.y;
	// float distance = (float) 2;

	// double xVal;
	// double yVal;

	// // Distance Formula
	// double ratioOfDistance = Math.pow(targetXPos - this.getCenterX(), 2)
	// + Math.pow(targetYPos - this.getCenterY(), 2);
	// ratioOfDistance = Math.sqrt(ratioOfDistance);
	// ratioOfDistance = distance / ratioOfDistance;

	// xVal = (1 - ratioOfDistance) * this.getCenterX() + ratioOfDistance *
	// targetXPos;
	// yVal = (1 - ratioOfDistance) * this.getCenterY() + ratioOfDistance *
	// targetYPos;

	// float[] change = new float[2];
	// change[0] = (float) xVal - this.getCenterX();
	// change[1] = (float) yVal - this.getCenterY();

	// return change;
	// }

}
