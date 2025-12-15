package com.tron_master.tron.model.logic_strategy;

import com.tron_master.tron.controller.WallLayoutController;
import com.tron_master.tron.model.data.GameData;
import com.tron_master.tron.model.data.GameState;
import com.tron_master.tron.model.data.TwoPlayerOutcome;
import com.tron_master.tron.model.object.GameObjectFactory;
import com.tron_master.tron.model.object.Player;
import com.tron_master.tron.model.object.PlayerHuman;
import com.tron_master.tron.model.object.Wall;

/**
 * Game logic strategy for Two-Player mode.
 * Manages two human players, per-player scoring, boost counts, walls, and state transitions for head-to-head play.
 */
public class TwoPlayerLogicStrategy extends GameLogic {
    /** Reference to shared game data. */
    private final GameData gameData;
    /** First human player (player slot 1). */
    private PlayerHuman player1;
    /** Second human player (player slot 2). */
    private PlayerHuman player2;
    /** Walls in the game area. */
    private Wall[] walls;
    
    /** Slower velocity for two-player mode (more strategic gameplay) */
    private static final int TWO_PLAYER_VELOCITY = 3;

    /**
     * Constructs the two-player logic strategy and prepares initial players and game data.
     * @param gameWidth playfield width
     * @param gameHeight playfield height
     */
    public TwoPlayerLogicStrategy(int gameWidth, int gameHeight) {
        super(2, gameWidth, gameHeight); // Two human players
        VELOCITY = TWO_PLAYER_VELOCITY;  // Override parent's velocity for slower movement
        gameData = GameData.getInstance();
        // Initialize data directly instead of calling overridable reset() from constructor
        gameData.resetTwoPlayerData();
        gameData.resetTwoPlayerScore();
        isGameRunning = true; // use inherited field from GameLogic
        initializeWalls();    // Walls first, so players can avoid them
        initializePlayers();
    }

    /**
     * Initializes both human players used in two-object matches and assigns object slots.
     * Uses fixed spawn points in safe areas (top and bottom gaps).
     */
    private void initializePlayers() {
        // Fixed spawn points in safe areas
        // Player 1: Top area (between corner walls), moving down
        int[] start1 = {280, 50, 0, TWO_PLAYER_VELOCITY};
        // Player 2: Bottom area (between corner walls), moving up  
        int[] start2 = {280, 450, 0, -TWO_PLAYER_VELOCITY};
        
        player1 = (PlayerHuman) GameObjectFactory.createHumanPlayer(start1[0], start1[1], start1[2], start1[3], colors[0], "twoPlayer");
        player2 = (PlayerHuman) GameObjectFactory.createHumanPlayer(start2[0], start2[1], start2[2], start2[3], colors[1], "twoPlayer");
        player1.setPlayerSlot(1);
        player2.setPlayerSlot(2);
        players[0] = player1;
        players[1] = player2;
        for (Player p : players) {
            p.addPlayers(players);
        }
    }

    /**
     * Initializes walls for the two-player game area.
     * Loads wall layout from FXML file designed in SceneBuilder.
     */
    private void initializeWalls() {
        walls = WallLayoutController.loadTwoPlayerWalls();
    }

    /**
     * Advances the two-player game state for each animation tick.
     * Updates players, checks wall collisions, detects match end, and records the outcome and score.
     * @param stopCallback callback to stop the game loop if needed
     */
    @Override
    public void tick(Runnable stopCallback) {
        updateGame(players);
        
        // Check wall collisions
        checkWallCollisions();
        
        boolean p1Alive = player1.getAlive();
        boolean p2Alive = player2.getAlive();
        if (!p1Alive || !p2Alive) {
            stopCallback.run();
            isGameRunning = false;
            gameData.setTwoPlayerState(GameState.GAME_OVER);
            gameData.setTwoPlayerOutcome(determineOutcome(p1Alive, p2Alive));
        } else {
            isGameRunning = true;
            gameData.setTwoPlayerState(GameState.PLAYING);
            gameData.setTwoPlayerOutcome(TwoPlayerOutcome.TIE);
        }
    }

    /**
     * Determines the match outcome based on player aliveness and updates per-player score.
     * @param p1Alive whether player 1 is alive
     * @param p2Alive whether player 2 is alive
     * @return the computed TwoPlayerOutcome
     */
    private TwoPlayerOutcome determineOutcome(boolean p1Alive, boolean p2Alive) {
        if (p1Alive && !p2Alive) {
            gameData.setTwoPlayerP1Score(gameData.getTwoPlayerP1Score() + 1);
            return TwoPlayerOutcome.P1_WIN;
        } else if (!p1Alive && p2Alive) {
            gameData.setTwoPlayerP2Score(gameData.getTwoPlayerP2Score() + 1);
            return TwoPlayerOutcome.P2_WIN;
        }
        return TwoPlayerOutcome.TIE;
    }

    /**
     * Checks if any player collides with walls and kills them if so.
     */
    private void checkWallCollisions() {
        if (walls == null) {
            return;
        }
        for (Wall wall : walls) {
            if (wall.collidesWithPlayer(player1)) {
                player1.setAlive(false);
            }
            if (wall.collidesWithPlayer(player2)) {
                player2.setAlive(false);
            }
        }
    }

    /**
     * Resets two-player match data and reinitializes players and walls for a fresh match.
     */
    @Override
    public void reset() {
        gameData.resetTwoPlayerData();
        isGameRunning = true;
        initializeWalls();    // Walls first, so players can avoid them
        initializePlayers();
    }

    /**
     * Finalizes scoring for the match. Two-player mode tracks per-player scores rather than levels.
     * @return GAME_OVER for match end
     */
    @Override
    public GameState addScore() {
        return GameState.GAME_OVER;
    }

    /**
     * Get both players.
     * @return both players
     */
    public Player[] getPlayers() { return players; }
    /**
     * Get second player reference.
     * @return second player
     */
    public PlayerHuman getPlayer2() { return player2; }
    /**
     * Check whether logic is running.
     * @return whether logic is running
     */
    public boolean isRunning() { return isGameRunning; }
    /**
     * Get player1 score.
     * @return player1 score
     */
    public int getP1Score() { return gameData.getTwoPlayerP1Score(); }
    /**
     * Get player2 score.
     * @return player2 score
     */
    public int getP2Score() { return gameData.getTwoPlayerP2Score(); }
    /**
     * Get player1 remaining boosts.
     * @return player1 remaining boosts
     */
    public int getP1BoostCount() { return gameData.getPlayer1Boost(); }
    /**
     * Get player2 remaining boosts.
     * @return player2 remaining boosts
     */
    public int getP2BoostCount() { return gameData.getPlayer2Boost(); }
    /**
     * Get current two-player state.
     * @return current two-player state
     */
    public GameState getTwoPlayerState() { return gameData.getTwoPlayerState(); }
    
    /**
     * Gets all walls in the game area.
     * @return Array of walls
     */
    public Wall[] getWalls() { return walls; }
}
