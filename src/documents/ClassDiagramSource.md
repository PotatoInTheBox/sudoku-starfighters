# Class Diagram

```plantuml
@startuml SpaceInvadersClassDiagram
package "model" {
  class Game {
    - player: Player
    - invadersGroup: InvaderGroup[]
    - bullets: Bullet[]
    - score: int
    - isGameOver: boolean
    + getScore(): int
    + getAllInvaders(): Invader[]
    + getAllBullets(): Bullet[]
    + getPlayer(): Player
    + startGame()
    + update()
    + endGame()
  }

  class Player {
    + moveLeft()
    + moveRight()
    + shootBullet()
  }

  class InvadersGroup {
    - aliveInvaders: Invader[]
    - invadersColumns: InvadersColumn[]
    + getInvaders(): Invader[]
    + getFrontInvaders(): Invader[]
    + setDirection(direction: float)
    + moveVertically()
    + moveHorizontally()
    + hasReachedEdge(): boolean
    + hasReachedEnd(): boolean
    + shootBullet()
  }

  class InvadersColumn {
    + invaders: Invader[]
    + getFrontInvader(): Invader
  }

  class Invader {
    - directionX: int
    + setDirection(direction: float)
    + moveVertically()
    + moveHorizontally()
    + hasReachedEdge(): boolean
    + hasReachedEnd(): boolean
    + canShoot(): boolean
    + shootBullet()
  }

  class Bullet {
    - directionY: float
    + move()
  }

  class Score {
    - value: int
    + increaseScore(points: int)
  }

  abstract class Entity {
    - positionX: float
    - positionY: float
    - sizeX: float
    - sizeY: float
    + getX(): float
    + getY(): float
    + getSizeX(): float
    + getSizeY(): float
    + hasCollidedWith(Entity: other): boolean
  }

  Game *-- Score
  Game *-- Player
  Game *-- InvadersGroup
  Game *-- Bullet

  InvadersGroup *-- InvadersColumn
  InvadersColumn *-- Invader

  Player -- Bullet: shoots
  Invader -- Bullet: shoots

  Entity <|--- Player
  Entity <|--- Bullet
  Entity <|--- Invader

}
package "view_controller" {
  class Input {
    + isKeyDown(Key: key): boolean
    + isKeyUp(Key: key): boolean
    + isKeyPressed(Key: key): boolean
  }
  class Graphics {

  }
}
Game *---- Input
Graphics *-- Game
@enduml
```

Note: Sprites do not need to match the size of the player, bullet, or enemy. Instead, it's probably better to have the player sprite bigger than the player model (to stop unfair hitbox collisions).
