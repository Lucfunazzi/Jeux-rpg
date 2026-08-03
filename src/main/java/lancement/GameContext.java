package lancement;

import Equipement.Inventaire;
import Joueur.Personnage_principale;
import Personnage.PersonnageBase;
import lancement.Chapitres.Chapitre;
import lancement.Chapitres.Chapitre1;
import lancement.Chapitres.Chapitre2;
import lancement.Chapitres.Chapitre3;
import lancement.Chapitres.Chapitre4;
import lancement.Chapitres.Chapitre5;
import lancement.Chapitres.Chapitre6;
import lancement.Chapitres.Chapitre7;
import lancement.Chapitres.Chapitre8;
import lancement.Chapitres.Chapitre9;
import lancement.Chapitres.Chapitre10;
import lancement.Chapitres.Chapitre11;
import lancement.Chapitres.Chapitre12;
import lancement.Chapitres.Chapitre13;
import lancement.ChapitreElite.ChapitreElite;
import lancement.ChapitreElite.Chapitre1Elite;
import lancement.ChapitreElite.Chapitre2Elite;
import lancement.ChapitreElite.Chapitre3Elite;
import lancement.ChapitreElite.Chapitre4Elite;
import lancement.ChapitreElite.Chapitre5Elite;
import lancement.ChapitreElite.Chapitre6Elite;
import lancement.ChapitreElite.Chapitre7Elite;
import lancement.ChapitreElite.Chapitre8Elite;
import lancement.ChapitreElite.Chapitre9Elite;
import lancement.ChapitreElite.Chapitre10Elite;
import lancement.ChapitreElite.Chapitre11Elite;
import lancement.ChapitreElite.Chapitre12Elite;
import lancement.ChapitreElite.Chapitre13Elite;
import lancement.Gestionnaires.GestionnaireChasseTresor;
import lancement.Gestionnaires.GestionnaireDonjon;
import lancement.Gestionnaires.GestionnaireEnergie;
import lancement.Gestionnaires.GestionnaireEtoiles;
import lancement.Gestionnaires.GestionnaireEtoilesPerso;
import lancement.Gestionnaires.GestionnaireExamenS;
import lancement.Gestionnaires.GestionnaireQuetes;
import lancement.Gestionnaires.GestionnaireRecompenses;
import lancement.Gestionnaires.GestionnaireSauvegarde;
import lancement.Gestionnaires.GestionnaireTitres;
import lancement.Gestionnaires.GestionnaireCompagnons;
import lancement.Gestionnaires.Gestionnaire_pet;
import lancement.Gestionnaires.GestionnaireTutoriel;
import lancement.Menus.MenuEtoilesPerso;
import lancement.Menus.MenuExamenS;
import lancement.Menus.MenuRecrutement;
import lancement.Menus.MenuTirage_recrutement;
import java.util.ArrayList;
import java.util.List;

/**
 * Conteneur central passé à chaque chapitre, stage et menu.
 * Ajouter un nouveau chapitre = ajouter un champ ici, c'est tout.
 */
public class GameContext {

    // ── Joueur & équipe ───────────────────────────────────────────────────
    public Personnage_principale       joueur;
    public Formation                   formation;
    public ArrayList<PersonnageBase>   personnagesRecruites;
    public Inventaire                  inventaire;

    // ── Chapitres normaux ─────────────────────────────────────────────────
    public Chapitre1      chapitre1;
    public Chapitre2      chapitre2;
    public Chapitre3      chapitre3;
    public Chapitre4      chapitre4;
    public Chapitre5      chapitre5;
    public Chapitre6      chapitre6;
    public Chapitre7      chapitre7;
    public Chapitre8      chapitre8;
    public Chapitre9      chapitre9;
    public Chapitre10     chapitre10;
    public Chapitre11     chapitre11;
    public Chapitre12     chapitre12;
    public Chapitre13     chapitre13;

    // ── Chapitres élite ───────────────────────────────────────────────────
    public Chapitre1Elite chapitre1Elite;
    public Chapitre2Elite chapitre2Elite;
    public Chapitre3Elite chapitre3Elite;
    public Chapitre4Elite chapitre4Elite;
    public Chapitre5Elite chapitre5Elite;
    public Chapitre6Elite chapitre6Elite;
    public Chapitre7Elite chapitre7Elite;
    public Chapitre8Elite chapitre8Elite;
    public Chapitre9Elite chapitre9Elite;
    public Chapitre10Elite chapitre10Elite;
    public Chapitre11Elite chapitre11Elite;
    public Chapitre12Elite chapitre12Elite;
    public Chapitre13Elite chapitre13Elite;

