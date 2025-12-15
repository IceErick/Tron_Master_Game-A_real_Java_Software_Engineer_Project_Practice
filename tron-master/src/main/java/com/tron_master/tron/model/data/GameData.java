package com.tron_master.tron.model.data;

import com.tron_master.tron.constant.GameConstant;

/**
 * Game shared data model (Singleton pattern: globally unique, accessible by all controllers/views)
 * Stores global data such as scores, levels, settings, etc.
 */
public class GameData {
    // Singleton instance (ensures global uniqueness)
    private static GameData instance;

    // Game state data
    private int survivalScore; // Survival mode score
    private int survivalBoost;
    private GameState survivalState; // Survival mode state

    private int twoPlayerP1Score; // Two-player mode player 1 score
    private int twoPlayerP2Score; // Two-player mode player 2 score
    private int player1Boost;
    private int player2Boost;
    private GameState twoPlayerState;
    private TwoPlayerOutcome twoPlayerOutcome;

    private int storyLevel; // Story mode level
    private int storyScore; // Story mode score
    private int storyBoost;
    private GameState storyState;
    private String backgroundColorHex;

    // Private constructor (prevents external instantiation)
    private GameData() {
        initializeDefaults(); // Initialize data without calling overridable methods
    }

    /**
     * Get the singleton GameData instance.
     * @return global GameData
     */
    public static GameData getInstance() {
        if (instance == null) {
            instance = new GameData();
        }
        return instance;
    }

    /**
     * Reset all persisted game data to initial defaults.
     */
    public void resetAllData() {
        this.survivalScore = 0;
        this.survivalBoost = GameConstant.INIT_BOOST_COUNT;
        this.twoPlayerP1Score = 0;
        this.twoPlayerP2Score = 0;
        this.player1Boost = GameConstant.INIT_BOOST_COUNT;
        this.player2Boost = GameConstant.INIT_BOOST_COUNT;
        this.twoPlayerState = GameState.PLAYING;
        this.twoPlayerOutcome = TwoPlayerOutcome.TIE;
        this.storyLevel = 1;
        this.storyScore = 0;
        this.storyBoost = GameConstant.INIT_BOOST_COUNT;
        this.backgroundColorHex = GameConstant.DEFAULT_BG_COLOR;
    }

    /**
     * Initialize default values. This is a private helper used by the constructor to
     * avoid calling overridable methods during construction.
     */
    private void initializeDefaults() {
        this.survivalScore = 0;
        this.survivalBoost = GameConstant.INIT_BOOST_COUNT;
        this.twoPlayerP1Score = 0;
        this.twoPlayerP2Score = 0;
        this.player1Boost = GameConstant.INIT_BOOST_COUNT;
        this.player2Boost = GameConstant.INIT_BOOST_COUNT;
        this.twoPlayerState = GameState.PLAYING;
        this.twoPlayerOutcome = TwoPlayerOutcome.TIE;
        this.storyLevel = 1;
        this.storyScore = 0;
        this.storyBoost = GameConstant.INIT_BOOST_COUNT;
        this.backgroundColorHex = GameConstant.DEFAULT_BG_COLOR;
    }

    /** Reset survival-mode specific values. */
    public void resetSurvivalData() {
        this.survivalScore = 0;
        this.survivalBoost = GameConstant.INIT_BOOST_COUNT;
        this.survivalState = GameState.PLAYING;
    }

    /** Reset two-player mode values except scores. */
    public void resetTwoPlayerData() {
        this.player1Boost = GameConstant.INIT_BOOST_COUNT;
        this.player2Boost = GameConstant.INIT_BOOST_COUNT;
        this.twoPlayerState = GameState.PLAYING;
        this.twoPlayerOutcome = TwoPlayerOutcome.TIE;
    }

    /** Reset two-player scores and related state. */
    public void resetTwoPlayerMatch() {
        resetTwoPlayerScore();
        resetTwoPlayerData();
    }
    /** Reset only two-player scores. */
    public void resetTwoPlayerScore() {
        this.twoPlayerP1Score = 0;
        this.twoPlayerP2Score = 0;
    }

    /** Reset story-mode specific values. */
    public void resetStoryData() {
        this.storyLevel = 1;
        this.storyScore = 0;
        this.storyBoost = GameConstant.INIT_BOOST_COUNT;
        this.storyState = GameState.PLAYING;
    }

// ------------------- Getter/Setter -------------------

    // ––– –– – story mode (level, score, boost, state)

    /**
     * Get current story level.
     * @return level index
     */
    public int getStoryLevel() {
    return storyLevel;
}

