package sistema.sistemadesoportetecnicoit.pc5;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import sistema.sistemadesoportetecnicoit.PC5Application;
import sistema.sistemadesoportetecnicoit.shared.chat.ChatController;
import sistema.sistemadesoportetecnicoit.shared.models.Ticket;
import sistema.sistemadesoportetecnicoit.shared.utils.TemaManager;

public class EstacionController {

    @FXML private VBox screenRoot;
    @FXML private VBox panelHeader;
    @FXML private Label lblTecnico;
    @FXML private Label lblEstado;
    @FXML private Label lblInfo;
    @FXML private Button btnAtender;
    @FXML private ProgressIndicator spinner;
    @FXML private FontIcon iconTema;

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

        lblTecnico.setText("Tecnico: " + SesionPC5.getTecnico());
    }

    @FXML
    private void atenderSiguiente() {
        btnAtender.setDisable(true);
        spinner.setVisible(true);
        lblEstado.setText("Solicitando ticket al servidor...");

        Thread th = new Thread(() -> {
            try {
                Ticket t = solicitarTicket();
                Platform.runLater(() -> {
                    spinner.setVisible(false);
                    if (t == null) {
                        lblEstado.setText("No hay tickets en cola. Reintente.");
                        btnAtender.setDisable(false);
                    } else {
                        t.marcarInicioAtencion();
                        SesionPC5.setTicketActual(t);
                        PC5Application.cargarVista("pc5_atencion.fxml");
                    }
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    spinner.setVisible(false);
                    btnAtender.setDisable(false);
                    lblEstado.setText("Error al solicitar ticket.");
                    mostrarAlerta(Alert.AlertType.ERROR, "Error",
                            "No se pudo conectar con el servidor:\n"
                                    + (ex.getMessage() != null ? ex.getMessage() : "desconocido"));
                });
            }
        }, "pc5-solicitar");
        th.setDaemon(true);
        th.start();
    }

    @FXML private void abrirChat() { ChatController.abrir("PC5", SesionPC5.getConexion()); }

    @FXML
    private void toggleTema() {
        TemaManager.toggle();
        TemaManager.aplicar(screenRoot);
        actualizarIconTema();
    }

    private void actualizarIconTema() {
        iconTema.setIconLiteral(TemaManager.esModoClaro() ? "fas-moon" : "fas-sun");
    }

    private Ticket solicitarTicket() throws Exception {
        Cliente cli = SesionPC5.getConexion();
        if (cli == null) throw new Exception("No hay conexion activa con el servidor.");
        return cli.solicitarTicket("ESPECIAL");
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Runnable r = () -> {
            Alert a = new Alert(tipo);
            a.setTitle(titulo); a.setHeaderText(null); a.setContentText(mensaje);
            a.showAndWait();
        };
        if (Platform.isFxApplicationThread()) r.run();
        else Platform.runLater(r);
    }
}
