package lancement.Chapitres;

import Equipement.EquipementFactory;
import Personnage.PersonnageBase;
import Personnage.FairyTail.perso_Natsu;
import Personnage.FairyTail.perso_Mirajane;
import Personnage.FairyTail.perso_Elfman;
import Personnage.FairyTail.perso_Evergreen;
import Personnage.FairyTail.perso_Loki;
import Personnage.FairyTail.perso_Erza;
import Personnage.FairyTail.perso_Jubia_4elements;
import Personnage.FairyTail.perso_Gray;
import Personnage.FairyTail.perso_Lucy;
import Personnage.FairyTail.perso_Wendy;
import Personnage.FairyTail.perso_Gildarts;
import Personnage.pnj.Chapitre3.EnnemiMakarov;
import lancement.GameContext;
import lancement.Formation;
import lancement.Stage;
import java.util.ArrayList;
import java.util.Scanner;
import Personnage.pnj.EnnemisGeneriques.*;
import Personnage.pnj.Chapitre8.*;
import Personnage.pnj.Chapitre9.*;

public class Chapitre9 implements Chapitre {

    private static final int NB_STAGES = 10;
    private final boolean[] stagesDebloques = new boolean[NB_STAGES + 1];
    private final boolean[] stagesReussis   = new boolean[NB_STAGES + 1];

    public Chapitre9() {
        stagesDebloques[1] = true;
    }

    public void afficher(GameContext ctx, Scanner scanner) {
        boolean retour = false;

        while (!retour) {
            ctx.gestionnaireEnergie.mettreAJourRecharge();

            System.out.println("\n========================================");
            System.out.println("   CHAPITRE 9 — " + getNomChapitre());
            System.out.println("========================================");
            System.out.println("Or : " + String.format("%.0f", ctx.joueur.getOr())
                    + "  |  " + ctx.gestionnaireEnergie.afficherEnergie());
            System.out.println();

            for (int i = 1; i <= NB_STAGES; i++) {
                boolean jouable = ctx.gestionnaireQuetes.estStageJouable(9, i, false);
                String etat = stagesReussis[i] ? "[OK]  "
                        : !stagesDebloques[i]  ? "[###] "
                        : jouable              ? "[  ]  "
                        :                        "[QUETE] ";
                String etoiles = ctx.gestionnaireEtoiles.getEtoiles(9, i, false).afficher();
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
            } else if (!ctx.gestionnaireQuetes.estStageJouable(9, choix, false)) {
                System.out.println("Acceptez d'abord la quete associee a ce stage (menu Quetes).");
            } else {
                lancerStage(ctx, choix);
            }
        }
    }

