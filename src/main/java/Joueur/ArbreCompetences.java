package Joueur;

public class ArbreCompetences {

    // Huit arbres de 10 noeuds chacun
    private final NoeudArbre[] noeuds1 = new NoeudArbre[10];
    private final NoeudArbre[] noeuds2 = new NoeudArbre[10];
    private final NoeudArbre[] noeuds3 = new NoeudArbre[10];
    private final NoeudArbre[] noeuds4 = new NoeudArbre[10];
    private final NoeudArbre[] noeuds5 = new NoeudArbre[10];
    private final NoeudArbre[] noeuds6 = new NoeudArbre[10];
    private final NoeudArbre[] noeuds7 = new NoeudArbre[10];
    private final NoeudArbre[] noeuds8 = new NoeudArbre[10];
    private int pointsDisponibles = 0;

    public ArbreCompetences() {
        // ── Bonus stats des noeuds 1-9 : plat, +1% par arbre (Arbre 1 = 1%, Arbre 2 = 2%,
        // ... Arbre 8 = 8%), meme noeud = meme type de stat dans tous les arbres (cycle
        // ATK/DEF/PV/VIT/ATK/DEF/PV/VIT/ATK). Seul le cout en points differe par arbre/noeud.
        remplirArbreStats(noeuds1, COUTS_ARBRE_1, 0.01, "Nouvelle attaque speciale",   NoeudArbre.TypeBonus.COMPETENCE_SPECIALE);
        remplirArbreStats(noeuds2, COUTS_ARBRE_2, 0.02, "Deuxieme attaque speciale",   NoeudArbre.TypeBonus.COMPETENCE_SPECIALE);
        remplirArbreStats(noeuds3, COUTS_ARBRE_3, 0.03, "Troisieme attaque speciale",  NoeudArbre.TypeBonus.COMPETENCE_SPECIALE);
        remplirArbreStats(noeuds4, COUTS_ARBRE_4, 0.04, "Deuxieme attaque ultime",     NoeudArbre.TypeBonus.COMPETENCE_ULTIME);
        remplirArbreStats(noeuds5, COUTS_ARBRE_5, 0.05, "Quatrieme attaque speciale",  NoeudArbre.TypeBonus.COMPETENCE_SPECIALE);
        remplirArbreStats(noeuds6, COUTS_ARBRE_6, 0.06, "Cinquieme attaque speciale",  NoeudArbre.TypeBonus.COMPETENCE_SPECIALE);
        remplirArbreStats(noeuds7, COUTS_ARBRE_7, 0.07, "Sixieme attaque speciale",    NoeudArbre.TypeBonus.COMPETENCE_SPECIALE);
        remplirArbreStats(noeuds8, COUTS_ARBRE_8, 0.08, "Troisieme attaque ultime",    NoeudArbre.TypeBonus.COMPETENCE_ULTIME);
        // Arbre 4 et Arbre 8 debloquent une attaque ULTIME alternative (pas une speciale).
        // Arbres 5, 6 et 7 debloquent chacun une attaque speciale alternative de plus.
        // Aucune condition scenaristique (chapitre termine, objet en stock...) : le seul
        // prerequis est le total de points, accumule via les recompenses de stage et les
        // Parchemins d'Aptitude consommes (10 pts chacun, voir ParcheminAptitude.POINTS et
        // Personnage_principale.utiliserParcheminAptitude).
    }

    // Couts par noeud (1 a 10), croissants a l'interieur de chaque arbre et d'un arbre a l'autre.
    private static final int[] COUTS_ARBRE_1 = {3,   5,   7,   8,   10,  12,  13,  15,  18,  22};
    private static final int[] COUTS_ARBRE_2 = {5,   8,   10,  11,  12,  13,  14,  16,  18,  20};
    private static final int[] COUTS_ARBRE_3 = {6,   9,   11,  12,  13,  14,  15,  17,  19,  21};
    private static final int[] COUTS_ARBRE_4 = {50,  55,  60,  65,  70,  75,  80,  85,  90,  100};
    private static final int[] COUTS_ARBRE_5 = {100, 110, 120, 130, 140, 150, 160, 170, 180, 200};
    private static final int[] COUTS_ARBRE_6 = {200, 220, 240, 260, 280, 300, 320, 340, 360, 400};
    private static final int[] COUTS_ARBRE_7 = {400, 440, 480, 520, 560, 600, 640, 680, 720, 800};
    private static final int[] COUTS_ARBRE_8 = {800, 880, 960, 1040, 1120, 1200, 1280, 1360, 1440, 1600};

