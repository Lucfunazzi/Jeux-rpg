package lancement.Chapitres;

import Equipement.EquipementFactory;
import Personnage.PersonnageBase;
import Personnage.pnj.Chapitre4.*;
import Personnage.FairyTail.perso_Natsu;
import Personnage.FairyTail.perso_Gray;
import Personnage.FairyTail.perso_Lucy;
import Personnage.FairyTail.perso_Erza;
import Personnage.FairyTail.perso_Jubia_4elements;
import Personnage.FairyTail.perso_Simon;
import Personnage.FairyTail.perso_Shaw;
import Personnage.FairyTail.perso_Natsu_Etherion;
import lancement.GameContext;
import lancement.Formation;
import lancement.Stage;
import java.util.ArrayList;
import java.util.Scanner;

/**
 * Chapitre 4 — La Tour du Paradis. Une embuscade reunit d'emblee Wolly, Miliana, Shaw et
 * Simon (encore sous l'emprise de Jellal) contre l'equipe, avant que Shaw (stage 4) puis
 * Simon (stage 6) ne rejoignent la cause d'Erza. Ikaruga garde l'acces final (stage 7) avant
 * le duel scripte Erza contre Jellal (stage 8), l'assaut de toute l'equipe avec Simon (stage 9)
 * et l'affrontement final avec Natsu Etherion (stage 10).
 */
public class Chapitre4 {

    private static final int NB_STAGES = 10;
    private final boolean[] stagesDebloques = new boolean[NB_STAGES + 1];
    private final boolean[] stagesReussis   = new boolean[NB_STAGES + 1];

    public Chapitre4() {
        stagesDebloques[1] = true;
    }

    public void afficher(GameContext ctx, Scanner scanner) {
        boolean retour = false;

        while (!retour) {
            ctx.gestionnaireEnergie.mettreAJourRecharge();

            System.out.println("\n========================================");
            System.out.println("   CHAPITRE 4 — La Tour du Paradis");
            System.out.println("========================================");
            System.out.println("Or : " + String.format("%.0f", ctx.joueur.getOr())
                    + "  |  " + ctx.gestionnaireEnergie.afficherEnergie());
            System.out.println();

            for (int i = 1; i <= NB_STAGES; i++) {
                boolean jouable = ctx.gestionnaireQuetes.estStageJouable(4, i, false);
                String etat = stagesReussis[i] ? "[OK]  "
                        : !stagesDebloques[i]  ? "[###] "
                        : jouable              ? "[  ]  "
                        :                        "[QUETE] ";
                String etoiles = ctx.gestionnaireEtoiles.getEtoiles(4, i, false).afficher();
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
            } else if (!ctx.gestionnaireQuetes.estStageJouable(4, choix, false)) {
                System.out.println("Acceptez d'abord la quete associee a ce stage (menu Quetes).");
            } else {
                lancerStage(ctx, choix);
            }
        }
    }

