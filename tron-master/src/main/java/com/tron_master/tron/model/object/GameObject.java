package com.tron_master.tron.model.object;

import java.util.ArrayList;

import com.tron_master.tron.model.data.Intersection;
import com.tron_master.tron.view.utils.Line;

/**
 * Base class for renderable/movable game entities.
 * Provides position, velocity, bounds, and collision helpers.
 */
public abstract class GameObject {
	int x;
	int y;

	int width;
	int height;

	int velocityX;
	int velocityY;

	int rightBound;
	int bottomBound;

	/**
	 * Get current x coordinate.
	 * @return x (upper-left)
	 */
	public int getX() {
		return x;
	}

	/**
	 * Get current y coordinate.
	 * @return y (upper-left)
	 */
	public int getY() {
		return y;
	}

	/**
	 * Get object width.
	 * @return width in pixels
	 */
	public int getWidth() {
		return width;
	}

	/**
	 * Get object height.
	 * @return height in pixels
	 */
	public int getHeight() {
		return height;
	}

	/**
	 * Construct a game object with position, velocity and dimensions.
	 * @param x starting x coordinate
	 * @param y starting y coordinate
	 * @param velocityX starting velocity in x
	 * @param velocityY starting velocity in y
	 * @param width object width
	 * @param height object height
	 */
	public GameObject(int x, int y, int velocityX, int velocityY, int width,
					  int height) {
		this.x = x;
		this.y = y;
		this.velocityX = velocityX;
		this.velocityY = velocityY;
		this.width = width;
		this.height = height;
	}
	
	/**
	 * Set the allowable bounds for this object.
	 * @param width max width
	 * @param height max height
	 */
	public void setBounds(int width, int height) {
		rightBound = width - this.width;
		bottomBound = height - this.height;
	}
	
	/**
	 * Set horizontal velocity unless reversing direction.
	 * @param velocityX new velocity
	 */
	public void setVelocityX(int velocityX) {
		if (!(velocityX > 0 && this.velocityX < 0)
				&& !(velocityX < 0 && this.velocityX > 0)) {
			this.velocityX = velocityX;
		}
	}
	
	/**
	 * Set vertical velocity unless reversing direction.
	 * @param velocityY new velocity
	 */
	public void setVelocityY(int velocityY) {
		if (!(velocityY > 0 && this.velocityY < 0)
				&& !(velocityY < 0 && this.velocityY > 0)) {
			this.velocityY = velocityY;
		}
	}

	/** Advance position by velocity and clamp to bounds. */
	public void move() {
		x += velocityX;
		y += velocityY;

		accelerate();
		clip();
	}

	/** Clamp the object inside the play area. */
	public void clip() {
		int leftMargin = 5;
		if (x < leftMargin)
			x = leftMargin;
		else if (x > rightBound)
			x = rightBound;

		int topMargin = 5;
		if (y < topMargin)
			y = topMargin;
		else if (y > bottomBound)
			y = bottomBound;
	}

	/**
	 * Compute whether an object intersects a shape.
	 * 
	 * @param other
	 *            The other game object to test for intersection with.
	 * @return NONE if the objects do not intersect, otherwise UP.
	 * 
	 */
	public Intersection intersects(GameObject other) {
		if (other != this) {
			if (other.y - other.height/2 <= y + height/2 &&
				other.y + other.height/2 >= y - height/2 &&
				other.x - other.width/2 <= x + width/2 &&
				other.x + other.width/2 >= x - width/2) {
				return Intersection.UP;
			}
		}
		ArrayList<Line> pa = other.getPath();
		for (int i = 0; i < pa.size() - 1; i++) {
			Line k = pa.get(i);
			int x1 = k.getStartX();
			int y1 = k.getStartY();
			int x2 = k.getEndX();
			int y2 = k.getEndY();

			if (y1 == y2) {
				if (Math.abs(y1 - y) <= height/2 && 
					(x >= Math.min(x1, x2) && x <= Math.max(x1, x2))) {
					return Intersection.UP;
				}
			} else if (x1 == x2) {
				if (Math.abs(x1 - x) <= width/2 &&
					(y >= Math.min(y1, y2) && y <= Math.max(y1, y2))) {					
					return Intersection.UP;
				}
			}
		}
		return Intersection.NONE;
	}
	
	// checks if an object has crossed the bounds of the screen
	/**
	 * Check and respond to leaving the play bounds.
	 */
	public abstract void accelerate();
	
	// returns true if the player is alive
	/**
	 * Check whether this object is alive/active.
	 * @return true if active
	 */
	public abstract boolean getAlive();
	
	// returns the player's path as a list of shapes
	/**
	 * Get trail/path segments for collision checks.
	 * @return list of path segments
	 */
	public abstract ArrayList<Line> getPath();
}
