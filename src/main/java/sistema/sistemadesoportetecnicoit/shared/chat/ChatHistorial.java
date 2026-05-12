package sistema.sistemadesoportetecnicoit.shared.chat;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public final class ChatHistorial {

    private static final ObservableList<String> mensajes =
            FXCollections.observableArrayList();

    private ChatHistorial() {}

    public static void agregar(String origen, String texto) {
        mensajes.add(origen + ": " + texto);
    }

    public static ObservableList<String> getMensajes() {
        return mensajes;
    }
}
