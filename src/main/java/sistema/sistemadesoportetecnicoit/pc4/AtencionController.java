package sistema.sistemadesoportetecnicoit.pc4;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import sistema.sistemadesoportetecnicoit.PC4Application;
import sistema.sistemadesoportetecnicoit.shared.models.Ticket;
import sistema.sistemadesoportetecnicoit.shared.utils.BancoPreguntas;

import java.util.LinkedHashMap;
import java.util.List;

public class AtencionController {

    @FXML private Label lblTicketInfo;
    @FXML private Label lblUsuario;
    @FXML private Label lblTipo;
    @FXML private Label lblMotivoOriginal;
    @FXML private VBox contPreguntas;
    @FXML private ProgressBar barraSimulacion;
    @FXML private Label lblSimulacion;
    @FXML private Button btnIniciar;
    @FXML private Button btnFinalizar;

    private Ticket ticket;
    private final LinkedHashMap<String, TextField> camposPreguntas = new LinkedHashMap<>();

    @FXML
    public void initialize() {
        ticket = SesionPC4.getTicketActual();
        if (ticket == null) {
            PC4Application.cargarVista("pc4_estacion.fxml");
            return;
        }

        lblTicketInfo.setText("Ticket: " + ticket.getTicketId());
        lblUsuario   .setText("Usuario: " + ticket.getNombreApellido() + "  (DPI " + ticket.getDpi() + ")");
        lblTipo      .setText("Tipo: " + ticket.getTipo());

        boolean traeMotivo = ticket.getMotivo() != null
                && !ticket.getMotivo().isBlank()
                && !ticket.getMotivo().equals(ticket.getTipo());

        if (traeMotivo) {
            lblMotivoOriginal.setText("Motivo reportado: " + ticket.getMotivo());
        }

        List<String> preguntas = BancoPreguntas.getRespuestas(ticket.getTipo(), traeMotivo);
        for (String p : preguntas) {
            Label l = new Label(p);
            l.setStyle("-fx-text-fill: #cccccc; -fx-font-size: 12px;");
            TextField tf = new TextField();
            tf.setPromptText("Respuesta...");
            tf.setStyle("-fx-background-color: #3c3c3c; -fx-text-fill: white;");
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
            KeyFrame kf = new KeyFrame(Duration.seconds(tickSeg * i), ev -> {
                double progreso = paso / (double) pasos;
                barraSimulacion.setProgress(progreso);
                int idxMsg = Math.min((int)(progreso * mensajes.length), mensajes.length - 1);
                lblSimulacion.setText(mensajes[idxMsg]);
            });
            timeline.getKeyFrames().add(kf);
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

        ticket.marcarFinalAtencion(SesionPC4.getTecnico());

        Thread th = new Thread(() -> {
            try {
                enviarFinalizacion(ticket);
                Platform.runLater(() -> {
                    mostrarAlerta(Alert.AlertType.INFORMATION, "Atencion finalizada",
                            "Ticket " + ticket.getTicketId() + " cerrado y enviado al servidor.");
                    SesionPC4.setTicketActual(null);
                    PC4Application.cargarVista("pc4_estacion.fxml");
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    btnFinalizar.setDisable(false);
                    mostrarAlerta(Alert.AlertType.ERROR, "Error",
                            "No se pudo enviar la finalizacion al servidor:\n"
                                    + (ex.getMessage() != null ? ex.getMessage() : "desconocido"));
                });
            }
        }, "pc4-finalizar");
        th.setDaemon(true);
        th.start();
    }

    private void enviarFinalizacion(Ticket t) throws Exception {
        Cliente cli = null;
        try {
            cli = new Cliente();
            cli.startClient();
            cli.enviarFinalizacion(t);
        } finally {
            if (cli != null) cli.cerrar();
        }
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Runnable r = () -> {
            Alert a = new Alert(tipo);
            a.setTitle(titulo);
            a.setHeaderText(null);
            a.setContentText(mensaje);
            a.showAndWait();
        };
        if (Platform.isFxApplicationThread()) r.run();
        else Platform.runLater(r);
    }
}
