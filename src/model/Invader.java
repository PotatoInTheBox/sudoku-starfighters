package model;

import java.util.Random;

public class Invader extends Entity {

    public Collider collider;
    public Sprite sprite;

    private InvaderType invaderType;

    public Invader(Game game, float x, float y, float width, float height, float speed) {
        super(game, x, y);
        collider = new Collider(game, x + (-width / 2), y + (-height / 2), width, height);
        sprite = new Sprite(game, x + (-width / 2), y + (-height / 2), width, height, null);

        this.team = Team.INVADERS;
        invaderType = InvaderType.ONION;

        addChild(collider, sprite);
    }

    @Override
    public void update() {
        for (Entity entity : game.entities) {
            if (entity.getClass() == Bullet.class) {
                Bullet bullet = (Bullet) entity;
                if (bullet.team == Team.PLAYER) {
                    if (bullet.collider.hasCollidedWith(collider)) {
                        // has been hit
                        delete();
                        bullet.delete();
                        return;
                    }
                }
            }
        }
    }

    public void shootBullet() {
        Bullet bullet = new Bullet(game, getX(), getY(), Bullet.BULLET_INVADER_SPEED, team);
		instantiate(bullet);
    }

    public void setInvaderType(InvaderType invaderType) {
        this.invaderType = invaderType;
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

}
