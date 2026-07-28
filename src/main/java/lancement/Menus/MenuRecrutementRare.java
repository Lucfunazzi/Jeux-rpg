package lancement.Menus;

import Joueur.Personnage_principale;
import Personnage.PersonnageBase;
import Personnage.FairyTail.perso_Natsu;
import Personnage.FairyTail.perso_Natsu_Etherion;
import Personnage.FairyTail.perso_Mirajane;
import Personnage.FairyTail.perso_Mirajane_Halphas;
import Personnage.FairyTail.perso_jellal;
import Equipement.Inventaire;
import lancement.Gestionnaires.GestionnaireChasseTresor;
import java.util.ArrayList;
import java.util.Scanner;
import lancement.Formation;
import lancement.GameContext;

/**
 * Recrutement des personnages rares contre des Parchemins de Chasse A/S/SS
 * (obtenus via le mini-jeu Chasse au tresor, voir MenuChasseTresor), un palier
 * de raretes par personnage/evolution — pas de materiau dedie par personnage.
 */
public class MenuRecrutementRare {

    public static final String PARCHEMIN_A  = GestionnaireChasseTresor.PARCHEMIN_A;
    public static final String PARCHEMIN_S  = GestionnaireChasseTresor.PARCHEMIN_S;
    public static final String PARCHEMIN_SS = GestionnaireChasseTresor.PARCHEMIN_SS;

    public static final int COUT_RECRUTEMENT_NATSU = 30;  // Parchemin A -> Natsu [A]
    public static final int COUT_EVOLUTION_NATSU    = 65;  // Parchemin S -> Natsu Etherion [S]

    public static final int COUT_MIRAJANE_S  = 60;  // Parchemin S  -> Mirajane [S]
    public static final int COUT_MIRAJANE_SS = 125;  // Parchemin SS -> Mirajane Halphas [SS]

    public static final int COUT_JELLAL = 75;  // Parchemin S -> Jellal [S]
    public static final int COUT_JELLAL_SS=145;
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
            System.out.println("Parchemins de Chasse : "
                    + inventaire.getQuantiteMateriau(PARCHEMIN_A)  + "x A  |  "
                    + inventaire.getQuantiteMateriau(PARCHEMIN_S)  + "x S  |  "
                    + inventaire.getQuantiteMateriau(PARCHEMIN_SS) + "x SS");
            System.out.println("(Fouillez la Chasse au tresor pour en trouver davantage.)");

            // ── Natsu ──────────────────────────────────────────────────────
            int possedeA         = inventaire.getQuantiteMateriau(PARCHEMIN_A);
            int possedeS         = inventaire.getQuantiteMateriau(PARCHEMIN_S);
            boolean natsuA       = dejaRecruteParNom("Natsu",           personnagesRecruites);
            boolean natsuS       = dejaRecruteParNom("Natsu Etherion",  personnagesRecruites);

            System.out.println();
            System.out.println("[ Natsu ]");
            System.out.println("  1. Natsu [A]  — " + PARCHEMIN_A
                    + " : " + possedeA + "/" + COUT_RECRUTEMENT_NATSU
                    + (natsuA || natsuS ? "  [DEJA RECRUTE]" : ""));
            if (natsuA) {
                System.out.println("  2. Natsu Etherion [S]  — " + PARCHEMIN_S
                        + " : " + possedeS + "/" + COUT_EVOLUTION_NATSU
                        + "  [EVOLUTION]");
            }

            // ── Mirajane ───────────────────────────────────────────────────
            int possedeSS        = inventaire.getQuantiteMateriau(PARCHEMIN_SS);
            boolean miraS        = dejaRecruteParNom("Mirajane",          personnagesRecruites);
            boolean miraSS       = dejaRecruteParNom("Mirajane Halphas",  personnagesRecruites);

            System.out.println();
            System.out.println("[ Mirajane ]");
            System.out.println("  3. Mirajane [S]  — " + PARCHEMIN_S
                    + " : " + possedeS + "/" + COUT_MIRAJANE_S
                    + (miraS || miraSS ? "  [DEJA RECRUTE]" : ""));
            if (miraS) {
                System.out.println("  4. Mirajane Halphas [SS]  — " + PARCHEMIN_SS
                        + " : " + possedeSS + "/" + COUT_MIRAJANE_SS
                        + "  [EVOLUTION]");
            }

            // ── Jellal ─────────────────────────────────────────────────────
            boolean jellalRecru = dejaRecruteParNom("Jellal", personnagesRecruites);

