package sistema.sistemadesoportetecnicoit.pc2;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.scene.control.Button;
import java.io.IOException;

public class MenuController {

    @FXML private Button btnRegistrar;
    @FXML private Button btnHistorial;
    @FXML private Button btnSalir;

    @FXML
    private void abrirRegistro() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(
            "/sistema/sistemadesoportetecnicoit/pc2/pc2_registro.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Registrar Ticket");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void abrirHistorial() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource(
            "/sistema/sistemadesoportetecnicoit/pc2/pc2_historial.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Consultar Historial");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void salir() {
        Stage stage = (Stage) btnSalir.getScene().getWindow();
        stage.close();
    }
}