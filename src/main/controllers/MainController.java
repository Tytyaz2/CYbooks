package main.controllers;
import javafx.beans.property.SimpleStringProperty;
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
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import javafx.util.Callback;
import main.API.SearchBookAPI;
import main.dataBase.DatabaseConnection;
import main.models.*;
import javafx.scene.paint.Color;
import javafx.scene.control.TableCell;
import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.Objects;

public class MainController {

    // UI elements declaration
    public Button showLateBooksButton;
    private int startIndex = 1;
    private final int pageSize = 20;

    @FXML
    private TableView<Book> bookTableView;
    @FXML
    private TextArea SearchBook;
    @FXML
    private TextArea SearchUser;

    private TextField searchTextField;

    @FXML
    private TableView<User> userTableView;

    @FXML
    private TableColumn<User, String> lastName;

    @FXML
    private TableColumn<User, String> firstName;

    @FXML
    private TableColumn<User, String> email;

    private User selectedUser;

    @FXML
    private RadioButton searchbyauthor;

    @FXML
    private RadioButton searchbytitle;

    @FXML
    private RadioButton searchbyisbn;
    @FXML
    private RadioButton searchbypublisher;
    @FXML
    private RadioButton searchbypublisheddate;
    @FXML
    private RadioButton searchbydescription;
    @FXML
    private RadioButton searchbygenre;
    @FXML
    private RadioButton searchbycreation;
    @FXML
    private RadioButton searchbysubject;
    @FXML
    private RadioButton searchbyratingscount;
    @FXML
    private RadioButton searchbylanguage;
    private ToggleGroup searchToggleGroup;




    /**
     * Loads the first 20 books based on the search query and updates the TableView.
     */
    @FXML
    private void loadFirst20Books() {
        String searchQuery = SearchBook.getText();
        List<Book> books = SearchBookAPI.search("anywhere", searchQuery, startIndex, pageSize);
        bookTableView.getItems().setAll(books);
    }

    /**
     * Handles the action when the Next button is clicked.
     * Increments the startIndex and reloads the first 20 books.
     */
    @FXML
    private void handleNextButtonAction() {
        startIndex += pageSize;
        loadFirst20Books();
    }

