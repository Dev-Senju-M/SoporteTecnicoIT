package sistema.sistemadesoportetecnicoit.pc5;

import sistema.sistemadesoportetecnicoit.shared.conexion.Conexion;
import sistema.sistemadesoportetecnicoit.shared.models.Ticket;
import sistema.sistemadesoportetecnicoit.shared.protocolo.Mensaje;
import sistema.sistemadesoportetecnicoit.shared.protocolo.TipoMensaje;

import java.io.IOException;

public class Cliente extends Conexion {

    public Cliente() throws IOException {
        super();
    }

    public void startClient() {
        System.out.println("PC5 conectada al servidor de objetos.");
    }

    public Ticket solicitarTicket(String tipoCola) throws IOException {
        Mensaje peticion = new Mensaje(TipoMensaje.SOLICITAR_TICKET, tipoCola, "PC5");
        enviar(peticion);

        try {
            Mensaje respuesta = recibir();
            if (respuesta != null && respuesta.getTipo() == TipoMensaje.ENTREGAR_TICKET) {
                return (Ticket) respuesta.getPayload();
            }
        } catch (ClassNotFoundException e) {
            throw new IOException("Error al reconstruir el ticket recibido", e);
        }

        return null;
    }

    public void enviarFinalizacion(Ticket t) throws IOException {
        Mensaje finalizado = new Mensaje(TipoMensaje.FINALIZAR_ATENCION, t, "PC5");
        enviar(finalizado);
        System.out.println("Ticket " + t.getTicketId() + " enviado para persistencia.");
    }
}
