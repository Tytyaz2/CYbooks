package main.models;

import java.time.LocalDate;

/**
 * Represents an instance of book borrowing.
 * Contains information about the user who borrowed the book,
 * the book itself, and the start and end dates of the borrowing period.
 */
public class Borrow {
    private User user; // The user who borrowed the book
    private Book book; // The book that was borrowed
    private LocalDate startDate; // The start date of the borrowing period
    private LocalDate endDate; // The end date of the borrowing period

    /**
     * Constructs a new Borrow instance with the specified user, book, start date, and end date.
     *
     * @param user      the user who borrowed the book
     * @param book      the book that was borrowed
     * @param startDate the start date of the borrowing period
     * @param endDate   the end date of the borrowing period
     */
    public Borrow(User user, Book book, LocalDate startDate, LocalDate endDate) {
        this.user = user;
        this.book = book;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    /**
     * Returns the book that was borrowed.
     *
     * @return the book that was borrowed
     */
    public Book getBook() {
        return book;
    }

    /**
     * Returns the title of the book that was borrowed.
     *
     * @return the title of the borrowed book
     */
    public String getBookTitle() {
        return book.getTitle();
    }

    /**
     * Returns the name of the borrower (user who borrowed the book).
     *
     * @return the name of the borrower
     */
    public String getBorrowerName() {
        return user.getFirstName() + " " + user.getLastName();
    }

    /**
     * Returns the end date of the borrowing period.
     *
     * @return the end date of the borrowing period
     */
    public LocalDate getEndDate() {
        return endDate;
    }

    /**
     * Returns the start date of the borrowing period.
     *
     * @return the start date of the borrowing period
     */
    public LocalDate getStartDate() {
        return startDate;
    }

    /**
     * Returns the user who borrowed the book.
     *
     * @return the user who borrowed the book
     */
    public User getUser() {
        return user;
    }

    /**
     * Sets the book that was borrowed.
     *
     * @param book the book to set
     */
    public void setBook(Book book) {
        this.book = book;
    }

    /**
     * Sets the end date of the borrowing period.
     *
     * @param endDate the end date to set
     */
    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    /**
     * Sets the start date of the borrowing period.
     *
     * @param startDate the start date to set
     */
    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    /**
     * Sets the user who borrowed the book.
     *
     * @param user the user to set
     */
    public void setUser(User user) {
        this.user = user;
    }
}
