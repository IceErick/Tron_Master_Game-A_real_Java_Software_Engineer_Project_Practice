package com.tron_master.tron.unit.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tron_master.tron.model.data.ColorValue;
import com.tron_master.tron.model.data.Intersection;
import com.tron_master.tron.model.object.PlayerHuman;
import com.tron_master.tron.view.utils.Line;

/**
 * Unit tests for GameObject collision detection (intersects method).
 * This is the core game mechanic - players die when colliding with trails.
 */
class GameObjectCollisionTest {

    private static final ColorValue TEST_COLOR = new ColorValue(1.0, 0.0, 0.0);
    private PlayerHuman player1;
    private PlayerHuman player2;

    @BeforeEach
    void setUp() {
        player1 = new PlayerHuman(100, 100, 3, 0, TEST_COLOR, "survival");
        player2 = new PlayerHuman(200, 200, 3, 0, TEST_COLOR, "survival");
        player1.setBounds(500, 500);
        player2.setBounds(500, 500);
    }

    // ===== Player-to-Player Body Collision =====

    @Test
    void intersects_returnsUpWhenPlayersOverlap() {
        // Place player2 at same position as player1
        player2 = new PlayerHuman(100, 100, 3, 0, TEST_COLOR, "survival");
        
        Intersection result = player1.intersects(player2);
        
        assertEquals(Intersection.UP, result);
    }

    @Test
    void intersects_returnsNoneWhenPlayersFarApart() {
        Intersection result = player1.intersects(player2);
        
        assertEquals(Intersection.NONE, result);
    }

    @Test
    void intersects_returnsNoneWhenComparingSameObject() {
        Intersection result = player1.intersects(player1);
        
        assertEquals(Intersection.NONE, result);
    }

    // ===== Player-to-Trail Collision (Horizontal Trail) =====

    @Test
    void intersects_detectsCollisionWithHorizontalTrail() {
        // Add horizontal trail to player2's path at player1's y position
        player2.getPath().add(new Line(50, 100, 150, 100)); // Horizontal line at y=100
        player2.getPath().add(new Line(150, 100, 150, 110)); // Second segment (needed because last segment is skipped)
        
        Intersection result = player1.intersects(player2);
        
        assertEquals(Intersection.UP, result);
    }

    @Test
    void intersects_noCollisionWhenAboveHorizontalTrail() {
        // Trail below player1
        player2.getPath().add(new Line(50, 200, 150, 200));
        player2.getPath().add(new Line(150, 200, 150, 210));
        
        Intersection result = player1.intersects(player2);
        
        assertEquals(Intersection.NONE, result);
    }

    // ===== Player-to-Trail Collision (Vertical Trail) =====

    @Test
    void intersects_detectsCollisionWithVerticalTrail() {
        // Add vertical trail to player2's path at player1's x position
        player2.getPath().add(new Line(100, 50, 100, 150)); // Vertical line at x=100
        player2.getPath().add(new Line(100, 150, 110, 150)); // Second segment
        
        Intersection result = player1.intersects(player2);
        
        assertEquals(Intersection.UP, result);
    }

    @Test
    void intersects_noCollisionWhenBesideVerticalTrail() {
        // Trail to the right of player1
        player2.getPath().add(new Line(200, 50, 200, 150));
        player2.getPath().add(new Line(200, 150, 210, 150));
        
        Intersection result = player1.intersects(player2);
        
        assertEquals(Intersection.NONE, result);
    }

    // ===== Edge Cases =====

    @Test
    void intersects_handlesEmptyPath() {
        // player2 has no trail
        assertTrue(player2.getPath().isEmpty());
        
        Intersection result = player1.intersects(player2);
        
        assertEquals(Intersection.NONE, result);
    }

    @Test
    void intersects_skipsLastTrailSegment() {
        // Only one segment - should be skipped (it's the current segment being drawn)
        player2.getPath().add(new Line(50, 100, 150, 100));
        
        // Position player1 to intersect this trail
        player1 = new PlayerHuman(100, 100, 3, 0, TEST_COLOR, "survival");
        
        Intersection result = player1.intersects(player2);
        
        // Should not collide because single segment is skipped
        assertEquals(Intersection.NONE, result);
    }

    @Test
    void intersects_detectsCollisionAtTrailEndpoint() {
        // Player exactly at the end of a trail segment
        player2.getPath().add(new Line(100, 50, 100, 100)); // Ends at player1's position
        player2.getPath().add(new Line(100, 100, 110, 100));
        
        Intersection result = player1.intersects(player2);
        
        assertEquals(Intersection.UP, result);
    }
}
