package com.tron_master.demo.controller.fxml;

import java.io.IOException;

import com.tron_master.demo.Game;
import com.tron_master.demo.constant.GameConstant;
import com.tron_master.demo.view.utils.ViewUtils;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

public class MainMenuController {
    // Bind components in FXML (fx:id must match with FXML)
    @FXML
    private ImageView mainContentImage;
    @FXML
    private Button playBtn;
    @FXML
    private Button instructBtn;
    @FXML
    private Button quitBtn;

    // Status flag
    private boolean isInstructionsShow = false;

    /**
     * Automatically called after FXML loading completes (initialize components)
     */
    @FXML
    public void initialize() {
        // 1. Set images for buttons (reuse logic through ViewUtils)
        playBtn.setGraphic(ViewUtils.createScaledImageView(GameConstant.BTN_PLAY, -1, -1));
        instructBtn.setGraphic(ViewUtils.createScaledImageView(GameConstant.BTN_INSTRUCTIONS, -1, -1));
        quitBtn.setGraphic(ViewUtils.createScaledImageView(GameConstant.BTN_QUIT, -1, -1));

        // 2. Initialize center area with default background image
        mainContentImage.setImage(ViewUtils.loadImage(GameConstant.MAIN_BG_IMAGE));
    }

    /**
     * Load main menu scene (called by entry program)
     */
    public Scene createMainMenuScene() {
        try {
            // Load FXML file (path based on resources root directory)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tron_master/demo/fxml/main_menu.fxml"));
            loader.setController(this); // Bind current controller
            BorderPane rootPane = loader.load();

            bindButtonEvents();

            return new Scene(rootPane, GameConstant.WINDOW_WIDTH, GameConstant.WINDOW_HEIGHT);
        } catch (IOException e) {
            System.err.println("Main menu FXML loading failed!");
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    // ------------------- Button events (bound with FXML, can also be bound with onAction in FXML) -------------------

    /**
     * Manually bind button events
     */
    private void bindButtonEvents() {
        playBtn.setOnAction(event -> onPlayBtnClick());
        instructBtn.setOnAction(event -> onInstructBtnClick());
        quitBtn.setOnAction(event -> onQuitBtnClick());
    }

    public void onPlayBtnClick() {
        // Jump to mode selection menu
        PlayMenuController playMenuController = new PlayMenuController();
        Game.getPrimaryStage().setScene(playMenuController.createPlayMenuScene());
    }

    public void onInstructBtnClick() {
        // 使用单例窗口管理器显示指令窗口
        InstructionsController.showInstructionsWindow();
    }
    
    public void onQuitBtnClick() {
        System.exit(0);
    }
}