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

        if (dpi.isEmpty() || !dpi.matches("\\d{13}") || nombre.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "validacion", "Revise los campos obligatorios.");
            return;
        }

        Boolean prioridad = Clasificador.esPrioridade(tipo);
        Ticket ticket = new Ticket(ticketIdActual, dpi, nombre, motivo, tipo, prioridad);

        new Thread(() -> {
            Cliente cli = null;
            try {
                cli = new Cliente();
                cli.startClient();
                cli.enviarTicket(ticket);

                javafx.application.Platform.runLater(() -> {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito",
                            "Ticket " + ticket.getTicketId() + " enviado.");
                    lblEstado.setText("Último enviado: " + ticket.getTicketId());
                    limpiarCampos();
                });

            }catch(Exception e){
                javafx.application.Platform.runLater(() -> {
                    mostrarAlerta(Alert.AlertType.ERROR,"Error de Red",
                            "No se pudo conectar con PC1: " + e.getMessage());
                });
            }finally{
                if (cli != null) cli.cerrar();
            }
        }).start();
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
