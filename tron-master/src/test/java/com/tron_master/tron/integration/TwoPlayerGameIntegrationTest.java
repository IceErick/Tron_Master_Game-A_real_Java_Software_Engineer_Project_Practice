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
import com.tron_master.tron.model.data.TwoPlayerOutcome;
import com.tron_master.tron.model.logic_strategy.TwoPlayerLogicStrategy;
import com.tron_master.tron.model.object.Player;
import com.tron_master.tron.model.object.Wall;

/**
 * Integration tests for Two-Player mode.
 * Tests interaction between TwoPlayerLogicStrategy, Wall, Player, and GameData.
 * Focus: initialization, state transitions, win/lose outcome, and reset functionality.
 */
class TwoPlayerGameIntegrationTest {

    private TwoPlayerLogicStrategy gameLogic;

    @BeforeEach
    void setUp() {
        GameData.getInstance().resetAllData();
        gameLogic = new TwoPlayerLogicStrategy(
            GameConstant.GAME_AREA_WIDTH, 
            GameConstant.GAME_AREA_HEIGHT
        );
    }

    // Helper methods to access players
    private Player getPlayer1() {
        return gameLogic.getPlayers()[0];
    }
    
    private Player getPlayer2() {
        return gameLogic.getPlayers()[1];
    }

    // ===== Initialization Integration Tests =====

    @Test
    void initialization_loadsTwoHumanPlayers() {
        Player player1 = getPlayer1();
        Player player2 = getPlayer2();
        
        assertNotNull(player1, "Player 1 should exist");
        assertNotNull(player2, "Player 2 should exist");
        assertTrue(player1.isHuman(), "Player 1 should be human");
        assertTrue(player2.isHuman(), "Player 2 should be human");
    }

    @Test
    void initialization_loadsWallsFromFxml() {
        Wall[] walls = gameLogic.getWalls();
        
        assertNotNull(walls, "Walls should be loaded");
        assertTrue(walls.length > 0, "Should have at least one wall");
    }

    @Test
    void initialization_bothPlayersStartAlive() {
        assertTrue(getPlayer1().getAlive(), "Player 1 should be alive");
        assertTrue(getPlayer2().getAlive(), "Player 2 should be alive");
    }

    @Test
    void initialization_scoresStartAtZero() {
        assertEquals(0, GameData.getInstance().getTwoPlayerP1Score(),
            "Player 1 score should be 0");
        assertEquals(0, GameData.getInstance().getTwoPlayerP2Score(),
            "Player 2 score should be 0");
    }

    @Test
    void initialization_gameStateIsPlaying() {
        assertEquals(GameState.PLAYING, GameData.getInstance().getTwoPlayerState(),
            "Game state should be PLAYING at start");
    }

    // ===== State Transition & Outcome Integration Tests =====

    @Test
    void stateTransition_player1DeathGivesPlayer2Win() {
        getPlayer1().setAlive(false);
        
        MockStopCallback mockHandle = new MockStopCallback();
        gameLogic.tick(mockHandle);
        
        assertEquals(GameState.GAME_OVER, GameData.getInstance().getTwoPlayerState(),
            "Game state should be GAME_OVER");
        assertEquals(TwoPlayerOutcome.P2_WIN, GameData.getInstance().getTwoPlayerOutcome(),
            "Outcome should be P2_WIN when P1 dies");
        assertTrue(mockHandle.wasStopped(),
            "Game loop should be stopped");
    }

    @Test
    void stateTransition_player2DeathGivesPlayer1Win() {
        getPlayer2().setAlive(false);
        
        MockStopCallback mockHandle = new MockStopCallback();
        gameLogic.tick(mockHandle);
        
        assertEquals(GameState.GAME_OVER, GameData.getInstance().getTwoPlayerState(),
            "Game state should be GAME_OVER");
        assertEquals(TwoPlayerOutcome.P1_WIN, GameData.getInstance().getTwoPlayerOutcome(),
            "Outcome should be P1_WIN when P2 dies");
    }