    /**
     * Lance le stage donne (avec les invites temporaires stages 3 a 7, 9 et 10, et le duel
     * scripte du stage 8) et applique les recompenses en cas de victoire. Suppose que le stage est
     * deja debloque. Reutilisable par la console et l'interface graphique.
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
            case 1 -> lancerStage1AvecEquipeHeros(ctx, stage, estNouveau);
            // Stage 2 : pas d'invite, l'equipe du joueur affronte seule les gardes generiques.
            case 3 -> {
                perso_Natsu invite = new perso_Natsu();
                invite.setNiveau(47);
                invite.setVie(3600);
                invite.setVieMax(3600);
                invite.setAttaque(1050);
                invite.setDefense(480);
                invite.setVitesse(210);
                yield lancerStageAvecInvite(ctx, stage, estNouveau, invite);
            }
            case 4 -> {
                perso_Erza invite = new perso_Erza();
                invite.setNiveau(47);
                invite.setVie(4200);
                invite.setVieMax(4200);
                invite.setAttaque(950);
                invite.setDefense(650);
                invite.setVitesse(190);
                yield lancerStageAvecInvite(ctx, stage, estNouveau, invite);
            }
            case 5 -> {
                perso_Lucy lucy = new perso_Lucy();
                lucy.setNiveau(48);
                lucy.setVie(3300);
                lucy.setVieMax(3300);
                lucy.setAttaque(650);
                lucy.setDefense(420);
                lucy.setVitesse(200);

                perso_Jubia_4elements jubia = new perso_Jubia_4elements();
                jubia.setNiveau(48);
                jubia.setVie(3500);
                jubia.setVieMax(3500);
                jubia.setAttaque(750);
                jubia.setDefense(500);
                jubia.setVitesse(198);

                yield lancerStageAvecDeuxInvites(ctx, stage, estNouveau, lucy, jubia);
            }
            case 6 -> {
                perso_Natsu natsu = new perso_Natsu();
                natsu.setNiveau(48);
                natsu.setVie(3600);
                natsu.setVieMax(3600);
                natsu.setAttaque(1050);
                natsu.setDefense(480);
                natsu.setVitesse(210);

                // Simon vient de devoiler sa veritable allegeance : il combat desormais a nos cotes.
                perso_Simon simon = new perso_Simon();
                simon.setNiveau(48);
                simon.setVie(4000);
                simon.setVieMax(4000);
                simon.setAttaque(700);
                simon.setDefense(900);
                simon.setVitesse(170);

                yield lancerStageAvecDeuxInvites(ctx, stage, estNouveau, natsu, simon);
            }
            case 7 -> {
                perso_Erza erza = new perso_Erza();
                erza.setNiveau(49);
                erza.setVie(4200);
                erza.setVieMax(4200);
                erza.setAttaque(950);
                erza.setDefense(650);
                erza.setVitesse(190);

                perso_Shaw shaw = new perso_Shaw();
                shaw.setNiveau(49);
                shaw.setVie(3600);
                shaw.setVieMax(3600);
                shaw.setAttaque(800);
                shaw.setDefense(550);
                shaw.setVitesse(200);

                yield lancerStageAvecDeuxInvites(ctx, stage, estNouveau, erza, shaw);
            }
            case 8 -> lancerStage8AvecErza(ctx, stage, estNouveau);
            case 9 -> {
                // Simon (desormais alliee) prete main forte a l'equipe pour l'assaut sur Jellal.
                perso_Simon invite = new perso_Simon();
                invite.setNiveau(49);
                invite.setVie(4000);
                invite.setVieMax(4000);
                invite.setAttaque(700);
                invite.setDefense(900);
                invite.setVitesse(170);
                yield lancerStageAvecInvite(ctx, stage, estNouveau, invite);
            }
            case 10 -> {
                perso_Natsu_Etherion invite = new perso_Natsu_Etherion();
                invite.setNiveau(51);
                invite.setVie(4800);
                invite.setVieMax(4800);
                invite.setAttaque(1400);
                invite.setDefense(650);
                invite.setVitesse(230);
                yield lancerStageAvecInvite(ctx, stage, estNouveau, invite);
            }
            default -> stage.lancer(ctx, ctx.formation.getEquipe(), estNouveau);
        };

        if (resultatStage.victoire) {
            stagesReussis[numero] = true;
            if (numero < NB_STAGES) {
                stagesDebloques[numero + 1] = true;
                System.out.println(">> Stage " + (numero + 1) + " debloque !");
            } else {
                System.out.println(">> Felicitations ! La Tour du Paradis s'est effondree !");
            }

            ctx.gestionnaireQuetes.notifierOrGagne(stage.getRecompenseOr());
            ctx.gestionnaireQuetes.notifierStageFini(4, numero, false,
                    ctx.joueur, ctx.menuRecrutement, ctx.personnagesRecruites);
            ctx.gestionnaireEtoiles.mettreAJour(4, numero, false,
                    resultatStage.victoire, resultatStage.sansAllieMort, resultatStage.enMoinsDe10Tours);
        }
        return resultatStage;
    }

    private Stage construireStage(int numero) {
        ArrayList<PersonnageBase> e = new ArrayList<>();
        
        switch (numero) {

            // Stage 1 — Infiltration de la Tour du Paradis (combat natsu,gray,jubia,lucy et erza contre wolly miliana shaw et simon)
            case 1 -> {
                e.add(new EnnemiWolly(45));
                e.add(new EnnemiMiliana(45));
                e.add(new EnnemiShaw(45));
                e.add(new EnnemiSimon(46));
                e.add(new EnnemiMage2DPS(46));
                return new Stage(1, "Embuscade dans le casino", 1900, 62, e);
            }

            // Stage 2 — gardes generiques
            case 2 -> {
                e.add(new EnnemiMage7DPS(46));
                e.add(new EnnemiMage2DPS(45));
                e.add(new EnnemiMage9Tank(45));
                e.add(new EnnemiMage1DPS(45));
                e.add(new EnnemiMage3Soigneur(45));
                return new Stage(2, "Infiltration dans la tour du paradis", 2150, 64, e);
            }

            // Stage 3 — Natsu vs Wolly + Miliana + gardes generiques
            case 3 -> {
                e.add(new EnnemiMiliana(47));
                e.add(new EnnemiWolly(46));
                e.add(new EnnemiMage6Debuff(46));
                e.add(new EnnemiMage5Tank(46));
                e.add(new EnnemiMage3Soigneur(45));
                return new Stage(3, "Miaou, Il faut sauver Happy", 2400, 66, e);
            }

            // Stage 4 — Erza contre Shaw (encore sous emprise) + gardes generiques
            case 4 -> {
                e.add(new EnnemiShaw(47));
                e.add(new EnnemiMage2DPS(46));
                e.add(new EnnemiMage2DPS(46));
                e.add(new EnnemiMage9Tank(46));
                e.add(new EnnemiMage3Soigneur(46));
                return new Stage(4, "Libération d'erza", 2700, 68, e);
            }

            // Stage 5 —  Lucy + jubia vs Vivaldus + gardes generiques
            case 5 -> {
                e.add(new EnnemiVivaldus(48));
                e.add(new EnnemiMage3Soigneur(47));
                e.add(new EnnemiMage9Tank(47));
                e.add(new EnnemiMage2DPS(47));
                e.add(new EnnemiMage3Soigneur(46));
                return new Stage(5, "Les esprits et l'eau", 3050, 70, e);
            }

            // Stage 6 — Natsu et Simon contre Owl + gardes generiques
            case 6 -> {
                e.add(new EnnemiOwl(48));
                e.add(new EnnemiMage9Tank(47));
                e.add(new EnnemiMage3Soigneur(47));
                e.add(new EnnemiMage2DPS(47));
                e.add(new EnnemiMage6Debuff(47));
                return new Stage(6, "Le hiboux assasin", 3450, 72, e);
            }

            // Stage 7 — Erza + Shaw vs Ikaruga + gardes generiques
            case 7 -> {
                e.add(new EnnemiIkaruga(47));
                e.add(new EnnemiMage9Tank(48));
                e.add(new EnnemiMage3Soigneur(48));
                e.add(new EnnemiMage2DPS(48));
                e.add(new EnnemiMage8DPS(48));
                return new Stage(7, "Epée contre Epée", 3900, 74, e);
            }

            // Stage 8 — Combat scripte : Erza seule contre Jellal
            case 8 -> {
                e.add(new EnnemiJellal(51));
                return new Stage(8, "Erza contre Jellal — Le Passe Ressurgit", 4950, 80, e);
            }

            // Stage 9 — Simon (alliee) + equipe contre Jellal + gardes generiques
            case 9 -> {
                e.add(new EnnemiJellal(49));
                e.add(new EnnemiMage9Tank(48));
                e.add(new EnnemiMage3Soigneur(48));
                e.add(new EnnemiMage2DPS(48));
                return new Stage(9, "Simon Revient — L'Assaut sur Jellal", 5200, 82, e);
            }

            // Stage 10 — Natsu Etherion vs Jellal + gardes generiques
            case 10 -> {
                e.add(new EnnemiJellal(52));
                e.add(new EnnemiMage9Tank(50));
                e.add(new EnnemiMage3Soigneur(50));
                e.add(new EnnemiMage2DPS(50));
                e.add(new EnnemiMage8DPS(50));
                return new Stage(10, "Jellal — L'Effondrement de la Tour du Paradis", 6500, 84, e);
            }

            default -> { return new Stage(numero, "???", 0, 0, e); }
        }
    }

    public String getTitreStage(int numero) {
        return switch (numero) {
            case 1  -> "Embuscade dans le casino";
            case 2  -> "Infiltration dans la tour du paradis";
            case 3  -> "Miaou, Il faut sauver Happy";
            case 4  -> "Libération d'erza";
            case 5  -> "Les esprits et l'eau";
            case 6  -> "Le hiboux assasin";
            case 7  -> "Epée contre Epée";
            case 8  -> "Erza contre Jellal — Le Passe Ressurgit";
            case 9  -> "Simon Revient — L'Assaut sur Jellal";
            case 10 -> "Jellal — L'Effondrement de la Tour du Paradis";
            default -> "???";
        };
    }

    public boolean[] getStagesDebloques() { return stagesDebloques; }
    public boolean[] getStagesReussis()   { return stagesReussis; }
    public void setStagesDebloques(boolean[] d) { for (int i = 0; i <= NB_STAGES; i++) stagesDebloques[i] = d[i]; }
    public void setStagesReussis(boolean[] r)   { for (int i = 0; i <= NB_STAGES; i++) stagesReussis[i]   = r[i]; }

    // ── Invites temporaires (stages 3 a 7, 9 et 10) ────────────────────────

    /**
     * Injecte un invite deja configure (niveau/stats) dans l'equipe pour la duree
     * d'un seul combat, en respectant les regles de formation, puis lance le stage.
     */
    private Stage.ResultatStage lancerStageAvecInvite(GameContext ctx, Stage stage, boolean estNouveau, PersonnageBase invite) {
        // Equipement fantome pour l'invite, au meme titre que les ennemis, sinon il se retrouve
        // largement depasse face a des ennemis qui, eux, sont equipes selon leur niveau.
        EquipementFactory.equiperSetStandard(invite, EquipementFactory.rareteEnnemiPourNiveau(invite.getNiveau()));

        ArrayList<PersonnageBase> equipe = ctx.formation.getEquipe();
        Formation.ajouterInviteTemporaire(equipe, invite);

        System.out.println(">> " + invite.getNom() + " rejoint votre equipe pour ce combat !");
        Stage.ResultatStage resultat = stage.lancer(ctx, equipe, estNouveau);
        System.out.println(">> " + invite.getNom() + " quitte l'equipe apres le combat.");
        return resultat;
    }

