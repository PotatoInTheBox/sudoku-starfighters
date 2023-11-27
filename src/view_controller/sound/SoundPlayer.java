package view_controller.sound;

import java.io.File;
import java.net.URI;
import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

import javafx.beans.property.DoubleProperty;
import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;

public class SoundPlayer {
	public static HashMap<String, Media> songs = new HashMap<>();
	public static HashMap<String, AudioClip> sounds = new HashMap<>();
	public static ArrayList<String> fileNameSongs = new ArrayList<>();
	public static ArrayList<String> fileNameSounds = new ArrayList<>();
	private static List<MediaPlayer> currentlyPlayingMedia = new ArrayList<>();
	private static double volume = 1d;
	private static double sfxVolume = 1d;
	private static double musicVolume = 1d;
	private static MediaPlayer currentThemeMusic = null;

	public static void loadAllAudio() {
		loadAllSongs();
		loadAllSounds();
	}

	private static void loadAllSongs() {
		fileNameSongs.add("game_over.mp3");
		fileNameSongs.add("theme_song.mp3");
		createMedia();
	}

	private static void loadAllSounds() {
		fileNameSounds.add("player_shoot.wav");
		fileNameSounds.add("player_death.wav");
		fileNameSounds.add("enemy_shoot.wav");
		fileNameSounds.add("enemy_death.wav");
		fileNameSounds.add("enemy_death_2.wav");
		createSounds();
	}

	public static void setVolume(double newVolume) {
		volume = newVolume;
		updateVolume();
	}

	public static void setSfxVolume(double newVolume) {
		sfxVolume = newVolume;
		updateVolume();
	}

	public static void setMusicVolume(double newVolume) {
		musicVolume = newVolume;
		updateVolume();
	}

	public static void playMainThemeMusic() {
		if (currentThemeMusic != null) {
			// System.err.println("Cannot play theme music, already playing theme music!");
			return;
		}
		currentThemeMusic = playSong("theme_song.mp3", true);
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

	public static void playSound(String fileName) {
		if (!sounds.containsKey(fileName)){
			loadSound(fileName);
		}
		if (!sounds.containsKey(fileName)){
			return;
		}
		AudioClip sound = sounds.get(fileName);
		sound.setVolume(sfxVolume * volume);
		sound.play();
	}

	public static MediaPlayer playSong(String fileName, boolean isMusic) {
		Media media = null;

		if (songs.containsKey(fileName)) {
			media = songs.get(fileName);
		} else {
			loadSong(fileName);
		}
		if (!songs.containsKey(fileName)){
			return null;
		}

		media = songs.get(fileName);
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

	private static void createMedia() {
		for (String fileName : fileNameSongs) {
			loadSong(fileName);
		}
	}

	private static void createSounds() {
		for (String fileName : fileNameSounds) {
			loadSound(fileName);
		}
	}

	private static void loadSong(String fileName) {
		File file = loadAudioFile(fileName);
		if (file != null) {
			URI uri = file.toURI();
			// Play one mp3 and and have code run when the song ends
			Media media = new Media(uri.toString());
			songs.put(fileName, media);
		}
	}

	private static void loadSound(String fileName) {
		File file = loadAudioFile(fileName);
		if (file != null) {
			URI uri = file.toURI();
			AudioClip sound = new AudioClip(uri.toString());
			sounds.put(fileName, sound);
		}
	}

	private static File loadAudioFile(String fileName) {
		String path = "resources/sounds/" + fileName;
		try {
			// Need a File and URI object so the path works on all OSs
			return new File(path);
		} catch (Exception e) {
			System.err.println("Cannot local audio file " + path);
			return null;
		}
	}

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
