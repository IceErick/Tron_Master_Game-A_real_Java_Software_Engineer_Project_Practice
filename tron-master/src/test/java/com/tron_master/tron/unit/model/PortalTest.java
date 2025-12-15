package com.tron_master.tron.unit.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tron_master.tron.model.data.ColorValue;
import com.tron_master.tron.model.object.PlayerHuman;
import com.tron_master.tron.model.object.Portal;

/**
 * Unit tests for Portal class.
 * Tests portal creation, linking, and teleportation functionality.
 */
class PortalTest {

    private static final ColorValue TEST_COLOR = new ColorValue(1.0, 0.0, 0.0);
    private Portal entrance;
    private Portal exit;
    private PlayerHuman player;

    @BeforeEach
    void setUp() {
        entrance = new Portal(100, 100, 20, 50, true);
        exit = new Portal(300, 300, 20, 50, false);
        player = new PlayerHuman(100, 100, 3, 0, TEST_COLOR, "survival");
        player.setBounds(500, 500);
    }

    // ===== linkToExit Tests =====

    @Test
    void linkToExit_successfullyLinksEntranceToExit() {
        entrance.linkToExit(exit);
        
        // Verify by attempting teleport (would fail if not linked)
        player.setX(entrance.getX() + 5);
        player.setY(entrance.getY() + 5);
        assertTrue(entrance.handleCollision(player));
    }

    @Test
    void linkToExit_throwsExceptionWhenExitTriesToLink() {
        assertThrows(IllegalStateException.class, () -> exit.linkToExit(entrance));
    }

    @Test
    void linkToExit_throwsExceptionWhenLinkingEntranceToEntrance() {
        Portal anotherEntrance = new Portal(200, 200, 20, 50, true);
        
        assertThrows(IllegalArgumentException.class, () -> entrance.linkToExit(anotherEntrance));
    }

    // ===== teleport Tests =====

    @Test
    void teleport_movesPlayerToExitPosition() {
        entrance.linkToExit(exit);
        
        boolean result = entrance.teleport(player);
        
        assertTrue(result);
        // Player should be near exit center (with offset applied)
        int expectedX = exit.getX() + exit.getWidth() / 2 + 7; // EXIT_OFFSET_X = 7
        assertEquals(expectedX, player.getX());
    }

    // ===== handleCollision Tests =====

    @Test
    void handleCollision_teleportsWhenPlayerCollidesWithEntrance() {
        entrance.linkToExit(exit);
        // Place player inside entrance portal
        player.setX(entrance.getX() + 5);
        player.setY(entrance.getY() + 5);
        
        boolean result = entrance.handleCollision(player);
        
        assertTrue(result);
    }

    @Test
    void handleCollision_returnsFalseWhenNoCollision() {
        entrance.linkToExit(exit);
        // Player is far from entrance
        player.setX(500);
        player.setY(500);
        
        boolean result = entrance.handleCollision(player);
        
        assertFalse(result);
    }
}