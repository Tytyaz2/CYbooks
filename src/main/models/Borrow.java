package main.models;

import java.time.LocalDate;

public class Borrow {
    private User user;
    private Book book;
    private LocalDate startDate;
    private LocalDate endDate;

    public Borrow(User user, Book book, LocalDate startDate, LocalDate endDate) {
        this.user = user;
        this.book = book;
        this.startDate = startDate;
        this.endDate = endDate;
    }


    public Book getBook() {
        return book;
    }
    public String getBookTitle(){
        return book.getTitle();
    }

    public String getBorrowerName() {
        return user.getFirstName() + " " + user.getLastName();
    }



    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public User getUser() {
        return user;
    }

    public void setBook(Book book) {
        this.book = book;
    }

    public void setEndDate(LocalDate endDate) {
        this.endDate = endDate;
    }

    public void setStartDate(LocalDate startDate) {
        this.startDate = startDate;
    }

    public void setUser(User user) {
        this.user = user;
    }
    // Getters and setters
}
