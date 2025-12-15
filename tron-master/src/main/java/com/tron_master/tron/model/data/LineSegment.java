package com.tron_master.tron.model.data;

/**
 * Represents a line segment defined by two endpoints.
 * This is a pure data class in the model layer, without any view dependencies.
 * Used for player trails and collision detection.
 */
public class LineSegment {
    
    private final int startX;
    private final int startY;
    private final int endX;
    private final int endY;
    
    /**
     * Creates a new line segment.
     * @param startX Starting X coordinate
     * @param startY Starting Y coordinate
     * @param endX Ending X coordinate
     * @param endY Ending Y coordinate
     */
    public LineSegment(int startX, int startY, int endX, int endY) {
        this.startX = startX;
        this.startY = startY;
        this.endX = endX;
        this.endY = endY;
    }

    /**
     * Determine if the segment is vertical.
     * @return true if the segment is vertical
     */
    public boolean isVertical() {
        return startX == endX;
    }

    /**
     * Determine if the segment is horizontal.
     * @return true if the segment is horizontal
     */
    public boolean isHorizontal() {
        return startY == endY;
    }
    
    /**
     * Get starting X coordinate.
     * @return starting X coordinate
     */
    public int getStartX() {
        return startX;
    }
    
    /**
     * Get starting Y coordinate.
     * @return starting Y coordinate
     */
    public int getStartY() {
        return startY;
    }
    
    /**
     * Get ending X coordinate.
     * @return ending X coordinate
     */
    public int getEndX() {
        return endX;
    }
    
    /**
     * Get ending Y coordinate.
     * @return ending Y coordinate
     */
    public int getEndY() {
        return endY;
    }
    
    /**
     * Gets the minimum X coordinate of this segment.
     * @return Minimum X value
     */
    public int getMinX() {
        return Math.min(startX, endX);
    }
    
    /**
     * Gets the maximum X coordinate of this segment.
     * @return Maximum X value
     */
    public int getMaxX() {
        return Math.max(startX, endX);
    }
    
    /**
     * Gets the minimum Y coordinate of this segment.
     * @return Minimum Y value
     */
    public int getMinY() {
        return Math.min(startY, endY);
    }
    
    /**
     * Gets the maximum Y coordinate of this segment.
     * @return Maximum Y value
     */
    public int getMaxY() {
        return Math.max(startY, endY);
    }
}
