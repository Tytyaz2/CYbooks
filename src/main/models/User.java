package main.models;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * The User class represents a user in your system.
 */
public class User {
    private String email;
    private final String firstName;
    private final String lastName;
    private int state;
    private int maxBorrow; // Initialized to 5

    /**
     * Creates a new user with the specified information.
     *
     * @param email     the email address of the user
     * @param firstName the first name of the user
     * @param lastName  the last name of the user
     * @param state     the state of the user
     * @param maxBorrow the maximum number of borrows allowed for the user
     */
    public User(String email, String firstName, String lastName, int state, int maxBorrow) {
        this.email = email;
        this.firstName = firstName;
        this.lastName = lastName;
        this.state = state;
        this.maxBorrow = maxBorrow;
    }

    /**
     * Returns the first name of the user.
     *
     * @return the first name
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * Returns the last name of the user.
     *
     * @return the last name
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * Returns the email address of the user.
     *
     * @return the email address
     */
    public String getEmail() {
        return email;
    }

    /**
     * Returns the state of the user.
     *
     * @return the state
     */
    public int getState() {
        return state;
    }

    /**
     * Sets the state of the user.
     *
     * @param state the state to set
     */
    public void setState(int state) {
        this.state = state;
    }

    /**
     * Returns the maximum number of borrows allowed for the user.
     *
     * @return the maximum number of borrows
     */
    public int getMaxBorrow() {
        return maxBorrow;
    }

    /**
     * Sets the maximum number of borrows allowed for the user.
     *
     * @param maxBorrow the maximum number of borrows to set
     */
    public void setMaxBorrow(int maxBorrow) {
        this.maxBorrow = maxBorrow;
    }

    /**
     * Method to validate the format of the email.
     *
     * @param email the email to validate
     * @return true if the email format is valid, false otherwise
     */
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[\\p{L}0-9._%+-]+@[\\p{L}0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    public String toString() {
        return "Nom: " + lastName + ", Prénom: " + firstName + ", Email: " + email + ", Statut: " + state + ", Max emprunts: " + maxBorrow;
    }


    /**
     * Sets the email address of the user.
     *
     * @param newEmail the new email address to set
     */
    public void setEmail(String newEmail) {
        this.email = newEmail;
    }

    /**
     * Checks if the user has any overdue borrows based on the current date.
     *
     * @param currentDate the current date
     * @return true if the user has at least one overdue borrow, false otherwise
     * @throws SQLException if a database access error occurs
     */
    public boolean hasOverdueBorrow(LocalDate currentDate) throws SQLException {
        List<Borrow> borrows = DatabaseConnection.getUserBorrow(this);
        for (Borrow borrow : borrows) {
            if (borrow.getEndDate().isBefore(currentDate)) {
                return true; // There is at least one overdue borrow
            }
        }
        return false; // No overdue borrows
    }
}
