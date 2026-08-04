package lancement.Menus;

import Joueur.Personnage_principale;
import Personnage.PersonnageBase;
import Personnage.FairyTail.perso_Aria;
import Personnage.FairyTail.perso_Arzak;
import Personnage.FairyTail.perso_Biska;
import Personnage.FairyTail.perso_Nab;
import Personnage.FairyTail.perso_Levy;
import Personnage.FairyTail.perso_Lisanna;
import Personnage.FairyTail.perso_Bixrow;
import Personnage.FairyTail.perso_Evergreen;
import Personnage.FairyTail.perso_Kana;

import Personnage.FairyTail.perso_Bora;
import Personnage.FairyTail.perso_Cherry;
import Personnage.FairyTail.perso_DucEverlue;
import Personnage.FairyTail.perso_Eligor;
import Personnage.FairyTail.perso_Jose;
import Personnage.FairyTail.perso_Leon;
import Personnage.FairyTail.perso_Sol;
import Personnage.FairyTail.perso_Tobi;
import Personnage.FairyTail.perso_Totomaru;
import Personnage.FairyTail.perso_Yuka;
import Personnage.FairyTail.perso_Elfman;
import Personnage.FairyTail.perso_Lucy;
import Personnage.FairyTail.perso_Jubia_4elements;
import Personnage.FairyTail.perso_Ul;
import Personnage.FairyTail.perso_Ikaruga;
import Personnage.FairyTail.perso_Owl;
import Personnage.FairyTail.perso_Vivaldus;
import Personnage.FairyTail.perso_Miliana;
import Personnage.FairyTail.perso_Wolly;
import Personnage.FairyTail.perso_Simon;
import Personnage.FairyTail.perso_Shaw;
import Personnage.FairyTail.perso_Jura;
import Personnage.FairyTail.perso_Ichiya;
import Personnage.FairyTail.perso_Hibiki;
import Personnage.FairyTail.perso_Ren;
import Personnage.FairyTail.perso_Eve;
import Personnage.FairyTail.perso_PantherLily;
import Personnage.FairyTail.perso_Gildarts;
import Personnage.FairyTail.perso_Loki;
import Personnage.FairyTail.perso_Mest;
import Personnage.FairyTail.perso_Meredy;
import Personnage.FairyTail.perso_Ultear;
import Personnage.FairyTail.perso_Cobra;
import Personnage.FairyTail.perso_Racer;
import Personnage.FairyTail.perso_Hoteye;
import Personnage.FairyTail.perso_Midnight;
import Personnage.FairyTail.perso_Sugarboy;
import Personnage.FairyTail.perso_Hughes;
import Personnage.FairyTail.perso_Byro;
import Personnage.FairyTail.perso_ErzaKnightwalker;
import Personnage.FairyTail.perso_Azuma;
import Personnage.FairyTail.perso_Zancrow;
import Personnage.FairyTail.perso_Rustyrose;
import Personnage.FairyTail.perso_Capricorn;
import Personnage.FairyTail.perso_Zero;
import Personnage.FairyTail.perso_Bluenote;
import Personnage.FairyTail.perso_Zeref;
import Personnage.FairyTail.perso_Acnologia;
import Personnage.FairyTail.perso_Freed;
import Personnage.FairyTail.perso_Angel;
import Personnage.FairyTail.perso_Yamaru;
import Personnage.FairyTail.perso_Kawazu;
import Personnage.FairyTail.perso_Yukino;
import Equipement.ParcheminXP;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import lancement.MiniJeuPFC;
import lancement.GameContext;

public class MenuRecrutement {

    private static final int COUT_PARCHEMIN_XP_C  = 3;
    private static final int COUT_PARCHEMIN_XP_B  = 8;

    // ── Données statiques des personnages recrutables ─────────────────────
    // Évite d'instancier des objets complets juste pour afficher nom + rôle
    private record InfoPerso(String nom, String role) {}

    /** Une page de recrutement : un rang, un niveau de déblocage, un coût unitaire, une liste de persos. */
    private record PageRecrutement(String rang, int niveauRequis, int parcheminsRequis, List<InfoPerso> personnages) {}

