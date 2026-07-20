package lancement.Menus;

import Equipement.Inventaire;
import Equipement.ParcheminXP;
import Joueur.Personnage_principale;
import Personnage.PersonnageBase;



// Fairy Tail
import Personnage.FairyTail.*;

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
                        // Tank: Nab (C) | DPS: Alzack (C), Jet (C), Bisca (C) | Support: Warren (C)
                        ennemis.add(niv(new perso_Nab(),     niv));
                        ennemis.add(niv(new perso_Arzak(),  niv));
                        ennemis.add(niv(new perso_Jett(),     niv));
                        ennemis.add(niv(new perso_Biska(),   niv));
                        ennemis.add(niv(new perso_Warren(),  niv));
                    }
                    case AFFINAGE -> {
                        // Tank: Elfman Bête (B) | DPS: Max (C), Droy (C) | Support: Levy (B), Lisanna (B)
                        ennemis.add(niv(new perso_ElfmanBete(), niv));
                        ennemis.add(niv(new perso_Max(),        niv));
                        ennemis.add(niv(new perso_Droy(),       niv));
                        ennemis.add(niv(new perso_Levy(),       niv));
                        ennemis.add(niv(new perso_Lisanna(),    niv));
                    }
                    case XP -> {
                        // Tank: Nab (C) | DPS: Romeo (C), Elfman (C), Bickslow (B) | Support: Evergreen (B)
                        ennemis.add(niv(new perso_Nab(),       niv));
                        ennemis.add(niv(new perso_Romeo(),     niv));
                        ennemis.add(niv(new perso_Elfman(),    niv));
                        ennemis.add(niv(new perso_Bixrow(),  niv));
                        ennemis.add(niv(new perso_Evergreen(), niv));
                    }
                }
            }

            // ── DIFFICILE (B + A) ────────────────────────────────────────
            case DIFFICILE -> {
                switch (type) {
                    case OR -> {
                        // Tank: Elfman Bête (B) | DPS: Loke (B), Gajeel (A), Angel (A) | Support: Levy (B)
                        ennemis.add(niv(new perso_ElfmanBete(), niv));
                        ennemis.add(niv(new perso_Loke(),       niv));
                        ennemis.add(niv(new perso_Gajeel(),     niv));
                        ennemis.add(niv(new perso_Angel(),      niv));
                        ennemis.add(niv(new perso_Levy(),       niv));
                    }
                    case AFFINAGE -> {
                        // Tank: Rogue (S) | DPS: Bickslow (B), Loke (B), Gray (A) | Support: Freed (A)
                        ennemis.add(niv(new perso_Rogue(),     niv));
                        ennemis.add(niv(new perso_Bixrow(),  niv));
                        ennemis.add(niv(new perso_Loke(),      niv));
                        ennemis.add(niv(new perso_Gray(),      niv));
                        ennemis.add(niv(new perso_Freed(),     niv));
                    }
                    case XP -> {
                        // Tank: Natsu (A) | DPS: Gajeel (A), Lucy (A), Wendy (A) | Support: Jubia (A)
                        ennemis.add(niv(new perso_Natsu(),     niv));
                        ennemis.add(niv(new perso_Gajeel(),    niv));
                        ennemis.add(niv(new perso_Lucy(),      niv));
                        ennemis.add(niv(new perso_Wendy(),     niv));
                        ennemis.add(niv(new perso_Jubia_4elements(),     niv));
                    }
                }
            }

            // ── EXTREME (A + S) ──────────────────────────────────────────
            case EXTREME -> {
                switch (type) {
                    case OR -> {
                        // Tank: Erza (S) | DPS: Itachi (S), Kakashi (S), Natsu (A) | Support: Asuma (A)
                        ennemis.add(niv(new perso_Erza(),      niv));
                        ennemis.add(niv(new perso_Erza(),    niv));
                        ennemis.add(niv(new perso_Sting(),   niv));
                        ennemis.add(niv(new perso_Natsu(),     niv));
                        ennemis.add(niv(new perso_Angel(),     niv));
                    }
                    case AFFINAGE -> {
                        // Tank: C18 (S) | DPS: Sasuke (S), Gai (S), Mirajane (S) | Support: Jubia (A)
                        ennemis.add(niv(new perso_Mirajane_Halphas(),           niv));
                        ennemis.add(niv(new perso_Rogue(),        niv));
                        ennemis.add(niv(new perso_Mirajane(),           niv));
                        ennemis.add(niv(new perso_Mirajane(),      niv));
                        ennemis.add(niv(new perso_Jubia_4elements(),         niv));
                    }
                    case XP -> {
                        // Tank: C17 (S) | DPS: Orochimaru (S), Natsu Etherion (S) | Support: Shikamaru (A), Wendy (A)
                        ennemis.add(niv(new perso_Lucas(),           niv));
                        ennemis.add(niv(new perso_Yukino(),    niv));
                        ennemis.add(niv(new perso_Natsu_Etherion(), niv));
                        ennemis.add(niv(new perso_Natsu(),     niv));
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