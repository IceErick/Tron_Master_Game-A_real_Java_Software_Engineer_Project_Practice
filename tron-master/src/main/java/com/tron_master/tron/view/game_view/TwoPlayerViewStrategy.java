package com.tron_master.tron.view.game_view;

import java.io.IOException;

import com.tron_master.tron.constant.GameConstant;
import com.tron_master.tron.controller.game_controller.TwoPlayerGameController;
import com.tron_master.tron.model.data.TwoPlayerOutcome;
import com.tron_master.tron.model.object.Wall;
import com.tron_master.tron.view.utils.ViewUtils;

import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

/**
 * Two-player mode game view (manages FXML loading, UI components, and game canvas)
 */
public class TwoPlayerViewStrategy extends GameArea {
    private Text p1ScoreText;
    private Text p2ScoreText;
    private Button resetBtn;
    private Button exitBtn;
    
    /* Walls are managed by GameArea (set via controller) */
    
    /** Countdown display state */
    private boolean showingCountdown = false;
    private int countdownValue = 0;

    /** Default constructor for JavaFX. */
    public TwoPlayerViewStrategy() {
        super();
    }

    @Override
    protected void initGame() {
        drawBackground();
    }

    @Override
    public void reset() {
        // Only reset visuals
        drawBackground();
        renderWalls(); // Redraw walls after reset
    }
    
    /**
     * Shows the countdown number on the game area.
     * @param seconds The countdown number to display (3, 2, 1)
     */
    public void showCountdown(int seconds) {
        showingCountdown = true;
        countdownValue = seconds;
        renderCountdown();
    }
    
    /**
     * Hides the countdown display.
     */
    public void hideCountdown() {
        showingCountdown = false;
        // Redraw the game area without countdown
        drawBackground();
        renderWalls();
    }
    
    /**
     * Renders the countdown number in the center of the screen.
     */
    private void renderCountdown() {
        // First draw background and walls
        drawBackground();
        renderWalls();
        
        if (!showingCountdown) {
            return;
        }
        
        // Draw semi-transparent overlay
        gc.setFill(javafx.scene.paint.Color.rgb(0, 0, 0, 0.5));
        gc.fillRect(0, 0, GameConstant.GAME_AREA_WIDTH, GameConstant.GAME_AREA_HEIGHT);
        
        // Draw countdown number
        String countdownText = String.valueOf(countdownValue);
        javafx.scene.text.Font countdownFont = javafx.scene.text.Font.font(
            GameConstant.SCORE_FONT_FAMILY, 
            javafx.scene.text.FontWeight.BOLD, 
            120
        );
        gc.setFont(countdownFont);
        gc.setFill(javafx.scene.paint.Color.web("#00FFFF")); // Neon cyan color
        
        // Calculate text position for centering
        javafx.scene.text.Text tempText = new javafx.scene.text.Text(countdownText);
        tempText.setFont(countdownFont);
        double textWidth = tempText.getLayoutBounds().getWidth();
        double textHeight = tempText.getLayoutBounds().getHeight();
        
        double x = (GameConstant.GAME_AREA_WIDTH - textWidth) / 2;
        double y = (GameConstant.GAME_AREA_HEIGHT + textHeight / 2) / 2;
        
        // Draw glow effect
        gc.setFill(javafx.scene.paint.Color.rgb(0, 255, 255, 0.3));
        gc.fillText(countdownText, x - 2, y - 2);
        gc.fillText(countdownText, x + 2, y + 2);
        
        // Draw main text
        gc.setFill(javafx.scene.paint.Color.web("#00FFFF"));
        gc.fillText(countdownText, x, y);
    }

    /**
     * Sets the walls to be rendered.
     * @param walls Array of walls from game logic
     */
    @Override
    public void setWalls(Wall[] walls) {
        super.setWalls(walls);
    }

    /**
     * Renders all walls on the canvas.
     */
    @Override
    public void renderWalls() {
        super.renderWalls();
    }

    @Override
    public void showGameOverScreen() {
        showGameOverScreen(0, 0);
    }
    
