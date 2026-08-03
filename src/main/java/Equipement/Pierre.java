package Equipement;

/**
 * Pierre inserable dans un emplacement (socket) d'equipement.
 * Chaque pierre augmente une seule statistique, avec un bonus qui monte par palier (niveau 1 a 12).
 */
public class Pierre {

    public enum Type {
        FORCE,      // % ATK
        AGILITE,    // % VIT
        VIE,        // % PV
        PRECISION,  // points sur l'echelle Precision (100 = neutre)
        ATTAQUE_S,  // points sur l'echelle Attaque S (100 = neutre)
        CONTRE,     // points sur l'echelle Contre (100 = neutre)
        CRITIQUE,   // points de % sur le taux de critique
        BLOCAGE,    // points de % sur le taux de blocage
        ESQUIVE     // points de % sur le taux d'esquive
    }

    public static final int NIVEAU_MAX = 12;

    // Index 0 = niveau 1 ... index 11 = niveau 12
    private static final double[] VALEURS_GRANDES = {1.5, 3, 4.5, 6, 7.5, 10.5, 13.5, 18, 24, 30, 37.5, 45};
    private static final double[] VALEURS_PETITES = {1, 2, 3, 4, 5, 7, 9, 12, 16, 20, 25, 30};

    private final Type type;
    private final int niveau;

    public Pierre(Type type, int niveau) {
        if (niveau < 1 || niveau > NIVEAU_MAX) {
            throw new IllegalArgumentException("Niveau de pierre invalide : " + niveau);
        }
        this.type = type;
        this.niveau = niveau;
    }

    public Type getType()   { return type; }
    public int getNiveau()  { return niveau; }

    /** Bonus apporte par cette pierre a son niveau actuel (ex : 1.5 pour +1.5%). */
    public double getBonusPourcent() {
        return (estGrande(type) ? VALEURS_GRANDES : VALEURS_PETITES)[niveau - 1];
    }

    private static boolean estGrande(Type t) {
        return switch (t) {
            case FORCE, AGILITE, PRECISION, ATTAQUE_S, CONTRE -> true;
            case CRITIQUE, BLOCAGE, VIE, ESQUIVE               -> false;
        };
    }

    public String getNomType() {
        return switch (type) {
            case FORCE     -> "Force";
            case AGILITE   -> "Agilite";
            case VIE       -> "Vie";
            case PRECISION -> "Precision";
            case ATTAQUE_S -> "Attaque S";
            case CONTRE    -> "Contre";
            case CRITIQUE  -> "Critique";
            case BLOCAGE   -> "Blocage";
            case ESQUIVE   -> "Esquive";
        };
    }

    /**
     * Couleur associee a ce type de pierre, pour les differencier d'un coup d'oeil.
     * Rendue comme une pastille coloree via CSS (-fx-text-fill) plutot qu'un emoji colore :
     * le rendu des emoji "grand cercle colore" depend d'une police couleur (Segoe UI Emoji) que
     * JavaFX ne charge pas toujours en fallback, ce qui donnait un rendu incoherent selon le type.
     */
    public String getCouleurHex() {
        return switch (type) {
            case FORCE     -> "#e0393c"; // rouge
            case AGILITE   -> "#f2c14e"; // jaune/or
            case VIE       -> "#56c98a"; // vert
            case PRECISION -> "#4ea8f2"; // bleu
            case ATTAQUE_S -> "#e08a3c"; // orange
            case CONTRE    -> "#b565d8"; // violet
            case CRITIQUE  -> "#f2f2f2"; // blanc
            case BLOCAGE   -> "#a97c50"; // brun
            case ESQUIVE   -> "#4ecdc4"; // turquoise
        };
    }

    private String formaterPourcent() {
        double v = getBonusPourcent();
        return (v == Math.floor(v)) ? String.valueOf((int) v) : String.valueOf(v);
    }

    public String getNom() {
        return "Pierre de " + getNomType() + " Niv." + niveau;
    }

    @Override
    public String toString() {
        return getNom() + " (+" + formaterPourcent() + "%)";
    }
}
