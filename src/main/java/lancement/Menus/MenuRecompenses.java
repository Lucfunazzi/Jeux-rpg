package lancement.Menus;

import lancement.GameContext;
import lancement.Gestionnaires.GestionnaireRecompenses;
import java.util.List;
import java.util.Scanner;

/**
 * Menu Récompenses — regroupe les différents systèmes de récompenses du jeu :
 * récompenses de niveau, pointage du mois, connexion 7 jours, et récompense
 * quotidienne (paliers de temps). Le contenu de la récompense quotidienne
 * n'est défini que pour le palier 30 minutes ; les autres restent à venir.
 */
public class MenuRecompenses {

    public void afficher(GameContext ctx, Scanner scanner) {
        GestionnaireRecompenses gr = ctx.gestionnaireRecompenses;
        gr.mettreAJourPointageMois();
        gr.mettreAJourConnexion();

        boolean retour = false;
        while (!retour) {
            System.out.println("\n========================================");
            System.out.println("            RECOMPENSES");
            System.out.println("========================================");
            System.out.println("1. Recompense de niveau");
            System.out.println("2. Pointage du mois");
            if (!gr.estTerminee()) System.out.println("3. Recompense des 7 jours");
            System.out.println("4. Recompense quotidienne");
            System.out.println("0. Retour");
            System.out.print("Votre choix : ");

            switch (scanner.nextLine().trim()) {
                case "1" -> menuNiveau(ctx, scanner);
                case "2" -> menuMois(ctx, scanner);
                case "3" -> { if (!gr.estTerminee()) menuConnexion(ctx, scanner); else System.out.println("Choix invalide."); }
                case "4" -> menuQuotidienne(ctx, scanner);
                case "0" -> retour = true;
                default  -> System.out.println("Choix invalide.");
            }
        }
    }

    // ── Récompenses de niveau ────────────────────────────────────────────
    private void menuNiveau(GameContext ctx, Scanner scanner) {
        GestionnaireRecompenses gr = ctx.gestionnaireRecompenses;
        int niveauJoueur = ctx.joueur.getNiveau();

        System.out.println("\n--- Recompense de niveau ---");
        for (int i = 0; i < GestionnaireRecompenses.PALIERS_NIVEAU.length; i++) {
            String etat = gr.isNiveauReclame(i) ? "[RECLAME]"
                    : gr.estNiveauDisponible(i, niveauJoueur) ? "[DISPONIBLE]" : "[VERROUILLE]";
            System.out.println("  " + (i + 1) + ". Niveau " + GestionnaireRecompenses.PALIERS_NIVEAU[i] + " " + etat
                    + "  — " + gr.afficherRecompenseNiveau(i));
        }
        System.out.println("  0. Retour");
        System.out.print("Reclamer lequel ? ");

        int choix = lireChoix(scanner);
        if (choix == 0) return;
        int index = choix - 1;
        if (index < 0 || index >= GestionnaireRecompenses.PALIERS_NIVEAU.length) {
            System.out.println("Choix invalide.");
            return;
        }
        if (!gr.estNiveauDisponible(index, niveauJoueur)) {
            System.out.println("Ce palier n'est pas reclamable.");
            return;
        }
        System.out.println(gr.reclamerNiveau(index, ctx.joueur, ctx.inventaire));
        ctx.sauvegarde.sauvegarder(ctx);
    }

    // ── Pointage du mois ──────────────────────────────────────────────────
    private void menuMois(GameContext ctx, Scanner scanner) {
        GestionnaireRecompenses gr = ctx.gestionnaireRecompenses;
        int[] paliers = gr.getPaliersMois();

        System.out.println("\n--- Pointage du mois (" + gr.getJoursCumulesMois() + " jour(s) cumules) ---");
        System.out.println("Points du mois : " + gr.getPointsMois());
        for (int i = 0; i < paliers.length; i++) {
            String etat = gr.isMoisReclame(i) ? "[RECLAME]"
                    : gr.estMoisDisponible(i) ? "[DISPONIBLE]" : "[VERROUILLE]";
            System.out.println("  " + (i + 1) + ". " + paliers[i] + " jours " + etat
                    + "  — " + gr.afficherRecompenseMois(i));
        }
        System.out.println("  B. Boutique du mois (depenser les points)");
        System.out.println("  0. Retour");
        System.out.print("Votre choix : ");

        String saisie = scanner.nextLine().trim();
        if (saisie.equalsIgnoreCase("B")) { menuBoutiqueMois(ctx, scanner); return; }

        int choix;
        try {
            choix = Integer.parseInt(saisie);
        } catch (NumberFormatException e) {
            System.out.println("Entree invalide.");
            return;
        }
        if (choix == 0) return;
        int index = choix - 1;
        if (index < 0 || index >= paliers.length) {
            System.out.println("Choix invalide.");
            return;
        }
        if (!gr.estMoisDisponible(index)) {
            System.out.println("Ce palier n'est pas reclamable.");
            return;
        }
        System.out.println(gr.reclamerMois(index, ctx.joueur, ctx.inventaire));
        ctx.sauvegarde.sauvegarder(ctx);
    }

