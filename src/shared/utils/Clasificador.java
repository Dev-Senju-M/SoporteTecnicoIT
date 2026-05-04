package shared.utils;

import shared.models.Ticket;

public class Clasificador {

    // ── Destino según tipo de problema ───────────────────────────────────────
    // Decide a qué PC va el ticket
    public static String getDestino(String tipo) {
        switch (tipo) {
            case "Red/Internet":
            case "Servidor":
                return "PC5"; // Soporte especializado
            case "Accesos/Permisos":
                return "PC4"; // Incidentes críticos
            case "Hardware":
            case "Software":
            case "Otro":
            default:
                return "PC3"; // Soporte general
        }
    }

    // ── Prioridad según tipo de problema ─────────────────────────────────────
    // 1 = Crítica, 2 = Alta, 3 = Normal
    public static int getPrioridad(String tipo) {
        switch (tipo) {
            case "Servidor":
                return 1; // Crítica
            case "Red/Internet":
            case "Accesos/Permisos":
                return 2; // Alta
            default:
                return 3; // Normal
        }
    }

    // ── Tiempo estimado de atención en milisegundos ──────────────────────────
    public static int getTiempoEstimado(String tipo) {
        switch (tipo) {
            case "Hardware":         return 2  * 60 * 1000; //  2 min
            case "Software":         return 5  * 60 * 1000; //  5 min
            case "Red/Internet":     return 5  * 60 * 1000; //  5 min
            case "Servidor":         return 10 * 60 * 1000; // 10 min
            case "Accesos/Permisos": return 5  * 60 * 1000; //  5 min
            default:                 return 2  * 60 * 1000; //  2 min
        }
    }

    // ── Resumen de clasificación de un ticket ────────────────────────────────
    public static void mostrarClasificacion(Ticket ticket) {
        System.out.println("\n--- Clasificacion del Ticket ---");
        System.out.println("  Usuario   : " + ticket.getNombreApellido());
        System.out.println("  DPI       : " + ticket.getDpi());
        System.out.println("  Tipo      : " + ticket.getTipo());
        System.out.println("  Motivo    : " + ticket.getMotivo());
        System.out.println("  Destino   : " + getDestino(ticket.getTipo()));
        System.out.println("  Prioridad : " + etiquetaPrioridad(getPrioridad(ticket.getTipo())));
        System.out.println("  T.Estimado: " + (getTiempoEstimado(ticket.getTipo()) / 60000) + " min");
    }

    // ── Etiqueta legible de prioridad ────────────────────────────────────────
    private static String etiquetaPrioridad(int p) {
        switch (p) {
            case 1:  return "CRITICA";
            case 2:  return "ALTA";
            default: return "NORMAL";
        }
    }
}