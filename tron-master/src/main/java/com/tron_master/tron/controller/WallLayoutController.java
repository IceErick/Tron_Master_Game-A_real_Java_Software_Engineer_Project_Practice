package com.tron_master.tron.controller;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;

import com.tron_master.tron.model.data.ColorValue;
import com.tron_master.tron.model.object.GameObjectFactory;
import com.tron_master.tron.model.object.Portal;
import com.tron_master.tron.model.object.Wall;

import javafx.fxml.FXMLLoader;
import javafx.scene.layout.Pane;
import javafx.scene.shape.Rectangle;

/**
 * Loads wall and portal layouts from FXML files designed in SceneBuilder.
 * FXML naming conventions:
 * - Walls: Any Rectangle without "portal_" prefix in fx:id
 * - Portal entrances: fx:id starting with "portal_entrance_" (e.g., portal_entrance_1)
 * - Portal exits: fx:id starting with "portal_exit_" (e.g., portal_exit_1)
 * Entrance and exit portals with matching suffix numbers are automatically linked.
 * Survival mode walls are loaded with random gaps cut into each wall segment.
 */
public class WallLayoutController {

    /** Default constructor (utility class). */
    public WallLayoutController() {}

    private static final String TWO_PLAYER_FXML = "/com/tron_master/tron/fxml/custom_walls/two_player_walls.fxml";
    private static final String SURVIVAL_FXML = "/com/tron_master/tron/fxml/custom_walls/survival_walls.fxml";
    private static final String STORY_FXML = "/com/tron_master/tron/fxml/custom_walls/story_walls.fxml";

    /** Minimum gap size for survival walls */
    private static final int MIN_GAP_SIZE = 25;
    /** Maximum gap size for survival walls */
    private static final int MAX_GAP_SIZE = 45;
    /** Random generator for gap placement */
    private static final Random random = new Random();

    // ==================== Public API ====================

    /**
     * Load walls for two-player mode.
     * @return walls for two-player mode.
     */
    public static Wall[] loadTwoPlayerWalls() { return loadWalls(TWO_PLAYER_FXML); }
    /**
     * Load walls for story mode.
     * @return walls for story mode.
     */
    public static Wall[] loadStoryWalls()     { return loadWalls(STORY_FXML); }
    /**
     * Load walls for survival mode (with gaps).
     * @return walls for survival mode (with gaps).
     */
    public static Wall[] loadSurvivalWalls()  { return loadWallsWithGaps(SURVIVAL_FXML); }
    /**
     * Load portals for story mode.
     * @return portals for story mode.
     */
    public static Portal[] loadStoryPortals() { return loadPortals(STORY_FXML); }
    /**
     * Load portals for survival mode.
     * @return portals for survival mode.
     */
    public static Portal[] loadSurvivalPortals() { return loadPortals(SURVIVAL_FXML); }


    /**
     * Checks if a position is safe (not too close to any wall).
     * @param walls walls to test against
     * @param x x coordinate
     * @param y y coordinate
     * @param safeDistance padding distance from walls
     * @return true if position is clear
     */
    public static boolean isPositionSafe(Wall[] walls, int x, int y, int safeDistance) {
        if (walls == null) return true;
        for (Wall wall : walls) {
            if (wall != null && 
                x >= wall.getX() - safeDistance && x <= wall.getX() + wall.getWidth() + safeDistance &&
                y >= wall.getY() - safeDistance && y <= wall.getY() + wall.getHeight() + safeDistance) {
                return false;
            }
        }
        return true;
    }

    // ==================== Internal Loading ====================

    private static Wall[] loadWalls(String fxmlPath) {
        Pane root = loadFxml(fxmlPath);
        if (root == null) return new Wall[0];

        List<Wall> walls = new ArrayList<>();
        root.getChildren().stream()
            .filter(node -> node instanceof Rectangle rect && !isPortal(rect.getId()))
            .map(node -> toWall((Rectangle) node))
            .forEach(walls::add);

        return walls.toArray(Wall[]::new);
    }

    /**
     * Loads walls from FXML and cuts a random gap into each wall segment.
     * Horizontal walls (width > height) get horizontal gaps.
     * Vertical walls (height > width) get vertical gaps.
     */
    private static Wall[] loadWallsWithGaps(String fxmlPath) {
        Pane root = loadFxml(fxmlPath);
        if (root == null) return new Wall[0];

        List<Wall> walls = new ArrayList<>();
        root.getChildren().stream()
            .filter(node -> node instanceof Rectangle rect && !isPortal(rect.getId()))
            .map(node -> (Rectangle) node)
            .forEach(rect -> splitWallWithGap(rect, walls));

        return walls.toArray(Wall[]::new);
    }

