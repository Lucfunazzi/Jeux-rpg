package Equipement;

/**
 * Fragment d'équipement — matériau spécial droppable en jeu ou acheté à la Boutique
 * d'équipement (contre des Pièces d'équipement obtenues en recyclant de l'équipement).
 * Il en faut {@link #getQuantiteRequise()} pour synthétiser l'équipement cible ; ce nombre
 * augmente avec la rareté de la pièce visée (A/S/SS/SSS/UR).
 *
 * Les fragments sont stockés dans l'inventaire comme des Materiaux dont le nom
 * suit la convention : "Fragment: <nomEquipement>".
 * Cette classe sert uniquement à définir les associations fragment ↔ équipement.
 */
public class FragmentEquipement {

    /** Prefixe des noms de materiaux representant des fragments d'equipement dans l'inventaire. */
    public static final String PREFIXE_FRAGMENT = "Fragment: ";

    private final String     nomFragment;   // ex. "Fragment: Baton de Foudre"
    private final String     nomEquipement; // ex. "Baton de Foudre"
    private final Equipement.Slot   slot;
    private final Equipement.TypeArme typeArme;
    private final Equipement.Rarete rarete;

    public FragmentEquipement(String nomEquipement, Equipement.Slot slot,
                              Equipement.TypeArme typeArme, Equipement.Rarete rarete) {
        this.nomEquipement = nomEquipement;
        this.slot          = slot;
        this.typeArme      = typeArme;
        this.rarete        = rarete;
        this.nomFragment   = PREFIXE_FRAGMENT + nomEquipement;
    }

    public String getNomFragment()   { return nomFragment; }
    public String getNomEquipement() { return nomEquipement; }
    public Equipement.Slot     getSlot()     { return slot; }
    public Equipement.TypeArme getTypeArme() { return typeArme; }
    public Equipement.Rarete   getRarete()   { return rarete; }

    /** Nombre de fragments necessaires pour synthetiser cette piece — augmente avec la rarete. */
    public int getQuantiteRequise() { return quantiteRequisePour(rarete); }

    /** Nombre de fragments necessaires pour synthetiser une piece de cette rarete. */
    public static int quantiteRequisePour(Equipement.Rarete rarete) {
        return switch (rarete) {
            case C, B, A -> 20;
            case S        -> 30;
            case SS       -> 45;
            case SSS      -> 65;
            case UR       -> 90;
        };
    }

    @Override
    public String toString() {
        return nomFragment + " (" + getQuantiteRequise() + " → " + nomEquipement + " [" + rarete + "])";
    }
}
