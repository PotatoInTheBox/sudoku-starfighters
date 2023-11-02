package model;

import java.util.ArrayList;
import java.util.List;

public class Game {

    public Player player;
    public ArrayList<Invader> invaders = new ArrayList<>();
    public ArrayList<Bullet> bullets = new ArrayList<>();
    public List<Entity> markedForRemoval = new ArrayList<>();

    private float width;
    private float height;

    public Game() {
        this(100, 100);
    }

    public Game(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public void startNewGame() {
        spawnPlayer(width - 20, height - 20, 40, 40);

        final float START_X_SPAWN = 10f;
        final float END_SPAWN = height - START_X_SPAWN;
        final int INVADER_X_COUNT = 10;
        for (int i = 0; i < INVADER_X_COUNT; i++) {
            float newSpawnXPos = (END_SPAWN - START_X_SPAWN) * i / INVADER_X_COUNT;
            final int INVADER_Y_COUNT = 5;
            final float START_Y_SPAWN = 10f;
            for (int j = 0; j < INVADER_Y_COUNT; j++) {
                float newSpawnYPos = START_Y_SPAWN + ((END_SPAWN - START_X_SPAWN) / INVADER_X_COUNT) * j;
                spawnInvader(newSpawnXPos, newSpawnYPos, 30, 30);
            }
            
        }

    }

    // This update is primarily for user input, game logic shouldn't go here
    // (can't guarentee that it will run at a constant rate)
    // TODO figure out a use for frame update loop.
    public void update() {

    }

    // Game logic here, this will run at a constant rate.
    // CURRENTLY SET TO 50hz
    public void fixedUpdate() {

        // remove all marked entities
        for (Entity entity : markedForRemoval) {
            if (entity.getClass() == Player.class) {
                // player = null;
            } else if (entity.getClass() == Bullet.class) {
                bullets.remove(entity);
            } else if (entity.getClass() == Invader.class) {
                invaders.remove(entity);
            } else {
                throw new RuntimeException("Cannot delete entity that is marked for deletion! " + entity);
            }
        }
        markedForRemoval.clear();

        // move bullets
        for (Entity e : bullets) {
            e.move();
        }

        // move invaders

        // player is moved elsewhere...

        // bound player to map
        if (player != null && player.isOutOfBounds(0f, 0f, (float) width, (float) height)) {
            player.setX((float) width / 2);
            player.setY((float) height / 2);
        }

        // bound invaders to map

        // bullet collision detection
        for (Bullet bullet : bullets) {
            Entity hitEntity = getBulletHitEntity(bullet);
            if (hitEntity != null) {
                markedForRemoval.add(bullet);
                markedForRemoval.add(hitEntity);
            }
        }
    }

    private Entity getBulletHitEntity(Bullet bullet) {
        if (bullet.getTeam() == Team.PLAYER) {
            for (Invader invader : invaders) {
                if (bullet.hasCollidedWith(invader))
                    return invader;
            }
        } else if (bullet.getTeam() == Team.INVADERS) {
            if (bullet.hasCollidedWith(player))
                return player;
        }
        return null;
    }

    public void shootPlayerBullet() {
        bullets.add(player.shootBullet());
    }

    public void addBullet(Bullet bullet) {
        bullets.add(bullet);
    }

    public void spawnPlayer(float x, float y, float width, float height) {
        Player player = new Player(x, y, width, height, 10f);
        this.player = player;
    }

    public void spawnInvader(float x, float y, float width, float height) {
        Invader invader = new Invader(x, y, width, height, 2f);
        invaders.add(invader);
    }

    // for rendering only
    public Player getPlayer() {
        // Not sure if I should return an error, maybe an "err" instead?
        // if (player == null)
        // throw new RuntimeException("Cannot get player, " +
        // "make sure you spawn one first");
        return player;
    }

    // for rendering only
    public List<Invader> getInvaders() {
        return invaders;
    }

    // for rendering only
    public List<Bullet> getBullets() {
        return bullets;
    }

    public void movePlayer(float analogX, float analogY) {
        if (player != null) {
            player.moveHorizontal(analogX);
            player.testMoveVertical(analogY);
        }
    }

    public final List<Entity> getMarkedForRemovalEntities() {
        return markedForRemoval;
    }
}
