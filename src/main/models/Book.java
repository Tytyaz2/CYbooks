package main.models;

import java.sql.SQLException;

/**
 * Represents an instance of book.
 */
public class Book {
    private String title;
    private String authors;
    private String isbn;


    private String dateBorrow;
    private String dateGB;

    private int stock;

    /**
     * Constructs a new {@code Book} instance with the specified details.
     *
     * @param title      the title of the book
     * @param authors    the authors of the book
     * @param isbn       the International Standard Book Number (ISBN) of the book
     * @param dateBorrow the date the book was borrowed
     * @param dateGB     the date the book is to be returned (date de grande bibliotheque)
     * @throws SQLException if there is an error accessing the database to retrieve stock information
     */
    public Book(String title, String authors, String isbn, String dateBorrow, String dateGB) throws SQLException {
        this.title = title;
        this.authors = authors;
        this.isbn = isbn;
        this.dateBorrow = dateBorrow;
        this.dateGB = dateGB;
        this.stock = DatabaseConnection.getStockFromDatabase(DatabaseConnection.getBookByISBN(isbn));
    }


    /**
     * Constructs a new {@code Book} instance with the specified title, authors, and ISBN.
     * The stock is initialized to 10 by default.
     *
     * @param title   the title of the book
     * @param authors the authors of the book
     * @param isbn    the International Standard Book Number (ISBN) of the book
     */
    public Book(String title, String authors, String isbn) {
        this.title = title;
        this.authors = authors;
        this.isbn = isbn;
        this.stock = 10;
    }


    /**
     * Returns the current stock of the book.
     *
     * @return the stock of the book
     */
    public int getStock() {
        return stock;
    }


    /**
     * Sets the stock of the book to the specified value.
     *
     * @param stock the new stock value to set
     */
    public void setStock(int stock) {
        this.stock = stock;
    }


    /**
     * Returns the date when the book was borrowed.
     *
     * @return the borrow date of the book
     */
    public String getDateBorrow() {
        return dateBorrow;
    }


    /**
     * Sets the borrow date of the book to the specified value.
     *
     * @param dateBorrow the new borrow date to set
     */
    public void setDateBorrow(String dateBorrow) {
        this.dateBorrow = dateBorrow;
    }


    /**
     * Returns the date when the book is to be returned (date de grande bibliothèque).
     *
     * @return the return date of the book
     */
    public String getDateGB() {
        return  dateGB;
    }


    /**
     * Sets the return date of the book (date de grande bibliothèque) to the specified value.
     *
     * @param dateGB the new return date to set
     */
    public void setDateGB(String dateGB) {
        this.dateGB = dateGB;
    }


    /**
     * Returns the title of the book.
     *
     * @return the title of the book
     */
    public String getTitle() {
        return title;
    }


    /**
     * Sets the title of the book to the specified value.
     *
     * @param title the new title to set
     */
    public void setTitle(String title) {
        this.title = title;
    }


    /**
     * Returns the authors of the book.
     *
     * @return the authors of the book
     */
    public String getAuthors() {
        return authors;
    }

    /**
     * Returns the International Standard Book Number (ISBN) of the book.
     *
     * @return the ISBN of the book
     */
    public String getIsbn() {
        return isbn;
    }


    /**
     * Sets the International Standard Book Number (ISBN) of the book to the specified value.
     *
     * @param isbn the new ISBN to set
     */
    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }


    /**
     * Returns a string representation of the book, including its title and authors.
     *
     * @return a string representation of the book
     */
    @Override
    public String toString() {
        return this.title + "," + this.authors;
    }


}
