package com.tron_master.tron.controller.game_controller;

import com.tron_master.tron.constant.GameConstant;
import com.tron_master.tron.controller.PlayerController;
import com.tron_master.tron.model.data.GameData;
import com.tron_master.tron.model.data.GameState;
import com.tron_master.tron.model.data.HighScoreManager;
import com.tron_master.tron.model.logic_strategy.StoryLogicStrategy;
import com.tron_master.tron.model.object.Player;
import com.tron_master.tron.view.game_view.GameArea;
import com.tron_master.tron.view.game_view.StoryViewStrategy;

import javafx.animation.PauseTransition;
import javafx.scene.Scene;
import javafx.util.Duration;

/**
 * Controller for story mode game scene.
 * Extends AbstractGameController using Template Method Pattern.
 */
public class StoryGameController extends AbstractGameController {

    /** Default constructor for JavaFX. */
    public StoryGameController() {}

    private StoryViewStrategy gameArea;
    private StoryLogicStrategy gameLogic;
    /** Delay transition for level complete - needs to be cancelled on reset */
    private PauseTransition levelTransition;

    // ==================== Template Method Implementations ====================

    @Override
    protected Player[] getPlayers() {
        return gameLogic != null ? gameLogic.getPlayers() : new Player[0];
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
        // Centralized scene rendering in GameArea
        gameArea.renderScene(getPlayers());
    }

    @Override
    protected void updateUI() {
        if (gameArea != null && gameLogic != null) {
            gameArea.updateScoreDisplay(
                gameLogic.getStoryScore(),
                gameLogic.getBoostCount(),
                gameLogic.getStoryLevel()
            );
        }
    }

    @Override
    protected void handleGameEnd() {
        playerController.setRenderingEnabled(false);
        GameState state = gameLogic.getStoryState();
        int score = gameLogic.getStoryScore();

        switch (state) {
            case LEVEL_COMPLETE -> {
                gameArea.showLevelCompleteScreen();
                levelTransition = new PauseTransition(Duration.seconds(1.5));
                levelTransition.setOnFinished(_ -> nextLevel());
                levelTransition.play();
            }
            case VICTORY -> {
                HighScoreManager.getInstance().addScore(score, HighScoreManager.GameMode.STORY);
                gameArea.showLevelCompleteScreen(score);
            }
            case GAME_OVER -> {
                HighScoreManager.getInstance().addScore(score, HighScoreManager.GameMode.STORY);
                gameArea.showGameOverScreen(score);
            }
            default -> {}
        }
    }

    @Override
    protected void doReset() {
        // Cancel pending level transition to prevent unexpected nextLevel() call
        if (levelTransition != null) {
            levelTransition.stop();
            levelTransition = null;
        }
        unregisterPlayerListeners();
        GameData.getInstance().resetStoryData();
        initializeGameLogic(StoryLogicStrategy.getInitialPlayerCount());
        gameArea.setWalls(gameLogic.getWalls());
        gameArea.setPortals(gameLogic.getPortals());
        gameArea.reset();
        updateUI();
    }

    @Override
    protected void beforeGameStart() {
        gameLogic.setRun(true);
    }

    // ==================== Story Mode Specific ====================

    /**
     * Create and return the story mode game scene.
     * @return story mode Scene
     */
    public Scene createStoryScene() {
        gameArea = new StoryViewStrategy();
        Scene scene = gameArea.createScene(this);
        initialize(gameArea);
        return scene;
    }

    /**
     * Initialize the story game and start gameplay.
     * @param view story view strategy instance
     */
    public void initialize(StoryViewStrategy view) {
        this.gameArea = view;
        initializeGameLogic(StoryLogicStrategy.getInitialPlayerCount());
        gameArea.setWalls(gameLogic.getWalls());
        gameArea.setPortals(gameLogic.getPortals());
        initializeGameTimer();
        startGame();
        updateUI();
    }

    private void initializeGameLogic(int playerCount) {
        gameLogic = new StoryLogicStrategy(playerCount, GameConstant.GAME_AREA_WIDTH, GameConstant.GAME_AREA_HEIGHT);
        playerController = new PlayerController(gameLogic.getPlayers());
        playerController.setGameArea(gameArea);
        registerPlayerListeners();
    }

    /**
     * Proceed to next level.
     */
    private void nextLevel() {
        if (!gameLogic.nextLevel()) {
            gameArea.showLevelCompleteScreen();
            return;
        }
        stopGame();
        unregisterPlayerListeners();
        initializeGameLogic(gameLogic.getCurrentLevelPlayerCount());
        gameArea.setWalls(gameLogic.getWalls());
        gameArea.reset();
        initializeGameTimer();
        startGame();
        updateUI();
        playerController.setRenderingEnabled(true);
    }
}
