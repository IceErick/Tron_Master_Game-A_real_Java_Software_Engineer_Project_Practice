package com.tron_master.tron.model.object;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.tron_master.tron.constant.GameConstant;
import com.tron_master.tron.model.data.ColorValue;
import com.tron_master.tron.model.data.GameData;
import com.tron_master.tron.model.data.Intersection;
import com.tron_master.tron.view.utils.Line;

/**
 * Abstract base class for all player types.
 * Stores player state, movement, boost logic, and basic properties.
 * Subclasses implement specific control logic (human/AI).
 * 
 * Supports Observer pattern: listeners can be registered to receive state change notifications.
 */
public abstract class Player extends GameObject {

    private final GameData gameData = GameData.getInstance();
    
    /** List of state change listeners (thread-safe for concurrent modifications) */
    private final List<PlayerStateListener> stateListeners = new CopyOnWriteArrayList<>();
    
    /** Player color, alive state, jump/boost flags, velocity, boost count, etc. */
    final ColorValue color;
    boolean alive = true;
    boolean jumping = false;
    boolean boosting = false;
    int startVelocity = 0;
    /** Game mode type: "story", "survival", "twoPlayer" */
    protected String mode = "story";
    /** Player slot identifier for two-object mode (1 or 2). -1 means not set. */
    protected int playerSlot = -1;
    int boostLeft;
    
    /** Player width in pixels. */
    public static final int WIDTH = 5;
    /** Player height in pixels. */
    public static final int HEIGHT = 5;
    /** Boosted velocity magnitude. */
    public static final int VELBOOST = 5;
    /** Jump height in pixels. */
    public static final int JUMPHEIGHT = 16;
    private static final int BOOST_DURATION_TICKS = 15; // roughly 300 ms at 20ms/tick
    private int boostTicksRemaining = 0;
    
    /** Player movement path (trail) */
    private final ArrayList<Line> path = new ArrayList<>();
    
    /**
     * Constructs a player instance.
     * @param x Initial x coordinate
     * @param y Initial y coordinate
     * @param velocityX Initial velocity in x direction
     * @param velocityY Initial velocity in y direction
     * @param color Player color
     * @param mode Game mode ("story", "survival", "twoPlayer")
     */
    public Player(int x, int y, int velocityX, int velocityY, ColorValue color, String mode) {
        super(x, y, velocityX, velocityY, WIDTH, HEIGHT);
        this.startVelocity = Math.max(Math.abs(velocityX), Math.abs(velocityY));
        this.color = color;
        this.mode = mode;
        // Initialize boost count based on mode
        if ("survival".equals(mode)) {
            this.boostLeft = gameData.getSurvivalBoost();
        } else if ("story".equals(mode)) {
            this.boostLeft = gameData.getStoryBoost();
        } else if ("twoPlayer".equals(mode)) {
            this.boostLeft = GameConstant.INIT_BOOST_COUNT; // Will be set after slot assignment
        } else {
            this.boostLeft = GameConstant.INIT_BOOST_COUNT;
        }
        this.boostTicksRemaining = 0;
    }

    /**
     * Checks if the object is out of bounds and updates alive state.
     * Uses setAlive() to trigger observer notifications.
     */
    public void accelerate() {
        if (x < 0 || x > rightBound) {
            velocityX = 0;
            setAlive(false); // Use setter to trigger notification
        }
        if (y < 0 || y > bottomBound) {
            velocityY = 0;
            setAlive(false); // Use setter to trigger notification
        }
    }

    /**
     * Activates boost and updates GameData boost count according to current mode and slot.
     */
    public void startBoost() {
        if (boostLeft > 0) {
            boosting = true;
            boostTicksRemaining = BOOST_DURATION_TICKS;
            boostLeft--;
            if (isHuman()) {
                switch (mode) {
                    case "survival" -> gameData.setSurvivalBoost(boostLeft);
                    case "story" -> gameData.setStoryBoost(boostLeft);
                    case "twoPlayer" -> updateTwoPlayerBoost();
                    default -> {
                    }
                }
            }
            notifyPlayerBoosted(); // Notify listeners
        }
    }

    private void updateTwoPlayerBoost() {
        if (playerSlot == 1) {
            gameData.setPlayer1Boost(boostLeft);
        } else if (playerSlot == 2) {
            gameData.setPlayer2Boost(boostLeft);
        }
    }

    /**
     * Updates velocity for boosting or resets to normal velocity.
     */
    public void boost() {
        boosting = boostTicksRemaining > 0;
        if (boostTicksRemaining > 0) {
            boostTicksRemaining--;
            if (velocityX > 0) {
                velocityX = VELBOOST;
            } else if (velocityX < 0) {
                velocityX = -VELBOOST;
            } else if (velocityY > 0) {
                velocityY = VELBOOST;
            } else if (velocityY < 0) {
                velocityY = -VELBOOST;
            }
        } else {
            if (velocityX > 0) {
                velocityX = startVelocity;
            } else if (velocityX < 0) {
                velocityX = -startVelocity;
            } else if (velocityY > 0) {
                velocityY = startVelocity;
            } else if (velocityY < 0) {
                velocityY = -startVelocity;
            }
        }
    }

