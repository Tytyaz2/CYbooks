package main;

import javafx.application.Application;
import main.controllers.ConnexionController;
import main.models.BookSearch;
import main.models.DatabaseConnection;

import java.sql.SQLException;

public class Main {
    public static void main(String[] args) {
        try{
            DatabaseConnection.insertUserData("soares.flavio2002@gmail.com","Flavio","Soares");
            DatabaseConnection.insertDataLivre(3,"JEAN MARC","Auteur");
            DatabaseConnection.insertDataEmprunt("soares.flavio2002@gmail.com",3,"2004-06-02","2005-02-03");
        }
        catch(SQLException e){
            System.out.println("Utilisateur deja existant");
        }
            // Lancer l'application JavaFX en appelant la méthode launch()
            Application.launch(ConnexionController.class, args);
            BookSearch bookSearch = new BookSearch();
        //bookSearch.search("Harry Potter");
    }

}
