package com.tron_master.tron.controller.game_controller;

import com.tron_master.tron.constant.GameConstant;
import com.tron_master.tron.controller.PlayerController;
import com.tron_master.tron.model.data.GameState;
import com.tron_master.tron.model.logic_strategy.TwoPlayerLogicStrategy;
import com.tron_master.tron.model.object.Player;
import com.tron_master.tron.model.sound.SoundManager;
import com.tron_master.tron.view.game_view.GameArea;
import com.tron_master.tron.view.game_view.TwoPlayerViewStrategy;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.scene.Scene;
import javafx.util.Duration;

/**
 * Controller for two-player mode game scene.
 * Extends AbstractGameController using Template Method Pattern.
 * Adds countdown feature before game starts.
 */
public class TwoPlayerGameController extends AbstractGameController {

    /** Default constructor for JavaFX. */
    public TwoPlayerGameController() {}

    private TwoPlayerViewStrategy gameArea;
    private TwoPlayerLogicStrategy gameLogic;
    private Timeline countdownTimeline;

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
        gameArea.setWalls(gameLogic.getWalls());
        gameArea.renderScene(getPlayers());
    }

    @Override
    protected void updateUI() {
        if (gameArea != null && gameLogic != null) {
            gameArea.updateScoresDisplay(
                gameLogic.getP1Score(),
                gameLogic.getP1BoostCount(),
                gameLogic.getP2Score(),
                gameLogic.getP2BoostCount()
            );
        }
    }

    @Override
    protected void handleGameEnd() {
        playerController.setRenderingEnabled(false);
        if (gameLogic.getTwoPlayerState() == GameState.GAME_OVER) {
            gameArea.showGameOverScreen(gameLogic.getP1Score(), gameLogic.getP2Score());
        }
    }

    @Override
    protected void doReset() {
        if (countdownTimeline != null) {
            countdownTimeline.stop();
        }
        unregisterPlayerListeners();
        gameLogic.reset();
        gameArea.setWalls(gameLogic.getWalls());
        gameArea.reset();
        initializePlayerController();
        gameArea.renderScene(getPlayers());
        startCountdown();
        updateUI();
    }

    // Override to not start immediately - we use countdown instead
    @Override
    public void onResetBtnClick() {
        SoundManager.getInstance().playSoundEffect("reset");
        stopGame();
        doReset();
        // Note: setRenderingEnabled is now handled in startCountdown()
        getGameArea().requestFocus();
    }

    // ==================== Two Player Mode Specific ====================

    /**
     * Create and return the two-player mode game scene.
     * @return two-player Scene
     */
    public Scene createTwoPlayerScene() {
        gameArea = new TwoPlayerViewStrategy();
        Scene scene = gameArea.createScene(this);
        initialize(gameArea);
        return scene;
    }

    /**
     * Initialize the two-player game with countdown.
     * @param view two-player view strategy
     */
    public void initialize(TwoPlayerViewStrategy view) {
        this.gameArea = view;
        gameLogic = new TwoPlayerLogicStrategy(GameConstant.GAME_AREA_WIDTH, GameConstant.GAME_AREA_HEIGHT);
        initializePlayerController();
        gameArea.setWalls(gameLogic.getWalls());
        initializeGameTimer();
        
        // Render initial state before countdown
        gameArea.renderScene(getPlayers());
        
        startCountdown();
        updateUI();
    }

    private void initializePlayerController() {
        playerController = new PlayerController(gameLogic.getPlayers());
        playerController.setGameArea(gameArea);
        registerPlayerListeners();
    }

    /**
     * Start 3-second countdown before game begins.
     */
    private void startCountdown() {
        // Disable player input during countdown
        playerController.setRenderingEnabled(false);
        
        final int[] countdown = {3};
        gameArea.showCountdown(countdown[0]);
        SoundManager.getInstance().playSoundEffect("clic");

        countdownTimeline = new Timeline(new KeyFrame(Duration.seconds(1), _ -> {
            countdown[0]--;
            if (countdown[0] > 0) {
                gameArea.showCountdown(countdown[0]);
                SoundManager.getInstance().playSoundEffect("clic");
            } else {
                gameArea.hideCountdown();
                // Enable player input when game starts
                playerController.setRenderingEnabled(true);
                startGame();
            }
        }));
        countdownTimeline.setCycleCount(3);
        countdownTimeline.play();
        gameArea.requestFocus();
    }

    @Override
    public void onExitBtnClick() {
        if (countdownTimeline != null) {
            countdownTimeline.stop();
        }
        super.onExitBtnClick();
    }
}
