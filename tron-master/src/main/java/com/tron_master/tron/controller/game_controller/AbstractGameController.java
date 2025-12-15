package com.tron_master.tron.controller.game_controller;

import com.tron_master.tron.Game;
import com.tron_master.tron.controller.PlayerController;
import com.tron_master.tron.controller.interfaces.PlayMenuController;
import com.tron_master.tron.controller.sound.SoundEffectListener;
import com.tron_master.tron.model.object.Player;
import com.tron_master.tron.model.object.PlayerStateListener;
import com.tron_master.tron.model.sound.SoundManager;
import com.tron_master.tron.view.game_view.GameArea;

import javafx.animation.AnimationTimer;
import javafx.application.Platform;

/**
 * Abstract base controller for all game modes using Template Method Pattern.
 * Defines the skeleton of game lifecycle: initialization, game loop, and cleanup.
 * Subclasses implement abstract methods to customize specific game mode behavior.
 * 
 * <p>Template methods define the algorithm structure:</p>
 * <ul>
 *   <li>{@link #gameTick()} - Main game loop iteration</li>
 *   <li>{@link #handleGameEnd()} - Process game completion</li>
 *   <li>{@link #doReset()} - Reset game to initial state</li>
 * </ul>
 */
public abstract class AbstractGameController implements PlayerStateListener {

    protected PlayerController playerController;
    protected GameLoopTimer timer;
    protected SoundEffectListener soundListener;

    /**
     * Base constructor for shared controller setup.
     */
    protected AbstractGameController() {
        // No-op base constructor
    }

    // ==================== Abstract Template Methods (must be implemented) ====================

    /** Get all players for this game mode */
    protected abstract Player[] getPlayers();

    /** Get the game area (view) */
    protected abstract GameArea getGameArea();

    /** Check if game logic is still running */
    protected abstract boolean isGameRunning();

    /** Execute game logic tick (call logic.tick()) */
    protected abstract void doGameTick();

    /** Render game objects (walls, portals, players, etc.) */
    protected abstract void render();

    /** Update UI display (scores, boost, level, etc.) */
    protected abstract void updateUI();

    /** Handle game end state (show game over, save scores, etc.) */
    protected abstract void handleGameEnd();

    /** Reset game to initial state (mode-specific reset logic) */
    protected abstract void doReset();

    // ==================== Hook Methods (optional override) ====================

    /** Hook called before game starts - default does nothing */
    protected void beforeGameStart() {}

    /** Hook called after game ends - default does nothing */
    protected void afterGameEnd() {}

    // ==================== Common Implementation (Template Method Pattern) ====================

    /**
     * Standard game loop timer - shared by all game modes.
     */
    protected class GameLoopTimer extends AnimationTimer {
        private long lastUpdate = 0;
        private static final long TICK_INTERVAL = 20_000_000; // 20ms

        @Override
        public void handle(long now) {
            if (now - lastUpdate >= TICK_INTERVAL && isGameRunning()) {
                gameTick();
                lastUpdate = now;
            }
        }

        @Override
        public void stop() {
            super.stop();
        }
    }

    /** Initialize the game timer */
    protected void initializeGameTimer() {
        timer = new GameLoopTimer();
    }

    /** Start the game loop and request focus */
    protected void startGame() {
        beforeGameStart();
        if (timer != null) {
            timer.start();
        }
        getGameArea().requestFocus();
    }

    /** Stop the game loop */
    protected void stopGame() {
        if (timer != null) {
            timer.stop();
        }
    }

    /**
     * Main game tick - the core template method.
     * Calls abstract methods in fixed order: logic -> UI -> render.
     */
    protected void gameTick() {
        doGameTick();
        updateUI();
        render();
    }

    // ==================== Observer Pattern Support ====================

    /** Register this controller as listener for player state changes */
    protected void registerPlayerListeners() {
        if (soundListener == null) {
            soundListener = new SoundEffectListener();
        }
        Player[] players = getPlayers();
        if (players != null) {
            for (Player player : players) {
                if (player != null) {
                    player.addStateListener(this);
                    player.addStateListener(soundListener);
                }
            }
        }
    }

    /** Unregister this controller from all player listeners */
    protected void unregisterPlayerListeners() {
        Player[] players = getPlayers();
        if (players != null) {
            for (Player player : players) {
                if (player != null) {
                    player.removeStateListener(this);
                    if (soundListener != null) {
                        player.removeStateListener(soundListener);
                    }
                }
            }
        }
    }

    // ==================== Common Button Handlers ====================

    /** Handle reset button click - common flow with mode-specific doReset() */
    public void onResetBtnClick() {
        SoundManager.getInstance().playSoundEffect("reset");
        stopGame();
        doReset();
        initializeGameTimer();
        startGame();
        playerController.setRenderingEnabled(true);
        getGameArea().requestFocus();
    }

    /** Handle exit button click - return to play menu */
    public void onExitBtnClick() {
        SoundManager.getInstance().playSoundEffect("clic");
        unregisterPlayerListeners();
        stopGame();
        Game.getPrimaryStage().setScene(new PlayMenuController().createPlayMenuScene());
    }

    // ==================== PlayerStateListener Default Implementation ====================

    @Override
    public void onPlayerDied(Player player) {
        Platform.runLater(() -> {
            if (!isGameRunning()) {
                handleGameEnd();
                afterGameEnd();
            }
            playerController.renderPlayers();
        });
    }

    @Override
    public void onPlayerBoosted(Player player, int boostLeft) {
        Platform.runLater(this::updateUI);
    }

    @Override
    public void onPlayerJumped(Player player) {
        // Default: do nothing, subclasses can override if needed
    }
}
