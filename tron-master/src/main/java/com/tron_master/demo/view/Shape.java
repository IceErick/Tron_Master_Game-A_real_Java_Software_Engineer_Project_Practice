package com.tron_master.demo.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Interface for all shapes drawn on the game court
 * Allows for more shapes to be drawn if necessary
 * Refactored for JavaFX GraphicsContext
 */
public interface Shape {
    
    /**
     * Draw the shape using JavaFX GraphicsContext
     * @param gc The GraphicsContext to draw on
     */
    void draw(GraphicsContext gc, Color color);
    
    /**
     * Check if the shape is vertical (true) or horizontal (false)
     * @return true if vertical, false if horizontal
     */
    boolean isVertical();
    
    /**
     * Get the starting X coordinate
     * @return starting X coordinate
     */
    int getStartX();
    
    /**
     * Get the starting Y coordinate
     * @return starting Y coordinate
     */
    int getStartY();
    
    /**
     * Get the ending X coordinate
     * @return ending X coordinate
     */
    int getEndX();
    
    /**
     * Get the ending Y coordinate
     * @return ending Y coordinate
     */
    int getEndY();
}