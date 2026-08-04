package lancement.Chapitres;

import Equipement.EquipementFactory;
import Personnage.PersonnageBase;

import Personnage.pnj.Chapitre2.*;
import Personnage.pnj.EnnemisGeneriques.*;
import Personnage.FairyTail.perso_Lucy;
import Personnage.FairyTail.perso_Natsu;
import Personnage.FairyTail.perso_Gray;
import lancement.GameContext;
import lancement.Formation;
import lancement.Stage;
import java.util.ArrayList;
import java.util.Scanner;

public class Chapitre2 implements Chapitre {

    private static final int NB_STAGES = 10;
    private final boolean[] stagesDebloques = new boolean[NB_STAGES + 1];
    private final boolean[] stagesReussis   = new boolean[NB_STAGES + 1];

    public Chapitre2() {
        stagesDebloques[1] = true;
    }

    public void afficher(GameContext ctx, Scanner scanner) {
        boolean retour = false;

        while (!retour) {
            ctx.gestionnaireEnergie.mettreAJourRecharge();

            System.out.println("\n========================================");
            System.out.println("   CHAPITRE 2 — " + getNomChapitre());
            System.out.println("========================================");
            System.out.println("Or : " + String.format("%.0f", ctx.joueur.getOr())
                    + "  |  " + ctx.gestionnaireEnergie.afficherEnergie());
            System.out.println();

            for (int i = 1; i <= NB_STAGES; i++) {
                boolean jouable = ctx.gestionnaireQuetes.estStageJouable(2, i, false);
                String etat = stagesReussis[i] ? "[OK]  "
                        : !stagesDebloques[i]  ? "[###] "
                        : jouable              ? "[  ]  "
                        :                        "[QUETE] ";
                String etoiles = ctx.gestionnaireEtoiles.getEtoiles(2, i, false).afficher();
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
            } else if (!ctx.gestionnaireQuetes.estStageJouable(2, choix, false)) {
                System.out.println("Acceptez d'abord la quete associee a ce stage (menu Quetes).");
            } else {
                lancerStage(ctx, choix);
            }
        }
    }

