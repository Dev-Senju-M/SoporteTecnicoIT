package client.pc2_registro;

import shared.models.Ticket;
import shared.utils.Clasificador;

import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class PC2_Registro {

    private static final String HOST   = "localhost";
    private static final int    PUERTO = 5000;

    public static void registrar(Scanner scanner) {

        System.out.println("\n--- Registro de Nuevo Ticket ---");

        // ── Datos del usuario ────────────────────────────────────────────────
        System.out.print("Ingrese su DPI: ");
        String dpi = scanner.nextLine();

        System.out.print("Ingrese su nombre completo: ");
        String nombreApellido = scanner.nextLine();

        // ── Tipo de problema ─────────────────────────────────────────────────
        System.out.println("\nTipo de problema:");
        System.out.println("  1. Hardware");
        System.out.println("  2. Software");
        System.out.println("  3. Red/Internet");
        System.out.println("  4. Servidor");
        System.out.println("  5. Accesos/Permisos");
        System.out.println("  6. Otro");
        System.out.print("Seleccione una opcion: ");

        int opcion;
        try {
            opcion = Integer.parseInt(scanner.nextLine());
        } catch (NumberFormatException e) {
            System.out.println("Opcion invalida, se asignara tipo Otro.");
            opcion = 6;
        }

        String tipo;
        switch (opcion) {
            case 1: tipo = "Hardware";         break;
            case 2: tipo = "Software";         break;
            case 3: tipo = "Red/Internet";     break;
            case 4: tipo = "Servidor";         break;
            case 5: tipo = "Accesos/Permisos"; break;
            default: tipo = "Otro";            break;
        }

        System.out.print("Describa brevemente su problema: ");
        String motivo = scanner.nextLine();

        // ── Crear ticket ─────────────────────────────────────────────────────
        Ticket ticket = new Ticket(dpi, nombreApellido, motivo, tipo);

        // ── Mostrar clasificacion ────────────────────────────────────────────
        Clasificador.mostrarClasificacion(ticket);

        // ── Enviar al servidor PC1 ───────────────────────────────────────────
        try {
            Socket socket          = new Socket(HOST, PUERTO);
            ObjectOutputStream out = new ObjectOutputStream(socket.getOutputStream());
            out.writeObject(ticket);
            out.flush();
            socket.close();
            System.out.println("\nTicket enviado correctamente.");
            System.out.println("   Sera atendido en: " + Clasificador.getDestino(tipo));
        } catch (Exception e) {
            System.out.println("Error al conectar con el servidor: " + e.getMessage());
        }
    }
}