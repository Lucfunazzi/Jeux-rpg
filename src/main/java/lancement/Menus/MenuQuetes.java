package lancement.Menus;

import Equipement.PotionEnergie;
import Joueur.Personnage_principale;
import Personnage.PersonnageBase;
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
            System.out.println("\n========================================");
            System.out.println("            QUETES");
            System.out.println("========================================");

            int pts = ctx.gestionnaireQuetes.getPointsJournaliers();
            System.out.println("\n[ Barre de points journaliere : " + pts + " / 100 ]");
            for (int i = 0; i < GestionnaireQuetes.PALIERS_BARRE_JOURNALIERE.length; i++) {
                String etat = ctx.gestionnaireQuetes.isBarreReclamee(i) ? "[FAIT] "
                        : ctx.gestionnaireQuetes.estBarreDisponible(i) ? "[DISPO] " : "[  ]  ";
                System.out.println("  " + etat + GestionnaireQuetes.PALIERS_BARRE_JOURNALIERE[i] + " pts : "
                        + ctx.gestionnaireQuetes.afficherRecompenseBarre(i));
            }

            System.out.println("\n[ Quetes journalieres ]");
            for (QueteJournaliere qj : ctx.gestionnaireQuetes.getQuetesJournalieres()) {
                System.out.println("  " + qj.getEtat() + qj.getTitre());
                System.out.println("    " + qj.getDescription());
                System.out.println("    Progression : " + qj.getProgression());
                System.out.println("    Recompense  : " + qj.afficherRecompenses());
            }

            System.out.println("\n[ Quetes de progression ]");
            for (QueteProgression q : ctx.gestionnaireQuetes.getQuetesVisibles(ctx)) {
                System.out.println("  " + q.getEtat() + q.getTitre());
                System.out.println("    " + q.getDescription());
                System.out.println("    Recompense : " + q.afficherRecompenses());
            }

            System.out.println("\n1. Reclamer une recompense de quete");
            System.out.println("2. Reclamer un palier de la barre journaliere");
            System.out.println("0. Retour");
            System.out.print("Votre choix : ");

            int choix;
            try {
                choix = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Entree invalide.");
                continue;
            }

            switch (choix) {
                case 0 -> retour = true;
                case 1 -> reclamerRecompense(ctx, scanner);
                case 2 -> reclamerBarre(ctx, scanner);
                default -> System.out.println("Choix invalide.");
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

    private void reclamerRecompense(GameContext ctx, Scanner scanner) {
        ArrayList<Quete> reclamables = new ArrayList<>();

        for (QueteJournaliere qj : ctx.gestionnaireQuetes.getQuetesJournalieres())
            if (qj.isCompletee() && !qj.isReclamee()) reclamables.add(qj);

        for (QueteProgression q : ctx.gestionnaireQuetes.getQuetesVisibles(ctx))
            if (q.isCompletee() && !q.isReclamee()) reclamables.add(q);

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
        q.setReclamee(true);

        System.out.println("\n>> Recompenses recues pour : " + q.getTitre());
        if (q.getRecompenseXP() > 0)
            for (PersonnageBase p : ctx.formation.getEquipe())
                p.gagnerExperience(q.getRecompenseXP());
        if (q.getRecompenseOr() > 0)
            ctx.joueur.ajouterOr(q.getRecompenseOr());
        if (q.getRecompenseParcheminC() > 0) {
            ctx.menuRecrutement.ajouterParcheminC(q.getRecompenseParcheminC());
            System.out.println("  + " + q.getRecompenseParcheminC() + " parchemins C !");
        }
        if (q instanceof QueteJournaliere) {
            ctx.inventaire.ajouterMateriau(PotionEnergie.MOYENNE.nom, 1);
            System.out.println("  + 1x " + PotionEnergie.MOYENNE.nom + " !");
        }
        for (Quete.RecompenseItem item : q.getRecompensesItems()) {
            ctx.inventaire.ajouterMateriau(item.nom(), item.quantite());
            System.out.println("  + " + item.quantite() + "x " + item.nom() + " !");
        }

        ctx.sauvegarde.sauvegarder(ctx);
    }
}