package com.tron_master.tron.model.data;

/**
 * Game state enumeration
 * Define the possible states of the game
 */
public enum GameState {
    /** Game is actively running. */
    PLAYING,
    /** Current level is completed (story mode). */
    LEVEL_COMPLETE,
    /** Game is over. */
    GAME_OVER,
    /** Game is paused. */
    PAUSED,
    /** Game is won. */
    VICTORY
}
