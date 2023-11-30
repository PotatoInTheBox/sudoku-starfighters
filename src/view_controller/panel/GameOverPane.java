package view_controller.panel;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * GameOverPane places itself ontop of the game to inform the player that the
 * game is over and telling the player they can submit their game for a score.
 * After which the GameOverPane will inform the player that the game needs to be
 * restarted to continue.
 */
public class GameOverPane extends BorderPane {
    VBox enterScorePane;
    VBox submittedPane;
    Button submitButton;
    TextField inputBox;

    /**
     * Sets up the game over pane
     */
    public GameOverPane() {
        Label gameOverLabel = new Label("GAME OVER");
        Label enterNameLabel = new Label("ENTER NAME FOR LEADERBOARD");
        inputBox = new TextField();
        inputBox.setPrefSize(100, 10);
        inputBox.setMaxSize(300, 20);
        submitButton = new Button("SUBMIT");

        enterScorePane = new VBox(gameOverLabel, enterNameLabel, inputBox, submitButton);
        enterScorePane.setAlignment(Pos.CENTER);
        enterScorePane.setPadding(new Insets(10, 10, 10, 10));
        enterScorePane.setSpacing(10);

        this.setCenter(enterScorePane);

        Label pressEscReturnMenu = new Label("PRESS ESC TO RETURN TO MENU");
        submittedPane = new VBox(pressEscReturnMenu);
        submittedPane.setAlignment(Pos.CENTER);
        submittedPane.setPadding(new Insets(10, 10, 10, 10));
        submittedPane.setSpacing(10);
    }

    /**
     * Shows the game over pane
     */
    public void showGameOver() {
        this.setCenter(enterScorePane);
    }

    /**
     * Shows that the score has been submitted to the leader board
     */
    public void showSubmitted(){
        this.setCenter(submittedPane);
    }

    /**
     * Sets the button to submit score
     * @param event Handles the score submit
     */
    public void setOnSubmitButtonAction(EventHandler<ActionEvent> event){
        submitButton.setOnAction(e -> {
            event.handle(new ActionEvent(submitButton, inputBox));
        });
        inputBox.setOnAction(e -> {
            event.handle(new ActionEvent(submitButton, inputBox));
        });
    }
}
