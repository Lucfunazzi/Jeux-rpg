package lancement.ChapitreElite;

import Personnage.PersonnageBase;
import Personnage.pnj.chapitre1Elite.*;
import Personnage.pnj.Chapitre1.EnnemiBora;
import Personnage.pnj.Chapitre1.EnnemiEvaro;
import Personnage.pnj.Chapitre1.EnnemiEligor;
import Equipement.EquipementFactory;
import Equipement.Equipement;
import lancement.GameContext;
import lancement.Stage;
import lancement.Chapitres.Chapitre1;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Chapitre1Elite {

    private static final int NB_STAGES = 10;
    private final boolean[] stagesDebloques = new boolean[NB_STAGES + 1];
    private final boolean[] stagesReussis   = new boolean[NB_STAGES + 1];

    public Chapitre1Elite(Chapitre1 chapitre1) {
        stagesDebloques[1] = true;
    }

    public void afficher(GameContext ctx, Scanner scanner) {
        boolean retour = false;

        while (!retour) {
            ctx.gestionnaireEnergie.mettreAJourRecharge();

            System.out.println("\n========================================");
            System.out.println("      CHAPITRE 1 ELITE — L'Eveil");
            System.out.println("========================================");
            System.out.println("Or : " + String.format("%.0f", ctx.joueur.getOr())
                    + "  |  " + ctx.gestionnaireEnergie.afficherEnergie());
            System.out.println();

            for (int i = 1; i <= NB_STAGES; i++) {
                String etat = !stagesDebloques[i] ? "[###] " : stagesReussis[i] ? "[OK]  " : "[  ]  ";
                int restants = ctx.gestionnaireEnergie.getRunsEliteRestants(i);
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
                        System.out.println(">> Vous avez termine le Chapitre 1 Elite !");
                        ctx.gestionnaireTitres.debloquerTitre("Conquerant de l'Elite");
                    }

                    
                    ctx.gestionnaireQuetes.notifierOrGagne(stage.getRecompenseOr());
                    ctx.gestionnaireQuetes.notifierStageFini(1, choix, true,
                            ctx.joueur, ctx.menuRecrutement, ctx.personnagesRecruites);
                    ctx.gestionnaireEtoiles.mettreAJour(1, choix, true,
                            resultatStage.victoire, resultatStage.sansAllieMort, resultatStage.enMoinsDe10Tours);

                    if (choix == 10 && new Random().nextInt(100) < 25) {
                        ctx.inventaire.ajouterMateriau("Echarpe blanche d'Ignir", 1);
                        System.out.println(">> Drop rare : Echarpe blanche d'Ignir !");
                        System.out.println("   (Total : "
                                + ctx.inventaire.getQuantiteMateriau("Echarpe blanche d'Ignir") + "/50)");
                    }
                }
            }
        }
    }

    private Stage construireStage(int numero, GameContext ctx) {
        ArrayList<PersonnageBase> ennemis = new ArrayList<>();
        // recompenseXP = 0 : la montée de niveau passe exclusivement par les quêtes
        switch (numero) {
            case 1 -> {
                ennemis.add(new EnnemiMage1DPS());ennemis.add(new EnnemiMage2DPS());
                ennemis.add(new EnnemiMage5Tank()); ennemis.add(new EnnemiMage4Buff());
                Equipement arme = null;
                int roll = new Random().nextInt(4);
                if (roll == 0)      arme = EquipementFactory.kunaiC();
                else if (roll == 1) arme = EquipementFactory.batonC();
                else if (roll == 2) arme = EquipementFactory.gantsArmeC();
                return new Stage(1, "Prologue Elite", 200, 0, ennemis, arme);
            }
            case 2  -> { ennemis.add(new EnnemiMage2DPS()); ennemis.add(new EnnemiMage5Tank()); ennemis.add(new EnnemiMage3Soigneur());
                         ennemis.add(new EnnemiBora()); ennemis.add(new EnnemiMage1DPS());
                         return new Stage(2, "Bora le charmeur Elite", 3000, 300, ennemis, EquipementFactory.couvreCheC()); }
            
            case 3  -> { ennemis.add(new EnnemiBora()); ennemis.add(new EnnemiMage4Buff()); ennemis.add(new EnnemiMage2DPS());
                        ennemis.add(new EnnemiMage3Soigneur()); ennemis.add(new EnnemiMage5Tank());
                         return new Stage(3, "Chemin vers fairy tail Elite", 4000, 310, ennemis, EquipementFactory.bottesC()); }
            
            case 4  -> { ennemis.add(new EnnemiNatsuStage4()); ennemis.add(new EnnemiGrayStage4()); ennemis.add(new EnnemiMage5Tank());
                         return new Stage(4, "L'arrivée de la reine des fées Elite", 5600, 320, ennemis, EquipementFactory.jambieresC()); }
            
            case 5  -> { ennemis.add(new EnnemiMage5Tank()); ennemis.add(new EnnemiMage3Soigneur()); ennemis.add(new EnnemiMage4Buff());
                        ennemis.add(new EnnemiMage6Debuff()); ennemis.add(new EnnemiMage1DPS());
                         return new Stage(5, "Premier mission pour Lucy Elite", 6800, 330, ennemis, EquipementFactory.mainsC()); }
            
            case 6  -> { ennemis.add(new EnnemiEvaro()); ennemis.add(new EnnemiMage7DPS()); ennemis.add(new EnnemiMage2DPS());
                        ennemis.add(new EnnemiMage3Soigneur()); ennemis.add(new EnnemiMage1DPS());
                         return new Stage(6, "Le duc evarlo Elite", 7600, 340, ennemis, EquipementFactory.torseC()); }
            
            
            case 7  -> { ennemis.add(new EnnemiEvaro()); ennemis.add(new EnnemiBora());
                         ennemis.add(new EnnemiMage8DPS()); ennemis.add(new EnnemiMage2DPS());
                         ennemis.add(new EnnemiMage4Buff());
                         return new Stage(7, "Retour a fairy tail Elite ", 8800, 350, ennemis); }
            
            
            case 8  -> { ennemis.add(new EnnemiMage9Tank()); ennemis.add(new EnnemiMage7DPS());
                         ennemis.add(new EnnemiMage8DPS()); ennemis.add(new EnnemiMage3Soigneur()); ennemis.add(new EnnemiMage6Debuff());
                         return new Stage(8, "Eisen Wald Elite", 10400, 360, ennemis); }
            
            
            case 9  -> { ennemis.add(new EnnemiMage9Tank()); ennemis.add(new EnnemiEligor());
                         ennemis.add(new EnnemiMage3Soigneur()); ennemis.add(new EnnemiMage4Buff()); ennemis.add(new EnnemiMage6Debuff());
                         return new Stage(9, "Eligor le mage de vent Elite", 12400, 370, ennemis); }
            
            case 10 -> { ennemis.add(new EnnemiLullaby(1));
                        
                         return new Stage(10, "La flute maudite Elite", 16000, 380, ennemis); }
            default -> { return new Stage(numero, "???", 0, 0, ennemis); }
        }
    }

    private String getTitreStage(int numero) {
        return switch (numero) {
            case 1  -> "Prologue Elite";
            case 2  -> "Bora le charmeur Elite";
            case 3  -> "Chemin vers fairy tail Elite";
            case 4  -> "L'arrivée de la reine des fées Elite";
            case 5  -> "Premier mission pour Lucy Elite";
            case 6  -> "Le duc evarlo Elite";
            case 7  -> "Retour a fairy tail Elite";
            case 8  -> "Eisen Wald Elite";
            case 9  -> "Eligor le mage de vent Elite";
            case 10 -> "La flute maudite Elite";
            default -> "???";
        };
    }

    public boolean[] getStagesDebloques() { return stagesDebloques; }
    public boolean[] getStagesReussis()   { return stagesReussis; }
    public void setStagesDebloques(boolean[] d) { for (int i = 0; i <= 10; i++) stagesDebloques[i] = d[i]; }
    public void setStagesReussis(boolean[] r)   { for (int i = 0; i <= 10; i++) stagesReussis[i]   = r[i]; }
}