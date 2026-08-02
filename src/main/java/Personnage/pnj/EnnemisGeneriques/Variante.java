package Personnage.pnj.EnnemisGeneriques;

/** Identifie le chapitre/version dont un ennemi generique tire ses stats de base. */
public enum Variante {
    CHAPITRE_1,
    CHAPITRE_1_ELITE,
    CHAPITRE_2,
    CHAPITRE_2_ELITE,
    CHAPITRE_3,
    CHAPITRE_4,
    CHAPITRE_5,
    CHAPITRE_6,
    CHAPITRE_7,
    CHAPITRE_8,
    CHAPITRE_9,
    CHAPITRE_10,
    CHAPITRE_11,
    CHAPITRE_12,
    CHAPITRE_13;

    /** Numero du chapitre associe (1 a 13) ; les variantes Elite comptent comme leur chapitre de base. */
    public int getChapitre() {
        return switch (this) {
            case CHAPITRE_1, CHAPITRE_1_ELITE -> 1;
            case CHAPITRE_2, CHAPITRE_2_ELITE -> 2;
            case CHAPITRE_3  -> 3;
            case CHAPITRE_4  -> 4;
            case CHAPITRE_5  -> 5;
            case CHAPITRE_6  -> 6;
            case CHAPITRE_7  -> 7;
            case CHAPITRE_8  -> 8;
            case CHAPITRE_9  -> 9;
            case CHAPITRE_10 -> 10;
            case CHAPITRE_11 -> 11;
            case CHAPITRE_12 -> 12;
            case CHAPITRE_13 -> 13;
        };
    }

    /** Multiplicateur de stats : +5% compose par chapitre (chapitre 1 = base, x1.00). */
    public double getMultiplicateur() {
        return Math.pow(1.05, getChapitre() - 1);
    }
}
