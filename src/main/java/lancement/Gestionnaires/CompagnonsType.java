package lancement.Gestionnaires;

/**
 * Définit les compagnons disponibles dans l'ordre de progression.
 * Chaque compagnon a :
 *   - un nom d'affichage
 *   - un coût en or par niveau (1→10)
 *   - un coût en or pour évoluer vers le suivant
 *   - un bonus % par niveau appliqué à ATK/PV/DEF/VIT de l'équipe
 */
public enum CompagnonsType {

    HAPPY(
        "Happy",
        20_000,      // coût par niveau × palier → moyenne ~100 000 or par upgrade
        500_000,     // coût d'évolution vers Carla
        0.5         // +0.5% par niveau
    ),
    CARLA(
        "Carla",
        100_000,     // coût par niveau × palier → moyenne ~500 000 or par upgrade
        2_000_000,   // coût d'évolution vers Panthère Lily
        0.8          // +0.8% par niveau
    ),
    PANTHERE_LILY(
        "Panthère Lily",
        200_000,     // coût par niveau × palier → moyenne ~1 000 000 or par upgrade
        -1,          // pas d'évolution suivante (pour l'instant)
        1          // +1% par niveau
    );

    public final String nom;
    public final int    coutParNiveau;
    public final int    coutEvolution;
    public final double bonusParNiveau; // en %

    CompagnonsType(String nom, int coutParNiveau, int coutEvolution, double bonusParNiveau) {
        this.nom            = nom;
        this.coutParNiveau  = coutParNiveau;
        this.coutEvolution  = coutEvolution;
        this.bonusParNiveau = bonusParNiveau;
    }

    /** Retourne le compagnon suivant, ou null si c'est le dernier. */
    public CompagnonsType suivant() {
        CompagnonsType[] vals = values();
        int idx = this.ordinal() + 1;
        return idx < vals.length ? vals[idx] : null;
    }

    /** Retourne true si une évolution est possible. */
    public boolean peutEvoluer() {
        return suivant() != null;
    }
}