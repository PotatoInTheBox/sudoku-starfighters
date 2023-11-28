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

    private List<Entity> entities = new ArrayList<>();
    public Queue<Runnable> markedForRemoval = new ConcurrentLinkedQueue<Runnable>();
    public Queue<Runnable> markedForSpawn = new ConcurrentLinkedQueue<Runnable>();

    private boolean isPlayerHit = false;
    private boolean processingGameLoop = false;

    private boolean lastIsPaused = false;
    private boolean isPaused = false;

    private float difficultyLevel = 0f;

    private float width;
    private float height;

    public Score score = new Score();

    public Game() {
        this(100, 100);
    }

    public Game(float width, float height) {
        this.width = width;
        this.height = height;
    }

    public void startGame() {
        delete(); // make sure there are no entities in the Game
        spawnPlayer(width - 20, height - 20, 30, 30);
        startNewRound();
    }

    public void startNewRound() {
        // keep player
        // delete all invaders
        deleteAllInvaders();
        // delete all bullets
        deleteAllBullets();

        // spawn new invaders
        InvaderCluster cluster = spawnInvaderCluster(0, 0, width / 2, height / 2);
        increaseDifficulty();
        cluster.setDifficulty(difficultyLevel);

        startPlayerLife();
    }

    public void startPlayerLife() {
        // keep player
        // keep invaders
        // delete all bullets
        deleteAllBullets();
        // bullets.clear();
        Player player = getPlayer();
        player.setX(width / 2);
        player.setY(height - player.collider.getHeight() * 1.5f);
        isPlayerHit = false;

    }

    private void deleteAllInvaders() {
        for (Entity entity : entities) {
            if (entity.getClass() == InvaderCluster.class) {
                entity.delete(); // clusters should also be deleting their children (invaders)
            }
        }
        // Iterator<Invader> invaders = getInvaders();
        // while (invaders.hasNext()) {
        // Invader invader = invaders.next();
        // invader.delete();
        // }
    }

    private void deleteAllBullets() {
        Iterator<Bullet> bullets = getBullets();
        while (bullets.hasNext()) {
            Bullet bullet = bullets.next();
            bullet.delete();
        }
    }

    // game logic fixed rate loop
    public void update() {
        processingGameLoop = true;

        /// remove marked for deletion items before starting tick
        while (markedForRemoval.isEmpty() == false) {
            markedForRemoval.remove().run();
        }

        /// update all entities
        for (Entity entity : entities) {
            if (entity.isFrozen() == false)
                entity.update();
        }

        // add items to game after ALL updates are finished processing
        while (markedForSpawn.isEmpty() == false) {
            markedForSpawn.remove().run();
        }

        processingGameLoop = false;
    }

    public void spawnPlayer(float x, float y, float width, float height) {
        Player player = new Player(this, x, y, width, height);
        Entity.instantiate(this, player);
    }

    public InvaderCluster spawnInvaderCluster(float x, float y, float width, float height) {
        InvaderCluster cluster = new InvaderCluster(this, 0, 0);
        Entity.instantiate(this, cluster);
        cluster.spawnAllInvaders(x, y, width, height, 3, 4);
        return cluster;
    }

    public void shootPlayerBullet() {
        // entities.add(player.shootBullet());
        // SoundPlayer.playSound("player_shoot.wav", false);
    }

    public void shootInvaderBullet(Invader invader) {
        // entities.add(invader.shootBullet());
        // SoundPlayer.playSound("enemy_shoot.wav", false);
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

    public void loseLife() {
        isPlayerHit = true;
        score.changeLives();
    }

    public void loseGame() {
        isPlayerHit = true;
        score.setLives(0);
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

    public Iterator<Invader> getInvaders() {
        return new EntityClassIterator<Invader>(entities, Invader.class);
    }

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

    public boolean isPaused() {
        return isPaused;
    }

    public void setPaused(boolean isPaused) {
        if (lastIsPaused == isPaused) {
            return;
        }
        lastIsPaused = isPaused;
        this.isPaused = isPaused;
        for (Entity entity : entities) {
            if (isPaused) {
                entity.setFrozen(true);
            } else {
                entity.setFrozen(false);
            }
        }

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
        Iterator<Invader> invaders = getInvaders();
        return invaders.hasNext() == false;
    }

    public boolean isGameOver() {
        return score.getLives() <= 0;
    }

    public void increaseDifficulty() {
        difficultyLevel += 1f;
    }

    public void addEntity(Entity entity) {
        addOnSpawnList(() -> {
            entities.add(entity);
        });
    }

    public void removeEntity(Entity entity) {
        addOnDeletedList(() -> {
            entities.remove(entity);
        });
    }

    public void delete() {
        for (Entity entity : entities) {
            entity.delete();
        }
        // for (Runnable runnable : markedForRemoval) {
        // runnable.run();
        // }
        while (markedForRemoval.isEmpty() == false) {
            markedForRemoval.remove().run();
        }
    }

    public List<Entity> getEntities() {
        return entities;
    }

    public void addOnDeletedList(Runnable event) {
        markedForRemoval.add(event);
        // if (processingGameLoop == false) {
        // while (markedForRemoval.isEmpty() == false) {
        // markedForRemoval.remove().run();
        // }
        // markedForRemoval.clear();
        // }
    }

    public void addOnSpawnList(Runnable event) {
        markedForSpawn.add(event);
        if (processingGameLoop == false) {
            while (markedForSpawn.isEmpty() == false) {
                markedForSpawn.remove().run();
            }
        }
    }

}
