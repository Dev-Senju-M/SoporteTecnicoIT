package sistema.sistemadesoportetecnicoit.pc2;

import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import sistema.sistemadesoportetecnicoit.PC2Application;
import sistema.sistemadesoportetecnicoit.shared.models.Ticket;

import java.util.List;

public class HistorialController {

    @FXML private TextField txtDpiBuscar;
    @FXML private TableView<Ticket> tablaTickets;
    @FXML private TableColumn<Ticket, String> colTicketId;
    @FXML private TableColumn<Ticket, String> colDpi;
    @FXML private TableColumn<Ticket, String> colNombre;
    @FXML private TableColumn<Ticket, String> colMotivo;
    @FXML private TableColumn<Ticket, String> colTipo;
    @FXML private TableColumn<Ticket, String> colTiempoTotal;
    @FXML private Label lblEstado;

    private final ObservableList<Ticket> datos = FXCollections.observableArrayList();

    @FXML
    public void initialize() {
        colTicketId   .setCellValueFactory(new PropertyValueFactory<>("ticketId"));
        colDpi        .setCellValueFactory(new PropertyValueFactory<>("dpi"));
        colNombre     .setCellValueFactory(new PropertyValueFactory<>("nombreApellido"));
        colMotivo     .setCellValueFactory(new PropertyValueFactory<>("motivo"));
        colTipo       .setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colTiempoTotal.setCellValueFactory(cd -> {
            Ticket t = cd.getValue();
            if (t == null) return new SimpleStringProperty("");
            long min = t.getTiempoFinal();
            return new SimpleStringProperty(min >= 0 ? min + " min" : "N/A");
        });

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

        Thread t = new Thread(() -> {
            try {
                List<Ticket> resultado = consultarServidor(dpi);
                Platform.runLater(() -> {
                    if (resultado == null || resultado.isEmpty()) {
                        lblEstado.setText("Sin historial para DPI: " + dpi);
                    } else {
                        datos.setAll(resultado);
                        lblEstado.setText("Se encontraron " + resultado.size() + " ticket(s) para DPI " + dpi + ".");
                    }
                });
            } catch (Exception ex) {
                Platform.runLater(() -> {
                    lblEstado.setText("Error en la consulta.");
                    mostrarAlerta(Alert.AlertType.ERROR, "Error",
                            "No se pudo consultar al servidor:\n"
                                    + (ex.getMessage() != null ? ex.getMessage() : "desconocido"));
                });
            }
        }, "historial-busqueda");
        t.setDaemon(true);
        t.start();
    }

    private List<Ticket> consultarServidor(String dpi) throws Exception {
        Cliente cli = null;
        try {
            cli = new Cliente();
            cli.startClient();
            return cli.buscarPorDpi(dpi);
        } finally {
            if (cli != null) cli.cerrar();
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
