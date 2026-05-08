package sistema.sistemadesoportetecnicoit.pc2;

import sistema.sistemadesoportetecnicoit.shared.config.Configuracion;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class Conexion {

    protected final String HOST  = Configuracion.HOST;
    protected final int    PUERTO = Configuracion.PUERTO_PC1;

    protected Socket             cs;
    protected ObjectOutputStream salidaServidor;

    public Conexion() throws IOException {
        cs = new Socket(HOST, PUERTO);
        cs.setSoTimeout(Configuracion.TIMEOUT);
    }

    public void cerrar() {
        try {
            if (salidaServidor != null) salidaServidor.close();
            if (cs != null && !cs.isClosed()) cs.close();
        } catch (IOException e) {
            System.out.println("Error al cerrar conexion: " + e.getMessage());
        }
    }
}