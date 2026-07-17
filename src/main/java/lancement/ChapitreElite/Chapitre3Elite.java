package lancement.ChapitreElite;

import Personnage.PersonnageBase;
import Personnage.pnj.Chapitre3Elite.*;
import Equipement.CarteOr;
import lancement.GameContext;
import lancement.Stage;
import lancement.Chapitres.Chapitre3;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Chapitre3Elite {

    private static final int NB_STAGES = 10;
    private static final double CHANCE_CARTE_OR_REPLAY = 0.30;

    private final boolean[] stagesDebloques = new boolean[NB_STAGES + 1];
    private final boolean[] stagesReussis   = new boolean[NB_STAGES + 1];
    private final boolean[] premiereVictoire = new boolean[NB_STAGES + 1];

    private final Chapitre3      chapitre3;
    private final Chapitre2Elite chapitre2Elite;

    public Chapitre3Elite(Chapitre3 chapitre3, Chapitre2Elite chapitre2Elite) {
        this.chapitre3      = chapitre3;
        this.chapitre2Elite = chapitre2Elite;
        stagesDebloques[1]  = true;
    }

    // ── Condition de déblocage ────────────────────────────────────────────
    public boolean estDebloque() {
        return chapitre3.getStagesReussis()[10]
            && chapitre2Elite.getStagesReussis()[10];
    }

    public void afficher(GameContext ctx, Scanner scanner) {
        if (!estDebloque()) {
            System.out.println("Terminez le Chapitre 3 et le Chapitre 2 Elite pour debloquer le Chapitre 3 Elite !");
            return;
        }

        boolean retour = false;

        while (!retour) {
            ctx.gestionnaireEnergie.mettreAJourRecharge();

            System.out.println("\n========================================");
            System.out.println("   CHAPITRE 3 ELITE — Le Tournoi Ultime");
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

                if (resultatStage.victoire) {
                    stagesReussis[choix] = true;

                    if (choix < NB_STAGES) {
                        stagesDebloques[choix + 1] = true;
                        System.out.println(">> Stage " + (choix + 1) + " debloque !");
                    } else {
                        System.out.println(">> Felicitations ! Vous avez termine le Chapitre 3 Elite !");
                    }

                    // ── Récompenses Carte d'Or Lv.2 ──────────────────────
                    if (estNouveau && !premiereVictoire[choix]) {
                        premiereVictoire[choix] = true;
                        int ajoute = ctx.inventaire.ajouterCartesOr(CarteOr.NIVEAU_2, 10);
                        System.out.println("   + " + ajoute + "x " + CarteOr.NIVEAU_2.nom
                                + " (premiere victoire) !");
                    } else {
                        if (Math.random() < CHANCE_CARTE_OR_REPLAY) {
                            int ajoute = ctx.inventaire.ajouterCartesOr(CarteOr.NIVEAU_2, 1);
                            if (ajoute > 0)
                                System.out.println("   + 1x " + CarteOr.NIVEAU_2.nom + " !");
                        }
                    }

                    ctx.gestionnaireQuetes.notifierOrGagne(stage.getRecompenseOr());
                    ctx.gestionnaireQuetes.notifierStageFini(3, choix, true,
                            ctx.joueur, ctx.menuRecrutement, ctx.personnagesRecruites);
                    ctx.gestionnaireEtoiles.mettreAJour(3, choix, true,
                            resultatStage.victoire, resultatStage.sansAllieMort, resultatStage.enMoinsDe10Tours);

                    ctx.sauvegarde.sauvegarder(ctx);
                }
            }
        }
    }

    private Stage construireStage(int numero, GameContext ctx) {
        ArrayList<PersonnageBase> ennemis = new ArrayList<>();
        switch (numero) {
            case 1  -> { ennemis.add(new EnnemiYamchaC3Elite()); ennemis.add(new EnnemiChiaotzuC3Elite());
                         ennemis.add(new EnnemiTienC3Elite()); ennemis.add(new EnnemiCombattantDBZElite()); ennemis.add(new EnnemiSoigneurDBZElite());
                         return new Stage(1, "[ELITE] Qualifications — La Garde du Tournoi", 9000, 0, ennemis); }
            case 2  -> { ennemis.add(new EnnemiChiaotzuC3Elite()); ennemis.add(new EnnemiTienC3Elite());
                         ennemis.add(new EnnemiYamchaC3Elite()); ennemis.add(new EnnemiCombattantDBZElite()); ennemis.add(new EnnemiProtecteurDBZElite());
                         return new Stage(2, "[ELITE] L'equipe de Chiaotzu Renforcee", 11000, 0, ennemis); }
            case 3  -> { ennemis.add(new EnnemiNappaC3Elite()); ennemis.add(new EnnemiRaditzC3Elite());
                         ennemis.add(new EnnemiCombattantDBZElite()); ennemis.add(new EnnemiCombattantDBZElite()); ennemis.add(new EnnemiProtecteurDBZElite());
                         return new Stage(3, "[ELITE] Les Soldats Saiyan d'Elite", 13500, 0, ennemis); }
            case 4  -> { ennemis.add(new EnnemiFreezrC3Elite()); ennemis.add(new EnnemiCombattantDBZElite());
                         ennemis.add(new EnnemiCombattantDBZElite()); ennemis.add(new EnnemiProtecteurDBZElite()); ennemis.add(new EnnemiSoigneurDBZElite());
                         return new Stage(4, "[ELITE] La Terreur de Freezer Transcende", 16000, 0, ennemis); }
            case 5  -> { ennemis.add(new EnnemiKrillinC3Elite()); ennemis.add(new EnnemiGohanEnfantC3Elite());
                         ennemis.add(new EnnemiTienC3Elite()); ennemis.add(new EnnemiSoigneurDBZElite()); ennemis.add(new EnnemiCombattantDBZElite());
                         return new Stage(5, "[ELITE] Krillin et ses Allies d'Elite", 19000, 0, ennemis); }
            case 6  -> { ennemis.add(new EnnemiC17C3Elite()); ennemis.add(new EnnemiC18C3Elite());
                         ennemis.add(new EnnemiCombattantDBZElite()); ennemis.add(new EnnemiProtecteurDBZElite()); ennemis.add(new EnnemiSoigneurDBZElite());
                         return new Stage(6, "[ELITE] Les Cyborgs Reprogrammes", 22000, 0, ennemis); }
            case 7  -> { ennemis.add(new EnnemiVegetaC3Elite()); ennemis.add(new EnnemiNappaC3Elite());
                         ennemis.add(new EnnemiRaditzC3Elite()); ennemis.add(new EnnemiProtecteurDBZElite()); ennemis.add(new EnnemiSoigneurDBZElite());
                         return new Stage(7, "[ELITE] L'Armee Saiyan d'Elite", 26000, 0, ennemis); }
            case 8  -> { ennemis.add(new EnnemiPiccoloC3Elite()); ennemis.add(new EnnemiKrillinC3Elite());
                         ennemis.add(new EnnemiGohanEnfantC3Elite()); ennemis.add(new EnnemiProtecteurDBZElite()); ennemis.add(new EnnemiCombattantDBZElite());
                         return new Stage(8, "[ELITE] Le Demon Vert et ses Disciples d'Elite", 30000, 0, ennemis); }
            case 9  -> { ennemis.add(new EnnemiFreezrC3Elite()); ennemis.add(new EnnemiC17C3Elite());
                         ennemis.add(new EnnemiC18C3Elite()); ennemis.add(new EnnemiSoigneurDBZElite()); ennemis.add(new EnnemiProtecteurDBZElite());
                         return new Stage(9, "[ELITE] L'Armee Inarretable d'Elite", 35000, 0, ennemis); }
            case 10 -> { ennemis.add(new EnnemiCellC3Elite());
                         return new Stage(10, "[ELITE] Cell Parfait — La Forme Ultime", 42000, 0, ennemis); }
            default -> { return new Stage(numero, "???", 0, 0, ennemis); }
        }
    }

    private String getTitreStage(int numero) {
        return switch (numero) {
            case 1  -> "[ELITE] Qualifications — La Garde du Tournoi";
            case 2  -> "[ELITE] L'equipe de Chiaotzu Renforcee";
            case 3  -> "[ELITE] Les Soldats Saiyan d'Elite";
            case 4  -> "[ELITE] La Terreur de Freezer Transcende";
            case 5  -> "[ELITE] Krillin et ses Allies d'Elite";
            case 6  -> "[ELITE] Les Cyborgs Reprogrammes";
            case 7  -> "[ELITE] L'Armee Saiyan d'Elite";
            case 8  -> "[ELITE] Le Demon Vert et ses Disciples d'Elite";
            case 9  -> "[ELITE] L'Armee Inarretable d'Elite";
            case 10 -> "[ELITE] Cell Parfait — La Forme Ultime";
            default -> "???";
        };
    }

    public boolean[] getStagesDebloques()  { return stagesDebloques; }
    public boolean[] getStagesReussis()    { return stagesReussis; }
    public boolean[] getPremiereVictoire() { return premiereVictoire; }
    public void setStagesDebloques(boolean[] d)  { for (int i = 0; i <= NB_STAGES; i++) stagesDebloques[i]  = d[i]; }
    public void setStagesReussis(boolean[] r)    { for (int i = 0; i <= NB_STAGES; i++) stagesReussis[i]    = r[i]; }
    public void setPremiereVictoire(boolean[] p) { for (int i = 0; i <= NB_STAGES; i++) premiereVictoire[i] = p[i]; }
}