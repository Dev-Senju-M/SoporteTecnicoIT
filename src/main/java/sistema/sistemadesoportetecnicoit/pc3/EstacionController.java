package sistema.sistemadesoportetecnicoit.pc3;

import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import sistema.sistemadesoportetecnicoit.PC3Application;
import sistema.sistemadesoportetecnicoit.shared.config.Configuracion;
import sistema.sistemadesoportetecnicoit.shared.models.Ticket;
import sistema.sistemadesoportetecnicoit.shared.protocolo.Mensaje;
import sistema.sistemadesoportetecnicoit.shared.protocolo.TipoMensaje;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class EstacionController {

    @FXML private Label lblTecnico;
    @FXML private Label lblEstado;
    @FXML private Label lblInfo;
    @FXML private Button btnAtender;
    @FXML private ProgressIndicator spinner;

    @FXML
    public void initialize() {
        lblTecnico.setText("Tecnico: " + SesionPC3.getTecnico());
    }

    @FXML
    private void atenderSiguiente() {
        btnAtender.setDisable(true);
        spinner.setVisible(true);
        lblEstado.setText("Solicitando ticket al servidor...");

        Task<Ticket> tarea = new Task<>() {
            @Override
            protected Ticket call() throws Exception {
                return solicitarTicket();
            }
        };

        tarea.setOnSucceeded(e -> {
            spinner.setVisible(false);
            Ticket t = tarea.getValue();
            if (t == null) {
                lblEstado.setText("No hay tickets en cola. Reintente.");
                btnAtender.setDisable(false);
            } else {
                t.marcarInicioAtencion();
                SesionPC3.setTicketActual(t);
                PC3Application.cargarVista("pc3_atencion.fxml");
            }
        });

        tarea.setOnFailed(e -> {
            spinner.setVisible(false);
            btnAtender.setDisable(false);
            lblEstado.setText("Error al solicitar ticket.");
            Throwable ex = tarea.getException();
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo conectar con el servidor:\n"
                            + (ex != null ? ex.getMessage() : "desconocido"));
        });

        Thread th = new Thread(tarea, "pc3-solicitar");
        th.setDaemon(true);
        th.start();
    }

    private Ticket solicitarTicket() throws Exception {
        String host = Configuracion.HOST;
        int    port = Configuracion.PUERTO_PC1;

        try (Socket socket = new Socket(host, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream  in  = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject(new Mensaje(TipoMensaje.SOLICITAR_TICKET, "GENERAL", "PC3"));
            out.flush();

            Object resp = in.readObject();
            if (!(resp instanceof Mensaje msg)) {
                throw new IllegalStateException("Respuesta inesperada: " + resp);
            }
            if (msg.getTipo() == TipoMensaje.ENTREGAR_TICKET && msg.getPayload() instanceof Ticket t) {
                return t;
            }
            return null;
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