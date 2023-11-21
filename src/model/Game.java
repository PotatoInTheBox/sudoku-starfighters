package model;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import view_controller.sound.SoundPlayer;

public class Game {

    public Player player;
    public ArrayList<Invader> invaders = new ArrayList<>();
    public ArrayList<Bullet> bullets = new ArrayList<>();
    public List<Entity> markedForRemoval = new ArrayList<>();

    private boolean isPlayerHit = false;

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

    public void startNewRound() {
        clearAllEntities();
        spawnPlayer(width - 20, height - 20, 40, 40);
        final float xInvadersPadding = width / 2.5f;
        final float yInvadersHeight = height / 3;
        startInvadersCount = 0;
        spawnAllInvaders(xInvadersPadding, 20, width - xInvadersPadding, yInvadersHeight, 7, 5);
        applyInvaderMotion();
        startPlayerLife();
    }

    public void startPlayerLife() {
        bullets.clear();
        player.setCenterX(width / 2);
        player.setCenterY(height - player.getHeight() * 1.5f);
        isPlayerHit = false;
    }

    // Game logic here, this will run at a constant rate.
    public void update() {

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

        // if (invaders.isEmpty()) {
        // final float xInvadersPadding = width / 8;
        // final float yInvadersHeight = height / 3;
        // spawnAllInvaders(xInvadersPadding, 20, width - xInvadersPadding,
        // yInvadersHeight, 8, 5);
        // applyInvaderMotion();
        // }

        markedForRemoval.clear();

        // move bullets
        for (Entity bullet : bullets) {
            bullet.move();
        }

        // move invaders
        for (Entity invader : invaders) {
            invader.move();
        }

        // player is moved elsewhere...

        // bind player to map
        bindPlayerToCanvas();

        // bind invaders to map
        bindInvadersToCanvas();

        // bullet collision detection
        processBulletCollisions();

        // check invader -> player collision
        processInvaderPlayerCollision();

        tryInvaderShootBullet(5 + (78 - invaders.size()));

        // update invader's speed based on missing invaders
        updateInvadersSpeed();

        // lose all lives if invaders reach bottom of screen
        if (invadersReachedEnd())
            loseGame();
    }

    private void clearAllEntities() {
        invaders.clear();
        bullets.clear();
        markedForRemoval.clear();
    }