    /**
     * Lance le stage donne (avec les invites temporaires stage 3/4/5/6/8/9) et applique
     * les recompenses en cas de victoire. Suppose que le stage est deja debloque.
     * Reutilisable par la console et l'interface graphique.
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
            case 3 -> {
                perso_Lucy invite = Formation.creerInvite(perso_Lucy::new, CourbeChapitres.niveauEnnemiPourStage(2, 3), 900, 140, 95, 160);
                yield lancerStageAvecInvite(ctx, stage, estNouveau, invite);
            }
            case 4, 5 -> {
                perso_Natsu invite = Formation.creerInvite(perso_Natsu::new, CourbeChapitres.niveauEnnemiPourStage(2, 4), 1100, 220, 130, 140);
                yield lancerStageAvecInvite(ctx, stage, estNouveau, invite);
            }
            case 6, 8 -> {
                perso_Gray invite = Formation.creerInvite(perso_Gray::new, CourbeChapitres.niveauEnnemiPourStage(2, 6), 950, 210, 140, 120);
                yield lancerStageAvecInvite(ctx, stage, estNouveau, invite);
            }
            case 9  -> lancerStage9AvecUl(ctx, stage, estNouveau);
            default -> stage.lancer(ctx, ctx.formation.getEquipe(), estNouveau);
        };

        if (resultatStage.victoire) {
            stagesReussis[numero] = true;
            if (numero < NB_STAGES) {
                stagesDebloques[numero + 1] = true;
                System.out.println(">> Stage " + (numero + 1) + " debloque !");
            } else {
                System.out.println(">> Félicitations ! Vous avez sauvé l'île de Galuna !");
            }

            ctx.gestionnaireQuetes.notifierOrGagne(stage.getRecompenseOr());
            ctx.gestionnaireQuetes.notifierStageFini(2, numero, false,
                    ctx.joueur, ctx.menuRecrutement, ctx.personnagesRecruites);
            ctx.gestionnaireEtoiles.mettreAJour(2, numero, false,
                    resultatStage.victoire, resultatStage.sansAllieMort, resultatStage.enMoinsDe10Tours);
        }
        return resultatStage;
    }

    private Stage construireStage(int numero) {
        ArrayList<PersonnageBase> ennemis = new ArrayList<>();
        int niveau = CourbeChapitres.niveauEnnemiPourStage(2, numero);
        // Recompenses tres faibles : la montee de niveau passe surtout par les quetes,
        // mais un peu plus d'XP qu'au Chapitre 1 pour suivre la progression des chapitres.
        switch (numero) {
            case 1  -> { ennemis.add(new EnnemiMage1DPS(Variante.CHAPITRE_2, niveau)); ennemis.add(new EnnemiMage1DPS(Variante.CHAPITRE_2, niveau));
                         ennemis.add(new EnnemiMage5Tank(Variante.CHAPITRE_2, niveau)); ennemis.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_2, niveau));
                         ennemis.add(new EnnemiMage2DPS(Variante.CHAPITRE_2, niveau));
                         return new Stage(1, "Prologue Chapitre 2", 75, 8, ennemis); }

            case 2  -> { ennemis.add(new EnnemiMage4Buff(Variante.CHAPITRE_2, niveau)); ennemis.add(new EnnemiMage2DPS(Variante.CHAPITRE_2, niveau));
                        ennemis.add(new EnnemiMage5Tank(Variante.CHAPITRE_2, niveau)); ennemis.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_2, niveau));
                         ennemis.add(new EnnemiMage2DPS(Variante.CHAPITRE_2, niveau));
                         return new Stage(2, "Arrivée a l'ile de galuna", 90, 9, ennemis); }

            case 3  -> { ennemis.add(new EnnemiCherry(niveau)); ennemis.add(new EnnemiMage2DPS(Variante.CHAPITRE_2, niveau)); ennemis.add(new EnnemiMage1DPS(Variante.CHAPITRE_2, niveau));
                         ennemis.add(new EnnemiMage5Tank(Variante.CHAPITRE_2, niveau)); ennemis.add(new EnnemiMage4Buff(Variante.CHAPITRE_2, niveau));

                         return new Stage(3, "Lucy VS Cherry", 105, 10, ennemis); } // Lucy rejoint l'equipe : voir lancerStage3AvecLucy

            case 4  -> { ennemis.add(new EnnemiYuka(niveau)); ennemis.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_2, niveau));
                         ennemis.add(new EnnemiMage2DPS(Variante.CHAPITRE_2, niveau)); ennemis.add(new EnnemiMage1DPS(Variante.CHAPITRE_2, niveau));
                         ennemis.add(new EnnemiMage6Debuff(Variante.CHAPITRE_2, niveau));

                         return new Stage(4, "Yuka contre Natsu", 125, 12, ennemis); } // Natsu rejoint l'equipe : voir lancerStageAvecNatsu

            case 5  -> { ennemis.add(new EnnemiTobi(niveau)); ennemis.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_2, niveau)); ennemis.add(new EnnemiMage2DPS(Variante.CHAPITRE_2, niveau));
                         ennemis.add(new EnnemiMage9Tank(Variante.CHAPITRE_2, niveau)); ennemis.add(new EnnemiMage1DPS(Variante.CHAPITRE_2, niveau));

                         return new Stage(5, "Tobi contre Natsu", 140, 13, ennemis); }// Natsu rejoint l'equipe : voir lancerStageAvecNatsu

            case 6  -> { ennemis.add(new EnnemiLeon(niveau)); ennemis.add(new EnnemiMage1DPS(Variante.CHAPITRE_2, niveau));
                         ennemis.add(new EnnemiMage6Debuff(Variante.CHAPITRE_2, niveau)); ennemis.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_2, niveau));
                         ennemis.add(new EnnemiMage9Tank(Variante.CHAPITRE_2, niveau));

                         return new Stage(6, "Gray vs Leon 1", 160, 15, ennemis); }//Gray rejoint l'equipe : voir lancerStageAvecGray

            case 7  -> { ennemis.add(new EnnemiHomme_mysterieux(niveau)); ennemis.add(new EnnemiMage1DPS(Variante.CHAPITRE_2, niveau)); ennemis.add(new EnnemiMage2DPS(Variante.CHAPITRE_2, niveau));
                           ennemis.add(new EnnemiMage5Tank(Variante.CHAPITRE_2, niveau)); //combat avec Natsu contre  homme mysterieux ( ultia en gros combat demonstation)
                         return new Stage(7, "Natsu contre l'homme mysterieux", 190, 16, ennemis); }
            case 8  -> { ennemis.add(new EnnemiLeon(niveau)); ennemis.add(new EnnemiMage1DPS(Variante.CHAPITRE_2, niveau)); ennemis.add(new EnnemiMage5Tank(Variante.CHAPITRE_2, niveau)); // Gray rejoint l'equipe : voir lancerStageAvecGray
                        ennemis.add(new EnnemiMage4Buff(Variante.CHAPITRE_2, niveau)); ennemis.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_2, niveau));
                         return new Stage(8, "Gray vs Leon part 2", 215, 18, ennemis); }
            case 9  -> { ennemis.add(new EnnemiDeliora_passe()); // Combat flashback (niveau fixe) : Ul seule contre Deliora, voir lancerStage9AvecUl
                         return new Stage(9, "Le passé de Gray", 240, 19, ennemis); }
            case 10 -> { ennemis.add(new EnnemiDeliora(niveau));
                         return new Stage(10, "Deliora le demon", 275, 20, ennemis); }
            default -> { return new Stage(numero, "???", 0, 0, ennemis); }
        }
    }

    public String getTitreStage(int numero) {
        return switch (numero) {
            case 1  -> "Prologue Chapitre 2";
            case 2  -> "Arrivée a l'ile de galuna";
            case 3  -> "Lucy VS Cherry";
            case 4  -> "Yuka contre Natsu";
            case 5  -> "Tobi contre Natsu";
            case 6  -> "Gray vs Leon 1";
            case 7  -> "Natsu contre l'homme mysterieux";
            case 8  -> "Gray vs Leon part 2";
            case 9  -> "Le passé de Gray";
            case 10 -> "Deliora le demon";
            default -> "???";
        };
    }

    public String getNomChapitre() { return "L'Île de Galuna"; }

    public boolean[] getStagesDebloques() { return stagesDebloques; }
    public boolean[] getStagesReussis()   { return stagesReussis; }
    public void setStagesDebloques(boolean[] d) { for (int i = 0; i <= NB_STAGES; i++) stagesDebloques[i] = d[i]; }
    public void setStagesReussis(boolean[] r)   { for (int i = 0; i <= NB_STAGES; i++) stagesReussis[i]   = r[i]; }

    // ── Invites temporaires (stage 3, 4/5, 6/8) ───────────────────────────

    /**
     * Injecte un invite deja configure (niveau/stats) dans l'equipe pour la
     * duree d'un seul combat, puis lance le stage. Le role de remplacement
     * est lu directement sur l'invite (perso_Lucy/perso_Natsu/perso_Gray
     * portent deja le bon role).
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

    private Stage.ResultatStage lancerStage9AvecUl(GameContext ctx, Stage stage, boolean estNouveau) {
        // Flashback : le passe de Gray, avant Fairy Tail — l'equipe du joueur
        // n'existait pas encore. Seule Ul affronte Deliora.
        EnnemiUlFlashback ulFlashback = new EnnemiUlFlashback();

        ArrayList<PersonnageBase> equipeFixe = new ArrayList<>();
        equipeFixe.add(ulFlashback);

        System.out.println(">> Flashback : Ul Milkovich affronte seule le demon Deliora !");
        System.out.println("   « Je vais sceller ce monstre, quoi qu'il m'en coute. » — Ul\n");

        Stage.ResultatStage resultat = stage.lancer(ctx, equipeFixe, estNouveau);

        System.out.println("\n>> Ul scelle Deliora dans la glace eternelle... au prix de son propre corps.");

        return resultat;
    }
}