package lancement.Chapitres;

import Equipement.EquipementFactory;
import Personnage.PersonnageBase;
import lancement.GameContext;
import lancement.Stage;
import java.util.ArrayList;
import java.util.Scanner;
import Personnage.pnj.EnnemisGeneriques.*;
import Personnage.pnj.Chapitre5.*;
import lancement.Formation;
import Personnage.FairyTail.*;

public class Chapitre5 implements Chapitre {

    private static final int NB_STAGES = 10;
    private final boolean[] stagesDebloques = new boolean[NB_STAGES + 1];
    private final boolean[] stagesReussis   = new boolean[NB_STAGES + 1];

    public Chapitre5() {
        stagesDebloques[1] = true;
    }

    public void afficher(GameContext ctx, Scanner scanner) {
        boolean retour = false;

        while (!retour) {
            ctx.gestionnaireEnergie.mettreAJourRecharge();

            System.out.println("\n========================================");
            System.out.println("   CHAPITRE 5 — " + getNomChapitre());
            System.out.println("========================================");
            System.out.println("Or : " + String.format("%.0f", ctx.joueur.getOr())
                    + "  |  " + ctx.gestionnaireEnergie.afficherEnergie());
            System.out.println();

            for (int i = 1; i <= NB_STAGES; i++) {
                boolean jouable = ctx.gestionnaireQuetes.estStageJouable(5, i, false);
                String etat = stagesReussis[i] ? "[OK]  "
                        : !stagesDebloques[i]  ? "[###] "
                        : jouable              ? "[  ]  "
                        :                        "[QUETE] ";
                String etoiles = ctx.gestionnaireEtoiles.getEtoiles(5, i, false).afficher();
                System.out.println(etat + "Stage " + i + " — " + getTitreStage(i) + "  " + etoiles);
            }

            System.out.println("\nEntrez le numero du stage (0 pour revenir) :");
            int choix;
            try {
                choix = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Entree invalide.");
                continue;
            }

            if (choix == 0) {
                retour = true;
            } else if (choix < 1 || choix > NB_STAGES) {
                System.out.println("Stage invalide.");
            } else if (!stagesDebloques[choix]) {
                System.out.println("Ce stage est verrouille. Terminez d'abord le stage precedent.");
            } else if (!ctx.gestionnaireQuetes.estStageJouable(5, choix, false)) {
                System.out.println("Acceptez d'abord la quete associee a ce stage (menu Quetes).");
            } else {
                lancerStage(ctx, choix);
            }
        }
    }

    /**
     * Lance le stage donne et applique les recompenses en cas de victoire. Suppose que le stage
     * est deja debloque. Reutilisable par la console et l'interface graphique.
     * TODO : ajouter ici les invites temporaires / combats scriptes propres a ce chapitre,
     * sur le modele de Chapitre4.lancerStage (switch par numero de stage).
     */
    public Stage.ResultatStage lancerStage(GameContext ctx, int numero) {
        if (!ctx.gestionnaireEnergie.consommerEnergie(1)) {
            System.out.println("Pas assez d'energie ! (il faut 1, vous avez "
                    + ctx.gestionnaireEnergie.getEnergie() + ")");
            return new Stage.ResultatStage(false, false, false, 0,
                    java.util.List.of(), java.util.List.of());
        }

        Stage stage        = construireStage(numero);
        boolean estNouveau = !stagesReussis[numero];
       Stage.ResultatStage resultatStage = switch (numero) {
           case 3 ->{
               perso_Elfman invite = Formation.creerInvite(perso_Elfman::new,52,5000,800,600,200);
               yield lancerStageAvecInvite(ctx,stage,estNouveau,invite);
           }
           case 4 -> {
               perso_Gray invite = Formation.creerInvite(perso_Gray::new, 53, 4500, 950, 450, 220);
               yield lancerStageAvecInvite(ctx, stage, estNouveau, invite);
           }
           case 5 -> {
               perso_Lucy invite = Formation.creerInvite(perso_Lucy::new, 54, 4800, 750, 400, 240);
               yield lancerStageAvecInvite(ctx, stage, estNouveau, invite);
           }
           case 6 -> {
               perso_Erza invite = Formation.creerInvite(perso_Erza::new, 55, 5500, 850, 750, 210);
               yield lancerStageAvecInvite(ctx, stage, estNouveau, invite);
           }
           case 7 -> {
               // Combat scripte : Mirajane affronte seule Freed (et ses mobs), l'equipe du joueur n'intervient pas.
               perso_Mirajane mirajane = Formation.creerInvite(perso_Mirajane::new, 56, 7000, 1300, 550, 240);
               yield lancerStageSolo(ctx, stage, estNouveau, mirajane);
           }
           case 8 -> {
               perso_Erza invite = Formation.creerInvite(perso_Erza::new, 57, 5800, 900, 800, 220);
               yield lancerStageAvecInvite(ctx, stage, estNouveau, invite);
           }
           case 9 -> {
               // Combat scripte 1 vs 1 : Mistgun (Jellal infiltre sous un autre nom) affronte seul Luxus.
               perso_jellal mistgun = Formation.creerInvite(perso_jellal::new, 58, 6500, 1400, 500, 270);
               mistgun.setNom("Mistgun");
               yield lancerStageSolo(ctx, stage, estNouveau, mistgun);
           }
           case 10 -> {
               perso_Natsu natsu     = Formation.creerInvite(perso_Natsu::new, 59, 5000, 1050, 480, 230);
               perso_Gajeel gadjeel  = Formation.creerInvite(perso_Gajeel::new, 59, 5200, 1000, 520, 200);
               yield lancerStageAvecDeuxInvites(ctx, stage, estNouveau, natsu, gadjeel);
           }
            default -> stage.lancer(ctx,ctx.formation.getEquipe(),estNouveau);
       };

        if (resultatStage.victoire) {
            stagesReussis[numero] = true;
            if (numero < NB_STAGES) {
                stagesDebloques[numero + 1] = true;
                System.out.println(">> Stage " + (numero + 1) + " debloque !");
            } else {
                System.out.println(">> Felicitations ! Chapitre 5 termine !");
            }

            ctx.gestionnaireQuetes.notifierOrGagne(stage.getRecompenseOr());
            ctx.gestionnaireQuetes.notifierStageFini(5, numero, false,
                    ctx.joueur, ctx.menuRecrutement, ctx.personnagesRecruites);
            ctx.gestionnaireEtoiles.mettreAJour(5, numero, false,
                    resultatStage.victoire, resultatStage.sansAllieMort, resultatStage.enMoinsDe10Tours);
        }
        return resultatStage;
    }

