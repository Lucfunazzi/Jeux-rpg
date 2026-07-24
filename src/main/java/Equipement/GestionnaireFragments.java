package Equipement;

import java.util.List;

/**
 * Gère la synthèse des équipements A/S/SS/SSS/UR à partir de fragments.
 *
 * Catalogue des fragments disponibles (droppables en jeu, ou achetables à la
 * Boutique d'équipement contre des Pièces d'équipement obtenues en recyclant
 * de l'équipement obsolète). Chaque fragment correspond à un équipement unique.
 */
public class GestionnaireFragments {

    // ── Catalogue de tous les fragments disponibles ───────────────────────
    private static final List<FragmentEquipement> CATALOGUE = List.of(

        // ── Rang A (Chapitre 3 Elite) ──
        new FragmentEquipement("Baton de Foudre",    Equipement.Slot.ARME, Equipement.TypeArme.BATON, Equipement.Rarete.A),
        new FragmentEquipement("Gants de Titane",    Equipement.Slot.ARME, Equipement.TypeArme.GANTS, Equipement.Rarete.A),
        new FragmentEquipement("Lance Celeste",      Equipement.Slot.ARME, Equipement.TypeArme.LANCE, Equipement.Rarete.A),
        new FragmentEquipement("Fouet Stellaire",    Equipement.Slot.ARME, Equipement.TypeArme.FOUET, Equipement.Rarete.A),
        new FragmentEquipement("Heaume Sacré",       Equipement.Slot.COUVRE_CHEF, Equipement.TypeArme.AUCUN, Equipement.Rarete.A),
        new FragmentEquipement("Cuirasse de l'Aube", Equipement.Slot.TORSE,       Equipement.TypeArme.AUCUN, Equipement.Rarete.A),
        new FragmentEquipement("Gantelets Forgés",   Equipement.Slot.MAINS,       Equipement.TypeArme.AUCUN, Equipement.Rarete.A),
        new FragmentEquipement("Jambières de Fer",   Equipement.Slot.JAMBIERES,   Equipement.TypeArme.AUCUN, Equipement.Rarete.A),
        new FragmentEquipement("Bottes de Tempête",  Equipement.Slot.BOTTES,      Equipement.TypeArme.AUCUN, Equipement.Rarete.A),

        // ── Rang S ──
        new FragmentEquipement("Baton Astral",       Equipement.Slot.ARME, Equipement.TypeArme.BATON, Equipement.Rarete.S),
        new FragmentEquipement("Gants Astraux",      Equipement.Slot.ARME, Equipement.TypeArme.GANTS, Equipement.Rarete.S),
        new FragmentEquipement("Lance Astrale",      Equipement.Slot.ARME, Equipement.TypeArme.LANCE, Equipement.Rarete.S),
        new FragmentEquipement("Fouet Astral",       Equipement.Slot.ARME, Equipement.TypeArme.FOUET, Equipement.Rarete.S),
        new FragmentEquipement("Heaume Astral",      Equipement.Slot.COUVRE_CHEF, Equipement.TypeArme.AUCUN, Equipement.Rarete.S),
        new FragmentEquipement("Cuirasse Astrale",   Equipement.Slot.TORSE,       Equipement.TypeArme.AUCUN, Equipement.Rarete.S),
        new FragmentEquipement("Gantelets Astraux",  Equipement.Slot.MAINS,       Equipement.TypeArme.AUCUN, Equipement.Rarete.S),
        new FragmentEquipement("Jambières Astrales", Equipement.Slot.JAMBIERES,   Equipement.TypeArme.AUCUN, Equipement.Rarete.S),
        new FragmentEquipement("Bottes Astrales",    Equipement.Slot.BOTTES,      Equipement.TypeArme.AUCUN, Equipement.Rarete.S),

        // ── Rang SS ──
        new FragmentEquipement("Baton Divin",        Equipement.Slot.ARME, Equipement.TypeArme.BATON, Equipement.Rarete.SS),
        new FragmentEquipement("Gants Divins",       Equipement.Slot.ARME, Equipement.TypeArme.GANTS, Equipement.Rarete.SS),
        new FragmentEquipement("Lance Divine",       Equipement.Slot.ARME, Equipement.TypeArme.LANCE, Equipement.Rarete.SS),
        new FragmentEquipement("Fouet Divin",        Equipement.Slot.ARME, Equipement.TypeArme.FOUET, Equipement.Rarete.SS),
        new FragmentEquipement("Heaume Divin",       Equipement.Slot.COUVRE_CHEF, Equipement.TypeArme.AUCUN, Equipement.Rarete.SS),
        new FragmentEquipement("Cuirasse Divine",    Equipement.Slot.TORSE,       Equipement.TypeArme.AUCUN, Equipement.Rarete.SS),
        new FragmentEquipement("Gantelets Divins",   Equipement.Slot.MAINS,       Equipement.TypeArme.AUCUN, Equipement.Rarete.SS),
        new FragmentEquipement("Jambières Divines",  Equipement.Slot.JAMBIERES,   Equipement.TypeArme.AUCUN, Equipement.Rarete.SS),
        new FragmentEquipement("Bottes Divines",     Equipement.Slot.BOTTES,      Equipement.TypeArme.AUCUN, Equipement.Rarete.SS),

        // ── Rang SSS ──
        new FragmentEquipement("Baton Primordial",       Equipement.Slot.ARME, Equipement.TypeArme.BATON, Equipement.Rarete.SSS),
        new FragmentEquipement("Gants Primordiaux",      Equipement.Slot.ARME, Equipement.TypeArme.GANTS, Equipement.Rarete.SSS),
        new FragmentEquipement("Lance Primordiale",      Equipement.Slot.ARME, Equipement.TypeArme.LANCE, Equipement.Rarete.SSS),
        new FragmentEquipement("Fouet Primordial",       Equipement.Slot.ARME, Equipement.TypeArme.FOUET, Equipement.Rarete.SSS),
        new FragmentEquipement("Heaume Primordial",      Equipement.Slot.COUVRE_CHEF, Equipement.TypeArme.AUCUN, Equipement.Rarete.SSS),
        new FragmentEquipement("Cuirasse Primordiale",   Equipement.Slot.TORSE,       Equipement.TypeArme.AUCUN, Equipement.Rarete.SSS),
        new FragmentEquipement("Gantelets Primordiaux",  Equipement.Slot.MAINS,       Equipement.TypeArme.AUCUN, Equipement.Rarete.SSS),
        new FragmentEquipement("Jambières Primordiales", Equipement.Slot.JAMBIERES,   Equipement.TypeArme.AUCUN, Equipement.Rarete.SSS),
        new FragmentEquipement("Bottes Primordiales",    Equipement.Slot.BOTTES,      Equipement.TypeArme.AUCUN, Equipement.Rarete.SSS),

        // ── Rang UR ──
        new FragmentEquipement("Baton Transcendant",       Equipement.Slot.ARME, Equipement.TypeArme.BATON, Equipement.Rarete.UR),
        new FragmentEquipement("Gants Transcendants",      Equipement.Slot.ARME, Equipement.TypeArme.GANTS, Equipement.Rarete.UR),
        new FragmentEquipement("Lance Transcendante",      Equipement.Slot.ARME, Equipement.TypeArme.LANCE, Equipement.Rarete.UR),
        new FragmentEquipement("Fouet Transcendant",       Equipement.Slot.ARME, Equipement.TypeArme.FOUET, Equipement.Rarete.UR),
        new FragmentEquipement("Heaume Transcendant",      Equipement.Slot.COUVRE_CHEF, Equipement.TypeArme.AUCUN, Equipement.Rarete.UR),
        new FragmentEquipement("Cuirasse Transcendante",   Equipement.Slot.TORSE,       Equipement.TypeArme.AUCUN, Equipement.Rarete.UR),
        new FragmentEquipement("Gantelets Transcendants",  Equipement.Slot.MAINS,       Equipement.TypeArme.AUCUN, Equipement.Rarete.UR),
        new FragmentEquipement("Jambières Transcendantes", Equipement.Slot.JAMBIERES,   Equipement.TypeArme.AUCUN, Equipement.Rarete.UR),
        new FragmentEquipement("Bottes Transcendantes",    Equipement.Slot.BOTTES,      Equipement.TypeArme.AUCUN, Equipement.Rarete.UR)
    );

