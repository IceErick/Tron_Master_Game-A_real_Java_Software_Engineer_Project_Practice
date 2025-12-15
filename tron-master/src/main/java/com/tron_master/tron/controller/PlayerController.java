package com.tron_master.tron.controller;

import java.util.ArrayList;
import java.util.List;

import com.tron_master.tron.model.object.Player;
import com.tron_master.tron.model.object.PlayerHuman;
import com.tron_master.tron.view.game_view.GameArea;

import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;

/**
 * Controller class for handling player input and connect view layer for rendering
 */
public class PlayerController {

    private final Player[] players;
    private GameArea gameArea;
    private final List<PlayerHuman> humanPlayers = new ArrayList<>(); // store all human players
    private static final int VELOCITY = 3; // Default player velocity
    private boolean isRenderingEnabled = true;

    /**
     * Create controller for given players.
     * @param players players to manage input/rendering
     */
    public PlayerController(Player[] players) {
        this.players = players;

        // Add all human players to the list
        for (Player player : players) {
            if (player != null && player.isHuman()) {
                humanPlayers.add((PlayerHuman) player);
            }
        }
    }

    /**
     * Render all managed players if rendering is enabled.
     */
    public void renderPlayers() {
        if (!isRenderingEnabled) {
            return;
        }
        if (gameArea != null) {
            gameArea.renderPlayers(players);
        }
    }

    /**
     * Handle keyboard input for a specific player
     * 
     * @param event       Keyboard event
     */
    public void handleKeyPress(KeyEvent event) {
        // Don't process input if rendering is disabled (e.g., during countdown)
        if (!isRenderingEnabled) {
            return;
        }
        
        KeyCode key = event.getCode();

        // Get the player associated with the key press
        PlayerHuman targetPlayer = getPlayerForKey(key);

        // Only process input if player exists and is alive
        if (targetPlayer != null && targetPlayer.getAlive()) {
            switch (key) {
                case W, UP -> {
                    targetPlayer.setVelocityX(0);
                    targetPlayer.setVelocityY(-VELOCITY);
                }
                case A, LEFT -> {
                    targetPlayer.setVelocityX(-VELOCITY);
                    targetPlayer.setVelocityY(0);
                }
                case S, DOWN -> {
                    targetPlayer.setVelocityX(0);
                    targetPlayer.setVelocityY(VELOCITY);
                }
                case D, RIGHT -> {
                    targetPlayer.setVelocityX(VELOCITY);
                    targetPlayer.setVelocityY(0);
                }
                case Q, SPACE -> targetPlayer.jump();
                case DIGIT1, B -> targetPlayer.startBoost();
                default -> {
                    // Ignore other keys
                }
            }
            // Consume the event to prevent it from triggering focus traversal
            event.consume();
        }
    }

    private PlayerHuman getPlayerForKey(KeyCode key) {
        // If no human players, return null
        if (humanPlayers.isEmpty()) {
            return null;
        }

        // player 1 controls
        if (key == KeyCode.UP || key == KeyCode.LEFT || key == KeyCode.DOWN ||
                key == KeyCode.RIGHT || key == KeyCode.SPACE || key == KeyCode.B) {
            return humanPlayers.getFirst();
        }

        // player 2 controls
        if  (key == KeyCode.W || key == KeyCode.A || key == KeyCode.S ||
                key == KeyCode.D || key == KeyCode.Q || key == KeyCode.DIGIT1) {
            return humanPlayers.size() > 1 ? humanPlayers.get(1)
                    : humanPlayers.getFirst();
        }

        return null;
    }
    
    // Method to set the game area for binding key events
    /**
     * Bind game area to receive key events for players.
     * @param gameArea game area canvas
     */
    public void setGameArea(GameArea gameArea) {
        this.gameArea = gameArea;
        // Bind key events from GameArea to this controller
        gameArea.setOnKeyPressed(this::handleKeyPress);
    }

    /**
     * Enable or disable rendering (used during countdowns).
     * @param renderingEnabled true to render players
     */
    public void setRenderingEnabled(boolean renderingEnabled) {
        this.isRenderingEnabled = renderingEnabled;
    }
}
