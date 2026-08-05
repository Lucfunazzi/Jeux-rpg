package lancement.ChapitreElite;

import Equipement.Equipement;
import Equipement.EquipementFactory;
import Personnage.PersonnageBase;
import Personnage.pnj.EnnemisGeneriques.*;
import Personnage.pnj.Chapitre5.*;
import lancement.GameContext;
import lancement.Stage;
import lancement.Chapitres.Chapitre5;
import lancement.Chapitres.CourbeChapitres;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Chapitre 5 Elite — memes affrontements que le Chapitre 5 normal, mais sans aucun invite
 * temporaire : c'est toujours la formation reelle du joueur qui affronte les ennemis, a des
 * niveaux nettement plus eleves. Stages 7 et 9 remplacent les duels scriptes (Mirajane vs Freed,
 * Mistgun vs Luxus) par de vrais combats d'equipe ; stage 10 reunit l'Equipe du Tonnerre au
 * complet et cloture le chapitre.
 */
public class Chapitre5Elite implements ChapitreElite {

    private static final int NB_STAGES = 10;
    private final boolean[] stagesDebloques = new boolean[NB_STAGES + 1];
    private final boolean[] stagesReussis   = new boolean[NB_STAGES + 1];

    private final Chapitre5      chapitre5;
    private final Chapitre4Elite chapitre4Elite;

    public Chapitre5Elite(Chapitre5 chapitre5, Chapitre4Elite chapitre4Elite) {
        this.chapitre5      = chapitre5;
        this.chapitre4Elite = chapitre4Elite;
        stagesDebloques[1]    = true;
    }

    // -- Condition de deblocage --------------------------------------------
    public boolean estDebloque() {
        return chapitre5.getStagesReussis()[10]
            && chapitre4Elite.getStagesReussis()[10];
    }

    public void afficher(GameContext ctx, Scanner scanner) {
        if (!estDebloque()) {
            System.out.println("Terminez le Chapitre 5 et le Chapitre 4 Elite pour debloquer le Chapitre 5 Elite !");
            return;
        }

        boolean retour = false;

        while (!retour) {
            ctx.gestionnaireEnergie.mettreAJourRecharge();

            System.out.println("\n========================================");
            System.out.println("   CHAPITRE 5 ELITE -- " + getNomChapitre());
            System.out.println("========================================");
            System.out.println("Or : " + String.format("%.0f", ctx.joueur.getOr())
                    + "  |  " + ctx.gestionnaireEnergie.afficherEnergie());
            System.out.println();

            for (int i = 1; i <= NB_STAGES; i++) {
                boolean jouable = ctx.gestionnaireQuetes.estStageJouable(5, i, true);
                String etat     = !stagesDebloques[i] ? "[###] "
                        : stagesReussis[i]             ? "[OK]  "
                        : jouable                       ? "[  ]  "
                        :                                 "[QUETE] ";
                int    restants = ctx.gestionnaireEnergie.getRunsEliteRestants(i);
                String etoiles  = ctx.gestionnaireEtoiles.getEtoiles(5, i, true).afficher();
                System.out.println(etat + "Stage " + i + " -- " + getTitreStage(i)
                        + "  " + etoiles + "  (" + restants + "/10 runs restants)");
            }

            System.out.println("\nEntrez le numero du stage (0 pour revenir) :");
            int choix;
            try {
                choix = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Entree invalide.");
                continue;
            }

            if (choix == 0) {
                retour = true;
            } else if (choix < 1 || choix > NB_STAGES) {
                System.out.println("Stage invalide.");
            } else if (!stagesDebloques[choix]) {
                System.out.println("Ce stage est verrouille. Terminez d'abord le stage precedent.");
            } else if (!ctx.gestionnaireQuetes.estStageJouable(5, choix, true)) {
                System.out.println("Acceptez d'abord la quete associee a ce stage (menu Quetes).");
            } else {
                lancerStage(ctx, choix);
            }
        }
    }

