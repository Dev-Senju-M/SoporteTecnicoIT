package sistema.sistemadesoportetecnicoit.shared.utils;

import java.util.List;
import java.util.Map;

public class BancoPreguntas {

    private  BancoPreguntas(){}

    private static final Map<String, List<String>> Base = Map.ofEntries(

            Map.entry("Hardware",       List.of(
            "Que dispositivo presenta el problema?",
            "El dispositivo enciende? (Si / No / Intermitente)",
            "Cuando comenzo la falla?")),
            Map.entry("Software",       List.of(
            "Que aplicacion presenta el error?",
            "Aparece algun mensaje de error? Cual?",
            "Cuando ocurrio por primera vez?")),
            Map.entry("Conectividad",   List.of(
            "Pierde toda la conexion o solo algunos sitios?",
            "Esta por cable o WiFi?",
            "Otros equipos en la red tienen el problema?")),

            // PC4 - Critico
            Map.entry("Accesos",          List.of(
            "A que sistema o cuenta no puede acceder?",
            "Que mensaje aparece al intentar ingresar?",
            "Es solo su usuario o hay mas afectados?")),
            Map.entry("Incidente Critico", List.of(
            "Que sistema esta afectado?",
            "Es caida total o intermitente / lento?",
            "Cuantos usuarios estan afectados?")),

            // PC5 - Especial
            Map.entry("Servidores",        List.of(
            "Que servidor esta afectado?",
            "Es problema de hardware, software o configuracion?",
            "Esta caido completamente o degradado?")),
            Map.entry("Infraestructura",   List.of(
            "Que componente?",
            "Es instalacion nueva o problema con existente?",
            "Cual es el alcance del problema?"))
            );

    private static final Map<String, List<String>> EXTRA = Map.ofEntries(
            Map.entry("Hardware",       List.of(
                    "Ha probado con otro cable o puerto?",
                    "Otros usuarios tienen el mismo problema?")),
            Map.entry("Software",       List.of(
                    "Ya reinicio la aplicacion o el equipo?",
                    "Recientemente se actualizo o reinstalo algo?")),
            Map.entry("Conectividad",   List.of(
                    "Ya reinicio el router/modem?",
                    "Esta usando VPN? La VPN conecta?")),
            Map.entry("Accesos",          List.of(
                    "Cuando fue su ultimo cambio de contrasena?",
                    "Su cuenta tiene MFA/2FA activo?")),
            Map.entry("Incidente Critico", List.of(
                    "Hay perdida de datos confirmada?",
                    "Cuando se detecto por primera vez?")),
            Map.entry("Servidores",        List.of(
                    "Cuando fue el ultimo reinicio o mantenimiento?",
                    "Hay logs de error visibles?")),
            Map.entry("Infraestructura",   List.of(
                    "Hay licenciamiento o garantia involucrada?",
                    "Requiere coordinar ventana de mantenimiento?"))
    );

    public static List<String> getRespuestas(String tipo, boolean traeMotivo) {
        List<String> rp = Base.getOrDefault(tipo, List.of());
        if (traeMotivo) return rp;

        List<String> extra = EXTRA.getOrDefault(tipo, List.of());
        java.util.ArrayList<String> total = new java.util.ArrayList<>(rp);
        total.addAll(extra);
        return total;
    }
}
