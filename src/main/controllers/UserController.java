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

public class UserController {

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

    public void displayUserDetails(User user) throws SQLException {
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

            int maxBorrow = 5 - DatabaseConnection.getUserMaxBorrow(user);
            nbBorrowsLabel.setText(String.valueOf(maxBorrow));

            loadBorrowedBooks(user.getEmail());
        } else {
            lastNameLabel.setText("");
            firstNameLabel.setText("");
            mailLabel.setText("");
            nbBorrowsLabel.setText("");
        }
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

    /**
     * Handles modifying user information.
     *
     * @param actionEvent The mouse event triggering the modification.
     * @throws SQLException if a database access error occurs.
     */
    @FXML
    public void modifyUser(MouseEvent actionEvent) throws SQLException {
        // Check if user information is currently being edited
        if (lastNameLabel.isVisible()) {
            // If information is visible, hide labels and show text areas for editing
            historylabel.setVisible(false);
            lastNameLabel.setVisible(false);
            firstNameLabel.setVisible(false);
            mailLabel.setVisible(false);
            lastNameTextArea.setVisible(true);
            firstNameTextArea.setVisible(true);
            mailTextArea.setVisible(true);

            // Set text areas with current information
            lastNameTextArea.setText(lastNameLabel.getText());
            firstNameTextArea.setText(firstNameLabel.getText());
            mailTextArea.setText(mailLabel.getText());
        } else {
            // If information is not visible, hide text areas and show labels
            lastNameLabel.setVisible(true);
            firstNameLabel.setVisible(true);
            mailLabel.setVisible(true);
            lastNameTextArea.setVisible(false);
            firstNameTextArea.setVisible(false);
            mailTextArea.setVisible(false);

            // Get new information from text areas
            String newLastName = lastNameTextArea.getText();
            String newFirstName = firstNameTextArea.getText();
            String newEmail = mailTextArea.getText();

            // Validate new email format
            if (!User.isValidEmail(newEmail)) {
                // Display error message for invalid email
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Validation Error");
                alert.setHeaderText(null);
                alert.setContentText("Please enter a valid email address.");
                alert.showAndWait();
                return;
            }

            // Update user object with new email
            int state = user.getState(); // Assuming `state` is part of your user object
            int maxBorrow = DatabaseConnection.getUserMaxBorrow(user); // Assuming `maxBorrow` is part of your user object
            user.setEmail(newEmail);

            // Modify user information in the database
            try {
                DatabaseConnection.modifyUser(user, user.getEmail());
                // Update UI with new information
                lastNameLabel.setText(newLastName);
                firstNameLabel.setText(newFirstName);
                mailLabel.setText(newEmail);
                nbBorrowsLabel.setText(String.valueOf(5 - maxBorrow)); // Assuming `nbBorrowsLabel` is a label for showing remaining borrows
                System.out.println("Changes successfully made");
            } catch (SQLException e) {
                System.out.println("No changes made");
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
            selectedBooks.removeAll(selectedBooks);
        } else {
            // Afficher un message à l'utilisateur ou effectuer une autre action appropriée
            System.out.println("Aucun livre sélectionné.");
        }
    }
    /**
     * Gives back selected books.
     * This method is responsible for displaying the selected books and calling the appropriate
     * method in the DatabaseConnection class to handle SQL transactions.
     *
     * @param books The list of books to be returned.
     */
    private void giveBackSelectedBooks(List<Book> books) {
        try {
            // Call the method in the DatabaseConnection class to handle SQL transactions
            DatabaseConnection.giveBackSelectedBooks(user, books);
                DatabaseConnection.updateUserMaxBorrow(user, DatabaseConnection.getUserMaxBorrow(user) + 1);

            // Update the user interface
            borrowList.removeAll(books);
            nbBorrowsLabel.setText(String.valueOf(5 - DatabaseConnection.getUserMaxBorrow(user)));
            books.forEach(book -> System.out.println("Book returned: " + book.getTitle()));
        } catch (SQLException e) {
            e.printStackTrace();
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


