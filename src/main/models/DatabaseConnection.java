package main.models;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

/**
 * The DatabaseConnection class provides methods to interact with the database.
 */
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

    /**
     * Retrieves a list of borrows that are late.
     *
     * @return a list of late borrows
     * @throws SQLException if a database access error occurs
     */
    public static List<Borrow> getLateBorrow() throws SQLException {
        List<Borrow> lateBorrows = new ArrayList<>();
        LocalDate today = LocalDate.now();

        String query = "SELECT e.user_email, e.book_isbn, e.start, e.end, u.firstname, u.lastname, b.title " +
                "FROM Borrow e " +
                "JOIN User u ON e.user_email = u.email " +
                "JOIN Book b ON e.book_isbn = b.isbn " +
                "WHERE e.end < ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setDate(1, Date.valueOf(today));

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String userEmail = resultSet.getString("user_email");
                    String bookIsbn = resultSet.getString("book_isbn");
                    LocalDate startDate = resultSet.getDate("start").toLocalDate();
                    LocalDate endDate = resultSet.getDate("end").toLocalDate();
                    String firstName = resultSet.getString("firstname");
                    String lastName = resultSet.getString("lastname");
                    String title = resultSet.getString("title");

                    User user = new User(userEmail, firstName, lastName, 0, 0); // Status and maxLoan are placeholders
                    Book book = new Book(bookIsbn, title, ""); // Author is a placeholder
                    Borrow borrow = new Borrow(user, book, startDate, endDate);

                    lateBorrows.add(borrow);
                }
            }
        }
        return lateBorrows;
    }


    /**
     * Creates the database if it does not exist.
     *
     * @throws SQLException if a database access error occurs
     */
    private static void createDatabaseIfNotExists() throws SQLException {
        try (Connection connection = DriverManager.getConnection(URL, USERNAME, PASSWORD)) {
            String createDatabaseQuery = "CREATE DATABASE IF NOT EXISTS " + DATABASE_NAME;
            try (PreparedStatement createDatabaseStatement = connection.prepareStatement(createDatabaseQuery)) {
                createDatabaseStatement.executeUpdate();
            }
        }
    }




    /**
     * Creates tables if they do not exist in the database.
     *
     * @throws SQLException if a database access error occurs
     */
    private static void createTablesIfNotExist() throws SQLException {
        try (Connection connection = getConnection()) {
            // SQL Query that create the User's Table
            String createTableQueryUser = "CREATE TABLE IF NOT EXISTS User (" +
                    "email VARCHAR(100) PRIMARY KEY, " +
                    "firstname VARCHAR(255), " +
                    "lastname VARCHAR(255), " +
                    "state INT NOT NULL," +
                    "maxborrow INT NOT NULL" +
                    ")";
            // SQL Query that create the Book's Table
            String createTableQueryBook = "CREATE TABLE IF NOT EXISTS Book (" +
                    "isbn VARCHAR(100)  PRIMARY KEY, " +
                    "title VARCHAR(255), " +
                    "author VARCHAR(255), " +
                    "stock INT DEFAULT 10" +
                    ")";
            // SQL Query that create the Borrow's Table
            String createTableQueryBorrow = "CREATE TABLE IF NOT EXISTS Borrow (" +
                    "user_email VARCHAR(100), " +
                    "book_isbn VARCHAR(100) , " +
                    "start DATE, " +
                    "end DATE, " +
                    "FOREIGN KEY (user_email) REFERENCES User(email), " +
                    "FOREIGN KEY (book_isbn) REFERENCES Book(isbn)" +
                    ")";
            // SQL Query that create the History's Table
            String createHistoquery = "CREATE TABLE IF NOT EXISTS History ("
                    + "id INT AUTO_INCREMENT PRIMARY KEY,"
                    + "book_isbn VARCHAR(100),"
                    + "user_email VARCHAR(100),"
                    + "start DATE,"
                    + "end DATE,"
                    + "delay BOOLEAN,"
                    + "FOREIGN KEY (book_isbn) REFERENCES Book(isbn),"
                    + "FOREIGN KEY (user_email) REFERENCES User(email)"
                    + ")";

            try (PreparedStatement createTableStatementUser2= connection.prepareStatement(createTableQueryUser);
                 PreparedStatement createTableStatementBook2 = connection.prepareStatement(createTableQueryBook);
                 PreparedStatement createTableStatementBorrow2 = connection.prepareStatement(createTableQueryBorrow);
                 PreparedStatement createTableStatementHistory = connection.prepareStatement(createHistoquery)) {
                createTableStatementUser2.executeUpdate();
                createTableStatementBook2.executeUpdate();
                createTableStatementBorrow2.executeUpdate();
                createTableStatementHistory.executeUpdate();
            }
        }
    }

    /**
     * Checks if a book exists in the database.
     *
     * @param isbn the ISBN of the book
     * @return true if the book exists, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public static boolean isBookExists(String isbn) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT COUNT(*) FROM Book WHERE isbn = ?")) {
            stmt.setString(1, isbn);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() && rs.getInt(1) > 0;
            }
        }
    }
    /**
     * Inserts a new book into the database.
     *
     * @param book the book to insert
     * @throws SQLException if a database access error occurs
     */
    public static void insertBook(Book book) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("INSERT INTO Book (isbn, title, author, stock) VALUES (?, ?, ?, ?)")) {
            stmt.setString(1, book.getIsbn());
            stmt.setString(2, book.getTitle());
            stmt.setString(3, book.getAuthors());
            stmt.setInt(4, 10); // Initial stock at 10
            stmt.executeUpdate();
        }
    }
    /**
     * Retrieves the stock quantity of a book from the database.
     *
     * @param isbn the ISBN of the book
     * @return the stock quantity of the book
     * @throws SQLException if a database access error occurs
     */
    public static int getBookStock(String isbn) throws SQLException {
        int stock = 0;
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("SELECT stock FROM Book WHERE isbn = ?")) {
            stmt.setString(1, isbn);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    stock = rs.getInt("stock");
                }
            }
        }
        return stock;
    }
    /**
     * Updates the stock quantity of a book in the database.
     *
     * @param isbn     the ISBN of the book
     * @param newStock the new stock quantity
     * @throws SQLException if a database access error occurs
     */
    public static void updateStock(String isbn, int newStock) throws SQLException {
        try (Connection conn = getConnection();
             PreparedStatement stmt = conn.prepareStatement("UPDATE Book SET stock = ? WHERE isbn = ?")) {
            stmt.setInt(1, newStock);
            stmt.setString(2, isbn);
            stmt.executeUpdate();
        }
    }
    /**
     * Retrieves the maximum number of borrowings allowed for a user.
     *
     * @param userEmail the email of the user
     * @return the maximum number of borrowings allowed
     * @throws SQLException if a database access error occurs
     */
    public static int getUserMaxEmprunt(String userEmail) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;
        int maxBorrow = 0;

        try {
            // Connection to the DataBase
            connection = getConnection();

            // Prepare the query to retrieve the User's maxborrow
            String query = "SELECT maxborrow FROM User WHERE email = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, userEmail);

            // Execute the query
            resultSet = preparedStatement.executeQuery();

            // Retrieve the maxborrow
            if (resultSet.next()) {
                maxBorrow = resultSet.getInt("maxborrow");
            }
        } finally {
            // closeResources
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

        return maxBorrow;


    }


    /**
     * Retrieves the stock quantity of a book from the database.
     *
     * @param isbn the ISBN of the book
     * @return the stock quantity of the book
     */
    public static int getStockFromDatabase(String isbn) {
        int stock = 0;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();

            String query = "SELECT stock FROM Book WHERE isbn = ?";
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

    /**
     * Updates the maximum number of borrowings allowed for a user in the database.
     *
     * @param userEmail    the email of the user
     * @param newMaxBorrow the new maximum number of borrowings allowed
     * @throws SQLException if a database access error occurs
     */
    public static void updateUserMaxBorrow(String userEmail, int newMaxBorrow) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {

            connection = getConnection();


            String query = "UPDATE User SET maxborrow = ? WHERE email = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, newMaxBorrow);
            preparedStatement.setString(2, userEmail);

            // Execute the Query
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
    /**
     * Updates the status of a user in the database.
     *
     * @param userEmail the email of the user
     * @param newState  the new status of the user
     * @throws SQLException if a database access error occurs
     */
    public static void updateUserStatus(String userEmail, int newState) throws SQLException {
        Connection connection = null;
        PreparedStatement preparedStatement = null;

        try {
            // Obtenez la connexion à la base de données
            connection = getConnection();

            // Préparez la requête SQL pour mettre à jour le nombre d'emprunts maximal de l'utilisateur
            String query = "UPDATE User SET state = ? WHERE email = ?";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setInt(1, newState);
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

    /**
     * Checks if a book is already borrowed by a user.
     *
     * @param userEmail the email of the user
     * @param bookISBN  the ISBN of the book
     * @return true if the book is already borrowed by the user, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public static boolean isBookAlreadyBorrowed(String userEmail, String bookISBN) throws SQLException {
        Connection conn = null;
        PreparedStatement stmt = null;
        ResultSet rs = null;

        try {
            conn = getConnection();
            String query = "SELECT COUNT(*) FROM Borrow WHERE user_email = ? AND book_isbn = ?";
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


    /**
     * Closes the ResultSet, Statement, and Connection to avoid resource leaks.
     *
     * @param rs   the ResultSet to close
     * @param stmt the Statement to close
     * @param conn the Connection to close
     */
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
    /**
     * Retrieves a connection to the database.
     *
     * @return a Connection object
     * @throws SQLException if a database access error occurs
     */
    public static Connection getConnection() throws SQLException {
        return DriverManager.getConnection(URL + DATABASE_NAME, USERNAME, PASSWORD);
    }
    /**
     * Inserts user data into the User table.
     *
     * @param email     the email of the user
     * @param firstname the first name of the user
     * @param lastname  the last name of the user
     * @throws SQLException if a database access error occurs
     */
    public static void insertUserData(String email, String firstname, String lastname) throws SQLException {
        String query = "INSERT INTO User (email, firstname, lastname, state, maxborrow) VALUES (?, ?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, email);
            preparedStatement.setString(2, firstname);
            preparedStatement.setString(3, lastname);
            preparedStatement.setInt(4, 0); // statut
            preparedStatement.setInt(5, 5); // MaxEmprunt


            preparedStatement.executeUpdate();
            System.out.println("Data inserted successfully into User table!");
        }
    }



    /**
     * Inserts borrow data into the Borrow table.
     *
     * @param user_email the email of the user
     * @param bookIsbn   the ISBN of the book
     * @param start      the start date of the borrowing period
     * @param end        the end date of the borrowing period
     * @throws SQLException if a database access error occurs
     */
    public static void insertDataBorrow(String user_email, String bookIsbn, String start, String end) throws SQLException {
        String query = "INSERT INTO Borrow (user_email, book_isbn, start, end) VALUES (?, ?, ?, ?)";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, user_email);
            preparedStatement.setString(2, bookIsbn);
            preparedStatement.setString(3, start);
            preparedStatement.setString(4, end);

            preparedStatement.executeUpdate();
            System.out.println("Data inserted successfully into Emprunt table!");
        }
    }



    /**
     * Retrieves all users from the User table.
     *
     * @return a list of users
     * @throws SQLException if a database access error occurs
     */
    public static List<User> getAllUser() throws SQLException {
        List<User> userList = new ArrayList<>();
        String query = "SELECT email, firstname,lastname, state, maxborrow FROM User";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query);
             ResultSet resultSet = statement.executeQuery()) {

            while (resultSet.next()) {
                String email = resultSet.getString("email");
                String firstname = resultSet.getString("firstname");
                String lastname = resultSet.getString("lastname");
                int state = resultSet.getInt("state");
                int maxborrow = resultSet.getInt("maxborrow");


                // Création de l'objet User avec la nouvelle colonne nbrEmprunt
                User user = new User(email, firstname, lastname, state, maxborrow);
                userList.add(user);
            }
        }
        return userList;
    }
    /**
     * Retrieves borrows associated with a specific user.
     *
     * @param userEmail the email of the user
     * @return a list of borrows associated with the user
     * @throws SQLException if a database access error occurs
     */
    public static List<Borrow> getUserBorrow(String userEmail) throws SQLException {
        List<Borrow> borrows = new ArrayList<>();
        String query = "SELECT * FROM Borrow WHERE user_email = ?";

        try (Connection connection = getConnection();
             PreparedStatement preparedStatement = connection.prepareStatement(query)) {

            preparedStatement.setString(1, userEmail);

            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    // Récupérer les données de l'emprunt depuis le résultat de la requête
                    String bookIsbn = resultSet.getString("book_isbn");
                    LocalDate start = resultSet.getDate("start").toLocalDate();
                    LocalDate end = resultSet.getDate("end").toLocalDate();


                    // Supposez que vous ayez une méthode pour récupérer un utilisateur à partir de son email
                    User user = getUserByEmail(userEmail);

                    // Supposez que vous ayez une méthode pour récupérer un livre à partir de son ISBN
                    Book book = getBookByISBN(bookIsbn);

                    // Créer un nouvel objet Emprunt et l'ajouter à la liste
                    Borrow borrow = new Borrow(user, book, start, end);
                    borrows.add(borrow);
                }
            }
        }
        return borrows;
    }

    /**
     * Retrieves a user object by email.
     *
     * @param email the email of the user
     * @return a User object
     * @throws SQLException if a database access error occurs
     */
    public static User getUserByEmail(String email) throws SQLException {
        User user = null;
        String query = "SELECT * FROM User WHERE email = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, email);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String firstname = resultSet.getString("firstname");
                    String lastname = resultSet.getString("lastname");
                    int state = resultSet.getInt("state");
                    int maxborrow = resultSet.getInt("maxborrow");
                    user = new User(email, firstname, lastname, state, maxborrow);
                }
            }
        }
        return user;
    }

    /**
     * Retrieves a book object by ISBN.
     *
     * @param isbn the ISBN of the book
     * @return a Book object
     * @throws SQLException if a database access error occurs
     */
    public static Book getBookByISBN(String isbn) throws SQLException {
        Book book = null;
        String query = "SELECT * FROM Book WHERE isbn = ?";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {
            statement.setString(1, isbn);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    String title = resultSet.getString("title");
                    String author = resultSet.getString("author");
                    book = new Book(isbn, title, author);
                }
            }
        }
        return book;
    }

    /**
     * Retrieves the top 20 popular books in the last 30 days.
     *
     * @return a list of the top 20 popular books
     * @throws SQLException if a database access error occurs
     */
    public static List<Book> getTop20PopularBooksLast30Days() throws SQLException {
        List<Book> popularBooks = new ArrayList<>();

        // Obtenir la date d'il y a 30 jours à partir de la date actuelle
        LocalDate thirtyDaysAgo = LocalDate.now().minusDays(30);

        String query = "SELECT l.isbn, l.title, l.author, COUNT(h.book_isbn) AS borrow_count " +
                "FROM Book l " +
                "JOIN History h ON l.isbn = h.book_isbn " +
                "WHERE h.start >= ? " +
                "GROUP BY l.isbn, l.title, l.author " +
                "ORDER BY borrow_count DESC " +
                "LIMIT 20";

        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement(query)) {

            statement.setDate(1, Date.valueOf(thirtyDaysAgo));

            try (ResultSet resultSet = statement.executeQuery()) {
                while (resultSet.next()) {
                    String isbn = resultSet.getString("isbn");
                    String title = resultSet.getString("title");
                    String author = resultSet.getString("author");

                    Book livre = new Book(title, author, isbn);
                    popularBooks.add(livre);
                }
            }
        }
        return popularBooks;
    }

    /**
     * Retrieves the count of late returns for a user.
     *
     * @param email the email of the user
     * @return the count of late returns
     * @throws SQLException if a database access error occurs
     */
    public static int getUserLateCount(String email) throws SQLException {
        int lateCount = 0;
        Connection connection = null;
        PreparedStatement preparedStatement = null;
        ResultSet resultSet = null;

        try {
            connection = getConnection();
            String query = "SELECT COUNT(*) FROM History WHERE user_email = ? AND delay = 1";
            preparedStatement = connection.prepareStatement(query);
            preparedStatement.setString(1, email);
            resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                lateCount = resultSet.getInt(1);
            }
        } finally {
            if (resultSet != null) resultSet.close();
            if (preparedStatement != null) preparedStatement.close();
            if (connection != null) connection.close();
        }

        return lateCount;
    }



}
