package lancement.Menus;

import lancement.GameContext;
import lancement.Gestionnaires.GestionnaireCompagnons;
import lancement.Gestionnaires.GestionnaireCompagnons.ResultatCompagnon;
import java.util.Scanner;

public class MenuCompagnons {

    public void afficher(GameContext ctx, Scanner scanner) {
        GestionnaireCompagnons gc = ctx.gestionnaireCompagnons;
        boolean retour = false;

        while (!retour) {
            gc.afficher(ctx.joueur.getNiveau());
            System.out.printf("Or disponible : %.0f%n", ctx.joueur.getOr());
            System.out.print("Votre choix : ");
            String choix = scanner.nextLine().trim();

            switch (choix) {
                case "1" -> {
                    if (gc.peutEvoluer()) {
                        // Niveau 10 + évolution disponible → proposer d'évoluer
                        ResultatCompagnon res = gc.evoluer(ctx.joueur.getOr());
                        System.out.println(res.message());
                        if (res.succes()) {
                            ctx.joueur.setOr(ctx.joueur.getOr() - res.orDepense());
                            // Recalculer le bonus sur la formation
                            ctx.formation.appliquerBonusLiens();
                            ctx.sauvegarde.sauvegarder(ctx);
                        }
                    } else if (!gc.estAuNiveauMax()) {
                        // Amélioration normale
                        ResultatCompagnon res = gc.ameliorer(ctx.joueur.getOr());
                        System.out.println(res.message());
                        if (res.succes()) {
                            ctx.joueur.setOr(ctx.joueur.getOr() - res.orDepense());
                            ctx.formation.appliquerBonusLiens();
                            ctx.sauvegarde.sauvegarder(ctx);
                        }
                    } else {
                        System.out.println("Votre compagnon est au maximum actuel !");
                    }
                }
                case "0" -> retour = true;
                default  -> System.out.println("Choix invalide.");
            }
        }
    }
}