    /**
     * Verifie les runs/energie, lance le stage donne (toujours avec la formation reelle du
     * joueur, aucun invite) et applique les recompenses en cas de victoire. Suppose que le stage
     * est deja debloque. Retourne null si le stage n'a pas pu etre lance (runs epuises ou energie
     * insuffisante -- message imprime dans ce cas). Reutilisable par la console et l'interface graphique.
     */
    public Stage.ResultatStage lancerStage(GameContext ctx, int numero) {
        if (!ctx.gestionnaireEnergie.peutFaireRunElite(numero)) {
            System.out.println("Limite de runs atteinte pour ce stage aujourd'hui (10/10).");
            return null;
        }
        if (!ctx.gestionnaireEnergie.consommerEnergie(5)) {
            System.out.println("Pas assez d'energie ! (il faut 5, vous avez "
                    + ctx.gestionnaireEnergie.getEnergie() + ")");
            return null;
        }

        ctx.gestionnaireEnergie.enregistrerRunElite(numero);
        Stage stage        = construireStage(numero);
        boolean estNouveau = !stagesReussis[numero];
        Stage.ResultatStage resultatStage = stage.lancer(ctx, ctx.formation.getEquipe(), estNouveau);

        if (resultatStage.victoire) {
            stagesReussis[numero] = true;
            if (numero < NB_STAGES) {
                stagesDebloques[numero + 1] = true;
                System.out.println(">> Stage " + (numero + 1) + " debloque !");
            } else {
                System.out.println(">> Felicitations ! Vous avez termine le Chapitre 5 Elite !");
                ctx.gestionnaireTitres.debloquerTitre("Fleau de l'Equipe du Tonnerre");
            }

            ctx.gestionnaireQuetes.notifierOrGagne(stage.getRecompenseOr());
            ctx.gestionnaireQuetes.notifierStageFini(5, numero, true,
                    ctx.joueur, ctx.menuRecrutement, ctx.personnagesRecruites);
            ctx.gestionnaireEtoiles.mettreAJour(5, numero, true,
                    resultatStage.victoire, resultatStage.sansAllieMort, resultatStage.enMoinsDe10Tours);
        }
        return resultatStage;
    }

    /** Niveau scenarise (boss par boss, comme Chapitre3Elite) : un cran au-dessus du palier
     *  normal du Chapitre 5 (30-35), calibre sur le niveau reel du joueur au moment ou ce
     *  chapitre se debloque (juste apres avoir fini le Chapitre 5, donc ~niveau 35-45) plutot
     *  que sur une continuite artificielle avec le Chapitre 3 Elite. Stage 10 = le plus dur. */
    private static int niveauPourStage(int numero) {
        return switch (numero) {
            case 1  -> 40;
            case 2  -> 41;
            case 3  -> 42;
            case 4  -> 43;
            case 5  -> 44;
            case 6  -> 45;
            case 7  -> 46;
            case 8  -> 47;
            case 9  -> 48;
            case 10 -> 50;
            default -> CourbeChapitres.niveauEnnemiPourStage(5, numero);
        };
    }

    /** Bornes de niveau du chapitre, pour la fortification aleatoire de l'equipement Elite. */
    private static final int NIVEAU_MIN_CHAPITRE = 40;
    private static final int NIVEAU_MAX_CHAPITRE = 50;

    /** Equipement fantome d'Elite : set complet rang B, fortification dans les bornes de niveau
     *  du chapitre, affinage 10, pierres niveau 1 et 2 (moitie-moitie) sur les 5 emplacements. */
    private static void equiperEnnemisElite(ArrayList<PersonnageBase> ennemis) {
        for (PersonnageBase ennemi : ennemis) {
            EquipementFactory.equiperGearElite(ennemi, Equipement.Rarete.B, 6,
                    NIVEAU_MIN_CHAPITRE, NIVEAU_MAX_CHAPITRE, 10, 10, 5, 5, 1, 2);
        }
    }

