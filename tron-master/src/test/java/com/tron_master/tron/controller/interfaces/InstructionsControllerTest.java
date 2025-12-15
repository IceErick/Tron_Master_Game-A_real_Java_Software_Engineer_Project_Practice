package com.tron_master.tron.controller.interfaces;

import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertSame;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import com.tron_master.tron.testutil.FxTestUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import javafx.stage.Stage;

class InstructionsControllerTest {

    @BeforeAll
    static void startFx() throws InterruptedException {
        FxTestUtils.initFx();
    }

    @BeforeEach
    void clearInstance() throws Exception {
        FxTestUtils.clearInstructionsWindow();
    }

    @BeforeEach
    void resetInstructionsState() throws Exception {
        FxTestUtils.runOnFxAndWait(() -> {
            try {
                FxTestUtils.clearInstructionsWindow();
            } catch (Exception e) {
                fail(e);
            }
        });
    }

    @Test
    void showInstructionsWindowCreatesStage() throws Exception {
        FxTestUtils.runOnFxAndWait(() -> {
            Stage primary = new Stage();
            try {
                FxTestUtils.setPrimaryStage(primary);

                InstructionsController.showInstructionsWindow();

                InstructionsController controller = FxTestUtils.getInstructionsInstance();
                assertNotNull(controller);
                Stage dialog = FxTestUtils.getInstructionsStage(controller);
                assertNotNull(dialog);
                assertTrue(dialog.isShowing());
                assertNotNull(dialog.getScene());
                assertNotNull(dialog.getScene().getRoot());

                dialog.close();
                assertNull(FxTestUtils.getInstructionsInstance());
            } catch (Exception e) {
                fail(e);
            } finally {
                primary.close();
            }
        });
    }

    @Test
    void showInstructionsWindowReusesExistingStage() throws Exception {
        FxTestUtils.runOnFxAndWait(() -> {
            Stage primary = new Stage();
            try {
                FxTestUtils.setPrimaryStage(primary);

                InstructionsController.showInstructionsWindow();
                InstructionsController first = FxTestUtils.getInstructionsInstance();
                Stage dialog1 = FxTestUtils.getInstructionsStage(first);

                InstructionsController.showInstructionsWindow();
                InstructionsController second = FxTestUtils.getInstructionsInstance();
                Stage dialog2 = FxTestUtils.getInstructionsStage(second);

                assertSame(first, second);
                assertSame(dialog1, dialog2);
                assertTrue(dialog1.isShowing());

                dialog1.close();
                assertNull(FxTestUtils.getInstructionsInstance());
            } catch (Exception e) {
                fail(e);
            } finally {
                primary.close();
            }
        });
    }

    @Test
    void closeButtonClearsInstance() throws Exception {
        FxTestUtils.runOnFxAndWait(() -> {
            Stage primary = new Stage();
            try {
                FxTestUtils.setPrimaryStage(primary);

                InstructionsController.showInstructionsWindow();
                InstructionsController controller = FxTestUtils.getInstructionsInstance();
                assertNotNull(controller);
                Stage dialog = FxTestUtils.getInstructionsStage(controller);

                controller.onCloseButtonClick();

                assertFalse(dialog.isShowing());
                assertNull(FxTestUtils.getInstructionsInstance());
            } catch (Exception e) {
                fail(e);
            } finally {
                primary.close();
            }
        });
    }
}
