package lancement.Chapitres;

import Joueur.Personnage_principale;
import Personnage.PersonnageBase;
import Personnage.pnj.Chapitre1.*;
import Personnage.Naruto_Shippuden.perso_Iruka;
import Personnage.DragonBallZ.perso_Yamcha;
import lancement.GameContext;
import lancement.Stage;
import java.util.ArrayList;
import java.util.Scanner;

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
            System.out.println("         CHAPITRE 1 — L'Eveil");
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

                    if (choix == 3 && !dejaRecruteParNom("Iruka", ctx.personnagesRecruites)) {
                        ctx.personnagesRecruites.add(new perso_Iruka());
                        System.out.println(">> Iruka rejoint vos allies !");
                    }
                    if (choix == 6 && !dejaRecruteParNom("Yamcha", ctx.personnagesRecruites)) {
                        ctx.personnagesRecruites.add(new perso_Yamcha());
                        System.out.println(">> Yamcha rejoint vos allies !");
                    }
                }
            }
        }
    }

    private Stage construireStage(int numero) {
        ArrayList<PersonnageBase> ennemis = new ArrayList<>();

        switch (numero) {
            case 1  -> { ennemis.add(new EnnemiMage1());
                         return new Stage(1, "Le Sentier Maudit", 100, 10, ennemis); }
            case 2  -> { ennemis.add(new EnnemiMage2());
                         return new Stage(2, "L'Ancien Reveil", 150, 15, ennemis); }
            case 3  -> { ennemis.add(new EnnemiIruka());
                         return new Stage(3, "Le Maitre Ninja", 200, 20, ennemis); }
            case 4  -> { ennemis.add(new EnnemiGuerrier1()); ennemis.add(new EnnemiMage2()); ennemis.add(new EnnemiMage2());
                         return new Stage(4, "La Triade Maudite", 280, 25, ennemis); }
            case 5  -> { ennemis.add(new EnnemiGuerrier2()); ennemis.add(new EnnemiMage3()); ennemis.add(new EnnemiMage3());
                         return new Stage(5, "L'Avant-Garde", 340, 30, ennemis); }
            case 6  -> { ennemis.add(new EnnemiYamcha());
                         return new Stage(6, "Le Loup du Desert", 380, 35, ennemis); }
            case 7  -> { ennemis.add(new EnnemiNinja1()); ennemis.add(new EnnemiGuerrier3());
                         ennemis.add(new EnnemiNinja2()); ennemis.add(new EnnemiMage4());
                         return new Stage(7, "Les Quatre Predateurs", 440, 40, ennemis); }
            case 8  -> { ennemis.add(new EnnemiTank1()); ennemis.add(new EnnemiGuerrier4());
                         ennemis.add(new EnnemiGuerrier4()); ennemis.add(new EnnemiSoigneur1()); ennemis.add(new EnnemiSoigneur1());
                         return new Stage(8, "L'Armee des Ombres", 520, 45, ennemis); }
            case 9  -> { ennemis.add(new EnnemiTank2()); ennemis.add(new EnnemiNinja3());
                         ennemis.add(new EnnemiNinja3()); ennemis.add(new EnnemiNinja3()); ennemis.add(new EnnemiSoigneur2());
                         return new Stage(9, "Les Generaux des Tenebres", 620, 50, ennemis); }
            case 10 -> { ennemis.add(new EnnemiTank1()); ennemis.add(new EnnemiNatsu());
                         ennemis.add(new EnnemiNinja3()); ennemis.add(new EnnemiSoigneur1()); ennemis.add(new EnnemiSoigneur2());
                         return new Stage(10, "Le Dragon de Feu", 800, 55, ennemis); }
            default -> { return new Stage(numero, "???", 0, 0, ennemis); }
        }
    }

    private String getTitreStage(int numero) {
        return switch (numero) {
            case 1  -> "Le Sentier Maudit";
            case 2  -> "L'Ancien Reveil";
            case 3  -> "Le Maitre Ninja";
            case 4  -> "La Triade Maudite";
            case 5  -> "L'Avant-Garde";
            case 6  -> "Le Loup du Desert";
            case 7  -> "Les Quatre Predateurs";
            case 8  -> "L'Armee des Ombres";
            case 9  -> "Les Generaux des Tenebres";
            case 10 -> "Le Dragon de Feu";
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
}