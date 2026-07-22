package lancement.Menus;

import Joueur.Personnage_principale;
import Personnage.PersonnageBase;
import Personnage.FairyTail.perso_Natsu;
import Personnage.FairyTail.perso_Natsu_Etherion;
import Personnage.FairyTail.perso_Mirajane;
import Personnage.FairyTail.perso_Mirajane_Halphas;
import Equipement.Inventaire;
import java.util.ArrayList;
import java.util.Scanner;
import lancement.Formation;
import lancement.GameContext;

public class MenuRecrutementRare {

    public static final String MATERIAU_NATSU      = "Echarpe blanche d'Ignir";
    public static final int    COUT_RECRUTEMENT    = 50;
    public static final int    COUT_EVOLUTION      = 150;

    public static final String MATERIAU_MIRAJANE   = "Aile de demon";
    public static final int    COUT_MIRAJANE_S     = 100;
    public static final int    COUT_MIRAJANE_SS    = 250;

    public void afficher(GameContext ctx, Scanner scanner) {
        Personnage_principale      joueur               = ctx.joueur;
        ArrayList<PersonnageBase>  personnagesRecruites = ctx.personnagesRecruites;
        Formation                  formation            = ctx.formation;
        Inventaire                 inventaire           = ctx.inventaire;
        boolean retour = false;

        while (!retour) {
            System.out.println("\n========================================");
            System.out.println("       RECRUTEMENT RARE");
            System.out.println("========================================");

            // ── Natsu ──────────────────────────────────────────────────────
            int possedeIgnir     = inventaire.getQuantiteMateriau(MATERIAU_NATSU);
            boolean natsuA       = dejaRecruteParNom("Natsu",           personnagesRecruites);
            boolean natsuS       = dejaRecruteParNom("Natsu Etherion",  personnagesRecruites);

            System.out.println();
            System.out.println("[ Natsu ]");
            System.out.println("  1. Natsu [A]  — " + MATERIAU_NATSU
                    + " : " + possedeIgnir + "/" + COUT_RECRUTEMENT
                    + (natsuA || natsuS ? "  [DEJA RECRUTE]" : ""));
            if (natsuA) {
                System.out.println("  2. Natsu Etherion [S]  — " + MATERIAU_NATSU
                        + " : " + possedeIgnir + "/" + COUT_EVOLUTION
                        + "  [EVOLUTION]");
            }

            // ── Mirajane ───────────────────────────────────────────────────
            int possedeAile      = inventaire.getQuantiteMateriau(MATERIAU_MIRAJANE);
            boolean miraS        = dejaRecruteParNom("Mirajane",          personnagesRecruites);
            boolean miraSS       = dejaRecruteParNom("Mirajane Halphas",  personnagesRecruites);

            System.out.println();
            System.out.println("[ Mirajane ]");
            System.out.println("  3. Mirajane [S]  — " + MATERIAU_MIRAJANE
                    + " : " + possedeAile + "/" + COUT_MIRAJANE_S
                    + (miraS || miraSS ? "  [DEJA RECRUTE]" : ""));
            if (miraS) {
                System.out.println("  4. Mirajane Halphas [SS]  — " + MATERIAU_MIRAJANE
                        + " : " + possedeAile + "/" + COUT_MIRAJANE_SS
                        + "  [EVOLUTION]");
            }

            System.out.println();
            System.out.println("0. Retour");
            System.out.print("Votre choix : ");

            int choix;
            try {
                choix = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Entree invalide.");
                continue;
            }

            switch (choix) {
                case 0 -> retour = true;
                case 1 -> {
                    if (natsuA || natsuS) System.out.println("Natsu est deja dans vos allies !");
                    else tenterRecrutementNatsu(ctx, scanner);
                }
                case 2 -> {
                    if (!natsuA) System.out.println("Vous devez d'abord recruter Natsu [A].");
                    else tenterEvolutionNatsu(ctx, scanner);
                }
                case 3 -> {
                    if (miraS || miraSS) System.out.println("Mirajane est deja dans vos allies !");
                    else tenterRecrutementMirajane(ctx, scanner);
                }
                case 4 -> {
                    if (!miraS) System.out.println("Vous devez d'abord recruter Mirajane [S].");
                    else tenterEvolutionMirajane(ctx, scanner);
                }
                default -> System.out.println("Choix invalide.");
            }
        }
    }

