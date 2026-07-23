package Equipement;

/**
 * Fragment d'équipement — matériau spécial droppable en Chapitre Elite.
 * Il en faut {@link #QUANTITE_REQUISE} pour synthétiser l'équipement cible.
 *
 * Les fragments sont stockés dans l'inventaire comme des Materiaux dont le nom
 * suit la convention : "Fragment: <nomEquipement>".
 * Cette classe sert uniquement à définir les associations fragment ↔ équipement A.
 */
public class FragmentEquipement {

    /** Nombre de fragments nécessaires pour créer un équipement. */
    public static final int QUANTITE_REQUISE = 20;

    private final String     nomFragment;   // ex. "Fragment: Baton de Foudre"
    private final String     nomEquipement; // ex. "Baton de Foudre"
    private final Equipement.Slot   slot;
    private final Equipement.TypeArme typeArme;

    public FragmentEquipement(String nomEquipement, Equipement.Slot slot, Equipement.TypeArme typeArme) {
        this.nomEquipement = nomEquipement;
        this.slot          = slot;
        this.typeArme      = typeArme;
        this.nomFragment   = "Fragment: " + nomEquipement;
    }

    public String getNomFragment()   { return nomFragment; }
    public String getNomEquipement() { return nomEquipement; }
    public Equipement.Slot     getSlot()     { return slot; }
    public Equipement.TypeArme getTypeArme() { return typeArme; }

    @Override
    public String toString() {
        return nomFragment + " (" + QUANTITE_REQUISE + " → " + nomEquipement + " [A])";
    }
}
