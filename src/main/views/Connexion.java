package main.views;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class Connexion extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Charger le fichier FXML
        Parent root = FXMLLoader.load(getClass().getResource("pageprincipal.fxml"));

        // Créer une scène
        Scene scene = new Scene(root, 1920, 1080);

        // Définir la scène et afficher la fenêtre principale
        primaryStage.setScene(scene);
        primaryStage.setTitle("pageadherent");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
