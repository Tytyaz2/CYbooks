package main.models;

public class Book {
    private String title;
    private String authors;
    private String isbn;
    private String kind;

    public Book(String title, String authors, String isbn, String kind) {
        this.title = title;
        this.authors = authors;
        this.isbn = isbn;
        this.kind = kind;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getAuthors() {
        return authors;
    }

    public void setAuthors(String authors) {
        this.authors = authors;
    }

    public String getIsbn() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn = isbn;
    }

    public String getKind() {
        return kind;
    }

    public void setKind(String kind) {
        this.kind = kind;
    }
}
