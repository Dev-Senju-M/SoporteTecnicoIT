package sistema.sistemadesoportetecnicoit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class PC5Application extends Application {

    private static Stage stagePrincipal;

    @Override
    public void start(Stage stage) throws IOException {
        stagePrincipal = stage;
        stage.setTitle("PC5 - Soporte Especial");
        cargarVista("pc5_login.fxml");
        stage.show();
    }

    public static void cargarVista(String fxml) {
        try {
            URL url = PC5Application.class.getResource("pc5/" + fxml);
            if (url == null) throw new IOException("No se encontro: " + fxml);

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            if (stagePrincipal.getScene() == null) {
                stagePrincipal.setScene(new Scene(root));
            } else {
                stagePrincipal.getScene().setRoot(root);
            }
            stagePrincipal.sizeToScene();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
