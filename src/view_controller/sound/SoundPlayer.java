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
	private static List<MediaPlayer> allSfxMedia = new ArrayList<>();
	private static double volume = 1d;
	private static double sfxVolume = 1d;
	private static double musicVolume = 1d;
	private static MediaPlayer currentThemeMusic = null;
	private static String currentPlayingSong = null;

	public static final String BOSS_THEME_PATH = "boss_theme.mp3";
	public static final String THEME_SONG_PATH = "theme_song.mp3";

	/**
	 * Loads all of the sounds effects and songs to be used in the game
	 */
	public static void loadAllAudio() {
		loadAllSongs();
		loadAllSounds();
	}

	/**
	 * Loads all the songs in the game
	 */
	private static void loadAllSongs() {
		fileNameSongs.add(THEME_SONG_PATH);
		fileNameSongs.add(BOSS_THEME_PATH);
		createMedia();
	}

	/**
	 * Loads all sounds in the game
	 */
	private static void loadAllSounds() {
		fileNameSounds.add("player_shoot.wav");
		fileNameSounds.add("player_death.wav");
		fileNameSounds.add("enemy_shoot.wav");
		fileNameSounds.add("enemy_death.wav");
		fileNameSounds.add("enemy_death_2.wav");
		fileNameSounds.add("game_over.mp3");
		createSounds();
	}

	/**
	 * Sets the volume of all the sounds
	 * 
	 * @param newVolume The new volume to set to
	 */
	public static void setVolume(double newVolume) {
		volume = newVolume;
		updateVolume();
	}

	/**
	 * Sets the volume of the effects
	 * 
	 * @param newVolume The new volume to set to
	 */
	public static void setSfxVolume(double newVolume) {
		sfxVolume = newVolume;
		updateVolume();
	}

	/**
	 * Sets the volume of the music
	 * 
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
		if (currentPlayingSong == THEME_SONG_PATH) {
			return;
		}
		if (currentThemeMusic != null) {
			currentThemeMusic.dispose();
			currentThemeMusic = null;
			currentPlayingSong = null;
		}
		currentPlayingSong = THEME_SONG_PATH;
		currentThemeMusic = playSong(THEME_SONG_PATH);
		currentThemeMusic.setOnEndOfMedia(() -> {
			currentThemeMusic.seek(Duration.ZERO);
		});
	}

	/**
	 * Starts the boss battle theme song
	 */
	public static void playBossThemeMusic() {
		if (currentPlayingSong == BOSS_THEME_PATH) {
			return;
		}
		if (currentThemeMusic != null) {
			currentThemeMusic.dispose();
			currentThemeMusic = null;
		}
		currentPlayingSong = BOSS_THEME_PATH;
		currentThemeMusic = playSong(BOSS_THEME_PATH);
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
	 * 
	 * @param fileName The sound to play
	 */
	public static void playSound(String fileName) {
		if (!sounds.containsKey(fileName)) {
			loadSound(fileName);
		}
		if (!sounds.containsKey(fileName)) {
			return;
		}
		AudioClip sound = sounds.get(fileName);
		sound.setVolume(sfxVolume * volume);
		sound.play();
	}

	/**
	 * Plays a specific song
	 * 
	 * @param fileName The song to play
	 * @return The Mediaplayer
	 */
	public static MediaPlayer playSong(String fileName) {
		Media media = null;

		if (songs.containsKey(fileName)) {
			media = songs.get(fileName);
		} else {
			loadSong(fileName);
		}
		if (!songs.containsKey(fileName)) {
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
			mediaPlayer.setVolume(musicVolume * volume);
		});
		mediaPlayer.setVolume(musicVolume * volume);
		mediaPlayer.play();
		return mediaPlayer;
	}

	/**
	 * Creates new media
	 */
	private static void createMedia() {
		for (String fileName : fileNameSongs) {
			loadSong(fileName);
		}
	}

	/**
	 * Creates sounds based on loaded files
	 */
	private static void createSounds() {
		for (String fileName : fileNameSounds) {
			loadSound(fileName);
		}
	}

	/**
	 * Loads a specific song
	 * 
	 * @param fileName The song to load
	 */
	private static void loadSong(String fileName) {
		File file = loadAudioFile(fileName);
		if (file != null) {
			URI uri = file.toURI();
			// Play one mp3 and and have code run when the song ends
			Media media = new Media(uri.toString());
			songs.put(fileName, media);
		}
	}

	/**
	 * Loads a specific sound
	 * 
	 * @param fileName The sound to load
	 */
	private static void loadSound(String fileName) {
		File file = loadAudioFile(fileName);
		if (file != null) {
			URI uri = file.toURI();
			AudioClip sound = new AudioClip(uri.toString());
			sounds.put(fileName, sound);
		}
	}

	/**
	 * Loads an audio file
	 * 
	 * @param fileName The audio to load
	 * @return The file that was loaded
	 */
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
