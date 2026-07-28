package lancement.Gestionnaires;

import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Tutoriel guide du menu principal : au premier lancement, propose une visite guidee
 * (fleche + bulle d'explication) de chaque menu disponible. Les menus debloques plus tard
 * (ex: Compagnons) declenchent une bulle d'explication ciblee des leur premiere apparition.
 */
public class GestionnaireTutoriel {

    /** Une etape de la visite guidee : le libelle exact du bouton cible + le texte explicatif. */
    public record EtapeTutoriel(String libelle, String texte) {}

    private static final List<EtapeTutoriel> ETAPES = List.of(
        new EtapeTutoriel("Histoire",
                "Ici se trouvent les chapitres de votre aventure (pour l'instant il n'existe que 3 chapitres)."),
        new EtapeTutoriel("Quetes",
                "Le menu Quetes permet de debloquer les stages des chapitres : vous devez accepter chaque quete ici avant de pouvoir jouer le stage associe. Les quetes journalieres s'y ajoutent a partir du niveau 30."),
        new EtapeTutoriel("Recompenses",
                "Reclamez ici vos recompenses de palier de niveau, de connexion et vos points du mois."),
        new EtapeTutoriel("Abilites",
                "Depensez vos points d'aptitude pour renforcer votre personnage principal dans l'arbre de competences. Vous pourrez aussi y debloquer des attaques speciales et ultimes dans le futur."),
        new EtapeTutoriel("Rang & Titres",
                "Faites progresser votre Rang Joueur et equipez des titres qui boostent vos statistiques."),
        new EtapeTutoriel("Formation",
                "Composez votre equipe de combat parmi vos personnages recrutes."),
        new EtapeTutoriel("Personnages",
                "Consultez et equipez chacun de vos personnages recrutes."),
        new EtapeTutoriel("Inventaire",
                "Gerez vos equipements et materiaux, et equipez-les sur vos personnages."),
        new EtapeTutoriel("Ameliorations",
                "Fortifiez, affinez et incrustez des pierres sur vos equipements pour les rendre plus puissants."),
        new EtapeTutoriel("Compagnons",
                "Recrutez et faites evoluer un compagnon qui vous accompagne au combat."),
        new EtapeTutoriel("Creatures Sacrees",
                "Invoquez des creatures sacrees qui apportent des bonus supplementaires a votre equipe."),
        new EtapeTutoriel("Recrutement",
                "Recrutez de nouveaux personnages avec vos parchemins."),
        new EtapeTutoriel("Recrutement Rare",
                "Recrutez des personnages rares grace a des materiaux speciaux obtenus en jeu."),
        new EtapeTutoriel("Tirages",
                "Tentez votre chance pour obtenir des personnages et objets grace aux tirages."),
        new EtapeTutoriel("Etoiles & Fragments",
                "Utilisez vos fragments pour faire monter en etoiles vos personnages recrutes et augmenter leur puissance."),
        new EtapeTutoriel("Donjon de ressources",
                "Affrontez des donjons pour recolter des ressources utiles a votre progression."),
        new EtapeTutoriel("Arene",
                "Affrontez d'autres joueurs pour grimper au classement et gagner des recompenses."),
        new EtapeTutoriel("Examen de Rang S",
                "Passez des combats difficiles pour obtenir de l'equipement de rang S et progresser plus vite.")
    );

    public static List<EtapeTutoriel> getEtapes() { return ETAPES; }

    /** Explications affichees a la premiere visite d'un sous-ecran (independant de la visite guidee du menu principal). */
    private static final java.util.Map<String, String> EXPLICATIONS_ECRAN = java.util.Map.ofEntries(
        java.util.Map.entry("Histoire",
                "Chaque stage doit d'abord etre debloque en acceptant sa quete associee (menu Quetes). Terminez "
              + "tous les stages d'un chapitre pour debloquer sa version Elite, plus difficile, ainsi que le chapitre suivant."),
        java.util.Map.entry("Quetes",
                "L'onglet Quetes de chapitre liste les quetes a accepter pour debloquer les stages associes. "
              + "L'onglet Quetes journalieres (debloque au niveau 30) propose des objectifs renouveles chaque jour "
              + "contre des recompenses et des points pour la barre journaliere."),
        java.util.Map.entry("Inventaire",
                "Consultez vos equipements et materiaux, et equipez-les sur vos personnages. Depuis la fiche d'un "
              + "personnage, le bouton \"Equiper le meilleur disponible\" optimise automatiquement son equipement."),
        java.util.Map.entry("Ameliorations",
                "Fortifiez vos equipements avec de l'or pour augmenter leurs statistiques, affinez-les (des le "
              + "niveau 20) avec des pierres d'affinage, et incrustez des pierres pour des bonus supplementaires."),
        java.util.Map.entry("Abilites",
                "Chaque arbre de competences (debloque l'un apres l'autre) permet de debloquer une nouvelle "
              + "attaque speciale nommee en depensant vos points d'aptitude. Terminer les 3 arbres fait progresser "
              + "votre personnage principal, y compris son rang joueur."),
        java.util.Map.entry("Recrutement",
                "Utilisez vos Parchemins C/B/A pour recruter des personnages via les Pages de recrutement. "
              + "Le mini-jeu Pierre-Feuille-Ciseaux permet d'en gagner davantage : en manuel (3 manches jouees a la main, "
              + "remboursement partiel de l'or en cas de defaite) ou en automatique (x10 parties d'un coup). Vous pouvez "
              + "aussi echanger des parchemins de recrutement contre des Parchemins XP pour faire monter un personnage en niveau."),
        java.util.Map.entry("Recrutement Rare",
                "Recrutez certains personnages rares (Natsu, Mirajane, Jellal...) contre des Parchemins de Chasse "
              + "A/S/SS selon leur rarete, trouves a la Chasse au tresor. Une fois recrute, un personnage rare peut "
              + "ensuite evoluer vers une forme superieure en depensant le palier de parchemin superieur."),
        java.util.Map.entry("Chasse au tresor",
                "Fouillez jusqu'a 5 emplacements par jour (reset a minuit) pour trouver des Parchemins de Chasse "
              + "A/S/SS, utilises au Recrutement Rare pour recruter ou faire evoluer les personnages rares."),
        java.util.Map.entry("Tirages",
                "Deux types de tirages : le Tirage Ordinaire (Parchemins Ordinaires, donne des fragments C/B/A, "
              + "avec une petite chance d'obtenir directement un personnage) et le Tirage Elite (Parchemins Elite, "
              + "fragments B/A/S/SS, avec un systeme de pity qui garantit un personnage complet a intervalles reguliers)."),
        java.util.Map.entry("Formation",
                "Composez votre equipe de combat (jusqu'a 5 personnages) parmi vos personnages recrutes, et "
              + "choisissez votre competence speciale active. Certaines combinaisons de personnages presents "
              + "ensemble activent des liens qui offrent des bonus a toute l'equipe."),
        java.util.Map.entry("Personnages",
                "Consultez la fiche complete de chacun de vos personnages recrutes : statistiques, equipement porte, "
              + "bonus de set et competences. C'est ici que vous equipez individuellement chaque personnage."),
        java.util.Map.entry("Compagnons",
                "Faites evoluer un compagnon qui apporte des bonus permanents (ATK/PV/DEF/VIT) a toute votre equipe. "
              + "Il monte de niveau contre de l'or, et peut evoluer vers une forme superieure une fois au niveau maximum."),
        java.util.Map.entry("Creatures Sacrees",
                "Debloquee apres le Chapitre 2 Elite. Faites progresser votre creature sacree en utilisant une "
              + "Friandise de Familier (gain d'XP instantane) ou en lancant un entrainement minute a reclamer plus "
              + "tard. Une fois developpee, elle apporte des bonus de stats a toute l'equipe."),
        java.util.Map.entry("Donjon de ressources",
                "Trois donjons distincts selon la ressource visee (Or, Affinage, Experience), chacun avec plusieurs "
              + "difficultes et 3 tentatives par jour et par difficulte (reset a minuit) — combat automatique contre "
              + "une equipe d'ennemis predefinie."),
        java.util.Map.entry("Arene",
                "Affrontez des adversaires proches de votre rang pour grimper au classement. Un coffre journalier "
              + "(disponible chaque jour a partir de 20h) et un coffre de rang d'Arene (a chaque palier de rang "
              + "atteint) offrent de l'or, des points boutique et des Sceaux de Rang."),
        java.util.Map.entry("Examen de Rang S",
                "Des stages debloques progressivement, avec 1 tentative par stage et par jour. La toute premiere "
              + "victoire d'un stage donne une recompense garantie ; les victoires suivantes n'ont qu'une chance "
              + "d'en obtenir une."),
        java.util.Map.entry("Rang & Titres",
                "Tentez une montee de Rang Joueur une fois ses conditions remplies : ca augmente un multiplicateur "
              + "de stats global. Vous pouvez aussi equiper un titre obtenu en jeu pour booster les statistiques de "
              + "toute l'equipe."),
        java.util.Map.entry("Recompenses",
                "Quatre systemes reunis ici : recompenses de palier de niveau, pointage du mois (avec une boutique "
              + "dediee), connexion de 7 jours, et recompense quotidienne reclamable toutes les 30 minutes."),
        java.util.Map.entry("Etoiles & Fragments",
                "Deux usages pour les fragments d'un personnage : recruter un personnage pas encore possede une "
              + "fois assez de fragments collectes, ou faire monter en etoiles (jusqu'a 5) un personnage deja "
              + "recrute pour augmenter durablement sa puissance.")
    );

    /** Texte d'explication de l'ecran donne, ou {@code null} si aucun n'est defini pour cette cle. */
    public static String explicationEcran(String cle) { return EXPLICATIONS_ECRAN.get(cle); }

    private boolean propose = false;
    private boolean actif   = false;
    private Set<String> etapesVues = new HashSet<>();

    public boolean isPropose() { return propose; }
    public void setPropose(boolean v) { propose = v; }

    public boolean isActif() { return actif; }
    public void setActif(boolean v) { actif = v; }

    public boolean aEteVue(String libelle) { return etapesVues.contains(libelle); }
    public void marquerEtapeVue(String libelle) { etapesVues.add(libelle); }

    /** Vrai si l'explication de premiere visite de cet ecran n'a encore jamais ete affichee. */
    public boolean doitAfficherExplicationEcran(String cle) { return !aEteVue("ecran:" + cle); }
    public void marquerExplicationEcranVue(String cle) { marquerEtapeVue("ecran:" + cle); }

    public Set<String> getEtapesVues() { return etapesVues; }
    public void setEtapesVues(Collection<String> v) {
        etapesVues = v != null ? new HashSet<>(v) : new HashSet<>();
    }
}
