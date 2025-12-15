package com.tron_master.tron.model.logic_strategy;

import java.util.Random;

import com.tron_master.tron.model.data.ColorValue;
import com.tron_master.tron.model.data.GameState;
import com.tron_master.tron.model.object.Player;
import com.tron_master.tron.model.object.PlayerHuman;

/**
 * Class for handling game logic (player collision, player operation effect –>
 * boost and jump, score-related logic)
 */
public abstract class GameLogic {
    Random rand = new Random();

    // the player and all other players
    PlayerHuman player;
    Player[] players;
    ColorValue[] colors = {
            new ColorValue(0.0, 1.0, 1.0), // cyan
            new ColorValue(1.0, 0.75, 0.8), // pink
            new ColorValue(1.0, 1.0, 1.0), // white
            new ColorValue(1.0, 1.0, 0.0), // yellow
            new ColorValue(0.0, 0.0, 1.0), // blue
            new ColorValue(1.0, 0.65, 0.0), // orange
            new ColorValue(1.0, 0.0, 0.0), // red
            new ColorValue(0.0, 1.0, 0.0) // green
    };

    int scores = 0;
    boolean isGameRunning = true;
    int VELOCITY = 3;
    private final int gameWidth;
    private final int gameHeight;

    // constructor adds KeyListeners and initializes fields
    /**
     * Construct base game logic with given player count and bounds.
     * @param p number of players (capped at 8)
     * @param gameWidth playfield width
     * @param gameHeight playfield height
     */
    public GameLogic(int p, int gameWidth, int gameHeight) {
        if (p > 8) {
            p = 8;
        }
        this.players = new Player[p];
        this.gameWidth = gameWidth;
        this.gameHeight = gameHeight;
    }

    // returns an array of velocities and dimensions for a Player
    // ensures that the Player moves toward the center initially
    /**
     * Generate a random starting point and velocity aimed toward center.
     * @return starting position and velocity [x, y, velX, velY]
     */
    public int[] getRandomStart() {
        int[] start = new int[4];
        int xnew = 50 + rand.nextInt(400);
        int ynew = 50 + rand.nextInt(400);
        int ra = rand.nextInt(2);
        int velx = 0;
        int vely = 0;
        if (ra == 0) {
            if (xnew < 250) {
                velx = VELOCITY;
            } else {
                velx = -VELOCITY;
            }
        } else {
            if (ynew < 250) {
                vely = VELOCITY;
            } else {
                vely = -VELOCITY;
            }
        }
        start[0] = xnew;
        start[1] = ynew;
        start[2] = velx;
        start[3] = vely;
        return start;
    }

    /**
     * Update movement and collision for all alive players.
     * @param players players to update
     */
    public void updateGame(Player[] players) {
        // game logic update - only move alive players
        for (Player k : players) {
            if (k != null && k.getAlive()) {
                k.setBounds(gameWidth, gameHeight);
                k.move();
            }
        }
        // collision detection - check all players
        for (Player k1 : players) {
            if (k1 == null) {
                continue;
            }
            for (Player k2 : players) {
                if (k2 == null) {
                    continue;
                }
                k1.crash(k1.intersects(k2));
            }
        }
    }

    abstract void tick(Runnable stopCallback);

    /**
     * initializes all new characters and restarts the timer
     */
    abstract void reset();

    /**
     * adds scores to high scores or sets the score after a level
     */
    abstract GameState addScore();

    // returns the velocity
    /**
     * Get base velocity used by players.
     * @return base velocity
     */
    public int getVelocity() {
        return VELOCITY;
    }
    /**
     * Check if game loop should keep running.
     * @return true if running
     */
    public boolean getRun() {return isGameRunning;}
    /**
     * Set whether the game loop should run.
     * @param run true to run
     */
    public void setRun(boolean run) {this.isGameRunning = run;}
}
