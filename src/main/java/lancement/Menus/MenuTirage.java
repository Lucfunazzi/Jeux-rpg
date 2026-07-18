package lancement.Menus;

import Personnage.PersonnageBase;
import Personnage.FairyTail.*;
import lancement.GameContext;
import lancement.Gestionnaires.GestionnaireEtoilesPerso;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

/**
 * Système de tirage gacha.
 *
 * ── TIRAGE ORDINAIRE ────────────────────────────────────────────────────────
 * Pool : tous les C (10), tous les B (17), 2 A sélectionnés (Naruto, Natsu).
 * Coût : 1 Parchemin de Tirage par tirage x1, 10 par tirage x10.
 *
 * Par tirage unitaire :
 *   - 100% chance d'obtenir AU MOINS un rang C
 *   - 20%  chance d'obtenir un rang B  (sinon C)
 *   - 2%   chance d'obtenir un rang A  (sinon B ou C)
 *
 * Fragments possibles à la place d'un personnage (plus probable qu'un perso) :
 *   - C : 1-5 fragments du perso C
 *   - B : 1-3 fragments du perso B
 *   - A : 1-2 fragments du perso A
 * Probabilité d'obtenir des fragments plutôt qu'un perso : 60% C, 50% B, 40% A
 *
 * ── TIRAGE ELITE ────────────────────────────────────────────────────────────
 * Pool : tous les A (20), tous les S (16), tous les SS (2).
 * Coût : 1 Parchemin Elite par tirage x1, 10 par tirage x10.
 *
 * Par tirage unitaire :
 *   - 100% chance d'obtenir AU MOINS un rang A
 *   - Pity S   : garanti à 150 tirages cumulés sans rang S+
 *   - Pity SS  : garanti à 450 tirages cumulés sans rang SS
 *   - Hors pity : 5% S, 0.3% SS, le reste A
 *
 * Fragments possibles à la place (même logique, probabilités élevées) :
 *   - A : 1-2 frags, B : 1-3 frags, C : 1-5 frags dans le remplissage x10
 *
 * Doublon → fragments : si le personnage est déjà recruté, il se convertit
 * automatiquement en fragments de ce personnage (1 doublon = coût_recrutement / 4).
 */
public class MenuTirage {

    // ── Parchemins ────────────────────────────────────────────────────────
    private int parcheminOrdinaire = 0;
    private int parcheminElite     = 0;

    public int  getParcheminOrdinaire()       { return parcheminOrdinaire; }
    public void setParcheminOrdinaire(int n)  { this.parcheminOrdinaire = n; }
    public int  getParcheminElite()           { return parcheminElite; }
    public void setParcheminElite(int n)      { this.parcheminElite = n; }

    // ── Compteurs pity Elite ──────────────────────────────────────────────
    private int compteurSansSRang   = 0;   // tirages depuis dernier S ou SS
    private int compteurSansSS      = 0;   // tirages depuis dernier SS

    public int  getCompteurSansSRang()       { return compteurSansSRang; }
    public void setCompteurSansSRang(int n)  { this.compteurSansSRang = n; }
    public int  getCompteurSansSS()          { return compteurSansSS; }
    public void setCompteurSansSS(int n)     { this.compteurSansSS = n; }

    private static final int PITY_S  = 150;
    private static final int PITY_SS = 450;

    private static final Random RNG = new Random();

    // ── Pool Tirage Ordinaire ─────────────────────────────────────────────
    // 2 persos A sélectionnés dans la bannière ordinaire
    private static final List<String> POOL_A_ORDINAIRE = List.of("Gray", "Natsu");

    private static final List<String> POOL_B = List.of(
        "Bickslow", "Cana", "Evergreen", "Loke",
        "Levy", "Lisanna", "Elfman Bête"
    );

    private static final List<String> POOL_C = List.of(
        "Alzack", "Bisca", "Elfman", "Max", "Droy",
        "Jet", "Warren", "Nab", "Romeo"
    );

    // ── Pool Tirage Elite ─────────────────────────────────────────────────
    private static final List<String> POOL_A_ELITE = List.of(
        "Angel", "Freed", "Gajeel",
        "Gray", "Jubia", "Lucy",
        "Natsu", "Wendy"
    );

    private static final List<String> POOL_S = List.of(
        "Erza", "Mirajane", "Natsu Etherion",
        "Rogue", "Sting", "Yukino"
    );

