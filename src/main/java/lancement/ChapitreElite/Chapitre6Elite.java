package lancement.ChapitreElite;

import Equipement.Equipement;
import Equipement.EquipementFactory;
import Equipement.FragmentEquipement;
import Equipement.GestionnaireFragments;
import Personnage.PersonnageBase;
import Personnage.pnj.EnnemisGeneriques.*;
import Personnage.pnj.Chapitre6.*;
import Personnage.pnj.Chapitre2.EnnemiLeon;
import Personnage.pnj.Chapitre2.EnnemiCherry;
import lancement.GameContext;
import lancement.Stage;
import lancement.Chapitres.Chapitre6;
import lancement.Chapitres.CourbeChapitres;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Chapitre 6 Elite — memes affrontements que le Chapitre 6 normal, mais sans aucun invite
 * temporaire : c'est toujours la formation reelle du joueur qui affronte les ennemis, a des
 * niveaux nettement plus eleves. Stages 5 et 10 remplacent les duels scriptes (Jura vs Hoteye,
 * Natsu Etherion vs Zero) par de vrais combats d'equipe, le stage 10 etant le plus dur du chapitre.
 */
public class Chapitre6Elite implements ChapitreElite {

    private static final int    NB_STAGES            = 10;
    private static final double CHANCE_FRAGMENT      = 0.40;
    private static final double CHANCE_FRAGMENT_BOSS = 0.70;

    private static final GestionnaireFragments gestionnaireFragments = new GestionnaireFragments();

    private final boolean[] stagesDebloques = new boolean[NB_STAGES + 1];
    private final boolean[] stagesReussis   = new boolean[NB_STAGES + 1];

    private final Chapitre6      chapitre6;
    private final Chapitre5Elite chapitre5Elite;

    public Chapitre6Elite(Chapitre6 chapitre6, Chapitre5Elite chapitre5Elite) {
        this.chapitre6      = chapitre6;
        this.chapitre5Elite = chapitre5Elite;
        stagesDebloques[1]    = true;
    }

    // -- Condition de deblocage --------------------------------------------
    public boolean estDebloque() {
        return chapitre6.getStagesReussis()[10]
            && chapitre5Elite.getStagesReussis()[10];
    }

    public void afficher(GameContext ctx, Scanner scanner) {
        if (!estDebloque()) {
            System.out.println("Terminez le Chapitre 6 et le Chapitre 5 Elite pour debloquer le Chapitre 6 Elite !");
            return;
        }

        boolean retour = false;

        while (!retour) {
            ctx.gestionnaireEnergie.mettreAJourRecharge();

            System.out.println("\n========================================");
            System.out.println("   CHAPITRE 6 ELITE -- " + getNomChapitre());
            System.out.println("========================================");
            System.out.println("Or : " + String.format("%.0f", ctx.joueur.getOr())
                    + "  |  " + ctx.gestionnaireEnergie.afficherEnergie());
            System.out.println();

            for (int i = 1; i <= NB_STAGES; i++) {
                boolean jouable = ctx.gestionnaireQuetes.estStageJouable(6, i, true);
                String etat     = !stagesDebloques[i] ? "[###] "
                        : stagesReussis[i]             ? "[OK]  "
                        : jouable                       ? "[  ]  "
                        :                                 "[QUETE] ";
                int    restants = ctx.gestionnaireEnergie.getRunsEliteRestants(i);
                String etoiles  = ctx.gestionnaireEtoiles.getEtoiles(6, i, true).afficher();
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
            } else if (!ctx.gestionnaireQuetes.estStageJouable(6, choix, true)) {
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
                System.out.println(">> Felicitations ! Vous avez termine le Chapitre 6 Elite !");
                ctx.gestionnaireTitres.debloquerTitre("Vainqueur d'Oracion Seis");
            }

            double chanceFragment = (numero == NB_STAGES) ? CHANCE_FRAGMENT_BOSS : CHANCE_FRAGMENT;
            if (Math.random() < chanceFragment) {
                // Fragments rang A (Chapitres 6-7 Elite).
                List<FragmentEquipement> catalogue = gestionnaireFragments.getCatalogue().stream()
                        .filter(f -> f.getRarete() == Equipement.Rarete.A)
                        .toList();
                FragmentEquipement fragment = catalogue.get((int) (Math.random() * catalogue.size()));
                ctx.inventaire.ajouterMateriau(fragment.getNomFragment(), 1);
                int total = ctx.inventaire.getQuantiteMateriau(fragment.getNomFragment());
                System.out.printf("   ✦ Fragment obtenu : %s (%d/%d)%n",
                        fragment.getNomFragment(), total, fragment.getQuantiteRequise());
            }

            ctx.gestionnaireQuetes.notifierOrGagne(stage.getRecompenseOr());
            ctx.gestionnaireQuetes.notifierStageFini(6, numero, true,
                    ctx.joueur, ctx.menuRecrutement, ctx.personnagesRecruites);
            ctx.gestionnaireEtoiles.mettreAJour(6, numero, true,
                    resultatStage.victoire, resultatStage.sansAllieMort, resultatStage.enMoinsDe10Tours);
        }
        return resultatStage;
    }

