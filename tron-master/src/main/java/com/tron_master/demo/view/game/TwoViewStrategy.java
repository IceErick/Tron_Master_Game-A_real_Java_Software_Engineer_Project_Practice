package com.tron_master.demo.view.game;

import com.tron_master.demo.constant.GameConstant;
import com.tron_master.demo.model.GameData;
import javafx.scene.canvas.GraphicsContext;

/**
 * Two-player mode game area (Player 1: arrow keys, Player 2: WASD)
 */
public class TwoViewStrategy extends GameArea {
    private GameData gameData;
    private int p1BoostCount; // Player 1 boost count
    private int p2BoostCount; // Player 2 boost count

    public TwoViewStrategy() {
        super();
        this.gameData = GameData.getInstance();
        this.p1BoostCount = GameConstant.INIT_BOOST_COUNT;
        this.p2BoostCount = GameConstant.INIT_BOOST_COUNT;
    }

    @Override
    protected void initGame() {
        drawBackground();
        // TODO: Add two-player mode logic (two character initialization, collision detection, etc.)
    }

    @Override
    public void reset() {
        gameData.resetTwoPlayerData();
        this.p1BoostCount = GameConstant.INIT_BOOST_COUNT;
        this.p2BoostCount = GameConstant.INIT_BOOST_COUNT;
        drawBackground();
        // TODO: Add two-player mode reset logic
    }

    @Override
    public void showGameOverScreen() {

    }

    // ------------------- Data Access -------------------
    public int getP1Score() {
        return gameData.getTwoPlayerP1Score();
    }

    public int getP2Score() {
        return gameData.getTwoPlayerP2Score();
    }

    public int getP1BoostCount() {
        return p1BoostCount;
    }

    public int getP2BoostCount() {
        return p2BoostCount;
    }
}