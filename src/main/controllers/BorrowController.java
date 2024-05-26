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
import main.API.SearchBookAPI;
import main.dataBase.DatabaseConnection;
import main.models.*;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

public class BorrowController {

    @FXML
    private TableView<User> userTableView;

    @FXML
    private TextArea textFieldUser;

    @FXML
    private TableColumn<User, String> lastNameColumn;
    @FXML
    private TableColumn<User, String> emailColumn;

    @FXML
    private TableColumn<User, String> firsNameColumn;

    @FXML
    private TextArea bookSearchTextArea;

    @FXML
    private Label selectedUserLabel;

    @FXML
    private TableView<Book> bookTableView;

    @FXML
    private ListView<Book> selectedBooksListView;

    private User selectedUser;

    /**
     * Method called during the loading of the FXML view.
     */
    @FXML
    public void initialize() {
        try {
            // Load user data
            bookTableView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);

            loadData();

            // Configure the columns to display the name and first name
            emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
            lastNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLastName()));
            firsNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFirstName()));
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the exception appropriately
        }
    }

    @FXML
    private void searchBooks() {
        loadBooksByTitle();
    }

    /**
     * Loads user data from the database and populates the TableView.
     *
     * @throws SQLException if a database access error occurs.
     */
    private void loadData() throws SQLException {
        userTableView.getItems().clear(); // Clear the TableView before loading new data
        try {
            List<User> data = DatabaseConnection.loadUsers();
            userTableView.getItems().addAll(data);
        } catch (SQLException e) {
            System.err.println("Error loading data: " + e.getMessage());
            throw e; // Re-throw the exception for handling in the caller
        }
    }

    private void loadBooksByTitle() {
        String title = bookSearchTextArea.getText();
        int startIndex = 1;
        int pageSize = 20;
        List<Book> books = SearchBookAPI.search("title", title, startIndex, pageSize);
        bookTableView.getItems().setAll(books);
    }

    /**
     * Handles user selection from the TableView.
     *
     * @param event the mouse event triggered by selecting a user.
     * @throws SQLException if a database access error occurs.
     */
    @FXML
    private void handleUserSelection(MouseEvent event) throws SQLException {
        selectedBooksListView.getItems().clear();
        selectedUser = userTableView.getSelectionModel().getSelectedItem();

        if (selectedUser != null) {
            // Check if the user is banned or overdue
            if (selectedUser.getState() == 1) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Incorrect Status");
                alert.setHeaderText("You are not in a position to borrow a book");
                alert.setContentText("Member has at least one overdue book!");
                alert.showAndWait();
                // Deselect the user and return
                userTableView.getSelectionModel().clearSelection();
                selectedUser = null;
                return;
            } else if (selectedUser.getState() == 3) {
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Incorrect Status");
                alert.setHeaderText("You are not in a position to borrow a book");
                alert.setContentText("Banned member!");
                alert.showAndWait();
                // Deselect the user and return
                userTableView.getSelectionModel().clearSelection();
                selectedUser = null;
                return;
            }

            selectedUserLabel.setText(selectedUser.getLastName() + " " + selectedUser.getFirstName());

            // Check if the user has already reached their maximum borrowing limit
            if (selectedUser.getMaxBorrow() <= 0) {
                // Show an alert message
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Borrowing Limit Reached");
                alert.setHeaderText("You have already reached your borrowing limit");
                alert.setContentText("You cannot borrow more books.");
                alert.showAndWait();
            } else {
                int maxBorrow = selectedUser.getMaxBorrow();
                // Update the selected books list to reflect the maximum borrowing limit
                selectedBooksListView.getItems().addAll(selectedBooks.subList(0, Math.min(selectedBooks.size(), maxBorrow)));
            }
        }
    }

    private final ObservableList<Book> selectedBooks = FXCollections.observableArrayList();

    /**
     * Handles book selection from the TableView.
     *
     * @throws SQLException if a database access error occurs.
     */
    @FXML
    private void handleBookSelection() throws SQLException {
        if (selectedUser != null) {
            int maxBorrow = selectedUser.getMaxBorrow();
            int selectedBooksCount = selectedBooks.size();

            // Check if the maximum borrowing limit is exceeded after selection
            if (selectedBooksCount >= maxBorrow) {
                // Show an alert if the maximum borrowing limit is reached
                Alert alert = new Alert(Alert.AlertType.WARNING);
                alert.setTitle("Borrowing Limit Reached");
                alert.setHeaderText("You have already reached the borrowing limit");
                alert.setContentText("The maximum borrowing limit is " + maxBorrow + " books.");
                alert.showAndWait();
                return;
            }

            // Retrieve the selected books from the TableView
            List<Book> selectedBooksFromTable = bookTableView.getSelectionModel().getSelectedItems();

            for (Book selectedBook : selectedBooksFromTable) {
                // Check if the selection exceeds the borrowing limit
                if (selectedBooksCount + 1 > maxBorrow) {
                    // Show an alert if the maximum borrowing limit is exceeded
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Borrowing Limit Exceeded");
                    alert.setHeaderText("You cannot borrow more books");
                    alert.setContentText("The maximum borrowing limit is " + maxBorrow + " books. Please select fewer books.");
                    alert.showAndWait();
                    return;
                }

                // Check if the book is already in the selected books list
                if (!selectedBooks.contains(selectedBook)) {
                    // Add the selected book to the selected books list
                    selectedBooks.add(selectedBook);

                    // Update the display of the selected books list
                    selectedBooksListView.setItems(FXCollections.observableArrayList(selectedBooks));

                    // Show a confirmation message
                    System.out.println("Book added: " + selectedBook.getTitle());

                    // Decrease the remaining borrowing limit
                    maxBorrow--;

                    // Display the full list of selected books
                    System.out.println("Full list of selected books:");
                    for (Book book : selectedBooks) {
                        System.out.println("- Title: " + book.getTitle() + ", Author(s): " + book.getAuthors() + ", ISBN: " + book.getIsbn());
                    }
                } else {
                    // Show an error message if the book is already selected
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.setTitle("Book Already Selected");
                    alert.setHeaderText("This book is already selected");
                    alert.setContentText("Please select another book.");
                    alert.showAndWait();
                }
            }
        }
    }

    /**
     * Handles the removal of all selected books.
     */
    @FXML
    private void handleRemoveBook() {
        // Clear the entire list of selected books
        selectedBooks.clear();

        // Update the display of the selected books list
        selectedBooksListView.getItems().clear();

        // Show a success message
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Success");
        alert.setHeaderText("Book list removed successfully");
        alert.setContentText("The selected books list has been successfully cleared.");
        alert.showAndWait();
    }


    /**
     * Handles the borrow operation.
     * Displays the list of selected books in the console, checks user and book selections,
     * available borrow limits, book availability, and inserts borrow data into the database.
     *
     * @throws SQLException if a database access error occurs
     */
    @FXML
    private void handleBorrow() throws SQLException {
        User selectedUser = userTableView.getSelectionModel().getSelectedItem();

        // Display the list of selected books in the console
        System.out.println("Selected books:");
        for (Book book : selectedBooks) {
            System.out.println("- Title: " + book.getTitle() + ", Author(s): " + book.getAuthors() + ", ISBN: " + book.getIsbn());
        }

        // Get the local date
        LocalDate startDate = LocalDate.now(); // Current date

        // Check if a user and at least one book have been selected
        if (selectedUser == null || selectedBooks.isEmpty()) {
            // Display an error message
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Selection Required");
            alert.setContentText("Please select a user and at least one book.");
            alert.showAndWait();
            return;
        }

        // Check if the user still has remaining borrows
        int remainingBorrows = selectedUser.getMaxBorrow();
        if (remainingBorrows <= 0) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Borrow Limit Reached");
            alert.setContentText("You have reached the borrow limit.");
            alert.showAndWait();
            return;
        }

        // Check if the number of selected books does not exceed the maximum allowed borrows
        if (selectedBooks.size() > remainingBorrows) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Borrow Limit Exceeded");
            alert.setContentText("You cannot borrow more books than your current borrow limit.");
            alert.showAndWait();
            return;
        }

        try {
            // Loop through the selected books
            for (Book selectedBook : selectedBooks) {

                // Check if the book exists in the database
                if (!DatabaseConnection.isBookExists(selectedBook)) {
                    // Insert the book into the database if it does not already exist
                    DatabaseConnection.insertBook(selectedBook);
                }

                // Check if the user has already borrowed this book
                if (DatabaseConnection.isBookAlreadyBorrowed(selectedUser, selectedBook)) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Book Already Borrowed");
                    alert.setContentText("You have already borrowed the book \"" + selectedBook.getTitle() + "\".");
                    alert.showAndWait();
                    return;
                }

                // Check if the book is available in the stock
                int currentStock = DatabaseConnection.getBookStock(selectedBook);
                if (currentStock <= 0) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Out of Stock");
                    alert.setContentText("The selected book is not available in the stock.");
                    alert.showAndWait();
                    return;
                }

                // End date is set to 30 days after the start date
                LocalDate endDate = startDate.plusDays(30);

                // Insert the borrow into the database
                DatabaseConnection.insertDataBorrow(new Borrow(selectedUser, selectedBook, startDate, endDate));

                // Update the stock in the database by reducing it by 1
                int newStock = currentStock - 1;
                selectedBook.setStock(newStock);

                // Update the stock in the database
                DatabaseConnection.updateStock(selectedBook.getIsbn(), newStock);

                // Display a success message
                System.out.println("Borrow of the book \"" + selectedBook.getTitle() + "\" added successfully for user " + selectedUser.getFirstName() + " " + selectedUser.getLastName());

                // Decrement the maximum allowed borrows for the user
                selectedUser.setMaxBorrow(selectedUser.getMaxBorrow() - 1);
            }

            // Display a global success message after borrowing all books
            Alert alert = new Alert(Alert.AlertType.INFORMATION);
            alert.setTitle("Success");
            alert.setHeaderText("Borrows Added Successfully");
            alert.setContentText("Borrows have been added successfully to the database.");
            alert.showAndWait();

            // Clear selection fields
            userTableView.getSelectionModel().clearSelection();
            bookTableView.getSelectionModel().clearSelection();

            // Update the maximum allowed borrows for the user in the database
            DatabaseConnection.updateUserMaxBorrow(selectedUser, selectedUser.getMaxBorrow());
        } catch (SQLException e) {
            e.printStackTrace(); // Handle the error appropriately
            // Display an error message
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error Adding Borrows");
            alert.setContentText("An error occurred while adding borrows to the database.");
            alert.showAndWait();
        }
    }

    /**
     * Handles the action when the return button is clicked.
     * Loads the view of the main page.
     *
     * @param event the action event
     */
    @FXML
    private void handleReturnButtonClick(ActionEvent event) {
        try {
            // Load the view of the main page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/views/MainPage.fxml"));
            Parent root = loader.load();

            // Get the current stage from any scene component
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();

            // Set the new scene with the loaded root
            Scene scene = new Scene(root);
            stage.setScene(scene);

            // Optionally: set the window title
            stage.setTitle("Main Page");

            // Show the main scene
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the action when searching for a user.
     * Searches for users based on the provided search pattern and updates the user table view.
     *
     * @param actionEvent the action event
     * @throws SQLException if a database access error occurs
     */
    @FXML
    public void handleSearchUser(ActionEvent actionEvent) throws SQLException {
        String searchPattern = "%" + textFieldUser.getText() + "%";
        userTableView.getItems().clear();

        if (Objects.equals(searchPattern, "")) {
            loadData();
        } else {
            try {
                List<User> data = DatabaseConnection.searchUsers(searchPattern);
                userTableView.getItems().addAll(data);
            } catch (SQLException e) {
                System.err.println("Error retrieving data: " + e.getMessage());
            }
        }
    }
}

