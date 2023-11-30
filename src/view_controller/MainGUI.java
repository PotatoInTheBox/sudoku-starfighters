package view_controller;

import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyCombination;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import view_controller.panel.GamePane;
import view_controller.panel.KeyBindingsPane;
import view_controller.panel.LeaderboardPane;
import view_controller.panel.MenuPane;
import view_controller.panel.OptionsPane;
import view_controller.sound.SoundPlayer;
import view_controller.utils.Input;

import java.util.Stack;

/**
 * MainGUI is the start point of the java application which manages all the
 * subpanes and allows switching between panes using a stack.
 */
public class MainGUI extends Application {

	public static void main(String[] args) {
		launch(args);
	}

	private BorderPane rootBorderPane;
	private GamePane gamePane;
	private MenuPane menuPane;
	private OptionsPane optionsPane;
	private KeyBindingsPane keyBindingsPane;
	private LeaderboardPane leaderboardPane;
	private Scene scene;

	private Stack<Pane> guiStack;

	@Override
	public void start(Stage primaryStage) {
		primaryStage.setTitle("Space Invaders");
		rootBorderPane = new BorderPane();
		this.scene = new Scene(rootBorderPane, 600, 600);

		Input.setScene(scene);
		keyBindingsPane = new KeyBindingsPane();
		keyBindingsPane.addKeyBindFields(Input.getKeyBindings());
		keyBindingsPane.displayKeyBindFields();
		primaryStage.setFullScreenExitKeyCombination(KeyCombination.NO_MATCH);
		primaryStage.setScene(scene);
		loadCssStyleFile(scene);

		guiStack = new Stack<>();

		menuPane = new MenuPane();
		leaderboardPane = new LeaderboardPane();
		LeaderboardPane.loadLeaderboard("saved_scores");
		optionsPane = new OptionsPane();

		addButtonHandlers(scene);

		pushAndEnterPane(menuPane);

		// global keybind
		Input.onKeyDown(e -> {
			if (e.getCode().equals(KeyCode.ESCAPE)) {
				if (keyBindingsPane.isUsingEscapeKey() == false) {
					if (peekGuiStack() == gamePane) {
						exitGame();
					}
					if (peekGuiStack() == menuPane) {
						if (gamePane == null)
							instantiateGame();
						enterGame();
					} else {
						popAndExitPane();
					}
				}
			}
			if (e.getCode().equals(KeyCode.F11)) {
				primaryStage.setFullScreen(!primaryStage.isFullScreen());
			}
		});

		primaryStage.show();
	}

	/**
	 * Adds all the button handlers
	 * 
	 * @param scene The scene
	 */
	private void addButtonHandlers(Scene scene) {
		menuPane.onNewGame(e -> {
			instantiateGame();
			pushAndEnterPane(gamePane);
		});
		menuPane.onContinueGame(e -> {
			if (gamePane != null) {
				enterGame();
			} else {
				System.err.println("No game currently exists!");
			}
		});
		menuPane.onLeaderboard(e -> {
			leaderboardPane.updateScores();
			pushAndEnterPane(leaderboardPane);
		});
		menuPane.onOptions(e -> {
			pushAndEnterPane(optionsPane);
		});
		menuPane.onExit(e -> {
			Platform.exit();
			System.exit(0);
		});
		leaderboardPane.onBack(e -> {
			popAndExitPane();
		});
		optionsPane.onBack(e -> {
			popAndExitPane();
		});
		keyBindingsPane.onBack(e -> {
			popAndExitPane();
		});
		optionsPane.onKeyBindingsButton(e -> {
			pushAndEnterPane(keyBindingsPane);
		});
	}

	/**
	 * Instantiates the game
	 */
	private void instantiateGame() {
		if (gamePane != null) {
			gamePane.delete();
		}
		gamePane = new GamePane(scene, optionsPane, 600, 600);
	}

	/**
	 * Enters the player into the game
	 */
	private void enterGame() {
		gamePane.disabledInput(false);
		pushAndEnterPane(gamePane);
		gamePane.unpauseGame();
	}

	/**
	 * Exits the player out of the game
	 */
	private void exitGame() {
		gamePane.pauseGame();
		gamePane.disabledInput(true);
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

	/**
	 * Pushes the pane to the GUI stack
	 * 
	 * @param pane The pane to push
	 */
	private void pushAndEnterPane(Pane pane) {
		guiStack.push(pane);
		rootBorderPane.setCenter(pane);
		updateContinueButton(pane);
		chooseThemeMusic(pane);
	}

	/**
	 * Pops the pane from the GUI stack
	 */
	private void popAndExitPane() {
		if (guiStack.size() <= 1) {
			System.err.println("Cannot exit Gui any further, already at last pane possible.");
			return;
		}
		Pane poppedPane = guiStack.pop();
		Pane pane = guiStack.peek();
		rootBorderPane.setCenter(pane);
		updateContinueButton(pane);
		chooseThemeMusic(pane);
	}

	/**
	 * Changes the continue button if a current game is going
	 * 
	 * @param currentPane The current pane
	 */
	private void updateContinueButton(Pane currentPane) {
		if (currentPane == menuPane) {
			menuPane.setDisableContinueButton(!isGameExisting());
		}
	}

	/**
	 * If a game is currently going
	 * 
	 * @return True if there is a game
	 */
	private boolean isGameExisting() {
		if (gamePane != null && gamePane.game != null) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Selects the theme music to play
	 * 
	 * @param currentPane The current pane
	 */
	private void chooseThemeMusic(Pane currentPane) {
		if (currentPane == gamePane) {
			// SoundPlayer.stopThemeMusic();
		}
		if (currentPane != gamePane) {
			SoundPlayer.playMainThemeMusic();
		}
	}

	/**
	 * Peeks the GUI stack
	 * 
	 * @return The Pane that was peeked
	 */
	private Pane peekGuiStack() {
		return guiStack.peek();
	}
}
