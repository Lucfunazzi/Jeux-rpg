package lancement.Chapitres;

import Equipement.EquipementFactory;
import Personnage.PersonnageBase;
import Personnage.pnj.Chapitre4.*;
import Personnage.pnj.EnnemisGeneriques.*;
import Personnage.FairyTail.perso_Natsu;
import Personnage.FairyTail.perso_Gray;
import Personnage.FairyTail.perso_Lucy;
import Personnage.FairyTail.perso_Erza;
import Personnage.FairyTail.perso_Jubia_4elements;
import Personnage.FairyTail.perso_Simon;
import Personnage.FairyTail.perso_Shaw;
import Personnage.FairyTail.perso_Natsu_Etherion;
import lancement.GameContext;
import lancement.Formation;
import lancement.Stage;
import java.util.ArrayList;
import java.util.Scanner;


public class Chapitre4 implements Chapitre {

    private static final int NB_STAGES = 10;
    private final boolean[] stagesDebloques = new boolean[NB_STAGES + 1];
    private final boolean[] stagesReussis   = new boolean[NB_STAGES + 1];

    public Chapitre4() {
        stagesDebloques[1] = true;
    }

    public void afficher(GameContext ctx, Scanner scanner) {
        boolean retour = false;

        while (!retour) {
            ctx.gestionnaireEnergie.mettreAJourRecharge();

            System.out.println("\n========================================");
            System.out.println("   CHAPITRE 4 — " + getNomChapitre());
            System.out.println("========================================");
            System.out.println("Or : " + String.format("%.0f", ctx.joueur.getOr())
                    + "  |  " + ctx.gestionnaireEnergie.afficherEnergie());
            System.out.println();

            for (int i = 1; i <= NB_STAGES; i++) {
                boolean jouable = ctx.gestionnaireQuetes.estStageJouable(4, i, false);
                String etat = stagesReussis[i] ? "[OK]  "
                        : !stagesDebloques[i]  ? "[###] "
                        : jouable              ? "[  ]  "
                        :                        "[QUETE] ";
                String etoiles = ctx.gestionnaireEtoiles.getEtoiles(4, i, false).afficher();
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
            } else if (!ctx.gestionnaireQuetes.estStageJouable(4, choix, false)) {
                System.out.println("Acceptez d'abord la quete associee a ce stage (menu Quetes).");
            } else {
                lancerStage(ctx, choix);
            }
        }
    }