    private static final List<String> POOL_SS = List.of(
        "Lucas", "Mirajane Halphas"
    );

    // ── Rarete de chaque perso ────────────────────────────────────────────
    private static String rareteDeNom(String nom) {
        if (POOL_SS.contains(nom))           return "SS";
        if (POOL_S.contains(nom))            return "S";
        if (POOL_A_ELITE.contains(nom))      return "A";
        if (POOL_B.contains(nom))            return "B";
        return "C";
    }

    // ── Menu principal ────────────────────────────────────────────────────
    public void afficher(GameContext ctx, Scanner scanner) {
        boolean retour = false;
        while (!retour) {
            System.out.println("\n========================================");
            System.out.println("            TIRAGES");
            System.out.println("========================================");
            System.out.printf("  Parchemins Ordinaires : %d%n", parcheminOrdinaire);
            System.out.printf("  Parchemins Elite      : %d  (pity S : %d/%d | SS : %d/%d)%n",
                    parcheminElite, compteurSansSRang, PITY_S, compteurSansSS, PITY_SS);
            System.out.println();
            System.out.println("1. Tirage Ordinaire  (pool : C / B / A limité)");
            System.out.println("2. Tirage Elite      (pool : A / S / SS)");
            System.out.println("0. Retour");
            System.out.print("Votre choix : ");

            switch (scanner.nextLine().trim()) {
                case "1" -> menuTirageOrdinaire(ctx, scanner);
                case "2" -> menuTirageElite(ctx, scanner);
                case "0" -> retour = true;
                default  -> System.out.println("Choix invalide.");
            }
        }
    }

    // ════════════════════════════════════════════════════════════════════════
    // TIRAGE ORDINAIRE
    // ════════════════════════════════════════════════════════════════════════
    private static final int COUT_COUPON_ORD_1  = 20;
    private static final int COUT_COUPON_ORD_10 = 200;

    private void menuTirageOrdinaire(GameContext ctx, Scanner scanner) {
        boolean retour = false;
        while (!retour) {
            int coupons = ctx.joueur.getCoupons();
            System.out.println("\n--- TIRAGE ORDINAIRE ---");
            System.out.println("Pool : C (9 persos FT)  |  B (7 persos FT)  |  A (2 persos : Gray, Natsu)");
            System.out.println("Taux par tirage : 100% C minimum  |  20% B  |  2% A");
            System.out.printf("Parchemins Ordinaires : %d  |  Coupons : %d%n", parcheminOrdinaire, coupons);
            System.out.println();
            System.out.println("1. Tirage x1  (1 parchemin ordinaire)");
            System.out.println("2. Tirage x10 (10 parchemins ordinaires)");
            System.out.printf("3. Tirage x1  (%d coupons)%n", COUT_COUPON_ORD_1);
            System.out.printf("4. Tirage x10 (%d coupons)%n", COUT_COUPON_ORD_10);
            System.out.println("0. Retour");
            System.out.print("Votre choix : ");

            switch (scanner.nextLine().trim()) {
                case "1" -> {
                    if (parcheminOrdinaire < 1) System.out.println("Parchemins insuffisants.");
                    else {
                        parcheminOrdinaire--;
                        List<ResultatTirage> res = List.of(tirageOrdinaireUnitaire());
                        afficherResultats(res, ctx); appliquerResultats(res, ctx);
                        ctx.sauvegarde.sauvegarder(ctx);
                    }
                }
                case "2" -> {
                    if (parcheminOrdinaire < 10)
                        System.out.println("Parchemins insuffisants (besoin : 10, vous avez : " + parcheminOrdinaire + ").");
                    else {
                        parcheminOrdinaire -= 10;
                        List<ResultatTirage> res = tirageOrdinaireDix();
                        afficherResultats(res, ctx); appliquerResultats(res, ctx);
                        ctx.sauvegarde.sauvegarder(ctx);
                    }
                }
                case "3" -> {
                    if (coupons < COUT_COUPON_ORD_1)
                        System.out.printf("Coupons insuffisants (besoin : %d, vous avez : %d).%n", COUT_COUPON_ORD_1, coupons);
                    else {
                        ctx.joueur.setCoupons(coupons - COUT_COUPON_ORD_1);
                        List<ResultatTirage> res = List.of(tirageOrdinaireUnitaire());
                        afficherResultats(res, ctx); appliquerResultats(res, ctx);
                        ctx.sauvegarde.sauvegarder(ctx);
                    }
                }
                case "4" -> {
                    if (coupons < COUT_COUPON_ORD_10)
                        System.out.printf("Coupons insuffisants (besoin : %d, vous avez : %d).%n", COUT_COUPON_ORD_10, coupons);
                    else {
                        ctx.joueur.setCoupons(coupons - COUT_COUPON_ORD_10);
                        List<ResultatTirage> res = tirageOrdinaireDix();
                        afficherResultats(res, ctx); appliquerResultats(res, ctx);
                        ctx.sauvegarde.sauvegarder(ctx);
                    }
                }
                case "0" -> retour = true;
                default  -> System.out.println("Choix invalide.");
            }
        }
    }

