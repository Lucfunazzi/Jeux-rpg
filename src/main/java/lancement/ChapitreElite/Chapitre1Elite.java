package lancement.ChapitreElite;

import Personnage.PersonnageBase;
import Personnage.pnj.chapitre1Elite.*;
import Personnage.pnj.Chapitre1Elite.EnnemiMage3Elite;
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
    private final Chapitre1 chapitre1;

    public Chapitre1Elite(Chapitre1 chapitre1) {
        this.chapitre1 = chapitre1;
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
                ennemis.add(new EnnemiMage1Elite());
                Equipement arme = null;
                int roll = new Random().nextInt(4);
                if (roll == 0)      arme = EquipementFactory.kunaiC();
                else if (roll == 1) arme = EquipementFactory.batonC();
                else if (roll == 2) arme = EquipementFactory.gantsArmeC();
                return new Stage(1, "[ELITE] Le Sentier Maudit", 200, 0, ennemis, arme);
            }
            case 2  -> { ennemis.add(new EnnemiMage2Elite());
                         return new Stage(2, "[ELITE] L'Ancien Reveil", 3000, 300, ennemis, EquipementFactory.couvreCheC()); }
            case 3  -> { ennemis.add(new EnnemiIrukaElite());
                         return new Stage(3, "[ELITE] Le Maitre Ninja", 4000, 310, ennemis, EquipementFactory.bottesC()); }
            case 4  -> { ennemis.add(new EnnemiGuerrier1Elite()); ennemis.add(new EnnemiMage2Elite()); ennemis.add(new EnnemiMage2Elite());
                         return new Stage(4, "[ELITE] La Triade Maudite", 5600, 320, ennemis, EquipementFactory.jambieresC()); }
            case 5  -> { ennemis.add(new EnnemiGuerrier2Elite()); ennemis.add(new EnnemiMage3Elite()); ennemis.add(new EnnemiMage3Elite());
                         return new Stage(5, "[ELITE] L'Avant-Garde", 6800, 330, ennemis, EquipementFactory.mainsC()); }
            case 6  -> { ennemis.add(new EnnemiYamchaElite());
                         return new Stage(6, "[ELITE] Le Loup du Desert", 7600, 340, ennemis, EquipementFactory.torseC()); }
            case 7  -> { ennemis.add(new EnnemiNinja1Elite()); ennemis.add(new EnnemiGuerrier3Elite());
                         ennemis.add(new EnnemiNinja2Elite()); ennemis.add(new EnnemiMage4Elite());
                         return new Stage(7, "[ELITE] Les Quatre Predateurs", 8800, 350, ennemis); }
            case 8  -> { ennemis.add(new EnnemiTank1Elite()); ennemis.add(new EnnemiGuerrier4Elite());
                         ennemis.add(new EnnemiGuerrier4Elite()); ennemis.add(new EnnemiSoigneur1Elite()); ennemis.add(new EnnemiSoigneur1Elite());
                         return new Stage(8, "[ELITE] L'Armee des Ombres", 10400, 360, ennemis); }
            case 9  -> { ennemis.add(new EnnemiTank2Elite()); ennemis.add(new EnnemiNinja3Elite());
                         ennemis.add(new EnnemiNinja3Elite()); ennemis.add(new EnnemiNinja3Elite()); ennemis.add(new EnnemiSoigneur2Elite());
                         return new Stage(9, "[ELITE] Les Generaux des Tenebres", 12400, 370, ennemis); }
            case 10 -> { ennemis.add(new EnnemiTank1Elite()); ennemis.add(new EnnemiNatsuElite());
                         ennemis.add(new EnnemiNinja3Elite()); ennemis.add(new EnnemiSoigneur1Elite()); ennemis.add(new EnnemiSoigneur2Elite());
                         return new Stage(10, "[ELITE] Le Dragon de Feu", 16000, 380, ennemis); }
            default -> { return new Stage(numero, "???", 0, 0, ennemis); }
        }
    }

    private String getTitreStage(int numero) {
        return switch (numero) {
            case 1  -> "[ELITE] Le Sentier Maudit";
            case 2  -> "[ELITE] L'Ancien Reveil";
            case 3  -> "[ELITE] Le Maitre Ninja";
            case 4  -> "[ELITE] La Triade Maudite";
            case 5  -> "[ELITE] L'Avant-Garde";
            case 6  -> "[ELITE] Le Loup du Desert";
            case 7  -> "[ELITE] Les Quatre Predateurs";
            case 8  -> "[ELITE] L'Armee des Ombres";
            case 9  -> "[ELITE] Les Generaux des Tenebres";
            case 10 -> "[ELITE] Le Dragon de Feu";
            default -> "???";
        };
    }

    public boolean[] getStagesDebloques() { return stagesDebloques; }
    public boolean[] getStagesReussis()   { return stagesReussis; }
    public void setStagesDebloques(boolean[] d) { for (int i = 0; i <= 10; i++) stagesDebloques[i] = d[i]; }
    public void setStagesReussis(boolean[] r)   { for (int i = 0; i <= 10; i++) stagesReussis[i]   = r[i]; }
}