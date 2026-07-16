package lancement.Chapitres;

import Personnage.PersonnageBase;
import Personnage.pnj.Chapitre2.*;
import lancement.GameContext;
import lancement.Stage;
import java.util.ArrayList;
import java.util.Scanner;

public class Chapitre2 {

    private static final int NB_STAGES = 10;
    private final boolean[] stagesDebloques = new boolean[NB_STAGES + 1];
    private final boolean[] stagesReussis   = new boolean[NB_STAGES + 1];

    public Chapitre2() {
        stagesDebloques[1] = true;
    }

    public void afficher(GameContext ctx, Scanner scanner) {
        boolean retour = false;

        while (!retour) {
            System.out.println("\n========================================");
            System.out.println("      CHAPITRE 2 — L'Examen Ninja");
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
                        System.out.println(">> Felicitations ! Vous avez termine le Chapitre 2 !");
                        // Cadeau de fin de chapitre 2
                        int deja = ctx.inventaire.getQuantiteMateriau("Echarpe blanche d'Ignir");
                        if (deja < 50) {
                            int aAjouter = 50 - deja;
                            ctx.inventaire.ajouterMateriau("Echarpe blanche d'Ignir", aAjouter);
                            System.out.println(">> Cadeau : +" + aAjouter
                                    + " Echarpe(s) blanche d'Ignir !");
                            System.out.println("   Vous pouvez maintenant recruter Natsu"
                                    + " dans le Recrutement Rare !");
                        }
                    }

                   
                    ctx.gestionnaireQuetes.notifierOrGagne(stage.getRecompenseOr());
                    ctx.gestionnaireQuetes.notifierStageFini(2, choix, false,
                            ctx.joueur, ctx.menuRecrutement, ctx.personnagesRecruites);
                    ctx.gestionnaireEtoiles.mettreAJour(2, choix, false,
                            resultatStage.victoire, resultatStage.sansAllieMort, resultatStage.enMoinsDe10Tours);
                }
            }
        }
    }

    private Stage construireStage(int numero) {
        ArrayList<PersonnageBase> ennemis = new ArrayList<>();
        // recompenseXP = 0 : la montée de niveau passe exclusivement par les quêtes
        switch (numero) {
            case 1  -> { ennemis.add(new EnnemiLee()); ennemis.add(new EnnemiNeji());
                         ennemis.add(new EnnemiTenten()); ennemis.add(new EnnemiNinja1()); ennemis.add(new EnnemiSoigneur1());
                         return new Stage(1, "L'equipe Gai entre en action !", 1500, 0, ennemis); }
            case 2  -> { ennemis.add(new EnnemiKiba()); ennemis.add(new EnnemiHinata());
                         ennemis.add(new EnnemiShino()); ennemis.add(new EnnemiNinja1()); ennemis.add(new EnnemiSoigneur2());
                         return new Stage(2, "L'Equipe 8 entre en action !", 1800, 0, ennemis); }
            case 3  -> { ennemis.add(new EnnemiChoji()); ennemis.add(new EnnemiIno());
                         ennemis.add(new EnnemiShikamaru()); ennemis.add(new EnnemiNinja2()); ennemis.add(new EnnemiNinja1());
                         return new Stage(3, "L'Equipe 10 entre en action !", 2100, 0, ennemis); }
            case 4  -> { ennemis.add(new EnnemiNaruto()); ennemis.add(new EnnemiSakura());
                         ennemis.add(new EnnemiSasuke()); ennemis.add(new EnnemiSoigneur1()); ennemis.add(new EnnemiNinja1());
                         return new Stage(4, "L'Equipe 7 entre en action !", 2500, 0, ennemis); }
            case 5  -> { ennemis.add(new EnnemiZabusa()); ennemis.add(new EnnemiHaku());
                         ennemis.add(new EnnemiTank1()); ennemis.add(new EnnemiSoigneur1()); ennemis.add(new EnnemiSoigneur2());
                         return new Stage(5, "Interlude — Zabuza et Haku !", 2800, 0, ennemis); }
            case 6  -> { ennemis.add(new EnnemiGaara()); ennemis.add(new EnnemiTemari());
                         ennemis.add(new EnnemiKankuro()); ennemis.add(new EnnemiNinja1()); ennemis.add(new EnnemiSoigneur2());
                         return new Stage(6, "L'equipe de Suna entre en action !", 3200, 0, ennemis); }
            case 7  -> { ennemis.add(new EnnemiLee()); ennemis.add(new EnnemiNeji());
                         ennemis.add(new EnnemiTenten()); ennemis.add(new EnnemiGai()); ennemis.add(new EnnemiSoigneur1());
                         return new Stage(7, "Equipe Gai — La Revanche !", 3800, 0, ennemis); }
            case 8  -> { ennemis.add(new EnnemiChoji()); ennemis.add(new EnnemiIno());
                         ennemis.add(new EnnemiShikamaru()); ennemis.add(new EnnemiAsuma()); ennemis.add(new EnnemiNinja1());
                         return new Stage(8, "Equipe 10 — Second Match !", 4200, 0, ennemis); }
            case 9  -> { ennemis.add(new EnnemiHinata()); ennemis.add(new EnnemiShino());
                         ennemis.add(new EnnemiKiba()); ennemis.add(new EnnemiKurenai()); ennemis.add(new EnnemiNinja2());
                         return new Stage(9, "Equipe 8 — Second Match !", 4500, 0, ennemis); }
            case 10 -> { ennemis.add(new EnnemiOrochimaru()); ennemis.add(new EnnemiKabuto());
                         ennemis.add(new EnnemiTank1()); ennemis.add(new EnnemiNinja2()); ennemis.add(new EnnemiSoigneur1());
                         return new Stage(10, "Orochimaru — Le Ninja Legendaire !", 5000, 0, ennemis); }
            default -> { return new Stage(numero, "???", 0, 0, ennemis); }
        }
    }

    private String getTitreStage(int numero) {
        return switch (numero) {
            case 1  -> "L'equipe Gai entre en action !";
            case 2  -> "L'Equipe 8 — Kiba, Hinata, Shino";
            case 3  -> "L'Equipe 10 — Choji, Ino, Shikamaru";
            case 4  -> "L'Equipe 7 — Naruto, Sakura, Sasuke";
            case 5  -> "Interlude — Zabuza et Haku";
            case 6  -> "L'equipe de Suna";
            case 7  -> "Equipe Gai — La Revanche";
            case 8  -> "Equipe 10 — Second Match";
            case 9  -> "Equipe 8 — Second Match";
            case 10 -> "Orochimaru — Le Ninja Legendaire !";
            default -> "???";
        };
    }

    public boolean[] getStagesDebloques() { return stagesDebloques; }
    public boolean[] getStagesReussis()   { return stagesReussis; }
    public void setStagesDebloques(boolean[] d) { for (int i = 0; i <= NB_STAGES; i++) stagesDebloques[i] = d[i]; }
    public void setStagesReussis(boolean[] r)   { for (int i = 0; i <= NB_STAGES; i++) stagesReussis[i]   = r[i]; }
}