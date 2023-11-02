package view_controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainGUI extends Application {

	GamePane gamePane;
	MenuPane menuPane;
	LeaderboardPane leaderboardPane;
	Input input;

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Space Invaders");
		BorderPane root = new BorderPane();
		Scene scene = new Scene(root, 600, 600);
		this.input = new Input(scene);
		primaryStage.setScene(scene);
		loadCssStyleFile(scene);

		menuPane = new MenuPane();
		leaderboardPane = new LeaderboardPane();

		menuPane.onNewGame(e -> {
			gamePane = new GamePane(scene, input, 600, 600);
			root.setCenter(gamePane);
		});
		menuPane.onContinueGame(e -> {
			if (gamePane != null) {
				root.setCenter(gamePane);
				gamePane.unpauseGame();
			} else {
				System.err.println("No game currently exists!");
			}
		});
		menuPane.onLeaderboard(e -> {
			leaderboardPane.updateScores();
			root.setCenter(leaderboardPane);
		});
		menuPane.onOptions(e -> {
			System.out.println("Unimplemented");
		});
		menuPane.onExit(e -> {
			Platform.exit();
			System.exit(0);
		});
		leaderboardPane.onBack(e -> {
			root.setCenter(menuPane);
		});

		input.onKeyDown(e -> {
			if (e.getCode().equals(KeyCode.ESCAPE)) {
				gamePane.pauseGame();
				root.setCenter(menuPane);
			}
		});

		root.setCenter(menuPane);
		primaryStage.show();
	}

	/**
	 * Load CSS styles from a file if available and apply them to the scene.
	 * 
	 * @param scene The JavaFX scene to which CSS styles should be applied.
	 */
	private void loadCssStyleFile(final Scene scene) {
		try {
			final String css = "file:./resources/css/darkStyle.css";
			scene.getStylesheets().add(css);
		} catch (final Exception e) {
			System.out.println("Unable to load style.css");
		}

	}

	public static void main(String[] args) {
		launch(args);
	}
}
