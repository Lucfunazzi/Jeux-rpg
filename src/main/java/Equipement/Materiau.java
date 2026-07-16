package Equipement;

public class Materiau {
    private final String nom;
    private int quantite;

    public Materiau(String nom, int quantite) {
        this.nom      = nom;
        this.quantite = quantite;
    }

    public String getNom()      { return nom; }
    public int getQuantite()    { return quantite; }
    public void ajouterQuantite(int n) { this.quantite += n; }
    public void retirerQuantite(int n) { this.quantite = Math.max(0, this.quantite - n); }
    public boolean estVide()    { return quantite <= 0; }

    @Override
    public String toString() {
        return nom + " x" + quantite;
    }
}