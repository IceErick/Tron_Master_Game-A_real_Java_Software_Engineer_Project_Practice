package com.tron_master.demo.model.logic_strategy;

import com.tron_master.demo.model.GameData;
import com.tron_master.demo.model.GameState;
import com.tron_master.demo.model.player.Player;
import com.tron_master.demo.model.player.TestFixedAI;
import com.tron_master.demo.model.player.TestFixedPlayer;
import com.tron_master.demo.model.player.PlayerHuman;
import com.tron_master.demo.constant.GameConstant;
import javafx.animation.AnimationTimer;
import javafx.application.Platform;
import javafx.scene.paint.Color;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;

class StoryLogicStrategyTest {

    private static class TestAnimationTimer extends AnimationTimer {
        private boolean stopped;

        @Override
        public void handle(long now) {
            // no-op for tests
        }

        @Override
        public void stop() {
            stopped = true;
        }

        boolean isStopped() {
            return stopped;
        }
    }

    private static class TestStoryLogicStrategy extends StoryLogicStrategy {
        TestStoryLogicStrategy(int p) {
            super(p);
        }

        @Override
        public void updateGame(Player[] players) {
            // override to keep player states deterministic for assertions
        }
    }

    @BeforeAll
    static void startJavaFx() throws InterruptedException {
        try {
            CountDownLatch latch = new CountDownLatch(1);
            Platform.startup(latch::countDown);
            latch.await(2, TimeUnit.SECONDS);
        } catch (IllegalStateException ignored) {
            // JavaFX already started
        }
    }

    @BeforeEach
    void resetStoryData() {
        GameData.getInstance().resetStoryData();
    }

    @Test
    void tickStopsAndMarksGameOverWhenHumanDies() {
        TestStoryLogicStrategy logic = new TestStoryLogicStrategy(2);
        TestAnimationTimer timer = new TestAnimationTimer();

        logic.getPlayer().setAlive(false);

        logic.tick(timer);

        assertFalse(logic.isRunning());
        assertEquals(GameState.GAME_OVER, logic.getStoryState());
        assertTrue(timer.isStopped());
    }

    @Test
    void tickAwardsScoreAndCompletesLevelWhenOnlyHumanSurvives() {
        TestStoryLogicStrategy logic = new TestStoryLogicStrategy(3);
        TestAnimationTimer timer = new TestAnimationTimer();

        Player[] players = logic.getPlayers();
        for (int i = 1; i < players.length; i++) {
            players[i].setAlive(false);
        }

        logic.tick(timer);

        assertFalse(logic.isRunning());
        assertEquals(GameState.LEVEL_COMPLETE, logic.getStoryState());
        assertEquals(50 * (players.length - 1), logic.getStoryScore());
        assertTrue(timer.isStopped());
    }

    @Test
    void tickKeepsPlayingWhileMultiplePlayersAreAlive() {
        TestStoryLogicStrategy logic = new TestStoryLogicStrategy(4);
        TestAnimationTimer timer = new TestAnimationTimer();

        logic.tick(timer);

        assertTrue(logic.isRunning());
        assertEquals(GameState.PLAYING, logic.getStoryState());
        assertFalse(timer.isStopped());
    }

    @Test
    void resetRestoresStoryDefaults() {
        TestStoryLogicStrategy logic = new TestStoryLogicStrategy(2);
        GameData gameData = GameData.getInstance();
        gameData.setStoryLevel(3);
        gameData.setStoryScore(200);
        gameData.setBoostCount(1);

        logic.reset();

        assertEquals(1, logic.getStoryLevel());
        assertEquals(0, logic.getStoryScore());
        assertEquals(GameState.PLAYING, logic.getStoryState());
        assertEquals(3, logic.getBoostCount());
        assertTrue(logic.isRunning());
    }

    @Test
    void addScoreReturnsVictoryOnFinalLevel() {
        GameData gameData = GameData.getInstance();
        gameData.setStoryLevel(9);
        TestStoryLogicStrategy logic = new TestStoryLogicStrategy(2);

        GameState result = logic.addScore();

        assertEquals(GameState.VICTORY, result);
        assertEquals(50, logic.getStoryScore());
    }

    @Test
    void tickStopsWhenCollisionOccursDuringUpdateGame() {
        StoryLogicStrategy logic = new StoryLogicStrategy(2);
        TestAnimationTimer timer = new TestAnimationTimer();
        TestFixedPlayer human = new TestFixedPlayer(50, 50, 0, 0, Color.CYAN);
        TestFixedAI ai = new TestFixedAI(50, 50, 0, 0, Color.YELLOW);
        logic.players = new Player[]{human, ai};
        logic.player = human;

        logic.tick(timer);

        assertFalse(human.getAlive());
        assertEquals(GameState.GAME_OVER, logic.getStoryState());
        assertTrue(timer.isStopped());
    }

