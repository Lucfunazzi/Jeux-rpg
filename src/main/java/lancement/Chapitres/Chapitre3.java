package lancement.Chapitres;

import Personnage.PersonnageBase;
import Personnage.pnj.Chapitre1.EnnemiMage1;
import Personnage.pnj.Chapitre1.EnnemiMage2;
import Personnage.pnj.Chapitre1.EnnemiMage3;
import Personnage.pnj.Chapitre1.EnnemiGuerrier1;
import Personnage.pnj.Chapitre1.EnnemiGuerrier2;
import Personnage.pnj.Chapitre1.EnnemiTank1;
import Personnage.pnj.Chapitre1.EnnemiSoigneur1;
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
            System.out.println("   CHAPITRE 3 — Phantom Lord");
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
                        System.out.println(">> Félicitations ! Vous avez vaincu Phantom Lord !");
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
            case 1  -> { ennemis.add(new EnnemiMage1()); ennemis.add(new EnnemiGuerrier1()); ennemis.add(new EnnemiMage2());
                         return new Stage(1, "L'assaut de Phantom Lord", 6000, 0, ennemis); }
            case 2  -> { ennemis.add(new EnnemiTotomaru()); ennemis.add(new EnnemiMage2()); ennemis.add(new EnnemiGuerrier2());
                         return new Stage(2, "Totomaru — Sept Flammes", 7500, 0, ennemis); }
            case 3  -> { ennemis.add(new EnnemiSol()); ennemis.add(new EnnemiGuerrier1()); ennemis.add(new EnnemiTank1()); ennemis.add(new EnnemiMage2());
                         return new Stage(3, "Sol — L'Impénétrable", 9500, 0, ennemis); }
            case 4  -> { ennemis.add(new EnnemiTotomaru()); ennemis.add(new EnnemiSol());
                         ennemis.add(new EnnemiMage2()); ennemis.add(new EnnemiGuerrier2());
                         return new Stage(4, "L'Élément 4 se déploie", 11500, 0, ennemis); }
            case 5  -> { ennemis.add(new EnnemiJubia_4elements()); ennemis.add(new EnnemiMage3()); ennemis.add(new EnnemiGuerrier2()); ennemis.add(new EnnemiSoigneur1());
                         return new Stage(5, "Jubia — L'Eau qui emprisonne", 13500, 0, ennemis); }
            case 6  -> { ennemis.add(new EnnemiJubia_4elements()); ennemis.add(new EnnemiTotomaru());
                         ennemis.add(new EnnemiSol()); ennemis.add(new EnnemiMage2()); ennemis.add(new EnnemiGuerrier1());
                         return new Stage(6, "L'Élément 4 au complet", 16000, 0, ennemis); }
            case 7  -> { ennemis.add(new EnnemiAria()); ennemis.add(new EnnemiMage3());
                         ennemis.add(new EnnemiGuerrier2()); ennemis.add(new EnnemiTank1()); ennemis.add(new EnnemiSoigneur1());
                         return new Stage(7, "Aria — Magie du Ciel Vide", 19000, 0, ennemis); }
            case 8  -> { ennemis.add(new EnnemiAria()); ennemis.add(new EnnemiJubia_4elements());
                         ennemis.add(new EnnemiTotomaru()); ennemis.add(new EnnemiSol()); ennemis.add(new EnnemiMage2());
                         return new Stage(8, "L'Élément 4 — Dernière résistance", 22500, 0, ennemis); }
            case 9  -> { ennemis.add(new EnnemiJose()); ennemis.add(new EnnemiAria());
                         ennemis.add(new EnnemiMage3()); ennemis.add(new EnnemiGuerrier2()); ennemis.add(new EnnemiSoigneur1());
                         return new Stage(9, "José — L'Ombre s'éveille", 27000, 0, ennemis); }
            case 10 -> { ennemis.add(new EnnemiJose());
                         return new Stage(10, "José Porla — Maître de Phantom Lord", 34000, 0, ennemis); }
            default -> { return new Stage(numero, "???", 0, 0, ennemis); }
        }
    }

    private String getTitreStage(int numero) {
        return switch (numero) {
            case 1  -> "L'assaut de Phantom Lord";
            case 2  -> "Totomaru — Sept Flammes";
            case 3  -> "Sol — L'Impénétrable";
            case 4  -> "L'Élément 4 se déploie";
            case 5  -> "Jubia — L'Eau qui emprisonne";
            case 6  -> "L'Élément 4 au complet";
            case 7  -> "Aria — Magie du Ciel Vide";
            case 8  -> "L'Élément 4 — Dernière résistance";
            case 9  -> "José — L'Ombre s'éveille";
            case 10 -> "José Porla — Maître de Phantom Lord";
            default -> "???";
        };
    }

    public boolean[] getStagesDebloques() { return stagesDebloques; }
    public boolean[] getStagesReussis()   { return stagesReussis; }
    public void setStagesDebloques(boolean[] d) { for (int i = 0; i <= NB_STAGES; i++) stagesDebloques[i] = d[i]; }
    public void setStagesReussis(boolean[] r)   { for (int i = 0; i <= NB_STAGES; i++) stagesReussis[i]   = r[i]; }
}