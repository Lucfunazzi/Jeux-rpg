package lancement.Gestionnaires;

import java.time.LocalDate;

/**
 * Gere les runs quotidiens pour chaque donjon et difficulte.
 * 3 dojons x {@link Difficulte#values()}.length difficultes.
 * Reset automatique a minuit.
 */
public class GestionnaireDonjon {

    public enum TypeDonjon {
        OR, AFFINAGE, XP
    }

    public enum Difficulte {
        FACILE, MOYEN, DIFFICILE, EXTREME
    }

    private static final int MAX_RUNS_PAR_JOUR = 3;
    private static final int NB_TYPES       = TypeDonjon.values().length;
    private static final int NB_DIFFICULTES = Difficulte.values().length;

    // runs[typeDonjon][difficulte] — indexe par ordinal(). Derive des enums (pas de constante en
    // dur) pour ne jamais revivre le bug d'un tableau [3][3] fige alors que Difficulte est passe
    // a 4 valeurs (ArrayIndexOutOfBoundsException des qu'on touchait la difficulte EXTREME).
    private int[][]   runs            = new int[NB_TYPES][NB_DIFFICULTES];
    private LocalDate dernierReset    = LocalDate.now();

    // ── Reset quotidien ───────────────────────────────────────────────────
    public void mettreAJour() {
        LocalDate aujourdhui = LocalDate.now();
        if (!aujourdhui.equals(dernierReset)) {
            runs         = new int[NB_TYPES][NB_DIFFICULTES];
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

    /** Recopie dans un tableau aux dimensions actuelles : une sauvegarde anterieure a l'ajout
     *  d'une difficulte (tableau [3][3]) ne doit pas ecraser runs par un tableau trop petit,
     *  sous peine d'ArrayIndexOutOfBoundsException des qu'on accede a la nouvelle difficulte. */
    public void setRuns(int[][] v) {
        this.runs = new int[NB_TYPES][NB_DIFFICULTES];
        if (v == null) return;
        for (int i = 0; i < Math.min(v.length, NB_TYPES); i++) {
            if (v[i] != null) {
                System.arraycopy(v[i], 0, this.runs[i], 0, Math.min(v[i].length, NB_DIFFICULTES));
            }
        }
    }
    public LocalDate  getDernierReset()                { return dernierReset; }
    public void       setDernierReset(LocalDate date)  { this.dernierReset = date; }
}
