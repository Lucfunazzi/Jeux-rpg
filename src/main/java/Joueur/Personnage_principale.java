package Joueur;

import Personnage.*;
import java.util.List;
import Combat.Combat;
import lancement.GameContext;
import Equipement.Inventaire;
import Equipement.ParcheminAptitude;

public class Personnage_principale extends PersonnageBase {

    private String[]     classes = {"Chevalier", "Chasseur de Dragon", "Mage", "Constellationniste"};
    private String       choixClasse;
    private Competences  competenceChoisie;
    private double       or      = 1000.00;
    private int          coupons = 50;
    private String       genre   = "Homme";

    // Arbre de compétences
    private ArbreCompetences arbreCompetences = new ArbreCompetences();

    /**
     * Quel arbre fournit la spéciale active (0 = spéciale de base) : 1, 2, 3, 5, 6 ou 7 selon
     * le nœud 10 debloque le plus recemment active (voir activerArbreN). Les arbres 4 et 8 ne
     * touchent pas la spéciale — ce sont des arbres d'ULTIME (voir competenceUltimeActive).
     */
    private int competenceSpecialeActive = 0;

    /**
     * Quel arbre fournit l'ultime active (0 = ultime de base) : 4 ou 8 selon le nœud 10
     * debloque le plus recemment active (voir activerUltimeArbreN).
     */
    private int competenceUltimeActive = 0;

    private GameContext ctx;

    public Personnage_principale(String nom, int niveau) {
        this.nom    = nom;
        this.niveau = niveau;
        this.rarete = "C";
        this.role   = "DPS";
        this.vie      = 300;
        this.attaque  = 110;
        this.defense  = 60;
        this.vitesse  = 100;
        this.taux_critiques    = 0.10;
        this.degat_critiques   = 1.10;
        this.taux_precisions   = 100.00;
        this.taux_esquives     = 0.08;
        this.taux_blocage      = 0.05;
        this.reduction_blocage = 0.10;
        this.degats_renvoi     = 0.80;
        initialiserVieMax();
    }

    public void setGameContext(GameContext ctx) { this.ctx = ctx; }

    /** Profil de stats de base d'une classe (PV, ATK, DEF, VIT), consultable avant meme la creation du personnage. */
    public record StatsClasse(double vie, double attaque, double defense, double vitesse) {}

    /**
     * Chaque classe reste un DPS mais avec une repartition differente :
     * Chevalier = DEF/PV, Chasseur de Dragon = ATK, Invocateur = VIT, Mage = equilibre.
     */
    public static StatsClasse statsPourClasse(String classeInterne) {
        return switch (classeInterne) {
            case "Chasseur de Dragon" -> new StatsClasse(280, 130, 55, 95);
            case "Chevalier"          -> new StatsClasse(345, 100, 70, 90);
            case "Constellationniste", "Invocateur" -> new StatsClasse(280, 100, 55, 120);
            default                  -> new StatsClasse(300, 110, 60, 100); // Mage
        };
    }

    /** Applique le profil de stats de base propre a la classe choisie (a appeler juste apres setChoixClasses). */
    public void appliquerStatsClasse(String classeInterne) {
        StatsClasse s = statsPourClasse(classeInterne);
        this.vie = s.vie();
        this.attaque = s.attaque();
        this.defense = s.defense();
        this.vitesse = s.vitesse();
        initialiserVieMax();
    }

    private double getMultiplicateurRang() {
        if (ctx != null && ctx.rangJoueur != null)
            return ctx.rangJoueur.getMultiplicateur();
        return 1.00;
    }

    /**
     * Le coefficient de rang n'est plus applique comme multiplicateur "live" sur le total
     * courant (ce qui reappliquait retroactivement tout l'historique de croissance a chaque
     * montee de rang et provoquait un boost instantane disproportionne). A la place, il
     * modifie le taux de croissance des montees de niveau FUTURES : monter de rang ne change
     * rien dans l'instant, mais chaque niveau gagne ensuite rapporte proportionnellement plus.
     */
    @Override protected double multiplicateurCroissanceNiveau() {
        return getMultiplicateurRang();
    }

    /** La rarete affichee (badge, couleur) suit toujours le rang actuel du joueur. */
    @Override public String getRarete() {
        if (ctx != null && ctx.rangJoueur != null) return ctx.rangJoueur.getRangNom();
        return super.getRarete();
    }

