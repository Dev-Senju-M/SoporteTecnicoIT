package sistema.sistemadesoportetecnicoit.pc5;

public final class SesionPC5 {

    private static String tecnico = "Tecnico - PC5";
    private static sistema.sistemadesoportetecnicoit.shared.models.Ticket ticketActual;

    private SesionPC5() {}

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
