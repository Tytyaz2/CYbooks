package main.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.TableView;
import javafx.scene.input.MouseEvent;
import javafx.scene.control.TableColumn;
import javafx.stage.Stage;
import main.models.DatabaseConnection;
import main.models.DataUserModel;


import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class MainControllers {

    @FXML
    private TableView<DataUserModel> userTableView;

    @FXML
    private TableColumn<DataUserModel, String> test1Column;

    @FXML
    private TableColumn<DataUserModel, String> test2Column;

    @FXML
    private TableColumn<DataUserModel, String> test3Column;

    private DataUserModel selectedUser;


    private void loadData() {
        List<DataUserModel> data = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Utiliser la méthode getConnection() de DatabaseConnection
            connection = DatabaseConnection.getConnection();

            // Exécuter la requête pour récupérer les données
            String query = "SELECT * FROM utilisateurs";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            // Itérer à travers le jeu de résultats et ajouter les données à la liste
            while (resultSet.next()) {
                DataUserModel model = new DataUserModel(
                        resultSet.getInt("id"),
                        resultSet.getString("nom"),
                        resultSet.getString("prenom"),
                        resultSet.getString("email"));
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

    @FXML
    public void initialize() {
        // Associer les colonnes du TableView aux propriétés du modèle de données
        test1Column.setCellValueFactory(cellData -> cellData.getValue().nomProperty());
        test2Column.setCellValueFactory(cellData -> cellData.getValue().prenomProperty());
        test3Column.setCellValueFactory(cellData -> cellData.getValue().emailProperty());

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
    public DataUserModel getSelectedUser() {
        return selectedUser;
    }
}
