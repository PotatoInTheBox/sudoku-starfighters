package view_controller;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.beans.property.DoubleProperty;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class SoundPlayer {
	public static HashMap<String, Media> songs = new HashMap<>();
	public static ArrayList<String> fileNames = new ArrayList<>();
	private static List<MediaPlayer> currentlyPlayingMedia = new ArrayList<>();
	private static DoubleProperty soundVolume = null;
	private static MediaPlayer currentThemeMusic = null;

	public static void loadAllSongs() {
		fileNames.add("player_shoot.wav");
		fileNames.add("player_death.wav");
		fileNames.add("enemy_shoot.wav");
		fileNames.add("enemy_death.wav");
		fileNames.add("enemy_death_2.wav");
		fileNames.add("game_over.mp3");
		fileNames.add("theme_song.mp3");

		createMedia();
	}

	public static void setVolume(DoubleProperty volumeProperty) {
		soundVolume = volumeProperty;
		for (MediaPlayer mediaPlayer : currentlyPlayingMedia) {
			mediaPlayer.setVolume(soundVolume.doubleValue());
		}
	}

	public static void playMainThemeMusic() {
		if (currentThemeMusic != null) {
			// System.err.println("Cannot play theme music, already playing theme music!");
			return;
		}
		currentThemeMusic = playSound("theme_song.mp3");
		currentThemeMusic.setOnEndOfMedia(() -> {
			currentThemeMusic.seek(Duration.ZERO);
		});
	}

	public static void stopThemeMusic() {
		if (currentThemeMusic != null) {
			currentThemeMusic.stop();
			currentThemeMusic.dispose();
			currentThemeMusic = null;
		}
	}

	public static MediaPlayer playSound(String fileName) {
		Media media = null;

		if (songs.containsKey(fileName)) {
			media = songs.get(fileName);
		} else {
			String path = "resources/sounds/" + fileName;

			// Need a File and URI object so the path works on all OSs
			File file = new File(path);
			URI uri = file.toURI();
			// Play one mp3 and and have code run when the song ends
			media = new Media(uri.toString());

		}
		songs.put(fileName, media);

		MediaPlayer mediaPlayer = new MediaPlayer(media);
		mediaPlayer.setOnEndOfMedia(() -> {
			// System.out.println("media ended");
			currentlyPlayingMedia.remove(mediaPlayer);
			System.out.println("# of sounds playing: " + currentlyPlayingMedia.size());
		});
		mediaPlayer.setOnStopped(() -> {
			// System.out.println("media stopped");
			currentlyPlayingMedia.remove(mediaPlayer);
			System.out.println("# of sounds playing: " + currentlyPlayingMedia.size());
		});
		mediaPlayer.setOnHalted(() -> {
			// System.out.println("media halted");
			currentlyPlayingMedia.remove(mediaPlayer);
			System.out.println("# of sounds playing: " + currentlyPlayingMedia.size());
		});
		mediaPlayer.setOnPlaying(() -> {
			// System.out.println("media started");
			if (currentlyPlayingMedia.contains(mediaPlayer) == false)
				currentlyPlayingMedia.add(mediaPlayer);
			updateMediaPreferences(mediaPlayer);
			System.out.println("# of sounds playing: " + currentlyPlayingMedia.size());
		});
		mediaPlayer.play();
		return mediaPlayer;
	}

	private static void updateMediaPreferences(MediaPlayer mediaPlayer) {
		if (soundVolume != null) {
			mediaPlayer.setVolume(soundVolume.doubleValue());
		}
	}

	private static void createMedia() {
		for (String fileName : fileNames) {
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
