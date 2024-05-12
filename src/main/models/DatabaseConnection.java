package main.models;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class DatabaseConnection {
    // Database connection parameters
    private static final String URL = "jdbc:mysql://localhost:3306/utilisateurs";
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
            String createDatabaseQuery = "CREATE DATABASE IF NOT EXISTS utilisateurs";
            try (PreparedStatement createDatabaseStatement = connection.prepareStatement(createDatabaseQuery)) {
                createDatabaseStatement.executeUpdate();
            }

            // Connect to the 'test' database
            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

            // Create table if it doesn't exist
            String createTableQuery = "CREATE TABLE IF NOT EXISTS utilisateurs (" +
                    "id INT AUTO_INCREMENT PRIMARY KEY, " +
                    "nom VARCHAR(255), " +
                    "prenom VARCHAR(255), " +
                    "email VARCHAR(255), " +
                    "livre_titre VARCHAR(255), " +
                    "livre_auteur VARCHAR(255), " +
                    "isbn VARCHAR(255), " +
                    "date_emprunt DATE, " +
                    "date_rendu DATE)";
            try (PreparedStatement createTableStatement = connection.prepareStatement(createTableQuery)) {
                createTableStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to database", e);
        }

        return connection;
    }

    // Method to insert data into the 'utilisateurs' table
    public static void insertUserData(String nom, String prenom, String email, String titre, String auteur, String isbn, String dateEmprunt, String dateRendu) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            // Get connection to the database
            connection = getConnection();

            // Build INSERT SQL query for utilisateurs table
            String query = "INSERT INTO utilisateurs (nom, prenom, email, livre_titre, livre_auteur, isbn, date_emprunt, date_rendu) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(query);

            // Set parameter values
            preparedStatement.setString(1, nom);
            preparedStatement.setString(2, prenom);
            preparedStatement.setString(3, email);
            preparedStatement.setString(4, titre);
            preparedStatement.setString(5, auteur);
            preparedStatement.setString(6, isbn);
            preparedStatement.setString(7, dateEmprunt);
            preparedStatement.setString(8, dateRendu);

            // Execute INSERT query
            preparedStatement.executeUpdate();
            System.out.println("Data inserted successfully into utilisateurs table!");
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
