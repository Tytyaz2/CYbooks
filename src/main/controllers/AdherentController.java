package main.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import main.models.Utilisateur;
import main.models.DatabaseConnection;
import main.models.Livre;

import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;

public class AdherentController {

    private Utilisateur user;
    @FXML
    private Label nomLabel;

    @FXML
    private Label prenomLabel;

    @FXML
    private Label mailLabel;

    @FXML
    private Label nbEmpruntsLabel;

    @FXML
    private TextArea nomTextArea;

    @FXML
    private TextArea prenomTextArea;

    @FXML
    private TextArea mailTextArea;

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
    public void afficherDetailsUtilisateur(Utilisateur utilisateur) {

        if (utilisateur != null) {
            user = utilisateur;
            titreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
           auteurColumn.setCellValueFactory(new PropertyValueFactory<>("auteur"));
            isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
            // Afficher les détails de l'utilisateur dans les labels correspondants
            nomLabel.setText(utilisateur.getNom());
            prenomLabel.setText(utilisateur.getPrenom());
            mailLabel.setText(utilisateur.getEmail());

            // Charger les livres empruntés par cet utilisateur
            //chargerLivresEmpruntes(utilisateur.getId());
        } else {
            // Effacer les labels si aucun utilisateur n'est sélectionné
            nomLabel.setText("");
            prenomLabel.setText("");
            mailLabel.setText("");
            nbEmpruntsLabel.setText("");
        }
    }

    @FXML
    public void modifierAdherent(ActionEvent actionEvent) {
        if (nomLabel.isVisible()) {
            // Afficher les TextArea et masquer les Label
            nomLabel.setVisible(false);
            prenomLabel.setVisible(false);
            mailLabel.setVisible(false);
            nomTextArea.setVisible(true);
            prenomTextArea.setVisible(true);
            mailTextArea.setVisible(true);

            // Remplir les TextArea avec les valeurs actuelles des Label
            nomTextArea.setText(nomLabel.getText());
            prenomTextArea.setText(prenomLabel.getText());
            mailTextArea.setText(mailLabel.getText());
        } else {
            // Masquer les TextArea et afficher les Label
            nomLabel.setVisible(true);
            prenomLabel.setVisible(true);
            mailLabel.setVisible(true);
            nomTextArea.setVisible(false);
            prenomTextArea.setVisible(false);
            mailTextArea.setVisible(false);



            // Récupérer les nouvelles valeurs depuis les TextAreas
            String nouveauNom = nomTextArea.getText();
            String nouveauPrenom = prenomTextArea.getText();
            String nouvelEmail = mailTextArea.getText();

            // Récupérer l'adresse e-mail de l'utilisateur que vous souhaitez modifier
            String ancienEmail = user.getEmail(); // Vous devez avoir une méthode getEmail() dans votre classe Utilisateur

            Connection connection = null;
            PreparedStatement preparedStatement = null;
            try {
                // Obtenez une connexion à la base de données
                connection = DatabaseConnection.getConnection();

                // Créez votre instruction SQL UPDATE
                String query = "UPDATE Utilisateur SET email=?, prenom=?, nom=? WHERE email=?";

                // Créez un objet PreparedStatement et passez les valeurs nécessaires
                preparedStatement = connection.prepareStatement(query);
                preparedStatement.setString(1, nouvelEmail);
                preparedStatement.setString(2, nouveauPrenom);
                preparedStatement.setString(3, nouveauNom);
                preparedStatement.setString(4, ancienEmail);

                // Exécutez la mise à jour
                int rowsAffected = preparedStatement.executeUpdate();

                if (rowsAffected > 0) {
                    // Mise à jour réussie
                    System.out.println("Utilisateur mis à jour avec succès !");
                    // Mettre à jour les Label avec les nouvelles valeurs des TextArea
                    nomLabel.setText(nomTextArea.getText());
                    prenomLabel.setText(prenomTextArea.getText());
                    mailLabel.setText(mailTextArea.getText());
                } else {
                    // Aucune ligne affectée, échec de la mise à jour
                    System.out.println("Échec de la mise à jour de l'utilisateur !");
                }


            } catch (SQLException e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERREUR");
                alert.setHeaderText(null);
                alert.setContentText("Cet email existe deja.");
                alert.showAndWait();
                System.out.println("cet email existe deja.");
            } finally {
                // Fermez les ressources
                try {
                    if (preparedStatement != null) preparedStatement.close();
                    if (connection != null) connection.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    public void initialize() {
            // Masquer les TextArea et afficher les Label par défaut
            nomTextArea.setVisible(false);
            prenomTextArea.setVisible(false);
            mailTextArea.setVisible(false);

            nomLabel.setVisible(true);
            prenomLabel.setVisible(true);
            mailLabel.setVisible(true);
        }

/*
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
            String query = "SELECT * FROM Emprunt WHERE utilisateur_id = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, userId);
            resultSet = preparedStatement.executeQuery();

            // Itérer à travers le jeu de résultats et ajouter les livres à la liste
            while (resultSet.next()) {
                Livre livre = new Livre(
                        resultSet.getString("titre"),
                        resultSet.getString("auteur"),
                        resultSet.getString("isbn"));
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
*/


}
