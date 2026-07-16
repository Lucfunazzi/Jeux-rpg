package lancement.Menus;

import lancement.GameContext;
import lancement.Gestionnaires.GestionnaireCreaturesSacrees;
import lancement.Gestionnaires.GestionnaireCreaturesSacrees.Entrainement;
import java.util.Scanner;

public class MenuCreaturesSacrees {

    public void afficher(GameContext ctx, Scanner scanner) {
        GestionnaireCreaturesSacrees gcs = ctx.gestionnaireCreaturesSacrees;
        boolean retour = false;

        while (!retour) {
            gcs.afficher(ctx.joueur.getNiveau());

            if (!gcs.isOeufDebloque()) {
                System.out.print("Votre choix : ");
                scanner.nextLine();
                retour = true;
                continue;
            }

            System.out.print("Votre choix : ");
            String choix = scanner.nextLine().trim();

            if (gcs.entrainementEnCours()) {
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

    private void lancerEntrainement(GestionnaireCreaturesSacrees gcs,
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