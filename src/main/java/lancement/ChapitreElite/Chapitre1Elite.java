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
                System.out.println(">> Vous avez termine le Chapitre 1 Elite !");
                ctx.gestionnaireTitres.debloquerTitre("Conquerant de l'Elite");
            }

            ctx.gestionnaireQuetes.notifierOrGagne(stage.getRecompenseOr());
            ctx.gestionnaireQuetes.notifierStageFini(1, numero, true,
                    ctx.joueur, ctx.menuRecrutement, ctx.personnagesRecruites);
            ctx.gestionnaireEtoiles.mettreAJour(1, numero, true,
                    resultatStage.victoire, resultatStage.sansAllieMort, resultatStage.enMoinsDe10Tours);

            if (numero == 10 && new Random().nextInt(100) < 25) {
                ctx.inventaire.ajouterMateriau("Echarpe blanche d'Ignir", 1);
                System.out.println(">> Drop rare : Echarpe blanche d'Ignir !");
                System.out.println("   (Total : "
                        + ctx.inventaire.getQuantiteMateriau("Echarpe blanche d'Ignir") + "/50)");
            }
        }
        return resultatStage;
    }

    // Palier de niveau du chapitre 1 Elite : les ennemis vont de niveau 31 (stage 1) a 40 (stage 10).
    private static final int PALIER_NIVEAU = 30;

    private int niveauPourStage(int numero) { return numero + PALIER_NIVEAU; }

    private Stage construireStage(int numero, GameContext ctx) {
        ArrayList<PersonnageBase> ennemis = new ArrayList<>();
        int niveau = niveauPourStage(numero);
        // recompenseXP = 0 : la montée de niveau passe exclusivement par les quêtes
        switch (numero) {
            case 1 -> {
                ennemis.add(new EnnemiMage1DPS(niveau));ennemis.add(new EnnemiMage2DPS(niveau));
                ennemis.add(new EnnemiMage5Tank(niveau)); ennemis.add(new EnnemiMage4Buff(niveau));
                Equipement arme = null;
                int roll = new Random().nextInt(4);
                if (roll == 0)      arme = EquipementFactory.kunaiC();
                else if (roll == 1) arme = EquipementFactory.batonC();
                else if (roll == 2) arme = EquipementFactory.gantsArmeC();
                return new Stage(1, "Prologue Elite", 200, 0, ennemis, arme);
            }
            case 2  -> { ennemis.add(new EnnemiMage2DPS(niveau)); ennemis.add(new EnnemiMage5Tank(niveau)); ennemis.add(new EnnemiMage3Soigneur(niveau));
                         ennemis.add(new EnnemiBora(niveau)); ennemis.add(new EnnemiMage1DPS(niveau));
                         return new Stage(2, "Bora le charmeur Elite", 3000, 300, ennemis, EquipementFactory.couvreCheC()); }

            case 3  -> { ennemis.add(new EnnemiBora(niveau)); ennemis.add(new EnnemiMage4Buff(niveau)); ennemis.add(new EnnemiMage2DPS(niveau));
                        ennemis.add(new EnnemiMage3Soigneur(niveau)); ennemis.add(new EnnemiMage5Tank(niveau));
                         return new Stage(3, "Chemin vers fairy tail Elite", 4000, 310, ennemis, EquipementFactory.bottesC()); }

            case 4  -> { ennemis.add(new EnnemiNatsuStage4(niveau)); ennemis.add(new EnnemiGrayStage4(niveau)); ennemis.add(new EnnemiMage5Tank(niveau));
                         return new Stage(4, "L'arrivée de la reine des fées Elite", 5600, 320, ennemis, EquipementFactory.jambieresC()); }

            case 5  -> { ennemis.add(new EnnemiMage5Tank(niveau)); ennemis.add(new EnnemiMage3Soigneur(niveau)); ennemis.add(new EnnemiMage4Buff(niveau));
                        ennemis.add(new EnnemiMage6Debuff(niveau)); ennemis.add(new EnnemiMage1DPS(niveau));
                         return new Stage(5, "Premier mission pour Lucy Elite", 6800, 330, ennemis, EquipementFactory.mainsC()); }

            case 6  -> { ennemis.add(new EnnemiEvaro(niveau)); ennemis.add(new EnnemiMage7DPS(niveau)); ennemis.add(new EnnemiMage2DPS(niveau));
                        ennemis.add(new EnnemiMage3Soigneur(niveau)); ennemis.add(new EnnemiMage1DPS(niveau));
                         return new Stage(6, "Le duc evarlo Elite", 7600, 340, ennemis, EquipementFactory.torseC()); }


            case 7  -> { ennemis.add(new EnnemiEvaro(niveau)); ennemis.add(new EnnemiBora(niveau));
                         ennemis.add(new EnnemiMage8DPS(niveau)); ennemis.add(new EnnemiMage2DPS(niveau));
                         ennemis.add(new EnnemiMage4Buff(niveau));
                         return new Stage(7, "Retour a fairy tail Elite ", 8800, 350, ennemis); }


            case 8  -> { ennemis.add(new EnnemiMage9Tank(niveau)); ennemis.add(new EnnemiMage7DPS(niveau));
                         ennemis.add(new EnnemiMage8DPS(niveau)); ennemis.add(new EnnemiMage3Soigneur(niveau)); ennemis.add(new EnnemiMage6Debuff(niveau));
                         return new Stage(8, "Eisen Wald Elite", 10400, 360, ennemis); }


            case 9  -> { ennemis.add(new EnnemiMage9Tank(niveau)); ennemis.add(new EnnemiEligor(niveau));
                         ennemis.add(new EnnemiMage3Soigneur(niveau)); ennemis.add(new EnnemiMage4Buff(niveau)); ennemis.add(new EnnemiMage6Debuff(niveau));
                         return new Stage(9, "Eligor le mage de vent Elite", 12400, 370, ennemis); }

            case 10 -> { ennemis.add(new EnnemiLullaby(niveau));

                         return new Stage(10, "La flute maudite Elite", 16000, 380, ennemis); }
            default -> { return new Stage(numero, "???", 0, 0, ennemis); }
        }
    }

    public String getTitreStage(int numero) {
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