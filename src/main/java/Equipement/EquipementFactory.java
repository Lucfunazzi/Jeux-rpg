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
                case C   -> armeC(p.getType());
                case B   -> armeB(p.getType());
                case A   -> armeA(p.getType());
                case S   -> armeS(p.getType());
                case SS  -> armeSS(p.getType());
                case SSS -> armeSSS(p.getType());
                case UR  -> armeUR(p.getType());
            });
        } catch (IllegalArgumentException ignored) {
            // Type de personnage sans arme correspondante (ex : "Demon") -> pas d'arme.
        }
        p.equiper(switch (rarete) {
            case C   -> couvreCheC();
            case B   -> couvreCheB();
            case A   -> couvreCheA();
            case S   -> couvreCheS();
            case SS  -> couvreCheSS();
            case SSS -> couvreCheSSS();
            case UR  -> couvreCheUR();
        });
        p.equiper(switch (rarete) {
            case C   -> torseC();
            case B   -> torseB();
            case A   -> torseA();
            case S   -> torseS();
            case SS  -> torseSS();
            case SSS -> torseSSS();
            case UR  -> torseUR();
        });
        p.equiper(switch (rarete) {
            case C   -> mainsC();
            case B   -> mainsB();
            case A   -> mainsA();
            case S   -> mainsS();
            case SS  -> mainsSS();
            case SSS -> mainsSSS();
            case UR  -> mainsUR();
        });
        p.equiper(switch (rarete) {
            case C   -> jambieresC();
            case B   -> jambieresB();
            case A   -> jambieresA();
            case S   -> jambieresS();
            case SS  -> jambieresSS();
            case SSS -> jambieresSSS();
            case UR  -> jambieresUR();
        });
        p.equiper(switch (rarete) {
            case C   -> bottesC();
            case B   -> bottesB();
            case A   -> bottesA();
            case S   -> bottesS();
            case SS  -> bottesSS();
            case SSS -> bottesSSS();
            case UR  -> bottesUR();
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

    // ── Rang S (nettement plus fort que A — synthese par fragments, boutique d'equipement) ──

    public static Equipement batonS() {
        return new Equipement("Baton Astral", Equipement.Slot.ARME, Equipement.Rarete.S,
                Equipement.TypeArme.BATON, 2000, 0, 0, 0);
    }

    public static Equipement gantsArmeS() {
        return new Equipement("Gants Astraux", Equipement.Slot.ARME, Equipement.Rarete.S,
                Equipement.TypeArme.GANTS, 2000, 0, 0, 0);
    }

    public static Equipement lanceS() {
        return new Equipement("Lance Astrale", Equipement.Slot.ARME, Equipement.Rarete.S,
                Equipement.TypeArme.LANCE, 2000, 0, 0, 0);
    }

    public static Equipement fouetS() {
        return new Equipement("Fouet Astral", Equipement.Slot.ARME, Equipement.Rarete.S,
                Equipement.TypeArme.FOUET, 2000, 0, 0, 0);
    }

    public static Equipement couvreCheS() {
        return new Equipement("Heaume Astral", Equipement.Slot.COUVRE_CHEF, Equipement.Rarete.S,
                Equipement.TypeArme.AUCUN, 0, 0, 2500, 0);
    }

    public static Equipement torseS() {
        return new Equipement("Cuirasse Astrale", Equipement.Slot.TORSE, Equipement.Rarete.S,
                Equipement.TypeArme.AUCUN, 0, 700, 0, 0);
    }

    public static Equipement mainsS() {
        return new Equipement("Gantelets Astraux", Equipement.Slot.MAINS, Equipement.Rarete.S,
                Equipement.TypeArme.AUCUN, 0, 700, 0, 0);
    }

    public static Equipement jambieresS() {
        return new Equipement("Jambières Astrales", Equipement.Slot.JAMBIERES, Equipement.Rarete.S,
                Equipement.TypeArme.AUCUN, 0, 0, 2500, 0);
    }

    public static Equipement bottesS() {
        return new Equipement("Bottes Astrales", Equipement.Slot.BOTTES, Equipement.Rarete.S,
                Equipement.TypeArme.AUCUN, 0, 0, 0, 2000);
    }

    public static Equipement armeS(String type) {
        return switch (normaliserType(type)) {
            case "ChasseurDeDragon" -> gantsArmeS();
            case "Elementaliste"    -> batonS();
            case "Chevalier"        -> lanceS();
            case "Invocateur"       -> fouetS();
            default -> throw new IllegalArgumentException("Classe inconnue : " + type);
        };
    }

    // ── Rang SS (nettement plus fort que S) ──────────────────────────────

    public static Equipement batonSS() {
        return new Equipement("Baton Divin", Equipement.Slot.ARME, Equipement.Rarete.SS,
                Equipement.TypeArme.BATON, 6000, 0, 0, 0);
    }

    public static Equipement gantsArmeSS() {
        return new Equipement("Gants Divins", Equipement.Slot.ARME, Equipement.Rarete.SS,
                Equipement.TypeArme.GANTS, 6000, 0, 0, 0);
    }

    public static Equipement lanceSS() {
        return new Equipement("Lance Divine", Equipement.Slot.ARME, Equipement.Rarete.SS,
                Equipement.TypeArme.LANCE, 6000, 0, 0, 0);
    }

    public static Equipement fouetSS() {
        return new Equipement("Fouet Divin", Equipement.Slot.ARME, Equipement.Rarete.SS,
                Equipement.TypeArme.FOUET, 6000, 0, 0, 0);
    }

    public static Equipement couvreCheSS() {
        return new Equipement("Heaume Divin", Equipement.Slot.COUVRE_CHEF, Equipement.Rarete.SS,
                Equipement.TypeArme.AUCUN, 0, 0, 7500, 0);
    }

    public static Equipement torseSS() {
        return new Equipement("Cuirasse Divine", Equipement.Slot.TORSE, Equipement.Rarete.SS,
                Equipement.TypeArme.AUCUN, 0, 2000, 0, 0);
    }

    public static Equipement mainsSS() {
        return new Equipement("Gantelets Divins", Equipement.Slot.MAINS, Equipement.Rarete.SS,
                Equipement.TypeArme.AUCUN, 0, 2000, 0, 0);
    }

    public static Equipement jambieresSS() {
        return new Equipement("Jambières Divines", Equipement.Slot.JAMBIERES, Equipement.Rarete.SS,
                Equipement.TypeArme.AUCUN, 0, 0, 7500, 0);
    }

    public static Equipement bottesSS() {
        return new Equipement("Bottes Divines", Equipement.Slot.BOTTES, Equipement.Rarete.SS,
                Equipement.TypeArme.AUCUN, 0, 0, 0, 6000);
    }

    public static Equipement armeSS(String type) {
        return switch (normaliserType(type)) {
            case "ChasseurDeDragon" -> gantsArmeSS();
            case "Elementaliste"    -> batonSS();
            case "Chevalier"        -> lanceSS();
            case "Invocateur"       -> fouetSS();
            default -> throw new IllegalArgumentException("Classe inconnue : " + type);
        };
    }

    // ── Rang SSS (nettement plus fort que SS — debloque au rang joueur SSS) ──

    public static Equipement batonSSS() {
        return new Equipement("Baton Primordial", Equipement.Slot.ARME, Equipement.Rarete.SSS,
                Equipement.TypeArme.BATON, 20000, 0, 0, 0);
    }

    public static Equipement gantsArmeSSS() {
        return new Equipement("Gants Primordiaux", Equipement.Slot.ARME, Equipement.Rarete.SSS,
                Equipement.TypeArme.GANTS, 20000, 0, 0, 0);
    }

    public static Equipement lanceSSS() {
        return new Equipement("Lance Primordiale", Equipement.Slot.ARME, Equipement.Rarete.SSS,
                Equipement.TypeArme.LANCE, 20000, 0, 0, 0);
    }

    public static Equipement fouetSSS() {
        return new Equipement("Fouet Primordial", Equipement.Slot.ARME, Equipement.Rarete.SSS,
                Equipement.TypeArme.FOUET, 20000, 0, 0, 0);
    }

    public static Equipement couvreCheSSS() {
        return new Equipement("Heaume Primordial", Equipement.Slot.COUVRE_CHEF, Equipement.Rarete.SSS,
                Equipement.TypeArme.AUCUN, 0, 0, 25000, 0);
    }

    public static Equipement torseSSS() {
        return new Equipement("Cuirasse Primordiale", Equipement.Slot.TORSE, Equipement.Rarete.SSS,
                Equipement.TypeArme.AUCUN, 0, 7000, 0, 0);
    }

    public static Equipement mainsSSS() {
        return new Equipement("Gantelets Primordiaux", Equipement.Slot.MAINS, Equipement.Rarete.SSS,
                Equipement.TypeArme.AUCUN, 0, 7000, 0, 0);
    }

    public static Equipement jambieresSSS() {
        return new Equipement("Jambières Primordiales", Equipement.Slot.JAMBIERES, Equipement.Rarete.SSS,
                Equipement.TypeArme.AUCUN, 0, 0, 25000, 0);
    }

    public static Equipement bottesSSS() {
        return new Equipement("Bottes Primordiales", Equipement.Slot.BOTTES, Equipement.Rarete.SSS,
                Equipement.TypeArme.AUCUN, 0, 0, 0, 20000);
    }

    public static Equipement armeSSS(String type) {
        return switch (normaliserType(type)) {
            case "ChasseurDeDragon" -> gantsArmeSSS();
            case "Elementaliste"    -> batonSSS();
            case "Chevalier"        -> lanceSSS();
            case "Invocateur"       -> fouetSSS();
            default -> throw new IllegalArgumentException("Classe inconnue : " + type);
        };
    }

    // ── Rang UR (nettement plus fort que SSS — le sommet de l'equipement) ──

    public static Equipement batonUR() {
        return new Equipement("Baton Transcendant", Equipement.Slot.ARME, Equipement.Rarete.UR,
                Equipement.TypeArme.BATON, 80000, 0, 0, 0);
    }

    public static Equipement gantsArmeUR() {
        return new Equipement("Gants Transcendants", Equipement.Slot.ARME, Equipement.Rarete.UR,
                Equipement.TypeArme.GANTS, 80000, 0, 0, 0);
    }

    public static Equipement lanceUR() {
        return new Equipement("Lance Transcendante", Equipement.Slot.ARME, Equipement.Rarete.UR,
                Equipement.TypeArme.LANCE, 80000, 0, 0, 0);
    }

    public static Equipement fouetUR() {
        return new Equipement("Fouet Transcendant", Equipement.Slot.ARME, Equipement.Rarete.UR,
                Equipement.TypeArme.FOUET, 80000, 0, 0, 0);
    }

    public static Equipement couvreCheUR() {
        return new Equipement("Heaume Transcendant", Equipement.Slot.COUVRE_CHEF, Equipement.Rarete.UR,
                Equipement.TypeArme.AUCUN, 0, 0, 100000, 0);
    }

    public static Equipement torseUR() {
        return new Equipement("Cuirasse Transcendante", Equipement.Slot.TORSE, Equipement.Rarete.UR,
                Equipement.TypeArme.AUCUN, 0, 28000, 0, 0);
    }

    public static Equipement mainsUR() {
        return new Equipement("Gantelets Transcendants", Equipement.Slot.MAINS, Equipement.Rarete.UR,
                Equipement.TypeArme.AUCUN, 0, 28000, 0, 0);
    }

    public static Equipement jambieresUR() {
        return new Equipement("Jambières Transcendantes", Equipement.Slot.JAMBIERES, Equipement.Rarete.UR,
                Equipement.TypeArme.AUCUN, 0, 0, 100000, 0);
    }

    public static Equipement bottesUR() {
        return new Equipement("Bottes Transcendantes", Equipement.Slot.BOTTES, Equipement.Rarete.UR,
                Equipement.TypeArme.AUCUN, 0, 0, 0, 80000);
    }

    public static Equipement armeUR(String type) {
        return switch (normaliserType(type)) {
            case "ChasseurDeDragon" -> gantsArmeUR();
            case "Elementaliste"    -> batonUR();
            case "Chevalier"        -> lanceUR();
            case "Invocateur"       -> fouetUR();
            default -> throw new IllegalArgumentException("Classe inconnue : " + type);
        };
    }

    /**
     * Rang joueur (RangJoueur.Rang, par ordinal) minimum requis pour porter un equipement
     * de la rarete donnee. C/B/A n'ont pas de restriction (rang C suffit).
     */
    public static int rangJoueurRequisPourEquiper(Equipement.Rarete rarete) {
        return switch (rarete) {
            case C, B, A -> 0; // Rang C (aucune restriction reelle)
            case S        -> 3; // Rang S
            case SS       -> 4; // Rang SS
            case SSS      -> 5; // Rang SSS
            case UR       -> 6; // Rang UR
        };
    }

    // ── Recyclage / Boutique d'équipement ────────────────────────────────

    /** Nom du materiau utilise comme monnaie de recyclage d'equipement. */
    public static final String MATERIAU_PIECE_EQUIPEMENT = "Piece d'equipement";

    /** Pieces d'equipement obtenues en recyclant (detruisant) une piece de cette rarete. */
    public static int valeurRecyclage(Equipement.Rarete rarete) {
        return switch (rarete) {
            case C   -> 5;
            case B   -> 15;
            case A   -> 40;
            case S   -> 100;
            case SS  -> 250;
            case SSS -> 600;
            case UR  -> 1500;
        };
    }

    /** Prix (en pieces d'equipement) d'un fragment de cette rarete a la Boutique d'equipement. */
    public static int prixFragmentBoutiqueEquipement(Equipement.Rarete rarete) {
        return switch (rarete) {
            case C, B -> 10; // pas de fragments C/B au catalogue actuellement
            case A    -> 30;
            case S    -> 80;
            case SS   -> 200;
            case SSS  -> 500;
            case UR   -> 1200;
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

    /** @deprecated Utiliser {@link #creerEquipement(String, Equipement.Slot, Equipement.TypeArme, Equipement.Rarete)}. */
    @Deprecated
    public static Equipement creerEquipementA(String nom, Equipement.Slot slot,
                                              Equipement.TypeArme typeArme) {
        return creerEquipement(nom, slot, typeArme, Equipement.Rarete.A);
    }

    /**
     * Crée dynamiquement un équipement de la rareté donnée à partir des paramètres de synthèse.
     * Utilisé par {@link Equipement.GestionnaireFragments} lors de la synthèse (fragments
     * A/S/SS/SSS/UR, obtenus en jeu ou achetés à la Boutique d'équipement).
     */
    public static Equipement creerEquipement(String nom, Equipement.Slot slot,
                                             Equipement.TypeArme typeArme, Equipement.Rarete rarete) {
        return switch (nom) {
            // Rang A
            case "Baton de Foudre"    -> batonA();
            case "Gants de Titane"    -> gantsArmeA();
            case "Lance Celeste"      -> lanceA();
            case "Fouet Stellaire"    -> fouetA();
            case "Heaume Sacré"       -> couvreCheA();
            case "Cuirasse de l'Aube" -> torseA();
            case "Gantelets Forgés"   -> mainsA();
            case "Jambières de Fer"   -> jambieresA();
            case "Bottes de Tempête"  -> bottesA();
            // Rang S
            case "Baton Astral"       -> batonS();
            case "Gants Astraux"      -> gantsArmeS();
            case "Lance Astrale"      -> lanceS();
            case "Fouet Astral"       -> fouetS();
            case "Heaume Astral"      -> couvreCheS();
            case "Cuirasse Astrale"   -> torseS();
            case "Gantelets Astraux"  -> mainsS();
            case "Jambières Astrales" -> jambieresS();
            case "Bottes Astrales"    -> bottesS();
            // Rang SS
            case "Baton Divin"        -> batonSS();
            case "Gants Divins"       -> gantsArmeSS();
            case "Lance Divine"       -> lanceSS();
            case "Fouet Divin"        -> fouetSS();
            case "Heaume Divin"       -> couvreCheSS();
            case "Cuirasse Divine"    -> torseSS();
            case "Gantelets Divins"   -> mainsSS();
            case "Jambières Divines"  -> jambieresSS();
            case "Bottes Divines"     -> bottesSS();
            // Rang SSS
            case "Baton Primordial"       -> batonSSS();
            case "Gants Primordiaux"      -> gantsArmeSSS();
            case "Lance Primordiale"      -> lanceSSS();
            case "Fouet Primordial"       -> fouetSSS();
            case "Heaume Primordial"      -> couvreCheSSS();
            case "Cuirasse Primordiale"   -> torseSSS();
            case "Gantelets Primordiaux"  -> mainsSSS();
            case "Jambières Primordiales" -> jambieresSSS();
            case "Bottes Primordiales"    -> bottesSSS();
            // Rang UR
            case "Baton Transcendant"       -> batonUR();
            case "Gants Transcendants"      -> gantsArmeUR();
            case "Lance Transcendante"      -> lanceUR();
            case "Fouet Transcendant"       -> fouetUR();
            case "Heaume Transcendant"      -> couvreCheUR();
            case "Cuirasse Transcendante"   -> torseUR();
            case "Gantelets Transcendants"  -> mainsUR();
            case "Jambières Transcendantes" -> jambieresUR();
            case "Bottes Transcendantes"    -> bottesUR();

            default -> new Equipement(nom, slot, rarete, typeArme, 0, 0, 0, 0);
        };
    }
}