    // ── Boutique du mois (fragments de personnages contre points) ──────────
    private void menuBoutiqueMois(GameContext ctx, Scanner scanner) {
        GestionnaireRecompenses gr = ctx.gestionnaireRecompenses;
        List<String[]> catalogue = new java.util.ArrayList<>();
        for (String[] info : MenuEtoilesPerso.getCatalogue()) {
            if (!info[0].equals("Lucas")) catalogue.add(info); // Lucas [SS] non vendable en boutique
        }

        boolean retour = false;
        while (!retour) {
            System.out.println("\n--- Boutique du mois — Points : " + gr.getPointsMois() + " ---");
            for (int i = 0; i < catalogue.size(); i++) {
                String[] info = catalogue.get(i);
                int prix = GestionnaireRecompenses.prixFragmentBoutiqueMois(info[1]);
                System.out.println("  " + (i + 1) + ". " + info[0] + " [" + info[1] + "] — " + prix + " points/fragment");
            }
            System.out.println("  0. Retour");
            System.out.print("Acheter 1 fragment de qui ? ");

            int choix = lireChoix(scanner);
            if (choix == 0) { retour = true; continue; }
            if (choix < 1 || choix > catalogue.size()) {
                System.out.println("Choix invalide.");
                continue;
            }
            String[] info = catalogue.get(choix - 1);
            System.out.println(gr.acheterFragmentBoutiqueMois(info[0], info[1], ctx.inventaire));
            ctx.sauvegarde.sauvegarder(ctx);
        }
    }

    // ── Récompense de connexion 7 jours ─────────────────────────────────────
    private void menuConnexion(GameContext ctx, Scanner scanner) {
        GestionnaireRecompenses gr = ctx.gestionnaireRecompenses;

        System.out.println("\n--- Recompense des 7 jours (jour " + gr.getJourConnexion() + "/7) ---");
        for (int j = 1; j <= 7; j++) {
            String etat = gr.isJourReclame(j) ? "[RECLAME]"
                    : gr.estJourDisponible(j) ? "[DISPONIBLE]" : "[VERROUILLE]";
            System.out.println("  " + j + ". Jour " + j + " " + etat + "  — " + gr.afficherRecompenseJour(j));
        }
        System.out.println("  0. Retour");
        System.out.print("Reclamer lequel ? ");

        int choix = lireChoix(scanner);
        if (choix == 0) return;
        if (choix < 1 || choix > 7) {
            System.out.println("Choix invalide.");
            return;
        }
        if (!gr.estJourDisponible(choix)) {
            System.out.println("Ce jour n'est pas reclamable.");
            return;
        }

        if (choix == 7) {
            System.out.println("\nChoisissez votre personnage [S] :");
            String[] options = GestionnaireRecompenses.CHOIX_COFFRE_RANG_S;
            for (int i = 0; i < options.length; i++) System.out.println("  " + (i + 1) + ". " + options[i]);
            System.out.println("  0. Annuler");
            System.out.print("Votre choix : ");
            int c = lireChoix(scanner);
            if (c < 1 || c > options.length) return;
            System.out.println(gr.reclamerJour7(options[c - 1], ctx.personnagesRecruites));
        } else {
            System.out.println(gr.reclamerJour(choix, ctx.joueur, ctx.inventaire));
        }
        ctx.sauvegarde.sauvegarder(ctx);
    }

    // ── Récompense quotidienne (paliers de temps) ───────────────────────────
    private void menuQuotidienne(GameContext ctx, Scanner scanner) {
        GestionnaireRecompenses gr = ctx.gestionnaireRecompenses;
        boolean retour = false;
        while (!retour) {
            System.out.println("\n--- Recompense quotidienne ---");
            System.out.println("1. 30 minutes  [" + gr.getTempsRestant30min() + "]");
            System.out.println("2. 1 heure  [Bientot disponible]");
            System.out.println("3. 2 heures  [Bientot disponible]");
            System.out.println("4. 4 heures  [Bientot disponible]");
            System.out.println("0. Retour");
            System.out.print("Votre choix : ");

            switch (scanner.nextLine().trim()) {
                case "1" -> { System.out.println(gr.reclamer30min(ctx.inventaire)); ctx.sauvegarde.sauvegarder(ctx); }
                case "2" -> System.out.println("[Recompense quotidienne (1 h)] Bientot disponible.");
                case "3" -> System.out.println("[Recompense quotidienne (2 h)] Bientot disponible.");
                case "4" -> System.out.println("[Recompense quotidienne (4 h)] Bientot disponible.");
                case "0" -> retour = true;
                default  -> System.out.println("Choix invalide.");
            }
        }
    }

    private int lireChoix(Scanner scanner) {
        try {
            return Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Entree invalide.");
            return -1;
        }
    }
}
