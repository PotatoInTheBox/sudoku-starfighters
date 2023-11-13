package view_controller.options;

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
import view_controller.Input;

public class KeyBindingsPane extends BorderPane {
    private GridPane gridList;
    ScrollPane scrollPane;
    private Input input;
    private boolean isUsingEscapeKey = false;
    private List<KeyBindingEntry> keyBindingEntries = new ArrayList<>();

    public KeyBindingsPane(Input input) {
        this.input = input;

        gridList = new GridPane();
        gridList.setPadding(new Insets(10, 10, 10, 10));
        gridList.setVgap(5);
        gridList.setHgap(5);

        scrollPane = new ScrollPane(gridList);
        scrollPane.setFitToWidth(true);

        this.setCenter(scrollPane);
    }

    public boolean isUsingEscapeKey() {
        return isUsingEscapeKey;
    }

    public void displayKeyBindFields() {
        Collections.sort(keyBindingEntries, KeyBindingEntry.comparator);
        gridList.getChildren().clear();
        for (KeyBindingEntry keyBindingEntry : keyBindingEntries) {
            gridList.addColumn(0, keyBindingEntry.label);
            gridList.addColumn(1, keyBindingEntry.button);
        }
    }

    public void addKeyBindFields(Collection<KeyBinding> keyBindings) {
        for (KeyBinding keyBindingSetItem : keyBindings)
            addKeyBindField(keyBindingSetItem);
    }

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

    private void assigningButton(Button button, KeyBinding keyBinding) {
        isUsingEscapeKey = true;
        scrollPane.setDisable(true);
        button.setText("<waiting for input>");
        input.onKeyDown(new NewKeyHandler(button, keyBinding));
    }

    private class NewKeyHandler implements EventHandler<KeyEvent> {
        Button button;
        KeyBinding keyBinding;

        public NewKeyHandler(Button button, KeyBinding keyBinding) {
            isUsingEscapeKey = true;
            this.button = button;
            this.keyBinding = keyBinding;
        }

        public void handle(KeyEvent e) {
            if (e.getCode() != KeyCode.ESCAPE) {
                keyBinding.setKey(e.getCode());
            }
            button.setText(keyBinding.getKey().toString());
            scrollPane.setDisable(false);
            input.removeOnKeyDown(this);
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
}
