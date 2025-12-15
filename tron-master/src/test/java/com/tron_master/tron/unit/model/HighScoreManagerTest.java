package com.tron_master.tron.unit.model;

import java.nio.file.Path;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import com.tron_master.tron.model.data.HighScoreManager;
import com.tron_master.tron.model.data.HighScoreManager.GameMode;
import com.tron_master.tron.model.data.HighScoreManager.ScoreEntry;

/**
 * Unit tests for HighScoreManager.
 * Tests score persistence, sorting, and mode filtering.
 */
class HighScoreManagerTest {

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        // Use temp file for isolated testing
        Path tempFile = tempDir.resolve("test_scores.txt");
        System.setProperty("highscores.path", tempFile.toString());
        HighScoreManager.resetInstance();
    }

    @AfterEach
    void tearDown() {
        System.clearProperty("highscores.path");
        HighScoreManager.resetInstance();
    }

    @Test
    void addScore_persistsAndReturnsInDescendingOrder() {
        HighScoreManager manager = HighScoreManager.getInstance();
        
        manager.addScore(100, GameMode.SURVIVAL);
        manager.addScore(300, GameMode.SURVIVAL);
        manager.addScore(200, GameMode.SURVIVAL);
        
        List<Integer> scores = manager.getHighScores(GameMode.SURVIVAL);
        
        assertEquals(3, scores.size());
        assertEquals(300, scores.get(0));
        assertEquals(200, scores.get(1));
        assertEquals(100, scores.get(2));
    }

    @Test
    void getHighScores_filtersByGameMode() {
        HighScoreManager manager = HighScoreManager.getInstance();
        
        manager.addScore(100, GameMode.SURVIVAL);
        manager.addScore(200, GameMode.STORY);
        manager.addScore(150, GameMode.SURVIVAL);
        
        List<Integer> survivalScores = manager.getHighScores(GameMode.SURVIVAL);
        List<Integer> storyScores = manager.getHighScores(GameMode.STORY);
        
        assertEquals(2, survivalScores.size());
        assertEquals(1, storyScores.size());
        assertTrue(survivalScores.contains(100));
        assertTrue(survivalScores.contains(150));
        assertEquals(200, storyScores.getFirst());
    }

    @Test
    void getAllHighScores_returnsEmptyListWhenNoScores() {
        HighScoreManager manager = HighScoreManager.getInstance();
        
        List<ScoreEntry> scores = manager.getAllHighScores();
        
        assertTrue(scores.isEmpty());
    }

    @Test
    void scoreEntry_fromString_parsesValidFormat() {
        ScoreEntry entry = ScoreEntry.fromString("150,SURVIVAL");
        
        assertNotNull(entry);
        assertEquals(150, entry.getScore());
        assertEquals(GameMode.SURVIVAL, entry.getMode());
    }

    @Test
    void scoreEntry_fromString_returnsNullForInvalidFormat() {
        assertNull(ScoreEntry.fromString("invalid"));
        assertNull(ScoreEntry.fromString(""));
        assertNull(ScoreEntry.fromString(null));
        assertNull(ScoreEntry.fromString("abc,SURVIVAL"));
        assertNull(ScoreEntry.fromString("100,INVALID_MODE"));
    }

    @Test
    void scoreEntry_compareTo_sortsDescending() {
        ScoreEntry high = new ScoreEntry(300, GameMode.SURVIVAL);
        ScoreEntry low = new ScoreEntry(100, GameMode.SURVIVAL);
        
        assertTrue(high.compareTo(low) < 0); // high should come first (negative)
        assertTrue(low.compareTo(high) > 0); // low should come after (positive)
    }
}
