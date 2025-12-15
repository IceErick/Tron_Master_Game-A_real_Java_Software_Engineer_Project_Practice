package com.tron_master.tron.view.utils;

import com.tron_master.tron.model.data.LineSegment;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * View layer line class for rendering.
 * Extends LineSegment (model layer) to add drawing capability.
 */
public class Line extends LineSegment implements Shape {
    
    /**
     * Create a line from start/end coordinates.
     * @param startX start x
     * @param startY start y
     * @param endX end x
     * @param endY end y
     */
    public Line(int startX, int startY, int endX, int endY) {
        super(startX, startY, endX, endY);
    }
    
    /**
     * Conversion constructor: creates a Line from a LineSegment.
     * @param segment The LineSegment to convert
     */
    public Line(LineSegment segment) {
        super(segment.getStartX(), segment.getStartY(), 
              segment.getEndX(), segment.getEndY());
    }
    
    @Override
    public void draw(GraphicsContext gc, Color color) {
        gc.setLineWidth(1.0);
        gc.setStroke(color);
        gc.strokeLine(getStartX(), getStartY(), getEndX(), getEndY());
    }
}
