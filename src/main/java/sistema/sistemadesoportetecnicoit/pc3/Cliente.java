package sistema.sistemadesoportetecnicoit.pc3;

import sistema.sistemadesoportetecnicoit.shared.conexion.Conexion;
import sistema.sistemadesoportetecnicoit.shared.models.Ticket;
import sistema.sistemadesoportetecnicoit.shared.protocolo.Mensaje;
import sistema.sistemadesoportetecnicoit.shared.protocolo.TipoMensaje;

import java.io.IOException;
import java.util.Map;


public class Cliente extends Conexion {

    public Cliente() throws IOException {
        // El constructor padre ya inicializa el Socket y los ObjectStreams
        super();
    }

    // Ya no necesitas abrir flujos manuales, el padre ya lo hizo
    public void startClient() {
        System.out.println("PC3 conectada al servidor de objetos.");
    }

    /**
     * Solicita el siguiente ticket de la cola (Normal o Urgente)
     */
    public Ticket solicitarTicket(String tipoCola) throws IOException {
        // 1. Enviamos el mensaje de solicitud
        // El payload es el nombre de la cola (ej. "NORMAL" o "URGENTE")
        Mensaje peticion = new Mensaje(TipoMensaje.SOLICITAR_TICKET, tipoCola, "PC3");
        enviar(peticion);

        try {
            // 2. Recibimos la respuesta
            Mensaje respuesta = recibir();

            if (respuesta != null && respuesta.getTipo() == TipoMensaje.ENTREGAR_TICKET) {
                // El payload es el objeto Ticket directamente (o null si está vacía)
                return (Ticket) respuesta.getPayload();
            }
        } catch (ClassNotFoundException e) {
            throw new IOException("Error al reconstruir el ticket recibido", e);
        }

        return null;
    }

    /**
     * Envía el ticket ya procesado con su resolución para guardarlo en el Árbol B+
     */
    public void enviarFinalizacion(Ticket t) throws IOException {
        // Ya no necesitas StringBuilder ni "==" ni ";;"
        // Simplemente metes el objeto Ticket (con sus respuestas ya dentro) en el Mensaje
        Mensaje finalizado = new Mensaje(TipoMensaje.FINALIZAR_ATENCION, t, "PC3");
        enviar(finalizado);

        System.out.println("Ticket " + t.getTicketId() + " enviado para persistencia.");
    }
}
