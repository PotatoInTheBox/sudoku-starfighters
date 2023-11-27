package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Iterator;

public class InvaderCluster extends Entity {

    public Collider collider;
    private int lastChildrenCount = 0;
    private float speed = 1f;
    private float direction = 1f;
    private boolean isMovingVertically = false;
    private float downAmount = 30f;
    private float moveDownOriginY = 0f;
    private final int bulletCountLimit = 4;

    private int startInvadersCount = 0;

    private float difficultyLevel = 1f;

    private float invaderBaseSpeed = 0.6f;
    private float invaderSpeed = invaderBaseSpeed;
    private float invaderDifficultyScalingSpeed = 0.1f;

    private float invaderMaxBaseSpeed = 1f;
    private float invaderMaxSpeed = invaderMaxBaseSpeed;
    private float invaderMaxDifficultyScalingSpeed = 0.2f;

    private float baseInvaderBulletCount = 4f;
    private float invaderBulletCount = baseInvaderBulletCount;
    private float invaderDifficultyScalingBulletCount = 1f;

    public InvaderCluster(Game game, float x, float y) {
        super(game, x, y);
        collider = new Collider(game, 0, 0, 0, 0);
        addChild(collider);
    }

    public void update() {
        // recalculate hitbox if needed
        if (getChildren().size() != lastChildrenCount)
            calculateHitBox();
        lastChildrenCount = getChildren().size();

        // move invaders left/right/down
        if (isMovingVertically) {
            move(0, speed);
            if (moveDownOriginY + downAmount < getY()){
                isMovingVertically = false;
            }
        } else {
            move(direction * speed, 0);
        }

        // check if hitting a wall
        if (collider.isOutOfBounds(0, 0, game.getWidth(), game.getHeight())) {
            // bind the invaders to the game
            bindToGame();
            // reverse direction
            direction = direction * -1f;
            // set position where we started moving down and signal we are going down
            moveDownOriginY = getY();
            isMovingVertically = true;
        }

        List<Invader> invaders = new ArrayList<>();
        for (Entity entity : getChildren()) {
            if (entity.getClass() == Invader.class) {
                Invader invader = (Invader) entity;
                invaders.add(invader);
            }
        }
        tryInvaderShootBullet(5 + (78 - invaders.size()));

        if (hasClusterReachedEnd()){
            System.out.println("Game has reached end!");
        }
    }

    public void calculateHitBox() {
        boolean firstOne = true;
        float minX = getX();
        float minY = getY();
        float maxX = minX;
        float maxY = minY;
        for (Entity entity : getChildren()) {
            if (entity.getClass() == Invader.class) {
                Invader invader = (Invader) entity;
                float newMinX = invader.collider.getX();
                float newMinY = invader.collider.getY();
                float newMaxX = newMinX + invader.collider.getWidth();
                float newMaxY = newMinY + invader.collider.getHeight();
                if (firstOne) {
                    minX = newMinX;
                    minY = newMinY;
                    maxX = newMaxX;
                    maxY = newMaxY;
                    firstOne = false;
                } else {
                    minX = Math.min(newMinX, minX);
                    minY = Math.min(newMinY, minY);
                    maxX = Math.max(newMaxX, maxX);
                    maxY = Math.max(newMaxY, maxY);
                }

            }
        }
        collider.setX(minX);
        collider.setY(minY);
        collider.setWidth(maxX - minX);
        collider.setHeight(maxY - minY);
    }

    public void spawnAllInvaders(float startX, float startY, float width, float height, int xCount, int yCount) {
        float invaderWidth = 35f;
        float invaderHeight = 35f;
        startInvadersCount = 0;

        width = width - invaderWidth / 2;
        width = width - invaderHeight / 2;

        for (int y = 0; y < yCount; y++) {
            for (int x = 0; x < xCount; x++) {
                InvaderType invaderType;
                switch (y % 3) {
                    case 0:
                        invaderType = InvaderType.ONION;
                        break;
                    case 1:
                        invaderType = InvaderType.SPIDER;
                        break;
                    default:
                        invaderType = InvaderType.MUSHROOM;
                        break;
                }
                float spawnX = x * width / (xCount - 1) + startX + invaderWidth / 2;
                float spawnY = y * height / (yCount - 1) + startY + invaderHeight / 2;
                spawnInvader(spawnX, spawnY, invaderWidth, invaderHeight, invaderType);
                startInvadersCount += 1;
            }
        }
    }

