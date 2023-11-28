package view_controller.sound;

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
	private static double volume = 1d;
	private static double sfxVolume = 1d;
	private static double musicVolume = 1d;
	private static MediaPlayer currentThemeMusic = null;

	/**
	 * Loads all of the sounds effects and songs to be used in the game
	 */
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

	/**
	 * Sets the volume of all the sounds
	 * @param newVolume The new volume to set to
	 */
	public static void setVolume(double newVolume) {
		volume = newVolume;
		updateVolume();
	}

	/**
	 * Sets the volume of the effects
	 * @param newVolume The new volume to set to
	 */
	public static void setSfxVolume(double newVolume) {
		sfxVolume = newVolume;
		updateVolume();
	}

	/**
	 * Sets the volume of the music
	 * @param newVolume The new volume to set to
	 */
	public static void setMusicVolume(double newVolume) {
		musicVolume = newVolume;
		updateVolume();
	}

	/**
	 * Starts the theme song
	 */
	public static void playMainThemeMusic() {
		if (currentThemeMusic != null) {
			// System.err.println("Cannot play theme music, already playing theme music!");
			return;
		}
		currentThemeMusic = playSound("theme_song.mp3", true);
		currentThemeMusic.setOnEndOfMedia(() -> {
			currentThemeMusic.seek(Duration.ZERO);
		});
	}

	/**
	 * Stops the theme song
	 */
	public static void stopThemeMusic() {
		if (currentThemeMusic != null) {
			currentThemeMusic.stop();
			currentThemeMusic.dispose();
			currentThemeMusic = null;
		}
	}

	/**
	 * Plays a specific sound
	 * @param fileName The sound to play
	 * @param isMusic If the sound is music or an effect
	 * @return The MediaPlayer of the sound
	 */
	public static MediaPlayer playSound(String fileName, boolean isMusic) {
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
		});
		mediaPlayer.setOnStopped(() -> {
			// System.out.println("media stopped");
			currentlyPlayingMedia.remove(mediaPlayer);
		});
		mediaPlayer.setOnHalted(() -> {
			// System.out.println("media halted");
			currentlyPlayingMedia.remove(mediaPlayer);
		});
		mediaPlayer.setOnPlaying(() -> {
			// System.out.println("media started");
			if (currentlyPlayingMedia.contains(mediaPlayer) == false)
				currentlyPlayingMedia.add(mediaPlayer);
			if (isMusic)
				mediaPlayer.setVolume(musicVolume * volume);
			else
				mediaPlayer.setVolume(sfxVolume * volume);
		});
		if (isMusic)
			mediaPlayer.setVolume(musicVolume * volume);
		else
			mediaPlayer.setVolume(sfxVolume * volume);
		mediaPlayer.play();
		return mediaPlayer;
	}

	/**
	 * Creates new media
	 */
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

	/**
	 * Updates the overall volume
	 */
	private static void updateVolume() {
		for (MediaPlayer mediaPlayer : currentlyPlayingMedia) {
			if (mediaPlayer != currentThemeMusic) {
				mediaPlayer.setVolume(sfxVolume * volume);
			}
		}
		if (currentThemeMusic != null) {
			currentThemeMusic.setVolume(musicVolume * volume);
		}
	}
}
