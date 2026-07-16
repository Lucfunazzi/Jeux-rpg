package Equipement;

/**
 * Fabrique les instances d'équipements.
 * Ajouter ici les nouvelles pièces au fur et à mesure des rangs.
 */
public class EquipementFactory {

    // ── Rang C ────────────────────────────────────────────────────────────

    public static Equipement kunaiC() {
        return new Equipement("Kunai tranchant", Equipement.Slot.ARME, Equipement.Rarete.C,
                Equipement.TypeArme.KUNAI, 20, 0, 0, 0);
    }

    public static Equipement batonC() {
        return new Equipement("Baton de bois", Equipement.Slot.ARME, Equipement.Rarete.C,
                Equipement.TypeArme.BATON, 20, 0, 0, 0);
    }

    public static Equipement gantsArmeC() {
        return new Equipement("Gants de combat", Equipement.Slot.ARME, Equipement.Rarete.C,
                Equipement.TypeArme.GANTS, 20, 0, 0, 0);
    }

    public static Equipement couvreCheC() {
        return new Equipement("Bandeau ninja", Equipement.Slot.COUVRE_CHEF, Equipement.Rarete.C,
                Equipement.TypeArme.AUCUN, 0, 0, 50, 0);
    }

    public static Equipement torseC() {
        return new Equipement("Veste de cuir", Equipement.Slot.TORSE, Equipement.Rarete.C,
                Equipement.TypeArme.AUCUN, 0, 15, 0, 0);
    }

    public static Equipement mainsC() {
        return new Equipement("Brassards de cuir", Equipement.Slot.MAINS, Equipement.Rarete.C,
                Equipement.TypeArme.AUCUN, 0, 10, 0, 0);
    }

    public static Equipement jambieresC() {
        return new Equipement("Pantalon renforce", Equipement.Slot.JAMBIERES, Equipement.Rarete.C,
                Equipement.TypeArme.AUCUN, 0, 0, 40, 0);
    }

    public static Equipement bottesC() {
        return new Equipement("Bottes legeres", Equipement.Slot.BOTTES, Equipement.Rarete.C,
                Equipement.TypeArme.AUCUN, 0, 0, 0, 10);
    }

    /**
     * Retourne l'arme rang C correspondant au type du personnage.
     * @param type "Ninja", "Mage" ou "Guerrier"
     */
    public static Equipement armeC(String type) {
        return switch (type) {
            case "Ninja"    -> kunaiC();
            case "Mage"     -> batonC();
            case "Guerrier" -> gantsArmeC();
            default         -> kunaiC();
        };
    }

    // ── Rang B ────────────────────────────────────────────────────────────

    public static Equipement kunaiB() {
        return new Equipement("Kunai en acier", Equipement.Slot.ARME, Equipement.Rarete.B,
                Equipement.TypeArme.KUNAI, 40, 0, 0, 0);
    }

    public static Equipement batonB() {
        return new Equipement("Baton de fer", Equipement.Slot.ARME, Equipement.Rarete.B,
                Equipement.TypeArme.BATON, 40, 0, 0, 0);
    }

    public static Equipement gantsArmeB() {
        return new Equipement("Gants renforces", Equipement.Slot.ARME, Equipement.Rarete.B,
                Equipement.TypeArme.GANTS, 40, 0, 0, 0);
    }

    public static Equipement couvreCheB() {
        return new Equipement("Casque ninja", Equipement.Slot.COUVRE_CHEF, Equipement.Rarete.B,
                Equipement.TypeArme.AUCUN, 0, 0, 100, 0);
    }

    public static Equipement torseB() {
        return new Equipement("Armure de mailles", Equipement.Slot.TORSE, Equipement.Rarete.B,
                Equipement.TypeArme.AUCUN, 0, 30, 0, 0);
    }

    public static Equipement mainsB() {
        return new Equipement("Brassards d'acier", Equipement.Slot.MAINS, Equipement.Rarete.B,
                Equipement.TypeArme.AUCUN, 0, 20, 0, 0);
    }

    public static Equipement jambieresB() {
        return new Equipement("Jambières en acier", Equipement.Slot.JAMBIERES, Equipement.Rarete.B,
                Equipement.TypeArme.AUCUN, 0, 0, 80, 0);
    }

    public static Equipement bottesB() {
        return new Equipement("Bottes de combat", Equipement.Slot.BOTTES, Equipement.Rarete.B,
                Equipement.TypeArme.AUCUN, 0, 0, 0, 20);
    }

    /**
     * Retourne l'arme rang B correspondant au type du personnage.
     * @param type "Ninja", "Mage" ou "Guerrier"
     */
    public static Equipement armeB(String type) {
        return switch (type) {
            case "Ninja"    -> kunaiB();
            case "Mage"     -> batonB();
            case "Guerrier" -> gantsArmeB();
            default         -> kunaiB();
        };
    }
}