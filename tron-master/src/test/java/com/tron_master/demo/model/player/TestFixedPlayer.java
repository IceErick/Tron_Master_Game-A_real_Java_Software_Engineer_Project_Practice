package com.tron_master.demo.model.player;

import com.tron_master.demo.view.Line;
import javafx.scene.paint.Color;

/**
 * Test helper for deterministic movement; keeps public setters for position/velocity.
 * Extends PlayerHuman to satisfy StoryLogicStrategy's player field type.
 */
public class TestFixedPlayer extends PlayerHuman {

    public TestFixedPlayer(int x, int y, int vx, int vy, Color color) {
        super(x, y, vx, vy, color);
    }

    @Override
    public void addPlayers(Player[] players) {
        // no-op for tests
    }

    // deterministic movement without path merging heuristics
    @Override
    public void move() {
        int startX = x;
        int startY = y;
        boost();
        if (jumping) {
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
        } else {
            x += velocityX;
            y += velocityY;
            getPath().add(new Line(startX, startY, x, y));
        }
        accelerate();
        clip();
    }

    public void setPosition(int newX, int newY) {
        this.x = newX;
        this.y = newY;
    }

    public void setVelocity(int vx, int vy) {
        this.velocityX = vx;
        this.velocityY = vy;
    }
}