    private Stage construireStage(int numero) {
        ArrayList<PersonnageBase> e = new ArrayList<>();
        int niv = niveauPourStage(numero);

        Stage stage = switch (numero) {
            // Stage 1 — memes ennemis que le Chapitre 5 normal.
            case 1 -> {
                e.add(new EnnemiMage7DPS(Variante.CHAPITRE_5, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_5, niv));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_5, niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_5, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_5, niv));
                yield new Stage(1, "[ELITE] Le début de la bataille de Fairy Tail", 12000, 0, e);
            }
            // Stage 2 — memes ennemis que le Chapitre 5 normal.
            case 2 -> {
                e.add(new EnnemiMage5Tank(Variante.CHAPITRE_5, niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_5, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_5, niv));
                e.add(new EnnemiMage8DPS(Variante.CHAPITRE_5, niv));
                e.add(new EnnemiMage6Debuff(Variante.CHAPITRE_5, niv));
                yield new Stage(2, "[ELITE] En route pour sauver les femmes de Fairy Tail", 13000, 0, e);
            }
            // Stage 3 — memes ennemis que le Chapitre 5 normal (Evergreen + generiques).
            case 3 -> {
                e.add(new EnnemiEvergreen(niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_5, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_5, niv));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_5, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_5, niv));
                yield new Stage(3, "[ELITE] Elfman vs Evergreen", 14000, 0, e);
            }
            // Stage 4 — memes ennemis que le Chapitre 5 normal (Bixrow + generiques).
            case 4 -> {
                e.add(new EnnemiBixrow(niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_5, niv));
                e.add(new EnnemiMage5Tank(Variante.CHAPITRE_5, niv));
                e.add(new EnnemiMage6Debuff(Variante.CHAPITRE_5, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_5, niv));
                yield new Stage(4, "[ELITE] Gray vs Bixrow", 15000, 0, e);
            }
            // Stage 5 — memes ennemis que le Chapitre 5 normal (Bixrow + generiques).
            case 5 -> {
                e.add(new EnnemiBixrow(niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_5, niv));
                e.add(new EnnemiMage4Buff(Variante.CHAPITRE_5, niv));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_5, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_5, niv));
                yield new Stage(5, "[ELITE] Lucy vs Bixrow", 16000, 0, e);
            }
            // Stage 6 — memes ennemis que le Chapitre 5 normal (Evergreen + generiques).
            case 6 -> {
                e.add(new EnnemiEvergreen(niv));
                e.add(new EnnemiMage7DPS(Variante.CHAPITRE_5, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_5, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_5, niv));
                e.add(new EnnemiMage5Tank(Variante.CHAPITRE_5, niv));
                yield new Stage(6, "[ELITE] Erza contre Evergreen", 17000, 0, e);
            }
            // Stage 7 — remplace le duel scripte Mirajane vs Freed : toute l'equipe alliee contre
            // l'Equipe du Tonnerre au grand complet (Freed, Evergreen, Bixrow) + escorte.
            case 7 -> {
                e.add(new EnnemiFreed(niv));
                e.add(new EnnemiEvergreen(niv));
                e.add(new EnnemiBixrow(niv));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_5, niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_5, niv));
                yield new Stage(7, "L'unité radjin", 18500, 0, e);
            }
            // Stage 8 — memes ennemis que le Chapitre 5 normal (Luxus + generiques).
            case 8 -> {
                e.add(new EnnemiLuxus(niv));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_5, niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_5, niv));
                e.add(new EnnemiMage4Buff(Variante.CHAPITRE_5, niv));
                e.add(new EnnemiMage6Debuff(Variante.CHAPITRE_5, niv));
                yield new Stage(8, "[ELITE] Erza vs Luxus", 20000, 0, e);
            }
            // Stage 9 — remplace le duel scripte Mistgun vs Luxus : toute l'equipe alliee contre
            // Luxus et une escorte plus consequente (1 tank, 2 DPS, 1 support).
            case 9 -> {
                e.add(new EnnemiLuxus(niv));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_5, niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_5, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_5, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_5, niv));
                yield new Stage(9, "[ELITE] Mistgun vs Luxus", 22000, 0, e);
            }
            // Stage 10 — remplace le duel scripte Natsu et Gajeel vs Luxus : l'Equipe du Tonnerre
            // au grand complet (Freed, Luxus, Evergreen, Bixrow) + escorte, combat le plus dur du chapitre.
            case 10 -> {
                e.add(new EnnemiFreed(niv));
                e.add(new EnnemiLuxus(niv));
                e.add(new EnnemiEvergreen(niv));
                e.add(new EnnemiBixrow(niv));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_5, niv));
                yield new Stage(10, "L'unité radjin au complet", 26000, 0, e);
            }
            default -> new Stage(numero, "???", 0, 0, e);
        };

        equiperEnnemisElite(e);
        return stage;
    }

    public String getTitreStage(int numero) {
        return switch (numero) {
            case 1  -> "[ELITE] Le début de la bataille de Fairy Tail";
            case 2  -> "[ELITE] En route pour sauver les femmes de Fairy Tail";
            case 3  -> "[ELITE] Elfman vs Evergreen";
            case 4  -> "[ELITE] Gray vs Bixrow";
            case 5  -> "[ELITE] Lucy vs Bixrow";
            case 6  -> "[ELITE] Erza contre Evergreen";
            case 7  -> "L'unité radjin";
            case 8  -> "[ELITE] Erza vs Luxus";
            case 9  -> "[ELITE] Mistgun vs Luxus";
            case 10 -> "L'unité radjin au complet";
            default -> "???";
        };
    }

    public String getNomChapitre() { return "La bataille de Fairy Tail"; }

    public boolean[] getStagesDebloques() { return stagesDebloques; }
    public boolean[] getStagesReussis()   { return stagesReussis; }
    public void setStagesDebloques(boolean[] d) { for (int i = 0; i <= NB_STAGES; i++) stagesDebloques[i] = d[i]; }
    public void setStagesReussis(boolean[] r)   { for (int i = 0; i <= NB_STAGES; i++) stagesReussis[i]   = r[i]; }
}
