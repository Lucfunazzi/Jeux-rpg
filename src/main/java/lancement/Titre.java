package lancement;

public class Titre {

    private final String nom;
    private final String description;
    private final double bonusStatsPourcentage; // ex: 0.03 = +3% a toutes les stats de l'equipe
    private boolean equipe = false;

    public Titre(String nom, String description, double bonusStatsPourcentage) {
        this.nom                   = nom;
        this.description           = description;
        this.bonusStatsPourcentage = bonusStatsPourcentage;
    }

    public String  getNom()                   { return nom; }
    public String  getDescription()           { return description; }
    public double  getBonusStatsPourcentage() { return bonusStatsPourcentage; }
    public boolean isEquipe()                 { return equipe; }
    public void    setEquipe(boolean equipe)  { this.equipe = equipe; }

    @Override
    public String toString() {
        return "[" + nom + "]  " + description
                + "  (+" + (int)(bonusStatsPourcentage * 100) + "% stats equipe)"
                + (equipe ? "  [EQUIPE]" : "");
    }
}