    // Type de stat par noeud 1-9, identique dans tous les arbres.
    private static final NoeudArbre.TypeBonus[] CYCLE_STATS = {
        NoeudArbre.TypeBonus.ATK, NoeudArbre.TypeBonus.DEF, NoeudArbre.TypeBonus.PV, NoeudArbre.TypeBonus.VIT,
        NoeudArbre.TypeBonus.ATK, NoeudArbre.TypeBonus.DEF, NoeudArbre.TypeBonus.PV, NoeudArbre.TypeBonus.VIT,
        NoeudArbre.TypeBonus.ATK
    };

    /**
     * Remplit un arbre avec ses 9 noeuds de stats (bonus plat {@code valeurStat}, type selon
     * CYCLE_STATS) et son noeud 10 (competence speciale/ultime), en appliquant le cout fourni
     * pour chacun des 10 noeuds.
     */
    private static void remplirArbreStats(NoeudArbre[] noeuds, int[] couts, double valeurStat,
            String nomNoeud10, NoeudArbre.TypeBonus typeNoeud10) {
        for (int i = 0; i < 9; i++) {
            String nomStat = switch (CYCLE_STATS[i]) {
                case ATK -> "ATK";
                case DEF -> "DEF";
                case PV  -> "PV";
                default  -> "VIT";
            };
            noeuds[i] = new NoeudArbre(i + 1, "+" + Math.round(valeurStat * 100) + "% " + nomStat + " de base",
                    couts[i], CYCLE_STATS[i], valeurStat);
        }
        noeuds[9] = new NoeudArbre(10, nomNoeud10, couts[9], typeNoeud10, 0);
    }

    // ── Débloquer un nœud ─────────────────────────────────────────────────
    /**
     * @param arbre  1 a 8
     * @param index  1–10
     */
    public String tenterDebloquer(int arbre, int index) {
        NoeudArbre[] noeuds = noeudsDe(arbre);
        int i = index - 1;

        if (arbre >= 2 && arbre <= 8 && !arbreDebloque(arbre))
            return "L'arbre " + arbre + " est verrouille. Terminez l'arbre " + (arbre - 1) + " pour le debloquer.";
        if (i < 0 || i >= 10) return "Noeud invalide.";
        if (noeuds[i].isDebloque()) return "Noeud deja debloque.";
        if (i > 0 && !noeuds[i - 1].isDebloque())
            return "Debloquez d'abord le noeud precedent (noeud " + index + " requis).";
        if (pointsDisponibles < noeuds[i].getCoutPoints())
            return "Points insuffisants : " + pointsDisponibles
                    + " / " + noeuds[i].getCoutPoints() + " requis.";

        pointsDisponibles -= noeuds[i].getCoutPoints();
        noeuds[i].debloquer();

        return "OK";
    }

    /** Compatibilité ascendante — redirige vers arbre 1 */
    public String tenterDebloquer(int index) {
        return tenterDebloquer(1, index);
    }

    // ── Bonus cumulés (arbre 1 + arbre 2 + arbre 3) ──────────────────────
    public double getBonusATK() { return bonusPar(NoeudArbre.TypeBonus.ATK); }
    public double getBonusDEF() { return bonusPar(NoeudArbre.TypeBonus.DEF); }
    public double getBonusPV()  { return bonusPar(NoeudArbre.TypeBonus.PV);  }
    public double getBonusVIT() { return bonusPar(NoeudArbre.TypeBonus.VIT); }

    private double bonusPar(NoeudArbre.TypeBonus type) {
        double total = 0;
        for (NoeudArbre[] noeuds : tousLesArbres())
            for (NoeudArbre n : noeuds)
                if (n.isDebloque() && n.getTypeBonus() == type) total += n.getValeurBonus();
        return total;
    }

    private NoeudArbre[][] tousLesArbres() {
        return new NoeudArbre[][]{ noeuds1, noeuds2, noeuds3, noeuds4, noeuds5, noeuds6, noeuds7, noeuds8 };
    }

    /** @param arbre 1 a 8 */
    private NoeudArbre[] noeudsDe(int arbre) {
        return switch (arbre) {
            case 1 -> noeuds1;
            case 2 -> noeuds2;
            case 3 -> noeuds3;
            case 4 -> noeuds4;
            case 5 -> noeuds5;
            case 6 -> noeuds6;
            case 7 -> noeuds7;
            default -> noeuds8;
        };
    }

    /** Un arbre (2 a 8) est debloque quand le noeud 10 de l'arbre precedent est debloque. */
    private boolean arbreDebloque(int arbre) {
        return arbre <= 1 || noeudsDe(arbre - 1)[9].isDebloque();
    }

