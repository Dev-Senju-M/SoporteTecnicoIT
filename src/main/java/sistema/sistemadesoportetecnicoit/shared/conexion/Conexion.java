package sistema.sistemadesoportetecnicoit.shared.conexion;

import javafx.application.Platform;
import javafx.scene.control.Alert;
import sistema.sistemadesoportetecnicoit.shared.chat.ChatHistorial;
import sistema.sistemadesoportetecnicoit.shared.config.Configuracion;
import sistema.sistemadesoportetecnicoit.shared.protocolo.Mensaje;
import sistema.sistemadesoportetecnicoit.shared.protocolo.TipoMensaje;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;

public class Conexion {

    protected final int PUERTO = Configuracion.PUERTO_PC1;
    protected final String HOST = Configuracion.HOST;

    protected ServerSocket ss;
    protected Socket cs;

    protected ObjectOutputStream salida;
    protected ObjectInputStream entrada;

    // Cola para mensajes de respuesta (request-response)
    private final BlockingQueue<Mensaje> cola = new LinkedBlockingQueue<>();

    public Conexion() throws IOException {
        cs = new Socket(HOST, PUERTO);
        cs.setSoTimeout(0);

        this.salida = new ObjectOutputStream(cs.getOutputStream());
        this.salida.flush();
        this.entrada = new ObjectInputStream(cs.getInputStream());

        iniciarEscucha();
    }

    private void iniciarEscucha() {
        Thread t = new Thread(() -> {
            while (cs != null && !cs.isClosed()) {
                try {
                    Mensaje m = (Mensaje) entrada.readObject();
                    if (m == null) break;

                    if (m.getTipo() == TipoMensaje.SERVIDOR_DETENIDO) {
                        Platform.runLater(() -> {
                            mostrarAlertaCritica("Servidor Desconectado",
                                    "El servidor central (PC1) ha finalizado la sesión.\n" +
                                            "La aplicación se cerrará por seguridad.");
                        });
                        break;
                    }

                    if (m.getTipo() == TipoMensaje.CHAT_MENSAJE) {
                        Platform.runLater(() ->
                                ChatHistorial.agregar(m.getOrigen(), (String) m.getPayload()));
                    } else {
                        cola.put(m);
                    }
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    break;
                }catch(Exception e){
                    Platform.runLater(() -> {
                        mostrarAlertaCritica("Error de Comunicación",
                                "Se ha perdido la conexión física con el servidor.");
                    });
                    break;
                }
            }
        }, "conexion-listener");
        t.setDaemon(true);
        t.start();
    }

    public void enviar(Mensaje msg) throws IOException {
        if (salida != null && cs != null && !cs.isClosed()) {
            salida.writeObject(msg);
            salida.flush();
            salida.reset();
        } else {
            throw new IOException("Socket cerrado o no esta disponible");
        }
    }

    public Mensaje recibir() throws IOException, ClassNotFoundException {
        try {
            return cola.take();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IOException("Espera de mensaje interrumpida", e);
        }
    }

    public void cerrar() {
        try {
            if (salida != null) salida.close();
            if (entrada != null) entrada.close();
            if (cs != null && !cs.isClosed()) cs.close();
            if (ss != null && !ss.isClosed()) ss.close();
        } catch (IOException e) {
            System.err.println("Error al cerrar: " + e.getMessage());
        }
    }
    private void mostrarAlertaCritica(String titulo, String mensaje){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(titulo);
        alert.setHeaderText(null);
        alert.setContentText(mensaje);

        alert.showAndWait();

        System.exit(0);
    }
}
