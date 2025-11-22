package com.tron_master.demo.view.game;

import java.util.List;

import com.tron_master.demo.view.Shape;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * View class responsible for rendering Player objects
 */
public class PlayerRenderer {
    
    /**
     * Draw player and their trail
     * @param gc JavaFX graphics context
     */
    public void drawPlayer(GraphicsContext gc, Color color, int x, int y,
                           int width, int height, List<Shape> path) {
        // Draw player body
        gc.setFill(color);
        gc.fillRect(x - (double)width/2, y - (double)height/2, width, height);

        // Draw player trail
        for (Shape shape : path) {
            shape.draw(gc, color);
        }
    }
}