package lancement.Menus;

import lancement.GameContext;
import lancement.Gestionnaires.AreneData;
import lancement.Gestionnaires.GestionnaireArene;
import lancement.Gestionnaires.GestionnaireEtoilesPerso;
import java.util.Scanner;

/**
 * Boutique de l'arene : vend des FRAGMENTS de personnages (et non plus le personnage entier
 * d'un coup) contre des points boutique. Les fragments achetes rejoignent le meme pool que les
 * fragments gagnes ailleurs (quetes, Sceau de Rang...) et se consomment ensuite normalement
 * depuis l'Inventaire : recrutement une fois le seuil atteint (GestionnaireEtoilesPerso), puis
 * montee en etoiles pour les personnages deja recrutes.
 */
public class MenuBoutiqueArene {

    // ── Catalogue : {nom, rarete} ────────────────────────────────────────
    public static java.util.List<Object[]> getCatalogue() {
        return java.util.List.of(
            new Object[]{"Bisca",     "C"},
            new Object[]{"Alzack",    "C"},
            new Object[]{"Evergreen", "A"},
            new Object[]{"Bickslow",  "A"},
            new Object[]{"Freed",     "A"},
            new Object[]{"Sting",     "S"},
            new Object[]{"Rogue",     "S"}
        );
    }

    /** Prix en points boutique d'arene d'1 fragment, selon la rarete du personnage. */
    public static int prixFragment(String rarete) {
        return switch (rarete) {
            case "C" -> 20;
            case "B" -> 50;
            case "A" -> 150;
            case "S" -> 300;
            default  -> 800; // SS (et rangs superieurs a venir)
        };
    }

    private final GameContext       ctx;
    private final Scanner           scanner;
    private final AreneData         joueurArene;
    private final GestionnaireArene gestionnaireArene;

    public MenuBoutiqueArene(GameContext ctx, Scanner scanner, AreneData joueurArene,
                              GestionnaireArene gestionnaireArene) {
        this.ctx               = ctx;
        this.scanner           = scanner;
        this.joueurArene       = joueurArene;
        this.gestionnaireArene = gestionnaireArene;
    }

    // ── Point d'entrée (console) ────────────────────────────────────────────

    public void afficher() {
        boolean continuer = true;
        while (continuer) continuer = afficherMenu();
    }

    private boolean afficherMenu() {
        var catalogue = getCatalogue();
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║       🏪  BOUTIQUE DE L'ARÈNE        ║");
        System.out.println("╚══════════════════════════════════════╝");
        System.out.println("  Points boutique disponibles : "
                         + joueurArene.getPointsBoutique() + " pts");
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  FRAGMENTS DE PERSONNAGES");
        for (int i = 0; i < catalogue.size(); i++) {
            Object[] entree = catalogue.get(i);
            afficherLigne(String.valueOf(i + 1), (String) entree[0], (String) entree[1]);
        }
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
        System.out.println("  [0] Retour");
        System.out.print("  Choix : ");

        String choix = scanner.nextLine().trim();
        if (choix.equals("0")) return false;

        int index;
        try { index = Integer.parseInt(choix) - 1; }
        catch (NumberFormatException e) { System.out.println("  Choix invalide."); return true; }
        if (index < 0 || index >= catalogue.size()) { System.out.println("  Choix invalide."); return true; }

        Object[] entree = catalogue.get(index);
        String nom    = (String) entree[0];
        String rarete = (String) entree[1];
        System.out.print("  Combien de fragments acheter (" + prixFragment(rarete) + " pts/fragment) ? ");
        int quantite;
        try { quantite = Integer.parseInt(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("  Quantite invalide."); return true; }

        System.out.println(acheterFragments(nom, rarete, quantite));
        return true;
    }

    private void afficherLigne(String index, String nom, String rarete) {
        int qte = GestionnaireEtoilesPerso.getFragments(ctx.inventaire, nom);
        System.out.printf("  [%s] %-10s │ Rang %s │ %d pts/fragment │ %d fragments possedes%n",
            index, nom, rarete, prixFragment(rarete), qte);
    }

    // ── Achat de fragments ────────────────────────────────────────────────

    /**
     * Achete {@code quantite} fragments de {@code nom} au prix unitaire de sa rarete. Retourne
     * le message resultat. Reutilisable par la console et l'interface graphique.
     */
    public String acheterFragments(String nom, String rarete, int quantite) {
        if (quantite <= 0) return "Quantite invalide.";

        int coutTotal = prixFragment(rarete) * quantite;
        if (joueurArene.getPointsBoutique() < coutTotal) {
            int manque = coutTotal - joueurArene.getPointsBoutique();
            return "Points insuffisants ! Il te manque " + manque
                    + " pts pour " + quantite + " fragment(s) de " + nom + ".";
        }

        joueurArene.setPointsBoutique(joueurArene.getPointsBoutique() - coutTotal);
        GestionnaireEtoilesPerso.ajouterFragments(ctx.inventaire, nom, quantite);
        gestionnaireArene.uploaderRangJoueur(joueurArene);
        ctx.sauvegarde.sauvegarder(ctx);

        return "+" + quantite + " fragment(s) de " + nom + " ! (-" + coutTotal + " pts)\n"
                + "Points boutique restants : " + joueurArene.getPointsBoutique() + " pts\n"
                + "Rendez-vous dans l'Inventaire (section Fragments) pour recruter ou monter en étoiles.";
    }
}
