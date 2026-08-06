package lancement.ChapitreElite;

import Equipement.Equipement;
import Equipement.EquipementFactory;
import Personnage.PersonnageBase;
import Personnage.pnj.EnnemisGeneriques.*;
import Personnage.pnj.Chapitre7.*;
import lancement.GameContext;
import lancement.Stage;
import lancement.Chapitres.Chapitre7;
import lancement.Chapitres.CourbeChapitres;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Chapitre 7 Elite — memes affrontements que le Chapitre 7 normal, mais sans aucun invite
 * temporaire (Gray, Lucy, Natsu, Gajeel, Wendy n'y participent pas) : c'est toujours la
 * formation reelle du joueur qui affronte les ennemis, a des niveaux nettement plus eleves.
 */
public class Chapitre7Elite implements ChapitreElite {

    private static final int NB_STAGES = 10;
    private final boolean[] stagesDebloques = new boolean[NB_STAGES + 1];
    private final boolean[] stagesReussis   = new boolean[NB_STAGES + 1];

    private final Chapitre7      chapitre7;
    private final Chapitre6Elite chapitre6Elite;

    public Chapitre7Elite(Chapitre7 chapitre7, Chapitre6Elite chapitre6Elite) {
        this.chapitre7      = chapitre7;
        this.chapitre6Elite = chapitre6Elite;
        stagesDebloques[1]    = true;
    }

    // -- Condition de deblocage --------------------------------------------
    public boolean estDebloque() {
        return chapitre7.getStagesReussis()[10]
            && chapitre6Elite.getStagesReussis()[10];
    }

    public void afficher(GameContext ctx, Scanner scanner) {
        if (!estDebloque()) {
            System.out.println("Terminez le Chapitre 7 et le Chapitre 6 Elite pour debloquer le Chapitre 7 Elite !");
            return;
        }

        boolean retour = false;

        while (!retour) {
            ctx.gestionnaireEnergie.mettreAJourRecharge();

            System.out.println("\n========================================");
            System.out.println("   CHAPITRE 7 ELITE -- " + getNomChapitre());
            System.out.println("========================================");
            System.out.println("Or : " + String.format("%.0f", ctx.joueur.getOr())
                    + "  |  " + ctx.gestionnaireEnergie.afficherEnergie());
            System.out.println();

            for (int i = 1; i <= NB_STAGES; i++) {
                boolean jouable = ctx.gestionnaireQuetes.estStageJouable(7, i, true);
                String etat     = !stagesDebloques[i] ? "[###] "
                        : stagesReussis[i]             ? "[OK]  "
                        : jouable                       ? "[  ]  "
                        :                                 "[QUETE] ";
                int    restants = ctx.gestionnaireEnergie.getRunsEliteRestants(i);
                String etoiles  = ctx.gestionnaireEtoiles.getEtoiles(7, i, true).afficher();
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
            } else if (!ctx.gestionnaireQuetes.estStageJouable(7, choix, true)) {
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
                System.out.println(">> Felicitations ! Vous avez termine le Chapitre 7 Elite !");
                ctx.gestionnaireTitres.debloquerTitre("Vainqueur du Royaume d'Edolas");
            }

            ctx.gestionnaireQuetes.notifierOrGagne(stage.getRecompenseOr());
            ctx.gestionnaireQuetes.notifierStageFini(7, numero, true,
                    ctx.joueur, ctx.menuRecrutement, ctx.personnagesRecruites);
            ctx.gestionnaireEtoiles.mettreAJour(7, numero, true,
                    resultatStage.victoire, resultatStage.sansAllieMort, resultatStage.enMoinsDe10Tours);
        }
        return resultatStage;
    }

    /** Niveau scenarise (boss par boss, comme Chapitre6Elite) : un cran au-dessus du palier
     *  du Chapitre 6 Elite (48-58), calibre sur le niveau reel du joueur au moment ou ce
     *  chapitre se debloque (fin du Chapitre 7 normal ~45, fin du Chapitre 6 Elite ~58).
     *  Stage 10 = le plus dur du chapitre, Dorma Anim boosté sans escorte. */
    private static int niveauPourStage(int numero) {
        return switch (numero) {
            case 1  -> 56;
            case 2  -> 57;
            case 3  -> 58;
            case 4  -> 59;
            case 5  -> 60;
            case 6  -> 61;
            case 7  -> 62;
            case 8  -> 63;
            case 9  -> 64;
            case 10 -> 66;
            default -> CourbeChapitres.niveauEnnemiPourStage(7, numero);
        };
    }

    /** Bornes de niveau du chapitre, pour la fortification aleatoire de l'equipement Elite. */
    private static final int NIVEAU_MIN_CHAPITRE = 56;
    private static final int NIVEAU_MAX_CHAPITRE = 66;

    /** Equipement fantome d'Elite : set complet rang A, fortification dans les bornes de niveau
     *  du chapitre, affinage au maximum (25), pierres niveau 3 sur les 5 emplacements. */
    private static void equiperEnnemisElite(ArrayList<PersonnageBase> ennemis) {
        for (PersonnageBase ennemi : ennemis) {
            EquipementFactory.equiperGearElite(ennemi, Equipement.Rarete.A, 6,
                    NIVEAU_MIN_CHAPITRE, NIVEAU_MAX_CHAPITRE, 25, 25, 5, 5, 3, 3);
        }
    }

    private Stage construireStage(int numero) {
        ArrayList<PersonnageBase> e = new ArrayList<>();
        int niv = niveauPourStage(numero);

        Stage stage = switch (numero) {
            // Stage 1 — memes ennemis que le Chapitre 7 normal : 1 tank, 2 dps, 2 supports.
            case 1 -> {
                e.add(new EnnemiMage5Tank(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage6Debuff(Variante.CHAPITRE_7, niv));
                yield new Stage(1, "[ELITE] Prologue Edolas", 20000, 0, e);
            }
            // Stage 2 — memes ennemis que le Chapitre 7 normal : 1 tank, 3 dps, 1 support.
            case 2 -> {
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage7DPS(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_7, niv));
                yield new Stage(2, "[ELITE] Magie Limité", 21500, 0, e);
            }
            // Stage 3 — Erza Knightwalker + 3 dps + 1 support generiques.
            case 3 -> {
                e.add(new EnnemiErzaKnightwalker(niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage7DPS(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_7, niv));
                yield new Stage(3, "[ELITE] La chasseus de fée", 23000, 0, e);
            }
            // Stage 4 — Sugarboy + 2 dps + 2 supports generiques, sans Gray.
            case 4 -> {
                e.add(new EnnemiSugarboy(niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage6Debuff(Variante.CHAPITRE_7, niv));
                yield new Stage(4, "[ELITE] Gray contre sugarBoy", 24500, 0, e);
            }
            // Stage 5 — Hughes + 1 tank + 2 dps + 2 supports generiques, sans Natsu ni Lucy.
            case 5 -> {
                e.add(new EnnemiHughes(niv));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage6Debuff(Variante.CHAPITRE_7, niv));
                yield new Stage(5, "[ELITE] Natsu et Lucy vs huges", 26000, 0, e);
            }
            // Stage 6 — Byro + 1 tank + 3 dps generiques, sans Lucy.
            case 6 -> {
                e.add(new EnnemiByro(niv));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage7DPS(Variante.CHAPITRE_7, niv));
                yield new Stage(6, "[ELITE] Lucy contre bario", 27500, 0, e);
            }
            // Stage 7 — Panther Lily + 3 supports generiques, sans Gajeel.
            case 7 -> {
                e.add(new EnnemiPantherLily(niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage6Debuff(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_7, niv));
                yield new Stage(7, "[ELITE] Gajeel vs Panthère Lilly", 29000, 0, e);
            }
            // Stage 8 — memes ennemis que le Chapitre 7 normal : Dorma Anim + 1 dps + 3 supports.
            case 8 -> {
                e.add(new EnnemiDormaAnim(niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage6Debuff(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_7, niv));
                yield new Stage(8, "[ELITE] Le roi d'edolas rentre en jeu", 30500, 0, e);
            }
            // Stage 9 — Dorma Anim + 3 dps + 1 support generiques, sans Wendy/Natsu/Gajeel.
            case 9 -> {
                e.add(new EnnemiDormaAnim(niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage7DPS(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_7, niv));
                yield new Stage(9, "[ELITE] Natsu et Gajeel et wendy vs Le roi d'edolas", 32000, 0, e);
            }
            // Stage 10 — Dorma Anim boosté, seul contre toute l'equipe : le combat le plus dur du chapitre.
            case 10 -> {
                e.add(new EnnemiDormaAnim(niv));
                yield new Stage(10, "[ELITE] Le dernier combat", 40000, 0, e);
            }
            default -> new Stage(numero, "???", 0, 0, e);
        };

        equiperEnnemisElite(e);
        return stage;
    }

    public String getTitreStage(int numero) {
        return switch (numero) {
            case 1  -> "[ELITE] Prologue Edolas";
            case 2  -> "[ELITE] Magie Limité";
            case 3  -> "[ELITE] La chasseus de fée";
            case 4  -> "[ELITE] Gray contre sugarBoy";
            case 5  -> "[ELITE] Natsu et Lucy vs huges";
            case 6  -> "[ELITE] Lucy contre bario";
            case 7  -> "[ELITE] Gajeel vs Panthère Lilly";
            case 8  -> "[ELITE] Le roi d'edolas rentre en jeu";
            case 9  -> "[ELITE] Natsu et Gajeel et wendy vs Le roi d'edolas";
            case 10 -> "[ELITE] Le dernier combat";
            default -> "???";
        };
    }

    public String getNomChapitre() { return "Arc Edolas"; }

    public boolean[] getStagesDebloques() { return stagesDebloques; }
    public boolean[] getStagesReussis()   { return stagesReussis; }
    public void setStagesDebloques(boolean[] d) { for (int i = 0; i <= NB_STAGES; i++) stagesDebloques[i] = d[i]; }
    public void setStagesReussis(boolean[] r)   { for (int i = 0; i <= NB_STAGES; i++) stagesReussis[i]   = r[i]; }
}
