package com.tron_master.tron.model.sound;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import javafx.scene.media.AudioClip;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;

/**
 * Singleton sound manager for handling game audio (sound effects and background music).
 * Manages loading, caching, and playback of audio files.
 */
public class SoundManager {
    
    private static SoundManager instance;
    
    // Sound effect AudioClips (optimized for rapid playback)
    private final Map<String, AudioClip> soundEffects;
    
    // Background music players (dual-track support)
    private MediaPlayer arcadeAmbientPlayer;  // Always playing
    private MediaPlayer futuristicBgPlayer;   // Plays during gameplay
    
    // Settings
    private boolean soundEnabled = true;
    private boolean musicEnabled = true;
    private double musicVolume = 0.5;
    private final double ambientReducedVolume = 0.25; // 50% of normal volume
    
    // Individual sound effect volumes
    private final Map<String, Double> soundEffectVolumes = new HashMap<>();
    
    // Available sound effect files
    private static final Map<String, String> SOUND_FILES = new HashMap<>();
    static {
        SOUND_FILES.put("player_died", "/sounds/die.wav");
        SOUND_FILES.put("boost", "/sounds/boost.wav");
        SOUND_FILES.put("jump", "/sounds/jump.wav");
        SOUND_FILES.put("click", "/sounds/click.wav");
        SOUND_FILES.put("quit", "/sounds/quit.wav");
        SOUND_FILES.put("reset", "/sounds/reset.wav");
        SOUND_FILES.put("clic", "/sounds/clic.wav");
        SOUND_FILES.put("teleport", "/sounds/teleport.wav");
    }
    
    // Available background music files
    private static final Map<String, String> MUSIC_FILES = new HashMap<>();
    static {
        MUSIC_FILES.put("arcade_ambient", "/sounds/arcade_ambient.wav");
        MUSIC_FILES.put("futuristic_bg", "/sounds/futuristic_bg.wav");
        SOUND_FILES.put("instructions", "/sounds/instructions.wav");
    }
    
    private SoundManager() {
        soundEffects = new HashMap<>();
        initializeSoundEffectVolumes();
        preloadSoundEffects();
        initializeBackgroundMusic();
    }
    
    /**
     * Initialize individual volume levels for each sound effect.
     */
    private void initializeSoundEffectVolumes() {
        soundEffectVolumes.put("player_died", 0.7);
        soundEffectVolumes.put("boost", 0.5);
        soundEffectVolumes.put("jump", 0.5);
        soundEffectVolumes.put("click", 0.6);
        soundEffectVolumes.put("quit", 0.7);
        soundEffectVolumes.put("reset", 0.7);
        soundEffectVolumes.put("clic", 0.8);
        soundEffectVolumes.put("instructions", 0.7);
        soundEffectVolumes.put("teleport", 0.6);
    }
    
    /**
     * Initialize both background music tracks.
     * arcade_ambient will start playing immediately.
     */
    private void initializeBackgroundMusic() {
        try {
            // Load arcade_ambient (menu music)
            URL arcadeResource = getClass().getResource(MUSIC_FILES.get("arcade_ambient"));
            if (arcadeResource != null) {
                Media arcadeMedia = new Media(arcadeResource.toString());
                arcadeAmbientPlayer = new MediaPlayer(arcadeMedia);
                arcadeAmbientPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                arcadeAmbientPlayer.setVolume(musicVolume);
                if (musicEnabled) {
                    arcadeAmbientPlayer.play();
                }
            }
            
            // Load futuristic_bg (game music) but don't play yet
            URL futuristicResource = getClass().getResource(MUSIC_FILES.get("futuristic_bg"));
            if (futuristicResource != null) {
                Media futuristicMedia = new Media(futuristicResource.toString());
                futuristicBgPlayer = new MediaPlayer(futuristicMedia);
                futuristicBgPlayer.setCycleCount(MediaPlayer.INDEFINITE);
                futuristicBgPlayer.setVolume(0); // Start silent
            }
        } catch (Exception e) {
            System.err.println("Failed to initialize background music: " + e.getMessage());
        }
    }
    
    /**
     * Get the singleton SoundManager instance.
     * @return shared SoundManager
     */
    public static SoundManager getInstance() {
        if (instance == null) {
            instance = new SoundManager();
        }
        return instance;
    }
    
