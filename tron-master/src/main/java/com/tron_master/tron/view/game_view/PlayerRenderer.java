package com.tron_master.tron.view.game_view;

import java.util.List;

import com.tron_master.tron.model.data.ColorValue;
import com.tron_master.tron.view.utils.Line;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * View class responsible for rendering Player objects.
 */
public class PlayerRenderer {

    /** Default renderer constructor. */
    public PlayerRenderer() {}

    /**
     * Draw player and their trail.
     * @param gc JavaFX graphics context
     * @param colorValue player color
     * @param x player x coordinate
     * @param y player y coordinate
     * @param width player width
     * @param height player height
     * @param path trail segments to draw
     */
    public void drawPlayer(GraphicsContext gc, ColorValue colorValue, int x, int y,
                       int width, int height, List<Line> path) {
        Color fxColor = toFxColor(colorValue);
        gc.setFill(fxColor);
        gc.fillRect(x - (double)width/2, y - (double)height/2, width, height);

        for (Line segment : path) {
            segment.draw(gc, fxColor);
        }
    }

    private Color toFxColor(ColorValue colorValue) {
        return Color.color(colorValue.red(), colorValue.green(), colorValue.blue());
    }
}
