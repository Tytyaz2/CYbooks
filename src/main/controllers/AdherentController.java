package main.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import main.models.Utilisateur;
import main.models.DatabaseConnection;
import main.models.Book;

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
    private TableView<Book> livresTableView;

    @FXML
    private TableColumn<Book, String> titreColumn;

    @FXML
    private TableColumn<Book, String> auteurColumn;

    @FXML
    private TableColumn<Book, String> isbnColumn;

    @FXML
    private TableColumn<Book, String> dateEmprunt;

    @FXML
    private TableColumn<Book, String> dateRendu;

    private ObservableList<Book> listeEmprunts = FXCollections.observableArrayList();


    public void afficherDetailsUtilisateur(Utilisateur utilisateur) {
        if (utilisateur != null) {
            user = utilisateur;
            // Définir les PropertyValueFactory pour les colonnes de la TableView
            titreColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
            auteurColumn.setCellValueFactory(new PropertyValueFactory<>("authors"));
            isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
            dateEmprunt.setCellValueFactory(new PropertyValueFactory<>("dateBorrow"));
            dateRendu.setCellValueFactory(new PropertyValueFactory<>("dateGB"));

            // Afficher les détails de l'utilisateur dans les labels correspondants
            nomLabel.setText(utilisateur.getNom());
            prenomLabel.setText(utilisateur.getPrenom());
            mailLabel.setText(utilisateur.getEmail());

            // Récupérer le nombre maximum d'emprunts depuis la base de données
            int maxEmprunt = 5 - getMaxEmpruntFromDatabase(utilisateur.getEmail()); // Modification : récupérer le nombre maximum d'emprunts
            nbEmpruntsLabel.setText(String.valueOf(maxEmprunt)); // Afficher le nombre maximum d'emprunts

            // Charger les livres empruntés par cet utilisateur
            chargerLivresEmpruntes(utilisateur.getEmail());
        } else {
            // Effacer les labels si aucun utilisateur n'est sélectionné
            nomLabel.setText("");
            prenomLabel.setText("");
            mailLabel.setText("");
            nbEmpruntsLabel.setText("");
        }
    }

    private int getMaxEmpruntFromDatabase(String email) {
        int maxEmprunt = 0;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Obtenez une connexion à la base de données
            connection = DatabaseConnection.getConnection();

            // Exécuter la requête pour récupérer le nombre maximum d'emprunts de l'utilisateur
            String query = "SELECT MaxEmprunt FROM Utilisateur WHERE email = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            resultSet = preparedStatement.executeQuery();

            // Si une ligne est retournée, récupérez le nombre maximum d'emprunts
            if (resultSet.next()) {
                maxEmprunt = resultSet.getInt("MaxEmprunt");
            }
        } catch (SQLException e) {
            e.printStackTrace(); // Gérer les erreurs de manière appropriée
        } finally {
            // Fermer les ressources
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                // Ne fermez pas la connexion ici, car elle est réutilisée
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return maxEmprunt;
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
            PreparedStatement preparedStatementUtilisateur = null;
            PreparedStatement preparedStatementEmprunt = null;
            try {
                // Obtenez une connexion à la base de données
                connection = DatabaseConnection.getConnection();
                connection.setAutoCommit(false); // Début de la transaction

                // 1. Mettre à jour les informations dans la table Utilisateur
                String updateUtilisateurQuery = "UPDATE Utilisateur SET email=?, prenom=?, nom=? WHERE email=?";
                preparedStatementUtilisateur = connection.prepareStatement(updateUtilisateurQuery);
                preparedStatementUtilisateur.setString(1, nouvelEmail);
                preparedStatementUtilisateur.setString(2, nouveauPrenom);
                preparedStatementUtilisateur.setString(3, nouveauNom);
                preparedStatementUtilisateur.setString(4, ancienEmail);
                preparedStatementUtilisateur.executeUpdate();

                // 2. Mettre à jour les informations dans la table Emprunt où l'e-mail de l'utilisateur est utilisé
                String updateEmpruntQuery = "UPDATE Emprunt SET user_email=? WHERE user_email=?";
                preparedStatementEmprunt = connection.prepareStatement(updateEmpruntQuery);
                preparedStatementEmprunt.setString(1, nouvelEmail);
                preparedStatementEmprunt.setString(2, ancienEmail);
                preparedStatementEmprunt.executeUpdate();

                // 3. Mettre à jour les informations dans la table Emprunt où l'adresse e-mail de l'utilisateur est référencée comme une clé étrangère
                // Cette mise à jour n'est pas nécessaire car les clés étrangères sont mises à jour automatiquement lorsque les clés primaires correspondantes dans la table Utilisateur sont modifiées.

                connection.commit(); // Valider la transaction

                // Mise à jour réussie
                System.out.println("Utilisateur et Emprunt mis à jour avec succès !");
                // Mettre à jour les Label avec les nouvelles valeurs des TextArea
                nomLabel.setText(nomTextArea.getText());
                prenomLabel.setText(prenomTextArea.getText());
                mailLabel.setText(mailTextArea.getText());
            } catch (SQLException e) {
                // En cas d'erreur, annuler la transaction
                try {
                    if (connection != null) {
                        connection.rollback();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("ERREUR");
                alert.setHeaderText(null);
                alert.setContentText("Une erreur s'est produite lors de la mise à jour de l'utilisateur.");
                alert.showAndWait();
                e.printStackTrace();
            } finally {
                // Fermez les ressources
                try {
                    if (preparedStatementUtilisateur != null) preparedStatementUtilisateur.close();
                    if (preparedStatementEmprunt != null) preparedStatementEmprunt.close();
                    if (connection != null) {
                        connection.setAutoCommit(true); // Rétablir le mode de commutation automatique
                        connection.close();
                    }
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

        livresTableView.setItems(listeEmprunts);

        // Définir les PropertyValueFactory pour les colonnes de la TableView
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        auteurColumn.setCellValueFactory(new PropertyValueFactory<>("authors"));
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        dateEmprunt.setCellValueFactory(new PropertyValueFactory<>("dateBorrow"));
        dateRendu.setCellValueFactory(new PropertyValueFactory<>("dateGB"));
    }


    private void chargerLivresEmpruntes(String email) {
        listeEmprunts.clear(); // Inutile, car déjà géré par le rafraîchissement de la TableView
        List<Book> livres = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Obtenez une connexion à la base de données
            connection = DatabaseConnection.getConnection();

            // Exécuter la requête pour récupérer les livres empruntés par l'utilisateur avec les dates d'emprunt et de rendu
            String query = "SELECT Livre.titre, Livre.auteur, Livre.isbn, Emprunt.date_debut, Emprunt.date_fin " +
                    "FROM Emprunt " +
                    "JOIN Livre ON Emprunt.livre_isbn = Livre.isbn " +
                    "WHERE Emprunt.user_email = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            resultSet = preparedStatement.executeQuery();

            // Itérer à travers le jeu de résultats et ajouter les livres à la liste
            while (resultSet.next()) {
                String titre = resultSet.getString("titre");
                String auteur = resultSet.getString("auteur");
                String isbn = resultSet.getString("isbn");
                String dateEmprunt = resultSet.getString("date_debut");
                String dateRendu = resultSet.getString("date_fin");
                Book livre = new Book(titre, auteur, isbn);
                livre.setDateBorrow(dateEmprunt);
                livre.setDateGB(dateRendu);
                livres.add(livre);
            }
            // Peupler le TableView avec les livres empruntés
            listeEmprunts.addAll(livres);

        } catch (SQLException e) {
            e.printStackTrace(); // Gérer les erreurs de manière appropriée
        } finally {
            // Fermer les ressources
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                // Ne fermez pas la connexion ici, car elle est réutilisée
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}
