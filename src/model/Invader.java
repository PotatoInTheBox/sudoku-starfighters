package model;

public class Invader extends Entity
{

    public Invader(float positionX, float positionY, float sizeX, float sizeY) {
        super(positionX, positionY, sizeX, sizeY);
        this.team = Team.INVADERS;
    }

}
