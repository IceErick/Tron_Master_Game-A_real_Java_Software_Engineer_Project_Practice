module com.tron_master.demo {
    requires javafx.base;
    requires javafx.graphics;
    requires javafx.controls;
    requires javafx.fxml;
    requires javafx.media;
    requires java.desktop;

    opens com.tron_master.demo to javafx.fxml;
    opens com.tron_master.demo.controller to javafx.fxml;
    exports com.tron_master.demo;
    opens com.tron_master.demo.controller.fxml to javafx.fxml;

}