package main.models;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Utilisateur {
    private String email;
    private String prenom;
    private String nom;
    private int statut;
    private int maxEmprunt ; // Initialisation à 5

    public Utilisateur(String email, String prenom, String nom, int statut, int maxEmprunt) {
        this.email = email;
        this.prenom = prenom;
        this.nom = nom;
        this.statut = statut;
        this.maxEmprunt = maxEmprunt;
    }

    public String getNom() {
        return nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public String getEmail() {
        return email;
    }

    public int getStatut() {
        return statut;
    }

    public void setStatut(int statut) {
        this.statut = statut;
    }

    public int getMaxEmprunt() {
        return maxEmprunt;
    }

    public void setMaxEmprunt(int maxEmprunt) {
        this.maxEmprunt = maxEmprunt;
    }

    // Méthode pour valider le format de l'email
    public static boolean isValidEmail(String email) {
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