    /** Vue generique des chapitres 1-13, indexee a partir de 0 (chapitres.get(0) = chapitre 1). */
    public List<Chapitre>       chapitres;
    /** Vue generique des chapitres elite 1-13, indexee a partir de 0 (chapitresElite.get(0) = chapitre 1 elite). */
    public List<ChapitreElite>  chapitresElite;

    // ── Managers ──────────────────────────────────────────────────────────
    public GestionnaireSauvegarde        sauvegarde;
    public GestionnaireQuetes            gestionnaireQuetes;
    public GestionnaireEnergie           gestionnaireEnergie;
    public GestionnaireTitres            gestionnaireTitres;
    public GestionnaireDonjon            gestionnaireDonjon;
    public GestionnaireChasseTresor      gestionnaireChasseTresor;
    public GestionnaireExamenS           gestionnaireExamenS;
    public GestionnaireEtoiles           gestionnaireEtoiles;
    public GestionnaireEtoilesPerso      gestionnaireEtoilesPerso;
    public GestionnaireCompagnons        gestionnaireCompagnons;
    public Gestionnaire_pet  gestionnaireCreaturesSacrees;
    public GestionnaireRecompenses       gestionnaireRecompenses;
    public GestionnaireTutoriel          gestionnaireTutoriel;
    public RangJoueur                    rangJoueur;

    // ── Menus ─────────────────────────────────────────────────────────────
    public MenuRecrutement   menuRecrutement;
    public MenuEtoilesPerso  menuEtoilesPerso;
    public MenuTirage_recrutement        menuTirage;
    public MenuExamenS       menuExamenS;

    // ── Divers ────────────────────────────────────────────────────────────
    public int    coupons            = 0;
    public String dernierCoffreArene = null;
    /** Deverrouille via le code secret dans Options ; donne acces au menu de debug (test only). */
    public boolean debugDeverrouille = false;

    public GameContext() {}

    /**
     * Construit un GameContext avec tous les managers/chapitres de base initialisés
     * (sans joueur/formation, qui dépendent du choix nouvelle-partie / chargement).
     * Utilisé par le lancement console (Main) et par l'interface graphique.
     */
    public static GameContext creerContexteBase() {
        GameContext ctx = new GameContext();
        ctx.sauvegarde           = new GestionnaireSauvegarde();
        ctx.inventaire           = new Inventaire();
        ctx.menuRecrutement      = new MenuRecrutement();
        ctx.menuEtoilesPerso     = new MenuEtoilesPerso();
        ctx.menuTirage           = new MenuTirage_recrutement();
        ctx.chapitre1            = new Chapitre1();
        ctx.chapitre2            = new Chapitre2();
        ctx.chapitre3            = new Chapitre3();
        ctx.chapitre4            = new Chapitre4();
        ctx.chapitre5            = new Chapitre5();
        ctx.chapitre6            = new Chapitre6();
        ctx.chapitre7            = new Chapitre7();
        ctx.chapitre8            = new Chapitre8();
        ctx.chapitre9            = new Chapitre9();
        ctx.chapitre10           = new Chapitre10();
        ctx.chapitre11           = new Chapitre11();
        ctx.chapitre12           = new Chapitre12();
        ctx.chapitre13           = new Chapitre13();
        ctx.gestionnaireQuetes   = new GestionnaireQuetes();
        ctx.gestionnaireEnergie  = new GestionnaireEnergie();
        ctx.rangJoueur           = new RangJoueur();
        ctx.gestionnaireTitres   = new GestionnaireTitres();
        ctx.gestionnaireDonjon   = new GestionnaireDonjon();
        ctx.gestionnaireChasseTresor = new GestionnaireChasseTresor();
        ctx.gestionnaireExamenS  = new GestionnaireExamenS();
        ctx.menuExamenS          = new MenuExamenS();
        ctx.gestionnaireEtoiles    = new GestionnaireEtoiles();
        ctx.gestionnaireCompagnons       = new GestionnaireCompagnons();
        ctx.gestionnaireCreaturesSacrees = new Gestionnaire_pet();
        ctx.gestionnaireRecompenses      = new GestionnaireRecompenses();
        ctx.gestionnaireTutoriel         = new GestionnaireTutoriel();
        ctx.chapitre1Elite         = new Chapitre1Elite(ctx.chapitre1);
        ctx.chapitre2Elite         = new Chapitre2Elite(ctx.chapitre1, ctx.chapitre2, ctx.chapitre1Elite);
        ctx.chapitre3Elite         = new Chapitre3Elite(ctx.chapitre3, ctx.chapitre2Elite);
        ctx.chapitre4Elite         = new Chapitre4Elite(ctx.chapitre4, ctx.chapitre3Elite);
        ctx.chapitre5Elite         = new Chapitre5Elite(ctx.chapitre5, ctx.chapitre4Elite);
        ctx.chapitre6Elite         = new Chapitre6Elite(ctx.chapitre6, ctx.chapitre5Elite);
        ctx.chapitre7Elite         = new Chapitre7Elite(ctx.chapitre7, ctx.chapitre6Elite);
        ctx.chapitre8Elite         = new Chapitre8Elite(ctx.chapitre8, ctx.chapitre7Elite);
        ctx.chapitre9Elite         = new Chapitre9Elite(ctx.chapitre9, ctx.chapitre8Elite);
        ctx.chapitre10Elite        = new Chapitre10Elite(ctx.chapitre10, ctx.chapitre9Elite);
        ctx.chapitre11Elite        = new Chapitre11Elite(ctx.chapitre11, ctx.chapitre10Elite);
        ctx.chapitre12Elite        = new Chapitre12Elite(ctx.chapitre12, ctx.chapitre11Elite);
        ctx.chapitre13Elite        = new Chapitre13Elite(ctx.chapitre13, ctx.chapitre12Elite);

        ctx.chapitres = List.of(ctx.chapitre1, ctx.chapitre2, ctx.chapitre3, ctx.chapitre4, ctx.chapitre5,
                ctx.chapitre6, ctx.chapitre7, ctx.chapitre8, ctx.chapitre9, ctx.chapitre10,
                ctx.chapitre11, ctx.chapitre12, ctx.chapitre13);
        ctx.chapitresElite = List.of(ctx.chapitre1Elite, ctx.chapitre2Elite, ctx.chapitre3Elite, ctx.chapitre4Elite,
                ctx.chapitre5Elite, ctx.chapitre6Elite, ctx.chapitre7Elite, ctx.chapitre8Elite, ctx.chapitre9Elite,
                ctx.chapitre10Elite, ctx.chapitre11Elite, ctx.chapitre12Elite, ctx.chapitre13Elite);

        return ctx;
    }

