package lancement.Chapitres;

import Personnage.PersonnageBase;
import lancement.GameContext;
import lancement.Stage;
import java.util.ArrayList;
import java.util.Scanner;
import Personnage.pnj.Chapitre8.*;
import Personnage.pnj.Chapitre9.*;

public class Chapitre9 implements Chapitre {

    private static final int NB_STAGES = 10;
    private final boolean[] stagesDebloques = new boolean[NB_STAGES + 1];
    private final boolean[] stagesReussis   = new boolean[NB_STAGES + 1];

    public Chapitre9() {
        stagesDebloques[1] = true;
    }

    public void afficher(GameContext ctx, Scanner scanner) {
        boolean retour = false;

        while (!retour) {
            ctx.gestionnaireEnergie.mettreAJourRecharge();

            System.out.println("\n========================================");
            System.out.println("   CHAPITRE 9 — " + getNomChapitre());
            System.out.println("========================================");
            System.out.println("Or : " + String.format("%.0f", ctx.joueur.getOr())
                    + "  |  " + ctx.gestionnaireEnergie.afficherEnergie());
            System.out.println();

            for (int i = 1; i <= NB_STAGES; i++) {
                boolean jouable = ctx.gestionnaireQuetes.estStageJouable(9, i, false);
                String etat = stagesReussis[i] ? "[OK]  "
                        : !stagesDebloques[i]  ? "[###] "
                        : jouable              ? "[  ]  "
                        :                        "[QUETE] ";
                String etoiles = ctx.gestionnaireEtoiles.getEtoiles(9, i, false).afficher();
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
            } else if (!ctx.gestionnaireQuetes.estStageJouable(9, choix, false)) {
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
        Stage.ResultatStage resultatStage = stage.lancer(ctx, ctx.formation.getEquipe(), estNouveau);

        if (resultatStage.victoire) {
            stagesReussis[numero] = true;
            if (numero < NB_STAGES) {
                stagesDebloques[numero + 1] = true;
                System.out.println(">> Stage " + (numero + 1) + " debloque !");
            } else {
                System.out.println(">> Felicitations ! Chapitre 9 termine !");
            }

            ctx.gestionnaireQuetes.notifierOrGagne(stage.getRecompenseOr());
            ctx.gestionnaireQuetes.notifierStageFini(9, numero, false,
                    ctx.joueur, ctx.menuRecrutement, ctx.personnagesRecruites);
            ctx.gestionnaireEtoiles.mettreAJour(9, numero, false,
                    resultatStage.victoire, resultatStage.sansAllieMort, resultatStage.enMoinsDe10Tours);
        }
        return resultatStage;
    }

    private Stage construireStage(int numero) {
        ArrayList<PersonnageBase> e = new ArrayList<>();

        return switch (numero) {
            case 1 -> {
                e.add(new EnnemiZancrow(79));
                yield new Stage(1, "Natsu et makarof vs Thuncrow", 20300, 264, e);
            }
            case 2 -> {
                e.add(new EnnemiAzuma(79));
                yield new Stage(2, "Mirajane vs Azuma", 20600, 268, e);
            }
            case 3 -> {
                e.add(new EnnemiRustyrose(80));
                yield new Stage(3, "Elfman et evergreen vs Rustyrose", 20900, 272, e);
            }
            case 4 -> {
                e.add(new EnnemiCapricorn(80));
                yield new Stage(4, "Leo le lion vs Caprico", 21200, 276, e);
            }
            case 5 -> {
                e.add(new EnnemiMeredy(80));
                yield new Stage(5, "Erza et jubia vs Meldy", 21500, 280, e);
            }
            case 6 -> {
                e.add(new EnnemiMeredy(81));
                yield new Stage(6, "Jubia contre Meldy", 21800, 284, e);
            }
            case 7 -> {
                e.add(new EnnemiBluenote(81));
                yield new Stage(7, "BlueNotes vs Gildarts", 22100, 288, e);
            }
            case 8 -> {
                e.add(new EnnemiAzuma(81));
                yield new Stage(8, "Erza contre Azuma", 22400, 292, e);
            }
            case 9 -> {
                e.add(new EnnemiUltear(82));
                yield new Stage(9, "Grey conte Ultia", 22700, 296, e);
            }
            case 10 -> {
                e.add(new EnnemiHades(82));
                yield new Stage(10, "Fée contre Hades", 23500, 306, e);
            }
            default -> new Stage(numero, "???", 0, 0, e);
        };
    }

    public String getTitreStage(int numero) {
        return switch (numero) {
            
            case 1  -> "Natsu et makarof vs Thuncrow";
            case 2  -> "Mirajane vs Azuma";
            case 3  -> "Elfman et evergreen vs Rustyrose";
            case 4  -> "Leo le lion vs Caprico";
            case 5  -> "Erza et jubia vs Meldy";
            case 6  -> "Jubia contre Meldy ";
            case 7  -> "BlueNotes vs Gildarts ";
            case 8  -> "Erza contre Azuma";
            case 9  -> "Grey conte Ultia";
            case 10 -> "Fée contre Hades";
            default -> "???";
                
            
            
            
           
            
            
            
            
        };
    }

    public String getNomChapitre() { return "Arc de tenro 2"; }

    public boolean[] getStagesDebloques() { return stagesDebloques; }
    public boolean[] getStagesReussis()   { return stagesReussis; }
    public void setStagesDebloques(boolean[] d) { for (int i = 0; i <= NB_STAGES; i++) stagesDebloques[i] = d[i]; }
    public void setStagesReussis(boolean[] r)   { for (int i = 0; i <= NB_STAGES; i++) stagesReussis[i]   = r[i]; }
}
