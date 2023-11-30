package model;
/**
 * EntityEvent is a container for event classes. It is similar to Runnable from
 * the standard java library.
 */
public interface EntityEvent {
    abstract void run(Entity entity);
}
