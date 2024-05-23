package main.controllers;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.Stage;
import main.models.User;
import main.models.DatabaseConnection;
import main.models.Book;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class AdherentController {

    protected User user;
    @FXML
    protected Button giveBack;
    @FXML
    protected Button history;
    @FXML
    protected Button borrow;
    @FXML
    protected Label lastNameLabel;

    @FXML
    protected Label firstNameLabel;

    @FXML
    protected Label mailLabel;

    @FXML
    protected Label nbBorrowsLabel;
    @FXML
    protected Label historylabel;
    @FXML
    protected Label borrowlabel;

    @FXML
    protected TextArea lastNameTextArea;

    @FXML
    protected TextArea firstNameTextArea;

    @FXML
    protected TextArea mailTextArea;

    @FXML
    protected TableView<Book> bookTableView;

    @FXML
    protected TableColumn<Book, String> titleColumn;

    @FXML
    protected TableColumn<Book, String> authorColumn;

    @FXML
    protected TableColumn<Book, String> isbnColumn;

    @FXML
    protected TableColumn<Book, String> start;

    @FXML
    protected TableColumn<Book, String> end;

    protected ObservableList<Book> borrowList;


    protected final ObservableList<Book> selectedBooks = FXCollections.observableArrayList();

    public void displayUserDetails(User user) {
        if (user != null) {
            this.user = user;

            titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
            authorColumn.setCellValueFactory(new PropertyValueFactory<>("authors"));
            isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
            start.setCellValueFactory(new PropertyValueFactory<>("dateBorrow"));
            end.setCellValueFactory(new PropertyValueFactory<>("dateGB"));

            lastNameLabel.setText(user.getLastName());
            firstNameLabel.setText(user.getFirstName());
            mailLabel.setText(user.getEmail());

            int maxBorrow = 5 - getMaxBorrowFromDatabase(user.getEmail());
            nbBorrowsLabel.setText(String.valueOf(maxBorrow));

            loadBorrowedBooks(user.getEmail());
        } else {
            lastNameLabel.setText("");
            firstNameLabel.setText("");
            mailLabel.setText("");
            nbBorrowsLabel.setText("");
        }
    }

    private int getMaxBorrowFromDatabase(String email) {
        int maxBorrow = 0;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = DatabaseConnection.getConnection();

            String query = "SELECT maxborrow FROM User WHERE email = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                maxBorrow = resultSet.getInt("maxborrow");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Fermeture des ressources...
        }

        return maxBorrow;
    }

    @FXML
    public void initialize() {
        historylabel.setVisible(false);
        borrow.setVisible(false);
        lastNameTextArea.setVisible(false);
        firstNameTextArea.setVisible(false);
        mailTextArea.setVisible(false);

        lastNameLabel.setVisible(true);
        firstNameLabel.setVisible(true);
        mailLabel.setVisible(true);

        bookTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        borrowList = bookTableView.getItems();

        // Gestion de la sélection des livres dans la TableView
        bookTableView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                handleBookSelection();
            }
        });
    }

    @FXML
    public void modifyUser(MouseEvent actionEvent) throws SQLException {
        if (lastNameLabel.isVisible()) {
            historylabel.setVisible(false);
            lastNameLabel.setVisible(false);
            firstNameLabel.setVisible(false);
            mailLabel.setVisible(false);
            lastNameTextArea.setVisible(true);
            firstNameTextArea.setVisible(true);
            mailTextArea.setVisible(true);

            lastNameTextArea.setText(lastNameLabel.getText());
            firstNameTextArea.setText(firstNameLabel.getText());
            mailTextArea.setText(mailLabel.getText());
        } else {
            lastNameLabel.setVisible(true);
            firstNameLabel.setVisible(true);
            mailLabel.setVisible(true);
            lastNameTextArea.setVisible(false);
            firstNameTextArea.setVisible(false);
            mailTextArea.setVisible(false);

            String newLastName = lastNameTextArea.getText();
            String newFirstName = firstNameTextArea.getText();
            String newEmail = mailTextArea.getText();

            if (!User.isValidEmail(newEmail)) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur de validation");
                alert.setHeaderText(null);
                alert.setContentText("Veuillez entrer une adresse email valide.");
                alert.showAndWait();
                return;
            }


            int state = user.getState();  // Assuming `statut` is part of your user object
            int maxBorrow = DatabaseConnection.getUserMaxEmprunt(user.getEmail());  // Assuming `maxEmprunt` is part of your user object
            String previousEmail = user.getEmail();
            user.setEmail(newEmail);

            Connection connection = null;
            PreparedStatement insertNewUserStatement = null;
            PreparedStatement updateBorrowStatement = null;
            PreparedStatement updateHistoryStatement = null;
            PreparedStatement deleteUserStatement = null;

            try {
                connection = DatabaseConnection.getConnection();
                connection.setAutoCommit(false);  // Commence une transaction

                // Insérer le nouvel utilisateur
                String insertNewUserQuery = "INSERT INTO User (email, lastname, firstname, state, maxborrow) VALUES (?, ?, ?, ?, ?)";
                insertNewUserStatement = connection.prepareStatement(insertNewUserQuery);
                insertNewUserStatement.setString(1, newEmail);
                insertNewUserStatement.setString(2, newLastName);
                insertNewUserStatement.setString(3, newFirstName);
                insertNewUserStatement.setInt(4, state);
                insertNewUserStatement.setInt(5, maxBorrow);
                insertNewUserStatement.executeUpdate();

                // Mettre à jour les emprunts pour utiliser le nouvel email
                String updateBorrowQuery = "UPDATE Borrow SET user_email = ? WHERE user_email = ?";
                updateBorrowStatement = connection.prepareStatement(updateBorrowQuery);
                updateBorrowStatement.setString(1, newEmail);
                updateBorrowStatement.setString(2, previousEmail);
                updateBorrowStatement.executeUpdate();

                // Mettre à jour l'historique pour utiliser le nouvel email
                String updateHistoryQuery = "UPDATE History SET user_email = ? WHERE user_email = ?";
                updateHistoryStatement = connection.prepareStatement(updateHistoryQuery);
                updateHistoryStatement.setString(1, newEmail);
                updateHistoryStatement.setString(2, previousEmail);
                updateHistoryStatement.executeUpdate();

                // Supprimer l'ancien utilisateur
                String deleteUserQuery = "DELETE FROM User WHERE email = ?";
                deleteUserStatement = connection.prepareStatement(deleteUserQuery);
                deleteUserStatement.setString(1, previousEmail);
                deleteUserStatement.executeUpdate();

                connection.commit();  // Confirme la transaction

                lastNameLabel.setText(newLastName);
                firstNameLabel.setText(newFirstName);
                mailLabel.setText(newEmail);
                nbBorrowsLabel.setText(String.valueOf(5 - getMaxBorrowFromDatabase(newEmail)));

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
                    if (updateBorrowStatement != null) {
                        updateBorrowStatement.close();
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
    public void giveBackBook() {
        if (!selectedBooks.isEmpty()) {
            // Afficher la liste des livres sélectionnés dans la console
            System.out.println("Livres sélectionnés :");
            for (Book livre : selectedBooks) {
                System.out.println("- Titre : " + livre.getTitle() + ", Auteur(s) : " + livre.getAuthors() + ", ISBN : " + livre.getIsbn());

                // Afficher un message pour chaque livre ajouté à la liste
                System.out.println("Livre ajouté : " + livre.getTitle());
            }

            giveBackSelectedBooks(selectedBooks);
        } else {
            // Afficher un message à l'utilisateur ou effectuer une autre action appropriée
            System.out.println("Aucun livre sélectionné.");
        }
    }
    private void giveBackSelectedBooks(List<Book> books) {
        Connection connection = null;
        PreparedStatement updateBorrowStatement = null;
        PreparedStatement updateBookStatement = null;
        PreparedStatement insertHistoryStatement = null;
        PreparedStatement deleteBorrowStatement = null;

        try {
            connection = DatabaseConnection.getConnection();
            connection.setAutoCommit(false);

            String updateBorrowQuery = "UPDATE Borrow SET end = ? WHERE book_isbn = ? AND user_email = ?";
            updateBorrowStatement = connection.prepareStatement(updateBorrowQuery);

            String updateBookQuery = "UPDATE Book SET stock = stock + 1 WHERE isbn = ?";
            updateBookStatement = connection.prepareStatement(updateBookQuery);

            String insertHistoriqueQuery = "INSERT INTO History (book_isbn, user_email, start, end, delay) VALUES (?, ?, ?, ?, ?)";
            insertHistoryStatement = connection.prepareStatement(insertHistoriqueQuery);

            String deleteEmpruntQuery = "DELETE FROM Borrow WHERE book_isbn = ? AND user_email = ?";
            deleteBorrowStatement = connection.prepareStatement(deleteEmpruntQuery);

            for (Book book : books) {
                // Vérifier si le livre est déjà emprunté par l'utilisateur
                if (DatabaseConnection.isBookAlreadyBorrowed(user.getEmail(), book.getIsbn())) {
                    // Mise à jour de la date de fin de l'emprunt
                    updateBorrowStatement.setDate(1, java.sql.Date.valueOf(LocalDate.now()));
                    updateBorrowStatement.setString(2, book.getIsbn());
                    updateBorrowStatement.setString(3, user.getEmail());
                    updateBorrowStatement.executeUpdate();

                    // Mise à jour du stock du livre
                    updateBookStatement.setString(1, book.getIsbn());
                    updateBookStatement.executeUpdate();

                    // Calcul du retard
                    LocalDate giveBackDate = LocalDate.parse(book.getDateGB());
                    boolean delay = false;
                    if (giveBackDate != null && LocalDate.now().isAfter(giveBackDate)) {
                        delay = true;
                    }

                    // Ajout des informations de l'emprunt dans la table Historique
                    insertHistoryStatement.setString(1, book.getIsbn());
                    insertHistoryStatement.setString(2, user.getEmail());
                    insertHistoryStatement.setString(3, book.getDateBorrow());
                    insertHistoryStatement.setString(4, LocalDate.now().toString());
                    insertHistoryStatement.setBoolean(5, delay);
                    insertHistoryStatement.executeUpdate();

                    // Suppression de l'emprunt de la table Emprunt
                    deleteBorrowStatement.setString(1, book.getIsbn());
                    deleteBorrowStatement.setString(2, user.getEmail());
                    deleteBorrowStatement.executeUpdate();

                    // Mise à jour du nombre maximum d'emprunts de l'utilisateur
                    DatabaseConnection.updateUserMaxBorrow(user.getEmail(), DatabaseConnection.getUserMaxEmprunt(user.getEmail()) + 1);

                    // Mise à jour de l'interface utilisateur
                    borrowList.remove(book);
                    nbBorrowsLabel.setText(String.valueOf(5 - getMaxBorrowFromDatabase(user.getEmail())));
                    System.out.println("Livre rendu : " + book.getTitle());
                } else {
                    // Afficher un message d'erreur si le livre n'est pas emprunté par l'utilisateur
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Erreur de rendu");
                    alert.setHeaderText("Sélection Livre");
                    alert.setContentText("Veuillez sélectionner un livre à rendre !");
                    alert.showAndWait();
                }
            }

            connection.commit();

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
                if (updateBorrowStatement != null) updateBorrowStatement.close();
                if (updateBookStatement != null) updateBookStatement.close();
                if (insertHistoryStatement != null) insertHistoryStatement.close();
                if (deleteBorrowStatement != null) deleteBorrowStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }



    protected void loadBorrowedBooks(String email) {
        borrowList = FXCollections.observableArrayList();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        try {
            connection = DatabaseConnection.getConnection();

            String query = "SELECT L.title, L.author, L.isbn, E.start, E.end " +
                    "FROM Book L " +
                    "INNER JOIN Borrow E ON L.isbn = E.book_isbn " +
                    "WHERE E.user_email = ? ";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                String isbn = resultSet.getString("isbn");
                String start1 = resultSet.getDate("start").toString();
                LocalDate end1 = resultSet.getDate("end").toLocalDate().plusDays(30);
                borrowList.add(new Book(title, author, isbn, start1, end1.toString()));
            }

            bookTableView.setItems(borrowList);
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
    @FXML
    protected void loadBooksButton(javafx.event.ActionEvent ActionEvent) {
        historylabel.setVisible(false);
        borrowlabel.setVisible(true);
        borrowList = FXCollections.observableArrayList();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        giveBack.setVisible(true);
        borrow.setVisible(false);
        history.setVisible(true);
        try {
            connection = DatabaseConnection.getConnection();

            String query = "SELECT L.title, L.author, L.isbn, E.start, E.end " +
                    "FROM Book L " +
                    "INNER JOIN Borrow E ON L.isbn = E.book_isbn " +
                    "WHERE E.user_email = ? ";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, user.getEmail());
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                String isbn = resultSet.getString("isbn");
                String start1 = resultSet.getDate("start").toString();
                LocalDate end1 = resultSet.getDate("end").toLocalDate().plusDays(30);
                borrowList.add(new Book(title, author, isbn, start1, end1.toString()));
            }

            bookTableView.setItems(borrowList);
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

    @FXML
    protected void loadHistory(javafx.event.ActionEvent ActionEvent) {
        historylabel.setVisible(true);
        borrowlabel.setVisible(false);
        ObservableList<Book> historyList = FXCollections.observableArrayList();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        giveBack.setVisible(false);
        history.setVisible(false);
        borrow.setVisible(true);

        try {
            connection = DatabaseConnection.getConnection();

            String query = "SELECT L.title, L.author, L.isbn, H.start, H.end, H.delay " +
                    "FROM Book L " +
                    "INNER JOIN History H ON L.isbn = H.book_isbn " +
                    "WHERE H.user_email = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, user.getEmail());
            resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String title = resultSet.getString("title");
                String author = resultSet.getString("author");
                String isbn = resultSet.getString("isbn");
                String start1 = resultSet.getDate("start").toString();
                String end1 = resultSet.getDate("end").toString();
                boolean delay = resultSet.getBoolean("delay");
                System.out.println(title + " " + author + " " + isbn);
                historyList.add(new Book(title, author, isbn, start1, end1.toString()));
            }

            bookTableView.setItems(historyList);
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
    protected void handleBookSelection() {
        selectedBooks.clear();
        selectedBooks.addAll(bookTableView.getSelectionModel().getSelectedItems());
    }

    @FXML
    private void handleReturnButtonClick(ActionEvent event) {
        try {
            //Charge la vue de la page principale
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/views/pageprincipal.fxml"));
             Parent root = loader.load();
            // Obtient le stage actuel à partir de n'importe quel composant de la scène
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Définit la nouvelle scène avec la racine chargée
             Scene scene = new Scene(root);
            stage.setScene(scene);
            // Optionnel : redéfinir le titre de la fenêtre
            stage.setTitle("Page Principale");
             //Affiche la scène principale
             stage.show();
            } catch (IOException e) {
             e.printStackTrace();
        }
    }


    @FXML
    private void banUser() {

        if (user != null) {
            // Afficher une boîte de dialogue de confirmation
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Voulez-vous vraiment bannir cet utilisateur ?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                // Mettre à jour le statut de l'utilisateur pour le bannir (statut 3)

                // Mise à jour dans la base de données
                try (Connection connection = DatabaseConnection.getConnection();
                     PreparedStatement updateUserStatement = connection.prepareStatement("UPDATE User SET state = ? WHERE email = ?")) {

                    updateUserStatement.setInt(1, 3);
                    updateUserStatement.setString(2, user.getEmail());
                    updateUserStatement.executeUpdate();

                    // Optionnel : mettre à jour l'interface utilisateur pour refléter ce changement
                    Alert info = new Alert(Alert.AlertType.INFORMATION, "L'utilisateur a été banni avec succès.");
                    info.show();
                } catch (SQLException e) {
                    e.printStackTrace();
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR, "Une erreur s'est produite lors de la mise à jour de l'utilisateur.");
                    errorAlert.show();
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "Aucun utilisateur sélectionné.");
            alert.show();
        }
    }
}


