package lancement.Gestionnaires;

import java.util.EnumMap;
import java.util.Map;

/**
 * Gère la collection de Clefs Célestes du joueur.
 * - Chaque clef peut être obtenue (débloquée) ou non.
 * - Chaque clef a un niveau 1→10 améliorable via Fragments d'Étoiles.
 * - Une seule clef peut être "active" en combat à la fois.
 */
public class GestionnaireClefsCelestes {

    /** État d'une clef dans la collection du joueur. */
    public static class EtatClef {
        public boolean debloquee = false;
        public int     niveau    = 1;

        public EtatClef() {}
        public EtatClef(boolean debloquee, int niveau) {
            this.debloquee = debloquee;
            this.niveau    = niveau;
        }
    }

    private final Map<ClefCeleste, EtatClef> collection = new EnumMap<>(ClefCeleste.class);
    private ClefCeleste clefActive = null;

    public GestionnaireClefsCelestes() {
        for (ClefCeleste c : ClefCeleste.values())
            collection.put(c, new EtatClef());
    }

    // ── Déblocage ─────────────────────────────────────────────────────────

    /**
     * Débloque une clef (première obtention).
     * @return false si déjà débloquée
     */
    public boolean debloquer(ClefCeleste clef) {
        EtatClef e = collection.get(clef);
        if (e.debloquee) return false;
        e.debloquee = true;
        return true;
    }

    public boolean estDebloquee(ClefCeleste clef) {
        return collection.get(clef).debloquee;
    }

    // ── Activation ────────────────────────────────────────────────────────

    /** Active une clef pour le combat. Doit être débloquée. */
    public boolean activer(ClefCeleste clef) {
        if (!estDebloquee(clef)) return false;
        this.clefActive = clef;
        return true;
    }

    /** Désactive la clef active (aucune clef en combat). */
    public void desactiver() { this.clefActive = null; }

    public ClefCeleste getClefActive() { return clefActive; }
    public int         getNiveauClefActive() {
        return clefActive != null ? collection.get(clefActive).niveau : 1;
    }

    // ── Amélioration ──────────────────────────────────────────────────────

    /**
     * Tente d'améliorer la clef d'un niveau.
     * @param clef la clef à améliorer
     * @param fragmentsDispo fragments disponibles dans l'inventaire
     * @return le coût en fragments si succès, -1 si niveau max, -2 si fragments insuffisants
     */
    public int ameliorer(ClefCeleste clef, int fragmentsDispo) {
        EtatClef e = collection.get(clef);
        if (!e.debloquee) return -3;
        if (e.niveau >= ClefCeleste.NIVEAU_MAX) return -1;
        int cout = ClefCeleste.COUT_FRAGMENTS[e.niveau - 1];
        if (fragmentsDispo < cout) return -2;
        e.niveau++;
        return cout;
    }

    public int getNiveau(ClefCeleste clef)   { return collection.get(clef).niveau; }
    public int getCoutProchainNiveau(ClefCeleste clef) {
        int niv = collection.get(clef).niveau;
        if (niv >= ClefCeleste.NIVEAU_MAX) return 0;
        return ClefCeleste.COUT_FRAGMENTS[niv - 1];
    }

    public Map<ClefCeleste, EtatClef> getCollection() { return collection; }

    // ── Restauration sauvegarde ───────────────────────────────────────────

    public void restaurer(String nomClef, boolean debloquee, int niveau, boolean active) {
        try {
            ClefCeleste clef = ClefCeleste.valueOf(nomClef);
            EtatClef e = collection.get(clef);
            e.debloquee = debloquee;
            e.niveau    = Math.max(1, Math.min(ClefCeleste.NIVEAU_MAX, niveau));
            if (active && debloquee) clefActive = clef;
        } catch (IllegalArgumentException ignored) {}
    }

    // ── Arène : génère une clef aléatoire selon le rang ──────────────────

    /**
     * Génère une clef pour un faux joueur en arène selon son rang.
     * Rangs hauts (bas numéros) → clefs fortes ; rangs bas → clefs faibles ou aucune.
     * @return null si aucune clef (rangs très bas)
     */
    public static ClefCeleste genererClefArene(int rang, java.util.Random rng) {
        // Rangs 76-100 : 30% de chance d'avoir une clef (faible)
        if (rang > 75) {
            if (rng.nextInt(100) >= 30) return null;
            return rng.nextBoolean() ? ClefCeleste.TAURUS : ClefCeleste.VIRGO;
        }
        // Rangs 51-75 : 60% de chance
        if (rang > 50) {
            if (rng.nextInt(100) >= 60) return null;
            ClefCeleste[] faibles = { ClefCeleste.TAURUS, ClefCeleste.VIRGO, ClefCeleste.CANCER };
            return faibles[rng.nextInt(faibles.length)];
        }
        // Rangs 26-50 : toujours une clef, pool étendu
        if (rang > 25) {
            ClefCeleste[] moyennes = {
                ClefCeleste.TAURUS, ClefCeleste.VIRGO, ClefCeleste.CANCER,
                ClefCeleste.AQUARIUS, ClefCeleste.SAGITTARIUS, ClefCeleste.SCORPIO
            };
            return moyennes[rng.nextInt(moyennes.length)];
        }
        // Rangs 11-25 : pool complet
        if (rang > 10) {
            ClefCeleste[] valeurs = ClefCeleste.values();
            // Exclure Ophiuchus (rang 1-5 seulement)
            return valeurs[rng.nextInt(valeurs.length - 1)];
        }
        // Rangs 1-10 : n'importe laquelle y compris Ophiuchus
        ClefCeleste[] toutes = ClefCeleste.values();
        return toutes[rng.nextInt(toutes.length)];
    }

    /**
     * Niveau de la clef arène selon le rang de l'adversaire.
     */
    public static int genererNiveauClefArene(int rang, java.util.Random rng) {
        if (rang <= 1)  return 8 + rng.nextInt(3);   // 8-10
        if (rang <= 5)  return 6 + rng.nextInt(3);   // 6-8
        if (rang <= 10) return 4 + rng.nextInt(3);   // 4-6
        if (rang <= 25) return 3 + rng.nextInt(3);   // 3-5
        if (rang <= 50) return 2 + rng.nextInt(2);   // 2-3
        return 1;
    }
}