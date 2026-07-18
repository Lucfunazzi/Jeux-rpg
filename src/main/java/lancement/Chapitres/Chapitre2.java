package lancement.Chapitres;

import Personnage.PersonnageBase;
import Personnage.pnj.Chapitre1.EnnemiMage1;
import Personnage.pnj.Chapitre1.EnnemiMage2;
import Personnage.pnj.Chapitre1.EnnemiMage3;
import Personnage.pnj.Chapitre2.*;
import lancement.GameContext;
import lancement.Stage;
import java.util.ArrayList;
import java.util.Scanner;

public class Chapitre2 {

    private static final int NB_STAGES = 10;
    private final boolean[] stagesDebloques = new boolean[NB_STAGES + 1];
    private final boolean[] stagesReussis   = new boolean[NB_STAGES + 1];

    public Chapitre2() {
        stagesDebloques[1] = true;
    }

    public void afficher(GameContext ctx, Scanner scanner) {
        boolean retour = false;

        while (!retour) {
            System.out.println("\n========================================");
            System.out.println("   CHAPITRE 2 — L'Île de Galuna");
            System.out.println("========================================");
            System.out.println("Or : " + String.format("%.0f", ctx.joueur.getOr()));
            System.out.println();

            for (int i = 1; i <= NB_STAGES; i++) {
                String etat = stagesReussis[i] ? "[OK]  " : stagesDebloques[i] ? "[  ]  " : "[###] ";
                System.out.println(etat + "Stage " + i + " — " + getTitreStage(i));
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
                Stage stage        = construireStage(choix);
                boolean estNouveau = !stagesReussis[choix];
                Stage.ResultatStage resultatStage = stage.lancer(ctx, ctx.formation.getEquipe(), estNouveau);
                boolean victoire = resultatStage.victoire;

                if (victoire) {
                    stagesReussis[choix] = true;
                    if (choix < NB_STAGES) {
                        stagesDebloques[choix + 1] = true;
                        System.out.println(">> Stage " + (choix + 1) + " debloque !");
                    } else {
                        System.out.println(">> Félicitations ! Vous avez sauvé l'île de Galuna !");
                        // Cadeau de fin de chapitre 2 — première victoire uniquement
                        if (estNouveau) {
                            ctx.inventaire.ajouterMateriau("Echarpe blanche d'Ignir", 50);
                            System.out.println(">> Cadeau : +50 Echarpe(s) blanche d'Ignir !");
                            System.out.println("   Vous pouvez maintenant recruter Natsu"
                                    + " dans le Recrutement Rare !");
                        }
                    }

                   
                    ctx.gestionnaireQuetes.notifierOrGagne(stage.getRecompenseOr());
                    ctx.gestionnaireQuetes.notifierStageFini(2, choix, false,
                            ctx.joueur, ctx.menuRecrutement, ctx.personnagesRecruites);
                    ctx.gestionnaireEtoiles.mettreAJour(2, choix, false,
                            resultatStage.victoire, resultatStage.sansAllieMort, resultatStage.enMoinsDe10Tours);
                }
            }
        }
    }

    private Stage construireStage(int numero) {
        ArrayList<PersonnageBase> ennemis = new ArrayList<>();
        // recompenseXP = 0 : la montée de niveau passe exclusivement par les quêtes
        switch (numero) {
            case 1  -> { ennemis.add(new EnnemiMage1()); ennemis.add(new EnnemiMage1());
                         return new Stage(1, "Débarquement sur l'île maudite", 1500, 0, ennemis); }
            case 2  -> { ennemis.add(new EnnemiTobi()); ennemis.add(new EnnemiMage2());
                         return new Stage(2, "Tobi, mage de glace de l'île", 1800, 0, ennemis); }
            case 3  -> { ennemis.add(new EnnemiYuka()); ennemis.add(new EnnemiMage2()); ennemis.add(new EnnemiMage1());
                         return new Stage(3, "Yuka, l'annuleur de magie", 2100, 0, ennemis); }
            case 4  -> { ennemis.add(new EnnemiTobi()); ennemis.add(new EnnemiYuka());
                         ennemis.add(new EnnemiMage2()); ennemis.add(new EnnemiMage1());
                         return new Stage(4, "Alliance des mages de l'île", 2500, 0, ennemis); }
            case 5  -> { ennemis.add(new EnnemiChery()); ennemis.add(new EnnemiMage3()); ennemis.add(new EnnemiMage2());
                         return new Stage(5, "Chery, gardienne du sanctuaire", 2800, 0, ennemis); }
            case 6  -> { ennemis.add(new EnnemiTobi()); ennemis.add(new EnnemiYuka());
                         ennemis.add(new EnnemiChery()); ennemis.add(new EnnemiMage2());
                         return new Stage(6, "Le trio de l'île réuni", 3200, 0, ennemis); }
            case 7  -> { ennemis.add(new EnnemiLeon()); ennemis.add(new EnnemiMage3());
                         ennemis.add(new EnnemiMage2()); ennemis.add(new EnnemiTank1());
                         return new Stage(7, "Leon Bastia, chef des mages", 3800, 0, ennemis); }
            case 8  -> { ennemis.add(new EnnemiLeon()); ennemis.add(new EnnemiTobi());
                         ennemis.add(new EnnemiYuka()); ennemis.add(new EnnemiChery()); ennemis.add(new EnnemiMage2());
                         return new Stage(8, "La dernière défense de l'île", 4300, 0, ennemis); }
            case 9  -> { ennemis.add(new EnnemiLeon()); ennemis.add(new EnnemiTobi());
                         ennemis.add(new EnnemiYuka()); ennemis.add(new EnnemiTank1()); ennemis.add(new EnnemiSoigneur1());
                         return new Stage(9, "Leon — Ultime résistance", 4800, 0, ennemis); }
            case 10 -> { ennemis.add(new EnnemiLeon()); ennemis.add(new EnnemiYuka());
                         ennemis.add(new EnnemiChery()); ennemis.add(new EnnemiTobi()); ennemis.add(new EnnemiSoigneur1());
                         return new Stage(10, "Leon — Le Pacte Brisé", 5500, 0, ennemis); }
            default -> { return new Stage(numero, "???", 0, 0, ennemis); }
        }
    }

    private String getTitreStage(int numero) {
        return switch (numero) {
            case 1  -> "Débarquement sur l'île maudite";
            case 2  -> "Tobi, mage de glace de l'île";
            case 3  -> "Yuka, l'annuleur de magie";
            case 4  -> "Alliance des mages de l'île";
            case 5  -> "Chery, gardienne du sanctuaire";
            case 6  -> "Le trio de l'île réuni";
            case 7  -> "Leon Bastia, chef des mages";
            case 8  -> "La dernière défense de l'île";
            case 9  -> "Equipe 8 — Second Match";
            case 10 -> "Orochimaru — Le Ninja Legendaire !";
            default -> "???";
        };
    }

    public boolean[] getStagesDebloques() { return stagesDebloques; }
    public boolean[] getStagesReussis()   { return stagesReussis; }
    public void setStagesDebloques(boolean[] d) { for (int i = 0; i <= NB_STAGES; i++) stagesDebloques[i] = d[i]; }
    public void setStagesReussis(boolean[] r)   { for (int i = 0; i <= NB_STAGES; i++) stagesReussis[i]   = r[i]; }
}