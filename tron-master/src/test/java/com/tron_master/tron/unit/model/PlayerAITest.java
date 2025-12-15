package com.tron_master.tron.unit.model;

import java.lang.reflect.Method;
import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.Test;

import com.tron_master.tron.model.data.ColorValue;
import com.tron_master.tron.model.data.LineSegment;
import com.tron_master.tron.model.object.PlayerAI;
import com.tron_master.tron.model.object.Wall;

/**
 * Unit tests for PlayerAI class.
 * Focuses on two critical aspects:
 * 1. Wall detection foundation (getWallLines)
 * 2. Random turn behavior (ensures AI is not a straight-line robot)
 */
@DisplayName("PlayerAI Tests")
class PlayerAITest {

    private static final ColorValue TEST_COLOR = new ColorValue(1.0, 0.0, 0.0);

    @Nested
    @DisplayName("getWallLines - Wall to LineSegment conversion")
    class GetWallLinesTest {

        private PlayerAI ai;

        @BeforeEach
        void setUp() {
            ai = new PlayerAI(250, 250, 3, 0, TEST_COLOR, "story");
            ai.setBounds(500, 500);
        }

        /**
         * Invokes the private getWallLines() method via reflection.
         */
        @SuppressWarnings("unchecked")
        private ArrayList<LineSegment> invokeGetWallLines(PlayerAI ai) throws Exception {
            Method method = PlayerAI.class.getDeclaredMethod("getWallLines");
            method.setAccessible(true);
            return (ArrayList<LineSegment>) method.invoke(ai);
        }

        @Test
        @DisplayName("Single wall converts to 4 edge LineSegments")
        void singleWall_convertsToFourEdges() throws Exception {
            // Wall at (100, 100) with size 50x20
            Wall wall = new Wall(100, 100, 50, 20, TEST_COLOR);
            ai.setWalls(new Wall[]{wall});

            ArrayList<LineSegment> lines = invokeGetWallLines(ai);

            assertEquals(4, lines.size(), "One wall should produce 4 edge lines");

            // Verify the edges exist (top, bottom, left, right)
            // Wall: x=100, y=100, width=50, height=20
            // Expected edges:
            // - Top:    (100,100) -> (150,100)
            // - Bottom: (100,120) -> (150,120)
            // - Left:   (100,100) -> (100,120)
            // - Right:  (150,100) -> (150,120)

            boolean hasTopEdge = lines.stream().anyMatch(l ->
                    l.getMinY() == 100 && l.getMaxY() == 100 && l.getMinX() == 100 && l.getMaxX() == 150);
            boolean hasBottomEdge = lines.stream().anyMatch(l ->
                    l.getMinY() == 120 && l.getMaxY() == 120 && l.getMinX() == 100 && l.getMaxX() == 150);
            boolean hasLeftEdge = lines.stream().anyMatch(l ->
                    l.getMinX() == 100 && l.getMaxX() == 100 && l.getMinY() == 100 && l.getMaxY() == 120);
            boolean hasRightEdge = lines.stream().anyMatch(l ->
                    l.getMinX() == 150 && l.getMaxX() == 150 && l.getMinY() == 100 && l.getMaxY() == 120);

            assertTrue(hasTopEdge, "Should have top edge");
            assertTrue(hasBottomEdge, "Should have bottom edge");
            assertTrue(hasLeftEdge, "Should have left edge");
            assertTrue(hasRightEdge, "Should have right edge");
        }

        @Test
        @DisplayName("Null walls array returns empty list")
        void nullWalls_returnsEmptyList() throws Exception {
            ai.setWalls(null);

            ArrayList<LineSegment> lines = invokeGetWallLines(ai);

            assertNotNull(lines);
            assertTrue(lines.isEmpty());
        }

        @Test
        @DisplayName("Empty walls array returns empty list")
        void emptyWalls_returnsEmptyList() throws Exception {
            ai.setWalls(new Wall[0]);

            ArrayList<LineSegment> lines = invokeGetWallLines(ai);

            assertNotNull(lines);
            assertTrue(lines.isEmpty());
        }
    }

    @Nested
    @DisplayName("Random Turn Behavior")
    class RandomTurnTest {

        @RepeatedTest(value = 5, name = "Run {currentRepetition}/{totalRepetitions}")
        @DisplayName("AI eventually changes direction (not a straight-line robot)")
        void randomTurn_eventuallyChangesDirection() {
            // AI at center, moving right, with plenty of space
            PlayerAI ai = new PlayerAI(100, 250, 3, 0, TEST_COLOR, "story");
            ai.setBounds(500, 500);
            ai.setWalls(new Wall[0]);

            int directionChanges = 0;
            int lastVelX = ai.getVelocityX();
            int lastVelY = ai.getVelocityY();

            // Run for 200 frames (40-frame timer means ~5 potential turn opportunities)
            for (int frame = 0; frame < 200; frame++) {
                ai.move();

                // Check if direction changed
                if (ai.getVelocityX() != lastVelX || ai.getVelocityY() != lastVelY) {
                    directionChanges++;
                    lastVelX = ai.getVelocityX();
                    lastVelY = ai.getVelocityY();
                }

                // Stop early if AI dies (hit boundary or own trail)
                if (!ai.getAlive()) {
                    break;
                }
            }

            // AI should change direction at least once in 200 frames
            // (either randomly or due to approaching boundary)
            // The key assertion: AI is not stuck going in one direction forever
            assertTrue(directionChanges >= 1,
                    "AI should change direction at least once in 200 frames, " +
                    "actual changes: " + directionChanges);
        }
    }
}
