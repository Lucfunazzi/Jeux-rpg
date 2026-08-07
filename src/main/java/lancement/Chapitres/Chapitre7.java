package lancement.Chapitres;

import Equipement.EquipementFactory;
import Personnage.PersonnageBase;
import Personnage.FairyTail.perso_Gray;
import Personnage.FairyTail.perso_Lucy;
import Personnage.FairyTail.perso_Natsu;
import Personnage.FairyTail.perso_Gajeel;
import Personnage.FairyTail.perso_Wendy;
import lancement.GameContext;
import lancement.Formation;
import lancement.Stage;
import java.util.ArrayList;
import java.util.Scanner;
import Personnage.pnj.EnnemisGeneriques.*;
import Personnage.pnj.Chapitre7.*;

public class Chapitre7 implements Chapitre {

    private static final int NB_STAGES = 10;
    private final boolean[] stagesDebloques = new boolean[NB_STAGES + 1];
    private final boolean[] stagesReussis   = new boolean[NB_STAGES + 1];

    public Chapitre7() {
        stagesDebloques[1] = true;
    }

    public void afficher(GameContext ctx, Scanner scanner) {
        boolean retour = false;

        while (!retour) {
            ctx.gestionnaireEnergie.mettreAJourRecharge();

            System.out.println("\n========================================");
            System.out.println("   CHAPITRE 7 — " + getNomChapitre());
            System.out.println("========================================");
            System.out.println("Or : " + String.format("%.0f", ctx.joueur.getOr())
                    + "  |  " + ctx.gestionnaireEnergie.afficherEnergie());
            System.out.println();

            for (int i = 1; i <= NB_STAGES; i++) {
                boolean jouable = ctx.gestionnaireQuetes.estStageJouable(7, i, false);
                String etat = stagesReussis[i] ? "[OK]  "
                        : !stagesDebloques[i]  ? "[###] "
                        : jouable              ? "[  ]  "
                        :                        "[QUETE] ";
                String etoiles = ctx.gestionnaireEtoiles.getEtoiles(7, i, false).afficher();
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
            } else if (!ctx.gestionnaireQuetes.estStageJouable(7, choix, false)) {
                System.out.println("Acceptez d'abord la quete associee a ce stage (menu Quetes).");
            } else {
                lancerStage(ctx, choix);
            }
        }
    }