    // ── Rang C : 3 pages ────────────────────────────────────────────────────
    private static final PageRecrutement PAGE_C1 = new PageRecrutement("C", 5, 150, List.of(
        new InfoPerso("Alzack",      "DPS"),
        new InfoPerso("Bisca",       "DPS"),
        new InfoPerso("Duc Everlue", "Tank"),
        new InfoPerso("Eligoal",     "DPS")
    ));
    private static final PageRecrutement PAGE_C2 = new PageRecrutement("C", 15, 200, List.of(
        new InfoPerso("Cherry", "Support"),
        new InfoPerso("Tobi",   "DPS"),
        new InfoPerso("Yuka",   "Tank")
    ));
    private static final PageRecrutement PAGE_C3 = new PageRecrutement("C", 20, 250, List.of(
        new InfoPerso("Miliana", "Support"),
        new InfoPerso("Wolly",   "DPS")
    ));

    // ── Rang B : 3 pages ────────────────────────────────────────────────────
    private static final PageRecrutement PAGE_B1 = new PageRecrutement("B", 30, 250, List.of(
        new InfoPerso("Leon",     "DPS"),
        new InfoPerso("Sol",      "Tank"),
        new InfoPerso("Totomaru", "DPS")
    ));
    private static final PageRecrutement PAGE_B2 = new PageRecrutement("B", 35, 300, List.of(
        new InfoPerso("Levy",    "Support"),
        new InfoPerso("Elfman",  "Tank"),
        new InfoPerso("Lisanna", "Support"),
        new InfoPerso("Kana",    "Support")
    ));
    private static final PageRecrutement PAGE_B3 = new PageRecrutement("B", 40, 400, List.of(
        new InfoPerso("Simon", "Tank"),
        new InfoPerso("Shaw",  "Support")
    ));

    // ── Rang A : 5 pages ────────────────────────────────────────────────────
    private static final PageRecrutement PAGE_A1 = new PageRecrutement("A", 40, 300, List.of(
        new InfoPerso("Aria",      "DPS"),
        new InfoPerso("Ikaruga",   "Tank"),
        new InfoPerso("Owl",       "DPS"),
        new InfoPerso("Vivaldus",  "Support")
    ));
    private static final PageRecrutement PAGE_A2 = new PageRecrutement("A", 45, 450, List.of(
        new InfoPerso("Bickslow",  "DPS"),
        new InfoPerso("Evergreen", "Support"),
        new InfoPerso("Freed",     "Support")
    ));
    private static final PageRecrutement PAGE_A3 = new PageRecrutement("A", 55, 600, List.of(
        new InfoPerso("Angel",   "DPS"),
        new InfoPerso("Cobra",   "DPS"),
        new InfoPerso("Hoteye",  "Tank"),
        new InfoPerso("Racer",   "DPS"),
        new InfoPerso("Midnight","DPS"),
        new InfoPerso("Ren",     "DPS"),
        new InfoPerso("Eve",     "Support"),
        new InfoPerso("Hibiki",  "Support")
    ));
    private static final PageRecrutement PAGE_A4 = new PageRecrutement("A", 65, 800, List.of(
        new InfoPerso("Panther Lily", "DPS"),
        new InfoPerso("Byro",         "Support"),
        new InfoPerso("Hughes",       "Support"),
        new InfoPerso("Sugarboy",     "Tank")
    ));
    private static final PageRecrutement PAGE_A5 = new PageRecrutement("A", 70, 1000, List.of(
        new InfoPerso("Meredy", "Support"),
        new InfoPerso("Mest",   "Support"),
        new InfoPerso("Yamaru", "Support"),
        new InfoPerso("Kawazu", "DPS")
    ));

    // ── Rang S : 3 pages ────────────────────────────────────────────────────
    private static final PageRecrutement PAGE_S1 = new PageRecrutement("S", 70, 1000, List.of(
        new InfoPerso("Jura", "Tank"),
        new InfoPerso("Loki", "DPS")
    ));
    private static final PageRecrutement PAGE_S2 = new PageRecrutement("S", 75, 1200, List.of(
        new InfoPerso("Erza Knightwalker", "Tank"),
        new InfoPerso("Zancrow",           "DPS"),
        new InfoPerso("Capricorn",         "DPS"),
        new InfoPerso("Rustyrose",         "Tank")
    ));
    private static final PageRecrutement PAGE_S3 = new PageRecrutement("S", 80, 1500, List.of(
        new InfoPerso("Azuma",  "Tank"),
        new InfoPerso("Ultear", "Support"),
        new InfoPerso("Yukino", "Support")
    ));

