package lancement;

import Equipement.Inventaire;
import Equipement.SceauDeRang;
import Joueur.ArbreCompetences;

public class RangJoueur {

    public enum Rang { C, B, A, S, SS, SSS, UR }

    private static final double[] MULTIPLICATEURS = { 1.00, 1.35, 1.80, 2.50, 3.50, 4.20, 5.00 };
    private static final int      SCEAUX_PAR_COFFRE = 5;

    private Rang rang = Rang.C;

    // ── Coffre d'Arene (une fois par palier de rang atteint) ──────────────
    private boolean[] coffreRangReclame = new boolean[Rang.values().length];

    /** Vrai si le coffre d'Arene du rang actuel n'a pas encore ete reclame. */
    public boolean estCoffreRangDisponible() { return !coffreRangReclame[rang.ordinal()]; }

    /** Reclame le coffre d'Arene du rang actuel : +5x Sceau de Rang correspondant. */
    public String reclamerCoffreRang(Inventaire inventaire) {
        if (coffreRangReclame[rang.ordinal()]) return "Coffre d'Arene deja reclame pour ce rang.";

        coffreRangReclame[rang.ordinal()] = true;
        SceauDeRang sceau = SceauDeRang.valueOf(rang.name());
        inventaire.ajouterMateriau(sceau.nom, SCEAUX_PAR_COFFRE);
        return "Coffre d'Arene reclame ! +" + SCEAUX_PAR_COFFRE + "x " + sceau.nom;
    }

    public boolean[] getCoffreRangReclame() { return coffreRangReclame; }

    public void setCoffreRangReclame(boolean[] v) {
        boolean[] resultat = new boolean[Rang.values().length];
        if (v != null) System.arraycopy(v, 0, resultat, 0, Math.min(v.length, resultat.length));
        this.coffreRangReclame = resultat;
    }

    // Niveau joueur requis pour monter depuis B, A, S, SS, SSS (C a sa propre condition, voir ci-dessous).
    // D'autres conditions pourront s'ajouter plus tard en plus du niveau.
    private static final int[] NIVEAU_REQUIS_DEPUIS = { 30, 50, 70, 80, 90, 100 }; // C, B, A, S, SS, SSS

    // ── Tentative de montée de rang ───────────────────────────────────────
    public String tenterMonteeRang(int niveauJoueur, ArbreCompetences arbre) {
        int i = rang.ordinal();
        if (i >= Rang.values().length - 1)
            return "Vous etes deja au rang maximum : UR !";

        if (rang == Rang.C) {
            if (!arbre.isNoeud10Debloque())
                return "Completez l'arbre 1 pour monter de rang.";
            if (!arbre.isNoeud10Arbre2Debloque())
                return "Completez l'arbre 2 pour monter de rang.";
        }

        int niveauRequis = NIVEAU_REQUIS_DEPUIS[i];
        if (niveauJoueur < niveauRequis)
            return "Atteignez le niveau " + niveauRequis + " pour monter de rang.";

        rang = Rang.values()[i + 1];
        return "OK";
    }

    // ── Multiplicateur de stats lié au rang ───────────────────────────────
    public double getMultiplicateur() {
        return MULTIPLICATEURS[rang.ordinal()];
    }

    // ── Getters / Setters ─────────────────────────────────────────────────
    public Rang   getRang()    { return rang; }
    public String getRangNom() { return rang.name(); }
    public void   setRang(Rang r) { this.rang = r; }
    public void   setRangDepuisNom(String nom) {
        try { this.rang = Rang.valueOf(nom); }
        catch (IllegalArgumentException e) { this.rang = Rang.C; }
    }

    // ── Affichage ─────────────────────────────────────────────────────────
    public String afficherRang() {
        int i = rang.ordinal();
        StringBuilder sb = new StringBuilder();
        sb.append("Rang : ").append(rang.name());
        sb.append("  |  Multiplicateur : x").append(String.format("%.2f", MULTIPLICATEURS[i]));
        if (i < Rang.values().length - 1) {
            sb.append("\nProchain rang [").append(Rang.values()[i + 1].name()).append("] : ");
            if (rang == Rang.C)
                sb.append("Completez les arbres 1 et 2, niveau ").append(NIVEAU_REQUIS_DEPUIS[i]);
            else
                sb.append("Niveau ").append(NIVEAU_REQUIS_DEPUIS[i]);
        } else {
            sb.append("  [RANG MAXIMUM]");
        }
        return sb.toString();
    }
}