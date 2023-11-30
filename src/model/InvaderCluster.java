package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.lang.Math;

import javafx.scene.paint.Color;
import view_controller.sound.SoundPlayer;

import java.util.Iterator;

/**
 * InvaderCluster contains Invaders to move and manage. The invader cluster
 * manages how invaders should move in a group. It also manages when to change
 * direction, where to spawn the invaders, and when they reach the end.
 * 
 * The InvaderCluster has a collider that is automatically changes depending on
 * the invaders contained inside this entity. There is no sprite inside of the
 * InvaderCluster.
 */
public class InvaderCluster extends Entity {

    private final static float INVADER_WIDTH = 30f;
    private final static float INVADER_HEIGHT = 30f;
    private static final Color[] INVADER_PALETTE = { Color.PURPLE, Color.BLUE, Color.LIGHTGREEN, Color.RED.brighter() };
    public Collider collider;
    private int lastChildrenCount = 0;
    private float speed = 1f;
    private float direction = 1f;
    private boolean isMovingVertically = false;
    private float downAmount = 30f;

    private float moveDownOriginY = 0f;
    private float distanceTravelled = 0f;

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
    Random random = new Random();

    /**
     * Construct invader cluster at a given x and y position. The width and
     * height are automatically determined by the invaders spawned.
     * 
     * @param game to instantiate to
     * @param x    absolute x to spawn at
     * @param y    absolute y to spawn at
     */
    public InvaderCluster(Game game, float x, float y) {
        super(game, x, y);
        collider = new Collider(game, 0, 0, 0, 0);
        collider.instantiate();
        addChild(collider);
    }

