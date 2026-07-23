package lancement.ChapitreElite;

import Personnage.PersonnageBase;
import Personnage.pnj.Chapitre2Elite.*;
import lancement.GameContext;
import lancement.Stage;
import lancement.Chapitres.Chapitre1;
import lancement.Chapitres.Chapitre2;
import lancement.ChapitreElite.Chapitre1Elite;
import java.util.ArrayList;
import java.util.Scanner;

public class Chapitre2Elite {

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
            System.out.println("    CHAPITRE 2 ELITE — L'Ile de Galuna");
            System.out.println("========================================");
            System.out.println("Or : " + String.format("%.0f", ctx.joueur.getOr())
                    + "  |  " + ctx.gestionnaireEnergie.afficherEnergie());
            System.out.println();

            for (int i = 1; i <= NB_STAGES; i++) {
                String etat     = !stagesDebloques[i] ? "[###] " : stagesReussis[i] ? "[OK]  " : "[  ]  ";
                int    restants = ctx.gestionnaireEnergie.getRunsEliteRestants(i);
                System.out.println(etat + "Stage " + i + " — " + getTitreStage(i)
                        + "  (" + restants + "/10 runs restants)");
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
                if (estNouveau && !ctx.gestionnaireCreaturesSacrees.isOeufDebloque()) {
                    ctx.gestionnaireCreaturesSacrees.debloquerOeuf();
                    System.out.println(">> Vous avez obtenu un Œuf Mystérieux !");
                    System.out.println("      Un être légendaire sommeille à l'intérieur... Accessible depuis le menu Créatures Sacrées (niveau 30 requis).");
                }
            }

            if (numero == NB_STAGES && Math.random() < 0.50) {
                ctx.inventaire.ajouterMateriau("Echarpe blanche d'Ignir", 1);
                System.out.println("   + 1x Echarpe blanche d'Ignir !");
            }