    /**
     * Lance le stage donne (avec les invites temporaires des stages 4, 5, 6, 7 et 9) et applique
     * les recompenses en cas de victoire. Suppose que le stage est deja debloque. Reutilisable par
     * la console et l'interface graphique.
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
        Stage.ResultatStage resultatStage = switch (numero) {
            // Stage 4 : Gray prete main forte contre Sugarboy.
            case 4 -> {
                perso_Gray invite = Formation.creerInvite(perso_Gray::new,
                        CourbeChapitres.niveauEnnemiPourStage(7, 4), 6900, 1450, 700, 285);
                yield lancerStageAvecInvites(ctx, stage, estNouveau, invite);
            }
            // Stage 5 : Natsu et Lucy rejoignent l'assaut contre Hughes.
            case 5 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(7, 5);
                perso_Lucy lucy   = Formation.creerInvite(perso_Lucy::new, niv, 7400, 1150, 620, 310);
                perso_Natsu natsu = Formation.creerInvite(perso_Natsu::new, niv, 7700, 1600, 740, 300);
                yield lancerStageAvecInvites(ctx, stage, estNouveau, lucy, natsu);
            }
            // Stage 6 : Lucy affronte Byro aux cotes de l'equipe.
            case 6 -> {
                perso_Lucy invite = Formation.creerInvite(perso_Lucy::new,
                        CourbeChapitres.niveauEnnemiPourStage(7, 6), 7500, 1180, 630, 312);
                yield lancerStageAvecInvites(ctx, stage, estNouveau, invite);
            }
            // Stage 7 : Gajeel affronte Panther Lily.
            case 7 -> {
                perso_Gajeel invite = Formation.creerInvite(perso_Gajeel::new,
                        CourbeChapitres.niveauEnnemiPourStage(7, 7), 7300, 1400, 730, 245);
                yield lancerStageAvecInvites(ctx, stage, estNouveau, invite);
            }
            // Stage 9 : Wendy, Natsu et Gajeel se joignent a l'assaut final contre Dorma Anim.
            case 9 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(7, 9);
                perso_Wendy wendy   = Formation.creerInvite(perso_Wendy::new, niv, 6800, 700, 600, 300);
                perso_Natsu natsu   = Formation.creerInvite(perso_Natsu::new, niv, 7850, 1630, 750, 303);
                perso_Gajeel gajeel = Formation.creerInvite(perso_Gajeel::new, niv, 7450, 1425, 745, 248);
                yield lancerStageAvecInvites(ctx, stage, estNouveau, wendy, natsu, gajeel);
            }
            default -> stage.lancer(ctx, ctx.formation.getEquipe(), estNouveau);
        };

        if (resultatStage.victoire) {
            stagesReussis[numero] = true;
            if (numero < NB_STAGES) {
                stagesDebloques[numero + 1] = true;
                System.out.println(">> Stage " + (numero + 1) + " debloque !");
            } else {
                System.out.println(">> Felicitations ! Chapitre 7 termine !");
            }

            ctx.gestionnaireQuetes.notifierOrGagne(stage.getRecompenseOr());
            ctx.gestionnaireQuetes.notifierStageFini(7, numero, false,
                    ctx.joueur, ctx.menuRecrutement, ctx.personnagesRecruites);
            ctx.gestionnaireEtoiles.mettreAJour(7, numero, false,
                    resultatStage.victoire, resultatStage.sansAllieMort, resultatStage.enMoinsDe10Tours);
        }
        return resultatStage;
    }

    private Stage construireStage(int numero) {
        ArrayList<PersonnageBase> e = new ArrayList<>();

        return switch (numero) {
            // Stage 1 — equipe generique : 1 tank, 2 dps, 2 supports.
            case 1 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(7, 1);
                e.add(new EnnemiMage5Tank(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage6Debuff(Variante.CHAPITRE_7, niv));
                yield new Stage(1, "Prologue Edolas", 13500, 0, e);
            }
            // Stage 2 — equipe generique : 1 tank, 3 dps, 1 support.
            case 2 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(7, 2);
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage7DPS(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_7, niv));
                yield new Stage(2, "Magie Limité", 13800, 0, e);
            }
            // Stage 3 — Erza Knightwalker + 3 dps + 1 support generiques.
            case 3 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(7, 3);
                e.add(new EnnemiErzaKnightwalker(niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage7DPS(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_7, niv));
                yield new Stage(3, "La chasseuses de fées", 14100, 0, e);
            }
            // Stage 4 — Gray invite vs Sugarboy + 2 dps + 2 supports generiques.
            case 4 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(7, 4);
                e.add(new EnnemiSugarboy(niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage6Debuff(Variante.CHAPITRE_7, niv));
                yield new Stage(4, "Gray contre sugarBoy", 14400, 0, e);
            }
            // Stage 5 — Lucy et Natsu invites vs Hughes + 1 tank + 2 dps + 2 supports generiques.
            case 5 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(7, 5);
                e.add(new EnnemiHughes(niv));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage8DPS(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage6Debuff(Variante.CHAPITRE_7, niv));
                yield new Stage(5, "Natsu et Lucy vs huges", 14700, 0, e);
            }
            // Stage 6 — Lucy invitee vs Byro + 1 tank + 3 dps generiques.
            case 6 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(7, 6);
                e.add(new EnnemiByro(niv));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage8DPS(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage7DPS(Variante.CHAPITRE_7, niv));
                yield new Stage(6, "Lucy contre byro", 15000, 0, e);
            }
            // Stage 7 — Gajeel invite vs Panther Lily + 3 supports generiques.
            case 7 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(7, 7);
                e.add(new EnnemiPantherLily(niv));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage6Debuff(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_7, niv));
                yield new Stage(7, "Gajeel vs Panthère Lilly", 15300, 0, e);
            }
            // Stage 8 — Dorma Anim + 1 dps + 3 supports generiques, sans invite.
            case 8 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(7, 8);
                e.add(new EnnemiDormaAnim(niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage6Debuff(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_7, niv));
                yield new Stage(8, "Le roi d'edolas rentre en jeu", 15600, 0, e);
            }
            // Stage 9 — Wendy, Natsu et Gajeel invites vs Dorma Anim + 3 dps + 1 support generiques.
            case 9 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(7, 9);
                e.add(new EnnemiDormaAnim(niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage7DPS(Variante.CHAPITRE_7, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_7, niv));
                yield new Stage(9, "Natsu et Gajeel et wendy vs Le roi d'edolas", 15900, 0, e);
            }
            // Stage 10 — Dorma Anim boosté, seul contre toute l'equipe : le combat le plus dur du chapitre.
            case 10 -> {
                e.add(new EnnemiDormaAnim(CourbeChapitres.niveauEnnemiPourStage(7, 10) + 8));
                yield new Stage(10, "Le dernier combat", 16500, 0, e);
            }
            default -> new Stage(numero, "???", 0, 0, e);
        };
    }

    public String getTitreStage(int numero) {
        return switch (numero) {
            case 1  -> "Prologue Edolas";
            case 2  -> "Magie Limité";
            case 3  -> "La chasseus de fée";
            case 4  -> "Gray contre sugarBoy";
            case 5  -> "Natsu et Lucy vs huges";
            case 6  -> "Lucy contre bario";
            case 7  -> "Gajeel vs Panthère Lilly";
            case 8  -> "Le roi d'edolas rentre en jeu";
            case 9  -> "Natsu et Gajeel et wendy vs Le roi d'edolas";
            case 10 -> "Le dernier combat";
            default -> "???";
        };
    }

    public String getNomChapitre() { return "Arc Edolas"; }

    public boolean[] getStagesDebloques() { return stagesDebloques; }
    public boolean[] getStagesReussis()   { return stagesReussis; }
    public void setStagesDebloques(boolean[] d) { for (int i = 0; i <= NB_STAGES; i++) stagesDebloques[i] = d[i]; }
    public void setStagesReussis(boolean[] r)   { for (int i = 0; i <= NB_STAGES; i++) stagesReussis[i]   = r[i]; }

    // ── Invites temporaires (stages 4, 5, 6, 7 et 9) ───────────────────────

    /**
     * Injecte un ou plusieurs invites deja configures (niveau/stats) dans l'equipe pour la duree
     * d'un seul combat, en respectant les regles de formation, puis lance le stage.
     */
    private Stage.ResultatStage lancerStageAvecInvites(GameContext ctx, Stage stage, boolean estNouveau, PersonnageBase... invites) {
        ArrayList<PersonnageBase> equipe = ctx.formation.getEquipe();
        StringBuilder noms = new StringBuilder();
        for (int i = 0; i < invites.length; i++) {
            PersonnageBase invite = invites[i];
            // Equipement fantome pour l'invite, au meme titre que les ennemis, sinon il se retrouve
            // largement depasse face a des ennemis qui, eux, sont equipes selon leur niveau.
            EquipementFactory.equiperSetStandard(invite, EquipementFactory.rareteEnnemiPourNiveau(invite.getNiveau()));
            Formation.ajouterInviteTemporaire(equipe, invite);

            if (i > 0) noms.append(i == invites.length - 1 ? " et " : ", ");
            noms.append(invite.getNom());
        }

        System.out.println(">> " + noms + (invites.length > 1 ? " rejoignent" : " rejoint") + " votre equipe pour ce combat !");
        Stage.ResultatStage resultat = stage.lancer(ctx, equipe, estNouveau);
        System.out.println(">> " + noms + (invites.length > 1 ? " quittent" : " quitte") + " l'equipe apres le combat.");
        return resultat;
    }
}
