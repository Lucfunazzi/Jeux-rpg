package lancement.Chapitres;

import Personnage.PersonnageBase;
import Personnage.pnj.Chapitre3.*;
import lancement.GameContext;
import lancement.Stage;
import java.util.ArrayList;
import java.util.Scanner;

public class Chapitre3 {

    private static final int NB_STAGES = 10;
    private final boolean[] stagesDebloques = new boolean[NB_STAGES + 1];
    private final boolean[] stagesReussis   = new boolean[NB_STAGES + 1];

    public Chapitre3() {
        stagesDebloques[1] = true;
    }

    public void afficher(GameContext ctx, Scanner scanner) {
        boolean retour = false;

        while (!retour) {
            System.out.println("\n========================================");
            System.out.println("   CHAPITRE 3 — Le Tournoi du Tenkaichi");
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
                Stage.ResultatStage resultatStage = stage.lancer(ctx, ctx.formation.getEquipe(), estNouveau);

                if (resultatStage.victoire) {
                    stagesReussis[choix] = true;
                    if (choix < NB_STAGES) {
                        stagesDebloques[choix + 1] = true;
                        System.out.println(">> Stage " + (choix + 1) + " debloque !");
                    } else {
                        System.out.println(">> Vous avez remporte le Tournoi du Tenkaichi !");
                        // Donner la Clef Celeste Taurus (une seule fois)
                        if (estNouveau && ctx.gestionnaireClefsCelestes != null
                                && ctx.gestionnaireClefsCelestes.debloquer(
                                    lancement.Gestionnaires.ClefCeleste.TAURUS)) {
                            System.out.println(">> Vous avez obtenu la Clef Celeste : Taurus !");
                            System.out.println("   Activez-la depuis le menu Clefs Celestes.");
                        }
                    }

                    ctx.gestionnaireQuetes.notifierOrGagne(stage.getRecompenseOr());
                    ctx.gestionnaireQuetes.notifierStageFini(3, choix, false,
                            ctx.joueur, ctx.menuRecrutement, ctx.personnagesRecruites);
                    ctx.gestionnaireEtoiles.mettreAJour(3, choix, false,
                            resultatStage.victoire, resultatStage.sansAllieMort, resultatStage.enMoinsDe10Tours);
                }
            }
        }
    }

    private Stage construireStage(int numero) {
        ArrayList<PersonnageBase> ennemis = new ArrayList<>();
        switch (numero) {
            case 1  -> { ennemis.add(new EnnemiYamchaC3()); ennemis.add(new EnnemiCombattantDBZ1()); ennemis.add(new EnnemiCombattantDBZ1()); ennemis.add(new EnnemiSoigneurDBZ()); ennemis.add(new EnnemiProtecteurDBZ());
                         return new Stage(1, "Qualifications — Premier sang", 6000, 0, ennemis); }
            case 2  -> { ennemis.add(new EnnemiChiaotzuC3()); ennemis.add(new EnnemiTienC3()); ennemis.add(new EnnemiYamchaC3()); ennemis.add(new EnnemiCombattantDBZ1()); ennemis.add(new EnnemiSoigneurDBZ());
                         return new Stage(2, "L'equipe de Chiaotzu", 7500, 0, ennemis); }
            case 3  -> { ennemis.add(new EnnemiNappaC3()); ennemis.add(new EnnemiRaditzC3()); ennemis.add(new EnnemiCombattantDBZ1()); ennemis.add(new EnnemiCombattantDBZ2()); ennemis.add(new EnnemiProtecteurDBZ());
                         return new Stage(3, "Les soldats de Nappa", 9000, 0, ennemis); }
            case 4  -> { ennemis.add(new EnnemiFreezrC3()); ennemis.add(new EnnemiCombattantDBZ2()); ennemis.add(new EnnemiCombattantDBZ2()); ennemis.add(new EnnemiProtecteurDBZ()); ennemis.add(new EnnemiSoigneurDBZ());
                         return new Stage(4, "La terreur de Freezer", 11000, 0, ennemis); }
            case 5  -> { ennemis.add(new EnnemiKrillinC3()); ennemis.add(new EnnemiGohanEnfantC3()); ennemis.add(new EnnemiTienC3()); ennemis.add(new EnnemiSoigneurDBZ()); ennemis.add(new EnnemiCombattantDBZ2());
                         return new Stage(5, "Krillin et ses allies", 13000, 0, ennemis); }
            case 6  -> { ennemis.add(new EnnemiC17C3()); ennemis.add(new EnnemiC18C3()); ennemis.add(new EnnemiCombattantDBZ2()); ennemis.add(new EnnemiProtecteurDBZ()); ennemis.add(new EnnemiSoigneurDBZ());
                         return new Stage(6, "Les Cyborgs et leur garde", 15000, 0, ennemis); }
            case 7  -> { ennemis.add(new EnnemiVegetaC3()); ennemis.add(new EnnemiNappaC3()); ennemis.add(new EnnemiRaditzC3()); ennemis.add(new EnnemiProtecteurDBZ()); ennemis.add(new EnnemiSoigneurDBZ());
                         return new Stage(7, "L'elite Saiyan", 18000, 0, ennemis); }
            case 8  -> { ennemis.add(new EnnemiPiccoloC3()); ennemis.add(new EnnemiKrillinC3()); ennemis.add(new EnnemiGohanEnfantC3()); ennemis.add(new EnnemiProtecteurDBZ()); ennemis.add(new EnnemiCombattantDBZ2());
                         return new Stage(8, "Le demon vert et ses disciples", 21000, 0, ennemis); }
            case 9  -> { ennemis.add(new EnnemiFreezrC3()); ennemis.add(new EnnemiC17C3()); ennemis.add(new EnnemiC18C3()); ennemis.add(new EnnemiSoigneurDBZ()); ennemis.add(new EnnemiProtecteurDBZ());
                         return new Stage(9, "L'armee de Freezer", 25000, 0, ennemis); }
            case 10 -> { ennemis.add(new EnnemiCellC3());
                         return new Stage(10, "Cell — Forme Impure", 30000, 0, ennemis); }
            default -> { return new Stage(numero, "???", 0, 0, ennemis); }
        }
    }

    private String getTitreStage(int numero) {
        return switch (numero) {
            case 1  -> "Qualifications — Premier sang";
            case 2  -> "L'equipe de Chiaotzu";
            case 3  -> "Les soldats de Nappa";
            case 4  -> "La terreur de Freezer";
            case 5  -> "Krillin et ses allies";
            case 6  -> "Les Cyborgs et leur garde";
            case 7  -> "L'elite Saiyan";
            case 8  -> "Le demon vert et ses disciples";
            case 9  -> "L'armee de Freezer";
            case 10 -> "Cell — Forme Impure";
            default -> "???";
        };
    }

    public boolean[] getStagesDebloques() { return stagesDebloques; }
    public boolean[] getStagesReussis()   { return stagesReussis; }
    public void setStagesDebloques(boolean[] d) { for (int i = 0; i <= NB_STAGES; i++) stagesDebloques[i] = d[i]; }
    public void setStagesReussis(boolean[] r)   { for (int i = 0; i <= NB_STAGES; i++) stagesReussis[i]   = r[i]; }
}