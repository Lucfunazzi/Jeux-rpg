package lancement.Menus;

import Equipement.Inventaire;
import Equipement.ParcheminXP;
import Joueur.Personnage_principale;
import Personnage.PersonnageBase;

// Naruto
import Personnage.Naruto_Shippuden.perso_Iruka;
import Personnage.Naruto_Shippuden.perso_Tenten;
import Personnage.Naruto_Shippuden.perso_Kiba;
import Personnage.Naruto_Shippuden.perso_Shino;
import Personnage.Naruto_Shippuden.perso_Hinata;
import Personnage.Naruto_Shippuden.perso_Choji;
import Personnage.Naruto_Shippuden.perso_Ino;
import Personnage.Naruto_Shippuden.perso_Haku;
import Personnage.Naruto_Shippuden.perso_Kabuto;
import Personnage.Naruto_Shippuden.perso_Temari;
import Personnage.Naruto_Shippuden.perso_Kankuro;
import Personnage.Naruto_Shippuden.perso_Lee;
import Personnage.Naruto_Shippuden.perso_Zabuza;
import Personnage.Naruto_Shippuden.perso_Asuma;
import Personnage.Naruto_Shippuden.perso_Kurenai;
import Personnage.Naruto_Shippuden.perso_Deidara;
import Personnage.Naruto_Shippuden.perso_Hidan;
import Personnage.Naruto_Shippuden.perso_Naruto;
import Personnage.Naruto_Shippuden.perso_Neji;
import Personnage.Naruto_Shippuden.perso_Shikamaru;
import Personnage.Naruto_Shippuden.perso_Sakura;
import Personnage.Naruto_Shippuden.perso_Itachi;
import Personnage.Naruto_Shippuden.perso_Kakashi;
import Personnage.Naruto_Shippuden.perso_Sasuke;
import Personnage.Naruto_Shippuden.perso_Gai;
import Personnage.Naruto_Shippuden.perso_Orochimaru;

// Dragon Ball Z
import Personnage.DragonBallZ.perso_Yamcha;
import Personnage.DragonBallZ.perso_Tien;
import Personnage.DragonBallZ.perso_Chiaotzu;
import Personnage.DragonBallZ.perso_Raditz;
import Personnage.DragonBallZ.perso_Krillin;
import Personnage.DragonBallZ.perso_Sangoku;
import Personnage.DragonBallZ.perso_C17;
import Personnage.DragonBallZ.perso_C18;

// Fairy Tail
import Personnage.FairyTail.perso_Alzack;
import Personnage.FairyTail.perso_Bisca;
import Personnage.FairyTail.perso_Elfman;
import Personnage.FairyTail.perso_Kana;
import Personnage.FairyTail.perso_Evergreen;
import Personnage.FairyTail.perso_Bickslow;
import Personnage.FairyTail.perso_Loke;
import Personnage.FairyTail.perso_Lucy;
import Personnage.FairyTail.perso_Gray;
import Personnage.FairyTail.perso_Natsu;
import Personnage.FairyTail.perso_Freed;
import Personnage.FairyTail.perso_Gajeel;
import Personnage.FairyTail.perso_Jubia;
import Personnage.FairyTail.perso_Mirajane;
import Personnage.FairyTail.perso_Erza;
import Personnage.FairyTail.perso_Natsu_Etherion;
import Personnage.FairyTail.perso_Wendy;

import lancement.Donjon.StageDonjon;
import lancement.GameContext;
import lancement.Gestionnaires.GestionnaireDonjon;
import lancement.Gestionnaires.GestionnaireDonjon.TypeDonjon;
import lancement.Gestionnaires.GestionnaireDonjon.Difficulte;
import lancement.Gestionnaires.GestionnaireTitres;
import java.util.ArrayList;
import java.util.Scanner;

public class MenuDonjon {

    private static final int NIV_DIFFICILE = 25;
    private static final int NIV_EXTREME   = 50;

