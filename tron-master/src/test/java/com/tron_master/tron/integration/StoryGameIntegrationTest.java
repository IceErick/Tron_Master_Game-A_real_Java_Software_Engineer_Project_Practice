package com.tron_master.tron.integration;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tron_master.tron.constant.GameConstant;
import com.tron_master.tron.model.data.GameData;
import com.tron_master.tron.model.data.GameState;
import com.tron_master.tron.model.logic_strategy.StoryLogicStrategy;
import com.tron_master.tron.model.object.Player;
import com.tron_master.tron.model.object.Portal;
import com.tron_master.tron.model.object.Wall;

/**
 * Integration tests for Story mode.
 * Tests interaction between StoryLogicStrategy, Wall, Portal, Player (AI), and GameData.
 * Focus: initialization, state transitions, level progression, and reset functionality.
 */
class StoryGameIntegrationTest {

    private StoryLogicStrategy gameLogic;
    private static final int INITIAL_PLAYER_COUNT = 2; // Level 1: 1 human + 1 AI

    @BeforeEach
    void setUp() {
        GameData.getInstance().resetAllData();
        gameLogic = new StoryLogicStrategy(
            INITIAL_PLAYER_COUNT,
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
    void initialization_createsCorrectNumberOfPlayers() {
        Player[] players = gameLogic.getPlayers();
        
        assertEquals(INITIAL_PLAYER_COUNT, players.length,
            "Should have correct number of players");
    }

    @Test
    void initialization_humanPlayerIsFirstPlayer() {
        Player[] players = gameLogic.getPlayers();
        
        assertTrue(players[0].isHuman(), 
            "First player should be human");
    }

    @Test
    void initialization_allPlayersStartAlive() {
        Player[] players = gameLogic.getPlayers();
        
        for (Player p : players) {
            assertTrue(p.getAlive(), "All players should be alive at start");
        }
    }

    // ===== State Transition Integration Tests =====

    @Test
    void stateTransition_humanDeathEndsGame() {
        gameLogic.getPlayer().setAlive(false);
        
        MockStopCallback mockHandle = new MockStopCallback();
        gameLogic.tick(mockHandle);
        
        assertEquals(GameState.GAME_OVER, GameData.getInstance().getStoryState(),
            "Game state should be GAME_OVER when human dies");
        assertTrue(mockHandle.wasStopped(),
            "Game loop should be stopped");
    }

    @Test
    void stateTransition_allAiDeathCompletesLevel() {
        Player[] players = gameLogic.getPlayers();
        
        // Kill all AI players
        for (int i = 1; i < players.length; i++) {
            players[i].setAlive(false);
        }
        
        MockStopCallback mockHandle = new MockStopCallback();
        gameLogic.tick(mockHandle);
        
        assertEquals(GameState.LEVEL_COMPLETE, GameData.getInstance().getStoryState(),
            "Game state should be LEVEL_COMPLETE when all AI die");
    }

    @Test
    void stateTransition_gameRunningDuringPlay() {
        MockStopCallback mockHandle = new MockStopCallback();
        gameLogic.tick(mockHandle);
        
        assertTrue(gameLogic.isRunning(),
            "Game should be running during normal play");
        assertEquals(GameState.PLAYING, GameData.getInstance().getStoryState(),
            "Game state should be PLAYING");
    }

    // ===== Level Progression Integration Tests =====

    @Test
    void levelProgression_nextLevelIncreasesLevel() {
        // Complete current level first
        Player[] players = gameLogic.getPlayers();
        for (int i = 1; i < players.length; i++) {
            players[i].setAlive(false);
        }
        gameLogic.tick(new MockStopCallback());
        
        boolean hasNextLevel = gameLogic.nextLevel();
        
        assertTrue(hasNextLevel, "Should have next level");
        assertEquals(2, GameData.getInstance().getStoryLevel(),
            "Level should increase to 2");
    }

    @Test
    void levelProgression_playerCountIncreasesWithLevel() {
        // Complete level and advance
        Player[] players = gameLogic.getPlayers();
        for (int i = 1; i < players.length; i++) {
            players[i].setAlive(false);
        }
        gameLogic.tick(new MockStopCallback());
        gameLogic.nextLevel();
        
        Player[] newPlayers = gameLogic.getPlayers();
        assertEquals(3, newPlayers.length,
            "Level 2 should have 3 players (1 human + 2 AI)");
    }

    @Test
    void levelProgression_scoreAccumulatesAcrossLevels() {
        int initialScore = GameData.getInstance().getStoryScore();
        
        // Complete level
        Player[] players = gameLogic.getPlayers();
        for (int i = 1; i < players.length; i++) {
            players[i].setAlive(false);
        }
        gameLogic.tick(new MockStopCallback());
        
        int scoreAfterLevel = GameData.getInstance().getStoryScore();
        assertTrue(scoreAfterLevel > initialScore,
            "Score should increase after completing level");
    }

    // ===== Reset Integration Tests =====

    @Test
    void reset_levelResetsToOne() {
        // Advance to level 2
        Player[] players = gameLogic.getPlayers();
        for (int i = 1; i < players.length; i++) {
            players[i].setAlive(false);
        }
        gameLogic.tick(new MockStopCallback());
        gameLogic.nextLevel();
        assertEquals(2, GameData.getInstance().getStoryLevel());
        
        gameLogic.reset();
        
        assertEquals(1, GameData.getInstance().getStoryLevel(),
            "Level should reset to 1");
    }

    @Test
    void reset_allPlayersAreAlive() {
        gameLogic.getPlayer().setAlive(false);
        
        gameLogic.reset();
        
        Player[] players = gameLogic.getPlayers();
        for (Player p : players) {
            assertTrue(p.getAlive(), "All players should be alive after reset");
        }
    }

    @Test
    void reset_gameStateResetsToPlaying() {
        gameLogic.getPlayer().setAlive(false);
        gameLogic.tick(new MockStopCallback());
        
        gameLogic.reset();
        
        assertEquals(GameState.PLAYING, GameData.getInstance().getStoryState(),
            "Game state should be PLAYING after reset");
    }

    @Test
    void reset_wallsAndPortalsAreReloaded() {
        gameLogic.reset();
        
        assertNotNull(gameLogic.getWalls(), "Walls should exist after reset");
        assertNotNull(gameLogic.getPortals(), "Portals should exist after reset");
        assertTrue(gameLogic.getWalls().length > 0, "Should have walls");
        assertTrue(gameLogic.getPortals().length > 0, "Should have portals");
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
