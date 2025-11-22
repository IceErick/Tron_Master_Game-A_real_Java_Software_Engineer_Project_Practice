package com.tron_master.demo.model;

/**
 * Game shared data model (Singleton pattern: globally unique, accessible by all controllers/views)
 * Stores global data such as scores, levels, settings, etc.
 */
public class GameData {
    // Singleton instance (ensures global uniqueness)
    private static GameData instance;

    // Game state data
    private int survivalScore; // Survival mode score
    private int twoPlayerP1Score; // Two-player mode player 1 score
    private int twoPlayerP2Score; // Two-player mode player 2 score
    private int storyLevel; // Story mode level
    private int storyScore; // Story mode score
    private int boostCount;
    private GameState storyState;

    // Private constructor (prevents external instantiation)
    private GameData() {
        resetAllData(); // Initialize data
    }

    // Global instance getter
    public static GameData getInstance() {
        if (instance == null) {
            instance = new GameData();
        }
        return instance;
    }

    // Reset all game data (called when game restarts/switches modes)
    public void resetAllData() {
        this.survivalScore = 0;
        this.twoPlayerP1Score = 0;
        this.twoPlayerP2Score = 0;
        this.storyLevel = 1;
        this.storyScore = 0;
        this.boostCount = 3;
    }

    // Reset survival mode data
    public void resetSurvivalData() {
        this.survivalScore = 0;
    }

    // Reset two-player mode data
    public void resetTwoPlayerData() {
        this.twoPlayerP1Score = 0;
        this.twoPlayerP2Score = 0;
    }

    // Reset story mode data
    public void resetStoryData() {
        this.storyLevel = 1;
        this.storyScore = 0;
        this.boostCount = 3;
        this.storyState = GameState.PLAYING;
    }

    // ------------------- Getter/Setter -------------------
    public int getSurvivalScore() {
        return survivalScore;
    }

    public void setSurvivalScore(int survivalScore) {
        this.survivalScore = survivalScore;
    }

    public int getTwoPlayerP1Score() {
        return twoPlayerP1Score;
    }

    public void setTwoPlayerP1Score(int twoPlayerP1Score) {
        this.twoPlayerP1Score = twoPlayerP1Score;
    }

    public int getTwoPlayerP2Score() {
        return twoPlayerP2Score;
    }

    public void setTwoPlayerP2Score(int twoPlayerP2Score) {
        this.twoPlayerP2Score = twoPlayerP2Score;
    }

    public int getStoryLevel() {
        return storyLevel;
    }

    public void setStoryLevel(int storyLevel) {
        this.storyLevel = storyLevel;
    }

    public int getStoryScore() {
        return storyScore;
    }

    public void setStoryScore(int storyScore) {
        this.storyScore = storyScore;
    }

    public int getBoostCount() {return boostCount;}

    public void setBoostCount(int boostCount) {this.boostCount = boostCount;}

    public GameState getStoryState() {return storyState;}

    public void setStoryState(GameState storyState) {this.storyState = storyState;}
}