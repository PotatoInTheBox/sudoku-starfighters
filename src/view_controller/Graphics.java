package view_controller;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Graphics extends Application
{
	GamePane gamePane = new GamePane();
	MenuPane menuPane = new MenuPane();
	Scene scene;
	
	public static void main(String[] args)
	{
		launch(args);
	}	

	@Override
	public void start(Stage stage)
	{
		scene = new Scene(gamePane, 800, 800);
		
		registerInputs();
		
		stage.setScene(scene);
		stage.show();
		
		
	}
	
	public void registerInputs()
	{
		scene.setOnKeyPressed(event -> 
		{
			gamePane.makeMove(event);
			gamePane.update();
		});
	}
	
}
