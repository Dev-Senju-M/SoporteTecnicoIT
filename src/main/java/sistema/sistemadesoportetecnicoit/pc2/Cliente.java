package sistema.sistemadesoportetecnicoit.pc2;

import sistema.sistemadesoportetecnicoit.shared.models.Ticket;
import sistema.sistemadesoportetecnicoit.shared.protocolo.Mensaje;
import sistema.sistemadesoportetecnicoit.shared.protocolo.TipoMensaje;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import static sistema.sistemadesoportetecnicoit.shared.config.Configuracion.HOST;
import static sistema.sistemadesoportetecnicoit.shared.config.Configuracion.PUERTO_PC1;

public class Cliente {

    public static void enviarTicket(Ticket ticket) throws IOException {
        try (Socket socket = new Socket(HOST, PUERTO_PC1);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream())) {

            Mensaje msg = new Mensaje(TipoMensaje.REGISTRAR_TICKET, ticket, "PC2");
            out.writeObject(msg);
            out.flush();
            System.out.println("✅ Ticket enviado: " + ticket);
        }
    }

    public static Object buscarPorDpi(String dpi) throws IOException, ClassNotFoundException {
        try (Socket socket = new Socket(HOST, PUERTO_PC1);
             ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
             ObjectInputStream  in  = new ObjectInputStream(socket.getInputStream())) {

            Mensaje msg = new Mensaje(TipoMensaje.BUSCAR_DPI, dpi, "PC2");
            out.writeObject(msg);
            out.flush();

            Mensaje respuesta = (Mensaje) in.readObject();
            if (respuesta.getTipo() == TipoMensaje.RESPUESTA_DPI) {
                return respuesta.getPayload();
            }
            return null;
        }
    }
}