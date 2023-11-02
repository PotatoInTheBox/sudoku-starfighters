package model;

public class Invader extends Entity {

    protected float speed;
    private static final float BULLET_SPEED = 4f;
    private InvaderType invaderType;

    public Invader(float positionX, float positionY, float sizeX, float sizeY, float speed) {
        super(positionX, positionY, sizeX, sizeY);
        this.speed = speed;
        this.team = Team.INVADERS;
        invaderType = InvaderType.ONION;
    }

    public Bullet shootBullet() {
        Bullet newBullet = new Bullet(x + width / 2, y + height / 2, BULLET_SPEED, team);
        return newBullet;
    }

    public void setInvaderType(InvaderType invaderType) {
        this.invaderType = invaderType;
    }

    public InvaderType getInvaderType(){
        return invaderType;
    }

}
