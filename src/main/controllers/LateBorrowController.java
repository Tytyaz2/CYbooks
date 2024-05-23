package main.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import main.models.Borrow;
import main.models.DatabaseConnection;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;

public class LateBorrowController {

    @FXML
    private TableView<Borrow> lateLoansTable;
    @FXML
    private TableColumn<Borrow, String> bookTitleColumn;
    @FXML
    private TableColumn<Borrow, String> borrowerNameColumn;
    @FXML
    private TableColumn<Borrow, LocalDate> dueDateColumn;

    @FXML
    private void initialize() {
        // Initialize the TableView columns using PropertyValueFactory to call getters
        bookTitleColumn.setCellValueFactory(new PropertyValueFactory<>("bookTitle"));
        borrowerNameColumn.setCellValueFactory(new PropertyValueFactory<>("borrowerName"));
        dueDateColumn.setCellValueFactory(new PropertyValueFactory<>("EndDate"));

        // Load late loans when the view is initialized
        loadLateLoans();

//         Set the stage size to 720x1280
//        Stage stage = (Stage) lateLoansTable.getScene().getWindow();
//        stage.setWidth(720);
//        stage.setHeight(1280);
    }

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

    private void displayLateLoans(List<Borrow> lateLoans) {
        ObservableList<Borrow> loanObservableList = FXCollections.observableArrayList(lateLoans);
        lateLoansTable.setItems(loanObservableList);
    }
}
