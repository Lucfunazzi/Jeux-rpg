package lancement.ChapitreElite;

import Equipement.Equipement;
import Equipement.EquipementFactory;
import Personnage.PersonnageBase;
import Personnage.pnj.EnnemisGeneriques.*;
import Personnage.pnj.Chapitre5.*;
import Personnage.pnj.Chapitre8.*;
import lancement.GameContext;
import lancement.Stage;
import lancement.Chapitres.Chapitre8;
import lancement.Chapitres.CourbeChapitres;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Chapitre 8 Elite — memes affrontements que le Chapitre 8 normal, mais sans aucun invite
 * temporaire : c'est toujours la formation reelle du joueur qui affronte les ennemis, a des
 * niveaux nettement plus eleves. Les deux combats scriptes du Chapitre 8 normal (stage 4 :
 * Natsu seul vs Gildarts ; stage 9 : Makarov seul vs Hades) deviennent de vrais combats
 * d'equipe. Le stage 9 remplace Hades par un double affrontement contre Zancrow et Azuma,
 * deja croises aux stages 10 et 8 du Chapitre 8 normal.
 */
public class Chapitre8Elite implements ChapitreElite {

    private static final int NB_STAGES = 10;
    private final boolean[] stagesDebloques = new boolean[NB_STAGES + 1];
    private final boolean[] stagesReussis   = new boolean[NB_STAGES + 1];

    private final Chapitre8      chapitre8;
    private final Chapitre7Elite chapitre7Elite;

    public Chapitre8Elite(Chapitre8 chapitre8, Chapitre7Elite chapitre7Elite) {
        this.chapitre8      = chapitre8;
        this.chapitre7Elite = chapitre7Elite;
        stagesDebloques[1]    = true;
    }

    // -- Condition de deblocage --------------------------------------------
    public boolean estDebloque() {
        return chapitre8.getStagesReussis()[10]
            && chapitre7Elite.getStagesReussis()[10];
    }

    public void afficher(GameContext ctx, Scanner scanner) {
        if (!estDebloque()) {
            System.out.println("Terminez le Chapitre 8 et le Chapitre 7 Elite pour debloquer le Chapitre 8 Elite !");
            return;
        }

        boolean retour = false;

        while (!retour) {
            ctx.gestionnaireEnergie.mettreAJourRecharge();

            System.out.println("\n========================================");
            System.out.println("   CHAPITRE 8 ELITE -- " + getNomChapitre());
            System.out.println("========================================");
            System.out.println("Or : " + String.format("%.0f", ctx.joueur.getOr())
                    + "  |  " + ctx.gestionnaireEnergie.afficherEnergie());
            System.out.println();

            for (int i = 1; i <= NB_STAGES; i++) {
                boolean jouable = ctx.gestionnaireQuetes.estStageJouable(8, i, true);
                String etat     = !stagesDebloques[i] ? "[###] "
                        : stagesReussis[i]             ? "[OK]  "
                        : jouable                       ? "[  ]  "
                        :                                 "[QUETE] ";
                int    restants = ctx.gestionnaireEnergie.getRunsEliteRestants(i);
                String etoiles  = ctx.gestionnaireEtoiles.getEtoiles(8, i, true).afficher();
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
            } else if (!ctx.gestionnaireQuetes.estStageJouable(8, choix, true)) {
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
                System.out.println(">> Felicitations ! Vous avez termine le Chapitre 8 Elite !");
                ctx.gestionnaireTitres.debloquerTitre("Vainqueur de Grimoire Heart");
            }

            ctx.gestionnaireQuetes.notifierOrGagne(stage.getRecompenseOr());
            ctx.gestionnaireQuetes.notifierStageFini(8, numero, true,
                    ctx.joueur, ctx.menuRecrutement, ctx.personnagesRecruites);
            ctx.gestionnaireEtoiles.mettreAJour(8, numero, true,
                    resultatStage.victoire, resultatStage.sansAllieMort, resultatStage.enMoinsDe10Tours);
        }
        return resultatStage;
    }

    /** Niveau scenarise (boss par boss, comme Chapitre5/6/7Elite) : bande calibree dans la
     *  continuite du Chapitre 7 Elite (56-66) -- stage 10 termine 2 crans en dessous. */
    private static int niveauPourStage(int numero) {
        return switch (numero) {
            case 1  -> 64;
            case 2  -> 65;
            case 3  -> 66;
            case 4  -> 67;
            case 5  -> 68;
            case 6  -> 69;
            case 7  -> 70;
            case 8  -> 71;
            case 9  -> 72;
            case 10 -> 74;
            default -> CourbeChapitres.niveauEnnemiPourStage(8, numero);
        };
    }

    /** Bornes de niveau du chapitre, pour la fortification aleatoire de l'equipement Elite. */
    private static final int NIVEAU_MIN_CHAPITRE = 64;
    private static final int NIVEAU_MAX_CHAPITRE = 74;

    /** Equipement fantome d'Elite : set complet rang A, fortification dans les bornes de niveau
     *  du chapitre, affinage 30, pierres niveau 4 sur les 5 emplacements. */
    private static void equiperEnnemisElite(ArrayList<PersonnageBase> ennemis) {
        for (PersonnageBase ennemi : ennemis) {
            EquipementFactory.equiperGearElite(ennemi, Equipement.Rarete.A, 6,
                    NIVEAU_MIN_CHAPITRE, NIVEAU_MAX_CHAPITRE, 30, 30, 5, 5, 4, 4);
        }
    }