    // TODO : remplacer par les vrais ennemis de chaque stage, ex :
    // e.add(new EnnemiXxx(niveau));
    private Stage construireStage(int numero) {
        ArrayList<PersonnageBase> e = new ArrayList<>();

        return switch (numero) {
            case 1  -> {
                e.add(new EnnemiMage7DPS(Variante.CHAPITRE_5,50));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_5, 50));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_5, 50));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_5, 50));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_5, 50));
                yield new Stage(1,  "Le début de la bataille de Fairy Tail",  7200, 90, e);
              }
            
            case 2  ->{
                e.add(new EnnemiMage5Tank(Variante.CHAPITRE_5,51));
                e.add (new EnnemiMage1DPS(Variante.CHAPITRE_5,51));
                e.add (new EnnemiMage2DPS(Variante.CHAPITRE_5,51));
                e.add (new EnnemiMage8DPS(Variante.CHAPITRE_5,51));
                e.add(new EnnemiMage6Debuff(Variante.CHAPITRE_5,51));
                
                
              yield new Stage(2,  "En route pour sauver les femmes de Fairy Tail",  7500, 94, e);
                
            }
            case 3  -> {
                e.add(new EnnemiEvergreen(52));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_5, 52));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_5, 52));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_5, 52));
                yield new Stage(3, "Elfman vs Evergreen", 7800, 98, e);
            }
            case 4  -> {
                e.add(new EnnemiBixrow(53));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_5, 53));
                e.add(new EnnemiMage5Tank(Variante.CHAPITRE_5, 53));
                e.add(new EnnemiMage6Debuff(Variante.CHAPITRE_5, 53));
                yield new Stage(4, "Gray vs Bixrow", 8100, 102, e);
            }
            case 5  -> {
                e.add(new EnnemiBixrow(54));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_5, 54));
                e.add(new EnnemiMage4Buff(Variante.CHAPITRE_5, 54));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_5, 54));
                yield new Stage(5, "Lucy vs Bixrow", 8400, 106, e);
            }
            case 6  -> {
                e.add(new EnnemiEvergreen(55));
                e.add(new EnnemiMage7DPS(Variante.CHAPITRE_5, 55));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_5, 55));
                e.add(new EnnemiMage5Tank(Variante.CHAPITRE_5, 55));
                yield new Stage(6, "Erza contre Evergreen", 8700, 110, e);
            }
            case 7  -> {
                e.add(new EnnemiFreed(56));
                e.add(new EnnemiMage8DPS(Variante.CHAPITRE_5, 56));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_5, 56));
                yield new Stage(7, "Mirajane vs Freed", 9000, 114, e);
            }
            case 8  -> {
                e.add(new EnnemiLuxus(57));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_5, 57));
                e.add(new EnnemiMage4Buff(Variante.CHAPITRE_5, 57));
                e.add(new EnnemiMage6Debuff(Variante.CHAPITRE_5, 57));
                yield new Stage(8, "Erza vs Luxus", 9300, 118, e);
            }
            case 9  -> {
                // Combat scripte 1 vs 1 : pas de mobs generiques.
                e.add(new EnnemiLuxus(58));
                yield new Stage(9, "Mistgun vs Luxus", 9600, 122, e);
            }
            case 10 -> {
                e.add(new EnnemiLuxus(59));
                e.add(new EnnemiMage7DPS(Variante.CHAPITRE_5, 59));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_5, 59));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_5, 59));
                e.add(new EnnemiMage6Debuff(Variante.CHAPITRE_5, 59));
                yield new Stage(10, "Natsu et Gadjeel vs Luxus", 10000, 130, e);
            }
            default -> new Stage(numero, "???", 0, 0, e);
        };
    }

    public String getTitreStage(int numero) {
        return switch (numero) {
            case 1  -> "Le début de la bataille de Fairy Tail";
            case 2  -> "En route pour sauver les femmes de Fairy Tail";
            case 3  -> "Elfman vs Evergreen";
            case 4  -> "Gray vs Bixrow";
            case 5  -> "Lucy vs Bixrow";
            case 6  -> "Erza contre Evergreen";
            case 7  -> "Mirajane vs Freed";
            case 8  -> "Erza vs Luxus";
            case 9  -> "Mistgun vs Luxus";
            case 10 -> "Natsu et Gadjeel vs Luxus";
            default -> "???";
        };
    }

    public String getNomChapitre() { return "La bataille de Fairy Tail"; }

     private Stage.ResultatStage lancerStageAvecInvite(GameContext ctx, Stage stage, boolean estNouveau, PersonnageBase invite) {
         EquipementFactory.equiperSetStandard(invite, EquipementFactory.rareteEnnemiPourNiveau(invite.getNiveau()));

        ArrayList<PersonnageBase> equipe = ctx.formation.getEquipe();
        Formation.ajouterInviteTemporaire(equipe, invite);

        System.out.println(">> " + invite.getNom() + " rejoint votre equipe pour ce combat !");
        Stage.ResultatStage resultat = stage.lancer(ctx, equipe, estNouveau);
        System.out.println(">> " + invite.getNom() + " quitte l'equipe apres le combat.");
        return resultat;
     }
     
     private Stage.ResultatStage lancerStageAvecDeuxInvites(GameContext ctx, Stage stage, boolean estNouveau,
                                                            PersonnageBase invite1, PersonnageBase invite2) {
        EquipementFactory.equiperSetStandard(invite1, EquipementFactory.rareteEnnemiPourNiveau(invite1.getNiveau()));
        EquipementFactory.equiperSetStandard(invite2, EquipementFactory.rareteEnnemiPourNiveau(invite2.getNiveau()));

        ArrayList<PersonnageBase> equipe = ctx.formation.getEquipe();
        Formation.ajouterInviteTemporaire(equipe, invite1);
        Formation.ajouterInviteTemporaire(equipe, invite2);

        System.out.println(">> " + invite1.getNom() + " et " + invite2.getNom() + " rejoignent votre equipe pour ce combat !");
        Stage.ResultatStage resultat = stage.lancer(ctx, equipe, estNouveau);
        System.out.println(">> " + invite1.getNom() + " et " + invite2.getNom() + " quittent l'equipe apres le combat.");
        return resultat;
    }

    /**
     * Combat scripte en solo : remplace entierement la formation du joueur par un
     * seul personnage calibre (Mirajane vs Freed, Mistgun vs Luxus), sur le modele
     * de Chapitre2.lancerStage9AvecUl.
     */
    private Stage.ResultatStage lancerStageSolo(GameContext ctx, Stage stage, boolean estNouveau, PersonnageBase solo) {
        EquipementFactory.equiperSetStandard(solo, EquipementFactory.rareteEnnemiPourNiveau(solo.getNiveau()));

        ArrayList<PersonnageBase> equipeFixe = new ArrayList<>();
        equipeFixe.add(solo);

        System.out.println(">> " + solo.getNom() + " affronte seul(e) cet ennemi !");
        return stage.lancer(ctx, equipeFixe, estNouveau);
    }

    public boolean[] getStagesDebloques() { return stagesDebloques; }
    public boolean[] getStagesReussis()   { return stagesReussis; }
    public void setStagesDebloques(boolean[] d) { for (int i = 0; i <= NB_STAGES; i++) stagesDebloques[i] = d[i]; }
    public void setStagesReussis(boolean[] r)   { for (int i = 0; i <= NB_STAGES; i++) stagesReussis[i]   = r[i]; }
}