    // ── Accès au catalogue ────────────────────────────────────────────────

    public List<FragmentEquipement> getCatalogue() {
        return CATALOGUE;
    }

    /**
     * Retourne tous les fragments dont le joueur possède au moins 1 exemplaire.
     */
    public List<FragmentEquipement> getFragmentsDisponibles(Inventaire inventaire) {
        return CATALOGUE.stream()
            .filter(f -> inventaire.getQuantiteMateriau(f.getNomFragment()) > 0)
            .toList();
    }

    /**
     * Retourne tous les fragments pour lesquels le joueur a atteint la quantite requise.
     */
    public List<FragmentEquipement> getFragmentsSynthétisables(Inventaire inventaire) {
        return CATALOGUE.stream()
            .filter(f -> inventaire.getQuantiteMateriau(f.getNomFragment()) >= f.getQuantiteRequise())
            .toList();
    }

    // ── Synthèse ──────────────────────────────────────────────────────────

    /**
     * Tente de synthétiser l'équipement lié au fragment donné.
     *
     * @return L'équipement créé, ou {@code null} si fragments insuffisants.
     */
    public Equipement synthetiser(FragmentEquipement fragment, Inventaire inventaire) {
        int quantite = inventaire.getQuantiteMateriau(fragment.getNomFragment());
        if (quantite < fragment.getQuantiteRequise()) return null;

        inventaire.retirerMateriau(fragment.getNomFragment(), fragment.getQuantiteRequise());
        return EquipementFactory.creerEquipement(fragment.getNomEquipement(),
                                                 fragment.getSlot(),
                                                 fragment.getTypeArme(),
                                                 fragment.getRarete());
    }

    // ── Utilitaire d'affichage ────────────────────────────────────────────

    /**
     * Ligne d'affichage complète pour un fragment : nom + progression.
     */
    public String afficherProgression(FragmentEquipement fragment, Inventaire inventaire) {
        int qte    = inventaire.getQuantiteMateriau(fragment.getNomFragment());
        int requis = fragment.getQuantiteRequise();
        String barre = construireBarre(qte, requis);
        return String.format("%-32s %s %2d/%d",
                fragment.getNomFragment(), barre, qte, requis);
    }

    private String construireBarre(int qte, int max) {
        int rempli = Math.min(10, (int) Math.round((double) qte / max * 10));
        return "[" + "█".repeat(rempli) + "░".repeat(10 - rempli) + "]";
    }
}