    /**
     * Lance le stage donne (avec les invites temporaires stages 3 a 7, 9 et 10, et le duel
     * scripte du stage 8) et applique les recompenses en cas de victoire. Suppose que le stage est
     * deja debloque. Reutilisable par la console et l'interface graphique.
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
            case 1 -> lancerStage1AvecEquipeHeros(ctx, stage, estNouveau);
            // Stage 2 : pas d'invite, l'equipe du joueur affronte seule les gardes generiques.
            case 3 -> {
                perso_Natsu invite = Formation.creerInvite(perso_Natsu::new, 47, 3600, 1050, 480, 210);
                yield lancerStageAvecInvite(ctx, stage, estNouveau, invite);
            }
            case 4 -> {
                perso_Erza invite = Formation.creerInvite(perso_Erza::new, 47, 4200, 950, 650, 190);
                yield lancerStageAvecInvite(ctx, stage, estNouveau, invite);
            }
            case 5 -> {
                perso_Lucy lucy = Formation.creerInvite(perso_Lucy::new, 48, 3300, 650, 420, 200);

                perso_Jubia_4elements jubia = Formation.creerInvite(perso_Jubia_4elements::new, 48, 3500, 750, 500, 198);

                yield lancerStageAvecDeuxInvites(ctx, stage, estNouveau, lucy, jubia);
            }
            case 6 -> {
                perso_Natsu natsu = Formation.creerInvite(perso_Natsu::new, 48, 3600, 1050, 480, 210);

                // Simon vient de devoiler sa veritable allegeance : il combat desormais a nos cotes.
                perso_Simon simon = Formation.creerInvite(perso_Simon::new, 48, 4000, 700, 900, 170);

                yield lancerStageAvecDeuxInvites(ctx, stage, estNouveau, natsu, simon);
            }
            case 7 -> {
                perso_Erza erza = Formation.creerInvite(perso_Erza::new, 49, 4200, 950, 650, 190);

                perso_Shaw shaw = Formation.creerInvite(perso_Shaw::new, 49, 3600, 800, 550, 200);

                yield lancerStageAvecDeuxInvites(ctx, stage, estNouveau, erza, shaw);
            }
            case 8 -> lancerStage8AvecErza(ctx, stage, estNouveau);
            case 9 -> {
                // Simon (desormais alliee) prete main forte a l'equipe pour l'assaut sur Jellal.
                perso_Simon invite = Formation.creerInvite(perso_Simon::new, 49, 4000, 700, 900, 170);
                yield lancerStageAvecInvite(ctx, stage, estNouveau, invite);
            }
            case 10 -> {
                perso_Natsu_Etherion invite = Formation.creerInvite(perso_Natsu_Etherion::new, 51, 4800, 1400, 650, 230);
                yield lancerStageAvecInvite(ctx, stage, estNouveau, invite);
            }
            default -> stage.lancer(ctx, ctx.formation.getEquipe(), estNouveau);
        };

        if (resultatStage.victoire) {
            stagesReussis[numero] = true;
            if (numero < NB_STAGES) {
                stagesDebloques[numero + 1] = true;
                System.out.println(">> Stage " + (numero + 1) + " debloque !");
            } else {
                System.out.println(">> Felicitations ! La Tour du Paradis s'est effondree !");
            }

            ctx.gestionnaireQuetes.notifierOrGagne(stage.getRecompenseOr());
            ctx.gestionnaireQuetes.notifierStageFini(4, numero, false,
                    ctx.joueur, ctx.menuRecrutement, ctx.personnagesRecruites);
            ctx.gestionnaireEtoiles.mettreAJour(4, numero, false,
                    resultatStage.victoire, resultatStage.sansAllieMort, resultatStage.enMoinsDe10Tours);
        }
        return resultatStage;
    }

    private Stage construireStage(int numero) {
        ArrayList<PersonnageBase> e = new ArrayList<>();
        
        switch (numero) {

            // Stage 1 — Infiltration de la Tour du Paradis (combat natsu,gray,jubia,lucy et erza contre wolly miliana shaw et simon)
            case 1 -> {
                e.add(new EnnemiWolly(45));
                e.add(new EnnemiMiliana(45));
                e.add(new EnnemiShaw(45));
                e.add(new EnnemiSimon(46));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_4, 46));
                return new Stage(1, "Embuscade dans le casino", 1900, 62, e);
            }

            // Stage 2 — gardes generiques
            case 2 -> {
                e.add(new EnnemiMage7DPS(Variante.CHAPITRE_4, 46));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_4, 45));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_4, 45));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_4, 45));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_4, 45));
                return new Stage(2, "Infiltration dans la tour du paradis", 2150, 64, e);
            }

            // Stage 3 — Natsu vs Wolly + Miliana + gardes generiques
            case 3 -> {
                e.add(new EnnemiMiliana(47));
                e.add(new EnnemiWolly(46));
                e.add(new EnnemiMage6Debuff(Variante.CHAPITRE_4, 46));
                e.add(new EnnemiMage5Tank(Variante.CHAPITRE_4, 46));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_4, 45));
                return new Stage(3, "Miaou, Il faut sauver Happy", 2400, 66, e);
            }

            // Stage 4 — Erza contre Shaw (encore sous emprise) + gardes generiques
            case 4 -> {
                e.add(new EnnemiShaw(47));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_4, 46));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_4, 46));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_4, 46));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_4, 46));
                return new Stage(4, "Libération d'erza", 2700, 68, e);
            }

            // Stage 5 —  Lucy + jubia vs Vivaldus + gardes generiques
            case 5 -> {
                e.add(new EnnemiVivaldus(48));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_4, 47));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_4, 47));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_4, 47));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_4, 46));
                return new Stage(5, "Les esprits et l'eau", 3050, 70, e);
            }

            // Stage 6 — Natsu et Simon contre Owl + gardes generiques
            case 6 -> {
                e.add(new EnnemiOwl(48));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_4, 47));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_4, 47));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_4, 47));
                e.add(new EnnemiMage6Debuff(Variante.CHAPITRE_4, 47));
                return new Stage(6, "Le hiboux assasin", 3450, 72, e);
            }

            // Stage 7 — Erza + Shaw vs Ikaruga + gardes generiques
            case 7 -> {
                e.add(new EnnemiIkaruga(47));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_4, 48));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_4, 48));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_4, 48));
                e.add(new EnnemiMage8DPS(Variante.CHAPITRE_4, 48));
                return new Stage(7, "Epée contre Epée", 3900, 74, e);
            }

            // Stage 8 — Combat scripte : Erza seule contre Jellal
            case 8 -> {
                e.add(new EnnemiJellal(51));
                return new Stage(8, "Erza contre Jellal — Le Passe Ressurgit", 4950, 80, e);
            }

            // Stage 9 — Simon (alliee) + equipe contre Jellal + gardes generiques
            case 9 -> {
                e.add(new EnnemiJellal(49));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_4, 48));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_4, 48));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_4, 48));
                return new Stage(9, "Le sacrifice de Simon — L'Assaut sur Jellal", 5200, 82, e);
            }

            // Stage 10 — Natsu Etherion vs Jellal + gardes generiques
            case 10 -> {
                e.add(new EnnemiJellal(52));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_4, 50));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_4, 50));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_4, 50));
                e.add(new EnnemiMage8DPS(Variante.CHAPITRE_4, 50));
                return new Stage(10, "Jellal — L'Effondrement de la Tour du Paradis", 6500, 84, e);
            }

            default -> { return new Stage(numero, "???", 0, 0, e); }
        }
    }

    public String getTitreStage(int numero) {
        return switch (numero) {
            case 1  -> "Embuscade dans le casino";
            case 2  -> "Infiltration dans la tour du paradis";
            case 3  -> "Miaou, Il faut sauver Happy";
            case 4  -> "Libération d'erza";
            case 5  -> "Les esprits et l'eau";
            case 6  -> "Le hiboux assasin";
            case 7  -> "Epée contre Epée";
            case 8  -> "Erza contre Jellal — Le Passe Ressurgit";
            case 9  -> "Le sacrifice de Simon — L'Assaut sur Jellal";
            case 10 -> "Jellal — L'Effondrement de la Tour du Paradis";
            default -> "???";
        };
    }

    public String getNomChapitre() { return "La Tour du Paradis"; }

    public boolean[] getStagesDebloques() { return stagesDebloques; }
    public boolean[] getStagesReussis()   { return stagesReussis; }
    public void setStagesDebloques(boolean[] d) { for (int i = 0; i <= NB_STAGES; i++) stagesDebloques[i] = d[i]; }
    public void setStagesReussis(boolean[] r)   { for (int i = 0; i <= NB_STAGES; i++) stagesReussis[i]   = r[i]; }

    // ── Invites temporaires (stages 3 a 7, 9 et 10) ────────────────────────

    /**
     * Injecte un invite deja configure (niveau/stats) dans l'equipe pour la duree
     * d'un seul combat, en respectant les regles de formation, puis lance le stage.
     */
    private Stage.ResultatStage lancerStageAvecInvite(GameContext ctx, Stage stage, boolean estNouveau, PersonnageBase invite) {
        // Equipement fantome pour l'invite, au meme titre que les ennemis, sinon il se retrouve
        // largement depasse face a des ennemis qui, eux, sont equipes selon leur niveau.
        EquipementFactory.equiperSetStandard(invite, EquipementFactory.rareteEnnemiPourNiveau(invite.getNiveau()));

        ArrayList<PersonnageBase> equipe = ctx.formation.getEquipe();
        Formation.ajouterInviteTemporaire(equipe, invite);

        System.out.println(">> " + invite.getNom() + " rejoint votre equipe pour ce combat !");
        Stage.ResultatStage resultat = stage.lancer(ctx, equipe, estNouveau);
        System.out.println(">> " + invite.getNom() + " quitte l'equipe apres le combat.");
        return resultat;
    }

