package lancement.Chapitres;

import Personnage.PersonnageBase;
import lancement.GameContext;
import lancement.Stage;
import java.util.ArrayList;
import java.util.Scanner;
import Personnage.pnj.EnnemisGeneriques.*;
import Personnage.pnj.Chapitre7.*;

public class Chapitre7 implements Chapitre {

    private static final int NB_STAGES = 10;
    private final boolean[] stagesDebloques = new boolean[NB_STAGES + 1];
    private final boolean[] stagesReussis   = new boolean[NB_STAGES + 1];

    public Chapitre7() {
        stagesDebloques[1] = true;
    }

    public void afficher(GameContext ctx, Scanner scanner) {
        boolean retour = false;

        while (!retour) {
            ctx.gestionnaireEnergie.mettreAJourRecharge();

            System.out.println("\n========================================");
            System.out.println("   CHAPITRE 7 — " + getNomChapitre());
            System.out.println("========================================");
            System.out.println("Or : " + String.format("%.0f", ctx.joueur.getOr())
                    + "  |  " + ctx.gestionnaireEnergie.afficherEnergie());
            System.out.println();

            for (int i = 1; i <= NB_STAGES; i++) {
                boolean jouable = ctx.gestionnaireQuetes.estStageJouable(7, i, false);
                String etat = stagesReussis[i] ? "[OK]  "
                        : !stagesDebloques[i]  ? "[###] "
                        : jouable              ? "[  ]  "
                        :                        "[QUETE] ";
                String etoiles = ctx.gestionnaireEtoiles.getEtoiles(7, i, false).afficher();
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
            } else if (!ctx.gestionnaireQuetes.estStageJouable(7, choix, false)) {
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
                System.out.println(">> Felicitations ! Chapitre 7 termine !");
            }

            ctx.gestionnaireQuetes.notifierOrGagne(stage.getRecompenseOr());
            ctx.gestionnaireQuetes.notifierStageFini(7, numero, false,
                    ctx.joueur, ctx.menuRecrutement, ctx.personnagesRecruites);
            ctx.gestionnaireEtoiles.mettreAJour(7, numero, false,
                    resultatStage.victoire, resultatStage.sansAllieMort, resultatStage.enMoinsDe10Tours);
        }
        return resultatStage;
    }

    private Stage construireStage(int numero) {
        ArrayList<PersonnageBase> e = new ArrayList<>();

        return switch (numero) {
            case 1 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(7, 1);
                e.add(new EnnemiErzaKnightwalker(niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_7, niv));
                yield new Stage(1, "Prologue Edolas", 13500, 175, e);
            }
            case 2 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(7, 2);
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage5Tank(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_7, niv));
                yield new Stage(2, "Magie Limité", 13800, 179, e);
            }
            case 3 -> {
                e.add(new EnnemiErzaKnightwalker(CourbeChapitres.niveauEnnemiPourStage(7, 3)));
                yield new Stage(3, "La chasseus de fée", 14100, 183, e);
            }
            case 4 -> {
                e.add(new EnnemiSugarboy(CourbeChapitres.niveauEnnemiPourStage(7, 4)));
                yield new Stage(4, "Gray contre sugarBoy", 14400, 187, e);
            }
            case 5 -> {
                e.add(new EnnemiHughes(CourbeChapitres.niveauEnnemiPourStage(7, 5)));
                yield new Stage(5, "Natsu et Lucy vs huges", 14700, 191, e);
            }
            case 6 -> {
                e.add(new EnnemiByro(CourbeChapitres.niveauEnnemiPourStage(7, 6)));
                yield new Stage(6, "Lucy contre bario", 15000, 195, e);
            }
            case 7 -> {
                e.add(new EnnemiPantherLily(CourbeChapitres.niveauEnnemiPourStage(7, 7)));
                yield new Stage(7, "Gajeel vs Panthère Lilly", 15300, 199, e);
            }
            case 8 -> {
                e.add(new EnnemiDormaAnim(CourbeChapitres.niveauEnnemiPourStage(7, 8)));
                yield new Stage(8, "Le roi d'edolas rentre en jeu", 15600, 203, e);
            }
            case 9 -> {
                e.add(new EnnemiDormaAnim(CourbeChapitres.niveauEnnemiPourStage(7, 9)));
                yield new Stage(9, "Natsu et Gajeel et wendy vs Le roi d'edolas", 15900, 207, e);
            }
            case 10 -> {
                e.add(new EnnemiDormaAnim(CourbeChapitres.niveauEnnemiPourStage(7, 10)));
                yield new Stage(10, "Le dernier combat", 16500, 214, e);
            }
            default -> new Stage(numero, "???", 0, 0, e);
        };
    }

    public String getTitreStage(int numero) {
        return switch (numero) {
            case 1  -> "Prologue Edolas";
            case 2  -> "Magie Limité";
            case 3  -> "La chasseus de fée";
            case 4  -> "Gray contre sugarBoy";
            case 5  -> "Natsu et Lucy vs huges";
            case 6  -> "Lucy contre bario";
            case 7  -> "Gajeel vs Panthère Lilly";
            case 8  -> "Le roi d'edolas rentre en jeu";
            case 9  -> "Natsu et Gajeel et wendy vs Le roi d'edolas";
            case 10 -> "Le dernier combat";
            default -> "???";
        };
    }

    public String getNomChapitre() { return "Arc Edolas"; }

    public boolean[] getStagesDebloques() { return stagesDebloques; }
    public boolean[] getStagesReussis()   { return stagesReussis; }
    public void setStagesDebloques(boolean[] d) { for (int i = 0; i <= NB_STAGES; i++) stagesDebloques[i] = d[i]; }
    public void setStagesReussis(boolean[] r)   { for (int i = 0; i <= NB_STAGES; i++) stagesReussis[i]   = r[i]; }
}
