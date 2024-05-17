package main.models;

public class Book {
    private String title;
    private String authors;
    private String isbn;


    private String dateBorrow;
    private String dateGB;

    private int stock;

    public Book(String title,String authors, String isbn, String dateBorrow, String dateGB){
        this.title=title;
        this.authors=authors;
        this.isbn=isbn;
        this.dateBorrow=dateBorrow;
        this.dateGB=dateGB;
        this.stock = DatabaseConnection.getStockFromDatabase(isbn);
    }

    public Book(String title, String authors, String isbn) {
        this.title = title;
        this.authors = authors;
        this.isbn = isbn;

        this.stock=10;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public String getDateBorrow() {
        return dateBorrow;
    }

    public void setDateBorrow(String dateBorrow) {
        this.dateBorrow = dateBorrow;
    }

    public String getDateGB() {
        return dateGB;
    }

    public void setDateGB(String dateGB) {
        this.dateGB = dateGB;
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





    public String toString(){
        return this.title+","+this.authors;
    }


}
