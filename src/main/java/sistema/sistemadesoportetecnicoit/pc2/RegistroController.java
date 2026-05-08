package sistema.sistemadesoportetecnicoit.pc2;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import sistema.sistemadesoportetecnicoit.PC2Application;
import sistema.sistemadesoportetecnicoit.shared.models.Ticket;
import sistema.sistemadesoportetecnicoit.shared.utils.Clasificador;

import java.util.concurrent.atomic.AtomicInteger;

import static sistema.sistemadesoportetecnicoit.shared.config.Configuracion.HOST;
import static sistema.sistemadesoportetecnicoit.shared.config.Configuracion.PUERTO_PC1;

public class RegistroController {

    @FXML private TextField        txtDpi;
    @FXML private TextField        txtNombre;
    @FXML private ComboBox<String> cmbTipo;
    @FXML private TextField        txtMotivo;
    @FXML private Label            lblMotivoLabel;
    @FXML private Label            lblDestino;
    @FXML private Label            lblTicketId;
    @FXML private Label            lblEstado;

    private static final AtomicInteger contador = new AtomicInteger(1);
    private String ticketIdActual;

    @FXML
    public void initialize() {
        cmbTipo.setItems(FXCollections.observableArrayList(
                "Hardware", "Software", "Conectividad", "Accesos",
                "Incidente Critico", "Servidores", "Infraestructura"));
        cmbTipo.getSelectionModel().selectFirst();
        lblDestino.setText(Clasificador.getDestino(cmbTipo.getValue()));

        txtMotivo.setVisible(false);
        lblMotivoLabel.setVisible(false);

        ticketIdActual = generarTicketId();
        lblTicketId.setText("Ticket: " + ticketIdActual);
    }

    @FXML
    private void actualizarDestino() {
        String tipo = cmbTipo.getValue();
        if (tipo == null) return;

        lblDestino.setText(Clasificador.getDestino(tipo));

        boolean pideMotivo = Clasificador.esPrioridade(tipo);
        txtMotivo.setVisible(pideMotivo);
        lblMotivoLabel.setVisible(pideMotivo);
        if (!pideMotivo) txtMotivo.clear();
    }

    @FXML
    private void enviarTicket() {
        String dpi    = txtDpi.getText()    == null ? "" : txtDpi.getText().trim();
        String nombre = txtNombre.getText() == null ? "" : txtNombre.getText().trim();
        String tipo   = cmbTipo.getValue();
        String motivo = txtMotivo.isVisible() ? txtMotivo.getText().trim() : tipo;

        if (dpi.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos vacios", "Por favor ingrese su DPI."); return;
        }
        if (!dpi.matches("\\d{13}")) {
            mostrarAlerta(Alert.AlertType.WARNING, "DPI invalido", "El DPI debe contener exactamente 13 digitos."); return;
        }
        if (nombre.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos vacios", "Por favor ingrese el nombre."); return;
        }
        if (txtMotivo.isVisible() && motivo.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "Campos vacios", "Por favor describa el motivo."); return;
        }

        Boolean prioridad = Clasificador.esPrioridade(tipo);
        Ticket ticket = new Ticket(ticketIdActual, dpi, nombre, motivo, tipo, prioridad);

        Cliente cli = null;
        try {
            cli = new Cliente();
            cli.startClient();
            cli.enviarTicket(ticket);

            mostrarAlerta(Alert.AlertType.INFORMATION, "Exito",
                    "Ticket " + ticketIdActual + " enviado correctamente.\n"
                            + "Sera atendido en: " + Clasificador.getDestino(tipo));
            lblEstado.setText("Ultimo enviado: " + ticketIdActual + " -> " + Clasificador.getDestino(tipo));
            limpiarCampos();
        } catch (Exception e) {
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo conectar con el servidor (" + HOST + ":" + PUERTO_PC1 + ")\n" + e.getMessage());
        } finally {
            if (cli != null) cli.cerrar();
        }
    }

    @FXML
    private void regresarMenu() {
        PC2Application.cargarVista("pc2_menu.fxml");
    }

    private void limpiarCampos() {
        txtDpi.clear();
        txtNombre.clear();
        txtMotivo.clear();
        lblMotivoLabel.setVisible(false);
        txtMotivo.setVisible(false);
        cmbTipo.getSelectionModel().selectFirst();
        lblDestino.setText(Clasificador.getDestino(cmbTipo.getValue()));
        ticketIdActual = generarTicketId();
        lblTicketId.setText("Ticket: " + ticketIdActual);
    }

    private String generarTicketId() {
        return String.format("TK-%04d", contador.getAndIncrement());
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Alert alert = new Alert(tipo);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}
