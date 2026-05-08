package sistema.sistemadesoportetecnicoit.pc3;

import sistema.sistemadesoportetecnicoit.shared.conexion.Conexion;
import sistema.sistemadesoportetecnicoit.shared.models.Ticket;
import sistema.sistemadesoportetecnicoit.shared.protocolo.TipoMensaje;

import java.io.IOException;
import java.util.Map;


public class Cliente extends Conexion {

    public Cliente() throws IOException {
        super("cliente");
    }

    public void startClient() throws IOException {
        abrirFlujosCliente();
    }

    public Ticket solicitarTicket(String cola) throws IOException {
        enviarAlServidor(TipoMensaje.SOLICITAR_TICKET.name() + "|" + safe(cola));

        mensajeCliente = entradaServidor.readLine();
        if (mensajeCliente == null) return null;

        String[] partes = mensajeCliente.split("\\|", -1);
        if (partes.length < 2) return null;
        if (!partes[0].equals(TipoMensaje.ENTREGAR_TICKET.name())) return null;
        if (partes[1].equalsIgnoreCase("NONE")) return null;

        String ticketId = partes[1];
        String dpi      = partes.length > 2 ? partes[2] : "";
        String nombre   = partes.length > 3 ? partes[3] : "";
        String tipo     = partes.length > 4 ? partes[4] : "";
        String motivo   = partes.length > 5 ? partes[5] : "";
        Boolean pri     = Boolean.FALSE;
        if (partes.length > 6) pri = Boolean.parseBoolean(partes[6]);
        return new Ticket(ticketId, dpi, nombre, motivo, tipo, pri);
    }

    public void enviarFinalizacion(Ticket t) throws IOException {
        StringBuilder respuestas = new StringBuilder();
        if (t.getRespuestas() != null) {
            boolean primera = true;
            for (Map.Entry<String, String> e : t.getRespuestas().entrySet()) {
                if (!primera) respuestas.append(";;");
                respuestas.append(safe(e.getKey()))
                          .append("==")
                          .append(safe(e.getValue()));
                primera = false;
            }
        }

        String linea = TipoMensaje.FINALIZAR_ATENCION.name()
                + "|" + safe(t.getTicketId())
                + "|" + safe(t.getUsuarioAtendio())
                + "|" + t.getTiempoEntrada()
                + "|" + t.getTiempoAtencion()
                + "|" + t.getTiempoFinal()
                + "|" + respuestas.toString();
        enviarAlServidor(linea);
    }

    private static String safe(String s) {
        return s == null ? "" : s.replace('|', ' ').replace('\n', ' ').replace('\r', ' ');
    }
}