    /**
     * Set current story level.
     * @param storyLevel level index
     */
    public void setStoryLevel(int storyLevel) {
        this.storyLevel = storyLevel;
    }

    /**
     * Get story mode score.
     * @return score value
     */
    public int getStoryScore() {
        return storyScore;
    }

    /**
     * Set story mode score.
     * @param storyScore score value
     */
    public void setStoryScore(int storyScore) {
        this.storyScore = storyScore;
    }

    /**
     * Get available boosts in story mode.
     * @return remaining boosts
     */
    public int getStoryBoost() {return storyBoost;}

    /**
     * Set available boosts for story mode.
     * @param storyBoost remaining boosts
     */
    public void setStoryBoost(int storyBoost) {this.storyBoost = storyBoost;}

    /**
     * Get current story mode state.
     * @return state enum
     */
    public GameState getStoryState() {return storyState;}

    /**
     * Set current story mode state.
     * @param storyState state enum
     */
    public void setStoryState(GameState storyState) {this.storyState = storyState;}

    // ––– –– – survival mode (score, boost, state)

    /**
     * Get survival mode state.
     * @return state enum
     */
    public GameState getSurvivalState() {return survivalState;}

    /**
     * Set survival mode state.
     * @param survivalState state enum
     */
    public void setSurvivalState(GameState survivalState) {this.survivalState = survivalState;}

    /**
     * Get survival mode score.
     * @return score value
     */
    public int getSurvivalScore() {
        return survivalScore;
    }

    /**
     * Set survival mode score.
     * @param survivalScore score value
     */
    public void setSurvivalScore(int survivalScore) {
        this.survivalScore = survivalScore;
    }

    /**
     * Get available boosts in survival mode.
     * @return remaining boosts
     */
    public int getSurvivalBoost() {return survivalBoost;}

    /**
     * Set available boosts for survival mode.
     * @param survivalBoost remaining boosts
     */
    public void setSurvivalBoost(int survivalBoost) {this.survivalBoost = survivalBoost;}

    // ––– –– – two player mode (score1, score2, boost1, boost2, state)

    /**
     * Get player 1 score in two-player mode.
     * @return score value
     */
    public int getTwoPlayerP1Score() {
        return twoPlayerP1Score;
    }

    /**
     * Set player 1 score in two-player mode.
     * @param twoPlayerP1Score score value
     */
    public void setTwoPlayerP1Score(int twoPlayerP1Score) {
        this.twoPlayerP1Score = twoPlayerP1Score;
    }

    /**
     * Get player 2 score in two-player mode.
     * @return score value
     */
    public int getTwoPlayerP2Score() {
        return twoPlayerP2Score;
    }

    /**
     * Set player 2 score in two-player mode.
     * @param twoPlayerP2Score score value
     */
    public void setTwoPlayerP2Score(int twoPlayerP2Score) {
        this.twoPlayerP2Score = twoPlayerP2Score;
    }

    /**
     * Get player 1 remaining boosts.
     * @return boost count
     */
    public int getPlayer1Boost() { return player1Boost; }

    /**
     * Set player 1 remaining boosts.
     * @param player1Boost boost count
     */
    public void setPlayer1Boost(int player1Boost) { this.player1Boost = player1Boost;}

    /**
     * Get player 2 remaining boosts.
     * @return boost count
     */
    public int getPlayer2Boost() { return player2Boost; }

    /**
     * Set player 2 remaining boosts.
     * @param player2Boost boost count
     */
    public void setPlayer2Boost(int player2Boost) { this.player2Boost = player2Boost;}

    /**
     * Get two-player game state.
     * @return state enum
     */
    public GameState getTwoPlayerState() {return twoPlayerState;}

    /**
     * Set two-player game state.
     * @param twoPlayerState state enum
     */
    public void setTwoPlayerState(GameState twoPlayerState) {this.twoPlayerState = twoPlayerState;}

    /**
     * Get outcome of last two-player game.
     * @return outcome enum
     */
    public TwoPlayerOutcome getTwoPlayerOutcome() {return twoPlayerOutcome;}

    /**
     * Set outcome of two-player game.
     * @param twoPlayerOutcome outcome enum
     */
    public void setTwoPlayerOutcome(TwoPlayerOutcome twoPlayerOutcome) {this.twoPlayerOutcome = twoPlayerOutcome;}

    /**
     * Get current background color hex.
     * @return color string
     */
    public String getBackgroundColor() { return backgroundColorHex; }

    /**
     * Set background color hex.
     * @param backgroundColorHex color string
     */
    public void setBackgroundColor(String backgroundColorHex) { this.backgroundColorHex = backgroundColorHex; }
}
