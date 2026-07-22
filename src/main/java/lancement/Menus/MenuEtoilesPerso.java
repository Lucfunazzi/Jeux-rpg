package lancement.Menus;

import Equipement.Inventaire;
import Personnage.PersonnageBase;
import lancement.GameContext;
import lancement.Gestionnaires.GestionnaireEtoilesPerso;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Menu de gestion des étoiles et fragments de personnages.
 *
 * Options :
 *   1. Voir mes fragments
 *   2. Recruter un personnage via fragments
 *   3. Monter en étoile un personnage
 *   0. Retour
 */
public class MenuEtoilesPerso {

    // Catalogue statique : nom → rarete (tous les persos recrutables)
    private record InfoPerso(String nom, String rarete, String classe) {}

    private static final List<InfoPerso> CATALOGUE = List.of(
        // Rang C
        new InfoPerso("Alzack",  "C", "Mage"),
        new InfoPerso("Bisca",   "C", "Mage"),
        new InfoPerso("Elfman",  "C", "Guerrier"),
        new InfoPerso("Max",     "C", "Mage"),
        new InfoPerso("Droy",    "C", "Mage"),
        new InfoPerso("Jet",     "C", "Guerrier"),
        new InfoPerso("Warren",  "C", "Mage"),
        new InfoPerso("Nab",     "C", "Guerrier"),
        new InfoPerso("Romeo",   "C", "Mage"),
        // Rang B
        new InfoPerso("Bickslow",    "B", "Mage"),
        new InfoPerso("Evergreen",   "B", "Mage"),
        new InfoPerso("Cana",        "B", "Mage"),
        new InfoPerso("Loke",        "B", "Mage"),
        new InfoPerso("Levy",        "B", "Mage"),
        new InfoPerso("Lisanna",     "B", "Mage"),
        new InfoPerso("Elfman Bête", "B", "Guerrier"),
        // Rang A
        new InfoPerso("Angel",  "A", "Mage"),
        new InfoPerso("Freed",  "A", "Mage"),
        new InfoPerso("Gajeel", "A", "Guerrier"),
        new InfoPerso("Gray",   "A", "Mage"),
        new InfoPerso("Jubia",  "A", "Mage"),
        new InfoPerso("Lucy",   "A", "Mage"),
        new InfoPerso("Natsu",  "A", "Guerrier"),
        new InfoPerso("Wendy",  "A", "Mage"),
        // Rang S
        new InfoPerso("Erza",           "S", "Guerrier"),
        new InfoPerso("Mirajane",       "S", "Mage"),
        new InfoPerso("Natsu Etherion", "S", "Guerrier"),
        new InfoPerso("Rogue",          "S", "Guerrier"),
        new InfoPerso("Sting",          "S", "Guerrier"),
        new InfoPerso("Yukino",         "S", "Mage"),
        // Rang SS
        new InfoPerso("Lucas",            "SS", "Mage"),
        new InfoPerso("Mirajane Halphas", "SS", "Mage")
    );

    /** Catalogue accessible depuis l'exterieur (ex: interface graphique) sous forme {nom, rarete, classe}. */
    public static List<String[]> getCatalogue() {
        List<String[]> resultat = new ArrayList<>();
        for (InfoPerso info : CATALOGUE) resultat.add(new String[]{info.nom(), info.rarete(), info.classe()});
        return resultat;
    }

    public void afficher(GameContext ctx, Scanner scanner) {
        boolean retour = false;
        while (!retour) {
            System.out.println("\n========================================");
            System.out.println("      ETOILES & FRAGMENTS PERSO");
            System.out.println("========================================");
            System.out.println("1. Voir mes fragments");
            System.out.println("2. Recruter via fragments");
            System.out.println("3. Monter en etoile un personnage");
            System.out.println("0. Retour");
            System.out.print("Votre choix : ");

            switch (scanner.nextLine().trim()) {
                case "1" -> voirFragments(ctx);
                case "2" -> recruterViaFragments(ctx, scanner);
                case "3" -> monterEtoile(ctx, scanner);
                case "0" -> retour = true;
                default  -> System.out.println("Choix invalide.");
            }
        }
    }