            System.out.println();
            System.out.println("[ Jellal ]");
            System.out.println("  5. Jellal [S]  — " + PARCHEMIN_S
                    + " : " + possedeS + "/" + COUT_JELLAL
                    + (jellalRecru ? "  [DEJA RECRUTE]" : ""));

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
                case 5 -> {
                    if (jellalRecru) System.out.println("Jellal est deja dans vos allies !");
                    else tenterRecrutementJellal(ctx, scanner);
                }
                default -> System.out.println("Choix invalide.");
            }
        }
    }

    // ── Recrutement Natsu A ───────────────────────────────────────────────
    private void tenterRecrutementNatsu(GameContext ctx, Scanner scanner) {
        int possede = ctx.inventaire.getQuantiteMateriau(PARCHEMIN_A);
        if (possede < COUT_RECRUTEMENT_NATSU) {
            System.out.println("Parchemins insuffisants : "
                    + possede + "/" + COUT_RECRUTEMENT_NATSU + " " + PARCHEMIN_A);
            return;
        }
        System.out.println("Recruter Natsu [A] pour " + COUT_RECRUTEMENT_NATSU
                + " " + PARCHEMIN_A + " ? (1 : Oui / 2 : Non)");
        if (!scanner.nextLine().trim().equals("1")) return;

        System.out.println(">> " + recruterNatsu(ctx));
    }

    // ── Evolution Natsu A → Natsu Etherion S ─────────────────────────────
    private void tenterEvolutionNatsu(GameContext ctx, Scanner scanner) {
        int possede = ctx.inventaire.getQuantiteMateriau(PARCHEMIN_S);
        if (possede < COUT_EVOLUTION_NATSU) {
            System.out.println("Parchemins insuffisants : "
                    + possede + "/" + COUT_EVOLUTION_NATSU + " " + PARCHEMIN_S);
            return;
        }
        System.out.println("Evoluer Natsu [A] vers Natsu Etherion [S] pour "
                + COUT_EVOLUTION_NATSU + " " + PARCHEMIN_S + " ?");
        System.out.println("ATTENTION : Natsu [A] sera remplace definitivement. (1 : Oui / 2 : Non)");
        if (!scanner.nextLine().trim().equals("1")) return;

        System.out.println(evoluerNatsu(ctx));
    }

    // ── Recrutement Mirajane S ────────────────────────────────────────────
    private void tenterRecrutementMirajane(GameContext ctx, Scanner scanner) {
        int possede = ctx.inventaire.getQuantiteMateriau(PARCHEMIN_S);
        if (possede < COUT_MIRAJANE_S) {
            System.out.println("Parchemins insuffisants : "
                    + possede + "/" + COUT_MIRAJANE_S + " " + PARCHEMIN_S);
            return;
        }
        System.out.println("Recruter Mirajane [S] pour " + COUT_MIRAJANE_S
                + " " + PARCHEMIN_S + " ? (1 : Oui / 2 : Non)");
        if (!scanner.nextLine().trim().equals("1")) return;

        System.out.println(">> " + recruterMirajane(ctx));
    }

    // ── Evolution Mirajane S → Mirajane Halphas SS ───────────────────────
    private void tenterEvolutionMirajane(GameContext ctx, Scanner scanner) {
        int possede = ctx.inventaire.getQuantiteMateriau(PARCHEMIN_SS);
        if (possede < COUT_MIRAJANE_SS) {
            System.out.println("Parchemins insuffisants : "
                    + possede + "/" + COUT_MIRAJANE_SS + " " + PARCHEMIN_SS);
            return;
        }
        System.out.println("Evoluer Mirajane [S] vers Mirajane Halphas [SS] pour "
                + COUT_MIRAJANE_SS + " " + PARCHEMIN_SS + " ?");
        System.out.println("ATTENTION : Mirajane [S] sera remplacee definitivement. (1 : Oui / 2 : Non)");
        if (!scanner.nextLine().trim().equals("1")) return;

        System.out.println(evoluerMirajane(ctx));
    }

    // ── Recrutement Jellal S ──────────────────────────────────────────────
    private void tenterRecrutementJellal(GameContext ctx, Scanner scanner) {
        int possede = ctx.inventaire.getQuantiteMateriau(PARCHEMIN_S);
        if (possede < COUT_JELLAL) {
            System.out.println("Parchemins insuffisants : "
                    + possede + "/" + COUT_JELLAL + " " + PARCHEMIN_S);
            return;
        }
        System.out.println("Recruter Jellal [S] pour " + COUT_JELLAL
                + " " + PARCHEMIN_S + " ? (1 : Oui / 2 : Non)");
        if (!scanner.nextLine().trim().equals("1")) return;

        System.out.println(">> " + recruterJellal(ctx));
    }

    // ── Logique pure (reutilisable par la console et l'interface graphique) ─

    /** Tente de recruter Natsu [A]. Retourne le message resultat (parchemins non deduits si echec). */
    public String recruterNatsu(GameContext ctx) {
        int possede = ctx.inventaire.getQuantiteMateriau(PARCHEMIN_A);
        if (possede < COUT_RECRUTEMENT_NATSU) {
            return "Parchemins insuffisants : " + possede + "/" + COUT_RECRUTEMENT_NATSU + " " + PARCHEMIN_A;
        }
        ctx.inventaire.retirerMateriau(PARCHEMIN_A, COUT_RECRUTEMENT_NATSU);
        ctx.personnagesRecruites.add(new perso_Natsu());
        ctx.sauvegarde.sauvegarder(ctx);
        return "Natsu a rejoint vos allies !";
    }

    /** Tente de faire evoluer Natsu [A] en Natsu Etherion [S]. */
    public String evoluerNatsu(GameContext ctx) {
        int possede = ctx.inventaire.getQuantiteMateriau(PARCHEMIN_S);
        if (possede < COUT_EVOLUTION_NATSU) {
            return "Parchemins insuffisants : " + possede + "/" + COUT_EVOLUTION_NATSU + " " + PARCHEMIN_S;
        }

        PersonnageBase natsuA = null;
        for (PersonnageBase p : ctx.personnagesRecruites) {
            if (p.getNom().equals("Natsu")) { natsuA = p; break; }
        }
        if (natsuA == null) return "Erreur : Natsu introuvable dans les recrues.";

        ctx.formation.retirerPersonnage(natsuA);
        ctx.personnagesRecruites.remove(natsuA);
        ctx.inventaire.retirerMateriau(PARCHEMIN_S, COUT_EVOLUTION_NATSU);

        perso_Natsu_Etherion natsuS = new perso_Natsu_Etherion();
        while (natsuS.getNiveau() < natsuA.getNiveau()) natsuS.monterDeNiveau();
        ctx.personnagesRecruites.add(natsuS);
        ctx.sauvegarde.sauvegarder(ctx);

        return "Natsu a evolue en Natsu Etherion [S] !\nNatsu Etherion est desormais disponible dans votre formation.";
    }

    /** Tente de recruter Mirajane [S]. */
    public String recruterMirajane(GameContext ctx) {
        int possede = ctx.inventaire.getQuantiteMateriau(PARCHEMIN_S);
        if (possede < COUT_MIRAJANE_S) {
            return "Parchemins insuffisants : " + possede + "/" + COUT_MIRAJANE_S + " " + PARCHEMIN_S;
        }
        ctx.inventaire.retirerMateriau(PARCHEMIN_S, COUT_MIRAJANE_S);
        ctx.personnagesRecruites.add(new perso_Mirajane());
        ctx.sauvegarde.sauvegarder(ctx);
        return "Mirajane a rejoint vos allies !";
    }

    /** Tente de faire evoluer Mirajane [S] en Mirajane Halphas [SS]. */
    public String evoluerMirajane(GameContext ctx) {
        int possede = ctx.inventaire.getQuantiteMateriau(PARCHEMIN_SS);
        if (possede < COUT_MIRAJANE_SS) {
            return "Parchemins insuffisants : " + possede + "/" + COUT_MIRAJANE_SS + " " + PARCHEMIN_SS;
        }

        PersonnageBase miraS = null;
        for (PersonnageBase p : ctx.personnagesRecruites) {
            if (p.getNom().equals("Mirajane")) { miraS = p; break; }
        }
        if (miraS == null) return "Erreur : Mirajane introuvable dans les recrues.";

        ctx.formation.retirerPersonnage(miraS);
        ctx.personnagesRecruites.remove(miraS);
        ctx.inventaire.retirerMateriau(PARCHEMIN_SS, COUT_MIRAJANE_SS);

        perso_Mirajane_Halphas miraSS = new perso_Mirajane_Halphas();
        while (miraSS.getNiveau() < miraS.getNiveau()) miraSS.monterDeNiveau();
        ctx.personnagesRecruites.add(miraSS);
        ctx.sauvegarde.sauvegarder(ctx);

        return "Mirajane a eveille sa forme demoniaque ultime !\nMirajane Halphas [SS] est desormais disponible dans votre formation.";
    }

    /** Tente de recruter Jellal [S]. Retourne le message resultat (parchemins non deduits si echec). */
    public String recruterJellal(GameContext ctx) {
        int possede = ctx.inventaire.getQuantiteMateriau(PARCHEMIN_S);
        if (possede < COUT_JELLAL) {
            return "Parchemins insuffisants : " + possede + "/" + COUT_JELLAL + " " + PARCHEMIN_S;
        }
        ctx.inventaire.retirerMateriau(PARCHEMIN_S, COUT_JELLAL);
        ctx.personnagesRecruites.add(new perso_jellal());
        ctx.sauvegarde.sauvegarder(ctx);
        return "Jellal a rejoint vos allies !";
    }

    // ── Utilitaire ────────────────────────────────────────────────────────
    public static boolean dejaRecruteParNom(String nom,
                                       ArrayList<PersonnageBase> liste) {
        for (PersonnageBase p : liste)
            if (p.getNom().equalsIgnoreCase(nom)) return true;
        return false;
    }
}
