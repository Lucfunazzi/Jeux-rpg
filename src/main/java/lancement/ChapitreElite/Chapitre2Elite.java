package lancement.ChapitreElite;

import Personnage.PersonnageBase;
import Personnage.pnj.Chapitre2Elite.*;
import Personnage.pnj.EnnemisGeneriques.*;
import Equipement.Equipement;
import Equipement.EquipementFactory;
import lancement.GameContext;
import lancement.Stage;
import lancement.Chapitres.Chapitre1;
import lancement.Chapitres.Chapitre2;
import lancement.ChapitreElite.Chapitre1Elite;
import java.util.ArrayList;
import java.util.Scanner;

public class Chapitre2Elite implements ChapitreElite {

    private static final int NB_STAGES = 10;
    private final boolean[] stagesDebloques = new boolean[NB_STAGES + 1];
    private final boolean[] stagesReussis   = new boolean[NB_STAGES + 1];

    private final Chapitre1      chapitre1;
    private final Chapitre2      chapitre2;
    private final Chapitre1Elite chapitre1Elite;

    public Chapitre2Elite(Chapitre1 chapitre1, Chapitre2 chapitre2, Chapitre1Elite chapitre1Elite) {
        this.chapitre1      = chapitre1;
        this.chapitre2      = chapitre2;
        this.chapitre1Elite = chapitre1Elite;
        stagesDebloques[1]  = true;
    }

    // ── Condition de déblocage ────────────────────────────────────────────
    public boolean estDebloque() {
        return chapitre1.getStagesReussis()[10]
            && chapitre2.getStagesReussis()[10]
            && chapitre1Elite.getStagesReussis()[10];
    }

