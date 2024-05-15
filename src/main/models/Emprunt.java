package main.models;

import java.time.LocalDate;

public class Emprunt {
    private Utilisateur user;
    private Book book;
    private LocalDate startDate;
    private LocalDate endDate;

    public Emprunt(Utilisateur user, Book book, LocalDate startDate, LocalDate endDate) {
        this.user = user;
        this.book = book;
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public Book getBook() {
        return book;
    }

    public LocalDate getEndDate() {
        return endDate;
    }

    public LocalDate getStartDate() {
        return startDate;
    }

    public Utilisateur getUser() {
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

    public void setUser(Utilisateur user) {
        this.user = user;
    }
    // Getters et setters
}
