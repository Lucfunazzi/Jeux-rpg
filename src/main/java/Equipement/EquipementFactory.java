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

    public static Equipement lanceC() {
        return new Equipement("Lance de fer", Equipement.Slot.ARME, Equipement.Rarete.C,
                Equipement.TypeArme.LANCE, 20, 0, 0, 0);
    }

    public static Equipement fouetC() {
        return new Equipement("Fouet de cuir", Equipement.Slot.ARME, Equipement.Rarete.C,
                Equipement.TypeArme.FOUET, 20, 0, 0, 0);
    }

    public static Equipement couvreCheC() {
        return new Equipement("Coiffe en cuir", Equipement.Slot.COUVRE_CHEF, Equipement.Rarete.C,
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
        return new Equipement("Pantalon en cuir", Equipement.Slot.JAMBIERES, Equipement.Rarete.C,
                Equipement.TypeArme.AUCUN, 0, 0, 40, 0);
    }

    public static Equipement bottesC() {
        return new Equipement("Bottes en cuir", Equipement.Slot.BOTTES, Equipement.Rarete.C,
                Equipement.TypeArme.AUCUN, 0, 0, 0, 10);
    }

    /**
     * Retourne l'arme rang C correspondant au type du personnage.
     * @param type "Chevalier", "Chasseur de Dragon"/"ChasseurDeDragon", "Mage"/"Elementaliste"
     *             ou "Constellationniste"/"Invocateur"
     */
    public static Equipement armeC(String type) {
        return switch (normaliserType(type)) {
            case "ChasseurDeDragon" -> gantsArmeC();
            case "Elementaliste"    -> batonC();
            case "Chevalier"        -> lanceC();
            case "Invocateur"       -> fouetC();
            default                 -> kunaiC();
        };
    }

    // ── Rang B ────────────────────────────────────────────────────────────

    public static Equipement kunaiB() {
        return new Equipement("Kunai en acier", Equipement.Slot.ARME, Equipement.Rarete.B,
                Equipement.TypeArme.KUNAI, 40, 0, 0, 0);
    }

    public static Equipement batonB() {
        return new Equipement("Baton Crépusculaire", Equipement.Slot.ARME, Equipement.Rarete.B,
                Equipement.TypeArme.BATON,100, 0, 0, 0);
    }

    public static Equipement gantsArmeB() {
        return new Equipement("Gants Crépusculaire", Equipement.Slot.ARME, Equipement.Rarete.B,
                Equipement.TypeArme.GANTS, 40, 0, 0, 0);
    }

    public static Equipement lanceB() {
        return new Equipement("Lance Crépusculaire", Equipement.Slot.ARME, Equipement.Rarete.B,
                Equipement.TypeArme.LANCE, 40, 0, 0, 0);
    }

    public static Equipement fouetB() {
        return new Equipement("Fouet Crépusculaire", Equipement.Slot.ARME, Equipement.Rarete.B,
                Equipement.TypeArme.FOUET, 40, 0, 0, 0);
    }

    public static Equipement couvreCheB() {
        return new Equipement("Casque Crépusculaire", Equipement.Slot.COUVRE_CHEF, Equipement.Rarete.B,
                Equipement.TypeArme.AUCUN, 0, 0, 100, 0);
    }

    public static Equipement torseB() {
        return new Equipement("Armure Crépusculaire", Equipement.Slot.TORSE, Equipement.Rarete.B,
                Equipement.TypeArme.AUCUN, 0, 60, 0, 0);
    }

    public static Equipement mainsB() {
        return new Equipement("Brassards Crépusculaire", Equipement.Slot.MAINS, Equipement.Rarete.B,
                Equipement.TypeArme.AUCUN, 0, 40, 0, 0);
    }

    public static Equipement jambieresB() {
        return new Equipement("Jambières Crépusculaire", Equipement.Slot.JAMBIERES, Equipement.Rarete.B,
                Equipement.TypeArme.AUCUN, 0, 0, 120, 0);
    }

    public static Equipement bottesB() {
        return new Equipement("Bottes Crépusculaire", Equipement.Slot.BOTTES, Equipement.Rarete.B,
                Equipement.TypeArme.AUCUN, 0, 0, 0, 80);
    }

    /**
     * Retourne l'arme rang B correspondant au type du personnage.
     * @param type "Chevalier", "Chasseur de Dragon"/"ChasseurDeDragon", "Mage"/"Elementaliste"
     *             ou "Constellationniste"/"Invocateur"
     */
    public static Equipement armeB(String type) {
        return switch (normaliserType(type)) {
            case "ChasseurDeDragon" -> gantsArmeB();
            case "Elementaliste"    -> batonB();
            case "Chevalier"        -> lanceB();
            case "Invocateur"       -> fouetB();
            default                 -> kunaiB();
        };
    }

    // ── Rang A (synthèse par fragments — Chapitre 3 Elite) ────────────────

    public static Equipement kunaiA() {
        return new Equipement("Kunai du Vent", Equipement.Slot.ARME, Equipement.Rarete.A,
                Equipement.TypeArme.KUNAI, 220, 0, 0, 0);
    }

    public static Equipement batonA() {
        return new Equipement("Baton de Foudre", Equipement.Slot.ARME, Equipement.Rarete.A,
                Equipement.TypeArme.BATON, 70, 0, 0, 0);
    }

    public static Equipement gantsArmeA() {
        return new Equipement("Gants de Titane", Equipement.Slot.ARME, Equipement.Rarete.A,
                Equipement.TypeArme.GANTS, 70, 0, 0, 0);
    }

    public static Equipement lanceA() {
        return new Equipement("Lance Celeste", Equipement.Slot.ARME, Equipement.Rarete.A,
                Equipement.TypeArme.LANCE, 70, 0, 0, 0);
    }

    public static Equipement fouetA() {
        return new Equipement("Fouet Stellaire", Equipement.Slot.ARME, Equipement.Rarete.A,
                Equipement.TypeArme.FOUET, 70, 0, 0, 0);
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
     * @param type "Chevalier", "Chasseur de Dragon"/"ChasseurDeDragon", "Mage"/"Elementaliste"
     *             ou "Constellationniste"/"Invocateur"
     */
    public static Equipement armeA(String type) {
        return switch (normaliserType(type)) {
            case "ChasseurDeDragon" -> gantsArmeA();
            case "Elementaliste"    -> batonA();
            case "Chevalier"        -> lanceA();
            case "Invocateur"       -> fouetA();
            default                 -> kunaiA();
        };
    }

    // ── Compatibilite arme / classe (utilisee par les menus Inventaire/Personnages) ──

    /**
     * Normalise les deux conventions de nommage coexistant dans le code :
     * le nom de classe affiche au joueur ("Mage", "Chasseur de Dragon", "Constellationniste")
     * et la cle interne utilisee par les personnages recrutes ("Elementaliste", "ChasseurDeDragon", "Invocateur").
     */
    private static String normaliserType(String type) {
        if (type == null) return "";
        return switch (type) {
            case "Chasseur de Dragon", "ChasseurDeDragon" -> "ChasseurDeDragon";
            case "Mage", "Elementaliste"                  -> "Elementaliste";
            case "Constellationniste", "Invocateur"       -> "Invocateur";
            case "Chevalier"                              -> "Chevalier";
            default -> type;
        };
    }

    /** Determine si une piece d'equipement peut etre portee par un personnage du type donne. */
    public static boolean estCompatibleArme(String type, Equipement e) {
        if (e.getSlot() != Equipement.Slot.ARME) return true;
        if (e.getTypeArme() == Equipement.TypeArme.AUCUN) return true;
        return switch (normaliserType(type)) {
            case "ChasseurDeDragon" -> e.getTypeArme() == Equipement.TypeArme.GANTS;
            case "Elementaliste"    -> e.getTypeArme() == Equipement.TypeArme.BATON;
            case "Chevalier"        -> e.getTypeArme() == Equipement.TypeArme.LANCE;
            case "Invocateur"       -> e.getTypeArme() == Equipement.TypeArme.FOUET;
            default                 -> false;
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