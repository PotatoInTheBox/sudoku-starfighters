package model;

import java.util.ArrayList;
import java.util.Random;

import view_controller.sound.SoundPlayer;

import java.util.Iterator;
import java.lang.Math;

public class Turret extends Entity {

	public Collider collider;
	public Sprite sprite;

	private Sprite[] barrelSprites = new Sprite[8];
	private float barrelLength = 40f;

	public Invader currentTarget = null;

	public int shootTimer = 0;

	public Turret(Game game, float x, float y, float width, float height) {
		super(game, x, y);
		collider = new Collider(game, 0, 0, width, height);
		collider.setCenter(x, y);
		sprite = new Sprite(game, 0, 0, width, height);
		sprite.setCenter(x, y);
		this.team = Team.PLAYER;
		sprite.setImage("player_ship.png");
		for (int i = 0; i < barrelSprites.length; i++) {
			barrelSprites[i] = new Sprite(game, 0, 0, width/3, height/3);
			barrelSprites[i].setImage("player_ship.png");
		}
		addChild(collider, sprite);
		addChild(barrelSprites);
		aimBarrel(0, -1); // aim up initially
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

		// aim barrel towards target enemy (if about to shoot)
		if (shootTimer < 60 && currentTarget != null && currentTarget.isAlive) {
			float xOffset = currentTarget.getX() - getX();
			float yOffset = currentTarget.getY() - getY();
			float dist = (float) Math.sqrt(xOffset * xOffset + yOffset * yOffset);
			aimBarrel(xOffset / dist, yOffset / dist);
		}

		// check if shoot timer is ready
		if (shootTimer <= 0) {
			if (currentTarget != null && currentTarget.isAlive) {
				float xOffset = currentTarget.getX() - getX();
				float yOffset = currentTarget.getY() - getY();
				float dist = (float) Math.sqrt(xOffset * xOffset + yOffset * yOffset);
				shootBullet(0, 0, xOffset / dist, yOffset / dist);
			}
			currentTarget = getNextTarget();
		}
		

		if (shootTimer <= 0) {
			shootTimer = 120;
		}
		shootTimer -= 1;
	}

	private void aimBarrel(float dx, float dy) {
		for (int i = 0; i < barrelSprites.length; i++) {
			float segLen = i / (float)barrelSprites.length;
			barrelSprites[i].setCenter(getX() + segLen * dx * barrelLength, getY() + segLen * dy * barrelLength);
		}
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
}
