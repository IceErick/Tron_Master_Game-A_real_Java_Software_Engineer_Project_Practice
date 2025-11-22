package com.tron_master.demo.model;

/**
 * Game state enumeration
 * Define the possible states of the game
 */
public enum GameState {
    // game is playing
    PLAYING,
    // level is complete (story mode)
    LEVEL_COMPLETE,
    // game is over
    GAME_OVER,
    // game is paused
    PAUSED,
    // game is won
    VICTORY
}