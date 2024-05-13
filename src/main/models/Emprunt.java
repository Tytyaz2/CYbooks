package main.models;

import java.time.LocalDate;

public class Emprunt {
    private Utilisateur utilisateur;
    private Livre livre;
    private LocalDate dateDebut;
    private LocalDate dateFin;

    public Emprunt(Utilisateur utilisateur, Livre livre, LocalDate dateDebut, LocalDate dateFin) {
        this.utilisateur = utilisateur;
        this.livre = livre;
        this.dateDebut = dateDebut;
        this.dateFin = dateFin;
    }

    // Getters and setters
    public Utilisateur getUtilisateur() {
        return utilisateur;
    }

    public void setUtilisateur(Utilisateur utilisateur) {
        this.utilisateur = utilisateur;
    }

    public Livre getLivre() {
        return livre;
    }

    public void setLivre(Livre livre) {
        this.livre = livre;
    }

    public LocalDate getDateDebut() {
        return dateDebut;
    }

    public void setDateDebut(LocalDate dateDebut) {
        this.dateDebut = dateDebut;
    }

    public LocalDate getDateFin() {
        return dateFin;
    }

    public void setDateFin(LocalDate dateFin) {
        this.dateFin = dateFin;
    }
}