    public void afficher(GameContext ctx, Scanner scanner) {
        ctx.gestionnaireDonjon.mettreAJour();
        boolean retour = false;

        while (!retour) {
            System.out.println("\n========================================");
            System.out.println("        DONJON DE RESSOURCES");
            System.out.println("========================================");
            System.out.println("Chaque donjon offre 3 runs par jour (reset minuit).");
            System.out.println();
            afficherResumeDojons(ctx);
            System.out.println();
            System.out.println("1. Donjon de l'Or");
            System.out.println("2. Donjon de l'Affinage");
            System.out.println("3. Donjon de l'Experience");
            System.out.println("0. Retour");
            System.out.print("Votre choix : ");

            switch (scanner.nextLine().trim()) {
                case "1" -> choisirDifficulte(TypeDonjon.OR,       ctx, scanner);
                case "2" -> choisirDifficulte(TypeDonjon.AFFINAGE, ctx, scanner);
                case "3" -> choisirDifficulte(TypeDonjon.XP,       ctx, scanner);
                case "0" -> retour = true;
                default  -> System.out.println("Choix invalide.");
            }
        }
    }

    private void afficherResumeDojons(GameContext ctx) {
        String[] noms = {"Or", "Affinage", "XP"};
        for (TypeDonjon type : TypeDonjon.values()) {
            System.out.print("  " + noms[type.ordinal()] + " : ");
            for (Difficulte diff : Difficulte.values()) {
                if (estDebloque(diff, ctx)) {
                    System.out.print(diff.name().charAt(0)
                            + diff.name().substring(1).toLowerCase()
                            + " " + ctx.gestionnaireDonjon.getRunsRestants(type, diff) + "/3  ");
                }
            }
            System.out.println();
        }
    }

    private void choisirDifficulte(TypeDonjon type, GameContext ctx, Scanner scanner) {
        boolean retour = false;
        while (!retour) {
            System.out.println("\n--- Donjon " + nomType(type) + " ---");
            System.out.println("Choisissez une difficulte :");
            System.out.println();
            afficherDifficulte(type, Difficulte.NORMAL,    ctx, 1);
            afficherDifficulte(type, Difficulte.DIFFICILE, ctx, 2);
            afficherDifficulte(type, Difficulte.EXTREME,   ctx, 3);
            System.out.println("0. Retour");
            System.out.print("Votre choix : ");

            switch (scanner.nextLine().trim()) {
                case "1" -> lancerRun(type, Difficulte.NORMAL,    ctx, scanner);
                case "2" -> {
                    if (estDebloque(Difficulte.DIFFICILE, ctx))
                        lancerRun(type, Difficulte.DIFFICILE, ctx, scanner);
                    else
                        System.out.println("Debloque au niveau " + NIV_DIFFICILE + " !");
                }
                case "3" -> {
                    if (estDebloque(Difficulte.EXTREME, ctx))
                        lancerRun(type, Difficulte.EXTREME, ctx, scanner);
                    else
                        System.out.println("Debloque au niveau " + NIV_EXTREME + " !");
                }
                case "0" -> retour = true;
                default  -> System.out.println("Choix invalide.");
            }
        }
    }

    private void afficherDifficulte(TypeDonjon type, Difficulte diff,
                                     GameContext ctx, int numero) {
        int niv = switch (diff) {
            case NORMAL    -> 1;
            case DIFFICILE -> NIV_DIFFICILE;
            case EXTREME   -> NIV_EXTREME;
        };
        String recompense = descriptionRecompense(type, diff);
        if (estDebloque(diff, ctx)) {
            System.out.println("  " + numero + ". " + nomDiff(diff)
                    + "  [" + ctx.gestionnaireDonjon.getRunsRestants(type, diff) + "/3 runs]  " + recompense);
        } else {
            System.out.println("  " + numero + ". " + nomDiff(diff)
                    + "  [Debloque niveau " + niv + "]  " + recompense);
        }
    }

    private void lancerRun(TypeDonjon type, Difficulte diff,
                            GameContext ctx, Scanner scanner) {
        if (!ctx.gestionnaireDonjon.peutFaireRun(type, diff)) {
            System.out.println("Plus de runs disponibles pour aujourd'hui !");
            return;
        }
        if (ctx.formation.getEquipe().isEmpty()) {
            System.out.println("Votre formation est vide ! Ajoutez des personnages d'abord.");
            return;
        }

        StageDonjon stage = creerStage(type, diff, ctx);
        boolean victoire = stage.lancer(ctx.joueur, ctx.formation.getEquipe(),
                ctx.inventaire, ctx.personnagesRecruites,
                ctx.gestionnaireDonjon, ctx.gestionnaireTitres, scanner);

        if (victoire) {
            ctx.sauvegarde.sauvegarder(ctx);
            System.out.println(">> Partie sauvegardee automatiquement.");
        }
    }

