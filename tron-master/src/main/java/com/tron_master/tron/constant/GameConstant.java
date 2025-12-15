package com.tron_master.tron.constant;

/**
 * Global constants in tron_master
 */
public class GameConstant {
    /** Utility class; prevent instantiation. */
    private GameConstant() {}

    // window configurations (16:9)
    /** Window width in pixels. */
    public static final int WINDOW_WIDTH = 560;
    /** Window height in pixels. */
    public static final int WINDOW_HEIGHT = 620;
    /** Playable game area height in pixels. */
    public static final int GAME_AREA_HEIGHT = 500; // Game pane (play area) size
    /** Playable game area width in pixels. */
    public static final int GAME_AREA_WIDTH = 560;
    /** Window title string. */
    public static final String GAME_TITLE = "Tron";

    // Image interface
    /** Default background image path. */
    public static final String MAIN_BG_IMAGE = "/images/tron0_0.jpg";
    /** Play menu background image path. */
    public static final String PLAY_MENU_IMAGE = "/images/play_menu.jpg";
    /** Instructions image path. */
    public static final String INSTRUCTIONS_IMAGE = "/images/instructions_page.png";

    // Buttons
    /** Play button image path. */
    public static final String BTN_PLAY = "/images/play_before.png";
    /** Instructions button image path. */
    public static final String BTN_INSTRUCTIONS = "/images/instructions_before.png";
    /** Quit button image path. */
    public static final String BTN_QUIT = "/images/quit_before.png";
    /** Settings button image path. */
    public static final String BTN_SETTINGS = "/images/settings.png";
    /** Volume-on button image path. */
    public static final String BTN_VOLUME_ON = "/images/volume_on.png";
    /** Volume-off button image path. */
    public static final String BTN_VOLUME_OFF = "/images/volume_off.png";
    /** Main-menu button image path. */
    public static final String BTN_MAIN_MENU = "/images/main_menu.png";
    /** Story mode button image path. */
    public static final String BTN_STORY = "/images/story.png";
    /** Survival mode button image path. */
    public static final String BTN_SURVIVAL = "/images/survival.png";
    /** Two-player mode button image path. */
    public static final String BTN_TWO_PLAYER = "/images/two_player.png";
    /** High-scores button image path. */
    public static final String BTN_HIGH_SCORES = "/images/high_scores.png";
    /** Restart button image path. */
    public static final String BTN_RESTART = "/images/restart.png";

    // Basic configuration in game
    /** Initial boost count for new games. */
    public static final int INIT_BOOST_COUNT = 3; // Initial boost count
    // Default background color uses partial transparency to keep player colors visible
    /** Default background color (hex, with alpha). */
    public static final String DEFAULT_BG_COLOR = "#000000B3"; // 30% opacity
    /** Predefined background color options. */
    public static final String[] BG_COLOR_OPTIONS = {
            "#000000B3", // Black
            "#0b3d91B3", // Deep Blue
            "#0f6b0fB3", // green
            "#008b8bB3", // Teal
            "#3f1a58B3", // Purple
            "#2e2e2eB3", // Charcoal Gray
            "#b34700B3", // Orange
            "#8b0000B3"  // Dark Red
    };
    
    // Score display style constants
    /** Font family used for score rendering. */
    public static final String SCORE_FONT_FAMILY = "Courier New";
    /** Font size used for scores. */
    public static final int SCORE_FONT_SIZE = 24;
    /** Score text color for game-over state. */
    public static final String SCORE_COLOR_GAMEOVER = "#FFFFFF";  // White for game over
    /** Score text color for victory state. */
    public static final String SCORE_COLOR_VICTORY = "#FFD700";   // Gold for victory
}
