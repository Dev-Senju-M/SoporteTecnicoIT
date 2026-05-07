package sistema.sistemadesoportetecnicoit.shared.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public final class Configuracion {

    private static final String Ruta = "/config/cliente.properties";

    private static String host = "localhost";
    private static int port = 5000;
    private static boolean cargada = false;

    private Configuracion() {}

    private static void cargarSiHaceFalta(){
        if (cargada) return;
        try (InputStream in = Configuracion.class.getResourceAsStream(Ruta)) {
            if (in != null) {
                Properties prop = new Properties();
                prop.load(in);
                host = prop.getProperty("host", host);
                port = Integer.parseInt(prop.getProperty("port", String.valueOf(port)));
            } else {
                System.err.println("[Configuracion] No se encontro" + Ruta + "Usando Default.");
            }
        } catch ( IOException|NumberFormatException e) {
            System.err.println("[Configuracion] Error: " + e.getMessage());
        }
    }

    public static String getHost() {
        cargarSiHaceFalta(); return  host;
    }

    public static int getPort() {
        cargarSiHaceFalta(); return  port;
    }
}
