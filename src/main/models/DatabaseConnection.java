package main.models;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class DatabaseConnection {
    private static final String URL = "jdbc:mysql://localhost:3306/";
    private static final String DATABASE_NAME = "Cybooks";
    private static final String USERNAME = "root";
    private static final String PASSWORD = "";

    static {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            createDatabaseIfNotExists();
            createTablesIfNotExist();
        } catch (ClassNotFoundException | SQLException e) {
            throw new RuntimeException("Error initializing DatabaseConnection", e);
        }
    }

    private static void createDatabaseIfNotExists() throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String createDatabaseQuery = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
            try (PreparedStatement createDatabaseStatement = connection.prepareStatement(createDatabaseQuery)) {
                createDatabaseStatement.executeUpdate();
            }
        }
    }

    private static void createTablesIfNotExist() throws SQLException {
        try (Connection connection = getConnection()) {
            String createTableQueryUtilisateur = "CREATE TABLE IF NOT EXISTS Utilisateur (" +
                    "email VARCHAR(100) PRIMARY KEY, " + // Adjusted length of email column
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
                    "user_email VARCHAR(100), " +
                    "livre_isbn INT NOT NULL, " +
                    "date_debut DATE, " +
                    "date_fin DATE, " +
                    "FOREIGN KEY (user_email) REFERENCES Utilisateur(email), " +
                    "FOREIGN KEY (livre_isbn) REFERENCES Livre(isbn)" +
                    ")";


            try (PreparedStatement createTableStatementUtilisateur = connection.prepareStatement(createTableQueryUtilisateur);
                 PreparedStatement createTableStatementLivre = connection.prepareStatement(createTableQueryLivre);
                 PreparedStatement createTableStatementEmprunt = connection.prepareStatement(createTableQueryEmprunt)) {
                createTableStatementUtilisateur.executeUpdate();
                createTableStatementLivre.executeUpdate();
                createTableStatementEmprunt.executeUpdate();
            }
        }
    }

    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL + DATABASE_NAME, USERNAME, PASSWORD);
    }


    public static void insertUserData(String email, String prenom, String nom) throws SQLException {
        String query = "INSERT INTO Utilisateur (email, prenom, nom, statut, MaxEmprunt) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, email);
            preparedStatement.setString(2, prenom);
            preparedStatement.setString(3, nom);
            preparedStatement.setInt(4, 0); // statut
            preparedStatement.setInt(5, 5); // MaxEmprunt

            preparedStatement.executeUpdate();
            System.out.println("Data inserted successfully into Utilisateur table!");
        }
    }

    public static void insertDataEmprunt(String user_email, int livreIsbn, String dateDebut, String dateFin) throws SQLException {
        String query = "INSERT INTO Emprunt (user_email, livre_isbn, date_debut, date_fin) VALUES (?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, user_email);
            preparedStatement.setInt(2, livreIsbn);
            preparedStatement.setString(3, dateDebut);
            preparedStatement.setString(4, dateFin);

            preparedStatement.executeUpdate();
            System.out.println("Data inserted successfully into Emprunt table!");
        }
    }

    public static void insertDataLivre(int isbn, String titre, String auteur) throws SQLException {
        String query = "INSERT INTO Livre (isbn, titre, auteur) VALUES (?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, isbn);
            preparedStatement.setString(2, titre);
            preparedStatement.setString(3, auteur);

            preparedStatement.executeUpdate();
            System.out.println("Data inserted successfully into Livre table!");
        }
    }

    public static List<Utilisateur> getAllUtilisateur() throws SQLException {
        List<Utilisateur> userList = new ArrayList<>();
        String query = "SELECT email, prenom, nom, statut, MaxEmprunt FROM Utilisateur";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String email = resultSet.getString("email");
                String prenom = resultSet.getString("prenom");
                String nom = resultSet.getString("nom");
                int statut = resultSet.getInt("statut");
                int maxEmprunt = resultSet.getInt("MaxEmprunt");

                Utilisateur user = new Utilisateur(email, prenom, nom, statut, maxEmprunt);
                userList.add(user);
            }
        }

        return userList;
    }
}
