package com.tron_master.tron.controller.game_controller;

import com.tron_master.tron.constant.GameConstant;
import com.tron_master.tron.controller.PlayerController;
import com.tron_master.tron.model.data.GameState;
import com.tron_master.tron.model.data.HighScoreManager;
import com.tron_master.tron.model.logic_strategy.SurvLogicStrategy;
import com.tron_master.tron.model.object.Player;
import com.tron_master.tron.view.game_view.GameArea;
import com.tron_master.tron.view.game_view.SurvViewStrategy;

import javafx.scene.Scene;

/**
 * Controller for survival mode game scene.
 * Extends AbstractGameController using Template Method Pattern.
 */
public class SurvivalGameController extends AbstractGameController {

    /** Default constructor for JavaFX. */
    public SurvivalGameController() {}

    private SurvViewStrategy gameArea;
    private SurvLogicStrategy gameLogic;

    // ==================== Template Method Implementations ====================

    @Override
    protected Player[] getPlayers() {
        return gameLogic != null ? new Player[]{gameLogic.getPlayer()} : new Player[0];
    }

    @Override
    protected GameArea getGameArea() {
        return gameArea;
    }

    @Override
    protected boolean isGameRunning() {
        return gameLogic != null && gameLogic.isRunning();
    }

    @Override
    protected void doGameTick() {
        gameLogic.tick(timer::stop);
    }

    @Override
    protected void render() {
        // Update walls/portals and render the whole scene
        gameArea.setWalls(gameLogic.getWalls());
        gameArea.setPortals(gameLogic.getPortals());
        gameArea.renderScene(getPlayers());
    }

    @Override
    protected void updateUI() {
        if (gameArea != null && gameLogic != null) {
            gameArea.updateScoreDisplay(
                gameLogic.getScore(),
                gameLogic.getBoostCount()
            );
        }
    }

    @Override
    protected void handleGameEnd() {
        playerController.setRenderingEnabled(false);
        if (gameLogic.getSurvivalState() == GameState.GAME_OVER) {
            int finalScore = gameLogic.getScore();
            HighScoreManager.getInstance().addScore(finalScore, HighScoreManager.GameMode.SURVIVAL);
            gameArea.showGameOverScreen(finalScore);
        }
    }

    @Override
    protected void doReset() {
        unregisterPlayerListeners();
        gameLogic.reset();
        gameArea.setWalls(gameLogic.getWalls());
        gameArea.setPortals(gameLogic.getPortals());
        gameArea.reset();
        initializePlayerController();
        updateUI();
    }

    // ==================== Survival Mode Specific ====================

    /**
     * Create and return the survival mode game scene.
     * @return survival mode Scene
     */
    public Scene createSurvivalScene() {
        gameArea = new SurvViewStrategy();
        Scene scene = gameArea.createScene(this);
        initialize(gameArea);
        return scene;
    }

    /**
     * Initialize the survival game and start gameplay.
     * @param view survival view strategy
     */
    public void initialize(SurvViewStrategy view) {
        this.gameArea = view;
        gameLogic = new SurvLogicStrategy(GameConstant.GAME_AREA_WIDTH, GameConstant.GAME_AREA_HEIGHT);
        initializePlayerController();
        gameArea.setWalls(gameLogic.getWalls());
        gameArea.setPortals(gameLogic.getPortals());
        initializeGameTimer();
        startGame();
        updateUI();
    }

    private void initializePlayerController() {
        playerController = new PlayerController(new Player[]{gameLogic.getPlayer()});
        playerController.setGameArea(gameArea);
        registerPlayerListeners();
    }
}
