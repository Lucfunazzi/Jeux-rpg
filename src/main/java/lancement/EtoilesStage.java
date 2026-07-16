package lancement;

/**
 * Stocke les 3 étoiles d'un stage pour un chapitre donné.
 *
 * Étoile 1 : victoire (toujours gagnée si on termine le stage)
 * Étoile 2 : aucun allié KO pendant le combat
 * Étoile 3 : combat terminé en 10 tours ou moins
 */
public class EtoilesStage {

    private boolean etoile1; // victoire
    private boolean etoile2; // aucun allié mort
    private boolean etoile3; // ≤ 10 tours

    public EtoilesStage() {}

    public EtoilesStage(boolean etoile1, boolean etoile2, boolean etoile3) {
        this.etoile1 = etoile1;
        this.etoile2 = etoile2;
        this.etoile3 = etoile3;
    }

    /** Met à jour les étoiles — on ne peut qu'améliorer, jamais rétrograder. */
    public void mettreAJour(boolean e1, boolean e2, boolean e3) {
        if (e1) this.etoile1 = true;
        if (e2) this.etoile2 = true;
        if (e3) this.etoile3 = true;
    }

    public int compter() {
        return (etoile1 ? 1 : 0) + (etoile2 ? 1 : 0) + (etoile3 ? 1 : 0);
    }

    public boolean isEtoile1() { return etoile1; }
    public boolean isEtoile2() { return etoile2; }
    public boolean isEtoile3() { return etoile3; }
    public void setEtoile1(boolean v) { this.etoile1 = v; }
    public void setEtoile2(boolean v) { this.etoile2 = v; }
    public void setEtoile3(boolean v) { this.etoile3 = v; }

    public String afficher() {
        return (etoile1 ? "★" : "☆") + (etoile2 ? "★" : "☆") + (etoile3 ? "★" : "☆");
    }
}
