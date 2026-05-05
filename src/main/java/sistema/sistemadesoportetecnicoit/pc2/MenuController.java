package sistema.sistemadesoportetecnicoit.pc2;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.stage.Stage;

import java.io.IOException;

public class MenuController {

    @FXML private Button btnRegistrar;
    @FXML private Button btnHistorial;
    @FXML private  Button btnSalir;


    @FXML
    public void abrirRegistro() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sistema/sistemadesoportetecnicoit/pc2/pc2_registro.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Registrar Ticket");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public void abrirHistorial() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/sistema/sistemadesoportetecnicoit/pc2/pc2_historial.fxml"));
        Parent root = loader.load();
        Stage stage = new Stage();
        stage.setTitle("Historial Ticket");
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    public void salir() throws IOException {
        Stage stage = (Stage) btnSalir.getScene().getWindow();
        stage.close();
    }
}
