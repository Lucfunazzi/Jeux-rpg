package lancement.Chapitres;

import Personnage.PersonnageBase;
import lancement.GameContext;
import lancement.Stage;
import java.util.ArrayList;
import java.util.Scanner;
import Personnage.pnj.EnnemisGeneriques.*;
import Personnage.pnj.Chapitre6.*;

public class Chapitre6 implements Chapitre {

    private static final int NB_STAGES = 10;
    private final boolean[] stagesDebloques = new boolean[NB_STAGES + 1];
    private final boolean[] stagesReussis   = new boolean[NB_STAGES + 1];

    public Chapitre6() {
        stagesDebloques[1] = true;
    }

    public void afficher(GameContext ctx, Scanner scanner) {
        boolean retour = false;

        while (!retour) {
            ctx.gestionnaireEnergie.mettreAJourRecharge();

            System.out.println("\n========================================");
            System.out.println("   CHAPITRE 6 — " + getNomChapitre());
            System.out.println("========================================");
            System.out.println("Or : " + String.format("%.0f", ctx.joueur.getOr())
                    + "  |  " + ctx.gestionnaireEnergie.afficherEnergie());
            System.out.println();

            for (int i = 1; i <= NB_STAGES; i++) {
                boolean jouable = ctx.gestionnaireQuetes.estStageJouable(6, i, false);
                String etat = stagesReussis[i] ? "[OK]  "
                        : !stagesDebloques[i]  ? "[###] "
                        : jouable              ? "[  ]  "
                        :                        "[QUETE] ";
                String etoiles = ctx.gestionnaireEtoiles.getEtoiles(6, i, false).afficher();
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
            } else if (!ctx.gestionnaireQuetes.estStageJouable(6, choix, false)) {
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
                System.out.println(">> Felicitations ! Chapitre 6 termine !");
            }

            ctx.gestionnaireQuetes.notifierOrGagne(stage.getRecompenseOr());
            ctx.gestionnaireQuetes.notifierStageFini(6, numero, false,
                    ctx.joueur, ctx.menuRecrutement, ctx.personnagesRecruites);
            ctx.gestionnaireEtoiles.mettreAJour(6, numero, false,
                    resultatStage.victoire, resultatStage.sansAllieMort, resultatStage.enMoinsDe10Tours);
        }
        return resultatStage;
    }

    private Stage construireStage(int numero) {
        ArrayList<PersonnageBase> e = new ArrayList<>();

        return switch (numero) {
            case 1 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(6, 1);
                e.add(new EnnemiCobra(niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_6, niv));
                yield new Stage(1, "Prologue — L'alliance des guildes", 10200, 132, e);
            }
            case 2 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(6, 2);
                e.add(new EnnemiRacer(niv));
                e.add(new EnnemiHoteye(niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_6, niv));
                yield new Stage(2, "Oracions seis vs l'alliance des guildes", 10500, 136, e);
            }
            case 3 -> {
                e.add(new EnnemiRacer(CourbeChapitres.niveauEnnemiPourStage(6, 3)));
                yield new Stage(3, "Gray et leon vs racer", 10800, 140, e);
            }
            case 4 -> {
                e.add(new EnnemiAngel(CourbeChapitres.niveauEnnemiPourStage(6, 4)));
                yield new Stage(4, "Combat de constellasioniste", 11100, 144, e);
            }
            case 5 -> {
                e.add(new EnnemiHoteye(CourbeChapitres.niveauEnnemiPourStage(6, 5)));
                yield new Stage(5, "Duel entre Jura et Hoy-eyes", 11400, 148, e);
            }
            case 6 -> {
                e.add(new EnnemiCobra(CourbeChapitres.niveauEnnemiPourStage(6, 6)));
                yield new Stage(6, "Cobras vs Natsu", 11700, 152, e);
            }
            case 7 -> {
                e.add(new EnnemiMidnight(CourbeChapitres.niveauEnnemiPourStage(6, 7)));
                yield new Stage(7, "Jellal et erza vs midnight", 12000, 156, e);
            }
            case 8 -> {
                e.add(new EnnemiBrain(CourbeChapitres.niveauEnnemiPourStage(6, 8)));
                yield new Stage(8, "Natsu vs Brain", 12300, 160, e);
            }
            case 9 -> {
                e.add(new EnnemiBrain(CourbeChapitres.niveauEnnemiPourStage(6, 9)));
                yield new Stage(9, "Jura vs Brain", 12700, 165, e);
            }
            case 10 -> {
                e.add(new EnnemiZero(CourbeChapitres.niveauEnnemiPourStage(6, 10)));
                yield new Stage(10, "Natsu vs la nouvelle forme de Brain", 13200, 172, e);
            }
            default -> new Stage(numero, "???", 0, 0, e);
        };
    }

    public String getTitreStage(int numero) {
        return switch (numero) {
            case 1  -> "Prologue — L'alliance des guildes";
            case 2  -> "Oracions seis vs l'alliance des guildes";
            case 3  -> "Gray et leon vs racer";
            case 4  -> "Combat de constellasioniste";
            case 5  -> "Duel entre Jura et Hoy-eyes";
            case 6  -> "Cobras vs Natsu";
            case 7  -> "Jellal et erza vs midnight";
            case 8  -> "Natsu vs Brain ";
            case 9  -> "Jura vs Brain ";
            case 10 -> "Natsu vs la nouvelle forme de Brain";
            default -> "???";
        };
    }

    public String getNomChapitre() { return "Oracions seis"; }

    public boolean[] getStagesDebloques() { return stagesDebloques; }
    public boolean[] getStagesReussis()   { return stagesReussis; }
    public void setStagesDebloques(boolean[] d) { for (int i = 0; i <= NB_STAGES; i++) stagesDebloques[i] = d[i]; }
    public void setStagesReussis(boolean[] r)   { for (int i = 0; i <= NB_STAGES; i++) stagesReussis[i]   = r[i]; }
}
