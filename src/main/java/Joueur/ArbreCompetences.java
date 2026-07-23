package Joueur;

public class ArbreCompetences {

    // Trois arbres de 10 noeuds chacun
    private final NoeudArbre[] noeuds1 = new NoeudArbre[10];
    private final NoeudArbre[] noeuds2 = new NoeudArbre[10];
    private final NoeudArbre[] noeuds3 = new NoeudArbre[10];
    private int pointsDisponibles = 0;

    // ── Quel arbre 2 est débloqué ──────────────────────────────────────────
    private boolean arbre2Debloque = false;

    public ArbreCompetences() {
        // ── Arbre 1 — budget 100 pts (Chapitre 1 + Chapitre 2) ───────────
        // Coûts : 3/5/7/8/10/12/14/16/10/15 = 100 pts
        noeuds1[0] = new NoeudArbre(1,  "+2% ATK de base",          3,  NoeudArbre.TypeBonus.ATK, 0.02);
        noeuds1[1] = new NoeudArbre(2,  "+2% DEF de base",          5,  NoeudArbre.TypeBonus.DEF, 0.02);
        noeuds1[2] = new NoeudArbre(3,  "+3% PV de base",           7,  NoeudArbre.TypeBonus.PV,  0.03);
        noeuds1[3] = new NoeudArbre(4,  "+2% VIT de base",          8,  NoeudArbre.TypeBonus.VIT, 0.02);
        noeuds1[4] = new NoeudArbre(5,  "+3% ATK de base",          10, NoeudArbre.TypeBonus.ATK, 0.03);
        noeuds1[5] = new NoeudArbre(6,  "+3% DEF de base",          12, NoeudArbre.TypeBonus.DEF, 0.03);
        noeuds1[6] = new NoeudArbre(7,  "+4% PV de base",           14, NoeudArbre.TypeBonus.PV,  0.04);
        noeuds1[7] = new NoeudArbre(8,  "+3% VIT de base",          16, NoeudArbre.TypeBonus.VIT, 0.03);
        noeuds1[8] = new NoeudArbre(9,  "+5% ATK de base",          10, NoeudArbre.TypeBonus.ATK, 0.05);
        noeuds1[9] = new NoeudArbre(10, "Nouvelle attaque speciale", 15, NoeudArbre.TypeBonus.COMPETENCE_SPECIALE, 0);

        // ── Arbre 2 — budget 110 pts ──────────────────────────────────────
        // Bonus supérieurs à l'arbre 1, coûts : 5/8/10/11/12/13/14/16/8/13 = 110 pts
        noeuds2[0] = new NoeudArbre(1,  "+3% ATK de base",          5,  NoeudArbre.TypeBonus.ATK, 0.03);
        noeuds2[1] = new NoeudArbre(2,  "+3% DEF de base",          8,  NoeudArbre.TypeBonus.DEF, 0.03);
        noeuds2[2] = new NoeudArbre(3,  "+5% PV de base",           10, NoeudArbre.TypeBonus.PV,  0.05);
        noeuds2[3] = new NoeudArbre(4,  "+3% VIT de base",          11, NoeudArbre.TypeBonus.VIT, 0.03);
        noeuds2[4] = new NoeudArbre(5,  "+4% ATK de base",          12, NoeudArbre.TypeBonus.ATK, 0.04);
        noeuds2[5] = new NoeudArbre(6,  "+4% DEF de base",          13, NoeudArbre.TypeBonus.DEF, 0.04);
        noeuds2[6] = new NoeudArbre(7,  "+6% PV de base",           14, NoeudArbre.TypeBonus.PV,  0.06);
        noeuds2[7] = new NoeudArbre(8,  "+4% VIT de base",          16, NoeudArbre.TypeBonus.VIT, 0.04);
        noeuds2[8] = new NoeudArbre(9,  "+6% ATK de base",          8,  NoeudArbre.TypeBonus.ATK, 0.06);
        noeuds2[9] = new NoeudArbre(10, "Deuxieme attaque speciale", 13, NoeudArbre.TypeBonus.COMPETENCE_SPECIALE, 0);

        // ── Arbre 3 — budget 120 pts ─────────────────────────────────────
        // Débloqué automatiquement une fois l'Arbre 2 complété (nœud 10)
        // Coûts : 6/9/11/12/13/14/15/17/9/14 = 120 pts
        noeuds3[0] = new NoeudArbre(1,  "+4% ATK de base",          6,  NoeudArbre.TypeBonus.ATK, 0.04);
        noeuds3[1] = new NoeudArbre(2,  "+4% DEF de base",          9,  NoeudArbre.TypeBonus.DEF, 0.04);
        noeuds3[2] = new NoeudArbre(3,  "+6% PV de base",           11, NoeudArbre.TypeBonus.PV,  0.06);
        noeuds3[3] = new NoeudArbre(4,  "+4% VIT de base",          12, NoeudArbre.TypeBonus.VIT, 0.04);
        noeuds3[4] = new NoeudArbre(5,  "+5% ATK de base",          13, NoeudArbre.TypeBonus.ATK, 0.05);
        noeuds3[5] = new NoeudArbre(6,  "+5% DEF de base",          14, NoeudArbre.TypeBonus.DEF, 0.05);
        noeuds3[6] = new NoeudArbre(7,  "+7% PV de base",           15, NoeudArbre.TypeBonus.PV,  0.07);
        noeuds3[7] = new NoeudArbre(8,  "+5% VIT de base",          17, NoeudArbre.TypeBonus.VIT, 0.05);
        noeuds3[8] = new NoeudArbre(9,  "+7% ATK de base",          9,  NoeudArbre.TypeBonus.ATK, 0.07);
        noeuds3[9] = new NoeudArbre(10, "Troisieme attaque speciale", 14, NoeudArbre.TypeBonus.COMPETENCE_SPECIALE, 0);
    }

