package com.tron_master.tron.unit.controller;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.tron_master.tron.controller.WallLayoutController;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import com.tron_master.tron.model.data.ColorValue;
import com.tron_master.tron.model.object.Wall;

/**
 * Unit tests for WallLayoutController.
 * Tests the isPositionSafe method which checks if a position is safe from walls.
 */
@DisplayName("WallLayoutController Tests")
class WallLayoutControllerTest {

    private static final ColorValue TEST_COLOR = new ColorValue(0, 255, 255);

    @Nested
    @DisplayName("isPositionSafe - Position Outside Walls")
    class PositionOutsideWalls {
        
        @Test
        @DisplayName("Should return true when position is far from wall")
        void shouldReturnTrueWhenPositionFarFromWall() {
            // Wall at (100, 100) with size 50x20
            Wall wall = new Wall(100, 100, 50, 20, TEST_COLOR);
            Wall[] walls = new Wall[]{wall};
            
            // Position at (0, 0) is far from wall
            assertTrue(WallLayoutController.isPositionSafe(walls, 0, 0, 10));
        }
        
        @Test
        @DisplayName("Should return true when position is just outside safe distance")
        void shouldReturnTrueWhenJustOutsideSafeDistance() {
            // Wall at (100, 100) with size 50x20
            Wall wall = new Wall(100, 100, 50, 20, TEST_COLOR);
            Wall[] walls = new Wall[]{wall};
            
            int safeDistance = 10;
            // Position at (100 - 10 - 1, 110) = (89, 110) should be safe
            assertTrue(WallLayoutController.isPositionSafe(walls, 89, 110, safeDistance));
        }
    }

    @Nested
    @DisplayName("isPositionSafe - Position Inside Wall Area")
    class PositionInsideWallArea {
        
        @Test
        @DisplayName("Should return false when position is inside wall bounds")
        void shouldReturnFalseWhenInsideWall() {
            // Wall at (100, 100) with size 50x20
            Wall wall = new Wall(100, 100, 50, 20, TEST_COLOR);
            Wall[] walls = new Wall[]{wall};
            
            // Position at center of wall (125, 110)
            assertFalse(WallLayoutController.isPositionSafe(walls, 125, 110, 0));
        }
        
        @Test
        @DisplayName("Should return false when position is within safe distance of wall")
        void shouldReturnFalseWhenWithinSafeDistance() {
            // Wall at (100, 100) with size 50x20
            Wall wall = new Wall(100, 100, 50, 20, TEST_COLOR);
            Wall[] walls = new Wall[]{wall};
            
            int safeDistance = 10;
            // Position at (95, 110) is within safe distance (wall left edge is 100, safe zone starts at 90)
            assertFalse(WallLayoutController.isPositionSafe(walls, 95, 110, safeDistance));
        }
        
        @Test
        @DisplayName("Should return false when position is on safe distance boundary")
        void shouldReturnFalseWhenOnSafeDistanceBoundary() {
            // Wall at (100, 100) with size 50x20
            Wall wall = new Wall(100, 100, 50, 20, TEST_COLOR);
            Wall[] walls = new Wall[]{wall};
            
            int safeDistance = 10;
            // Position at (90, 110) is exactly on left boundary
            assertFalse(WallLayoutController.isPositionSafe(walls, 90, 110, safeDistance));
        }
    }

    @Nested
    @DisplayName("isPositionSafe - Multiple Walls")
    class MultipleWalls {
        
        @Test
        @DisplayName("Should return false if position is near any wall")
        void shouldReturnFalseIfNearAnyWall() {
            Wall wall1 = new Wall(0, 0, 50, 20, TEST_COLOR);
            Wall wall2 = new Wall(200, 200, 50, 20, TEST_COLOR);
            Wall[] walls = new Wall[]{wall1, wall2};
            
            // Position is safe from wall1 but inside wall2
            assertFalse(WallLayoutController.isPositionSafe(walls, 210, 210, 5));
        }
        
        @Test
        @DisplayName("Should return true only if position is safe from all walls")
        void shouldReturnTrueOnlyIfSafeFromAllWalls() {
            Wall wall1 = new Wall(0, 0, 50, 20, TEST_COLOR);
            Wall wall2 = new Wall(200, 200, 50, 20, TEST_COLOR);
            Wall[] walls = new Wall[]{wall1, wall2};
            
            // Position at (100, 100) is far from both walls
            assertTrue(WallLayoutController.isPositionSafe(walls, 100, 100, 10));
        }
    }

    @Nested
    @DisplayName("isPositionSafe - Edge Cases")
    class EdgeCases {
        
        @Test
        @DisplayName("Should work with zero safe distance")
        void shouldWorkWithZeroSafeDistance() {
            Wall wall = new Wall(100, 100, 50, 20, TEST_COLOR);
            Wall[] walls = new Wall[]{wall};
            
            // Position just outside wall (99, 110) with 0 safe distance
            assertTrue(WallLayoutController.isPositionSafe(walls, 99, 110, 0));
            // Position on wall edge (100, 110) with 0 safe distance
            assertFalse(WallLayoutController.isPositionSafe(walls, 100, 110, 0));
        }
        
        @Test
        @DisplayName("Should handle large safe distance")
        void shouldHandleLargeSafeDistance() {
            Wall wall = new Wall(100, 100, 10, 10, TEST_COLOR);
            Wall[] walls = new Wall[]{wall};
            
            // With large safe distance (50), even far positions are unsafe
            assertFalse(WallLayoutController.isPositionSafe(walls, 60, 105, 50));
            assertTrue(WallLayoutController.isPositionSafe(walls, 40, 105, 50));
        }
    }
}