    public boolean isNoeud10Debloque()  { return noeuds1[9].isDebloque(); }
    public boolean isNoeud10Arbre2Debloque() { return noeuds2[9].isDebloque(); }
    public boolean isNoeud10Arbre3Debloque() { return noeuds3[9].isDebloque(); }
    public boolean isNoeud10Arbre4Debloque() { return noeuds4[9].isDebloque(); }
    public boolean isNoeud10Arbre5Debloque() { return noeuds5[9].isDebloque(); }
    public boolean isNoeud10Arbre6Debloque() { return noeuds6[9].isDebloque(); }
    public boolean isNoeud10Arbre7Debloque() { return noeuds7[9].isDebloque(); }
    public boolean isNoeud10Arbre8Debloque() { return noeuds8[9].isDebloque(); }
    public boolean isArbre2Debloque()   { return isNoeud10Debloque(); }
    public boolean isArbre3Debloque()   { return isNoeud10Arbre2Debloque(); }
    public boolean isArbre4Debloque()   { return isNoeud10Arbre3Debloque(); }
    public boolean isArbre5Debloque()   { return isNoeud10Arbre4Debloque(); }
    public boolean isArbre6Debloque()   { return isNoeud10Arbre5Debloque(); }
    public boolean isArbre7Debloque()   { return isNoeud10Arbre6Debloque(); }
    public boolean isArbre8Debloque()   { return isNoeud10Arbre7Debloque(); }

    // ── Points ────────────────────────────────────────────────────────────
    public void ajouterPoints(int pts)      { this.pointsDisponibles += pts; }
    public int  getPointsDisponibles()      { return pointsDisponibles; }
    public void setPointsDisponibles(int n) { this.pointsDisponibles = n; }

    // ── Accesseurs noeuds ─────────────────────────────────────────────────
    public NoeudArbre getNoeud(int index)           { return noeuds1[index - 1]; }
    public NoeudArbre getNoeudArbre2(int index)     { return noeuds2[index - 1]; }
    public NoeudArbre getNoeudArbre3(int index)     { return noeuds3[index - 1]; }
    public NoeudArbre getNoeudArbre4(int index)     { return noeuds4[index - 1]; }
    public NoeudArbre getNoeudArbre5(int index)     { return noeuds5[index - 1]; }
    public NoeudArbre getNoeudArbre6(int index)     { return noeuds6[index - 1]; }
    public NoeudArbre getNoeudArbre7(int index)     { return noeuds7[index - 1]; }
    public NoeudArbre getNoeudArbre8(int index)     { return noeuds8[index - 1]; }

    /** @param arbre 1 a 8, @param index 1-10 — accesseur generique equivalent a getNoeud/getNoeudArbreN. */
    public NoeudArbre getNoeud(int arbre, int index) { return noeudsDe(arbre)[index - 1]; }

    // ── Sérialisation ─────────────────────────────────────────────────────
    private boolean[] etatDe(NoeudArbre[] noeuds) {
        boolean[] etat = new boolean[10];
        for (int i = 0; i < 10; i++) etat[i] = noeuds[i].isDebloque();
        return etat;
    }

    private void appliquerEtat(NoeudArbre[] noeuds, boolean[] etat) {
        if (etat == null) return;
        for (int i = 0; i < Math.min(etat.length, 10); i++)
            if (etat[i]) noeuds[i].debloquer();
    }

    public boolean[] getEtatNoeuds()  { return etatDe(noeuds1); }
    public boolean[] getEtatNoeuds2() { return etatDe(noeuds2); }
    public boolean[] getEtatNoeuds3() { return etatDe(noeuds3); }
    public boolean[] getEtatNoeuds4() { return etatDe(noeuds4); }
    public boolean[] getEtatNoeuds5() { return etatDe(noeuds5); }
    public boolean[] getEtatNoeuds6() { return etatDe(noeuds6); }
    public boolean[] getEtatNoeuds7() { return etatDe(noeuds7); }
    public boolean[] getEtatNoeuds8() { return etatDe(noeuds8); }

    public void setEtatNoeuds(boolean[] etat)  { appliquerEtat(noeuds1, etat); }
    public void setEtatNoeuds2(boolean[] etat) { appliquerEtat(noeuds2, etat); }
    public void setEtatNoeuds3(boolean[] etat) { appliquerEtat(noeuds3, etat); }
    public void setEtatNoeuds4(boolean[] etat) { appliquerEtat(noeuds4, etat); }
    public void setEtatNoeuds5(boolean[] etat) { appliquerEtat(noeuds5, etat); }
    public void setEtatNoeuds6(boolean[] etat) { appliquerEtat(noeuds6, etat); }
    public void setEtatNoeuds7(boolean[] etat) { appliquerEtat(noeuds7, etat); }
    public void setEtatNoeuds8(boolean[] etat) { appliquerEtat(noeuds8, etat); }
}