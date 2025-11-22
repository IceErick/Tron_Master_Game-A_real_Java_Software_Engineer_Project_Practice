package com.tron_master.demo.controller.fxml;

import com.tron_master.demo.Game;
import com.tron_master.demo.constant.GameConstant;
import com.tron_master.demo.model.GameData;
import com.tron_master.demo.model.GameState;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.Assertions.fail;

class StoryGameControllerTest {

    @BeforeAll
    static void initFx() throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        try {
            Platform.startup(latch::countDown);
        } catch (IllegalStateException e) {
            latch.countDown(); // already started
        }
        latch.await(2, TimeUnit.SECONDS);
    }

    @BeforeEach
    void resetData() {
        GameData.getInstance().resetStoryData();
    }

    private void setPrimaryStage(Stage stage) throws Exception {
        Field field = Game.class.getDeclaredField("primaryStage");
        field.setAccessible(true);
        field.set(null, stage);
    }

    private void runOnFxAndWait(Runnable task) throws Exception {
        CountDownLatch latch = new CountDownLatch(1);
        Platform.runLater(() -> {
            try {
                task.run();
            } finally {
                latch.countDown();
            }
        });
        if (!latch.await(4, TimeUnit.SECONDS)) {
            fail("FX task timed out");
        }
    }

    @Test
    void resetButtonResetsStoryStateToDefaults() throws Exception {
        runOnFxAndWait(() -> {
            Stage stage = new Stage();
            try {
                setPrimaryStage(stage);
            } catch (Exception e) {
                fail(e);
            }

            StoryGameController controller = new StoryGameController();
            Scene storyScene = controller.createStoryScene();
            stage.setScene(storyScene);

            GameData data = GameData.getInstance();
            data.setStoryLevel(4);
            data.setStoryScore(250);
            data.setBoostCount(1);
            data.setStoryState(GameState.GAME_OVER);

            controller.onResetBtnClick();

            assertEquals(1, data.getStoryLevel());
            assertEquals(0, data.getStoryScore());
            assertEquals(GameConstant.INIT_BOOST_COUNT, data.getBoostCount());
            assertEquals(GameState.PLAYING, data.getStoryState());
        });
    }

    @Test
    void exitButtonNavigatesToPlayMenuScene() throws Exception {
        runOnFxAndWait(() -> {
            Stage stage = new Stage();
            try {
                setPrimaryStage(stage);
            } catch (Exception e) {
                fail(e);
            }

            StoryGameController controller = new StoryGameController();
            Scene storyScene = controller.createStoryScene();
            stage.setScene(storyScene);

            controller.onExitBtnClick();

            Scene current = stage.getScene();
            assertNotNull(current);
            assertNotSame(storyScene, current);
            assertNotNull(current.getRoot());
        });
    }
}