    /** Niveau scenarise (boss par boss, comme Chapitre3Elite) : un cran au-dessus du palier
     *  normal du Chapitre 6 (35-40) et du Chapitre 5 Elite (40-50), calibre sur le niveau reel
     *  du joueur au moment ou ce chapitre se debloque plutot que sur une continuite artificielle
     *  avec le Chapitre 3 Elite. Stage 10 = le plus dur du chapitre. */
    private static int niveauPourStage(int numero) {
        return switch (numero) {
            case 1  -> 48;
            case 2  -> 49;
            case 3  -> 50;
            case 4  -> 51;
            case 5  -> 52;
            case 6  -> 53;
            case 7  -> 54;
            case 8  -> 55;
            case 9  -> 56;
            case 10 -> 58;
            default -> CourbeChapitres.niveauEnnemiPourStage(6, numero);
        };
    }

    /** Bornes de niveau du chapitre, pour la fortification aleatoire de l'equipement Elite. */
    private static final int NIVEAU_MIN_CHAPITRE = 48;
    private static final int NIVEAU_MAX_CHAPITRE = 58;

    /** Equipement fantome d'Elite : set complet rang B, fortification dans les bornes de niveau
     *  du chapitre, affinage 15, uniquement des pierres niveau 2 sur les 5 emplacements. */
    private static void equiperEnnemisElite(ArrayList<PersonnageBase> ennemis) {
        for (PersonnageBase ennemi : ennemis) {
            EquipementFactory.equiperGearElite(ennemi, Equipement.Rarete.B, 6,
                    NIVEAU_MIN_CHAPITRE, NIVEAU_MAX_CHAPITRE, 15, 15, 5, 5, 2, 2);
        }
    }

