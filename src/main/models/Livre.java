package main.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Livre {
    private final StringProperty titre;
    private final StringProperty auteur;
    private final StringProperty isbn;


    public Livre() {
        this.titre = new SimpleStringProperty("");
        this.auteur = new SimpleStringProperty("");
        this.isbn = new SimpleStringProperty("");
    }

    public Livre(String titre, String auteur, String isbn) {
        this.titre = new SimpleStringProperty(titre);
        this.auteur = new SimpleStringProperty(auteur);
        this.isbn = new SimpleStringProperty(isbn);
    }

    // Getters and setters for properties

    public String getTitre() {
        return titre.get();
    }

    public StringProperty titreProperty() {
        return titre;
    }

    public void setTitre(String titre) {
        this.titre.set(titre);
    }

    public String getAuteur() {
        return auteur.get();
    }

    public StringProperty auteurProperty() {
        return auteur;
    }

    public void setAuteur(String auteur) {
        this.auteur.set(auteur);
    }

    public String getIsbn() {
        return isbn.get();
    }

    public StringProperty isbnProperty() {
        return isbn;
    }

    public void setIsbn(String isbn) {
        this.isbn.set(isbn);
    }
}
