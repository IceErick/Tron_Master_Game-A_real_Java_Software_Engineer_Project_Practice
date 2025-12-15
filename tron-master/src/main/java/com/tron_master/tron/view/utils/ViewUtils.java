package com.tron_master.tron.view.utils;

import com.tron_master.tron.constant.GameConstant;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Background;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.paint.Color;

import java.util.Objects;

/**
 * View utility class: create image buttons, load images, common UI configurations
 */
public class ViewUtils {

    /** Utility class; prevent instantiation. */
    private ViewUtils() {}

    /**
     * Create image-filled button (game-specific: borderless, black background, adaptive image)
     * @param imagePath Image path (refer to GameConstant definitions)
     * @return Image button
     */
    public static Button createImageButton(String imagePath) {
        // Load image (print log when path is wrong to avoid crash)
        Image image = loadImage(imagePath);
        ImageView imageView = new ImageView(image);

        // Configure button style
        Button button = new Button();
        button.setGraphic(imageView); // Set image as button content
        button.setBackground(new Background(new BackgroundFill(
                Color.valueOf(GameConstant.DEFAULT_BG_COLOR),
                null, null
        )));
        button.setBorder(null); // Remove default border
        button.setPadding(new javafx.geometry.Insets(0)); // Remove padding
        button.setCursor(javafx.scene.Cursor.HAND); // Show hand cursor on hover

        return button;
    }

    /**
     * Load image (handle path errors, return default empty image to avoid crash)
     * @param imagePath Image path
     * @return Loaded Image object
     */
    public static Image loadImage(String imagePath) {
        try {
            return new Image(Objects.requireNonNull(ViewUtils.class.getResourceAsStream(imagePath)));
        } catch (Exception e) {
            System.err.println("Image loading failed! Path: " + imagePath);
            // Return 1x1 transparent image to avoid button display issues
            return new Image(new java.io.ByteArrayInputStream(new byte[0]));
        }
    }

    /**
     * Create image view (adapt to specified dimensions)
     * @param imagePath Image path
     * @param width Target width
     * @param height Target height
     * @return Scaled ImageView
     */
    public static ImageView createScaledImageView(String imagePath, int width, int height) {
        Image image = loadImage(imagePath);
        ImageView imageView = new ImageView(image);
        imageView.setFitWidth(width);
        imageView.setFitHeight(height);
        imageView.setPreserveRatio(false); // Don't preserve ratio, fill target dimensions
        return imageView;
    }
}
