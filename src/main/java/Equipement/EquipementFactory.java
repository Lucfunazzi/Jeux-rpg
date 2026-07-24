package Equipement;

import Personnage.PersonnageBase;

/**
 * Fabrique les instances d'équipements.
 * Ajouter ici les nouvelles pièces au fur et à mesure des rangs.
 */
public class EquipementFactory {

    /**
     * Équipe un personnage (typiquement un ennemi) avec un set complet (arme + 5 pièces
     * d'armure) de la rareté donnée, pour compenser le fait qu'il n'a ni équipement,
     * ni pierres, ni compagnons/créatures sacrées, ni rang, contrairement au joueur.
     * L'arme est ignorée si le type du personnage n'a pas d'arme correspondante (ex: "Demon").
     */
    public static void equiperSetStandard(PersonnageBase p, Equipement.Rarete rarete) {
        try {
            p.equiper(switch (rarete) {
                case C    -> armeC(p.getType());
                case B    -> armeB(p.getType());
                case A, S -> armeA(p.getType());
            });
        } catch (IllegalArgumentException ignored) {
            // Type de personnage sans arme correspondante (ex : "Demon") -> pas d'arme.
        }
        p.equiper(switch (rarete) {
            case C    -> couvreCheC();
            case B    -> couvreCheB();
            case A, S -> couvreCheA();
        });
        p.equiper(switch (rarete) {
            case C    -> torseC();
            case B    -> torseB();
            case A, S -> torseA();
        });
        p.equiper(switch (rarete) {
            case C    -> mainsC();
            case B    -> mainsB();
            case A, S -> mainsA();
        });
        p.equiper(switch (rarete) {
            case C    -> jambieresC();
            case B    -> jambieresB();
            case A, S -> jambieresA();
        });
        p.equiper(switch (rarete) {
            case C    -> bottesC();
            case B    -> bottesB();
            case A, S -> bottesA();
        });
    }

    /** Rareté d'équipement fantôme appropriée pour un ennemi de ce niveau (calée sur ce que le joueur peut réellement obtenir à ce stade). */
    public static Equipement.Rarete rareteEnnemiPourNiveau(int niveau) {
        if (niveau < 11) return Equipement.Rarete.C;   // Chapitre 1
        if (niveau < 35) return Equipement.Rarete.B;   // Chapitre 1 Elite / 2 / 2 Elite / 3
        return Equipement.Rarete.A;                     // Chapitre 3 Elite (rang A par synthèse de fragments)
    }

    /**
     * Rareté d'équipement fantôme pour un faux joueur d'arène, selon sa position au classement
     * (1 = meilleur, 100 = pire) — calée sur les paliers de {@code GestionnaireArene.genererFauxJoueurs()}.
     */
    public static Equipement.Rarete rareteEnnemiPourRangArene(int rangArene) {
        if (rangArene >= 80) return Equipement.Rarete.C;
        if (rangArene >= 20) return Equipement.Rarete.B;
        return Equipement.Rarete.A;
    }

    // ── Rang C ────────────────────────────────────────────────────────────

    public static Equipement batonC() {
        return new Equipement("Baton de bois", Equipement.Slot.ARME, Equipement.Rarete.C,
                Equipement.TypeArme.BATON, 100, 0, 0, 0);
    }

    public static Equipement gantsArmeC() {
        return new Equipement("Gants de combat", Equipement.Slot.ARME, Equipement.Rarete.C,
                Equipement.TypeArme.GANTS, 100, 0, 0, 0);
    }

    public static Equipement lanceC() {
        return new Equipement("Lance de fer", Equipement.Slot.ARME, Equipement.Rarete.C,
                Equipement.TypeArme.LANCE, 100, 0, 0, 0);
    }

    public static Equipement fouetC() {
        return new Equipement("Fouet de cuir", Equipement.Slot.ARME, Equipement.Rarete.C,
                Equipement.TypeArme.FOUET, 100, 0, 0, 0);
    }

    public static Equipement couvreCheC() {
        return new Equipement("Coiffe en cuir", Equipement.Slot.COUVRE_CHEF, Equipement.Rarete.C,
                Equipement.TypeArme.AUCUN, 0, 0, 130, 0);
    }

    public static Equipement torseC() {
        return new Equipement("Veste de cuir", Equipement.Slot.TORSE, Equipement.Rarete.C,
                Equipement.TypeArme.AUCUN, 0, 35, 0, 0);
    }

    public static Equipement mainsC() {
        return new Equipement("Brassards de cuir", Equipement.Slot.MAINS, Equipement.Rarete.C,
                Equipement.TypeArme.AUCUN, 0, 35, 0, 0);
    }

    public static Equipement jambieresC() {
        return new Equipement("Pantalon en cuir", Equipement.Slot.JAMBIERES, Equipement.Rarete.C,
                Equipement.TypeArme.AUCUN, 0, 0, 130, 0);
    }

