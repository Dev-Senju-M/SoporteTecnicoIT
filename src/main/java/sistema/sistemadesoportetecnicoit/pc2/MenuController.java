package sistema.sistemadesoportetecnicoit.pc2;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import sistema.sistemadesoportetecnicoit.PC2Application;
import sistema.sistemadesoportetecnicoit.shared.chat.ChatController;
import sistema.sistemadesoportetecnicoit.shared.config.Configuracion;

import java.io.IOException;

public class MenuController {

    @FXML private Label lblServidor;

    @FXML
    public void initialize() {
        lblServidor.setText("Servidor: " + Configuracion.HOST + ":" + Configuracion.PUERTO_PC1);
        if (SesionPC2.getConexion() == null){
            new Thread(() -> {
                try{
                    Cliente cli = new Cliente();
                    cli.startClient();
                    SesionPC2.setConexion(cli);

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

    @FXML
    private void abrirRegistro() {
        PC2Application.cargarVista("pc2_registro.fxml");
    }

    @FXML
    private void abrirHistorial() {
        PC2Application.cargarVista("pc2_historial.fxml");
    }

    @FXML
    private void abrirChat() {
        ChatController.abrir("PC2", SesionPC2.getConexion());
    }

    @FXML
    private void salir() {
        if (SesionPC2.getConexion()!=null){
            SesionPC2.getConexion().cerrar();
        }
        Platform.exit();
    }

    }
