package sistema.sistemadesoportetecnicoit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class PC4Application extends Application {

    private static Stage stagePrincipal;

    @Override
    public void start(Stage stage) throws IOException {
        stagePrincipal = stage;
        stage.setTitle("PC4 - Soporte Prioritario");
        cargarVista("pc4_login.fxml");
        stage.show();
        stage.setMaximized(true);
    }

    public static void cargarVista(String fxml) {
        try {
            URL url = PC4Application.class.getResource("pc4/" + fxml);
            if (url == null) throw new IOException("No se encontro: " + fxml);

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            if (stagePrincipal.getScene() == null) {
                stagePrincipal.setScene(new Scene(root));
                stagePrincipal.sizeToScene();
            } else {
                stagePrincipal.getScene().setRoot(root);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
