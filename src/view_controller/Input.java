package view_controller;

import javafx.event.EventHandler;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

// Input class for delivering input to the Game.
// To simplify Game logic, considering having more specific methods such as
// `onJump(EventHandler e)`.
public class Input {
    private Scene scene;

    private HashMap<KeyCode, Boolean> heldKeys = new HashMap<>();

    private List<EventHandler<KeyEvent>> keyPressedHandlers = new ArrayList<>();
    private List<EventHandler<KeyEvent>> keyReleasedHandlers = new ArrayList<>();

    public Input(Scene scene) {
        this.scene = scene;
        assignButtonHandlers();
    }

    public void onKeyDown(EventHandler<KeyEvent> eventHandler) {
        keyPressedHandlers.add(eventHandler);
    }

    public void onKeyUp(EventHandler<KeyEvent> eventHandler) {
        keyReleasedHandlers.add(eventHandler);
    }

    public float getJoystickX() {
        float joystickXInput = 0f;

        if (isKeyDown(KeyCode.LEFT))
            joystickXInput -= 1f;
        if (isKeyDown(KeyCode.RIGHT))
            joystickXInput += 1f;

        joystickXInput = clamp(-1f, 1f, joystickXInput);
        return joystickXInput;
    }

    public float getJoystickY() {
        float joystickYInput = 0f;

        if (isKeyDown(KeyCode.UP))
            joystickYInput -= 1f;
        if (isKeyDown(KeyCode.DOWN))
            joystickYInput += 1f;

        joystickYInput = clamp(-1f, 1f, joystickYInput);
        return joystickYInput;
    }

    private float clamp(float min, float max, float val) {
        if (val < min)
            return min;
        if (val > max)
            return max;
        return val;
    }

    private boolean isKeyDown(KeyCode keyCode) {
        Boolean key = heldKeys.get(keyCode);
        if (key != null) {
            return key;
        } else
            return false;
    }

    private boolean isKeyUp(KeyCode keyCode) {
        Boolean key = heldKeys.get(keyCode);
        if (key != null) {
            return key;
        } else
            return true; // if it's not in the set it can't be down
    }

    private void assignButtonHandlers() {
        scene.setOnKeyPressed(e -> {
            boolean wasKeyAlreadyDown = isKeyDown(e.getCode());
            heldKeys.put(e.getCode(), true);
            // only fire the events for non-repeating press
            // (can't be held down while repeatedly giving keyDown)
            if (wasKeyAlreadyDown == false) {
                for (EventHandler<KeyEvent> handler : keyPressedHandlers) {
                    handler.handle(e);
                }
            }
        });
        scene.setOnKeyReleased(e -> {
            for (EventHandler<KeyEvent> handler : keyReleasedHandlers) {
                handler.handle(e);
            }
            heldKeys.put(e.getCode(), false);
        });
    }
}
