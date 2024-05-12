package main;

import javafx.application.Application;
import main.controllers.ConnexionControllers;
import main.models.DatabaseConnection;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            DatabaseConnection.insertUserData("Soares","Flavio","soares.flavio2002@gmail.com","Le Petit Prince","Saint-Exupery","2305126516065","2024-06-12","2024-06-13");

            System.out.println("Insertion de données réussie !");

            // Lancer l'application JavaFX en appelant la méthode launch()
            Application.launch(ConnexionControllers.class, args);

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Une erreur s'est produite lors de l'insertion des données.");
        }
    }

}