    public static Equipement bottesC() {
        return new Equipement("Bottes en cuir", Equipement.Slot.BOTTES, Equipement.Rarete.C,
                Equipement.TypeArme.AUCUN, 0, 0, 0, 105);
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
            default -> throw new IllegalArgumentException("Classe inconnue : " + type);
        };
    }

    // ── Rang B ────────────────────────────────────────────────────────────

    public static Equipement batonB() {
        return new Equipement("Baton Crépusculaire", Equipement.Slot.ARME, Equipement.Rarete.B,
                Equipement.TypeArme.BATON,460, 0, 0, 0);
    }

    public static Equipement gantsArmeB() {
        return new Equipement("Gants Crépusculaire", Equipement.Slot.ARME, Equipement.Rarete.B,
                Equipement.TypeArme.GANTS, 460, 0, 0, 0);
    }

    public static Equipement lanceB() {
        return new Equipement("Lance Crépusculaire", Equipement.Slot.ARME, Equipement.Rarete.B,
                Equipement.TypeArme.LANCE, 460, 0, 0, 0);
    }

    public static Equipement fouetB() {
        return new Equipement("Fouet Crépusculaire", Equipement.Slot.ARME, Equipement.Rarete.B,
                Equipement.TypeArme.FOUET, 460, 0, 0, 0);
    }

    public static Equipement couvreCheB() {
        return new Equipement("Casque Crépusculaire", Equipement.Slot.COUVRE_CHEF, Equipement.Rarete.B,
                Equipement.TypeArme.AUCUN, 0, 0, 600, 0);
    }

    public static Equipement torseB() {
        return new Equipement("Armure Crépusculaire", Equipement.Slot.TORSE, Equipement.Rarete.B,
                Equipement.TypeArme.AUCUN, 0, 160, 0, 0);
    }

    public static Equipement mainsB() {
        return new Equipement("Brassards Crépusculaire", Equipement.Slot.MAINS, Equipement.Rarete.B,
                Equipement.TypeArme.AUCUN, 0,160, 0, 0);
    }

    public static Equipement jambieresB() {
        return new Equipement("Jambières Crépusculaire", Equipement.Slot.JAMBIERES, Equipement.Rarete.B,
                Equipement.TypeArme.AUCUN, 0, 0, 600, 0);
    }

    public static Equipement bottesB() {
        return new Equipement("Bottes Crépusculaire", Equipement.Slot.BOTTES, Equipement.Rarete.B,
                Equipement.TypeArme.AUCUN, 0, 0, 0,480);
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
            default -> throw new IllegalArgumentException("Classe inconnue : " + type);
        };
    }

    // ── Rang A (synthèse par fragments — Chapitre 3 Elite) ────────────────

    public static Equipement batonA() {
        return new Equipement("Baton de Foudre", Equipement.Slot.ARME, Equipement.Rarete.A,
                Equipement.TypeArme.BATON, 800, 0, 0, 0);
    }

    public static Equipement gantsArmeA() {
        return new Equipement("Gants de Titane", Equipement.Slot.ARME, Equipement.Rarete.A,
                Equipement.TypeArme.GANTS, 800, 0, 0, 0);
    }

    public static Equipement lanceA() {
        return new Equipement("Lance Celeste", Equipement.Slot.ARME, Equipement.Rarete.A,
                Equipement.TypeArme.LANCE, 800, 0, 0, 0);
    }

    public static Equipement fouetA() {
        return new Equipement("Fouet Stellaire", Equipement.Slot.ARME, Equipement.Rarete.A,
                Equipement.TypeArme.FOUET,800, 0, 0, 0);
    }

    public static Equipement couvreCheA() {
        return new Equipement("Heaume Sacré", Equipement.Slot.COUVRE_CHEF, Equipement.Rarete.A,
                Equipement.TypeArme.AUCUN, 0, 0, 1000, 0);
    }

    public static Equipement torseA() {
        return new Equipement("Cuirasse de l'Aube", Equipement.Slot.TORSE, Equipement.Rarete.A,
                Equipement.TypeArme.AUCUN, 0, 270, 0, 0);
    }

    public static Equipement mainsA() {
        return new Equipement("Gantelets Forgés", Equipement.Slot.MAINS, Equipement.Rarete.A,
                Equipement.TypeArme.AUCUN, 0, 270, 0, 0);
    }

    public static Equipement jambieresA() {
        return new Equipement("Jambières de Fer", Equipement.Slot.JAMBIERES, Equipement.Rarete.A,
                Equipement.TypeArme.AUCUN, 0, 0, 1000, 0);
    }

    public static Equipement bottesA() {
        return new Equipement("Bottes de Tempête", Equipement.Slot.BOTTES, Equipement.Rarete.A,
                Equipement.TypeArme.AUCUN, 0, 0, 0, 800);
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
            default -> throw new IllegalArgumentException("Classe inconnue : " + type);
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