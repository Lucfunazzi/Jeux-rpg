package lancement.Gestionnaires;

/**
 * Définit la progression linéaire des Créatures Sacrées (univers DBZ).
 *
 * Cycle : Oeuf → Ptérodactyle → Icari → Shenron → Porunga → Oozaru → Super Shenron
 * Chaque phase se joue niveaux 1→20 via l'entraînement, puis évolue vers la suivante.
 *
 * Les stats sont des BONUS FLAT ajoutés aux stats de l'équipe en formation.
 * Valeurs au niveau 1 de chaque phase ; chaque niveau ajoute bonusParNiveau.
 */
public enum CreatureSacreeType {

    //                    nom               atk1  pv1   def1  vit1  dAtk  dPV   dDef  dVit
    OEUF(
        "Œuf Mystérieux",
         8,    60,   5,    2,
         4,    30,   3,    1
    ),
    PTERODACTYLE(
        "Ptérodactyle",
        90,   700,  60,   25,
        18,   120,  10,    4
    ),
    ICARI(
        "Icari",
        200,  1_600, 130,  55,
        35,   250,   20,   8
    ),
    SHENRON(
        "Shenron",
        420,  3_500, 280, 115,
        65,   500,   40,  15
    ),
    PORUNGA(
        "Porunga",
        800,  7_000, 550, 220,
       110,   900,   75,  25
    ),
    OOZARU(
        "Oozaru",
       1_500, 14_000, 1_100, 430,
        200,  1_800,   150,   45
    ),
    SUPER_SHENRON(
        "Super Shenron",
       3_000, 28_000, 2_200, 850,
        380,  3_500,   280,   80
    );

    public final String nom;

    /** Stats de base au niveau 1 de cette phase */
    public final double bonusATKBase;
    public final double bonusPVBase;
    public final double bonusDEFBase;
    public final double bonusVITBase;

    /** Incrément de stats par niveau (1→20) */
    public final double bonusATKParNiveau;
    public final double bonusPVParNiveau;
    public final double bonusDEFParNiveau;
    public final double bonusVITParNiveau;

    CreatureSacreeType(String nom,
                       double atk1, double pv1, double def1, double vit1,
                       double dAtk, double dPV,  double dDef, double dVit) {
        this.nom               = nom;
        this.bonusATKBase      = atk1;
        this.bonusPVBase       = pv1;
        this.bonusDEFBase      = def1;
        this.bonusVITBase      = vit1;
        this.bonusATKParNiveau = dAtk;
        this.bonusPVParNiveau  = dPV;
        this.bonusDEFParNiveau = dDef;
        this.bonusVITParNiveau = dVit;
    }

    /** Stats totales ATK au niveau donné (1-based). */
    public double getATK(int niveau) { return bonusATKBase + bonusATKParNiveau * (niveau - 1); }
    public double getPV (int niveau) { return bonusPVBase  + bonusPVParNiveau  * (niveau - 1); }
    public double getDEF(int niveau) { return bonusDEFBase + bonusDEFParNiveau * (niveau - 1); }
    public double getVIT(int niveau) { return bonusVITBase + bonusVITParNiveau * (niveau - 1); }

    /** Créature suivante dans la chaîne, ou null si c'est la dernière. */
    public CreatureSacreeType suivant() {
        CreatureSacreeType[] vals = values();
        int idx = this.ordinal() + 1;
        return idx < vals.length ? vals[idx] : null;
    }

    public boolean peutEvoluer() { return suivant() != null; }
}