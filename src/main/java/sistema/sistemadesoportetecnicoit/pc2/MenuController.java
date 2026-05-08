package sistema.sistemadesoportetecnicoit.pc2;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import sistema.sistemadesoportetecnicoit.PC2Application;
import sistema.sistemadesoportetecnicoit.shared.config.Configuracion;

public class MenuController {

    @FXML private Label lblServidor;

    @FXML
    public void initialize() {
        lblServidor.setText("Servidor: " + Configuracion.HOST + ":" + Configuracion.PUERTO_PC1);
    }

    @FXML
    private void abrirRegistro() {
        PC2Application.cargarVista("pc2_registro.fxml");
    }

    @FXML
    private void abrirHistorial() {
        PC2Application.cargarVista("pc2_historial.fxml");
    }

    @FXML
    private void salir() {
        Platform.exit();
    }
}