    // ── Recrutement Natsu A ───────────────────────────────────────────────
    private void tenterRecrutementNatsu(GameContext ctx, Scanner scanner) {
        int possede = ctx.inventaire.getQuantiteMateriau(MATERIAU_NATSU);
        if (possede < COUT_RECRUTEMENT) {
            System.out.println("Materiaux insuffisants : "
                    + possede + "/" + COUT_RECRUTEMENT + " " + MATERIAU_NATSU);
            return;
        }
        System.out.println("Recruter Natsu [A] pour " + COUT_RECRUTEMENT
                + " " + MATERIAU_NATSU + " ? (1 : Oui / 2 : Non)");
        if (!scanner.nextLine().trim().equals("1")) return;

        System.out.println(">> " + recruterNatsu(ctx));
    }

    // ── Evolution Natsu A → Natsu Etherion S ─────────────────────────────
    private void tenterEvolutionNatsu(GameContext ctx, Scanner scanner) {
        int possede = ctx.inventaire.getQuantiteMateriau(MATERIAU_NATSU);
        if (possede < COUT_EVOLUTION) {
            System.out.println("Materiaux insuffisants : "
                    + possede + "/" + COUT_EVOLUTION + " " + MATERIAU_NATSU);
            return;
        }
        System.out.println("Evoluer Natsu [A] vers Natsu Etherion [S] pour "
                + COUT_EVOLUTION + " " + MATERIAU_NATSU + " ?");
        System.out.println("ATTENTION : Natsu [A] sera remplace definitivement. (1 : Oui / 2 : Non)");
        if (!scanner.nextLine().trim().equals("1")) return;

        System.out.println(evoluerNatsu(ctx));
    }

    // ── Recrutement Mirajane S ────────────────────────────────────────────
    private void tenterRecrutementMirajane(GameContext ctx, Scanner scanner) {
        int possede = ctx.inventaire.getQuantiteMateriau(MATERIAU_MIRAJANE);
        if (possede < COUT_MIRAJANE_S) {
            System.out.println("Materiaux insuffisants : "
                    + possede + "/" + COUT_MIRAJANE_S + " " + MATERIAU_MIRAJANE);
            return;
        }
        System.out.println("Recruter Mirajane [S] pour " + COUT_MIRAJANE_S
                + " " + MATERIAU_MIRAJANE + " ? (1 : Oui / 2 : Non)");
        if (!scanner.nextLine().trim().equals("1")) return;

        System.out.println(">> " + recruterMirajane(ctx));
    }

    // ── Evolution Mirajane S → Mirajane Halphas SS ───────────────────────
    private void tenterEvolutionMirajane(GameContext ctx, Scanner scanner) {
        int possede = ctx.inventaire.getQuantiteMateriau(MATERIAU_MIRAJANE);
        if (possede < COUT_MIRAJANE_SS) {
            System.out.println("Materiaux insuffisants : "
                    + possede + "/" + COUT_MIRAJANE_SS + " " + MATERIAU_MIRAJANE);
            return;
        }
        System.out.println("Evoluer Mirajane [S] vers Mirajane Halphas [SS] pour "
                + COUT_MIRAJANE_SS + " " + MATERIAU_MIRAJANE + " ?");
        System.out.println("ATTENTION : Mirajane [S] sera remplacee definitivement. (1 : Oui / 2 : Non)");
        if (!scanner.nextLine().trim().equals("1")) return;

        System.out.println(evoluerMirajane(ctx));
    }

    // ── Logique pure (reutilisable par la console et l'interface graphique) ─

    /** Tente de recruter Natsu [A]. Retourne le message resultat (materiaux non deduits si echec). */
    public String recruterNatsu(GameContext ctx) {
        int possede = ctx.inventaire.getQuantiteMateriau(MATERIAU_NATSU);
        if (possede < COUT_RECRUTEMENT) {
            return "Materiaux insuffisants : " + possede + "/" + COUT_RECRUTEMENT + " " + MATERIAU_NATSU;
        }
        ctx.inventaire.retirerMateriau(MATERIAU_NATSU, COUT_RECRUTEMENT);
        ctx.personnagesRecruites.add(new perso_Natsu());
        ctx.sauvegarde.sauvegarder(ctx);
        return "Natsu a rejoint vos allies !";
    }

