package main.models;

import main.models.Utilisateur;

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
    public static void updateUserNbrEmprunt(String email, int nbrEmprunt) throws SQLException {
        String query = "UPDATE Utilisateur SET MaxEmprunt = ? WHERE email = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setInt(1, 5 - nbrEmprunt);
            preparedStatement.setString(2, email);

            preparedStatement.executeUpdate();
            System.out.println("Nombre d'emprunts restants mis à jour pour l'utilisateur avec l'email: " + email);
        }
    }


    private static void createTablesIfNotExist() throws SQLException {
        try (Connection connection = getConnection()) {
            String createTableQueryUtilisateur = "CREATE TABLE IF NOT EXISTS Utilisateur (" +
                    "email VARCHAR(100) PRIMARY KEY, " +
                    "prenom VARCHAR(255), " +
                    "nom VARCHAR(255), " +
                    "statut INT NOT NULL," +
                    "MaxEmprunt INT NOT NULL" +
                    ")";

            String createTableQueryLivre = "CREATE TABLE IF NOT EXISTS Livre (" +
                    "isbn VARCHAR(100)  PRIMARY KEY, " +
                    "titre VARCHAR(255), " +
                    "auteur VARCHAR(255), " +
                    "stock INT DEFAULT 10" +
                    ")";
            String createTableQueryEmprunt = "CREATE TABLE IF NOT EXISTS Emprunt (" +
                    "user_email VARCHAR(100), " +
                    "livre_isbn VARCHAR(100) , " +
                    "date_debut DATE, " +
                    "date_fin DATE, " +
                    "FOREIGN KEY (user_email) REFERENCES Utilisateur(email), " +
                    "FOREIGN KEY (livre_isbn) REFERENCES Livre(isbn)" +
                    ")";
            // Requête SQL pour créer la table Historique
            String createHistoquery = "CREATE TABLE IF NOT EXISTS Historique ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "livre_isbn VARCHAR(100),"
                    + "user_email VARCHAR(100),"
                    + "date_debut DATE,"
                    + "date_fin DATE,"
                    + "retard BOOLEAN,"
                    + "FOREIGN KEY (livre_isbn) REFERENCES Livre(isbn),"
                    + "FOREIGN KEY (user_email) REFERENCES Utilisateur(email)"
                    + ")";

            try (PreparedStatement createTableStatementUtilisateur = connection.prepareStatement(createTableQueryUtilisateur);
                 PreparedStatement createTableStatementLivre = connection.prepareStatement(createTableQueryLivre);
                 PreparedStatement createTableStatementEmprunt = connection.prepareStatement(createTableQueryEmprunt);
                 PreparedStatement createTableStatementHistorique = connection.prepareStatement(createHistoquery)) {
                createTableStatementUtilisateur.executeUpdate();
                createTableStatementLivre.executeUpdate();
                createTableStatementEmprunt.executeUpdate();
                createTableStatementHistorique.executeUpdate();
            }
        }
    }


    public static boolean isBookExists(String isbn) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM Livre WHERE isbn = ?")) {
            stmt.setString(1, isbn);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }

    public static void insertBook(Book book) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO Livre (isbn, titre, auteur, stock) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, book.getIsbn());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthors());
            stmt.setInt(4, 10); // Stock initial à 10
            stmt.executeUpdate();
        }
    }

    public static int getBookStock(String isbn) throws SQLException {
        int stock = 0;
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT stock FROM Livre WHERE isbn = ?")) {
            stmt.setString(1, isbn);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stock = rs.getInt("stock");
                }
            }
        }
        return stock;
    }

    public static void updateStock(String isbn, int newStock) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE Livre SET stock = ? WHERE isbn = ?")) {
            stmt.setInt(1, newStock);
            stmt.setString(2, isbn);
            stmt.executeUpdate();
        }
    }

    public static int getUserMaxEmprunt(String userEmail) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int maxEmprunt = 0;

        try {
            // Obtenez la connexion à la base de données
            connection = getConnection();

            // Préparez la requête SQL pour récupérer le nombre d'emprunts maximal de l'utilisateur
            String query = "SELECT MaxEmprunt FROM utilisateur WHERE email = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, userEmail);

            // Exécutez la requête et récupérez le résultat
            resultSet = preparedStatement.executeQuery();

            // Si une ligne est renvoyée, récupérez le nombre d'emprunts maximal
            if (resultSet.next()) {
                maxEmprunt = resultSet.getInt("MaxEmprunt");
            }
        } finally {
            // Fermer les ressources
            if (resultSet != null) {
                resultSet.close();
            }
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }

        return maxEmprunt;


    }

    public static void updateBookStock(String isbn, int newStock) {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            connection = getConnection();

            String query = "UPDATE Livre SET stock = ? WHERE isbn = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, newStock);
            preparedStatement.setString(2, isbn);

            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    public static int getStockFromDatabase(String isbn) {
        int stock = 0;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();

            String query = "SELECT stock FROM Livre WHERE isbn = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, isbn);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                stock = resultSet.getInt("stock");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (resultSet != null) resultSet.close();
                if (preparedStatement != null) preparedStatement.close();
                if (connection != null) connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

        return stock;
    }


    public static void updateUserMaxEmprunt(String userEmail, int newMaxEmprunt) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            // Obtenez la connexion à la base de données
            connection = getConnection();

            // Préparez la requête SQL pour mettre à jour le nombre d'emprunts maximal de l'utilisateur
            String query = "UPDATE utilisateur SET MaxEmprunt = ? WHERE email = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, newMaxEmprunt);
            preparedStatement.setString(2, userEmail);

            // Exécutez la requête
            preparedStatement.executeUpdate();
        } finally {
            // Fermer les ressources
            if (preparedStatement != null) {
                preparedStatement.close();
            }
            if (connection != null) {
                connection.close();
            }
        }
    }

    public static boolean isBookAlreadyBorrowed(String userEmail, String bookISBN) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String query = "SELECT COUNT(*) FROM Emprunt WHERE user_email = ? AND livre_isbn = ?";
            stmt = conn.prepareStatement(query);
            stmt.setString(1, userEmail);
            stmt.setString(2, bookISBN);
            rs = stmt.executeQuery();

            if (rs.next()) {
                int count = rs.getInt(1);
                return count > 0;
            }
            return false;
        } finally {
            // Assurez-vous de fermer toutes les ressources JDBC pour éviter les fuites de ressources
            closeResources(rs, stmt, conn);
        }
    }


    public static void closeResources(ResultSet rs, Statement stmt, Connection conn) {
        try {
            if (rs != null) {
                rs.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (stmt != null) {
                stmt.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        try {
            if (conn != null) {
                conn.close();
            }
        } catch (SQLException e) {
            e.printStackTrace();
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




    public static void insertDataEmprunt(String user_email, String livreIsbn, String dateDebut, String dateFin) throws SQLException {
        String query = "INSERT INTO Emprunt (user_email, livre_isbn, date_debut, date_fin) VALUES (?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, user_email);
            preparedStatement.setString(2, livreIsbn);
            preparedStatement.setString(3, dateDebut);
            preparedStatement.setString(4, dateFin);

            preparedStatement.executeUpdate();
            System.out.println("Data inserted successfully into Emprunt table!");
        }
    }

    public static void insertDataLivre(String isbn, String titre, String auteur) throws SQLException {
        String query = "INSERT INTO Livre (isbn, titre, auteur) VALUES (?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, isbn);
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


                // Création de l'objet Utilisateur avec la nouvelle colonne nbrEmprunt
                Utilisateur user = new Utilisateur(email, prenom, nom, statut, maxEmprunt);
                userList.add(user);
            }
        }

        return userList;
    }
}
