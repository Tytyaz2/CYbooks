package main.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
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
import main.models.Book;
import main.models.BookSearch;
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
    private int startIndex = 0;
    private final int pageSize = 20;

    @FXML
    private TableView<Book> bookTableView;
    @FXML
    private TextArea SearchBook;
    @FXML

    private TextField searchTextField;
    private BookSearch bookSearch;

    public MainControllers() throws SQLException {
        this.bookSearch = new BookSearch();


    }


    @FXML
    private TableView<Utilisateur> userTableView;

    @FXML
    private TableColumn<Utilisateur, String> nom;

    @FXML
    private TableColumn<Utilisateur, String> prenom;

    @FXML
    private TableColumn<Utilisateur, String> email;

    private Utilisateur selectedUser;

    public void searchAndUpdateTableView(String keyword) {
        List<Book> books = bookSearch.search(keyword, startIndex, pageSize);
        bookTableView.getItems().setAll(books);
    }



    @FXML
    private void loadFirst20Books() {
        String recherche = SearchBook.getText();
        List<Book> books = bookSearch.search(recherche, startIndex, pageSize);
        bookTableView.getItems().setAll(books);
    }
    @FXML
    private void handleNextButtonAction() {
        startIndex += pageSize;
        loadFirst20Books();
    }

    @FXML
    private void handlePreviousButtonAction() {
        startIndex = Math.max(0, startIndex - pageSize);
        loadFirst20Books();
    }




    @FXML
    private void handleSearchButtonAction(ActionEvent event) {
        String keyword = searchTextField.getText();
        searchAndUpdateTableView(keyword);
    }

    private void loadData() throws SQLException {
        List<Utilisateur> data = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Utiliser la méthode getConnection() de DatabaseConnection
            System.out.println("Tentative de connexion à la base de données...");
            connection = DatabaseConnection.getConnection();
            System.out.println("Connexion réussie !");

            // Exécuter la requête pour récupérer les données
            String query = "SELECT * FROM utilisateur";
            System.out.println("Exécution de la requête : " + query);
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
    private RadioButton searchbyauthor;

    @FXML
    private RadioButton searchbytitle;

    @FXML
    private RadioButton searchbyisbn;

    private ToggleGroup searchToggleGroup;


    public void initialize() throws SQLException {
        // Charger les données dans TableView
        loadData();

        // Définir les usines de valeurs de cellule personnalisées pour chaque colonne
        nom.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
        prenom.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrenom()));
        email.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));

        // Créer un groupe de bascule pour les boutons radio de recherche
        searchToggleGroup = new ToggleGroup();

        // Ajouter les boutons radio au groupe de bascule
        searchbyauthor.setToggleGroup(searchToggleGroup);
        searchbytitle.setToggleGroup(searchToggleGroup);
        searchbyisbn.setToggleGroup(searchToggleGroup);

        // Ajouter des écouteurs de changement pour les boutons radio
        searchToggleGroup.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
            if (newToggle == searchbyauthor) {
                // Appeler la méthode de recherche par auteur
                loadBooksByAuthor();
            } else if (newToggle == searchbytitle) {
                // Appeler la méthode de recherche par titre
                loadBooksByTitle();
            } else if (newToggle == searchbyisbn) {
                // Appeler la méthode de recherche par ISBN
                loadBooksByISBN();
            }
        });
    }




    private void loadBooksByAuthor() {
        String author = SearchBook.getText();
        List<Book> books = bookSearch.searchByAuthor(author, startIndex, pageSize);
        bookTableView.getItems().setAll(books);
    }

    private void loadBooksByTitle() {
        String title = SearchBook.getText();
        List<Book> books = bookSearch.searchByTitle(title, startIndex, pageSize);
        bookTableView.getItems().setAll(books);
    }

    private void loadBooksByISBN() {
        String isbn = SearchBook.getText();
        List<Book> books = bookSearch.searchByISBN(isbn, startIndex, pageSize);
        bookTableView.getItems().setAll(books);
    }


    @FXML
    public void handleUserClick(MouseEvent mouseEvent) {
        // Récupérer l'adhérent sélectionné dans la TableView
        selectedUser = userTableView.getSelectionModel().getSelectedItem();

        // Vérifier si un adhérent est sélectionné
        if (selectedUser != null) {
            // Appelle la méthode pour afficher la page adhérent dans une nouvelle scène
            showAdherentPage();
        }
    }



    public Utilisateur getSelectedUser() {
        return selectedUser;
    }

    @FXML
    public void handleNewAdherentButtonClick(ActionEvent event) {
        showNewAdherentPage();
    }

    @FXML
    private Button empruntButton; // Assurez-vous d'annoter avec @FXML

    @FXML
    void handleNewBorrowButtonClick(ActionEvent event) {
        try {
            // Charger le fichier FXML de la scène d'emprunt
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/views/emprunt.fxml"));
            Parent empruntRoot = loader.load();

            // Créer une nouvelle scène
            Scene empruntScene = new Scene(empruntRoot);

            // Créer une nouvelle fenêtre pour la scène d'emprunt
            Stage empruntStage = new Stage();
            empruntStage.setScene(empruntScene);
            empruntStage.setTitle("Emprunt");

            // Afficher la nouvelle fenêtre
            empruntStage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public void showAdherentPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/views/pageadherent.fxml"));
            Parent root = loader.load();
            AdherentController adherentController = loader.getController();
            adherentController.afficherDetailsUtilisateur(selectedUser);
            Stage stage = (Stage) bookTableView.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
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
            e.getMessage();
        }
    }


}
