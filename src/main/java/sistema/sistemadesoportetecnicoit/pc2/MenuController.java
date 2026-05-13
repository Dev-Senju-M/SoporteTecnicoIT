package sistema.sistemadesoportetecnicoit.pc2;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import sistema.sistemadesoportetecnicoit.PC2Application;
import sistema.sistemadesoportetecnicoit.shared.chat.ChatController;
import sistema.sistemadesoportetecnicoit.shared.config.Configuracion;
import sistema.sistemadesoportetecnicoit.shared.utils.TemaManager;

import java.io.IOException;

public class MenuController {

    @FXML private VBox screenRoot;
    @FXML private VBox panelHeader;
    @FXML private Label lblServidor;
    @FXML private FontIcon iconTema;

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

        lblServidor.setText("Servidor: " + Configuracion.HOST + ":" + Configuracion.PUERTO_PC1);
        if (SesionPC2.getConexion() == null){
            new Thread(() -> {
                try{
                    Cliente cli = new Cliente();
                    cli.startClient();
                    SesionPC2.setConexion(cli);

                    sistema.sistemadesoportetecnicoit.shared.protocolo.Mensaje saludo =
                            new sistema.sistemadesoportetecnicoit.shared.protocolo.Mensaje(
                                    sistema.sistemadesoportetecnicoit.shared.protocolo.TipoMensaje.CHAT_MENSAJE,
                                    "Sistema PC2 Iniciado",
                                    "PC2"
                            );
                    cli.enviar(saludo);

                    Platform.runLater(() ->
                            lblServidor.setText("Servidor: Conectado a " + Configuracion.HOST));

                } catch(IOException e){
                    Platform.runLater(() ->
                            lblServidor.setText("Servidor: DESCONECTADO (Error)"));
                    System.err.println("PC2 no pudo conectar al iniciar: " + e.getMessage());
                }
            }).start();
        }
    }

    @FXML private void abrirRegistro()  { PC2Application.cargarVista("pc2_registro.fxml"); }
    @FXML private void abrirHistorial() { PC2Application.cargarVista("pc2_historial.fxml"); }
    @FXML private void abrirChat()      { ChatController.abrir("PC2", SesionPC2.getConexion()); }

    @FXML
    private void salir() {
        if (SesionPC2.getConexion() != null) SesionPC2.getConexion().cerrar();
        Platform.exit();
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
}
