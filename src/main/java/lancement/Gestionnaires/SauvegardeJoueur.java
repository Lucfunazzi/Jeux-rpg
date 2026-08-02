package lancement.Gestionnaires;

import Joueur.Personnage_principale;
import Joueur.Elementaliste;
import Joueur.ChasseurDeDragon;
import Joueur.Chevalier;
import Joueur.Invocateur;
import Joueur.Competences;
import Personnage.PersonnageBase;
import Personnage.FairyTail.*;
import Equipement.Equipement;
import lancement.Formation;
import lancement.GameContext;
import lancement.SauvegardeData;
import lancement.Menus.MenuRecrutement;
import java.util.ArrayList;

/** Sauvegarde/restauration du joueur principal, des personnages recrutes et de la formation. */
public class SauvegardeJoueur {

    public static void sauvegarder(GameContext ctx, SauvegardeData data) {
        Personnage_principale joueur = ctx.joueur;

        // Joueur de base
        data.joueurNom           = joueur.getNom();
        data.joueurNiveau        = joueur.getNiveau();
        data.joueurExperience    = joueur.getExperience();
        data.joueurExperienceMax = joueur.getExperienceMax();
        data.joueurOr            = joueur.getOr();
        data.joueurClasse        = joueur.getChoixClasses();
        data.joueurChoixComp     = joueur.getChoixComp();
        data.joueurGenre         = joueur.getGenre();

        // Coffre arène
        data.dernierCoffreArene = ctx.dernierCoffreArene;

        // Arbre de compétences
        data.arbreNoeudDebloques      = joueur.getArbreCompetences().getEtatNoeuds();
        data.arbreNoeudDebloques2     = joueur.getArbreCompetences().getEtatNoeuds2();
        data.arbreNoeudDebloques3     = joueur.getArbreCompetences().getEtatNoeuds3();
        data.arbrePointsDisponibles   = joueur.getArbreCompetences().getPointsDisponibles();
        data.competenceSpecialeActive = joueur.getCompetenceSpecialeActive();

        // Équipements portés par le joueur principal
        for (Equipement e : joueur.getEquipementsPortes())
            data.joueurEquipementsPortes.add(SauvegardeEquipement.versEquipementData(e, 1));

        // Personnages recrutés
        for (PersonnageBase p : ctx.personnagesRecruites) {
            SauvegardeData.PersonnageData pd = new SauvegardeData.PersonnageData(
                p.getNom(), p.getNiveau(), p.getExperience(), p.getExperienceMax()
            );
            pd.nbreEtoiles = p.getNbreEtoiles();
            for (Equipement e : p.getEquipementsPortes())
                pd.equipementsPortes.add(SauvegardeEquipement.versEquipementData(e, 1));
            data.personnagesRecruites.add(pd);
        }

        // Formation
        if (ctx.formation.getTank() != null)
            data.formationTank = ctx.formation.getTank().getNom();
        for (PersonnageBase a : ctx.formation.getAttaquants())
            data.formationAttaquants.add(a.getNom());
        for (PersonnageBase s : ctx.formation.getSupports())
            data.formationSupports.add(s.getNom());
    }

    /**
     * Restaure le joueur ET injecte le GameContext pour que le multiplicateur
     * de rang soit lu dynamiquement dans getAttaque/getDefense/getVieMax/getVitesse.
     * Restaure aussi le coffre arène.
     */
    public static Personnage_principale restaurerJoueur(SauvegardeData data, GameContext ctx) {
        Personnage_principale joueur = new Personnage_principale(data.joueurNom, 1);
        Competences comp = switch (data.joueurClasse) {
            case "Mage"     -> new Elementaliste();
            case "Chasseur de Dragon"  -> new ChasseurDeDragon();
            case "Chevalier"           -> new Chevalier();
            case "Constellationniste"  -> new Invocateur();
            default         -> null;
        };
        joueur.setChoixClasses(data.joueurClasse);
        joueur.appliquerStatsClasse(data.joueurClasse);
        joueur.setChoixComp(data.joueurChoixComp);
        joueur.setGenre(data.joueurGenre != null ? data.joueurGenre : "Homme");
        joueur.setCompetencesChoisie(comp);
        joueur.setOr(data.joueurOr);
        joueur.getArbreCompetences().setEtatNoeuds(data.arbreNoeudDebloques);
        joueur.getArbreCompetences().setPointsDisponibles(data.arbrePointsDisponibles);
        joueur.getArbreCompetences().setEtatNoeuds2(data.arbreNoeudDebloques2);
        joueur.getArbreCompetences().setEtatNoeuds3(data.arbreNoeudDebloques3);
        joueur.setCompetenceSpecialeActive(data.competenceSpecialeActive);
        while (joueur.getNiveau() < data.joueurNiveau) joueur.monterDeNiveau();
        joueur.setExperience(data.joueurExperience);
        joueur.setExperienceMax(data.joueurExperienceMax);
        if (data.joueurEquipementsPortes != null)
            for (SauvegardeData.EquipementData ed : data.joueurEquipementsPortes)
                joueur.equiper(SauvegardeEquipement.versEquipement(ed));

        // Injection du contexte pour le multiplicateur de rang
        joueur.setGameContext(ctx);

        // Restauration du coffre arène
        if (ctx != null) {
            ctx.dernierCoffreArene = data.dernierCoffreArene;
        }

        return joueur;
    }

