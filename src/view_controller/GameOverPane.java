package view_controller;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

public class GameOverPane extends BorderPane {
    VBox enterScorePane;
    VBox submittedPane;
    Button submitButton;
    TextField inputBox;

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

    public void showGameOver() {
        this.setCenter(enterScorePane);
    }

    public void showSubmitted(){
        this.setCenter(submittedPane);
    }

    public void setOnSubmitButtonAction(EventHandler<ActionEvent> event){
        submitButton.setOnAction(e -> {
            event.handle(new ActionEvent(submitButton, inputBox));
        });
        inputBox.setOnAction(e -> {
            event.handle(new ActionEvent(submitButton, inputBox));
        });
    }
}