    @Test
    void stateTransition_bothDeathResultsInTie() {
        getPlayer1().setAlive(false);
        getPlayer2().setAlive(false);
        
        MockStopCallback mockHandle = new MockStopCallback();
        gameLogic.tick(mockHandle);
        
        assertEquals(TwoPlayerOutcome.TIE, GameData.getInstance().getTwoPlayerOutcome(),
            "Outcome should be TIE when both die");
    }

    @Test
    void stateTransition_gameRunningDuringPlay() {
        MockStopCallback mockHandle = new MockStopCallback();
        gameLogic.tick(mockHandle);
        
        assertTrue(gameLogic.isRunning(),
            "Game should be running during normal play");
        assertFalse(mockHandle.wasStopped(),
            "Game loop should not be stopped during play");
    }

    // ===== Score Persistence Integration Tests =====

    @Test
    void scorePersistence_player1WinIncreasesP1Score() {
        int initialP1Score = GameData.getInstance().getTwoPlayerP1Score();
        
        getPlayer2().setAlive(false);
        gameLogic.tick(new MockStopCallback());
        
        assertEquals(initialP1Score + 1, GameData.getInstance().getTwoPlayerP1Score(),
            "P1 score should increase by 1 after winning");
    }

    @Test
    void scorePersistence_player2WinIncreasesP2Score() {
        int initialP2Score = GameData.getInstance().getTwoPlayerP2Score();
        
        getPlayer1().setAlive(false);
        gameLogic.tick(new MockStopCallback());
        
        assertEquals(initialP2Score + 1, GameData.getInstance().getTwoPlayerP2Score(),
            "P2 score should increase by 1 after winning");
    }

    @Test
    void scorePersistence_tieDoesNotChangeScores() {
        int initialP1Score = GameData.getInstance().getTwoPlayerP1Score();
        int initialP2Score = GameData.getInstance().getTwoPlayerP2Score();
        
        getPlayer1().setAlive(false);
        getPlayer2().setAlive(false);
        gameLogic.tick(new MockStopCallback());
        
        assertEquals(initialP1Score, GameData.getInstance().getTwoPlayerP1Score(),
            "P1 score should not change on tie");
        assertEquals(initialP2Score, GameData.getInstance().getTwoPlayerP2Score(),
            "P2 score should not change on tie");
    }

    @Test
    void scorePersistence_scoresAccumulateAcrossMatches() {
        // Player 1 wins first match
        getPlayer2().setAlive(false);
        gameLogic.tick(new MockStopCallback());
        assertEquals(1, GameData.getInstance().getTwoPlayerP1Score());
        
        // Reset and play again
        gameLogic.reset();
        
        // Player one wins second match
        getPlayer2().setAlive(false);
        gameLogic.tick(new MockStopCallback());
        
        assertEquals(2, GameData.getInstance().getTwoPlayerP1Score(),
            "Score should accumulate across matches");
    }

    // ===== Reset Integration Tests =====

    @Test
    void reset_bothPlayersAreAlive() {
        getPlayer1().setAlive(false);
        getPlayer2().setAlive(false);
        
        gameLogic.reset();
        
        assertTrue(getPlayer1().getAlive(), 
            "Player 1 should be alive after reset");
        assertTrue(getPlayer2().getAlive(),
            "Player 2 should be alive after reset");
    }

    @Test
    void reset_gameIsRunning() {
        getPlayer1().setAlive(false);
        gameLogic.tick(new MockStopCallback());
        assertFalse(gameLogic.isRunning());
        
        gameLogic.reset();
        
        assertTrue(gameLogic.isRunning(),
            "Game should be running after reset");
    }

    @Test
    void reset_wallsAreReloaded() {
        gameLogic.reset();
        
        Wall[] walls = gameLogic.getWalls();
        assertNotNull(walls, "Walls should exist after reset");
        assertTrue(walls.length > 0, "Should have walls after reset");
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
