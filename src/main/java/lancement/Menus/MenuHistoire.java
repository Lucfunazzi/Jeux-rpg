package lancement.Menus;

import lancement.Chapitres.Chapitre1;
import lancement.Chapitres.Chapitre2;
import lancement.ChapitreElite.Chapitre1Elite;
import lancement.ChapitreElite.Chapitre2Elite;
import lancement.ChapitreElite.Chapitre3Elite;
import lancement.Chapitres.Chapitre3;
import lancement.GameContext;
import lancement.Gestionnaires.GestionnaireEtoiles;
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

            afficherLigneChapitreAvecCoffres(ctx, 1, false, "1. Chapitre 1 — Prologue", true);
            if (chapitre1.getStagesReussis()[10])
                afficherLigneChapitreAvecCoffres(ctx, 2, false, "2. Chapitre 2 — L'Île de Galuna", true);
            if (chapitre2.getStagesReussis()[10])
                afficherLigneChapitreAvecCoffres(ctx, 3, false, "3. Chapitre 3 — Phantom Lord", true);

            System.out.println("0. Retour");
            System.out.println();
            System.out.print("Votre choix : ");

            String choix = scanner.nextLine().trim();
            switch (choix) {
                case "1" -> chapitre1.afficher(ctx, scanner);
                case "1c" -> reclamerCoffresMenu(ctx, scanner, 1, false);
                case "2" -> {
                    if (chapitre1.getStagesReussis()[10]) chapitre2.afficher(ctx, scanner);
                    else System.out.println("Choix invalide.");
                }
                case "2c" -> {
                    if (chapitre1.getStagesReussis()[10]) reclamerCoffresMenu(ctx, scanner, 2, false);
                    else System.out.println("Choix invalide.");
                }
                case "3" -> {
                    if (chapitre2.getStagesReussis()[10]) chapitre3.afficher(ctx, scanner);
                    else System.out.println("Choix invalide.");
                }
                case "3c" -> {
                    if (chapitre2.getStagesReussis()[10]) reclamerCoffresMenu(ctx, scanner, 3, false);
                    else System.out.println("Choix invalide.");
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
                afficherLigneChapitreAvecCoffres(ctx, 1, true, "1. Chapitre 1 Elite", true);
            else
                System.out.println("[###] Chapitre 1 Elite (terminez le Chapitre 1 pour debloquer)");

            if (chapitre2Elite.estDebloque())
                afficherLigneChapitreAvecCoffres(ctx, 2, true, "2. Chapitre 2 Elite", true);
            else
                System.out.println("[###] Chapitre 2 Elite (terminez C1, C2 et C1 Elite pour debloquer)");

            if (chapitre3Elite.estDebloque())
                afficherLigneChapitreAvecCoffres(ctx, 3, true, "3. Chapitre 3 Elite", true);
            else
                System.out.println("[###] Chapitre 3 Elite (terminez C3 et C2 Elite pour debloquer)");

            System.out.println("0. Retour");
            System.out.println();
            System.out.print("Votre choix : ");

            String choix = scanner.nextLine().trim();
            switch (choix) {
                case "1" -> {
                    if (chapitre1.getStagesReussis()[10]) chapitre1Elite.afficher(ctx, scanner);
                    else System.out.println("Terminez d'abord le Chapitre 1.");
                }
                case "1c" -> {
                    if (chapitre1.getStagesReussis()[10]) reclamerCoffresMenu(ctx, scanner, 1, true);
                    else System.out.println("Choix invalide.");
                }
                case "2" -> {
                    if (chapitre2Elite.estDebloque()) chapitre2Elite.afficher(ctx, scanner);
                    else System.out.println("Terminez le Chapitre 1, le Chapitre 2 et le Chapitre 1 Elite pour debloquer.");
                }
                case "2c" -> {
                    if (chapitre2Elite.estDebloque()) reclamerCoffresMenu(ctx, scanner, 2, true);
                    else System.out.println("Choix invalide.");
                }
                case "3" -> {
                    if (chapitre3Elite.estDebloque()) chapitre3Elite.afficher(ctx, scanner);
                    else System.out.println("Terminez le Chapitre 3 et le Chapitre 2 Elite pour debloquer.");
                }
                case "3c" -> {
                    if (chapitre3Elite.estDebloque()) reclamerCoffresMenu(ctx, scanner, 3, true);
                    else System.out.println("Choix invalide.");
                }
                case "0" -> retour = true;
                default  -> System.out.println("Choix invalide.");
            }
        }
    }

    // ── Affichage ligne chapitre avec indicateur coffres ──────────────────
    private void afficherLigneChapitreAvecCoffres(GameContext ctx, int chapitre,
                                                   boolean elite, String label,
                                                   boolean afficherIndicateur) {
        GestionnaireEtoiles ge = ctx.gestionnaireEtoiles;
        int etoiles   = ge.compterEtoiles(chapitre, elite);
        StringBuilder coffres = new StringBuilder();
        for (int i = 1; i <= 3; i++) {
            if (ge.coffreReclame(chapitre, elite, i))        coffres.append(" [✓]");
            else if (ge.coffreDisponible(chapitre, elite, i)) coffres.append(" [🎁]");
            else coffres.append(" [" + ge.getSeuilCoffre(i) + "★]");
        }
        boolean unDispo = ge.coffreDisponible(chapitre, elite, 1)
                       || ge.coffreDisponible(chapitre, elite, 2)
                       || ge.coffreDisponible(chapitre, elite, 3);
        String hint = unDispo ? "  (tapez " + (elite ? chapitre : chapitre) + "c pour coffres)" : "";
        System.out.println(label + "  [" + etoiles + "/30★]" + coffres + hint);
    }

    // ── Menu de réclamation des coffres ───────────────────────────────────
    private void reclamerCoffresMenu(GameContext ctx, Scanner scanner,
                                      int chapitre, boolean elite) {
        GestionnaireEtoiles ge = ctx.gestionnaireEtoiles;
        String nomChap = (elite ? "Chapitre " + chapitre + " Elite" : "Chapitre " + chapitre);

        System.out.println("\n--- COFFRES : " + nomChap + " ---");
        System.out.printf("Etoiles obtenues : %d / 30%n", ge.compterEtoiles(chapitre, elite));
        System.out.println();

        String[] labelsRecomp = {
            "2x Parchemin Tirage Ordinaire",
            "5x Parchemin Tirage Ordinaire",
            "1x Parchemin Tirage Elite"
        };

        boolean unDisponible = false;
        for (int i = 1; i <= 3; i++) {
            String statut;
            if (ge.coffreReclame(chapitre, elite, i))
                statut = "[RECL.] ";
            else if (ge.coffreDisponible(chapitre, elite, i))
                statut = "[DISPO] ";
            else
                statut = "[" + ge.getSeuilCoffre(i) + "★   ] ";
            System.out.println("  " + i + ". " + statut
                    + "Coffre " + i + " (" + ge.getSeuilCoffre(i) + " étoiles) → "
                    + labelsRecomp[i - 1]);
            if (ge.coffreDisponible(chapitre, elite, i)) unDisponible = true;
        }

        if (!unDisponible) {
            System.out.println("\nAucun coffre disponible pour l'instant.");
            return;
        }

        System.out.println("\nChoisissez un coffre a reclamer (0 pour annuler) :");
        System.out.print("Votre choix : ");

        int choixCoffre;
        try { choixCoffre = Integer.parseInt(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("Entree invalide."); return; }
        if (choixCoffre == 0) return;
        if (choixCoffre < 1 || choixCoffre > 3) { System.out.println("Choix invalide."); return; }

        GestionnaireEtoiles.RecompenseCoffre recomp =
                ge.reclamerCoffre(chapitre, elite, choixCoffre);
        if (recomp == null) {
            System.out.println("Ce coffre n'est pas disponible.");
            return;
        }

        // Appliquer la récompense
        switch (recomp.type()) {
            case PARCHEMIN_ORDINAIRE -> {
                ctx.menuTirage.setParcheminOrdinaire(
                        ctx.menuTirage.getParcheminOrdinaire() + recomp.quantite());
                System.out.printf("%n>> +%d Parchemin(s) de Tirage Ordinaire !%n", recomp.quantite());
                System.out.printf("   Total : %d parchemins ordinaires.%n",
                        ctx.menuTirage.getParcheminOrdinaire());
            }
            case PARCHEMIN_ELITE -> {
                ctx.menuTirage.setParcheminElite(
                        ctx.menuTirage.getParcheminElite() + recomp.quantite());
                System.out.printf("%n>> +%d Parchemin(s) de Tirage Elite !%n", recomp.quantite());
                System.out.printf("   Total : %d parchemins elite.%n",
                        ctx.menuTirage.getParcheminElite());
            }
        }
        ctx.sauvegarde.sauvegarder(ctx);
    }
}