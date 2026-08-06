package lancement.Chapitres;

import Equipement.EquipementFactory;
import Personnage.PersonnageBase;
import Personnage.FairyTail.perso_Lucy;
import Personnage.FairyTail.perso_Kana;
import Personnage.FairyTail.perso_Elfman;
import Personnage.FairyTail.perso_Evergreen;
import Personnage.FairyTail.perso_Erza;
import Personnage.FairyTail.perso_Gray;
import Personnage.FairyTail.perso_Loki;
import Personnage.FairyTail.perso_Gajeel;
import Personnage.FairyTail.perso_Levy;
import Personnage.FairyTail.perso_Mest;
import Personnage.FairyTail.perso_PantherLily;
import Personnage.FairyTail.perso_Wendy;
import Personnage.FairyTail.perso_Natsu;
import Personnage.pnj.Chapitre3.EnnemiMakarov;
import lancement.GameContext;
import lancement.Formation;
import lancement.Stage;
import java.util.ArrayList;
import java.util.Scanner;
import Personnage.pnj.EnnemisGeneriques.*;
import Personnage.pnj.Chapitre5.*;
import Personnage.pnj.Chapitre8.*;

public class Chapitre8 implements Chapitre {

    private static final int NB_STAGES = 10;
    private final boolean[] stagesDebloques = new boolean[NB_STAGES + 1];
    private final boolean[] stagesReussis   = new boolean[NB_STAGES + 1];

    public Chapitre8() {
        stagesDebloques[1] = true;
    }

    public void afficher(GameContext ctx, Scanner scanner) {
        boolean retour = false;

        while (!retour) {
            ctx.gestionnaireEnergie.mettreAJourRecharge();

            System.out.println("\n========================================");
            System.out.println("   CHAPITRE 8 — " + getNomChapitre());
            System.out.println("========================================");
            System.out.println("Or : " + String.format("%.0f", ctx.joueur.getOr())
                    + "  |  " + ctx.gestionnaireEnergie.afficherEnergie());
            System.out.println();

            for (int i = 1; i <= NB_STAGES; i++) {
                boolean jouable = ctx.gestionnaireQuetes.estStageJouable(8, i, false);
                String etat = stagesReussis[i] ? "[OK]  "
                        : !stagesDebloques[i]  ? "[###] "
                        : jouable              ? "[  ]  "
                        :                        "[QUETE] ";
                String etoiles = ctx.gestionnaireEtoiles.getEtoiles(8, i, false).afficher();
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
            } else if (!ctx.gestionnaireQuetes.estStageJouable(8, choix, false)) {
                System.out.println("Acceptez d'abord la quete associee a ce stage (menu Quetes).");
            } else {
                lancerStage(ctx, choix);
            }
        }
    }

