package lancement.Menus;

import lancement.GameContext;
import Personnage.PersonnageBase;
import Personnage.FairyTail.*;
import lancement.Gestionnaires.AreneData;
import java.util.Scanner;

public class MenuBoutiqueArene {

    // ── Catalogue ─────────────────────────────────────────────────────────

    private static final int PRIX_BISKA  = 3_500;
    private static final int PRIX_ARZAK  = 3_500;
    private static final int PRIX_EVERGREEN = 12_500;
    private static final int PRIX_BIXROW = 12_500;
    private static final int PRIX_FREED = 12_500;

    private static final int PRIX_STING = 50_000;
    private static final int PRIX_ROGUE = 50_000;

    /** Catalogue accessible depuis l'exterieur (ex: interface graphique) sous forme {nom, rarete, prix}. */
    public static java.util.List<Object[]> getCatalogue() {
        return java.util.List.of(
            new Object[]{"Bisca",     "C", PRIX_BISKA},
            new Object[]{"Alzack",    "C", PRIX_ARZAK},
            new Object[]{"Evergreen", "A", PRIX_EVERGREEN},
            new Object[]{"Bickslow",  "B", PRIX_BIXROW},
            new Object[]{"Freed",     "A", PRIX_FREED},
            new Object[]{"Sting",     "S", PRIX_STING},
            new Object[]{"Rogue",     "S", PRIX_ROGUE}
        );
    }

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
        afficherLigne("1", "Bisca",     "C", PRIX_BISKA,  dejaRecruté("Bisca"));
        afficherLigne("2", "Alzack",    "C", PRIX_ARZAK,  dejaRecruté("Alzack"));
        afficherLigne("3", "Evergreen", "A", PRIX_EVERGREEN, dejaRecruté("Evergreen"));
        afficherLigne("4", "Bickslow",  "B", PRIX_BIXROW, dejaRecruté("Bickslow"));
        afficherLigne("5", "Freed",     "A", PRIX_FREED, dejaRecruté("Freed"));
        afficherLigne("6", "Sting",     "S", PRIX_STING, dejaRecruté("Sting"));
        afficherLigne("7", "Rogue",     "S", PRIX_ROGUE, dejaRecruté("Rogue"));
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  [0] Retour");
        System.out.print("  Choix : ");

        return switch (scanner.nextLine().trim()) {
            case "1" -> { acheterPersonnage("Bisca",     PRIX_BISKA);  yield true; }
            case "2" -> { acheterPersonnage("Alzack",    PRIX_ARZAK);  yield true; }
            case "3" -> { acheterPersonnage("Evergreen", PRIX_EVERGREEN);  yield true; }
            case "4" -> { acheterPersonnage("Bickslow",  PRIX_BIXROW);  yield true; }
            case "5" -> { acheterPersonnage("Freed",     PRIX_FREED);  yield true; }
            case "6" -> { acheterPersonnage("Sting",     PRIX_STING);  yield true; }
            case "7" -> { acheterPersonnage("Rogue",     PRIX_ROGUE);  yield true; }
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

        System.out.println(executerAchat(nom, prix));
    }

    /**
     * Deduit les points et recrute le personnage si les conditions sont remplies
     * (pas deja recrute, points suffisants). Retourne le message resultat.
     * Reutilisable par la console et l'interface graphique (sans le prompt de confirmation,
     * a gerer cote appelant).
     */
    public String executerAchat(String nom, int prix) {
        if (dejaRecruté(nom)) return "Tu as deja recrute " + nom + " !";
        if (joueurArene.getPointsBoutique() < prix) {
            int manque = prix - joueurArene.getPointsBoutique();
            return "Points insuffisants ! Il te manque " + manque + " pts pour recruter " + nom + ".";
        }

        joueurArene.setPointsBoutique(joueurArene.getPointsBoutique() - prix);

        PersonnageBase perso = switch (nom) {
            case "Bisca" -> new perso_Biska();
            case "Alzack" -> new perso_Arzak();
            case "Evergreen"  -> new perso_Evergreen();
            case "Bickslow"  -> new perso_Bixrow();
            case "Freed" -> new perso_Freed();
            case "Sting" -> new perso_Sting();
            case "Rogue" -> new perso_Rogue();
            default       -> null;
        };

        if (perso == null) return "Erreur : personnage introuvable.";

        ctx.personnagesRecruites.add(perso);
        ctx.sauvegarde.sauvegarder(ctx);
        return nom + " a rejoint ton equipe !\nPoints boutique restants : " + joueurArene.getPointsBoutique() + " pts";
    }

    // ── Utilitaire ────────────────────────────────────────────────────────

    public boolean dejaRecruté(String nom) {
        return ctx.personnagesRecruites.stream()
                .anyMatch(p -> p.getNom().equals(nom));
    }
}