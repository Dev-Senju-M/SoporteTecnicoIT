module sistema.sistemadesoportetecnicoit {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.base;

    opens sistema.sistemadesoportetecnicoit to javafx.fxml;
    opens sistema.sistemadesoportetecnicoit.pc2 to javafx.fxml;
    opens sistema.sistemadesoportetecnicoit.shared.models to javafx.fxml;
    opens sistema.sistemadesoportetecnicoit.shared.structures to javafx.fxml;
    opens sistema.sistemadesoportetecnicoit.shared.utils to javafx.fxml;

    exports sistema.sistemadesoportetecnicoit;
}