    /** Restaure ce contexte (joueur, formation, progression...) depuis une sauvegarde chargee. */
    public void restaurerDepuis(SauvegardeData data) {
        this.joueur               = sauvegarde.restaurerJoueur(data, this);
        this.personnagesRecruites = sauvegarde.restaurerPersonnagesRecruites(data);
        sauvegarde.restaurerCompagnons(gestionnaireCompagnons, data);
        sauvegarde.restaurerCreaturesSacrees(gestionnaireCreaturesSacrees, data);
        sauvegarde.restaurerRecompenses(gestionnaireRecompenses, data);
        this.formation             = new Formation(this.joueur, gestionnaireCompagnons);
        this.formation.setGestionnaireTitres(this.gestionnaireTitres);
        sauvegarde.restaurerFormation(this.formation, data, this.personnagesRecruites);
        sauvegarde.restaurerChapitres(chapitres, data.chapitresDebloques, data.chapitresReussis);
        sauvegarde.restaurerChapitresElite(chapitresElite, data.chapitresEliteDebloques, data.chapitresEliteReussis);
        sauvegarde.restaurerChapitre3ElitePremiereVictoire(chapitre3Elite, data);
        sauvegarde.restaurerInventaire(inventaire, data);
        sauvegarde.restaurerQuetes(this, data);
        sauvegarde.restaurerTutoriel(this, data);
        sauvegarde.restaurerEnergie(gestionnaireEnergie, data);
        sauvegarde.restaurerRangEtTitres(rangJoueur, gestionnaireTitres, data);
        this.formation.appliquerBonusLiens();
        sauvegarde.restaurerDonjon(gestionnaireDonjon, data);
        sauvegarde.restaurerChasseTresor(gestionnaireChasseTresor, data);
        sauvegarde.restaurerExamenS(gestionnaireExamenS, data);
        menuRecrutement.setParcheminC(data.parcheminC);
        menuRecrutement.setParcheminB(data.parcheminB);
        menuRecrutement.setParcheminA(data.parcheminA);
        menuRecrutement.setParcheminS(data.parcheminS);
        menuTirage.setParcheminOrdinaire(data.parcheminTirageOrdinaire);
        menuTirage.setParcheminElite(data.parcheminTirageElite);
        menuTirage.setCompteurPityA(data.tirageEliteCompteurPityA);
        menuTirage.setCompteurPitySS(data.tirageEliteCompteurSansSS);
        menuTirage.setCompteurPityS(data.tirageEliteCompteurSansS);
        sauvegarde.restaurerEtoiles(gestionnaireEtoiles, data);
        this.coupons = data.coupons;
        this.debugDeverrouille = data.debugDeverrouille;
    }
}