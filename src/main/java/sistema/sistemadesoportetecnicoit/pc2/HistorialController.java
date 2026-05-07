package sistema.sistemadesoportetecnicoit.pc2;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import sistema.sistemadesoportetecnicoit.PC2Application;
import sistema.sistemadesoportetecnicoit.shared.config.Configuracion;
import sistema.sistemadesoportetecnicoit.shared.models.Ticket;
import sistema.sistemadesoportetecnicoit.shared.protocolo.Mensaje;
import sistema.sistemadesoportetecnicoit.shared.protocolo.TipoMensaje;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.List;

public class HistorialController {

    @FXML private TextField txtDpiBuscar;
    @FXML private TableView<Ticket> tablaTickets;
    @FXML private TableColumn<Ticket, String> colTicketId;
    @FXML private TableColumn<Ticket, String> colTipo;
    @FXML private TableColumn<Ticket, String> colMotivo;
    @FXML private TableColumn<Ticket, String> colTecnico;
    @FXML private TableColumn<Ticket, String> colFecha;
    @FXML private Label lblEstado;

    private final ObservableList<Ticket> datos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colTicketId.setCellValueFactory(new PropertyValueFactory<>("ticketId"));
        colTipo    .setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colMotivo  .setCellValueFactory(new PropertyValueFactory<>("motivo"));
        colTecnico .setCellValueFactory(new PropertyValueFactory<>("usuarioAtendio"));
        colFecha.setCellValueFactory(cd -> new SimpleStringProperty(
                cd.getValue() == null ? "" : cd.getValue().getFechaHoraAtencion()));

        tablaTickets.setItems(datos);
        tablaTickets.setPlaceholder(new Label("Sin resultados. Ingrese un DPI y presione Buscar."));
    }

    @FXML
    private void buscar() {
        String dpi = txtDpiBuscar.getText() == null ? "" : txtDpiBuscar.getText().trim();
        if (dpi.isEmpty()) {
            mostrarAlerta(Alert.AlertType.WARNING, "DPI vacio", "Ingrese un DPI.");
            return;
        }
        if (!dpi.matches("\\d{13}")) {
            mostrarAlerta(Alert.AlertType.WARNING, "DPI invalido", "El DPI debe tener 13 digitos.");
            return;
        }

        lblEstado.setText("Consultando al servidor...");
        datos.clear();

        Task<List<Ticket>> tarea = new Task<>() {
            @Override
            protected List<Ticket> call() throws Exception {
                return consultarServidor(dpi);
            }
        };

        tarea.setOnSucceeded(e -> {
            List<Ticket> resultado = tarea.getValue();
            if (resultado == null || resultado.isEmpty()) {
                lblEstado.setText("Sin historial para DPI: " + dpi);
            } else {
                datos.setAll(resultado);
                lblEstado.setText("Se encontraron " + resultado.size() + " ticket(s) para DPI " + dpi + ".");
            }
        });

        tarea.setOnFailed(e -> {
            Throwable ex = tarea.getException();
            lblEstado.setText("Error en la consulta.");
            mostrarAlerta(Alert.AlertType.ERROR, "Error",
                    "No se pudo consultar al servidor:\n" + (ex != null ? ex.getMessage() : "desconocido"));
        });

        Thread t = new Thread(tarea, "historial-busqueda");
        t.setDaemon(true);
        t.start();
    }

    @SuppressWarnings("unchecked")
    private List<Ticket> consultarServidor(String dpi) throws Exception {
        String host = Configuracion.getHost();
        int    port = Configuracion.getPort();

        try (Socket socket = new Socket(host, port);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream  in  = new ObjectInputStream(socket.getInputStream())) {

            out.writeObject(new Mensaje(TipoMensaje.BUSCAR_DPI, dpi, "PC2"));
            out.flush();

            Object respuesta = in.readObject();
            if (!(respuesta instanceof Mensaje msg)) {
                throw new IllegalStateException("Respuesta inesperada: " + respuesta);
            }
            if (msg.getTipo() == TipoMensaje.ERROR) {
                throw new IllegalStateException("Servidor respondio ERROR: " + msg.getPayload());
            }
            if (msg.getTipo() != TipoMensaje.RESPUESTA_DPI) {
                throw new IllegalStateException("Tipo inesperado: " + msg.getTipo());
            }

            Object payload = msg.getPayload();
            if (payload == null) return List.of();
            if (payload instanceof List<?> lista) return (List<Ticket>) lista;
            throw new IllegalStateException("Payload no es lista: " + payload.getClass());
        }
    }

    @FXML
    private void limpiar() {
        txtDpiBuscar.clear();
        datos.clear();
        lblEstado.setText(" ");
    }

    @FXML
    private void regresarMenu() {
        PC2Application.cargarVista("pc2_menu.fxml");
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje) {
        Runnable r = () -> {
            Alert alert = new Alert(tipo);
            alert.setTitle(titulo);
            alert.setHeaderText(null);
            alert.setContentText(mensaje);
            alert.showAndWait();
        };
        if (Platform.isFxApplicationThread()) r.run();
        else Platform.runLater(r);
    }
}