    // ── 1. Voir fragments ─────────────────────────────────────────────────
    private void voirFragments(GameContext ctx) {
        Inventaire inv = ctx.inventaire;

        System.out.println("\n=== FRAGMENTS DE PERSONNAGES ===");
        System.out.println("(Format : nom [rang] — barre — qte/cout_recrutement)\n");

        boolean aucun = true;
        for (InfoPerso info : CATALOGUE) {
            int qte  = GestionnaireEtoilesPerso.getFragments(inv, info.nom());
            if (qte == 0) continue;
            aucun = false;
            int cout = GestionnaireEtoilesPerso.coutFragmentsRecrutement(info.rarete());
            String barre = GestionnaireEtoilesPerso.barreFragments(qte, cout);
            boolean recrute = GestionnaireEtoilesPerso.dejaRecruteParNom(
                    ctx.personnagesRecruites, info.nom());
            String statut = recrute ? " [RECRUTE]" : "";
            System.out.printf("  %-20s [%2s] %s %3d/%-3d%s%n",
                    info.nom(), info.rarete(), barre, qte, cout, statut);
        }

        if (aucun) {
            System.out.println("  Vous n'avez aucun fragment de personnage pour l'instant.");
            System.out.println("  Obtenez-en dans le mode Recrutement par Fragments !");
        }
    }

