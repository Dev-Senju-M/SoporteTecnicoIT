package sistema.sistemadesoportetecnicoit.pc2;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
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
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import sistema.sistemadesoportetecnicoit.PC2Application;
import sistema.sistemadesoportetecnicoit.shared.models.Ticket;
import sistema.sistemadesoportetecnicoit.shared.utils.TemaManager;

import java.util.List;

public class HistorialController {

    @FXML private VBox screenRoot;
    @FXML private VBox panelHeader;
    @FXML private TextField txtDpiBuscar;
    @FXML private TableView<Ticket> tablaTickets;
    @FXML private TableColumn<Ticket, String> colTicketId;
    @FXML private TableColumn<Ticket, String> colDpi;
    @FXML private TableColumn<Ticket, String> colNombre;
    @FXML private TableColumn<Ticket, String> colMotivo;
    @FXML private TableColumn<Ticket, String> colTipo;
    @FXML private TableColumn<Ticket, String> colTiempoTotal;
    @FXML private Label lblEstado;
    @FXML private FontIcon iconTema;

    private final ObservableList<Ticket> datos = FXCollections.observableArrayList();

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

        colTicketId   .setCellValueFactory(new PropertyValueFactory<>("ticketId"));
        colDpi        .setCellValueFactory(new PropertyValueFactory<>("dpi"));
        colNombre     .setCellValueFactory(new PropertyValueFactory<>("nombreApellido"));
        colMotivo     .setCellValueFactory(new PropertyValueFactory<>("motivo"));
        colTipo       .setCellValueFactory(new PropertyValueFactory<>("tipo"));
        colTiempoTotal.setCellValueFactory(cd -> {
            Ticket t = cd.getValue();
            if (t == null) return new SimpleStringProperty("");
            double min = t.getDuracionAtencionMinutos();
            if (min < 0 || min > 10000) return new SimpleStringProperty("0.00 min");
            return new SimpleStringProperty(String.format("%.2f min", min));
        });

        tablaTickets.setColumnResizePolicy(TableView.CONSTRAINED_RESIZE_POLICY);
        tablaTickets.setItems(datos);
        tablaTickets.setPlaceholder(new Label("Sin resultados. Ingrese un DPI y presione Buscar."));
    }

    @FXML
    private void buscar() {
        String dpi = txtDpiBuscar.getText() == null ? "" : txtDpiBuscar.getText().trim();
        if (dpi.isEmpty()) { mostrarAlerta(Alert.AlertType.WARNING, "DPI vacio", "Ingrese un DPI."); return; }
        if (!dpi.matches("\\d{13}")) { mostrarAlerta(Alert.AlertType.WARNING, "DPI invalido", "El DPI debe tener 13 digitos."); return; }

        lblEstado.setText("Consultando al servidor...");
        datos.clear();

        Thread t = new Thread(() -> {
            try {
                List<Ticket> resultado = consultarServidor(dpi);
                Platform.runLater(() -> {
                    if (resultado == null || resultado.isEmpty()) {
                        lblEstado.setText("Sin historial para DPI: " + dpi);
                        Alert alerta = new Alert(Alert.AlertType.WARNING);
                        alerta.setTitle("Consulta de Historial");
                        alerta.setHeaderText(null);
                        alerta.setContentText("No se encontró ningún registro asociado al DPI: " + dpi);
                        alerta.showAndWait();
                    } else {
                        datos.setAll(resultado);
                        lblEstado.setText("Se encontraron " + resultado.size() + " ticket(s).");
                        Alert alerta = new Alert(Alert.AlertType.INFORMATION);
                        alerta.setTitle("Consulta Exitosa");
                        alerta.setHeaderText(null);
                        alerta.setContentText("Se han cargado correctamente " + resultado.size() + " registros.");
                        alerta.showAndWait();
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
        Cliente cli = SesionPC2.getConexion();
        if (cli == null) throw new Exception("No hay conexion activa con el servidor central.");
        return cli.buscarPorDpi(dpi);
    }

    @FXML private void limpiar() { txtDpiBuscar.clear(); datos.clear(); lblEstado.setText(" "); }
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
