package sistema.sistemadesoportetecnicoit.pc5;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import sistema.sistemadesoportetecnicoit.PC5Application;
import sistema.sistemadesoportetecnicoit.shared.models.Ticket;
import sistema.sistemadesoportetecnicoit.shared.utils.BancoPreguntas;
import sistema.sistemadesoportetecnicoit.shared.utils.TemaManager;

import java.util.LinkedHashMap;
import java.util.List;

public class AtencionController {

    @FXML private VBox screenRoot;
    @FXML private VBox panelHeader;
    @FXML private Label lblTicketInfo;
    @FXML private Label lblUsuario;
    @FXML private Label lblTipo;
    @FXML private Label lblMotivoOriginal;
    @FXML private VBox contPreguntas;
    @FXML private ProgressBar barraSimulacion;
    @FXML private Label lblSimulacion;
    @FXML private Button btnIniciar;
    @FXML private Button btnFinalizar;
    @FXML private FontIcon iconTema;

    private Ticket ticket;
    private final LinkedHashMap<String, TextField> camposPreguntas = new LinkedHashMap<>();

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

        ticket = SesionPC5.getTicketActual();
        if (ticket == null) { PC5Application.cargarVista("pc5_estacion.fxml"); return; }

        lblTicketInfo.setText("Ticket: " + ticket.getTicketId());
        lblUsuario   .setText("Usuario: " + ticket.getNombreApellido() + "  (DPI " + ticket.getDpi() + ")");
        lblTipo      .setText(ticket.getTipo());

        boolean traeMotivo = ticket.getMotivo() != null
                && !ticket.getMotivo().isBlank()
                && !ticket.getMotivo().equals(ticket.getTipo());

        if (traeMotivo) lblMotivoOriginal.setText("Motivo: " + ticket.getMotivo());

        List<String> preguntas = BancoPreguntas.getRespuestas(ticket.getTipo(), traeMotivo);
        for (String p : preguntas) {
            Label l = new Label(p);
            l.getStyleClass().add("pregunta-label");
            l.setWrapText(true);

            TextField tf = new TextField();
            tf.setPromptText("Respuesta...");
            tf.getStyleClass().add("text-field");

            VBox bloque = new VBox(4.0, l, tf);
            contPreguntas.getChildren().add(bloque);
            camposPreguntas.put(p, tf);
        }
    }

    @FXML
    private void iniciarSimulacion() {
        for (var e : camposPreguntas.entrySet()) {
            if (e.getValue().getText() == null || e.getValue().getText().isBlank()) {
                mostrarAlerta(Alert.AlertType.WARNING, "Campos vacios",
                        "Debe responder todas las preguntas antes de iniciar la atencion.");
                return;
            }
        }

        btnIniciar.setDisable(true);
        camposPreguntas.values().forEach(tf -> tf.setEditable(false));
        barraSimulacion.setVisible(true);
        barraSimulacion.setProgress(0);

        final double duracionSeg = 5.0;
        final int    pasos       = 50;
        final double tickSeg     = duracionSeg / pasos;

        String[] mensajes = {
                "Diagnosticando...",
                "Consultando base de conocimiento...",
                "Aplicando solucion...",
                "Verificando resultado...",
                "Documentando atencion..."
        };

        Timeline timeline = new Timeline();
        for (int i = 1; i <= pasos; i++) {
            final int paso = i;
            timeline.getKeyFrames().add(new KeyFrame(Duration.seconds(tickSeg * i), ev -> {
                double progreso = paso / (double) pasos;
                barraSimulacion.setProgress(progreso);
                int idxMsg = Math.min((int)(progreso * mensajes.length), mensajes.length - 1);
                lblSimulacion.setText(mensajes[idxMsg]);
            }));
        }
        timeline.setOnFinished(ev -> {
            lblSimulacion.setText("Atencion completada. Revise y finalice.");
            btnFinalizar.setDisable(false);
        });
        timeline.play();
    }

    @FXML
    private void finalizar() {
        btnFinalizar.setDisable(true);

        LinkedHashMap<String, String> respuestas = new LinkedHashMap<>();
        for (var e : camposPreguntas.entrySet()) {
            respuestas.put(e.getKey(), e.getValue().getText().trim());
        }
        ticket.setRespuestas(respuestas);
        ticket.marcarFinalAtencion(SesionPC5.getTecnico());

        Thread th = new Thread(() -> {
            try {
                enviarFinalizacion(ticket);
                Platform.runLater(() -> {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Atencion finalizada",
                            "Ticket " + ticket.getTicketId() + " cerrado y enviado al servidor.");
                    SesionPC5.setTicketActual(null);
                    PC5Application.cargarVista("pc5_estacion.fxml");
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    btnFinalizar.setDisable(false);
                    mostrarAlerta(Alert.AlertType.ERROR, "Error",
                            "No se pudo enviar la finalizacion:\n"
                                    + (ex.getMessage() != null ? ex.getMessage() : "desconocido"));
                });
            }
        }, "pc5-finalizar");
        th.setDaemon(true);
        th.start();
    }

    @FXML
    private void toggleTema() {
        TemaManager.toggle();
        TemaManager.aplicar(screenRoot);
        actualizarIconTema();
    }

    private void actualizarIconTema() {
        iconTema.setIconLiteral(TemaManager.esModoClaro() ? "fas-moon" : "fas-sun");
    }

    private void enviarFinalizacion(Ticket t) throws Exception {
        Cliente cli = SesionPC5.getConexion();
        if (cli == null) throw new Exception("La conexion con el servidor se ha perdido.");
        cli.enviarFinalizacion(t);
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
