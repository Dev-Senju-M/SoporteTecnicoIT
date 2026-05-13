package sistema.sistemadesoportetecnicoit.pc3;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import sistema.sistemadesoportetecnicoit.PC3Application;

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
            if (SesionPC3.getConexion() == null) {
                Cliente c = new Cliente();
                c.startClient();
                SesionPC3.setConexion(c);
                c.enviar(new sistema.sistemadesoportetecnicoit.shared.protocolo.Mensaje(
                        sistema.sistemadesoportetecnicoit.shared.protocolo.TipoMensaje.CHAT_MENSAJE,
                        "PC3 conectada (Técnico: " + nombre + ")",
                        "PC3"
                ));
                System.out.println("Nueva conexion creada.");
            } else{
                System.out.println("Usando conexion existente.");
            }
            SesionPC3.setTecnico(nombre);
            PC3Application.cargarVista("pc3_estacion.fxml");

        }catch (IOException e){
            lblError.setText("Error: No se pudo conectar con el Servidor Central.");
            System.err.println("Fallo de conexión inicial: " + e.getMessage());
        }

    }
}