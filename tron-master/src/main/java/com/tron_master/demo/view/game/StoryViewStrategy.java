package com.tron_master.demo.view.game;

import com.tron_master.demo.constant.GameConstant;
import com.tron_master.demo.view.utils.ViewUtils;

import javafx.scene.image.Image;

/**
 * Story mode game area (level progress + score)
 */
public class StoryViewStrategy extends GameArea {
    private int boostCount;
    private PlayerRenderer playerRenderer;

    public StoryViewStrategy() {
        super();
        this.boostCount = GameConstant.INIT_BOOST_COUNT;
    }

    @Override
    protected void initGame() {
        drawBackground();
        // TODO: Add story mode logic (level loading, enemy AI, etc.)
    }

    // reset scores & game object status..
    @Override
    public void reset() {
        this.boostCount = GameConstant.INIT_BOOST_COUNT;
        drawBackground();
        // TODO: Add story mode reset logic
    }

    @Override
    public void showGameOverScreen() {
        // Load and draw the win image
        Image winImage = ViewUtils.loadImage("/images/over.png");

        // Calculate position to center the image
        double imageWidth = winImage.getWidth();
        double imageHeight = winImage.getHeight();
        double x = (GameConstant.GAME_AREA_WIDTH - imageWidth) / 2;
        double y = (GameConstant.GAME_AREA_HEIGHT - imageHeight) / 2;

        // Draw the image centered on the game area
        gc.drawImage(winImage, x, y);
    }

    public void showLevelCompleteScreen() {
        // Load and draw the win image
        Image winImage = ViewUtils.loadImage("/images/win.png");
        
        // Calculate position to center the image
        double imageWidth = winImage.getWidth();
        double imageHeight = winImage.getHeight();
        double x = (GameConstant.GAME_AREA_WIDTH - imageWidth) / 2;
        double y = (GameConstant.GAME_AREA_HEIGHT - imageHeight) / 2;
        
        // Draw the image centered on the game area
        gc.drawImage(winImage, x, y);
    }

    // ------------------- 3 Data Access -------------------

    public int getBoostCount() {
        return boostCount;
    }
}