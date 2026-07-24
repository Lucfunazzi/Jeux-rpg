package lancement.Menus;

import Equipement.FriandiseFamilier;
import lancement.GameContext;
import lancement.Gestionnaires.Gestionnaire_pet;
import lancement.Gestionnaires.Gestionnaire_pet.Entrainement;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Menu_Pet {

    public void afficher(GameContext ctx, Scanner scanner) {
        Gestionnaire_pet gcs = ctx.gestionnaireCreaturesSacrees;
        boolean retour = false;

        while (!retour) {
            gcs.afficher(ctx.joueur.getNiveau());

            if (!gcs.isOeufDebloque()) {
                System.out.print("Votre choix : ");
                scanner.nextLine();
                retour = true;
                continue;
            }

            if (!gcs.estAuNiveauMax()) {
                System.out.println("  [F] Utiliser une Friandise de Familier");
            }
            System.out.print("Votre choix : ");
            String choix = scanner.nextLine().trim();

            if (choix.equalsIgnoreCase("F") && !gcs.estAuNiveauMax()) {
                menuUtiliserFriandise(gcs, ctx, scanner);

            } else if (gcs.entrainementEnCours()) {
                // Seul retour est possible
                if (choix.equals("0")) retour = true;
                else System.out.println("Un entraînement est en cours, revenez plus tard !");

            } else if (gcs.entrainementTermine()) {
                switch (choix) {
                    case "1" -> {
                        String msg = gcs.reclamerEntrainement();
                        if (msg != null) System.out.println(msg);
                        ctx.formation.appliquerBonusLiens();
                        ctx.sauvegarde.sauvegarder(ctx);
                    }
                    case "0" -> retour = true;
                    default  -> System.out.println("Choix invalide.");
                }

            } else if (gcs.estAuNiveauMax() && gcs.getType().peutEvoluer()) {
                switch (choix) {
                    case "2" -> {
                        String msg = gcs.evoluer();
                        System.out.println(msg);
                        ctx.formation.appliquerBonusLiens();
                        ctx.sauvegarde.sauvegarder(ctx);
                    }
                    case "0" -> retour = true;
                    default  -> System.out.println("Choix invalide.");
                }

            } else if (!gcs.estAuNiveauMax()) {
                // Choix entraînement 1/2/3
                switch (choix) {
                    case "1" -> lancerEntrainement(gcs, Entrainement.COURT, ctx);
                    case "2" -> lancerEntrainement(gcs, Entrainement.MOYEN, ctx);
                    case "3" -> lancerEntrainement(gcs, Entrainement.LONG,  ctx);
                    case "0" -> retour = true;
                    default  -> System.out.println("Choix invalide.");
                }

            } else {
                // Niveau max, pas d'évolution disponible
                if (choix.equals("0")) retour = true;
                else System.out.println("Choix invalide.");
            }
        }
    }

    private void menuUtiliserFriandise(Gestionnaire_pet gcs, GameContext ctx, Scanner scanner) {
        List<FriandiseFamilier> dispo = new ArrayList<>();
        for (FriandiseFamilier f : FriandiseFamilier.values()) {
            if (ctx.inventaire.getQuantiteMateriau(f.nom) > 0) dispo.add(f);
        }
        if (dispo.isEmpty()) {
            System.out.println("Aucune Friandise de Familier en stock.");
            return;
        }

        System.out.println("\nFriandises disponibles :");
        for (int i = 0; i < dispo.size(); i++) {
            FriandiseFamilier f = dispo.get(i);
            System.out.println("  " + (i + 1) + ". " + f + " x" + ctx.inventaire.getQuantiteMateriau(f.nom));
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
            System.out.println(gcs.utiliserFriandise(ctx.inventaire, dispo.get(choix - 1)));
            ctx.formation.appliquerBonusLiens();
            ctx.sauvegarde.sauvegarder(ctx);
        } catch (NumberFormatException e) {
            System.out.println("Entree invalide.");
        }
    }

    private void lancerEntrainement(Gestionnaire_pet gcs,
                                    Entrainement e, GameContext ctx) {
        boolean ok = gcs.lancerEntrainement(e);
        if (ok) {
            System.out.println("Entraînement " + e.libelle + " lancé ! Revenez dans " + e.dureeHeures + "h.");
            ctx.sauvegarde.sauvegarder(ctx);
        } else {
            System.out.println("Impossible de lancer l'entraînement.");
        }
    }
}