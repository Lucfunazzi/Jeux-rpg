package lancement;

import Equipement.Inventaire;
import Joueur.Personnage_principale;
import Personnage.PersonnageBase;
import lancement.Chapitres.Chapitre1;
import lancement.Chapitres.Chapitre2;
import lancement.Chapitres.Chapitre3;
import lancement.ChapitreElite.Chapitre1Elite;
import lancement.ChapitreElite.Chapitre2Elite;
import lancement.ChapitreElite.Chapitre3Elite;
import lancement.Gestionnaires.GestionnaireDonjon;
import lancement.Gestionnaires.GestionnaireEnergie;
import lancement.Gestionnaires.GestionnaireEtoiles;
import lancement.Gestionnaires.GestionnaireEtoilesPerso;
import lancement.Gestionnaires.GestionnaireExamenS;
import lancement.Gestionnaires.GestionnaireQuetes;
import lancement.Gestionnaires.GestionnaireSauvegarde;
import lancement.Gestionnaires.GestionnaireTitres;
import lancement.Gestionnaires.GestionnaireCompagnons;
import lancement.Gestionnaires.Gestionnaire_pet;
import lancement.Menus.MenuEtoilesPerso;
import lancement.Menus.MenuExamenS;
import lancement.Menus.MenuRecrutement;
import lancement.Menus.MenuTirage_recrutement;
import java.util.ArrayList;

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

    // ── Chapitres élite ───────────────────────────────────────────────────
    public Chapitre1Elite chapitre1Elite;
    public Chapitre2Elite chapitre2Elite;
    public Chapitre3Elite chapitre3Elite;

    // ── Managers ──────────────────────────────────────────────────────────
    public GestionnaireSauvegarde        sauvegarde;
    public GestionnaireQuetes            gestionnaireQuetes;
    public GestionnaireEnergie           gestionnaireEnergie;
    public GestionnaireTitres            gestionnaireTitres;
    public GestionnaireDonjon            gestionnaireDonjon;
    public GestionnaireExamenS           gestionnaireExamenS;
    public GestionnaireEtoiles           gestionnaireEtoiles;
    public GestionnaireEtoilesPerso      gestionnaireEtoilesPerso;
    public GestionnaireCompagnons        gestionnaireCompagnons;
    public Gestionnaire_pet  gestionnaireCreaturesSacrees;
    public RangJoueur                    rangJoueur;

    // ── Menus ─────────────────────────────────────────────────────────────
    public MenuRecrutement   menuRecrutement;
    public MenuEtoilesPerso  menuEtoilesPerso;
    public MenuTirage_recrutement        menuTirage;
    public MenuExamenS       menuExamenS;

    // ── Divers ────────────────────────────────────────────────────────────
    public int    coupons            = 0;
    public String dernierCoffreArene = null;

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
        ctx.gestionnaireQuetes   = new GestionnaireQuetes();
        ctx.gestionnaireEnergie  = new GestionnaireEnergie();
        ctx.rangJoueur           = new RangJoueur();
        ctx.gestionnaireTitres   = new GestionnaireTitres();
        ctx.gestionnaireDonjon   = new GestionnaireDonjon();
        ctx.gestionnaireExamenS  = new GestionnaireExamenS();
        ctx.menuExamenS          = new MenuExamenS();
        ctx.gestionnaireEtoiles    = new GestionnaireEtoiles();
        ctx.gestionnaireCompagnons       = new GestionnaireCompagnons();
        ctx.gestionnaireCreaturesSacrees = new Gestionnaire_pet();
        ctx.chapitre1Elite         = new Chapitre1Elite(ctx.chapitre1);
        ctx.chapitre2Elite         = new Chapitre2Elite(ctx.chapitre1, ctx.chapitre2, ctx.chapitre1Elite);
        ctx.chapitre3Elite         = new Chapitre3Elite(ctx.chapitre3, ctx.chapitre2Elite);
        return ctx;
    }

    /** Restaure ce contexte (joueur, formation, progression...) depuis une sauvegarde chargee. */
    public void restaurerDepuis(SauvegardeData data) {
        this.joueur               = sauvegarde.restaurerJoueur(data, this);
        this.personnagesRecruites = sauvegarde.restaurerPersonnagesRecruites(data);
        sauvegarde.restaurerCompagnons(gestionnaireCompagnons, data);
        this.formation             = new Formation(this.joueur, gestionnaireCompagnons);
        sauvegarde.restaurerFormation(this.formation, data, this.personnagesRecruites);
        sauvegarde.restaurerChapitre1(chapitre1, data);
        sauvegarde.restaurerChapitre1Elite(chapitre1Elite, data);
        sauvegarde.restaurerChapitre2(chapitre2, data);
        sauvegarde.restaurerChapitre3(chapitre3, data);
        sauvegarde.restaurerChapitre2Elite2(chapitre2Elite, data);
        sauvegarde.restaurerChapitre3Elite(chapitre3Elite, data);
        sauvegarde.restaurerInventaire(inventaire, data);
        sauvegarde.restaurerQuetes(gestionnaireQuetes, data);
        sauvegarde.restaurerEnergie(gestionnaireEnergie, data);
        sauvegarde.restaurerRangEtTitres(rangJoueur, gestionnaireTitres, data);
        sauvegarde.restaurerDonjon(gestionnaireDonjon, data);
        sauvegarde.restaurerExamenS(gestionnaireExamenS, data);
        menuRecrutement.setParcheminC(data.parcheminC);
        menuRecrutement.setParcheminB(data.parcheminB);
        menuRecrutement.setParcheminA(data.parcheminA);
        menuTirage.setParcheminOrdinaire(data.parcheminTirageOrdinaire);
        menuTirage.setParcheminElite(data.parcheminTirageElite);
        menuTirage.setCompteurPityA(data.tirageEliteCompteurPityA);
        menuTirage.setCompteurPitySS(data.tirageEliteCompteurSansSS);
        menuTirage.setCompteurPityS(data.tirageEliteCompteurSansS);
        sauvegarde.restaurerEtoiles(gestionnaireEtoiles, data);
        this.coupons = data.coupons;
    }
}