    // ── 2. Recruter via fragments ─────────────────────────────────────────
    private void recruterViaFragments(GameContext ctx, Scanner scanner) {
        Inventaire inv = ctx.inventaire;

        List<InfoPerso> recrutables = new ArrayList<>();
        for (InfoPerso info : CATALOGUE) {
            if (!GestionnaireEtoilesPerso.dejaRecruteParNom(ctx.personnagesRecruites, info.nom())) {
                int qte  = GestionnaireEtoilesPerso.getFragments(inv, info.nom());
                int cout = GestionnaireEtoilesPerso.coutFragmentsRecrutement(info.rarete());
                // Afficher même si pas encore assez, pour montrer la progression
                recrutables.add(info);
            }
        }

        if (recrutables.isEmpty()) {
            System.out.println("\nTous les personnages disponibles sont deja recrutes !");
            return;
        }

        System.out.println("\n=== RECRUTEMENT PAR FRAGMENTS ===");
        System.out.println("Les [OK] sont prêts à être recrutés.\n");

        List<InfoPerso> prets = new ArrayList<>();
        for (InfoPerso info : recrutables) {
            int qte  = GestionnaireEtoilesPerso.getFragments(inv, info.nom());
            int cout = GestionnaireEtoilesPerso.coutFragmentsRecrutement(info.rarete());
            String barre  = GestionnaireEtoilesPerso.barreFragments(qte, cout);
            boolean pret  = qte >= cout;
            String prefixe = pret ? "[OK] " : "     ";
            System.out.printf("  %s%-20s [%2s] %s %3d/%-3d%n",
                    prefixe, info.nom(), info.rarete(), barre, qte, cout);
            if (pret) prets.add(info);
        }

        if (prets.isEmpty()) {
            System.out.println("\nAucun personnage recrutables pour l'instant (fragments insuffisants).");
            return;
        }

        System.out.println("\nChoisissez un personnage a recruter :");
        for (int i = 0; i < prets.size(); i++) {
            InfoPerso info = prets.get(i);
            System.out.printf("  [%d] %s [%s]%n", i + 1, info.nom(), info.rarete());
        }
        System.out.println("  [0] Annuler");
        System.out.print("Votre choix : ");

        int choix;
        try { choix = Integer.parseInt(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("Entree invalide."); return; }
        if (choix == 0) return;
        if (choix < 1 || choix > prets.size()) { System.out.println("Choix invalide."); return; }

        InfoPerso cible = prets.get(choix - 1);
        GestionnaireEtoilesPerso.recruterViaFragments(inv, cible.nom(), cible.rarete());
        PersonnageBase nouveau = ctx.sauvegarde.creerPersonnageParNom(cible.nom());
        if (nouveau != null) {
            ctx.personnagesRecruites.add(nouveau);
            System.out.println("\n>> " + cible.nom() + " [" + cible.rarete() + "] a rejoint l'equipe !");
            System.out.println("   " + GestionnaireEtoilesPerso.coutFragmentsRecrutement(cible.rarete())
                    + " fragments consommes.");
            ctx.sauvegarde.sauvegarder(ctx);
        } else {
            System.out.println("Erreur : personnage introuvable.");
        }
    }

    // ── 3. Monter en étoile ───────────────────────────────────────────────
    private void monterEtoile(GameContext ctx, Scanner scanner) {
        List<PersonnageBase> tous = new ArrayList<>();
        tous.add(ctx.joueur);
        tous.addAll(ctx.personnagesRecruites);

        // Filtre : personnages pouvant encore monter d'étoile
        List<PersonnageBase> eligibles = new ArrayList<>();
        for (PersonnageBase p : tous) {
            if (p.getNbreEtoiles() < 5) eligibles.add(p);
        }

        if (eligibles.isEmpty()) {
            System.out.println("\nTous vos personnages sont a 5 etoiles !");
            return;
        }

        System.out.println("\n=== MONTEE EN ETOILE ===");
        System.out.println("Chaque etoile = +5% ATK/DEF/PV/VIT\n");

        for (int i = 0; i < eligibles.size(); i++) {
            PersonnageBase p   = eligibles.get(i);
            int etoiles        = p.getNbreEtoiles();
            int cout           = GestionnaireEtoilesPerso.coutFragmentsEtoile(p.getRarete(), etoiles);
            int fragments      = GestionnaireEtoilesPerso.getFragments(ctx.inventaire, p.getNom());
            String barre       = GestionnaireEtoilesPerso.barreFragments(fragments, cout);
            String etoilesStr  = "★".repeat(etoiles) + "☆".repeat(5 - etoiles);
            boolean pret       = fragments >= cout;
            String ok          = pret ? " [PRET]" : "";

            System.out.printf("  [%d] %-20s %s  Frag: %s %3d/%-3d%s%n",
                    i + 1, p.getNom(), etoilesStr, barre, fragments, cout, ok);
        }
        System.out.println("  [0] Annuler");
        System.out.print("Votre choix : ");

        int choix;
        try { choix = Integer.parseInt(scanner.nextLine().trim()); }
        catch (NumberFormatException e) { System.out.println("Entree invalide."); return; }
        if (choix == 0) return;
        if (choix < 1 || choix > eligibles.size()) { System.out.println("Choix invalide."); return; }

        PersonnageBase cible = eligibles.get(choix - 1);
        int avant            = cible.getNbreEtoiles();
        boolean succes = GestionnaireEtoilesPerso.monterEtoileViaFragments(ctx.inventaire, cible);

        if (!succes) {
            int cout = GestionnaireEtoilesPerso.coutFragmentsEtoile(cible.getRarete(), avant);
            System.out.printf("%nFragments insuffisants. Il vous faut %d fragments de %s.%n",
                    cout, cible.getNom());
            return;
        }

        int apres = cible.getNbreEtoiles();
        System.out.println("\n>> " + cible.getNom() + " passe a " + apres + " etoile(s) !");
        System.out.println("   " + "★".repeat(apres) + "☆".repeat(5 - apres));
        System.out.printf("   Bonus stats : +%d%% ATK/DEF/PV/VIT%n", apres * 5);
        ctx.sauvegarde.sauvegarder(ctx);
    }
}
