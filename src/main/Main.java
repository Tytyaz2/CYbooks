package main;

import javafx.application.Application;
import javafx.stage.Stage;
import main.controllers.ConnexionController;
import main.models.ApiCaller;
import main.models.DatabaseConnection;
import main.models.MySQLStarter;

import java.sql.SQLException;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Lancer l'application JavaFX en appelant la méthode launch() avec le contrôleur de connexion
        new ConnexionController().start(primaryStage);
    }

    public static void main(String[] args) {
        try {

            MySQLStarter.startMySQL();
            // Effectuer les opérations de base de données nécessaires
            DatabaseConnection.insertUserData("nadir1401@gmail.com", "Nadir", "NEHILI");
            DatabaseConnection.insertUserData("flavio2002@gmail.com", "Flavio", "Soares");


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        // Lancer l'application JavaFX
        launch(args);
    }

}
