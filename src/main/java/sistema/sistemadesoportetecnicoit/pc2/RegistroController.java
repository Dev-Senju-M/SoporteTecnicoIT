package sistema.sistemadesoportetecnicoit.pc2;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import sistema.sistemadesoportetecnicoit.shared.models.Ticket;
import sistema.sistemadesoportetecnicoit.shared.utils.Clasificador;

import java.io.ObjectOutputStream;
import java.net.Socket;

public class RegistroController {

    @FXML private TextField txtDpi;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<String> cmbTipo;
    @FXML private TextField txtMotivo;
    @FXML private Label lblDestino;

    private static final String HOST   = "localhost";
    private static final int    PUERTO = 5000;

    @FXML
    public void initialize() {
        cmbTipo.setItems(FXCollections.observableArrayList(
            "Hardware", "Software", "Red/Internet",
            "Servidor", "Accesos/Permisos", "Otro"
        ));
        cmbTipo.getSelectionModel().selectFirst();
        lblDestino.setText(Clasificador.getDestino("Hardware"));
    }

    @FXML
    private void actualizarDestino() {
        String tipo = cmbTipo.getValue();
        if (tipo != null) {
            lblDestino.setText(Clasificador.getDestino(tipo));
        }
    }

    @FXML
    private void enviarTicket() {
        String dpi            = txtDpi.getText().trim();
        String nombreApellido = txtNombre.getText().trim();
        String tipo           = cmbTipo.getValue();
        String motivo         = txtMotivo.getText().trim();

        if (dpi.isEmpty() || nombreApellido.isEmpty() || motivo.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING,
                "Campos vacíos", "Por favor complete todos los campos.");
            return;
        }

        Ticket ticket = new Ticket(dpi, nombreApellido, motivo, tipo);

        try {
            Socket socket          = new Socket(HOST, PUERTO);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(ticket);
            out.flush();
            socket.close();
            mostrarAlerta(Alert.AlertType.INFORMATION,
                "Éxito", "Ticket enviado correctamente.\nSerá atendido en: "
                + Clasificador.getDestino(tipo));
            limpiarCampos();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR,
                "Error", "No se pudo conectar con el servidor:\n" + e.getMessage());
        }
    }

    private void limpiarCampos() {
        txtDpi.clear();
        txtNombre.clear();
        txtMotivo.clear();
        cmbTipo.getSelectionModel().selectFirst();
        lblDestino.setText("PC3");
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}