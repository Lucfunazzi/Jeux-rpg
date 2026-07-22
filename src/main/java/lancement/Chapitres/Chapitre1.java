package lancement.Chapitres;

import Personnage.PersonnageBase;
import Personnage.pnj.Chapitre1.*;
import lancement.GameContext;
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
            System.out.println("\n========================================");
            System.out.println("      CHAPITRE 1 — Prologue : L'Éveil");
            System.out.println("========================================");
            System.out.println("Or : " + String.format("%.0f", ctx.joueur.getOr()));
            System.out.println();

            for (int i = 1; i <= NB_STAGES; i++) {
                String etat = stagesReussis[i] ? "[OK]  " : stagesDebloques[i] ? "[  ]  " : "[###] ";
                System.out.println(etat + "Stage " + i + " — " + getTitreStage(i));
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
                Stage stage        = construireStage(choix);
                boolean estNouveau = !stagesReussis[choix];
                Stage.ResultatStage resultatStage;
                if (choix == 4) {
                    resultatStage = lancerStage4AvecErza(ctx, stage, estNouveau);
                } else if (choix == 10) {
                    resultatStage = lancerStage10AvecN_G_E(ctx, stage, estNouveau);
                } else {
                    resultatStage = stage.lancer(ctx, ctx.formation.getEquipe(), estNouveau);
                }
                boolean victoire = resultatStage.victoire;
                    
                if (victoire) {
                    stagesReussis[choix] = true;
                    if (choix < NB_STAGES) {
                        stagesDebloques[choix + 1] = true;
                        System.out.println(">> Stage " + (choix + 1) + " debloque !");
                    } else {
                        System.out.println(">> Vous avez termine le Chapitre 1 !");
                    }

                    
                    ctx.gestionnaireQuetes.notifierOrGagne(stage.getRecompenseOr());
                    ctx.gestionnaireQuetes.notifierStageFini(1, choix, false,
                            ctx.joueur, ctx.menuRecrutement, ctx.personnagesRecruites);
                    ctx.gestionnaireEtoiles.mettreAJour(1, choix, false,
                            resultatStage.victoire, resultatStage.sansAllieMort, resultatStage.enMoinsDe10Tours);


                }
            }
        }
    }

    private Stage construireStage(int numero) {
        ArrayList<PersonnageBase> ennemis = new ArrayList<>();

        switch (numero) {
            case 1  -> { ennemis.add(new EnnemiMage1DPS());
                         return new Stage(1, "Prologue", 100, 10, ennemis); }
            case 2  -> {  ennemis.add(new EnnemiBora());
                         return new Stage(2, "Bora le charmeur", 150, 15, ennemis); }
            case 3  -> { ennemis.add(new EnnemiMage1DPS()); ennemis.add(new EnnemiMage2DPS()); 
                         return new Stage(3, "Chemin vers fairy tail", 220, 20, ennemis); }
            case 4  -> { ennemis.add(new EnnemiNatsuStage4()); ennemis.add(new EnnemiGrayStage4());
                         return new Stage(4, "L'arrivée de la reine des fées", 290, 25, ennemis); }
            case 5  -> { ennemis.add(new EnnemiMage3Soigneur()); ennemis.add(new EnnemiMage4Buff()); ennemis.add(new EnnemiMage1DPS());
                         return new Stage(5, "Premier mission pour Lucy", 360, 30, ennemis); }
            case 6  -> { ennemis.add(new EnnemiMage9Tank()); ennemis.add(new EnnemiMage1DPS()); ennemis.add(new EnnemiEvaro());
                         return new Stage(6, "Le duc evarlo", 430, 35, ennemis); }
            case 7  -> { ennemis.add(new EnnemiMage5Tank()); ennemis.add(new EnnemiMage2DPS());
                         ennemis.add(new EnnemiMage6Debuff()); ennemis.add(new EnnemiMage4Buff());
                         return new Stage(7, "Retour a fairy tail ", 500, 40, ennemis); }
            case 8  -> { ennemis.add(new EnnemiMage9Tank()); ennemis.add(new EnnemiMage7DPS());
                         ennemis.add(new EnnemiMage4Buff()); ennemis.add(new EnnemiMage3Soigneur()); ennemis.add(new EnnemiMage7DPS());
                         return new Stage(8, "Eisen Wald", 580, 45, ennemis); }
            case 9  -> { ennemis.add(new EnnemiMage9Tank()); ennemis.add(new EnnemiMage7DPS());
                         ennemis.add(new EnnemiEligor());  ennemis.add(new EnnemiMage3Soigneur()); ennemis.add(new EnnemiMage4Buff());
                         return new Stage(9, "Eligor le mage de vent", 680, 50, ennemis); }
            case 10 -> { ennemis.add(new EnnemiLullaby(1));
                         return new Stage(10, "La flute maudite", 860, 55, ennemis); }
            default -> { return new Stage(numero, "???", 0, 0, ennemis); }
        }
    }

    private String getTitreStage(int numero) {
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

    ArrayList<PersonnageBase> equipeOriginale = ctx.formation.getEquipe();
    ArrayList<PersonnageBase> equipeAvecErza = new ArrayList<>();
    equipeAvecErza.add(erzaTemporaire);
    equipeAvecErza.addAll(equipeOriginale);
    
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