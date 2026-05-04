package client.pc2_registro;

import shared.models.Ticket;
import shared.structures.HashMapUsuarios;

import java.util.LinkedList;
import java.util.Scanner;

public class ConsultaHistorial {

    // La misma tabla hash que usa el servidor
    private static HashMapUsuarios historial = new HashMapUsuarios();

    public static void consultar(Scanner scanner) {

        System.out.println("\n--- Consulta de Historial por DPI ---");

        System.out.print("Ingrese su DPI: ");
        String dpi = scanner.nextLine();

        // ── Verificar si existe el DPI ───────────────────────────────────────
        if (!historial.containsKey(dpi)) {
            System.out.println("No se encontro historial para el DPI: " + dpi);
            return;
        }

        // ── Obtener y mostrar historial ──────────────────────────────────────
        LinkedList<Ticket> tickets = historial.get(dpi);

        System.out.println("\n=========================================");
        System.out.println("   HISTORIAL DE TICKETS | DPI: " + dpi);
        System.out.println("=========================================");
        System.out.printf("%-5s %-15s %-20s %-12s %-20s%n",
                "#", "Tipo", "Motivo", "Duracion", "Tecnico");
        System.out.println("-----------------------------------------" +
                           "-----------------------------------------");

        int contador = 1;
        for (Ticket t : tickets) {
            System.out.printf("%-5s %-15s %-20s %-12s %-20s%n",
                    contador++,
                    t.getTipo(),
                    t.getMotivo(),
                    String.format("%.2f min", t.getDuracionAtencionMinutos()),
                    t.getUsuarioAtendio() != null ? t.getUsuarioAtendio() : "Pendiente"
            );
        }

        System.out.println("=========================================");
        System.out.println("Total de tickets: " + tickets.size());
    }

    // ── Permite al servidor agregar tickets al historial ─────────────────────
    // Este método lo llamará PC1 cada vez que un ticket sea atendido
    public static void agregarAlHistorial(String dpi, Ticket ticket) {
        historial.agregarTicket(dpi, ticket);
    }
}