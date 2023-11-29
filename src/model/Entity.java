package model;

import java.util.ArrayList;
import java.util.List;

import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import view_controller.utils.Input;

import java.lang.RuntimeException;

/**
 * Entity is a universal game object that participates in an entity system.
 * It follows a hierarchical structure with parent and children nodes,
 * providing a flexible way to organize game objects.
 * 
 * This system allows for quickly and easily attaching code and elements into
 * the game. The system will also handle deleting components for further ease
 * of access.
 * 
 * Example usage:
 * {@code
 * 
 *	Invader invader = new Invader(game);
 *	Entity.instantiate(game, invader);
 *
 * }
 * Example continued:
 * {@code
 * 
 *	invader.addChild(new Sprite(game));
 * 
 * }
 */
public abstract class Entity {
	protected Game game = null;
	protected float x, y, scale;
	protected float dx = 0f;
	protected float dy = 0f;
	protected boolean isFrozen = false;
	protected boolean isAlive = true;

	protected Team team = Team.NEUTRAL;

	private Entity parent = null;
	private List<Entity> children = new ArrayList<>();
	private List<EventHandler<?>> keyDownEvents = new ArrayList<>();
	private List<EventHandler<?>> keyUpEvents = new ArrayList<>();
	private List<EventHandler<?>> frozenKeyDownEventHandlers = new ArrayList<>();
	private List<EventHandler<?>> frozenKeyUpEventHandlers = new ArrayList<>();

	/**
	 * Spawn entity at a given position.
	 * 
	 * @param game to instantiate children nodes to.
	 * @param x    absolute x position
	 * @param y    absolute x position
	 */
	public Entity(Game game, float x, float y) {
		this.game = game;
		this.x = x;
		this.y = y;
	}

	/**
	 * Set the position of the entity to a given x position. The x values will
	 * be treated as absolute so it will use the topmost parent/game
	 * coordinates.
	 * 
	 * @param x absolute x position
	 */
	public void setX(float x) {
		if (this.parent != null) {
			this.x = x - this.parent.getX();
		} else {
			this.x = x;
		}

	}

	/**
	 * Set the position of the entity to a given y position. The y values will
	 * be treated as absolute so it will use the topmost parent/game
	 * coordinates.
	 * 
	 * @param y absolute y position
	 */
	public void setY(float y) {
		if (this.parent != null) {
			this.y = y - this.parent.getY();
		} else {
			this.y = y;
		}
	}

	/**
	 * Get the position of the entity at a given x position. The x values will
	 * be treated as absolute so it will use the topmost parent/game
	 * coordinates.
	 * 
	 * @param x absolute x position
	 */
	public float getX() {
		// can also be done recursively using .getX()
		// I'm doing it iteratively in a loop
		float absoluteX = 0;
		Entity curEntity = this;
		while (curEntity != null) {
			absoluteX += curEntity.x;
			curEntity = curEntity.parent;
		}
		return absoluteX;
	}

	/**
	 * Get the position of the entity at a given y position. The y values will
	 * be treated as absolute so it will use the topmost parent/game
	 * coordinates.
	 * 
	 * @param y absolute y position
	 */
	public float getY() {
		// can also be done recursively using .getY()
		// I'm doing it iteratively in a loop
		float absoluteY = 0;
		Entity curEntity = this;
		while (curEntity != null) {
			absoluteY += curEntity.y;
			curEntity = curEntity.parent;
		}
		return absoluteY;
	}

	/**
	 * Set the dx value. This value can later be utilized by any entity to know
	 * what direction it should move.
	 * 
	 * @param newHorizontalSpeed 
	 */
	public void setDx(float newHorizontalSpeed) {
		this.dx = newHorizontalSpeed;
	}

	/**
	 * Set the dy value. This value can later be utilized by any entity to know
	 * what direction it should move.
	 * 
	 * @param newHorizontalSpeed
	 */
	public void setDy(float newVerticalSpeed) {
		this.dy = newVerticalSpeed;
	}

	public float getAbsoluteX() {
		float absoluteX = 0;
		Entity currEntity = this;
		while (this != null) {
			absoluteX += currEntity.getX();
			currEntity = this.parent;
		}
		return absoluteX;
	}

	// public float getAbsoluteY() {
	// float absoluteY = this.y;
	// Entity currEntity = this;
	// while (this != null) {
	// absoluteY += currEntity.getY();
	// currEntity = this.parent;
	// }
	// return absoluteY;
	// }

	// public void setAbsoluteX(float newX) {
	// Entity currEntity = this;
	// while (this != null) {
	// newX -= currEntity.getX();
	// currEntity = this.parent;
	// }
	// this.setX(newX);
	// }

