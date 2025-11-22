package com.tron_master.demo.controller.fxml;

import com.tron_master.demo.Game;
import com.tron_master.demo.constant.GameConstant;
import com.tron_master.demo.view.utils.ViewUtils;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.Text;

import java.io.IOException;

public class PlayMenuController {
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
    private boolean isScoresShow = false;
    // High scores panel (dynamically created, toggled for display)
    private VBox highScoresPane;
    // Mode selection background image (cached, reused when switching)
    private Image playMenuBgImage;
    // Cached index of the dynamic content slot inside the center container
    private int dynamicContentIndex;

    /**
     * Load mode selection scene
     */
    public Scene createPlayMenuScene() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/tron_master/demo/fxml/play_menu.fxml"));
            loader.setController(this);
            BorderPane rootPane = loader.load();

            bindButtonEvents();

            return new Scene(rootPane, GameConstant.WINDOW_WIDTH, GameConstant.WINDOW_HEIGHT);
        } catch (IOException e) {
            System.err.println("Mode selection FXML loading failed!");
            e.printStackTrace();
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
        initHighScoresPane();
        // 5. Remember where the dynamic content lives so we can swap it later
        dynamicContentIndex = centerContainer.getChildren().indexOf(playMenuContent);
        if (dynamicContentIndex < 0) {
            centerContainer.getChildren().add(playMenuContent);
            dynamicContentIndex = centerContainer.getChildren().size() - 1;
        }
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
     * Initialize high scores panel (static data example, can be replaced with real storage)
     */
    private void initHighScoresPane() {
        highScoresPane = new VBox(10);
        highScoresPane.setStyle("-fx-background-color: #000000; -fx-padding: 20;");
        highScoresPane.setAlignment(javafx.geometry.Pos.CENTER);

        // High scores title
        Text title = new Text("High Scores");
        title.setFill(Color.WHITE);
        title.setFont(new Font(24));
        highScoresPane.getChildren().add(title);

        // Example high score data (can be loaded from file/database later)
        String[] scoreData = {
                "1. TronMaster - 9999",
                "2. LightCycle - 8888",
                "3. GridRider - 7777",
                "4. NeonRacer - 6666",
                "5. SpeedDemon - 5555"
        };
        for (String score : scoreData) {
            Text scoreText = new Text(score);
            scoreText.setFill(Color.WHITE);
            scoreText.setFont(new Font(18));
            highScoresPane.getChildren().add(scoreText);
        }
    }

    // ------------------- Button event handling -------------------
    /**
     * Story mode button click
     */
    private void bindButtonEvents() {
        storyBtn.setOnAction(event -> onStoryBtnClick());
        survivalBtn.setOnAction(event -> onSurvivalBtnClick());
        twoPlayerBtn.setOnAction(event -> onTwoPlayerBtnClick());
        highScoresBtn.setOnAction(event -> onHighScoresBtnClick());
        backBtn.setOnAction(event -> onBackBtnClick());
    }

    @FXML
    public void onStoryBtnClick() {
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
    }

    /**
     * Two-player mode button click
     */
    @FXML
    public void onTwoPlayerBtnClick() {
        TwoPlayerGameController controller = new TwoPlayerGameController();
        Game.getPrimaryStage().setScene(controller.createTwoPlayerScene());
    }

    /**
     * High scores button click (toggle show/hide high scores panel)
     */
    @FXML
    public void onHighScoresBtnClick() {
        if (isScoresShow) {
            // Hide high scores, show background image
            centerContainer.getChildren().set(dynamicContentIndex, playMenuContent);
            playMenuContent.setImage(playMenuBgImage);
        } else {
            // Show high scores by swapping the middle slot content
            centerContainer.getChildren().set(dynamicContentIndex, highScoresPane);
        }
        isScoresShow = !isScoresShow;
    }

    /**
     * Return to main menu button click
     */
    @FXML
    public void onBackBtnClick() {
        MainMenuController controller = new MainMenuController();
        Game.getPrimaryStage().setScene(controller.createMainMenuScene());
    }
}
