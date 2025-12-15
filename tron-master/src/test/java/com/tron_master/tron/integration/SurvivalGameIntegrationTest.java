package com.tron_master.tron.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tron_master.tron.constant.GameConstant;
import com.tron_master.tron.model.data.GameData;
import com.tron_master.tron.model.data.GameState;
import com.tron_master.tron.model.logic_strategy.SurvLogicStrategy;
import com.tron_master.tron.model.object.Portal;
import com.tron_master.tron.model.object.Wall;

/**
 * Integration tests for Survival mode.
 * Tests interaction between SurvLogicStrategy, Wall, Portal, and GameData.
 * Focus: initialization, state transitions, score persistence, and reset functionality.
 */
class SurvivalGameIntegrationTest {

    private SurvLogicStrategy gameLogic;

    @BeforeEach
    void setUp() {
        GameData.getInstance().resetAllData();
        gameLogic = new SurvLogicStrategy(
            GameConstant.GAME_AREA_WIDTH, 
            GameConstant.GAME_AREA_HEIGHT
        );
    }

    // ===== Initialization Integration Tests =====

    @Test
    void initialization_loadsWallsFromFxml() {
        Wall[] walls = gameLogic.getWalls();
        
        assertNotNull(walls, "Walls should be loaded");
        assertTrue(walls.length > 0, "Should have at least one wall");
    }

    @Test
    void initialization_loadsPortalsFromFxml() {
        Portal[] portals = gameLogic.getPortals();
        
        assertNotNull(portals, "Portals should be loaded");
        assertTrue(portals.length > 0, "Should have at least one portal");
    }

    @Test
    void initialization_playerStartsAlive() {
        assertTrue(gameLogic.getPlayer().getAlive(), 
            "Player should be alive at game start");
    }

    @Test
    void initialization_scoreStartsAtZero() {
        assertEquals(0, gameLogic.getScore(), 
            "Score should be 0 at game start");
    }

    @Test
    void initialization_gameStateIsPlaying() {
        assertEquals(GameState.PLAYING, gameLogic.getSurvivalState(),
            "Game state should be PLAYING at start");
    }

    @Test
    void initialization_gameDataIsSynchronized() {
        assertEquals(gameLogic.getScore(), GameData.getInstance().getSurvivalScore(),
            "GameLogic score should match GameData score");
    }

    // ===== State Transition Integration Tests =====

    @Test
    void stateTransition_playerDeathChangesGameState() {
        // Simulate player death
        gameLogic.getPlayer().setAlive(false);
        
        // Create mock loop handle
        MockStopCallback mockHandle = new MockStopCallback();
        gameLogic.tick(mockHandle);
        
        assertEquals(GameState.GAME_OVER, gameLogic.getSurvivalState(),
            "Game state should be GAME_OVER after player death");
        assertTrue(mockHandle.wasStopped(), 
            "Game loop should be stopped after player death");
    }

    @Test
    void stateTransition_gameRunningFlagUpdatesCorrectly() {
        assertTrue(gameLogic.isRunning(), "Game should be running initially");
        
        gameLogic.getPlayer().setAlive(false);
        gameLogic.tick(new MockStopCallback());
        
        assertFalse(gameLogic.isRunning(), 
            "Game should not be running after player death");
    }

    // ===== Score Persistence Integration Tests =====

    @Test
    void scorePersistence_scoreIncreasesEachTick() {
        int initialScore = gameLogic.getScore();
        
        gameLogic.tick(new MockStopCallback());
        
        assertEquals(initialScore + 1, gameLogic.getScore(),
            "Score should increase by 1 each tick");
    }

    @Test
    void scorePersistence_gameDataUpdatesWithScore() {
        gameLogic.tick(new MockStopCallback());
        gameLogic.tick(new MockStopCallback());
        
        assertEquals(gameLogic.getScore(), GameData.getInstance().getSurvivalScore(),
            "GameData should reflect current score");
    }

    // ===== Reset Integration Tests =====

    @Test
    void reset_scoreResetsToZero() {
        // Accumulate some score
        for (int i = 0; i < 10; i++) {
            gameLogic.tick(new MockStopCallback());
        }
        assertTrue(gameLogic.getScore() > 0, "Should have accumulated score");
        
        gameLogic.reset();
        
        assertEquals(0, gameLogic.getScore(), "Score should be 0 after reset");
    }

    @Test
    void reset_playerIsAliveAfterReset() {
        gameLogic.getPlayer().setAlive(false);
        
        gameLogic.reset();
        
        assertTrue(gameLogic.getPlayer().getAlive(), 
            "Player should be alive after reset");
    }

    @Test
    void reset_gameStateResetsToPlaying() {
        gameLogic.getPlayer().setAlive(false);
        gameLogic.tick(new MockStopCallback());
        assertEquals(GameState.GAME_OVER, gameLogic.getSurvivalState());
        
        gameLogic.reset();
        
        assertEquals(GameState.PLAYING, gameLogic.getSurvivalState(),
            "Game state should be PLAYING after reset");
    }

    @Test
    void reset_wallsAreReloaded() {
        gameLogic.reset();
        
        Wall[] newWalls = gameLogic.getWalls();
        assertNotNull(newWalls, "Walls should exist after reset");
        assertTrue(newWalls.length > 0, "Should have walls after reset");
    }

    @Test
    void reset_portalsAreReloaded() {
        Portal[] originalPortals = gameLogic.getPortals();
        
        gameLogic.reset();
        
        Portal[] newPortals = gameLogic.getPortals();
        assertNotNull(newPortals, "Portals should exist after reset");
        assertEquals(originalPortals.length, newPortals.length,
            "Portal count should remain consistent after reset");
    }

    @Test
    void reset_gameDataIsSynchronized() {
        gameLogic.tick(new MockStopCallback());
        gameLogic.reset();
        
        assertEquals(0, GameData.getInstance().getSurvivalScore(),
            "GameData score should be reset to 0");
        assertEquals(GameState.PLAYING, GameData.getInstance().getSurvivalState(),
            "GameData state should be PLAYING after reset");
    }

    /**
     * Mock stop callback for testing tick() behavior.
     */
    private static class MockStopCallback implements Runnable {
        private boolean stopped = false;
        
        @Override
        public void run() {
            stopped = true;
        }
        
        public boolean wasStopped() {
            return stopped;
        }
    }
}
