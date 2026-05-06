package sistema.sistemadesoportetecnicoit.pc2;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;

import sistema.sistemadesoportetecnicoit.shared.models.Ticket;
import sistema.sistemadesoportetecnicoit.shared.utils.Clasificador;

public class RegistroController {

    @FXML private TextField        txtDpi;
    @FXML private TextField        txtNombre;
    @FXML private ComboBox<String> cmbTipo;
    @FXML private TextArea         txtMotivo;
    @FXML private Label            lblDestino;

    @FXML
    private void initialize() {
        cmbTipo.setItems(FXCollections.observableArrayList(
                "Hardware",
                "Software",
                "Red/Internet",
                "Servidor",
                "Accesos/Permisos"
        ));
        lblDestino.setText("-");
    }

    @FXML
    private void actualizarDestino() {
        String tipo = cmbTipo.getValue();
        if (tipo == null) {
            lblDestino.setText("-");
            return;
        }
        lblDestino.setText(Clasificador.getDestino(tipo));
    }

    @FXML
    private void enviarTicket() {
        String dpi    = txtDpi.getText().trim();
        String nombre = txtNombre.getText().trim();
        String tipo   = cmbTipo.getValue();
        String motivo = txtMotivo.getText().trim();

        if (dpi.isEmpty() || nombre.isEmpty() || tipo == null || motivo.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING,
                    "Campos incompletos",
                    "Por favor llena todos los campos antes de enviar.");
            return;
        }

        Ticket ticket = new Ticket(dpi, nombre, motivo, tipo,true);
        Clasificador.mostrarClasificacion(ticket);

        mostrarAlerta(Alert.AlertType.INFORMATION,
                "Ticket registrado",
                "Ticket creado para " + nombre +
                        "\nDestino: " + Clasificador.getDestino(tipo));

        cerrarVentana();
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }

    private void cerrarVentana() {
        Stage stage = (Stage) txtDpi.getScene().getWindow();
        stage.close();
    }
}
