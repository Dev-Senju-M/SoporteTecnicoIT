package sistema.sistemadesoportetecnicoit.pc3;

public final class SesionPC3 {

    private static String tecnico = "Tecnico - PC3";
    private static sistema.sistemadesoportetecnicoit.shared.models.Ticket ticketActual;

    private SesionPC3() {}

    public static String getTecnico() {
        return tecnico;
    }

    public static void setTecnico(String t) {
        if (t != null && !t.isBlank()) tecnico = t;
    }

    public static sistema.sistemadesoportetecnicoit.shared.models.Ticket getTicketActual() {
        return ticketActual;
    }

    public static void setTicketActual(sistema.sistemadesoportetecnicoit.shared.models.Ticket t) {
        ticketActual = t;
    }
}