    /** Toutes les pages de recrutement, dans l'ordre d'affichage (numéro de page = index + 1). */
    private static final List<PageRecrutement> PAGES = List.of(
        PAGE_C1, PAGE_C2, PAGE_C3,
        PAGE_B1, PAGE_B2, PAGE_B3,
        PAGE_A1, PAGE_A2, PAGE_A3, PAGE_A4, PAGE_A5,
        PAGE_S1, PAGE_S2, PAGE_S3
    );

    private int parcheminC = 0;
    private int parcheminB = 0;
    private int parcheminA = 0;
    private int parcheminS = 0;

    private final MiniJeuPFC miniJeu = new MiniJeuPFC();

    /** Nombre total de pages de recrutement (tous rangs confondus). */
    public static int getNombrePages() { return PAGES.size(); }

    /** Accès pour l'interface graphique : {nom, role} pour la page donnée (1 à getNombrePages()). */
    public static List<String[]> getPage(int numero) {
        List<String[]> resultat = new ArrayList<>();
        for (InfoPerso p : PAGES.get(numero - 1).personnages()) resultat.add(new String[]{p.nom(), p.role()});
        return resultat;
    }

    public static int getNiveauRequisPage(int numero) { return PAGES.get(numero - 1).niveauRequis(); }

    public static int getParcheminsRequisPage(int numero) { return PAGES.get(numero - 1).parcheminsRequis(); }

    public static String getRangPage(int numero) { return PAGES.get(numero - 1).rang(); }

    /** Niveau de déblocage de la première page d'un rang donné (C/B/A/S) — utilisé pour le mini-jeu. */
    public static int getNiveauRequisRang(String rang) {
        return PAGES.stream()
                .filter(p -> p.rang().equals(rang))
                .mapToInt(PageRecrutement::niveauRequis)
                .min()
                .orElse(Integer.MAX_VALUE);
    }

    /** Index de cette page au sein de son rang (1-based) — ex : 2e page de rang C → 2. */
    public static int getIndexPageDansRang(int numero) {
        String rang = getRangPage(numero);
        int index = 0;
        for (int i = 0; i < numero; i++) {
            if (PAGES.get(i).rang().equals(rang)) index++;
        }
        return index;
    }

    /** Nombre total de pages pour un rang donné. */
    public static int getNombrePagesPourRang(String rang) {
        return (int) PAGES.stream().filter(p -> p.rang().equals(rang)).count();
    }

    /** Numéro de la première page (1-based, global) d'un rang donné. */
    public static int getPremierePageDuRang(String rang) {
        for (int i = 0; i < PAGES.size(); i++) {
            if (PAGES.get(i).rang().equals(rang)) return i + 1;
        }
        throw new IllegalArgumentException("Rang inconnu : " + rang);
    }

    /** Tous les personnages {nom, role} de toutes les pages d'un rang donné, dans l'ordre. */
    public static List<String[]> getPersonnagesDuRang(String rang) {
        List<String[]> resultat = new ArrayList<>();
        for (PageRecrutement page : PAGES) {
            if (!page.rang().equals(rang)) continue;
            for (InfoPerso p : page.personnages()) resultat.add(new String[]{p.nom(), p.role()});
        }
        return resultat;
    }

    public int getCoutParcheminXpC() { return COUT_PARCHEMIN_XP_C; }
    public int getCoutParcheminXpB() { return COUT_PARCHEMIN_XP_B; }

    public MiniJeuPFC getMiniJeu() { return miniJeu; }

