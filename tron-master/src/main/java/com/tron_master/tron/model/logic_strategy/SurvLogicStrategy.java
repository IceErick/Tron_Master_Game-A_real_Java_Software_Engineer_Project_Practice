package com.tron_master.tron.model.logic_strategy;

import java.util.Random;

import com.tron_master.tron.constant.GameConstant;
import com.tron_master.tron.controller.WallLayoutController;
import com.tron_master.tron.model.data.GameData;
import com.tron_master.tron.model.data.GameState;
import com.tron_master.tron.model.object.GameObjectFactory;
import com.tron_master.tron.model.object.PlayerHuman;
import com.tron_master.tron.model.object.Portal;
import com.tron_master.tron.model.object.Wall;
import com.tron_master.tron.model.sound.SoundManager;

/**
 * Game logic strategy for Survival mode.
 * Manages a single human player, scoring, and state transitions specific to Survival mode.
 * Walls are loaded from FXML with random gaps generated at runtime.
 * Portals allow the player to teleport across the game area.
 */
public class SurvLogicStrategy extends GameLogic {
    /** Reference to shared game data. */
    private final GameData gameData;
    /** Walls in the game area (loaded from FXML with random gaps). */
    private Wall[] walls;
    /** Portals in the game area (entrance-exit pairs). */
    private Portal[] portals;
    /** Random generator for spawn position. */
    private final Random spawnRandom = new Random();
    /** Record how many times player teleported. */
    private int portalRecord = 0;
    
    /** Center of the game area */
    private static final int CENTER_X = GameConstant.GAME_AREA_WIDTH / 2;
    private static final int CENTER_Y = GameConstant.GAME_AREA_HEIGHT / 2;
    
    /** Spawn distance from center (at corners) */
    private static final int SPAWN_RADIUS = 230;
    
    /** 
     * Fixed spawn points at 4 corners (relative to center).
     * Format: {offsetX, offsetY, directionX, directionY}
     * Directions point toward center to avoid hitting nearby boundaries.
     */
    private static final int[][] SPAWN_CONFIGS = {
        {-SPAWN_RADIUS, -SPAWN_RADIUS, 1, 0},  // Top-left: face right
        {SPAWN_RADIUS, -SPAWN_RADIUS, -1, 0},  // Top-right: face left
        {-SPAWN_RADIUS, SPAWN_RADIUS, 1, 0},   // Bottom-left: face right
        {SPAWN_RADIUS, SPAWN_RADIUS, -1, 0}    // Bottom-right: face left
    };

    /**
     * Constructs a Survival mode logic strategy and prepares the initial player and game data.
     * @param gameWidth playfield width
     * @param gameHeight playfield height
     */
    public SurvLogicStrategy(int gameWidth, int gameHeight) {
        super(1, gameWidth, gameHeight);
        gameData = GameData.getInstance();
        initializeWalls();
        initializePortals();
        initializePlayer();
        scores = 0;
        gameData.setSurvivalScore(scores);
        gameData.setSurvivalState(GameState.PLAYING);
        isGameRunning = true;
        gameData.resetSurvivalData();
    }

    /**
     * Initializes walls by loading from FXML with random gaps.
     */
    private void initializeWalls() {
        walls = WallLayoutController.loadSurvivalWalls();
    }

    /**
     * Initializes portals by loading from FXML.
     */
    private void initializePortals() {
        portals = WallLayoutController.loadSurvivalPortals();
    }

    /**
     * Initializes the single human player used in Survival mode.
     */
    private void initializePlayer() {
        int[] start = getSafeRandomStart();
        player = (PlayerHuman) GameObjectFactory.createHumanPlayer(
            start[0], start[1], start[2], start[3], colors[0], "survival");
        players[0] = player;
        player.addPlayers(players);
    }
    
    /**
     * Gets a random spawn position from one of the 4 corner positions.
     */
    private int[] getSafeRandomStart() {
        int[] config = SPAWN_CONFIGS[spawnRandom.nextInt(SPAWN_CONFIGS.length)];
        return new int[]{
            CENTER_X + config[0],
            CENTER_Y + config[1],
            config[2] * VELOCITY,
            config[3] * VELOCITY
        };
    }
    
    /**
     * Checks if player collides with any wall.
     */
    private void checkWallCollisions() {
        if (walls == null || player == null) return;
        for (Wall wall : walls) {
            if (wall != null && wall.collidesWithPlayer(player)) {
                player.setAlive(false);
                return;
            }
        }
    }

    /**
     * Checks if the player collides with any portal entrance and teleports them.
     * On successful teleport, walls are reloaded with new random gaps.
     */
    private void checkPortalCollisions() {
        if (portals == null || player == null || !player.getAlive()) return;
        for (Portal portal : portals) {
            if (portal != null && portal.isEntrance() && portal.handleCollision(player)) {
                // Play teleport sound effect
                SoundManager.getInstance().playSoundEffect("teleport");
                portalRecord++;
                scores += 50*portalRecord;
                // Reload walls with new random gaps on successful teleport
                initializeWalls();
                break; // Only one teleport per tick
            }
        }
    }

    @Override
    public void tick(Runnable stopCallback) {
        updateGame(players);
        checkPortalCollisions();
        checkWallCollisions();
        if (player.getAlive()) {
            scores++;
            gameData.setSurvivalScore(scores);
            isGameRunning = true;
            gameData.setSurvivalState(GameState.PLAYING);
        } else {
            stopCallback.run();
            isGameRunning = false;
            gameData.setSurvivalState(addScore());
        }
    }

    @Override
    public void reset() {
        scores = 0;
        portalRecord = 0;
        gameData.setSurvivalScore(scores);
        gameData.setSurvivalState(GameState.PLAYING);
        isGameRunning = true;
        gameData.resetSurvivalData();
        initializeWalls();
        initializePortals();
        initializePlayer();
    }

    @Override
    public GameState addScore() {
        gameData.setSurvivalScore(scores);
        return GameState.GAME_OVER;
    }

    /**
     * Get survival mode player.
     * @return survival mode player
     */
    public PlayerHuman getPlayer() { return player; }
    /**
     * Get current survival score.
     * @return current survival score
     */
    public int getScore() { return gameData.getSurvivalScore(); }
    /**
     * Check whether survival logic is running.
     * @return whether survival logic is running
     */
    public boolean isRunning() { return isGameRunning; }
    /**
     * Get remaining boosts.
     * @return remaining boosts
     */
    public int getBoostCount() { return gameData.getSurvivalBoost(); }
    /**
     * Get survival state.
     * @return survival state
     */
    public GameState getSurvivalState() { return gameData.getSurvivalState(); }
    /**
     * Get walls for rendering.
     * @return walls for rendering
     */
    public Wall[] getWalls() { return walls; }
    /**
     * Get portals for rendering.
     * @return portals for rendering
     */
    public Portal[] getPortals() { return portals; }
}
