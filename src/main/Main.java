package main;

import javafx.application.Application;
import main.views.Connexion;
import main.models.DatabaseConnection;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try {
            String[] columns = {"test1", "test2", "test3", "test4"};
            Object[] values = {1, 23, 45, 5};


            DatabaseConnection.insertData("test", columns, values);

            System.out.println("Insertion de données réussie !");

            // Lancer l'application JavaFX en appelant la méthode launch()
            Application.launch(Connexion.class, args);

        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Une erreur s'est produite lors de l'insertion des données.");
        }
    }

}
