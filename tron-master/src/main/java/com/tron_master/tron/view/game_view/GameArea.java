package com.tron_master.tron.view.game_view;

import com.tron_master.tron.constant.GameConstant;
import com.tron_master.tron.model.data.GameData;
import com.tron_master.tron.model.object.Player;
import com.tron_master.tron.model.object.Portal;
import com.tron_master.tron.model.object.Wall;

import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Game area abstract base class (parent class for all game mode canvases)
 * Encapsulates Canvas initialization, background drawing, common reset logic
 */
public abstract class GameArea extends Canvas {
    protected GraphicsContext gc;
    protected final GameData gameData = GameData.getInstance();
    private final PlayerRenderer playerRenderer = new PlayerRenderer();
    // Unified wall/portal renderer for all game modes
    protected final WallRenderer wallRenderer = new WallRenderer();

    // Mode-specific walls and portals to render
    protected Wall[] walls;
    protected Portal[] portals;

    /**
     * Create a game area canvas with default dimensions and initialize it.
     */
    public GameArea() {
        super(GameConstant.GAME_AREA_WIDTH, GameConstant.GAME_AREA_HEIGHT);
        this.gc = getGraphicsContext2D();
        initGame();
    }

    /**
     * Set walls to be used by the GameArea. Subclasses typically call this when logic changes or resets.
     * @param walls array of Wall objects
     */
    public void setWalls(Wall[] walls) {
        this.walls = walls != null ? walls : new Wall[0];
    }

    /**
     * Set portals to be used by the GameArea.
     * @param portals array of Portal objects
     */
    public void setPortals(Portal[] portals) {
        this.portals = portals != null ? portals : new Portal[0];
    }

    /** Initialize game-specific resources. */
    protected abstract void initGame();

    /** Reset the game area to its initial state. */
    public abstract void reset();

    protected void drawBackground() {
        Color bgColor = Color.valueOf(gameData.getBackgroundColor());
        gc.setFill(bgColor);
        gc.fillRect(0, 0, GameConstant.GAME_AREA_WIDTH, GameConstant.GAME_AREA_HEIGHT);
        drawBoundary();
    }
    
    protected void drawBoundary() {
        gc.setStroke(Color.web("#FFFFFF", 0.7));
        gc.setLineWidth(5);
        gc.strokeRect(0, 0, GameConstant.GAME_AREA_WIDTH, GameConstant.GAME_AREA_HEIGHT);
    }

    /**
     * Render only the players (delegates to full scene rendering for compatibility).
     * @param players players to render
     */
    public void renderPlayers(Player[] players) {
        // Backwards compatible: render full scene
        renderScene(players);
    }

    /**
     * Render the full scene: background, walls/portals, then players.
     * This centralizes wall/portal rendering for all view strategies.
     * @param players players to render
     */
    public void renderScene(Player[] players) {
        drawBackground();
        renderWalls();
        renderPortals();
        // draw players on top
        for (Player player : players) {
            if (player == null) {
                continue;
            }
            playerRenderer.drawPlayer(
                    gc,
                    player.getColor(),
                    player.getX(),
                    player.getY(),
                    Player.WIDTH,
                    Player.HEIGHT,
                    player.getPath()
            );
        }
    }

    /**
     * Render configured walls (if any) using the centralized WallRenderer.
     */
    public void renderWalls() {
        if (walls != null) {
            wallRenderer.drawWalls(gc, walls);
        }
    }

    /**
     * Render configured portals (if any) using the centralized WallRenderer.
     */
    public void renderPortals() {
        if (portals != null) {
            wallRenderer.drawWalls(gc, portals);
        }
    }

    abstract void showGameOverScreen();

    @Override
    public void requestFocus() {
        super.requestFocus();
        setFocusTraversable(true);
    }
}
