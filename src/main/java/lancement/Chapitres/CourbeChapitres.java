package lancement.Chapitres;

import Personnage.PersonnageBase;
import java.util.List;

/**
 * Source de verite unique pour les paliers de niveau des chapitres.
 * Niveau d'ennemi par stage, XP totale d'un chapitre et recompense XP par
 * quete en derivent tous, pour eviter que ces valeurs se desynchronisent
 * quand un palier change (chaque valeur ci-dessous n'existe qu'ici).
 */
public final class CourbeChapitres {

    private CourbeChapitres() {}

    /**
     * Chapitres normaux/Elite avec du contenu reel (ennemis, or, XP) a afficher dans les menus.
     * Les Chapitres normaux 11-13 et Elite 9-13 restent des coquilles vides (0 XP/Or/ennemis) :
     * masques ici en attendant d'etre construits, sans toucher a leur logique de deblocage.
     */
    public static final int NB_CHAPITRES_VISIBLES       = 10;
    public static final int NB_CHAPITRES_ELITE_VISIBLES = 8;

    public record Palier(int chapitre, int niveauDebut, int niveauFin) {}

    private static final List<Palier> PALIERS = List.of(
        new Palier(1, 1, 10),   new Palier(2, 10, 15),  new Palier(3, 15, 22),
        new Palier(4, 24, 30),  new Palier(5, 30, 35),  new Palier(6, 35, 40),
        new Palier(7, 40, 45),  new Palier(8, 45, 48),  new Palier(9, 48, 51),
        new Palier(10, 52, 54), new Palier(11, 54, 57), new Palier(12, 57, 59),
        new Palier(13, 61, 63)
    );

    public static Palier palier(int chapitre) {
        for (Palier p : PALIERS) if (p.chapitre() == chapitre) return p;
        throw new IllegalArgumentException("Aucun palier defini pour le chapitre " + chapitre);
    }

    /** Niveau joueur requis pour debloquer ce chapitre (= niveau de debut du palier). */
    public static int niveauRequis(int chapitre) {
        return palier(chapitre).niveauDebut();
    }

    /** Niveau d'un ennemi/invite au stage donne (1-10), interpolation lineaire debut -> fin. */
    public static int niveauEnnemiPourStage(int chapitre, int stage) {
        Palier p = palier(chapitre);
        return p.niveauDebut() + Math.round((p.niveauFin() - p.niveauDebut()) * (stage - 1) / 9f);
    }

    // Paliers Elite 1 et 2 : bande fixe de 10 niveaux, sans rapport avec le palier normal du
    // meme chapitre (verifie dans Chapitre1Elite/Chapitre2Elite avant ce refactor). A partir du
    // Chapitre 3 Elite, les niveaux sont scenarises boss par boss (montee jusqu'a 58 des le
    // Chapitre 3 Elite) et ne suivent aucune formule lineaire : ne pas les faire passer par ici
    // sans une decision de contenu (cf. divergence avec le palier normal du Chapitre 12 : 57-59).
    private static final List<Palier> PALIERS_ELITE_LINEAIRES = List.of(
        new Palier(1, 11, 20), new Palier(2, 21, 30)
    );

    /** Niveau d'un ennemi Elite au stage donne, pour les chapitres Elite a bande lineaire (1 et 2 uniquement). */
    public static int niveauEnnemiEliteLineairePourStage(int chapitreElite, int stage) {
        for (Palier p : PALIERS_ELITE_LINEAIRES) {
            if (p.chapitre() == chapitreElite) {
                return p.niveauDebut() + Math.round((p.niveauFin() - p.niveauDebut()) * (stage - 1) / 9f);
            }
        }
        throw new IllegalArgumentException(
            "Chapitre Elite " + chapitreElite + " sans bande lineaire (niveaux scenarises a la main).");
    }

    /** XP totale que les 10 quetes du chapitre doivent distribuer pour amener niveauDebut -> niveauFin. */
    public static int xpTotalChapitre(int chapitre) {
        Palier p = palier(chapitre);
        return (int) (PersonnageBase.experienceCumuleePourNiveau(p.niveauFin())
                     - PersonnageBase.experienceCumuleePourNiveau(p.niveauDebut()));
    }

    // Repartition par stage (1 a 10) : montee, plateau, pic, puis le stage final redescend
    // au niveau des stages 4/5 - motif deja utilise a la main pour tous les chapitres.
    private static final int[] POIDS = {7, 8, 9, 10, 10, 11, 11, 12, 12, 10}; // somme = 100

    /** Recompense XP de la quete du stage donne (arrondie au multiple de 10 ; le stage 10 absorbe le reste exact). */
    public static int xpQuete(int chapitre, int stage) {
        int total = xpTotalChapitre(chapitre);
        if (stage == 10) {
            int sommeAvant = 0;
            for (int s = 1; s <= 9; s++) sommeAvant += xpQuete(chapitre, s);
            return total - sommeAvant;
        }
        double exact = total * POIDS[stage - 1] / 100.0;
        return (int) (Math.round(exact / 10.0) * 10);
    }
}
