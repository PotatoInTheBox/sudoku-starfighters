package view_controller;

import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

public class MenuPane extends GridPane {
    private Button continueButton;
    private Button newGameButton;
    private Button optionsButton;
    private Button exitButton;

    private List<EventHandler<ActionEvent>> continueGameHandlers = new ArrayList<>();
    private List<EventHandler<ActionEvent>> newGameHandlers = new ArrayList<>();
    private List<EventHandler<ActionEvent>> optionsHandlers = new ArrayList<>();
    private List<EventHandler<ActionEvent>> exitHandlers = new ArrayList<>();

    public MenuPane() {
        continueButton = new Button("Continue");
        newGameButton = new Button("New Game");
        optionsButton = new Button("Options");
        exitButton = new Button("Exit");

        setAlignment(Pos.CENTER);
        setHalignment(continueButton, HPos.CENTER);
        setHalignment(newGameButton, HPos.CENTER);
        setHalignment(optionsButton, HPos.CENTER);
        setHalignment(exitButton, HPos.CENTER);

        setVgap(20);

        addColumn(0, continueButton, newGameButton, optionsButton, exitButton);

        continueButton.setOnAction(e -> {
            for (EventHandler<ActionEvent> event : continueGameHandlers)
                event.handle(e);
        });
        newGameButton.setOnAction(e -> {
            for (EventHandler<ActionEvent> event : newGameHandlers)
                event.handle(e);
        });
        optionsButton.setOnAction(e -> {
            for (EventHandler<ActionEvent> event : optionsHandlers)
                event.handle(e);
        });
        exitButton.setOnAction(e -> {
            for (EventHandler<ActionEvent> event : exitHandlers)
                event.handle(e);
        });
    }

    /// Expose button handlers.
    public void onContinueGame(EventHandler<ActionEvent> eventHandler) {
        continueGameHandlers.add(eventHandler);
    }

    public void onNewGame(EventHandler<ActionEvent> eventHandler) {
        newGameHandlers.add(eventHandler);
    }

    public void onOptions(EventHandler<ActionEvent> eventHandler) {
        optionsHandlers.add(eventHandler);
    }

    public void onExit(EventHandler<ActionEvent> eventHandler) {
        exitHandlers.add(eventHandler);
    }
}
