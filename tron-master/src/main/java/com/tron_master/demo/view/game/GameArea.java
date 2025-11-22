package com.tron_master.demo.view.game;

import com.tron_master.demo.constant.GameConstant;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Game area abstract base class (parent class for all game mode canvases)
 * Encapsulates Canvas initialization, background drawing, common reset logic
 */
public abstract class GameArea extends Canvas {
    protected GraphicsContext gc; // Graphics context (core drawing tool)
    protected Color bgColor = Color.valueOf(GameConstant.DEFAULT_BG_COLOR); // Background color

    public GameArea() {
        // Initialize canvas size (consistent with game area size)
        super(GameConstant.GAME_AREA_WIDTH, GameConstant.GAME_AREA_HEIGHT);
        this.gc = getGraphicsContext2D();
        initGame(); // Subclass implements specific game initialization
    }

    /**
     * Initialize game (subclass implementation: characters, maps, collision detection, etc.)
     */
    protected abstract void initGame();

    /**
     * Reset game (subclass implementation: restore initial state)
     */
    public abstract void reset();

    /**
     * Draw background (common method, can be directly called by subclasses)
     */
    protected void drawBackground() {
        gc.setFill(bgColor);
        // Use fixed size set during construction, not dynamic getWidth()
        gc.fillRect(0, 0, GameConstant.GAME_AREA_WIDTH, GameConstant.GAME_AREA_HEIGHT);
    }

    abstract void showGameOverScreen();

    /**
     * Request focus (ensure keyboard events can be captured)
     * Must be called when game scene is displayed
     */
    @Override
    public void requestFocus() {
        super.requestFocus();
        setFocusTraversable(true); // Allow getting focus
    }
}