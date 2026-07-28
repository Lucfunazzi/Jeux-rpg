package lancement.Menus;

import lancement.GameContext;
import lancement.Gestionnaires.GestionnaireChasseTresor;
import java.util.Scanner;

public class MenuChasseTresor {

    public void afficher(GameContext ctx, Scanner scanner) {
        boolean retour = false;
        while (!retour) {
            ctx.gestionnaireChasseTresor.mettreAJour();

            System.out.println("\n========================================");
            System.out.println("          CHASSE AU TRESOR");
            System.out.println("========================================");
            System.out.println("Fouillez pour trouver des Parchemins de Chasse (A/S/SS),");
            System.out.println("utilisables au Recrutement Rare pour recruter/faire evoluer");
            System.out.println("des personnages rares.");
            System.out.println();
            System.out.printf("  Parchemin A  : %d%n", ctx.inventaire.getQuantiteMateriau(GestionnaireChasseTresor.PARCHEMIN_A));
            System.out.printf("  Parchemin S  : %d%n", ctx.inventaire.getQuantiteMateriau(GestionnaireChasseTresor.PARCHEMIN_S));
            System.out.printf("  Parchemin SS : %d%n", ctx.inventaire.getQuantiteMateriau(GestionnaireChasseTresor.PARCHEMIN_SS));
            System.out.println();
            System.out.printf("  Fouilles restantes aujourd'hui : %d/%d%n",
                    ctx.gestionnaireChasseTresor.getFouillesRestantes(), GestionnaireChasseTresor.MAX_FOUILLES_PAR_JOUR);
            System.out.println();
            System.out.println("1. Fouiller");
            System.out.println("0. Retour");
            System.out.print("Votre choix : ");

            switch (scanner.nextLine().trim()) {
                case "1" -> fouiller(ctx);
                case "0" -> retour = true;
                default  -> System.out.println("Choix invalide.");
            }
        }
    }

    private void fouiller(GameContext ctx) {
        if (!ctx.gestionnaireChasseTresor.peutFouiller()) {
            System.out.println("\n  Plus de fouilles disponibles aujourd'hui ! Revenez demain.");
            return;
        }
        GestionnaireChasseTresor.ResultatFouille r = ctx.gestionnaireChasseTresor.fouiller(ctx.inventaire, ctx.joueur);
        System.out.println("\n  " + formaterResultat(r));
        ctx.sauvegarde.sauvegarder(ctx);
    }

    public static String formaterResultat(GestionnaireChasseTresor.ResultatFouille r) {
        if (r.materiau() == null) {
            return "Petit tresor... vous trouvez " + r.or() + " or !";
        }
        return "Vous deterrez " + r.quantite() + "x " + r.materiau() + " !";
    }
}
