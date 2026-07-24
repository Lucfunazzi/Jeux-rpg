package lancement;

import java.util.ArrayList;
import java.util.List;

public class SauvegardeData {

    // ── Joueur ────────────────────────────────────────────────────────────
    public String  joueurNom;
    public int     joueurNiveau;
    public int     joueurExperience;
    public int     joueurExperienceMax;
    public double  joueurOr;
    public String  joueurClasse;
    public int     joueurChoixComp;

    public boolean[] arbreNoeudDebloques     = new boolean[10];
    public boolean[] arbreNoeudDebloques2    = new boolean[10];
    public boolean[] arbreNoeudDebloques3    = new boolean[10];
    public int       arbrePointsDisponibles  = 0;
    public int       competenceSpecialeActive = 0;

    public String rangJoueur = "C";
    // prestige supprimé

    public List<String> titresObtenus = new ArrayList<>();
    public String       titreActifNom = null;
    public String dernierCoffreArene = null;

    // ── Personnages recrutes ───────────────────────────────────────────────
    public List<PersonnageData> personnagesRecruites = new ArrayList<>();

    // ── Formation ─────────────────────────────────────────────────────────
    public String       formationTank;
    public List<String> formationAttaquants = new ArrayList<>();
    public List<String> formationSupports   = new ArrayList<>();

    // ── Progression Chapitre 1 ────────────────────────────────────────────
    public boolean[] chapitre1Debloques      = new boolean[11];
    public boolean[] chapitre1Reussis        = new boolean[11];
    public boolean[] chapitre1EliteDebloques = new boolean[11];
    public boolean[] chapitre1EliteReussis   = new boolean[11];

    public boolean[] chapitre2Debloques      = new boolean[11];
    public boolean[] chapitre2Reussis        = new boolean[11];
    public boolean[] chapitre2EliteDebloques = new boolean[11];
    public boolean[] chapitre2EliteReussis   = new boolean[11];
    public boolean[] chapitre3EliteDebloques  = new boolean[11];
    public boolean[] chapitre3EliteReussis    = new boolean[11];
    public boolean[] chapitre3ElitePremiereVictoire = new boolean[11];

    public boolean[] chapitre3Debloques = new boolean[11];
    public boolean[] chapitre3Reussis   = new boolean[11];

 

    // ── Recrutement ───────────────────────────────────────────────────────
    public int parcheminC;
    public int parcheminB;
    public int parcheminA;

    // ── Tirages ───────────────────────────────────────────────────────────
    public int parcheminTirageOrdinaire = 0;
    public int parcheminTirageElite     = 0;
    public int tirageEliteCompteurPityA  = 0;
    public int tirageEliteCompteurSansS = 0;
    public int tirageEliteCompteurSansSS = 0;

    // ── Inventaire ────────────────────────────────────────────────────────
    public List<EquipementData>  inventaireEquipements   = new ArrayList<>();
    public List<EquipementData>  joueurEquipementsPortes = new ArrayList<>();
    public List<ParcheminXPData> inventaireParcheminsXP  = new ArrayList<>();
    public List<CarteOrData>     inventaireCartesOr      = new ArrayList<>();
    public List<PierreStackData> inventairePierres       = new ArrayList<>();

    // ── Donnees personnage ────────────────────────────────────────────────
    public static class PersonnageData {
        public String nom;
        public int    niveau;
        public int    experience;
        public int    experienceMax;
        public int    nbreEtoiles = 0;
        public List<EquipementData> equipementsPortes = new ArrayList<>();

        public PersonnageData() {}
        public PersonnageData(String nom, int niveau, int experience, int experienceMax) {
            this.nom = nom; this.niveau = niveau;
            this.experience = experience; this.experienceMax = experienceMax;
        }
    }

    // ── Donnees carte d'or ────────────────────────────────────────────────
    public static class CarteOrData {
        public String niveau;   // nom de l'enum CarteOr (ex : "NIVEAU_1")
        public int    quantite;
        public CarteOrData() {}
        public CarteOrData(String niveau, int quantite) {
            this.niveau   = niveau;
            this.quantite = quantite;
        }
    }

    // ── Donnees pierre (socket) ────────────────────────────────────────────
    public static class PierreData {
        public String type;   // nom de l'enum Pierre.Type
        public int    niveau;
        public PierreData() {}
        public PierreData(String type, int niveau) {
            this.type   = type;
            this.niveau = niveau;
        }
    }

    // ── Donnees stock de pierres (inventaire) ─────────────────────────────
    public static class PierreStackData {
        public String type;   // nom de l'enum Pierre.Type
        public int    niveau;
        public int    quantite;
        public PierreStackData() {}
        public PierreStackData(String type, int niveau, int quantite) {
            this.type     = type;
            this.niveau   = niveau;
            this.quantite = quantite;
        }
    }

