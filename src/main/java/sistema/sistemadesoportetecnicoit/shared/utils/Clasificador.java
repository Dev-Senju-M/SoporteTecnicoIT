package sistema.sistemadesoportetecnicoit.shared.utils;

public class Clasificador {

    public static final String PC3_GENERAL = "PC3_GENERAL";
    public static final String PC4_PRIORIDAD = "PC4_PRIORIDAD";
    public static final String PC5_ESPECIAL = "PC5_ESPECIAL";

    private  Clasificador(){ /* Utils Class*/}

    public  static String getDestino(String tipo){
        if (tipo == null) return PC3_GENERAL;

        switch (tipo){
            case "Accesos/Red":
            case "Incidente Critico":
                return PC4_PRIORIDAD;
            case "Servidores":
            case "Infraestructura":
                return PC5_ESPECIAL;
            case "Hardware":
            case "Software":
            case "Conectividad":
            default:
                return PC3_GENERAL;
        }
    }

    public static  boolean esPrioridade(String tipo){
        if (tipo == null) return false;
        return tipo.equals("Accesos/Red") || tipo.equals("Incidente Critico");
    }

    public static boolean esEspecial(String tipo){
        if (tipo == null) return false;
        switch (tipo){
            case "Hardware":
            case "Software":
            case "Conectividad":
                return true;
            default:
                return false;
        }
    }
}
