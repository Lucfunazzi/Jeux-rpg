package lancement.ChapitreElite;

import Personnage.PersonnageBase;
import Personnage.pnj.Chapitre2Elite.*;
import Personnage.pnj.Chapitre2.EnnemiTobi;
import Personnage.pnj.Chapitre2.EnnemiYuka;
import Personnage.pnj.Chapitre2.EnnemiChery;
import Personnage.pnj.Chapitre2.EnnemiLeon;
import Personnage.pnj.Chapitre1.EnnemiMage1;
import Personnage.pnj.Chapitre1.EnnemiMage2;
import Personnage.pnj.Chapitre1.EnnemiMage3;
import Personnage.pnj.Chapitre1.EnnemiTank1;
import Personnage.pnj.Chapitre1.EnnemiSoigneur1;
import Personnage.pnj.Chapitre1.EnnemiSoigneur2;
import Equipement.EquipementFactory;
import Equipement.Equipement;
import lancement.GameContext;
import lancement.Stage;
import lancement.Chapitres.Chapitre1;
import lancement.Chapitres.Chapitre2;
import lancement.ChapitreElite.Chapitre1Elite;
import java.util.ArrayList;
import java.util.Random;
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
            System.out.println("    CHAPITRE 2 ELITE — L'Examen Ninja");
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
            } else if (!ctx.gestionnaireEnergie.peutFaireRunElite(choix)) {
                System.out.println("Limite de runs atteinte pour ce stage aujourd'hui (10/10).");
            } else if (!ctx.gestionnaireEnergie.consommerEnergie(5)) {
                System.out.println("Pas assez d'energie ! (il faut 5, vous avez "
                        + ctx.gestionnaireEnergie.getEnergie() + ")");
            } else {
                ctx.gestionnaireEnergie.enregistrerRunElite(choix);
                Stage stage        = construireStage(choix, ctx);
                boolean estNouveau = !stagesReussis[choix];
                Stage.ResultatStage resultatStage = stage.lancer(ctx, ctx.formation.getEquipe(), estNouveau);
                boolean victoire = resultatStage.victoire;

                if (victoire) {
                    stagesReussis[choix] = true;
                    if (choix < NB_STAGES) {
                        stagesDebloques[choix + 1] = true;
                        System.out.println(">> Stage " + (choix + 1) + " debloque !");
                    } else {
                        System.out.println(">> Félicitations ! Vous avez terminé le Chapitre 2 Elite !");
                        // Débloquer l'arbre 2 des abilités
                        ctx.joueur.getArbreCompetences().setArbre2Debloque(true);
                        System.out.println(">> L'Arbre 2 des Abilités est maintenant accessible !");
                        // Donner l'Oeuf de Créature Sacrée (une seule fois)
                        if (estNouveau && !ctx.gestionnaireCreaturesSacrees.isOeufDebloque()) {
                            ctx.gestionnaireCreaturesSacrees.debloquerOeuf();
                            System.out.println(">> Vous avez obtenu un Œuf Mystérieux !");
                            System.out.println("      Un être légendaire sommeille à l'intérieur... Accessible depuis le menu Créatures Sacrées (niveau 30 requis).");
                        }
                    }

                    // ── Drop écharpe d'Ignir sur stage 10 Elite (50%) ──
                    if (choix == NB_STAGES && Math.random() < 0.50) {
                        ctx.inventaire.ajouterMateriau("Echarpe blanche d'Ignir", 1);
                        System.out.println("   + 1x Echarpe blanche d'Ignir !");
                    }

                    ctx.gestionnaireQuetes.notifierOrGagne(stage.getRecompenseOr());
                    ctx.gestionnaireQuetes.notifierStageFini(2, choix, true,
                            ctx.joueur, ctx.menuRecrutement, ctx.personnagesRecruites);
                    ctx.gestionnaireEtoiles.mettreAJour(2, choix, true,
                            resultatStage.victoire, resultatStage.sansAllieMort, resultatStage.enMoinsDe10Tours);
                }
            }
        }
    }

    private Stage construireStage(int numero, GameContext ctx) {
        ArrayList<PersonnageBase> ennemis = new ArrayList<>();
        switch (numero) {
            case 1  -> { ennemis.add(new EnnemiTobi(15)); ennemis.add(new EnnemiMage2(15)); ennemis.add(new EnnemiMage1(15));
                         return new Stage(1, "[ELITE] Débarquement — La garde de l'île", 6000, 0, ennemis); }
            case 2  -> { ennemis.add(new EnnemiYuka(15)); ennemis.add(new EnnemiTobi(15)); ennemis.add(new EnnemiMage2(15)); ennemis.add(new EnnemiSoigneur1());
                         return new Stage(2, "[ELITE] Yuka et Tobi renforcés", 7500, 0, ennemis); }
            case 3  -> { ennemis.add(new EnnemiChery(16)); ennemis.add(new EnnemiYuka(16)); ennemis.add(new EnnemiMage3(16)); ennemis.add(new EnnemiTank1());
                         return new Stage(3, "[ELITE] Chery et l'annuleur d'élite", 9000, 0, ennemis); }
            case 4  -> { ennemis.add(new EnnemiTobi(17)); ennemis.add(new EnnemiYuka(17)); ennemis.add(new EnnemiChery(17)); ennemis.add(new EnnemiMage2(17)); ennemis.add(new EnnemiSoigneur1());
                         return new Stage(4, "[ELITE] Le trio de l'île — Forme renforcée", 11000, 0, ennemis); }
            case 5  -> { ennemis.add(new EnnemiLeon(20)); ennemis.add(new EnnemiMage3(18)); ennemis.add(new EnnemiTank1()); ennemis.add(new EnnemiSoigneur2());
                         return new Stage(5, "[ELITE] Leon Bastia d'élite", 13000, 0, ennemis); }
            case 6  -> { ennemis.add(new EnnemiLeon(20)); ennemis.add(new EnnemiTobi(18)); ennemis.add(new EnnemiYuka(18)); ennemis.add(new EnnemiChery(18)); ennemis.add(new EnnemiMage2());
                         return new Stage(6, "[ELITE] L'île en guerre totale", 16000, 0, ennemis); }
            case 7  -> { ennemis.add(new EnnemiLeon(22)); ennemis.add(new EnnemiYuka(20)); ennemis.add(new EnnemiChery(20)); ennemis.add(new EnnemiTank1()); ennemis.add(new EnnemiSoigneur2());
                         return new Stage(7, "[ELITE] Leon — Maître des glaces d'élite", 19500, 0, ennemis); }
            case 8  -> { ennemis.add(new EnnemiLeon(24)); ennemis.add(new EnnemiTobi(22)); ennemis.add(new EnnemiYuka(22)); ennemis.add(new EnnemiChery(22)); ennemis.add(new EnnemiMage3());
                         return new Stage(8, "[ELITE] Dernier bastion de l'île", 23000, 0, ennemis); }
            case 9  -> { ennemis.add(new EnnemiLeon(26)); ennemis.add(new EnnemiYuka(24)); ennemis.add(new EnnemiTank1()); ennemis.add(new EnnemiSoigneur1()); ennemis.add(new EnnemiSoigneur2());
                         return new Stage(9, "[ELITE] Leon — Résistance ultime", 27000, 0, ennemis); }
            case 10 -> { ennemis.add(new EnnemiLeon(28)); ennemis.add(new EnnemiYuka(26)); ennemis.add(new EnnemiChery(26)); ennemis.add(new EnnemiTobi(24)); ennemis.add(new EnnemiSoigneur2());
                         return new Stage(10, "[ELITE] Leon — Le Pacte Brisé Ultime", 32000, 0, ennemis); }
            default -> { return new Stage(numero, "???", 0, 0, ennemis); }
        }
    }

    private String getTitreStage(int numero) {
        return switch (numero) {
            case 1  -> "[ELITE] Débarquement — La garde de l'île";
            case 2  -> "[ELITE] Yuka et Tobi renforcés";
            case 3  -> "[ELITE] Chery et l'annuleur d'élite";
            case 4  -> "[ELITE] Le trio de l'île — Forme renforcée";
            case 5  -> "[ELITE] Leon Bastia d'élite";
            case 6  -> "[ELITE] L'île en guerre totale";
            case 7  -> "[ELITE] Leon — Maître des glaces d'élite";
            case 8  -> "[ELITE] Dernier bastion de l'île";
            case 9  -> "[ELITE] Leon — Résistance ultime";
            case 10 -> "[ELITE] Leon — Le Pacte Brisé Ultime";
            default -> "???";
        };
    }
    

    public boolean[] getStagesDebloques() { return stagesDebloques; }
    
    public boolean[] getStagesReussis()   { return stagesReussis; }
    public void setStagesDebloques(boolean[] d) { for (int i = 0; i <= NB_STAGES; i++) stagesDebloques[i] = d[i]; }
    public void setStagesReussis(boolean[] r)   { for (int i = 0; i <= NB_STAGES; i++) stagesReussis[i]   = r[i]; }
}