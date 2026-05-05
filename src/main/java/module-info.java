module sistema.sistemadesoportetecnicoit {
    requires javafx.controls;
    requires javafx.fxml;


    opens sistema.sistemadesoportetecnicoit to javafx.fxml;
    exports sistema.sistemadesoportetecnicoit;
}