package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.ArrayDeque;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.Queue;
import java.util.Iterator;
import java.util.ConcurrentModificationException;
import java.util.NoSuchElementException;

import view_controller.sound.SoundPlayer;

public class Game {

    public Player player;
    public ArrayList<Invader> invaders = new ArrayList<>();
    public ArrayList<Bullet> bullets = new ArrayList<>();
    public List<Entity> entities = new ArrayList<>();
    public Queue<Runnable> markedForRemoval = new ConcurrentLinkedQueue<Runnable>();
    public Queue<Runnable> markedForSpawn = new ConcurrentLinkedQueue<Runnable>();

    private List<EntityEvent> entitySpawnListeners = new ArrayList<>();
    private List<EntityEvent> entityDestroyListeners = new ArrayList<>();

    private boolean isPlayerHit = false;
    private boolean processingGameLoop = false;

    private float invaderDirection = -1f;
    private float invaderEncroachAmount = 20f;
    private float playerSpeed = 3f;

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

    private float startInvadersCount;

    private float width;
    private float height;

    private Score score = new Score();

    public Game() {
        this(100, 100);
    }

    public Game(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public void startGame() {
        delete(); // make sure there are no entities in the Game
        spawnPlayer(width - 20, height - 20, 20, 30);
        spawnInvaderCluster(0, 0, width / 2, height / 2);
        startNewRound();
    }

    public void startNewRound() {
        // keep player
        // delete all invaders
        // delete all bullets

        final float xInvadersPadding = width / 2.5f;
        final float yInvadersHeight = height / 3;
        startInvadersCount = 0;
        // spawnAllInvaders(xInvadersPadding, 20, width - xInvadersPadding,
        // yInvadersHeight, 7, 5);
        // applyInvaderMotion();
        startPlayerLife();
    }

    public void startPlayerLife() {
        // keep player
        // keep invaders
        // delete all bullets
        // bullets.clear();
        Player player = getPlayer();
        player.setX(width / 2);
        player.setY(height - player.collider.getHeight() * 1.5f);
        isPlayerHit = false;

    }

    // Game logic here, this will run at a constant rate.
    public void update() {
        processingGameLoop = true;
        for (Runnable runnable : markedForRemoval) {
            runnable.run();
        }
        markedForRemoval.clear();
        // remove all items marked for deletion

        // update all entities
        for (Entity entity : entities) {
            entity.update();
        }

        // remove all marked entities
        // for (Entity entity : markedForRemoval) {
        // if (entity.getClass() == Player.class) {
        // // player = null;
        // } else if (entity.getClass() == Bullet.class) {
        // bullets.remove(entity);
        // } else if (entity.getClass() == Invader.class) {
        // invaders.remove(entity);
        // } else {
        // throw new RuntimeException("Cannot delete entity that is marked for deletion!
        // " + entity);
        // }
        // }

        // // if (invaders.isEmpty()) {
        // // final float xInvadersPadding = width / 8;
        // // final float yInvadersHeight = height / 3;
        // // spawnAllInvaders(xInvadersPadding, 20, width - xInvadersPadding,
        // // yInvadersHeight, 8, 5);
        // // applyInvaderMotion();
        // // }

        // markedForRemoval.clear();

        // // move bullets
        // for (Entity bullet : bullets) {
        // bullet.move();
        // }

        // // move invaders
        // for (Entity invader : invaders) {
        // invader.move();
        // }

        // // player is moved elsewhere...

        // // bind player to map
        // bindPlayerToCanvas();

        // // bind invaders to map
        // bindInvadersToCanvas();

        // // bullet collision detection
        // processBulletCollisions();

        // // check invader -> player collision
        // processInvaderPlayerCollision();

        // tryInvaderShootBullet(5 + (78 - invaders.size()));

        // // update invader's speed based on missing invaders
        // updateInvadersSpeed();

        // // lose all lives if invaders reach bottom of screen
        // if (invadersReachedEnd())
        // loseGame();

        for (Runnable runnable : markedForSpawn) {
            runnable.run();
        }
        markedForSpawn.clear();
        processingGameLoop = false;
    }

    public void spawnPlayer(float x, float y, float width, float height) {
        Player player = new Player(this, x, y, width, height, playerSpeed);
        Entity.instantiate(this, player);
    }

    public void spawnInvaderCluster(float x, float y, float width, float height) {
        InvaderCluster cluster = new InvaderCluster(this, 0, 0);
        Entity.instantiate(this, cluster);
        cluster.spawnAllInvaders(x, y, width, height, 3, 4);
    }

    public void shootPlayerBullet() {
        // entities.add(player.shootBullet());
        SoundPlayer.playSound("player_shoot.wav", false);
    }

    public void shootInvaderBullet(Invader invader) {
        // entities.add(invader.shootBullet());
        SoundPlayer.playSound("enemy_shoot.wav", false);
    }

    // TODO

    public void addBullet(Bullet bullet) {
        entities.add(bullet);
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    // TODO
    private void processInvaderPlayerCollision() {
        for (Invader invader : invaders) {
            if (invader.getY() + invader.collider.getHeight() > height) {
                loseGame();
            }
            if (invader.collider.hasCollidedWith(player.collider) && player.getTeam() == Team.PLAYER) {
                isPlayerHit = true;
            }
        }
    }

    // TODO
    private boolean invadersReachedEnd() {
        for (Invader invader : invaders) {
            if (invader.collider.isOutOfBounds(0, 0, width, height)) {
                if (invader.getY() + invader.collider.getHeight() > height) {
                    return true;
                }
            }
        }
        return false;
    }

    private void loseGame() {
        isPlayerHit = true;
        score.setLives(0);
    }

    // TODO
    private void updateInvadersSpeed() {
        float newSpeed = invaderSpeed
                + (invaderMaxSpeed - invaderSpeed) * (float) (1 - Math.pow(invaders.size() / startInvadersCount, 2));
        for (Invader invader : invaders) {
            float sign = Math.signum(invader.getDx());
            invader.setDx(newSpeed * sign);
        }
    }

    // TODO
    private int invaderBulletCount() {
        int count = 0;
        for (Bullet bullet : bullets) {
            if (bullet.getTeam() == Team.INVADERS)
                count++;
        }
        return count;
    }

    // TODO
    private void processBulletCollisions() {
        for (Bullet bullet : bullets) {
            Entity hitEntity = getBulletHitEntity(bullet);
            if (hitEntity != null) {
                if (hitEntity.getTeam() == Team.INVADERS) {
                    // markedForRemoval.add(bullet);
                    // markedForRemoval.add(hitEntity);
                    Invader invader = (Invader) hitEntity;
                    score.changeScore(invader);
                    invaderHitSound();
                    return; // it hit something, don't keep processing loop
                } else if (hitEntity.getTeam() == Team.PLAYER) {
                    playerHit();
                    // markedForRemoval.add(bullet);
                    return; // it hit something, don't keep processing loop
                }
            } else if (bullet.collider.isOutOfBounds(0, 0, width, height)) {
                // markedForRemoval.add(bullet);
            }
        }
    }

    private void playerHit() {
        isPlayerHit = true;
        score.changeLives();
    }

    private void invaderHitSound() {
        Random r = new Random();
        if (r.nextBoolean()) {
            SoundPlayer.playSound("enemy_death_2.wav", false);
        } else {
            SoundPlayer.playSound("enemy_death.wav", false);
        }
    }

    // TODO
    public void movePlayer(float analogX, float analogY) {
        if (player != null) {
            player.moveHorizontal(analogX);
            player.moveVertical(analogY);
        }
    }

    // TODO
    private Entity getBulletHitEntity(Bullet bullet) {
        if (bullet.getTeam() == Team.PLAYER) {
            for (Invader invader : invaders) {
                if (bullet.collider.hasCollidedWith(invader.collider))
                    return invader;
            }
        } else if (bullet.getTeam() == Team.INVADERS) {
            if (bullet.collider.hasCollidedWith(player.collider))
                return player;
        }
        return null;
    }

    public Player getPlayer() {
        // Not sure if I should return an error, maybe an "err" instead?
        // if (player == null)
        // throw new RuntimeException("Cannot get player, " +
        // "make sure you spawn one first");
        for (Entity entity : entities) {
            if (entity.getClass() == Player.class)
                return (Player) entity;
        }
        return null;

    }

    // TODO
    public Iterator<Invader> getInvaders() {
        return new EntityClassIterator<Invader>(entities, Invader.class);
    }

    // TODO
    public Iterator<Bullet> getBullets() {
        return new EntityClassIterator<Bullet>(entities, Bullet.class);
    }

    private class EntityClassIterator<T> implements Iterator<T> {

        Iterator<Entity> entitiesIterator;
        Class<T> entityType;
        boolean hasAvailable = false;
        T currentEntity = null;

        public EntityClassIterator(List<Entity> entities, Class<T> entityType) {
            this.entityType = entityType;
            entitiesIterator = entities.iterator();
            seekNext();
        }

        @Override
        public boolean hasNext() {
            return hasAvailable;
        }

        @Override
        public T next() {
            if (hasAvailable == false) {
                throw new NoSuchElementException();
            }
            T returnEntity = currentEntity;
            hasAvailable = false;
            seekNext();
            return returnEntity;
        }

        private void seekNext() {
            while (hasAvailable == false && entitiesIterator.hasNext()) {
                Entity entity = entitiesIterator.next();
                if (entityType.isInstance(entity)) {
                    currentEntity = entityType.cast(entity);
                    hasAvailable = true;
                }
            }
        }

    }

    // TODO
    public final List<Entity> getMarkedForRemovalEntities() {
        return null;
        // return markedForRemoval;
    }

    public boolean isPlayerHit() {
        return isPlayerHit;
    }

    public void setPlayerHit(boolean isPlayerHit) {
        this.isPlayerHit = isPlayerHit;
    }

    public int getScore() {
        return score.getScore();
    }

    public Score getUser() {
        return score;
    }

    public int getLives() {
        return score.getLives();
    }

    public boolean hasWon() {
        return invaders.size() == 0;
    }

    public boolean isGameOver() {
        return score.getLives() <= 0;
    }

    public void increaseDifficulty() {
        difficultyLevel += 1f;
        // modify values of the game to increase difficulty
        invaderSpeed = invaderBaseSpeed + invaderBaseSpeed * invaderDifficultyScalingSpeed * difficultyLevel;
        invaderMaxSpeed = invaderMaxSpeed + invaderMaxBaseSpeed *
                invaderMaxDifficultyScalingSpeed * difficultyLevel;
        invaderBulletCount = baseInvaderBulletCount + baseInvaderBulletCount *
                invaderDifficultyScalingBulletCount * difficultyLevel;
    }

    public void onEntitySpawned(EntityEvent event) {
        entitySpawnListeners.add(event);
    }

    public boolean removeOnEntitySpawned(EntityEvent event) {
        return entityDestroyListeners.remove(event);
    }

    public void onEntityDestroyed(EntityEvent event) {
        entitySpawnListeners.add(event);
    }

    public boolean removeOnEntityDestroyed(EntityEvent event) {
        return entityDestroyListeners.remove(event);
    }

    private void triggerAllSpawnListeners(Entity entity) {
        for (EntityEvent event : entitySpawnListeners) {
            event.run(entity);
        }
    }

    private void triggerAllDestroyListeners(Entity entity) {
        for (EntityEvent event : entityDestroyListeners) {
            event.run(entity);
        }
    }

    public void addEntity(Entity entity) {
        entities.add(entity);
    }

    public void removeEntity(Entity entity) {
        entities.remove(entity);
    }

    public void delete() {
        for (Entity entity : entities) {
            entity.delete();
        }
        for (Runnable runnable : markedForRemoval) {
            runnable.run();
        }
        markedForRemoval.clear();
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void addOnDeletedList(Runnable event) {
        markedForRemoval.add(event);
        if (processingGameLoop == false) {
            for (Runnable runnable : markedForRemoval) {
                runnable.run();
            }
            markedForRemoval.clear();
        }
    }

    public void addOnSpawnList(Runnable event) {
        markedForSpawn.add(event);
        if (processingGameLoop == false) {
            for (Runnable runnable : markedForSpawn) {
                runnable.run();
            }
            markedForSpawn.clear();
        }
    }

}
