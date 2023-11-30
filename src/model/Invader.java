package model;

import java.util.Random;

import view_controller.sound.SoundPlayer;
import java.lang.Math;
import java.util.Random;

public class Invader extends Entity {

    public Collider collider;
    public Sprite sprite;

    private InvaderType invaderType;

    private int hp;
    private int maxHp;
    private HealthBar healthBar = null;

    public Invader(Game game, float x, float y, float width, float height, float speed) {
        super(game, x, y);
        collider = new Collider(game, 0, 0, width, height);
        collider.setCenter(x, y);
        sprite = new Sprite(game, 0, 0, width, height);
        sprite.setCenter(x, y);

        this.maxHp = 1;
        this.hp = maxHp;

        this.team = Team.INVADERS;
        invaderType = InvaderType.ONION;

        collider.instantiate();
        sprite.instantiate();
        addChild(collider, sprite);
    }

    @Override
    public void update() {
        for (Entity entity : game.getEntities()) {
            if (entity.getClass() == Bullet.class) {
                Bullet bullet = (Bullet) entity;
                if (bullet.team == Team.PLAYER) {
                    if (bullet.collider.hasCollidedWith(collider)) {
                        if (healthBar != null) {
                            Explosion explosion = new Explosion(game, bullet.getX(), bullet.getY(),
                                    bullet.sprite.getWidth(), bullet.sprite.getHeight());
                            instantiate(game, explosion);
                            bullet.delete();
                            hp -= 1;
                            healthBar.setHp(hp);
                            if (hp > 0) {
                                continue;
                            }
                            game.score.changeScore(500);
                        }

                        // has been hit
                        playHitSound();
                        Explosion explosion = new Explosion(game, getX(), getY(), sprite.getWidth(),
                                sprite.getHeight());
                        instantiate(game, explosion);
                        if (new Random().nextDouble() < 0.25)
                            dropCoin();
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

    public void setMaxHp(int newMaxHp) {
        if (this.maxHp == 1) {
            this.maxHp = newMaxHp;
            hp = maxHp;
            healthBar = new HealthBar(game, getX(), sprite.getHeight() / 2 + getY(), sprite.getWidth(), 30f, newMaxHp);
            healthBar.instantiate();
            addChild(healthBar);
        } else {
            this.maxHp = newMaxHp;
            hp = maxHp;
            if (healthBar != null) {
                healthBar.delete();
                healthBar = null;
            }
        }
    }

    private void dropCoin() {
        Coin coin = new Coin(game, getX(), getY(), 30, 30);
        instantiate(game, coin);
    }

    public void shootBullet() {
        SoundPlayer.playSound("enemy_shoot.wav");
        Bullet bullet = new Bullet(game, getX(), getY(), 1, team);
        bullet.sprite.setHeight(-bullet.sprite.getHeight()); // flip the bullet
        bullet.sprite.setCenter(getX(), getY());
        bullet.instantiate();
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
