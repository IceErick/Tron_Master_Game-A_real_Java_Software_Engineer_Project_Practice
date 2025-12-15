package com.tron_master.tron.controller.interfaces;

import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map;

import com.tron_master.tron.Game;
import com.tron_master.tron.constant.GameConstant;
import com.tron_master.tron.model.data.GameData;
import com.tron_master.tron.model.sound.SoundManager;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Toggle;
import javafx.scene.control.ToggleButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.Region;

/**
 * Settings controller: currently handles background color selection for the game canvas.
 */
public class SettingsController {

    /** Default constructor for FXML. */
    public SettingsController() {}

    @FXML
    private ToggleButton color1;
    @FXML
    private ToggleButton color2;
    @FXML
    private ToggleButton color3;
    @FXML
    private ToggleButton color4;
    @FXML
    private ToggleButton color5;
    @FXML
    private ToggleButton color6;
    @FXML
    private ToggleButton color7;
    @FXML
    private ToggleButton color8;
    @FXML
    private Region previewBox;
    @FXML
    private Button saveBtn;
    @FXML
    private Button backBtn;

    private final ToggleGroup colorGroup = new ToggleGroup();
    private final GameData gameData = GameData.getInstance();
    private final Map<ToggleButton, String> colorMap = new LinkedHashMap<>();

    /**
     * Create the settings scene.
     * @return constructed settings scene
     */
    public Scene createSettingsScene() {
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/com/tron_master/tron/fxml/interfaces/settings.fxml"));
            loader.setController(this);
            BorderPane root = loader.load();
            return new Scene(root, GameConstant.WINDOW_WIDTH, GameConstant.WINDOW_HEIGHT);
        } catch (IOException e) {
            throw new RuntimeException("Failed to load settings FXML", e);
        }
    }

    /**
     * Initialize toggle buttons and preview after FXML load.
     */
    @FXML
    public void initialize() {
        bindColorButtons();
        applyInitialSelection();
        saveBtn.setOnAction(_ -> onSave());
        backBtn.setOnAction(_ -> onBack());
        
        // Pause arcade_ambient and play instructions sound
        SoundManager.getInstance().pauseBackgroundMusic();
        SoundManager.getInstance().playSoundEffect("instructions");
    }

    private void bindColorButtons() {
        ToggleButton[] buttons = {color1, color2, color3, color4, color5, color6, color7, color8};
        String[] colors = GameConstant.BG_COLOR_OPTIONS;
        for (int i = 0; i < buttons.length; i++) {
            ToggleButton btn = buttons[i];
            btn.setToggleGroup(colorGroup);
            String hex = colors[i % colors.length];
            colorMap.put(btn, hex);
        }
        colorGroup.selectedToggleProperty().addListener((obs, oldVal, newVal) -> {
            String hex = getColorForToggle(newVal);
            updatePreview(hex);
        });
    }

    private void applyInitialSelection() {
        String currentColor = gameData.getBackgroundColor();
        ToggleButton match = colorMap.entrySet().stream()
                .filter(entry -> entry.getValue().equalsIgnoreCase(currentColor))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElse(color1);
        colorGroup.selectToggle(match);
        updatePreview(colorMap.get(match));
    }

    private String getColorForToggle(Toggle toggle) {
        if (toggle instanceof ToggleButton button) {
            return colorMap.get(button);
        }
        return null;
    }

    private String getSelectedColor() {
        return getColorForToggle(colorGroup.getSelectedToggle());
    }

    private void updatePreview(String hex) {
        if (hex == null) {
            return;
        }
        previewBox.setStyle("-fx-background-color: " + hex + "; -fx-border-color: white; -fx-border-width: 1;");
    }

    private void onSave() {
        String selectedColor = getSelectedColor();
        if (selectedColor != null) {
            gameData.setBackgroundColor(selectedColor);
        }
        returnToMainMenu();
    }

    private void onBack() {
        returnToMainMenu();
    }

    private void returnToMainMenu() {
        // Stop instructions sound and resume arcade_ambient
        SoundManager.getInstance().stopAllSoundEffects();
        SoundManager.getInstance().setMusicEnabled(true);
        
        MainMenuController controller = new MainMenuController();
        Game.getPrimaryStage().setScene(controller.createMainMenuScene());
    }
}
