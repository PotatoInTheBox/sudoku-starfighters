package view_controller;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import model.Score;

public class LeaderboardPane extends GridPane {

	private ArrayList<Score> topScores = new ArrayList<Score>();
	private VBox pane;

	private Button backButton;
	private List<EventHandler<ActionEvent>> backHandlers = new ArrayList<>();

	public LeaderboardPane() {
		setAlignment(Pos.CENTER);

		backButton = new Button("Back");

		pane = new VBox();
		pane.setPadding(new Insets(10, 10, 10, 10));
		pane.setSpacing(10);

		backButton.setOnAction(e -> {
			for (EventHandler<ActionEvent> event : backHandlers)
				event.handle(e);
		});

		updateScores();
	}

	public void onBack(EventHandler<ActionEvent> eventHandler) {
		backHandlers.add(eventHandler);
	}

	/**
	 * Updates the score for the Leaderboard
	 */
	public void updateScores() {
		getChildren().clear();
		pane = new VBox();
		pane.setPadding(new Insets(10, 10, 10, 10));
		pane.setSpacing(10);
		Collections.sort(topScores, new Comparator<Score>() {
			@Override
			public int compare(Score score1, Score score2) {
				return Integer.compare(score2.getScore(), score1.getScore());
			}
		});
		if(topScores.isEmpty()) {
			Label cur = new Label("No Scores on Leaderboard");
			pane.getChildren().add(cur);
		}
		int i = 0;
		for (Score score : topScores) {
			if (i < 10) {
				Label cur = new Label(score.getUsername() + " : " + Integer.toString(score.getScore()));
				pane.getChildren().add(cur);
			}
			i++;
		}
		addColumn(0, pane, backButton);
	}
}
