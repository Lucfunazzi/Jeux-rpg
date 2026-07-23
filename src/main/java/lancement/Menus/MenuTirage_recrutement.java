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
 * Ne donne jamais de personnage complet de rang B ou A — uniquement des
 * fragments, sauf 2% de chance d'obtenir un personnage complet de rang C.
 *
 * Par tirage unitaire :
 *   -  2% : personnage complet rang C
 *   - 70% : fragments rang C (2-5)
 *   - 23% : fragments rang B (1-3)
 *   -  5% : fragments rang A (1-2)
 *
 * ── TIRAGE ELITE ────────────────────────────────────────────────────────────
 * Hors pity, ne donne jamais de personnage complet — uniquement des fragments.
 * Trois pity indépendants, chacun ne se remet à zéro que lorsqu'il se déclenche
 * lui-même (si plusieurs seuils tombent sur le même tirage, tous se déclenchent) :
 *   - tous les 10 tirages  : 1 personnage complet rang A aléatoire
 *   - tous les 150 tirages : 1 personnage complet rang S aléatoire
 *   - tous les 500 tirages : 1 personnage complet rang SS aléatoire
 *
 * Fragments hors déclenchement de pity (un seul tirage aléatoire parmi) :
 *   - 55% rang B  (2-5 frags)
 *   - 35% rang A  (1-4 frags)
 *   -  9% rang S  (1-2 frags)
 *   -  1% rang SS (1 frag, très rare)
 *
 * Doublon → fragments : si le personnage est déjà recruté, il se convertit
 * automatiquement en fragments de ce personnage (1 doublon = coût_recrutement plein).
 */
public class MenuTirage_recrutement {

    // ── Parchemins ────────────────────────────────────────────────────────
    private int parcheminOrdinaire = 0;
    private int parcheminElite     = 0;

    public int  getParcheminOrdinaire()       { return parcheminOrdinaire; }
    public void setParcheminOrdinaire(int n)  { this.parcheminOrdinaire = n; }
    public int  getParcheminElite()           { return parcheminElite; }
    public void setParcheminElite(int n)      { this.parcheminElite = n; }

    // ── Compteurs pity Elite (indépendants les uns des autres) ────────────
    private int compteurPityA  = 0;   // tirages depuis le dernier rang A garanti
    private int compteurPityS  = 0;   // tirages depuis le dernier rang S garanti
    private int compteurPitySS = 0;   // tirages depuis le dernier rang SS garanti

    public int  getCompteurPityA()      { return compteurPityA; }
    public void setCompteurPityA(int n) { this.compteurPityA = n; }
    public int  getCompteurPityS()      { return compteurPityS; }
    public void setCompteurPityS(int n) { this.compteurPityS = n; }
    public int  getCompteurPitySS()     { return compteurPitySS; }
    public void setCompteurPitySS(int n){ this.compteurPitySS = n; }

    private static final int PITY_A  = 10;
    private static final int PITY_S  = 150;
    private static final int PITY_SS = 500;

    private static final Random RNG = new Random();

    // ── Pool Tirage Ordinaire ─────────────────────────────────────────────
    private static final List<String> POOL_A_ORDINAIRE = List.of("Gray", "Natsu");

    private static final List<String> POOL_B = List.of(
        "Cana", 
        "Levy", "Lisanna","Elfman","Leon","Totomaru","Sol"
    );

    private static final List<String> POOL_C = List.of(
        "Alzack", "Bisca","Bora","Eligoal","Duc Everlue","Cherry","Yuka","Tobi"
        
    );

    // ── Pool Tirage Elite ─────────────────────────────────────────────────
    private static final List<String> POOL_A_ELITE = List.of(
        "Angel", "Freed", "Gajeel",
        "Gray", "Jubia (phantom lord)", "Lucy",
        "Wendy","Bickslow","Evergreen","Aria"
    );

    private static final List<String> POOL_S = List.of(
        "Erza",
        "Rogue", "Sting", "Yukino"
    );

