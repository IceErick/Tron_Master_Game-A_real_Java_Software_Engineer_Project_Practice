package com.tron_master.tron.view.utils;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Shape drawn on the game court.
 * Implementations render themselves using a JavaFX {@link GraphicsContext}.
 */
public interface Shape {

    /**
     * Draws the shape using the provided JavaFX graphics context and color.
     *
     * @param gc    the GraphicsContext to draw on
     * @param color the color to use when rendering
     */
    void draw(GraphicsContext gc, Color color);

    /**
     * Returns whether this shape is vertical.
     *
     * @return {@code true} if the shape is vertical, {@code false} otherwise
     */
    boolean isVertical();

    /**
     * Get starting X coordinate.
     * @return starting X coordinate
     */
    int getStartX();
    /**
     * Get starting Y coordinate.
     * @return starting Y coordinate
     */
    int getStartY();
    /**
     * Get ending X coordinate.
     * @return ending X coordinate
     */
    int getEndX();
    /**
     * Get ending Y coordinate.
     * @return ending Y coordinate
     */
    int getEndY();
}