    /** Variante a deux invites simultanes (stages 5, 6 et 7). */
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

    private Stage.ResultatStage lancerStage1AvecEquipeHeros(GameContext ctx, Stage stage, boolean estNouveau) {
        // Combat scripte : notre formation n'intervient pas, l'embuscade oppose l'equipe
        // partie en mission (Erza, Jubia, Lucy, Gray, Natsu) a Wolly, Miliana, Shaw et Simon.
        perso_Erza erza = Formation.creerInvite(perso_Erza::new, 45, 4200, 950, 650, 190);
        EquipementFactory.equiperSetStandard(erza, EquipementFactory.rareteEnnemiPourNiveau(erza.getNiveau()));

        perso_Jubia_4elements jubia = Formation.creerInvite(perso_Jubia_4elements::new, 45, 3500, 750, 500, 198);
        EquipementFactory.equiperSetStandard(jubia, EquipementFactory.rareteEnnemiPourNiveau(jubia.getNiveau()));

        perso_Lucy lucy = Formation.creerInvite(perso_Lucy::new, 45, 3300, 650, 420, 200);
        EquipementFactory.equiperSetStandard(lucy, EquipementFactory.rareteEnnemiPourNiveau(lucy.getNiveau()));

        perso_Gray gray = Formation.creerInvite(perso_Gray::new, 45, 3700, 1000, 550, 195);
        EquipementFactory.equiperSetStandard(gray, EquipementFactory.rareteEnnemiPourNiveau(gray.getNiveau()));

        perso_Natsu natsu = Formation.creerInvite(perso_Natsu::new, 45, 3600, 1050, 480, 210);
        EquipementFactory.equiperSetStandard(natsu, EquipementFactory.rareteEnnemiPourNiveau(natsu.getNiveau()));

        ArrayList<PersonnageBase> equipeFixe = new ArrayList<>();
        equipeFixe.add(erza);
        equipeFixe.add(jubia);
        equipeFixe.add(lucy);
        equipeFixe.add(gray);
        equipeFixe.add(natsu);

        System.out.println(">> Erza, Jubia, Lucy, Gray et Natsu tombent dans une embuscade au casino de la tour !");

        Stage.ResultatStage resultat = stage.lancer(ctx, equipeFixe, estNouveau);

        System.out.println("\n>> Wolly, Miliana, Shaw et Simon barrent la route de l'equipe...");

        return resultat;
    }

    private Stage.ResultatStage lancerStage8AvecErza(GameContext ctx, Stage stage, boolean estNouveau) {
        // Combat scripte : notre formation n'intervient pas, seule Erza affronte Jellal.
        // Stats relevees pour rester competitives face a Jellal(51) (~6200 PV / 2580 ATK avec la formule).
        perso_Erza erza = Formation.creerInvite(perso_Erza::new, 51, 6800, 2300, 1500, 210);
        EquipementFactory.equiperSetStandard(erza, EquipementFactory.rareteEnnemiPourNiveau(erza.getNiveau()));

        ArrayList<PersonnageBase> equipeFixe = new ArrayList<>();
        equipeFixe.add(erza);

        System.out.println(">> Erza affronte seule Jellal, au sommet de la Tour du Paradis !");
        System.out.println("   « Jellal... reviens-nous. » — Erza\n");

        Stage.ResultatStage resultat = stage.lancer(ctx, equipeFixe, estNouveau);

        System.out.println("\n>> Le duel dechire les vieux souvenirs d'enfance d'Erza et de Jellal.");

        return resultat;
    }
}