    private StageDonjon creerStage(TypeDonjon type, Difficulte diff, GameContext ctx) {
        ArrayList<PersonnageBase> ennemis = creerEnnemis(type, diff, ctx);
        int or = 0, pierres = 0;
        ParcheminXP parchemin = null;

        switch (type) {
            case OR -> or = switch (diff) {
                case NORMAL -> 15000; case DIFFICILE -> 50000; case EXTREME -> 500000;
            };
            case AFFINAGE -> pierres = switch (diff) {
                case NORMAL -> 10; case DIFFICILE -> 50; case EXTREME -> 150;
            };
            case XP -> parchemin = switch (diff) {
                case NORMAL    -> new ParcheminXP(ParcheminXP.Rarete.C);
                case DIFFICILE -> new ParcheminXP(ParcheminXP.Rarete.B);
                case EXTREME   -> new ParcheminXP(ParcheminXP.Rarete.A);
            };
        }
        return new StageDonjon(type, diff, ennemis, or, pierres, parchemin);
    }

    // ─────────────────────────────────────────────────────────────────────
    // EQUIPES PAR DONJON ET DIFFICULTE
    // Regles : max 5 personnages, 1 tank max, 3 DPS max, 3 support max
    // NORMAL   : rang C et B uniquement
    // DIFFICILE: rang B et A uniquement
    // EXTREME  : rang A et S uniquement
    // ─────────────────────────────────────────────────────────────────────
    private ArrayList<PersonnageBase> creerEnnemis(TypeDonjon type, Difficulte diff,
                                                    GameContext ctx) {
        ArrayList<PersonnageBase> ennemis = new ArrayList<>();
        int niv = switch (diff) {
            case NORMAL    -> 10;
            case DIFFICILE -> 25;
            case EXTREME   -> 50;
        };

        switch (diff) {

            // ── NORMAL (C + B) ──────────────────────────────────────────
            case NORMAL -> {
                switch (type) {
                    case OR -> {
                        // Tank: Iruka (C) | DPS: Yamcha (C), Kiba (C), Alzack (C) | Support: Shino (C)
                        ennemis.add(niv(new perso_Iruka(),   niv));
                        ennemis.add(niv(new perso_Yamcha(),  niv));
                        ennemis.add(niv(new perso_Kiba(),    niv));
                        ennemis.add(niv(new perso_Alzack(),  niv));
                        ennemis.add(niv(new perso_Shino(),   niv));
                    }
                    case AFFINAGE -> {
                        // Tank: Hinata (B) | DPS: Tenten (C), Bisca (C) | Support: Ino (B), Kana (B)
                        ennemis.add(niv(new perso_Hinata(),    niv));
                        ennemis.add(niv(new perso_Tenten(),    niv));
                        ennemis.add(niv(new perso_Bisca(),     niv));
                        ennemis.add(niv(new perso_Ino(),       niv));
                        ennemis.add(niv(new perso_Kana(),      niv));
                    }
                    case XP -> {
                        // Tank: Choji (B) | DPS: Elfman (C), Raditz (B), Kankuro (B) | Support: Haku (B)
                        ennemis.add(niv(new perso_Choji(),     niv));
                        ennemis.add(niv(new perso_Elfman(),    niv));
                        ennemis.add(niv(new perso_Raditz(),    niv));
                        ennemis.add(niv(new perso_Kankuro(),   niv));
                        ennemis.add(niv(new perso_Haku(),      niv));
                    }
                }
            }

            // ── DIFFICILE (B + A) ────────────────────────────────────────
            case DIFFICILE -> {
                switch (type) {
                    case OR -> {
                        // Tank: Hidan (A) | DPS: Lee (B), Zabuza (B), Deidara (A) | Support: Temari (B)
                        ennemis.add(niv(new perso_Hidan(),     niv));
                        ennemis.add(niv(new perso_Lee(),       niv));
                        ennemis.add(niv(new perso_Zabuza(),    niv));
                        ennemis.add(niv(new perso_Deidara(),   niv));
                        ennemis.add(niv(new perso_Temari(),    niv));
                    }
                    case AFFINAGE -> {
                        // Tank: Naruto (A) | DPS: Bickslow (B), Loke (B), Gray (A) | Support: Freed (A)
                        ennemis.add(niv(new perso_Naruto(),    niv));
                        ennemis.add(niv(new perso_Bickslow(),  niv));
                        ennemis.add(niv(new perso_Loke(),      niv));
                        ennemis.add(niv(new perso_Gray(),      niv));
                        ennemis.add(niv(new perso_Freed(),     niv));
                    }
                    case XP -> {
                        // Tank: Neji (A) | DPS: Sangoku (A), Gajeel (A), Sakura (A) | Support: Kurenai (A)
                        ennemis.add(niv(new perso_Neji(),      niv));
                        ennemis.add(niv(new perso_Sangoku(),   niv));
                        ennemis.add(niv(new perso_Gajeel(),    niv));
                        ennemis.add(niv(new perso_Sakura(),    niv));
                        ennemis.add(niv(new perso_Kurenai(),   niv));
                    }
                }
            }

            // ── EXTREME (A + S) ──────────────────────────────────────────
            case EXTREME -> {
                switch (type) {
                    case OR -> {
                        // Tank: Erza (S) | DPS: Itachi (S), Kakashi (S), Natsu (A) | Support: Asuma (A)
                        ennemis.add(niv(new perso_Erza(),      niv));
                        ennemis.add(niv(new perso_Itachi(),    niv));
                        ennemis.add(niv(new perso_Kakashi(),   niv));
                        ennemis.add(niv(new perso_Natsu(),     niv));
                        ennemis.add(niv(new perso_Asuma(),     niv));
                    }
                    case AFFINAGE -> {
                        // Tank: C18 (S) | DPS: Sasuke (S), Gai (S), Mirajane (S) | Support: Jubia (A)
                        ennemis.add(niv(new perso_C18(),           niv));
                        ennemis.add(niv(new perso_Sasuke(),        niv));
                        ennemis.add(niv(new perso_Gai(),           niv));
                        ennemis.add(niv(new perso_Mirajane(),      niv));
                        ennemis.add(niv(new perso_Jubia(),         niv));
                    }
                    case XP -> {
                        // Tank: C17 (S) | DPS: Orochimaru (S), Natsu Etherion (S) | Support: Shikamaru (A), Wendy (A)
                        ennemis.add(niv(new perso_C17(),           niv));
                        ennemis.add(niv(new perso_Orochimaru(),    niv));
                        ennemis.add(niv(new perso_Natsu_Etherion(), niv));
                        ennemis.add(niv(new perso_Shikamaru(),     niv));
                        ennemis.add(niv(new perso_Wendy(),         niv));
                    }
                }
            }
        }
        return ennemis;
    }

