package com.tron_master.demo.model.player;

import com.tron_master.demo.view.Line;
import com.tron_master.demo.view.Shape;

import javafx.scene.paint.Color;

public class PlayerHuman extends Player {

	public PlayerHuman(int randX, int randY, int velx, int vely, Color color) {
		super(randX, randY, velx, vely, color);
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
				Shape l1 = getPath().get(getPath().size() - 2);
				Shape l2 = getPath().get(getPath().size() - 1);
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