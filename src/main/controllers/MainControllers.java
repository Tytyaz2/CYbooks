package main.controllers;

import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;
import main.models.DatabaseConnection;
import main.models.Utilisateur;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainControllers {

    @FXML
    private TableView<Utilisateur> userTableView;

    @FXML
    private TableColumn<Utilisateur, String> test1Column;

    @FXML
    private TableColumn<Utilisateur, String> test2Column;

    @FXML
    private TableColumn<Utilisateur, String> test3Column;

    private Utilisateur selectedUser;


    private void loadData() throws SQLException {
        List<Utilisateur> data = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Utiliser la méthode getConnection() de DatabaseConnection
            connection = DatabaseConnection.getConnection();

            // Exécuter la requête pour récupérer les données
            String query = "SELECT * FROM utilisateur";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            // Itérer à travers le jeu de résultats et ajouter les données à la liste
            while (resultSet.next()) {
                Utilisateur model = new Utilisateur(
                        resultSet.getString("email"),
                        resultSet.getString("prenom"),
                        resultSet.getString("nom"),
                        resultSet.getInt("statut"),
                        resultSet.getInt("MaxEmprunt"));
                data.add(model);
            }

            // Peupler TableView avec les données
            userTableView.getItems().addAll(data);

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

    // Méthode pour rafraîchir les données des adhérents
    public void refreshUserData() throws SQLException {
        // Effacez les données actuelles de la table
        userTableView.getItems().clear();

        // Rechargez les données depuis la base de données et ajoutez-les à la table
        // Utilisez une méthode de votre classe DatabaseConnection pour récupérer les données
        List<Utilisateur> userList = DatabaseConnection.getAllUtilisateur();
        userTableView.getItems().addAll(userList);
    }

    @FXML
    public void initialize() throws SQLException {
        // Associer les colonnes du TableView aux propriétés du modèle de données
        test1Column.setCellValueFactory(new PropertyValueFactory<>("nom"));
        test2Column.setCellValueFactory(new PropertyValueFactory<>("prenom"));
        test3Column.setCellValueFactory(new PropertyValueFactory<>("email"));

        // Charger les données dans TableView
        loadData();
    }

    @FXML
    public void handleUserClick(MouseEvent mouseEvent) {
        // Récupérer l'adhérent sélectionné dans la TableView
        selectedUser = userTableView.getSelectionModel().getSelectedItem();

        // Vérifier si un adhérent est sélectionné
        if (selectedUser != null) {
            try {
                // Charge la vue de la page adhérent
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/views/pageadherent.fxml"));
                Parent root = loader.load();

                // Obtient le contrôleur de la page adhérent
                AdherentController adherentController = loader.getController();

                adherentController.afficherDetailsUtilisateur(selectedUser);
                // Crée une nouvelle scène avec la racine chargée
                Scene scene = new Scene(root);

                // Obtient le stage actuel
                Stage stage = (Stage) ((Node) mouseEvent.getSource()).getScene().getWindow();

                // Définit la nouvelle scène sur le stage actuel
                stage.setScene(scene);
                stage.setTitle("Page Adhérent");
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public Utilisateur getSelectedUser() {
        return selectedUser;
    }

    @FXML
    public void handleNewAdherentButtonClick(ActionEvent event) {
        showNewAdherentPage();
    }

    public void showNewAdherentPage() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/views/newAdherent.fxml"));
        Parent root;
        try {
            root = loader.load();
            NewAdherentController controller = loader.getController();
            controller.setMainController(this);
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
