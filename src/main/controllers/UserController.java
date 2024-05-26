package main.controllers;

import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.util.Callback;
import main.models.User;
import main.dataBase.DatabaseConnection;
import main.models.Book;
import javafx.event.ActionEvent;
import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Controller that handle the information of a user.
 */
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

    /**
     * Displays user details in the UI.
     *
     * @param user The user whose details are to be displayed.
     * @throws SQLException if a database access error occurs.
     */
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

    /**
     * Initializes the UI components.
     */
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

        // Set custom cell value factories for each column
        titleColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTitle()));
        authorColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAuthors()));
        isbnColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getIsbn()));
        start.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDateBorrow()));
        end.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getDateGB()));


        // Apply custom cell factories to change color based on status
        titleColumn.setCellFactory(getColorCellFactory());
        authorColumn.setCellFactory(getColorCellFactory());
        isbnColumn.setCellFactory(getColorCellFactory());
        start.setCellFactory(getColorCellFactory());
        end.setCellFactory(getColorCellFactory());


        bookTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

        borrowList = bookTableView.getItems();

        // Handle book selection in the TableView
        bookTableView.setOnMouseClicked(event -> {
            if (event.getButton() == MouseButton.PRIMARY && event.getClickCount() == 1) {
                handleBookSelection();
            }
        });
    }
    /**
     * Returns a TableCell factory to set text color based on user status.
     *
     * @return TableCell factory
     */
    private Callback<TableColumn<Book, String>, TableCell<Book, String>> getColorCellFactory() {
        return column -> new TableCell<Book, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    Book book = getTableView().getItems().get(getIndex());
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
                    LocalDate dateTime = LocalDate.parse(book.getDateGB(), formatter);
                    if (LocalDate.now().isBefore(dateTime)) {
                        setTextFill(Color.BLACK);
                    }
                    else{
                        setTextFill(Color.RED);
                    }
                }
            }
        };
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
            int maxBorrow = DatabaseConnection.getUserMaxBorrow(user); // Assuming `maxBorrow` is part of your user object
            user.setEmail(newEmail);
            user.setFirstName(newFirstName);
            user.setLastname(newLastName);

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

    /**
     * this method is called by the button to select the book you want to give back
     */

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



    /**
     * Load borrowed books from the database and display them in the TableView.
     *
     * @param email The email of the user whose borrowed books are to be loaded.
     */
    protected void loadBorrowedBooks(String email) {
        try {
            borrowList = DatabaseConnection.loadBorrowedBooks(email);
            bookTableView.setItems(borrowList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * load books in the table view
     * @param actionEvent The event that triggered the method call.
     */
    @FXML
    protected void loadBooksButton(ActionEvent actionEvent) {
        historylabel.setVisible(false);
        borrowlabel.setVisible(true);
        giveBack.setVisible(true);
        borrow.setVisible(false);
        history.setVisible(true);

        try {
            borrowList = DatabaseConnection.loadBorrowedBooks(user.getEmail());
            bookTableView.setItems(borrowList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }



    /**
     * Load history of borrowed books for the user and display them in the TableView.
     *
     * @param actionEvent The event that triggered the method call.
     */
    @FXML
    protected void loadHistory(ActionEvent actionEvent) {
        historylabel.setVisible(true);
        borrowlabel.setVisible(false);
        giveBack.setVisible(false);
        history.setVisible(false);
        borrow.setVisible(true);

        try {
            ObservableList<Book> historyList = DatabaseConnection.loadBorrowHistory(user.getEmail());
            bookTableView.setItems(historyList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Method to handle book selection in the TableView.
     */
    @FXML
    protected void handleBookSelection() {
        selectedBooks.clear();
        selectedBooks.addAll(bookTableView.getSelectionModel().getSelectedItems());
    }

    /**
     * Handles the return button click event.
     *
     * @param event The event that triggered the method call.
     */
    @FXML
    private void handleReturnButtonClick(ActionEvent event) {
        try {
            // Load the main page view
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/views/MainPage.fxml"));
            Parent root = loader.load();
            // Get the current stage from any scene component
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            // Set the new scene with the loaded root
            Scene scene = new Scene(root);
            stage.setScene(scene);
            // Optionally: redefine the window title
            stage.setTitle("Main Page");
            // Show the main scene
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Bans the user from the system.
     */
    @FXML
    private void banUser() {

        if (user != null) {
            // Display a confirmation dialog box
            Alert alert = new Alert(Alert.AlertType.CONFIRMATION, "Do you really want to ban this user?", ButtonType.YES, ButtonType.NO);
            alert.showAndWait();

            if (alert.getResult() == ButtonType.YES) {
                // Update the user's status to ban (status 3)

                // Update in the database
                try (Connection connection = DatabaseConnection.getConnection();
                     PreparedStatement updateUserStatement = connection.prepareStatement("UPDATE User SET state = ? WHERE email = ?")) {

                    updateUserStatement.setInt(1, 3);
                    updateUserStatement.setString(2, user.getEmail());
                    updateUserStatement.executeUpdate();

                    // Optionally: update the user interface to reflect this change
                    Alert info = new Alert(Alert.AlertType.INFORMATION, "The user has been successfully banned.");
                    info.show();
                } catch (SQLException e) {
                    e.printStackTrace();
                    Alert errorAlert = new Alert(Alert.AlertType.ERROR, "An error occurred while updating the user.");
                    errorAlert.show();
                }
            }
        } else {
            Alert alert = new Alert(Alert.AlertType.WARNING, "No user selected.");
            alert.show();
        }
    }
}


