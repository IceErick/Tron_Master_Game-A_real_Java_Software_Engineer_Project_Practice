package com.tron_master.tron.model.object;

/**
 * Observer interface for listening to object state changes.
 * Implement this interface to react to object events (death, boost, jump, etc.).
 * 
 * This allows the View and Controller layers to observe Model changes without tight coupling.
 */
public interface PlayerStateListener {
    
    /**
     * Called when a object dies (alive state changes to false).
     * @param player The object that died
     */
    void onPlayerDied(Player player);
    
    /**
     * Called when a object activates boost.
     * @param player The object that boosted
     * @param boostLeft Remaining boost count
     */
    default void onPlayerBoosted(Player player, int boostLeft) {
    }
    
    /**
     * Called when a object jumps.
     * @param player The object that jumped
     */
    default void onPlayerJumped(Player player) {
    }
}