    private void spawnAllInvaders(float startX, float startY, float width, float height, int xCount, int yCount) {
        float invaderHeight = 35f;
        float invaderWidth = 35f;

        width = width - invaderWidth;
        width = width - invaderHeight;

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
                float spawnX = x * width / xCount + startX;
                float spawnY = y * height / yCount + startY;
                spawnInvader(spawnX, spawnY, 35, 35, invaderType);
                startInvadersCount++;
            }
        }
    }

    public void spawnPlayer(float x, float y, float width, float height) {
        Player player = new Player(x, y, width, height, playerSpeed);
        this.player = player;
    }

    public void spawnInvader(float x, float y, float width, float height) {
        Invader invader = new Invader(x, y, width, height, 2f);
        invaders.add(invader);
    }

    public void spawnInvader(float x, float y, float width, float height, InvaderType invaderType) {
        Invader invader = new Invader(x, y, width, height, 2f);
        invader.setInvaderType(invaderType);
        invaders.add(invader);
    }

    public void shootPlayerBullet() {
        bullets.add(player.shootBullet());
        SoundPlayer.playSound("player_shoot.wav");
    }

    public void shootInvaderBullet(Invader entity) {
        bullets.add(entity.shootBullet());
        SoundPlayer.playSound("enemy_shoot.wav");
    }

    private void tryInvaderShootBullet(int threshhold) {
        if (invaders.size() == 0) {
            return;
        }

        Random r = new Random();

        Invader toShoot = invaders.get(r.nextInt(Integer.MAX_VALUE) % invaders.size());

        float overLimitInvaders = Math.max(invaderBulletCount() - invaderBulletCount, 0);

        float reducedPercentModifier = 1 / (1 + overLimitInvaders);

        if (r.nextInt(1000) < threshhold * reducedPercentModifier) {
            shootInvaderBullet(toShoot);
        }
    }

    public void addBullet(Bullet bullet) {
        bullets.add(bullet);
    }

    public float getWidth() {
        return width;
    }

    public float getHeight() {
        return height;
    }

    private void processInvaderPlayerCollision() {
        for (Invader invader : invaders) {
            if (invader.getY() + invader.getHeight() > height) {
                loseGame();
            }
            if (invader.hasCollidedWith(player)) {
                isPlayerHit = true;
            }
        }
    }

    private boolean invadersReachedEnd() {
        for (Entity invader : invaders) {
            if (invader.isOutOfBounds(0, 0, width, height)) {
                if (invader.getY() + invader.getHeight() > height) {
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

    private void updateInvadersSpeed() {
        float newSpeed = invaderSpeed
                + (invaderMaxSpeed - invaderSpeed) * (float) (1 - Math.pow(invaders.size() / startInvadersCount, 2));
        for (Invader invader : invaders) {
            float sign = Math.signum(invader.getDx());
            invader.setDx(newSpeed * sign);
        }
    }

    private int invaderBulletCount() {
        int count = 0;
        for (Bullet bullet : bullets) {
            if (bullet.getTeam() == Team.INVADERS)
                count++;
        }
        return count;
    }

    private void processBulletCollisions() {
        for (Bullet bullet : bullets) {
            Entity hitEntity = getBulletHitEntity(bullet);
            if (hitEntity != null) {
                if (hitEntity.getTeam() == Team.INVADERS) {
                    markedForRemoval.add(bullet);
                    markedForRemoval.add(hitEntity);
                    Invader invader = (Invader) hitEntity;
                    score.changeScore(invader);
                    invaderHitSound();
                    return; // it hit something, don't keep processing loop
                } else if (hitEntity.getTeam() == Team.PLAYER) {
                    playerHit();
                    markedForRemoval.add(bullet);
                    return; // it hit something, don't keep processing loop
                }
            } else if (bullet.isOutOfBounds(0, 0, width, height)) {
                markedForRemoval.add(bullet);
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
            SoundPlayer.playSound("enemy_death_2.wav");
        } else {
            SoundPlayer.playSound("enemy_death.wav");
        }
    }

    private void bindPlayerToCanvas() {
        if (player != null && player.isOutOfBounds(0f, 0f, (float) width, (float) height)) {
            if (player.getX() < 0) {
                player.setX(0);
            }
            if (player.getX() + player.getWidth() > width) {
                player.setX(width - player.getWidth());
            }
            if (player.getY() < 0) {
                player.setY(0);
            }
            if (player.getY() + player.getHeight() > height) {
                player.setY(height - player.getHeight());
            }
        }
    }

    private void bindInvadersToCanvas() {
        boolean invaderOutofBounds = isAnyInvaderOutOfBounds();
        if (invaderOutofBounds) {
            flipInvaderDirection();
            applyInvaderMotion();
            for (Entity invader : invaders) {
                invader.move();
            }
            moveInvadersDown(invaderEncroachAmount);
        }
    }

    private boolean isAnyInvaderOutOfBounds() {
        for (Entity invader : invaders) {
            if (invader.isOutOfBounds(0, 0, width, height)) {
                // make sure that the offender is an X cord
                // it's extra computation but will make our debugging easier
                if (invader.getX() < 0 || invader.getX() + invader.getWidth() > width) {
                    return true;
                }
            }
        }
        return false;
    }

    public void applyInvaderMotion() {
        float newDx = invaderDirection * invaderSpeed;
        for (Entity invader : invaders) {
            invader.setDx(newDx);
        }
    }

    private void moveInvadersDown(float amount) {
        for (Entity invader : invaders) {
            invader.setY(invader.getY() + amount);
        }
    }

    public void movePlayer(float analogX, float analogY) {
        if (player != null) {
            player.moveHorizontal(analogX);
            player.testMoveVertical(analogY);
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

    public Player getPlayer() {
        // Not sure if I should return an error, maybe an "err" instead?
        // if (player == null)
        // throw new RuntimeException("Cannot get player, " +
        // "make sure you spawn one first");
        return player;
    }

    public List<Invader> getInvaders() {
        return invaders;
    }

    public List<Bullet> getBullets() {
        return bullets;
    }

    public float getInvaderDirection() {
        return invaderDirection;
    }

    public final List<Entity> getMarkedForRemovalEntities() {
        return markedForRemoval;
    }

    public void flipInvaderDirection() {
        invaderDirection = -invaderDirection;
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
}