    // ── Menu principal ────────────────────────────────────────────────────
    public void afficher(GameContext ctx, Scanner scanner) {
        Personnage_principale joueur = ctx.joueur;

        int niveauRequisPremierePage = getNiveauRequisPage(1);
        if (joueur.getNiveau() < niveauRequisPremierePage) {
            System.out.println("Le recrutement se debloque au niveau " + niveauRequisPremierePage + " !");
            return;
        }

        boolean retour = false;
        while (!retour) {
            int niveau = joueur.getNiveau();
            List<Runnable> actions = new ArrayList<>();

            System.out.println("\n========================================");
            System.out.println("          MENU RECRUTEMENT");
            System.out.println("========================================");
            System.out.println("Or : " + String.format("%.0f", joueur.getOr())
                    + "  |  Parchemins C : " + parcheminC
                    + "  |  Parchemins B : " + parcheminB
                    + "  |  Parchemins A : " + parcheminA
                    + "  |  Parchemins S : " + parcheminS);
            System.out.println();

            for (int numero = 1; numero <= getNombrePages(); numero++) {
                int niveauRequisPage = getNiveauRequisPage(numero);
                if (niveau < niveauRequisPage) continue;
                String rang = getRangPage(numero);
                int indexDansRang = getIndexPageDansRang(numero);
                int totalPagesRang = getNombrePagesPourRang(rang);
                List<InfoPerso> personnages = PAGES.get(numero - 1).personnages();
                int parcheminsRequis = getParcheminsRequisPage(numero);

                System.out.println((actions.size() + 1) + ". Rang " + rang + " — Page " + indexDansRang + "/" + totalPagesRang);
                actions.add(() -> afficherPage(ctx, scanner, rang, personnages, parcheminsRequis, niveauRequisPage,
                        indexDansRang, totalPagesRang));
            }

            System.out.println((actions.size() + 1) + ". Acheter des Parchemins XP");
            actions.add(() -> menuAchatParcheminXP(ctx, scanner));

            for (String rang : List.of("C", "B", "A", "S")) {
                if (niveau < getNiveauRequisRang(rang)) continue;
                int coutMinijeu = switch (rang) {
                    case "C" -> miniJeu.getCoutPartieC();
                    case "B" -> miniJeu.getCoutPartieB();
                    case "A" -> miniJeu.getCoutPartieA();
                    default  -> miniJeu.getCoutPartieS();
                };
                System.out.println((actions.size() + 1) + ". Mini-jeu PFC — Rang " + rang + " (" + coutMinijeu + " or)");
                actions.add(() -> menuMiniJeu(ctx, scanner, rang));
            }

            System.out.println("0. Retour");
            System.out.print("Votre choix : ");

            String choix = scanner.nextLine().trim();
            if (choix.equals("0")) { retour = true; continue; }
            try {
                int n = Integer.parseInt(choix);
                if (n >= 1 && n <= actions.size()) actions.get(n - 1).run();
                else System.out.println("Choix invalide.");
            } catch (NumberFormatException e) {
                System.out.println("Choix invalide.");
            }
        }
    }

    // ── Mini-jeu (C, B et A) ────────────────────────────────────────────
    private void menuMiniJeu(GameContext ctx, Scanner scanner, String rang) {
        Personnage_principale joueur = ctx.joueur;
        int coutPartie = switch (rang) {
            case "C" -> miniJeu.getCoutPartieC();
            case "B" -> miniJeu.getCoutPartieB();
            case "A" -> miniJeu.getCoutPartieA();
            default  -> miniJeu.getCoutPartieS();
        };

        System.out.println("\n1. Jouer 1 partie (" + coutPartie + " or)");
        System.out.println("2. Jouer 10 parties automatiquement (" + (coutPartie * 10) + " or)");
        System.out.print("Votre choix : ");

        switch (scanner.nextLine().trim()) {
            case "1" -> {
                int gagnes = switch (rang) {
                    case "C" -> miniJeu.jouer(joueur, scanner);
                    case "B" -> miniJeu.jouerB(joueur, scanner);
                    case "A" -> miniJeu.jouerA(joueur, scanner);
                    default  -> miniJeu.jouerS(joueur, scanner);
                };
                if (gagnes > 0) {
                    ajouterParchemins(rang, gagnes);
                    System.out.println("Parchemins " + rang + " : " + getParchemins(rang) + "/" + getParcheminsRequis(rang));
                    ctx.sauvegarde.sauvegarder(ctx);
                }
            }
            case "2" -> {
                if (joueur.getOr() < coutPartie * 10) {
                    System.out.println("Pas assez d'or ! Il faut " + (coutPartie * 10) + " or.");
                } else {
                    int totalGagnes = 0;
                    for (int i = 0; i < 10; i++) {
                        totalGagnes += switch (rang) {
                            case "C" -> miniJeu.jouerAutoC(joueur);
                            case "B" -> miniJeu.jouerAutoB(joueur);
                            case "A" -> miniJeu.jouerAutoA(joueur);
                            default  -> miniJeu.jouerAutoS(joueur);
                        };
                    }
                    ajouterParchemins(rang, totalGagnes);
                    System.out.println("10 parties terminees !");
                    System.out.println("Total parchemins " + rang + " gagnes : " + totalGagnes);
                    System.out.println("Parchemins " + rang + " : " + getParchemins(rang) + "/" + getParcheminsRequis(rang));
                    ctx.sauvegarde.sauvegarder(ctx);
                }
            }
            default -> System.out.println("Choix invalide.");
        }
    }

