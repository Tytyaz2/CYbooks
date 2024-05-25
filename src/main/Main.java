package main;

import javafx.application.Application;
import javafx.stage.Stage;
import main.controllers.ConnexionController;
import main.models.DatabaseConnection;
import main.models.MySQLStarter;
import main.models.User;

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
            DatabaseConnection.insertUserData(new User("nadir1401@gmail.com", "Nadir", "NEHILI",0,5));
            DatabaseConnection.insertUserData(new User("flavio2002@gmail.com", "Flavio", "Soares",0,5));


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        // Lancer l'application JavaFX
        launch(args);
    }

}
