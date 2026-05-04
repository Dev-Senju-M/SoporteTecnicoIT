package client.pc2_registro;

import java.util.Scanner;

public class MenuPrincipal {

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        int opcion = 0;

        do {
            System.out.println("\n=========================================");
            System.out.println("   PC2 - MESA DE AYUDA - HELP DESK       ");
            System.out.println("=========================================");
            System.out.println("  1. Registrar nuevo ticket");
            System.out.println("  2. Consultar historial por DPI");
            System.out.println("  3. Salir");
            System.out.println("=========================================");
            System.out.print("Seleccione una opcion: ");

            try {
                opcion = Integer.parseInt(scanner.nextLine());
            } catch (NumberFormatException e) {
                System.out.println("Opcion invalida, intente de nuevo.");
                continue;
            }

            switch (opcion) {
                case 1:
                    // Lanza el registro de ticket
                    PC2_Registro.registrar(scanner);
                    break;
                case 2:
                    // Lanza la consulta de historial
                    ConsultaHistorial.consultar(scanner);
                    break;
                case 3:
                    System.out.println("\nCerrando PC2. Hasta luego.");
                    break;
                default:
                    System.out.println("Opcion invalida, intente de nuevo.");
            }

        } while (opcion != 3);

        scanner.close();
    }
}