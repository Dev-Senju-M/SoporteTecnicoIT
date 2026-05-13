package sistema.sistemadesoportetecnicoit;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;

public class PC2Application extends Application {

    private static Stage primaryStage;

    @Override
    public void start(Stage primaryStage) throws Exception{
        this.primaryStage = primaryStage;
        primaryStage.setTitle("PC2 - Registro de Tickets");
        cargarVista("pc2_menu.fxml");
        primaryStage.show();
        primaryStage.setMaximized(true);
    }

    public static void cargarVista(String fxml){
        try {
            URL url = PC2Application.class.getResource("pc2/" + fxml);
            if (url == null) throw new IOException("No se encontro el FXML: " + fxml);

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            if (primaryStage.getScene() == null){
                primaryStage.setScene(new Scene(root));
                primaryStage.sizeToScene();
            } else {
                primaryStage.getScene().setRoot(root);
            }
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
