package com.tron_master.tron.model.object;

import com.tron_master.tron.model.data.ColorValue;

/**
 * A portal wall that teleports players to a linked exit portal instead of killing them.
 * Used in Story mode for level design variety.
 * This is a one-way portal: entrance teleports to exit, but exit acts as a normal wall.
 * The entrance portal has the same color as the player (cyan), and the exit has a different color.
 */
public class Portal extends Wall {

    /** Portal entrance color (same as story mode player - cyan) */
    public static final ColorValue ENTRANCE_COLOR = new ColorValue(0.0, 1.0, 1.0);
    
    /** Portal exit color (purple/violet) */
    public static final ColorValue EXIT_COLOR = new ColorValue(0.6, 0.2, 0.8);
    
    /** The linked exit portal (destination) */
    private Portal exitPortal;
    
    /** Whether this is an entrance (true) or exit (false) */
    private final boolean isEntrance;
    
    /** Cooldown flag to prevent instant re-teleportation */
    private boolean cooldown = false;
    
    /** Offset multiplier for exit position (to avoid re-collision) */
    private static final int EXIT_OFFSET_X = 7;
    private static final int EXIT_OFFSET_Y = 15;

    /**
     * Constructs a portal at the specified position.
     *
     * @param x          Top-left x coordinate
     * @param y          Top-left y coordinate
     * @param width      Portal width in pixels
     * @param height     Portal height in pixels
     * @param isEntrance true for entrance portal, false for exit portal
     */
    public Portal(int x, int y, int width, int height, boolean isEntrance) {
        super(x, y, width, height, isEntrance ? ENTRANCE_COLOR : EXIT_COLOR);
        this.isEntrance = isEntrance;
    }

    /**
     * Constructs a portal with a custom color.
     *
     * @param x          Top-left x coordinate
     * @param y          Top-left y coordinate
     * @param width      Portal width in pixels
     * @param height     Portal height in pixels
     * @param color      Custom portal color
     * @param isEntrance true for entrance portal, false for exit portal
     */
    public Portal(int x, int y, int width, int height, ColorValue color, boolean isEntrance) {
        super(x, y, width, height, color);
        this.isEntrance = isEntrance;
    }

    /**
     * Links this entrance portal to an exit portal (one-way).
     * Only entrance portals can be linked to exits.
     *
     * @param exit The exit portal to link to
     * @throws IllegalStateException if this portal is not an entrance
     * @throws IllegalArgumentException if exit is not an exit portal
     */
    public void linkToExit(Portal exit) {
        if (!this.isEntrance) {
            throw new IllegalStateException("Only entrance portals can be linked to exits");
        }
        if (exit != null && exit.isEntrance) {
            throw new IllegalArgumentException("Cannot link to another entrance portal");
        }
        this.exitPortal = exit;
    }

    /**
     * Teleports a player to the linked exit portal's position.
     * Only works for entrance portals with a valid link.
     *
     * @param player The player to teleport
     * @return true if teleportation occurred, false if not possible
     */
    public boolean teleport(Player player) {
        // Only entrance portals can teleport
        if (!isEntrance || exitPortal == null) {
            return false;
        }
        
        // Check cooldown to prevent instant re-teleportation
        if (cooldown) {
            return false;
        }
        
        // Calculate exit position (center of exit portal)
        int exitX = exitPortal.getX() + exitPortal.getWidth() / 2;
        int exitY = exitPortal.getY() + exitPortal.getHeight() / 2;
        
        // Offset by player velocity direction to prevent immediate re-collision
        int velX = player.getVelocityX();
        int velY = player.getVelocityY();
        
        if (velX != 0 || velY != 0) {
            exitX += (velX != 0 ? Integer.signum(velX) * EXIT_OFFSET_X : 0);
            exitY += (velY != 0 ? Integer.signum(velY) * EXIT_OFFSET_Y : 0);
        } else {
            // Default offset if player has no velocity (shouldn't happen normally)
            exitX += EXIT_OFFSET_X;
        }
        
        // Teleport player
        player.setX(exitX);
        player.setY(exitY);
        
        // Set cooldown on exit portal to prevent instant re-entry if exit is near another entrance
        exitPortal.cooldown = true;
        
        // Clear player's trail to avoid visual glitches
        player.clearPath();
        
        return true;
    }

    /**
     * Checks if player collides with this portal and handles accordingly.
     * For entrance: teleports player. For exit: acts as normal wall.
     *
     * @param player The player to check
     * @return true if player was teleported, false if collision should be handled normally
     */
    public boolean handleCollision(Player player) {
        if (!collidesWithPlayer(player)) {
            return false;
        }
        
        if (isEntrance && exitPortal != null) {
            return teleport(player);
        }
        
        // Exit portal or unlinked entrance acts as normal wall
        return false;
    }

    /**
     * Returns whether this is an entrance portal.
     *
     * @return true if the entrance one, false if exit
     */
    public boolean isEntrance() {
        return isEntrance;
    }
}
