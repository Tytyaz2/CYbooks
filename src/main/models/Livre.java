package main.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Livre {
    private final StringProperty titre;
    private final StringProperty auteur;
    private final StringProperty isbn;
    private final StringProperty dateEmprunt;
    private final StringProperty dateRendu;

    public Livre() {
        this.titre = new SimpleStringProperty("");
        this.auteur = new SimpleStringProperty("");
        this.isbn = new SimpleStringProperty("");
        this.dateEmprunt = new SimpleStringProperty("");
        this.dateRendu = new SimpleStringProperty("");
    }

    public Livre(String titre, String auteur, String isbn, String dateEmprunt, String dateRendu) {
        this.titre = new SimpleStringProperty(titre);
        this.auteur = new SimpleStringProperty(auteur);
        this.isbn = new SimpleStringProperty(isbn);
        this.dateEmprunt = new SimpleStringProperty(dateEmprunt);
        this.dateRendu = new SimpleStringProperty(dateRendu);
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

    public String getDateEmprunt() {
        return dateEmprunt.get();
    }

    public StringProperty dateEmpruntProperty() {
        return dateEmprunt;
    }

    public void setDateEmprunt(String dateEmprunt) {
        this.dateEmprunt.set(dateEmprunt);
    }

    public String getDateRendu() {
        return dateRendu.get();
    }

    public StringProperty dateRenduProperty() {
        return dateRendu;
    }

    public void setDateRendu(String dateRendu) {
        this.dateRendu.set(dateRendu);
    }
}
