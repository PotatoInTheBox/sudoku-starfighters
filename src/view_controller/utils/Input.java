package view_controller.utils;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import view_controller.panel.KeyBindingsPane;

import java.time.LocalTime;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Collection;
import java.util.Queue;
import java.util.Iterator;
import java.util.concurrent.ConcurrentLinkedQueue;

// Input class for delivering input to the Game.
// To simplify Game logic, considering having more specific methods such as
// `onJump(EventHandler e)`.
public class Input {
	private static Scene scene;

	private static HashMap<KeyCode, Boolean> heldKeys = new HashMap<>();
	private static HashMap<KeyBinding.Type, KeyBinding> keyBindings = new HashMap<>();

	private static Queue<EventHandler<KeyEvent>> keyPressedHandlers = new ConcurrentLinkedQueue<>();
	private static Queue<EventHandler<KeyEvent>> keyReleasedHandlers = new ConcurrentLinkedQueue<>();

	private static List<EventHandler<KeyEvent>> markedKeyPressedHandlers = new ArrayList<>();
	private static List<EventHandler<KeyEvent>> markedKeyReleasedHandlers = new ArrayList<>();

	// private HashMap<KeyBinding.Type, EventHandler<KeyEvent>>
	// keyBindPressedHandlers = new HashMap<>();
	// private HashMap<KeyBinding.Type, EventHandler<KeyEvent>>
	// keyBindReleasedHandlers = new HashMap<>();

	public static void setScene(Scene scene) {
		Input.scene = scene;
		assignButtonHandlers();
		putKeyBind(KeyCode.LEFT, KeyBinding.Type.MOVE_LEFT);
		putKeyBind(KeyCode.RIGHT, KeyBinding.Type.MOVE_RIGHT);
		putKeyBind(KeyCode.Z, KeyBinding.Type.FIRE);
		putKeyBind(KeyCode.UP, KeyBinding.Type.MOVE_UP);
		putKeyBind(KeyCode.DOWN, KeyBinding.Type.MOVE_DOWN);
		//putKeyBind(KeyCode.X, KeyBinding.Type.RAPID_FIRE);
		putKeyBind(KeyCode.V, KeyBinding.Type.WIREFRAME);
		//putKeyBind(KeyCode.SPACE, KeyBinding.Type.FORCE_UNPAUSE);
		//putKeyBind(KeyCode.G, KeyBinding.Type.GHOST);
		putKeyBind(KeyCode.H, KeyBinding.Type.SHOOT_MANY);
		putKeyBind(KeyCode.X, KeyBinding.Type.SPAWN_TURRET);
	}

	public static KeyCode getKeyFromType(KeyBinding.Type type) {
		KeyBinding keyBinding = keyBindings.get(type);
		if (keyBinding == null) {
			System.err.println("Warning, unbound type in Input: \"" + type + "\" Description \"" + type.name + "\"!");
			return null;
		}
		return keyBinding.getKey();
	}

	/**
	 * Takes input when the key is down
	 * 
	 * @param eventHandler
	 */
	public static void onKeyDown(EventHandler<KeyEvent> eventHandler) {
		markedKeyPressedHandlers.remove(eventHandler);
		if (keyPressedHandlers.contains(eventHandler)) {
			return;
		}
		keyPressedHandlers.add(eventHandler);
	}

	public static void removeOnKeyDown(EventHandler<KeyEvent> eventHandler) {
		if (keyPressedHandlers.contains(eventHandler)) {
			markedKeyPressedHandlers.add(eventHandler);
		}
	}

	/**
	 * Stops the input when the key isn't pressed
	 * 
	 * @param eventHandler
	 */
	public static void onKeyUp(EventHandler<KeyEvent> eventHandler) {
		markedKeyReleasedHandlers.remove(eventHandler);
		if (keyReleasedHandlers.contains(eventHandler)) {
			return;
		}
		keyReleasedHandlers.add(eventHandler);
	}

	public static void removeOnKeyUp(EventHandler<KeyEvent> eventHandler) {
		if (keyReleasedHandlers.contains(eventHandler)) {
			markedKeyReleasedHandlers.add(eventHandler);
		}

	}

