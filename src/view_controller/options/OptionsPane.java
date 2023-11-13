package view_controller.options;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;

// NOTE: Pane won't resize the child panes, BorderPane will.
public class OptionsPane extends BorderPane {
    GridPane gridList;

    private List<EventHandler<ActionEvent>> keyBindingHandlers = new ArrayList<>();

    private CheckBox checkBox;
    private Button keyBindingsButton;

    public OptionsPane() {
        Slider slider1 = new Slider(0, 100, 50);
        Slider slider2 = new Slider(0, 100, 50);

        checkBox = new CheckBox();

        keyBindingsButton = new Button("Keybinds");

        gridList = new GridPane();
        gridList.setPadding(new Insets(10, 10, 10, 10));
        gridList.setVgap(5);
        gridList.setHgap(5);

        // Add components to the layout
        addSettingsItem(new Label("Slider 1:"), slider1);
        addSettingsItem(new Label("Slider 2:"), slider2);
        addSettingsItem(new Label("Wireframes:"), checkBox);
        gridList.addColumn(0, keyBindingsButton);

        // add scroll pane
        ScrollPane scrollPane = new ScrollPane(gridList);
        scrollPane.setFitToWidth(true);

        keyBindingsButton.setOnAction(e -> {
            for (EventHandler<ActionEvent> event : keyBindingHandlers)
                event.handle(e);
        });

        this.setCenter(scrollPane);
    }

    public boolean isWireframeEnabled() {
        return checkBox.isSelected();
    }

    public void setWireframeEnabled(boolean newValue) {
        checkBox.setSelected(newValue);
    }

    public void onKeyBindingsButton(EventHandler<ActionEvent> eventHandler) {
        keyBindingHandlers.add(eventHandler);
    }

    private void addSettingsItem(Label label, Control item) {
        gridList.addColumn(0, label);
        gridList.addColumn(1, item);
    }
}
