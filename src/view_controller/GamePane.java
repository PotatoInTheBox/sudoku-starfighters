package view_controller;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.paint.Color;
import model.Game;
import model.Player;

public class GamePane extends BorderPane
{
	Game game;
	Canvas canvas;
	GraphicsContext graphics;
	
	public GamePane()
	{
		game = new Game();
		canvas = new Canvas(750, 750);
		graphics = canvas.getGraphicsContext2D();
		update();
		
		this.setCenter(canvas);
	}
	
	public void makeMove(KeyEvent event)
	{
		switch (event.getCode())
		{
		case A:
			game.getPlayer().moveLeft();
			break;
		case D:
			game.getPlayer().moveRight();
			break;
		default:
			break;
		}
	}
	
	public void update()
	{
		graphics.setFill(Color.BLACK);
		graphics.fillRect(0, 0, 750, 750);
		game.update();
		drawObjects();
	}
	
	private void drawObjects()
	{
		Player player = game.getPlayer();
	    Image playerImage = new Image("file:player_ship.png");
		graphics.drawImage(playerImage, player.getPositionX(), player.getPositionY(), player.getSizeX() * 2, player.getSizeY() * 2);
		//graphics.setStroke(Color.WHITE);
		//graphics.strokeRect(player.getPositionX(), player.getPositionY(), player.getSizeX(), player.getSizeY());
	}
}
