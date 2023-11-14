package view_controller;

import java.io.File;
import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

public class SoundPlayer
{
	public static HashMap<String, Media> songs = new HashMap<>();
	public static ArrayList<String> fileNames = new ArrayList<>();
	
	public static void loadAllSongs() {
		fileNames.add("player_shoot.wav");
		fileNames.add("player_death.wav");
		fileNames.add("enemy_shoot.wav");
		fileNames.add("enemy_death.wav");
		fileNames.add("enemy_death_2.wav");
		fileNames.add("game_over.mp3");
		
		createMedia();
	}
	
	public static void playSound(String fileName) {
		Media media = null;
		
		if (songs.containsKey(fileName))
		{
			media = songs.get(fileName);
			MediaPlayer mediaPlayer = new MediaPlayer(media);

			mediaPlayer.play();
		} else
		{
			String path = "resources/sounds/" + fileName;
			
			// Need a File and URI object so the path works on all OSs
			File file = new File(path);
			URI uri = file.toURI();
			// Play one mp3 and and have code run when the song ends
			media = new Media(uri.toString());
			songs.put(fileName, media);
		}
		
	}
	
	private static void createMedia() {
		for (String fileName : fileNames)
		{
			String path = "resources/sounds/" + fileName;
			
			// Need a File and URI object so the path works on all OSs
			File file = new File(path);
			URI uri = file.toURI();
			// Play one mp3 and and have code run when the song ends
			Media media = new Media(uri.toString());
			songs.put(fileName, media);
		}
	}
}


