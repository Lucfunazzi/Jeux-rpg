package lancement.Gestionnaires;

/**
 * Définit les compagnons disponibles dans l'ordre de progression.
 * Chaque compagnon a :
 *   - un nom d'affichage
 *   - un coût en or par niveau (1→10)
 *   - un coût en or pour évoluer vers le suivant
 *   - un bonus FLAT par niveau appliqué à ATK/PV/DEF/VIT de l'équipe
 */
public enum CompagnonsType {

    //           nom             coutNiveau  coutEvolution    atk1   pv1   def1  vit1    dAtk   dPV  dDef  dVit
    HAPPY(
        "Happy",
        20_000,      // coût par niveau × palier → moyenne ~100 000 or par upgrade
        500_000,     // coût d'évolution vers Carla
        350,   600,  130,  200,
         50,   100,   30,   50
    ),                                                          // niv.10 → ATK 800 | PV 1500 | DEF 400 | VIT 650
    CARLA(
        "Carla",
        100_000,     // coût par niveau × palier → moyenne ~500 000 or par upgrade
        2_000_000,   // coût d'évolution vers Panthère Lily
        600,   900,  300,  600,
        200,   400,  100,  150
    ),                                                          // niv.10 → ATK 2400 | PV 4500 | DEF 1200 | VIT 1950
    PANTHERE_LILY(
        "Panthère Lily",
        200_000,     // coût par niveau × palier → moyenne ~1 000 000 or par upgrade
        -1,          // pas d'évolution suivante (pour l'instant)
       1500,  2000,  750, 1200,
        500,  1000,  250,  400
    );                                                          // niv.10 → ATK 6000 | PV 11000 | DEF 3000 | VIT 4800

    public final String nom;
    public final int    coutParNiveau;
    public final int    coutEvolution;

    /** Stats de base au niveau 1 de cette phase */
    public final double bonusATKBase;
    public final double bonusPVBase;
    public final double bonusDEFBase;
    public final double bonusVITBase;

    /** Incrément de stats par niveau (1→10) */
    public final double bonusATKParNiveau;
    public final double bonusPVParNiveau;
    public final double bonusDEFParNiveau;
    public final double bonusVITParNiveau;

    CompagnonsType(String nom, int coutParNiveau, int coutEvolution,
                   double atk1, double pv1, double def1, double vit1,
                   double dAtk, double dPV,  double dDef, double dVit) {
        this.nom               = nom;
        this.coutParNiveau     = coutParNiveau;
        this.coutEvolution     = coutEvolution;
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