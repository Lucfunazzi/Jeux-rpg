package lancement.Gestionnaires;

import Personnage.PersonnageBase;
import java.util.List;

/**
 * Gère le compagnon actif du joueur.
 *
 * Progression :
 *   - Niveaux 1 à 10 pour chaque phase (Happy → Carla → Panthère Lily → …)
 *   - Chaque niveau coûte de l'or (coutParNiveau défini dans CompagnonsType)
 *   - Au niveau 10, le joueur peut évoluer vers le compagnon suivant (coût d'évolution)
 *   - Le bonus FLAT s'applique à ATK, PV, DEF et VIT de chaque membre de la formation
 *
 * Le menu Compagnons est débloqué au niveau 25 du joueur.
 */
public class GestionnaireCompagnons {

    public static final int NIVEAU_DEBLOCAGE = 25;
    public static final int NIVEAU_MAX       = 10;

    private CompagnonsType type;
    private int            niveau; // 1 à 10

    public GestionnaireCompagnons() {
        this.type   = CompagnonsType.HAPPY;
        this.niveau = 1;
    }

    // ── Getters ──────────────────────────────────────────────────────────

    public CompagnonsType getType()   { return type; }
    public int            getNiveau() { return niveau; }

    /** Bonus ATK flat au niveau actuel. */
    public double getBonusATK() { return type.getATK(niveau); }

    /** Bonus PV flat au niveau actuel. */
    public double getBonusPV()  { return type.getPV(niveau); }

    /** Bonus DEF flat au niveau actuel. */
    public double getBonusDEF() { return type.getDEF(niveau); }

    /** Bonus VIT flat au niveau actuel. */
    public double getBonusVIT() { return type.getVIT(niveau); }

    /** Coût en or pour monter au niveau suivant (niveau < 10). */
    public int getCoutProchainNiveau() {
        return type.coutParNiveau * niveau; // coût croissant par palier
    }

    /** Coût en or pour évoluer (niveau = 10 et évolution disponible). */
    public int getCoutEvolution() {
        return type.coutEvolution;
    }

    public boolean estAuNiveauMax() {
        return niveau >= NIVEAU_MAX;
    }

    public boolean peutEvoluer() {
        return estAuNiveauMax() && type.peutEvoluer();
    }

    // ── Actions ──────────────────────────────────────────────────────────

    /**
     * Tente d'améliorer le compagnon d'un niveau.
     * @param orDisponible or actuel du joueur
     * @return résultat sous forme de message
     */
    public ResultatCompagnon ameliorer(double orDisponible) {
        if (estAuNiveauMax()) {
            return new ResultatCompagnon(false, 0,
                type.nom + " est déjà au niveau maximum (10). Évoluez-le !");
        }
        int cout = getCoutProchainNiveau();
        if (orDisponible < cout) {
            return new ResultatCompagnon(false, 0,
                "Or insuffisant. Coût : " + cout + " or | Vous avez : " + (int) orDisponible + " or.");
        }
        niveau++;
        return new ResultatCompagnon(true, cout,
            type.nom + " est maintenant niveau " + niveau + " ! Bonus équipe : ATK +" + (int) getBonusATK()
                + " | PV +" + (int) getBonusPV() + " | DEF +" + (int) getBonusDEF() + " | VIT +" + (int) getBonusVIT());
    }

    /**
     * Tente de faire évoluer le compagnon vers la phase suivante.
     * @param orDisponible or actuel du joueur
     * @return résultat sous forme de message
     */
    public ResultatCompagnon evoluer(double orDisponible) {
        if (!estAuNiveauMax()) {
            return new ResultatCompagnon(false, 0,
                type.nom + " doit être niveau 10 pour évoluer.");
        }
        if (!type.peutEvoluer()) {
            return new ResultatCompagnon(false, 0,
                type.nom + " est déjà la forme finale disponible.");
        }
        int cout = getCoutEvolution();
        if (orDisponible < cout) {
            return new ResultatCompagnon(false, 0,
                "Or insuffisant. Coût d'évolution : " + cout + " or | Vous avez : " + (int) orDisponible + " or.");
        }
        CompagnonsType ancienType = type;
        type   = type.suivant();
        niveau = 1;
        return new ResultatCompagnon(true, cout,
            ancienType.nom + " a évolué en " + type.nom + " ! Niveau remis à 1. Bonus équipe : ATK +" + (int) getBonusATK()
                + " | PV +" + (int) getBonusPV() + " | DEF +" + (int) getBonusDEF() + " | VIT +" + (int) getBonusVIT());
    }

    // ── Application du bonus sur la formation ────────────────────────────

    /**
     * Applique le bonus compagnon à tous les membres de la formation.
     * À appeler après chaque changement de formation ET après chaque amélioration/évolution.
     */
    public void appliquerBonus(List<PersonnageBase> equipe) {
        double atk = getBonusATK();
        double pv  = getBonusPV();
        double def = getBonusDEF();
        double vit = getBonusVIT();
        for (PersonnageBase p : equipe) {
            p.setBonusCompagnonsATK(atk);
            p.setBonusCompagnonsDEF(def);
            p.setBonusCompagnonsPV(pv);
            p.setBonusCompagnonsVIT(vit);
        }
    }

    /**
     * Retire le bonus compagnon d'un personnage (quand il quitte la formation).
     */
    public void retirerBonus(PersonnageBase p) {
        p.setBonusCompagnonsATK(0);
        p.setBonusCompagnonsDEF(0);
        p.setBonusCompagnonsPV(0);
        p.setBonusCompagnonsVIT(0);
    }

    // ── Restauration depuis sauvegarde ────────────────────────────────────

    public void restaurer(String typeNom, int niveauSauvegarde) {
        try {
            this.type   = CompagnonsType.valueOf(typeNom);
            this.niveau = Math.max(1, Math.min(NIVEAU_MAX, niveauSauvegarde));
        } catch (IllegalArgumentException e) {
            // Valeur inconnue → on repart de zéro
            this.type   = CompagnonsType.HAPPY;
            this.niveau = 1;
        }
    }

    // ── Affichage console ────────────────────────────────────────────────

    public void afficher(int niveauJoueur) {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║          COMPAGNONS                  ║");
        System.out.println("╠══════════════════════════════════════╣");

        if (niveauJoueur < NIVEAU_DEBLOCAGE) {
            System.out.println("║  Débloqué au niveau " + NIVEAU_DEBLOCAGE + " du joueur.    ║");
            System.out.println("╚══════════════════════════════════════╝");
            return;
        }

        System.out.println("║  Compagnon actif : " + type.nom);
        System.out.println("║  Niveau          : " + niveau + " / " + NIVEAU_MAX);
        System.out.printf ("║  Bonus équipe    : ATK +%.0f | PV +%.0f | DEF +%.0f | VIT +%.0f%n",
                getBonusATK(), getBonusPV(), getBonusDEF(), getBonusVIT());
        System.out.println("╠══════════════════════════════════════╣");

        if (!estAuNiveauMax()) {
            System.out.println("║  [1] Améliorer → Niv." + (niveau + 1) + "  (" + getCoutProchainNiveau() + " or)");
        } else if (peutEvoluer()) {
            System.out.println("║  [1] Évoluer → " + type.suivant().nom + "  (" + getCoutEvolution() + " or)");
        } else {
            System.out.println("║  Compagnon au maximum actuel !");
        }
        System.out.println("║  [0] Retour");
        System.out.println("╚══════════════════════════════════════╝");
    }

    // ── Record résultat ──────────────────────────────────────────────────

    public record ResultatCompagnon(boolean succes, int orDepense, String message) {}
}