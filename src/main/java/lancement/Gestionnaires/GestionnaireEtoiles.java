package lancement.Gestionnaires;

import lancement.EtoilesStage;
import java.util.HashMap;
import java.util.Map;

/**
 * Gère les étoiles et les coffres pour tous les chapitres.
 *
 * Clé de la map : "C1S3" = Chapitre 1 Stage 3
 *                 "C1E5" = Chapitre 1 Elite Stage 5
 *                 "C2S7" = Chapitre 2 Stage 7
 *
 * Coffres par chapitre (clé : "C1", "C1E", "C2"...) :
 *   Coffre 1 : 9  étoiles → 2 Parchemins de Tirage Ordinaire
 *   Coffre 2 : 18 étoiles → 5 Parchemins de Tirage Ordinaire
 *   Coffre 3 : 30 étoiles → 1 Parchemin de Tirage Elite
 */
public class GestionnaireEtoiles {

    private static final int[] SEUILS     = {9, 18, 30};
    private static final int[] NB_STAGES  = {10};   // kept for compat
    private static final int   NB_STAGES_INT = 10;

    /** Types de récompense des coffres étoiles. */
    public enum TypeRecompenseCoffre {
        PARCHEMIN_ORDINAIRE,
        PARCHEMIN_ELITE
    }

    public record RecompenseCoffre(TypeRecompenseCoffre type, int quantite) {}

    private static final RecompenseCoffre[] RECOMPENSES = {
        new RecompenseCoffre(TypeRecompenseCoffre.PARCHEMIN_ORDINAIRE, 2),   // coffre 1
        new RecompenseCoffre(TypeRecompenseCoffre.PARCHEMIN_ORDINAIRE, 5),   // coffre 2
        new RecompenseCoffre(TypeRecompenseCoffre.PARCHEMIN_ELITE,     1)    // coffre 3
    };

    // étoiles par stage : clé = "C1S3", "C1E5", "C2S7"...
    private final Map<String, EtoilesStage> etoiles = new HashMap<>();

    // coffres réclamés : clé = "C1_1", "C1_2", "C1_3", "C2_1"...
    private final Map<String, Boolean> coffresClaimes = new HashMap<>();

    // ── Clés ─────────────────────────────────────────────────────────────

    public static String cle(int chapitre, int stage, boolean elite) {
        if (elite) return "C" + chapitre + "E" + stage;
        return "C" + chapitre + "S" + stage;
    }

    private static String cleGroupe(int chapitre, boolean elite) {
        return elite ? "C" + chapitre + "E" : "C" + chapitre;
    }

    private static String cleCoffre(int chapitre, boolean elite, int numeroCoffre) {
        return cleGroupe(chapitre, elite) + "_" + numeroCoffre;
    }

    // ── Mise à jour après un combat ──────────────────────────────────────

    public void mettreAJour(int chapitre, int stage, boolean elite,
                             boolean victoire, boolean sansAllieMort, boolean enMoinsDe10Tours) {
        if (!victoire) return;
        String k = cle(chapitre, stage, elite);
        etoiles.computeIfAbsent(k, x -> new EtoilesStage())
               .mettreAJour(victoire, sansAllieMort, enMoinsDe10Tours);
    }

    // ── Comptage ─────────────────────────────────────────────────────────

    public int compterEtoiles(int chapitre, boolean elite) {
        int total = 0;
        for (int s = 1; s <= NB_STAGES_INT; s++) {
            EtoilesStage e = etoiles.get(cle(chapitre, s, elite));
            if (e != null) total += e.compter();
        }
        return total;
    }

    public EtoilesStage getEtoiles(int chapitre, int stage, boolean elite) {
        return etoiles.getOrDefault(cle(chapitre, stage, elite), new EtoilesStage());
    }

    // ── Coffres ──────────────────────────────────────────────────────────

    /** Retourne true si le coffre est disponible (seuil atteint et pas encore réclamé). */
    public boolean coffreDisponible(int chapitre, boolean elite, int numeroCoffre) {
        if (numeroCoffre < 1 || numeroCoffre > 3) return false;
        String k = cleCoffre(chapitre, elite, numeroCoffre);
        if (Boolean.TRUE.equals(coffresClaimes.get(k))) return false;
        return compterEtoiles(chapitre, elite) >= SEUILS[numeroCoffre - 1];
    }

    public boolean coffreReclame(int chapitre, boolean elite, int numeroCoffre) {
        return Boolean.TRUE.equals(coffresClaimes.get(cleCoffre(chapitre, elite, numeroCoffre)));
    }

    /** Réclame le coffre et retourne la récompense (null si non disponible). */
    public RecompenseCoffre reclamerCoffre(int chapitre, boolean elite, int numeroCoffre) {
        if (!coffreDisponible(chapitre, elite, numeroCoffre)) return null;
        coffresClaimes.put(cleCoffre(chapitre, elite, numeroCoffre), true);
        return RECOMPENSES[numeroCoffre - 1];
    }

    public RecompenseCoffre getRecompenseCoffre(int numeroCoffre) {
        if (numeroCoffre < 1 || numeroCoffre > 3) return null;
        return RECOMPENSES[numeroCoffre - 1];
    }

    public int getSeuilCoffre(int numeroCoffre) {
        if (numeroCoffre < 1 || numeroCoffre > 3) return 0;
        return SEUILS[numeroCoffre - 1];
    }

    // ── Sauvegarde ───────────────────────────────────────────────────────

    public Map<String, EtoilesStage> getEtoilesMap()     { return etoiles; }
    public Map<String, Boolean>      getCoffresClaimes()  { return coffresClaimes; }

    public void setEtoiles(String cle, boolean e1, boolean e2, boolean e3) {
        etoiles.put(cle, new EtoilesStage(e1, e2, e3));
    }

    public void setCoffreClaime(String cle, boolean value) {
        coffresClaimes.put(cle, value);
    }
}