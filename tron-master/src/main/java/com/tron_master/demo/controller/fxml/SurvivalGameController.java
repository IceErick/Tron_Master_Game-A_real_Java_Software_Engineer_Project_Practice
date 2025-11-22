package com.tron_master.demo.controller.fxml;

import com.tron_master.demo.Game;
import com.tron_master.demo.constant.GameConstant;
import com.tron_master.demo.view.game.SurvViewStrategy;
import com.tron_master.demo.view.utils.ViewUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.IOException;

public class SurvivalGameController {
    // Bind FXML components
    @FXML
    private Pane gameAreaContainer; // Game canvas container
    @FXML
    private Text scoreText;
    @FXML
    private Button resetBtn;
    @FXML
    private Button exitBtn;

    // Game area instance
    private SurvViewStrategy gameArea;

    /**
     * Initialize after FXML loading completes
     */
    @FXML
    public void initialize() {
        // 1. Initialize game area and add to container
        gameArea = new SurvViewStrategy();
        gameAreaContainer.getChildren().add(gameArea);

        // 2. Set button images
        resetBtn.setGraphic(ViewUtils.createScaledImageView(GameConstant.BTN_RESTART, -1, -1));
        exitBtn.setGraphic(ViewUtils.createScaledImageView(GameConstant.BTN_PLAY, -1, -1));

        // 3. Ensure game area is correctly positioned in container
        gameArea.setLayoutX(0);
        gameArea.setLayoutY(0);

        // 4. Initial UI update (uniformly use updateUI())
        updateUI();
    }

    /**
     * Load survival mode scene
     */
    public Scene createSurvivalScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tron_master/demo/fxml/survival_game.fxml"));
            loader.setController(this);
            BorderPane rootPane = loader.load();

            bindButtonEvents();
            Scene scene = new Scene(rootPane, GameConstant.WINDOW_WIDTH, GameConstant.WINDOW_HEIGHT);
            // Request focus after scene creation
            gameArea.requestFocus();

            return scene;
        } catch (IOException e) {
            System.err.println("Survival mode FXML loading failed!");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    // ------------------- Button events (bound with FXML, can also be bound with onAction in FXML) -------------------

    /**
     * Manually bind button events
     */
    private void bindButtonEvents() {
        resetBtn.setOnAction(event -> onResetBtnClick());
        exitBtn.setOnAction(event -> onExitBtnClick());
    }

    @FXML
    public void onResetBtnClick() {
        gameArea.reset();
        updateUI();
        gameArea.requestFocus();
    }

    @FXML
    public void onExitBtnClick() {
        PlayMenuController controller = new PlayMenuController();
        Game.getPrimaryStage().setScene(controller.createPlayMenuScene());
    }

    /**
     * Update UI display
     */
    private void updateUI() {
        if (scoreText != null && gameArea != null) {
            scoreText.setText(String.format("Score: %d    Boost: %d",
                    gameArea.getScore(), gameArea.getBoostCount()));
        }
    }
}