package com.tron_master.demo;

import com.tron_master.demo.constant.GameConstant;
import com.tron_master.demo.controller.fxml.MainMenuController;

import javafx.application.Application;
import javafx.stage.Stage;

/**
 * Tron game entry program (JavaFX launcher)
 * Responsibilities: initialize stage, load main menu, configure window
 */
public class Game extends Application {
    // Global primary stage (for all controllers to access, used for scene switching)
    private static Stage primaryStage;

    public static void main(String[] args) {
        launch(args); // Launch JavaFX application
    }

    @Override
    public void start(Stage stage) {
        primaryStage = stage;
        configureStage(stage);

        // Load main menu scene (changed to load FXML)
        MainMenuController mainMenuController = new MainMenuController();
        stage.setScene(mainMenuController.createMainMenuScene());

        stage.show();
    }

    /**
     * Configure game window (title, icon, size, close logic, etc.)
     */
    private void configureStage(Stage stage) {
        // Window title
        stage.setTitle(GameConstant.GAME_TITLE);

        // Window size (fixed size, not resizable)
        stage.setWidth(com.tron_master.demo.constant.GameConstant.WINDOW_WIDTH);
        stage.setHeight(com.tron_master.demo.constant.GameConstant.WINDOW_HEIGHT);
        stage.setResizable(false);

        // Center window on screen
        stage.centerOnScreen();

        // Custom close logic (release resources when exiting)
        stage.setOnCloseRequest(event -> {
            event.consume(); // Cancel default close
            exitGame(); // Custom exit
        });
    }

    /**
     * Custom game exit logic (release resources, save data, etc.)
     */
    private void exitGame() {
        System.out.println("Goodbye!...");
        // TODO: window (stage) close logic (e.g., save game process, relieve caches)
        primaryStage.close();
        System.exit(0);
    }

    // ------------------- Global utility methods -------------------
    /**
     * For all controllers to get the primary stage (used for scene switching)
     */
    public static Stage getPrimaryStage() {
        return primaryStage;
    }
}