    /**
     * Preload all sound effects as AudioClips for instant, low-overhead playback.
     * AudioClip is optimized for short sounds, has better performance and supports overlapping playback natively.
     */
    private void preloadSoundEffects() {
        for (Map.Entry<String, String> entry : SOUND_FILES.entrySet()) {
            try {
                URL resource = getClass().getResource(entry.getValue());
                if (resource != null) {
                    AudioClip clip = new AudioClip(resource.toString());
                    soundEffects.put(entry.getKey(), clip);
                } else {
                    System.err.println("Sound file not found: " + entry.getValue());
                }
            } catch (Exception e) {
                System.err.println("Failed to load sound: " + entry.getKey() + " - " + e.getMessage());
            }
        }
    }
    
    /**
     * Play a sound effect by name using AudioClip.
     * AudioClip supports instant playback and automatic overlapping without manual resource management.
     * Uses individual volume level for each sound effect.
     * @param soundName Name of the sound effect (e.g., "player_died", "boost", "jump")
     */
    public void playSoundEffect(String soundName) {
        if (!soundEnabled) {
            return;
        }
        
        AudioClip clip = soundEffects.get(soundName);
        if (clip != null) {
            // Use individual volume level for each sound effect
            double soundVolume = 0.7;
            double volume = soundEffectVolumes.getOrDefault(soundName, soundVolume);
            clip.play(volume);
        } else {
            System.err.println("Sound effect not found: " + soundName);
        }
    }
    
    /**
     * Stop all playing sound effects.
     * This will immediately stop all AudioClips that are currently playing.
     */
    public void stopAllSoundEffects() {
        for (AudioClip clip : soundEffects.values()) {
            if (clip.isPlaying()) {
                clip.stop();
            }
        }
    }
    
    /**
     * Play background music with dual-track support.
     * arcade_ambient always plays, futuristic_bg overlays during gameplay.
     * @param musicName Name of the music track ("arcade_ambient" or "futuristic_bg")
     */
    public void playBackgroundMusic(String musicName) {
        if (!musicEnabled) {
            return;
        }
        
        if ("arcade_ambient".equals(musicName)) {
            // Ensure arcade is playing at full volume
            if (arcadeAmbientPlayer != null) {
                arcadeAmbientPlayer.setVolume(musicVolume);
                if (arcadeAmbientPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
                    arcadeAmbientPlayer.play();
                }
            }
            // Stop futuristic if playing
            if (futuristicBgPlayer != null && futuristicBgPlayer.getStatus() == MediaPlayer.Status.PLAYING) {
                futuristicBgPlayer.setVolume(0);
                futuristicBgPlayer.pause();
            }
        } else if ("futuristic_bg".equals(musicName)) {
            // Reduce arcade volume to 50% (0.25 = 50% of 0.5)
            if (arcadeAmbientPlayer != null) {
                arcadeAmbientPlayer.setVolume(ambientReducedVolume);
            }
            // Start futuristic
            if (futuristicBgPlayer != null) {
                futuristicBgPlayer.setVolume(musicVolume);
                if (futuristicBgPlayer.getStatus() != MediaPlayer.Status.PLAYING) {
                    futuristicBgPlayer.play();
                }
            }
        }
    }
    
    /**
     * Pause all background music.
     */
    public void pauseBackgroundMusic() {
        if (arcadeAmbientPlayer != null) {
            arcadeAmbientPlayer.pause();
        }
        if (futuristicBgPlayer != null) {
            futuristicBgPlayer.pause();
        }
    }
    
    /**
     * Check if background music is enabled.
     * @return true if music is enabled, false otherwise
     */
    public boolean isMusicEnabled() {
        return musicEnabled;
    }
    
    /**
     * Enable or disable background music.
     * When disabled, all background music will be paused.
     * When enabled, music will resume from where it was paused.
     * @param enabled true to enable music, false to disable
     */
    public void setMusicEnabled(boolean enabled) {
        this.musicEnabled = enabled;
        
        if (enabled) {
            // Resume music playback
            if (arcadeAmbientPlayer != null && arcadeAmbientPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
                arcadeAmbientPlayer.play();
            }
            if (futuristicBgPlayer != null && futuristicBgPlayer.getStatus() == MediaPlayer.Status.PAUSED) {
                futuristicBgPlayer.play();
            }
        } else {
            // Pause all music
            pauseBackgroundMusic();
        }
    }
}