    @Override public double getAttaque() {
        return super.getAttaque() * (1.0 + arbreCompetences.getBonusATK());
    }
    @Override public double getDefense() {
        return super.getDefense() * (1.0 + arbreCompetences.getBonusDEF());
    }
    @Override public double getVieMax() {
        return super.getVieMax() * (1.0 + arbreCompetences.getBonusPV());
    }
    @Override public double getVitesse() {
        return super.getVitesse() * (1.0 + arbreCompetences.getBonusVIT());
    }

    @Override public boolean estPersonnagePrincipal() { return true; }

    @Override public String[] getNomsAttaques() {
        if (competenceChoisie == null)
            return new String[]{"Attaque de base", "Attaque spéciale", "Attaque ultime"};
        String[] noms = competenceChoisie.getNomsCompetences();
        // noms[0] = spéciale de base, noms[1] = ultime de base (jamais remplacée par defaut)
        String nomSpeciale = competenceSpecialeActive == 0
                ? noms[0]
                : getNomCompetenceArbre(choixClasse, competenceSpecialeActive);
        String nomUltime = competenceUltimeActive == 0
                ? noms[1]
                : getNomUltimeArbre(choixClasse, competenceUltimeActive);
        return new String[]{"Attaque de base", nomSpeciale, nomUltime};
    }

    /**
     * Noms des spéciales débloquées par les arbres 1/2/3/5/6/7 — dupliqué de MenuAbilite pour
     * éviter la dépendance circulaire.
     */
    public static String getNomCompetenceArbre(String classe, int arbre) {
        if (classe == null) return "Compétence spéciale (Arbre " + arbre + ")";
        return switch (classe) {
            case "Mage" -> switch (arbre) {
                case 1  -> "Rayon sacré";
                case 2  -> "Décharge de foudre";
                case 3  -> "Epines fleuries";
                case 5  -> "Les 5 Flèches Sacrées";
                case 6  -> "Dogme de la Flamme Éternelle";
                case 7  -> "Floraisons Électriques";
                default -> "Compétence spéciale (Arbre " + arbre + ")";
            };
            case "Chasseur de Dragon" -> switch (arbre) {
                case 1  -> "Fouet du Dragon d'Eau";
                case 2  -> "Tir à haute pression du dragon d'eau";
                case 3  -> "Triples Tir du Dragon de l'eau";
                case 5  -> "Canon du Dragon de l'Eau";
                case 6  -> "Tourbillon du Dragon d'Eau";
                case 7  -> "Pluie de la Nuit Nocturne du Dragon d'Eau et de la Neige";
                default -> "Compétence spéciale (Arbre " + arbre + ")";
            };
            case "Chevalier" -> switch (arbre) {
                case 1  -> "Lance de Feu";
                case 2  -> "Marteau vengeur";
                case 3  -> "Lance du Tyran céleste";
                case 5  -> "Lance Étoilée";
                case 6  -> "Lance Émeraude de la Revendication";
                case 7  -> "Lance de la Grâce Purificatrice";
                default -> "Compétence spéciale (Arbre " + arbre + ")";
            };
            case "Constellationniste" -> switch (arbre) {
                case 1  -> "Invocation : Cancer";
                case 2  -> "Invocation : Virgo";
                case 3  -> "Invocation Aries";
                case 5  -> "Invocation Libra : Gravity Changes";
                case 6  -> "Invocation Taurus + Scorpion : Haches Sablées";
                case 7  -> "Invocation Ophiuchus le Serpentaire";
                default -> "Compétence spéciale (Arbre " + arbre + ")";
            };
            default -> "Compétence spéciale (Arbre " + arbre + ")";
        };
    }

    /** Noms des ultimes débloqués par les arbres 4/8 — dupliqué de MenuAbilite pour éviter la dépendance circulaire. */
    public static String getNomUltimeArbre(String classe, int arbre) {
        if (classe == null) return "Attaque ultime (Arbre " + arbre + ")";
        return switch (classe) {
            case "Mage" -> switch (arbre) {
                case 4  -> "Cataclysme d'Elipse";
                case 8  -> "Nature Volcanique du Purgatoire";
                default -> "Attaque ultime (Arbre " + arbre + ")";
            };
            case "Chasseur de Dragon" -> switch (arbre) {
                case 4  -> "Tsunamie du Dragon d'Eau";
                case 8  -> "Tsunamie et Blizzard du Dragon de l'Eau et de la Neige";
                default -> "Attaque ultime (Arbre " + arbre + ")";
            };
            case "Chevalier" -> switch (arbre) {
                case 4  -> "Danse des Lances Anémisthe";
                case 8  -> "Lance Explosive Chasseuse de Démons";
                default -> "Attaque ultime (Arbre " + arbre + ")";
            };
            case "Constellationniste" -> switch (arbre) {
                case 4  -> "Invocation Leo : Regulus Punch";
                case 8  -> "Urano Metria";
                default -> "Attaque ultime (Arbre " + arbre + ")";
            };
            default -> "Attaque ultime (Arbre " + arbre + ")";
        };
    }

