package lancement.Chapitres;

import Personnage.PersonnageBase;
import Personnage.pnj.Chapitre1.*;
import lancement.GameContext;
import lancement.Formation;
import lancement.Stage;
import java.util.ArrayList;
import java.util.Scanner;
import Personnage.FairyTail.perso_Erza;
import Personnage.FairyTail.perso_Gray;
import Personnage.FairyTail.perso_Natsu;

public class Chapitre1 {

    private static final int NB_STAGES = 10;
    private final boolean[] stagesDebloques = new boolean[NB_STAGES + 1];
    private final boolean[] stagesReussis   = new boolean[NB_STAGES + 1];

    public Chapitre1() {
        stagesDebloques[1] = true;
    }

    public void afficher(GameContext ctx, Scanner scanner) {
        boolean retour = false;

        while (!retour) {
            ctx.gestionnaireEnergie.mettreAJourRecharge();

            System.out.println("\n========================================");
            System.out.println("      CHAPITRE 1 — Prologue ");
            System.out.println("========================================");
            System.out.println("Or : " + String.format("%.0f", ctx.joueur.getOr())
                    + "  |  " + ctx.gestionnaireEnergie.afficherEnergie());
            System.out.println();

            for (int i = 1; i <= NB_STAGES; i++) {
                String etat = stagesReussis[i] ? "[OK]  " : stagesDebloques[i] ? "[  ]  " : "[###] ";
                String etoiles = ctx.gestionnaireEtoiles.getEtoiles(1, i, false).afficher();
                System.out.println(etat + "Stage " + i + " — " + getTitreStage(i) + "  " + etoiles);
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
     * Lance le stage donne (avec les invites speciaux stage 4/10) et applique les recompenses
     * en cas de victoire. Suppose que le stage est deja debloque. Reutilisable par la console
     * et l'interface graphique.
     */
    public Stage.ResultatStage lancerStage(GameContext ctx, int numero) {
        if (!ctx.gestionnaireEnergie.consommerEnergie(1)) {
            System.out.println("Pas assez d'energie ! (il faut 1, vous avez "
                    + ctx.gestionnaireEnergie.getEnergie() + ")");
            return new Stage.ResultatStage(false, false, false, 0,
                    java.util.List.of(), java.util.List.of());
        }

        Stage stage        = construireStage(numero);
        boolean estNouveau = !stagesReussis[numero];
        Stage.ResultatStage resultatStage;
        if (numero == 4) {
            resultatStage = lancerStage4AvecErza(ctx, stage, estNouveau);
        } else if (numero == 10) {
            resultatStage = lancerStage10AvecN_G_E(ctx, stage, estNouveau);
        } else {
            resultatStage = stage.lancer(ctx, ctx.formation.getEquipe(), estNouveau);
        }

        if (resultatStage.victoire) {
            stagesReussis[numero] = true;
            if (numero < NB_STAGES) {
                stagesDebloques[numero + 1] = true;
                System.out.println(">> Stage " + (numero + 1) + " debloque !");
            } else {
                System.out.println(">> Vous avez termine le Chapitre 1 !");
            }

            ctx.gestionnaireQuetes.notifierOrGagne(stage.getRecompenseOr());
            ctx.gestionnaireQuetes.notifierStageFini(1, numero, false,
                    ctx.joueur, ctx.menuRecrutement, ctx.personnagesRecruites);
            ctx.gestionnaireEtoiles.mettreAJour(1, numero, false,
                    resultatStage.victoire, resultatStage.sansAllieMort, resultatStage.enMoinsDe10Tours);
        }
        return resultatStage;
    }

    // Palier de niveau du chapitre 1 : les ennemis vont de niveau 1 (stage 1) a 10 (stage 10).
    private static final int PALIER_NIVEAU = 0;

    private int niveauPourStage(int numero) { return numero + PALIER_NIVEAU; }

    private Stage construireStage(int numero) {
        ArrayList<PersonnageBase> ennemis = new ArrayList<>();
        int niveau = niveauPourStage(numero);

        // Recompenses tres faibles : la montee de niveau passe exclusivement par les quetes.
        switch (numero) {
            case 1  -> { ennemis.add(new EnnemiMage1DPS(niveau));
                         return new Stage(1, "Prologue", 5, 1, ennemis); }
            case 2  -> {  ennemis.add(new EnnemiBora(niveau));
                         return new Stage(2, "Bora le charmeur", 8, 2, ennemis); }
            case 3  -> { ennemis.add(new EnnemiMage1DPS(niveau)); ennemis.add(new EnnemiMage2DPS(niveau));
                         return new Stage(3, "Chemin vers fairy tail", 11, 2, ennemis); }
            case 4  -> { ennemis.add(new EnnemiNatsuStage4(niveau)); ennemis.add(new EnnemiGrayStage4(niveau));
                         return new Stage(4, "L'arrivée de la reine des fées", 15, 3, ennemis); }
            case 5  -> { ennemis.add(new EnnemiMage3Soigneur(niveau)); ennemis.add(new EnnemiMage4Buff(niveau)); ennemis.add(new EnnemiMage1DPS(niveau));
                         return new Stage(5, "Premier mission pour Lucy", 18, 3, ennemis); }
            case 6  -> { ennemis.add(new EnnemiMage2DPS(niveau)); ennemis.add(new EnnemiMage1DPS(niveau)); ennemis.add(new EnnemiEvaro(niveau));
                         return new Stage(6, "Le duc evarlo", 22, 4, ennemis); }
            case 7  -> { ennemis.add(new EnnemiMage5Tank(niveau)); ennemis.add(new EnnemiMage2DPS(niveau));
                         ennemis.add(new EnnemiMage6Debuff(niveau)); ennemis.add(new EnnemiMage4Buff(niveau));
                         return new Stage(7, "Retour a fairy tail ", 25, 4, ennemis); }
            case 8  -> { ennemis.add(new EnnemiMage5Tank(niveau)); ennemis.add(new EnnemiMage7DPS(niveau));
                         ennemis.add(new EnnemiMage4Buff(niveau)); ennemis.add(new EnnemiMage3Soigneur(niveau)); ennemis.add(new EnnemiMage7DPS(niveau));
                         return new Stage(8, "Eisen Wald", 29, 5, ennemis); }
            case 9  -> { ennemis.add(new EnnemiMage9Tank(niveau)); ennemis.add(new EnnemiMage2DPS(niveau));
                         ennemis.add(new EnnemiEligor(niveau));  ennemis.add(new EnnemiMage3Soigneur(niveau)); ennemis.add(new EnnemiMage4Buff(niveau));
                         return new Stage(9, "Eligor le mage de vent", 34, 5, ennemis); }
            case 10 -> { ennemis.add(new EnnemiLullaby(niveau));
                         return new Stage(10, "La flute maudite", 43, 6, ennemis); }
            default -> { return new Stage(numero, "???", 0, 0, ennemis); }
        }
    }

    public String getTitreStage(int numero) {
        return switch (numero) {
            case 1  -> "Prologue";
            case 2  -> "Bora le charmeur";
            case 3  -> "Chemin vers fairy tail";
            case 4  -> "L'arrivé de la reine des fées";
            case 5  -> "Premier mission pour Lucy";
            case 6  -> "Le duc evarlo";
            case 7  -> "Retour a fairy tail ";
            case 8  -> "Eisen Wald";
            case 9  -> "Eligor le mage de vent";
            case 10 -> "La flute maudite";
            default -> "???";
        };
    }

    private boolean dejaRecruteParNom(String nom, ArrayList<PersonnageBase> liste) {
        for (PersonnageBase p : liste)
            if (p.getNom().equalsIgnoreCase(nom)) return true;
        return false;
    }

    public boolean[] getStagesDebloques() { return stagesDebloques; }
    public boolean[] getStagesReussis()   { return stagesReussis; }
    public void setStagesDebloques(boolean[] d) { for (int i = 0; i <= 10; i++) stagesDebloques[i] = d[i]; }
    public void setStagesReussis(boolean[] r)   { for (int i = 0; i <= 10; i++) stagesReussis[i]   = r[i]; }
  
    
    private Stage.ResultatStage lancerStage4AvecErza(GameContext ctx,Stage stage, boolean estNouveau){
        perso_Erza erzaTemporaire = new perso_Erza();
        erzaTemporaire.setNiveau(70);
        erzaTemporaire.setVie(10000);
        erzaTemporaire.setVieMax(10000);
        erzaTemporaire.setAttaque(4500);
        erzaTemporaire.setDefense(10);
        erzaTemporaire.setVitesse(4000);
        erzaTemporaire.ajouterRage(100);

    ArrayList<PersonnageBase> equipeAvecErza = ctx.formation.getEquipe();
    Formation.ajouterInviteTemporaire(equipeAvecErza, erzaTemporaire);

     System.out.println(">> Erza Scarlet rejoint votre équipe pour ce combat !");
    System.out.println("   « Je m'en occupe. Regardez bien. »\n");

    // Lancer le combat avec l'équipe augmentée
    Stage.ResultatStage resultat = stage.lancer(ctx, equipeAvecErza, estNouveau);

    // Retirer Erza après le combat
    System.out.println("\n>> Erza quitte l'équipe après sa démonstration.");

    return resultat;
    }
    
    private Stage.ResultatStage lancerStage10AvecN_G_E(GameContext ctx, Stage stage, boolean estNouveau) {

    // Natsu
    perso_Natsu natsuTemporaire = new perso_Natsu();
    
   natsuTemporaire.setVie(6000);
natsuTemporaire.setVieMax(6000);
natsuTemporaire.setAttaque(900);
natsuTemporaire.setDefense(250);
natsuTemporaire.setVitesse(180);

    // Gray
    perso_Gray grayTemporaire = new perso_Gray();
    
 grayTemporaire.setVie(5500);
grayTemporaire.setVieMax(5500);
grayTemporaire.setAttaque(750);
grayTemporaire.setDefense(320);
grayTemporaire.setVitesse(150);

    // Erza
    perso_Erza erza = new perso_Erza();
    erza.setVie(8000);
erza.setVieMax(8000);
erza.setAttaque(850);
erza.setDefense(500);
erza.setVitesse(160);

    // Équipe fixe — sans les personnages du joueur
    ArrayList<PersonnageBase> equipeFixe = new ArrayList<>();
    equipeFixe.add(erza);
    equipeFixe.add(natsuTemporaire);
    equipeFixe.add(grayTemporaire);

    System.out.println(">> Natsu, Gray et Erza prennent les choses en main !");
    System.out.println("   « Cette fois, on s'en occupe ensemble. » — Erza\n");

    Stage.ResultatStage resultat = stage.lancer(ctx, equipeFixe, estNouveau);

    System.out.println("\n>> Natsu, Gray et Erza retournent à la guilde.");

    return resultat;
}
    
    
}