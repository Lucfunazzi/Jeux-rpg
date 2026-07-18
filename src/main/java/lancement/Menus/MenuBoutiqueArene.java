package lancement.Menus;

import lancement.GameContext;
import Personnage.PersonnageBase;
import Personnage.FairyTail.*;
import lancement.Gestionnaires.AreneData;
import java.util.Scanner;

public class MenuBoutiqueArene {

    // ── Catalogue ─────────────────────────────────────────────────────────

    private static final int PRIX_LOKE  = 10_000;
    private static final int PRIX_LEVY  = 13_000;

    private final GameContext ctx;
    private final Scanner     scanner;
    private final AreneData   joueurArene;

    public MenuBoutiqueArene(GameContext ctx, Scanner scanner, AreneData joueurArene) {
        this.ctx         = ctx;
        this.scanner     = scanner;
        this.joueurArene = joueurArene;
    }

    // ── Point d'entrée ────────────────────────────────────────────────────

    public void afficher() {
        boolean continuer = true;
        while (continuer) continuer = afficherMenu();
    }

    private boolean afficherMenu() {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║       🏪  BOUTIQUE DE L'ARÈNE        ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.println("  Points boutique disponibles : "
                         + joueurArene.getPointsBoutique() + " pts");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  PERSONNAGES");
        afficherLigne("1", "Loke",  "B", PRIX_LOKE,  dejaRecruté("Loke"));
        afficherLigne("2", "Levy",  "B", PRIX_LEVY,  dejaRecruté("Levy"));
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  [0] Retour");
        System.out.print("  Choix : ");

        return switch (scanner.nextLine().trim()) {
            case "1" -> { acheterPersonnage("Loke",  PRIX_LOKE);  yield true; }
            case "2" -> { acheterPersonnage("Levy",  PRIX_LEVY);  yield true; }
            case "0" -> false;
            default  -> { System.out.println("  Choix invalide."); yield true; }
        };
    }

    // ── Affichage d'une ligne catalogue ───────────────────────────────────

    private void afficherLigne(String index, String nom, String rarete,
                                int prix, boolean possede) {
        String statut = possede ? "✔ Déjà recruté" : prix + " pts";
        System.out.printf("  [%s] %-10s │ Rang %s │ %s%n",
            index, nom, rarete, statut);
    }

    // ── Achat d'un personnage ─────────────────────────────────────────────

    private void acheterPersonnage(String nom, int prix) {
        if (dejaRecruté(nom)) {
            System.out.println("  Tu as déjà recruté " + nom + " !");
            return;
        }

        if (joueurArene.getPointsBoutique() < prix) {
            int manque = prix - joueurArene.getPointsBoutique();
            System.out.println("  Points insuffisants ! Il te manque "
                             + manque + " pts pour recruter " + nom + ".");
            return;
        }

        // Confirmation
        System.out.println("\n  Recruter " + nom + " pour " + prix
                         + " points boutique ? (1 : Oui / 2 : Non)");
        System.out.print("  Choix : ");
        if (!scanner.nextLine().trim().equals("1")) {
            System.out.println("  Achat annulé.");
            return;
        }

        // Déduire les points et recruter
        joueurArene.setPointsBoutique(joueurArene.getPointsBoutique() - prix);

        PersonnageBase perso = switch (nom) {
            case "Loke"  -> new perso_Loke();
            case "Levy"  -> new perso_Levy();
            default       -> null;
        };

        if (perso != null) {
            ctx.personnagesRecruites.add(perso);
            System.out.println("\n  ✔ " + nom + " a rejoint ton équipe !");
            System.out.println("  Points boutique restants : "
                             + joueurArene.getPointsBoutique() + " pts");
            ctx.sauvegarde.sauvegarder(ctx);
        }
    }

    // ── Utilitaire ────────────────────────────────────────────────────────

    private boolean dejaRecruté(String nom) {
        return ctx.personnagesRecruites.stream()
                .anyMatch(p -> p.getNom().equals(nom));
    }
}