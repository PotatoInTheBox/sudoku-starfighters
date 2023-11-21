package view_controller.options;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
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
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import view_controller.SoundPlayer;

// NOTE: Pane won't resize the child panes, BorderPane will.
public class OptionsPane extends BorderPane {
    private GridPane gridList;

    private List<EventHandler<ActionEvent>> keyBindingHandlers = new ArrayList<>();

    private CheckBox wireframeCheckBox;
    private CheckBox capFpsCheckBox;
    private Button keyBindingsButton;
    private Slider volumeSlider;
    private Slider sfxSlider;
    private Slider musicSlider;

    private Button backButton;
    private List<EventHandler<ActionEvent>> backHandlers = new ArrayList<>();

    public OptionsPane() {
        Label paneTitleLabel = new Label("Options");
        paneTitleLabel.getStyleClass().add("dark-mode-header");
        paneTitleLabel.setPadding(new Insets(25));
        setAlignment(paneTitleLabel, Pos.CENTER);

        initializeVolumeSliders();

        wireframeCheckBox = new CheckBox();
        capFpsCheckBox = new CheckBox();
        capFpsCheckBox.setSelected(true);
        keyBindingsButton = new Button("Keybinds");

        gridList = new GridPane();
        gridList.setAlignment(Pos.CENTER);
        gridList.setPadding(new Insets(10, 10, 10, 10));
        gridList.setVgap(5);
        gridList.setHgap(5);

        // Add components to the layout
        addSettingsItem(new Label("Volume:"), volumeSlider);
        addSettingsItem(new Label("Sfx:"), sfxSlider);
        addSettingsItem(new Label("Music:"), musicSlider);
        addSettingsItem(new Label("Wireframes:"), wireframeCheckBox);
        addSettingsItem(new Label("Limit fps to game update:"), capFpsCheckBox);
        gridList.addColumn(0, keyBindingsButton);

        // add scroll pane
        ScrollPane scrollPane = new ScrollPane(gridList);
        scrollPane.setFitToWidth(true);

        keyBindingsButton.setOnAction(e -> {
            for (EventHandler<ActionEvent> event : keyBindingHandlers)
                event.handle(e);
        });

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

    private void initializeVolumeSliders() {
        final double MAX_VALUE = 1d;
        volumeSlider = new Slider(0, MAX_VALUE, 0.6d / MAX_VALUE);
        SoundPlayer.setVolume(volumeSlider.valueProperty().doubleValue() / MAX_VALUE);
        volumeSlider.valueProperty().addListener(e -> {
            SoundPlayer.setVolume(volumeSlider.valueProperty().doubleValue() / MAX_VALUE);
        });
        sfxSlider = new Slider(0, MAX_VALUE, 1.0d / MAX_VALUE);
        SoundPlayer.setSfxVolume(sfxSlider.valueProperty().doubleValue() / MAX_VALUE);
        sfxSlider.valueProperty().addListener(e -> {
            SoundPlayer.setSfxVolume(sfxSlider.valueProperty().doubleValue() / MAX_VALUE);
        });
        musicSlider = new Slider(0, MAX_VALUE, 0.6d / MAX_VALUE);
        SoundPlayer.setMusicVolume(musicSlider.valueProperty().doubleValue() / MAX_VALUE);
        musicSlider.valueProperty().addListener(e -> {
            SoundPlayer.setMusicVolume(musicSlider.valueProperty().doubleValue() / MAX_VALUE);
        });
    }

    public boolean isWireframeEnabled() {
        return wireframeCheckBox.isSelected();
    }

    public boolean isCapFpsEnabled() {
        return capFpsCheckBox.isSelected();
    }

    public void setWireframeEnabled(boolean newValue) {
        wireframeCheckBox.setSelected(newValue);
    }

    public void onKeyBindingsButton(EventHandler<ActionEvent> eventHandler) {
        keyBindingHandlers.add(eventHandler);
    }

    private void addSettingsItem(Label label, Control item) {
        gridList.addColumn(0, label);
        gridList.addColumn(1, item);
    }

    public void onBack(EventHandler<ActionEvent> eventHandler) {
        backHandlers.add(eventHandler);
    }
}
