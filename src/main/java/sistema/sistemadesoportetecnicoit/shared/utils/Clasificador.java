package sistema.sistemadesoportetecnicoit.shared.utils;

import sistema.sistemadesoportetecnicoit.shared.models.Ticket;

public class Clasificador {

    public static String getDestino(String tipo) {
        switch (tipo) {
            case "Red/Internet":
            case "Servidor":      return "PC5";
            case "Accesos/Permisos": return "PC4";
            default:              return "PC3";
        }
    }

    public static int getPrioridad(String tipo) {
        switch (tipo) {
            case "Servidor":                               return 1;
            case "Red/Internet": case "Accesos/Permisos": return 2;
            default:                                       return 3;
        }
    }

    public static int getTiempoEstimado(String tipo) {
        switch (tipo) {
            case "Hardware":         return 2  * 60 * 1000;
            case "Software":         return 5  * 60 * 1000;
            case "Red/Internet":     return 5  * 60 * 1000;
            case "Servidor":         return 10 * 60 * 1000;
            case "Accesos/Permisos": return 5  * 60 * 1000;
            default:                 return 2  * 60 * 1000;
        }
    }

    public static void mostrarClasificacion(Ticket ticket) {
        System.out.println("\n--- Clasificacion del Ticket ---");
        System.out.println("  DPI       : " + ticket.getDpi());
        System.out.println("  Tipo      : " + ticket.getTipo());
        System.out.println("  Motivo    : " + ticket.getMotivo());
        System.out.println("  Destino   : " + getDestino(ticket.getTipo()));
        System.out.println("  Prioridad : " + etiquetaPrioridad(getPrioridad(ticket.getTipo())));
        System.out.println("  T.Estimado: " + (getTiempoEstimado(ticket.getTipo()) / 60000) + " min");
    }

    private static String etiquetaPrioridad(int p) {
        switch (p) {
            case 1:  return "CRITICA";
            case 2:  return "ALTA";
            default: return "NORMAL";
        }
    }
}