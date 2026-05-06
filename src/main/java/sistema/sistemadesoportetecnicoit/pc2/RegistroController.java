package sistema.sistemadesoportetecnicoit.pc2;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import sistema.sistemadesoportetecnicoit.shared.models.Ticket;

import java.io.ObjectOutputStream;
import java.net.Socket;

public class RegistroController {

    @FXML private TextField txtDpi;
    @FXML private ComboBox<String> cmbTipo;
    @FXML private TextField txtMotivo;
    @FXML private Label lblMotivo;
    @FXML private Label lblDestino;

    private static final  String HOST = "localhost";
    private static final  String PORT = "5000";


     @FXML
    public void initialize(){
         cmbTipo.setItems(FXCollections.observableArrayList(
                 "Hardware", "Software", "Red/Internet",
                 "Servidor", "Accesos/Permisos", "Otro"));
         cmbTipo.getSelectionModel().selectFirst();
         lblMotivo.setText(Clasificador.getDestino("Hardware"));

         txtMotivo.setVisible(false);
         lblMotivo.setVisible(false);
     }

     @FXML
    private void actualizarDestino(){
         String tipo = cmbTipo.getValue();
         if(tipo == null) return;

         lblDestino.setText(Clasificador.getDestino(tipo));

         boolean esPrioritario = tipo.equals("Servidor") || tipo.equals("Accesos/Permisos");
         txtMotivo.setVisible(esPrioritario);
         lblMotivo.setVisible(esPrioritario);

         if(!esPrioritario) txtMotivo.clear();
     }

     @FXML
    private void enviarTicket(){
         String dpi = txtDpi.getText().trim();
         String tipo = cmbTipo.getValue();

         String motivo = txtMotivo.isVisible() ? txtMotivo.getText().trim() : tipo;

         if(dpi.isEmpty()){
             mostrarAlerta(Alert.AlertType.WARNING, "Campos vacíos", "Por favor ingrese su DPI.");
             return;
         }

         if (txtMotivo.isVisible() && motivo.isEmpty()) {
             mostrarAlerta(Alert.AlertType.WARNING, "Campos vacíos", "Por favor describa el motivo del problema.");
             return;
         }

         Ticket ticket = new Ticket(dpi,dpi,motivo,tipo);

         try {
             Socket socket = new Socket(HOST,PUERTO);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             out.writeObject(ticket);
             out.flush();
             socket.close();
             mostrarAlerta(Alert.AlertType.INFORMATION, "Éxito", "Ticket enviado correctamente.\nSerá atendido en: " + Clasificador.getDestino(tipo));
         } catch (Exception e) {
             mostrarAlerta(Alert.AlertType.ERROR, "Error", "No se pudo conectar con el servidor:\n" + e.getMessage());
         }
    }

    private void limpiarCampos(){
        txtDpi.clear();
        txtMotivo.clear();
        lblMotivo.setVisible(false);
        lblDestino.setVisible(false);
        cmbTipo.getSelectionModel().selectFirst();
        lblMotivo.setText(Clasificador.getDestino("Hardware");
    }

    private void mostrarAlerta(Alert.AlertType tipo, String titulo, String mensaje){
         Alert alert = new Alert(tipo);
         alert.setTitle(titulo);
         alert.setHeaderText(null);
         alert.setContentText(mensaje);
         alert.showAndWait();
    }
}