    @Override
    public void update() {
        // recalculate hitbox if needed
        if (getChildren().size() != lastChildrenCount)
            calculateHitBox();
        lastChildrenCount = getChildren().size();

        // move invaders left/right/down
        if (isMovingVertically) {
            move(0, speed);
            distanceTravelled += speed;
            if (moveDownOriginY + downAmount < getY()) {
                isMovingVertically = false;
            }
        } else {
            move(direction * speed, 0);
            distanceTravelled += speed;
        }

        if (distanceTravelled > 40f) {
            distanceTravelled = 0f;
            for (Entity entity : getChildren()) {
                if (entity.getClass() == Invader.class) {
                    Invader invader = (Invader) entity;
                    invader.sprite.nextFrame();
                }
            }
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

        // kill player if invader cluster touches goal
        for (Entity entity : game.getEntities()) {
            if (entity.getClass() == InvaderGoal.class) {
                InvaderGoal invaderGoal = (InvaderGoal) entity;
                if (collider.hasCollidedWith(invaderGoal.collider)) {
                    game.score.setLives(0);
                }
            }
        }
    }

    /**
     * Calculates the new hitbox/collider size depending on how big it needs to
     * be to **contain** all the invaders. Contain here means that the invaders
     * will be fully encompassed inside of the InvaderCluser collider, such that
     * the all verticies of all invader colliders are inside the area of the
     * InvaderCluser collider.
     */
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

    /**
     * Spawn all invaders at the given positions and size. The invaders will
     * have their own size. This method simply places the invaders within
     * a bounding region given (x y width height) while making sure to contain
     * all invaders inside of the region.
     * 
     * @param x      absolute x to spawn at (centered)
     * @param y      absolute y to spawn at (centered)
     * @param width  to scale collider of InvaderCluster to
     * @param height to scale collider of InvaderCluster to
     * @param xCount how many invaders to place left to right
     * @param yCount how many invaders to place up to down
     */
    public void spawnAllInvaders(float x, float y, float width, float height, int xCount, int yCount) {
        InvaderType lastInvaderType = null;
        Color lastColor = null;
        startInvadersCount = 0;

        for (int r = 0; r < yCount; r++) {
            Color color = null;
            InvaderType invaderType = null;
            color = getNextUniqueColor(lastColor);
            lastColor = color;
            invaderType = getNextUniqueSprite(lastInvaderType);
            lastInvaderType = invaderType;

            for (int c = 0; c < xCount; c++) {
                float spawnX = c * width / xCount + x + INVADER_WIDTH / 2;
                float spawnY = r * height / yCount + y + INVADER_HEIGHT / 2;
                spawnInvader(spawnX, spawnY, INVADER_WIDTH, INVADER_HEIGHT, invaderType, color);
                startInvadersCount += 1;
            }
        }
    }

    /**
     * Spawns a boss invader at the specified location. The height given will
     * determine the size of this invader. A maxHp will also be passed to
     * determine the amount of health this invader will have.
     * 
     * @param game   to instantiate to
     * @param x      absolute x to spawn at (centered)
     * @param y      absolute y to spawn at (centered)
     * @param width  to scale collider and sprite to
     * @param height to scale collider and sprite to
     * @param maxHp  of the spawned boss
     */
    public void spawnBoss(float x, float y, float width, float height, int maxHp) {
        SoundPlayer.playBossThemeMusic();
        Color color = null;
        InvaderType invaderType = null;
        color = getNextUniqueColor(null);
        invaderType = getNextUniqueSprite(null);
        Invader boss = spawnInvader(x, y, width, height, invaderType, color);
        boss.setMaxHp(maxHp);
        System.out.println("Boss max HP: " + maxHp);
        startInvadersCount += 1;
    }

    /**
     * Spawn an invader at a given position and size. The size will determine
     * the size of the invader. The invader type and color will determine the
     * sprite and sprite color of the invader.
     * 
     * @param game        to instantiate to
     * @param x           absolute x to spawn at (centered)
     * @param y           absolute y to spawn at (centered)
     * @param width       to scale collider and sprite of Invader to
     * @param height      to scale collider and sprite of Invader to
     * @param invaderType to represent the invader with (for sprites)
     * @param color       to paint the sprite to
     * @return a new invader which has been instantiated
     */
    public Invader spawnInvader(float x, float y, float width, float height, InvaderType invaderType, Color color) {
        Invader invader = new Invader(game, x, y, width, height, 2f);
        invader.setInvaderType(invaderType);
        invader.sprite.setColor(color);
        invader.instantiate();
        addChild(invader);
        return invader;
    }

    /**
     * Sets the difficulty of the InvaderCluster. This applies a new speed and
     * bullet count value to the InvaderCluster, thus affecting how the Invaders
     * behave.
     * 
     * @param amount new difficulty multiplier to set to.
     */
    public void setDifficulty(float amount) {
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

    /**
     * Updates invader cluster speeds by determining a new speed for the invader
     * cluster based on the speed multipliers and the current invader count.
     */
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
     * Randomly chooses a new color, it needs the lastColor of the previous call
     * to attempt to generate a new color that is unique (so that the colors
     * at least alternate and don't repreat consecutively).
     * 
     * @param lastColor color of the previous .getNextUniqueColor() call
     * @return a new unique color from the INVADER_PALETTE options
     */
    private Color getNextUniqueColor(Color lastColor) {
        Color color = lastColor;
        for (int i = 0; i < 5; i++) {
            color = INVADER_PALETTE[Math.floorMod(random.nextInt(), INVADER_PALETTE.length)];
            if (lastColor != color) {
                break;
            }
        }
        return color;
    }

    /**
     * Randomly chooses a new InvaderType, it needs the lastInvaderType of the
     * previous call to attempt to generate a new InvaderType that is unique (so
     * that the InvaderType at least alternate and don't repreat consecutively).
     * 
     * @param lastInvaderType InvaderType of the last .getNextUniqueSprite() call
     * @return a new unique InvaderType from the InvaderType options
     */
    private InvaderType getNextUniqueSprite(InvaderType lastInvaderType) {
        InvaderType invaderType = null;
        for (int i = 0; i < 5; i++) {
            switch (Math.floorMod(random.nextInt(), 3)) {
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
            if (lastInvaderType != invaderType) {
                break;
            }
        }
        return invaderType;
    }

    /**
     * Push cluster back into the bounds of the game (so that it isn't clipping
     * into the bounds).
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

    /**
     * Attempt to shoot a bullet by randomly determining if and where to shoot
     * from. The method will choose to shoot depending on how many invaders
     * exist and what the maximum bullet count is. The bullet will shoot if the
     * random chance meets the threshhold. If a shot will be made, then a random
     * invader is chosen to shoot.
     * 
     * The max bullet only reduces the likelyhood of there being more bullets
     * than this chance. It does not try to hit that peak of bullets.
     * 
     * @param threshhold to be met before shooting. 1000 means no chance, 0
     *                   means highly likely (unless the reducedPercentModifier is
     *                   active).
     */
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

    /**
     * Get the amount of invader bullets currently active in the game.
     * 
     * @return count of bullets in game.
     */
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

}
