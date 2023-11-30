package view_controller.utils;

import javafx.scene.input.KeyCode;

/**
 * KeyBinding class is responsible for representing a single keybind. A keybind
 * can have a lot of information associated with the key presses such as the
 * key itself, the name of the key, and a description for what the keybind does.
 */
public class KeyBinding {

    private KeyCode key;
    private Type type;
    private String name;
    private String description;
    private static int globalInsertOrder = 0;
    private int insertOrder;

    /**
     * Construct the keybinding with a KeyCode and the Type of KeyCode it is.
     * 
     * @param defaultKey to assign
     * @param type       to assign
     */
    public KeyBinding(KeyCode defaultKey, Type type) {
        this(defaultKey, type, type.name);
    }

    /**
     * Construct the keybinding the a keycode, type, and description.
     * 
     * @param defaultKey  to assign
     * @param type        to assign
     * @param description to assign, explaining what the keybinding is for
     */
    public KeyBinding(KeyCode defaultKey, Type type, String description) {
        this.key = defaultKey;
        this.type = type;
        this.name = type.name;
        this.description = description;
        this.insertOrder = globalInsertOrder;
        globalInsertOrder += 1;
    }

    /**
     * Get the KeyCode associated with this key binding.
     *
     * @return the KeyCode assigned to this key binding
     */
    public KeyCode getKey() {
        return key;
    }

    /**
     * Set the KeyCode associated with this key binding.
     *
     * @param key the KeyCode to set for this key binding
     */
    public void setKey(KeyCode key) {
        this.key = key;
    }

    /**
     * Get the Type of KeyCode associated with this key binding.
     *
     * @return the Type of KeyCode assigned to this key binding
     */
    public Type getType() {
        return type;
    }

    /**
     * Get the name of this key binding.
     *
     * @return the name of the key binding
     */
    public String getName() {
        return name;
    }

    /**
     * Get the description of this key binding.
     *
     * @return the description explaining what the key binding is for
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the insert order of this key binding.
     *
     * @return the insert order of this key binding
     */
    public int getInsertOrder() {
        return insertOrder;
    }

    /**
     * Enum representing various types of key bindings along with their
     * descriptions.
     */
    public enum Type {
        MOVE_LEFT("Move left"),
        MOVE_RIGHT("Move right"),
        FIRE("Fire"),
        MOVE_UP("Move up"),
        MOVE_DOWN("Move down"),
        RAPID_FIRE("Debug fire"),
        WIREFRAME("Debug toggle wireframe"),
        FORCE_UNPAUSE("Debug force unpause"),
        GHOST("Can't be hit or hit others"),
        SHOOT_MANY("Shoot dozens of bullets"),
        SPAWN_TURRET("Spawns turret for 3 coins");

        public final String name;

        private Type(String name) {
            this.name = name;
        }
    }
}