            ctx.gestionnaireQuetes.notifierOrGagne(stage.getRecompenseOr());
            ctx.gestionnaireQuetes.notifierStageFini(2, numero, true,
                    ctx.joueur, ctx.menuRecrutement, ctx.personnagesRecruites);
            ctx.gestionnaireEtoiles.mettreAJour(2, numero, true,
                    resultatStage.victoire, resultatStage.sansAllieMort, resultatStage.enMoinsDe10Tours);
        }
        return resultatStage;
    }

    // Palier de niveau du chapitre 2 Elite : les ennemis vont de niveau 21 (stage 1) a 30 (stage 10).
    private static final int PALIER_NIVEAU = 20;

    private int niveauPourStage(int numero) { return numero + PALIER_NIVEAU; }

    private Stage construireStage(int numero, GameContext ctx) {
        ArrayList<PersonnageBase> ennemis = new ArrayList<>();
        int niveau = niveauPourStage(numero);
        // Pas de combat scripte (pas d'invite Natsu/Lucy/Gray, pas de flashback Ul vs Deliora) :
        // uniquement l'equipe du joueur contre les ennemis du Chapitre 2, en version elite.
        switch (numero) {
            case 1  -> { ennemis.add(new EnnemiMage1DPS(niveau)); ennemis.add(new EnnemiMage1DPS(niveau));
                         ennemis.add(new EnnemiMage5Tank(niveau)); ennemis.add(new EnnemiMage3Soigneur(niveau));
                         ennemis.add(new EnnemiMage2DPS(niveau));
                         return new Stage(1, "Prologue Chapitre 2 Elite", 3000, 30, ennemis); }
            case 2  -> { ennemis.add(new EnnemiMage4Buff(niveau)); ennemis.add(new EnnemiMage2DPS(niveau));
                        ennemis.add(new EnnemiMage5Tank(niveau)); ennemis.add(new EnnemiMage3Soigneur(niveau));
                         ennemis.add(new EnnemiMage2DPS(niveau));
                         return new Stage(2, "Arrivée a l'ile de galuna Elite", 3750, 50, ennemis); }
            case 3  -> { ennemis.add(new EnnemiCherry(niveau)); ennemis.add(new EnnemiMage2DPS(niveau)); ennemis.add(new EnnemiMage1DPS(niveau));
                         ennemis.add(new EnnemiMage5Tank(niveau)); ennemis.add(new EnnemiMage4Buff(niveau));
                         return new Stage(3, "Cherry, gardienne d'elite", 4500, 100, ennemis); }
            case 4  -> { ennemis.add(new EnnemiYuka(niveau)); ennemis.add(new EnnemiMage3Soigneur(niveau));
                         ennemis.add(new EnnemiMage2DPS(niveau)); ennemis.add(new EnnemiMage1DPS(niveau));
                         ennemis.add(new EnnemiMage6Debuff(niveau));
                         return new Stage(4, "Yuka, l'annuleur renforce", 5500, 150, ennemis); }
            case 5  -> { ennemis.add(new EnnemiTobi(niveau)); ennemis.add(new EnnemiMage3Soigneur(niveau)); ennemis.add(new EnnemiMage2DPS(niveau));
                         ennemis.add(new EnnemiMage9Tank(niveau)); ennemis.add(new EnnemiMage1DPS(niveau));
                         return new Stage(5, "Tobi, mage de glace d'elite", 6500, 160, ennemis); }
            case 6  -> { ennemis.add(new EnnemiLeon(niveau)); ennemis.add(new EnnemiMage1DPS(niveau));
                         ennemis.add(new EnnemiMage6Debuff(niveau)); ennemis.add(new EnnemiMage3Soigneur(niveau));
                         ennemis.add(new EnnemiMage9Tank(niveau));
                         return new Stage(6, "Leon Bastia d'elite", 8000, 170, ennemis); }
            case 7  -> { ennemis.add(new EnnemiHomme_mysterieux(niveau)); ennemis.add(new EnnemiMage1DPS(niveau)); ennemis.add(new EnnemiMage2DPS(niveau));
                         ennemis.add(new EnnemiMage5Tank(niveau));
                         return new Stage(7, "L'homme mysterieux d'elite", 9750, 180, ennemis); }
            case 8  -> { ennemis.add(new EnnemiLeon(niveau)); ennemis.add(new EnnemiMage1DPS(niveau)); ennemis.add(new EnnemiMage5Tank(niveau));
                        ennemis.add(new EnnemiMage4Buff(niveau)); ennemis.add(new EnnemiMage3Soigneur(niveau));
                         return new Stage(8, "Leon Bastia — Resistance d'elite", 11500, 190, ennemis); }
            case 9  -> { ennemis.add(new EnnemiLeon(niveau)); ennemis.add(new EnnemiYuka(niveau)); ennemis.add(new EnnemiCherry(niveau));
                         ennemis.add(new EnnemiMage2DPS(niveau)); ennemis.add(new EnnemiTobi(niveau));
                         return new Stage(9, "L'ile de Galuna unie — Elite", 13500, 200, ennemis); }
            case 10 -> { ennemis.add(new EnnemiDeliora(niveau));
                         return new Stage(10, "Deliora le demon Elite", 16000, 210, ennemis); }
            default -> { return new Stage(numero, "???", 0, 0, ennemis); }
        }
    }

    public String getTitreStage(int numero) {
        return switch (numero) {
            case 1  -> "[ELITE] Prologue Chapitre 2";
            case 2  -> "[ELITE] Arrivée à l'île de Galuna";
            case 3  -> "[ELITE] Cherry, gardienne d'élite";
            case 4  -> "[ELITE] Yuka, l'annuleur renforcé";
            case 5  -> "[ELITE] Tobi, mage de glace d'élite";
            case 6  -> "[ELITE] Leon Bastia d'élite";
            case 7  -> "[ELITE] L'homme mystérieux d'élite";
            case 8  -> "[ELITE] Leon Bastia — Résistance d'élite";
            case 9  -> "[ELITE] L'île de Galuna unie";
            case 10 -> "[ELITE] Deliora, le démon";
            default -> "???";
        };
    }
    

    public boolean[] getStagesDebloques() { return stagesDebloques; }
    
    public boolean[] getStagesReussis()   { return stagesReussis; }
    public void setStagesDebloques(boolean[] d) { for (int i = 0; i <= NB_STAGES; i++) stagesDebloques[i] = d[i]; }
    public void setStagesReussis(boolean[] r)   { for (int i = 0; i <= NB_STAGES; i++) stagesReussis[i]   = r[i]; }
}