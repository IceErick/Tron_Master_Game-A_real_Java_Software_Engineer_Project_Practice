package com.tron_master.demo.view;

import javafx.scene.canvas.GraphicsContext;
import javafx.scene.paint.Color;

/**
 * Implementation of Shape interface for line segments
 * Represents a straight line between two points
 */
public class Line implements Shape {
    
    private final int startX;
    private final int startY;
    private final int endX;
    private final int endY;
    
    public Line(int startX, int startY, int endX, int endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }
    
    @Override
    public void draw(GraphicsContext gc, Color color) {
        gc.setLineWidth(1.0); // Set line width for better visibility
        gc.setStroke(color); // Set stroke color
        gc.strokeLine(startX, startY, endX, endY);
    }
    
    @Override
    public boolean isVertical() {
        // Consider vertical if x coordinates are approximately equal
        return Math.abs(startX - endX) < 0.01;
    }
    
    @Override
    public int getStartX() {
        return startX;
    }
    
    @Override
    public int getStartY() {
        return startY;
    }
    
    @Override
    public int getEndX() {
        return endX;
    }
    
    @Override
    public int getEndY() {
        return endY;
    }
}