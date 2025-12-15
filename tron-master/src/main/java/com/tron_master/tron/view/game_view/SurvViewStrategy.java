package com.tron_master.tron.view.game_view;

import java.io.IOException;

import com.tron_master.tron.constant.GameConstant;
import com.tron_master.tron.controller.game_controller.SurvivalGameController;
import com.tron_master.tron.model.object.Portal;
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
 * Survival mode game view (manages FXML loading, UI components, and game canvas)
 */
public class SurvViewStrategy extends GameArea {
    
    // FXML components
    private Pane gameAreaContainer;
    private Text scoreText;
    private Button resetBtn;
    private Button exitBtn;
    
    // Root pane loaded from FXML
    private BorderPane rootPane;
    
    /** Walls and portals are managed by GameArea (set via controller) */
    
    public SurvViewStrategy() {
        super();
    }

    @Override
    protected void initGame() {
        drawBackground(); // Draw black background
    }

    @Override
    public void reset() {
        // Only reset visuals
        drawBackground();
        renderWalls(); // Redraw walls after reset
        renderPortals(); // Redraw portals after reset
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
    
    /**
     * Sets the portals to be rendered.
     * @param portals Array of portals from game logic
     */
    @Override
    public void setPortals(Portal[] portals) {
        super.setPortals(portals);
    }
    
    /**
     * Renders all portals on the canvas.
     * Portals extend Wall, so they use the same renderer.
     */
    @Override
    public void renderPortals() {
        super.renderPortals();
    }

    @Override
    public void showGameOverScreen() {
        showGameOverScreen(0);
    }
    
    /**
     * Display game over screen with final score.
     * @param finalScore The final score to display
     */
    public void showGameOverScreen(int finalScore) {
        // Load and draw the game over image
        Image overImage = ViewUtils.loadImage("/images/over.png");

        // Calculate position to center the image
        double imageWidth = overImage.getWidth();
        double imageHeight = overImage.getHeight();
        double x = (GameConstant.GAME_AREA_WIDTH - imageWidth) / 2;
        double y = (GameConstant.GAME_AREA_HEIGHT - imageHeight) / 2 - 30;

        // Draw the image centered on the game area
        gc.drawImage(overImage, x, y);
        
        // Draw final score below the image
        gc.setFill(javafx.scene.paint.Color.web(GameConstant.SCORE_COLOR_VICTORY));
        javafx.scene.text.Font scoreFont = javafx.scene.text.Font.font(GameConstant.SCORE_FONT_FAMILY, javafx.scene.text.FontWeight.BOLD, GameConstant.SCORE_FONT_SIZE);
        gc.setFont(scoreFont);
        String finalScoreText = "Final Score: " + finalScore;
        
        // Calculate text width for centering
        Text tempText = new Text(finalScoreText);
        tempText.setFont(scoreFont);
        double textWidth = tempText.getLayoutBounds().getWidth();
        
        gc.fillText(finalScoreText, (GameConstant.GAME_AREA_WIDTH - textWidth) / 2, y + imageHeight + 40);
    }
    
    /**
     * Create and configure the complete survival mode scene with FXML.
     * @param controller The controller to bind to this view
     * @return Configured JavaFX Scene
     */
    public Scene createScene(SurvivalGameController controller) {
        try {
            // Load FXML
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/tron_master/tron/fxml/interfaces/survival_game.fxml")
            );
            loader.setController(controller);
            rootPane = loader.load();
            
            // Get references to FXML components
            gameAreaContainer = (Pane) rootPane.lookup("#gameAreaContainer");
            scoreText = (Text) rootPane.lookup("#scoreText");
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
            System.err.println("Survival mode FXML loading failed: " + e.getMessage());
            throw new RuntimeException("Failed to load survival game FXML", e);
        }
    }
    
    /**
     * Initialize UI components (button graphics and event bindings).
     * @param controller The controller to bind button events to
     */
    private void initializeUIComponents(SurvivalGameController controller) {
        // Set button graphics
        resetBtn.setGraphic(ViewUtils.createScaledImageView(GameConstant.BTN_RESTART, -1, -1));
        exitBtn.setGraphic(ViewUtils.createScaledImageView(GameConstant.BTN_PLAY, -1, -1));
        
        // Bind button events
        resetBtn.setOnAction(event -> controller.onResetBtnClick());
        exitBtn.setOnAction(event -> controller.onExitBtnClick());
    }
    
    /**
     * Update score display in the UI.
     * @param score Current score
     * @param boost Current boost count
     */
    public void updateScoreDisplay(int score, int boost) {
        if (scoreText != null) {
            String scoreStr = String.format("Score: %-6d Boost: %d", score, boost);
            scoreText.setText(scoreStr);
        }
    }
}
