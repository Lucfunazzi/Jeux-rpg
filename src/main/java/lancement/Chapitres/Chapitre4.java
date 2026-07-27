package lancement.Chapitres;

import Equipement.EquipementFactory;
import Personnage.PersonnageBase;
import Personnage.pnj.Chapitre4.*;
import Personnage.FairyTail.perso_Natsu;
import Personnage.FairyTail.perso_Gray;
import Personnage.FairyTail.perso_Lucy;
import Personnage.FairyTail.perso_Erza;
import Personnage.FairyTail.perso_Jubia_4elements;
import Personnage.FairyTail.perso_Simon;
import Personnage.FairyTail.perso_Shaw;
import lancement.GameContext;
import lancement.Formation;
import lancement.Stage;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Chapitre 4 — La Tour du Paradis. Une embuscade reunit d'emblee Wolly, Miliana, Shaw et
 * Simon (encore sous l'emprise de Jellal) contre l'equipe, avant que Shaw ne soit libere lors
 * de la delivrance d'Erza puis que Simon ne rejoigne la cause. Ikagura puis Ikaruga gardent
 * l'acces final avant l'affrontement avec Jellal.
 */
public class Chapitre4 {

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
            System.out.println("   CHAPITRE 4 — La Tour du Paradis");
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
     * Lance le stage donne (avec les invites temporaires stages 2 a 8, et le duel scripte
     * du stage 9) et applique les recompenses en cas de victoire. Suppose que le stage est
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
            case 2 -> {
                perso_Natsu invite = new perso_Natsu();
                invite.setNiveau(46);
                invite.setVie(2200);
                invite.setVieMax(2200);
                invite.setAttaque(335);
                invite.setDefense(230);
                invite.setVitesse(165);
                yield lancerStageAvecInvite(ctx, stage, estNouveau, invite);
            }
            case 3 -> {
                perso_Gray invite = new perso_Gray();
                invite.setNiveau(47);
                invite.setVie(2300);
                invite.setVieMax(2300);
                invite.setAttaque(345);
                invite.setDefense(260);
                invite.setVitesse(160);
                yield lancerStageAvecInvite(ctx, stage, estNouveau, invite);
            }
            case 4 -> {
                perso_Lucy invite = new perso_Lucy();
                invite.setNiveau(47);
                invite.setVie(2200);
                invite.setVieMax(2200);
                invite.setAttaque(355);
                invite.setDefense(210);
                invite.setVitesse(170);
                yield lancerStageAvecInvite(ctx, stage, estNouveau, invite);
            }
            case 5 -> {
                // Shaw vient de se liberer de l'emprise de Jellal : il combat desormais a nos cotes.
                perso_Shaw invite = new perso_Shaw();
                invite.setNiveau(48);
                invite.setVie(2350);
                invite.setVieMax(2350);
                invite.setAttaque(365);
                invite.setDefense(235);
                invite.setVitesse(175);
                yield lancerStageAvecInvite(ctx, stage, estNouveau, invite);
            }
            case 6 -> {
                perso_Erza invite = new perso_Erza();
                invite.setNiveau(48);
                invite.setVie(2500);
                invite.setVieMax(2500);
                invite.setAttaque(375);
                invite.setDefense(300);
                invite.setVitesse(170);
                yield lancerStageAvecInvite(ctx, stage, estNouveau, invite);
            }
            case 7 -> {
                perso_Jubia_4elements invite = new perso_Jubia_4elements();
                invite.setNiveau(49);
                invite.setVie(2400);
                invite.setVieMax(2400);
                invite.setAttaque(365);
                invite.setDefense(250);
                invite.setVitesse(172);
                yield lancerStageAvecInvite(ctx, stage, estNouveau, invite);
            }
            case 8 -> {
                // Simon vient de devoiler sa veritable allegeance : il combat desormais a nos cotes.
                perso_Simon invite = new perso_Simon();
                invite.setNiveau(49);
                invite.setVie(2600);
                invite.setVieMax(2600);
                invite.setAttaque(385);
                invite.setDefense(330);
                invite.setVitesse(170);
                yield lancerStageAvecInvite(ctx, stage, estNouveau, invite);
            }
            case 9 -> lancerStage9AvecErza(ctx, stage, estNouveau);
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
                e.add(new EnnemiMage5Tank(46));
                return new Stage(1, "Embuscade dans le casino", 1900, 62, e);
            }

            // Stage 2 — gardes generiques
            case 2 -> {
                e.add(new EnnemiMage7DPS(46));
                e.add(new EnnemiMage2DPS(45));
                e.add(new EnnemiMage9Tank(45));
                e.add(new EnnemiMage1DPS(45));
                e.add(new EnnemiMage3Soigneur(45));
                return new Stage(2, "Infiltration dans la tour du paradis", 2150, 64, e);
            }

            // Stage 3 — Natsu vs Wolly + Miliana + gardes generiques
            case 3 -> {
                e.add(new EnnemiMiliana(47));
                e.add(new EnnemiWolly(46));
                e.add(new EnnemiMage6Debuff(46));
                e.add(new EnnemiMage5Tank(46));
                e.add(new EnnemiMage3Soigneur(45));
                return new Stage(3, "Miaou, Il faut sauver Happy", 2400, 66, e);
            }

            // Stage 4 — Erza contre Shaw (encore sous emprise) + gardes generiques
            case 4 -> {
                e.add(new EnnemiShaw(47));
                e.add(new EnnemiMage2DPS(46));
                e.add(new EnnemiMage2DPS(46));
                e.add(new EnnemiMage9Tank(46));
                e.add(new EnnemiMage3Soigneur(46));
                return new Stage(4, "Libération d'erza", 2700, 68, e);
            }

            // Stage 5 —  Lucy + jubia vs Vivaldus + gardes generiques
            case 5 -> {
                e.add(new EnnemiVivaldus(48));
                e.add(new EnnemiMage3Soigneur(47));
                e.add(new EnnemiMage9Tank(47));
                e.add(new EnnemiMage2DPS(47));
                e.add(new EnnemiMage3Soigneur(46));
                return new Stage(5, "Les esprits et l'eau", 3050, 70, e);
            }

            // Stage 6 — Natsu et Simon contre Owl + gardes generiques
            case 6 -> {
                e.add(new EnnemiOwl(48));
                e.add(new EnnemiMage9Tank(47));
                e.add(new EnnemiMage3Soigneur(47));
                e.add(new EnnemiMage2DPS(47));
                e.add(new EnnemiMage6Debuff(47));
                return new Stage(6, "Le hiboux assasin", 3450, 72, e);
            }

            // Stage 7 — Erza + Shaw vs Ikaruga + gardes generiques
            case 7 -> {
                e.add(new EnnemiIkaruga(47));
                e.add(new EnnemiMage9Tank(48));
                e.add(new EnnemiMage3Soigneur(48));
                e.add(new EnnemiMage2DPS(48));
                e.add(new EnnemiMage8DPS(48));
                return new Stage(7, "Epée contre Epée", 3900, 74, e);
            }

            // Stage 8 — Ennemi générique
            case 8 -> {
                e.add(new EnnemiIkaruga(49));
                return new Stage(8, "Ikaruga — L'Epee du Ciel", 4400, 77, e);
            }

            // Stage 9 — Simon + erza contre jellal + ennemi génerique
            case 9 -> {
                e.add(new EnnemiJellal(51));
                return new Stage(9, "Erza contre Jellal — Le Passe Ressurgit", 4950, 80, e);
            }

            // Stage 10 — Natsu etherion vs jellal + ennemis générique
            case 10 -> {
                e.add(new EnnemiJellal(50));
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
            case 8  -> "Ikaruga — L'Epee du Ciel";
            case 9  -> "Erza contre Jellal — Le Passe Ressurgit";
            case 10 -> "Jellal — L'Effondrement de la Tour du Paradis";
            default -> "???";
        };
    }

    public boolean[] getStagesDebloques() { return stagesDebloques; }
    public boolean[] getStagesReussis()   { return stagesReussis; }
    public void setStagesDebloques(boolean[] d) { for (int i = 0; i <= NB_STAGES; i++) stagesDebloques[i] = d[i]; }
    public void setStagesReussis(boolean[] r)   { for (int i = 0; i <= NB_STAGES; i++) stagesReussis[i]   = r[i]; }

    // ── Invites temporaires (stages 2 a 8) ─────────────────────────────────

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

    private Stage.ResultatStage lancerStage9AvecErza(GameContext ctx, Stage stage, boolean estNouveau) {
        // Combat scripte : notre formation n'intervient pas, seule Erza affronte Jellal.
        perso_Erza erza = new perso_Erza();
        erza.setNiveau(51);
        erza.setVie(2900);
        erza.setVieMax(2900);
        erza.setAttaque(420);
        erza.setDefense(350);
        erza.setVitesse(195);
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
