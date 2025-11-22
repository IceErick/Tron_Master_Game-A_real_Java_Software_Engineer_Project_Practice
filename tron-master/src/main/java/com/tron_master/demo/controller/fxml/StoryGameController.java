package com.tron_master.demo.controller.fxml;

import java.io.IOException;

import com.tron_master.demo.Game;
import com.tron_master.demo.constant.GameConstant;
import com.tron_master.demo.controller.PlayerController;
import com.tron_master.demo.model.GameData;
import com.tron_master.demo.model.GameState;
import com.tron_master.demo.model.logic_strategy.StoryLogicStrategy;
import com.tron_master.demo.model.player.Player;
import com.tron_master.demo.view.game.PlayerRenderer;
import com.tron_master.demo.view.game.StoryViewStrategy;
import com.tron_master.demo.view.utils.ViewUtils;

import javafx.animation.AnimationTimer;
import javafx.animation.PauseTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;
import javafx.util.Duration;

/**
 * Story mode controller
 */
public class StoryGameController {

    @FXML
    private Pane gameAreaContainer;
    @FXML
    private Text scoreText;
    @FXML
    private Text levelText;
    @FXML
    private Button resetBtn;
    @FXML
    private Button exitBtn;

    private StoryViewStrategy gameArea;
    private StoryLogicStrategy gameLogic;
    private Player[] players;
    private PlayerController playerController;
    private AnimationTimer timer;

    @FXML
    public void initialize() {

        // 1. Initialize game area and add to container
        gameArea = new StoryViewStrategy();
        gameAreaContainer.getChildren().add(gameArea);

        // 2. Set button images
        resetBtn.setGraphic(ViewUtils.createScaledImageView(GameConstant.BTN_RESTART, -1, -1));
        exitBtn.setGraphic(ViewUtils.createScaledImageView(GameConstant.BTN_PLAY, -1, -1));

        // 3. Ensure game area is correctly positioned in container
        gameArea.setLayoutX(0);
        gameArea.setLayoutY(0);

        // 4. Initialize game logic with 2 players (level 1 default)
        initializeGamePlayer(2);

        // 5. Initialize game timer
        initializeGameTimer();

        startGame();

        // 6. Initial UI update
        updateUI();
    }

    /**
     * Initialize game player with specified count
     */
    private void initializeGamePlayer(int playerCount) {
        // Create game logic with specified player count
        gameLogic = new StoryLogicStrategy(playerCount);

        // Get players from game logic
        players = gameLogic.getPlayers();

        // Initialize player controller
        playerController = new PlayerController(players, new PlayerRenderer(), gameArea.getGraphicsContext2D());

        // Connect GameArea with PlayerController for keyboard event handling
        playerController.setGameArea(gameArea);
    }

    /**
     * Initialize game timer
     */
    private void initializeGameTimer() {
        timer = new AnimationTimer() {
            private long lastUpdate = 0;

            @Override
            public void handle(long now) {
                if (now - lastUpdate >= 20_000_000) { // 20ms interval
                    if (gameLogic.getRun()) {
                        gameTick();
                    }
                    lastUpdate = now;
                }
            }
        };
    }

    private void startGame() {
        gameLogic.setRun(true);
        if (timer != null) {
            timer.start();
        }
        gameArea.requestFocus(); // Ensure game area has focus for keyboard input
    }

    /**
     * Main game loop tick
     */
    private void gameTick() {
        // Update game logic
        gameLogic.tick(timer);

        // Check game state
        if (!gameLogic.isRunning()) {
            // Game is not running, check why
            handleGameEnd();
        }

        // Update UI
        updateUI();

        // Render game
        playerController.renderPlayers();
    }

    /**
     * Handle game end (level complete or game over)
     */
    private void handleGameEnd() {
        // Stop the game timer
        gameLogic.setRun(false);
        playerController.renderPlayers();
        playerController.setRenderingEnabled(false);

        if (gameLogic.getStoryState() == GameState.LEVEL_COMPLETE) {
            gameArea.showLevelCompleteScreen();

            // Delay before next level
            PauseTransition delay = new PauseTransition(Duration.seconds(1.5));
            delay.setOnFinished(event -> {
                nextLevel();
            });
            delay.play();
        } else if (gameLogic.getStoryState() == GameState.GAME_OVER) {
            gameArea.showGameOverScreen();
        }
    }

    /**
     * Proceed to next level: increase player count, keep score, reset game state
     */
    private void nextLevel() {
        // 1. Get current game state
        int currentLevel = gameLogic.getStoryLevel();
        int currentScore = gameLogic.getStoryScore(); // Save current score
        if (currentLevel == 7) {
            gameArea.showLevelCompleteScreen();
            return;
        }
        int newLevel = currentLevel + 1;
        int newPlayerCount = newLevel + 1; // Player count increases with level (level1:2, level2:3...)

        // 2. Update game data (keep score, update level)
        GameData gameData = GameData.getInstance();
        gameData.setStoryLevel(newLevel);
        gameData.setStoryScore(currentScore); // Preserve score
        gameData.setStoryState(GameState.PLAYING);

        // 3. Stop current timer
        if (timer != null) {
            timer.stop();
        }

        // 4. Reinitialize game with new player count
        initializeGamePlayer(newPlayerCount);

        // 5. Reset view (keep background, reset characters and effects)
        gameArea.reset();

        // 6. Restart game loop
        initializeGameTimer();
        startGame();

        // 7. Update UI with new level
        updateUI();

        // 8. Enable rendering
        playerController.setRenderingEnabled(true);
    }

    public Scene createStoryScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tron_master/demo/fxml/story_game.fxml"));
            loader.setController(this);
            BorderPane rootPane = loader.load();

            bindButtonEvents();

            Scene scene = new Scene(rootPane, GameConstant.WINDOW_WIDTH, GameConstant.WINDOW_HEIGHT);
            gameArea.requestFocus();

            return scene;
        } catch (IOException e) {
            System.err.println("Story mode FXML loading failed!");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    // ------------------- Button events -------------------
    private void bindButtonEvents() {
        resetBtn.setOnAction(event -> onResetBtnClick());
        exitBtn.setOnAction(event -> onExitBtnClick());
    }

    @FXML
    public void onResetBtnClick() {
        // 1. Stop current timer
        if (timer != null) {
            timer.stop();
        }

        // 2. Reset game data to level 1 with 2 players
        GameData gameData = GameData.getInstance();
        gameData.resetStoryData(); // Resets to level 1, score 0, etc.

        // 3. Reinitialize with 2 players (level 1 default)
        initializeGamePlayer(2);

        // 4. Reset view
        gameArea.reset();

        // 5. Restart game loop
        initializeGameTimer();
        startGame();

        // 6. Update UI
        updateUI();

        // 7. Reset player controller and focus
        playerController.setRenderingEnabled(true);
        gameArea.requestFocus();
    }

    @FXML
    public void onExitBtnClick() {
        PlayMenuController controller = new PlayMenuController();
        Game.getPrimaryStage().setScene(controller.createPlayMenuScene());
    }

    /**
     * Uniformly update UI display
     */
    private void updateUI() {
        if (scoreText != null && levelText != null) {
            scoreText.setText(String.format("Score: %d    Boost: %d",
                    gameLogic.getStoryScore(), gameLogic.getBoostCount()));
            levelText.setText(String.format("Level: %d", gameLogic.getStoryLevel()));
        }
    }
}