package com.tron_master.tron.model.logic_strategy;

import com.tron_master.tron.controller.WallLayoutController;
import com.tron_master.tron.model.data.GameData;
import com.tron_master.tron.model.data.GameState;
import com.tron_master.tron.model.object.GameObjectFactory;
import com.tron_master.tron.model.object.Player;
import com.tron_master.tron.model.object.PlayerAI;
import com.tron_master.tron.model.object.PlayerHuman;
import com.tron_master.tron.model.object.Portal;
import com.tron_master.tron.model.object.Wall;
import com.tron_master.tron.model.sound.SoundManager;

/**
 * Game logic strategy for Story mode.
 * Handles player initialization, level progression, score calculation, and game state transitions for Story mode.
 */
public class StoryLogicStrategy extends GameLogic {
    /** Reference to game data singleton. */
    private final GameData gameData;
    /** Walls in the game area. */
    private Wall[] walls;
    /** Portals in the game area (entrance-exit pairs). */
    private Portal[] portals;
    /** Minimum safe distance from walls for player spawn. */
    private static final int SAFE_DISTANCE = 60;

    /**
     * Constructs a StoryLogicStrategy for Story mode.
     * Initializes players and game data for the current level.
     * @param p Number of players
     * @param gameWidth playfield width
     * @param gameHeight playfield height
     */
    public StoryLogicStrategy(int p, int gameWidth, int gameHeight) {
        super(p, gameWidth, gameHeight);
        gameData = GameData.getInstance();
        initializeWalls();    // Walls first, so players can avoid them
        initializePortals();  // Initialize portals for teleportation
        initializePlayers();  // Initialize players on construction
    }

    /**
     * Initializes walls for the story mode game area.
     * Loads wall layout from FXML file designed in SceneBuilder.
     */
    private void initializeWalls() {
        walls = WallLayoutController.loadStoryWalls();
    }

    /**
     * Initializes portals for the story mode game area.
     * Loads portal layout from FXML file designed in SceneBuilder.
     * Entrance portals teleport the player to the corresponding exit.
     */
    private void initializePortals() {
        portals = WallLayoutController.loadStoryPortals();
    }

    /**
     * Initializes all players for the current story level.
     * Creates one human player and the rest as AI players.
     * Uses safe spawn positions that avoid walls.
     */
    private void initializePlayers() {
        int[] start = getSafeRandomStart();
        player = (PlayerHuman) GameObjectFactory.createHumanPlayer(start[0], start[1], start[2], start[3], colors[0], "story");
        players[0] = player;

        for (int i = 1; i < players.length; i++) {
            start = getSafeRandomStart();
            players[i] = GameObjectFactory.createAIPlayer(start[0], start[1], start[2], start[3], colors[i % colors.length], "story");
            // Pass walls to AI players so they can detect and avoid them
            if (players[i] instanceof PlayerAI aiPlayer) {
                aiPlayer.setWalls(walls);
            }
        }
        for (Player p: players) {
            p.addPlayers(players);
        }
    }
    
    /**
     * Gets a random start position that is safe from walls.
     * Keeps trying until a safe position is found.
     * @return Array of [x, y, velX, velY]
     */
    private int[] getSafeRandomStart() {
        int[] start;
        int attempts = 0;
        int maxAttempts = 100;
        
        do {
            start = getRandomStart();
            attempts++;
        } while (!WallLayoutController.isPositionSafe(walls, start[0], start[1], SAFE_DISTANCE)
                 && attempts < maxAttempts);
        
        // If still not safe after max attempts, use a known safe position
        if (attempts >= maxAttempts) {
            System.out.println("Warning: Could not find safe spawn position, using fallback");
            start[0] = 280; // Center X
            start[1] = 250; // Center Y
        }
        
        return start;
    }

    /**
     * Advances the game state for each tick.
     * Handles win/lose logic and updates game state accordingly.
     * @param stopCallback Callback to stop the game loop if needed
     */
    @Override
    public void tick(Runnable stopCallback) {
        updateGame(players);
        
        // Check portal collisions first (teleport instead of death)
        checkPortalCollisions();
        
        // Check wall collisions for all players
        checkWallCollisions();
        
        if (!player.getAlive()) {
            stopCallback.run();
            isGameRunning = false;
            gameData.setStoryState(addScore());
        } else {
            int check = 0;
            for (Player k: players) {
                if (!k.getAlive()) {
                    check++;
                }
            }
            if (check == players.length - 1) {
                isGameRunning = false;
                stopCallback.run();
                gameData.setStoryState(addScore());
            } else {
                isGameRunning = true;
                gameData.setStoryState(GameState.PLAYING);
            }
        }
    }
    