    private int getParchemins(String rang) {
        return switch (rang) {
            case "C" -> parcheminC;
            case "B" -> parcheminB;
            case "A" -> parcheminA;
            default  -> parcheminS;
        };
    }

    /** Coût de la moins chère des pages d'un rang (repere affiche apres le mini-jeu). */
    private int getParcheminsRequis(String rang) {
        return PAGES.stream()
                .filter(p -> p.rang().equals(rang))
                .mapToInt(PageRecrutement::parcheminsRequis)
                .min()
                .orElse(0);
    }

    // ── Achat de Parchemins XP ────────────────────────────────────────────
    private void menuAchatParcheminXP(GameContext ctx, Scanner scanner) {
        boolean retour = false;
        while (!retour) {
            int stockC = ctx.inventaire.getQuantiteParcheminXP(ParcheminXP.Rarete.C);
            int stockB = ctx.inventaire.getQuantiteParcheminXP(ParcheminXP.Rarete.B);
            int stockA = ctx.inventaire.getQuantiteParcheminXP(ParcheminXP.Rarete.A);

            System.out.println("\n========================================");
            System.out.println("        ACHAT — PARCHEMINS XP");
            System.out.println("========================================");
            System.out.println("Parchemins C disponibles : " + parcheminC);
            System.out.println("Parchemins B disponibles : " + parcheminB);
            System.out.println();
            System.out.println("1. Parchemin XP [C]  (+500 XP)   — cout : "
                    + COUT_PARCHEMIN_XP_C + " parchemins C    [stock : " + stockC + "]");
            System.out.println("2. Parchemin XP [B]  (+1500 XP)  — cout : "
                    + COUT_PARCHEMIN_XP_B + " parchemins B   [stock : " + stockB + "]");
            System.out.println("3. Parchemin XP [A]  (+5000 XP)  — (a venir)              [stock : " + stockA + "]");
            System.out.println("0. Retour");
            System.out.print("Votre choix : ");

            switch (scanner.nextLine().trim()) {
                case "1" -> acheterParcheminXP(ctx, ParcheminXP.Rarete.C, scanner);
                case "2" -> acheterParcheminXP(ctx, ParcheminXP.Rarete.B, scanner);
                case "3" -> System.out.println("Les parchemins XP [A] ne sont pas encore disponibles.");
                case "0" -> retour = true;
                default  -> System.out.println("Choix invalide.");
            }
        }
    }

    private void acheterParcheminXP(GameContext ctx, ParcheminXP.Rarete rarete, Scanner scanner) {
        boolean estC   = rarete == ParcheminXP.Rarete.C;
        int cout       = estC ? COUT_PARCHEMIN_XP_C : COUT_PARCHEMIN_XP_B;
        int stockDispo = estC ? parcheminC : parcheminB;
        String nomRang = rarete.name();

        System.out.print("Combien de parchemins XP [" + nomRang + "] voulez-vous acheter ? (0 pour annuler) : ");
        int qte;
        try {
            qte = Integer.parseInt(scanner.nextLine().trim());
        } catch (NumberFormatException e) {
            System.out.println("Entree invalide.");
            return;
        }
        if (qte <= 0) return;

        int coutTotal = qte * cout;
        if (stockDispo < coutTotal) {
            System.out.println("Pas assez de parchemins " + nomRang + " ! (il faut "
                    + coutTotal + ", vous avez " + stockDispo + ")");
            return;
        }

        if (estC) parcheminC -= coutTotal;
        else      parcheminB -= coutTotal;

        ctx.inventaire.ajouterParcheminXP(rarete, qte);
        System.out.println(">> " + qte + " Parchemin(s) XP [" + nomRang + "] ajoutes a l'inventaire !");
        System.out.println("   Parchemins " + nomRang + " restants : " + (estC ? parcheminC : parcheminB));
        ctx.sauvegarde.sauvegarder(ctx);
    }

