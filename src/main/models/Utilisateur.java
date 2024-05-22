package main.models;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import main.models.DatabaseConnection;

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
        String emailRegex = "^[\\p{L}0-9._%+-]+@[\\p{L}0-9.-]+\\.[a-zA-Z]{2,}$";
        Pattern pattern = Pattern.compile(emailRegex);
        Matcher matcher = pattern.matcher(email);
        return matcher.matches();
    }

    @Override
    public String toString() {
        return super.toString();
    }

    public void setEmail(String nouvelEmail) {
        this.email = nouvelEmail;
    }

    public boolean hasOverdueLoans(LocalDate currentDate) throws SQLException {

        System.out.println("Fonction hasOverDueLoans: on recup tous les emprunts");
        List<Emprunt> emprunts = DatabaseConnection.getEmpruntsUtilisateur(this.getEmail());
        for (Emprunt emprunt : emprunts) {
            System.out.println("Verification de la date de rendu par rapport à la date actuelle");
            if (emprunt.getEndDate().isBefore(currentDate)) {
                return true; // Il y a au moins un emprunt en retard
            }
        }
        return false; // Aucun emprunt en retard
    }
}
