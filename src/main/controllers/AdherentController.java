package main.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import main.models.DataUserModel;
import main.models.DatabaseConnection;
import main.models.Livre;

import javax.xml.crypto.Data;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AdherentController {

    private DataUserModel user;
    @FXML
    private Label nomLabel;

    @FXML
    private Label prenomLabel;

    @FXML
    private Label mailLabel;

    @FXML
    private Label nbEmpruntsLabel;

    @FXML
    private TableView<Livre> livresTableView;

    @FXML
    private TableColumn<Livre, String> titreColumn;

    @FXML
    private TableColumn<Livre, String> auteurColumn;

    @FXML
    private TableColumn<Livre, String> isbnColumn;

    @FXML
    private TableColumn<Livre, String> dateEmpruntColumn;

    @FXML
    private TableColumn<Livre, String> dateRenduColumn;

    // Méthode pour afficher les détails de l'utilisateur et les livres empruntés
    public void afficherDetailsUtilisateur(DataUserModel utilisateur) {

        if (utilisateur != null) {
            user = utilisateur;
            titreColumn.setCellValueFactory(cellData -> cellData.getValue().titreProperty());
            auteurColumn.setCellValueFactory(cellData -> cellData.getValue().auteurProperty());
            isbnColumn.setCellValueFactory(cellData -> cellData.getValue().isbnProperty());
            dateEmpruntColumn.setCellValueFactory(cellData -> cellData.getValue().dateEmpruntProperty());
            dateRenduColumn.setCellValueFactory(cellData -> cellData.getValue().dateRenduProperty());
            // Afficher les détails de l'utilisateur dans les labels correspondants
            nomLabel.setText(utilisateur.getNom());
            prenomLabel.setText(utilisateur.getPrenom());
            mailLabel.setText(utilisateur.getEmail());

            // Charger les livres empruntés par cet utilisateur
            chargerLivresEmpruntes(utilisateur.getId());
        } else {
            // Effacer les labels si aucun utilisateur n'est sélectionné
            nomLabel.setText("");
            prenomLabel.setText("");
            mailLabel.setText("");
            nbEmpruntsLabel.setText("");
        }
    }

    // Méthode pour charger les livres empruntés par un utilisateur à partir de la base de données
    private void chargerLivresEmpruntes(int userId) {
        // Effacer les éléments actuels de la TableView
        livresTableView.getItems().clear();

        List<Livre> livres = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Utiliser la méthode getConnection() de DatabaseConnection pour obtenir la connexion
            connection = DatabaseConnection.getConnection();

            // Exécuter la requête pour récupérer les livres empruntés par l'utilisateur
            String query = "SELECT * FROM utilisateurs WHERE id = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            // Itérer à travers le jeu de résultats et ajouter les livres à la liste
            while (resultSet.next()) {
                Livre livre = new Livre(
                        resultSet.getString("livre_titre"),
                        resultSet.getString("livre_auteur"),
                        resultSet.getString("isbn"),
                        resultSet.getString("date_emprunt"),
                        resultSet.getString("date_rendu"));
                livres.add(livre);
            }
            // Peupler le TableView avec les livres empruntés
            livresTableView.getItems().addAll(livres);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Fermer les ressources
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    @FXML
    public void initialize(URL url, ResourceBundle resourceBundle) {

    }

}
