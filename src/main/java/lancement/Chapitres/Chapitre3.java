package lancement.Chapitres;

import Personnage.PersonnageBase;
import Personnage.pnj.Chapitre3.*;
import Personnage.FairyTail.perso_Natsu;
import Personnage.FairyTail.perso_Elfman;
import Personnage.FairyTail.perso_Gray;
import Personnage.FairyTail.perso_Erza;
import lancement.GameContext;
import lancement.Formation;
import lancement.Stage;
import java.util.ArrayList;
import java.util.Scanner;

public class Chapitre3 {

    private static final int NB_STAGES = 10;
    private final boolean[] stagesDebloques = new boolean[NB_STAGES + 1];
    private final boolean[] stagesReussis   = new boolean[NB_STAGES + 1];

    public Chapitre3() {
        stagesDebloques[1] = true;
    }

    public void afficher(GameContext ctx, Scanner scanner) {
        boolean retour = false;

        while (!retour) {
            ctx.gestionnaireEnergie.mettreAJourRecharge();

            System.out.println("\n========================================");
            System.out.println("   CHAPITRE 3 — Phantom Lord");
            System.out.println("========================================");
            System.out.println("Or : " + String.format("%.0f", ctx.joueur.getOr())
                    + "  |  " + ctx.gestionnaireEnergie.afficherEnergie());
            System.out.println();

            for (int i = 1; i <= NB_STAGES; i++) {
                String etat = stagesReussis[i] ? "[OK]  " : stagesDebloques[i] ? "[  ]  " : "[###] ";
                String etoiles = ctx.gestionnaireEtoiles.getEtoiles(3, i, false).afficher();
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
            } else {
                lancerStage(ctx, choix);
            }
        }
    }

