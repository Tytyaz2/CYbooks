package main.models;


public class Livre {
    private final String titre;
    private final String auteur;
    private final String isbn;
    private Integer quantiteActuelle;
    private Integer quantiteInitiale;

    public Livre(String titre, String auteur, String isbn, Integer quantiteActuelle, Integer quantiteInitiale) {
        this.titre = titre;
        this.auteur = auteur;
        this.isbn = isbn;
        this.quantiteActuelle = quantiteActuelle;
        this.quantiteInitiale = quantiteInitiale;
    }

    public Livre(String titre, String auteur, String isbn) {
        this.titre = titre;
        this.auteur = auteur;
        this.isbn = isbn;
    }

    public String getTitre() {
        return titre;
    }

    public String getAuteur() {
        return auteur;
    }

    public String getIsbn() {
        return isbn;
    }

    public Integer getQuantiteActuelle() {
        return quantiteActuelle;
    }

    public Integer getQuantiteInitiale() {
        return quantiteInitiale;
    }

    public void setQuantiteInitiale(Integer quantiteInitiale) {
        this.quantiteInitiale = quantiteInitiale;
    }

    public void setQuantiteActuelle(Integer quantiteActuelle) {
        this.quantiteActuelle = quantiteActuelle;
    }
}




