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
import lancement.Gestionnaires.GestionnaireQuetes;
import lancement.Gestionnaires.GestionnaireSauvegarde;
import lancement.Gestionnaires.GestionnaireTitres;
import lancement.Gestionnaires.GestionnaireCompagnons;
import lancement.Gestionnaires.Gestionnaire_pet;
import lancement.Gestionnaires.GestionnaireClefsCelestes;
import lancement.Menus.MenuEtoilesPerso;
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
    public GestionnaireEtoiles           gestionnaireEtoiles;
    public GestionnaireEtoilesPerso      gestionnaireEtoilesPerso;
    public GestionnaireCompagnons        gestionnaireCompagnons;
    public Gestionnaire_pet  gestionnaireCreaturesSacrees;
    public GestionnaireClefsCelestes     gestionnaireClefsCelestes;
    public RangJoueur                    rangJoueur;

    // ── Menus ─────────────────────────────────────────────────────────────
    public MenuRecrutement   menuRecrutement;
    public MenuEtoilesPerso  menuEtoilesPerso;
    public MenuTirage_recrutement        menuTirage;

    // ── Divers ────────────────────────────────────────────────────────────
    public int    coupons            = 0;
    public String dernierCoffreArene = null;

    public GameContext() {}
}