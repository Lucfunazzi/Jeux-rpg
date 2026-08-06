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

    // Memes paliers/valeurs que RangJoueur (C->B au niveau 30, B->A au niveau 50), pour que
    // les ennemis generiques montent aussi de "rang" en meme temps que le joueur et ne
    // deviennent pas trop faciles passe ces niveaux.
    private static final int    NIVEAU_RANG_B = 30;
    private static final int    NIVEAU_RANG_A = 50;
    private static final double MULT_RANG_B   = 1.35;
    private static final double MULT_RANG_A   = 1.80;

    /** Multiplicateur de rang applique aux ennemis generiques selon le niveau du combat, en
     *  plus du multiplicateur de chapitre/elite ci-dessus (memes paliers que RangJoueur). */
    public static double multiplicateurRang(int niveau) {
        if (niveau >= NIVEAU_RANG_A) return MULT_RANG_A;
        if (niveau >= NIVEAU_RANG_B) return MULT_RANG_B;
        return 1.00;
    }
}
