package sistema.sistemadesoportetecnicoit.pc2;

public class SesionPC2 {
    private static String tecnico = "Recepcion - PC2";
    private static Cliente conexionFija;

    private SesionPC2() {}

    public static String getTecnico() { return tecnico; }
    public static void setTecnico(String t) { tecnico = t; }

    public static Cliente getConexion() {
        return conexionFija;
    }
    public static void setConexion(Cliente cli) {
        conexionFija = cli;
    }
}