    /** Effectue un tirage ordinaire unitaire. */
    private ResultatTirage tirageOrdinaireUnitaire() {
        double roll = RNG.nextDouble();

        if (roll < 0.02) {
            // 2% → rang A
            String nom = POOL_A_ORDINAIRE.get(RNG.nextInt(POOL_A_ORDINAIRE.size()));
            if (RNG.nextDouble() < 0.40) {
                // 40% fragments à la place
                int qte = 1 + RNG.nextInt(2); // 1-2
                return ResultatTirage.fragments(nom, "A", qte);
            }
            return ResultatTirage.personnage(nom, "A");

        } else if (roll < 0.22) {
            // 20% → rang B
            String nom = POOL_B.get(RNG.nextInt(POOL_B.size()));
            if (RNG.nextDouble() < 0.50) {
                int qte = 1 + RNG.nextInt(3); // 1-3
                return ResultatTirage.fragments(nom, "B", qte);
            }
            return ResultatTirage.personnage(nom, "B");

        } else {
            // 78% → rang C
            String nom = POOL_C.get(RNG.nextInt(POOL_C.size()));
            if (RNG.nextDouble() < 0.60) {
                int qte = 1 + RNG.nextInt(5); // 1-5
                return ResultatTirage.fragments(nom, "C", qte);
            }
            return ResultatTirage.personnage(nom, "C");
        }
    }

    /** Tirage x10 : garantit au moins 1 B (si aucun B+ dans les 9 premiers). */
    private List<ResultatTirage> tirageOrdinaireDix() {
        List<ResultatTirage> liste = new ArrayList<>();
        boolean aObtenuBOuPlus = false;

        for (int i = 0; i < 9; i++) {
            ResultatTirage r = tirageOrdinaireUnitaire();
            liste.add(r);
            if (!r.rarete.equals("C")) aObtenuBOuPlus = true;
        }

        // 10e tirage : garanti B si rien de mieux n'est sorti
        if (!aObtenuBOuPlus) {
            String nom = POOL_B.get(RNG.nextInt(POOL_B.size()));
            ResultatTirage r;
            if (RNG.nextDouble() < 0.50) {
                int qte = 1 + RNG.nextInt(3);
                r = ResultatTirage.fragments(nom, "B", qte);
            } else {
                r = ResultatTirage.personnage(nom, "B");
            }
            liste.add(r);
        } else {
            liste.add(tirageOrdinaireUnitaire());
        }

        return liste;
    }

    // ════════════════════════════════════════════════════════════════════════
    // TIRAGE ELITE
    // ════════════════════════════════════════════════════════════════════════
    private static final int COUT_COUPON_ELI_1  = 100;
    private static final int COUT_COUPON_ELI_10 = 1000;

