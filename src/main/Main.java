package main;

import javafx.application.Application;
import main.controllers.ConnexionController;
import main.models.DatabaseConnection;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try{
            DatabaseConnection.insertUserData("soares.flavio2002@gmail.com","Flavio","Soares");
        }
        catch(SQLException e){
            System.out.println("Utilisateur deja existant");
        }
            // Lancer l'application JavaFX en appelant la méthode launch()
            Application.launch(ConnexionController.class, args);
            /*BookSearch bookSearch = new BookSearch();
        bookSearch.search("Harry Potter");*/
    }

}
