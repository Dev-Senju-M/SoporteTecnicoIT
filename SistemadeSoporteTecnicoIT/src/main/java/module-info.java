module Sistema {
    requires javafx.controls;
    requires javafx.fxml;

    opens Sistema to javafx.fxml;
    exports Sistema;
}
