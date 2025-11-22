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
import javafx.stage.Modality;
import javafx.stage.Stage;

public class InstructionsController {
    @FXML
    private ImageView instructionsImage;

    @FXML
    private Button closeButton;

    private Stage stage;
    private static InstructionsController instance;

    @FXML
    public void initialize() {
        // load the instruction image
        instructionsImage.setImage(ViewUtils.loadImage(GameConstant.INSTRUCTIONS_IMAGE));
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    public void onCloseButtonClick() {
        if (stage != null) {
            stage.close();
        }
    }

    /**
     * Show the instruction window (Singleton Pattern)
     */
    public static void showInstructionsWindow() {
        // If the instance already exists and the stage is showing, bring it to the
        // front
        // instead of creating a new instance
        if (instance != null && instance.stage != null && instance.stage.isShowing()) {
            instance.stage.toFront();
            return;
        }

        try {
            FXMLLoader loader = new FXMLLoader(
                    InstructionsController.class.getResource("/com/tron_master/demo/fxml/instructions_dialog.fxml"));
            BorderPane rootPane = loader.load();

            // get the instance of the controller
            instance = loader.getController();

            // create a new stage (window)
            Stage instructionsStage = new Stage();
            instructionsStage.setTitle("Instructions");
            instructionsStage.initModality(Modality.NONE); // Non-modal, allow interaction with the main window
            instructionsStage.setResizable(false);

            // position the window, make it slightly offset from the main window
            positionWindow(instructionsStage);

            // set the stage for the controller
            instance.setStage(instructionsStage);

            // set the scene for the stage
            Scene scene = new Scene(rootPane);
            instructionsStage.setScene(scene);

            // set the close request handler for the stage
            instructionsStage.setOnCloseRequest(_ -> {
                instance = null; // Clear reference when the window is closed
            });

            // show the stage
            instructionsStage.show();
        } catch (IOException e) {
            System.err.println("Failed to load instructions dialog FXML!");
            e.printStackTrace();
        }
    }

    /**
     * Set the position of the instruction window relative to the main window
     */
    private static void positionWindow(Stage instructionsStage) {
        // get the position of the main window
        double mainX = Game.getPrimaryStage().getX();
        double mainY = Game.getPrimaryStage().getY();

        // set the position of the instruction window relative to the main window
        instructionsStage.setX(mainX + 30);
        instructionsStage.setY(mainY - 30);
    }
}