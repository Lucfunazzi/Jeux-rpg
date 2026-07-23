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
import Personnage.FairyTail.perso_Loke;
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
import Equipement.ParcheminXP;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import lancement.MiniJeuPFC;
import lancement.GameContext;

public class MenuRecrutement {

    private static final int PARCHEMINS_REQUIS_C  = 350;
    private static final int PARCHEMINS_REQUIS_B  = 500;
    private static final int PARCHEMINS_REQUIS_A = 1250;
    private static final int COUT_PARCHEMIN_XP_C  = 3;
    private static final int COUT_PARCHEMIN_XP_B  = 8;
    private static final int NIVEAU_REQUIS_PAGE1  = 6;
    private static final int NIVEAU_REQUIS_PAGE2  = 20;
    private static final int NIVEAU_REQUIS_PAGE3 = 40;

    // ── Données statiques des personnages recrutables ─────────────────────
    // Évite d'instancier des objets complets juste pour afficher nom + rôle
    private record InfoPerso(String nom, String role) {}

    private static final List<InfoPerso> PAGE1 = List.of(
        
        
      
       
        new InfoPerso("Duc Everlue", "Tank"),
        new InfoPerso("Eligoal",     "DPS"),
        new InfoPerso("Cherry",      "Support"),
        new InfoPerso("Tobi",        "DPS"),
        new InfoPerso("Yuka",        "Tank")
    );

    private static final List<InfoPerso> PAGE2 = List.of(
       
        new InfoPerso("Cana",        "Support"),
        new InfoPerso("Levy",        "Support"),
        new InfoPerso("Lisanna",     "Support"),
        new InfoPerso("Elfman", "Tank"),
        new InfoPerso("Leon",        "DPS"),
        new InfoPerso("Totomaru",    "DPS"),
        new InfoPerso("Sol",         "Tank")
    );

    private static final List<InfoPerso> PAGE3 = List.of(
        new InfoPerso("Aria",        "DPS"),
        new InfoPerso("Lucy", "Support"),
        new InfoPerso("Jubia (phantom Lord)", "Support")
    );

    private int parcheminC = 0;
    private int parcheminB = 0;
    private int parcheminA = 0;

    private final MiniJeuPFC miniJeu = new MiniJeuPFC();

    /** Accès pour l'interface graphique : {nom, role} par page (1, 2 ou 3). */
    public static List<String[]> getPage(int numero) {
        List<InfoPerso> liste = switch (numero) {
            case 1  -> PAGE1;
            case 2  -> PAGE2;
            default -> PAGE3;
        };
        List<String[]> resultat = new ArrayList<>();
        for (InfoPerso p : liste) resultat.add(new String[]{p.nom(), p.role()});
        return resultat;
    }

    public static int getNiveauRequisPage(int numero) {
        return switch (numero) {
            case 1  -> NIVEAU_REQUIS_PAGE1;
            case 2  -> NIVEAU_REQUIS_PAGE2;
            default -> NIVEAU_REQUIS_PAGE3;
        };
    }

    public static int getParcheminsRequisPage(int numero) {
        return switch (numero) {
            case 1  -> PARCHEMINS_REQUIS_C;
            case 2  -> PARCHEMINS_REQUIS_B;
            default -> PARCHEMINS_REQUIS_A;
        };
    }

    public static String getRangPage(int numero) {
        return switch (numero) { case 1 -> "C"; case 2 -> "B"; default -> "A"; };
    }

    public int getCoutParcheminXpC() { return COUT_PARCHEMIN_XP_C; }
    public int getCoutParcheminXpB() { return COUT_PARCHEMIN_XP_B; }

    public MiniJeuPFC getMiniJeu() { return miniJeu; }

