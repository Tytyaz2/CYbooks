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
import javafx.scene.control.Button;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import main.models.*;
import javafx.scene.paint.Color;
import javafx.scene.control.TableCell;



import java.awt.*;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Objects;

public class MainControllers {
    public Button showLateBooksButton;
    private int startIndex = 0;
    private final int pageSize = 20;

    @FXML
    private TableView<Book> bookTableView;
    @FXML
    private TextArea SearchBook;
    @FXML
    private TextArea SearchUser;

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
            String query = "SELECT * FROM utilisateur WHERE statut != 3";
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

    public void initialize() throws SQLException, InterruptedException {
        // Appeler la méthode pour charger les livres populaires
        loadTop20PopularBooksLastMonth();
        checkAndUpdateUserStatus();
        // Charger les données dans TableView
        loadData();

        // Définir les usines de valeurs de cellule personnalisées pour chaque colonne
        nom.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getNom()));
        prenom.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getPrenom()));
        email.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));

        // Appliquer les usines de cellules personnalisées pour changer la couleur en fonction du statut
        nom.setCellFactory(column -> new TableCell<Utilisateur, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    Utilisateur user = getTableView().getItems().get(getIndex());
                    if (user.getStatut() == 1) {
                        setTextFill(Color.RED);
                    } else if (user.getStatut() == 2) {
                        setTextFill(Color.ORANGE);
                    } else if (user.getStatut() == 0) {
                        setTextFill(Color.BLACK);
                    } else {
                        setTextFill(Color.BLACK); // Couleur par défaut si le statut n'est pas 0, 1 ou 2
                    }
                }
            }
        });

        prenom.setCellFactory(column -> new TableCell<Utilisateur, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    Utilisateur user = getTableView().getItems().get(getIndex());
                    if (user.getStatut() == 1) {
                        setTextFill(Color.RED);
                    } else if (user.getStatut() == 2) {
                        setTextFill(Color.ORANGE);
                    } else if (user.getStatut() == 0) {
                        setTextFill(Color.BLACK);
                    } else {
                        setTextFill(Color.BLACK); // Couleur par défaut si le statut n'est pas 0, 1 ou 2
                    }
                }
            }
        });

        email.setCellFactory(column -> new TableCell<Utilisateur, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    Utilisateur user = getTableView().getItems().get(getIndex());
                    if (user.getStatut() == 1) {
                        setTextFill(Color.RED);
                    } else if (user.getStatut() == 2) {
                        setTextFill(Color.ORANGE);
                    } else if (user.getStatut() == 0) {
                        setTextFill(Color.BLACK);
                    } else {
                        setTextFill(Color.BLACK); // Couleur par défaut si le statut n'est pas 0, 1 ou 2
                    }
                }
            }
        });

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

    // This method will be called when you want to show the Late Loans view
    public void showLateLoansView() {
        try {
            // Load the FXML file
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/views/lateBorrow.fxml"));
            Parent root = loader.load();

            // Create a new scene with the loaded FXML
            Scene scene = new Scene(root);

            // Create a new stage (window) and set the scene
            Stage stage = new Stage();
            stage.setTitle("Late Loans");
            stage.setScene(scene);

            // Show the new stage
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception, maybe show an alert to the user
        }
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


            // Obtenir le conteneur racine de la scène actuelle et le convertir en AnchorPane
            AnchorPane root = (AnchorPane) ((Node) event.getSource()).getScene().getRoot();
            // Accéder à la liste des enfants du conteneur racine et remplacer les enfants par ceux de la nouvelle vue
            root.getChildren().setAll(empruntRoot);

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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/views/newAdherent.fxml"));
            Parent root = loader.load();
            NewAdherentController controller = loader.getController();
            controller.setMainController(this);
            Stage stage = (Stage) bookTableView.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    private void checkAndUpdateUserStatus() {
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    LocalDate currentDate = LocalDate.now();
                    List<Utilisateur> users = DatabaseConnection.getAllUtilisateur();

                    for (Utilisateur user : users) {
                        boolean hasOverdueLoans = user.hasOverdueLoans(currentDate);
                        int maxEmprunt = DatabaseConnection.getUserMaxEmprunt(user.getEmail());
                        int lateCount = DatabaseConnection.getUserLateCount(user.getEmail());

                        if (user.getStatut() != 3) {
                            if (lateCount >= 3) {
                                if (user.getStatut() != 3) {
                                    user.setStatut(3);
                                    DatabaseConnection.updateUserStatus(user.getEmail(), 3);
                                }
                            } else if (hasOverdueLoans) {
                                if (user.getStatut() != 1) {
                                    user.setStatut(1);
                                    DatabaseConnection.updateUserStatus(user.getEmail(), 1);
                                }
                            } else if (maxEmprunt == 0) {
                                if (user.getStatut() != 2) {
                                    user.setStatut(2);
                                    DatabaseConnection.updateUserStatus(user.getEmail(), 2);
                                }
                            } else {
                                if (user.getStatut() != 0) {
                                    user.setStatut(0);
                                    DatabaseConnection.updateUserStatus(user.getEmail(), 0);
                                }
                            }
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 24 * 60 * 60 * 1000); // Exécuter la tâche tous les jours
    }






    private void loadTop20PopularBooksLastMonth() {
        try {
            // Appeler la méthode pour obtenir les 20 livres les plus populaires du mois dernier
            List<Book> popularBooks = DatabaseConnection.getTop20PopularBooksLast30Days();

            // Mettre à jour la TableView avec les données des livres populaires
            bookTableView.getItems().setAll(popularBooks);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    public void handleSearchUser(ActionEvent actionEvent) throws SQLException {
        List<Utilisateur> data = new ArrayList<>();
        String searchPattern = "%" + SearchUser.getText() + "%";
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
                String query = "SELECT * FROM utilisateur WHERE (nom LIKE ? OR prenom LIKE ? OR email LIKE ?) AND statut != 3";
                System.out.println("Exécution de la requête : " + query);
                preparedStatement = connection.prepareStatement(query);

                // Définir les valeurs des paramètres de substitution
                preparedStatement.setString(1, searchPattern); // Pour le nom
                preparedStatement.setString(2, searchPattern); // Pour le prénom
                preparedStatement.setString(3, searchPattern); // Pour l'email

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
    }


    public void refreshStatut() throws SQLException {
        // Effacez les données actuelles de la table
        userTableView.getItems().clear();

        // Rechargez les données depuis la base de données et ajoutez-les à la table
        loadData();
    }

}
