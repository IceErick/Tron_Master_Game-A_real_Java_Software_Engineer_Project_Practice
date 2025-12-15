package com.tron_master.tron.unit.model;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.tron_master.tron.constant.GameConstant;
import com.tron_master.tron.model.data.ColorValue;
import com.tron_master.tron.model.data.GameData;
import com.tron_master.tron.model.data.Intersection;
import com.tron_master.tron.model.object.Player;
import com.tron_master.tron.model.object.PlayerHuman;
import com.tron_master.tron.model.object.PlayerStateListener;

/**
 * Unit tests for Player class.
 * Tests boost logic, crash handling, and observer notifications.
 */
class PlayerTest {

    private static final ColorValue TEST_COLOR = new ColorValue(1.0, 0.0, 0.0);
    private PlayerHuman player;

    @BeforeEach
    void setUp() {
        GameData.getInstance().resetAllData();
        player = new PlayerHuman(100, 100, 3, 0, TEST_COLOR, "survival");
        player.setBounds(GameConstant.GAME_AREA_WIDTH, GameConstant.GAME_AREA_HEIGHT);
    }

    // ===== Boost Tests =====

    @Test
    void startBoost_decreasesBoostCount() {
        int initialBoost = player.getBoostLeft();
        
        player.startBoost();
        
        assertEquals(initialBoost - 1, player.getBoostLeft());
    }

    @Test
    void startBoost_doesNothingWhenNoBoostLeft() {
        // Exhaust all boosts
        while (player.getBoostLeft() > 0) {
            player.startBoost();
        }
        
        player.startBoost(); // Try one more
        
        assertEquals(0, player.getBoostLeft());
    }

    @Test
    void startBoost_updatesSurvivalGameData() {
        player.startBoost();
        
        assertEquals(player.getBoostLeft(), GameData.getInstance().getSurvivalBoost());
    }

    @Test
    void boost_increasesVelocityWhenBoosting() {
        player.startBoost();
        player.boost();
        
        assertEquals(Player.VELBOOST, player.getVelocityX());
    }

    @Test
    void boost_resetsVelocityAfterBoostEnds() {
        int originalVelocity = player.getStartVelocity();
        player.startBoost();
        
        // Simulate boost duration ending
        for (int i = 0; i < 20; i++) {
            player.boost();
        }
        
        assertEquals(originalVelocity, player.getVelocityX());
        assertFalse(player.isBoosting());
    }

    // ===== Crash Tests =====

    @Test
    void crash_setsPlayerDeadOnIntersectionUp() {
        assertTrue(player.getAlive());
        
        player.crash(Intersection.UP);
        
        assertFalse(player.getAlive());
        assertEquals(0, player.getVelocityX());
        assertEquals(0, player.getVelocityY());
    }

    @Test
    void crash_doesNothingOnIntersectionNone() {
        player.crash(Intersection.NONE);
        
        assertTrue(player.getAlive());
    }

    @Test
    void crash_doesNothingWhenAlreadyDead() {
        player.setAlive(false);
        
        player.crash(Intersection.UP);
        
        assertFalse(player.getAlive());
    }

    // ===== Observer Pattern Tests =====

    @Test
    void setAlive_notifiesListenerOnDeath() {
        AtomicBoolean notified = new AtomicBoolean(false);
        player.addStateListener(new PlayerStateListener() {
            @Override
            public void onPlayerDied(Player p) {
                notified.set(true);
                assertEquals(player, p);
            }
            @Override
            public void onPlayerBoosted(Player p, int remaining) {}
            @Override
            public void onPlayerJumped(Player p) {}
        });
        
        player.setAlive(false);
        
        assertTrue(notified.get());
    }

    @Test
    void setAlive_doesNotNotifyWhenStillAlive() {
        AtomicBoolean notified = new AtomicBoolean(false);
        player.addStateListener(new PlayerStateListener() {
            @Override
            public void onPlayerDied(Player p) { notified.set(true); }
            @Override
            public void onPlayerBoosted(Player p, int remaining) {}
            @Override
            public void onPlayerJumped(Player p) {}
        });
        
        player.setAlive(true);
        
        assertFalse(notified.get());
    }

    @Test
    void startBoost_notifiesListener() {
        AtomicInteger remainingBoost = new AtomicInteger(-1);
        player.addStateListener(new PlayerStateListener() {
            @Override
            public void onPlayerDied(Player p) {}
            @Override
            public void onPlayerBoosted(Player p, int remaining) {
                remainingBoost.set(remaining);
            }
            @Override
            public void onPlayerJumped(Player p) {}
        });
        
        player.startBoost();
        
        assertEquals(player.getBoostLeft(), remainingBoost.get());
    }

    @Test
    void removeStateListener_stopsNotifications() {
        AtomicBoolean notified = new AtomicBoolean(false);
        PlayerStateListener listener = new PlayerStateListener() {
            @Override
            public void onPlayerDied(Player p) { notified.set(true); }
            @Override
            public void onPlayerBoosted(Player p, int remaining) {}
            @Override
            public void onPlayerJumped(Player p) {}
        };
        
        player.addStateListener(listener);
        player.removeStateListener(listener);
        player.setAlive(false);
        
        assertFalse(notified.get());
    }

    // ===== Boundary Tests =====

    @Test
    void accelerate_setsDeadWhenOutOfBoundsRight() {
        player = new PlayerHuman(GameConstant.GAME_AREA_WIDTH + 10, 100, 3, 0, TEST_COLOR, "survival");
        player.setBounds(GameConstant.GAME_AREA_WIDTH, GameConstant.GAME_AREA_HEIGHT);
        
        player.accelerate();
        
        assertFalse(player.getAlive());
    }

    @Test
    void accelerate_setsDeadWhenOutOfBoundsBottom() {
        player = new PlayerHuman(100, GameConstant.GAME_AREA_HEIGHT + 10, 0, 3, TEST_COLOR, "survival");
        player.setBounds(GameConstant.GAME_AREA_WIDTH, GameConstant.GAME_AREA_HEIGHT);
        
        player.accelerate();
        
        assertFalse(player.getAlive());
    }
}
