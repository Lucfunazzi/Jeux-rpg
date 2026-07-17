package lancement.Menus;

import lancement.Chapitres.Chapitre1;
import lancement.Chapitres.Chapitre2;
import lancement.ChapitreElite.Chapitre1Elite;
import lancement.ChapitreElite.Chapitre2Elite;
import lancement.ChapitreElite.Chapitre3Elite;
import lancement.Chapitres.Chapitre3;
import lancement.GameContext;
import java.util.Scanner;

public class MenuHistoire {

    private final Chapitre1      chapitre1;
    private final Chapitre1Elite chapitre1Elite;
    private final Chapitre2      chapitre2;
    private final Chapitre2Elite chapitre2Elite;
    private final Chapitre3Elite chapitre3Elite;
    private final Chapitre3      chapitre3;

    public MenuHistoire(Chapitre1 chapitre1, Chapitre1Elite chapitre1Elite, Chapitre2 chapitre2, Chapitre2Elite chapitre2Elite, Chapitre3 chapitre3, Chapitre3Elite chapitre3Elite) {
        this.chapitre1      = chapitre1;
        this.chapitre1Elite = chapitre1Elite;
        this.chapitre2      = chapitre2;
        this.chapitre2Elite = chapitre2Elite;
        this.chapitre3Elite = chapitre3Elite;
        this.chapitre3      = chapitre3;
    }

    public void afficher(GameContext ctx, Scanner scanner) {
        boolean retour = false;

        while (!retour) {
            System.out.println("\n========================================");
            System.out.println("              HISTOIRE");
            System.out.println("========================================");
            System.out.println("1. Chapitres");
            System.out.println("2. Chapitres Elite");
            System.out.println("0. Retour");
            System.out.println();
            System.out.print("Votre choix : ");

            switch (scanner.nextLine().trim()) {
                case "1" -> afficherMenuChapitres(ctx, scanner);
                case "2" -> afficherMenuChapitresElite(ctx, scanner);
                case "0" -> retour = true;
                default  -> System.out.println("Choix invalide.");
            }
        }
    }

    // ── Onglet Chapitres normaux ──────────────────────────────────────────
    private void afficherMenuChapitres(GameContext ctx, Scanner scanner) {
        boolean retour = false;

        while (!retour) {
            System.out.println("\n========================================");
            System.out.println("            CHAPITRES");
            System.out.println("========================================");
            System.out.println("1. Chapitre 1 — L'Eveil");

            if (chapitre1.getStagesReussis()[10])
                System.out.println("2. Chapitre 2 — L'Examen Ninja");

            if (chapitre2.getStagesReussis()[10])
                System.out.println("3. Chapitre 3 — Le Tournoi du Tenkaichi");

            System.out.println("0. Retour");
            System.out.println();
            System.out.print("Votre choix : ");

            switch (scanner.nextLine().trim()) {
                case "1" -> chapitre1.afficher(ctx, scanner);
                case "2" -> {
                    if (chapitre1.getStagesReussis()[10])
                        chapitre2.afficher(ctx, scanner);
                    else
                        System.out.println("Choix invalide.");
                }
                case "3" -> {
                    if (chapitre2.getStagesReussis()[10])
                        chapitre3.afficher(ctx, scanner);
                    else
                        System.out.println("Choix invalide.");
                }
                case "0" -> retour = true;
                default  -> System.out.println("Choix invalide.");
            }
        }
    }

    // ── Onglet Chapitres Elite ────────────────────────────────────────────
    private void afficherMenuChapitresElite(GameContext ctx, Scanner scanner) {
        boolean retour = false;

        while (!retour) {
            System.out.println("\n========================================");
            System.out.println("          CHAPITRES ELITE");
            System.out.println("========================================");

            if (chapitre1.getStagesReussis()[10])
                System.out.println("1. Chapitre 1 Elite");
            else
                System.out.println("[###] Chapitre 1 Elite (terminez le Chapitre 1 pour debloquer)");

            if (chapitre2Elite.estDebloque())
                System.out.println("2. Chapitre 2 Elite");
            else
                System.out.println("[###] Chapitre 2 Elite (terminez C1, C2 et C1 Elite pour debloquer)");

            if (chapitre3Elite.estDebloque())
                System.out.println("3. Chapitre 3 Elite");
            else
                System.out.println("[###] Chapitre 3 Elite (terminez C3 et C2 Elite pour debloquer)");

            System.out.println("0. Retour");
            System.out.println();
            System.out.print("Votre choix : ");

            switch (scanner.nextLine().trim()) {
                case "1" -> {
                    if (chapitre1.getStagesReussis()[10])
                        chapitre1Elite.afficher(ctx, scanner);
                    else
                        System.out.println("Terminez d'abord le Chapitre 1.");
                }
                case "2" -> {
                    if (chapitre2Elite.estDebloque())
                        chapitre2Elite.afficher(ctx, scanner);
                    else
                        System.out.println("Terminez le Chapitre 1, le Chapitre 2 et le Chapitre 1 Elite pour debloquer.");
                }
                case "3" -> {
                    if (chapitre3Elite.estDebloque())
                        chapitre3Elite.afficher(ctx, scanner);
                    else
                        System.out.println("Terminez le Chapitre 3 et le Chapitre 2 Elite pour debloquer.");
                }
                case "0" -> retour = true;
                default  -> System.out.println("Choix invalide.");
            }
        }
    }
}