    // ── Attaque de base ───────────────────────────────────────────────────
    @Override
    public void attaqueBase(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                            List<PersonnageBase> equipeEnnemie, List<String> log) {
        Combat.attaquer(this, cible, log);
    }

    // ── Attaque spéciale ──────────────────────────────────────────────────
    @Override
    public void attaqueSpeciale(PersonnageBase cible, List<PersonnageBase> equipeAlliee,
                                List<PersonnageBase> equipeEnnemie, List<String> log) {
        if (competenceChoisie == null) { log.add("Aucune classe active !"); return; }

        switch (competenceSpecialeActive) {
            case 1  -> competenceChoisie.competenceArbre(this, cible, equipeAlliee, equipeEnnemie, log);
            case 2  -> competenceChoisie.competenceArbre2(this, cible, equipeAlliee, equipeEnnemie, log);
            case 3  -> competenceChoisie.competenceArbre3(this, cible, equipeAlliee, equipeEnnemie, log);
            case 5  -> competenceChoisie.competenceArbre5(this, cible, equipeAlliee, equipeEnnemie, log);
            case 6  -> competenceChoisie.competenceArbre6(this, cible, equipeAlliee, equipeEnnemie, log);
            case 7  -> competenceChoisie.competenceArbre7(this, cible, equipeAlliee, equipeEnnemie, log);
            default -> competenceChoisie.attaqueSpeciale(this, cible, equipeAlliee, equipeEnnemie, log);
        }
    }

    // ── Attaque ultime ────────────────────────────────────────────────────
    @Override
    public void attaqueUltime(List<PersonnageBase> equipeAlliee,
                              List<PersonnageBase> equipeEnnemie, List<String> log) {
        if (competenceChoisie == null) { log.add("Aucune classe active !"); return; }

        switch (competenceUltimeActive) {
            case 4  -> competenceChoisie.ultimeArbre4(this, equipeAlliee, equipeEnnemie, log);
            case 8  -> competenceChoisie.ultimeArbre8(this, equipeAlliee, equipeEnnemie, log);
            default -> competenceChoisie.ultime(this, equipeAlliee, equipeEnnemie, log);
        }
    }


    // ── Descriptions ──────────────────────────────────────────────────────
    @Override public void descriptionAttaqueBase() {
        System.out.println("Attaque de base — Attaque la cible à 100% ATK.");
    }
    @Override public void descriptionAttaqueSpeciale() {
        if (competenceChoisie == null) { System.out.println("Aucune classe active."); return; }
        switch (competenceSpecialeActive) {
            case 1  -> competenceChoisie.descriptionCompetenceArbre();
            case 2  -> competenceChoisie.descriptionCompetenceArbre2();
            case 3  -> competenceChoisie.descriptionCompetenceArbre3();
            case 5  -> competenceChoisie.descriptionCompetenceArbre5();
            case 6  -> competenceChoisie.descriptionCompetenceArbre6();
            case 7  -> competenceChoisie.descriptionCompetenceArbre7();
            default -> competenceChoisie.descriptionAttaqueSpeciale();
        }
    }
    @Override public void descriptionAttaqueUltime() {
        if (competenceChoisie == null) { System.out.println("Aucune classe active."); return; }
        switch (competenceUltimeActive) {
            case 4  -> competenceChoisie.descriptionUltimeArbre4();
            case 8  -> competenceChoisie.descriptionUltimeArbre8();
            default -> competenceChoisie.descriptionUltime();
        }
    }

    // ── Getters / Setters ─────────────────────────────────────────────────
    public String[]    getClasses()                         { return classes; }
    public void        setClasses(String[] classes)         { this.classes = classes; }
    public String      getChoixClasses()                    { return choixClasse; }
    public void        setChoixClasses(String c)            { this.choixClasse = c; this.type = c; }
    public Competences getCompetencesChoisie()              { return competenceChoisie; }
    public void        setCompetencesChoisie(Competences c) { this.competenceChoisie = c; }
    public double      getOr()                              { return or; }
    public void        setOr(double or)                     { this.or = or; }
    public int         getCoupons()                         { return coupons; }
    public void        setCoupons(int coupons)              { this.coupons = coupons; }
    public String      getGenre()                           { return genre; }
    public void         setGenre(String genre)              { this.genre = genre; }