    /** Variante a deux invites simultanes (stages 5, 6 et 7). */
    private Stage.ResultatStage lancerStageAvecDeuxInvites(GameContext ctx, Stage stage, boolean estNouveau,
                                                            PersonnageBase invite1, PersonnageBase invite2) {
        EquipementFactory.equiperSetStandard(invite1, EquipementFactory.rareteEnnemiPourNiveau(invite1.getNiveau()));
        EquipementFactory.equiperSetStandard(invite2, EquipementFactory.rareteEnnemiPourNiveau(invite2.getNiveau()));

        ArrayList<PersonnageBase> equipe = ctx.formation.getEquipe();
        Formation.ajouterInviteTemporaire(equipe, invite1);
        Formation.ajouterInviteTemporaire(equipe, invite2);

        System.out.println(">> " + invite1.getNom() + " et " + invite2.getNom() + " rejoignent votre equipe pour ce combat !");
        Stage.ResultatStage resultat = stage.lancer(ctx, equipe, estNouveau);
        System.out.println(">> " + invite1.getNom() + " et " + invite2.getNom() + " quittent l'equipe apres le combat.");
        return resultat;
    }

    private Stage.ResultatStage lancerStage1AvecEquipeHeros(GameContext ctx, Stage stage, boolean estNouveau) {
        // Combat scripte : notre formation n'intervient pas, l'embuscade oppose l'equipe
        // partie en mission (Erza, Jubia, Lucy, Gray, Natsu) a Wolly, Miliana, Shaw et Simon.
        perso_Erza erza = new perso_Erza();
        erza.setNiveau(45);
        erza.setVie(4200);
        erza.setVieMax(4200);
        erza.setAttaque(950);
        erza.setDefense(650);
        erza.setVitesse(190);
        EquipementFactory.equiperSetStandard(erza, EquipementFactory.rareteEnnemiPourNiveau(erza.getNiveau()));

        perso_Jubia_4elements jubia = new perso_Jubia_4elements();
        jubia.setNiveau(45);
        jubia.setVie(3500);
        jubia.setVieMax(3500);
        jubia.setAttaque(750);
        jubia.setDefense(500);
        jubia.setVitesse(198);
        EquipementFactory.equiperSetStandard(jubia, EquipementFactory.rareteEnnemiPourNiveau(jubia.getNiveau()));

        perso_Lucy lucy = new perso_Lucy();
        lucy.setNiveau(45);
        lucy.setVie(3300);
        lucy.setVieMax(3300);
        lucy.setAttaque(650);
        lucy.setDefense(420);
        lucy.setVitesse(200);
        EquipementFactory.equiperSetStandard(lucy, EquipementFactory.rareteEnnemiPourNiveau(lucy.getNiveau()));

        perso_Gray gray = new perso_Gray();
        gray.setNiveau(45);
        gray.setVie(3700);
        gray.setVieMax(3700);
        gray.setAttaque(1000);
        gray.setDefense(550);
        gray.setVitesse(195);
        EquipementFactory.equiperSetStandard(gray, EquipementFactory.rareteEnnemiPourNiveau(gray.getNiveau()));

        perso_Natsu natsu = new perso_Natsu();
        natsu.setNiveau(45);
        natsu.setVie(3600);
        natsu.setVieMax(3600);
        natsu.setAttaque(1050);
        natsu.setDefense(480);
        natsu.setVitesse(210);
        EquipementFactory.equiperSetStandard(natsu, EquipementFactory.rareteEnnemiPourNiveau(natsu.getNiveau()));

        ArrayList<PersonnageBase> equipeFixe = new ArrayList<>();
        equipeFixe.add(erza);
        equipeFixe.add(jubia);
        equipeFixe.add(lucy);
        equipeFixe.add(gray);
        equipeFixe.add(natsu);

        System.out.println(">> Erza, Jubia, Lucy, Gray et Natsu tombent dans une embuscade au casino de la tour !");

        Stage.ResultatStage resultat = stage.lancer(ctx, equipeFixe, estNouveau);

        System.out.println("\n>> Wolly, Miliana, Shaw et Simon barrent la route de l'equipe...");

        return resultat;
    }

    private Stage.ResultatStage lancerStage8AvecErza(GameContext ctx, Stage stage, boolean estNouveau) {
        // Combat scripte : notre formation n'intervient pas, seule Erza affronte Jellal.
        // Stats relevees pour rester competitives face a Jellal(51) (~6200 PV / 2580 ATK avec la formule).
        perso_Erza erza = new perso_Erza();
        erza.setNiveau(51);
        erza.setVie(6800);
        erza.setVieMax(6800);
        erza.setAttaque(2300);
        erza.setDefense(1500);
        erza.setVitesse(210);
        EquipementFactory.equiperSetStandard(erza, EquipementFactory.rareteEnnemiPourNiveau(erza.getNiveau()));

        ArrayList<PersonnageBase> equipeFixe = new ArrayList<>();
        equipeFixe.add(erza);

        System.out.println(">> Erza affronte seule Jellal, au sommet de la Tour du Paradis !");
        System.out.println("   « Jellal... reviens-nous. » — Erza\n");

        Stage.ResultatStage resultat = stage.lancer(ctx, equipeFixe, estNouveau);

        System.out.println("\n>> Le duel dechire les vieux souvenirs d'enfance d'Erza et de Jellal.");

        return resultat;
    }
}
