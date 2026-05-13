package sistema.sistemadesoportetecnicoit.pc5;

import javafx.animation.FadeTransition;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.util.Duration;
import org.kordamp.ikonli.javafx.FontIcon;
import sistema.sistemadesoportetecnicoit.PC5Application;
import sistema.sistemadesoportetecnicoit.shared.utils.TemaManager;

import java.io.IOException;

public class LoginController {

    @FXML private VBox screenRoot;
    @FXML private VBox panelHeader;
    @FXML private TextField txtNombre;
    @FXML private Label lblError;
    @FXML private FontIcon iconTema;

    @FXML
    public void initialize() {
        screenRoot.setOpacity(0);
        panelHeader.setTranslateY(-12);

        FadeTransition fade = new FadeTransition(Duration.millis(300), screenRoot);
        fade.setFromValue(0); fade.setToValue(1);

        TranslateTransition slide = new TranslateTransition(Duration.millis(280), panelHeader);
        slide.setFromY(-12); slide.setToY(0);

        fade.play(); slide.play();

        TemaManager.aplicar(screenRoot);
        actualizarIconTema();
    }

    @FXML
    private void iniciar() {
        String nombre = txtNombre.getText() == null ? "" : txtNombre.getText().trim();
        if (nombre.isEmpty()) {
            lblError.setText("Ingrese su nombre para continuar.");
            shake(txtNombre);
            return;
        }
        try {
            if (SesionPC5.getConexion() == null) {
                Cliente c = new Cliente();
                c.startClient();
                SesionPC5.setConexion(c);
            }
            SesionPC5.setTecnico(nombre);
            PC5Application.cargarVista("pc5_estacion.fxml");
        } catch (IOException e) {
            lblError.setText("Error: No se pudo conectar con el Servidor Central.");
            System.err.println("Fallo de conexion: " + e.getMessage());
        }
    }

    @FXML private void toggleTema() {
        TemaManager.toggle();
        TemaManager.aplicar(screenRoot);
        actualizarIconTema();
    }

    private void actualizarIconTema() {
        iconTema.setIconLiteral(TemaManager.esModoClaro() ? "fas-moon" : "fas-sun");
    }

    private void shake(Node node) {
        TranslateTransition tt = new TranslateTransition(Duration.millis(65), node);
        tt.setByX(8); tt.setCycleCount(6); tt.setAutoReverse(true);
        tt.setOnFinished(e -> node.setTranslateX(0));
        tt.play();
    }
}
