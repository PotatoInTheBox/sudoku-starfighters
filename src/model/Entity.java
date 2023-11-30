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
	/**
	 * Instantiate the given entity into the game loop. This means that the
	 * added entity will automatically be called by game when needed. Currently,
	 * this means that the .update() method is called every game loop and all
	 * keystroke events are handled. In the future this could be expanded to
	 * also handle .collision() events.
	 * 
	 * Instantiated entities will automatically be read by other classes and
	 * methods when data is needed (colliders for collision, sprites for
	 * drawing, and other data such as entity type).
	 * 
	 * @param game   game to instantiate to
	 * @param entity to instantiate
	 * 
	 * @return whether or not the instantiation was successful.
	 */
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
	/**
	 * Instantiate the given entities into the game loop. This means that the
	 * added entity will automatically be called by game when needed. Currently,
	 * this means that the .update() method is called every game loop and all
	 * keystroke events are handled. In the future this could be expanded to
	 * also handle .collision() events.
	 * 
	 * Instantiated entities will automatically be read by other classes and
	 * methods when data is needed (colliders for collision, sprites for
	 * drawing, and other data such as entity type).
	 * 
	 * @param game        game to instantiate to
	 * @param entities... to instantiate
	 */
	public static void instantiate(Game game, Entity... entities) {
		for (Entity entity : entities) {
			if (game.getEntities().contains(entity)) {
				System.err.println(
						"Warning! Attempted to instantiate twice. Aborting.\n");
				continue;
			}
			game.addOnSpawnList(() -> {
				entity.game = game;
				game.addEntity(entity);
			});
		}
	}
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
	// @SuppressWarnings("unused") // may be needed later, not currently used
	// private List<EventHandler<?>> frozenKeyDownEventHandlers = new ArrayList<>();
	// @SuppressWarnings("unused") // may be needed later, not currently used
	// private List<EventHandler<?>> frozenKeyUpEventHandlers = new ArrayList<>();

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

	/**
	 * Get the Team this entity is assigned to.
	 * 
	 * @return the Team enum.
	 */
	public Team getTeam() {
		return team;
	}

	/**
	 * Move the topmost parent entity by the given delta x and delta y offsets.
	 * 
	 * @param dx amount to move by
	 * @param dy amount to move by
	 */
	public void move(float dx, float dy) {
		setX(getX() + dx);
		setY(getY() + dy);
	}

	/**
	 * Get the parent of this entity (but not the topmost parent).
	 *
	 * @return this entitie's parent
	 */
	public Entity getParent() {
		return parent;
	}

	/**
	 * Get the entitie's children list. This is the actual list the entity has.
	 * 
	 * @return a reference to the entitie's list of children
	 */
	public List<Entity> getChildren() {
		return children;
	}

	/**
	 * Should be overriden to add functionality for game tick updates. If
	 * instantiated, the game will be able to call this method on every game
	 * update. Will not be called as soon as it is deleted with .delete().
	 */
	public void update() {
	}

	/**
	 * Helper method to add keyevent to entities. The event handlers will be
	 * automatically added/frozen/deleted when needed. The only thing the
	 * programmer needs to do is specifiy the Event code that should be run.
	 * Take great care when modifying any loop inside of an event handler. The
	 * code could be running inside a for-loop and changing the size of an
	 * associated for-loop could cause a ConcurrentModification exception.
	 * 
	 * @param event to be called when a key is pressed down.
	 */
	public void onKeyDown(EventHandler<KeyEvent> event) {
		Input.onKeyDown(event);
		keyDownEvents.add(event);
	}

	/**
	 * Helper method to add keyevent to entities.
	 * See
	 * {@link #onKeyDown(EventHandler event)
	 * onKeyDown(EventHandler<KeyEvent> event)}
	 * for more information.
	 * 
	 * @param event to be called when a key is released.
	 */
	public void onKeyUp(EventHandler<KeyEvent> event) {
		Input.onKeyUp(event);
		keyUpEvents.add(event);
	}

	/**
	 * Check if the current entity is frozen. Frozen entities temporarily stop
	 * participating in the events and update() calls.
	 * 
	 * @return whether it is currently frozen
	 */
	public boolean isFrozen() {
		return isFrozen;
	}

	/**
	 * Set the frozen status of this entity. Frozen entities temporarily stop
	 * participating in the events and update() calls.
	 * 
	 * @param isFrozen new frozen value to set the entity to.
	 */
	public void setFrozen(boolean isFrozen) {
		this.isFrozen = isFrozen;
		if (isFrozen) {
			freezeEventHandlers();
		} else {
			unfreezeEventHandlers();
		}
	}

	/**
	 * Instantiate the given entity into the game loop. This means that the
	 * added entity will automatically be called by game when needed. Currently,
	 * this means that the .update() method is called every game loop and all
	 * keystroke events are handled. In the future this could be expanded to
	 * also handle .collision() events.
	 * 
	 * Instantiated entities will automatically be read by other classes and
	 * methods when data is needed (colliders for collision, sprites for
	 * drawing, and other data such as entity type).
	 * 
	 * @return whether or not the instantiation was successful.
	 */
	public boolean instantiate() {
		if (game == null) {
			System.err.println(
					"Cannot add object because no game is attached!");
			return false;
		}
		return instantiate(game, this);
	}

	/**
	 * Delete this entity from the game loop. Deleting an entity from the game
	 * loop simply means detaching it from the game objects. The children will
	 * also subsequently be removed from the game. All parent/child references
	 * will be removed. This is because .delete() usually means you no longer
	 * want to use the entity ever again (this is java so all references to
	 * this entity or children are valid pointers).
	 * 
	 * If the entity has to remain inside of the game consider using
	 * .setFrozen(). If only the parent has to be deleted then use
	 * {@link #removeChild(Entity child) removeChild(Entity child)} or
	 * {@link #removeChild(Entity... child) removeChild(Entity... child)}.
	 * 
	 */
	public void delete() {
		if (game == null) {
			return;
		}
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

	/**
	 * Add a given entity as a child to this current entity. The child will only
	 * be added if it doesn't have a parent. The entity will NOT be instantiated
	 * when added as a child (should be manually instantiated if needed).
	 * 
	 * @param child to add to this entity
	 * @return true if the child was added, false if the child was not added.
	 */
	protected boolean addChild(Entity child) {
		if (child == null) {
			throw new RuntimeException("Passed child was null!");
		}
		// check if double child
		if (children.contains(child)) {
			System.err.println("Warning! Attempted child twice. Aborting.");
			return false;
		}
		if (child.parent != null) {
			throw new RuntimeException("Child belongs to another parent!");
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
			// success = instantiate(game, child);
		} else {
			System.err.println("Could not add child to " + this);
		}
		return success;
	}

	/**
	 * Add multiple children to entity at a time. Does not return success or
	 * fail. See {@link #addChild(Entity child) addChild(Entity child)} for more
	 * information.
	 * 
	 * @param children to add to this entity.
	 */
	protected void addChild(Entity... children) {
		for (int i = 0; i < children.length; i++) {
			addChild(children[i]);
		}
	}

	/**
	 * helper method, instead of asking Game directly to make a new entity,
	 * we can use .instantiate() within our code to do it for us.
	 */

	/**
	 * Remove child entity from this entity. The remove child is unassigned from
	 * parent but is still instantiated.
	 * 
	 * @param child
	 * @return
	 */
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

	/**
	 * Remove multiple children from entity at a time. Does not return success
	 * or fail. See {@link #removeChild(Entity child) removeChild(Entity child)}
	 * for more information.
	 * 
	 * @param children to remove from this entity.
	 */
	protected void removeChild(Entity... children) {
		for (int i = 0; i < children.length; i++) {
			removeChild(children[i]);
		}
	}

	/**
	 * Unfreeze all event handlers.
	 */
	@SuppressWarnings("unchecked")
	private void unfreezeEventHandlers() {
		for (EventHandler<?> event : keyDownEvents) {
			Input.onKeyDown((EventHandler<KeyEvent>) event);
		}
		for (EventHandler<?> event : keyUpEvents) {
			Input.onKeyUp((EventHandler<KeyEvent>) event);
		}
	}

	/**
	 * Freeze all event handlers.
	 */
	@SuppressWarnings("unchecked")
	private void freezeEventHandlers() {
		for (EventHandler<?> event : keyDownEvents) {
			Input.removeEventHandler((EventHandler<KeyEvent>) event);
		}
		for (EventHandler<?> event : keyUpEvents) {
			Input.removeEventHandler((EventHandler<KeyEvent>) event);
		}
	}
}
