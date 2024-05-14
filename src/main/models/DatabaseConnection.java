package main.models;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {
    // Database connection parameters
    private static final String URL = "jdbc:mysql://localhost:3306/Cybooks";
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
            String createDatabaseQuery = "CREATE DATABASE IF NOT EXISTS CYbooks";
            try (PreparedStatement createDatabaseStatement = connection.prepareStatement(createDatabaseQuery)) {
                createDatabaseStatement.executeUpdate();
            }

            connection = DriverManager.getConnection(URL, USERNAME, PASSWORD);

            // Create table if it doesn't exist
            String createTableQueryUtilisateur = "CREATE TABLE IF NOT EXISTS Utilisateur (" +
                    "email VARCHAR(255) PRIMARY KEY, " +
                    "prenom VARCHAR(255), " +
                    "nom VARCHAR(255), " +
                    "statut INT NOT NULL," +
                    "MaxEmprunt INT NOT NULL" +
                    ")";

            String createTableQueryLivre = "CREATE TABLE IF NOT EXISTS Livre (" +
                    "isbn INT NOT NULL PRIMARY KEY, " +
                    "titre VARCHAR(255), " +
                    "auteur VARCHAR(255)" +
                    ")";

            String createTableQueryEmprunt = "CREATE TABLE IF NOT EXISTS Emprunt (" +
                    "user_email VARCHAR(255), " +
                    "livre_isbn INT, " +
                    "date_debut DATE, " +
                    "date_fin DATE, " +
                    "FOREIGN KEY (user_email) REFERENCES Utilisateur(email), " +
                    "FOREIGN KEY (livre_isbn) REFERENCES Livre(isbn)" +
                    ")";

            try (PreparedStatement createTableStatement = connection.prepareStatement(createTableQueryUtilisateur)) {
                createTableStatement.executeUpdate();
            }

            try (PreparedStatement createTableStatement = connection.prepareStatement(createTableQueryLivre)) {
                createTableStatement.executeUpdate();
            }

            try (PreparedStatement createTableStatement = connection.prepareStatement(createTableQueryEmprunt)) {
                createTableStatement.executeUpdate();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error connecting to database", e);
        }

        return connection;
    }

    // Method to insert data into the 'utilisateurs' table
    public static void insertUserData(String email, String prenom, String nom) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            // Get connection to the database
            connection = getConnection();

            // Build INSERT SQL query for utilisateurs table
            String query = "INSERT INTO utilisateur (email, prenom, nom, statut, MaxEmprunt) VALUES (?, ?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(query);

// Set parameter values
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, prenom);
            preparedStatement.setString(3, nom);
            preparedStatement.setInt(4, 0); // Valeur pour la colonne statut
            preparedStatement.setInt(5, 5); // Valeur pour la colonne MaxEmprunt


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
    public static void insertDataEmprunt(String user_email, int livreIsbn, String dateDebut, String dateFin) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            // Get connection to the database
            connection = getConnection();

            // Build INSERT SQL query for Emprunt table
            String query = "INSERT INTO Emprunt (user_email, livre_isbn, date_debut, date_fin) VALUES (?, ?, ?, ?)";
            preparedStatement = connection.prepareStatement(query);
            
            // Set parameter values
            preparedStatement.setString(1, user_email);
            preparedStatement.setInt(2, livreIsbn);
            preparedStatement.setString(3, dateDebut);
            preparedStatement.setString(4, dateFin);

            // Execute INSERT query
            preparedStatement.executeUpdate();
            System.out.println("Data inserted successfully into Emprunt table!");
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

    public static void insertDataLivre(int isbn, String titre, String auteur) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            // Get connection to the database
            connection = getConnection();

            // Build INSERT SQL query for Livre table
            String query = "INSERT INTO Livre (isbn, titre, auteur) VALUES (?, ?, ?)";
            preparedStatement = connection.prepareStatement(query);

            // Set parameter values
            preparedStatement.setInt(1, isbn);
            preparedStatement.setString(2, titre);
            preparedStatement.setString(3, auteur);

            // Execute INSERT query
            preparedStatement.executeUpdate();
            System.out.println("Data inserted successfully into Livre table!");
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

    // Méthode pour récupérer tous les utilisateurs depuis la base de données
    public static List<Utilisateur> getAllUtilisateur() throws SQLException {
        List<Utilisateur> userList = new ArrayList<>();
        Connection connection = null;
        PreparedStatement statement = null;
        ResultSet resultSet = null;

        try {
            // Obtenir une connexion à la base de données
            connection = getConnection();

            // Préparer la requête SQL
            String query = "SELECT email, prenom, nom, statut, MaxEmprunt FROM Utilisateur";
            statement = connection.prepareStatement(query);

            // Exécuter la requête
            resultSet = statement.executeQuery();

            // Parcourir les résultats et ajouter les utilisateurs à la liste
            while (resultSet.next()) {

                String email = resultSet.getString("email");
                String prenom = resultSet.getString("prenom");
                String nom = resultSet.getString("nom");
                int statut = resultSet.getInt("statut");
                int MaxEmprunt = resultSet.getInt("MaxEmprunt");

                // Créer un nouvel objet User et l'ajouter à la liste
                Utilisateur user = new Utilisateur(email, prenom, nom, statut, MaxEmprunt);
                userList.add(user);
            }
        } finally {
            // Fermer les ressources
            if (resultSet != null) {
                resultSet.close();
            }
            if (statement != null) {
                statement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }

        return userList;
    }
}


