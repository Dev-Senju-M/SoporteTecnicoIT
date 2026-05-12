package sistema.sistemadesoportetecnicoit.pc4;

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
        System.out.println("PC4 conectada al servidor.");
    }

    public Ticket solicitarTicket(String tipoCola) throws IOException {
        Mensaje peticion = new Mensaje(TipoMensaje.SOLICITAR_TICKET, tipoCola, "PC4");
        enviar(peticion);

        try {
            Mensaje respuesta = recibir();
            if (respuesta != null && respuesta.getTipo() == TipoMensaje.ENTREGAR_TICKET) {
                return (Ticket) respuesta.getPayload();
            }
            System.out.println("PC4: No hay tickets disponibles en la cola: " + tipoCola);
        } catch (ClassNotFoundException e) {
            throw new IOException("Error al reconstruir el ticket recibido", e);
        }

        return null;
    }

    public void enviarFinalizacion(Ticket t) throws IOException {
        if(t==null) return;
        Mensaje finalizado = new Mensaje(TipoMensaje.FINALIZAR_ATENCION, t, "PC4");
        enviar(finalizado);
        System.out.println("Ticket " + t.getTicketId() + " enviado para persistencia.");
    }
}