    /** Nom de classe affiche au joueur, accorde selon le genre (ex: Invocateur/Invocatrice). */
    public String getNomClasseAffiche() { return nomClasseAffiche(choixClasse, genre); }

    /** Version statique reutilisable avant meme la creation du personnage (ecran de choix de classe). */
    public static String nomClasseAffiche(String classeInterne, String genre) {
        boolean femme = "Femme".equals(genre);
        if (classeInterne == null) return "";
        return switch (classeInterne) {
            case "Chevalier"                    -> femme ? "Chevalière" : "Chevalier";
            case "Chasseur de Dragon"           -> femme ? "Chasseuse de Dragon" : "Chasseur de Dragon";
            case "Mage"                         -> "Mage";
            case "Constellationniste", "Invocateur" -> femme ? "Invocatrice" : "Invocateur";
            default -> classeInterne;
        };
    }

    public ArbreCompetences getArbreCompetences() { return arbreCompetences; }

    /**
     * Numero de l'arbre (1, 2, 3, 5, 6 ou 7) dont la spéciale est active, 0 = spéciale de base.
     * Mis à jour automatiquement quand un nœud 10 de type spéciale est débloqué.
     */
    public int  getCompetenceSpecialeActive()     { return competenceSpecialeActive; }
    public void setCompetenceSpecialeActive(int v){ this.competenceSpecialeActive = v; }

    /**
     * Numero de l'arbre (4 ou 8) dont l'ultime est active, 0 = ultime de base.
     * Mis à jour automatiquement quand un nœud 10 de type ultime est débloqué.
     */
    public int  getCompetenceUltimeActive()       { return competenceUltimeActive; }
    public void setCompetenceUltimeActive(int v)  { this.competenceUltimeActive = v; }

    /** Active la spéciale débloquée par l'arbre 1. */
    public void activerArbre1() {
        competenceSpecialeActive = 1;
    }

    /** Active la spéciale débloquée par l'arbre 2. */
    public void activerArbre2() {
        competenceSpecialeActive = 2;
    }

    /** Active la spéciale débloquée par l'arbre 3. */
    public void activerArbre3() {
        competenceSpecialeActive = 3;
    }

    /** Active la spéciale débloquée par l'arbre 5. */
    public void activerArbre5() {
        competenceSpecialeActive = 5;
    }

    /** Active la spéciale débloquée par l'arbre 6. */
    public void activerArbre6() {
        competenceSpecialeActive = 6;
    }

    /** Active la spéciale débloquée par l'arbre 7. */
    public void activerArbre7() {
        competenceSpecialeActive = 7;
    }

    /** Active l'ultime débloquée par l'arbre 4. */
    public void activerArbre4() {
        competenceUltimeActive = 4;
    }

    /** Active l'ultime débloquée par l'arbre 8. */
    public void activerArbre8() {
        competenceUltimeActive = 8;
    }

    // Compatibilité ascendante sauvegarde
    public boolean isUtiliserCompetenceArbre()   { return competenceSpecialeActive > 0; }
    public void    setUtiliserCompetenceArbre(boolean b) { if (b && competenceSpecialeActive == 0) competenceSpecialeActive = 1; }

    // Champs supprimés — gardés pour la compatibilité de sauvegarde uniquement
    public int  getchoixDescription_comp()          { return 0; }
    public void setChoixDescription_comp(int n)     {}
    public int  getChoixComp()                      { return 1; }
    public void setChoixComp(int n)                 {}

    public void ajouterOr(int montant) {
        this.or += montant;
        System.out.println("+ " + montant + " or ! (Total : " + String.format("%.0f", this.or) + ")");
    }

    public void retirerOr(double montant) {
        this.or = Math.max(0, this.or - montant);
    }

    /** Utilise 1 Parchemin d'Aptitude (palier au choix) : ajoute ses points a l'arbre de competences. */
    public String utiliserParcheminAptitude(Inventaire inventaire, ParcheminAptitude palier) {
        if (!inventaire.retirerMateriau(palier.nom, 1)) {
            return "Aucun " + palier.nom + " en stock.";
        }
        arbreCompetences.ajouterPoints(palier.points);
        return palier.nom + " utilise ! +" + palier.points
                + " points de talent (Total disponible : " + arbreCompetences.getPointsDisponibles() + ")";
    }

    @Override public String toString() {
        return nom + " est niveau " + niveau + " | Classe : "
                + (choixClasse != null ? choixClasse : "Aucune")
                + "\nStatistiques : " + vie + " PV, " + attaque + " ATK, "
                + defense + " DEF, " + vitesse + " VIT";
    }
}