package com.tron_master.tron.model.data;

/**
 * The results of an intersection test: no intersection, or the direction in
 * which the intersection happened.
 * 
 */
public enum Intersection {
    /** No intersection detected. */
    NONE,
    /** Intersection from above. */
    UP,
    /** Intersection from the left. */
    LEFT,
    /** Intersection from below. */
    DOWN,
    /** Intersection from the right. */
    RIGHT
}
