package sistema.sistemadesoportetecnicoit.shared.chat;

import javafx.application.Platform;
import javafx.collections.ListChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import sistema.sistemadesoportetecnicoit.shared.conexion.Conexion;
import sistema.sistemadesoportetecnicoit.shared.protocolo.Mensaje;
import sistema.sistemadesoportetecnicoit.shared.protocolo.TipoMensaje;

import java.io.IOException;

public class ChatController {

    @FXML private TextArea areaChat;
    @FXML private TextField txtMensaje;
    @FXML private Button btnEnviar;

    private String origen;
    private Conexion conexion;

    private static Stage ventanaActual = null;

    public void init(String origen, Conexion conexion) {
        this.origen = origen;
        this.conexion = conexion;

        StringBuilder sb = new StringBuilder();
        for (String m : ChatHistorial.getMensajes()) {
            sb.append(m).append("\n");
        }
        areaChat.setText(sb.toString());
        areaChat.setScrollTop(Double.MAX_VALUE);

        ChatHistorial.getMensajes().addListener((ListChangeListener<String>) change -> {
            while (change.next()) {
                if (change.wasAdded()) {
                    for (String msg : change.getAddedSubList()) {
                        Platform.runLater(() -> {
                            areaChat.appendText(msg + "\n");
                            areaChat.setScrollTop(Double.MAX_VALUE);
                        });
                    }
                }
            }
        });

        txtMensaje.setOnAction(e -> enviar());
    }

    @FXML
    private void enviar() {
        String texto = txtMensaje.getText() == null ? "" : txtMensaje.getText().trim();
        if (texto.isEmpty()) return;

        txtMensaje.clear();

        if (conexion != null) {
            Thread th = new Thread(() -> {
                try {
                    conexion.enviar(new Mensaje(TipoMensaje.CHAT_MENSAJE, texto, origen));
                } catch (IOException e) {
                    System.err.println("[Chat] Error al enviar: " + e.getMessage());
                }
            }, "chat-enviar");
            th.setDaemon(true);
            th.start();
        }
    }

    public static void abrir(String origen, Conexion conexion) {
        if (ventanaActual != null && ventanaActual.isShowing()) {
            ventanaActual.toFront();
            return;
        }
        try {
            FXMLLoader loader = new FXMLLoader(
                    ChatController.class.getResource(
                            "/sistema/sistemadesoportetecnicoit/shared/chat/pc_chat.fxml"));
            Parent root = loader.load();
            ChatController ctrl = loader.getController();
            ctrl.init(origen, conexion);

            Stage stage = new Stage();
            stage.setTitle("Chat — " + origen);
            stage.setScene(new Scene(root));
            stage.setResizable(false);
            stage.show();
            ventanaActual = stage;
        } catch (IOException e) {
            System.err.println("[Chat] Error al abrir ventana: " + e.getMessage());
        }
    }
}