    /**
     * Checks if any player (human or AI) collides with any portal entrance and teleports them.
     * All players can use portals in story mode.
     */
    private void checkPortalCollisions() {
        if (portals == null || players == null) {
            return;
        }
        for (Portal portal : portals) {
            if (portal != null && portal.isEntrance()) {
                // Check all players for portal collision
                for (Player p : players) {
                    if (p != null && p.getAlive() && portal.handleCollision(p)) {
                        SoundManager.getInstance().playSoundEffect("teleport");
                        // Grant bonus boost on teleport (only for human player)
                        if (p.isHuman()) {
                            p.addBoost(1); // Adds to both player's count and GameData
                        }
                        System.out.println("Player teleported!");
                        break; // Only one teleport per portal per tick
                    }
                }
            }
        }
    }

    /**
     * Checks if any player collides with walls and kills them if so.
     */
    private void checkWallCollisions() {
        if (walls == null) {
            return;
        }
        for (Wall wall : walls) {
            for (Player p : players) {
                if (p != null && p.getAlive() && wall.collidesWithPlayer(p)) {
                    p.setAlive(false);
                }
            }
        }
    }

    /**
     * Resets story mode data and players to their initial state.
     * Restarts the game from level 1.
     */
    @Override
    public void reset() {
        gameData.setStoryLevel(1);
        scores = 0;
        gameData.setStoryState(GameState.PLAYING);
        isGameRunning = true;
        gameData.resetStoryData();
        initializeWalls();    // Reload walls
        initializePortals();  // Reload portals
        initializePlayers();
    }

    /**
     * Adds score and determines game state after win/lose.
     * @return Updated GameState
     */
    @Override
    public GameState addScore() {
        if (player.getAlive()) {
            scores += 50 * (players.length - 1);
            gameData.setStoryScore(scores);
            // Check if current level is the final level (level 7)
            // Victory when completing level 7, otherwise level complete
            if (gameData.getStoryLevel() >= 7) {
                return GameState.VICTORY;
            } else {
                return GameState.LEVEL_COMPLETE;
            }
        } else {
            return GameState.GAME_OVER;
        }
    }

    /**
     * Advances to the next story level, updates score and state.
     * @return true if next level exists, false if finished
     */
    public boolean nextLevel() {
        int level = gameData.getStoryLevel();
        int currentScore = gameData.getStoryScore();
        if (level == 7) {
            gameData.setStoryState(GameState.VICTORY);
            return false;
        }
        int newLevel = level + 1;
        int newPlayerCount = newLevel + 1;
        gameData.setStoryLevel(newLevel);
        gameData.setStoryScore(currentScore);
        gameData.setStoryState(GameState.PLAYING);
        players = new Player[newPlayerCount];
        initializePlayers();
        return true;
    }


    /**
     * Returns all players for the current level.
     * @return Array of Player
     */
    public Player[] getPlayers() { return players; }

    /**
     * Returns the main human player.
     * @return PlayerHuman instance
     */
    public PlayerHuman getPlayer() { return player; }

    /**
     * Checks if the game is running.
     * @return true if running
     */
    public boolean isRunning() { return isGameRunning; }

    /**
     * Returns the current story mode score.
     * @return Score value
     */
    public int getStoryScore() { return gameData.getStoryScore(); }

    /**
     * Returns the current story mode level.
     * @return Level value
     */
    public int getStoryLevel() { return gameData.getStoryLevel(); }

    /**
     * Returns the current boost count.
     * @return Boost count
     */
    public int getBoostCount() { return gameData.getStoryBoost(); }

    /**
     * Set current boost count for story mode.
     * @param boostCount boosts remaining
     */
    public void setBoostCount(int boostCount) { gameData.setStoryBoost(boostCount); }

    /**
     * Returns the current story mode game state.
     * @return GameState
     */
    public GameState getStoryState() { return gameData.getStoryState(); }

    /**
     * Gets all walls in the game area.
     * @return Array of walls
     */
    public Wall[] getWalls() { return walls; }

    /**
     * Gets all portals in the game area.
     * @return Array of portals (entrances and exits)
     */
    public Portal[] getPortals() { return portals; }
    
    /**
     * Returns the object count for the current level.
     * Business rule: level N has N+1 players (level 1 = 2 players, level 2 = 3 players, etc.)
     * @return Number of players for current level
     */
    public int getCurrentLevelPlayerCount() {
        return gameData.getStoryLevel() + 1;
    }
    
    /**
     * Returns the initial object count for story mode (first level).
     * @return Initial number of players (2 for level 1)
     */
    public static int getInitialPlayerCount() {
        return 2; // First level always starts with 2 players
    }
}
