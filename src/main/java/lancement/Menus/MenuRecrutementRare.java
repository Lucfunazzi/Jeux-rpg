package lancement.Menus;

import Joueur.Personnage_principale;
import Personnage.PersonnageBase;
import Personnage.FairyTail.perso_Natsu;
import Personnage.FairyTail.perso_Natsu_Etherion;
import Personnage.FairyTail.perso_Gray;
import Personnage.FairyTail.perso_Mirajane;
import Personnage.FairyTail.perso_Mirajane_Halphas;
import Personnage.FairyTail.perso_jellal;
import Personnage.FairyTail.jellal_Arc_intermagie;
import Personnage.FairyTail.perso_Erza;
import Personnage.FairyTail.perso_Luxus;
import Personnage.FairyTail.perso_Brain;
import Personnage.FairyTail.perso_Hades;
import Personnage.FairyTail.perso_Lucy;
import Personnage.FairyTail.perso_Jubia_4elements;
import Personnage.FairyTail.perso_Wendy;
import Equipement.Inventaire;
import Personnage.FairyTail.perso_Lucy_intermagie;
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

    public static final int COUT_RECRUTEMENT_GRAY = 30;  // Parchemin A -> Gray [A] (memes valeurs que Natsu, rang A)

    public static final int COUT_RECRUTEMENT_LUCY  = 30;  // Parchemin A -> Lucy [A] (memes valeurs que Natsu/Gray, pas d'evolution)
    public static final int COUT_EVOLUTION_LUCY = 65;
    public static final int COUT_RECRUTEMENT_JUBIA  = 30;  // Parchemin A -> Jubia [A] (memes valeurs que Natsu/Gray, pas d'evolution)
    public static final int COUT_RECRUTEMENT_WENDY  = 30;  // Parchemin A -> Wendy [A] (memes valeurs que Natsu/Gray, pas d'evolution)

    public static final int COUT_MIRAJANE_S  = 60;  // Parchemin S  -> Mirajane [S]
    public static final int COUT_MIRAJANE_SS = 125;  // Parchemin SS -> Mirajane Halphas [SS]

    public static final int COUT_JELLAL = 75;  // Parchemin S -> Jellal [S]
    public static final int COUT_JELLAL_SS=145;  // Parchemin SS -> Jellal Intermagie [SS]

    public static final int COUT_ERZA = 80;  // Parchemin S -> Erza [S]

    public static final int COUT_LUXUS = 160;  // Parchemin SS -> Luxus [SS] (recrutement direct, pas d'evolution)

    public static final int COUT_BRAIN = 90;   // Parchemin S -> Brain [S]
    public static final int COUT_HADES = 180;  // Parchemin SS -> Hades [SSS] (recrutement direct, pas d'evolution)

    // ── Niveaux requis ────────────────────────────────────────────────────
    public static final int NIVEAU_REQUIS_NATSU_GRAY            = 30;  // Recrutement Natsu / Gray
    public static final int NIVEAU_REQUIS_EVOLUTION_NATSU       = 40;  // Evolution Natsu Etherion (evolution de Gray pas encore implementee)
    public static final int NIVEAU_REQUIS_EVOLUTION_LUCY = 40;
    public static final int NIVEAU_REQUIS_MIRAJANE_JELLAL           = 70;  // Recrutement Mirajane / Jellal
    public static final int NIVEAU_REQUIS_EVOLUTION_MIRAJANE_JELLAL = 80;  // Evolution Mirajane Halphas / Jellal Intermagie
    public static final int NIVEAU_REQUIS_ERZA                  = 85;  // Recrutement Erza (evolution niveau 95 pas encore implementee)
    public static final int NIVEAU_REQUIS_LUXUS                 = 90;  // Recrutement Luxus [SS]
    public static final int NIVEAU_REQUIS_BRAIN                 = 70;  // Recrutement Brain [S]
    public static final int NIVEAU_REQUIS_HADES                 = 100; // Recrutement Hades [SSS]

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
            System.out.println("[ Natsu ]  (niveau " + NIVEAU_REQUIS_NATSU_GRAY + " requis)");
            System.out.println("  1. Natsu [A]  — " + PARCHEMIN_A
                    + " : " + possedeA + "/" + COUT_RECRUTEMENT_NATSU
                    + (natsuA || natsuS ? "  [DEJA RECRUTE]" : ""));
            if (natsuA) {
                System.out.println("  2. Natsu Etherion [S]  — " + PARCHEMIN_S
                        + " : " + possedeS + "/" + COUT_EVOLUTION_NATSU
                        + "  [EVOLUTION]  (niveau " + NIVEAU_REQUIS_EVOLUTION_NATSU + " requis)");
            }
            
            
            // ── Lucy ───────────────────────────────────────────────────────
            boolean lucyRecru = dejaRecruteParNom("Lucy", personnagesRecruites);
            boolean lucyEvolue = dejaRecruteParNom("Lucy intermagie",personnagesRecruites);
            
            System.out.println();
            System.out.println("[ Lucy ]  (niveau " + NIVEAU_REQUIS_NATSU_GRAY + " requis)");
            System.out.println(" 12. Lucy [A]  — " + PARCHEMIN_A
                    + " : " + possedeA + "/" + COUT_RECRUTEMENT_LUCY
                    + (lucyRecru || lucyEvolue ? "  [DEJA RECRUTE]" : ""));
            if (lucyRecru){
                System.out.println(" 13. Lucy intermagie [S]  — " + PARCHEMIN_S
                        + " : " + possedeS + "/" + COUT_EVOLUTION_LUCY
                        + "  [EVOLUTION]  (niveau " + NIVEAU_REQUIS_EVOLUTION_LUCY + " requis)");
            }
            // ── Gray ───────────────────────────────────────────────────────
            boolean grayRecru = dejaRecruteParNom("Gray", personnagesRecruites);

            System.out.println();
            System.out.println("[ Gray ]  (niveau " + NIVEAU_REQUIS_NATSU_GRAY + " requis)");
            System.out.println("  3. Gray [A]  — " + PARCHEMIN_A
                    + " : " + possedeA + "/" + COUT_RECRUTEMENT_GRAY
                    + (grayRecru ? "  [DEJA RECRUTE]" : ""));

            // ── Mirajane ───────────────────────────────────────────────────
            int possedeSS        = inventaire.getQuantiteMateriau(PARCHEMIN_SS);
            boolean miraS        = dejaRecruteParNom("Mirajane",          personnagesRecruites);
            boolean miraSS       = dejaRecruteParNom("Mirajane Halphas",  personnagesRecruites);

            System.out.println();
            System.out.println("[ Mirajane ]  (niveau " + NIVEAU_REQUIS_MIRAJANE_JELLAL + " requis)");
            System.out.println("  4. Mirajane [S]  — " + PARCHEMIN_S
                    + " : " + possedeS + "/" + COUT_MIRAJANE_S
                    + (miraS || miraSS ? "  [DEJA RECRUTE]" : ""));
            if (miraS) {
                System.out.println("  5. Mirajane Halphas [SS]  — " + PARCHEMIN_SS
                        + " : " + possedeSS + "/" + COUT_MIRAJANE_SS
                        + "  [EVOLUTION]  (niveau " + NIVEAU_REQUIS_EVOLUTION_MIRAJANE_JELLAL + " requis)");
            }

            // ── Jellal ─────────────────────────────────────────────────────
            boolean jellalRecru    = dejaRecruteParNom("Jellal", personnagesRecruites);
            boolean jellalEvolue   = dejaRecruteParNom("Jellal Intermagie", personnagesRecruites);

            System.out.println();
            System.out.println("[ Jellal ]  (niveau " + NIVEAU_REQUIS_MIRAJANE_JELLAL + " requis)");
            System.out.println("  6. Jellal [S]  — " + PARCHEMIN_S
                    + " : " + possedeS + "/" + COUT_JELLAL
                    + (jellalRecru || jellalEvolue ? "  [DEJA RECRUTE]" : ""));
            if (jellalRecru) {
                System.out.println("  7. Jellal Intermagie [SS]  — " + PARCHEMIN_SS
                        + " : " + possedeSS + "/" + COUT_JELLAL_SS
                        + "  [EVOLUTION]  (niveau " + NIVEAU_REQUIS_EVOLUTION_MIRAJANE_JELLAL + " requis)");
            }

            // ── Erza ───────────────────────────────────────────────────────
            boolean erzaRecru = dejaRecruteParNom("Erza", personnagesRecruites);

            System.out.println();
            System.out.println("[ Erza ]  (niveau " + NIVEAU_REQUIS_ERZA + " requis)");
            System.out.println("  8. Erza [S]  — " + PARCHEMIN_S
                    + " : " + possedeS + "/" + COUT_ERZA
                    + (erzaRecru ? "  [DEJA RECRUTE]" : ""));

            // ── Luxus ──────────────────────────────────────────────────────
            boolean luxusRecru = dejaRecruteParNom("Luxus", personnagesRecruites);

            System.out.println();
            System.out.println("[ Luxus ]  (niveau " + NIVEAU_REQUIS_LUXUS + " requis)");
            System.out.println("  9. Luxus [SS]  — " + PARCHEMIN_SS
                    + " : " + possedeSS + "/" + COUT_LUXUS
                    + (luxusRecru ? "  [DEJA RECRUTE]" : ""));

            // ── Brain ──────────────────────────────────────────────────────
            boolean brainRecru = dejaRecruteParNom("Brain", personnagesRecruites);

            System.out.println();
            System.out.println("[ Brain ]  (niveau " + NIVEAU_REQUIS_BRAIN + " requis)");
            System.out.println(" 10. Brain [S]  — " + PARCHEMIN_S
                    + " : " + possedeS + "/" + COUT_BRAIN
                    + (brainRecru ? "  [DEJA RECRUTE]" : ""));

            // ── Hades ──────────────────────────────────────────────────────
            boolean hadesRecru = dejaRecruteParNom("Hades", personnagesRecruites);

            System.out.println();
            System.out.println("[ Hades ]  (niveau " + NIVEAU_REQUIS_HADES + " requis)");
            System.out.println(" 11. Hades [SSS]  — " + PARCHEMIN_SS
                    + " : " + possedeSS + "/" + COUT_HADES
                    + (hadesRecru ? "  [DEJA RECRUTE]" : ""));

            

            // ── Jubia ──────────────────────────────────────────────────────
            boolean jubiaRecru = dejaRecruteParNom("Jubia", personnagesRecruites);

            System.out.println();
            System.out.println("[ Jubia ]  (niveau " + NIVEAU_REQUIS_NATSU_GRAY + " requis)");
            System.out.println(" 14. Jubia [A]  — " + PARCHEMIN_A
                    + " : " + possedeA + "/" + COUT_RECRUTEMENT_JUBIA
                    + (jubiaRecru ? "  [DEJA RECRUTE]" : ""));

            // ── Wendy ──────────────────────────────────────────────────────
            boolean wendyRecru = dejaRecruteParNom("Wendy", personnagesRecruites);

            System.out.println();
            System.out.println("[ Wendy ]  (niveau " + NIVEAU_REQUIS_NATSU_GRAY + " requis)");
            System.out.println(" 15. Wendy [A]  — " + PARCHEMIN_A
                    + " : " + possedeA + "/" + COUT_RECRUTEMENT_WENDY
                    + (wendyRecru ? "  [DEJA RECRUTE]" : ""));

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
                    if (grayRecru) System.out.println("Gray est deja dans vos allies !");
                    else tenterRecrutementGray(ctx, scanner);
                }
                case 4 -> {
                    if (miraS || miraSS) System.out.println("Mirajane est deja dans vos allies !");
                    else tenterRecrutementMirajane(ctx, scanner);
                }
                case 5 -> {
                    if (!miraS) System.out.println("Vous devez d'abord recruter Mirajane [S].");
                    else tenterEvolutionMirajane(ctx, scanner);
                }
                case 6 -> {
                    if (jellalRecru || jellalEvolue) System.out.println("Jellal est deja dans vos allies !");
                    else tenterRecrutementJellal(ctx, scanner);
                }
                case 7 -> {
                    if (!jellalRecru) System.out.println("Vous devez d'abord recruter Jellal [S].");
                    else tenterEvolutionJellal(ctx, scanner);
                }
                case 8 -> {
                    if (erzaRecru) System.out.println("Erza est deja dans vos allies !");
                    else tenterRecrutementErza(ctx, scanner);
                }
                case 9 -> {
                    if (luxusRecru) System.out.println("Luxus est deja dans vos allies !");
                    else tenterRecrutementLuxus(ctx, scanner);
                }
                case 10 -> {
                    if (brainRecru) System.out.println("Brain est deja dans vos allies !");
                    else tenterRecrutementBrain(ctx, scanner);
                }
                case 11 -> {
                    if (hadesRecru) System.out.println("Hades est deja dans vos allies !");
                    else tenterRecrutementHades(ctx, scanner);
                }
                case 12 -> {
                    if (lucyRecru || lucyEvolue) System.out.println("Lucy est deja dans vos allies !");
                    else tenterRecrutementLucy(ctx, scanner);
                }
                case 13 ->{
                    if (!lucyRecru) System.out.println("Vous devez d'abord recruter Lucy [A]");
                    else tenterEvolutionLucy(ctx,scanner);
                }
                case 14 -> {
                    if (jubiaRecru) System.out.println("Jubia est deja dans vos allies !");
                    else tenterRecrutementJubia(ctx, scanner);
                }
                case 15 -> {
                    if (wendyRecru) System.out.println("Wendy est deja dans vos allies !");
                    else tenterRecrutementWendy(ctx, scanner);
                }
                default -> System.out.println("Choix invalide.");
            }
        }
    }

    // ── Recrutement Natsu A ───────────────────────────────────────────────
    private void tenterRecrutementNatsu(GameContext ctx, Scanner scanner) {
        if (ctx.joueur.getNiveau() < NIVEAU_REQUIS_NATSU_GRAY) {
            System.out.println("Natsu se debloque au niveau " + NIVEAU_REQUIS_NATSU_GRAY + " !");
            return;
        }
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
        if (ctx.joueur.getNiveau() < NIVEAU_REQUIS_EVOLUTION_NATSU) {
            System.out.println("Cette evolution se debloque au niveau " + NIVEAU_REQUIS_EVOLUTION_NATSU + " !");
            return;
        }
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

    // ── Recrutement Gray A ────────────────────────────────────────────────
    private void tenterRecrutementGray(GameContext ctx, Scanner scanner) {
        if (ctx.joueur.getNiveau() < NIVEAU_REQUIS_NATSU_GRAY) {
            System.out.println("Gray se debloque au niveau " + NIVEAU_REQUIS_NATSU_GRAY + " !");
            return;
        }
        int possede = ctx.inventaire.getQuantiteMateriau(PARCHEMIN_A);
        if (possede < COUT_RECRUTEMENT_GRAY) {
            System.out.println("Parchemins insuffisants : "
                    + possede + "/" + COUT_RECRUTEMENT_GRAY + " " + PARCHEMIN_A);
            return;
        }
        System.out.println("Recruter Gray [A] pour " + COUT_RECRUTEMENT_GRAY
                + " " + PARCHEMIN_A + " ? (1 : Oui / 2 : Non)");
        if (!scanner.nextLine().trim().equals("1")) return;

        System.out.println(">> " + recruterGray(ctx));
    }

    // ── Recrutement Lucy A ────────────────────────────────────────────────
    private void tenterRecrutementLucy(GameContext ctx, Scanner scanner) {
        if (ctx.joueur.getNiveau() < NIVEAU_REQUIS_NATSU_GRAY) {
            System.out.println("Lucy se debloque au niveau " + NIVEAU_REQUIS_NATSU_GRAY + " !");
            return;
        }
        int possede = ctx.inventaire.getQuantiteMateriau(PARCHEMIN_A);
        if (possede < COUT_RECRUTEMENT_LUCY) {
            System.out.println("Parchemins insuffisants : "
                    + possede + "/" + COUT_RECRUTEMENT_LUCY + " " + PARCHEMIN_A);
            return;
        }
        System.out.println("Recruter Lucy [A] pour " + COUT_RECRUTEMENT_LUCY
                + " " + PARCHEMIN_A + " ? (1 : Oui / 2 : Non)");
        if (!scanner.nextLine().trim().equals("1")) return;

        System.out.println(">> " + recruterLucy(ctx));
    }
    
     private void tenterEvolutionLucy(GameContext ctx, Scanner scanner) {
        if (ctx.joueur.getNiveau() < NIVEAU_REQUIS_EVOLUTION_LUCY) {
            System.out.println("Cette evolution se debloque au niveau " + NIVEAU_REQUIS_EVOLUTION_LUCY + " !");
            return;
        }
        int possede = ctx.inventaire.getQuantiteMateriau(PARCHEMIN_S);
        if (possede < COUT_EVOLUTION_LUCY) {
            System.out.println("Parchemins insuffisants : "
                    + possede + "/" + COUT_EVOLUTION_LUCY + " " + PARCHEMIN_S);
            return;
        }
        System.out.println("Evoluer Lucy [A] vers Lucy intermagie [S] pour "
                + COUT_EVOLUTION_LUCY + " " + PARCHEMIN_S + " ?");
        System.out.println("ATTENTION : Lucy [A] sera remplacee definitivement. (1 : Oui / 2 : Non)");
        if (!scanner.nextLine().trim().equals("1")) return;

        System.out.println(evoluerLucy(ctx));
    }


    // ── Recrutement Jubia A ───────────────────────────────────────────────
    private void tenterRecrutementJubia(GameContext ctx, Scanner scanner) {
        if (ctx.joueur.getNiveau() < NIVEAU_REQUIS_NATSU_GRAY) {
            System.out.println("Jubia se debloque au niveau " + NIVEAU_REQUIS_NATSU_GRAY + " !");
            return;
        }
        int possede = ctx.inventaire.getQuantiteMateriau(PARCHEMIN_A);
        if (possede < COUT_RECRUTEMENT_JUBIA) {
            System.out.println("Parchemins insuffisants : "
                    + possede + "/" + COUT_RECRUTEMENT_JUBIA + " " + PARCHEMIN_A);
            return;
        }
        System.out.println("Recruter Jubia [A] pour " + COUT_RECRUTEMENT_JUBIA
                + " " + PARCHEMIN_A + " ? (1 : Oui / 2 : Non)");
        if (!scanner.nextLine().trim().equals("1")) return;

        System.out.println(">> " + recruterJubia(ctx));
    }

    // ── Recrutement Wendy A ───────────────────────────────────────────────
    private void tenterRecrutementWendy(GameContext ctx, Scanner scanner) {
        if (ctx.joueur.getNiveau() < NIVEAU_REQUIS_NATSU_GRAY) {
            System.out.println("Wendy se debloque au niveau " + NIVEAU_REQUIS_NATSU_GRAY + " !");
            return;
        }
        int possede = ctx.inventaire.getQuantiteMateriau(PARCHEMIN_A);
        if (possede < COUT_RECRUTEMENT_WENDY) {
            System.out.println("Parchemins insuffisants : "
                    + possede + "/" + COUT_RECRUTEMENT_WENDY + " " + PARCHEMIN_A);
            return;
        }
        System.out.println("Recruter Wendy [A] pour " + COUT_RECRUTEMENT_WENDY
                + " " + PARCHEMIN_A + " ? (1 : Oui / 2 : Non)");
        if (!scanner.nextLine().trim().equals("1")) return;

        System.out.println(">> " + recruterWendy(ctx));
    }

    // ── Recrutement Mirajane S ────────────────────────────────────────────
    private void tenterRecrutementMirajane(GameContext ctx, Scanner scanner) {
        if (ctx.joueur.getNiveau() < NIVEAU_REQUIS_MIRAJANE_JELLAL) {
            System.out.println("Mirajane se debloque au niveau " + NIVEAU_REQUIS_MIRAJANE_JELLAL + " !");
            return;
        }
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
        if (ctx.joueur.getNiveau() < NIVEAU_REQUIS_EVOLUTION_MIRAJANE_JELLAL) {
            System.out.println("Cette evolution se debloque au niveau " + NIVEAU_REQUIS_EVOLUTION_MIRAJANE_JELLAL + " !");
            return;
        }
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
        if (ctx.joueur.getNiveau() < NIVEAU_REQUIS_MIRAJANE_JELLAL) {
            System.out.println("Jellal se debloque au niveau " + NIVEAU_REQUIS_MIRAJANE_JELLAL + " !");
            return;
        }
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

    // ── Evolution Jellal S → Jellal Intermagie SS ────────────────────────
    private void tenterEvolutionJellal(GameContext ctx, Scanner scanner) {
        if (ctx.joueur.getNiveau() < NIVEAU_REQUIS_EVOLUTION_MIRAJANE_JELLAL) {
            System.out.println("Cette evolution se debloque au niveau " + NIVEAU_REQUIS_EVOLUTION_MIRAJANE_JELLAL + " !");
            return;
        }
        int possede = ctx.inventaire.getQuantiteMateriau(PARCHEMIN_SS);
        if (possede < COUT_JELLAL_SS) {
            System.out.println("Parchemins insuffisants : "
                    + possede + "/" + COUT_JELLAL_SS + " " + PARCHEMIN_SS);
            return;
        }
        System.out.println("Evoluer Jellal [S] vers Jellal Intermagie [SS] pour "
                + COUT_JELLAL_SS + " " + PARCHEMIN_SS + " ?");
        System.out.println("ATTENTION : Jellal [S] sera remplace definitivement. (1 : Oui / 2 : Non)");
        if (!scanner.nextLine().trim().equals("1")) return;

        System.out.println(evoluerJellal(ctx));
    }

    // ── Recrutement Erza S ────────────────────────────────────────────────
    private void tenterRecrutementErza(GameContext ctx, Scanner scanner) {
        if (ctx.joueur.getNiveau() < NIVEAU_REQUIS_ERZA) {
            System.out.println("Erza se debloque au niveau " + NIVEAU_REQUIS_ERZA + " !");
            return;
        }
        int possede = ctx.inventaire.getQuantiteMateriau(PARCHEMIN_S);
        if (possede < COUT_ERZA) {
            System.out.println("Parchemins insuffisants : "
                    + possede + "/" + COUT_ERZA + " " + PARCHEMIN_S);
            return;
        }
        System.out.println("Recruter Erza [S] pour " + COUT_ERZA
                + " " + PARCHEMIN_S + " ? (1 : Oui / 2 : Non)");
        if (!scanner.nextLine().trim().equals("1")) return;

        System.out.println(">> " + recruterErza(ctx));
    }

    // ── Recrutement Luxus SS ──────────────────────────────────────────────
    private void tenterRecrutementLuxus(GameContext ctx, Scanner scanner) {
        if (ctx.joueur.getNiveau() < NIVEAU_REQUIS_LUXUS) {
            System.out.println("Luxus se debloque au niveau " + NIVEAU_REQUIS_LUXUS + " !");
            return;
        }
        int possede = ctx.inventaire.getQuantiteMateriau(PARCHEMIN_SS);
        if (possede < COUT_LUXUS) {
            System.out.println("Parchemins insuffisants : "
                    + possede + "/" + COUT_LUXUS + " " + PARCHEMIN_SS);
            return;
        }
        System.out.println("Recruter Luxus [SS] pour " + COUT_LUXUS
                + " " + PARCHEMIN_SS + " ? (1 : Oui / 2 : Non)");
        if (!scanner.nextLine().trim().equals("1")) return;

        System.out.println(">> " + recruterLuxus(ctx));
    }

    // ── Recrutement Brain S ──────────────────────────────────────────────
    private void tenterRecrutementBrain(GameContext ctx, Scanner scanner) {
        if (ctx.joueur.getNiveau() < NIVEAU_REQUIS_BRAIN) {
            System.out.println("Brain se debloque au niveau " + NIVEAU_REQUIS_BRAIN + " !");
            return;
        }
        int possede = ctx.inventaire.getQuantiteMateriau(PARCHEMIN_S);
        if (possede < COUT_BRAIN) {
            System.out.println("Parchemins insuffisants : "
                    + possede + "/" + COUT_BRAIN + " " + PARCHEMIN_S);
            return;
        }
        System.out.println("Recruter Brain [S] pour " + COUT_BRAIN
                + " " + PARCHEMIN_S + " ? (1 : Oui / 2 : Non)");
        if (!scanner.nextLine().trim().equals("1")) return;

        System.out.println(">> " + recruterBrain(ctx));
    }

    // ── Recrutement Hades SSS ────────────────────────────────────────────
    private void tenterRecrutementHades(GameContext ctx, Scanner scanner) {
        if (ctx.joueur.getNiveau() < NIVEAU_REQUIS_HADES) {
            System.out.println("Hades se debloque au niveau " + NIVEAU_REQUIS_HADES + " !");
            return;
        }
        int possede = ctx.inventaire.getQuantiteMateriau(PARCHEMIN_SS);
        if (possede < COUT_HADES) {
            System.out.println("Parchemins insuffisants : "
                    + possede + "/" + COUT_HADES + " " + PARCHEMIN_SS);
            return;
        }
        System.out.println("Recruter Hades [SSS] pour " + COUT_HADES
                + " " + PARCHEMIN_SS + " ? (1 : Oui / 2 : Non)");
        if (!scanner.nextLine().trim().equals("1")) return;

        System.out.println(">> " + recruterHades(ctx));
    }

    // ── Logique pure (reutilisable par la console et l'interface graphique) ─

    /** Tente de recruter Natsu [A]. Retourne le message resultat (parchemins non deduits si echec). */
    public String recruterNatsu(GameContext ctx) {
        if (ctx.joueur.getNiveau() < NIVEAU_REQUIS_NATSU_GRAY) {
            return "Natsu se debloque au niveau " + NIVEAU_REQUIS_NATSU_GRAY + " !";
        }
        int possede = ctx.inventaire.getQuantiteMateriau(PARCHEMIN_A);
        if (possede < COUT_RECRUTEMENT_NATSU) {
            return "Parchemins insuffisants : " + possede + "/" + COUT_RECRUTEMENT_NATSU + " " + PARCHEMIN_A;
        }
        ctx.inventaire.retirerMateriau(PARCHEMIN_A, COUT_RECRUTEMENT_NATSU);
        ctx.personnagesRecruites.add(new perso_Natsu());
        ctx.sauvegarde.sauvegarder(ctx);
        return "Natsu a rejoint vos allies !";
    }

    /** Tente de recruter Gray [A] (memes valeurs que Natsu, rang A). */
    public String recruterGray(GameContext ctx) {
        if (ctx.joueur.getNiveau() < NIVEAU_REQUIS_NATSU_GRAY) {
            return "Gray se debloque au niveau " + NIVEAU_REQUIS_NATSU_GRAY + " !";
        }
        int possede = ctx.inventaire.getQuantiteMateriau(PARCHEMIN_A);
        if (possede < COUT_RECRUTEMENT_GRAY) {
            return "Parchemins insuffisants : " + possede + "/" + COUT_RECRUTEMENT_GRAY + " " + PARCHEMIN_A;
        }
        ctx.inventaire.retirerMateriau(PARCHEMIN_A, COUT_RECRUTEMENT_GRAY);
        ctx.personnagesRecruites.add(new perso_Gray());
        ctx.sauvegarde.sauvegarder(ctx);
        return "Gray a rejoint vos allies !";
    }

    /** Tente de recruter Lucy [A] (memes valeurs que Natsu/Gray, pas d'evolution). */
    public String recruterLucy(GameContext ctx) {
        if (ctx.joueur.getNiveau() < NIVEAU_REQUIS_NATSU_GRAY) {
            return "Lucy se debloque au niveau " + NIVEAU_REQUIS_NATSU_GRAY + " !";
        }
        int possede = ctx.inventaire.getQuantiteMateriau(PARCHEMIN_A);
        if (possede < COUT_RECRUTEMENT_LUCY) {
            return "Parchemins insuffisants : " + possede + "/" + COUT_RECRUTEMENT_LUCY + " " + PARCHEMIN_A;
        }
        ctx.inventaire.retirerMateriau(PARCHEMIN_A, COUT_RECRUTEMENT_LUCY);
        ctx.personnagesRecruites.add(new perso_Lucy());
        ctx.sauvegarde.sauvegarder(ctx);
        return "Lucy a rejoint vos allies !";
    }

    /** Tente de recruter Jubia [A] (memes valeurs que Natsu/Gray, pas d'evolution). */
    public String recruterJubia(GameContext ctx) {
        if (ctx.joueur.getNiveau() < NIVEAU_REQUIS_NATSU_GRAY) {
            return "Jubia se debloque au niveau " + NIVEAU_REQUIS_NATSU_GRAY + " !";
        }
        int possede = ctx.inventaire.getQuantiteMateriau(PARCHEMIN_A);
        if (possede < COUT_RECRUTEMENT_JUBIA) {
            return "Parchemins insuffisants : " + possede + "/" + COUT_RECRUTEMENT_JUBIA + " " + PARCHEMIN_A;
        }
        ctx.inventaire.retirerMateriau(PARCHEMIN_A, COUT_RECRUTEMENT_JUBIA);
        ctx.personnagesRecruites.add(new perso_Jubia_4elements());
        ctx.sauvegarde.sauvegarder(ctx);
        return "Jubia a rejoint vos allies !";
    }

    /** Tente de recruter Wendy [A] (memes valeurs que Natsu/Gray, pas d'evolution). */
    public String recruterWendy(GameContext ctx) {
        if (ctx.joueur.getNiveau() < NIVEAU_REQUIS_NATSU_GRAY) {
            return "Wendy se debloque au niveau " + NIVEAU_REQUIS_NATSU_GRAY + " !";
        }
        int possede = ctx.inventaire.getQuantiteMateriau(PARCHEMIN_A);
        if (possede < COUT_RECRUTEMENT_WENDY) {
            return "Parchemins insuffisants : " + possede + "/" + COUT_RECRUTEMENT_WENDY + " " + PARCHEMIN_A;
        }
        ctx.inventaire.retirerMateriau(PARCHEMIN_A, COUT_RECRUTEMENT_WENDY);
        ctx.personnagesRecruites.add(new perso_Wendy());
        ctx.sauvegarde.sauvegarder(ctx);
        return "Wendy a rejoint vos allies !";
    }

    /** Tente de faire evoluer Natsu [A] en Natsu Etherion [S]. */
    public String evoluerNatsu(GameContext ctx) {
        if (ctx.joueur.getNiveau() < NIVEAU_REQUIS_EVOLUTION_NATSU) {
            return "Cette evolution se debloque au niveau " + NIVEAU_REQUIS_EVOLUTION_NATSU + " !";
        }
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
    public String evoluerLucy(GameContext ctx){
        if (ctx.joueur.getNiveau() < NIVEAU_REQUIS_EVOLUTION_LUCY){
            return "Cette evolution se debloque au niveau " + NIVEAU_REQUIS_EVOLUTION_LUCY + " ! ";
        }
        int possede =ctx.inventaire.getQuantiteMateriau(PARCHEMIN_S);
        if (possede < COUT_EVOLUTION_LUCY){
        return "Parchemins insuffisants : " + possede + "/" + COUT_EVOLUTION_LUCY + " " + PARCHEMIN_S;
    }
        
        PersonnageBase LucyRecrut = null;
        for (PersonnageBase p : ctx.personnagesRecruites){
            if (p.getNom().equals("Lucy")) {LucyRecrut = p; break;}
        }
        if (LucyRecrut == null) return "Erreur : Lucy introuvable dans les recrues";
        
        ctx.formation.retirerPersonnage(LucyRecrut);
        ctx.personnagesRecruites.remove(LucyRecrut);
        ctx.inventaire.retirerMateriau(PARCHEMIN_S,COUT_EVOLUTION_LUCY);
        perso_Lucy_intermagie LucyS = new perso_Lucy_intermagie();
        while (LucyS.getNiveau() < LucyRecrut.getNiveau()) LucyS.monterDeNiveau();
        ctx.personnagesRecruites.add(LucyS);
        ctx.sauvegarde.sauvegarder(ctx);
        return "Lucy a evolue en Lucy intermagie [S] !\nLucy intermagie est desormais disponible dans votre formation";
    }

    /** Tente de recruter Mirajane [S]. */
    public String recruterMirajane(GameContext ctx) {
        if (ctx.joueur.getNiveau() < NIVEAU_REQUIS_MIRAJANE_JELLAL) {
            return "Mirajane se debloque au niveau " + NIVEAU_REQUIS_MIRAJANE_JELLAL + " !";
        }
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
        if (ctx.joueur.getNiveau() < NIVEAU_REQUIS_EVOLUTION_MIRAJANE_JELLAL) {
            return "Cette evolution se debloque au niveau " + NIVEAU_REQUIS_EVOLUTION_MIRAJANE_JELLAL + " !";
        }
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
        if (ctx.joueur.getNiveau() < NIVEAU_REQUIS_MIRAJANE_JELLAL) {
            return "Jellal se debloque au niveau " + NIVEAU_REQUIS_MIRAJANE_JELLAL + " !";
        }
        int possede = ctx.inventaire.getQuantiteMateriau(PARCHEMIN_S);
        if (possede < COUT_JELLAL) {
            return "Parchemins insuffisants : " + possede + "/" + COUT_JELLAL + " " + PARCHEMIN_S;
        }
        ctx.inventaire.retirerMateriau(PARCHEMIN_S, COUT_JELLAL);
        ctx.personnagesRecruites.add(new perso_jellal());
        ctx.sauvegarde.sauvegarder(ctx);
        return "Jellal a rejoint vos allies !";
    }

    /** Tente de faire evoluer Jellal [S] en Jellal Intermagie [SS]. */
    public String evoluerJellal(GameContext ctx) {
        if (ctx.joueur.getNiveau() < NIVEAU_REQUIS_EVOLUTION_MIRAJANE_JELLAL) {
            return "Cette evolution se debloque au niveau " + NIVEAU_REQUIS_EVOLUTION_MIRAJANE_JELLAL + " !";
        }
        int possede = ctx.inventaire.getQuantiteMateriau(PARCHEMIN_SS);
        if (possede < COUT_JELLAL_SS) {
            return "Parchemins insuffisants : " + possede + "/" + COUT_JELLAL_SS + " " + PARCHEMIN_SS;
        }

        PersonnageBase jellalS = null;
        for (PersonnageBase p : ctx.personnagesRecruites) {
            if (p.getNom().equals("Jellal")) { jellalS = p; break; }
        }
        if (jellalS == null) return "Erreur : Jellal introuvable dans les recrues.";

        ctx.formation.retirerPersonnage(jellalS);
        ctx.personnagesRecruites.remove(jellalS);
        ctx.inventaire.retirerMateriau(PARCHEMIN_SS, COUT_JELLAL_SS);

        jellal_Arc_intermagie jellalSS = new jellal_Arc_intermagie();
        while (jellalSS.getNiveau() < jellalS.getNiveau()) jellalSS.monterDeNiveau();
        ctx.personnagesRecruites.add(jellalSS);
        ctx.sauvegarde.sauvegarder(ctx);

        return "Jellal a evolue en Jellal Intermagie [SS] !\nJellal Intermagie est desormais disponible dans votre formation.";
    }

    /** Tente de recruter Erza [S]. */
    public String recruterErza(GameContext ctx) {
        if (ctx.joueur.getNiveau() < NIVEAU_REQUIS_ERZA) {
            return "Erza se debloque au niveau " + NIVEAU_REQUIS_ERZA + " !";
        }
        int possede = ctx.inventaire.getQuantiteMateriau(PARCHEMIN_S);
        if (possede < COUT_ERZA) {
            return "Parchemins insuffisants : " + possede + "/" + COUT_ERZA + " " + PARCHEMIN_S;
        }
        ctx.inventaire.retirerMateriau(PARCHEMIN_S, COUT_ERZA);
        ctx.personnagesRecruites.add(new perso_Erza());
        ctx.sauvegarde.sauvegarder(ctx);
        return "Erza a rejoint vos allies !";
    }

    /** Tente de recruter Luxus [SS] (recrutement direct, pas d'evolution). */
    public String recruterLuxus(GameContext ctx) {
        if (ctx.joueur.getNiveau() < NIVEAU_REQUIS_LUXUS) {
            return "Luxus se debloque au niveau " + NIVEAU_REQUIS_LUXUS + " !";
        }
        int possede = ctx.inventaire.getQuantiteMateriau(PARCHEMIN_SS);
        if (possede < COUT_LUXUS) {
            return "Parchemins insuffisants : " + possede + "/" + COUT_LUXUS + " " + PARCHEMIN_SS;
        }
        ctx.inventaire.retirerMateriau(PARCHEMIN_SS, COUT_LUXUS);
        ctx.personnagesRecruites.add(new perso_Luxus());
        ctx.sauvegarde.sauvegarder(ctx);
        return "Luxus a rejoint vos allies !";
    }

    /** Tente de recruter Brain [S]. */
    public String recruterBrain(GameContext ctx) {
        if (ctx.joueur.getNiveau() < NIVEAU_REQUIS_BRAIN) {
            return "Brain se debloque au niveau " + NIVEAU_REQUIS_BRAIN + " !";
        }
        int possede = ctx.inventaire.getQuantiteMateriau(PARCHEMIN_S);
        if (possede < COUT_BRAIN) {
            return "Parchemins insuffisants : " + possede + "/" + COUT_BRAIN + " " + PARCHEMIN_S;
        }
        ctx.inventaire.retirerMateriau(PARCHEMIN_S, COUT_BRAIN);
        ctx.personnagesRecruites.add(new perso_Brain());
        ctx.sauvegarde.sauvegarder(ctx);
        return "Brain a rejoint vos allies !";
    }

    /** Tente de recruter Hades [SSS] (recrutement direct, pas d'evolution). */
    public String recruterHades(GameContext ctx) {
        if (ctx.joueur.getNiveau() < NIVEAU_REQUIS_HADES) {
            return "Hades se debloque au niveau " + NIVEAU_REQUIS_HADES + " !";
        }
        int possede = ctx.inventaire.getQuantiteMateriau(PARCHEMIN_SS);
        if (possede < COUT_HADES) {
            return "Parchemins insuffisants : " + possede + "/" + COUT_HADES + " " + PARCHEMIN_SS;
        }
        ctx.inventaire.retirerMateriau(PARCHEMIN_SS, COUT_HADES);
        ctx.personnagesRecruites.add(new perso_Hades());
        ctx.sauvegarde.sauvegarder(ctx);
        return "Hades a rejoint vos allies !";
    }

    // ── Utilitaire ────────────────────────────────────────────────────────
    public static boolean dejaRecruteParNom(String nom,
                                       ArrayList<PersonnageBase> liste) {
        for (PersonnageBase p : liste)
            if (p.getNom().equalsIgnoreCase(nom)) return true;
        return false;
    }
}
