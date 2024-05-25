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
        // Launch the JavaFX application by calling the launch() method with the connection controller
        new ConnexionController().start(primaryStage);
    }

    public static void main(String[] args) {
        try {

            MySQLStarter.startMySQL();
            // Perform necessary database operations
            DatabaseConnection.insertUserData(new User("nadir1401@gmail.com", "Nadir", "NEHILI",0,5));
            DatabaseConnection.insertUserData(new User("flavio2002@gmail.com", "Flavio", "Soares",0,5));


        } catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        // Launch the JavaFX application
        launch(args);
    }

}
