import javafx.collections.ObservableList;
import main.models.*;
import main.API.SearchBookAPI;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;
import main.models.User;
import main.dataBase.DatabaseConnection;
import main.models.Book;
import java.sql.Connection;
import java.sql.PreparedStatement;


/**
 * Main class to run the application and interact with users.
 * Provides a command-line interface for various functionalities such as searching for books,
 * adding new users, borrowing books, etc.
 */

public class MainTerminal {

    private static List<Book> books;
    protected static ObservableList<Book> borrowList;
    private static final Scanner scanner = new Scanner(System.in);

    /**
     * Main method to start the application.
     * Provides a command-line interface for interacting with users.
     *
     * @param args command-line arguments (not used)
     * @throws SQLException if there is an error with database operations
     */
    public static void main(String[] args) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1. Rechercher des livres");
            System.out.println("2. Afficher les utilisateurs");
            System.out.println("3. Rechercher des utilisateurs");
            System.out.println("4. Ajout");
            System.out.println("5. Emprunter un livre");
            System.out.println("6. Selectionner un utilisateur");

            System.out.println("7. Quitter");
            System.out.print("Choisissez une option : ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume the new line

            switch (choice) {
                case 1:
                    searchBooks(scanner);
                    break;
                case 2:
                    displayUsers();
                    break;
                case 3:
                    System.out.println("Entrez le motif de recherche pour l'utilisateur : ");
                    String searchPattern = scanner.nextLine();
                    handleSearchUser(searchPattern);
                    break;
                case 4:
                    addNewUser();
                    break;
                case 5:
                    List<User> userList;
                    try {
                        userList = DatabaseConnection.getAllUser();
                    } catch (SQLException e) {
                        throw new RuntimeException(e);
                    }
                    selectUserAndBookAndHandleBorrow(userList);
                case 6:
                    selectUserAndShowInfo();
                    break;
                case 7:
                    System.out.println("Au revoir !");
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Option invalide.");
            }
        }
    }
    /**
     * Adds a new user to the system.
     * Prompts the user to enter the user's last name, first name, and email address.
     * Validates the input data and adds the user to the database if valid.
     *
     * @param 'scanner' scanner object to read user input
     */
    public static void addNewUser() {
        try {
            System.out.println("Veuillez entrer le nom de famille de l'utilisateur : ");
            String lastName = scanner.nextLine();

            System.out.println("Veuillez entrer le prénom de l'utilisateur : ");
            String firstName = scanner.nextLine();

            System.out.println("Veuillez entrer l'adresse email de l'utilisateur : ");
            String email = scanner.nextLine();

            // Validate data
            if (lastName.isEmpty() || firstName.isEmpty() || email.isEmpty()) {
                System.out.println("Veuillez remplir tous les champs.");
            } else if (!User.isValidEmail(email)) {
                System.out.println("Veuillez entrer une adresse email valide.");
            } else {
                // Add user to database
                DatabaseConnection.insertUserData(new User(email, firstName, lastName, 0, 5));
                System.out.println("Nouvel adhérent ajouté : " + lastName + " " + firstName);
            }
        } catch (SQLException e) {
            // Show an error message if the addition fails
            System.err.println("Erreur lors de l'ajout de l'utilisateur : " + e.getMessage());
        }
    }
    /**
     * Searches for books based on user input.
     * Prompts the user to enter the category, search query, start index, and number of books to retrieve.
     * Displays the search results or a message if no books are found.
     *
     * @param scanner scanner object to read user input
     */
    private static void searchBooks(Scanner scanner) {
        System.out.print("Entrez la catégorie : ");
        String categorie = scanner.nextLine();
        System.out.print("Entrez le terme de recherche : ");
        String searchQuery = scanner.nextLine();
        System.out.print("Entrez l'index de départ : ");
        int start = scanner.nextInt();
        System.out.print("Entrez le nombre de livres à récupérer : ");
        int number = scanner.nextInt();
        scanner.nextLine(); // Consume the new line

        books = SearchBookAPI.search(categorie, searchQuery, start, number);
        if (books.isEmpty()) {
            System.out.println("Aucun livre trouvé.");
        } else {
            for (Book book : books) {
                System.out.println(book);
            }
        }
    }
    /**
     * Displays all users in the system.
     * Retrieves user data from the database and prints it to the console.
     *
     * @throws SQLException if there is an error with database operations
     */
    private static void displayUsers() throws SQLException {
        List<User> users = DatabaseConnection.loadUsers();
        for (User user : users) {
            System.out.println(user);
        }
    }
    /**
     * Handles searching for users based on a search pattern.
     * Prompts the user for a search pattern and performs the search.
     *
     * @param searchPattern the search pattern to use for finding users
     */
    public static void handleSearchUser(String searchPattern) {
        if (searchPattern.isBlank()) {
            System.out.println("Le motif de recherche est vide.");
        } else {
            searchUsersInDatabase("%" + searchPattern + "%");
        }
    }
    /**
     * Searches for users in the database based on a search pattern.
     * Retrieves and prints user data that matches the search pattern.
     *
     * @param searchPattern the search pattern to use for finding users
     */
    public static void searchUsersInDatabase(String searchPattern) {
        try {
            List<User> users = DatabaseConnection.searchUsers(searchPattern);
            if (users.isEmpty()) {
                System.out.println("Aucun utilisateur trouvé pour le motif de recherche : " + searchPattern);
            } else {
                System.out.println("Utilisateurs trouvés pour le motif de recherche 3'" + searchPattern + "' :");
                for (User user : users) {
                    System.out.println(user.getFirstName() + " " + user.getLastName() + " - " + user.getEmail());
                }
            }
        } catch (SQLException e) {
            System.err.println("Erreur lors de la recherche d'utilisateurs : " + e.getMessage());
        }
    }
    /**
     * Selects a user and a book, then handles borrowing the selected book for the selected user.
     * Prompts the user to choose a user and a book, then processes the borrowing transaction.
     *
     * @param userList the list of users to choose from
     * @throws SQLException if there is an error with database operations
     */
    public static void selectUserAndBookAndHandleBorrow(List<User> userList) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        // Show list of users with an index for each user
        System.out.println("Sélectionnez un utilisateur:");
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            System.out.println((i + 1) + ". " + user.getFirstName() + " " + user.getLastName());
        }

        // Ask user to choose a user
        System.out.print("Saisissez le numéro correspondant à l'utilisateur :");
        int userIndex = scanner.nextInt();

        // Check if the index is valid
        if (userIndex < 1 || userIndex > userList.size()) {
            System.out.println("Sélection d'utilisateur non valide.");
            return;
        }

        // Retrieve selected user
        User selectedUser = userList.get(userIndex - 1);

        // Show list of books with an index for each book
        System.out.println("Sélectionnez un livre :");
        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);
            System.out.println((i + 1) + ". " + book.getTitle() + " by " + book.getAuthors());
        }

        // Ask the user to choose a book
        System.out.print("Saisissez le numéro correspondant au livre :");
        int bookIndex = scanner.nextInt();

        // Check if the index is valid
        if (bookIndex < 1 || bookIndex > books.size()) {
            System.out.println("Sélection de livres non valide.");
            return;
        }

        // Call handleBorrow function with selected user and selected book
        handleBorrow(selectedUser,books.get(bookIndex - 1));
    }

    /**
     * Handles the borrowing process for a selected user and book.
     * Validates user and book information, updates the database, and prints appropriate messages.
     *
     * @param selectedUser the user who is borrowing the book
     * @param selectedBook the book to be borrowed
     * @throws SQLException if there is an error with database operations
     */
    public static void handleBorrow(User selectedUser, Book selectedBook) throws SQLException {
        // Retrieving the current date
        LocalDate startDate = LocalDate.now();

        // Checking if a user and at least one book have been selected
        if (selectedUser == null) {
            System.out.println("Erreur sélection requise");
            System.out.println("Veuillez sélectionner un utilisateur.");
            return;
        }

        // Checking if the user has reached their borrowing limit
        int remainingBorrows = selectedUser.getMaxBorrow();
        if (remainingBorrows <= 0) {
            System.out.println("Erreur limite d'emprunt atteinte");
            System.out.println("Vous avez atteint la limite d'emprunt.");
            return;
        }

        // Checking if the book exists in the database
        if (!DatabaseConnection.isBookExists(selectedBook)) {
            // Insérer le livre dans la base de données s'il n'existe pas déjà
            DatabaseConnection.insertBook(selectedBook);
        }

        // Checking if the user has already borrowed this book
        if (DatabaseConnection.isBookAlreadyBorrowed(selectedUser, selectedBook)) {
            System.out.println("Erreur livre déjà emprunté");
            System.out.println("Vous avez déjà emprunté le livre \"" + selectedBook.getTitle() + "\".");
            return;
        }

        // Checking if the book is available in stock
        int currentStock = DatabaseConnection.getBookStock(selectedBook);
        if (currentStock <= 0) {
            System.out.println("Erreur : En rupture de stock");
            System.out.println("The selected book is not available in the stock.");
            return;
        }

        // End date = 30 days after start date
        LocalDate endDate = startDate.plusDays(30);

        // Insertion of the loan into the database
        DatabaseConnection.insertDataBorrow(new Borrow(selectedUser, selectedBook, startDate, endDate));

        // Updating the stock in the database by reducing it by 1
        int newStock = currentStock - 1;
        selectedBook.setStock(newStock);
        DatabaseConnection.updateStock(selectedBook.getIsbn(), newStock);

        // Displaying a success message
        System.out.println("Emprunter le livre \"" + selectedBook.getTitle() + "\" ajouté avec succès pour l'utilisateur " + selectedUser.getFirstName() + " " + selectedUser.getLastName());

        // Decrementing the maximum number of borrowings authorized for the user
        selectedUser.setMaxBorrow(selectedUser.getMaxBorrow() - 1);


        // Displaying an overall success message after borrowing all books
        System.out.println("Succès emprunts ajoutés avec succès");
        System.out.println("Les emprunts ont été ajoutés avec succès à la base de données.");

        // Updating the user's borrowing limit in the database
        DatabaseConnection.updateUserMaxBorrow(selectedUser, selectedUser.getMaxBorrow());
    }
    /**
     * Selects a user and shows their information.
     * Prompts the user to choose a user from a list, then displays the user's information and options to perform actions.
     *
     * @throws SQLException if there is an error with database operations
     */
    public static void selectUserAndShowInfo() throws SQLException {
        List<User> userList = DatabaseConnection.getAllUser();
        Scanner scanner = new Scanner(System.in);

        // Show list of users with an index for each user
        System.out.println("Sélectionnez un utilisateur :");
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            System.out.println((i + 1) + ". " + user.getFirstName() + " " + user.getLastName());
        }

        // Ask user to choose a user
        System.out.print("Saisissez le numéro correspondant à l'utilisateur : ");
        int userIndex = scanner.nextInt();

        // Check if the index is valid
        if (userIndex < 1 || userIndex > userList.size()) {
            System.out.println("Sélection d'utilisateur non valide.");
            return;
        }

        // Retrieve selected user
        User selectedUser = userList.get(userIndex - 1);
        handleUserInfo(selectedUser);
    }
    /**
     * Handles displaying and managing information for a selected user.
     * Displays user information and provides options for returning books, banning the user, modifying user details, viewing borrows, and viewing borrowing history.
     *
     * @param selectedUser the user whose information is being managed
     * @throws SQLException if there is an error with database operations
     */
    public static void handleUserInfo(User selectedUser) throws SQLException {

        // Show all information for selected user
        System.out.println(selectedUser.toString());

        System.out.println("\nOptions:");
        System.out.println("1. Rendre un livre");
        System.out.println("2. Bannir l'utilisateur");
        System.out.println("3. Modifier les informations");
        System.out.println("4. Voir les emprunts");
        System.out.println("5. Voir l'historique d'emprunt");
        System.out.println("6. Retourner à la page principale");

        // Ask the user to choose an option
        System.out.print("\nSaisissez le numéro correspondant à l'option :");
        int option = scanner.nextInt();

        // Execute the corresponding action depending on the chosen option
        switch (option) {
            case 1:
                giveBackSelectedBooks(selectedUser,borrowList);
                break;
            case 2:
                banUser(selectedUser);
                break;
            case 3:
                modifyUser(selectedUser);
            case 4:
                loadBorrowedBooks(selectedUser.getEmail());
                handleUserInfo(selectedUser);
            case 5:
                loadHistory(selectedUser);
                break;
            case 6:
                break;
            default:
                System.out.println("Option invalide.");
        }

    }

    /**
     * Loads borrowed books for a user and displays them.
     *
     * @param email the email of the user whose borrowed books are to be loaded
     */
    protected static void loadBorrowedBooks(String email) {
        try {
            borrowList = DatabaseConnection.loadBorrowedBooks(email);
            // View borrowed books in the terminal
            for (Book book : borrowList) {
                System.out.println("Titre: " + book.getTitle() + ", Author(s): " + book.getAuthors() + ", ISBN: " + book.getIsbn());
            }
            System.out.println("\n");
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * Handles returning selected books for a user.
     * Prompts the user to select a book to return from a list and processes the return transaction.
     *
     * @param user  the user who is returning books
     * @param books the list of books to choose from
     */
    private static void giveBackSelectedBooks(User user, List<Book> books) {
        try {
            Scanner scanner = new Scanner(System.in);

            // Display the list of books to return with an index for each book
            System.out.println("Sélectionnez un livre à redonner :");
            for (int i = 0; i < books.size(); i++) {
                Book book = books.get(i);
                System.out.println((i + 1) + ". Title: " + book.getTitle());
            }

            // Ask the user to choose a book to return
            System.out.print("Saisissez le numéro correspondant au livre à rendre :");
            int bookIndex = scanner.nextInt();

            // Check if the index is valid
            if (bookIndex < 1 || bookIndex > books.size()) {
                System.out.println("Sélection de livres non valide.");
                return;
            }

            // Retrieve selected book
            Book selectedBook = books.get(bookIndex - 1);

            // Call the method in the DatabaseConnection class to handle SQL transactions
            DatabaseConnection.giveBackSelectedBooks(user, List.of(selectedBook));
            int updatedMaxBorrow = DatabaseConnection.getUserMaxBorrow(user) + 1;

            // Show a confirmation message
            System.out.println("Livre renvoyé avec succès : " + selectedBook.getTitle());

            // Show number of remaining loans
            System.out.println("Emprunts restants : " + (5 - updatedMaxBorrow));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
    /**
     * Bans a user by updating their status in the database.
     * Prompts for confirmation before banning the user.
     *
     * @param user the user to be banned
     */
    private static void banUser(User user) {
        if (user != null) {
            Scanner scanner = new Scanner(System.in);

            // Request user confirmation
            System.out.print("Voulez-vous vraiment bannir cet utilisateur ? (Oui/Non): ");
            String response = scanner.nextLine();

            if (response.equalsIgnoreCase("Oui")) {
                // Update user status to ban them (state 3)

                // Update in database
                try (Connection connection = DatabaseConnection.getConnection();
                     PreparedStatement updateUserStatement = connection.prepareStatement("UPDATE User SET state = ? WHERE email = ?")) {

                    updateUserStatement.setInt(1, 3);
                    updateUserStatement.setString(2, user.getEmail());
                    updateUserStatement.executeUpdate();

                    // Show a confirmation message
                    System.out.println("L'utilisateur a été banni avec succès.");
                } catch (SQLException e) {
                    e.printStackTrace();
                    System.out.println("Une erreur s'est produite lors de la mise à jour de l'utilisateur.");
                }
            } else {
                System.out.println("Opération d'interdiction annulée.");
            }
        } else {
            System.out.println("Aucun utilisateur sélectionné.");
        }
    }

    /**
     * Modifies the details of a user.
     * Prompts the user to choose which field to modify and updates the user information accordingly.
     *
     * @param user the user whose details are to be modified
     * @throws SQLException if there is an error with database operations
     */
    public static void modifyUser(User user) throws SQLException {
        System.out.println("Informations sur l'utilisateur actuel:");
        System.out.println("1. Nom de famille: " + user.getLastName());
        System.out.println("2. Prénom: " + user.getFirstName());
        System.out.println("3. Email: " + user.getEmail());

        System.out.println("Saisissez le numéro correspondant au champ que vous souhaitez modifier (ou 0 pour annuler):");
        int choice = scanner.nextInt();
        scanner.nextLine(); // Consume newline left-over

        switch (choice) {
            case 1:
                System.out.print("Entrez un nouveau nom de famille: ");
                String newLastName = scanner.nextLine();
                user = updateUser(user, newLastName, user.getFirstName(), user.getEmail());
                break;
            case 2:
                System.out.print("Entrez un nouveau prénom: ");
                String newFirstName = scanner.nextLine();
                user = updateUser(user, user.getLastName(), newFirstName, user.getEmail());
                break;
            case 3:
                System.out.print("Entrez un nouvel e-mail: ");
                String newEmail = scanner.nextLine();
                if (!User.isValidEmail(newEmail)) {
                    System.out.println("Format d'email invalide. S'il vous plaît, mettez une adresse email valide.");
                    return;
                }
                user = updateUser(user, user.getLastName(), user.getFirstName(), newEmail);
                break;
            case 0:
                System.out.println("Personnaliser la barre d'outils...");
                return;
            default:
                System.out.println("Choix invalide.");
                return;
        }

        System.out.println("Informations utilisateur mises à jour:");
        System.out.println("Nom de famille: " + user.getLastName());
        System.out.println("Prénom: " + user.getFirstName());
        System.out.println("Email: " + user.getEmail());
    }
    /**
     * Updates the user's information in the database with the provided last name, first name, and email.
     *
     * @param user      The user object containing the current user information.
     * @param lastName  The new last name for the user.
     * @param firstName The new first name for the user.
     * @param email     The new email for the user.
     * @return The updated user object with the new email set.
     * @throws SQLException If an SQL error occurs while updating the user information in the database.
     */
    private static User updateUser(User user, String lastName, String firstName, String email) throws SQLException {
        user.setEmail(email);
        DatabaseConnection.modifyUser(user, email);
        return user;
    }
    /**
     * Loads and displays the borrowing history of the specified user.
     *
     * @param user The user whose borrowing history is to be loaded and displayed.
     */
    private static void loadHistory(User user) {
        try {
            // Load the user's borrowing history
            List<Book> historyList = DatabaseConnection.loadBorrowHistory(user.getEmail());

            // View borrowing history in the console
            System.out.println("Historique d'emprunt pour" + user.getFirstName() + " " + user.getLastName() + ":");
            if (historyList.isEmpty()) {
                System.out.println("Aucun historique d'emprunt trouvé.");
            } else {
                for (Book book : historyList) {
                    System.out.println("- Titre: " + book.getTitle() + ", Emprunté le: " + book.getDateBorrow() + ", retourner le: " + book.getDateGB());
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


}