	// public void onKeyBindDown(KeyBinding.Type type, EventHandler<KeyEvent>
	// eventHandler) {
	// keyBindPressedHandlers.put(type, eventHandler);
	// }

	// public void onKeyBindUp(KeyBinding.Type type, EventHandler<KeyEvent>
	// eventHandler) {
	// keyBindReleasedHandlers.put(type, eventHandler);
	// }

	/**
	 * Retrieves the X input converted based on key pressed
	 * 
	 * @return A float of the X input
	 */
	public static float getJoystickX() {
		float joystickXInput = 0f;

		if (isKeyDown(keyBindings.get(KeyBinding.Type.MOVE_LEFT).getKey()))
			joystickXInput -= 1f;
		if (isKeyDown(keyBindings.get(KeyBinding.Type.MOVE_RIGHT).getKey()))
			joystickXInput += 1f;

		joystickXInput = clamp(-1f, 1f, joystickXInput);
		return joystickXInput;
	}

	/**
	 * Retrieves the Y input converted based on key pressed
	 * 
	 * @return a float of the Y input
	 */
	public static float getJoystickY() {
		float joystickYInput = 0f;

		if (isKeyDown(keyBindings.get(KeyBinding.Type.MOVE_UP).getKey()))
			joystickYInput -= 1f;
		if (isKeyDown(keyBindings.get(KeyBinding.Type.MOVE_DOWN).getKey()))
			joystickYInput += 1f;

		joystickYInput = clamp(-1f, 1f, joystickYInput);
		return joystickYInput;
	}

	/**
	 * Ensures that the current value is between min and max
	 * 
	 * @param min The min value to clamp
	 * @param max The max value to clamp
	 * @param val The current value
	 * @return The resulting float after clamping
	 */
	private static float clamp(float min, float max, float val) {
		if (val < min)
			return min;
		if (val > max)
			return max;
		return val;
	}

	/**
	 * Return true if a key is currently held down
	 * 
	 * @param keyCode The key that is being pressed
	 * @return True if a key is held, false if not
	 */
	public static boolean isKeyDown(KeyCode keyCode) {
		Boolean key = heldKeys.get(keyCode);
		if (key != null) {
			return key;
		} else
			return false;
	}

	/**
	 * Return true if no key is held down
	 * 
	 * @param keyCode The key being pressed
	 * @return True if no key is held, false if one is
	 */
	public static boolean isKeyUp(KeyCode keyCode) {
		Boolean key = heldKeys.get(keyCode);
		if (key != null) {
			return key;
		} else
			return true; // if it's not in the set it can't be down
	}

	/**
	 * Assigns button handlers for key pressed and key released
	 */
	private static void assignButtonHandlers() {
		scene.setOnKeyPressed(e -> {
			boolean wasKeyAlreadyDown = isKeyDown(e.getCode());
			heldKeys.put(e.getCode(), true);

			// remove marked handlers
			keyPressedHandlers.removeAll(markedKeyPressedHandlers);
			markedKeyPressedHandlers.clear();

			if (wasKeyAlreadyDown == false) {
				for (EventHandler<KeyEvent> handler : keyPressedHandlers) {
					handler.handle(e);
				}
			}
		});
		scene.setOnKeyReleased(e -> {
			heldKeys.put(e.getCode(), false);

			// remove marked handlers
			keyReleasedHandlers.removeAll(markedKeyReleasedHandlers);
			markedKeyReleasedHandlers.clear();

			for (EventHandler<KeyEvent> handler : keyReleasedHandlers) {
				handler.handle(e);
			}
		});
	}

	private static void putKeyBind(KeyCode key, KeyBinding.Type type) {
		keyBindings.put(type, new KeyBinding(key, type));
	}

	public static Collection<KeyBinding> getKeyBindings() {
		return keyBindings.values();
	}

	@SuppressWarnings("unchecked")
	public static void removeEventHandler(EventHandler<?> event) {
		removeOnKeyDown((EventHandler<KeyEvent>) event);
		removeOnKeyUp((EventHandler<KeyEvent>) event);
	}
}
