package sistema.sistemadesoportetecnicoit.shared.conexion;

import sistema.sistemadesoportetecnicoit.shared.config.Configuracion;
import sistema.sistemadesoportetecnicoit.shared.protocolo.Mensaje;

import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Conexion {

    protected final int PUERTO = Configuracion.PUERTO_PC1;
    protected final String HOST = Configuracion.HOST;

    protected ServerSocket ss;
    protected Socket cs;

    protected ObjectOutputStream salida;
    protected ObjectInputStream entrada;

    public Conexion() throws IOException{
        cs = new Socket(HOST, PUERTO);
        cs.setSoTimeout(Configuracion.TIMEOUT);

        salida = new ObjectOutputStream(cs.getOutputStream());
        entrada = new ObjectInputStream(cs.getInputStream());
    }

    public void enviar(Mensaje msg) throws IOException{
        if (salida != null){
            salida.writeObject(msg);
            salida.flush();
        }
    }

    public Mensaje recibir() throws IOException, ClassNotFoundException{
        if (entrada != null){
            return (Mensaje) entrada.readObject();
        }
        return null;
    }

    public void cerrar(){
        try{
            if (salida != null) salida.close();
            if (entrada != null) entrada.close();
            if (cs != null && !cs.isClosed()) cs.close();
            if (ss != null && !ss.isClosed()) ss.close();
        }catch(IOException e){
            System.err.println("Error al cerrar: " + e.getMessage());
        }
    }
}
