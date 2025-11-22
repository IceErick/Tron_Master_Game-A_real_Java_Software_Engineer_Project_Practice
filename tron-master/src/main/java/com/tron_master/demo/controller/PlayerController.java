package com.tron_master.demo.controller;

import java.util.ArrayList;
import java.util.List;

import com.tron_master.demo.constant.GameConstant;
import com.tron_master.demo.model.logic_strategy.GameLogic;
import com.tron_master.demo.model.player.Player;
import com.tron_master.demo.model.player.PlayerHuman;
import com.tron_master.demo.view.Shape;
import com.tron_master.demo.view.game.GameArea;
import com.tron_master.demo.view.game.PlayerRenderer;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.paint.Color;

/**
 * Controller class for handling player input and connect view layer for rendering
 */
public class PlayerController {

    private final Player[] players;
    private final PlayerRenderer playerRenderer;
    private GameArea gameArea;
    private GameLogic gameLogic;
    private final List<PlayerHuman> humanPlayers = new ArrayList<>(); // store all human players
    private final GraphicsContext gc;
    private static final int VELOCITY = 3; // 定义速度常量
    private boolean isRenderingEnabled = true;

    public PlayerController(Player[] players, PlayerRenderer playerRenderer, GraphicsContext gc) {
        this.players = players;
        this.playerRenderer = playerRenderer;
        this.gc = gc; // 初始化GraphicsContext

        // Add all human players to the list
        for (Player player : players) {
            if (player != null && player.isHuman()) { // 添加null检查
                humanPlayers.add((PlayerHuman) player);
            }
        }
    }

    public void renderPlayers() {
        if (!isRenderingEnabled) {
            return;
        }
        gc.setFill(Color.BLACK);
        gc.fillRect(0, 0, GameConstant.GAME_AREA_WIDTH, GameConstant.GAME_AREA_HEIGHT);
        for (Player player : players) {
            if (player == null) continue; // 添加null检查

            Color color = player.getColor();
            int x = player.getX();
            int y = player.getY();
            int width = Player.WIDTH;
            int height = Player.HEIGHT;
            List<Shape> path = player.getPath();

            // pass player data to renderer in view
            playerRenderer.drawPlayer(gc, color, x, y, width, height, path);
        }
    }

    /**
     * Handle keyboard input for a specific player
     * 
     * @param event       Keyboard event
     */
    public void handleKeyPress(KeyEvent event) {
        KeyCode key = event.getCode();

        // Get the player associated with the key press
        PlayerHuman targetPlayer = getPlayerForKey(key);

        if (targetPlayer != null) {
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
            }
        }
    }

    private PlayerHuman getPlayerForKey(KeyCode key) {
        // If no human players, return null
        if (humanPlayers.isEmpty()) {
            return null;
        }

        // player 1 controls
        if (key == KeyCode.W || key == KeyCode.A || key == KeyCode.S ||
                key == KeyCode.D || key == KeyCode.Q || key == KeyCode.DIGIT1) {
            return humanPlayers.size() > 0 ? humanPlayers.get(0) : null;
        }

        // player 2 controls
        if (key == KeyCode.UP || key == KeyCode.LEFT || key == KeyCode.DOWN ||
                key == KeyCode.RIGHT || key == KeyCode.SPACE || key == KeyCode.B) {
            return humanPlayers.size() > 1 ? humanPlayers.get(1)
                    : (humanPlayers.size() > 0 ? humanPlayers.get(0) : null);
        }

        return null;
    }
    
    // Method to set the game area for binding key events
    public void setGameArea(GameArea gameArea) {
        this.gameArea = gameArea;
        // Bind key events from GameArea to this controller
        gameArea.setOnKeyPressed(event -> handleKeyPress(event));
    }

    public void setRenderingEnabled(boolean renderingEnabled) {
        this.isRenderingEnabled = renderingEnabled;
    }
}