package sistema.sistemadesoportetecnicoit.pc2;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import sistema.sistemadesoportetecnicoit.shared.models.Ticket;
import sistema.sistemadesoportetecnicoit.shared.structures.HashMapUsuarios;

import java.util.LinkedList;

public class HistorialController {

    @FXML private TextField txtDpi;
    @FXML private TableView<Ticket> tablaHistorial;
    @FXML private TableColumn<Ticket, String> colNum;
    @FXML private TableColumn<Ticket, String> colTipo;
    @FXML private TableColumn<Ticket, String> colMotivo;
    @FXML private TableColumn<Ticket, String> colDuracion;
    @FXML private TableColumn<Ticket, String> colTecnico;
    @FXML private TableColumn<Ticket, String> colFecha;
    @FXML private Label lblTotal;

    private static HashMapUsuarios historial = new HashMapUsuarios();

    public void initialize() {
        colNum.setCellValueFactory(data -> {
            int index = tablaHistorial.getItems().indexOf(data.getValue()) + 1;
            return new SimpleStringProperty(String.valueOf(index));
        });
        colTipo.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getTipo()));
        colMotivo.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getMotivo()));
        colDuracion.setCellValueFactory(data ->
            new SimpleStringProperty(
                String.format("%.2f", data.getValue().getDuracionAtencionMinutos())));
        colTecnico.setCellValueFactory(data ->
            new SimpleStringProperty(
                data.getValue().getUsuarioAtendio() != null
                ? data.getValue().getUsuarioAtendio() : "Pendiente"));
        colFecha.setCellValueFactory(data ->
            new SimpleStringProperty(data.getValue().getFechaHoraAtencion()));
    }

    @FXML
    private void buscarHistorial() {
        String dpi = txtDpi.getText().trim();

        if (dpi.isEmpty()) {
            mostrarAlerta("Ingrese un DPI para buscar.");
            return;
        }

        LinkedList<Ticket> tickets = historial.get(dpi);

        if (tickets == null || tickets.isEmpty()) {
            tablaHistorial.setItems(FXCollections.observableArrayList());
            lblTotal.setText("Total: 0 tickets");
            mostrarAlerta("No se encontró historial para el DPI: " + dpi);
            return;
        }

        ObservableList<Ticket> lista = FXCollections.observableArrayList(tickets);
        tablaHistorial.setItems(lista);
        lblTotal.setText("Total: " + tickets.size() + " tickets");
    }

    public static void agregarAlHistorial(String dpi, Ticket ticket) {
        historial.agregarTicket(dpi, ticket);
    }

    private void mostrarAlerta(String mensaje) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Aviso");
        alert.setHeaderText(null);
        alert.setContentText(mensaje);
        alert.showAndWait();
    }
}