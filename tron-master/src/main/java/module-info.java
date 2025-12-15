/** Module descriptor for Tron application. */
module com.tron_master.tron {
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.desktop;

    opens com.tron_master.tron to javafx.fxml;
    opens com.tron_master.tron.controller to javafx.fxml;
    exports com.tron_master.tron;
    opens com.tron_master.tron.controller.game_controller to javafx.fxml;
    opens com.tron_master.tron.controller.interfaces to javafx.fxml;
    exports com.tron_master.tron.model.sound;
    exports com.tron_master.tron.controller.sound;
    exports com.tron_master.tron.model.object;
    exports com.tron_master.tron.model.data;
    exports com.tron_master.tron.view.utils;
    exports com.tron_master.tron.controller;

}
