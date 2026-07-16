package lancement.Gestionnaires;

/**
 * Les 12 Clefs Célestes de Lucy.
 * Chaque clef s'invoque automatiquement à certains tours en combat,
 * indépendamment de tout personnage dans la formation.
 *
 * Niveau 1→10 via Fragments d'Étoiles.
 * Dégâts/soins basés sur l'ATK moyenne de l'équipe du joueur × multiplicateur.
 */
public enum ClefCeleste {

    // ── Disponibles avec le contenu actuel ───────────────────────────────
    TAURUS(
        "Taurus",
        "Le Taureau céleste frappe une cible ennemie et booste l'équipe.",
        "Tours pairs",
        TourType.PAIR
    ),
    VIRGO(
        "Virgo",
        "La Vierge céleste soigne l'allié le plus bas en PV.",
        "Tours impairs",
        TourType.IMPAIR
    ),

    // ── Clefs futures (désactivées — chapitres à venir) ──────────────────
    CANCER(
        "Cancer",
        "Le Cancer céleste inflige Saignement à une cible ennemie aléatoire.",
        "Tours pairs",
        TourType.PAIR
    ),
    AQUARIUS(
        "Aquarius",
        "L'Verseau céleste inflige des dégâts AoE légers à tous les ennemis.",
        "Tours pairs",
        TourType.PAIR
    ),
    SAGITTARIUS(
        "Sagittarius",
        "Le Sagittaire céleste frappe la cible avec le plus de DEF et réduit sa défense.",
        "Tours impairs",
        TourType.IMPAIR
    ),
    SCORPIO(
        "Scorpio",
        "Le Scorpion céleste applique Poison à une cible ennemie aléatoire.",
        "Tours pairs",
        TourType.PAIR
    ),
    LEO(
        "Leo",
        "Le Lion céleste accorde un buff ATK et DEF léger à toute l'équipe.",
        "Tours impairs",
        TourType.IMPAIR
    ),
    ARIES(
        "Aries",
        "Le Bélier céleste réduit l'ATK d'un ennemi aléatoire et soigne légèrement.",
        "Tours impairs",
        TourType.IMPAIR
    ),
    CAPRICORN(
        "Capricorn",
        "Le Capricorne céleste frappe l'ennemi le plus puissant et l'applique Marquage.",
        "Tours pairs",
        TourType.PAIR
    ),
    GEMINI(
        "Gemini",
        "Les Gémeaux célestes copient un effet actif allié sur toute l'équipe.",
        "Tours impairs",
        TourType.IMPAIR
    ),
    PISCES(
        "Pisces",
        "Les Poissons célestes infligent des dégâts et volent de la vie.",
        "Tours pairs",
        TourType.PAIR
    ),
    OPHIUCHUS(
        "Ophiuchus",
        "Le Serpentaire céleste frappe toute l'équipe ennemie et applique un debuff aléatoire.",
        "Tous les 3 tours",
        TourType.TOUS_LES_3
    );

    // ── Coût en Fragments d'Étoiles par palier (index 0 = lv1→2, ..., 8 = lv9→10) ──
    public static final int[] COUT_FRAGMENTS = { 5, 10, 20, 35, 55, 80, 110, 145, 185 };
    // Total pour maxer une clef : 645 fragments

    public static final int NIVEAU_MAX = 10;

    // ── Multiplicateurs dégâts/soins par niveau (% ATK moy de l'équipe) ──
    // Lv1 = 3%, Lv10 = 12%, progression linéaire
    public static double getMultiplicateur(int niveau) {
        return 0.03 + (niveau - 1) * (0.09 / 9.0);
    }

    public enum TourType { PAIR, IMPAIR, TOUS_LES_3 }

    public final String   nom;
    public final String   description;
    public final String   tourLibelle;
    public final TourType tourType;

    ClefCeleste(String nom, String description, String tourLibelle, TourType tourType) {
        this.nom         = nom;
        this.description = description;
        this.tourLibelle = tourLibelle;
        this.tourType    = tourType;
    }

    /** Retourne true si cette clef doit s'invoquer au tour donné. */
    public boolean sInvoqueAuTour(int numeroTour) {
        return switch (tourType) {
            case PAIR       -> numeroTour % 2 == 0;
            case IMPAIR     -> numeroTour % 2 != 0;
            case TOUS_LES_3 -> numeroTour % 3 == 0;
        };
    }
}