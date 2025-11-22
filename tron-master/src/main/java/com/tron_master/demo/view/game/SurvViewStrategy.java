package com.tron_master.demo.view.game;

import com.tron_master.demo.constant.GameConstant;
import com.tron_master.demo.model.GameData;
import javafx.scene.canvas.GraphicsContext;

/**
 * Survival mode game area (implements core survival mode logic)
 */
public class SurvViewStrategy extends GameArea {
    private GameData gameData;
    private int boostCount; // Number of boosts

    public SurvViewStrategy() {
        super();
        this.gameData = GameData.getInstance();
        this.boostCount = GameConstant.INIT_BOOST_COUNT;
    }

    @Override
    protected void initGame() {
        drawBackground(); // Draw black background
        // TODO: Add original survival mode logic (character initialization, map generation, game loop, etc.)
    }

    @Override
    public void reset() {
        // Reset game state (score, boost count, background)
        gameData.resetSurvivalData();
        this.boostCount = GameConstant.INIT_BOOST_COUNT;
        drawBackground();
        // TODO: Add original reset logic (character position, map reset, etc.)
    }

    @Override
    public void showGameOverScreen() {

    }

    // ------------------- External Data Access -------------------
    public int getScore() {
        return gameData.getSurvivalScore();
    }

    public int getBoostCount() {
        return boostCount;
    }
}