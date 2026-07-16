package lancement.Menus;

import Joueur.Personnage_principale;
import Personnage.PersonnageBase;
import Personnage.Naruto_Shippuden.perso_Kiba;
import Personnage.Naruto_Shippuden.perso_Shino;
import Personnage.DragonBallZ.perso_Tien;
import Personnage.DragonBallZ.perso_Chiaotzu;
import Personnage.DragonBallZ.perso_Krillin;
import Personnage.DragonBallZ.perso_Raditz;
import Personnage.FairyTail.perso_Kana;
import Personnage.FairyTail.perso_Elfman;
import Personnage.Naruto_Shippuden.perso_Choji;
import Personnage.Naruto_Shippuden.perso_Hinata;
import Personnage.Naruto_Shippuden.perso_Ino;
import Personnage.Naruto_Shippuden.perso_Kabuto;
import Personnage.Naruto_Shippuden.perso_Kankuro;
import Personnage.Naruto_Shippuden.perso_Temari;
import Equipement.ParcheminXP;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import lancement.MiniJeuPFC;
import lancement.GameContext;

public class MenuRecrutement {

    private static final int PARCHEMINS_REQUIS_C  = 100;
    private static final int PARCHEMINS_REQUIS_B  = 250;
    private static final int COUT_PARCHEMIN_XP_C  = 3;
    private static final int COUT_PARCHEMIN_XP_B  = 8;
    private static final int NIVEAU_REQUIS_PAGE1  = 6;
    private static final int NIVEAU_REQUIS_PAGE2  = 20;

    // ── Données statiques des personnages recrutables ─────────────────────
    // Évite d'instancier des objets complets juste pour afficher nom + rôle
    private record InfoPerso(String nom, String role) {}

    private static final List<InfoPerso> PAGE1 = List.of(
        new InfoPerso("Kiba",     "DPS"),
        new InfoPerso("Shino",    "DPS"),
        new InfoPerso("Tien",     "Tank"),
        new InfoPerso("Chiaotzu", "Support"),
        new InfoPerso("Elfman",   "DPS")
    );

    private static final List<InfoPerso> PAGE2 = List.of(
        new InfoPerso("Cana",    "Support"),
        new InfoPerso("Krillin", "DPS"),
        new InfoPerso("Raditz",  "DPS"),
        new InfoPerso("Kabuto",  "Support"),
        new InfoPerso("Kankuro", "DPS"),
        new InfoPerso("Temari",  "DPS"),
        new InfoPerso("Ino",     "Support"),
        new InfoPerso("Choji",   "Tank"),
        new InfoPerso("Hinata",  "Tank")
    );

    private int parcheminC = 0;
    private int parcheminB = 0;

    private final MiniJeuPFC miniJeu = new MiniJeuPFC();

    // ── Menu principal ────────────────────────────────────────────────────
    public void afficher(GameContext ctx, Scanner scanner) {
        Personnage_principale joueur = ctx.joueur;

        if (joueur.getNiveau() < NIVEAU_REQUIS_PAGE1) {
            System.out.println("Le recrutement se debloque au niveau " + NIVEAU_REQUIS_PAGE1 + " !");
            return;
        }

        boolean retour = false;
        while (!retour) {
            System.out.println("\n========================================");
            System.out.println("          MENU RECRUTEMENT");
            System.out.println("========================================");
            System.out.println("Or : " + String.format("%.0f", joueur.getOr())
                    + "  |  Parchemins C : " + parcheminC
                    + "  |  Parchemins B : " + parcheminB);
            System.out.println();
            System.out.println("1. Page 1 — Rang C  (Niv." + NIVEAU_REQUIS_PAGE1 + " requis)");
            System.out.println("2. Page 2 — Rang B  (Niv." + NIVEAU_REQUIS_PAGE2 + " requis)");
            System.out.println("3. Page 3 — Rang A  (a venir)");
            System.out.println("4. Acheter des Parchemins XP");
            System.out.println("5. Mini-jeu PFC — Rang C (" + miniJeu.getCoutPartieC() + " or)");
            System.out.println("6. Mini-jeu PFC — Rang B (" + miniJeu.getCoutPartieB() + " or)");
            System.out.println("0. Retour");
            System.out.print("Votre choix : ");

            switch (scanner.nextLine().trim()) {
                case "1" -> afficherPage(ctx, scanner, "C", PAGE1,
                                          PARCHEMINS_REQUIS_C, NIVEAU_REQUIS_PAGE1);
                case "2" -> afficherPage(ctx, scanner, "B", PAGE2,
                                          PARCHEMINS_REQUIS_B, NIVEAU_REQUIS_PAGE2);
                case "3" -> afficherPage3();
                case "4" -> menuAchatParcheminXP(ctx, scanner);
                case "5" -> menuMiniJeu(ctx, scanner, "C");
                case "6" -> menuMiniJeu(ctx, scanner, "B");
                case "0" -> retour = true;
                default  -> System.out.println("Choix invalide.");
            }
        }
    }

