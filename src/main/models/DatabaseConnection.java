package main.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseConnection {
    // Database connection parameters
    private static final String URL = "jdbc:mysql://localhost:3306/test";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    // Static block to load JDBC driver
    static {
        try {
            // Load JDBC driver during class loading
            Class.forName("com.mysql.cj.jdbc.Driver");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException("JDBC Driver not found", e);
        }
    }

    // Method to establish database connection
    public static Connection getConnection() throws SQLException {
        Connection connection = null;

        try {
            // Attempt connection without selecting a database
            connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/", USERNAME, PASSWORD);

            // Create database if it doesn't exist
            String createDatabaseQuery = "CREATE DATABASE IF NOT EXISTS test";
            try (PreparedStatement createDatabaseStatement = connection.prepareStatement(createDatabaseQuery)) {
                createDatabaseStatement.executeUpdate();
            }

            // Connect to the 'test' database
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

            // Create table if it doesn't exist
            String createTableQuery = "CREATE TABLE IF NOT EXISTS test (" +
                    "test1 VARCHAR(255), " +
                    "test2 VARCHAR(255), " +
                    "test3 VARCHAR(255), " +
                    "test4 VARCHAR(255))";
            try (PreparedStatement createTableStatement = connection.prepareStatement(createTableQuery)) {
                createTableStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to database", e);
        }

        return connection;
    }

    // Method to insert data into a table
    public static void insertData(String table, String[] column, Object[] value) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            // Get connection to the database
            connection = getConnection();

            // Build INSERT SQL query
            StringBuilder queryBuilder = new StringBuilder("INSERT INTO ").append(table).append(" (");
            for (int i = 0; i < column.length; i++) {
                queryBuilder.append(column[i]);
                if (i < column.length - 1) {
                    queryBuilder.append(", ");
                }
            }
            queryBuilder.append(") VALUES (");
            for (int i = 0; i < value.length; i++) {
                queryBuilder.append("?");
                if (i < value.length - 1) {
                    queryBuilder.append(", ");
                }
            }
            queryBuilder.append(")");

            // Prepare statement
            String query = queryBuilder.toString();
            preparedStatement = connection.prepareStatement(query);

            // Set parameter values
            for (int i = 0; i < value.length; i++) {
                preparedStatement.setObject(i + 1, value[i]);
            }

            // Execute INSERT query
            preparedStatement.executeUpdate();
            System.out.println("Data inserted successfully!");
        } finally {
            // Close PreparedStatement and Connection
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }
}