    /**
     * Lance le stage donne (avec les invites temporaires stages 2/3/4/5/6/7, et le combat
     * scripte du stage 9) et applique les recompenses en cas de victoire. Suppose que le
     * stage est deja debloque. Reutilisable par la console et l'interface graphique.
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
                invite.setNiveau(24);
                invite.setVie(1300);
                invite.setVieMax(1300);
                invite.setAttaque(260);
                invite.setDefense(150);
                invite.setVitesse(150);
                yield lancerStageAvecInvite(ctx, stage, estNouveau, invite);
            }
            case 3 -> {
                perso_Elfman invite = new perso_Elfman();
                invite.setNiveau(26);
                invite.setVie(1900);
                invite.setVieMax(1900);
                invite.setAttaque(180);
                invite.setDefense(260);
                invite.setVitesse(110);
                yield lancerStageAvecInvite(ctx, stage, estNouveau, invite);
            }
            case 4 -> {
                perso_Gray invite = new perso_Gray();
                invite.setNiveau(28);
                invite.setVie(1450);
                invite.setVieMax(1450);
                invite.setAttaque(270);
                invite.setDefense(170);
                invite.setVitesse(140);
                yield lancerStageAvecInvite(ctx, stage, estNouveau, invite);
            }
            case 5 -> {
                perso_Natsu invite = new perso_Natsu();
                invite.setNiveau(30);
                invite.setVie(1600);
                invite.setVieMax(1600);
                invite.setAttaque(300);
                invite.setDefense(180);
                invite.setVitesse(160);
                yield lancerStageAvecInvite(ctx, stage, estNouveau, invite);
            }
            case 6, 7 -> {
                perso_Erza invite = new perso_Erza();
                invite.setNiveau(32);
                invite.setVie(2100);
                invite.setVieMax(2100);
                invite.setAttaque(320);
                invite.setDefense(280);
                invite.setVitesse(150);
                yield lancerStageAvecInvite(ctx, stage, estNouveau, invite);
            }
            case 9 -> lancerStage9AvecMakarov(ctx, stage, estNouveau);
            default -> stage.lancer(ctx, ctx.formation.getEquipe(), estNouveau);
        };

        if (resultatStage.victoire) {
            stagesReussis[numero] = true;
            if (numero < NB_STAGES) {
                stagesDebloques[numero + 1] = true;
                System.out.println(">> Stage " + (numero + 1) + " debloque !");
            } else {
                System.out.println(">> Félicitations ! Vous avez vaincu Phantom Lord !");
            }

            ctx.gestionnaireQuetes.notifierOrGagne(stage.getRecompenseOr());
            ctx.gestionnaireQuetes.notifierStageFini(3, numero, false,
                    ctx.joueur, ctx.menuRecrutement, ctx.personnagesRecruites);
            ctx.gestionnaireEtoiles.mettreAJour(3, numero, false,
                    resultatStage.victoire, resultatStage.sansAllieMort, resultatStage.enMoinsDe10Tours);
        }
        return resultatStage;
    }

    private Stage construireStage(int numero) {
        ArrayList<PersonnageBase> e = new ArrayList<>();
        // Recompenses tres faibles : la montee de niveau passe surtout par les quetes,
        // mais un peu plus d'XP qu'au Chapitre 2 pour suivre la progression des chapitres.
        switch (numero) {

            // Stage 1 — Avant-garde Phantom Lord (combat generique)
            case 1 -> {
                e.add(new EnnemiMage1DPS(22));
                e.add(new EnnemiMage8DPS(22));
                e.add(new EnnemiMage2DPS(22));
                e.add(new EnnemiMage8DPS(21));
                e.add(new EnnemiMage1DPS(21));
                return new Stage(1, "L'assaut de Phantom Lord", 300, 22, e);
            }

            // Stage 2 — Natsu contre Totomaru + ennemis generiques
            case 2 -> {
                e.add(new EnnemiTotomaru(25));
                e.add(new EnnemiMage2DPS(24));
                e.add(new EnnemiMage9Tank(24));
                e.add(new EnnemiMage1DPS(23));
                e.add(new EnnemiMage8DPS(23));
                return new Stage(2, "Natsu contre Totomaru — Sept Flammes", 375, 25, e);
            }

            // Stage 3 — Elfman contre Sol + ennemis generiques
            case 3 -> {
                e.add(new EnnemiSol(27));
                e.add(new EnnemiMage8DPS(25));
                e.add(new EnnemiMage5Tank(25));
                e.add(new EnnemiMage2DPS(24));
                e.add(new EnnemiMage3Soigneur(24));
                return new Stage(3, "Elfman contre Sol — L'Impénétrable", 475, 28, e);
            }

            // Stage 4 — Gray contre Jubia + ennemis generiques
            case 4 -> {
                e.add(new EnnemiJubia_4elements(29));
                e.add(new EnnemiMage2DPS(27));
                e.add(new EnnemiMage2DPS(26));
                e.add(new EnnemiMage9Tank(25));
                e.add(new EnnemiMage3Soigneur(25));
                return new Stage(4, "Gray contre Jubia — L'Eau qui emprisonne", 575, 31, e);
            }

            // Stage 5 — Natsu contre Gadjeel + ennemis generiques
            case 5 -> {
                e.add(new EnnemiGadjeel(30));
                e.add(new EnnemiMage3Soigneur(27));
                e.add(new EnnemiMage9Tank(26));
                e.add(new EnnemiMage2DPS(26));
                e.add(new EnnemiMage3Soigneur(25));
                return new Stage(5, "Natsu contre Gadjeel — Le Dragon d'Acier", 675, 35, e);
            }

            // Stage 6 — Erza contre Aria + ennemis generiques
            case 6 -> {
                e.add(new EnnemiAria(32));
                e.add(new EnnemiMage9Tank(30));
                e.add(new EnnemiMage3Soigneur(29));
                e.add(new EnnemiMage2DPS(29));
                e.add(new EnnemiMage6Debuff(28));
                return new Stage(6, "Erza contre Aria — Magie du Ciel Vide", 800, 39, e);
            }

            // Stage 7 — Erza contre José + ennemis generiques
            case 7 -> {
                e.add(new EnnemiJose(34));
                e.add(new EnnemiMage9Tank(31));
                e.add(new EnnemiMage3Soigneur(31));
                e.add(new EnnemiMage2DPS(30));
                e.add(new EnnemiMage8DPS(30));
                return new Stage(7, "Erza contre José — L'Ombre s'éveille", 950, 43, e);
            }

            // Stage 8 — Notre equipe seule contre José (sans invite, plus fort)
            case 8 -> {
                e.add(new EnnemiJose(38));
                return new Stage(8, "José Pora — Seul face à Phantom Lord", 1125, 48, e);
            }

            // Stage 9 — Combat scripte : Makarov contre José, notre formation n'intervient pas
            case 9 -> {
                e.add(new EnnemiJose(55));
                return new Stage(9, "Makarov contre José — La Loi des Fées", 1350, 54, e);
            }

            // Stage 10 — Notre equipe seule contre Aria (sans invite, dernier rempart)
            case 10 -> {
                e.add(new EnnemiAria(40));
                return new Stage(10, "Aria — Le Dernier Rempart", 1700, 60, e);
            }

            default -> { return new Stage(numero, "???", 0, 0, e); }
        }
    }

    public String getTitreStage(int numero) {
        return switch (numero) {
            case 1  -> "L'assaut de Phantom Lord";
            case 2  -> "Natsu contre Totomaru — Sept Flammes";
            case 3  -> "Elfman contre Sol — L'Impénétrable";
            case 4  -> "Gray contre Jubia — L'Eau qui emprisonne";
            case 5  -> "Natsu contre Gadjeel — Le Dragon d'Acier";
            case 6  -> "Erza contre Aria — Magie du Ciel Vide";
            case 7  -> "Erza contre José — L'Ombre s'éveille";
            case 8  -> "José Pora — Seul face à Phantom Lord";
            case 9  -> "Makarov contre José — La Loi des Fées";
            case 10 -> "Aria — Le Dernier Rempart";
            default -> "???";
        };
    }

    public boolean[] getStagesDebloques() { return stagesDebloques; }
    public boolean[] getStagesReussis()   { return stagesReussis; }
    public void setStagesDebloques(boolean[] d) { for (int i = 0; i <= NB_STAGES; i++) stagesDebloques[i] = d[i]; }
    public void setStagesReussis(boolean[] r)   { for (int i = 0; i <= NB_STAGES; i++) stagesReussis[i]   = r[i]; }

    // ── Invites temporaires (stages 2 a 7) ─────────────────────────────────

    /**
     * Injecte un invite deja configure (niveau/stats) dans l'equipe pour la duree
     * d'un seul combat, en respectant les regles de formation (1 Tank max, 3 DPS
     * max joueur compris, 3 Support max, 5 membres max), puis lance le stage.
     */
    private Stage.ResultatStage lancerStageAvecInvite(GameContext ctx, Stage stage, boolean estNouveau, PersonnageBase invite) {
        ArrayList<PersonnageBase> equipe = ctx.formation.getEquipe();
        Formation.ajouterInviteTemporaire(equipe, invite);

        System.out.println(">> " + invite.getNom() + " rejoint votre equipe pour ce combat !");
        Stage.ResultatStage resultat = stage.lancer(ctx, equipe, estNouveau);
        System.out.println(">> " + invite.getNom() + " quitte l'equipe apres le combat.");
        return resultat;
    }

    private Stage.ResultatStage lancerStage9AvecMakarov(GameContext ctx, Stage stage, boolean estNouveau) {
        // Combat scripte : notre formation n'intervient pas, seul Makarov affronte José.
        EnnemiMakarov makarov = new EnnemiMakarov();

        ArrayList<PersonnageBase> equipeFixe = new ArrayList<>();
        equipeFixe.add(makarov);

        System.out.println(">> Makarov arrive seul face a José !");
        System.out.println("   « Tu as touche a mes enfants... Tu vas le regretter. » — Makarov\n");

        Stage.ResultatStage resultat = stage.lancer(ctx, equipeFixe, estNouveau);

        System.out.println("\n>> La Loi des Fées s'abat sur José Pora, mettant fin au regne de Phantom Lord.");

        return resultat;
    }
}
