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

    public static final int NIV_MOYEN = 25;
    public static final int NIV_DIFFICILE   = 50;
    public static final int NIV_EXTREME   = 80;

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
            afficherDifficulte(type, Difficulte.FACILE,    ctx, 1);
            afficherDifficulte(type, Difficulte.MOYEN, ctx, 2);
            afficherDifficulte(type, Difficulte.DIFFICILE,   ctx, 3);
            afficherDifficulte(type, Difficulte.EXTREME, ctx,4);
            System.out.println("0. Retour");
            System.out.print("Votre choix : ");

            switch (scanner.nextLine().trim()) {
                case "1" -> lancerRun(type, Difficulte.FACILE,    ctx, scanner);
                case "2" -> {
                    if (estDebloque(Difficulte.MOYEN, ctx))
                        lancerRun(type, Difficulte.MOYEN, ctx, scanner);
                    else
                        System.out.println("Debloque au niveau " + NIV_MOYEN + " !");
                }
                case "3" -> {
                    if (estDebloque(Difficulte.DIFFICILE, ctx))
                        lancerRun(type, Difficulte.DIFFICILE, ctx, scanner);
                    else
                        System.out.println("Debloque au niveau " + NIV_DIFFICILE + " !");
                }
                case "4" -> {
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
            case FACILE    -> 1;
            case MOYEN -> NIV_MOYEN;
            case DIFFICILE   -> NIV_DIFFICILE;
            case EXTREME ->NIV_EXTREME;
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

    /** Resultat d'une tentative de run : indique si le combat a bien ete lance,
     *  s'il est gagne, et fournit l'instantane + les evenements pour la relecture GUI. */
    public record ResultatRun(boolean lance, boolean victoire,
                               java.util.List<Combat.Combat.PersonnageSnapshot> etatInitial,
                               java.util.List<Combat.Combat.CombatEvent> evenements) {}

    public boolean lancerRun(TypeDonjon type, Difficulte diff,
                              GameContext ctx, Scanner scanner) {
        return lancerRunAvecEvenements(type, diff, ctx, scanner).victoire();
    }

    public ResultatRun lancerRunAvecEvenements(TypeDonjon type, Difficulte diff,
                                                GameContext ctx, Scanner scanner) {
        if (!ctx.gestionnaireDonjon.peutFaireRun(type, diff)) {
            System.out.println("Plus de runs disponibles pour aujourd'hui !");
            return new ResultatRun(false, false, null, null);
        }
        if (ctx.formation.getEquipe().isEmpty()) {
            System.out.println("Votre formation est vide ! Ajoutez des personnages d'abord.");
            return new ResultatRun(false, false, null, null);
        }

        StageDonjon stage = creerStage(type, diff, ctx);
        boolean victoire = stage.lancer(ctx.joueur, ctx.formation.getEquipe(),
                ctx.inventaire, ctx.personnagesRecruites,
                ctx.gestionnaireDonjon, scanner);

        if (victoire) {
            ctx.sauvegarde.sauvegarder(ctx);
            System.out.println(">> Partie sauvegardee automatiquement.");
        }
        return new ResultatRun(true, victoire, stage.getEtatInitial(), stage.getEvenements());
    }

    private StageDonjon creerStage(TypeDonjon type, Difficulte diff, GameContext ctx) {
        ArrayList<PersonnageBase> ennemis = creerEnnemis(type, diff, ctx);
        int or = 0, pierres = 0;
        ParcheminXP parchemin = null;

        switch (type) {
            case OR -> or = switch (diff) {
                case FACILE -> 30000; case MOYEN -> 100000; case DIFFICILE -> 500000; case EXTREME ->1000000;
            };
            case AFFINAGE -> pierres = switch (diff) {
                case FACILE -> 10; case MOYEN -> 50; case DIFFICILE -> 150; case EXTREME -> 300;
            };
            case XP -> parchemin = switch (diff) {
                case FACILE    -> new ParcheminXP(ParcheminXP.Rarete.C);
                case MOYEN -> new ParcheminXP(ParcheminXP.Rarete.B);
                case DIFFICILE   -> new ParcheminXP(ParcheminXP.Rarete.A);
                case EXTREME -> new ParcheminXP(ParcheminXP.Rarete.S);
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
            case FACILE    -> 10;
            case MOYEN -> 25;
            case DIFFICILE   -> 50;
            case EXTREME -> 80;
        };

        switch (diff) {

            // ── NORMAL (C + B) ──────────────────────────────────────────
            case FACILE -> {
                switch (type) {
                    case OR -> {
                        
                        ennemis.add(niv(new perso_DucEverlue(),     niv));
                        ennemis.add(niv(new perso_Arzak(),  niv));
                        ennemis.add(niv(new perso_Biska(),   niv));
                        ennemis.add(niv(new perso_Miliana(), niv));
                        ennemis.add(niv(new perso_Shaw(),    niv));
                    }
                    case AFFINAGE -> {
                        // Support: Levy (B), Lisanna (B), Miliana (C) | Tank: Simon (B) | DPS: Wolly (C)
                        ennemis.add(niv(new perso_Levy(),       niv));
                        ennemis.add(niv(new perso_Cherry(),    niv));
                        ennemis.add(niv(new perso_Leon(),    niv));
                        ennemis.add(niv(new perso_Simon(),      niv));
                        ennemis.add(niv(new perso_Eligor(),      niv));
                    }
                    case XP -> {
                        // Tank: Nab (C) | Support: Shaw (B), Miliana (C) | DPS: Eligoal (C), Wolly (C)
                        ennemis.add(niv(new perso_Sol(),       niv));
                        ennemis.add(niv(new perso_Totomaru(),    niv));
                        ennemis.add(niv(new perso_Shaw(),      niv));
                        ennemis.add(niv(new perso_Miliana(),   niv));
                        ennemis.add(niv(new perso_Wolly(),     niv));
                    }
                }
            }

            // ── DIFFICILE (B + A) ────────────────────────────────────────
            case MOYEN -> {
                switch (type) {
                    case OR -> {
                        // Tank: Sugarboy (A) | DPS: Gajeel (A), Racer (A) | Support: Levy (B), Hibiki (A)
                        ennemis.add(niv(new perso_Evergreen(),   niv));
                        ennemis.add(niv(new perso_Bixrow(),     niv));
                        ennemis.add(niv(new perso_Freed(),      niv));
                        ennemis.add(niv(new perso_Levy(),       niv));
                        ennemis.add(niv(new perso_Hibiki(),     niv));
                    }
                    case AFFINAGE -> {
                        // Tank: Hoteye (A) | DPS: Bixrow (A), Cobra (A) | Support: Freed (A), Byro (A)
                        ennemis.add(niv(new perso_Hoteye(),    niv));
                        ennemis.add(niv(new perso_Bixrow(),    niv));
                        ennemis.add(niv(new perso_Cobra(),     niv));
                        ennemis.add(niv(new perso_Freed(),     niv));
                        ennemis.add(niv(new perso_Byro(),      niv));
                    }
                    case XP -> {
                        // Tank: Ikaruga (A) | DPS: Panther Lily (A), Midnight (A) | Support: Wendy (A), Mest (A)
                        ennemis.add(niv(new perso_Ikaruga(),     niv));
                        ennemis.add(niv(new perso_PantherLily(), niv));
                        ennemis.add(niv(new perso_Midnight(),    niv));
                        ennemis.add(niv(new perso_Wendy(),       niv));
                        ennemis.add(niv(new perso_Mest(),        niv));
                    }
                }
            }

            // ── EXTREME (A + S) ──────────────────────────────────────────
            case DIFFICILE -> {
                switch (type) {
                    case OR -> {
                        // Tank: Azuma (S) | DPS: Zancrow (S), Sting (S) | Support: Ultear (S), Yukino (S)
                        ennemis.add(niv(new perso_Azuma(),     niv));
                        ennemis.add(niv(new perso_Zancrow(),   niv));
                        ennemis.add(niv(new perso_Sting(),     niv));
                        ennemis.add(niv(new perso_Ultear(),    niv));
                        ennemis.add(niv(new perso_Yukino(),    niv));
                    }
                    case AFFINAGE -> {
                        // Tank: Erza Knightwalker (S) | DPS: Capricorn (S), Jellal (S) | Support: Meredy (S), Freed (A)
                        ennemis.add(niv(new perso_ErzaKnightwalker(), niv));
                        ennemis.add(niv(new perso_Capricorn(),        niv));
                        ennemis.add(niv(new perso_jellal(),           niv));
                        ennemis.add(niv(new perso_Meredy(),           niv));
                        ennemis.add(niv(new perso_Freed(),            niv));
                    }
                    case XP -> {
                        // Tank: Rustyrose (S) | DPS: Loki (S), Natsu Etherion (S), Mirajane (S) | Support: Brain (S)
                        ennemis.add(niv(new perso_Rustyrose(),      niv));
                        ennemis.add(niv(new perso_Loki(),           niv));
                        ennemis.add(niv(new perso_Natsu_Etherion(), niv));
                        ennemis.add(niv(new perso_Mirajane(),       niv));
                        ennemis.add(niv(new perso_Brain(),          niv));
                    }
                }
            }

            // ── EXTREME (SS + SSS + UR) ────────────────────────────────────
            // Peu de personnages a ce rang dans le roster actuel : reutilises entre les 3
            // categories plutot que de descendre au rang S (deja utilise en DIFFICILE).
            case EXTREME -> {
                switch (type) {
                    case OR -> {
                        // Tank: Zeref (UR) | DPS: Acnologia (UR), Gildarts (SSS), Hades (SSS), Jellal Intermagie (SS)
                        ennemis.add(niv(new perso_Zeref(),               niv));
                        ennemis.add(niv(new perso_Acnologia(),           niv));
                        ennemis.add(niv(new perso_Gildarts(),            niv));
                        ennemis.add(niv(new perso_Hades(),               niv));
                        ennemis.add(niv(new jellal_Arc_intermagie(),     niv));
                    }
                    case AFFINAGE -> {
                        // Tank: Jura (SS) | DPS: Luxus (SS), Mirajane Halphas (SS), Ul (SS), Zero (SS)
                        ennemis.add(niv(new perso_Jura(),             niv));
                        ennemis.add(niv(new perso_Luxus(),            niv));
                        ennemis.add(niv(new perso_Mirajane_Halphas(), niv));
                        ennemis.add(niv(new perso_Ul(),               niv));
                        ennemis.add(niv(new perso_Zero(),             niv));
                    }
                    case XP -> {
                        // Tank: Zeref (UR) | DPS: Hades (SSS), Luxus (SS), Zero (SS), Jellal Intermagie (SS)
                        ennemis.add(niv(new perso_Zeref(),           niv));
                        ennemis.add(niv(new perso_Hades(),           niv));
                        ennemis.add(niv(new perso_Luxus(),           niv));
                        ennemis.add(niv(new perso_Zero(),            niv));
                        ennemis.add(niv(new jellal_Arc_intermagie(), niv));
                    }
                }
            }
        }
        return ennemis;
    }

    /** Combativite totale de l'equipe ennemie d'un donjon (type + difficulte), pour affichage
     *  d'une recommandation au joueur avant de s'engager (voir PersonnageBase.getCombativite). */
    public double combativiteRecommandee(TypeDonjon type, Difficulte diff, GameContext ctx) {
        return creerEnnemis(type, diff, ctx).stream()
                .mapToDouble(PersonnageBase::getCombativite)
                .sum();
    }

    private PersonnageBase niv(PersonnageBase p, int n) {
        while (p.getNiveau() < n) p.monterDeNiveau();
        return p;
    }

    public static boolean estDebloque(Difficulte diff, GameContext ctx) {
        return switch (diff) {
            case FACILE    -> true;
                    
            case MOYEN -> ctx.joueur.getNiveau() >= NIV_MOYEN;
            case DIFFICILE   -> ctx.joueur.getNiveau() >= NIV_DIFFICILE;
            case EXTREME -> ctx.joueur.getNiveau() >= NIV_EXTREME;
        };
    }

    public static String nomType(TypeDonjon type) {
        return switch (type) { case OR -> "de l'Or"; case AFFINAGE -> "de l'Affinage"; case XP -> "de l'Experience"; };
    }
    public static String nomDiff(Difficulte diff) {
        return switch (diff) { case FACILE -> "Facile   "; case MOYEN -> "Moyen"; case DIFFICILE -> "Difficile  "; case EXTREME -> "Extreme";};
    }
    public static String descriptionRecompense(TypeDonjon type, Difficulte diff) {
        return switch (type) {
            case OR -> switch (diff) { case FACILE -> "30 000 or"; case MOYEN -> "100 000 or"; case DIFFICILE -> "500 000 or"; case EXTREME -> "1 000 000";};
            case AFFINAGE -> switch (diff) { case FACILE -> "10 pierres d'affinage"; case MOYEN -> "50 pierres d'affinage"; case DIFFICILE -> "150 pierres d'affinage"; case EXTREME -> " 300 pierres d'affinages"; };
            case XP -> switch (diff) { case FACILE -> "30x Parchemin XP [C] (500 XP)"; case MOYEN -> "30x Parchemin XP [B] (1500 XP)"; case DIFFICILE -> "30x Parchemin XP [A] (5000 XP)"; case EXTREME -> "30x Parchemin XP [S]"; };
        };
    }
}