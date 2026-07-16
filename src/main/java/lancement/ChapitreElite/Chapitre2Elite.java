package lancement.ChapitreElite;

import Personnage.PersonnageBase;
import Personnage.pnj.Chapitre2Elite.*;
import Equipement.EquipementFactory;
import Equipement.Equipement;
import lancement.GameContext;
import lancement.Stage;
import lancement.Chapitres.Chapitre1;
import lancement.Chapitres.Chapitre2;
import lancement.ChapitreElite.Chapitre1Elite;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class Chapitre2Elite {

    private static final int NB_STAGES = 10;
    private final boolean[] stagesDebloques = new boolean[NB_STAGES + 1];
    private final boolean[] stagesReussis   = new boolean[NB_STAGES + 1];

    private final Chapitre1      chapitre1;
    private final Chapitre2      chapitre2;
    private final Chapitre1Elite chapitre1Elite;

    public Chapitre2Elite(Chapitre1 chapitre1, Chapitre2 chapitre2, Chapitre1Elite chapitre1Elite) {
        this.chapitre1      = chapitre1;
        this.chapitre2      = chapitre2;
        this.chapitre1Elite = chapitre1Elite;
        stagesDebloques[1]  = true;
    }

    // ── Condition de déblocage ────────────────────────────────────────────
    public boolean estDebloque() {
        return chapitre1.getStagesReussis()[10]
            && chapitre2.getStagesReussis()[10]
            && chapitre1Elite.getStagesReussis()[10];
    }

    public void afficher(GameContext ctx, Scanner scanner) {
        if (!estDebloque()) {
            System.out.println("Terminez le Chapitre 1, le Chapitre 2 et le Chapitre 1 Elite pour débloquer le Chapitre 2 Elite !");
            return;
        }

        boolean retour = false;

        while (!retour) {
            ctx.gestionnaireEnergie.mettreAJourRecharge();

            System.out.println("\n========================================");
            System.out.println("    CHAPITRE 2 ELITE — L'Examen Ninja");
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
                boolean victoire = resultatStage.victoire;

                if (victoire) {
                    stagesReussis[choix] = true;
                    if (choix < NB_STAGES) {
                        stagesDebloques[choix + 1] = true;
                        System.out.println(">> Stage " + (choix + 1) + " debloque !");
                    } else {
                        System.out.println(">> Félicitations ! Vous avez terminé le Chapitre 2 Elite !");
                        // Débloquer l'arbre 2 des abilités
                        ctx.joueur.getArbreCompetences().setArbre2Debloque(true);
                        System.out.println(">> L'Arbre 2 des Abilités est maintenant accessible !");
                        // Donner l'Oeuf de Créature Sacrée (une seule fois)
                        if (estNouveau && !ctx.gestionnaireCreaturesSacrees.isOeufDebloque()) {
                            ctx.gestionnaireCreaturesSacrees.debloquerOeuf();
                            System.out.println(">> Vous avez obtenu un Oeuf de Creature Sacree !");
                            System.out.println("   Accessible depuis le menu Creatures Sacrees (niveau 30 requis).");
                        }
                        // Donner la Clef Celeste Virgo (une seule fois)
                        if (estNouveau && ctx.gestionnaireClefsCelestes != null
                                && ctx.gestionnaireClefsCelestes.debloquer(
                                    lancement.Gestionnaires.ClefCeleste.VIRGO)) {
                            System.out.println(">> Vous avez obtenu la Clef Celeste : Virgo !");
                            System.out.println("   Activez-la depuis le menu Clefs Celestes.");
                        }
                    }

                    ctx.gestionnaireQuetes.notifierOrGagne(stage.getRecompenseOr());
                    ctx.gestionnaireQuetes.notifierStageFini(2, choix, true,
                            ctx.joueur, ctx.menuRecrutement, ctx.personnagesRecruites);
                    ctx.gestionnaireEtoiles.mettreAJour(2, choix, true,
                            resultatStage.victoire, resultatStage.sansAllieMort, resultatStage.enMoinsDe10Tours);
                }
            }
        }
    }

    private Stage construireStage(int numero, GameContext ctx) {
        ArrayList<PersonnageBase> ennemis = new ArrayList<>();
        switch (numero) {
            case 1  -> { ennemis.add(new EnnemiLeeElite()); ennemis.add(new EnnemiNejiElite());
                         ennemis.add(new EnnemiTentenElite()); ennemis.add(new EnnemiNinja1Elite()); ennemis.add(new EnnemiSoigneur1Elite());
                         return new Stage(1, "[ELITE] L'equipe Gai entre en action !", 6000, 0, ennemis); }
            case 2  -> { ennemis.add(new EnnemiKibaElite()); ennemis.add(new EnnemiHinataElite());
                         ennemis.add(new EnnemiShinoElite()); ennemis.add(new EnnemiNinja1Elite()); ennemis.add(new EnnemiSoigneur2Elite());
                         return new Stage(2, "[ELITE] L'Equipe 8 entre en action !", 7200, 0, ennemis); }
            case 3  -> { ennemis.add(new EnnemiChojiElite()); ennemis.add(new EnnemiInoElite());
                         ennemis.add(new EnnemiShikamaruElite()); ennemis.add(new EnnemiNinja2Elite()); ennemis.add(new EnnemiNinja1Elite());
                         return new Stage(3, "[ELITE] L'Equipe 10 entre en action !", 8400, 0, ennemis); }
            case 4  -> { ennemis.add(new EnnemiNarutoElite()); ennemis.add(new EnnemiSakuraElite());
                         ennemis.add(new EnnemiSasukeElite()); ennemis.add(new EnnemiSoigneur1Elite()); ennemis.add(new EnnemiNinja1Elite());
                         return new Stage(4, "[ELITE] L'Equipe 7 entre en action !", 10000, 0, ennemis); }
            case 5  -> { ennemis.add(new EnnemiZabuzaElite()); ennemis.add(new EnnemiHakuElite());
                         ennemis.add(new EnnemiTank1Elite()); ennemis.add(new EnnemiSoigneur1Elite()); ennemis.add(new EnnemiSoigneur2Elite());
                         return new Stage(5, "[ELITE] Interlude — Zabuza et Haku !", 11200, 0, ennemis); }
            case 6  -> { ennemis.add(new EnnemiGaaraElite()); ennemis.add(new EnnemiTemariElite());
                         ennemis.add(new EnnemiKankuroElite()); ennemis.add(new EnnemiNinja1Elite()); ennemis.add(new EnnemiSoigneur2Elite());
                         return new Stage(6, "[ELITE] L'equipe de Suna entre en action !", 12800, 0, ennemis); }
            case 7  -> { ennemis.add(new EnnemiLeeElite()); ennemis.add(new EnnemiNejiElite());
                         ennemis.add(new EnnemiTentenElite()); ennemis.add(new EnnemiGaiElite()); ennemis.add(new EnnemiSoigneur1Elite());
                         return new Stage(7, "[ELITE] Equipe Gai — La Revanche !", 15200, 0, ennemis); }
            case 8  -> { ennemis.add(new EnnemiChojiElite()); ennemis.add(new EnnemiInoElite());
                         ennemis.add(new EnnemiShikamaruElite()); ennemis.add(new EnnemiAsumaElite()); ennemis.add(new EnnemiNinja1Elite());
                         return new Stage(8, "[ELITE] Equipe 10 — Second Match !", 16800, 0, ennemis); }
            case 9  -> { ennemis.add(new EnnemiHinataElite()); ennemis.add(new EnnemiShinoElite());
                         ennemis.add(new EnnemiKibaElite()); ennemis.add(new EnnemiKurenaiElite()); ennemis.add(new EnnemiNinja2Elite());
                         return new Stage(9, "[ELITE] Equipe 8 — Second Match !", 18000, 0, ennemis); }
            case 10 -> { ennemis.add(new EnnemiOrochimaruElite()); ennemis.add(new EnnemiKabutoElite());
                         ennemis.add(new EnnemiTank1Elite()); ennemis.add(new EnnemiNinja2Elite()); ennemis.add(new EnnemiSoigneur1Elite());
                         return new Stage(10, "[ELITE] Orochimaru — Le Ninja Legendaire !", 20000, 0, ennemis); }
            default -> { return new Stage(numero, "???", 0, 0, ennemis); }
        }
    }

    private String getTitreStage(int numero) {
        return switch (numero) {
            case 1  -> "[ELITE] L'equipe Gai entre en action !";
            case 2  -> "[ELITE] L'Equipe 8 — Kiba, Hinata, Shino";
            case 3  -> "[ELITE] L'Equipe 10 — Choji, Ino, Shikamaru";
            case 4  -> "[ELITE] L'Equipe 7 — Naruto, Sakura, Sasuke";
            case 5  -> "[ELITE] Interlude — Zabuza et Haku";
            case 6  -> "[ELITE] L'equipe de Suna";
            case 7  -> "[ELITE] Equipe Gai — La Revanche";
            case 8  -> "[ELITE] Equipe 10 — Second Match";
            case 9  -> "[ELITE] Equipe 8 — Second Match";
            case 10 -> "[ELITE] Orochimaru — Le Ninja Legendaire !";
            default -> "???";
        };
    }

    public boolean[] getStagesDebloques() { return stagesDebloques; }
    public boolean[] getStagesReussis()   { return stagesReussis; }
    public void setStagesDebloques(boolean[] d) { for (int i = 0; i <= NB_STAGES; i++) stagesDebloques[i] = d[i]; }
    public void setStagesReussis(boolean[] r)   { for (int i = 0; i <= NB_STAGES; i++) stagesReussis[i]   = r[i]; }
}