    /** Tente de faire evoluer Natsu [A] en Natsu Etherion [S]. */
    public String evoluerNatsu(GameContext ctx) {
        int possede = ctx.inventaire.getQuantiteMateriau(MATERIAU_NATSU);
        if (possede < COUT_EVOLUTION) {
            return "Materiaux insuffisants : " + possede + "/" + COUT_EVOLUTION + " " + MATERIAU_NATSU;
        }

        PersonnageBase natsuA = null;
        for (PersonnageBase p : ctx.personnagesRecruites) {
            if (p.getNom().equals("Natsu")) { natsuA = p; break; }
        }
        if (natsuA == null) return "Erreur : Natsu introuvable dans les recrues.";

        ctx.formation.retirerPersonnage(natsuA);
        ctx.personnagesRecruites.remove(natsuA);
        ctx.inventaire.retirerMateriau(MATERIAU_NATSU, COUT_EVOLUTION);

        perso_Natsu_Etherion natsuS = new perso_Natsu_Etherion();
        while (natsuS.getNiveau() < natsuA.getNiveau()) natsuS.monterDeNiveau();
        ctx.personnagesRecruites.add(natsuS);
        ctx.sauvegarde.sauvegarder(ctx);

        return "Natsu a evolue en Natsu Etherion [S] !\nNatsu Etherion est desormais disponible dans votre formation.";
    }

    /** Tente de recruter Mirajane [S]. */
    public String recruterMirajane(GameContext ctx) {
        int possede = ctx.inventaire.getQuantiteMateriau(MATERIAU_MIRAJANE);
        if (possede < COUT_MIRAJANE_S) {
            return "Materiaux insuffisants : " + possede + "/" + COUT_MIRAJANE_S + " " + MATERIAU_MIRAJANE;
        }
        ctx.inventaire.retirerMateriau(MATERIAU_MIRAJANE, COUT_MIRAJANE_S);
        ctx.personnagesRecruites.add(new perso_Mirajane());
        ctx.sauvegarde.sauvegarder(ctx);
        return "Mirajane a rejoint vos allies !";
    }

    /** Tente de faire evoluer Mirajane [S] en Mirajane Halphas [SS]. */
    public String evoluerMirajane(GameContext ctx) {
        int possede = ctx.inventaire.getQuantiteMateriau(MATERIAU_MIRAJANE);
        if (possede < COUT_MIRAJANE_SS) {
            return "Materiaux insuffisants : " + possede + "/" + COUT_MIRAJANE_SS + " " + MATERIAU_MIRAJANE;
        }

        PersonnageBase miraS = null;
        for (PersonnageBase p : ctx.personnagesRecruites) {
            if (p.getNom().equals("Mirajane")) { miraS = p; break; }
        }
        if (miraS == null) return "Erreur : Mirajane introuvable dans les recrues.";

        ctx.formation.retirerPersonnage(miraS);
        ctx.personnagesRecruites.remove(miraS);
        ctx.inventaire.retirerMateriau(MATERIAU_MIRAJANE, COUT_MIRAJANE_SS);

        perso_Mirajane_Halphas miraSS = new perso_Mirajane_Halphas();
        while (miraSS.getNiveau() < miraS.getNiveau()) miraSS.monterDeNiveau();
        ctx.personnagesRecruites.add(miraSS);
        ctx.sauvegarde.sauvegarder(ctx);

        return "Mirajane a eveille sa forme demoniaque ultime !\nMirajane Halphas [SS] est desormais disponible dans votre formation.";
    }

    // ── Utilitaire ────────────────────────────────────────────────────────
    public static boolean dejaRecruteParNom(String nom,
                                       ArrayList<PersonnageBase> liste) {
        for (PersonnageBase p : liste)
            if (p.getNom().equalsIgnoreCase(nom)) return true;
        return false;
    }
}