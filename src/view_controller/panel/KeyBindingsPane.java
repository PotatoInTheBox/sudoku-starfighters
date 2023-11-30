package view_controller.panel;

import java.util.ArrayList;
import java.util.List;
import java.util.Collection;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import javafx.application.Application;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Control;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.control.Slider;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import view_controller.utils.Input;
import view_controller.utils.KeyBinding;

/**
 * KeybindingsPane contains all the keybindings given by other classes. Another
 * class can access this instance and provide new keybinds which can show up on
 * the list and be modified by the user to be a different key.
 */
public class KeyBindingsPane extends BorderPane {
    private GridPane gridList;
    ScrollPane scrollPane;
    private boolean isUsingEscapeKey = false;
    private List<KeyBindingEntry> keyBindingEntries = new ArrayList<>();

    private Button backButton;
    private List<EventHandler<ActionEvent>> backHandlers = new ArrayList<>();

    /**
     * Constructor which makes the keybindings pane to hold all the keybindings.
     */
    public KeyBindingsPane() {

        Label paneTitleLabel = new Label("Keybindings");
        paneTitleLabel.getStyleClass().add("dark-mode-header");
        paneTitleLabel.setPadding(new Insets(25));
        this.setTop(paneTitleLabel);
        setAlignment(paneTitleLabel, Pos.CENTER);

        gridList = new GridPane();
        gridList.setAlignment(Pos.CENTER);
        gridList.setPadding(new Insets(10, 10, 10, 10));
        gridList.setVgap(5);
        gridList.setHgap(5);

        scrollPane = new ScrollPane(gridList);
        scrollPane.setFitToWidth(true);

        backButton = new Button("Back");
        backButton.setOnAction(e -> {
            for (EventHandler<ActionEvent> event : backHandlers)
                event.handle(e);
        });
        setAlignment(backButton, Pos.CENTER);

        this.setTop(paneTitleLabel);
        this.setCenter(scrollPane);
        this.setBottom(backButton);
    }

    /**
     * Finds if user is using the escape key
     * @return Boolean of user state
     */
    public boolean isUsingEscapeKey() {
        return isUsingEscapeKey;
    }

    /**
     * Displays the key bindings
     */
    public void displayKeyBindFields() {
        Collections.sort(keyBindingEntries, KeyBindingEntry.comparator);
        gridList.getChildren().clear();
        for (KeyBindingEntry keyBindingEntry : keyBindingEntries) {
            gridList.addColumn(0, keyBindingEntry.label);
            gridList.addColumn(1, keyBindingEntry.button);
        }
    }

    /**
     * Add key binding fields
     * @param keyBindings The Collection of KeyBindings
     */
    public void addKeyBindFields(Collection<KeyBinding> keyBindings) {
        for (KeyBinding keyBindingSetItem : keyBindings)
            addKeyBindField(keyBindingSetItem);
    }

    /**
     * Add key binding fields
     * @param keyBinding One specific key binding
     */
    public void addKeyBindField(KeyBinding keyBinding) {
        for (KeyBindingEntry entry : keyBindingEntries)
            if (entry.keyBinding == keyBinding)
                return;
        Button button = new Button(keyBinding.getKey().toString());
        button.setFocusTraversable(true);
        button.setOnAction(e -> {
            if (isUsingEscapeKey == false) {
                assigningButton(button, keyBinding);
            }
        });
        Label label = new Label(keyBinding.getName());
        keyBindingEntries.add(new KeyBindingEntry(keyBinding, button, label));
    }

    /**
     * Assigns a certain button
     * @param button A specific button
     * @param keyBinding A specific key bind
     */
    private void assigningButton(Button button, KeyBinding keyBinding) {
        isUsingEscapeKey = true;
        scrollPane.setDisable(true);
        button.setText("<waiting for input>");
        Input.onKeyDown(new NewKeyHandler(button, keyBinding));
    }

    private class NewKeyHandler implements EventHandler<KeyEvent> {
        Button button;
        KeyBinding keyBinding;

        public NewKeyHandler(Button button, KeyBinding keyBinding) {
            isUsingEscapeKey = true;
            this.button = button;
            this.keyBinding = keyBinding;
        }

        /**
         * Handles a key press
         */
        public void handle(KeyEvent e) {
            if (e.getCode() != KeyCode.ESCAPE) {
                keyBinding.setKey(e.getCode());
            }
            button.setText(keyBinding.getKey().toString());
            scrollPane.setDisable(false);
            Input.removeOnKeyDown(this);
            isUsingEscapeKey = false;
        }
    }

    private class KeyBindingEntry {
        KeyBinding keyBinding;
        Button button;
        Label label;
        public static Comparator<KeyBindingEntry> comparator = new Comparator<KeyBindingEntry>() {
            @Override
            public int compare(KeyBindingEntry o1, KeyBindingEntry o2) {
                return o1.keyBinding.getInsertOrder() - o2.keyBinding.getInsertOrder();
            }
        };

        public KeyBindingEntry(KeyBinding keyBinding, Button button, Label label) {
            this.keyBinding = keyBinding;
            this.button = button;
            this.label = label;
        }

    }

    public void onBack(EventHandler<ActionEvent> eventHandler) {
        backHandlers.add(eventHandler);
    }
}
