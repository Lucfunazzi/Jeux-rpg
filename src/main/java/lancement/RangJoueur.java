package lancement;

import Joueur.ArbreCompetences;

public class RangJoueur {

    public enum Rang { C, B, A, S, SS, UR }

    private static final double[] MULTIPLICATEURS = { 1.00, 1.35, 1.80, 2.50, 3.50, 5.00 };

    private Rang rang = Rang.C;

    // ── Tentative de montée de rang ───────────────────────────────────────
    public String tenterMonteeRang(int niveauJoueur, ArbreCompetences arbre) {
        int i = rang.ordinal();
        if (i >= Rang.values().length - 1)
            return "Vous etes deja au rang maximum : UR !";

        switch (rang) {
            case C -> {
                if (!arbre.isNoeud10Debloque())
                    return "Completez l'arbre 1 pour monter de rang.";
                if (!arbre.isNoeud10Arbre2Debloque())
                    return "Completez l'arbre 2 pour monter de rang.";
                if (!arbre.isNoeud10Arbre3Debloque())
                    return "Completez l'arbre 3 pour monter de rang.";
            }
            default -> {
                return "La condition pour passer au rang suivant n'est pas encore disponible.";
            }
        }

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
                sb.append("Completez les arbres 1 et 2");
            else
                sb.append("Condition a venir");
        } else {
            sb.append("  [RANG MAXIMUM]");
        }
        return sb.toString();
    }
}