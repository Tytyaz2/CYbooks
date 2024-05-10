package main.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import main.models.DatabaseConnection;
import main.models.DataModel;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import main.models.DataModel;

public class MainControllers {

    @FXML
    private TableView<DataModel> tableView;

    @FXML
    private TableColumn<DataModel, String> test1Column;

    @FXML
    private TableColumn<DataModel, String> test2Column;

    @FXML
    private TableColumn<DataModel, String> test3Column;

    @FXML
    private TableColumn<DataModel, String> test4Column;

    @FXML
    public void initialize() {
        // Load data into TableView
        loadData();
    }

    private void loadData() {
        List<DataModel> data = new ArrayList<>();
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            // Get database connection
            connection = DatabaseConnection.getConnection();

            // Execute query to fetch data
            String query = "SELECT * FROM test";
            preparedStatement = connection.prepareStatement(query);
            resultSet = preparedStatement.executeQuery();

            // Iterate through result set and add data to list
            while (resultSet.next()) {
                DataModel model = new DataModel(
                        resultSet.getString("test1"),
                        resultSet.getString("test2"),
                        resultSet.getString("test3"),
                        resultSet.getString("test4"));
                data.add(model);
            }

            // Populate TableView with data
            tableView.getItems().addAll(data);

        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            // Close resources
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
