package com.tron_master.demo.model.logic_strategy;

import java.util.Random;

import com.tron_master.demo.Game;
import com.tron_master.demo.constant.GameConstant;
import com.tron_master.demo.model.GameState;
import com.tron_master.demo.model.Intersection;
import com.tron_master.demo.model.player.Player;
import com.tron_master.demo.model.player.PlayerHuman;

import javafx.animation.AnimationTimer;
import javafx.scene.paint.Color;

/**
 * Class for handling game logic (player collision, player operation effect –>
 * boost&jump, score-related logic)
 */
public abstract class GameLogic {
    Random rand = new Random();

    // the player and all other players
    PlayerHuman player;
    Player[] players;
    Color[] colors = { Color.CYAN, Color.PINK, Color.WHITE, Color.YELLOW,
            Color.BLUE, Color.ORANGE, Color.RED, Color.GREEN };

    int scores = 0;
    boolean isGameRunning = true;
    int VELOCITY = 3;

    // constructor adds KeyListeners and initializes fields
    public GameLogic(int p) {
        if (p > 8) {
            p = 8;
        }
        this.players = new Player[p];
    }

    /**
     * Check and handle player boundary collision
     * 
     * @param player     Player to check
     * @param gameWidth  Game area width
     * @param gameHeight Game area height
     */
    public void checkBoundaryCollision(Player player, int gameWidth, int gameHeight) {
        if (player.getX() < 0 || player.getX() > gameWidth) {
            player.setVelocityX(0);
            player.setAlive(false);
        }
        if (player.getY() < 0 || player.getY() > gameHeight) {
            player.setVelocityY(0);
            player.setAlive(false);
        }
    }

    /**
     * Handle player collision with other objects
     * 
     * @param player       Player object
     * @param intersection Collision type
     */
    public void handleCollision(Player player, Intersection intersection) {
        if (intersection == Intersection.UP) {
            player.setVelocityX(0);
            player.setVelocityY(0);
            player.setAlive(false);
        }
    }

    /**
     * Update player velocity (boost state)
     * 
     * @param player Player object
     */
    public void updatePlayerVelocity(Player player) {
        if (player.isBoosting()) {
            // Velocity in boost state
            if (player.getVelocityX() > 0) {
                player.setVelocityX(Player.VELBOOST);
            } else if (player.getVelocityX() < 0) {
                player.setVelocityX(-Player.VELBOOST);
            } else if (player.getVelocityY() > 0) {
                player.setVelocityY(Player.VELBOOST);
            } else if (player.getVelocityY() < 0) {
                player.setVelocityY(-Player.VELBOOST);
            }
        } else {
            // Normal state velocity
            int startVel = player.getStartVelocity();
            if (player.getVelocityX() > 0) {
                player.setVelocityX(startVel);
            } else if (player.getVelocityX() < 0) {
                player.setVelocityX(-startVel);
            } else if (player.getVelocityY() > 0) {
                player.setVelocityY(startVel);
            } else if (player.getVelocityY() < 0) {
                player.setVelocityY(-startVel);
            }
        }
    }

    // returns an array of velocities and dimensions for a Player
    // ensures that the Player moves toward the center initially
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

    public void updateGame(Player[] players) {
        // game logic update
        for (Player k : players) {
            if (k != null && k.getAlive()) {
                k.setBounds(GameConstant.GAME_AREA_WIDTH, GameConstant.GAME_AREA_HEIGHT);
                k.move();
            }
        }
        for (Player k1 : players) {
            if (k1 == null || !k1.getAlive()) {
                continue;
            }
            for (Player k2 : players) {
                if (k2 == null || !k2.getAlive()) {
                    continue;
                }
                k1.crash(k1.intersects(k2));
            }
        }
        // TODO: Add other game logic here
    }

    abstract void tick(AnimationTimer animationTimer);

    /**
     * initializes all new characters and restarts the timer
     */
    abstract void reset();

    /**
     * adds scores to high scores or sets the score after a level
     */
    abstract GameState addScore();

    // returns the velocity
    public int getVelocity() {
        return VELOCITY;
    }
    public boolean getRun() {return isGameRunning;}
    public void setRun(boolean run) {this.isGameRunning = run;}
}
