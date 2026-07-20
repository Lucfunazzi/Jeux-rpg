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
        return new Equipement("Coiffe de Guilde", Equipement.Slot.COUVRE_CHEF, Equipement.Rarete.C,
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
     * @param type "Chevalier", "Chasseur de Dragon", "Mage" ou "Constellationniste"
     */
    public static Equipement armeC(String type) {
        return switch (type) {
            case "Chasseur de Dragon"  -> kunaiC();
            case "Mage"     -> batonC();
            case "Chevalier"           -> gantsArmeC();
            case "Constellationniste"  -> batonC();
            default         -> kunaiC();
        };
    }

    // ── Rang B ────────────────────────────────────────────────────────────

    public static Equipement kunaiB() {
        return new Equipement("Kunai en acier", Equipement.Slot.ARME, Equipement.Rarete.B,
                Equipement.TypeArme.KUNAI, 40, 0, 0, 0);
    }

    public static Equipement batonB() {
        return new Equipement("Baton de Cristal", Equipement.Slot.ARME, Equipement.Rarete.B,
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
     * @param type "Chevalier", "Chasseur de Dragon", "Mage" ou "Constellationniste"
     */
    public static Equipement armeB(String type) {
        return switch (type) {
            case "Chasseur de Dragon"  -> kunaiB();
            case "Mage"     -> batonB();
            case "Chevalier"           -> gantsArmeB();
            case "Constellationniste"  -> batonB();
            default         -> kunaiB();
        };
    }

    // ── Rang A (synthèse par fragments — Chapitre 3 Elite) ────────────────

    public static Equipement kunaiA() {
        return new Equipement("Kunai du Vent", Equipement.Slot.ARME, Equipement.Rarete.A,
                Equipement.TypeArme.KUNAI, 70, 0, 0, 0);
    }

    public static Equipement batonA() {
        return new Equipement("Baton de Foudre", Equipement.Slot.ARME, Equipement.Rarete.A,
                Equipement.TypeArme.BATON, 70, 0, 0, 0);
    }

    public static Equipement gantsArmeA() {
        return new Equipement("Gants de Titane", Equipement.Slot.ARME, Equipement.Rarete.A,
                Equipement.TypeArme.GANTS, 70, 0, 0, 0);
    }

    public static Equipement couvreCheA() {
        return new Equipement("Heaume Sacré", Equipement.Slot.COUVRE_CHEF, Equipement.Rarete.A,
                Equipement.TypeArme.AUCUN, 0, 0, 200, 0);
    }

    public static Equipement torseA() {
        return new Equipement("Cuirasse de l'Aube", Equipement.Slot.TORSE, Equipement.Rarete.A,
                Equipement.TypeArme.AUCUN, 0, 60, 0, 0);
    }

    public static Equipement mainsA() {
        return new Equipement("Gantelets Forgés", Equipement.Slot.MAINS, Equipement.Rarete.A,
                Equipement.TypeArme.AUCUN, 0, 40, 0, 0);
    }

    public static Equipement jambieresA() {
        return new Equipement("Jambières de Fer", Equipement.Slot.JAMBIERES, Equipement.Rarete.A,
                Equipement.TypeArme.AUCUN, 0, 0, 160, 0);
    }

    public static Equipement bottesA() {
        return new Equipement("Bottes de Tempête", Equipement.Slot.BOTTES, Equipement.Rarete.A,
                Equipement.TypeArme.AUCUN, 0, 0, 0, 35);
    }

    /**
     * Retourne l'arme rang A correspondant au type du personnage.
     * @param type "Chevalier", "Chasseur de Dragon", "Mage" ou "Constellationniste"
     */
    public static Equipement armeA(String type) {
        return switch (type) {
            case "Chasseur de Dragon"  -> kunaiA();
            case "Mage"     -> batonA();
            case "Chevalier"           -> gantsArmeA();
            case "Constellationniste"  -> batonA();
            default         -> kunaiA();
        };
    }

    /**
     * Crée dynamiquement un équipement rang A à partir des paramètres de synthèse.
     * Utilisé par {@link Equipement.GestionnaireFragments} lors de la synthèse.
     */
    public static Equipement creerEquipementA(String nom, Equipement.Slot slot,
                                              Equipement.TypeArme typeArme) {
        return switch (nom) {
            case "Kunai du Vent"      -> kunaiA();
            case "Baton de Foudre"    -> batonA();
            case "Gants de Titane"    -> gantsArmeA();
            case "Heaume Sacré"       -> couvreCheA();
            case "Cuirasse de l'Aube" -> torseA();
            case "Gantelets Forgés"   -> mainsA();
            case "Jambières de Fer"   -> jambieresA();
            case "Bottes de Tempête"  -> bottesA();
            default -> new Equipement(nom, slot, Equipement.Rarete.A, typeArme, 0, 0, 0, 0);
        };
    }
}