    public void afficher(GameContext ctx, Scanner scanner) {
        if (!estDebloque()) {
            System.out.println("Terminez le Chapitre 1, le Chapitre 2 et le Chapitre 1 Elite pour débloquer le Chapitre 2 Elite !");
            return;
        }

        boolean retour = false;

        while (!retour) {
            ctx.gestionnaireEnergie.mettreAJourRecharge();

            System.out.println("\n========================================");
            System.out.println("    CHAPITRE 2 ELITE — " + getNomChapitre());
            System.out.println("========================================");
            System.out.println("Or : " + String.format("%.0f", ctx.joueur.getOr())
                    + "  |  " + ctx.gestionnaireEnergie.afficherEnergie());
            System.out.println();

            for (int i = 1; i <= NB_STAGES; i++) {
                boolean jouable = ctx.gestionnaireQuetes.estStageJouable(2, i, true);
                String etat     = !stagesDebloques[i] ? "[###] "
                        : stagesReussis[i]             ? "[OK]  "
                        : jouable                       ? "[  ]  "
                        :                                 "[QUETE] ";
                int    restants = ctx.gestionnaireEnergie.getRunsEliteRestants(i);
                String etoiles  = ctx.gestionnaireEtoiles.getEtoiles(2, i, true).afficher();
                System.out.println(etat + "Stage " + i + " — " + getTitreStage(i)
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
            } else if (!ctx.gestionnaireQuetes.estStageJouable(2, choix, true)) {
                System.out.println("Acceptez d'abord la quete associee a ce stage (menu Quetes).");
            } else {
                lancerStage(ctx, choix);
            }
        }
    }

    /**
     * Verifie les runs/energie, lance le stage donne et applique les recompenses en cas de
     * victoire. Suppose que le stage est deja debloque. Retourne null si le stage n'a pas pu
     * etre lance (runs epuises ou energie insuffisante — message imprime dans ce cas).
     * Reutilisable par la console et l'interface graphique.
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
        Stage stage        = construireStage(numero, ctx);
        boolean estNouveau = !stagesReussis[numero];
        Stage.ResultatStage resultatStage = stage.lancer(ctx, ctx.formation.getEquipe(), estNouveau);

        if (resultatStage.victoire) {
            stagesReussis[numero] = true;
            if (numero < NB_STAGES) {
                stagesDebloques[numero + 1] = true;
                System.out.println(">> Stage " + (numero + 1) + " debloque !");
            } else {
                System.out.println(">> Félicitations ! Vous avez terminé le Chapitre 2 Elite !");
                ctx.gestionnaireTitres.debloquerTitre("Heros de l'Elite");
                if (estNouveau && !ctx.gestionnaireCreaturesSacrees.isOeufDebloque()) {
                    ctx.gestionnaireCreaturesSacrees.debloquerOeuf();
                    System.out.println(">> Vous avez obtenu un Œuf Mystérieux !");
                    System.out.println("      Un être légendaire sommeille à l'intérieur... Accessible depuis le menu Créatures Sacrées (niveau 30 requis).");
                }
            }

            ctx.gestionnaireQuetes.notifierOrGagne(stage.getRecompenseOr());
            ctx.gestionnaireQuetes.notifierStageFini(2, numero, true,
                    ctx.joueur, ctx.menuRecrutement, ctx.personnagesRecruites);
            ctx.gestionnaireEtoiles.mettreAJour(2, numero, true,
                    resultatStage.victoire, resultatStage.sansAllieMort, resultatStage.enMoinsDe10Tours);
        }
        return resultatStage;
    }

    private int niveauPourStage(int numero) {
        return lancement.Chapitres.CourbeChapitres.niveauEnnemiEliteLineairePourStage(2, numero);
    }

    private Stage construireStage(int numero, GameContext ctx) {
        ArrayList<PersonnageBase> ennemis = new ArrayList<>();
        int niveau = niveauPourStage(numero);
        // Pas de combat scripte (pas d'invite Natsu/Lucy/Gray, pas de flashback Ul vs Deliora) :
        // uniquement l'equipe du joueur contre les ennemis du Chapitre 2, en version elite.
        // recompenseXP = 0 : la montée de niveau passe exclusivement par les quêtes
        Stage stage = switch (numero) {
            case 1  -> { ennemis.add(new EnnemiMage1DPS(Variante.CHAPITRE_2_ELITE, niveau)); ennemis.add(new EnnemiMage1DPS(Variante.CHAPITRE_2_ELITE, niveau));
                         ennemis.add(new EnnemiMage5Tank(Variante.CHAPITRE_2_ELITE, niveau)); ennemis.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_2_ELITE, niveau));
                         ennemis.add(new EnnemiMage2DPS(Variante.CHAPITRE_2_ELITE, niveau));
                         yield new Stage(1, "Prologue Chapitre 2 Elite", 3000, 0, ennemis); }
            case 2  -> { ennemis.add(new EnnemiMage4Buff(Variante.CHAPITRE_2_ELITE, niveau)); ennemis.add(new EnnemiMage2DPS(Variante.CHAPITRE_2_ELITE, niveau));
                        ennemis.add(new EnnemiMage5Tank(Variante.CHAPITRE_2_ELITE, niveau)); ennemis.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_2_ELITE, niveau));
                         ennemis.add(new EnnemiMage2DPS(Variante.CHAPITRE_2_ELITE, niveau));
                         yield new Stage(2, "Arrivée a l'ile de galuna Elite", 3750, 0, ennemis); }
            case 3  -> { ennemis.add(new EnnemiCherry(niveau)); ennemis.add(new EnnemiMage2DPS(Variante.CHAPITRE_2_ELITE, niveau)); ennemis.add(new EnnemiMage1DPS(Variante.CHAPITRE_2_ELITE, niveau));
                         ennemis.add(new EnnemiMage5Tank(Variante.CHAPITRE_2_ELITE, niveau)); ennemis.add(new EnnemiMage4Buff(Variante.CHAPITRE_2_ELITE, niveau));
                         yield new Stage(3, "Cherry, l'amour d'elite", 4500, 0, ennemis); }
            case 4  -> { ennemis.add(new EnnemiYuka(niveau)); ennemis.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_2_ELITE, niveau));
                         ennemis.add(new EnnemiMage2DPS(Variante.CHAPITRE_2_ELITE, niveau)); ennemis.add(new EnnemiMage1DPS(Variante.CHAPITRE_2_ELITE, niveau));
                         ennemis.add(new EnnemiMage6Debuff(Variante.CHAPITRE_2_ELITE, niveau));
                         yield new Stage(4, "Yuka, l'annuleur renforce", 5500, 0, ennemis); }
            case 5  -> { ennemis.add(new EnnemiTobi(niveau)); ennemis.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_2_ELITE, niveau)); ennemis.add(new EnnemiMage2DPS(Variante.CHAPITRE_2_ELITE, niveau));
                         ennemis.add(new EnnemiMage7DPS(Variante.CHAPITRE_2_ELITE, niveau)); ennemis.add(new EnnemiMage5Tank(Variante.CHAPITRE_2_ELITE, niveau));
                         yield new Stage(5, "Tobi,les griffes paralysantes d'elite", 6500, 0, ennemis); }
            case 6  -> { ennemis.add(new EnnemiLeon(niveau)); ennemis.add(new EnnemiMage1DPS(Variante.CHAPITRE_2_ELITE, niveau));
                         ennemis.add(new EnnemiMage6Debuff(Variante.CHAPITRE_2_ELITE, niveau)); ennemis.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_2_ELITE, niveau));
                         ennemis.add(new EnnemiMage9Tank(Variante.CHAPITRE_2_ELITE, niveau));
                         yield new Stage(6, "Leon Bastia d'elite", 8000, 0, ennemis); }
            case 7  -> { ennemis.add(new EnnemiLeon(niveau)); ennemis.add(new EnnemiTobi(niveau)); ennemis.add(new EnnemiYuka(niveau));
                         ennemis.add(new EnnemiCherry(niveau));
                         yield new Stage(7, "Leon et son equipes", 9750, 0, ennemis); }
            case 8  -> { ennemis.add(new EnnemiLeon(niveau)); ennemis.add(new EnnemiMage1DPS(Variante.CHAPITRE_2_ELITE, niveau)); ennemis.add(new EnnemiMage5Tank(Variante.CHAPITRE_2_ELITE, niveau));
                        ennemis.add(new EnnemiMage4Buff(Variante.CHAPITRE_2_ELITE, niveau)); ennemis.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_2_ELITE, niveau));
                         yield new Stage(8, "Leon Bastia — Resistance d'elite", 11500, 0, ennemis); }
            case 9  -> { ennemis.add(new EnnemiLeon(niveau)); ennemis.add(new EnnemiHomme_mysterieux(niveau)); ennemis.add(new EnnemiMage7DPS(Variante.CHAPITRE_2_ELITE, niveau));
                         ennemis.add(new EnnemiMage8DPS(Variante.CHAPITRE_2_ELITE, niveau)); ennemis.add(new EnnemiMage5Tank(Variante.CHAPITRE_2_ELITE, niveau));
                         yield new Stage(9, "Leon et L'homme mysterieux — Elite", 13500, 0, ennemis); }
            case 10 -> { ennemis.add(new EnnemiDeliora(niveau));
                         yield new Stage(10, "Deliora le demon Elite", 16000, 0, ennemis); }
            default -> new Stage(numero, "???", 0, 0, ennemis);
        };

        // Equipement fantome d'Elite : contourne volontairement le seuil de niveau 25 de Stage
        // (voir EquipementFactory.equiperGearElite) car le mode Elite est optionnel et delibere.
        for (PersonnageBase e : ennemis) {
            EquipementFactory.equiperGearElite(e, Equipement.Rarete.C, 6, 11, 20, 5, 5, 4, 4);
        }
        return stage;
    }

    public String getTitreStage(int numero) {
        return switch (numero) {
            case 1  -> "[ELITE] Prologue Chapitre 2";
            case 2  -> "[ELITE] Arrivée à l'île de Galuna";
            case 3  -> "[ELITE] Cherry, l'amour d'elite";
            case 4  -> "[ELITE] Yuka, l'annuleur renforcé";
            case 5  -> "[ELITE] Tobi,les griffes paralysantes d'elite";
            case 6  -> "[ELITE] Leon Bastia d'élite";
            case 7  -> "[ELITE] Leon et son equipes";
            case 8  -> "[ELITE] Leon Bastia — Résistance d'élite";
            case 9  -> "[ELITE] Leon et L'homme mysterieux — Elite";
            case 10 -> "[ELITE] Deliora, le démon Elite";
            default -> "???";
        };
    }
    

    public String getNomChapitre() { return "L'Ile de Galuna"; }

    public boolean[] getStagesDebloques() { return stagesDebloques; }

    public boolean[] getStagesReussis()   { return stagesReussis; }
    public void setStagesDebloques(boolean[] d) { for (int i = 0; i <= NB_STAGES; i++) stagesDebloques[i] = d[i]; }
    public void setStagesReussis(boolean[] r)   { for (int i = 0; i <= NB_STAGES; i++) stagesReussis[i]   = r[i]; }
}