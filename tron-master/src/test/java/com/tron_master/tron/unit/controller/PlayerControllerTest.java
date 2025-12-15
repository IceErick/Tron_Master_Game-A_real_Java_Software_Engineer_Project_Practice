package com.tron_master.tron.unit.controller;

import java.lang.reflect.Method;

import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.fail;

import com.tron_master.tron.controller.PlayerController;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.tron_master.tron.model.data.ColorValue;
import com.tron_master.tron.model.object.Player;
import com.tron_master.tron.model.object.PlayerAI;
import com.tron_master.tron.model.object.PlayerHuman;

import javafx.scene.input.KeyCode;

/**
 * Unit tests for PlayerController.
 * Tests the key-to-player mapping logic (getPlayerForKey method).
 * Current key mapping (after swap):
 * - Player 1: Arrow keys (UP/DOWN/LEFT/RIGHT) + SPACE + B
 * - Player 2: WASD + Q + DIGIT1
 */
@DisplayName("PlayerController Tests")
class PlayerControllerTest {

    private static final ColorValue TEST_COLOR = new ColorValue(255, 0, 0);
    private static final String TEST_MODE = "survival";
    
    /**
     * Helper method to invoke private getPlayerForKey via reflection.
     */
    private PlayerHuman invokeGetPlayerForKey(PlayerController controller, KeyCode keyCode) {
        try {
            Method method = PlayerController.class.getDeclaredMethod("getPlayerForKey", KeyCode.class);
            method.setAccessible(true);
            return (PlayerHuman) method.invoke(controller, keyCode);
        } catch (Exception e) {
            fail("Reflection failed: " + e.getMessage());
            return null;
        }
    }

    @Nested
    @DisplayName("Empty Player List")
    class EmptyPlayerList {
        
        private PlayerController controller;
        
        @BeforeEach
        void setUp() {
            controller = new PlayerController(new Player[0]);
        }
        
        @Test
        @DisplayName("Should return null for any key when no players exist")
        void shouldReturnNullWhenNoPlayers() {
            assertNull(invokeGetPlayerForKey(controller, KeyCode.UP));
            assertNull(invokeGetPlayerForKey(controller, KeyCode.W));
            assertNull(invokeGetPlayerForKey(controller, KeyCode.SPACE));
            assertNull(invokeGetPlayerForKey(controller, KeyCode.Q));
        }
    }

    @Nested
    @DisplayName("Single Human Player Mode")
    class SingleHumanPlayerMode {
        
        private PlayerController controller;
        private PlayerHuman player1;
        
        @BeforeEach
        void setUp() {
            player1 = new PlayerHuman(100, 100, 0, -3, TEST_COLOR, TEST_MODE);
            controller = new PlayerController(new Player[]{player1});
        }
        
        @Test
        @DisplayName("Player 1 keys (Arrow keys) should map to player 1")
        void player1KeysShouldMapToPlayer1() {
            assertSame(player1, invokeGetPlayerForKey(controller, KeyCode.UP));
            assertSame(player1, invokeGetPlayerForKey(controller, KeyCode.DOWN));
            assertSame(player1, invokeGetPlayerForKey(controller, KeyCode.LEFT));
            assertSame(player1, invokeGetPlayerForKey(controller, KeyCode.RIGHT));
            assertSame(player1, invokeGetPlayerForKey(controller, KeyCode.SPACE));
            assertSame(player1, invokeGetPlayerForKey(controller, KeyCode.B));
        }
        
        @Test
        @DisplayName("Player 2 keys (WASD) should fallback to player 1 in single player mode")
        void player2KeysShouldFallbackToPlayer1() {
            // In single player mode, player 2 keys should map to player 1
            assertSame(player1, invokeGetPlayerForKey(controller, KeyCode.W));
            assertSame(player1, invokeGetPlayerForKey(controller, KeyCode.A));
            assertSame(player1, invokeGetPlayerForKey(controller, KeyCode.S));
            assertSame(player1, invokeGetPlayerForKey(controller, KeyCode.D));
            assertSame(player1, invokeGetPlayerForKey(controller, KeyCode.Q));
            assertSame(player1, invokeGetPlayerForKey(controller, KeyCode.DIGIT1));
        }
    }