    // ── Mini-jeu (C et B fusionnés) ───────────────────────────────────────
    private void menuMiniJeu(GameContext ctx, Scanner scanner, String rang) {
        Personnage_principale joueur = ctx.joueur;
        int coutPartie = rang.equals("C") ? miniJeu.getCoutPartieC() : miniJeu.getCoutPartieB();

        System.out.println("\n1. Jouer 1 partie (" + coutPartie + " or)");
        System.out.println("2. Jouer 10 parties automatiquement (" + (coutPartie * 10) + " or)");
        System.out.print("Votre choix : ");

        switch (scanner.nextLine().trim()) {
            case "1" -> {
                int gagnes = rang.equals("C")
                        ? miniJeu.jouer(joueur, scanner)
                        : miniJeu.jouerB(joueur, scanner);
                if (gagnes > 0) {
                    ajouterParchemins(rang, gagnes);
                    int total = rang.equals("C") ? parcheminC : parcheminB;
                    int requis = rang.equals("C") ? PARCHEMINS_REQUIS_C : PARCHEMINS_REQUIS_B;
                    System.out.println("Parchemins " + rang + " : " + total + "/" + requis);
                    ctx.sauvegarde.sauvegarder(ctx);
                }
            }
            case "2" -> {
                if (joueur.getOr() < coutPartie * 10) {
                    System.out.println("Pas assez d'or ! Il faut " + (coutPartie * 10) + " or.");
                } else {
                    int totalGagnes = 0;
                    for (int i = 0; i < 10; i++) {
                        totalGagnes += rang.equals("C")
                                ? miniJeu.jouerAutoC(joueur)
                                : miniJeu.jouerAutoB(joueur);
                    }
                    ajouterParchemins(rang, totalGagnes);
                    int total = rang.equals("C") ? parcheminC : parcheminB;
                    int requis = rang.equals("C") ? PARCHEMINS_REQUIS_C : PARCHEMINS_REQUIS_B;
                    System.out.println("10 parties terminees !");
                    System.out.println("Total parchemins " + rang + " gagnes : " + totalGagnes);
                    System.out.println("Parchemins " + rang + " : " + total + "/" + requis);
                    ctx.sauvegarde.sauvegarder(ctx);
                }
            }
            default -> System.out.println("Choix invalide.");
        }
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
            int parcheminsActuels = rang.equals("C") ? parcheminC : parcheminB;

            System.out.println("\n========================================");
            System.out.println("       PAGE " + (rang.equals("C") ? "1" : "2") + " — RANG " + rang);
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

            if (rang.equals("C")) parcheminC -= parcheminsRequis;
            else                  parcheminB -= parcheminsRequis;

            recrutes.add(recrute);
            System.out.println(">> " + recrute.getNom() + " a rejoint vos allies !");
            System.out.println("Parchemins " + rang + " restants : "
                    + (rang.equals("C") ? parcheminC : parcheminB));
            ctx.sauvegarde.sauvegarder(ctx);
        }
    }

    // ── Page 3 — Rang A (vide pour l'instant) ─────────────────────────────
    private void afficherPage3() {
        System.out.println("\n========================================");
        System.out.println("       PAGE 3 — RANG A");
        System.out.println("========================================");
        System.out.println("  Aucun personnage disponible pour le moment.");
        System.out.println("  (Contenu a venir dans une future mise a jour)");
    }

    // ── Factory ───────────────────────────────────────────────────────────
    private PersonnageBase creerPersonnage(String nom) {
        return switch (nom) {
            case "Kiba"     -> new perso_Kiba();
            case "Shino"    -> new perso_Shino();
            case "Tien"     -> new perso_Tien();
            case "Chiaotzu" -> new perso_Chiaotzu();
            case "Elfman"   -> new perso_Elfman();
            case "Cana"     -> new perso_Kana();
            case "Krillin"  -> new perso_Krillin();
            case "Raditz"   -> new perso_Raditz();
            case "Kabuto"   -> new perso_Kabuto();
            case "Kankuro"  -> new perso_Kankuro();
            case "Temari"   -> new perso_Temari();
            case "Ino"      -> new perso_Ino();
            case "Choji"    -> new perso_Choji();
            case "Hinata"   -> new perso_Hinata();
            default         -> null;
        };
    }

    // ── Utilitaires ───────────────────────────────────────────────────────
    private void ajouterParchemins(String rang, int montant) {
        if (rang.equals("C")) {
            parcheminC += montant;
            System.out.println("+ " + montant + " parchemins C ! (Total : " + parcheminC + ")");
        } else {
            parcheminB += montant;
            System.out.println("+ " + montant + " parchemins B ! (Total : " + parcheminB + ")");
        }
    }

    private boolean dejaRecruteParNom(String nom, ArrayList<PersonnageBase> liste) {
        return liste.stream().anyMatch(p -> p.getNom().equalsIgnoreCase(nom));
    }

    // ── Getters / Setters (sauvegarde) ────────────────────────────────────
    public int getParcheminC() { return parcheminC; }
    public int getParcheminB() { return parcheminB; }
    public void setParcheminC(int v) { this.parcheminC = v; }
    public void setParcheminB(int v) { this.parcheminB = v; }

    public void ajouterParcheminC(int montant) { ajouterParchemins("C", montant); }
    public void ajouterParcheminB(int montant) { ajouterParchemins("B", montant); }
}