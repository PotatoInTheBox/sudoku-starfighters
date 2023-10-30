package view_controller;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

public class MainGUI extends Application {

    @Override
    public void start(Stage primaryStage) {
        primaryStage.setTitle("JavaFX Window Template");
        BorderPane root = new BorderPane();
        Scene scene = new Scene(root, 600, 600);
        primaryStage.setScene(scene);
        root.setCenter(new GamePane(scene, 600, 600));
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
