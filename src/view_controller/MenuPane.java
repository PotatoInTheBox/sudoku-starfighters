package view_controller;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;

public class MenuPane extends GridPane {
	private Button continueButton;
	private Button newGameButton;
	private Button optionsButton;
	private Button exitButton;
	private Button leaderboardButton;
	
    private Image gameLogo;

	private List<EventHandler<ActionEvent>> continueGameHandlers = new ArrayList<>();
	private List<EventHandler<ActionEvent>> newGameHandlers = new ArrayList<>();
	private List<EventHandler<ActionEvent>> leaderboardHandlers = new ArrayList<>();
	private List<EventHandler<ActionEvent>> optionsHandlers = new ArrayList<>();
	private List<EventHandler<ActionEvent>> exitHandlers = new ArrayList<>();

	public MenuPane() {
		this.getStyleClass().add("main-menu");
		continueButton = new Button("Continue");
		newGameButton = new Button("New Game");
		leaderboardButton = new Button("Leaderboard");
		optionsButton = new Button("Options");
		exitButton = new Button("Exit");
		
        gameLogo = getSpriteFromFile("./resources/images/game_logo.png");

		setAlignment(Pos.CENTER);
		setHalignment(continueButton, HPos.CENTER);
		setHalignment(newGameButton, HPos.CENTER);
		setHalignment(leaderboardButton, HPos.CENTER);
		setHalignment(optionsButton, HPos.CENTER);
		setHalignment(exitButton, HPos.CENTER);

		setVgap(20);

        ImageView logoView = new ImageView(gameLogo);
        logoView.setFitWidth(400);
        logoView.setFitHeight(200);
        setConstraints(logoView, 0, 0);
        getChildren().add(logoView);
        
		addColumn(0, continueButton, newGameButton, leaderboardButton, optionsButton, exitButton);
		
		continueButton.setOnAction(e -> {
			for (EventHandler<ActionEvent> event : continueGameHandlers)
				event.handle(e);
		});
		newGameButton.setOnAction(e -> {
			for (EventHandler<ActionEvent> event : newGameHandlers)
				event.handle(e);
		});
		leaderboardButton.setOnAction(e -> {
			for (EventHandler<ActionEvent> event : leaderboardHandlers)
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

	public void onLeaderboard(EventHandler<ActionEvent> eventHandler) {
		leaderboardHandlers.add(eventHandler);
	}

	public void onOptions(EventHandler<ActionEvent> eventHandler) {
		optionsHandlers.add(eventHandler);
	}

	public void onExit(EventHandler<ActionEvent> eventHandler) {
		exitHandlers.add(eventHandler);
	}
	
    private Image getSpriteFromFile(String path) {
        FileInputStream playerImageFile;
        try {
            playerImageFile = new FileInputStream(path);
            Image sprite = new Image(playerImageFile);
            return sprite;
        } catch (FileNotFoundException e) {
            System.out.println("Could not find" + path);
            return null;
        }
    }
}
