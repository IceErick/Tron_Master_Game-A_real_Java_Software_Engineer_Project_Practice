package com.tron_master.tron.controller.interfaces;

import java.io.IOException;
import java.util.List;

import com.tron_master.tron.model.data.HighScoreManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.VBox;

/**
 * Controller for high score display.
 * Creates high scores panel for embedding in other views.
 * Displays top 10 scores for each game mode.
 */
public class HighScoreDialogController {
    
    /** Default constructor for FXML loader. */
    public HighScoreDialogController() {}

    /** Maximum number of high scores to display per mode */
    private static final int MAX_SCORES_PER_MODE = 10;
    
    @FXML
    private VBox storyScoreContainer;
    
    @FXML
    private VBox survivalScoreContainer;
    
    @FXML
    private Label titleLabel;

    /**
     * Create a high scores panel for embedding in other views.
     * 
     * @return ScrollPane containing the high scores
     */
    public static ScrollPane createHighScoresPanel() {
        try {
            FXMLLoader loader = new FXMLLoader(
                HighScoreDialogController.class.getResource("/com/tron_master/tron/fxml/interfaces/high_scores.fxml")
            );
            ScrollPane scrollPane = loader.load();
            
            // Get controller and load scores
            HighScoreDialogController controller = loader.getController();
            controller.loadScores();
            
            return scrollPane;
        } catch (IOException e) {
            System.err.println("Failed to load high_scores.fxml: " + e.getMessage());
            throw new RuntimeException(e);
        }
    }
    
    /**
     * Load scores dynamically into the containers.
     * Displays top 10 scores for each mode.
     */
    private void loadScores() {
        List<HighScoreManager.ScoreEntry> allEntries = HighScoreManager.getInstance().getAllHighScores();
        
        // Separate scores by mode and limit to top 10
        List<HighScoreManager.ScoreEntry> storyScores = allEntries.stream()
                .filter(entry -> entry.getMode() == HighScoreManager.GameMode.STORY)
                .limit(MAX_SCORES_PER_MODE)
                .toList();
        
        List<HighScoreManager.ScoreEntry> survivalScores = allEntries.stream()
                .filter(entry -> entry.getMode() == HighScoreManager.GameMode.SURVIVAL)
                .limit(MAX_SCORES_PER_MODE)
                .toList();
        
        // Load story scores
        int storyRank = 1;
        for (HighScoreManager.ScoreEntry entry : storyScores) {
            Label scoreLabel = new Label(String.format("%d. %d", storyRank++, entry.getScore()));
            scoreLabel.getStyleClass().add("score-label");
            storyScoreContainer.getChildren().add(scoreLabel);
        }
        
        // Add placeholder if no story scores
        if (storyScores.isEmpty()) {
            Label placeholder = new Label("No scores yet");
            placeholder.getStyleClass().add("score-placeholder");
            storyScoreContainer.getChildren().add(placeholder);
        }
        
        // Load survival scores
        int survivalRank = 1;
        for (HighScoreManager.ScoreEntry entry : survivalScores) {
            Label scoreLabel = new Label(String.format("%d. %d", survivalRank++, entry.getScore()));
            scoreLabel.getStyleClass().add("score-label");
            survivalScoreContainer.getChildren().add(scoreLabel);
        }
        
        // Add placeholder if no survival scores
        if (survivalScores.isEmpty()) {
            Label placeholder = new Label("No scores yet");
            placeholder.getStyleClass().add("score-placeholder");
            survivalScoreContainer.getChildren().add(placeholder);
        }
    }
}
