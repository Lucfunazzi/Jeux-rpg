package lancement.ChapitreElite;

import Personnage.PersonnageBase;
import lancement.GameContext;
import lancement.Stage;
import lancement.Chapitres.Chapitre7;
import java.util.ArrayList;
import java.util.Scanner;

public class Chapitre7Elite implements ChapitreElite {

    private static final int NB_STAGES = 10;
    private final boolean[] stagesDebloques = new boolean[NB_STAGES + 1];
    private final boolean[] stagesReussis   = new boolean[NB_STAGES + 1];

    private final Chapitre7      chapitre7;
    private final Chapitre6Elite chapitre6Elite;

    public Chapitre7Elite(Chapitre7 chapitre7, Chapitre6Elite chapitre6Elite) {
        this.chapitre7      = chapitre7;
        this.chapitre6Elite = chapitre6Elite;
        stagesDebloques[1]    = true;
    }

    // -- Condition de deblocage --------------------------------------------
    public boolean estDebloque() {
        return chapitre7.getStagesReussis()[10]
            && chapitre6Elite.getStagesReussis()[10];
    }

    public void afficher(GameContext ctx, Scanner scanner) {
        if (!estDebloque()) {
            System.out.println("Terminez le Chapitre 7 et le Chapitre 6 Elite pour debloquer le Chapitre 7 Elite !");
            return;
        }

        boolean retour = false;

        while (!retour) {
            ctx.gestionnaireEnergie.mettreAJourRecharge();

            System.out.println("\n========================================");
            System.out.println("   CHAPITRE 7 ELITE -- " + getNomChapitre());
            System.out.println("========================================");
            System.out.println("Or : " + String.format("%.0f", ctx.joueur.getOr())
                    + "  |  " + ctx.gestionnaireEnergie.afficherEnergie());
            System.out.println();

            for (int i = 1; i <= NB_STAGES; i++) {
                boolean jouable = ctx.gestionnaireQuetes.estStageJouable(7, i, true);
                String etat     = !stagesDebloques[i] ? "[###] "
                        : stagesReussis[i]             ? "[OK]  "
                        : jouable                       ? "[  ]  "
                        :                                 "[QUETE] ";
                int    restants = ctx.gestionnaireEnergie.getRunsEliteRestants(i);
                String etoiles  = ctx.gestionnaireEtoiles.getEtoiles(7, i, true).afficher();
                System.out.println(etat + "Stage " + i + " -- " + getTitreStage(i)
                        + "  " + etoiles + "  (" + restants + "/10 runs restants)");
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
            } else if (!ctx.gestionnaireQuetes.estStageJouable(7, choix, true)) {
                System.out.println("Acceptez d'abord la quete associee a ce stage (menu Quetes).");
            } else {
                lancerStage(ctx, choix);
            }
        }
    }

    /**
     * Verifie les runs/energie, lance le stage donne et applique les recompenses en cas de
     * victoire. Suppose que le stage est deja debloque. Retourne null si le stage n'a pas pu
     * etre lance (runs epuises ou energie insuffisante -- message imprime dans ce cas).
     * Reutilisable par la console et l'interface graphique.
     */
    public Stage.ResultatStage lancerStage(GameContext ctx, int numero) {
        if (!ctx.gestionnaireEnergie.peutFaireRunElite(numero)) {
            System.out.println("Limite de runs atteinte pour ce stage aujourd'hui (10/10).");
            return null;
        }
        if (!ctx.gestionnaireEnergie.consommerEnergie(5)) {
            System.out.println("Pas assez d'energie ! (il faut 5, vous avez "
                    + ctx.gestionnaireEnergie.getEnergie() + ")");
            return null;
        }

        ctx.gestionnaireEnergie.enregistrerRunElite(numero);
        Stage stage        = construireStage(numero, ctx);
        boolean estNouveau = !stagesReussis[numero];
        Stage.ResultatStage resultatStage = stage.lancer(ctx, ctx.formation.getEquipe(), estNouveau);

        if (resultatStage.victoire) {
            stagesReussis[numero] = true;
            if (numero < NB_STAGES) {
                stagesDebloques[numero + 1] = true;
                System.out.println(">> Stage " + (numero + 1) + " debloque !");
            } else {
                System.out.println(">> Felicitations ! Vous avez termine le Chapitre 7 Elite !");
                ctx.gestionnaireTitres.debloquerTitre("[Titre a definir] -- Chapitre 7 Elite");
            }

            ctx.gestionnaireQuetes.notifierOrGagne(stage.getRecompenseOr());
            ctx.gestionnaireQuetes.notifierStageFini(7, numero, true,
                    ctx.joueur, ctx.menuRecrutement, ctx.personnagesRecruites);
            ctx.gestionnaireEtoiles.mettreAJour(7, numero, true,
                    resultatStage.victoire, resultatStage.sansAllieMort, resultatStage.enMoinsDe10Tours);
        }
        return resultatStage;
    }

