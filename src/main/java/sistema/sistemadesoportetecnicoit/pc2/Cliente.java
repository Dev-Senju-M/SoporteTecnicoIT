package sistema.sistemadesoportetecnicoit.pc2;

import sistema.sistemadesoportetecnicoit.shared.conexion.Conexion;
import sistema.sistemadesoportetecnicoit.shared.models.Ticket;
import sistema.sistemadesoportetecnicoit.shared.protocolo.Mensaje;
import sistema.sistemadesoportetecnicoit.shared.protocolo.TipoMensaje;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Cliente extends Conexion {

    public Cliente() throws IOException{
        super();
    }

    public void startClient() throws IOException {
        System.out.println("Cliente conectado a los flujos de objetos.");
    }

    public void enviarTicket(Ticket t) throws IOException{
        Mensaje msg = new Mensaje(TipoMensaje.REGISTRAR_TICKET, t, "PC2");
        enviar(msg);
        System.out.println("Ticket enviado: " +t.getTicketId());
    }

    @SuppressWarnings("unchecked")
    public List<Ticket> buscarPorDpi(String dpi) throws IOException {
        Mensaje peticion = new Mensaje(TipoMensaje.BUSCAR_DPI, dpi, "PC2");
        enviar(peticion);

        try{
            Mensaje respuesta = recibir();

            if (respuesta != null){
                if (respuesta.getTipo() == TipoMensaje.RESPUESTA_DPI) {
                    return (List<Ticket>) respuesta.getPayload();
                }else if (respuesta.getTipo() == TipoMensaje.ERROR) {
                    throw new IOException("Error del servidor: " + respuesta.getPayload());
                }
            }
        }catch(ClassNotFoundException e){
            throw new IOException("Error al procesar la respuesta del servidor", e);
        }

        return new ArrayList<>();
    }
}