package lancement.Chapitres;

import Personnage.PersonnageBase;
import Personnage.pnj.Chapitre3.*;
import lancement.GameContext;
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
            System.out.println("\n========================================");
            System.out.println("   CHAPITRE 3 — Phantom Lord");
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
                lancerStage(ctx, choix);
            }
        }
    }

    /**
     * Lance le stage donne et applique les recompenses en cas de victoire.
     * Suppose que le stage est deja debloque. Reutilisable par la console et l'interface graphique.
     */
    public Stage.ResultatStage lancerStage(GameContext ctx, int numero) {
        Stage stage        = construireStage(numero);
        boolean estNouveau = !stagesReussis[numero];
        Stage.ResultatStage resultatStage = stage.lancer(ctx, ctx.formation.getEquipe(), estNouveau);

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
        switch (numero) {

            // Stage 1 — Avant-garde Phantom Lord (soldats bas rang)
            case 1 -> {
                e.add(new EnnemiMage1DPS(22));
                e.add(new EnnemiMage8DPS(22));
                e.add(new EnnemiMage2DPS(22));
                e.add(new EnnemiMage8DPS(21));
                e.add(new EnnemiMage1DPS(21));
                return new Stage(1, "L'assaut de Phantom Lord", 6000, 0, e);
            }

            // Stage 2 — Totomaru + renforts
            case 2 -> {
                e.add(new EnnemiTotomaru());        // niv 26 par défaut
                e.add(new EnnemiMage2DPS(24));
                e.add(new EnnemiMage9Tank(24));
                e.add(new EnnemiMage1DPS(23));
                e.add(new EnnemiMage8DPS(23));
                return new Stage(2, "Totomaru — Sept Flammes", 7500, 0, e);
            }

            // Stage 3 — Sol + troupe solide
            case 3 -> {
                e.add(new EnnemiSol());             // niv 26 par défaut
                e.add(new EnnemiMage8DPS(25));
                e.add(new EnnemiMage5Tank(25));
                e.add(new EnnemiMage2DPS(24));
                e.add(new EnnemiMage3Soigneur(24));
                return new Stage(3, "Sol — L'Impénétrable", 9500, 0, e);
            }

            // Stage 4 — Totomaru + Sol ensemble
            case 4 -> {
                e.add(new EnnemiTotomaru(27));
                e.add(new EnnemiSol(27));
                e.add(new EnnemiMage2DPS(26));
                e.add(new EnnemiMage9Tank(25));
                e.add(new EnnemiMage3Soigneur(25));
                return new Stage(4, "L'Élément 4 se déploie", 11500, 0, e);
            }

            // Stage 5 — Jubia + troupe
            case 5 -> {
                e.add(new EnnemiJubia_4elements()); // niv 28 par défaut
                e.add(new EnnemiMage3Soigneur(27));
                e.add(new EnnemiMage9Tank(26));
                e.add(new EnnemiMage5Tank(26));
                e.add(new EnnemiMage3Soigneur(25));
                return new Stage(5, "Jubia — L'Eau qui emprisonne", 13500, 0, e);
            }

            // Stage 6 — Élément 4 au complet (Jubia + Totomaru + Sol)
            case 6 -> {
                e.add(new EnnemiJubia_4elements(28));
                e.add(new EnnemiTotomaru(28));
                e.add(new EnnemiSol(28));
                e.add(new EnnemiMage2DPS(26));
                e.add(new EnnemiMage8DPS(26));
                return new Stage(6, "L'Élément 4 au complet", 16000, 0, e);
            }

            // Stage 7 — Aria + escorte lourde
            case 7 -> {
                e.add(new EnnemiAria());            // niv 30 par défaut
                e.add(new EnnemiMage3Soigneur(28));
                e.add(new EnnemiMage9Tank(28));
                e.add(new EnnemiMage5Tank(27));
                e.add(new EnnemiMage3Soigneur(27));
                return new Stage(7, "Aria — Magie du Ciel Vide", 19000, 0, e);
            }

            // Stage 8 — Aria + tout l'Élément 4
            case 8 -> {
                e.add(new EnnemiAria(30));
                e.add(new EnnemiJubia_4elements(29));
                e.add(new EnnemiTotomaru(29));
                e.add(new EnnemiSol(29));
                e.add(new EnnemiMage2DPS(27));
                return new Stage(8, "L'Élément 4 — Dernière résistance", 22500, 0, e);
            }

            // Stage 9 — José + Aria + renforts d'élite
            case 9 -> {
                e.add(new EnnemiJose());            // niv 35 par défaut
                e.add(new EnnemiAria(31));
                e.add(new EnnemiMage3Soigneur(29));
                e.add(new EnnemiMage9Tank(28));
                e.add(new EnnemiMage3Soigneur(28));
                return new Stage(9, "José — L'Ombre s'éveille", 27000, 0, e);
            }

            // Stage 10 — José seul, boss ultime
            case 10 -> {
                e.add(new EnnemiJose(35));
                e.add(new EnnemiAria(32));
                e.add(new EnnemiJubia_4elements(30));
                e.add(new EnnemiTotomaru(30));
                e.add(new EnnemiSol(30));
                return new Stage(10, "José Porla — Maître de Phantom Lord", 34000, 0, e);
            }

            default -> { return new Stage(numero, "???", 0, 0, e); }
        }
    }

    public String getTitreStage(int numero) {
        return switch (numero) {
            case 1  -> "L'assaut de Phantom Lord";
            case 2  -> "Totomaru — Sept Flammes";
            case 3  -> "Sol — L'Impénétrable";
            case 4  -> "L'Élément 4 se déploie";
            case 5  -> "Jubia — L'Eau qui emprisonne";
            case 6  -> "L'Élément 4 au complet";
            case 7  -> "Aria — Magie du Ciel Vide";
            case 8  -> "L'Élément 4 — Dernière résistance";
            case 9  -> "José — L'Ombre s'éveille";
            case 10 -> "José Porla — Maître de Phantom Lord";
            default -> "???";
        };
    }

    public boolean[] getStagesDebloques() { return stagesDebloques; }
    public boolean[] getStagesReussis()   { return stagesReussis; }
    public void setStagesDebloques(boolean[] d) { for (int i = 0; i <= NB_STAGES; i++) stagesDebloques[i] = d[i]; }
    public void setStagesReussis(boolean[] r)   { for (int i = 0; i <= NB_STAGES; i++) stagesReussis[i]   = r[i]; }
}