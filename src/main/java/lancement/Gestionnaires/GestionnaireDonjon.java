package lancement.Gestionnaires;

import java.time.LocalDate;

/**
 * Gere les runs quotidiens pour chaque donjon et difficulte.
 * 3 dojons x 3 difficultes = 9 compteurs.
 * Reset automatique a minuit.
 */
public class GestionnaireDonjon {

    public enum TypeDonjon {
        OR, AFFINAGE, XP
    }

    public enum Difficulte {
        NORMAL, DIFFICILE, EXTREME
    }

    private static final int MAX_RUNS_PAR_JOUR = 3;

    // runs[typeDonjon][difficulte] — indexe par ordinal()
    private int[][]   runs            = new int[3][3];
    private LocalDate dernierReset    = LocalDate.now();

    // ── Reset quotidien ───────────────────────────────────────────────────
    public void mettreAJour() {
        LocalDate aujourdhui = LocalDate.now();
        if (!aujourdhui.equals(dernierReset)) {
            runs         = new int[3][3];
            dernierReset = aujourdhui;
        }
    }

    // ── Vérifie si le joueur peut faire un run ────────────────────────────
    public boolean peutFaireRun(TypeDonjon type, Difficulte diff) {
        mettreAJour();
        return runs[type.ordinal()][diff.ordinal()] < MAX_RUNS_PAR_JOUR;
    }

    public int getRunsRestants(TypeDonjon type, Difficulte diff) {
        mettreAJour();
        return MAX_RUNS_PAR_JOUR - runs[type.ordinal()][diff.ordinal()];
    }

    public void enregistrerRun(TypeDonjon type, Difficulte diff) {
        runs[type.ordinal()][diff.ordinal()]++;
    }

    // ── Getters/Setters pour sauvegarde ───────────────────────────────────
    public int[][]    getRuns()                        { return runs; }
    public void       setRuns(int[][] runs)            { this.runs = runs; }
    public LocalDate  getDernierReset()                { return dernierReset; }
    public void       setDernierReset(LocalDate date)  { this.dernierReset = date; }
}
