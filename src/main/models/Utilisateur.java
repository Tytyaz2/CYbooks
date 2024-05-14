package main.models;



public class Utilisateur {
//changer les string property et pas de javafx

    private  String nom;
    private  String prenom;
    private  String email;
    private int statut;
    private int MaxEmprunt;

    public Utilisateur( String email, String prenom, String nom, int statut, int MaxEmprunt) {
        this.nom = nom;
        this.prenom =prenom;
        this.email =email;
        this.statut = statut;
        this.MaxEmprunt = MaxEmprunt;
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
        return MaxEmprunt;
    }

    public void setMaxEmprunt(int maxEmprunt) {
        MaxEmprunt = maxEmprunt;
    }
}
