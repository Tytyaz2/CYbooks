package main.views;

import javafx.application.Application;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;

public class Connexion extends Application {

    private Stage primaryStage;
    private VBox root;


    // Constructeur sans paramètres
    public Connexion() {
        // Vous pouvez initialiser des champs ou effectuer d'autres actions ici si nécessaire
    }



    @Override
    public void start(Stage primaryStage) {
        this.primaryStage = primaryStage;

        // Créer le bandeau CYBOOKS
        Label labelCybooks = new Label("CYBOOKS");
        labelCybooks.setStyle("-fx-font-size: 24pt; -fx-font-weight: bold;top:0%;");

        // Créer le conteneur racine
        root = new VBox();
        root.setAlignment(Pos.CENTER);
        root.getChildren().add(labelCybooks);

        // Créer la scène principale
        Scene scene = new Scene(root, 1920, 1080);

        // Configurer et afficher la scène principale
        primaryStage.setScene(scene);
        primaryStage.setTitle("CYBOOKS Application");
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