    private PersonnageBase niv(PersonnageBase p, int n) {
        while (p.getNiveau() < n) p.monterDeNiveau();
        return p;
    }

    private boolean estDebloque(Difficulte diff, GameContext ctx) {
        return switch (diff) {
            case NORMAL    -> true;
            case DIFFICILE -> ctx.joueur.getNiveau() >= NIV_DIFFICILE;
            case EXTREME   -> ctx.joueur.getNiveau() >= NIV_EXTREME;
        };
    }

    private String nomType(TypeDonjon type) {
        return switch (type) { case OR -> "de l'Or"; case AFFINAGE -> "de l'Affinage"; case XP -> "de l'Experience"; };
    }
    private String nomDiff(Difficulte diff) {
        return switch (diff) { case NORMAL -> "Normal   "; case DIFFICILE -> "Difficile"; case EXTREME -> "Extreme  "; };
    }
    private String descriptionRecompense(TypeDonjon type, Difficulte diff) {
        return switch (type) {
            case OR -> switch (diff) { case NORMAL -> "15 000 or"; case DIFFICILE -> "50 000 or"; case EXTREME -> "500 000 or"; };
            case AFFINAGE -> switch (diff) { case NORMAL -> "10 pierres d'affinage"; case DIFFICILE -> "50 pierres d'affinage"; case EXTREME -> "150 pierres d'affinage"; };
            case XP -> switch (diff) { case NORMAL -> "1x Parchemin XP [C] (500 XP)"; case DIFFICILE -> "1x Parchemin XP [B] (1500 XP)"; case EXTREME -> "1x Parchemin XP [A] (5000 XP)"; };
        };
    }
}