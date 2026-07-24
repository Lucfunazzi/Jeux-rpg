package lancement.Gestionnaires;

import java.time.LocalDate;

/**
 * Suivi de l'Examen de Rang S : 10 stages, deblocage sequentiel permanent,
 * mais une seule tentative par stage et par jour (reset a minuit).
 * Premiere reussite d'un stage = garantie ; les reussites suivantes ont 30% de chance de recompense.
 */
public class GestionnaireExamenS {

    public static final int NB_STAGES = 40;
    public static final int NIVEAU_REQUIS = 15;

    private boolean[] dejaReussi     = new boolean[NB_STAGES + 1]; // index 1..10, permanent
    private boolean[] faitAujourdhui = new boolean[NB_STAGES + 1]; // index 1..10, reset quotidien
    private LocalDate dernierReset   = LocalDate.now();

    // ── Reset quotidien ───────────────────────────────────────────────────
    public void mettreAJour() {
        LocalDate aujourdhui = LocalDate.now();
        if (!aujourdhui.equals(dernierReset)) {
            faitAujourdhui = new boolean[NB_STAGES + 1];
            dernierReset   = aujourdhui;
        }
    }

    public boolean estDebloque(int stage) {
        return stage == 1 || dejaReussi[stage - 1];
    }

    public boolean estDejaReussi(int stage)     { return dejaReussi[stage]; }

    public boolean estFaitAujourdhui(int stage) {
        mettreAJour();
        return faitAujourdhui[stage];
    }

    public boolean peutTenter(int stage) {
        return estDebloque(stage) && !estFaitAujourdhui(stage);
    }

    /** A appeler apres une victoire (manuelle ou via ratissage). Retourne true si une boite a ete gagnee. */
    public boolean enregistrerReussite(int stage) {
        mettreAJour();
        boolean premiereFois = !dejaReussi[stage];
        dejaReussi[stage]     = true;
        faitAujourdhui[stage] = true;
        return premiereFois || Math.random() < 0.30;
    }

    // ── Getters/Setters pour sauvegarde ───────────────────────────────────
    public boolean[] getDejaReussi()               { return dejaReussi; }
    public void      setDejaReussi(boolean[] v)    { this.dejaReussi = redimensionner(v); }
    public boolean[] getFaitAujourdhui()            { return faitAujourdhui; }
    public void      setFaitAujourdhui(boolean[] v) { this.faitAujourdhui = redimensionner(v); }

    /** Recopie dans un tableau a la taille actuelle de NB_STAGES+1, pour rester compatible
     *  avec une sauvegarde plus ancienne (moins de stages) sans deborder. */
    private boolean[] redimensionner(boolean[] v) {
        boolean[] resultat = new boolean[NB_STAGES + 1];
        if (v != null) System.arraycopy(v, 0, resultat, 0, Math.min(v.length, resultat.length));
        return resultat;
    }
    public LocalDate getDernierReset()              { return dernierReset; }
    public void      setDernierReset(LocalDate d)   { if (d != null) this.dernierReset = d; }
}
