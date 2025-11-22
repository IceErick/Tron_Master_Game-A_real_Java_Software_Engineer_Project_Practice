package com.tron_master.demo.controller.fxml;

import com.tron_master.demo.Game;
import com.tron_master.demo.constant.GameConstant;
import com.tron_master.demo.view.game.TwoViewStrategy;
import com.tron_master.demo.view.utils.ViewUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Pane;
import javafx.scene.text.Text;

import java.io.IOException;

/**
 * Two-player mode controller
 */
public class TwoPlayerGameController {

    @FXML
    private Pane gameAreaContainer;
    @FXML
    private Text p1ScoreText;
    @FXML
    private Text p2ScoreText;
    @FXML
    private Button resetBtn;
    @FXML
    private Button exitBtn;

    private TwoViewStrategy gameArea;

    @FXML
    public void initialize() {
        // 1. Initialize game area and add to container
        gameArea = new TwoViewStrategy();
        gameAreaContainer.getChildren().add(gameArea);

        // 2. Set button images
        resetBtn.setGraphic(ViewUtils.createScaledImageView(GameConstant.BTN_RESTART, -1, -1));
        exitBtn.setGraphic(ViewUtils.createScaledImageView(GameConstant.BTN_PLAY, -1, -1));

        // 3. Ensure game area is correctly positioned in container
        gameArea.setLayoutX(0);
        gameArea.setLayoutY(0);

        // 4. Initial UI update
        updateUI();
    }

    public Scene createTwoPlayerScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tron_master/demo/fxml/two_player.fxml"));
            loader.setController(this);
            BorderPane rootPane = loader.load();

            bindButtonEvents();

            Scene scene = new Scene(rootPane, GameConstant.WINDOW_WIDTH, GameConstant.WINDOW_HEIGHT);
            gameArea.requestFocus();

            return scene;
        } catch (IOException e) {
            System.err.println("Two-player mode FXML loading failed!");
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
     * Uniformly update UI display
     */
    private void updateUI() {
        if (p1ScoreText != null && p2ScoreText != null && gameArea != null) {
            p1ScoreText.setText(String.format("Player 1: %d    Boost: %d",
                    gameArea.getP1Score(), gameArea.getP1BoostCount()));
            p2ScoreText.setText(String.format("Player 2: %d    Boost: %d",
                    gameArea.getP2Score(), gameArea.getP2BoostCount()));
        }
    }

    /**
     * Get game area instance
     */
    public TwoViewStrategy getGameArea() {
        return gameArea;
    }
}