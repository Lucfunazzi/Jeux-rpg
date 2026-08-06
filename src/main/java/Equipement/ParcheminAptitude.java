package Equipement;

/** Parchemin d'Aptitude — consommable donnant des points a depenser dans l'arbre de competences. */
public enum ParcheminAptitude {
    PETITE ("Petit Parchemin d'Aptitude",  10),
    MOYENNE("Moyen Parchemin d'Aptitude",  50),
    GRANDE ("Grand Parchemin d'Aptitude", 100);

    public final String nom;
    public final int    points;

    ParcheminAptitude(String nom, int points) {
        this.nom    = nom;
        this.points = points;
    }

    @Override
    public String toString() {
        return nom + " (+" + points + " points)";
    }
}
