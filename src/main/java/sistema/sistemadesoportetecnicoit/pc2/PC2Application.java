package sistema.sistemadesoportetecnicoit.pc2;

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
    }

    public static void cargarVista(String fxml){
        try {
            URL url = PC2Application.class.getResource(fxml);
            if (url == null) throw new IOException("No se encontro el FXML: " + fxml);

            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();

            if (primaryStage.getScene() == null){
                primaryStage.setScene(new Scene(root));
            } else {
                primaryStage.getScene().setRoot(root);
            }
            primaryStage.sizeToScene();
        } catch (IOException e){
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        launch(args);
    }
}
