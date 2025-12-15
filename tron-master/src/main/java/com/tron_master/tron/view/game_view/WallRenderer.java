package com.tron_master.tron.view.game_view;

import com.tron_master.tron.model.data.ColorValue;
import com.tron_master.tron.model.object.Wall;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Renders wall objects on the game canvas.
 * Provides visual styling for walls with glow effects (just in case for the color changeable background).
 */
public class WallRenderer {

    /** Default renderer constructor. */
    public WallRenderer() {}

    /** Glow effect size in pixels */
    private static final double GLOW_SIZE = 3.0;

    /**
     * Draws a wall on the canvas with optional glow effect.
     *
     * @param gc   GraphicsContext for drawing
     * @param wall The wall to render
     */
    public void drawWall(GraphicsContext gc, Wall wall) {
        if (wall == null || !wall.getAlive()) {
            return;
        }

        ColorValue colorValue = wall.getColor();
        Color fxColor = toFxColor(colorValue);

        int x = wall.getX();
        int y = wall.getY();
        int width = wall.getWidth();
        int height = wall.getHeight();

        // Draw glow effect (semi-transparent outer layer)
        gc.setFill(Color.color(fxColor.getRed(), fxColor.getGreen(), fxColor.getBlue(), 0.3));
        gc.fillRect(x - GLOW_SIZE, y - GLOW_SIZE, 
                    width + GLOW_SIZE * 2, height + GLOW_SIZE * 2);

        // Draw main wall body
        gc.setFill(fxColor);
        gc.fillRect(x, y, width, height);

        // Draw inner highlight for 3D effect
        gc.setFill(Color.color(
            Math.min(1.0, fxColor.getRed() + 0.2),
            Math.min(1.0, fxColor.getGreen() + 0.2),
            Math.min(1.0, fxColor.getBlue() + 0.2),
            0.5
        ));
        gc.fillRect(x + 2, y + 2, width - 4, height - 4);
    }

    /**
     * Draws multiple walls on the canvas.
     *
     * @param gc    GraphicsContext for drawing
     * @param walls Array of walls to render
     */
    public void drawWalls(GraphicsContext gc, Wall[] walls) {
        if (walls == null) {
            return;
        }
        for (Wall wall : walls) {
            drawWall(gc, wall);
        }
    }

    /**
     * Converts model ColorValue to JavaFX Color.
     *
     * @param colorValue Model color value
     * @return JavaFX Color
     */
    private Color toFxColor(ColorValue colorValue) {
        return Color.color(colorValue.red(), colorValue.green(), colorValue.blue());
    }
}