    /**
     * Display game over screen with final scores.
     * @param p1Score Player 1's final score
     * @param p2Score Player 2's final score
     */
    public void showGameOverScreen(int p1Score, int p2Score) {
        TwoPlayerOutcome outcome = gameData.getTwoPlayerOutcome();
        String imagePath;
        switch (outcome) {
            case P1_WIN -> imagePath = "/images/p1_wins.png";
            case P2_WIN -> imagePath = "/images/p2_wins.png";
            default -> imagePath = "/images/tie.png";
        }

        Image winImage = ViewUtils.loadImage(imagePath);
        double imageWidth = winImage.getWidth();
        double imageHeight = winImage.getHeight();
        double x = (GameConstant.GAME_AREA_WIDTH - imageWidth) / 2;
        double y = (GameConstant.GAME_AREA_HEIGHT - imageHeight) / 2 - 30;
        gc.drawImage(winImage, x, y);
        
        // Draw final scores below the image
        gc.setFill(javafx.scene.paint.Color.web(GameConstant.SCORE_COLOR_GAMEOVER));
        javafx.scene.text.Font scoreFont = javafx.scene.text.Font.font(GameConstant.SCORE_FONT_FAMILY, javafx.scene.text.FontWeight.BOLD, GameConstant.SCORE_FONT_SIZE);
        gc.setFont(scoreFont);
        String finalScoreText = String.format("%d  :  %d", p1Score, p2Score);
        
        // Calculate text width for centering
        javafx.scene.text.Text tempText = new javafx.scene.text.Text(finalScoreText);
        tempText.setFont(scoreFont);
        double textWidth = tempText.getLayoutBounds().getWidth();
        
        gc.fillText(finalScoreText, (GameConstant.GAME_AREA_WIDTH - textWidth) / 2, y + imageHeight + 40);
    }
    
    /**
     * Create and configure the complete two-player mode scene with FXML.
     * @param controller The controller to bind to this view
     * @return Configured JavaFX Scene
     */
    public Scene createScene(TwoPlayerGameController controller) {
        try {
            // Load FXML
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/tron_master/tron/fxml/interfaces/two_player.fxml")
            );
            loader.setController(controller);
            // Root pane loaded from FXML
            BorderPane rootPane = loader.load();
            
            // Get references to FXML components
            // FXML components
            Pane gameAreaContainer = (Pane) rootPane.lookup("#gameAreaContainer");
            p1ScoreText = (Text) rootPane.lookup("#p1ScoreText");
            p2ScoreText = (Text) rootPane.lookup("#p2ScoreText");
            resetBtn = (Button) rootPane.lookup("#resetBtn");
            exitBtn = (Button) rootPane.lookup("#exitBtn");
            
            // Initialize UI components
            initializeUIComponents(controller);
            
            // Add game canvas to container
            gameAreaContainer.getChildren().add(this);
            this.setLayoutX(0);
            this.setLayoutY(0);
            
            // Create and return scene
            Scene scene = new Scene(rootPane, GameConstant.WINDOW_WIDTH, GameConstant.WINDOW_HEIGHT);
            this.requestFocus();
            
            return scene;
        } catch (IOException e) {
            System.err.println("Two-player mode FXML loading failed: " + e.getMessage());
            throw new RuntimeException("Failed to load two-player game FXML", e);
        }
    }
    
    /**
     * Initialize UI components (button graphics and event bindings).
     * @param controller The controller to bind button events to
     */
    private void initializeUIComponents(TwoPlayerGameController controller) {
        // Set button graphics
        resetBtn.setGraphic(ViewUtils.createScaledImageView(GameConstant.BTN_RESTART, -1, -1));
        exitBtn.setGraphic(ViewUtils.createScaledImageView(GameConstant.BTN_PLAY, -1, -1));
        
        // Bind button events
        resetBtn.setOnAction(_ -> controller.onResetBtnClick());
        exitBtn.setOnAction(_ -> controller.onExitBtnClick());
    }
    
    /**
     * Update player scores display in the UI.
     * @param p1Score Player 1's score
     * @param p1Boost Player 1's boost count
     * @param p2Score Player 2's score
     * @param p2Boost Player 2's boost count
     */
    public void updateScoresDisplay(int p1Score, int p1Boost, int p2Score, int p2Boost) {
        if (p1ScoreText != null && p2ScoreText != null) {
            p1ScoreText.setText(String.format("Player 1: %d    Boost: %d", p1Score, p1Boost));
            p2ScoreText.setText(String.format("Player 2: %d    Boost: %d", p2Score, p2Boost));
        }
    }

    @Override
    protected void drawBackground() {
        super.drawBackground();
    }
}
