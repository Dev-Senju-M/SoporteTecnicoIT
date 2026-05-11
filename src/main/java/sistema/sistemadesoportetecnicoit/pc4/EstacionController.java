package sistema.sistemadesoportetecnicoit.pc4;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressIndicator;
import sistema.sistemadesoportetecnicoit.PC4Application;
import sistema.sistemadesoportetecnicoit.shared.models.Ticket;

public class EstacionController {

    @FXML private Label lblTecnico;
    @FXML private Label lblEstado;
    @FXML private Label lblInfo;
    @FXML private Button btnAtender;
    @FXML private ProgressIndicator spinner;

    @FXML
    public void initialize() {
        lblTecnico.setText("Tecnico: " + SesionPC4.getTecnico());
    }

    @FXML
    private void atenderSiguiente() {
        btnAtender.setDisable(true);
        spinner.setVisible(true);
        lblEstado.setText("Solicitando ticket prioritario al servidor...");

        Thread th = new Thread(() -> {
            try {
                Ticket t = solicitarTicket();
                Platform.runLater(() -> {
                    spinner.setVisible(false);
                    if (t == null) {
                        lblEstado.setText("No hay tickets prioritarios en cola. Reintente.");
                        btnAtender.setDisable(false);
                    } else {
                        t.marcarInicioAtencion();
                        SesionPC4.setTicketActual(t);
                        PC4Application.cargarVista("pc4_atencion.fxml");
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
        }, "pc4-solicitar");
        th.setDaemon(true);
        th.start();
    }

    private Ticket solicitarTicket() throws Exception {
        Cliente cli = null;
        try {
            cli = new Cliente();
            cli.startClient();
            return cli.solicitarTicket("PRIORITARIO");
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
