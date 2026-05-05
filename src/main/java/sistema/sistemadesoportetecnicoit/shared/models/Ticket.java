package sistema.sistemadesoportetecnicoit.shared.models;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Ticket implements Serializable {

    private String dpi;
    private String nombreApellido;
    private String motivo;
    private String tipo;
    private String usuarioAtendio;
    private long tiempoEntrada;
    private long tiempoAtencion;
    private long tiempoFinal;

    public Ticket(String dpi, String nombreApellido, String motivo, String tipo) {
        this.dpi = dpi;
        this.nombreApellido = nombreApellido;
        this.motivo = motivo;
        this.tipo = tipo;
        this.tiempoEntrada = System.currentTimeMillis();
    }

    public String getFechaHoraAtencion() {
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return sdf.format(new Date(this.tiempoAtencion));
    }

    public double getDuracionAtencionMinutos() {
        return (double) (tiempoFinal - tiempoAtencion) / 60000;
    }

    public void marcarInicioAtencion() { this.tiempoAtencion = System.currentTimeMillis(); }

    public void marcarFinalAtencion(String tecnico) {
        this.tiempoFinal = System.currentTimeMillis();
        this.usuarioAtendio = tecnico;
    }

    public String getDpi()                          { return dpi; }
    public void   setDpi(String dpi)               { this.dpi = dpi; }
    public String getNombreApellido()               { return nombreApellido; }
    public void   setNombreApellido(String n)       { this.nombreApellido = n; }
    public String getMotivo()                       { return motivo; }
    public void   setMotivo(String motivo)          { this.motivo = motivo; }
    public String getTipo()                         { return tipo; }
    public void   setTipo(String tipo)              { this.tipo = tipo; }
    public String getUsuarioAtendio()               { return usuarioAtendio; }
    public void   setUsuarioAtendio(String u)       { this.usuarioAtendio = u; }
    public long   getTiempoEntrada()                { return tiempoEntrada; }
    public void   setTiempoEntrada(long t)          { this.tiempoEntrada = t; }
    public long   getTiempoAtencion()               { return tiempoAtencion; }
    public void   setTiempoAtencion(long t)         { this.tiempoAtencion = t; }
    public long   getTiempoFinal()                  { return tiempoFinal; }
    public void   setTiempoFinal(long t)            { this.tiempoFinal = t; }

    @Override
    public String toString() {
        return "[Ticket | DPI: " + dpi + " | Nombre: " + nombreApellido
             + " | Tipo: " + tipo + " | Motivo: " + motivo + "]";
    }
}