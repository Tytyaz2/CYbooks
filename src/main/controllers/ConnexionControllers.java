package main.controllers;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ConnexionControllers extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Charger le fichier FXML
        Parent root = FXMLLoader.load(getClass().getResource("/main/views/connexion.fxml"));

        // Créer une scène
        Scene scene = new Scene(root, 1920, 1080);

        // Définir la scène et afficher la fenêtre principale
        primaryStage.setScene(scene);
        primaryStage.setTitle("connexion");
        primaryStage.show();
    }
}