    public void spawnInvader(float x, float y, float width, float height, InvaderType invaderType) {
        Invader invader = new Invader(game, x, y, width, height, 2f);
        invader.setInvaderType(invaderType);
        addChild(invader);
        instantiate(invader);
    }

    public void setDifficulty(float amount){
        difficultyLevel = amount;
        invaderSpeed = invaderBaseSpeed + invaderBaseSpeed * invaderDifficultyScalingSpeed * difficultyLevel;
        invaderMaxSpeed = invaderMaxSpeed + invaderMaxBaseSpeed *
                invaderMaxDifficultyScalingSpeed * difficultyLevel;
        invaderBulletCount = baseInvaderBulletCount + baseInvaderBulletCount *
                invaderDifficultyScalingBulletCount * difficultyLevel;

        System.out.println("Difficulty settings: ");
        System.out.println("\tinvaderSpeed: " + invaderSpeed);
        System.out.println("\tinvaderMaxSpeed: " + invaderMaxSpeed);
        System.out.println("\tinvaderBulletCount: " + invaderBulletCount);
        updateClusterSpeed();
    }

    public void updateClusterSpeed() {
        List<Invader> invaders = new ArrayList<>();
        for (Entity entity : getChildren()) {
            if (entity.getClass() == Invader.class) {
                Invader invader = (Invader) entity;
                invaders.add(invader);
            }
        }
        float newSpeed = invaderSpeed
                + (invaderMaxSpeed - invaderSpeed) * (float) (1 - Math.pow(invaders.size() / startInvadersCount, 2));
        speed = newSpeed;
    }

    /**
     * Push cluster back into the bounds of the game
     */
    private void bindToGame() {
        if (collider.isOutOfBounds(0f, 0f, game.getWidth(), game.getHeight())) {
            if (collider.getX() < 0) {
                setX(getX() - collider.getX());
            }
            if (collider.getX() + collider.getWidth() > game.getWidth()) {
                setX(game.getWidth() - collider.getWidth() + getX() - collider.getX());
            }
            if (collider.getY() < 0) {
                setY(getY() - collider.getY());
            }
            if (collider.getY() + collider.getHeight() > game.getHeight()) {
                setY(game.getHeight() - collider.getHeight() + getY() - collider.getY());
            }
        }
    }

    private void tryInvaderShootBullet(int threshhold) {
        List<Invader> invaders = new ArrayList<>();
        for (Entity entity : getChildren()) {
            if (entity.getClass() == Invader.class) {
                Invader invader = (Invader) entity;
                invaders.add(invader);
            }
        }

        if (invaders.size() == 0) {
            return;
        }

        Random r = new Random();

        Invader shootingInvader = invaders.get(r.nextInt(Integer.MAX_VALUE) % invaders.size());

        float overLimitInvaders = Math.max(invaderBulletCount() - bulletCountLimit, 0);

        float reducedPercentModifier = 1 / (1 + overLimitInvaders);

        if (r.nextInt(1000) < threshhold * reducedPercentModifier) {
            shootingInvader.shootBullet();
        }
    }

    private int invaderBulletCount() {
        int count = 0;
        Iterator<Bullet> bullets = game.getBullets();
        while (bullets.hasNext()) {
            Bullet bullet = bullets.next();
            if (bullet.getTeam() == Team.INVADERS)
                count++;
        }
        return count;
    }

    private boolean hasClusterReachedEnd() {
        if (collider.isOutOfBounds(0, 0, game.getWidth(), game.getHeight())) {
            if (collider.getY() + collider.getHeight() > game.getHeight()) {
                return true;
            }
        }
        return false;
    }

}
