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
        500,       // coût par niveau (1→10)
        3_000,     // coût d'évolution vers Carla
        0.3        // +0.3% par niveau
    ),
    CARLA(
        "Carla",
        1_200,     // coût par niveau (1→10)
        8_000,     // coût d'évolution vers Panthère Lily
        0.4        // +0.4% par niveau
    ),
    PANTHERE_LILY(
        "Panthère Lily",
        2_500,     // coût par niveau (1→10)
        -1,        // pas d'évolution suivante (pour l'instant)
        0.5        // +0.5% par niveau
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