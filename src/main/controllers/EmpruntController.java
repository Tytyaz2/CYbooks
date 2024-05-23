package main.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import main.models.*;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class EmpruntController {



    @FXML
    private TableView<User> userTableView;


    @FXML
    private TextArea textFieldUser;



    @FXML
    private TableColumn<User, String> lastNameColumn ;
    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> firsNameColumn ;




    // Méthode appelée lors du chargement de la vue FXML
    @FXML
    public void initialize() {
        try {
            // Charger les données des utilisateurs
            bookTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

            loadData();

            // Configurer les colonnes pour afficher le nom et le prénom

            emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
            lastNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLastName()));
            firsNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFirstName()));
        } catch (SQLException e) {
            e.printStackTrace(); // Gérer l'exception de manière appropriée
        }
    }

    @FXML
    private TableView<Book> bookTableView;



    // Méthode pour charger les données des utilisateurs depuis la base de données et les afficher dans le TableView
    private void loadData() throws SQLException {
        List<User> data = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Utiliser la méthode getConnection() de DatabaseConnection
            connection = DatabaseConnection.getConnection();

            // Exécuter la requête pour récupérer les données
            String query = "SELECT * FROM User";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            // Itérer à travers le jeu de résultats et ajouter les données à la liste
            while (resultSet.next()) {
                User model = new User(
                        resultSet.getString("email"),
                        resultSet.getString("firstname"),
                        resultSet.getString("lastname"),
                        resultSet.getInt("state"),
                        resultSet.getInt("maxborrow")
                        );
                data.add(model);
            }

            // Peupler TableView avec les données
            userTableView.getItems().addAll(data);

        } finally {
            // Fermer les ressources
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace(); // Gérer l'exception de manière appropriée
            }
        }
    }





    @FXML
    private TextArea bookSearchTextArea;




    @FXML
    private void searchBooks() {
        loadBooksByTitle();
    }

    private void loadBooksByTitle() {
        String title = bookSearchTextArea.getText();
        int startIndex = 1;
        int pageSize = 20;
       List<Book> books = searchbookAPI.search("title",title, startIndex, pageSize);
       bookTableView.getItems().setAll(books);
    }




    @FXML
    private Label selectedUserLabel;

    @FXML
    private ListView<Book> selectedBooksListView;

    private User selectedUser;


    @FXML
    private void handleUserSelection(MouseEvent event) throws SQLException {
        selectedBooksListView.getItems().clear();
        selectedUser = userTableView.getSelectionModel().getSelectedItem();

        if (selectedUser != null) {
            // Vérifier si l'utilisateur est banni ou en retard
            if (selectedUser.getState() == 1) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Statut incorrect");
                alert.setHeaderText("Vous n'êtes pas en position pour emprunter un livre");
                alert.setContentText("Adhérent possédant au moins un livre en retard !");
                alert.showAndWait();
                // Désélectionner l'utilisateur et retourner
                userTableView.getSelectionModel().clearSelection();
                selectedUser = null;
                return;
            } else if (selectedUser.getState() == 3) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Statut incorrect");
                alert.setHeaderText("Vous n'êtes pas en position pour emprunter un livre");
                alert.setContentText("Adhérent banni !");
                alert.showAndWait();
                // Désélectionner l'utilisateur et retourner
                userTableView.getSelectionModel().clearSelection();
                selectedUser = null;
                return;
            }

            selectedUserLabel.setText(selectedUser.getLastName() + " " + selectedUser.getFirstName());

            // Vérifier si l'utilisateur a déjà atteint sa limite d'emprunt maximum
            if (selectedUser.getMaxBorrow() <= 0) {
                // Afficher un message d'alerte
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Limite d'emprunt atteinte");
                alert.setHeaderText("Vous avez déjà atteint votre limite d'emprunt");
                alert.setContentText("Vous ne pouvez pas emprunter plus de livres.");
                alert.showAndWait();
            } else {
                int maxEmprunt = selectedUser.getMaxBorrow();
                // Mettre à jour la liste des livres sélectionnés pour refléter la limite d'emprunt maximum
                selectedBooksListView.getItems().addAll(selectedBooks.subList(0, Math.min(selectedBooks.size(), maxEmprunt)));
            }
        }
    }



    private final ObservableList<Book> selectedBooks = FXCollections.observableArrayList();

    @FXML
    private void handleBookSelection() throws SQLException {
        if (selectedUser != null) {
            int maxBorrow = selectedUser.getMaxBorrow();
            int selectedBooksCount = selectedBooks.size();

            // Vérifier si la limite d'emprunt maximum est dépassée après la sélection
            if (selectedBooksCount >= maxBorrow) {
                // Afficher une alerte si la limite d'emprunt maximum est atteinte
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Limite d'emprunt atteinte");
                alert.setHeaderText("Vous avez déjà atteint la limite d'emprunt");
                alert.setContentText("La limite d'emprunt maximum est de " + maxBorrow + " livres.");
                alert.showAndWait();
                return;
            }

            // Récupérer les livres sélectionnés dans la TableView
            List<Book> selectedBooksFromTable = bookTableView.getSelectionModel().getSelectedItems();

            for (Book selectedBook : selectedBooksFromTable) {
                // Vérifier si la sélection dépasse la limite d'emprunt
                if (selectedBooksCount + 1 > maxBorrow) {
                    // Afficher une alerte si la limite d'emprunt maximum est dépassée
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Limite d'emprunt dépassée");
                    alert.setHeaderText("Vous ne pouvez pas emprunter plus de livres");
                    alert.setContentText("La limite d'emprunt maximum est de " + maxBorrow + " livres. Veuillez sélectionner moins de livres.");
                    alert.showAndWait();
                    return;
                }

                // Vérifier si le livre est déjà dans la liste des livres sélectionnés
                if (!selectedBooks.contains(selectedBook)) {
                    // Ajouter le livre sélectionné à la liste des livres sélectionnés
                    selectedBooks.add(selectedBook);

                    // Mettre à jour l'affichage de la liste des livres sélectionnés
                    selectedBooksListView.setItems(FXCollections.observableArrayList(selectedBooks));

                    // Afficher un message de confirmation
                    System.out.println("Livre ajouté : " + selectedBook.getTitle());

                    // Décrémenter la limite d'emprunt restante
                    maxBorrow--;


                    // Afficher la liste complète des livres sélectionnés
                    System.out.println("Liste complète des livres sélectionnés :");
                    for (Book book : selectedBooks) {
                        System.out.println("- Titre : " + book.getTitle() + ", Auteur(s) : " + book.getAuthors() + ", ISBN : " + book.getIsbn());
                    }
                } else {
                    // Afficher un message d'erreur si le livre est déjà sélectionné
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Livre déjà sélectionné");
                    alert.setHeaderText("Ce livre est déjà sélectionné");
                    alert.setContentText("Veuillez sélectionner un autre livre.");
                    alert.showAndWait();
                }
            }
        }
    }




    @FXML
    private void handleRemoveBook() {
        // Effacer complètement la liste des livres sélectionnés
        selectedBooks.clear();

        // Mettre à jour l'affichage de la liste des livres sélectionnés
        selectedBooksListView.getItems().clear();

        // Afficher un message de succès
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText("Liste de livres retirée avec succès");
        alert.setContentText("La liste des livres sélectionnés a été vidée avec succès.");
        alert.showAndWait();
    }


    @FXML
    private void handleBorrow() throws SQLException {
        User selectedUser = userTableView.getSelectionModel().getSelectedItem();


        // Affichez la liste des livres sélectionnés dans la console
        System.out.println("Livres sélectionnés :");
        for (Book book : selectedBooks) {
            System.out.println("- Titre : " + book.getTitle() + ", Auteur(s) : " + book.getAuthors() + ", ISBN : " + book.getIsbn());
        }

        // Obtenir la date locale
        LocalDate startDate = LocalDate.now(); // Date actuelle

        // Vérifiez si un utilisateur et au moins un livre ont été sélectionnés
        if (selectedUser == null || selectedBooks.isEmpty()) {
            // Affichez un message d'erreur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Sélection requise");
            alert.setContentText("Veuillez sélectionner un utilisateur et au moins un livre.");
            alert.showAndWait();
            return;
        }

        // Vérifier si l'utilisateur a encore des emprunts disponibles
        int remainingBorrows = selectedUser.getMaxBorrow();
        if (remainingBorrows <= 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Limite d'emprunts atteinte");
            alert.setContentText("Vous avez atteint la limite d'emprunts.");
            alert.showAndWait();
            return;
        }

        // Vérifier si le nombre de livres sélectionnés ne dépasse pas le nombre maximal d'emprunts autorisés
        if (selectedBooks.size() > remainingBorrows) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Limite d'emprunts dépassée");
            alert.setContentText("Vous ne pouvez pas emprunter plus de livres que votre limite actuelle d'emprunts.");
            alert.showAndWait();
            return;
        }

        try {
            // Boucle à travers les livres sélectionnés
            for (Book selectedBook : selectedBooks) {

                // Vérifier si le livre existe dans la base de données
                if (!DatabaseConnection.isBookExists(selectedBook.getIsbn())) {
                    // Insérer le livre dans la base de données s'il n'existe pas déjà
                    DatabaseConnection.insertBook(selectedBook);
                }

                // Vérifier si l'utilisateur a déjà emprunté ce livre
                if (DatabaseConnection.isBookAlreadyBorrowed(selectedUser.getEmail(), selectedBook.getIsbn())) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setHeaderText("Livre déjà emprunté");
                    alert.setContentText("Vous avez déjà emprunté le livre \"" + selectedBook.getTitle() + "\".");
                    alert.showAndWait();
                    return;
                }

                // Vérifier si le livre est disponible dans le stock
                int currentStock = DatabaseConnection.getBookStock(selectedBook.getIsbn());
                if (currentStock <= 0) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setHeaderText("Stock épuisé");
                    alert.setContentText("Le livre sélectionné n'est pas disponible dans le stock.");
                    alert.showAndWait();
                    return;
                }

                // La date de fin est fixée à 30 jours après la date de début
                LocalDate endDate = startDate.plusDays(30);

                // Insérer l'emprunt dans la base de données
                DatabaseConnection.insertDataBorrow(selectedUser.getEmail(), selectedBook.getIsbn(), startDate.toString(), endDate.toString());

                // Mettre à jour le stock dans la base de données en réduisant de 1
                int newStock = currentStock - 1;
                selectedBook.setStock(newStock);

                // Mettre à jour le stock dans la base de données
                DatabaseConnection.updateStock(selectedBook.getIsbn(), newStock);

                // Afficher un message de succès
                System.out.println("L'emprunt du livre \"" + selectedBook.getTitle() + "\" a été ajouté avec succès pour l'utilisateur " + selectedUser.getFirstName() + " " + selectedUser.getLastName());

                // Décrémenter le nombre maximal d'emprunts autorisés pour l'utilisateur
                selectedUser.setMaxBorrow(selectedUser.getMaxBorrow() - 1);
            }

            // Afficher un message de succès global après l'emprunt de tous les livres
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Succès");
            alert.setHeaderText("Emprunts ajoutés avec succès");
            alert.setContentText("Les emprunts ont été ajoutés avec succès dans la base de données.");
            alert.showAndWait();

            // Effacer les champs de sélection
            userTableView.getSelectionModel().clearSelection();
            bookTableView.getSelectionModel().clearSelection();


            // Mettre à jour le nombre maximal d'emprunts autorisés pour l'utilisateur dans la base de données
            DatabaseConnection.updateUserMaxBorrow(selectedUser.getEmail(), selectedUser.getMaxBorrow());
        } catch (SQLException e) {
            e.printStackTrace(); // Gérer l'erreur de manière appropriée
            // Afficher un message d'erreur
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur lors de l'ajout des emprunts");
            alert.setContentText("Une erreur s'est produite lors de l'ajout des emprunts dans la base de données.");
            alert.showAndWait();
        }
    }


    @FXML
    private void handleReturnButtonClick(ActionEvent event) {
        try {
            // Charge la vue de la page principale
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/views/pageprincipal.fxml"));
            Parent root = loader.load();

            // Obtient le stage actuel à partir de n'importe quel composant de la scène
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Définit la nouvelle scène avec la racine chargée
            Scene scene = new Scene(root);
            stage.setScene(scene);

            // Optionnel : redéfinir le titre de la fenêtre
            stage.setTitle("Page Principale");

            // Affiche la scène principale
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML

    public void handleSearchUser(ActionEvent actionEvent) throws SQLException {
        List<User> data = new ArrayList<>();

        String searchPattern = textFieldUser.getText();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        userTableView.getItems().clear();
        if (Objects.equals(searchPattern, "")) {
            loadData();
        } else {
            try {
                // Utiliser la méthode getConnection() de DatabaseConnection
                System.out.println("Tentative de connexion à la base de données...");
                connection = DatabaseConnection.getConnection();
                System.out.println("Connexion réussie !");

                // Exécuter la requête pour récupérer les données
                String query = "SELECT * FROM User WHERE lastname LIKE ? OR firstname LIKE ? OR email LIKE ?";
                System.out.println("Exécution de la requête : " + query);
                preparedStatement = connection.prepareStatement(query);

                // Définir les valeurs des paramètres de substitution
                preparedStatement.setString(1, searchPattern); // Pour le nom
                preparedStatement.setString(2, searchPattern); // Pour le prénom
                preparedStatement.setString(3, searchPattern); // Pour l'email

                resultSet = preparedStatement.executeQuery();

                // Itérer à travers le jeu de résultats et ajouter les données à la liste
                while (resultSet.next()) {
                    User model = new User(
                            resultSet.getString("email"),
                            resultSet.getString("firstname"),
                            resultSet.getString("lastname"),
                            resultSet.getInt("state"),
                            resultSet.getInt("maxborrow"));

                    data.add(model);

                }

                System.out.println("Données récupérées avec succès !");

                // Peupler TableView avec les données
                userTableView.getItems().addAll(data);
                System.out.println("Données ajoutées à la TableView avec succès !");

            } catch (SQLException e) {
                System.err.println("Erreur lors de la récupération des données : " + e.getMessage());
            } finally {
                // Fermer les ressources
                try {
                    if (resultSet != null) resultSet.close();
                    if (preparedStatement != null) preparedStatement.close();
                    if (connection != null) connection.close();
                    System.out.println("Ressources fermées avec succès !");
                } catch (SQLException e) {
                    System.err.println("Erreur lors de la fermeture des ressources : " + e.getMessage());
                }
            }
        }
    }


}