    // ── Débloquer un nœud ─────────────────────────────────────────────────
    /**
     * @param arbre  1, 2 ou 3
     * @param index  1–10
     */
    public String tenterDebloquer(int arbre, int index) {
        NoeudArbre[] noeuds = switch (arbre) {
            case 1 -> noeuds1;
            case 2 -> noeuds2;
            default -> noeuds3;
        };
        int i = index - 1;

        if (arbre == 2 && !isArbre2Debloque())
            return "L'arbre 2 est verrouille. Terminez le Chapitre 2 Elite pour le debloquer.";
        if (arbre == 3 && !isArbre3Debloque())
            return "L'arbre 3 est verrouille. Terminez l'arbre 2 pour le debloquer.";
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
        for (NoeudArbre n : noeuds1)
            if (n.isDebloque() && n.getTypeBonus() == type) total += n.getValeurBonus();
        for (NoeudArbre n : noeuds2)
            if (n.isDebloque() && n.getTypeBonus() == type) total += n.getValeurBonus();
        for (NoeudArbre n : noeuds3)
            if (n.isDebloque() && n.getTypeBonus() == type) total += n.getValeurBonus();
        return total;
    }

    public boolean isNoeud10Debloque()  { return noeuds1[9].isDebloque(); }
    public boolean isNoeud10Arbre2Debloque() { return noeuds2[9].isDebloque(); }
    public boolean isNoeud10Arbre3Debloque() { return noeuds3[9].isDebloque(); }
    public boolean isArbre2Debloque()   { return arbre2Debloque; }
    public boolean isArbre3Debloque()   { return isNoeud10Arbre2Debloque(); }

    // ── Points ────────────────────────────────────────────────────────────
    public void ajouterPoints(int pts)      { this.pointsDisponibles += pts; }
    public int  getPointsDisponibles()      { return pointsDisponibles; }
    public void setPointsDisponibles(int n) { this.pointsDisponibles = n; }

    // ── Accesseurs noeuds ─────────────────────────────────────────────────
    public NoeudArbre getNoeud(int index)           { return noeuds1[index - 1]; }
    public NoeudArbre getNoeudArbre2(int index)     { return noeuds2[index - 1]; }
    public NoeudArbre getNoeudArbre3(int index)     { return noeuds3[index - 1]; }

    // ── Sérialisation ─────────────────────────────────────────────────────
    public boolean[] getEtatNoeuds() {
        boolean[] etat = new boolean[10];
        for (int i = 0; i < 10; i++) etat[i] = noeuds1[i].isDebloque();
        return etat;
    }

    public void setEtatNoeuds(boolean[] etat) {
        if (etat == null) return;
        for (int i = 0; i < Math.min(etat.length, 10); i++) {
            if (etat[i]) noeuds1[i].debloquer();
        }
        // Resynchroniser l'état de l'arbre 2
        if (noeuds1[9].isDebloque()) arbre2Debloque = true;
    }

    public boolean[] getEtatNoeuds2() {
        boolean[] etat = new boolean[10];
        for (int i = 0; i < 10; i++) etat[i] = noeuds2[i].isDebloque();
        return etat;
    }

    public void setEtatNoeuds2(boolean[] etat) {
        if (etat == null) return;
        for (int i = 0; i < Math.min(etat.length, 10); i++)
            if (etat[i]) noeuds2[i].debloquer();
    }

    public boolean[] getEtatNoeuds3() {
        boolean[] etat = new boolean[10];
        for (int i = 0; i < 10; i++) etat[i] = noeuds3[i].isDebloque();
        return etat;
    }

    public void setEtatNoeuds3(boolean[] etat) {
        if (etat == null) return;
        for (int i = 0; i < Math.min(etat.length, 10); i++)
            if (etat[i]) noeuds3[i].debloquer();
    }

    public void setArbre2Debloque(boolean v) { this.arbre2Debloque = v; }
}