    private static final List<String> POOL_SS = List.of(
        "Lucas"
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
            System.out.printf("  Parchemins Elite      : %d  (pity A : %d/%d | S : %d/%d | SS : %d/%d)%n",
                    parcheminElite, compteurPityA, PITY_A, compteurPityS, PITY_S, compteurPitySS, PITY_SS);
            System.out.println();
            System.out.println("1. Tirage Ordinaire  (fragments C/B/A, 2% perso complet C)");
            System.out.println("2. Tirage Elite      (fragments B/A/S/SS, pity A/S/SS)");
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
            System.out.println("Jamais de perso complet B/A — uniquement des fragments.");
            System.out.println("Taux par tirage : 2% perso complet C  |  70% frags C  |  23% frags B  |  5% frags A");
            System.out.printf("Parchemins Ordinaires : %d  |  Coupons : %d%n", parcheminOrdinaire, coupons);
            System.out.println();
            System.out.println("1. Tirage x1  (1 parchemin ordinaire)");
            System.out.println("2. Tirage x10 (10 parchemins ordinaires)");
            System.out.printf("3. Tirage x1  (%d coupons)%n", COUT_COUPON_ORD_1);
            System.out.printf("4. Tirage x10 (%d coupons)%n", COUT_COUPON_ORD_10);
            System.out.println("0. Retour");
            System.out.print("Votre choix : ");

            switch (scanner.nextLine().trim()) {
                case "1" -> imprimerResultat(() -> tirageOrdinaireX1(ctx));
                case "2" -> imprimerResultat(() -> tirageOrdinaireX10(ctx));
                case "3" -> imprimerResultat(() -> tirageOrdinaireCouponX1(ctx));
                case "4" -> imprimerResultat(() -> tirageOrdinaireCouponX10(ctx));
                case "0" -> retour = true;
                default  -> System.out.println("Choix invalide.");
            }
        }
    }

    /** Effectue un tirage ordinaire unitaire : 2% perso complet C, sinon fragments C/B/A. */
    private ResultatTirage tirageOrdinaireUnitaire() {
        double roll = RNG.nextDouble();

        if (roll < 0.02) {
            // 2% → personnage complet rang C
            String nom = POOL_C.get(RNG.nextInt(POOL_C.size()));
            return ResultatTirage.personnage(nom, "C");

        } else if (roll < 0.72) {
            // 70% → fragments rang C (2-5)
            String nom = POOL_C.get(RNG.nextInt(POOL_C.size()));
            int qte = 2 + RNG.nextInt(4); // 2-5
            return ResultatTirage.fragments(nom, "C", qte);

        } else if (roll < 0.95) {
            // 23% → fragments rang B (1-3)
            String nom = POOL_B.get(RNG.nextInt(POOL_B.size()));
            int qte = 1 + RNG.nextInt(3); // 1-3
            return ResultatTirage.fragments(nom, "B", qte);

        } else {
            // 5% → fragments rang A (1-2)
            String nom = POOL_A_ORDINAIRE.get(RNG.nextInt(POOL_A_ORDINAIRE.size()));
            int qte = 1 + RNG.nextInt(2); // 1-2
            return ResultatTirage.fragments(nom, "A", qte);
        }
    }

    private List<ResultatTirage> tirageOrdinaireDix() {
        List<ResultatTirage> liste = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
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
            System.out.println("Jamais de perso complet hors pity — uniquement des fragments.");
            System.out.printf("Fragments : 55%% B  |  35%% A  |  9%% S  |  1%% SS (tres rare)%n");
            System.out.printf("Pity A : tous les %d  |  Pity S : tous les %d  |  Pity SS : tous les %d%n",
                    PITY_A, PITY_S, PITY_SS);
            System.out.printf("Parchemins Elite : %d  |  Coupons : %d  |  Pity A : %d/%d  |  S : %d/%d  |  SS : %d/%d%n",
                    parcheminElite, coupons, compteurPityA, PITY_A, compteurPityS, PITY_S, compteurPitySS, PITY_SS);
            System.out.println();
            System.out.println("1. Tirage x1  (1 parchemin elite)");
            System.out.println("2. Tirage x10 (10 parchemins elite)");
            System.out.printf("3. Tirage x1  (%d coupons)%n", COUT_COUPON_ELI_1);
            System.out.printf("4. Tirage x10 (%d coupons)%n", COUT_COUPON_ELI_10);
            System.out.println("0. Retour");
            System.out.print("Votre choix : ");

            switch (scanner.nextLine().trim()) {
                case "1" -> imprimerResultat(() -> tirageEliteX1(ctx));
                case "2" -> imprimerResultat(() -> tirageEliteX10(ctx));
                case "3" -> imprimerResultat(() -> tirageEliteCouponX1(ctx));
                case "4" -> imprimerResultat(() -> tirageEliteCouponX10(ctx));
                case "0" -> retour = true;
                default  -> System.out.println("Choix invalide.");
            }
        }
    }

    /** Execute un tirage et imprime son resultat (ou le message d'erreur si ressources insuffisantes). */
    private void imprimerResultat(java.util.function.Supplier<List<LigneResultat>> action) {
        try {
            System.out.println(formaterResultats(action.get()));
        } catch (TirageInsuffisantException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * Un tirage elite unitaire. Hors pity : fragments uniquement.
     * Chaque pity (A/S/SS) est indépendant ; si plusieurs seuils tombent sur
     * le même tirage, tous se déclenchent (le tirage renvoie alors plusieurs
     * personnages complets).
     */
    private List<ResultatTirage> tirageEliteUnitaire() {
        compteurPityA++;
        compteurPityS++;
        compteurPitySS++;

        List<ResultatTirage> resultats = new ArrayList<>();

        if (compteurPityA >= PITY_A) {
            String nom = POOL_A_ELITE.get(RNG.nextInt(POOL_A_ELITE.size()));
            resultats.add(ResultatTirage.personnage(nom, "A"));
            compteurPityA = 0;
        }
        if (compteurPityS >= PITY_S) {
            String nom = POOL_S.get(RNG.nextInt(POOL_S.size()));
            resultats.add(ResultatTirage.personnage(nom, "S"));
            compteurPityS = 0;
        }
        if (compteurPitySS >= PITY_SS) {
            String nom = POOL_SS.get(RNG.nextInt(POOL_SS.size()));
            resultats.add(ResultatTirage.personnage(nom, "SS"));
            compteurPitySS = 0;
        }

        if (!resultats.isEmpty()) return resultats;

        // Hors pity : fragments uniquement
        double roll = RNG.nextDouble();
        if (roll < 0.55) {
            String nom = POOL_B.get(RNG.nextInt(POOL_B.size()));
            int qte = 2 + RNG.nextInt(4); // 2-5
            resultats.add(ResultatTirage.fragments(nom, "B", qte));
        } else if (roll < 0.90) {
            String nom = POOL_A_ELITE.get(RNG.nextInt(POOL_A_ELITE.size()));
            int qte = 1 + RNG.nextInt(4); // 1-4
            resultats.add(ResultatTirage.fragments(nom, "A", qte));
        } else if (roll < 0.99) {
            String nom = POOL_S.get(RNG.nextInt(POOL_S.size()));
            int qte = 1 + RNG.nextInt(2); // 1-2
            resultats.add(ResultatTirage.fragments(nom, "S", qte));
        } else {
            String nom = POOL_SS.get(RNG.nextInt(POOL_SS.size()));
            resultats.add(ResultatTirage.fragments(nom, "SS", 1)); // toujours 1, tres rare
        }
        return resultats;
    }

    private List<ResultatTirage> tirageEliteDix() {
        List<ResultatTirage> liste = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            liste.addAll(tirageEliteUnitaire());
        }
        return liste;
    }

    // ════════════════════════════════════════════════════════════════════════
    // ACCES PUBLIC (console + interface graphique)
    // ════════════════════════════════════════════════════════════════════════

    public List<LigneResultat> tirageOrdinaireX1(GameContext ctx) {
        if (parcheminOrdinaire < 1) throw new TirageInsuffisantException("Parchemins insuffisants.");
        parcheminOrdinaire--;
        return executerTirage(List.of(tirageOrdinaireUnitaire()), ctx);
    }

    public List<LigneResultat> tirageOrdinaireX10(GameContext ctx) {
        if (parcheminOrdinaire < 10)
            throw new TirageInsuffisantException("Parchemins insuffisants (besoin : 10, vous avez : " + parcheminOrdinaire + ").");
        parcheminOrdinaire -= 10;
        return executerTirage(tirageOrdinaireDix(), ctx);
    }

    public List<LigneResultat> tirageOrdinaireCouponX1(GameContext ctx) {
        int coupons = ctx.joueur.getCoupons();
        if (coupons < COUT_COUPON_ORD_1)
            throw new TirageInsuffisantException(String.format("Coupons insuffisants (besoin : %d, vous avez : %d).", COUT_COUPON_ORD_1, coupons));
        ctx.joueur.setCoupons(coupons - COUT_COUPON_ORD_1);
        return executerTirage(List.of(tirageOrdinaireUnitaire()), ctx);
    }

    public List<LigneResultat> tirageOrdinaireCouponX10(GameContext ctx) {
        int coupons = ctx.joueur.getCoupons();
        if (coupons < COUT_COUPON_ORD_10)
            throw new TirageInsuffisantException(String.format("Coupons insuffisants (besoin : %d, vous avez : %d).", COUT_COUPON_ORD_10, coupons));
        ctx.joueur.setCoupons(coupons - COUT_COUPON_ORD_10);
        return executerTirage(tirageOrdinaireDix(), ctx);
    }

    public List<LigneResultat> tirageEliteX1(GameContext ctx) {
        if (parcheminElite < 1) throw new TirageInsuffisantException("Parchemins insuffisants.");
        parcheminElite--;
        return executerTirage(tirageEliteUnitaire(), ctx);
    }

    public List<LigneResultat> tirageEliteX10(GameContext ctx) {
        if (parcheminElite < 10)
            throw new TirageInsuffisantException("Parchemins insuffisants (besoin : 10, vous avez : " + parcheminElite + ").");
        parcheminElite -= 10;
        return executerTirage(tirageEliteDix(), ctx);
    }

    public List<LigneResultat> tirageEliteCouponX1(GameContext ctx) {
        int coupons = ctx.joueur.getCoupons();
        if (coupons < COUT_COUPON_ELI_1)
            throw new TirageInsuffisantException(String.format("Coupons insuffisants (besoin : %d, vous avez : %d).", COUT_COUPON_ELI_1, coupons));
        ctx.joueur.setCoupons(coupons - COUT_COUPON_ELI_1);
        return executerTirage(tirageEliteUnitaire(), ctx);
    }

    public List<LigneResultat> tirageEliteCouponX10(GameContext ctx) {
        int coupons = ctx.joueur.getCoupons();
        if (coupons < COUT_COUPON_ELI_10)
            throw new TirageInsuffisantException(String.format("Coupons insuffisants (besoin : %d, vous avez : %d).", COUT_COUPON_ELI_10, coupons));
        ctx.joueur.setCoupons(coupons - COUT_COUPON_ELI_10);
        return executerTirage(tirageEliteDix(), ctx);
    }

    public int getCoutCouponOrdX1()  { return COUT_COUPON_ORD_1; }
    public int getCoutCouponOrdX10() { return COUT_COUPON_ORD_10; }
    public int getCoutCouponEliX1()  { return COUT_COUPON_ELI_1; }
    public int getCoutCouponEliX10() { return COUT_COUPON_ELI_10; }
    public int getPityA()  { return PITY_A; }
    public int getPityS()  { return PITY_S; }
    public int getPitySS() { return PITY_SS; }

    /**
     * Determine le statut doublon de chaque resultat (avant application), applique les
     * gains (fragments/recrutement), sauvegarde, puis retourne les lignes pretes a afficher.
     */
    private List<LigneResultat> executerTirage(List<ResultatTirage> resultats, GameContext ctx) {
        List<LigneResultat> lignes = new ArrayList<>();
        for (ResultatTirage r : resultats) {
            boolean doublon = !r.estFragments
                    && GestionnaireEtoilesPerso.dejaRecruteParNom(ctx.personnagesRecruites, r.nom);
            lignes.add(new LigneResultat(r, doublon));
        }
        appliquerResultats(resultats, ctx);
        ctx.sauvegarde.sauvegarder(ctx);
        return lignes;
    }

    // ════════════════════════════════════════════════════════════════════════
    // APPLICATION & AFFICHAGE
    // ════════════════════════════════════════════════════════════════════════

    private String formaterResultats(List<LigneResultat> lignes) {
        StringBuilder sb = new StringBuilder("RESULTATS DU TIRAGE\n\n");

        for (LigneResultat l : lignes) {
            ResultatTirage r = l.resultat;
            String suffixe = l.doublon ? "  -> DOUBLON (fragments)" : "";

            if (r.estFragments) {
                sb.append(String.format("[%2s] %-22s  x%d frags%n", r.rarete, r.nom, r.quantiteFragments));
            } else {
                String etoile = switch (r.rarete) {
                    case "SS" -> "*****";
                    case "S"  -> "**** ";
                    case "A"  -> "***  ";
                    case "B"  -> "**   ";
                    default   -> "*    ";
                };
                sb.append(String.format("%s [%2s] %-22s%s%n", etoile, r.rarete, r.nom, suffixe));
            }
        }
        return sb.toString().trim();
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
                    // Doublon → fragments (montant plein = cout de recrutement)
                    int fragsDoublon = GestionnaireEtoilesPerso.coutFragmentsRecrutement(r.rarete);
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

    // ── Résultat d'un tirage (public : consommé par la console et l'UI graphique) ──
    public static final class ResultatTirage {
        public final String  nom;
        public final String  rarete;
        public final boolean estFragments;
        public final int     quantiteFragments;

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

    /** Un résultat de tirage accompagné de son statut doublon, calculé avant application des gains. */
    public static final class LigneResultat {
        public final ResultatTirage resultat;
        public final boolean doublon;

        LigneResultat(ResultatTirage resultat, boolean doublon) {
            this.resultat = resultat;
            this.doublon  = doublon;
        }
    }

    /** Levée quand le joueur n'a pas assez de parchemins/coupons pour lancer un tirage. */
    public static final class TirageInsuffisantException extends RuntimeException {
        public TirageInsuffisantException(String message) { super(message); }
    }
}