    // TODO : remplacer par les vrais ennemis (elite) de chaque stage, ex :
    // e.add(new EnnemiXxx(niveau));
    private Stage construireStage(int numero, GameContext ctx) {
        ArrayList<PersonnageBase> e = new ArrayList<>();

        return switch (numero) {
            case 1  -> new Stage(1,  "[ELITE] Stage 1 -- [Titre a definir]",  0, 0, e);
            case 2  -> new Stage(2,  "[ELITE] Stage 2 -- [Titre a definir]",  0, 0, e);
            case 3  -> new Stage(3,  "[ELITE] Stage 3 -- [Titre a definir]",  0, 0, e);
            case 4  -> new Stage(4,  "[ELITE] Stage 4 -- [Titre a definir]",  0, 0, e);
            case 5  -> new Stage(5,  "[ELITE] Stage 5 -- [Titre a definir]",  0, 0, e);
            case 6  -> new Stage(6,  "[ELITE] Stage 6 -- [Titre a definir]",  0, 0, e);
            case 7  -> new Stage(7,  "[ELITE] Stage 7 -- [Titre a definir]",  0, 0, e);
            case 8  -> new Stage(8,  "[ELITE] Stage 8 -- [Titre a definir]",  0, 0, e);
            case 9  -> new Stage(9,  "[ELITE] Stage 9 -- [Titre a definir]",  0, 0, e);
            case 10 -> new Stage(10, "[ELITE] Stage 10 -- [Titre a definir]", 0, 0, e);
            default -> new Stage(numero, "???", 0, 0, e);
        };
    }

    public String getTitreStage(int numero) {
        return switch (numero) {
            case 1  -> "[ELITE] Stage 1 -- [Titre a definir]";
            case 2  -> "[ELITE] Stage 2 -- [Titre a definir]";
            case 3  -> "[ELITE] Stage 3 -- [Titre a definir]";
            case 4  -> "[ELITE] Stage 4 -- [Titre a definir]";
            case 5  -> "[ELITE] Stage 5 -- [Titre a definir]";
            case 6  -> "[ELITE] Stage 6 -- [Titre a definir]";
            case 7  -> "[ELITE] Stage 7 -- [Titre a definir]";
            case 8  -> "[ELITE] Stage 8 -- [Titre a definir]";
            case 9  -> "[ELITE] Stage 9 -- [Titre a definir]";
            case 10 -> "[ELITE] Stage 10 -- [Titre a definir]";
            default -> "???";
        };
    }

    public String getNomChapitre() { return "[Titre a definir]"; }

    public boolean[] getStagesDebloques() { return stagesDebloques; }
    public boolean[] getStagesReussis()   { return stagesReussis; }
    public void setStagesDebloques(boolean[] d) { for (int i = 0; i <= NB_STAGES; i++) stagesDebloques[i] = d[i]; }
    public void setStagesReussis(boolean[] r)   { for (int i = 0; i <= NB_STAGES; i++) stagesReussis[i]   = r[i]; }
}