    private Stage construireStage(int numero) {
        ArrayList<PersonnageBase> e = new ArrayList<>();
        int niv = niveauPourStage(numero);

        Stage stage = switch (numero) {
            // Stage 1 — memes ennemis que le Chapitre 8 normal : 1 tank, 3 dps, 2 supports.
            case 1 -> {
                e.add(new EnnemiMage5Tank(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage7DPS(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage6Debuff(Variante.CHAPITRE_8, niv));
                yield new Stage(1, "[ELITE] Prologue examen de rang S", 24000, 0, e);
            }
            // Stage 2 — Bixrow et Freed + 1 tank + 1 dps + 2 supports generiques, sans Lucy ni Cana.
            case 2 -> {
                e.add(new EnnemiBixrow(niv));
                e.add(new EnnemiFreed(niv));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage6Debuff(Variante.CHAPITRE_8, niv));
                yield new Stage(2, "[ELITE] Lucy et cana vs bixrow et freed", 25500, 0, e);
            }
            // Stage 3 — Mirajane + 1 tank + 2 dps + 1 support generiques, sans Elfman ni Evergreen.
            case 3 -> {
                e.add(new EnnemiMirajane(niv));
                e.add(new EnnemiMage5Tank(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage7DPS(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_8, niv));
                yield new Stage(3, "[ELITE] Elfman et evergreen vs Mirajane", 27000, 0, e);
            }
            // Stage 4 — remplace le duel scripte Natsu vs Gildarts : toute l'equipe alliee contre
            // Gildarts, escorte d'une garde renforcee.
            case 4 -> {
                e.add(new EnnemiGildarts(niv));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage8DPS(Variante.CHAPITRE_8, niv));
                yield new Stage(4, "[ELITE] Natsu vs Gildarts", 28500, 0, e);
            }
            // Stage 5 — Jubia et Lisanna + 1 tank + 2 dps generiques, sans Erza.
            case 5 -> {
                e.add(new EnnemiJubia(niv));
                e.add(new EnnemiLisanna(niv));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage8DPS(Variante.CHAPITRE_8, niv));
                yield new Stage(5, "[ELITE] Erza vs jubia et lisanna", 30000, 0, e);
            }
            // Stage 6 — Mest et Wendy (ennemis) + 1 tank + 2 dps generiques, sans Gray ni Loki.
            case 6 -> {
                e.add(new EnnemiMest(niv));
                e.add(new EnnemiWendy(niv));
                e.add(new EnnemiMage5Tank(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage7DPS(Variante.CHAPITRE_8, niv));
                yield new Stage(6, "[ELITE] Gray et Loki vs Mest et Wendy", 31500, 0, e);
            }
            // Stage 7 — Kawazu et Yamaru + 1 tank + 2 dps generiques, sans Gajeel ni Levy.
            case 7 -> {
                e.add(new EnnemiKawazu(niv));
                e.add(new EnnemiYamaru(niv));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage8DPS(Variante.CHAPITRE_8, niv));
                yield new Stage(7, "[ELITE] Gajeel et Levy vs kawazu et yamaru", 33000, 0, e);
            }
            // Stage 8 — Azuma + 1 tank + 1 dps + 2 supports generiques, sans Mest, Panther Lily ni Wendy.
            case 8 -> {
                e.add(new EnnemiAzuma(niv));
                e.add(new EnnemiMage5Tank(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage6Debuff(Variante.CHAPITRE_8, niv));
                yield new Stage(8, "[ELITE] Mest, panthere lilly et wendy vs Azuma", 34500, 0, e);
            }
            // Stage 9 — remplace le duel scripte Makarov vs Hades : toute l'equipe alliee contre
            // Zancrow ET Azuma simultanement, deja croises aux stages 10 et 8, + une garde generique.
            case 9 -> {
                e.add(new EnnemiZancrow(niv));
                e.add(new EnnemiAzuma(niv));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_8, niv));
                yield new Stage(9, "[ELITE] Zancrow et Azuma unissent leurs forces", 36500, 0, e);
            }
            // Stage 10 — memes ennemis que le Chapitre 8 normal, sans Natsu : le combat le plus
            // dur du chapitre.
            case 10 -> {
                e.add(new EnnemiZancrow(niv));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage7DPS(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_8, niv));
                yield new Stage(10, "[ELITE] Natsu vs Zancrow", 44000, 0, e);
            }
            default -> new Stage(numero, "???", 0, 0, e);
        };

        equiperEnnemisElite(e);
        return stage;
    }

    public String getTitreStage(int numero) {
        return switch (numero) {
            case 1  -> "[ELITE] Prologue examen de rang S";
            case 2  -> "[ELITE] Lucy et cana vs bixrow et freed";
            case 3  -> "[ELITE] Elfman et evergreen vs Mirajane";
            case 4  -> "[ELITE] Natsu vs Gildarts";
            case 5  -> "[ELITE] Erza vs jubia et lisanna";
            case 6  -> "[ELITE] Gray et Loki vs Mest et Wendy";
            case 7  -> "[ELITE] Gajeel et Levy vs kawazu et yamaru";
            case 8  -> "[ELITE] Mest, panthere lilly et wendy vs Azuma";
            case 9  -> "[ELITE] Zancrow et Azuma unissent leurs forces";
            case 10 -> "[ELITE] Natsu vs Zancrow";
            default -> "???";
        };
    }

    public String getNomChapitre() { return "Arc île de Tenro 1"; }

    public boolean[] getStagesDebloques() { return stagesDebloques; }
    public boolean[] getStagesReussis()   { return stagesReussis; }
    public void setStagesDebloques(boolean[] d) { for (int i = 0; i <= NB_STAGES; i++) stagesDebloques[i] = d[i]; }
    public void setStagesReussis(boolean[] r)   { for (int i = 0; i <= NB_STAGES; i++) stagesReussis[i]   = r[i]; }
}
