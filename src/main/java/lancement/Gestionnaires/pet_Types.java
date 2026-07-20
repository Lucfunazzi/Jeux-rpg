package lancement.Gestionnaires;

/**
 * Définit la progression linéaire des Créatures Sacrées (univers Fairy Tail).
 *
 * Cycle d'évolution :
 *   Œuf → Bébé Happy → Happy → Ichiya (Edolas Chat) → Virgo (Esprit)
 *       → Cancer (Esprit) → Taurus (Esprit) → Leo (Esprit)
 *       → Bébé Dragon de Feu → Atlas Flame → Igneel (Roi Dragon de Feu)
 *
 * Chaque phase se joue niveaux 1→20 via l'entraînement, puis évolue vers la suivante.
 * Les stats sont des BONUS FLAT ajoutés aux stats de l'équipe en formation.
 * Valeurs au niveau 1 de chaque phase ; chaque niveau ajoute bonusParNiveau.
 */
public enum pet_Types {

    //                        nom                          atk1   pv1    def1  vit1   dAtk  dPV    dDef  dVit
    OEUF(
        "Œuf Mystérieux",
          8,     60,    5,    2,
          4,     30,    3,    1
    ),
    BEBE_HAPPY(
        "Bébé Happy",
         30,    280,   20,   12,
         10,     80,    6,    3
    ),
    HAPPY(
        "Happy",
         90,    700,   60,   28,
         18,    120,   10,    5
    ),
    ICHIYA_CHAT(
        "Ichiya (Chat d'Edolas)",
        180,  1_500,  120,   55,
         32,    230,   18,    8
    ),
    VIRGO(
        "Virgo — Esprit Céleste",
        350,  3_000,  240,  100,
         58,    450,   36,   14
    ),
    CANCER(
        "Cancer — Esprit Céleste",
        600,  5_500,  440,  180,
         95,    750,   62,   22
    ),
    TAURUS(
        "Taurus — Esprit Céleste",
        950,  9_000,  720,  290,
        145,  1_200,   98,   33
    ),
    LEO(
        "Leo — Esprit Céleste",
      1_500, 14_000, 1_100,  440,
        210,  1_800,  150,   48
    ),
    BEBE_DRAGON_FEU(
        "Bébé Dragon de Feu",
      2_200, 20_000, 1_600,  620,
        300,  2_600,  210,   65
    ),
    ATLAS_FLAME(
        "Atlas Flame",
      3_200, 30_000, 2_400,  880,
        430,  3_800,  310,   88
    ),
    IGNEEL(
        "Igneel — Roi Dragon de Feu",
      5_000, 50_000, 4_000, 1_400,
        700,  6_000,  500,  140
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

    pet_Types(String nom,
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

    /** Stats totales au niveau donné (1-based). */
    public double getATK(int niveau) { return bonusATKBase + bonusATKParNiveau * (niveau - 1); }
    public double getPV (int niveau) { return bonusPVBase  + bonusPVParNiveau  * (niveau - 1); }
    public double getDEF(int niveau) { return bonusDEFBase + bonusDEFParNiveau * (niveau - 1); }
    public double getVIT(int niveau) { return bonusVITBase + bonusVITParNiveau * (niveau - 1); }

    /** Créature suivante dans la chaîne, ou null si c'est la dernière. */
    public pet_Types suivant() {
        pet_Types[] vals = values();
        int idx = this.ordinal() + 1;
        return idx < vals.length ? vals[idx] : null;
    }

    public boolean peutEvoluer() { return suivant() != null; }
}