	// public void setAbsoluteY(float newY) {
	// Entity currEntity = this;
	// while (this != null) {
	// newY -= currEntity.getY();
	// currEntity = this.parent;
	// }
	// this.setY(newY);
	// }

	public float getDx() {
		return dx;
	}

	public float getDy() {
		return dy;
	}

	public void setTeam(Team team) {
		this.team = team;
	}

	public Team getTeam() {
		return team;
	}

	public void move(float dx, float dy) {
		setX(getX() + dx);
		setY(getY() + dy);
	}

	public Entity getParent() {
		return parent;
	}

	public List<Entity> getChildren() {
		return children;
	}

	protected boolean addChild(Entity child) {
		if (child == null) {
			throw new RuntimeException("Passed child was null!");
		}
		if (child.parent != null) {
			throw new RuntimeException("Child belongs to another parent!");
		}
		// check if double child
		if (children.contains(child)) {
			System.err.println("Warning! Attempted child twice. Aborting.");
			return false;
		}
		boolean success = children.add(child);
		if (success) {
			if (game == null) {
				// new Exception("No game in Entity when adding child");
				System.err.println("No game in Entity when adding child");
			}
			child.game = game;
			float originalX = child.getX();
			float originalY = child.getY();
			child.parent = this;
			child.setX(originalX);
			child.setY(originalY);
			// check if double instantiation
			success = instantiate(game, child);
		} else {
			System.err.println("Could not add child to " + this);
		}
		return success;
	}

	protected void addChild(Entity... children) {
		for (int i = 0; i < children.length; i++) {
			addChild(children[i]);
		}
	}

	protected boolean removeChild(Entity child) {
		if (child == null) {
			throw new RuntimeException("Passed child was null!");
		}
		if (child.parent != this) {
			throw new RuntimeException("Child belongs to another parent!");
		}
		boolean success = this.children.remove(child);
		if (success) {
			float originalX = child.getX();
			float originalY = child.getY();
			child.parent = null;
			child.setX(originalX);
			child.setY(originalY);
		}
		return success;
	}

	protected void removeChild(Entity... children) {
		for (int i = 0; i < children.length; i++) {
			removeChild(children[i]);
		}
	}

	/**
	 * Should be overriden to add functionality for game tick updates
	 */
	public void update() {
	}

	public void onKeyDown(EventHandler<KeyEvent> event) {
		Input.onKeyDown(event);
		keyDownEvents.add(event);
	}

	public void onKeyUp(EventHandler<KeyEvent> event) {
		Input.onKeyUp(event);
		keyUpEvents.add(event);
	}

	private void unfreezeEventHandlers() {
		for (EventHandler<?> event : keyDownEvents) {
			Input.onKeyDown((EventHandler<KeyEvent>) event);
		}
		for (EventHandler<?> event : keyUpEvents) {
			Input.onKeyUp((EventHandler<KeyEvent>) event);
		}
	}

	private void freezeEventHandlers() {
		for (EventHandler<?> event : keyDownEvents) {
			Input.removeEventHandler((EventHandler<KeyEvent>) event);
		}
		for (EventHandler<?> event : keyUpEvents) {
			Input.removeEventHandler((EventHandler<KeyEvent>) event);
		}
	}

	public boolean isFrozen() {
		return isFrozen;
	}

	public void setFrozen(boolean isFrozen) {
		this.isFrozen = isFrozen;
		if (isFrozen) {
			freezeEventHandlers();
		} else {
			unfreezeEventHandlers();
		}
	}

	/**
	 * helper method, instead of asking Game directly to make a new entity,
	 * we can use .instantiate() within our code to do it for us.
	 */
	public boolean instantiate(Entity entity) {
		if (game == null) {
			System.err.println(
					"Cannot add object because no game is attached!");
			return false;
		}
		return instantiate(game, entity);
	}

	public static boolean instantiate(Game game, Entity entity) {
		if (game.getEntities().contains(entity)) {
			System.err.println(
					"Warning! Attempted to instantiate twice. Aborting.\n");
			return false;
		}
		game.addOnSpawnList(() -> {
			entity.game = game;
			game.addEntity(entity);
		});
		return true;
	}

	public void delete() {
		isAlive = false;
		game.addOnDeletedList(() -> {
			for (EventHandler<?> event : keyDownEvents) {
				Input.removeEventHandler(event);
			}
			for (EventHandler<?> event : keyUpEvents) {
				Input.removeEventHandler(event);
			}
			keyDownEvents.clear();
			keyUpEvents.clear();
			game.removeEntity(this);
			for (Entity child : children) {
				child.delete();
			}
			children.clear();
			if (parent != null) {
				parent.children.remove(this);
				parent = null;
			}
		});

	}
}
