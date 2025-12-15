package com.tron_master.tron.controller.interfaces;

import java.io.IOException;

import com.tron_master.tron.Game;
import com.tron_master.tron.constant.GameConstant;
import com.tron_master.tron.controller.game_controller.StoryGameController;
import com.tron_master.tron.controller.game_controller.SurvivalGameController;
import com.tron_master.tron.controller.game_controller.TwoPlayerGameController;
import com.tron_master.tron.model.sound.SoundManager;
import com.tron_master.tron.view.utils.ViewUtils;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ScrollPane;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;

/**
 * Controller for the mode selection (play) menu.
 */
public class PlayMenuController {
    /** Default constructor for FXML. */
    public PlayMenuController() {}
    // Bind FXML components (fx:id matches with FXML)
    @FXML
    private ImageView playMenuContent;
    @FXML
    private Button storyBtn;
    @FXML
    private Button survivalBtn;
    @FXML
    private Button twoPlayerBtn;
    @FXML
    private Button highScoresBtn;
    @FXML
    private Button backBtn;
    @FXML
    private VBox centerContainer;
    
    // Status flag
    private boolean isShowingHighScores = false;
    // Cache background image
    private Image playMenuBgImage;
    // Cache high scores panel
    private ScrollPane highScoresPanel;

    /**
     * Load mode selection scene
     * @return constructed play menu Scene
     */
    public Scene createPlayMenuScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tron_master/tron/fxml/interfaces/play_menu.fxml"));
            loader.setController(this);
            BorderPane rootPane = loader.load();

            bindButtonEvents();

            return new Scene(rootPane, GameConstant.WINDOW_WIDTH, GameConstant.WINDOW_HEIGHT);
        } catch (IOException e) {
            System.err.println("Mode selection FXML loading failed!");
            System.err.println(e.getMessage());
            throw new RuntimeException(e);
        }
    }

    /**
     * Initialize after FXML loading completes
     */
    @FXML
    public void initialize() {
        // 1. Cache background image (avoid repeated loading)
        playMenuBgImage = ViewUtils.loadImage(GameConstant.PLAY_MENU_IMAGE);
        // 2. Initialize center area with background image
        playMenuContent.setImage(playMenuBgImage);
        // 3. Set images for all buttons
        initButtons();
        // 4. Initialize high scores panel (hidden by default)
        initHighScoresPanel();
        // Ensure playMenuContent is attached
        if (!centerContainer.getChildren().contains(playMenuContent)) {
            centerContainer.getChildren().add(playMenuContent);
        }
        
        // 5. Switch to game music when entering play menu
        SoundManager.getInstance().playBackgroundMusic("futuristic_bg");
    }

    /**
     * Initialize all button images
     */
    private void initButtons() {
        storyBtn.setGraphic(ViewUtils.createScaledImageView(GameConstant.BTN_STORY, -1, -1));
        survivalBtn.setGraphic(ViewUtils.createScaledImageView(GameConstant.BTN_SURVIVAL, -1, -1));
        twoPlayerBtn.setGraphic(ViewUtils.createScaledImageView(GameConstant.BTN_TWO_PLAYER, -1, -1));
        highScoresBtn.setGraphic(ViewUtils.createScaledImageView(GameConstant.BTN_HIGH_SCORES, -1, -1));
        backBtn.setGraphic(ViewUtils.createScaledImageView(GameConstant.BTN_MAIN_MENU, -1, -1));
    }
    
    /**
     * Initialize high scores panel
     */
    private void initHighScoresPanel() {
        highScoresPanel = HighScoreDialogController.createHighScoresPanel();
    }

    // ------------------- Button event handling -------------------
    /**
     * Story mode button click
     */
    private void bindButtonEvents() {
        storyBtn.setOnAction(_ -> onStoryBtnClick());
        survivalBtn.setOnAction(_ -> onSurvivalBtnClick());
        twoPlayerBtn.setOnAction(_ -> onTwoPlayerBtnClick());
        highScoresBtn.setOnAction(_ -> onHighScoresBtnClick());
        backBtn.setOnAction(_ -> onBackBtnClick());
    }

    /**
     * Story mode button click.
     */
    @FXML
    public void onStoryBtnClick() {
        SoundManager.getInstance().playSoundEffect("click");
        StoryGameController controller = new StoryGameController();
        Game.getPrimaryStage().setScene(controller.createStoryScene());
    }

    /**
     * Survival mode button click
     */
    @FXML
    public void onSurvivalBtnClick() {
        SurvivalGameController controller = new SurvivalGameController();
        Game.getPrimaryStage().setScene(controller.createSurvivalScene());
        SoundManager.getInstance().playSoundEffect("click");
    }

    /**
     * Two-player mode button click
     */
    @FXML
    public void onTwoPlayerBtnClick() {
        SoundManager.getInstance().playSoundEffect("click");
        TwoPlayerGameController controller = new TwoPlayerGameController();
        Game.getPrimaryStage().setScene(controller.createTwoPlayerScene());
    }

    /**
     * High scores button click: toggle high score display in center area.
     */
    @FXML
    public void onHighScoresBtnClick() {
        SoundManager.getInstance().playSoundEffect("clic");
        
        if (isShowingHighScores) {
            // Switch back to background image
            centerContainer.getChildren().clear();
            centerContainer.getChildren().add(playMenuContent);
            isShowingHighScores = false;
        } else {
            // Show high scores panel
            centerContainer.getChildren().clear();
            centerContainer.getChildren().add(highScoresPanel);
            isShowingHighScores = true;
        }
    }

    /**
     * Return to main menu button click
     */
    @FXML
    public void onBackBtnClick() {
        SoundManager.getInstance().playSoundEffect("quit");
        // Switch back to menu music
        SoundManager.getInstance().playBackgroundMusic("arcade_ambient");
        
        MainMenuController controller = new MainMenuController();
        Game.getPrimaryStage().setScene(controller.createMainMenuScene());
    }

}