    @Test
    void tickMarksGameOverWhenHumanExitsBounds() {
        StoryLogicStrategy logic = new StoryLogicStrategy(2);
        TestAnimationTimer timer = new TestAnimationTimer();
        int beyondRight = GameConstant.GAME_AREA_WIDTH + 10;
        TestFixedPlayer human = new TestFixedPlayer(beyondRight, 100, 1, 0, Color.CYAN);
        TestFixedAI ai = new TestFixedAI(100, 100, 0, 0, Color.YELLOW);
        logic.players = new Player[]{human, ai};
        logic.player = human;

        logic.tick(timer);

        assertFalse(human.getAlive());
        assertEquals(GameState.GAME_OVER, logic.getStoryState());
        assertTrue(timer.isStopped());
    }

    @Test
    void addScoreAccumulatesAcrossMultipleWins() {
        StoryLogicStrategy logic = new StoryLogicStrategy(3);
        assertEquals(GameState.LEVEL_COMPLETE, logic.addScore());
        assertEquals(100, logic.getStoryScore());

        assertEquals(GameState.LEVEL_COMPLETE, logic.addScore());
        assertEquals(200, logic.getStoryScore());
    }

    @Test
    void addScoreReturnsGameOverWhenPlayerDead() {
        StoryLogicStrategy logic = new StoryLogicStrategy(2);
        logic.getPlayer().setAlive(false);

        GameState result = logic.addScore();

        assertEquals(GameState.GAME_OVER, result);
        assertEquals(0, logic.getStoryScore());
    }

    @Test
    void addScoreStaysLevelCompleteBeforeFinalLevel() {
        GameData data = GameData.getInstance();
        data.setStoryLevel(7);
        StoryLogicStrategy logic = new StoryLogicStrategy(2);

        GameState result = logic.addScore();

        assertEquals(GameState.LEVEL_COMPLETE, result);
        assertEquals(50, logic.getStoryScore());
    }

    @Test
    void boostingConsumesSharedBoostCount() {
        StoryLogicStrategy logic = new StoryLogicStrategy(2);
        PlayerHuman human = logic.getPlayer();
        int before = logic.getBoostCount();

        human.startBoost();

        assertTrue(human.isBoosting());
        assertEquals(before - 1, logic.getBoostCount());
    }

    @Test
    void updatePlayerVelocityAdjustsBoostSpeed() {
        StoryLogicStrategy logic = new StoryLogicStrategy(2);
        PlayerHuman human = logic.getPlayer();
        human.setVelocityX(logic.getVelocity()); // ensure a positive baseline
        logic.updatePlayerVelocity(human);
        int normalVelocity = human.getVelocityX();

        human.startBoost();
        logic.updatePlayerVelocity(human);
        int boostedVelocity = human.getVelocityX();

        assertEquals(Player.VELBOOST, boostedVelocity);
        assertTrue(boostedVelocity > normalVelocity);
    }

    @Test
    void jumpSkipsAheadByJumpHeight() {
        StoryLogicStrategy logic = new StoryLogicStrategy(2);
        TestFixedPlayer human = new TestFixedPlayer(200, 200, logic.getVelocity(), 0, Color.CYAN);
        TestFixedAI ai = new TestFixedAI(400, 400, 0, 0, Color.YELLOW);
        logic.players = new Player[]{human, ai};
        logic.player = human;

        int beforeX = human.getX();
        human.jump();
        logic.updateGame(logic.getPlayers());

        assertEquals(beforeX + Player.JUMPHEIGHT, human.getX());
        assertTrue(human.getPath().isEmpty()); // jump should not draw path segments
    }

    @Test
    void boundaryCollisionKillsPlayerAndStopsVelocity() {
        StoryLogicStrategy logic = new StoryLogicStrategy(2);
        TestFixedPlayer human = new TestFixedPlayer(-5, -5, 3, -2, Color.CYAN);

        logic.checkBoundaryCollision(human, GameConstant.GAME_AREA_WIDTH, GameConstant.GAME_AREA_HEIGHT);

        assertFalse(human.getAlive());
        assertEquals(0, human.getVelocityX());
        assertEquals(0, human.getVelocityY());
    }

    @Test
    void aiBoostDoesNotMutateSharedBoostCount() {
        GameData data = GameData.getInstance();
        data.resetStoryData();
        TestFixedAI ai = new TestFixedAI(100, 100, 1, 0, Color.YELLOW);
        int before = data.getBoostCount();

        ai.startBoost();

        assertEquals(before, data.getBoostCount());
    }
}
