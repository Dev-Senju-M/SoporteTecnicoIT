package sistema.sistemadesoportetecnicoit.shared.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.LinkedHashMap;

public class Ticket implements Serializable {

    private static final long serialVersionUID = 1L;

    private String ticketId;          // Codigo TK-XXXX generado en PC2
    private String dpi;
    private String nombreApellido;
    private String motivo;
    private String tipo;
    private String usuarioAtendio;
    private long tiempoEntrada;
    private long tiempoAtencion;
    private long tiempoFinal;
    private boolean prioridad;
    private LinkedHashMap<String,String> respuestas = new LinkedHashMap<>();


    public Ticket() {
    }

    public Ticket(String dpi, String nombreApellido, String motivo, String tipo, Boolean prioridad) {
        this(null, dpi, nombreApellido, motivo, tipo, prioridad);
    }
    public Ticket(String ticketId,String tipo, String motivo, String usuarioAtendio) {
        this.ticketId = ticketId;
        this.motivo = motivo;
        this.tipo = tipo;
        this.usuarioAtendio = usuarioAtendio;
    }

    public Ticket(String ticketId, String dpi, String nombreApellido, String motivo, String tipo, Boolean prioridad) {
        this.ticketId = ticketId;
        this.dpi = dpi;
        this.nombreApellido = nombreApellido;
        this.motivo = motivo;
        this.tipo = tipo;
        this.tiempoEntrada = System.currentTimeMillis();
        this.prioridad = prioridad;
    }

    public String getFechaHoraAtencion() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(new Date(this.tiempoAtencion));
    }

    public String getFechaHoraEntrada() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(new Date(this.tiempoEntrada));
    }

    public double getDuracionAtencionMinutos() {
        return (double) (tiempoFinal - tiempoAtencion) / 60000;
    }

    public void marcarInicioAtencion() { this.tiempoAtencion = System.currentTimeMillis(); }

    public void marcarFinalAtencion(String tecnico) {
        this.tiempoFinal = System.currentTimeMillis();
        this.usuarioAtendio = tecnico;
    }

    public String getTicketId()                { return ticketId; }
    public void   setTicketId(String id)       { this.ticketId = id; }
    public String getDpi()                     { return dpi; }
    public void   setDpi(String dpi)           { this.dpi = dpi; }
    public String getNombreApellido()          { return nombreApellido; }
    public void   setNombreApellido(String n)  { this.nombreApellido = n; }
    public String getMotivo()                  { return motivo; }
    public void   setMotivo(String m)          { this.motivo = m; }
    public String getTipo()                    { return tipo; }
    public void   setTipo(String t)            { this.tipo = t; }
    public String getUsuarioAtendio()          { return usuarioAtendio; }
    public void   setUsuarioAtendio(String u)  { this.usuarioAtendio = u; }
    public long   getTiempoEntrada()           { return tiempoEntrada; }
    public void   setTiempoEntrada(long t)     { this.tiempoEntrada = t; }
    public long   getTiempoAtencion()          { return tiempoAtencion; }
    public void   setTiempoAtencion(long t)    { this.tiempoAtencion = t; }
    public long   getTiempoFinal()             { return tiempoFinal; }
    public void   setTiempoFinal(long t)       { this.tiempoFinal = t; }
    public boolean getPrioridad()              { return prioridad; }
    public void   setPrioridad(Boolean b)      { this.prioridad = b; }
    public LinkedHashMap<String,String> getRespuestas()                  { return respuestas; }
    public void setRespuestas(LinkedHashMap<String,String> r)            { this.respuestas = r; }
    public void agregarRespuesta(String pregunta, String respuesta)      { this.respuestas.put(pregunta, respuesta); }

    @Override
    public String toString() {
        return "[" + (ticketId != null ? ticketId + " | " : "")
                + "DPI: " + dpi + " | Nombre: " + nombreApellido
                + " | Tipo: " + tipo + " | Motivo: " + motivo + "]";
    }
}
