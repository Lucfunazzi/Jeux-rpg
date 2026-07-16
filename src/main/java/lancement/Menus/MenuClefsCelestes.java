package lancement.Menus;

import lancement.GameContext;
import lancement.Gestionnaires.ClefCeleste;
import lancement.Gestionnaires.GestionnaireClefsCelestes;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class MenuClefsCelestes {

    public void afficher(GameContext ctx, Scanner scanner) {
        GestionnaireClefsCelestes gc = ctx.gestionnaireClefsCelestes;

        boolean retour = false;
        while (!retour) {
            ClefCeleste active = gc.getClefActive();
            int fragments      = ctx.inventaire.getQuantiteMateriau("Fragment d'Etoile");

            System.out.println("\n╔══════════════════════════════════════╗");
            System.out.println("║        ✦  CLEFS CELESTES  ✦          ║");
            System.out.println("╠══════════════════════════════════════╣");
            System.out.println("║  Clef active : "
                    + (active != null ? active.nom + " (Lv." + gc.getNiveauClefActive() + ")" : "Aucune"));
            System.out.println("║  Fragments d'Etoiles : " + fragments);
            System.out.println("╠══════════════════════════════════════╣");
            System.out.println("║  [1] Mes clefs & activer");
            System.out.println("║  [2] Ameliorer une clef");
            System.out.println("║  [0] Retour");
            System.out.println("╚══════════════════════════════════════╝");
            System.out.print("Votre choix : ");

            switch (scanner.nextLine().trim()) {
                case "1" -> menuActiver(ctx, scanner);
                case "2" -> menuAmeliorer(ctx, scanner);
                case "0" -> retour = true;
                default  -> System.out.println("Choix invalide.");
            }
        }
    }

    // ── Activer une clef ──────────────────────────────────────────────────
    private void menuActiver(GameContext ctx, Scanner scanner) {
        GestionnaireClefsCelestes gc = ctx.gestionnaireClefsCelestes;

        List<ClefCeleste> debloquees = new ArrayList<>();
        for (ClefCeleste c : ClefCeleste.values())
            if (gc.estDebloquee(c)) debloquees.add(c);

        if (debloquees.isEmpty()) {
            System.out.println("\nVous ne possedez aucune Clef Celeste pour l'instant.");
            System.out.println("Terminez le Chapitre 3 ou le Chapitre 2 Elite pour en obtenir !");
            return;
        }

        System.out.println("\n╔══════════════════════════════════════════════════╗");
        System.out.println("║              MES CLEFS CELESTES                  ║");
        System.out.println("╠══════════════════════════════════════════════════╣");

        ClefCeleste active = gc.getClefActive();
        for (int i = 0; i < debloquees.size(); i++) {
            ClefCeleste c = debloquees.get(i);
            String mark = (c == active) ? " ◄ ACTIVE" : "";
            System.out.printf("║  [%d] %s  Lv.%d%s%n",
                    i + 1, c.nom, gc.getNiveau(c), mark);
            System.out.println("║      " + c.description);
            System.out.println("║      Invocation : " + c.tourLibelle);
            System.out.println("║      Bonus : +" + String.format("%.0f", ClefCeleste.getMultiplicateur(gc.getNiveau(c)) * 100)
                    + "% ATK moy de l'equipe");
        }
        System.out.println("║  [" + (debloquees.size() + 1) + "] Desactiver (aucune clef en combat)");
        System.out.println("║  [0] Retour");
        System.out.println("╚══════════════════════════════════════════════════╝");
        System.out.print("Votre choix : ");

        int choix;
        try { choix = Integer.parseInt(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("Entree invalide."); return; }

        if (choix == 0) return;
        if (choix == debloquees.size() + 1) {
            gc.desactiver();
            System.out.println("Aucune clef active en combat.");
            ctx.sauvegarde.sauvegarder(ctx);
            return;
        }
        if (choix < 1 || choix > debloquees.size()) {
            System.out.println("Choix invalide.");
            return;
        }

        ClefCeleste choisie = debloquees.get(choix - 1);
        gc.activer(choisie);
        System.out.println("\n>> " + choisie.nom + " est maintenant votre clef active !");
        System.out.println("   Elle s'invoquera " + choisie.tourLibelle.toLowerCase() + " en combat.");
        ctx.sauvegarde.sauvegarder(ctx);
    }

    // ── Améliorer une clef ────────────────────────────────────────────────
    private void menuAmeliorer(GameContext ctx, Scanner scanner) {
        GestionnaireClefsCelestes gc = ctx.gestionnaireClefsCelestes;
        int fragments = ctx.inventaire.getQuantiteMateriau("Fragment d'Etoile");

        List<ClefCeleste> debloquees = new ArrayList<>();
        for (ClefCeleste c : ClefCeleste.values())
            if (gc.estDebloquee(c)) debloquees.add(c);

        if (debloquees.isEmpty()) {
            System.out.println("\nAucune clef a ameliorer.");
            return;
        }

        System.out.println("\n[ Amelioration — Fragments d'Etoiles disponibles : " + fragments + " ]");
        for (int i = 0; i < debloquees.size(); i++) {
            ClefCeleste c    = debloquees.get(i);
            int niv          = gc.getNiveau(c);
            String coutStr   = (niv >= ClefCeleste.NIVEAU_MAX)
                    ? "MAX"
                    : gc.getCoutProchainNiveau(c) + " fragments";
            System.out.printf("  [%d] %s  Lv.%d → Lv.%d  (%s)%n",
                    i + 1, c.nom, niv, Math.min(niv + 1, ClefCeleste.NIVEAU_MAX), coutStr);
        }
        System.out.println("  [0] Retour");
        System.out.print("Votre choix : ");

        int choix;
        try { choix = Integer.parseInt(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("Entree invalide."); return; }

        if (choix == 0) return;
        if (choix < 1 || choix > debloquees.size()) { System.out.println("Choix invalide."); return; }

        ClefCeleste cible = debloquees.get(choix - 1);
        int resultat = gc.ameliorer(cible, fragments);

        switch (resultat) {
            case -1 -> System.out.println(cible.nom + " est deja au niveau maximum !");
            case -2 -> System.out.println("Fragments insuffisants. Cout : "
                    + gc.getCoutProchainNiveau(cible) + " | Vous avez : " + fragments);
            case -3 -> System.out.println("Cette clef n'est pas debloquee.");
            default -> {
                ctx.inventaire.retirerMateriau("Fragment d'Etoile", resultat);
                System.out.println("\n>> " + cible.nom + " passe au niveau " + gc.getNiveau(cible) + " !");
                System.out.printf("   Bonus : +%.0f%% ATK moy de l'equipe en combat.%n",
                        ClefCeleste.getMultiplicateur(gc.getNiveau(cible)) * 100);
                ctx.sauvegarde.sauvegarder(ctx);
            }
        }
    }
}