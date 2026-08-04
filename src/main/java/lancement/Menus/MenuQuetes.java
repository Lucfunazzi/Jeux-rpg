package lancement.Menus;

import Equipement.PotionEnergie;
import Joueur.Personnage_principale;
import lancement.GameContext;
import lancement.Gestionnaires.GestionnaireQuetes;
import lancement.Quetes.Quete;
import lancement.Quetes.QueteJournaliere;
import lancement.Quetes.QueteProgression;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class MenuQuetes {

    public void afficher(GameContext ctx, Scanner scanner) {
        ctx.gestionnaireQuetes.verifierRenouvellement();
        boolean retour = false;

        while (!retour) {
            boolean journalieresDebloquees = ctx.joueur.getNiveau() >= GestionnaireQuetes.NIVEAU_DEBLOCAGE_JOURNALIERES;

            System.out.println("\n========================================");
            System.out.println("            QUETES");
            System.out.println("========================================");
            System.out.println("1. Quetes de chapitre");
            if (journalieresDebloquees) {
                System.out.println("2. Quetes journalieres");
            } else {
                System.out.println("2. Quetes journalieres  [Debloque niveau " + GestionnaireQuetes.NIVEAU_DEBLOCAGE_JOURNALIERES + "]");
            }
            System.out.println("0. Retour");
            System.out.print("Votre choix : ");

            switch (scanner.nextLine().trim()) {
                case "1" -> afficherChapitres(ctx, scanner);
                case "2" -> {
                    if (journalieresDebloquees) {
                        afficherJournalieres(ctx, scanner);
                    } else {
                        System.out.println("Les quetes journalieres se debloquent au niveau "
                                + GestionnaireQuetes.NIVEAU_DEBLOCAGE_JOURNALIERES + " !");
                    }
                }
                case "0" -> retour = true;
                default  -> System.out.println("Choix invalide.");
            }
        }
    }

    // ── Quetes journalieres (+ barre de points) ────────────────────────────
    private void afficherJournalieres(GameContext ctx, Scanner scanner) {
        boolean retour = false;

        while (!retour) {
            System.out.println("\n========================================");
            System.out.println("        QUETES JOURNALIERES");
            System.out.println("========================================");

            int pts = ctx.gestionnaireQuetes.getPointsJournaliers();
            System.out.println("\n[ Barre de points journaliere : " + pts + " / 100 ]");
            for (int i = 0; i < GestionnaireQuetes.PALIERS_BARRE_JOURNALIERE.length; i++) {
                String etat = ctx.gestionnaireQuetes.isBarreReclamee(i) ? "[FAIT] "
                        : ctx.gestionnaireQuetes.estBarreDisponible(i) ? "[DISPO] " : "[  ]  ";
                System.out.println("  " + etat + GestionnaireQuetes.PALIERS_BARRE_JOURNALIERE[i] + " pts : "
                        + ctx.gestionnaireQuetes.afficherRecompenseBarre(i));
            }

            System.out.println("\n[ Quetes ]");
            for (QueteJournaliere qj : ctx.gestionnaireQuetes.getQuetesJournalieres()) {
                System.out.println("  " + qj.getEtat() + qj.getTitre());
                System.out.println("    " + qj.getDescription());
                System.out.println("    Progression : " + qj.getProgression());
                System.out.println("    Recompense  : " + qj.afficherRecompenses());
            }

            System.out.println("\n1. Reclamer une recompense de quete journaliere");
            System.out.println("2. Reclamer un palier de la barre journaliere");
            System.out.println("0. Retour");
            System.out.print("Votre choix : ");

            switch (scanner.nextLine().trim()) {
                case "1" -> reclamerRecompenseJournaliere(ctx, scanner);
                case "2" -> reclamerBarre(ctx, scanner);
                case "0" -> retour = true;
                default  -> System.out.println("Choix invalide.");
            }
        }
    }

    private void reclamerBarre(GameContext ctx, Scanner scanner) {
        List<Integer> dispo = new ArrayList<>();
        for (int i = 0; i < GestionnaireQuetes.PALIERS_BARRE_JOURNALIERE.length; i++) {
            if (ctx.gestionnaireQuetes.estBarreDisponible(i)) dispo.add(i);
        }
        if (dispo.isEmpty()) {
            System.out.println("Aucun palier disponible pour l'instant.");
            return;
        }

        System.out.println("\nPaliers disponibles :");
        for (int i = 0; i < dispo.size(); i++) {
            int idx = dispo.get(i);
            System.out.println("  " + (i + 1) + ". " + GestionnaireQuetes.PALIERS_BARRE_JOURNALIERE[idx]
                    + " pts : " + ctx.gestionnaireQuetes.afficherRecompenseBarre(idx));
        }
        System.out.println("  0. Annuler");
        System.out.print("Votre choix : ");

        try {
            int choix = Integer.parseInt(scanner.nextLine().trim());
            if (choix == 0) return;
            if (choix < 1 || choix > dispo.size()) {
                System.out.println("Choix invalide.");
                return;
            }
            System.out.println(ctx.gestionnaireQuetes.reclamerBarre(dispo.get(choix - 1), ctx.inventaire));
            ctx.sauvegarde.sauvegarder(ctx);
        } catch (NumberFormatException e) {
            System.out.println("Entree invalide.");
        }
    }

    private void reclamerRecompenseJournaliere(GameContext ctx, Scanner scanner) {
        ArrayList<Quete> reclamables = new ArrayList<>();
        for (QueteJournaliere qj : ctx.gestionnaireQuetes.getQuetesJournalieres())
            if (qj.isCompletee() && !qj.isReclamee()) reclamables.add(qj);

        reclamerParmi(ctx, scanner, reclamables);
    }

    // ── Quetes de chapitre (acceptation + reclamation) ─────────────────────
    private void afficherChapitres(GameContext ctx, Scanner scanner) {
        boolean retour = false;

        while (!retour) {
            System.out.println("\n========================================");
            System.out.println("        QUETES DE CHAPITRE");
            System.out.println("========================================");
            System.out.println("Acceptez une quete pour debloquer le stage associe.");

            List<QueteProgression> visibles = ctx.gestionnaireQuetes.getQuetesVisibles(ctx);
            if (visibles.isEmpty()) {
                System.out.println("\nAucune quete de chapitre disponible pour l'instant.");
            } else {
                System.out.println();
                for (QueteProgression q : visibles) {
                    System.out.println("  " + q.getEtat() + q.getTitre());
                    System.out.println("    " + q.getDescription());
                    System.out.println("    Recompense : " + q.afficherRecompenses());
                }
            }

            System.out.println("\n1. Accepter une quete (debloque le stage associe)");
            System.out.println("2. Reclamer une recompense de quete");
            System.out.println("0. Retour");
            System.out.print("Votre choix : ");

            switch (scanner.nextLine().trim()) {
                case "1" -> accepterQueteChapitre(ctx, scanner);
                case "2" -> reclamerRecompenseChapitre(ctx, scanner);
                case "0" -> retour = true;
                default  -> System.out.println("Choix invalide.");
            }
        }
    }

    private void accepterQueteChapitre(GameContext ctx, Scanner scanner) {
        List<QueteProgression> aAccepter = new ArrayList<>();
        for (QueteProgression q : ctx.gestionnaireQuetes.getQuetesVisibles(ctx))
            if (!q.isAcceptee()) aAccepter.add(q);

        if (aAccepter.isEmpty()) {
            System.out.println("Aucune quete a accepter pour l'instant.");
            return;
        }

        System.out.println("\nQuetes a accepter :");
        for (int i = 0; i < aAccepter.size(); i++)
            System.out.println("  " + (i + 1) + ". " + aAccepter.get(i).getTitre());
        System.out.println("  0. Annuler");
        System.out.print("Votre choix : ");

        int choix;
        try {
            choix = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Entree invalide.");
            return;
        }
        if (choix == 0) return;
        if (choix < 1 || choix > aAccepter.size()) {
            System.out.println("Choix invalide.");
            return;
        }

        System.out.println(ctx.gestionnaireQuetes.accepterQueteProgression(ctx, aAccepter.get(choix - 1).getId()));
        ctx.sauvegarde.sauvegarder(ctx);
    }

    private void reclamerRecompenseChapitre(GameContext ctx, Scanner scanner) {
        ArrayList<Quete> reclamables = new ArrayList<>();
        for (QueteProgression q : ctx.gestionnaireQuetes.getQuetesVisibles(ctx))
            if (q.isCompletee() && !q.isReclamee()) reclamables.add(q);

        reclamerParmi(ctx, scanner, reclamables);
    }

    // ── Reclamation (commun journalieres / chapitre) ────────────────────────
    private void reclamerParmi(GameContext ctx, Scanner scanner, ArrayList<Quete> reclamables) {
        if (reclamables.isEmpty()) {
            System.out.println("Aucune recompense a reclamer.");
            return;
        }

        System.out.println("\nChoisissez la quete a reclamer :");
        for (int i = 0; i < reclamables.size(); i++)
            System.out.println("  " + (i + 1) + ". " + reclamables.get(i).getTitre()
                    + "  — " + reclamables.get(i).afficherRecompenses());
        System.out.println("  0. Annuler");
        System.out.print("Votre choix : ");

        int choix;
        try {
            choix = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Entree invalide.");
            return;
        }

        if (choix == 0) return;
        if (choix < 1 || choix > reclamables.size()) {
            System.out.println("Choix invalide.");
            return;
        }

        Quete q = reclamables.get(choix - 1);
        GestionnaireQuetes.ResultatRecompense recompense = ctx.gestionnaireQuetes.reclamerRecompense(ctx, q);

        System.out.println("\n>> Recompenses recues pour : " + q.getTitre());
        if (recompense.parcheminC() > 0)
            System.out.println("  + " + recompense.parcheminC() + " parchemins C !");
        if (recompense.potionEnergie())
            System.out.println("  + 1x " + PotionEnergie.MOYENNE.nom + " !");
        for (Quete.RecompenseItem item : recompense.items())
            System.out.println("  + " + item.quantite() + "x " + item.nom() + " !");
    }
}