    private Stage construireStage(int numero) {
        ArrayList<PersonnageBase> e = new ArrayList<>();
        int niv = niveauPourStage(numero);

        Stage stage = switch (numero) {
            // Stage 1 — memes ennemis que le Chapitre 6 normal (Ren, Hibiki, Leon, Jura, Cherry).
            case 1 -> {
                e.add(new EnnemiRen(niv));
                e.add(new EnnemiHibiki(niv));
                e.add(new EnnemiLeon(niv));
                e.add(new EnnemiJura(niv));
                e.add(new EnnemiCherry(niv));
                yield new Stage(1, "[ELITE] Prologue — L'alliance des guildes", 15000, 0, e);
            }
            // Stage 2 — memes ennemis que le Chapitre 6 normal (Oracion Seis au complet).
            case 2 -> {
                e.add(new EnnemiAngel(niv));
                e.add(new EnnemiHoteye(niv));
                e.add(new EnnemiMidnight(niv));
                e.add(new EnnemiRacer(niv));
                e.add(new EnnemiBrain(niv));
                yield new Stage(2, "[ELITE] Oracions seis vs l'alliance des guildes", 16500, 0, e);
            }
            // Stage 3 — memes ennemis que le Chapitre 6 normal (Racer + generiques).
            case 3 -> {
                e.add(new EnnemiMage5Tank(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiRacer(niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage6Debuff(Variante.CHAPITRE_6, niv));
                yield new Stage(3, "[ELITE] Gray et leon vs racer", 18000, 0, e);
            }
            // Stage 4 — memes ennemis que le Chapitre 6 normal (Angel + generiques).
            case 4 -> {
                e.add(new EnnemiAngel(niv));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage7DPS(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_6, niv));
                yield new Stage(4, "[ELITE] Combat de constellasioniste", 19500, 0, e);
            }
            // Stage 5 — remplace le duel scripte Jura vs Hoteye : toute l'equipe alliee contre
            // Hoteye et une escorte plus consequente (3 DPS + 1 support).
            case 5 -> {
                e.add(new EnnemiHoteye(niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage7DPS(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_6, niv));
                yield new Stage(5, "[ELITE] Duel entre Jura et Hoy-eyes", 21000, 0, e);
            }
            // Stage 6 — memes ennemis que le Chapitre 6 normal (Cobra + generiques).
            case 6 -> {
                e.add(new EnnemiCobra(niv));
                e.add(new EnnemiMage5Tank(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage6Debuff(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_6, niv));
                yield new Stage(6, "[ELITE] Cobras vs Natsu", 22500, 0, e);
            }
            // Stage 7 — memes ennemis que le Chapitre 6 normal (Midnight + generiques).
            case 7 -> {
                e.add(new EnnemiMidnight(niv));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage6Debuff(Variante.CHAPITRE_6, niv));
                yield new Stage(7, "[ELITE] Jellal et erza vs midnight", 24000, 0, e);
            }
            // Stage 8 — memes ennemis que le Chapitre 6 normal (Brain + generiques).
            case 8 -> {
                e.add(new EnnemiBrain(niv));
                e.add(new EnnemiMage5Tank(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage7DPS(Variante.CHAPITRE_6, niv));
                yield new Stage(8, "[ELITE] Natsu vs Brain", 25500, 0, e);
            }
            // Stage 9 — memes ennemis que le Chapitre 6 normal (Brain + generiques).
            case 9 -> {
                e.add(new EnnemiBrain(niv));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_6, niv));
                yield new Stage(9, "[ELITE] Jura vs Brain", 27000, 0, e);
            }
            // Stage 10 — remplace le duel scripte Natsu Etherion vs Zero : toute l'equipe alliee
            // contre Zero et une escorte lourde (1 tank + 3 DPS), le combat le plus dur du chapitre.
            case 10 -> {
                e.add(new EnnemiZero(niv));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage7DPS(Variante.CHAPITRE_6, niv));
                yield new Stage(10, "[ELITE] Natsu vs la nouvelle forme de Brain", 32000, 0, e);
            }
            default -> new Stage(numero, "???", 0, 0, e);
        };

        equiperEnnemisElite(e);
        return stage;
    }

    public String getTitreStage(int numero) {
        return switch (numero) {
            case 1  -> "[ELITE] Prologue — L'alliance des guildes";
            case 2  -> "[ELITE] Oracions seis vs l'alliance des guildes";
            case 3  -> "[ELITE] Gray et leon vs racer";
            case 4  -> "[ELITE] Combat de constellasioniste";
            case 5  -> "[ELITE] Duel entre Jura et Hoy-eyes";
            case 6  -> "[ELITE] Cobras vs Natsu";
            case 7  -> "[ELITE] Jellal et erza vs midnight";
            case 8  -> "[ELITE] Natsu vs Brain";
            case 9  -> "[ELITE] Jura vs Brain";
            case 10 -> "[ELITE] Natsu vs la nouvelle forme de Brain";
            default -> "???";
        };
    }

    public String getNomChapitre() { return "Oracions seis"; }

    public boolean[] getStagesDebloques() { return stagesDebloques; }
    public boolean[] getStagesReussis()   { return stagesReussis; }
    public void setStagesDebloques(boolean[] d) { for (int i = 0; i <= NB_STAGES; i++) stagesDebloques[i] = d[i]; }
    public void setStagesReussis(boolean[] r)   { for (int i = 0; i <= NB_STAGES; i++) stagesReussis[i]   = r[i]; }
}
