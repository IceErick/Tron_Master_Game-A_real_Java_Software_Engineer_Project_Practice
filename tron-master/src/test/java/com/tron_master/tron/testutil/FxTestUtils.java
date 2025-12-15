package com.tron_master.tron.testutil;

import java.lang.reflect.Field;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

import com.tron_master.tron.Game;
import com.tron_master.tron.controller.interfaces.InstructionsController;

import javafx.application.Platform;
import javafx.stage.Stage;

/**
 * Lightweight JavaFX helpers for unit tests.
 * Handles JavaFX toolkit startup, runs tasks on the FX thread, and provides reflection
 * helpers to access private fields in {@link Game} and {@link InstructionsController}
 * that are otherwise inaccessible in tests.
 */
public final class FxTestUtils {

    private static final long FX_TIMEOUT_SECONDS = 5L;
    private static volatile boolean fxInitialized = false;

    private FxTestUtils() {
    }

    /**
     * Initialize the JavaFX runtime if it has not been started yet.
     */
    public static void initFx() throws InterruptedException {
        if (fxInitialized) {
            return;
        }
        synchronized (FxTestUtils.class) {
            if (fxInitialized) {
                return;
            }
            CountDownLatch latch = new CountDownLatch(1);
            try {
                Platform.startup(latch::countDown);
            } catch (IllegalStateException alreadyStarted) {
                // Toolkit already running, just mark as initialized
                latch.countDown();
            }
            if (!latch.await(FX_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                throw new IllegalStateException("Timed out starting JavaFX platform");
            }
            fxInitialized = true;
        }
    }

    /**
     * Run the given task on the JavaFX Application Thread and wait for completion.
     * Any thrown exception is rethrown to the caller.
     */
    public static void runOnFxAndWait(Runnable task) throws Exception {
        if (Platform.isFxApplicationThread()) {
            task.run();
            return;
        }
        CountDownLatch latch = new CountDownLatch(1);
        AtomicReference<Throwable> error = new AtomicReference<>();
        Platform.runLater(() -> {
            try {
                task.run();
            } catch (Throwable t) {
                error.set(t);
            } finally {
                latch.countDown();
            }
        });
        if (!latch.await(FX_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
            throw new IllegalStateException("Timeout waiting for FX task to finish");
        }
        if (error.get() != null) {
            Throwable throwable = error.get();
            if (throwable instanceof Exception) {
                throw (Exception) throwable;
            }
            throw new RuntimeException(throwable);
        }
    }

    /**
     * Inject a primary stage into the Game class (used for dialog positioning).
     */
    public static void setPrimaryStage(Stage stage) throws Exception {
        Field primaryStageField = Game.class.getDeclaredField("primaryStage");
        primaryStageField.setAccessible(true);
        primaryStageField.set(null, stage);
    }

    /**
     * Get the singleton InstructionsController instance via reflection.
     */
    public static InstructionsController getInstructionsInstance() throws Exception {
        Field instanceField = InstructionsController.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        return (InstructionsController) instanceField.get(null);
    }

    /**
     * Get the dialog Stage stored inside the controller via reflection.
     */
    public static Stage getInstructionsStage(InstructionsController controller) throws Exception {
        if (controller == null) {
            return null;
        }
        Field stageField = InstructionsController.class.getDeclaredField("stage");
        stageField.setAccessible(true);
        return (Stage) stageField.get(controller);
    }

    /**
     * Close the instructions dialog if present and clear the singleton instance.
     */
    public static void clearInstructionsWindow() throws Exception {
        Runnable clearTask = () -> {
            try {
                InstructionsController controller = getInstructionsInstance();
                Stage dialog = getInstructionsStage(controller);
                if (dialog != null) {
                    dialog.close();
                }
                clearInstructionsInstance();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        };

        if (Platform.isFxApplicationThread()) {
            clearTask.run();
        } else {
            runOnFxAndWait(clearTask);
        }
    }

    private static void clearInstructionsInstance() throws Exception {
        Field instanceField = InstructionsController.class.getDeclaredField("instance");
        instanceField.setAccessible(true);
        instanceField.set(null, null);
    }
}
