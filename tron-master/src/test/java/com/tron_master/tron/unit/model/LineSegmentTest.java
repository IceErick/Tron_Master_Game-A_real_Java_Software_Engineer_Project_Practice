package com.tron_master.tron.unit.model;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;

import com.tron_master.tron.model.data.LineSegment;

/**
 * Unit tests for LineSegment class.
 * Tests geometric properties used in collision detection.
 */
class LineSegmentTest {

    @Test
    void isVertical_returnsTrueWhenXCoordinatesAreEqual() {
        LineSegment vertical = new LineSegment(10, 0, 10, 100);
        assertTrue(vertical.isVertical());
    }

    @Test
    void isVertical_returnsFalseWhenXCoordinatesDiffer() {
        LineSegment horizontal = new LineSegment(0, 50, 100, 50);
        assertFalse(horizontal.isVertical());
    }

    @Test
    void isHorizontal_returnsTrueWhenYCoordinatesAreEqual() {
        LineSegment horizontal = new LineSegment(0, 50, 100, 50);
        assertTrue(horizontal.isHorizontal());
    }

    @Test
    void isHorizontal_returnsFalseWhenYCoordinatesDiffer() {
        LineSegment vertical = new LineSegment(10, 0, 10, 100);
        assertFalse(vertical.isHorizontal());
    }

    @Test
    void getMinMax_returnsCorrectValuesWhenStartLessThanEnd() {
        LineSegment segment = new LineSegment(10, 20, 100, 200);
        
        assertEquals(10, segment.getMinX());
        assertEquals(100, segment.getMaxX());
        assertEquals(20, segment.getMinY());
        assertEquals(200, segment.getMaxY());
    }

    @Test
    void getMinMax_returnsCorrectValuesWhenStartGreaterThanEnd() {
        LineSegment segment = new LineSegment(100, 200, 10, 20);
        
        assertEquals(10, segment.getMinX());
        assertEquals(100, segment.getMaxX());
        assertEquals(20, segment.getMinY());
        assertEquals(200, segment.getMaxY());
    }

    @Test
    void zeroLengthSegment_isVerticalAndHorizontal() {
        LineSegment point = new LineSegment(50, 50, 50, 50);
        
        assertTrue(point.isVertical());
        assertTrue(point.isHorizontal());
        assertEquals(50, point.getMinX());
        assertEquals(50, point.getMaxX());
    }
}
