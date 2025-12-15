package com.tron_master.tron.model.object;

import com.tron_master.tron.model.data.ColorValue;
import com.tron_master.tron.view.utils.Line;

/**
 * Human-controlled player implementation.
 */
public class PlayerHuman extends Player {

	/**
	 * Human player constructor with start position and velocity.
	 * @param randX x coordinate
	 * @param randY y coordinate
	 * @param velx starting velocity x
	 * @param vely starting velocity y
	 * @param color player color
	 * @param mode game mode ("story", "survival", "twoPlayer")
	 */
	public PlayerHuman(int randX, int randY, int velx, int vely, ColorValue color, String mode) {
		super(randX, randY, velx, vely, color, mode);
	}
	
	@Override
	public Boolean isHuman() {
		return true;
	}

	// does nothing because human players can see screen
	// only needed for AI, but required for abstract class
	public void addPlayers(Player[] players) {
	}
	
	// moves the Player based on its conditions
	public void move() {
		int a = x;
		int b = y;
		boost();
		
		if (!jumping) {
			x += velocityX;
			y += velocityY;
			if (getPath().size() > 1) {
				Line l1 = getPath().get(getPath().size() - 2);
				Line l2 = getPath().get(getPath().size() - 1);
				if (a == l1.getStartX() &&
						l1.getEndY() == l2.getStartY()) {
					getPath().add(new Line(l1.getStartX(), l1.getStartY(),
							l2.getEndX(), l2.getEndY()));
					getPath().remove(getPath().size() - 2);
					getPath().remove(getPath().size() - 2);
				} else if (b == l1.getStartY() &&
						l1.getEndX() == l2.getStartX()) {
					getPath().add(new Line(l1.getStartX(), l1.getStartY(),
							l2.getEndX(), l2.getEndY()));
					getPath().remove(getPath().size() - 2);
					getPath().remove(getPath().size() - 2);
				}
			}
			getPath().add(new Line(a, b, x, y));
		} else {
			if (velocityX > 0) {
				x += JUMPHEIGHT;
			} else if (velocityX < 0) {
				x -= JUMPHEIGHT;
			} else if (velocityY > 0) {
				y += JUMPHEIGHT;
			} else if (velocityY < 0) {
				y -= JUMPHEIGHT;
			}
			jumping = false;
		}
		accelerate();
		clip();
	}
	
}
