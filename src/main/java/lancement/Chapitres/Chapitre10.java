package lancement.Chapitres;

import Personnage.PersonnageBase;
import lancement.GameContext;
import lancement.Stage;
import java.util.ArrayList;
import java.util.Scanner;
import Personnage.pnj.EnnemisGeneriques.*;
import Personnage.pnj.Chapitre8.*;
import Personnage.pnj.Chapitre9.*;
import Personnage.pnj.Chapitre10.*;

public class Chapitre10 implements Chapitre {

    private static final int NB_STAGES = 10;
    private final boolean[] stagesDebloques = new boolean[NB_STAGES + 1];
    private final boolean[] stagesReussis   = new boolean[NB_STAGES + 1];

    public Chapitre10() {
        stagesDebloques[1] = true;
    }

    public void afficher(GameContext ctx, Scanner scanner) {
        boolean retour = false;

        while (!retour) {
            ctx.gestionnaireEnergie.mettreAJourRecharge();

            System.out.println("\n========================================");
            System.out.println("   CHAPITRE 10 — " + getNomChapitre());
            System.out.println("========================================");
            System.out.println("Or : " + String.format("%.0f", ctx.joueur.getOr())
                    + "  |  " + ctx.gestionnaireEnergie.afficherEnergie());
            System.out.println();

            for (int i = 1; i <= NB_STAGES; i++) {
                boolean jouable = ctx.gestionnaireQuetes.estStageJouable(10, i, false);
                String etat = stagesReussis[i] ? "[OK]  "
                        : !stagesDebloques[i]  ? "[###] "
                        : jouable              ? "[  ]  "
                        :                        "[QUETE] ";
                String etoiles = ctx.gestionnaireEtoiles.getEtoiles(10, i, false).afficher();
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
            } else if (!ctx.gestionnaireQuetes.estStageJouable(10, choix, false)) {
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
                System.out.println(">> Felicitations ! Chapitre 10 termine !");
            }

            ctx.gestionnaireQuetes.notifierOrGagne(stage.getRecompenseOr());
            ctx.gestionnaireQuetes.notifierStageFini(10, numero, false,
                    ctx.joueur, ctx.menuRecrutement, ctx.personnagesRecruites);
            ctx.gestionnaireEtoiles.mettreAJour(10, numero, false,
                    resultatStage.victoire, resultatStage.sansAllieMort, resultatStage.enMoinsDe10Tours);
        }
        return resultatStage;
    }

    private Stage construireStage(int numero) {
        ArrayList<PersonnageBase> e = new ArrayList<>();

        return switch (numero) {
            case 1 -> {
                e.add(new EnnemiHades(CourbeChapitres.niveauEnnemiPourStage(10, 1)));
                yield new Stage(1, "Luxus vs Hades", 23800, 0, e);
            }
            case 2 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(10, 2);
                e.add(new EnnemiMage6Debuff(Variante.CHAPITRE_10, niv));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_10, niv));
                yield new Stage(2, "L'oeil du Démon", 24100, 0, e);
            }
            case 3 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(10, 3);
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_10, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_10, niv));
                e.add(new EnnemiMage5Tank(Variante.CHAPITRE_10, niv));
                yield new Stage(3, "Panthère lilly vs l'armée d'hadès", 24400, 0, e);
            }
            case 4 -> {
                e.add(new EnnemiHades(CourbeChapitres.niveauEnnemiPourStage(10, 4)));
                yield new Stage(4, "Le coup de grâce", 24700, 0, e);
            }
            case 5 -> {
                e.add(new EnnemiRustyrose(CourbeChapitres.niveauEnnemiPourStage(10, 5)));
                yield new Stage(5, "Lisanna,Levy,bixrow et freed vs rustyRose", 25000, 0, e);
            }
            case 6 -> {
                e.add(new EnnemiZeref(CourbeChapitres.niveauEnnemiPourStage(10, 6)));
                yield new Stage(6, "Le mage Noir", 25300, 0, e);
            }
            case 7 -> {
                e.add(new EnnemiAcnologia(CourbeChapitres.niveauEnnemiPourStage(10, 7)));
                yield new Stage(7, "L'apparation d'acnologia", 25600, 0, e);
            }
            case 8 -> {
                e.add(new EnnemiAcnologia(CourbeChapitres.niveauEnnemiPourStage(10, 8)));
                yield new Stage(8, "Acnologia vs Makarov", 25900, 0, e);
            }
            case 9 -> {
                e.add(new EnnemiAcnologia(CourbeChapitres.niveauEnnemiPourStage(10, 9)));
                yield new Stage(9, "L'assaut des fées", 26200, 0, e);
            }
            case 10 -> {
                e.add(new EnnemiAcnologia(CourbeChapitres.niveauEnnemiPourStage(10, 10)));
                yield new Stage(10, "Le dernier espoir : sphère féerique", 27000, 0, e);
            }
            default -> new Stage(numero, "???", 0, 0, e);
        };
    }

    public String getTitreStage(int numero) {
        return switch (numero) {
            
            case 1  -> "Luxus vs Hades";
            case 2  -> "L'oeil du Démon";
            case 3  -> "Panthère lilly vs l'armée d'hadès";
            case 4  -> "Le coup de grâce";
            case 5  -> "Lisanna,Levy,bixrow et freed vs rustyRose";
            case 6  -> "Le mage Noir";
            case 7  -> "L'apparation d'acnologia";
            case 8  -> "Acnologia vs Makarov ";
            case 9  -> "L'assaut des fées";
            case 10 -> "Le dernier espoir : sphère féerique";
            default -> "???";
        };
        
            
            
            
            
    }

    public String getNomChapitre() { return "Arc Tenro 3"; }

    public boolean[] getStagesDebloques() { return stagesDebloques; }
    public boolean[] getStagesReussis()   { return stagesReussis; }
    public void setStagesDebloques(boolean[] d) { for (int i = 0; i <= NB_STAGES; i++) stagesDebloques[i] = d[i]; }
    public void setStagesReussis(boolean[] r)   { for (int i = 0; i <= NB_STAGES; i++) stagesReussis[i]   = r[i]; }
}
