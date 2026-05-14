package sistema.sistemadesoportetecnicoit.pc2;

import javafx.animation.FadeTransition;
import javafx.animation.ScaleTransition;
import javafx.animation.TranslateTransition;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import sistema.sistemadesoportetecnicoit.PC2Application;
import sistema.sistemadesoportetecnicoit.shared.models.Ticket;
import sistema.sistemadesoportetecnicoit.shared.utils.Clasificador;
import sistema.sistemadesoportetecnicoit.shared.utils.TemaManager;

import java.util.concurrent.atomic.AtomicInteger;

public class RegistroController {

    @FXML private VBox screenRoot;
    @FXML private VBox panelHeader;
    @FXML private TextField txtDpi;
    @FXML private TextField txtNombre;
    @FXML private ComboBox<String> cmbTipo;
    @FXML private TextField txtMotivo;
    @FXML private Label lblMotivoLabel;
    @FXML private Label lblDestino;
    @FXML private Label lblTicketId;
    @FXML private Label lblEstado;
    @FXML private Button btnEnviar;
    @FXML private FontIcon iconTema;

    private static final AtomicInteger contador = new AtomicInteger(1);
    private String ticketIdActual;

    @FXML
    public void initialize() {
        screenRoot.setOpacity(0);
        panelHeader.setTranslateY(-12);

        FadeTransition fade = new FadeTransition(Duration.millis(300), screenRoot);
        fade.setFromValue(0); fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(280), panelHeader);
        slide.setFromY(-12); slide.setToY(0);

        fade.play(); slide.play();

        TemaManager.aplicar(screenRoot);
        actualizarIconTema();

        cmbTipo.setItems(FXCollections.observableArrayList(
                "Hardware", "Software", "Conectividad", "Accesos",
                "Incidente Critico", "Servidores", "Infraestructura"));
        cmbTipo.getSelectionModel().selectFirst();
        lblDestino.setText(Clasificador.getDestino(cmbTipo.getValue()));

        txtMotivo.setVisible(false);
        lblMotivoLabel.setVisible(false);

        ticketIdActual = generarTicketId();
        lblTicketId.setText(ticketIdActual);
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

        boolean valid = true;
        if (dpi.isEmpty() || !dpi.matches("\\d{13}")) { shake(txtDpi);    valid = false; }
        if (nombre.isEmpty())                          { shake(txtNombre); valid = false; }
        if (!valid) { lblEstado.setText("Revise los campos en rojo."); return; }

        // Pulso en el botón
        pulse(btnEnviar);

        boolean prioridad = Clasificador.esPrioridade(tipo);
        Ticket ticket = new Ticket(ticketIdActual, dpi, nombre, motivo, tipo, prioridad);

        new Thread(() -> {
            try {
                Cliente servidor = SesionPC2.getConexion();
                if (servidor == null) throw new Exception("No hay conexion activa con el servidor.");
                servidor.enviarTicket(ticket);
                javafx.application.Platform.runLater(() -> {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Exito",
                            "Ticket " + ticket.getTicketId() + " enviado.");
                    lblEstado.setText("Ultimo enviado: " + ticket.getTicketId());
                    limpiarCampos();
                });
            } catch (Exception e) {
                javafx.application.Platform.runLater(() ->
                        mostrarAlerta(Alert.AlertType.ERROR, "Error de Red",
                                "Fallo al enviar el ticket: " + e.getMessage()));
            }
        }, "pc2-sender").start();
    }

    @FXML private void regresarMenu() { PC2Application.cargarVista("pc2_menu.fxml"); }

    @FXML
    private void toggleTema() {
        TemaManager.toggle();
        TemaManager.aplicar(screenRoot);
        actualizarIconTema();
    }

    private void actualizarIconTema() {
        iconTema.setIconLiteral(TemaManager.esModoClaro() ? "fas-moon" : "fas-sun");
    }

    private void shake(Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(65), node);
        tt.setByX(8);
        tt.setCycleCount(6);
        tt.setAutoReverse(true);
        tt.setOnFinished(e -> node.setTranslateX(0));
        tt.play();
    }

    private void pulse(Node node) {
        ScaleTransition st = new ScaleTransition(Duration.millis(100), node);
        st.setFromX(1); st.setFromY(1);
        st.setToX(0.94); st.setToY(0.94);
        st.setCycleCount(2);
        st.setAutoReverse(true);
        st.play();
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
        lblTicketId.setText(ticketIdActual);
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
