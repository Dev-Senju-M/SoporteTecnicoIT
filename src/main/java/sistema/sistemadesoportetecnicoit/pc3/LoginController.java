package sistema.sistemadesoportetecnicoit.pc3;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import sistema.sistemadesoportetecnicoit.PC3Application;

public class LoginController {

    @FXML private TextField txtNombre;
    @FXML private Label lblError;

    @FXML
    private void iniciar() {
        String nombre = txtNombre.getText() == null ? "" : txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            lblError.setText("Ingrese su nombre para continuar.");
            return;
        }
        SesionPC3.setTecnico(nombre);
        PC3Application.cargarVista("pc3_estacion.fxml");
    }
}