    // ── Page de recrutement générique (toutes pages, tous rangs) ───────────
    private void afficherPage(GameContext ctx, Scanner scanner,
                               String rang, List<InfoPerso> liste,
                               int parcheminsRequis, int niveauRequis,
                               int indexDansRang, int totalPagesRang) {
        Personnage_principale joueur             = ctx.joueur;
        ArrayList<PersonnageBase> recrutes       = ctx.personnagesRecruites;

        if (joueur.getNiveau() < niveauRequis) {
            System.out.println("Cette page se debloque au niveau " + niveauRequis + " !");
            return;
        }

        boolean retour = false;
        while (!retour) {
            int parcheminsActuels = getParchemins(rang);

            System.out.println("\n========================================");
            System.out.println("       RANG " + rang + " — PAGE " + indexDansRang + "/" + totalPagesRang);
            System.out.println("========================================");
            System.out.println("Parchemins " + rang + " : " + parcheminsActuels + "/" + parcheminsRequis);
            System.out.println();

            for (int i = 0; i < liste.size(); i++) {
                InfoPerso p = liste.get(i);
                String statut = dejaRecruteParNom(p.nom(), recrutes) ? "[DEJA RECRUTE]" : "";
                System.out.println((i + 1) + ". " + p.nom()
                        + " [" + p.role() + "] — " + parcheminsRequis
                        + " parchemins " + rang + "  " + statut);
            }
            System.out.println("0. Retour");
            System.out.print("Votre choix : ");

            String ligne = scanner.nextLine().trim();
            if (ligne.equals("0")) { retour = true; continue; }

            int choix;
            try { choix = Integer.parseInt(ligne); }
            catch (NumberFormatException e) { System.out.println("Entree invalide."); continue; }

            if (choix < 1 || choix > liste.size()) { System.out.println("Choix invalide."); continue; }

            InfoPerso cible = liste.get(choix - 1);
            ResultatRecrutement resultat = tenterRecruter(ctx, rang, parcheminsRequis, cible.nom());
            System.out.println(resultat.message());
            if (resultat.succes()) {
                System.out.println("Parchemins " + rang + " restants : " + getParchemins(rang));
            }
        }
    }

    /** Resultat d'une tentative de recrutement, pour affichage console/GUI. */
    public record ResultatRecrutement(boolean succes, String message) {}

    /**
     * Tente de recruter {@code nomPerso} au rang donne : verifie qu'il n'est pas deja recrute et
     * que le stock de parchemins suffit, cree le personnage, debite les parchemins, l'ajoute a
     * l'equipe et sauvegarde. Logique partagee entre afficherPage (console) et
     * EcranPageRecrutementController (GUI), qui ne different que par la mise en forme du message.
     */
    public ResultatRecrutement tenterRecruter(GameContext ctx, String rang, int parcheminsRequis, String nomPerso) {
        if (dejaRecruteParNom(nomPerso, ctx.personnagesRecruites)) {
            return new ResultatRecrutement(false, nomPerso + " est deja dans vos allies !");
        }

        int parcheminsActuels = getParchemins(rang);
        if (parcheminsActuels < parcheminsRequis) {
            return new ResultatRecrutement(false, "Pas assez de parchemins " + rang
                    + " ! (" + parcheminsActuels + "/" + parcheminsRequis + ")");
        }

        // Vérification de cohérence : creerPersonnage ne doit jamais retourner null
        PersonnageBase recrute = creerPersonnage(nomPerso);
        if (recrute == null) {
            return new ResultatRecrutement(false, "[ERREUR] Personnage introuvable : " + nomPerso
                    + ". Aucun parchemin deduit.");
        }

        switch (rang) {
            case "C" -> parcheminC -= parcheminsRequis;
            case "B" -> parcheminB -= parcheminsRequis;
            case "A" -> parcheminA -= parcheminsRequis;
            default  -> parcheminS -= parcheminsRequis;
        }

        ctx.personnagesRecruites.add(recrute);
        ctx.sauvegarde.sauvegarder(ctx);
        return new ResultatRecrutement(true, ">> " + recrute.getNom() + " a rejoint vos allies !");
    }