    /**
     * Splits a single wall rectangle into two segments with a random gap.
     * @param rect Original wall rectangle from FXML
     * @param walls List to add resulting wall segments to
     */
    private static void splitWallWithGap(Rectangle rect, List<Wall> walls) {
        int x = (int) rect.getLayoutX();
        int y = (int) rect.getLayoutY();
        int width = (int) rect.getWidth();
        int height = (int) rect.getHeight();
        ColorValue color = "magenta".equals(rect.getUserData()) ? Wall.NEON_MAGENTA : Wall.NEON_CYAN;

        boolean isHorizontal = width > height;
        int length = isHorizontal ? width : height;

        // If wall is too short for a gap, add it as-is
        if (length < MIN_GAP_SIZE * 2 + MIN_GAP_SIZE) {
            walls.add(GameObjectFactory.createWall(x, y, width, height, color));
            return;
        }

        // Calculate random gap position and size
        int maxGapStart = length - MIN_GAP_SIZE - MAX_GAP_SIZE;
        int gapStart = MIN_GAP_SIZE + (maxGapStart > 0 ? random.nextInt(maxGapStart) : 0);
        int gapSize = MIN_GAP_SIZE + random.nextInt(MAX_GAP_SIZE - MIN_GAP_SIZE);

        if (isHorizontal) {
            // Horizontal wall: split left and right of gap
            if (gapStart > 0) {
                walls.add(GameObjectFactory.createWall(x, y, gapStart, height, color));
            }
            int afterGapX = x + gapStart + gapSize;
            int afterGapWidth = width - gapStart - gapSize;
            if (afterGapWidth > 0) {
                walls.add(GameObjectFactory.createWall(afterGapX, y, afterGapWidth, height, color));
            }
        } else {
            // Vertical wall: split top and bottom of gap
            if (gapStart > 0) {
                walls.add(GameObjectFactory.createWall(x, y, width, gapStart, color));
            }
            int afterGapY = y + gapStart + gapSize;
            int afterGapHeight = height - gapStart - gapSize;
            if (afterGapHeight > 0) {
                walls.add(GameObjectFactory.createWall(x, afterGapY, width, afterGapHeight, color));
            }
        }
    }

    private static Portal[] loadPortals(String fxmlPath) {
        Pane root = loadFxml(fxmlPath);
        if (root == null) return new Portal[0];

        Map<String, Portal> entrances = new HashMap<>();
        Map<String, Portal> exits = new HashMap<>();

        root.getChildren().stream()
            .filter(node -> node instanceof Rectangle)
            .map(node -> (Rectangle) node)
            .forEach(rect -> {
                String id = rect.getId();
                if (id == null) return;
                if (id.startsWith("portal_entrance_")) {
                    entrances.put(id.substring(16), toPortal(rect, true));
                } else if (id.startsWith("portal_exit_")) {
                    exits.put(id.substring(12), toPortal(rect, false));
                }
            });

        // Link matching entrance-exit pairs
        entrances.forEach((key, entrance) -> {
            Portal exit = exits.get(key);
            if (exit != null) entrance.linkToExit(exit);
        });

        List<Portal> all = new ArrayList<>(entrances.values());
        all.addAll(exits.values());
        return all.toArray(Portal[]::new);
    }

    private static Pane loadFxml(String path) {
        try {
            return new FXMLLoader(WallLayoutController.class.getResource(path)).load();
        } catch (IOException e) {
            System.err.println("Failed to load FXML: " + path);
            return null;
        }
    }

    private static boolean isPortal(String id) {
        return id != null && id.startsWith("portal_");
    }

    private static Wall toWall(Rectangle rect) {
        ColorValue color = "magenta".equals(rect.getUserData()) ? Wall.NEON_MAGENTA : Wall.NEON_CYAN;
        return GameObjectFactory.createWall(
            (int) rect.getLayoutX(), (int) rect.getLayoutY(),
            (int) rect.getWidth(), (int) rect.getHeight(), color);
    }

    private static Portal toPortal(Rectangle rect, boolean isEntrance) {
        int x = (int) rect.getLayoutX(), y = (int) rect.getLayoutY();
        int w = (int) rect.getWidth(), h = (int) rect.getHeight();
        return isEntrance ? GameObjectFactory.createPortalEntrance(x, y, w, h)
                          : GameObjectFactory.createPortalExit(x, y, w, h);
    }
}