    // ── Menu principal ────────────────────────────────────────────────────
    public void afficher(GameContext ctx, Scanner scanner) {
        Personnage_principale joueur = ctx.joueur;

        if (joueur.getNiveau() < NIVEAU_REQUIS_PAGE1) {
            System.out.println("Le recrutement se debloque au niveau " + NIVEAU_REQUIS_PAGE1 + " !");
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
                    + "  |  Parchemins A : " + parcheminA);
            System.out.println();

            System.out.println((actions.size() + 1) + ". Page 1 — Rang C");
            actions.add(() -> afficherPage(ctx, scanner, "C", PAGE1, PARCHEMINS_REQUIS_C, NIVEAU_REQUIS_PAGE1));

            if (niveau >= NIVEAU_REQUIS_PAGE2) {
                System.out.println((actions.size() + 1) + ". Page 2 — Rang B");
                actions.add(() -> afficherPage(ctx, scanner, "B", PAGE2, PARCHEMINS_REQUIS_B, NIVEAU_REQUIS_PAGE2));
            }
            if (niveau >= NIVEAU_REQUIS_PAGE3) {
                System.out.println((actions.size() + 1) + ". Page 3 — Rang A");
                actions.add(() -> afficherPage(ctx, scanner, "A", PAGE3, PARCHEMINS_REQUIS_A, NIVEAU_REQUIS_PAGE3));
            }

            System.out.println((actions.size() + 1) + ". Acheter des Parchemins XP");
            actions.add(() -> menuAchatParcheminXP(ctx, scanner));

            System.out.println((actions.size() + 1) + ". Mini-jeu PFC — Rang C (" + miniJeu.getCoutPartieC() + " or)");
            actions.add(() -> menuMiniJeu(ctx, scanner, "C"));

            if (niveau >= NIVEAU_REQUIS_PAGE2) {
                System.out.println((actions.size() + 1) + ". Mini-jeu PFC — Rang B (" + miniJeu.getCoutPartieB() + " or)");
                actions.add(() -> menuMiniJeu(ctx, scanner, "B"));
            }
            if (niveau >= NIVEAU_REQUIS_PAGE3) {
                System.out.println((actions.size() + 1) + ". Mini-jeu PFC — Rang A (" + miniJeu.getCoutPartieA() + " or)");
                actions.add(() -> menuMiniJeu(ctx, scanner, "A"));
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
            default  -> miniJeu.getCoutPartieA();
        };

        System.out.println("\n1. Jouer 1 partie (" + coutPartie + " or)");
        System.out.println("2. Jouer 10 parties automatiquement (" + (coutPartie * 10) + " or)");
        System.out.print("Votre choix : ");

        switch (scanner.nextLine().trim()) {
            case "1" -> {
                int gagnes = switch (rang) {
                    case "C" -> miniJeu.jouer(joueur, scanner);
                    case "B" -> miniJeu.jouerB(joueur, scanner);
                    default  -> miniJeu.jouerA(joueur, scanner);
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
                            default  -> miniJeu.jouerAutoA(joueur);
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
            default  -> parcheminA;
        };
    }

    private int getParcheminsRequis(String rang) {
        return switch (rang) {
            case "C" -> PARCHEMINS_REQUIS_C;
            case "B" -> PARCHEMINS_REQUIS_B;
            default  -> PARCHEMINS_REQUIS_A;
        };
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

    // ── Page de recrutement générique (C et B fusionnées) ─────────────────
    private void afficherPage(GameContext ctx, Scanner scanner,
                               String rang, List<InfoPerso> liste,
                               int parcheminsRequis, int niveauRequis) {
        Personnage_principale joueur             = ctx.joueur;
        ArrayList<PersonnageBase> recrutes       = ctx.personnagesRecruites;

        if (joueur.getNiveau() < niveauRequis) {
            System.out.println("Cette page se debloque au niveau " + niveauRequis + " !");
            return;
        }

        boolean retour = false;
        while (!retour) {
            int parcheminsActuels = getParchemins(rang);
            String numeroPage = switch (rang) {
                case "C" -> "1";
                case "B" -> "2";
                default  -> "3";
            };

            System.out.println("\n========================================");
            System.out.println("       PAGE " + numeroPage + " — RANG " + rang);
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
            if (dejaRecruteParNom(cible.nom(), recrutes)) {
                System.out.println(cible.nom() + " est deja dans vos allies !");
                continue;
            }
            if (parcheminsActuels < parcheminsRequis) {
                System.out.println("Pas assez de parchemins " + rang
                        + " ! (" + parcheminsActuels + "/" + parcheminsRequis + ")");
                continue;
            }

            // Vérification de cohérence : creerPersonnage ne doit jamais retourner null
            PersonnageBase recrute = creerPersonnage(cible.nom());
            if (recrute == null) {
                System.out.println("[ERREUR] Personnage introuvable : " + cible.nom()
                        + ". Aucun parchemin deduit.");
                continue;
            }

            switch (rang) {
                case "C" -> parcheminC -= parcheminsRequis;
                case "B" -> parcheminB -= parcheminsRequis;
                default  -> parcheminA -= parcheminsRequis;
            }

            recrutes.add(recrute);
            System.out.println(">> " + recrute.getNom() + " a rejoint vos allies !");
            System.out.println("Parchemins " + rang + " restants : " + getParchemins(rang));
            ctx.sauvegarde.sauvegarder(ctx);
        }
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
            case "Cana"        -> new perso_Kana();
            case "Loke"        -> new perso_Loke();
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
            case "José Porla"  -> new perso_Jose();
            case "Totomaru"    -> new perso_Totomaru();
            case "Sol"         -> new perso_Sol();
            case "Lucy"        -> new perso_Lucy();
            case "Jubia (phantom Lord)" -> new perso_Jubia_4elements();
            default            -> null;
        };
    }

    // ── Utilitaires ───────────────────────────────────────────────────────
    private void ajouterParchemins(String rang, int montant) {
        switch (rang) {
            case "C" -> parcheminC += montant;
            case "B" -> parcheminB += montant;
            default  -> parcheminA += montant;
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
    public void setParcheminC(int v) { this.parcheminC = v; }
    public void setParcheminB(int v) { this.parcheminB = v; }
    public void setParcheminA(int v) { this.parcheminA = v; }

    public void ajouterParcheminC(int montant) { ajouterParchemins("C", montant); }
    public void ajouterParcheminB(int montant) { ajouterParchemins("B", montant); }
    public void ajouterParcheminA(int montant) { ajouterParchemins("A", montant); }
}