    // ── Factory ───────────────────────────────────────────────────────────
    public PersonnageBase creerPersonnage(String nom) {
        return switch (nom) {
            case "Alzack"      -> new perso_Arzak();
            case "Bisca"       -> new perso_Biska();
            case "Elfman"      -> new perso_Elfman();
            case "Nab"         -> new perso_Nab();
            case "Bickslow"    -> new perso_Bixrow();
            case "Evergreen"   -> new perso_Evergreen();
            case "Kana"        -> new perso_Kana();
            
            case "Levy"        -> new perso_Levy();
            case "Lisanna"     -> new perso_Lisanna();
            case "Bora"        -> new perso_Bora();
            case "Duc Everlue" -> new perso_DucEverlue();
            case "Eligoal"     -> new perso_Eligor();
            case "Leon"        -> new perso_Leon();
            case "Cherry"      -> new perso_Cherry();
            case "Tobi"        -> new perso_Tobi();
            case "Yuka"        -> new perso_Yuka();
            case "Aria"        -> new perso_Aria();
            case "José Pora"   -> new perso_Jose();
            case "Totomaru"    -> new perso_Totomaru();
            case "Sol"         -> new perso_Sol();
            case "Lucy"        -> new perso_Lucy();
            case "Ul Milkovich" -> new perso_Ul();
            case "Jubia (phantom Lord)" -> new perso_Jubia_4elements();
            case "Ikaruga"     -> new perso_Ikaruga();
            case "Owl"         -> new perso_Owl();
            case "Vivaldus"    -> new perso_Vivaldus();
            case "Miliana"     -> new perso_Miliana();
            case "Wolly"       -> new perso_Wolly();
            case "Simon"       -> new perso_Simon();
            case "Shaw"        -> new perso_Shaw();
            case "Jura"        -> new perso_Jura();
            case "Ichiya"      -> new perso_Ichiya();
            case "Hibiki"      -> new perso_Hibiki();
            case "Ren"         -> new perso_Ren();
            case "Eve"         -> new perso_Eve();
            case "Panther Lily" -> new perso_PantherLily();
            case "Gildarts"     -> new perso_Gildarts();
            case "Loki"         -> new perso_Loki();
            case "Mest"         -> new perso_Mest();
            case "Meredy"       -> new perso_Meredy();
            case "Ultear"       -> new perso_Ultear();
            case "Cobra"        -> new perso_Cobra();
            case "Racer"        -> new perso_Racer();
            case "Hoteye"       -> new perso_Hoteye();
            case "Midnight"     -> new perso_Midnight();
            case "Sugarboy"     -> new perso_Sugarboy();
            case "Hughes"       -> new perso_Hughes();
            case "Byro"         -> new perso_Byro();
            case "Freed"        -> new perso_Freed();
            case "Angel"        -> new perso_Angel();
            case "Yamaru"       -> new perso_Yamaru();
            case "Kawazu"       -> new perso_Kawazu();
            case "Erza Knightwalker" -> new perso_ErzaKnightwalker();
            case "Azuma"        -> new perso_Azuma();
            case "Zancrow"      -> new perso_Zancrow();
            case "Rustyrose"    -> new perso_Rustyrose();
            case "Capricorn"    -> new perso_Capricorn();
            case "Yukino"       -> new perso_Yukino();
            case "Zero"         -> new perso_Zero();
            case "Bluenote"     -> new perso_Bluenote();
            case "Zeref"        -> new perso_Zeref();
            case "Acnologia"    -> new perso_Acnologia();
            default            -> null;
        };
    }

    // ── Utilitaires ───────────────────────────────────────────────────────
    private void ajouterParchemins(String rang, int montant) {
        switch (rang) {
            case "C" -> parcheminC += montant;
            case "B" -> parcheminB += montant;
            case "A" -> parcheminA += montant;
            default  -> parcheminS += montant;
        }
        System.out.println("+ " + montant + " parchemins " + rang + " ! (Total : " + getParchemins(rang) + ")");
    }

    private boolean dejaRecruteParNom(String nom, ArrayList<PersonnageBase> liste) {
        return liste.stream().anyMatch(p -> p.getNom().equalsIgnoreCase(nom));
    }

    // ── Getters / Setters (sauvegarde) ────────────────────────────────────
    public int getParcheminC() { return parcheminC; }
    public int getParcheminB() { return parcheminB; }
    public int getParcheminA() { return parcheminA; }
    public int getParcheminS() { return parcheminS; }
    public void setParcheminC(int v) { this.parcheminC = v; }
    public void setParcheminB(int v) { this.parcheminB = v; }
    public void setParcheminA(int v) { this.parcheminA = v; }
    public void setParcheminS(int v) { this.parcheminS = v; }

    public void ajouterParcheminC(int montant) { ajouterParchemins("C", montant); }
    public void ajouterParcheminB(int montant) { ajouterParchemins("B", montant); }
    public void ajouterParcheminA(int montant) { ajouterParchemins("A", montant); }
    public void ajouterParcheminS(int montant) { ajouterParchemins("S", montant); }
}