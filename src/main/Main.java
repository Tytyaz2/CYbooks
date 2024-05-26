package main;

import javafx.application.Application;
import javafx.stage.Stage;
import main.controllers.ConnectionController;
import main.dataBase.DatabaseConnection;
import main.dataBase.MySQLStarter;
import main.models.*;
import java.sql.SQLException;


public class Main extends Application {
    static User Nadir = new User("nadir14@gmail.com", "Nadir", "NEHILI", 0, 5);

    @Override
    public void start(Stage primaryStage) throws Exception {
        // Launch the JavaFX application by calling the launch() method with the connection controller
        new ConnectionController().start(primaryStage);
    }

    public static void main(String[] args) {
        try {

            MySQLStarter.startMySQL();
            // Initialize with 1 user
                DatabaseConnection.insertUserData(Nadir);

        }catch (SQLException e) {
            System.out.println(e.getMessage());
        }

        // Launch the JavaFX application
        launch(args);
    }

}