    /**
     * Lance le stage donne (avec les invites temporaires des stages 2, 3, 5, 6, 7, 8 et 10, et
     * les combats scriptes des stages 4 et 9) et applique les recompenses en cas de victoire.
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
            // Stage 2 : Lucy et Cana rejoignent l'assaut contre Bixrow et Freed.
            case 2 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(8, 2);
                perso_Lucy lucy = Formation.creerInvite(perso_Lucy::new, niv, 8600, 1150, 700, 320);
                perso_Kana kana = Formation.creerInvite(perso_Kana::new, niv, 8500, 1050, 680, 300);
                yield lancerStageAvecInvites(ctx, stage, estNouveau, lucy, kana);
            }
            // Stage 3 : Elfman et Evergreen affrontent Mirajane aux cotes de l'equipe.
            case 3 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(8, 3);
                perso_Elfman elfman       = Formation.creerInvite(perso_Elfman::new, niv, 10500, 1300, 1400, 260);
                perso_Evergreen evergreen = Formation.creerInvite(perso_Evergreen::new, niv, 7800, 1500, 620, 340);
                yield lancerStageAvecInvites(ctx, stage, estNouveau, elfman, evergreen);
            }
            // Stage 4 : combat scripte, Natsu seul contre Gildarts — Natsu l'emporte.
            case 4 -> lancerStage4NatsuVsGildarts(ctx, stage, estNouveau);
            // Stage 5 : Erza affronte Jubia et Lisanna aux cotes de l'equipe.
            case 5 -> {
                perso_Erza invite = Formation.creerInvite(perso_Erza::new,
                        CourbeChapitres.niveauEnnemiPourStage(8, 5), 9800, 1900, 1100, 300);
                yield lancerStageAvecInvites(ctx, stage, estNouveau, invite);
            }
            // Stage 6 : Gray et Loki affrontent Wendy et Mest aux cotes de l'equipe.
            case 6 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(8, 6);
                perso_Gray gray = Formation.creerInvite(perso_Gray::new, niv, 9200, 1750, 900, 330);
                perso_Loki loki = Formation.creerInvite(perso_Loki::new, niv, 8300, 1650, 750, 360);
                yield lancerStageAvecInvites(ctx, stage, estNouveau, gray, loki);
            }
            // Stage 7 : Gajeel et Levy affrontent Kawazu et Yamaru aux cotes de l'equipe.
            case 7 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(8, 7);
                perso_Gajeel gajeel = Formation.creerInvite(perso_Gajeel::new, niv, 9500, 1800, 950, 280);
                perso_Levy levy     = Formation.creerInvite(perso_Levy::new, niv, 7200, 1100, 680, 310);
                yield lancerStageAvecInvites(ctx, stage, estNouveau, gajeel, levy);
            }
            // Stage 8 : Mest, Panther Lily et Wendy se joignent a l'assaut contre Azuma.
            case 8 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(8, 8);
                perso_Mest mest             = Formation.creerInvite(perso_Mest::new, niv, 7600, 1200, 750, 340);
                perso_PantherLily lily      = Formation.creerInvite(perso_PantherLily::new, niv, 9000, 1850, 900, 310);
                perso_Wendy wendy           = Formation.creerInvite(perso_Wendy::new, niv, 7900, 900, 800, 330);
                yield lancerStageAvecInvites(ctx, stage, estNouveau, mest, lily, wendy);
            }
            // Stage 9 : combat scripte, Makarov seul contre Hades — Makarov l'emporte.
            case 9 -> lancerStage9MakarovVsHades(ctx, stage, estNouveau);
            // Stage 10 : Natsu affronte Zancrow aux cotes de l'equipe.
            case 10 -> {
                perso_Natsu invite = Formation.creerInvite(perso_Natsu::new,
                        CourbeChapitres.niveauEnnemiPourStage(8, 10), 10200, 2100, 1050, 340);
                yield lancerStageAvecInvites(ctx, stage, estNouveau, invite);
            }
            default -> stage.lancer(ctx, ctx.formation.getEquipe(), estNouveau);
        };

        if (resultatStage.victoire) {
            stagesReussis[numero] = true;
            if (numero < NB_STAGES) {
                stagesDebloques[numero + 1] = true;
                System.out.println(">> Stage " + (numero + 1) + " debloque !");
            } else {
                System.out.println(">> Felicitations ! Chapitre 8 termine !");
            }

            ctx.gestionnaireQuetes.notifierOrGagne(stage.getRecompenseOr());
            ctx.gestionnaireQuetes.notifierStageFini(8, numero, false,
                    ctx.joueur, ctx.menuRecrutement, ctx.personnagesRecruites);
            ctx.gestionnaireEtoiles.mettreAJour(8, numero, false,
                    resultatStage.victoire, resultatStage.sansAllieMort, resultatStage.enMoinsDe10Tours);
        }
        return resultatStage;
    }

    private Stage construireStage(int numero) {
        ArrayList<PersonnageBase> e = new ArrayList<>();

        return switch (numero) {
            // Stage 1 — equipe generique : 1 tank, 3 dps, 2 supports.
            case 1 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(8, 1);
                e.add(new EnnemiMage5Tank(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage7DPS(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage6Debuff(Variante.CHAPITRE_8, niv));
                yield new Stage(1, "Prologue examen de rang S", 16800, 218, e);
            }
            // Stage 2 — Bixrow et Freed + 1 tank + 1 dps + 2 supports generiques.
            case 2 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(8, 2);
                e.add(new EnnemiBixrow(niv));
                e.add(new EnnemiFreed(niv));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage6Debuff(Variante.CHAPITRE_8, niv));
                yield new Stage(2, "Lucy et cana vs bixrow et freed", 17100, 222, e);
            }
            // Stage 3 — Mirajane + 1 tank + 2 dps + 1 support generiques.
            case 3 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(8, 3);
                e.add(new EnnemiMirajane(niv));
                e.add(new EnnemiMage5Tank(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage7DPS(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_8, niv));
                yield new Stage(3, "Elfman et evergreen vs Mirajane", 17400, 226, e);
            }
            // Stage 4 — combat scripte : Natsu seul contre Gildarts.
            case 4 -> {
                e.add(new EnnemiGildarts(CourbeChapitres.niveauEnnemiPourStage(8, 4)));
                yield new Stage(4, "Natsu vs Gildarts", 17700, 230, e);
            }
            // Stage 5 — Jubia et Lisanna + 1 tank + 2 dps generiques.
            case 5 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(8, 5);
                e.add(new EnnemiJubia(niv));
                e.add(new EnnemiLisanna(niv));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage8DPS(Variante.CHAPITRE_8, niv));
                yield new Stage(5, "Erza vs jubia et lisanna", 18000, 234, e);
            }
            // Stage 6 — Mest et Wendy (ennemis) + 1 tank + 2 dps generiques.
            case 6 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(8, 6);
                e.add(new EnnemiMest(niv));
                e.add(new EnnemiWendy(niv));
                e.add(new EnnemiMage5Tank(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage7DPS(Variante.CHAPITRE_8, niv));
                yield new Stage(6, "Gray et Loki vs Mest et Wendy", 18300, 238, e);
            }
            // Stage 7 — Kawazu et Yamaru + 1 tank + 2 dps generiques.
            case 7 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(8, 7);
                e.add(new EnnemiKawazu(niv));
                e.add(new EnnemiYamaru(niv));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage8DPS(Variante.CHAPITRE_8, niv));
                yield new Stage(7, "Gajeel et Levy vs kawazu et yamaru", 18600, 242, e);
            }
            // Stage 8 — Azuma + 1 tank + 1 dps + 2 supports generiques.
            case 8 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(8, 8);
                e.add(new EnnemiAzuma(niv));
                e.add(new EnnemiMage5Tank(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage6Debuff(Variante.CHAPITRE_8, niv));
                yield new Stage(8, "Mest, panthere lilly et wendy vs Azuma", 19000, 247, e);
            }
            // Stage 9 — combat scripte : Makarov seul contre Hades.
            case 9 -> {
                e.add(new EnnemiHades(CourbeChapitres.niveauEnnemiPourStage(8, 9)));
                yield new Stage(9, "Le maître de grimoir Heart vs Makarov", 19500, 253, e);
            }
            // Stage 10 — Zancrow + 1 tank + 2 dps + 1 support generiques.
            case 10 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(8, 10);
                e.add(new EnnemiZancrow(niv));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage7DPS(Variante.CHAPITRE_8, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_8, niv));
                yield new Stage(10, "Natsu vs Zancrow", 20000, 260, e);
            }
            default -> new Stage(numero, "???", 0, 0, e);
        };
    }

    public String getTitreStage(int numero) {
        return switch (numero) {
            case 1  -> "Prologue examen de rang S";
            case 2  -> "Lucy et cana vs bixrow et freed";
            case 3  -> "Elfman et evergreen vs Mirajane";
            case 4  -> "Natsu vs Gildarts";
            case 5  -> "Erza vs jubia et lisanna";
            case 6  -> "Gray et Loki vs Mest et Wendy";
            case 7  -> "Gajeel et Levy vs kawazu et yamaru ";
            case 8  -> "Mest , panthere lilly et wendy vs Azuma";
            case 9  -> "Le maître de grimoir Heart vs Makarov";
            case 10 -> "Natsu vs Zancrow";
            default -> "???";
        };
    }

    public String getNomChapitre() { return "Arc île de Tenro 1"; }

    public boolean[] getStagesDebloques() { return stagesDebloques; }
    public boolean[] getStagesReussis()   { return stagesReussis; }
    public void setStagesDebloques(boolean[] d) { for (int i = 0; i <= NB_STAGES; i++) stagesDebloques[i] = d[i]; }
    public void setStagesReussis(boolean[] r)   { for (int i = 0; i <= NB_STAGES; i++) stagesReussis[i]   = r[i]; }

    // ── Invites temporaires (stages 2, 3, 5, 6, 7, 8 et 10) ────────────────

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

    // ── Combats scriptes (stages 4 et 9) ────────────────────────────────────

    private Stage.ResultatStage lancerStage4NatsuVsGildarts(GameContext ctx, Stage stage, boolean estNouveau) {
        // Combat scripte : notre formation n'intervient pas, seul Natsu affronte Gildarts.
        // Stats relevees pour rester favorables face a Gildarts (~12000 PV / 4600 ATK avec la formule).
        perso_Natsu natsu = Formation.creerInvite(perso_Natsu::new,
                CourbeChapitres.niveauEnnemiPourStage(8, 4), 14000, 5200, 2900, 1050);
        EquipementFactory.equiperSetStandard(natsu, EquipementFactory.rareteEnnemiPourNiveau(natsu.getNiveau()));

        ArrayList<PersonnageBase> equipeFixe = new ArrayList<>();
        equipeFixe.add(natsu);

        System.out.println(">> Natsu affronte seul Gildarts !");
        System.out.println("   « Je vais enfin savoir si je peux le battre... » — Natsu\n");

        Stage.ResultatStage resultat = stage.lancer(ctx, equipeFixe, estNouveau);

        System.out.println("\n>> Natsu tient tete a l'un des plus puissants mages de Fairy Tail.");

        return resultat;
    }

    private Stage.ResultatStage lancerStage9MakarovVsHades(GameContext ctx, Stage stage, boolean estNouveau) {
        // Combat scripte : notre formation n'intervient pas, seul Makarov affronte Hades.
        // Stats relevees (au-dela de celles utilisees au Chapitre 3 face a Jose) pour rester
        // competitives face a un adversaire bien plus puissant ; son ultime, la Loi des Fees,
        // reste l'atout decisif qui scelle l'issue du duel.
        EnnemiMakarov makarov = new EnnemiMakarov();
        makarov.setNiveau(CourbeChapitres.niveauEnnemiPourStage(8, 9));
        makarov.setVie(18000);
        makarov.setVieMax(18000);
        makarov.setAttaque(5800);
        makarov.setDefense(3200);
        makarov.setVitesse(1000);
        EquipementFactory.equiperSetStandard(makarov, EquipementFactory.rareteEnnemiPourNiveau(makarov.getNiveau()));

        ArrayList<PersonnageBase> equipeFixe = new ArrayList<>();
        equipeFixe.add(makarov);

        System.out.println(">> Makarov arrive seul face a Hades, le maitre de Grimoire Heart !");
        System.out.println("   « Precht... non... Hades. C'est ici que ça se termine. » — Makarov\n");

        Stage.ResultatStage resultat = stage.lancer(ctx, equipeFixe, estNouveau);

        System.out.println("\n>> La Loi des Fées s'abat sur Hades, mettant fin au regne de Grimoire Heart.");

        return resultat;
    }
}
