package main.controllers;
import main.models.*;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;
import java.time.LocalDate;

public class MainControllersTerminal {

    private static List<Book> books;
    private static int startIndex = 0;
    private static final Scanner scanner = new Scanner(System.in);
    private static final int pageSize = 20;


    public static void main(String[] args) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        while (true) {
            System.out.println("1. Rechercher des livres");
            System.out.println("2. Afficher les utilisateurs");
            System.out.println("3. Rechercher des utilisateurs");
            System.out.println("4. Ajout");
            System.out.println("5. Emprunter un livre");

            System.out.println("6. Quitter");
            System.out.print("Choisissez une option : ");
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consomme la nouvelle ligne

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
                    System.out.println("Au revoir !");
                    scanner.close();
                    System.exit(0);
                    break;
                default:
                    System.out.println("Option invalide.");
            }
        }
    }

    public static void addNewUser() {
        try {
            System.out.println("Veuillez entrer le nom de famille de l'utilisateur : ");
            String lastName = scanner.nextLine();

            System.out.println("Veuillez entrer le prénom de l'utilisateur : ");
            String firstName = scanner.nextLine();

            System.out.println("Veuillez entrer l'adresse email de l'utilisateur : ");
            String email = scanner.nextLine();

            // Valider les données
            if (lastName.isEmpty() || firstName.isEmpty() || email.isEmpty()) {
                System.out.println("Veuillez remplir tous les champs.");
            } else if (!User.isValidEmail(email)) {
                System.out.println("Veuillez entrer une adresse email valide.");
            } else {
                // Ajouter l'utilisateur à la base de données
                DatabaseConnection.insertUserData(new User(email, firstName, lastName, 0, 5));
                System.out.println("Nouvel adhérent ajouté : " + lastName + " " + firstName);
            }
        } catch (SQLException e) {
            // Afficher un message d'erreur si l'ajout échoue
            System.err.println("Erreur lors de l'ajout de l'utilisateur : " + e.getMessage());
        }
    }

    private static void searchBooks(Scanner scanner) {
        System.out.print("Entrez la catégorie : ");
        String categorie = scanner.nextLine();
        System.out.print("Entrez le terme de recherche : ");
        String searchQuery = scanner.nextLine();
        System.out.print("Entrez l'index de départ : ");
        int start = scanner.nextInt();
        System.out.print("Entrez le nombre de livres à récupérer : ");
        int number = scanner.nextInt();
        scanner.nextLine(); // Consomme la nouvelle ligne

        books = SearchBookAPI.search(categorie, searchQuery, start, number);
        if (books == null || books.isEmpty()) {
            System.out.println("Aucun livre trouvé.");
        } else {
            for (Book book : books) {
                System.out.println(book);
            }
        }
    }

    private static void displayUsers() throws SQLException {
        List<User> users = DatabaseConnection.getAllUser();
        for (User user : users) {
            System.out.println(user);
        }
    }

    public static void handleSearchUser(String searchPattern) {
        if (searchPattern.isBlank()) {
            System.out.println("Le motif de recherche est vide.");
        } else {
            searchUsersInDatabase("%" + searchPattern + "%");
        }
    }

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

    public static void selectUserAndBookAndHandleBorrow(List<User> userList) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        // Afficher la liste des utilisateurs avec un index pour chaque utilisateur
        System.out.println("Select a user:");
        for (int i = 0; i < userList.size(); i++) {
            User user = userList.get(i);
            System.out.println((i + 1) + ". " + user.getFirstName() + " " + user.getLastName());
        }

        // Demander à l'utilisateur de choisir un utilisateur
        System.out.print("Enter the number corresponding to the user: ");
        int userIndex = scanner.nextInt();

        // Vérifier si l'index est valide
        if (userIndex < 1 || userIndex > userList.size()) {
            System.out.println("Invalid user selection.");
            return;
        }

        // Récupérer l'utilisateur sélectionné
        User selectedUser = userList.get(userIndex - 1);

        // Afficher la liste des livres avec un index pour chaque livre
        System.out.println("Select a book:");
        for (int i = 0; i < books.size(); i++) {
            Book book = books.get(i);
            System.out.println((i + 1) + ". " + book.getTitle() + " by " + book.getAuthors());
        }

        // Demander à l'utilisateur de choisir un livre
        System.out.print("Enter the number corresponding to the book: ");
        int bookIndex = scanner.nextInt();

        // Vérifier si l'index est valide
        if (bookIndex < 1 || bookIndex > books.size()) {
            System.out.println("Invalid book selection.");
            return;
        }

        // Appeler la fonction handleBorrow avec l'utilisateur sélectionné et le livre sélectionné
        handleBorrow(selectedUser,books.get(bookIndex - 1));
    }
    public static void handleBorrow(User selectedUser, Book selectedBook) throws SQLException {
        Scanner scanner = new Scanner(System.in);

        // Récupération de la date actuelle
        LocalDate startDate = LocalDate.now();

        // Vérification si un utilisateur et au moins un livre ont été sélectionnés
        if (selectedUser == null) {
            System.out.println("Error: Selection Required");
            System.out.println("Please select a user.");
            return;
        }

        // Vérification si l'utilisateur a atteint sa limite d'emprunts
        int remainingBorrows = selectedUser.getMaxBorrow();
        if (remainingBorrows <= 0) {
            System.out.println("Error: Borrow Limit Reached");
            System.out.println("You have reached the borrow limit.");
            return;
        }

        // Vérification si le livre existe dans la base de données
        if (!DatabaseConnection.isBookExists(selectedBook)) {
            // Insérer le livre dans la base de données s'il n'existe pas déjà
            DatabaseConnection.insertBook(selectedBook);
        }

        // Vérification si l'utilisateur a déjà emprunté ce livre
        if (DatabaseConnection.isBookAlreadyBorrowed(selectedUser, selectedBook)) {
            System.out.println("Error: Book Already Borrowed");
            System.out.println("You have already borrowed the book \"" + selectedBook.getTitle() + "\".");
            return;
        }

        // Vérification si le livre est disponible en stock
        int currentStock = DatabaseConnection.getBookStock(selectedBook);
        if (currentStock <= 0) {
            System.out.println("Error: Out of Stock");
            System.out.println("The selected book is not available in the stock.");
            return;
        }

        // Date de fin = 30 jours après la date de début
        LocalDate endDate = startDate.plusDays(30);

        // Insertion de l'emprunt dans la base de données
        DatabaseConnection.insertDataBorrow(new Borrow(selectedUser, selectedBook, startDate, endDate));

        // Mise à jour du stock dans la base de données en le réduisant de 1
        int newStock = currentStock - 1;
        selectedBook.setStock(newStock);
        DatabaseConnection.updateStock(selectedBook.getIsbn(), newStock);

        // Affichage d'un message de succès
        System.out.println("Borrow of the book \"" + selectedBook.getTitle() + "\" added successfully for user " + selectedUser.getFirstName() + " " + selectedUser.getLastName());

        // Décrémentation du nombre maximal d'emprunts autorisés pour l'utilisateur
        selectedUser.setMaxBorrow(selectedUser.getMaxBorrow() - 1);


        // Affichage d'un message de succès global après l'emprunt de tous les livres
        System.out.println("Success: Borrows Added Successfully");
        System.out.println("Borrows have been added successfully to the database.");

        // Mise à jour de la limite d'emprunts de l'utilisateur dans la base de données
        DatabaseConnection.updateUserMaxBorrow(selectedUser, selectedUser.getMaxBorrow());
    }
}

