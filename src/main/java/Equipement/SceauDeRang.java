package Equipement;

/**
 * Sceau de rang — consommable convertible en fragments d'un personnage deja
 * recrute de la meme rarete, au choix du joueur (voir GestionnaireEtoilesPerso).
 */
public enum SceauDeRang {

    C  ("Sceau de Rang [C]",   10),
    B  ("Sceau de Rang [B]",   15),
    A  ("Sceau de Rang [A]",   20),
    S  ("Sceau de Rang [S]",   25),
    SS ("Sceau de Rang [SS]",  25),
    SSS("Sceau de Rang [SSS]", 25),
    UR ("Sceau de Rang [UR]",  25);

    public final String nom;
    public final int    fragments;

    SceauDeRang(String nom, int fragments) {
        this.nom       = nom;
        this.fragments = fragments;
    }

    @Override
    public String toString() {
        return nom + " (+" + fragments + " fragments au choix)";
    }
}