    /**
     * Lance le stage donne (avec les invites temporaires des stages 1 a 6, 8 et 9, et les
     * combats scriptes des stages 7 et 10) et applique les recompenses en cas de victoire.
     * Suppose que le stage est deja debloque. Reutilisable par la console et l'interface graphique.
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
            // Stage 1 : Natsu et Makarov (affaibli, encore marque par Tenro) rejoignent l'assaut contre Zancrow.
            case 1 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(9, 1);
                perso_Natsu natsu   = Formation.creerInvite(perso_Natsu::new, niv, 10800, 2250, 1150, 350);
                EnnemiMakarov makarov = Formation.creerInvite(EnnemiMakarov::new, niv, 6500, 1350, 700, 260);
                yield lancerStageAvecInvites(ctx, stage, estNouveau, natsu, makarov);
            }
            // Stage 2 : Mirajane, encore diminuee, rejoint l'assaut contre Azuma.
            case 2 -> {
                perso_Mirajane invite = Formation.creerInvite(perso_Mirajane::new,
                        CourbeChapitres.niveauEnnemiPourStage(9, 2), 7200, 1400, 750, 300);
                yield lancerStageAvecInvites(ctx, stage, estNouveau, invite);
            }
            // Stage 3 : Elfman et Evergreen affrontent Rustyrose aux cotes de l'equipe.
            case 3 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(9, 3);
                perso_Elfman elfman       = Formation.creerInvite(perso_Elfman::new, niv, 12000, 1500, 1600, 280);
                perso_Evergreen evergreen = Formation.creerInvite(perso_Evergreen::new, niv, 9000, 1700, 700, 360);
                yield lancerStageAvecInvites(ctx, stage, estNouveau, elfman, evergreen);
            }
            // Stage 4 : Leo (Loki) affronte Capricorn, son "frere d'armes" spirituel, aux cotes de l'equipe.
            case 4 -> {
                perso_Loki invite = Formation.creerInvite(perso_Loki::new,
                        CourbeChapitres.niveauEnnemiPourStage(9, 4), 9500, 1900, 850, 380);
                yield lancerStageAvecInvites(ctx, stage, estNouveau, invite);
            }
            // Stage 5 : Erza et Jubia (encore convalescente) affrontent Meldy aux cotes de l'equipe.
            case 5 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(9, 5);
                perso_Erza erza             = Formation.creerInvite(perso_Erza::new, niv, 11000, 2100, 1300, 330);
                perso_Jubia_4elements jubia = Formation.creerInvite(perso_Jubia_4elements::new, niv, 6800, 1300, 700, 300);
                yield lancerStageAvecInvites(ctx, stage, estNouveau, erza, jubia);
            }
            // Stage 6 : Jubia, remise sur pied, affronte seule la rancune de Meldy.
            case 6 -> {
                perso_Jubia_4elements invite = Formation.creerInvite(perso_Jubia_4elements::new,
                        CourbeChapitres.niveauEnnemiPourStage(9, 6), 9000, 1800, 850, 330);
                yield lancerStageAvecInvites(ctx, stage, estNouveau, invite);
            }
            // Stage 7 : combat scripte, Gildarts seul contre Bluenote — Gildarts l'emporte.
            case 7 -> lancerStage7GildartsVsBluenote(ctx, stage, estNouveau);
            // Stage 8 : Erza prend sa revanche sur Azuma aux cotes de l'equipe.
            case 8 -> {
                perso_Erza invite = Formation.creerInvite(perso_Erza::new,
                        CourbeChapitres.niveauEnnemiPourStage(9, 8), 11500, 2200, 1350, 340);
                yield lancerStageAvecInvites(ctx, stage, estNouveau, invite);
            }
            // Stage 9 : Gray affronte Ultear aux cotes de l'equipe.
            case 9 -> {
                perso_Gray invite = Formation.creerInvite(perso_Gray::new,
                        CourbeChapitres.niveauEnnemiPourStage(9, 9), 10500, 2100, 1200, 350);
                yield lancerStageAvecInvites(ctx, stage, estNouveau, invite);
            }
            // Stage 10 : combat scripte, Erza/Lucy/Wendy/Gray/Natsu contre Hades — l'equipe l'emporte
            // au prix d'un combat long et acharne.
            case 10 -> lancerStage10AssautContreHades(ctx, stage, estNouveau);
            default -> stage.lancer(ctx, ctx.formation.getEquipe(), estNouveau);
        };

        if (resultatStage.victoire) {
            stagesReussis[numero] = true;
            if (numero < NB_STAGES) {
                stagesDebloques[numero + 1] = true;
                System.out.println(">> Stage " + (numero + 1) + " debloque !");
            } else {
                System.out.println(">> Felicitations ! Chapitre 9 termine !");
            }

            ctx.gestionnaireQuetes.notifierOrGagne(stage.getRecompenseOr());
            ctx.gestionnaireQuetes.notifierStageFini(9, numero, false,
                    ctx.joueur, ctx.menuRecrutement, ctx.personnagesRecruites);
            ctx.gestionnaireEtoiles.mettreAJour(9, numero, false,
                    resultatStage.victoire, resultatStage.sansAllieMort, resultatStage.enMoinsDe10Tours);
        }
        return resultatStage;
    }

    private Stage construireStage(int numero) {
        ArrayList<PersonnageBase> e = new ArrayList<>();

        return switch (numero) {
            // Stage 1 — Zancrow (alias Thuncrow) + 1 tank + 1 dps + 2 supports generiques.
            case 1 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(9, 1);
                e.add(new EnnemiZancrow(niv));
                e.add(new EnnemiMage5Tank(Variante.CHAPITRE_9, niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_9, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_9, niv));
                e.add(new EnnemiMage6Debuff(Variante.CHAPITRE_9, niv));
                yield new Stage(1, "Natsu et Makarov vs Thuncrow", 20300, 0, e);
            }
            // Stage 2 — Azuma + 2 dps + 2 supports generiques.
            case 2 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(9, 2);
                e.add(new EnnemiAzuma(niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_9, niv));
                e.add(new EnnemiMage7DPS(Variante.CHAPITRE_9, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_9, niv));
                e.add(new EnnemiMage4Buff(Variante.CHAPITRE_9, niv));
                yield new Stage(2, "Mirajane vs Azuma", 20600, 0, e);
            }
            // Stage 3 — Rustyrose + 3 dps + 1 support generiques.
            case 3 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(9, 3);
                e.add(new EnnemiRustyrose(niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_9, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_9, niv));
                e.add(new EnnemiMage7DPS(Variante.CHAPITRE_9, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_9, niv));
                yield new Stage(3, "Elfman et Evergreen vs Rustyrose", 20900, 0, e);
            }
            // Stage 4 — Capricorn + 1 tank + 2 dps + 1 support generiques.
            case 4 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(9, 4);
                e.add(new EnnemiCapricorn(niv));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_9, niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_9, niv));
                e.add(new EnnemiMage7DPS(Variante.CHAPITRE_9, niv));
                e.add(new EnnemiMage6Debuff(Variante.CHAPITRE_9, niv));
                yield new Stage(4, "Leo le Lion vs Capricorn", 21200, 0, e);
            }
            // Stage 5 — Meldy + 1 tank + 3 dps generiques.
            case 5 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(9, 5);
                e.add(new EnnemiMeredy(niv));
                e.add(new EnnemiMage5Tank(Variante.CHAPITRE_9, niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_9, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_9, niv));
                e.add(new EnnemiMage8DPS(Variante.CHAPITRE_9, niv));
                yield new Stage(5, "Erza et Jubia vs Meldy", 21500, 0, e);
            }
            // Stage 6 — Meldy (revanche) + 1 tank + 3 dps generiques.
            case 6 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(9, 6);
                e.add(new EnnemiMeredy(niv));
                e.add(new EnnemiMage5Tank(Variante.CHAPITRE_9, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_9, niv));
                e.add(new EnnemiMage7DPS(Variante.CHAPITRE_9, niv));
                e.add(new EnnemiMage8DPS(Variante.CHAPITRE_9, niv));
                yield new Stage(6, "Jubia contre Meldy", 21800, 0, e);
            }
            // Stage 7 — combat scripte : Gildarts seul contre Bluenote.
            case 7 -> {
                e.add(new EnnemiBluenote(CourbeChapitres.niveauEnnemiPourStage(9, 7)));
                yield new Stage(7, "Gildarts vs Bluenote", 22100, 0, e);
            }
            // Stage 8 — Azuma (revanche) + 3 dps + 1 support generiques.
            case 8 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(9, 8);
                e.add(new EnnemiAzuma(niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_9, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_9, niv));
                e.add(new EnnemiMage8DPS(Variante.CHAPITRE_9, niv));
                e.add(new EnnemiMage4Buff(Variante.CHAPITRE_9, niv));
                yield new Stage(8, "Erza contre Azuma", 22400, 0, e);
            }
            // Stage 9 — Ultear + 1 tank + 3 dps generiques.
            case 9 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(9, 9);
                e.add(new EnnemiUltear(niv));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_9, niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_9, niv));
                e.add(new EnnemiMage7DPS(Variante.CHAPITRE_9, niv));
                e.add(new EnnemiMage8DPS(Variante.CHAPITRE_9, niv));
                yield new Stage(9, "Gray contre Ultear", 22700, 0, e);
            }
            // Stage 10 — combat scripte : Erza, Lucy, Wendy, Gray et Natsu contre Hades.
            case 10 -> {
                e.add(new EnnemiHades(CourbeChapitres.niveauEnnemiPourStage(9, 10)));
                yield new Stage(10, "L'assaut final contre Hades", 23500, 0, e);
            }
            default -> new Stage(numero, "???", 0, 0, e);
        };
    }

    public String getTitreStage(int numero) {
        return switch (numero) {
            case 1  -> "Natsu et Makarov vs Thuncrow";
            case 2  -> "Mirajane vs Azuma";
            case 3  -> "Elfman et Evergreen vs Rustyrose";
            case 4  -> "Leo le Lion vs Capricorn";
            case 5  -> "Erza et Jubia vs Meldy";
            case 6  -> "Jubia contre Meldy";
            case 7  -> "Gildarts vs Bluenote";
            case 8  -> "Erza contre Azuma";
            case 9  -> "Gray contre Ultear";
            case 10 -> "L'assaut final contre Hades";
            default -> "???";
        };
    }

    public String getNomChapitre() { return "Arc île de Tenro 2"; }

    public boolean[] getStagesDebloques() { return stagesDebloques; }
    public boolean[] getStagesReussis()   { return stagesReussis; }
    public void setStagesDebloques(boolean[] d) { for (int i = 0; i <= NB_STAGES; i++) stagesDebloques[i] = d[i]; }
    public void setStagesReussis(boolean[] r)   { for (int i = 0; i <= NB_STAGES; i++) stagesReussis[i]   = r[i]; }

    // ── Invites temporaires (stages 1 a 6, 8 et 9) ──────────────────────────

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

    // ── Combats scriptes (stages 7 et 10) ───────────────────────────────────

    private Stage.ResultatStage lancerStage7GildartsVsBluenote(GameContext ctx, Stage stage, boolean estNouveau) {
        // Combat scripte : notre formation n'intervient pas, seul Gildarts affronte Bluenote.
        // Stats relevees pour rester nettement favorables face a Bluenote (~14500 PV / 3900 ATK
        // avec la formule) : l'un des mages les plus puissants de Fairy Tail doit s'imposer sans
        // ambiguite.
        perso_Gildarts gildarts = Formation.creerInvite(perso_Gildarts::new,
                CourbeChapitres.niveauEnnemiPourStage(9, 7), 15000, 4200, 2400, 450);
        EquipementFactory.equiperSetStandard(gildarts, EquipementFactory.rareteEnnemiPourNiveau(gildarts.getNiveau()));

        ArrayList<PersonnageBase> equipeFixe = new ArrayList<>();
        equipeFixe.add(gildarts);

        System.out.println(">> Gildarts affronte seul Bluenote, maitre de la gravite !");
        System.out.println("   « Desole gamin, mais je n'ai pas de temps a perdre. » — Gildarts\n");

        Stage.ResultatStage resultat = stage.lancer(ctx, equipeFixe, estNouveau);

        System.out.println("\n>> D'un seul Crash, Gildarts balaie Bluenote et sa magie de la gravite.");

        return resultat;
    }

    private Stage.ResultatStage lancerStage10AssautContreHades(GameContext ctx, Stage stage, boolean estNouveau) {
        // Combat scripte : notre formation n'intervient pas, seule une equipe fixe (Erza, Lucy,
        // Wendy, Gray, Natsu) affronte Hades. Stats volontairement mesurees (pas de solo ecrasant
        // comme Chapitre8) pour que le combat s'etire sur plusieurs tours avant que l'equipe ne
        // l'emporte : Hades (~19000 PV / 6260 ATK avec la formule) reste redoutable et revient
        // plus fort encore au Chapitre 10.
        int niv = CourbeChapitres.niveauEnnemiPourStage(9, 10);
        perso_Erza erza   = Formation.creerInvite(perso_Erza::new,   niv, 11000, 2300, 1500, 380);
        perso_Natsu natsu = Formation.creerInvite(perso_Natsu::new,  niv, 11500, 2400, 1350, 400);
        perso_Gray gray   = Formation.creerInvite(perso_Gray::new,   niv, 10500, 2200, 1400, 360);
        perso_Lucy lucy   = Formation.creerInvite(perso_Lucy::new,   niv,  9000, 1800, 1100, 340);
        perso_Wendy wendy = Formation.creerInvite(perso_Wendy::new,  niv,  8500, 1000, 1200, 350);

        ArrayList<PersonnageBase> equipeFixe = new ArrayList<>();
        for (PersonnageBase p : new PersonnageBase[]{erza, natsu, gray, lucy, wendy}) {
            EquipementFactory.equiperSetStandard(p, EquipementFactory.rareteEnnemiPourNiveau(p.getNiveau()));
            equipeFixe.add(p);
        }

        System.out.println(">> Erza, Natsu, Gray, Lucy et Wendy se dressent ensemble face a Hades !");
        System.out.println("   « Nous sommes Fairy Tail. Nous ne reculerons pas. » — Erza\n");

        Stage.ResultatStage resultat = stage.lancer(ctx, equipeFixe, estNouveau);

        System.out.println("\n>> Apres un combat acharne de plusieurs tours, l'equipe repousse Hades —");
        System.out.println("   qui s'echappe dans un rire, jurant de revenir plus fort encore.");

        return resultat;
    }
}
