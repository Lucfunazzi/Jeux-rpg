package Equipement;

import java.util.List;

/**
 * Gère la synthèse des équipements rang A à partir de fragments.
 *
 * Catalogue des fragments disponibles (droppables en Chapitre 3 Elite).
 * Chaque fragment correspond à un équipement rang A unique.
 */
public class GestionnaireFragments {

    // ── Catalogue de tous les fragments disponibles ───────────────────────
    private static final List<FragmentEquipement> CATALOGUE = List.of(

        // Armes
        new FragmentEquipement("Baton de Foudre",    Equipement.Slot.ARME, Equipement.TypeArme.BATON),
        new FragmentEquipement("Gants de Titane",    Equipement.Slot.ARME, Equipement.TypeArme.GANTS),

        // Armures
        new FragmentEquipement("Heaume Sacré",       Equipement.Slot.COUVRE_CHEF, Equipement.TypeArme.AUCUN),
        new FragmentEquipement("Cuirasse de l'Aube", Equipement.Slot.TORSE,       Equipement.TypeArme.AUCUN),
        new FragmentEquipement("Gantelets Forgés",   Equipement.Slot.MAINS,       Equipement.TypeArme.AUCUN),
        new FragmentEquipement("Jambières de Fer",   Equipement.Slot.JAMBIERES,   Equipement.TypeArme.AUCUN),
        new FragmentEquipement("Bottes de Tempête",  Equipement.Slot.BOTTES,      Equipement.TypeArme.AUCUN)
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
     * Retourne tous les fragments pour lesquels le joueur a atteint 20 exemplaires.
     */
    public List<FragmentEquipement> getFragmentsSynthétisables(Inventaire inventaire) {
        return CATALOGUE.stream()
            .filter(f -> inventaire.getQuantiteMateriau(f.getNomFragment())
                         >= FragmentEquipement.QUANTITE_REQUISE)
            .toList();
    }

    // ── Synthèse ──────────────────────────────────────────────────────────

    /**
     * Tente de synthétiser l'équipement rang A lié au fragment donné.
     *
     * @return L'équipement créé, ou {@code null} si fragments insuffisants.
     */
    public Equipement synthetiser(FragmentEquipement fragment, Inventaire inventaire) {
        int quantite = inventaire.getQuantiteMateriau(fragment.getNomFragment());
        if (quantite < FragmentEquipement.QUANTITE_REQUISE) return null;

        inventaire.retirerMateriau(fragment.getNomFragment(), FragmentEquipement.QUANTITE_REQUISE);
        return EquipementFactory.creerEquipementA(fragment.getNomEquipement(),
                                                  fragment.getSlot(),
                                                  fragment.getTypeArme());
    }

    // ── Utilitaire d'affichage ────────────────────────────────────────────

    /**
     * Ligne d'affichage complète pour un fragment : nom + progression.
     */
    public String afficherProgression(FragmentEquipement fragment, Inventaire inventaire) {
        int qte    = inventaire.getQuantiteMateriau(fragment.getNomFragment());
        int requis = FragmentEquipement.QUANTITE_REQUISE;
        String barre = construireBarre(qte, requis);
        return String.format("%-32s %s %2d/%d",
                fragment.getNomFragment(), barre, qte, requis);
    }

    private String construireBarre(int qte, int max) {
        int rempli = Math.min(10, (int) Math.round((double) qte / max * 10));
        return "[" + "█".repeat(rempli) + "░".repeat(10 - rempli) + "]";
    }
}
