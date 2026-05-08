package sistema.sistemadesoportetecnicoit.pc2;

import sistema.sistemadesoportetecnicoit.shared.conexion.Conexion;
import sistema.sistemadesoportetecnicoit.shared.models.Ticket;
import sistema.sistemadesoportetecnicoit.shared.protocolo.TipoMensaje;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class Cliente extends Conexion {

    public Cliente() throws IOException {
        super("cliente"); // se usa el constructor para cliente de Conexion
    }

    public void startClient() throws IOException {
        abrirFlujosCliente();
    }

    public void enviarTicket(Ticket t) throws IOException {
        String linea = TipoMensaje.REGISTRAR_TICKET.name()
                + "|" + safe(t.getTicketId())
                + "|" + safe(t.getDpi())
                + "|" + safe(t.getNombreApellido())
                + "|" + safe(t.getTipo())
                + "|" + safe(t.getMotivo())
                + "|" + (t.getPrioridad() != null && t.getPrioridad());
        enviarAlServidor(linea);
        System.out.println("Ticket enviado: " + t);
    }

    public List<Ticket> buscarPorDpi(String dpi) throws IOException {
        enviarAlServidor(TipoMensaje.BUSCAR_DPI.name() + "|" + safe(dpi));

        List<Ticket> resultado = new ArrayList<>();
        mensajeCliente = entradaServidor.readLine();
        if (mensajeCliente == null) return resultado;

        String[] cabecera = mensajeCliente.split("\\|", -1);
        if (cabecera.length < 1) return resultado;

        if (cabecera[0].equals(TipoMensaje.ERROR.name())) {
            throw new IOException("Servidor respondio ERROR: "
                    + (cabecera.length > 1 ? cabecera[1] : ""));
        }
        if (!cabecera[0].equals(TipoMensaje.RESPUESTA_DPI.name())) {
            throw new IOException("Tipo inesperado: " + cabecera[0]);
        }

        int n = 0;
        if (cabecera.length > 1) {
            try { n = Integer.parseInt(cabecera[1]); } catch (NumberFormatException ignored) {}
        }

        for (int i = 0; i < n; i++) {
            String fila = entradaServidor.readLine();
            if (fila == null) break;
            String[] c = fila.split(";", -1);
            String ticketId = c.length > 0 ? c[0] : "";
            String tipo     = c.length > 1 ? c[1] : "";
            String motivo   = c.length > 2 ? c[2] : "";
            String tecnico  = c.length > 3 ? c[3] : "";
            long   tAtenc   = 0L;
            if (c.length > 4) {
                try { tAtenc = Long.parseLong(c[4]); } catch (NumberFormatException ignored) {}
            }
            Ticket tk = new Ticket(ticketId, dpi, "", motivo, tipo, null);
            tk.setUsuarioAtendio(tecnico);
            tk.setTiempoAtencion(tAtenc);
            resultado.add(tk);
        }

        entradaServidor.readLine();
        return resultado;
    }

    private static String safe(String s) {
        return s == null ? "" : s.replace('|', ' ').replace('\n', ' ').replace('\r', ' ');
    }
}
