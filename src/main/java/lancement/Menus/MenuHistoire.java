package lancement.Menus;

import lancement.Chapitres.Chapitre;
import lancement.Chapitres.CourbeChapitres;
import lancement.ChapitreElite.ChapitreElite;
import lancement.GameContext;
import lancement.Gestionnaires.GestionnaireEtoiles;
import java.util.Scanner;

public class MenuHistoire {

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

            for (int i = 1; i <= CourbeChapitres.NB_CHAPITRES_VISIBLES; i++) {
                if (chapitreNormalDebloque(ctx, i)) {
                    Chapitre c = ctx.chapitres.get(i - 1);
                    afficherLigneChapitreAvecCoffres(ctx, i, false,
                            i + ". Chapitre " + i + " — " + c.getNomChapitre(), true);
                }
            }

            System.out.println("0. Retour");
            System.out.println();
            System.out.print("Votre choix : ");

            String choix = scanner.nextLine().trim();
            if (choix.equals("0")) { retour = true; continue; }

            boolean coffres  = choix.endsWith("c");
            Integer numero   = parseNumeroChapitre(coffres ? choix.substring(0, choix.length() - 1) : choix);

            if (numero == null || numero < 1 || numero > CourbeChapitres.NB_CHAPITRES_VISIBLES || !chapitreNormalDebloque(ctx, numero)) {
                System.out.println("Choix invalide.");
                continue;
            }

            if (coffres) reclamerCoffresMenu(ctx, scanner, numero, false);
            else ctx.chapitres.get(numero - 1).afficher(ctx, scanner);
        }
    }

    /**
     * Vrai si le chapitre normal `numero` est accessible : chapitre precedent termine
     * (ou chapitre 1) ET niveau joueur suffisant (voir CourbeChapitres).
     */
    private boolean chapitreNormalDebloque(GameContext ctx, int numero) {
        boolean sequentiel = numero == 1 || ctx.chapitres.get(numero - 2).getStagesReussis()[10];
        return sequentiel && ctx.joueur.getNiveau() >= CourbeChapitres.niveauRequis(numero);
    }

    // ── Onglet Chapitres Elite ────────────────────────────────────────────
    private void afficherMenuChapitresElite(GameContext ctx, Scanner scanner) {
        boolean retour = false;

        while (!retour) {
            System.out.println("\n========================================");
            System.out.println("          CHAPITRES ELITE");
            System.out.println("========================================");

            for (int i = 1; i <= CourbeChapitres.NB_CHAPITRES_ELITE_VISIBLES; i++) {
                ChapitreElite ce = ctx.chapitresElite.get(i - 1);
                if (ce.estDebloque()) {
                    afficherLigneChapitreAvecCoffres(ctx, i, true, i + ". Chapitre " + i + " Elite", true);
                } else {
                    System.out.println("[###] Chapitre " + i + " Elite (" + prerequisElite(i) + ")");
                }
            }

            System.out.println("0. Retour");
            System.out.println();
            System.out.print("Votre choix : ");

            String choix = scanner.nextLine().trim();
            if (choix.equals("0")) { retour = true; continue; }

            boolean coffres = choix.endsWith("c");
            Integer numero  = parseNumeroChapitre(coffres ? choix.substring(0, choix.length() - 1) : choix);

            if (numero == null || numero < 1 || numero > CourbeChapitres.NB_CHAPITRES_ELITE_VISIBLES) {
                System.out.println("Choix invalide.");
                continue;
            }

            ChapitreElite ce = ctx.chapitresElite.get(numero - 1);
            if (!ce.estDebloque()) {
                System.out.println(coffres ? "Choix invalide." : "Terminez " + prerequisElite(numero) + ".");
                continue;
            }

            if (coffres) reclamerCoffresMenu(ctx, scanner, numero, true);
            else ce.afficher(ctx, scanner);
        }
    }

    /** Texte de prerequis affiche quand le chapitre elite `numero` n'est pas encore debloque. */
    private String prerequisElite(int numero) {
        return numero == 1
                ? "terminez le Chapitre 1 pour debloquer"
                : "terminez C" + numero + " et C" + (numero - 1) + " Elite pour debloquer";
    }

    private Integer parseNumeroChapitre(String s) {
        try { return Integer.parseInt(s); }
        catch (NumberFormatException e) { return null; }
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
        String hint = unDispo ? "  (tapez " + chapitre + "c pour coffres)" : "";
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
            "5x Parchemin Tirage Ordinaire, 1x " + Equipement.CristalTranscendance.NOM,
            "1x Parchemin Tirage Elite, 1x " + Equipement.ParcheminAscension.NOM
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
                ge.reclamerCoffre(chapitre, elite, choixCoffre, ctx.inventaire);
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
        for (GestionnaireEtoiles.ItemBonus b : recomp.itemsBonus()) {
            System.out.printf(">> +%d %s !%n", b.quantite(), b.nom());
        }
        ctx.sauvegarde.sauvegarder(ctx);
    }
}
