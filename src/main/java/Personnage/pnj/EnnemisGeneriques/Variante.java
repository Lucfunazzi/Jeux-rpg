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

    /** Bonus de stats propre aux versions Elite, en plus du palier de niveau plus eleve
     *  (sans quoi une Elite n'est qu'un chapitre normal avec des ennemis simplement plus hauts
     *  niveau, sans reelle difficulte supplementaire). */
    private static final double MULT_ELITE = 1.25;

    public boolean estElite() {
        return this == CHAPITRE_1_ELITE || this == CHAPITRE_2_ELITE;
    }

    /** Multiplicateur de stats : +5% compose par chapitre (chapitre 1 = base, x1.00), plus le
     *  bonus Elite le cas echeant. */
    public double getMultiplicateur() {
        double base = Math.pow(1.05, getChapitre() - 1);
        return estElite() ? base * MULT_ELITE : base;
    }
}
