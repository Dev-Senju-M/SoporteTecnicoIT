package sistema.sistemadesoportetecnicoit.shared.conexion;

import sistema.sistemadesoportetecnicoit.shared.config.Configuracion;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.charset.StandardCharsets;

public class Conexion {

    protected final int    PUERTO = Configuracion.PUERTO_PC1;
    protected final String HOST   = Configuracion.HOST;

    protected String mensajeServidor;
    protected String mensajeCliente;

    protected ServerSocket ss;
    protected Socket       cs;


    protected DataOutputStream salidaServidor;
    protected DataOutputStream salidaCliente;

    protected BufferedReader entradaServidor;
    protected BufferedReader entradaCliente;

    public Conexion(String tipo) throws IOException {
        if (tipo.equalsIgnoreCase("servidor")) {
            ss = new ServerSocket(PUERTO);
            cs = new Socket();
        } else {
            cs = new Socket(HOST, PUERTO);
            cs.setSoTimeout(Configuracion.TIMEOUT);
        }
    }

    protected void enviarAlServidor(String linea) throws IOException {
        if (salidaServidor == null) throw new IOException("salidaServidor no inicializada");
        salidaServidor.write((linea + "\n").getBytes(StandardCharsets.UTF_8));
        salidaServidor.flush();
    }

    protected void enviarAlCliente(String linea) throws IOException {
        if (salidaCliente == null) throw new IOException("salidaCliente no inicializada");
        salidaCliente.write((linea + "\n").getBytes(StandardCharsets.UTF_8));
        salidaCliente.flush();
    }

    protected void abrirFlujosCliente() throws IOException {
        salidaServidor  = new DataOutputStream(cs.getOutputStream());
        entradaServidor = new BufferedReader(new InputStreamReader(cs.getInputStream(), StandardCharsets.UTF_8));
    }

    protected void abrirFlujosServidor() throws IOException {
        salidaCliente  = new DataOutputStream(cs.getOutputStream());
        entradaCliente = new BufferedReader(new InputStreamReader(cs.getInputStream(), StandardCharsets.UTF_8));
    }

    public void cerrar() {
        try {
            if (salidaServidor   != null) salidaServidor.close();
            if (salidaCliente    != null) salidaCliente.close();
            if (entradaServidor  != null) entradaServidor.close();
            if (entradaCliente   != null) entradaCliente.close();
            if (cs != null && !cs.isClosed()) cs.close();
            if (ss != null && !ss.isClosed()) ss.close();
        } catch (IOException e) {
            System.out.println("Error al cerrar conexion: " + e.getMessage());
        }
    }
}
