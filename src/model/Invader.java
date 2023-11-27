package model;

import java.util.Random;

import view_controller.sound.SoundPlayer;
import java.lang.Math;

public class Invader extends Entity {

    public Collider collider;
    public Sprite sprite;

    private InvaderType invaderType;

    public Invader(Game game, float x, float y, float width, float height, float speed) {
        super(game, x, y);
        collider = new Collider(game, x + (-width / 2), y + (-height / 2), width, height);
        sprite = new Sprite(game, x + (-width / 2), y + (-height / 2), width, height);

        this.team = Team.INVADERS;
        invaderType = InvaderType.ONION;

        addChild(collider, sprite);
    }

    @Override
    public void update() {
        for (Entity entity : game.getEntities()) {
            if (entity.getClass() == Bullet.class) {
                Bullet bullet = (Bullet) entity;
                if (bullet.team == Team.PLAYER) {
                    if (bullet.collider.hasCollidedWith(collider)) {
                        // has been hit
                        playHitSound();
                        delete();
                        game.score.changeScore(this);
                        bullet.delete();
                        if (getParent() != null && getParent().getClass() == InvaderCluster.class) {
                            ((InvaderCluster) getParent()).updateClusterSpeed();
                        }
                        return;
                    }
                }
            }
        }
    }

    public void shootBullet() {
        SoundPlayer.playSound("enemy_shoot.wav");
        Bullet bullet = new Bullet(game, getX(), getY(), Bullet.BULLET_INVADER_SPEED, team);
        bullet.sprite.setHeight(-bullet.sprite.getHeight()); // flip the bullet
        bullet.sprite.setCenter(0, 0);
        instantiate(bullet);
    }

    public void setInvaderType(InvaderType invaderType) {
        this.invaderType = invaderType;
        chooseSprites(invaderType);
    }

    public InvaderType getInvaderType() {
        return invaderType;
    }

    public int getScoreChange() {
        switch (invaderType) {
            case ONION:
                return 10;
            case SPIDER:
                return 15;
            case MUSHROOM:
                return 20;
            default:
                return 0;
        }
    }

    private void playHitSound() {
        Random r = new Random();
        if (r.nextBoolean()) {
            SoundPlayer.playSound("enemy_death_2.wav");
        } else {
            SoundPlayer.playSound("enemy_death.wav");
        }
    }

    private void chooseSprites(InvaderType invaderType) {
        sprite.clearImages();
        if (invaderType == InvaderType.ONION) {
            sprite.addImage("enemy1_frame1.png", "enemy1_frame2.png");
        } else if (invaderType == InvaderType.SPIDER) {
            sprite.addImage("enemy2_frame1.png", "enemy2_frame2.png");
        } else if (invaderType == InvaderType.MUSHROOM) {
            sprite.addImage("enemy3_frame1.png", "enemy3_frame2.png");
        }
    }

}
