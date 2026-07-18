package lancement.Gestionnaires;

import Equipement.Inventaire;
import Personnage.PersonnageBase;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * Gère le système de fragments et d'étoiles des personnages recrutables.
 *
 * Fragments :
 *   - Chaque personnage recrutables a son propre pool de fragments nommé
 *     "Fragment: <NomPersonnage>", stocké dans les Materiaux de l'inventaire.
 *   - Le recrutement via fragments coûte : C=20, B=40, A=60, S=80, SS=80, UR=80.
 *
 * Étoiles (0 → 5) :
 *   - 0★ : personnage recruté de base
 *   - 0→1 : 1 doublon (ou fragments équivalents)
 *   - 1→2 : 2 doublons
 *   - 2→3 : 3 doublons
 *   - 3→4 : 4 doublons
 *   - 4→5 : 5 doublons
 *   - Chaque étoile = +5% ATK/DEF/PV/VIT (max +25% à 5★)
 */
public class GestionnaireEtoilesPerso {

    // ── Coût en fragments pour recruter ou monter d'une étoile ───────────
    public static int coutFragmentsRecrutement(String rarete) {
        return switch (rarete) {
            case "C"  -> 20;
            case "B"  -> 40;
            case "A"  -> 60;
            default   -> 80;   // S, SS, UR
        };
    }

    /**
     * Coût en fragments pour passer de l'étoile actuelle à la suivante.
     * = (étoile_cible) × coût_base_recrutement
     * Ex : C, 0→1 = 1×20 = 20 ; C, 2→3 = 3×20 = 60
     */
    public static int coutFragmentsEtoile(String rarete, int etoileActuelle) {
        return (etoileActuelle + 1) * coutFragmentsRecrutement(rarete);
    }

    // ── Clé de fragment ──────────────────────────────────────────────────
    public static String cleFragment(String nomPersonnage) {
        return "Frag-Perso: " + nomPersonnage;
    }

    // ── Getters inventaire ───────────────────────────────────────────────
    public static int getFragments(Inventaire inv, String nomPersonnage) {
        return inv.getQuantiteMateriau(cleFragment(nomPersonnage));
    }

    public static void ajouterFragments(Inventaire inv, String nomPersonnage, int quantite) {
        inv.ajouterMateriau(cleFragment(nomPersonnage), quantite);
    }

    public static boolean retirerFragments(Inventaire inv, String nomPersonnage, int quantite) {
        return inv.retirerMateriau(cleFragment(nomPersonnage), quantite);
    }

    // ── Recrutement via fragments ─────────────────────────────────────────
    /**
     * Vérifie si le joueur peut recruter {@code nomPersonnage} via fragments.
     * Retourne true uniquement si le personnage n'est pas déjà recruté
     * et que les fragments sont suffisants.
     */
    public static boolean peutRecruterViaFragments(Inventaire inv,
                                                   List<PersonnageBase> recrutes,
                                                   String nomPersonnage,
                                                   String rarete) {
        if (dejaRecruteParNom(recrutes, nomPersonnage)) return false;
        return getFragments(inv, nomPersonnage) >= coutFragmentsRecrutement(rarete);
    }

    /**
     * Recrute le personnage en consommant ses fragments.
     * Appeler uniquement après avoir vérifié {@link #peutRecruterViaFragments}.
     */
    public static void recruterViaFragments(Inventaire inv, String nomPersonnage, String rarete) {
        retirerFragments(inv, nomPersonnage, coutFragmentsRecrutement(rarete));
    }

    // ── Montée d'étoile ──────────────────────────────────────────────────
    /**
     * Vérifie si le joueur peut monter d'une étoile via fragments.
     */
    public static boolean peutMonterEtoileViaFragments(Inventaire inv,
                                                       PersonnageBase perso) {
        int etoiles = perso.getNbreEtoiles();
        if (etoiles >= 5) return false;
        int cout = coutFragmentsEtoile(perso.getRarete(), etoiles);
        return getFragments(inv, perso.getNom()) >= cout;
    }

    /**
     * Monte d'une étoile via fragments et applique les bonus.
     */
    public static boolean monterEtoileViaFragments(Inventaire inv, PersonnageBase perso) {
        int etoiles = perso.getNbreEtoiles();
        if (etoiles >= 5) return false;
        int cout = coutFragmentsEtoile(perso.getRarete(), etoiles);
        if (!retirerFragments(inv, perso.getNom(), cout)) return false;
        perso.monterEtoile();
        return true;
    }

    // ── Utilitaire ───────────────────────────────────────────────────────
    public static boolean dejaRecruteParNom(List<PersonnageBase> recrutes, String nom) {
        for (PersonnageBase p : recrutes)
            if (p.getNom().equalsIgnoreCase(nom)) return true;
        return false;
    }

    /** Affichage de la progression en fragments sous forme de barre. */
    public static String barreFragments(int qte, int max) {
        int rempli = Math.min(10, (int) Math.round((double) qte / max * 10));
        return "[" + "█".repeat(rempli) + "░".repeat(10 - rempli) + "]";
    }

    /** Ligne résumée pour un personnage : étoiles + bonus actuel. */
    public static String afficherEtoiles(PersonnageBase p) {
        int e = p.getNbreEtoiles();
        String etoiles = "★".repeat(e) + "☆".repeat(5 - e);
        return etoiles + "  (+" + (e * 5) + "% stats)";
    }
}