    /**
     * Handles the action when the Previous button is clicked.
     * Decrements the startIndex (minimum 0) and reloads the first 20 books.
     */
    @FXML
    private void handlePreviousButtonAction() {
        startIndex = Math.max(0, startIndex - pageSize);
        loadFirst20Books();
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

    /**
     * Refreshes user data in the TableView.
     *
     * @throws SQLException if a database access error occurs.
     */
    public void refreshUserData() throws SQLException {
        // Clear current data from the table
        userTableView.getItems().clear();

        // Reload data from the database and add it to the table
        List<User> userList = DatabaseConnection.getAllUser();
        userTableView.getItems().addAll(userList);
    }

    /**
     * Initializes the controller.
     *
     * @throws SQLException        if a SQL exception occurs.
     * @throws InterruptedException if the thread is interrupted.
     */
    public void initialize() throws SQLException, InterruptedException {
        // Call the method to load popular books
        loadTop20PopularBooksLastMonth();
        checkAndUpdateUserStatus();
        // Load data into TableView
        loadData();

        // Set custom cell value factories for each column
        lastName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getLastName()));
        firstName.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFirstName()));
        email.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getEmail()));

        // Apply custom cell factories to change color based on status
        lastName.setCellFactory(getColorCellFactory());
        firstName.setCellFactory(getColorCellFactory());
        email.setCellFactory(getColorCellFactory());

        // Create a toggle group for the search radio buttons
        searchToggleGroup = new ToggleGroup();

        // Add radio buttons to the toggle group
        searchbyauthor.setToggleGroup(searchToggleGroup);
        searchbytitle.setToggleGroup(searchToggleGroup);
        searchbyisbn.setToggleGroup(searchToggleGroup);
        searchbypublisher.setToggleGroup(searchToggleGroup);
        searchbypublisheddate.setToggleGroup(searchToggleGroup);
        searchbydescription.setToggleGroup(searchToggleGroup);
        searchbygenre.setToggleGroup(searchToggleGroup);
        searchbycreation.setToggleGroup(searchToggleGroup);
        searchbysubject.setToggleGroup(searchToggleGroup);
        searchbyratingscount.setToggleGroup(searchToggleGroup);
        searchbylanguage.setToggleGroup(searchToggleGroup);

        // Add change listeners for the radio buttons
        searchToggleGroup.selectedToggleProperty().addListener((observable, oldToggle, newToggle) -> {
            if (newToggle == searchbyauthor) {
                // Call method to search by author
                loadBooksByAuthor();
            } else if (newToggle == searchbytitle) {
                // Call method to search by title
                loadBooksByTitle();
            } else if (newToggle == searchbyisbn) {
                // Call method to search by ISBN
                loadBooksByISBN();
            } else if (newToggle == searchbypublisher) {
                // Call method to search by publisher
                loadBooksByPublisher();
            } else if (newToggle == searchbypublisheddate) {
                // Call method to search by published date
                loadBooksByPublishedDate();
            } else if (newToggle == searchbydescription) {
                // Call method to search by description
                loadBooksByDescription();
            } else if (newToggle == searchbygenre) {
                // Call method to search by genre
                loadBooksByGenre();
            } else if (newToggle == searchbycreation) {
                // Call method to search by creation
                loadBooksByCreation();
            } else if (newToggle == searchbysubject) {
                // Call method to search by subject
                loadBooksBySubject();
            } else if (newToggle == searchbyratingscount) {
                // Call method to search by ratings count
                loadBooksByRatingsCount();
            } else if (newToggle == searchbylanguage) {
                // Call method to search by language
                loadBooksByLanguage();
            }
        });
    }

    /**
     * Returns a TableCell factory to set text color based on user status.
     *
     * @return TableCell factory
     */
    private Callback<TableColumn<User, String>, TableCell<User, String>> getColorCellFactory() {
        return column -> new TableCell<User, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                    setStyle("");
                } else {
                    setText(item);
                    User user = getTableView().getItems().get(getIndex());
                    switch (user.getState()) {
                        case 1:
                            setTextFill(Color.RED);
                            break;
                        case 2:
                            setTextFill(Color.ORANGE);
                            break;
                        case 0:
                            setTextFill(Color.BLACK);
                            break;
                        case 3:

                        default:
                            setTextFill(Color.BLACK); // Default color if status is not 0, 1, or 2
                            break;
                    }
                }
            }
        };
    }


    /**
     * Shows the Late Loans view by loading the Late Borrow FXML file in a new stage.
     * This method loads the FXML file, creates a new stage with the loaded FXML, sets the scene, and displays the stage.
     * If an IOException occurs during loading, it is printed and optionally handled.
     */
    public void showLateLoansView() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/views/lateBorrow.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Late Loans");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Handle the exception, maybe show an alert to the user
        }
    }

    /**
     * Loads books by author name.
     * This method fetches books using the provided author name via the SearchBookAPI.
     * If the fetched list is null, it clears the bookTableView; otherwise, it populates the table with the fetched books.
     */
    private void loadBooksByAuthor() {
        String author = SearchBook.getText();
        List<Book> books = SearchBookAPI.search("author", author, startIndex, pageSize);
        if (books == null) {
            bookTableView.getItems().clear();
        } else {
            bookTableView.getItems().setAll(books);
        }
    }

    /**
     * Loads books by title.
     * This method fetches books using the provided title via the SearchBookAPI.
     * If the fetched list is null, it clears the bookTableView; otherwise, it populates the table with the fetched books.
     */
    private void loadBooksByTitle() {
        String title = SearchBook.getText();
        List<Book> books = SearchBookAPI.search("title", title, startIndex, pageSize);
        if (books == null) {
            bookTableView.getItems().clear();
        } else {
            bookTableView.getItems().setAll(books);
        }
    }


    /**
     * Loads books by ISBN.
     * This method fetches books using the provided ISBN via the SearchBookAPI and populates the bookTableView with the fetched books.
     */
    private void loadBooksByISBN() {
        String isbn = SearchBook.getText();
        List<Book> books = SearchBookAPI.search("isbn", isbn, startIndex, pageSize);
        bookTableView.getItems().setAll(books);
    }

    /**
     * Loads books by publisher.
     * This method fetches books using the provided publisher name via the SearchBookAPI.
     * If the fetched list is null, it clears the bookTableView; otherwise, it populates the table with the fetched books.
     */
    private void loadBooksByPublisher() {
        String publisher = SearchBook.getText();
        List<Book> books = SearchBookAPI.search("publisher", publisher, startIndex, pageSize);
        if (books == null) {
            bookTableView.getItems().clear();
        } else {
            bookTableView.getItems().setAll(books);
        }
    }

    /**
     * Loads books by published date.
     * This method fetches books using the provided published date via the SearchBookAPI.
     * If the fetched list is null, it clears the bookTableView; otherwise, it populates the table with the fetched books.
     */
    private void loadBooksByPublishedDate() {
        String publishedDate = SearchBook.getText();
        List<Book> books = SearchBookAPI.search("publicationdate", publishedDate, startIndex, pageSize);
        if (books == null) {
            bookTableView.getItems().clear();
        } else {
            bookTableView.getItems().setAll(books);
        }
    }

    /**
     * Loads books by description.
     * This method fetches books using the provided description via the SearchBookAPI.
     * If the fetched list is null, it clears the bookTableView; otherwise, it populates the table with the fetched books.
     */
    private void loadBooksByDescription() {
        String description = SearchBook.getText();
        List<Book> books = SearchBookAPI.search("anywhere", description, startIndex, pageSize);
        if (books == null) {
            bookTableView.getItems().clear();
        } else {
            bookTableView.getItems().setAll(books);
        }
    }

    /**
     * Loads books by genre.
     * This method fetches books using the provided genre via the SearchBookAPI.
     * If the fetched list is null, it clears the bookTableView; otherwise, it populates the table with the fetched books.
     */
    private void loadBooksByGenre() {
        String genre = SearchBook.getText();
        List<Book> books = SearchBookAPI.search("genre", genre, startIndex, pageSize);
        if (books == null) {
            bookTableView.getItems().clear();
        } else {
            bookTableView.getItems().setAll(books);
        }
    }

    /**
     * Loads books by creation date.
     * This method fetches books using the provided creation date via the SearchBookAPI.
     * If the fetched list is null, it clears the bookTableView; otherwise, it populates the table with the fetched books.
     */
    private void loadBooksByCreation() {
        String creation = SearchBook.getText();
        List<Book> books = SearchBookAPI.search("creationdate", creation, startIndex, pageSize);
        if (books == null) {
            bookTableView.getItems().clear();
        } else {
            bookTableView.getItems().setAll(books);
        }
    }

    /**
     * Loads books by subject.
     * This method fetches books using the provided subject via the SearchBookAPI.
     * If the fetched list is null, it clears the bookTableView; otherwise, it populates the table with the fetched books.
     */
    private void loadBooksBySubject() {
        String subject = SearchBook.getText();
        List<Book> books = SearchBookAPI.search("subject", subject, startIndex, pageSize);
        if (books == null) {
            bookTableView.getItems().clear();
        } else {
            bookTableView.getItems().setAll(books);
        }
    }

    /**
     * Loads books by ratings count.
     * This method fetches books using the provided ratings count via the SearchBookAPI.
     * If the fetched list is null, it clears the bookTableView; otherwise, it populates the table with the fetched books.
     * If the provided ratings count is not a valid number, it prints a message to the console and clears the bookTableView.
     */
    private void loadBooksByRatingsCount() {
        try {
            int ratingsCount = Integer.parseInt(SearchBook.getText());
            List<Book> books = SearchBookAPI.search("cote", String.valueOf(ratingsCount), startIndex, pageSize);
            if (books == null) {
                bookTableView.getItems().clear();
            } else {
                bookTableView.getItems().setAll(books);
            }
        } catch (NumberFormatException e) {
            System.out.println("Veuillez entrer un nombre valide pour le nombre d'évaluations.");
            bookTableView.getItems().clear();
        }
    }

    /**
     * Loads books by language.
     * This method fetches books using the provided language via the SearchBookAPI.
     * If the fetched list is null, it clears the bookTableView; otherwise, it populates the table with the fetched books.
     */
    private void loadBooksByLanguage() {
        String language = SearchBook.getText();
        List<Book> books = SearchBookAPI.search("language", language, startIndex, pageSize);
        if (books == null) {
            bookTableView.getItems().clear();
        } else {
            bookTableView.getItems().setAll(books);
        }
    }

    /**
     * Handles the user click event in the TableView by retrieving the selected user and showing the adherent page.
     * This method retrieves the selected user from the TableView and calls the method to show the adherent page in a new scene.
     *
     * @param mouseEvent The MouseEvent representing the user's click action.
     */
    @FXML
    public void handleUserClick(MouseEvent mouseEvent) {
        selectedUser = userTableView.getSelectionModel().getSelectedItem();
        if (selectedUser != null) {
            showAdherentPage();
        }
    }






    /**
     * Handles the event when the "New Adherent" button is clicked to show the new adherent page.
     * This method triggers the display of the new adherent page when the button is clicked.
     *
     * @param event The ActionEvent representing the button click event.
     */
    @FXML
    public void handleNewAdherentButtonClick(ActionEvent event) {
        showNewAdherentPage();
    }

    /**
     * Handles the event when the "New Borrow" button is clicked to show the Borrow.fxml page.
     * This method loads the Borrow.fxml file and replaces the current scene's children with those of the new view.
     *
     * @param event The ActionEvent representing the button click event.
     */
    @FXML
    void handleNewBorrowButtonClick(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/views/Borrow.fxml"));
            Parent empruntRoot = loader.load();
            AnchorPane root = (AnchorPane) ((Node) event.getSource()).getScene().getRoot();
            root.getChildren().setAll(empruntRoot);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    /**
     * Shows the adherent page.
     * This method loads the AdherentPage.fxml file, displays the details of the selected user, and sets it as the scene.
     */
    public void showAdherentPage() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/main/views/AdherentPage.fxml"));
            Parent root = loader.load();
            UserController userController = loader.getController();
            userController.displayUserDetails(selectedUser);
            Stage stage = (Stage) bookTableView.getScene().getWindow();
            stage.getScene().setRoot(root);
        } catch (IOException e) {
            e.printStackTrace();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Shows the new adherent page.
     * This method loads the newAdherent.fxml file, sets the main controller, and sets it as the scene.
     */
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

    /**
     * Checks and updates user status.
     * This method checks for overdue loans, maximum number of loans, and updates user status accordingly.
     * It runs periodically using a Timer task.
     */
    private void checkAndUpdateUserStatus() {
        // Timer task to run the update periodically
        Timer timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() {
                try {
                    LocalDate currentDate = LocalDate.now();
                    List<User> users = DatabaseConnection.getAllUser();

                    for (User user : users) {
                        boolean hasOverdueLoans = user.hasOverdueBorrow(currentDate);
                        int maxEmprunt = DatabaseConnection.getUserMaxBorrow(user);
                        int lateCount = DatabaseConnection.getUserLateCount(user);

                        if (user.getState() != 3) {
                            if (lateCount >= 3) {
                                if (user.getState() != 3) {
                                    user.setState(3);
                                    DatabaseConnection.updateUserStatus(user, 3);
                                }
                            } else if (hasOverdueLoans) {
                                if (user.getState() != 1) {
                                    user.setState(1);
                                    DatabaseConnection.updateUserStatus(user, 1);
                                }
                            } else if (maxEmprunt == 0) {
                                if (user.getState() != 2) {
                                    user.setState(2);
                                    DatabaseConnection.updateUserStatus(user, 2);
                                }
                            } else {
                                if (user.getState() != 0) {
                                    user.setState(0);
                                    DatabaseConnection.updateUserStatus(user, 0);
                                }
                            }
                        }
                    }
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }, 0, 24 * 60 * 60 * 1000); // Runs daily
    }

    /**
     * Loads the top 20 popular books of the last month.
     * This method fetches the top 20 popular books from the database and updates the TableView with the data.
     */
    private void loadTop20PopularBooksLastMonth() {
        try {
            List<Book> popularBooks = DatabaseConnection.getTop20PopularBooksLast30Days();
            bookTableView.getItems().setAll(popularBooks);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles the search action for users.
     * This method searches for users based on the entered search pattern and updates the userTableView accordingly.
     *
     * @param actionEvent The ActionEvent representing the search action event.
     * @throws SQLException if a SQL exception occurs.
     */
    public void handleSearchUser(ActionEvent actionEvent) throws SQLException {
        String searchPattern = "%" + SearchUser.getText() + "%";
        userTableView.getItems().clear();

        if (Objects.equals(searchPattern, "")) {
            loadData();
        } else {
            try {
                List<User> data = DatabaseConnection.searchUsers(searchPattern);
                userTableView.getItems().addAll(data);
            } catch (SQLException e) {
                System.err.println("Error lors de la récupération des données : " + e.getMessage());
            }
        }
    }

    /**
     * Refreshes user status.
     * Clears the current data in the table.
     * Reloads the data from the database and adds it to the table.
     *
     * @throws SQLException if a SQL exception occurs.
     */
    public void refreshStatut() throws SQLException {
        userTableView.getItems().clear();
        loadData();
    }

}