    private void menuTirageElite(GameContext ctx, Scanner scanner) {
        boolean retour = false;
        while (!retour) {
            int coupons = ctx.joueur.getCoupons();
            System.out.println("\n--- TIRAGE ELITE ---");
            System.out.printf("Pool : A (8 persos)  |  S (6 persos)  |  SS (2 persos)%n");
            System.out.printf("Taux hors pity : 94.7%% A  |  5%% S  |  0.3%% SS%n");
            System.out.printf("Pity S : %d tirages  |  Pity SS : %d tirages%n", PITY_S, PITY_SS);
            System.out.printf("Parchemins Elite : %d  |  Coupons : %d  |  Pity S : %d/%d  |  Pity SS : %d/%d%n",
                    parcheminElite, coupons, compteurSansSRang, PITY_S, compteurSansSS, PITY_SS);
            System.out.println();
            System.out.println("1. Tirage x1  (1 parchemin elite)");
            System.out.println("2. Tirage x10 (10 parchemins elite)");
            System.out.printf("3. Tirage x1  (%d coupons)%n", COUT_COUPON_ELI_1);
            System.out.printf("4. Tirage x10 (%d coupons)%n", COUT_COUPON_ELI_10);
            System.out.println("0. Retour");
            System.out.print("Votre choix : ");

            switch (scanner.nextLine().trim()) {
                case "1" -> {
                    if (parcheminElite < 1) System.out.println("Parchemins insuffisants.");
                    else {
                        parcheminElite--;
                        List<ResultatTirage> res = List.of(tirageEliteUnitaire());
                        afficherResultats(res, ctx); appliquerResultats(res, ctx);
                        ctx.sauvegarde.sauvegarder(ctx);
                    }
                }
                case "2" -> {
                    if (parcheminElite < 10)
                        System.out.println("Parchemins insuffisants (besoin : 10, vous avez : " + parcheminElite + ").");
                    else {
                        parcheminElite -= 10;
                        List<ResultatTirage> res = tirageEliteDix();
                        afficherResultats(res, ctx); appliquerResultats(res, ctx);
                        ctx.sauvegarde.sauvegarder(ctx);
                    }
                }
                case "3" -> {
                    if (coupons < COUT_COUPON_ELI_1)
                        System.out.printf("Coupons insuffisants (besoin : %d, vous avez : %d).%n", COUT_COUPON_ELI_1, coupons);
                    else {
                        ctx.joueur.setCoupons(coupons - COUT_COUPON_ELI_1);
                        List<ResultatTirage> res = List.of(tirageEliteUnitaire());
                        afficherResultats(res, ctx); appliquerResultats(res, ctx);
                        ctx.sauvegarde.sauvegarder(ctx);
                    }
                }
                case "4" -> {
                    if (coupons < COUT_COUPON_ELI_10)
                        System.out.printf("Coupons insuffisants (besoin : %d, vous avez : %d).%n", COUT_COUPON_ELI_10, coupons);
                    else {
                        ctx.joueur.setCoupons(coupons - COUT_COUPON_ELI_10);
                        List<ResultatTirage> res = tirageEliteDix();
                        afficherResultats(res, ctx); appliquerResultats(res, ctx);
                        ctx.sauvegarde.sauvegarder(ctx);
                    }
                }
                case "0" -> retour = true;
                default  -> System.out.println("Choix invalide.");
            }
        }
    }

    /** Un tirage elite unitaire avec gestion du pity. */
    private ResultatTirage tirageEliteUnitaire() {
        compteurSansSRang++;
        compteurSansSS++;

        String rarete;
        String nom;

        // Pity SS en priorité
        if (compteurSansSS >= PITY_SS) {
            rarete = "SS";
            nom = POOL_SS.get(RNG.nextInt(POOL_SS.size()));
            compteurSansSS  = 0;
            compteurSansSRang = 0;

        } else if (compteurSansSRang >= PITY_S) {
            // Pity S : tirage entre S et SS (10% de chance d'être SS quand pity S déclenche)
            if (RNG.nextDouble() < 0.10) {
                rarete = "SS";
                nom = POOL_SS.get(RNG.nextInt(POOL_SS.size()));
                compteurSansSS = 0;
            } else {
                rarete = "S";
                nom = POOL_S.get(RNG.nextInt(POOL_S.size()));
            }
            compteurSansSRang = 0;

        } else {
            double roll = RNG.nextDouble();
            if (roll < 0.003) {
                rarete = "SS";
                nom = POOL_SS.get(RNG.nextInt(POOL_SS.size()));
                compteurSansSS  = 0;
                compteurSansSRang = 0;
            } else if (roll < 0.053) {
                rarete = "S";
                nom = POOL_S.get(RNG.nextInt(POOL_S.size()));
                compteurSansSRang = 0;
            } else {
                rarete = "A";
                nom = POOL_A_ELITE.get(RNG.nextInt(POOL_A_ELITE.size()));
            }
        }

        // Fragments à la place ?
        double chanceFrags = switch (rarete) {
            case "A"  -> 0.45;
            case "S"  -> 0.35;
            case "SS" -> 0.20;
            default   -> 0.50;
        };
        if (RNG.nextDouble() < chanceFrags) {
            int qte = switch (rarete) {
                case "A"  -> 1 + RNG.nextInt(2);  // 1-2
                case "S"  -> 1;
                case "SS" -> 1;
                default   -> 1 + RNG.nextInt(3);
            };
            return ResultatTirage.fragments(nom, rarete, qte);
        }
        return ResultatTirage.personnage(nom, rarete);
    }

