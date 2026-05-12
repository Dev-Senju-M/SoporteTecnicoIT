package sistema.sistemadesoportetecnicoit.pc4;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import sistema.sistemadesoportetecnicoit.PC4Application;
import sistema.sistemadesoportetecnicoit.pc4.Cliente;
import sistema.sistemadesoportetecnicoit.pc4.SesionPC4;

import java.io.IOException;

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

        try{
            if (SesionPC4.getConexion() == null) {
                Cliente c = new Cliente();
                c.startClient();
                SesionPC4.setConexion(c);
                System.out.println("Nueva conexion creada.");
            } else{
                System.out.println("Usando conexion existente.");
            }
            SesionPC4.setTecnico(nombre);
            PC4Application.cargarVista("pc4_estacion.fxml");

        }catch (IOException e){
            lblError.setText("Error: No se pudo conectar con el Servidor Central.");
            System.err.println("Fallo de conexión inicial: " + e.getMessage());
        }

    }
}
