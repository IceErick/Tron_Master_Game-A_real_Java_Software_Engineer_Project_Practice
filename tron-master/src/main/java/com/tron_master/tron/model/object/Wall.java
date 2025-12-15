package com.tron_master.tron.model.object;

import java.util.ArrayList;

import com.tron_master.tron.model.data.ColorValue;
import com.tron_master.tron.view.utils.Line;

/**
 * Represents a static wall (obstacle) in the game area.
 * Players collide with walls and die upon contact.
 * Extends GameObject for consistent collision detection.
 */
public class Wall extends GameObject {

    /** Wall color for rendering */
    private final ColorValue color;
    
    /** Whether this wall is active/visible */
    private boolean active = true;

    /** Default wall color (gray) */
    public static final ColorValue DEFAULT_COLOR = new ColorValue(0.5, 0.5, 0.5);
    
    /** Neon cyan color for walls */
    public static final ColorValue NEON_CYAN = new ColorValue(0.0, 1.0, 1.0);
    
    /** Neon magenta color for walls */
    public static final ColorValue NEON_MAGENTA = new ColorValue(1.0, 0.0, 1.0);

    /**
     * Constructs a static wall at the specified position and size.
     *
     * @param x      Top-left x coordinate
     * @param y      Top-left y coordinate
     * @param width  Wall width in pixels
     * @param height Wall height in pixels
     * @param color  Wall color for rendering
     */
    public Wall(int x, int y, int width, int height, ColorValue color) {
        super(x, y, 0, 0, width, height); // Velocity is 0 (static object)
        this.color = color != null ? color : DEFAULT_COLOR;
    }

    /**
     * Constructs a static wall with default color.
     *
     * @param x      Top-left x coordinate
     * @param y      Top-left y coordinate
     * @param width  Wall width in pixels
     * @param height Wall height in pixels
     */
    public Wall(int x, int y, int width, int height) {
        this(x, y, width, height, DEFAULT_COLOR);
    }

    /**
     * Static walls do not accelerate.
     */
    @Override
    public void accelerate() {
        // No-op: walls are static
    }

    /**
     * Walls are always "alive" (active) unless explicitly deactivated.
     *
     * @return true if the wall is active
     */
    @Override
    public boolean getAlive() {
        return active;
    }

    /**
     * Sets the active state of this wall.
     *
     * @param active true to activate, false to deactivate
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Walls have no path/trail.
     *
     * @return empty list
     */
    @Override
    public ArrayList<Line> getPath() {
        return new ArrayList<>();
    }

    /**
     * Gets the wall color.
     *
     * @return ColorValue for rendering
     */
    public ColorValue getColor() {
        return color;
    }

    /**
     * Gets the x coordinate.
     *
     * @return x coordinate
     */
    public int getX() {
        return x;
    }

    /**
     * Gets the y coordinate.
     *
     * @return y coordinate
     */
    public int getY() {
        return y;
    }

    /**
     * Gets wall width.
     *
     * @return width in pixels
     */
    public int getWidth() {
        return width;
    }

    /**
     * Gets wall height.
     *
     * @return height in pixels
     */
    public int getHeight() {
        return height;
    }

    /**
     * Checks if a point is inside this wall.
     *
     * @param px Point x coordinate
     * @param py Point y coordinate
     * @return true if point is inside wall bounds
     */
    public boolean containsPoint(int px, int py) {
        return px >= x && px <= x + width && py >= y && py <= y + height;
    }

    /**
     * Checks collision with a player using bounding box.
     *
     * @param player The player to check collision with
     * @return true if player collides with this wall
     */
    public boolean collidesWithPlayer(Player player) {
        if (!active || player == null || !player.getAlive()) {
            return false;
        }
        
        int playerX = player.getX();
        int playerY = player.getY();
        int playerHalfWidth = Player.WIDTH / 2;
        int playerHalfHeight = Player.HEIGHT / 2;

        // Bounding box collision detection
        return playerX + playerHalfWidth >= x &&
               playerX - playerHalfWidth <= x + width &&
               playerY + playerHalfHeight >= y &&
               playerY - playerHalfHeight <= y + height;
    }
}
