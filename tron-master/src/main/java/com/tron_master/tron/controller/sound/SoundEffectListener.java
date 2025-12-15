package com.tron_master.tron.controller.sound;

import com.tron_master.tron.model.object.Player;
import com.tron_master.tron.model.object.PlayerStateListener;
import com.tron_master.tron.model.sound.SoundManager;

/**
 * Sound effect listener that triggers audio feedback for player state changes.
 * Implements the Observer pattern to react to player events.
 * This class separates sound logic from game logic, maintaining clean MVC architecture.
 */
public class SoundEffectListener implements PlayerStateListener {
    
    private final SoundManager soundManager;
    
    /**
     * Create a new listener wired to the shared SoundManager.
     */
    public SoundEffectListener() {
        this.soundManager = SoundManager.getInstance();
    }
    
    /**
     * Play death sound when player dies.
     */
    @Override
    public void onPlayerDied(Player player) {
        soundManager.playSoundEffect("player_died");
    }
    
    /**
     * Play boost sound when player activates boost.
     */
    @Override
    public void onPlayerBoosted(Player player, int boostLeft) {
        soundManager.playSoundEffect("boost");
    }
    
    /**
     * Play jump sound when player jumps.
     */
    @Override
    public void onPlayerJumped(Player player) {
        soundManager.playSoundEffect("jump");
    }
}