    @Nested
    @DisplayName("Two Human Players Mode")
    class TwoHumanPlayersMode {
        
        private PlayerController controller;
        private PlayerHuman player1;
        private PlayerHuman player2;
        
        @BeforeEach
        void setUp() {
            player1 = new PlayerHuman(100, 100, 0, -3, TEST_COLOR, TEST_MODE);
            player2 = new PlayerHuman(200, 200, 0, -3, TEST_COLOR, TEST_MODE);
            controller = new PlayerController(new Player[]{player1, player2});
        }
        
        @Test
        @DisplayName("Player 1 movement keys (Arrow keys) should map to player 1")
        void player1MovementKeysShouldMapToPlayer1() {
            assertSame(player1, invokeGetPlayerForKey(controller, KeyCode.UP));
            assertSame(player1, invokeGetPlayerForKey(controller, KeyCode.DOWN));
            assertSame(player1, invokeGetPlayerForKey(controller, KeyCode.LEFT));
            assertSame(player1, invokeGetPlayerForKey(controller, KeyCode.RIGHT));
        }
        
        @Test
        @DisplayName("Player 1 action keys (SPACE, B) should map to player 1")
        void player1ActionKeysShouldMapToPlayer1() {
            assertSame(player1, invokeGetPlayerForKey(controller, KeyCode.SPACE));
            assertSame(player1, invokeGetPlayerForKey(controller, KeyCode.B));
        }
        
        @Test
        @DisplayName("Player 2 movement keys (WASD) should map to player 2")
        void player2MovementKeysShouldMapToPlayer2() {
            assertSame(player2, invokeGetPlayerForKey(controller, KeyCode.W));
            assertSame(player2, invokeGetPlayerForKey(controller, KeyCode.A));
            assertSame(player2, invokeGetPlayerForKey(controller, KeyCode.S));
            assertSame(player2, invokeGetPlayerForKey(controller, KeyCode.D));
        }
        
        @Test
        @DisplayName("Player 2 action keys (Q, DIGIT1) should map to player 2")
        void player2ActionKeysShouldMapToPlayer2() {
            assertSame(player2, invokeGetPlayerForKey(controller, KeyCode.Q));
            assertSame(player2, invokeGetPlayerForKey(controller, KeyCode.DIGIT1));
        }
    }

    @Nested
    @DisplayName("Mixed Player Types (Human + AI)")
    class MixedPlayerTypes {
        
        private PlayerController controller;
        private PlayerHuman humanPlayer;
        
        @BeforeEach
        void setUp() {
            humanPlayer = new PlayerHuman(100, 100, 0, -3, TEST_COLOR, TEST_MODE);
            PlayerAI aiPlayer = new PlayerAI(200, 200, 0, -3, TEST_COLOR, TEST_MODE);
            // AI player should be ignored, only human players are controllable
            controller = new PlayerController(new Player[]{humanPlayer, aiPlayer});
        }
        
        @Test
        @DisplayName("Should only recognize human players for key mapping")
        void shouldOnlyRecognizeHumanPlayers() {
            // All keys should map to the only human player
            assertSame(humanPlayer, invokeGetPlayerForKey(controller, KeyCode.UP));
            assertSame(humanPlayer, invokeGetPlayerForKey(controller, KeyCode.W));
        }
    }

    @Nested
    @DisplayName("Invalid Keys")
    class InvalidKeys {
        
        private PlayerController controller;
        
        @BeforeEach
        void setUp() {
            PlayerHuman player = new PlayerHuman(100, 100, 0, -3, TEST_COLOR, TEST_MODE);
            controller = new PlayerController(new Player[]{player});
        }
        
        @Test
        @DisplayName("Unrecognized keys should return null")
        void unrecognizedKeysShouldReturnNull() {
            assertNull(invokeGetPlayerForKey(controller, KeyCode.F));
            assertNull(invokeGetPlayerForKey(controller, KeyCode.ENTER));
            assertNull(invokeGetPlayerForKey(controller, KeyCode.ESCAPE));
            assertNull(invokeGetPlayerForKey(controller, KeyCode.TAB));
        }
    }
}