    public static ArrayList<PersonnageBase> restaurerPersonnagesRecruites(SauvegardeData data) {
        ArrayList<PersonnageBase> liste = new ArrayList<>();
        for (SauvegardeData.PersonnageData pd : data.personnagesRecruites) {
            PersonnageBase p = creerPersonnageParNom(pd.nom);
            if (p != null) {
                appliquerNiveaux(p, pd);
                p.setNbreEtoiles(pd.nbreEtoiles);
                if (pd.equipementsPortes != null)
                    for (SauvegardeData.EquipementData ed : pd.equipementsPortes)
                        p.equiper(SauvegardeEquipement.versEquipement(ed));
                liste.add(p);
            }
        }
        return liste;
    }

    public static void restaurerFormation(Formation formation,
                                           SauvegardeData data,
                                           ArrayList<PersonnageBase> personnagesRecruites) {
        for (PersonnageBase p : personnagesRecruites) {
            if (data.formationTank != null && p.getNom().equals(data.formationTank))
                formation.ajouterPersonnage(p);
            else if (data.formationAttaquants.contains(p.getNom()))
                formation.ajouterPersonnage(p);
            else if (data.formationSupports.contains(p.getNom()))
                formation.ajouterPersonnage(p);
        }
    }

    public static PersonnageBase creerPersonnageParNom(String nom) {
        PersonnageBase p = switch (nom) {
            // Rang C
            case "Alzack"         -> new perso_Arzak();
            case "Bisca"          -> new perso_Biska();
            case "Elfman"         -> new perso_Elfman();
            case "Nab"            -> new perso_Nab();
            // Rang B
            case "Bickslow"       -> new perso_Bixrow();
            case "Evergreen"      -> new perso_Evergreen();
            case "Kana"           -> new perso_Kana();

            case "Levy"           -> new perso_Levy();
            case "Lisanna"        -> new perso_Lisanna();
            // Rang A
            case "Angel"          -> new perso_Angel();
            case "Freed"          -> new perso_Freed();
            case "Gajeel"         -> new perso_Gajeel();
            case "Gray"           -> new perso_Gray();
            case "Jubia"          -> new perso_Jubia_4elements();
            case "Lucy"           -> new perso_Lucy();
            case "Natsu"          -> new perso_Natsu();
            case "Wendy"          -> new perso_Wendy();
            // Rang S+
            case "Erza"           -> new perso_Erza();
            case "Mirajane"       -> new perso_Mirajane();
            case "Natsu Etherion" -> new perso_Natsu_Etherion();
            case "Rogue"          -> new perso_Rogue();
            case "Sting"          -> new perso_Sting();
            case "Yukino"         -> new perso_Yukino();
            case "Lucas"          -> new perso_Lucas();
            case "Mirajane Halphas" -> new perso_Mirajane_Halphas();
            case "Jellal"           -> new perso_jellal();
            case "Jellal Intermagie" -> new jellal_Arc_intermagie();
            case "José Pora"      -> new perso_Jose();
            case "Ul Milkovich"   -> new perso_Ul();
            case "Luxus"          -> new perso_Luxus();
            default               -> null;
        };
        // Repli sur la fabrique du Recrutement normal (Cherry, Duc Everlue, Tobi, Yuka,
        // Leon, Totomaru, Sol, Aria, Bora, Eligoal...) qui n'etait pas connue ici,
        // ce qui faisait disparaitre silencieusement ces personnages au rechargement.
        if (p == null) p = new MenuRecrutement().creerPersonnage(nom);
        return p;
    }

    private static void appliquerNiveaux(PersonnageBase p, SauvegardeData.PersonnageData pd) {
        while (p.getNiveau() < pd.niveau) p.monterDeNiveau();
        p.setExperience(pd.experience);
        p.setExperienceMax(pd.experienceMax);
    }
}