    // ── Donnees parchemin XP ─────────────────────────────────────────────
    public static class ParcheminXPData {
        public String rarete;
        public int    quantite;
        public ParcheminXPData() {}
        public ParcheminXPData(String rarete, int quantite) {
            this.rarete = rarete; this.quantite = quantite;
        }
    }

    // ── Donnees equipement ────────────────────────────────────────────────
    public static class EquipementData {
        public String nom;
        public String slot;
        public String rarete;
        public String typeArme;
        public double bonusATK;
        public double bonusDEF;
        public double bonusPV;
        public double bonusVIT;
        public int    quantite            = 1;
        public int    niveauFortification = 0;
        public int    niveauAffinage      = 0;
        public List<PierreData> pierres   = new ArrayList<>();

        public EquipementData() {}
        public EquipementData(String nom, String slot, String rarete, String typeArme,
                              double bonusATK, double bonusDEF, double bonusPV, double bonusVIT,
                              int quantite) {
            this.nom = nom; this.slot = slot; this.rarete = rarete; this.typeArme = typeArme;
            this.bonusATK = bonusATK; this.bonusDEF = bonusDEF;
            this.bonusPV = bonusPV; this.bonusVIT = bonusVIT; this.quantite = quantite;
        }
    }

    // ── Quetes ────────────────────────────────────────────────────────────
    public String               dernierRenouvellementQuete;
    public int                  indexQueteJournaliere       = 0;
    public QueteJournaliereData queteJournaliereActive;
    public List<String>         quetesProgressionReclamees  = new ArrayList<>();
    public List<String>         quetesProgressionCompletees = new ArrayList<>();

    public static class QueteJournaliereData {
        public String  id;
        public String  titre;
        public String  description;
        public String  typeObjectif;
        public int     objectifCible;
        public int     recompenseXP;
        public int     recompenseOr;
        public int     progression;
        public boolean completee;
        public boolean reclamee;
        public QueteJournaliereData() {}
    }

    // ── Energie ───────────────────────────────────────────────────────────
    public int    energie                 = 100;
    public String derniereRechargeEnergie;
    public int    rechargesUtilisees      = 0;
    public String dernierResetRecharge;
    public int[]  runsEliteParStage       = new int[11];
    public String dernierResetRunsElite;

    // ── Donjon de ressources ──────────────────────────────────────────────
    public int[][]  donjonRuns        = new int[3][3];
    public String   donjonDernierReset;

    // ── Examen de Rang S ──────────────────────────────────────────────────
    public boolean[] examenSDejaReussi     = new boolean[11];
    public boolean[] examenSFaitAujourdhui = new boolean[11];
    public String    examenSDernierReset;

    // ── Etoiles & coffres ────────────────────────────────────────────────
    public List<EtoileData>   etoiles       = new ArrayList<>();
    public List<String>       coffresClaimes = new ArrayList<>();

    public static class EtoileData {
        public String  cle;
        public boolean e1, e2, e3;
        public EtoileData() {}
        public EtoileData(String cle, boolean e1, boolean e2, boolean e3) {
            this.cle = cle; this.e1 = e1; this.e2 = e2; this.e3 = e3;
        }
    }

    // ── Compagnons ────────────────────────────────────────────────────────
    public String compagnonsType   = "HAPPY";
    public int    compagnonsNiveau = 1;

    // ── Créature Sacrée ──────────────────────────────────────────────────
    public String  creatureType              = "OEUF";
    public int     creatureNiveau             = 1;
    public int     creatureExperience         = 0;
    public boolean creatureOeufDebloque       = false;
    public String  creatureEntrainement       = null;
    public String  creatureDebutEntrainement  = null;

    // ── Récompenses ───────────────────────────────────────────────────────
    public boolean[] recompensesNiveauReclame       = null;
    public int       recompensesJoursCumulesMois     = 0;
    public String    recompensesDernierJourMois      = null;
    public int       recompensesMoisCompte           = -1;
    public boolean[] recompensesMoisReclame          = null;
    public int       recompensesJourConnexion        = 0;
    public String    recompensesDernierJourConnexion = null;
    public boolean[] recompensesJourReclame          = null;
    public boolean   recompensesTerminee              = false;
    public String    recompensesDerniereReclamation30min = null;
    public int       recompensesPointsMois             = 0;

    // ── Coupons ───────────────────────────────────────────────────────────
    public int coupons = 0;

    // ── Materiaux ─────────────────────────────────────────────────────────
    public List<MateriauData> materiaux = new ArrayList<>();

    public static class MateriauData {
        public String nom;
        public int    quantite;
        public MateriauData() {}
        public MateriauData(String nom, int quantite) {
            this.nom = nom; this.quantite = quantite;
        }
    }
}