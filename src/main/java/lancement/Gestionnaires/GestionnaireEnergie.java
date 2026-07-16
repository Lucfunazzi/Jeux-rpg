package lancement.Gestionnaires;

import Joueur.Personnage_principale;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Duration;

public class GestionnaireEnergie {

    private static final int ENERGIE_MAX            = 100;
    private static final int RECHARGE_MINUTES       = 6;
    private static final int ENERGIE_PAR_COUPON     = 20;
    private static final int MAX_RECHARGES_PAR_JOUR = 3;
    private static final int[] COUT_COUPONS         = {10, 30, 50};
    private static final int MAX_RUNS_ELITE         = 10;
    private static final int MAX_STAGES_ELITE       = 20; // augmenter si nécessaire

    private int           energie;
    private LocalDateTime derniereRecharge;
    private int           rechargesUtilisees   = 0;
    private LocalDate     dernierResetRecharge;
    private int[]         runsEliteParStage    = new int[MAX_STAGES_ELITE + 1]; // index 1-MAX
    private LocalDate     dernierResetRunsElite;

    public GestionnaireEnergie() {
        this.energie               = ENERGIE_MAX;
        this.derniereRecharge      = LocalDateTime.now();
        this.dernierResetRecharge  = LocalDate.now();
        this.dernierResetRunsElite = LocalDate.now();
    }

    // ── Reset journalier ──────────────────────────────────────────────────
    private void resetJournalierSiNecessaire() {
        LocalDate aujourdhui = LocalDate.now();
        if (!aujourdhui.equals(dernierResetRecharge)) {
            rechargesUtilisees   = 0;
            dernierResetRecharge = aujourdhui;
        }
        if (!aujourdhui.equals(dernierResetRunsElite)) {
            runsEliteParStage     = new int[MAX_STAGES_ELITE + 1];
            dernierResetRunsElite = aujourdhui;
        }
    }

    // ── Recharge automatique ──────────────────────────────────────────────
    public void mettreAJourRecharge() {
        resetJournalierSiNecessaire();

        if (energie >= ENERGIE_MAX) return;

        LocalDateTime maintenant = LocalDateTime.now();
        long minutesEcoulees = Duration.between(derniereRecharge, maintenant).toMinutes();

        if (minutesEcoulees >= RECHARGE_MINUTES) {
            int energieAajouter    = (int)(minutesEcoulees / RECHARGE_MINUTES);
            energie                = Math.min(ENERGIE_MAX, energie + energieAajouter);
            long minutesConsommees = (long) energieAajouter * RECHARGE_MINUTES;
            derniereRecharge       = derniereRecharge.plusMinutes(minutesConsommees);
        }
    }

    // ── Consommer de l'énergie ────────────────────────────────────────────
    public boolean consommerEnergie(int cout) {
        mettreAJourRecharge();
        if (energie < cout) return false;
        energie -= cout;
        return true;
    }

    // ── Runs élite par stage ──────────────────────────────────────────────
    private boolean stageValide(int numeroStage) {
        if (numeroStage < 1 || numeroStage > MAX_STAGES_ELITE) {
            System.out.println("[ERREUR] Numero de stage elite invalide : " + numeroStage);
            return false;
        }
        return true;
    }

    public boolean peutFaireRunElite(int numeroStage) {
        if (!stageValide(numeroStage)) return false;
        resetJournalierSiNecessaire();
        return runsEliteParStage[numeroStage] < MAX_RUNS_ELITE;
    }

    public void enregistrerRunElite(int numeroStage) {
        if (!stageValide(numeroStage)) return;
        resetJournalierSiNecessaire();
        runsEliteParStage[numeroStage]++;
    }

    public int getRunsEliteRestants(int numeroStage) {
        if (!stageValide(numeroStage)) return 0;
        resetJournalierSiNecessaire();
        return MAX_RUNS_ELITE - runsEliteParStage[numeroStage];
    }

    // ── Recharge coupons ──────────────────────────────────────────────────
    public boolean peutRechargerAvecCoupon() {
        return rechargesUtilisees < MAX_RECHARGES_PAR_JOUR;
    }

    public int getCoutProchaineRecharge() {
        if (rechargesUtilisees >= MAX_RECHARGES_PAR_JOUR) return -1;
        return COUT_COUPONS[rechargesUtilisees];
    }

    public boolean rechargerAvecCoupon(Personnage_principale joueur) {
        mettreAJourRecharge();
        if (!peutRechargerAvecCoupon()) {
            System.out.println("Limite de recharges atteinte pour aujourd'hui ("
                    + MAX_RECHARGES_PAR_JOUR + "/" + MAX_RECHARGES_PAR_JOUR + ").");
            return false;
        }
        int cout = getCoutProchaineRecharge();
        if (joueur.getCoupons() < cout) {
            System.out.println("Pas assez de coupons ! (vous avez "
                    + joueur.getCoupons() + ", il faut " + cout + ")");
            return false;
        }
        joueur.setCoupons(joueur.getCoupons() - cout);
        energie = Math.min(ENERGIE_MAX, energie + ENERGIE_PAR_COUPON);
        rechargesUtilisees++;
        System.out.println("  +" + ENERGIE_PAR_COUPON + " energie ! (Total : " + energie + "/" + ENERGIE_MAX + ")");
        System.out.println("  Recharges restantes aujourd'hui : "
                + (MAX_RECHARGES_PAR_JOUR - rechargesUtilisees) + "/" + MAX_RECHARGES_PAR_JOUR);
        return true;
    }

    // ── Affichage ─────────────────────────────────────────────────────────
    public String afficherEnergie() {
        mettreAJourRecharge();
        String recharge = energie < ENERGIE_MAX
                ? "  (recharge dans " + getMinutesAvantProchaineRecharge() + " min)"
                : "  (pleine)";
        return "Energie : " + energie + "/" + ENERGIE_MAX + recharge;
    }

    private long getMinutesAvantProchaineRecharge() {
        LocalDateTime prochaine = derniereRecharge.plusMinutes(RECHARGE_MINUTES);
        long minutes = Duration.between(LocalDateTime.now(), prochaine).toMinutes();
        return Math.max(0, minutes);
    }

    // ── Getters/Setters pour sauvegarde ───────────────────────────────────
    public int getEnergie()                          { return energie; }
    public void setEnergie(int energie)              { this.energie = Math.min(ENERGIE_MAX, energie); }
    public LocalDateTime getDerniereRecharge()       { return derniereRecharge; }
    public void setDerniereRecharge(LocalDateTime d) { this.derniereRecharge = d; }
    public int getRechargesUtilisees()               { return rechargesUtilisees; }
    public void setRechargesUtilisees(int n)         { this.rechargesUtilisees = n; }
    public LocalDate getDernierResetRecharge()       { return dernierResetRecharge; }
    public void setDernierResetRecharge(LocalDate d) { this.dernierResetRecharge = d; }
    public int[] getRunsEliteParStage()              { return runsEliteParStage; }
    public void setRunsEliteParStage(int[] runs)     { this.runsEliteParStage = runs; }
    public LocalDate getDernierResetRunsElite()      { return dernierResetRunsElite; }
    public void setDernierResetRunsElite(LocalDate d){ this.dernierResetRunsElite = d; }
    public int getEnergieMax()                       { return ENERGIE_MAX; }
    public int getMaxStagesElite()                   { return MAX_STAGES_ELITE; }
}