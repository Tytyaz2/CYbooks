package main.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import main.models.Utilisateur;
import main.models.DatabaseConnection;
import main.models.Book;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

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

    private ObservableList<Book> listeEmprunts;

    private final ObservableList<Book> livresSelectionnes = FXCollections.observableArrayList();

    public void afficherDetailsUtilisateur(Utilisateur utilisateur) {
        if (utilisateur != null) {
            user = utilisateur;

            titreColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
            auteurColumn.setCellValueFactory(new PropertyValueFactory<>("authors"));
            isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
            dateEmprunt.setCellValueFactory(new PropertyValueFactory<>("dateBorrow"));
            dateRendu.setCellValueFactory(new PropertyValueFactory<>("dateGB"));

            nomLabel.setText(utilisateur.getNom());
            prenomLabel.setText(utilisateur.getPrenom());
            mailLabel.setText(utilisateur.getEmail());

            int maxEmprunt = 5 - getMaxEmpruntFromDatabase(utilisateur.getEmail());
            nbEmpruntsLabel.setText(String.valueOf(maxEmprunt));

            chargerLivresEmpruntes(utilisateur.getEmail());
        } else {
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
            connection = DatabaseConnection.getConnection();

            String query = "SELECT MaxEmprunt FROM Utilisateur WHERE email = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                maxEmprunt = resultSet.getInt("MaxEmprunt");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Fermeture des ressources...
        }

        return maxEmprunt;
    }

    @FXML
    public void initialize() {
        nomTextArea.setVisible(false);
        prenomTextArea.setVisible(false);
        mailTextArea.setVisible(false);

        nomLabel.setVisible(true);
        prenomLabel.setVisible(true);
        mailLabel.setVisible(true);

        livresTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        listeEmprunts = livresTableView.getItems();

        titreColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        auteurColumn.setCellValueFactory(new PropertyValueFactory<>("authors"));
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        dateEmprunt.setCellValueFactory(new PropertyValueFactory<>("dateBorrow"));
        dateRendu.setCellValueFactory(new PropertyValueFactory<>("dateGB"));

        // Gestion de la sélection des livres dans la TableView
        livresTableView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                handleBookSelection();
            }
        });
    }

    @FXML
    public void modifierAdherent(MouseEvent actionEvent) {
        if (nomLabel.isVisible()) {
            nomLabel.setVisible(false);
            prenomLabel.setVisible(false);
            mailLabel.setVisible(false);
            nomTextArea.setVisible(true);
            prenomTextArea.setVisible(true);
            mailTextArea.setVisible(true);

            nomTextArea.setText(nomLabel.getText());
            prenomTextArea.setText(prenomLabel.getText());
            mailTextArea.setText(mailLabel.getText());
        } else {
            nomLabel.setVisible(true);
            prenomLabel.setVisible(true);
            mailLabel.setVisible(true);
            nomTextArea.setVisible(false);
            prenomTextArea.setVisible(false);
            mailTextArea.setVisible(false);

            String nouveauNom = nomTextArea.getText();
            String nouveauPrenom = prenomTextArea.getText();
            String nouvelEmail = mailTextArea.getText();

            if (!Utilisateur.isValidEmail(nouvelEmail)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur de validation");
                alert.setHeaderText(null);
                alert.setContentText("Veuillez entrer une adresse email valide.");
                alert.showAndWait();
                return;
            }

            String ancienEmail = user.getEmail();
            int statut = user.getStatut();  // Assuming `statut` is part of your user object
            int maxEmprunt = user.getMaxEmprunt();  // Assuming `maxEmprunt` is part of your user object

            Connection connection = null;
            PreparedStatement insertNewUserStatement = null;
            PreparedStatement updateEmpruntStatement = null;
            PreparedStatement updateHistoriqueStatement = null;
            PreparedStatement deleteUserStatement = null;

            try {
                connection = DatabaseConnection.getConnection();
                connection.setAutoCommit(false);  // Commence une transaction

                // Insérer le nouvel utilisateur
                String insertNewUserQuery = "INSERT INTO Utilisateur (email, nom, prenom, statut, MaxEmprunt) VALUES (?, ?, ?, ?, ?)";
                insertNewUserStatement = connection.prepareStatement(insertNewUserQuery);
                insertNewUserStatement.setString(1, nouvelEmail);
                insertNewUserStatement.setString(2, nouveauNom);
                insertNewUserStatement.setString(3, nouveauPrenom);
                insertNewUserStatement.setInt(4, statut);
                insertNewUserStatement.setInt(5, maxEmprunt);
                insertNewUserStatement.executeUpdate();

                // Mettre à jour les emprunts pour utiliser le nouvel email
                String updateEmpruntQuery = "UPDATE Emprunt SET user_email = ? WHERE user_email = ?";
                updateEmpruntStatement = connection.prepareStatement(updateEmpruntQuery);
                updateEmpruntStatement.setString(1, nouvelEmail);
                updateEmpruntStatement.setString(2, ancienEmail);
                updateEmpruntStatement.executeUpdate();

                // Mettre à jour l'historique pour utiliser le nouvel email
                String updateHistoriqueQuery = "UPDATE Historique SET user_email = ? WHERE user_email = ?";
                updateHistoriqueStatement = connection.prepareStatement(updateHistoriqueQuery);
                updateHistoriqueStatement.setString(1, nouvelEmail);
                updateHistoriqueStatement.setString(2, ancienEmail);
                updateHistoriqueStatement.executeUpdate();

                // Supprimer l'ancien utilisateur
                String deleteUserQuery = "DELETE FROM Utilisateur WHERE email = ?";
                deleteUserStatement = connection.prepareStatement(deleteUserQuery);
                deleteUserStatement.setString(1, ancienEmail);
                deleteUserStatement.executeUpdate();

                connection.commit();  // Confirme la transaction

                nomLabel.setText(nouveauNom);
                prenomLabel.setText(nouveauPrenom);
                mailLabel.setText(nouvelEmail);
                nbEmpruntsLabel.setText(String.valueOf(5 - getMaxEmpruntFromDatabase(nouvelEmail)));

                System.out.println("changement(s) effectué(s)");

            } catch (SQLException e) {
                if (connection != null) {
                    try {
                        connection.rollback();  // Annule la transaction en cas d'erreur
                    } catch (SQLException rollbackEx) {
                        rollbackEx.printStackTrace();
                    }
                }
                System.out.println("Aucun changement apporté");
            } finally {
                try {
                    if (insertNewUserStatement != null) {
                        insertNewUserStatement.close();
                    }
                    if (updateEmpruntStatement != null) {
                        updateEmpruntStatement.close();
                    }
                    if (deleteUserStatement != null) {
                        deleteUserStatement.close();
                    }
                    if (connection != null) {
                        connection.setAutoCommit(true);  // Réactive l'auto-commit
                        connection.close();
                    }
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
        }
    }


    @FXML
    public void rendreLivre() {
        if (!livresSelectionnes.isEmpty()) {
            // Afficher la liste des livres sélectionnés dans la console
            System.out.println("Livres sélectionnés :");
            for (Book livre : livresSelectionnes) {
                System.out.println("- Titre : " + livre.getTitle() + ", Auteur(s) : " + livre.getAuthors() + ", ISBN : " + livre.getIsbn());

                // Afficher un message pour chaque livre ajouté à la liste
                System.out.println("Livre ajouté : " + livre.getTitle());
            }

            rendreLivresSelectionnes(livresSelectionnes);
        }else {
            // Afficher un message à l'utilisateur ou effectuer une autre action appropriée
            System.out.println("Aucun livre sélectionné.");
        }
    }

    private void rendreLivresSelectionnes(List<Book> livresARendre) {
        Connection connection = null;
        PreparedStatement updateEmpruntStatement = null;

        // reste de la méthode rendreLivresSelectionnes
        PreparedStatement updateLivreStatement = null;
        PreparedStatement insertHistoriqueStatement = null;
        PreparedStatement deleteEmpruntStatement = null;

        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            String updateEmpruntQuery = "UPDATE Emprunt SET date_fin = ? WHERE livre_isbn = ?";
            updateEmpruntStatement = connection.prepareStatement(updateEmpruntQuery);

            String updateLivreQuery = "UPDATE Livre SET stock = stock + 1 WHERE isbn = ?";
            updateLivreStatement = connection.prepareStatement(updateLivreQuery);

            String insertHistoriqueQuery = "INSERT INTO Historique (livre_isbn, user_email, date_debut, date_fin, retard) VALUES (?, ?, ?, ?, ?)";
            insertHistoriqueStatement = connection.prepareStatement(insertHistoriqueQuery);

            String deleteEmpruntQuery = "DELETE FROM Emprunt WHERE livre_isbn = ?";
            deleteEmpruntStatement = connection.prepareStatement(deleteEmpruntQuery);

            for (Book livre : livresARendre) {
                // Suppression de l'emprunt de la table Emprunt
                deleteEmpruntStatement.setString(1, livre.getIsbn());
                deleteEmpruntStatement.executeUpdate();

                // Mise à jour de la date de fin de l'emprunt
                updateEmpruntStatement.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
                updateEmpruntStatement.setString(2, livre.getIsbn());
                updateEmpruntStatement.executeUpdate();

                // Mise à jour du stock du livre
                updateLivreStatement.setString(1, livre.getIsbn());
                updateLivreStatement.executeUpdate();

                DatabaseConnection.updateUserMaxEmprunt(user.getEmail(), DatabaseConnection.getUserMaxEmprunt(user.getEmail()) + 1);

                // Calcul du retard
                LocalDate dateRetour = LocalDate.parse(livre.getDateGB());
                boolean retard = false;
                if (dateRetour != null && LocalDate.now().isAfter(dateRetour)) {
                    retard = true;
                }

                // Ajout des informations de l'emprunt dans la table Historique
                insertHistoriqueStatement.setString(1, livre.getIsbn());
                insertHistoriqueStatement.setString(2, user.getEmail());
                insertHistoriqueStatement.setString(3, livre.getDateBorrow());
                insertHistoriqueStatement.setString(4, LocalDate.now().toString());
                insertHistoriqueStatement.setBoolean(5, retard);
                insertHistoriqueStatement.executeUpdate();
            }

            connection.commit();
            listeEmprunts.removeAll(livresARendre);
            nbEmpruntsLabel.setText(String.valueOf(5 - getMaxEmpruntFromDatabase(user.getEmail())));

        } catch (SQLException e) {
            if (connection != null) {
                try {
                    connection.rollback();
                } catch (SQLException rollbackException) {
                    rollbackException.printStackTrace();
                }
            }
            e.printStackTrace();
        } finally {
            try {
                if (updateEmpruntStatement != null) updateEmpruntStatement.close();
                if (updateLivreStatement != null) updateLivreStatement.close();
                if (insertHistoriqueStatement != null) insertHistoriqueStatement.close();
                if (deleteEmpruntStatement != null) deleteEmpruntStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    private void chargerLivresEmpruntes(String email) {
        listeEmprunts = FXCollections.observableArrayList();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();

            String query = "SELECT L.titre, L.auteur, L.isbn, E.date_debut, E.date_fin " +
                    "FROM Livre L " +
                    "INNER JOIN Emprunt E ON L.isbn = E.livre_isbn " +
                    "WHERE E.user_email = ? ";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String titre = resultSet.getString("titre");
                String auteur = resultSet.getString("auteur");
                String isbn = resultSet.getString("isbn");
                String dateEmprunt = resultSet.getDate("date_debut").toString();
                LocalDate dateRetour = resultSet.getDate("date_debut").toLocalDate().plusDays(30);
                listeEmprunts.add(new Book(titre, auteur, isbn, dateEmprunt, dateRetour.toString()));
            }

            livresTableView.setItems(listeEmprunts);
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    // Méthode pour gérer la sélection des livres dans la TableView
    @FXML
    private void handleBookSelection() {
        livresSelectionnes.clear();
        livresSelectionnes.addAll(livresTableView.getSelectionModel().getSelectedItems());
    }
}