    /** Tirage x10 elite : garantit au moins 1 A par tirage de 10. */
    private List<ResultatTirage> tirageEliteDix() {
        List<ResultatTirage> liste = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            liste.add(tirageEliteUnitaire());
        }
        // Le x10 garantit déjà au moins A puisque le pool minimum est A —
        // tous les résultats sont A ou mieux.
        return liste;
    }

    // ════════════════════════════════════════════════════════════════════════
    // APPLICATION & AFFICHAGE
    // ════════════════════════════════════════════════════════════════════════

    private void afficherResultats(List<ResultatTirage> resultats, GameContext ctx) {
        System.out.println("\n╔══════════════════════════════════════╗");
        System.out.println("║         RESULTATS DU TIRAGE          ║");
        System.out.println("╠══════════════════════════════════════╣");

        for (ResultatTirage r : resultats) {
            boolean dejaRecru = !r.estFragments &&
                    GestionnaireEtoilesPerso.dejaRecruteParNom(ctx.personnagesRecruites, r.nom);
            String suffixe = dejaRecru ? " → DOUBLON (fragments)" : "";

            if (r.estFragments) {
                System.out.printf("║  [%2s] %-22s  x%d frags%s%n",
                        r.rarete, r.nom, r.quantiteFragments, "");
            } else {
                String etoile = switch (r.rarete) {
                    case "SS" -> "★★★★★";
                    case "S"  -> "★★★★ ";
                    case "A"  -> "★★★  ";
                    case "B"  -> "★★   ";
                    default   -> "★    ";
                };
                System.out.printf("║  %s [%2s] %-22s%s%n",
                        etoile, r.rarete, r.nom, suffixe);
            }
        }
        System.out.println("╚══════════════════════════════════════╝");
    }

    private void appliquerResultats(List<ResultatTirage> resultats, GameContext ctx) {
        for (ResultatTirage r : resultats) {
            if (r.estFragments) {
                GestionnaireEtoilesPerso.ajouterFragments(ctx.inventaire, r.nom, r.quantiteFragments);
                System.out.printf("  + %d fragment(s) de %s ajouté(s).%n", r.quantiteFragments, r.nom);
            } else {
                boolean dejaRecru = GestionnaireEtoilesPerso.dejaRecruteParNom(
                        ctx.personnagesRecruites, r.nom);

                if (dejaRecru) {
                    // Doublon → fragments
                    int fragsDoublon = Math.max(1,
                            GestionnaireEtoilesPerso.coutFragmentsRecrutement(r.rarete) / 4);
                    GestionnaireEtoilesPerso.ajouterFragments(ctx.inventaire, r.nom, fragsDoublon);
                    System.out.printf("  [Doublon] %s converti en %d fragment(s).%n",
                            r.nom, fragsDoublon);
                } else {
                    // Nouveau recrutement
                    PersonnageBase nouveau = ctx.sauvegarde.creerPersonnageParNom(r.nom);
                    if (nouveau != null) {
                        ctx.personnagesRecruites.add(nouveau);
                        System.out.printf("  >> %s [%s] a rejoint l'équipe !%n", r.nom, r.rarete);
                    }
                }
            }
        }
    }

    // ── Record interne : résultat d'un tirage ─────────────────────────────
    private static class ResultatTirage {
        final String  nom;
        final String  rarete;
        final boolean estFragments;
        final int     quantiteFragments;

        private ResultatTirage(String nom, String rarete, boolean estFragments, int qte) {
            this.nom               = nom;
            this.rarete            = rarete;
            this.estFragments      = estFragments;
            this.quantiteFragments = qte;
        }

        static ResultatTirage personnage(String nom, String rarete) {
            return new ResultatTirage(nom, rarete, false, 0);
        }

        static ResultatTirage fragments(String nom, String rarete, int qte) {
            return new ResultatTirage(nom, rarete, true, qte);
        }
    }
}