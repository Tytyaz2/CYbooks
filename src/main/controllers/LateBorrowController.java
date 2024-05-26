package main.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import main.models.Borrow;
import main.dataBase.DatabaseConnection;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

/**
 * Controller class for the Late Borrow view.
 * This class handles the display of late borrowed items in a TableView.
 */
public class LateBorrowController {

    @FXML
    private TableView<Borrow> lateLoansTable;
    @FXML
    private TableColumn<Borrow, String> bookTitleColumn;
    @FXML
    private TableColumn<Borrow, String> borrowerNameColumn;
    @FXML
    private TableColumn<Borrow, LocalDate> dueDateColumn;

    /**
     * Initializes the controller.
     * This method is automatically called when the corresponding FXML file is loaded.
     * It sets up the TableView columns and loads late loans from the database.
     */
    @FXML
    private void initialize() {
        // Initialize the TableView columns using PropertyValueFactory to call getters
        bookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        borrowerNameColumn.setCellValueFactory(new PropertyValueFactory<>("borrowerName"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("EndDate"));

        // Load late loans when the view is initialized
        loadLateLoans();
    }

    /**
     * Loads late loans from the database and displays them in the TableView.
     * This method retrieves late loans from the database using DatabaseConnection.getLateBorrow(),
     * then displays them in the TableView using displayLateLoans().
     */
    private void loadLateLoans() {
        try {
            List<Borrow> lateLoans = DatabaseConnection.getLateBorrow();
            // Display the late loans in your user interface
            displayLateLoans(lateLoans);
        } catch (SQLException e) {
            e.printStackTrace();
            // Handle database access errors
        }
    }

    /**
     * Displays late loans in the TableView.
     * This method takes a List of late loans and converts it into an ObservableList,
     * which is then set as the items of the TableView.
     *
     * @param lateLoans The List of late loans to be displayed.
     */
    private void displayLateLoans(List<Borrow> lateLoans) {
        ObservableList<Borrow> loanObservableList = FXCollections.observableArrayList(lateLoans);
        lateLoansTable.setItems(loanObservableList);
    }
}
