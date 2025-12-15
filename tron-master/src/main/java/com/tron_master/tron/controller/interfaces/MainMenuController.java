package com.tron_master.tron.controller.interfaces;

import java.io.IOException;

import com.tron_master.tron.Game;
import com.tron_master.tron.constant.GameConstant;
import com.tron_master.tron.model.sound.SoundManager;
import com.tron_master.tron.view.utils.ViewUtils;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;

/**
 * Controller for the main menu scene.
 * Handles loading FXML, initializing assets, and button navigation.
 */
public class MainMenuController {

    /** Default constructor for FXML. */
    public MainMenuController() {}
    // Bind components in FXML (fx:id must match with FXML)
    @FXML
    private ImageView mainContentImage;
    @FXML
    private Button playBtn;
    @FXML
    private Button instructBtn;
    @FXML
    private Button quitBtn;
    @FXML
    private Button settingsBtn;
    @FXML
    private Button settingsBtn1;

    /**
     * Automatically called after FXML loading completes (initialize components)
     */
    @FXML
    public void initialize() {
        // 1. Set images for buttons (reuse logic through ViewUtils)
        playBtn.setGraphic(ViewUtils.createScaledImageView(GameConstant.BTN_PLAY, -1, -1));
        instructBtn.setGraphic(ViewUtils.createScaledImageView(GameConstant.BTN_INSTRUCTIONS, -1, -1));
        quitBtn.setGraphic(ViewUtils.createScaledImageView(GameConstant.BTN_QUIT, -1, -1));
        settingsBtn.setGraphic(ViewUtils.createScaledImageView(GameConstant.BTN_SETTINGS, 40, 40));

        // Initialize music mute button with current state
        updateMusicButtonIcon();

        // 2. Initialize center area with default background image
        mainContentImage.setImage(ViewUtils.loadImage(GameConstant.MAIN_BG_IMAGE));
        
        // 3. Play background music when entering main menu
        SoundManager.getInstance().playBackgroundMusic("arcade_ambient");
    }

    /**
     * Load main menu scene (called by entry program)
     * @return constructed main menu Scene
     */
    public Scene createMainMenuScene() {
        try {
            // Load FXML file (path based on resources root directory)
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tron_master/tron/fxml/interfaces/main_menu.fxml"));
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
        playBtn.setOnAction(_ -> onPlayBtnClick());
        instructBtn.setOnAction(_ -> onInstructBtnClick());
        quitBtn.setOnAction(_ -> onQuitBtnClick());
        settingsBtn.setOnAction(_ -> onSettingsBtnClick());
        settingsBtn1.setOnAction(_ -> onMusicToggleBtnClick());
    }

    /** Handle Play button click. */
    public void onPlayBtnClick() {
        SoundManager.getInstance().playSoundEffect("click");
        // Jump to mode selection menu
        PlayMenuController playMenuController = new PlayMenuController();
        Game.getPrimaryStage().setScene(playMenuController.createPlayMenuScene());
    }

    /** Handle Instructions button click. */
    public void onInstructBtnClick() {
        // Use a separate window to show instructions
        InstructionsController.showInstructionsWindow();
    }
    
    /** Handle Quit button click. */
    public void onQuitBtnClick() {
        SoundManager.getInstance().playSoundEffect("quit");
        System.out.println("Goodbye!");
        System.exit(0);
    }

    /** Handle Settings button click. */
    public void onSettingsBtnClick() {
        // Jump to settings menu
        SettingsController settingsController = new SettingsController();
        Game.getPrimaryStage().setScene(settingsController.createSettingsScene());
    }
    
    /**
     * Toggle background music on/off
     */
    public void onMusicToggleBtnClick() {
        SoundManager soundManager = SoundManager.getInstance();
        boolean currentState = soundManager.isMusicEnabled();
        soundManager.setMusicEnabled(!currentState);
        updateMusicButtonIcon();
    }
    
    /**
     * Update music button icon based on current music state
     */
    private void updateMusicButtonIcon() {
        boolean musicEnabled = SoundManager.getInstance().isMusicEnabled();
        String iconPath = musicEnabled ? GameConstant.BTN_VOLUME_ON : GameConstant.BTN_VOLUME_OFF;
        settingsBtn1.setGraphic(ViewUtils.createScaledImageView(iconPath, 30, 28));
    }
}
