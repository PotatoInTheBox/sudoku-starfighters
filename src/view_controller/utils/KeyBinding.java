package view_controller.utils;

import javafx.scene.input.KeyCode;

public class KeyBinding {

    private KeyCode key;
    private Type type;
    private String name;
    private String description;
    private static int globalInsertOrder = 0;
    private int insertOrder;

    public KeyBinding(KeyCode defaultKey, Type type) {
        this(defaultKey, type, type.name);
    }

    public KeyBinding(KeyCode defaultKey, Type type, String description) {
        this.key = defaultKey;
        this.type = type;
        this.name = type.name;
        this.description = description;
        this.insertOrder = globalInsertOrder;
        globalInsertOrder += 1;
    }

    public KeyCode getKey() {
        return key;
    }

    public void setKey(KeyCode key) {
        this.key = key;
    }

    public Type getType(){
        return type;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public int getInsertOrder(){
        return insertOrder;
    }

    public enum Type {
		MOVE_LEFT("Move left"),
		MOVE_RIGHT("Move right"),
		FIRE("Fire"),
		MOVE_UP("Debug move up"),
		MOVE_DOWN("Debug move down"),
		RAPID_FIRE("Debug fire"),
		WIREFRAME("Debug toggle wireframe"),
        FORCE_UNPAUSE("Debug force unpause"),
        GHOST("Can't be hit or hit others"),
        WIN_GAME("Can't be hit or hit others"),
        SHOOT_MANY("Shoot dozens of bullets");

		public final String name;

		private Type(String name){
			this.name = name;
		}		
	}
}
