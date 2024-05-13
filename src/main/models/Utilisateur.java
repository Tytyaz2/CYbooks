package main.models;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Utilisateur {


    private final int id;
    private final StringProperty nom;
    private final StringProperty prenom;
    private final StringProperty email;

    public Utilisateur(int id, String nom, String prenom, String email) {
        this.id = id;
        this.nom = new SimpleStringProperty(nom);
        this.prenom = new SimpleStringProperty(prenom);
        this.email = new SimpleStringProperty(email);
    }

    public StringProperty nomProperty() {
        return nom;
    }

    public String getNom() {
        return nom.get();
    }

    public void setNom(String nom) {
        this.nom.set(nom);
    }

    public StringProperty prenomProperty() {
        return prenom;
    }

    public String getPrenom() {
        return prenom.get();
    }

    public void setPrenom(String prenom) {
        this.prenom.set(prenom);
    }

    public StringProperty emailProperty() {
        return email;
    }

    public String getEmail() {
        return email.get();
    }

    public void setEmail(String email) {
        this.email.set(email);
    }

    public int getId() {
        return id;
    }
}
