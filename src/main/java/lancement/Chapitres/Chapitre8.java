package lancement.Chapitres;

import Personnage.PersonnageBase;
import lancement.GameContext;
import lancement.Stage;
import java.util.ArrayList;
import java.util.Scanner;
import Personnage.pnj.EnnemisGeneriques.*;
import Personnage.pnj.Chapitre5.*;
import Personnage.pnj.Chapitre8.*;

public class Chapitre8 implements Chapitre {

    private static final int NB_STAGES = 10;
    private final boolean[] stagesDebloques = new boolean[NB_STAGES + 1];
    private final boolean[] stagesReussis   = new boolean[NB_STAGES + 1];

    public Chapitre8() {
        stagesDebloques[1] = true;
    }

    public void afficher(GameContext ctx, Scanner scanner) {
        boolean retour = false;

        while (!retour) {
            ctx.gestionnaireEnergie.mettreAJourRecharge();

            System.out.println("\n========================================");
            System.out.println("   CHAPITRE 8 — " + getNomChapitre());
            System.out.println("========================================");
            System.out.println("Or : " + String.format("%.0f", ctx.joueur.getOr())
                    + "  |  " + ctx.gestionnaireEnergie.afficherEnergie());
            System.out.println();

            for (int i = 1; i <= NB_STAGES; i++) {
                boolean jouable = ctx.gestionnaireQuetes.estStageJouable(8, i, false);
                String etat = stagesReussis[i] ? "[OK]  "
                        : !stagesDebloques[i]  ? "[###] "
                        : jouable              ? "[  ]  "
                        :                        "[QUETE] ";
                String etoiles = ctx.gestionnaireEtoiles.getEtoiles(8, i, false).afficher();
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
            } else if (!ctx.gestionnaireQuetes.estStageJouable(8, choix, false)) {
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
                System.out.println(">> Felicitations ! Chapitre 8 termine !");
            }

            ctx.gestionnaireQuetes.notifierOrGagne(stage.getRecompenseOr());
            ctx.gestionnaireQuetes.notifierStageFini(8, numero, false,
                    ctx.joueur, ctx.menuRecrutement, ctx.personnagesRecruites);
            ctx.gestionnaireEtoiles.mettreAJour(8, numero, false,
                    resultatStage.victoire, resultatStage.sansAllieMort, resultatStage.enMoinsDe10Tours);
        }
        return resultatStage;
    }

    private Stage construireStage(int numero) {
        ArrayList<PersonnageBase> e = new ArrayList<>();

        return switch (numero) {
            case 1 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(8, 1);
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_8, niv));
                yield new Stage(1, "Prologue examen de rang S", 16800, 218, e);
            }
            case 2 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(8, 2);
                e.add(new EnnemiBixrow(niv));
                e.add(new EnnemiFreed(niv));
                yield new Stage(2, "Lucy et cana vs bixrow et freed", 17100, 222, e);
            }
            case 3 -> {
                e.add(new EnnemiMirajane(CourbeChapitres.niveauEnnemiPourStage(8, 3)));
                yield new Stage(3, "Elfman et evergreen vs Mirajane", 17400, 226, e);
            }
            case 4 -> {
                e.add(new EnnemiGildarts(CourbeChapitres.niveauEnnemiPourStage(8, 4)));
                yield new Stage(4, "Natsu vs Gildarts", 17700, 230, e);
            }
            case 5 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(8, 5);
                e.add(new EnnemiJubia(niv));
                e.add(new EnnemiLisanna(niv));
                yield new Stage(5, "Erza vs jubia et lisanna", 18000, 234, e);
            }
            case 6 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(8, 6);
                e.add(new EnnemiMest(niv));
                e.add(new EnnemiWendy(niv));
                yield new Stage(6, "Gray et Loki vs Mest et Wendy", 18300, 238, e);
            }
            case 7 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(8, 7);
                e.add(new EnnemiKawazu(niv));
                e.add(new EnnemiYamaru(niv));
                yield new Stage(7, "Gadjeel et Levy vs kawazu et yamaru", 18600, 242, e);
            }
            case 8 -> {
                e.add(new EnnemiAzuma(CourbeChapitres.niveauEnnemiPourStage(8, 8)));
                yield new Stage(8, "Mest, panthere lilly et wendy vs Asuma", 19000, 247, e);
            }
            case 9 -> {
                e.add(new EnnemiHades(CourbeChapitres.niveauEnnemiPourStage(8, 9)));
                yield new Stage(9, "Le maître de grimoir Heart vs Makarof", 19500, 253, e);
            }
            case 10 -> {
                e.add(new EnnemiZancrow(CourbeChapitres.niveauEnnemiPourStage(8, 10)));
                yield new Stage(10, "Natsu vs Thuncrow", 20000, 260, e);
            }
            default -> new Stage(numero, "???", 0, 0, e);
        };
    }

    public String getTitreStage(int numero) {
        return switch (numero) {
            case 1  -> "Prologue examen de rang S";
            case 2  -> "Lucy et cana vs bixrow et freed";
            case 3  -> "Elfman et evergreen vs Mirajane";
            case 4  -> "Natsu vs Gildarts";
            case 5  -> "Erza vs jubia et lisanna";
               
            case 6  -> "Gray et Loki vs Mest et Wendy";
            case 7  -> "Gadjeel et Levy vs kawazu et yamaru ";
            case 8  -> "Mest , panthere lilly et wendy vs Asuma";
            case 9  -> "Le maître de grimoir Heart vs Makarof";
            case 10 -> "Natsu vs  Thuncrow";
         
            
            default -> "???";
        };
    }

    public String getNomChapitre() { return "Arc île de Tenro 1"; }

    public boolean[] getStagesDebloques() { return stagesDebloques; }
    public boolean[] getStagesReussis()   { return stagesReussis; }
    public void setStagesDebloques(boolean[] d) { for (int i = 0; i <= NB_STAGES; i++) stagesDebloques[i] = d[i]; }
    public void setStagesReussis(boolean[] r)   { for (int i = 0; i <= NB_STAGES; i++) stagesReussis[i]   = r[i]; }
}
