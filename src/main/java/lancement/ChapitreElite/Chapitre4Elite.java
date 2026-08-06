package lancement.ChapitreElite;

import Equipement.Equipement;
import Equipement.EquipementFactory;
import Equipement.FragmentEquipement;
import Equipement.GestionnaireFragments;
import Personnage.PersonnageBase;
import Personnage.pnj.EnnemisGeneriques.*;
import Personnage.pnj.Chapitre4.*;
import lancement.GameContext;
import lancement.Stage;
import lancement.Chapitres.Chapitre4;
import lancement.Chapitres.CourbeChapitres;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Chapitre 4 Elite — memes affrontements que le Chapitre 4 normal, mais sans aucun invite
 * temporaire (Natsu, Erza, Lucy, Jubia, Simon, Shaw, Natsu Etherion n'y participent pas) : c'est
 * toujours la formation reelle du joueur qui affronte les ennemis, a des niveaux nettement plus
 * eleves. Les deux combats scriptes du Chapitre 4 normal deviennent de vrais combats d'equipe :
 * stage 1 (l'embuscade du casino, jouee par une equipe de heros fixe) et stage 8 (Erza seule
 * contre Jellal, desormais escortee par le reste de l'equipe et par une garde renforcee).
 */
public class Chapitre4Elite implements ChapitreElite {

    private static final int    NB_STAGES            = 10;
    private static final double CHANCE_FRAGMENT      = 0.40;
    private static final double CHANCE_FRAGMENT_BOSS = 0.70;

    private static final GestionnaireFragments gestionnaireFragments = new GestionnaireFragments();

    private final boolean[] stagesDebloques = new boolean[NB_STAGES + 1];
    private final boolean[] stagesReussis   = new boolean[NB_STAGES + 1];

    private final Chapitre4      chapitre4;
    private final Chapitre3Elite chapitre3Elite;

    public Chapitre4Elite(Chapitre4 chapitre4, Chapitre3Elite chapitre3Elite) {
        this.chapitre4      = chapitre4;
        this.chapitre3Elite = chapitre3Elite;
        stagesDebloques[1]    = true;
    }

    // -- Condition de deblocage --------------------------------------------
    public boolean estDebloque() {
        return chapitre4.getStagesReussis()[10]
            && chapitre3Elite.getStagesReussis()[10];
    }

    public void afficher(GameContext ctx, Scanner scanner) {
        if (!estDebloque()) {
            System.out.println("Terminez le Chapitre 4 et le Chapitre 3 Elite pour debloquer le Chapitre 4 Elite !");
            return;
        }

        boolean retour = false;

        while (!retour) {
            ctx.gestionnaireEnergie.mettreAJourRecharge();

            System.out.println("\n========================================");
            System.out.println("   CHAPITRE 4 ELITE -- " + getNomChapitre());
            System.out.println("========================================");
            System.out.println("Or : " + String.format("%.0f", ctx.joueur.getOr())
                    + "  |  " + ctx.gestionnaireEnergie.afficherEnergie());
            System.out.println();

            for (int i = 1; i <= NB_STAGES; i++) {
                boolean jouable = ctx.gestionnaireQuetes.estStageJouable(4, i, true);
                String etat     = !stagesDebloques[i] ? "[###] "
                        : stagesReussis[i]             ? "[OK]  "
                        : jouable                       ? "[  ]  "
                        :                                 "[QUETE] ";
                int    restants = ctx.gestionnaireEnergie.getRunsEliteRestants(i);
                String etoiles  = ctx.gestionnaireEtoiles.getEtoiles(4, i, true).afficher();
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
            } else if (!ctx.gestionnaireQuetes.estStageJouable(4, choix, true)) {
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
                System.out.println(">> Felicitations ! Vous avez termine le Chapitre 4 Elite !");
                ctx.gestionnaireTitres.debloquerTitre("Vainqueur de la Tour du Paradis Renforcée");
            }

            double chanceFragment = (numero == NB_STAGES) ? CHANCE_FRAGMENT_BOSS : CHANCE_FRAGMENT;
            if (Math.random() < chanceFragment) {
                // Fragments rang B (Chapitres 3-5 Elite).
                List<FragmentEquipement> catalogue = gestionnaireFragments.getCatalogue().stream()
                        .filter(f -> f.getRarete() == Equipement.Rarete.B)
                        .toList();
                FragmentEquipement fragment = catalogue.get((int) (Math.random() * catalogue.size()));
                ctx.inventaire.ajouterMateriau(fragment.getNomFragment(), 1);
                int total = ctx.inventaire.getQuantiteMateriau(fragment.getNomFragment());
                System.out.printf("   ✦ Fragment obtenu : %s (%d/%d)%n",
                        fragment.getNomFragment(), total, fragment.getQuantiteRequise());
            }

            ctx.gestionnaireQuetes.notifierOrGagne(stage.getRecompenseOr());
            ctx.gestionnaireQuetes.notifierStageFini(4, numero, true,
                    ctx.joueur, ctx.menuRecrutement, ctx.personnagesRecruites);
            ctx.gestionnaireEtoiles.mettreAJour(4, numero, true,
                    resultatStage.victoire, resultatStage.sansAllieMort, resultatStage.enMoinsDe10Tours);
        }
        return resultatStage;
    }

    /** Niveau scenarise (boss par boss, comme Chapitre3Elite/5Elite) : bande calibree pour
     *  combler l'ecart entre le Chapitre 3 Elite et le plancher du Chapitre 5 Elite (40) —
     *  le stage 10 termine 2 crans en dessous, comme pour les Elites suivants. */
    private static int niveauPourStage(int numero) {
        return switch (numero) {
            case 1  -> 32;
            case 2  -> 33;
            case 3  -> 34;
            case 4  -> 35;
            case 5  -> 36;
            case 6  -> 37;
            case 7  -> 38;
            case 8  -> 39;
            case 9  -> 40;
            case 10 -> 42;
            default -> CourbeChapitres.niveauEnnemiPourStage(4, numero);
        };
    }

    /** Bornes de niveau du chapitre, pour la fortification aleatoire de l'equipement Elite. */
    private static final int NIVEAU_MIN_CHAPITRE = 32;
    private static final int NIVEAU_MAX_CHAPITRE = 42;

    /** Equipement fantome d'Elite : set complet rang B, fortification dans les bornes de niveau
     *  du chapitre, affinage 10, uniquement des pierres niveau 1 sur les 5 emplacements. */
    private static void equiperEnnemisElite(ArrayList<PersonnageBase> ennemis) {
        for (PersonnageBase ennemi : ennemis) {
            EquipementFactory.equiperGearElite(ennemi, Equipement.Rarete.B, 6,
                    NIVEAU_MIN_CHAPITRE, NIVEAU_MAX_CHAPITRE, 10, 10, 5, 5, 1, 1);
        }
    }

    private Stage construireStage(int numero) {
        ArrayList<PersonnageBase> e = new ArrayList<>();
        int niv = niveauPourStage(numero);

        Stage stage = switch (numero) {
            // Stage 1 — remplace l'embuscade scriptee (equipe de heros fixe) : la formation reelle
            // du joueur affronte directement Wolly, Miliana, Shaw et Simon + une garde generique.
            case 1 -> {
                e.add(new EnnemiWolly(niv));
                e.add(new EnnemiMiliana(niv));
                e.add(new EnnemiShaw(niv));
                e.add(new EnnemiSimon(niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_4, niv));
                yield new Stage(1, "[ELITE] Embuscade dans le casino", 2900, 0, e);
            }
            // Stage 2 — memes ennemis que le Chapitre 4 normal (gardes generiques).
            case 2 -> {
                e.add(new EnnemiMage7DPS(Variante.CHAPITRE_4, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_4, niv));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_4, niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_4, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_4, niv));
                yield new Stage(2, "[ELITE] Infiltration dans la tour du paradis", 3300, 0, e);
            }
            // Stage 3 — Miliana + Wolly + generiques, sans Natsu.
            case 3 -> {
                e.add(new EnnemiMiliana(niv));
                e.add(new EnnemiWolly(niv));
                e.add(new EnnemiMage6Debuff(Variante.CHAPITRE_4, niv));
                e.add(new EnnemiMage5Tank(Variante.CHAPITRE_4, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_4, niv));
                yield new Stage(3, "[ELITE] Miaou, Il faut sauver Happy", 3700, 0, e);
            }
            // Stage 4 — Shaw + generiques, sans Erza.
            case 4 -> {
                e.add(new EnnemiShaw(niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_4, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_4, niv));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_4, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_4, niv));
                yield new Stage(4, "[ELITE] Libération d'erza", 4200, 0, e);
            }
            // Stage 5 — Vivaldus + generiques, sans Lucy ni Jubia.
            case 5 -> {
                e.add(new EnnemiVivaldus(niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_4, niv));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_4, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_4, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_4, niv));
                yield new Stage(5, "[ELITE] Les esprits et l'eau", 4800, 0, e);
            }
            // Stage 6 — Owl + generiques, sans Natsu ni Simon.
            case 6 -> {
                e.add(new EnnemiOwl(niv));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_4, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_4, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_4, niv));
                e.add(new EnnemiMage6Debuff(Variante.CHAPITRE_4, niv));
                yield new Stage(6, "[ELITE] Le hiboux assasin", 5500, 0, e);
            }
            // Stage 7 — Ikaruga + generiques, sans Erza ni Shaw.
            case 7 -> {
                e.add(new EnnemiIkaruga(niv));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_4, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_4, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_4, niv));
                e.add(new EnnemiMage8DPS(Variante.CHAPITRE_4, niv));
                yield new Stage(7, "[ELITE] Epée contre Epée", 6300, 0, e);
            }
            // Stage 8 — remplace le duel scripte (Erza seule vs Jellal) : toute l'equipe alliee
            // affronte Jellal, desormais escorte d'une garde renforcee.
            case 8 -> {
                e.add(new EnnemiJellal(niv));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_4, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_4, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_4, niv));
                yield new Stage(8, "[ELITE] Erza contre Jellal — Le Passe Ressurgit", 8100, 0, e);
            }
            // Stage 9 — memes ennemis que le Chapitre 4 normal, sans Simon.
            case 9 -> {
                e.add(new EnnemiJellal(niv));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_4, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_4, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_4, niv));
                yield new Stage(9, "[ELITE] Le sacrifice de Simon — L'Assaut sur Jellal", 8600, 0, e);
            }
            // Stage 10 — memes ennemis que le Chapitre 4 normal, sans Natsu Etherion : le combat
            // le plus dur du chapitre.
            case 10 -> {
                e.add(new EnnemiJellal(niv));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_4, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_4, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_4, niv));
                e.add(new EnnemiMage8DPS(Variante.CHAPITRE_4, niv));
                yield new Stage(10, "[ELITE] Jellal — L'Effondrement de la Tour du Paradis", 11000, 0, e);
            }
            default -> new Stage(numero, "???", 0, 0, e);
        };

        equiperEnnemisElite(e);
        return stage;
    }

    public String getTitreStage(int numero) {
        return switch (numero) {
            case 1  -> "[ELITE] Embuscade dans le casino";
            case 2  -> "[ELITE] Infiltration dans la tour du paradis";
            case 3  -> "[ELITE] Miaou, Il faut sauver Happy";
            case 4  -> "[ELITE] Libération d'erza";
            case 5  -> "[ELITE] Les esprits et l'eau";
            case 6  -> "[ELITE] Le hiboux assasin";
            case 7  -> "[ELITE] Epée contre Epée";
            case 8  -> "[ELITE] Erza contre Jellal — Le Passe Ressurgit";
            case 9  -> "[ELITE] Le sacrifice de Simon — L'Assaut sur Jellal";
            case 10 -> "[ELITE] Jellal — L'Effondrement de la Tour du Paradis";
            default -> "???";
        };
    }

    public String getNomChapitre() { return "La Tour du Paradis"; }

    public boolean[] getStagesDebloques() { return stagesDebloques; }
    public boolean[] getStagesReussis()   { return stagesReussis; }
    public void setStagesDebloques(boolean[] d) { for (int i = 0; i <= NB_STAGES; i++) stagesDebloques[i] = d[i]; }
    public void setStagesReussis(boolean[] r)   { for (int i = 0; i <= NB_STAGES; i++) stagesReussis[i]   = r[i]; }
}