    /**
     * Checks if the player has crashed with a path and updates alive state.
     * @param i intersection result
     */
    public void crash(Intersection i) {
        if (alive && i == Intersection.UP) {
            velocityX = 0;
            velocityY = 0;
            setAlive(false); // Use setter to trigger notification
        }
    }

    /**
     * Whether this player is controlled by a human.
     * @return true if human-controlled
     */
    public abstract Boolean isHuman();
    
    // =============== Observer Pattern Support ===============
    
    /**
     * Registers a state listener to receive object state change notifications.
     * @param listener The listener to add
     */
    public void addStateListener(PlayerStateListener listener) {
        if (listener != null && !stateListeners.contains(listener)) {
            stateListeners.add(listener);
        }
    }
    
    /**
     * Removes a state listener.
     * @param listener The listener to remove
     */
    public void removeStateListener(PlayerStateListener listener) {
        stateListeners.remove(listener);
    }
    
    /**
     * Notifies all listeners that this object has died.
     */
    protected void notifyPlayerDied() {
        for (PlayerStateListener listener : stateListeners) {
            listener.onPlayerDied(this);
        }
    }
    
    /**
     * Notifies all listeners that this object has activated boost.
     */
    protected void notifyPlayerBoosted() {
        for (PlayerStateListener listener : stateListeners) {
            listener.onPlayerBoosted(this, boostLeft);
        }
    }
    
    /**
     * Notifies all listeners that this object has jumped.
     */
    protected void notifyPlayerJumped() {
        for (PlayerStateListener listener : stateListeners) {
            listener.onPlayerJumped(this);
        }
    }
    
    // Getters and setters (no comments needed unless logic is nontrivial)
    /**
     * Get player display color.
     * @return player display color
     */
    public ColorValue getColor() { return color; }
    /**
     * Check whether player is alive.
     * @return whether player is alive
     */
    public boolean getAlive() { return alive; }
    
    /**
     * Sets the alive state and notifies listeners if object dies.
     * @param alive New alive state
     */
    public void setAlive(boolean alive) {
        boolean wasAlive = this.alive;
        this.alive = alive;
        
        // Notify listeners if object just died
        if (wasAlive && !alive) {
            notifyPlayerDied();
        }
    }
    
    /**
     * Check whether player is currently jumping.
     * @return whether player is currently jumping
     */
    public boolean isJumping() { return jumping; }
    /** Trigger a jump action. */
    public void jump() {
        jumping = true;
        notifyPlayerJumped(); // Notify listeners
    }
    /**
     * Check whether boost is currently active.
     * @return whether boost is currently active
     */
    public boolean isBoosting() { return boosting; }
    /**
     * Get initial velocity magnitude.
     * @return initial velocity magnitude
     */
    public int getStartVelocity() { return startVelocity; }
    @Override
    public ArrayList<Line> getPath() { return path; }
    /**
     * Get current x velocity.
     * @return current x velocity
     */
    public int getVelocityX() { return velocityX; }
    /**
     * Get current y velocity.
     * @return current y velocity
     */
    public int getVelocityY() { return velocityY; }
    /**
     * Get remaining boosts.
     * @return remaining boosts
     */
    public int getBoostLeft() { return boostLeft; }
    /**
     * Get game mode string.
     * @return game mode string
     */
    public String getMode() { return mode; }
    
    /**
     * Adds boost to the player's boost count.
     * Updates both the player's internal count and GameData for UI display.
     * @param amount Amount of boost to add
     */
    public void addBoost(int amount) {
        boostLeft += amount;
        if (isHuman()) {
            switch (mode) {
                case "survival" -> gameData.setSurvivalBoost(boostLeft);
                case "story" -> gameData.setStoryBoost(boostLeft);
                case "twoPlayer" -> updateTwoPlayerBoost();
                default -> {}
            }
        }
    }
    /**
     * Assign this player's slot number (two-player mode).
     * @param playerSlot slot index (1 or 2)
     */
    public void setPlayerSlot(int playerSlot) {
        this.playerSlot = playerSlot;
        if ("twoPlayer".equals(mode)) {
            if (playerSlot == 1) {
                this.boostLeft = gameData.getPlayer1Boost();
            } else if (playerSlot == 2) {
                this.boostLeft = gameData.getPlayer2Boost();
            }
        }
    }

    /**
     * Sets the player's x coordinate directly.
     * Used by Portal for teleportation.
     * @param x New x coordinate
     */
    public void setX(int x) {
        this.x = x;
    }

    /**
     * Sets the player's y coordinate directly.
     * Used by Portal for teleportation.
     * @param y New y coordinate
     */
    public void setY(int y) {
        this.y = y;
    }

    /**
     * Clears the player's movement path (trail).
     * Used after teleportation to avoid visual glitches.
     */
    public void clearPath() {
        this.path.clear();
    }
    
    /**
     * Moves the player according to its control logic.
     */
    public abstract void move();

    /**
     * Adds all players to this player's context (for collision, AI, etc.).
     * @param players all players in the game
     */
    public abstract void addPlayers(Player[] players);
}
