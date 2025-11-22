package com.tron_master.demo.model.player;

import com.tron_master.demo.view.Line;
import javafx.scene.paint.Color;

/**
 * AI-flavored fixed player with deterministic movement and isHuman=false.
 */
public class TestFixedAI extends Player {
    public TestFixedAI(int x, int y, int vx, int vy, Color color) {
        super(x, y, vx, vy, color);
    }

    @Override
    public Boolean isHuman() {
        return false;
    }

    @Override
    public void addPlayers(Player[] players) {
        // no-op for tests
    }

    @Override
    public void move() {
        int startX = x;
        int startY = y;
        boost();
        x += velocityX;
        y += velocityY;
        getPath().add(new Line(startX, startY, x, y));
        accelerate();
        clip();
    }
}
