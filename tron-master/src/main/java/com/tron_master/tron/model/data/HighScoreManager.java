package com.tron_master.tron.model.data;

import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Model class for managing high scores.
 * Handles reading and writing high scores to persistent storage.
 * Supports different game modes (Story, Survival).
 */
public class HighScoreManager {
    private static HighScoreManager instance;
    private final Path storagePath;
    
    /**
     * Game mode enum for high score tracking.
     */
    public enum GameMode {
        /** Story mode scores. */
        STORY("Story"),
        /** Survival mode scores. */
        SURVIVAL("Survival");
        
        private final String displayName;
        
        GameMode(String displayName) {
            this.displayName = displayName;
        }
        
        /**
         * Get human-friendly mode name.
         * @return human-friendly mode name
         */
        public String getDisplayName() {
            return displayName;
        }
    }
    
    /**
     * High score entry containing score and mode.
     */
    public static class ScoreEntry implements Comparable<ScoreEntry> {
        private final int score;
        private final GameMode mode;
        
        /**
         * Create a score entry.
         * @param score score value
         * @param mode game mode
         */
        public ScoreEntry(int score, GameMode mode) {
            this.score = score;
            this.mode = mode;
        }
        
        /**
         * Get numeric score value.
         * @return numeric score
         */
        public int getScore() {
            return score;
        }
        
        /**
         * Get associated game mode.
         * @return associated game mode
         */
        public GameMode getMode() {
            return mode;
        }
        
        @Override
        public int compareTo(ScoreEntry other) {
            return Integer.compare(other.score, this.score); // Descending order
        }
        
        @Override
        public String toString() {
            return score + "," + mode.name();
        }
        
        /**
         * Parse a score entry from its string representation.
         * @param line stored line (score,mode)
         * @return parsed entry or null if invalid
         */
        public static ScoreEntry fromString(String line) {
            if (line == null || line.isBlank()) {
                return null;
            }
            String[] parts = line.split(",");
            if (parts.length == 2) {
                try {
                    int score = Integer.parseInt(parts[0].trim());
                    GameMode mode = GameMode.valueOf(parts[1].trim());
                    return new ScoreEntry(score, mode);
                } catch (Exception e) {
                    // Invalid format, return null
                }
            }
            return null;
        }
    }
    
    private HighScoreManager() {
        String customPath = System.getProperty("highscores.path");
        this.storagePath = Path.of(customPath != null ? customPath : "src/main/resources/HighScores.txt");
        try {
            Path parent = storagePath.getParent();
            if (parent != null) {
                Files.createDirectories(parent);
            }
            if (customPath != null) {
                Files.deleteIfExists(storagePath);
            }
        } catch (Exception ignored) {
        }
    }
    
    /**
     * Get the singleton HighScoreManager.
     * @return shared instance
     */
    public static HighScoreManager getInstance() {
        if (instance == null) {
            instance = new HighScoreManager();
        }
        return instance;
    }

    /**
     * Reset the singleton (used in tests to isolate state).
     */
    public static void resetInstance() {
        instance = null;
    }
    
    /**
     * Read all high score entries from file.
     * @return List of score entries in descending order
     */
    public List<ScoreEntry> getAllHighScores() {
        List<ScoreEntry> entries = new ArrayList<>();
        try {
            if (Files.exists(storagePath)) {
                for (String line : Files.readAllLines(storagePath)) {
                    ScoreEntry entry = ScoreEntry.fromString(line);
                    if (entry != null) {
                        entries.add(entry);
                    }
                }
                Collections.sort(entries);
            }
        } catch (Exception e) {
            System.err.println("Failed to read high scores: " + e.getMessage());
        }
        return entries;
    }
    
    /**
     * Read high scores for a specific game mode.
     * @param mode The game mode to filter by
     * @return List of scores in descending order
     */
    public List<Integer> getHighScores(GameMode mode) {
        return getAllHighScores().stream()
                .filter(entry -> entry.getMode() == mode)
                .map(ScoreEntry::getScore)
                .toList();
    }
    
    /**
     * Read all high scores (legacy method for backward compatibility).
     * @return List of all scores in descending order
     */
    public List<Integer> getHighScores() {
        return getAllHighScores().stream()
                .map(ScoreEntry::getScore)
                .toList();
    }
    
    /**
     * Add a new score with game mode and save to file.
     * @param score The score to add
     * @param mode The game mode
     */
    public void addScore(int score, GameMode mode) {
        List<ScoreEntry> entries = getAllHighScores();
        entries.add(new ScoreEntry(score, mode));
        Collections.sort(entries);
        saveScores(entries);
    }
    
    /**
     * Save score entries to file.
     * @param entries List of score entries to save
     */
    private void saveScores(List<ScoreEntry> entries) {
        try {
            List<String> lines = entries.stream()
                    .map(ScoreEntry::toString)
                    .toList();
            Files.write(storagePath, lines);
        } catch (Exception e) {
            System.err.println("Failed to save high scores: " + e.getMessage());
        }
    }
}
