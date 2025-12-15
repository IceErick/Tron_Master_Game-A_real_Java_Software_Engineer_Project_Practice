package com.tron_master.tron.view.game_view;

import java.io.IOException;

import com.tron_master.tron.constant.GameConstant;
import com.tron_master.tron.controller.game_controller.StoryGameController;
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
 * Story mode game view (manages FXML loading, UI components, and game canvas)
 */
public class StoryViewStrategy extends GameArea {
    
    // FXML components
    private Pane gameAreaContainer;
    private Text scoreText;
    private Text levelText;
    private Button resetBtn;
    private Button exitBtn;
    
    // Root pane loaded from FXML
    private BorderPane rootPane;
    
    /** Walls and portals are managed by GameArea (set via controller) */
    
    // Rendering is centralized in GameArea (wall/portal draw handled there)

    public StoryViewStrategy() {
        super();
    }

    @Override
    protected void initGame() {
        drawBackground();
    }

    @Override
    public void reset() {
        drawBackground();
        renderWalls();   // Redraw walls after reset (GameArea implementation)
        renderPortals(); // Redraw portals after reset (GameArea implementation)
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
     * Sets the portals to be rendered.
     * @param portals Array of portals from game logic
     */
    @Override
    public void setPortals(Portal[] portals) {
        super.setPortals(portals);
    }

    /**
     * Renders all walls on the canvas.
     */
    // Uses GameArea.renderWalls() implementation
    
    /**
     * Renders all portals on the canvas.
     * Portals extend Wall, so they use the same renderer.
     */
    // Uses GameArea.renderPortals() implementation

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
        gc.setFill(javafx.scene.paint.Color.web(GameConstant.SCORE_COLOR_GAMEOVER));
        javafx.scene.text.Font scoreFont = javafx.scene.text.Font.font(GameConstant.SCORE_FONT_FAMILY, javafx.scene.text.FontWeight.BOLD, GameConstant.SCORE_FONT_SIZE);
        gc.setFont(scoreFont);
        String finalScoreText = "Final Score: " + finalScore;
        
        // Calculate text width for centering
        javafx.scene.text.Text tempText = new javafx.scene.text.Text(finalScoreText);
        tempText.setFont(scoreFont);
        double textWidth = tempText.getLayoutBounds().getWidth();
        
        gc.fillText(finalScoreText, (GameConstant.GAME_AREA_WIDTH - textWidth) / 2, y + imageHeight + 40);
    }

    /** Show generic level complete screen without score. */
    public void showLevelCompleteScreen() {
        showLevelCompleteScreen(0);
    }
    
    /**
     * Display level complete/victory screen with final score.
     * @param finalScore The final score to display
     */
    public void showLevelCompleteScreen(int finalScore) {
        // Load and draw the win image
        Image winImage = ViewUtils.loadImage("/images/win.png");
        
        // Calculate position to center the image
        double imageWidth = winImage.getWidth();
        double imageHeight = winImage.getHeight();
        double x = (GameConstant.GAME_AREA_WIDTH - imageWidth) / 2;
        double y = (GameConstant.GAME_AREA_HEIGHT - imageHeight) / 2 - 30;
        
        // Draw the image centered on the game area
        gc.drawImage(winImage, x, y);
        
        // Draw final score below the image (only if score > 0, meaning victory)
        if (finalScore > 0) {
            gc.setFill(javafx.scene.paint.Color.web(GameConstant.SCORE_COLOR_VICTORY));
            javafx.scene.text.Font scoreFont = javafx.scene.text.Font.font(GameConstant.SCORE_FONT_FAMILY, javafx.scene.text.FontWeight.BOLD, GameConstant.SCORE_FONT_SIZE);
            gc.setFont(scoreFont);
            String finalScoreText = "Final Score: " + finalScore;
            
            // Calculate text width for centering
            javafx.scene.text.Text tempText = new javafx.scene.text.Text(finalScoreText);
            tempText.setFont(scoreFont);
            double textWidth = tempText.getLayoutBounds().getWidth();
            
            gc.fillText(finalScoreText, (GameConstant.GAME_AREA_WIDTH - textWidth) / 2, y + imageHeight + 40);
        }
    }
    
    /**
     * Create and configure the complete story mode scene with FXML.
     * @param controller The controller to bind to this view
     * @return Configured JavaFX Scene
     */
    public Scene createScene(StoryGameController controller) {
        try {
            // Load FXML
            FXMLLoader loader = new FXMLLoader(
                getClass().getResource("/com/tron_master/tron/fxml/interfaces/story_game.fxml")
            );
            loader.setController(controller);
            rootPane = loader.load();
            
            // Get references to FXML components from controller
            gameAreaContainer = (Pane) rootPane.lookup("#gameAreaContainer");
            scoreText = (Text) rootPane.lookup("#scoreText");
            levelText = (Text) rootPane.lookup("#levelText");
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
            System.err.println("Story mode FXML loading failed: " + e.getMessage());
            throw new RuntimeException("Failed to load story game FXML", e);
        }
    }
    
    /**
     * Initialize UI components (button graphics and event bindings).
     * @param controller The controller to bind button events to
     */
    private void initializeUIComponents(StoryGameController controller) {
        // Set button graphics
        resetBtn.setGraphic(ViewUtils.createScaledImageView(GameConstant.BTN_RESTART, -1, -1));
        exitBtn.setGraphic(ViewUtils.createScaledImageView(GameConstant.BTN_PLAY, -1, -1));
        
        // Bind button events
        resetBtn.setOnAction(event -> controller.onResetBtnClick());
        exitBtn.setOnAction(event -> controller.onExitBtnClick());
    }
    
    /**
     * Update score and level display in the UI.
     * @param score Current score
     * @param boost Current boost count
     * @param level Current level
     */
    public void updateScoreDisplay(int score, int boost, int level) {
        if (scoreText != null && levelText != null) {
            scoreText.setText(String.format("Score: %d    Boost: %d", score, boost));
            levelText.setText(String.format("Level: %d", level));
        }
    }
}
