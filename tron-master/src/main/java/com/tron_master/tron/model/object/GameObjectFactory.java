package com.tron_master.tron.model.object;

import com.tron_master.tron.model.data.ColorValue;

/**
 * Factory class for creating GameObject instances (currently Player types).
 * Encapsulates the object creation logic for different game object types.
 * Follows the Factory Pattern to decouple object instantiation from client code.
 * Designed to be extensible for future GameObject subclasses.
 */
public class GameObjectFactory {

    /**
     * Utility class; prevent instantiation.
     */
    private GameObjectFactory() {
    }

    /**
     * GameObject type enumeration for type-safe object creation.
     * Currently, supports Player types and Wall; extensible for future GameObject subclasses.
     */
    public enum GameObjectType {
        /** Human-controlled player. */
        PLAYER_HUMAN,
        /** AI-controlled player. */
        PLAYER_AI,
        /** Static wall object (use wall helper). */
        WALL
    }

    /**
     * Create a concrete {@link GameObject} based on type.
     * @param type      desired object type
     * @param x         x coordinate
     * @param y         y coordinate
     * @param velocityX initial velocity X
     * @param velocityY initial velocity Y
     * @param color     color for player objects
     * @param mode      game mode string
     * @return new game object instance
     */
    public static GameObject createGameObject(GameObjectType type, int x, int y, int velocityX, int velocityY, 
                                              ColorValue color, String mode) {
        if (type == null) {
            throw new IllegalArgumentException("GameObject type cannot be null");
        }
        
        return switch (type) {
            case PLAYER_HUMAN -> new PlayerHuman(x, y, velocityX, velocityY, color, mode);
            case PLAYER_AI -> new PlayerAI(x, y, velocityX, velocityY, color, mode);
            case WALL -> throw new IllegalArgumentException("Use createWall() for walls");
        };
    }

    /**
     * Convenience method to create a human object.
     * 
     * @param x Initial x coordinate
     * @param y Initial y coordinate
     * @param velocityX Initial velocity in x direction
     * @param velocityY Initial velocity in y direction
     * @param color Player color
     * @param mode Game mode
     * @return A new PlayerHuman instance
     */
    public static Player createHumanPlayer(int x, int y, int velocityX, int velocityY, 
                                          ColorValue color, String mode) {
        return (Player) createGameObject(GameObjectType.PLAYER_HUMAN, x, y, velocityX, velocityY, color, mode);
    }

    /**
     * Convenience method to create an AI object.
     * 
     * @param x Initial x coordinate
     * @param y Initial y coordinate
     * @param velocityX Initial velocity in x direction
     * @param velocityY Initial velocity in y direction
     * @param color Player color
     * @param mode Game mode
     * @return A new PlayerAI instance
     */
    public static Player createAIPlayer(int x, int y, int velocityX, int velocityY, 
                                       ColorValue color, String mode) {
        return (Player) createGameObject(GameObjectType.PLAYER_AI, x, y, velocityX, velocityY, color, mode);
    }

    /**
     * Creates a static wall with specified dimensions and color.
     *
     * @param x      Top-left x coordinate
     * @param y      Top-left y coordinate
     * @param width  Wall width
     * @param height Wall height
     * @param color  Wall color
     * @return A new Wall instance
     */
    public static Wall createWall(int x, int y, int width, int height, ColorValue color) {
        return new Wall(x, y, width, height, color);
    }

    /**
     * Creates a portal entrance with default entrance color (cyan).
     *
     * @param x      Top-left x coordinate
     * @param y      Top-left y coordinate
     * @param width  Portal width
     * @param height Portal height
     * @return A new Portal entrance instance
     */
    public static Portal createPortalEntrance(int x, int y, int width, int height) {
        return new Portal(x, y, width, height, true);
    }

    /**
     * Creates a portal exit with default exit color (purple).
     *
     * @param x      Top-left x coordinate
     * @param y      Top-left y coordinate
     * @param width  Portal width
     * @param height Portal height
     * @return A new Portal exit instance
     */
    public static Portal createPortalExit(int x, int y, int width, int height) {
        return new Portal(x, y, width, height, false);
    }

    /**
     * Creates a linked portal pair (entrance linked to exit).
     *
     * @param entranceX Top-left x coordinate of entrance
     * @param entranceY Top-left y coordinate of entrance
     * @param exitX     Top-left x coordinate of exit
     * @param exitY     Top-left y coordinate of exit
     * @param width     Portal width (same for both)
     * @param height    Portal height (same for both)
     * @return Array of [entrance, exit] Portal instances, already linked
     */
    public static Portal[] createLinkedPortalPair(int entranceX, int entranceY, 
                                                   int exitX, int exitY, 
                                                   int width, int height) {
        Portal entrance = createPortalEntrance(entranceX, entranceY, width, height);
        Portal exit = createPortalExit(exitX, exitY, width, height);
        entrance.linkToExit(exit);
        return new Portal[]{entrance, exit};
    }
}
