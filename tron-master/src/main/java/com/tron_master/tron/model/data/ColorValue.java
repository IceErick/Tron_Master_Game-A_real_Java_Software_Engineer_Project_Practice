package com.tron_master.tron.model.data;

/**
 * UI-agnostic color value used by the model layer.
 * Represents an RGB color in the 0-1.0 range to avoid JavaFX dependencies.
 */
public class ColorValue {
    private final double red;
    private final double green;
    private final double blue;

    /**
     * Create a color with RGB channels in the 0-1 range (values are clamped).
     * @param red   red channel (0-1)
     * @param green green channel (0-1)
     * @param blue  blue channel (0-1)
     */
    public ColorValue(double red, double green, double blue) {
        this.red = clamp(red);
        this.green = clamp(green);
        this.blue = clamp(blue);
    }

    private double clamp(double channel) {
        if (channel < 0.0) {
            return 0.0;
        }
        if (channel > 1.0) {
            return 1.0;
        }
        return channel;
    }

    /**
     * Get normalized red component.
     * @return red in range 0-1
     */
    public double red() {
        return red;
    }

    /**
     * Get normalized green component.
     * @return green in range 0-1
     */
    public double green() {
        return green;
    }

    /**
     * Get normalized blue component.
     * @return blue in range 0-1
     */
    public double blue() {
        return blue;
    }
}
