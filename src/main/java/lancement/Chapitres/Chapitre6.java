package lancement.Chapitres;

import Equipement.EquipementFactory;
import Personnage.PersonnageBase;
import Personnage.pnj.EnnemisGeneriques.*;
import Personnage.pnj.Chapitre6.*;
import Personnage.pnj.Chapitre2.EnnemiLeon;
import Personnage.pnj.Chapitre2.EnnemiCherry;
import Personnage.FairyTail.perso_Natsu;
import Personnage.FairyTail.perso_Gray;
import Personnage.FairyTail.perso_Erza;
import Personnage.FairyTail.perso_Lucy;
import Personnage.FairyTail.perso_Leon;
import Personnage.FairyTail.perso_Hibiki;
import Personnage.FairyTail.perso_Jura;
import Personnage.FairyTail.perso_jellal;
import Personnage.FairyTail.perso_Natsu_Etherion;
import lancement.GameContext;
import lancement.Formation;
import lancement.Stage;
import java.util.ArrayList;
import java.util.Scanner;

public class Chapitre6 implements Chapitre {

    private static final int NB_STAGES = 10;
    private final boolean[] stagesDebloques = new boolean[NB_STAGES + 1];
    private final boolean[] stagesReussis   = new boolean[NB_STAGES + 1];

    public Chapitre6() {
        stagesDebloques[1] = true;
    }

    public void afficher(GameContext ctx, Scanner scanner) {
        boolean retour = false;

        while (!retour) {
            ctx.gestionnaireEnergie.mettreAJourRecharge();

            System.out.println("\n========================================");
            System.out.println("   CHAPITRE 6 — " + getNomChapitre());
            System.out.println("========================================");
            System.out.println("Or : " + String.format("%.0f", ctx.joueur.getOr())
                    + "  |  " + ctx.gestionnaireEnergie.afficherEnergie());
            System.out.println();

            for (int i = 1; i <= NB_STAGES; i++) {
                boolean jouable = ctx.gestionnaireQuetes.estStageJouable(6, i, false);
                String etat = stagesReussis[i] ? "[OK]  "
                        : !stagesDebloques[i]  ? "[###] "
                        : jouable              ? "[  ]  "
                        :                        "[QUETE] ";
                String etoiles = ctx.gestionnaireEtoiles.getEtoiles(6, i, false).afficher();
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
            } else if (!ctx.gestionnaireQuetes.estStageJouable(6, choix, false)) {
                System.out.println("Acceptez d'abord la quete associee a ce stage (menu Quetes).");
            } else {
                lancerStage(ctx, choix);
            }
        }
    }

    /**
     * Lance le stage donne (combats scriptes stages 1, 2, 5 et 10 ; invites temporaires stages
     * 3, 4, 6, 7, 8 et 9) et applique les recompenses en cas de victoire. Suppose que le stage
     * est deja debloque. Reutilisable par la console et l'interface graphique.
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
            case 1 -> lancerStage1EquipeHeros(ctx, stage, estNouveau);
            case 2 -> lancerStage2EquipeHeros(ctx, stage, estNouveau);
            case 3 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(6, 3);
                perso_Gray gray = Formation.creerInvite(perso_Gray::new, niv, 3700, 1000, 550, 195);
                perso_Leon leon = Formation.creerInvite(perso_Leon::new, niv, 3200, 850, 480, 190);
                yield lancerStageAvecDeuxInvites(ctx, stage, estNouveau, gray, leon);
            }
            case 4 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(6, 4);
                perso_Lucy lucy     = Formation.creerInvite(perso_Lucy::new, niv, 3300, 650, 420, 200);
                perso_Hibiki hibiki = Formation.creerInvite(perso_Hibiki::new, niv, 3400, 700, 500, 200);
                yield lancerStageAvecDeuxInvites(ctx, stage, estNouveau, lucy, hibiki);
            }
            case 5 -> {
                // Combat scripte : Jura, largement plus fort qu'Hoteye ici, l'affronte seul.
                perso_Jura jura = Formation.creerInvite(perso_Jura::new,
                        CourbeChapitres.niveauEnnemiPourStage(6, 5), 9000, 2200, 1800, 250);
                yield lancerStageSolo(ctx, stage, estNouveau, jura);
            }
            case 6 -> {
                perso_Natsu invite = Formation.creerInvite(perso_Natsu::new, CourbeChapitres.niveauEnnemiPourStage(6, 6), 3600, 1050, 480, 210);
                yield lancerStageAvecInvite(ctx, stage, estNouveau, invite);
            }
            case 7 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(6, 7);
                perso_jellal jellal = Formation.creerInvite(perso_jellal::new, niv, 3800, 1100, 500, 220);
                perso_Erza erza     = Formation.creerInvite(perso_Erza::new, niv, 4200, 950, 650, 190);
                yield lancerStageAvecDeuxInvites(ctx, stage, estNouveau, jellal, erza);
            }
            case 8 -> {
                perso_Natsu invite = Formation.creerInvite(perso_Natsu::new, CourbeChapitres.niveauEnnemiPourStage(6, 8), 3600, 1050, 480, 210);
                yield lancerStageAvecInvite(ctx, stage, estNouveau, invite);
            }
            case 9 -> {
                // Jura non boosté (contrairement au stage 5) : rejoint l'equipe comme un invite
                // normal. Etant Tank, il remplace automatiquement le Tank deja present.
                perso_Jura invite = Formation.creerInvite(perso_Jura::new, CourbeChapitres.niveauEnnemiPourStage(6, 9), 5500, 900, 900, 180);
                yield lancerStageAvecInvite(ctx, stage, estNouveau, invite);
            }
            case 10 -> {
                // Combat scripte : Natsu Etherion, seul, doit venir a bout de Zero.
                perso_Natsu_Etherion natsu = Formation.creerInvite(perso_Natsu_Etherion::new,
                        CourbeChapitres.niveauEnnemiPourStage(6, 10), 16000, 5000, 2200, 320);
                yield lancerStageSolo(ctx, stage, estNouveau, natsu);
            }
            default -> stage.lancer(ctx, ctx.formation.getEquipe(), estNouveau);
        };

        if (resultatStage.victoire) {
            stagesReussis[numero] = true;
            if (numero < NB_STAGES) {
                stagesDebloques[numero + 1] = true;
                System.out.println(">> Stage " + (numero + 1) + " debloque !");
            } else {
                System.out.println(">> Felicitations ! Chapitre 6 termine !");
            }

            ctx.gestionnaireQuetes.notifierOrGagne(stage.getRecompenseOr());
            ctx.gestionnaireQuetes.notifierStageFini(6, numero, false,
                    ctx.joueur, ctx.menuRecrutement, ctx.personnagesRecruites);
            ctx.gestionnaireEtoiles.mettreAJour(6, numero, false,
                    resultatStage.victoire, resultatStage.sansAllieMort, resultatStage.enMoinsDe10Tours);
        }
        return resultatStage;
    }

    private Stage construireStage(int numero) {
        ArrayList<PersonnageBase> e = new ArrayList<>();

        return switch (numero) {
            // Stage 1 — Combat scripte : Natsu, Gray, Erza, Lucy et le personnage principal
            // contre les mages allies invites (Ren, Hibiki, Leon, Jura, Cherry).
            case 1 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(6, 1);
                e.add(new EnnemiRen(niv));
                e.add(new EnnemiHibiki(niv));
                e.add(new EnnemiLeon(niv));
                // Jura affaibli de 20% pour ce stage precis (equilibrage demande) : ecrase les
                // stats apres construction plutot que de toucher EnnemiJura.java, qui est
                // partage avec le stage 1 du Chapitre 6 Elite (non concerne par ce changement).
                EnnemiJura jura = new EnnemiJura(niv);
                jura.setVie(jura.getVie() * 0.8);
                jura.setVieMax(jura.getVieMax() * 0.8);
                jura.setAttaque(jura.getAttaque() * 0.8);
                jura.setDefense(jura.getDefense() * 0.8);
                jura.setVitesse(jura.getVitesse() * 0.8);
                e.add(jura);
                e.add(new EnnemiCherry(niv));
                yield new Stage(1, "Prologue — L'alliance des guildes", 10200, 132, e);
            }
            // Stage 2 — Combat scripte : meme equipe fixe contre Oracion Seis.
            case 2 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(6, 2);
                e.add(new EnnemiAngel(niv));
                e.add(new EnnemiHoteye(niv));
                e.add(new EnnemiMidnight(niv));
                e.add(new EnnemiRacer(niv));
                e.add(new EnnemiBrain(niv));
                yield new Stage(2, "Oracions seis vs l'alliance des guildes", 10500, 136, e);
            }
            // Stage 3 — Gray et Leon rejoignent l'equipe (remplacent 2 DPS) contre Racer et des generiques.
            case 3 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(6, 3);
                e.add(new EnnemiMage5Tank(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiRacer(niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage6Debuff(Variante.CHAPITRE_6, niv));
                yield new Stage(3, "Gray et leon vs racer", 10800, 140, e);
            }
            // Stage 4 — Lucy et Hibiki rejoignent l'equipe contre Angel et des generiques.
            case 4 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(6, 4);
                e.add(new EnnemiAngel(niv));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage7DPS(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_6, niv));
                yield new Stage(4, "Combat de constellasioniste", 11100, 144, e);
            }
            // Stage 5 — Combat scripte : Jura (seul, largement plus fort) contre Hoteye.
            case 5 -> {
                e.add(new EnnemiHoteye(CourbeChapitres.niveauEnnemiPourStage(6, 5)));
                yield new Stage(5, "Duel entre Jura et Hoy-eyes", 11400, 148, e);
            }
            // Stage 6 — Natsu rejoint l'equipe contre Cobra et des generiques.
            case 6 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(6, 6);
                e.add(new EnnemiCobra(niv));
                e.add(new EnnemiMage5Tank(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage6Debuff(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_6, niv));
                yield new Stage(6, "Cobras vs Natsu", 11700, 152, e);
            }
            // Stage 7 — Jellal et Erza rejoignent l'equipe contre Midnight et des generiques.
            case 7 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(6, 7);
                e.add(new EnnemiMidnight(niv));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage6Debuff(Variante.CHAPITRE_6, niv));
                yield new Stage(7, "Jellal et erza vs midnight", 12000, 156, e);
            }
            // Stage 8 — Natsu rejoint l'equipe contre Brain et des generiques.
            case 8 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(6, 8);
                e.add(new EnnemiBrain(niv));
                e.add(new EnnemiMage5Tank(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage7DPS(Variante.CHAPITRE_6, niv));
                yield new Stage(8, "Natsu vs Brain", 12300, 160, e);
            }
            // Stage 9 — Jura (non boosté) rejoint l'equipe (remplace le Tank) contre Brain et des generiques.
            case 9 -> {
                int niv = CourbeChapitres.niveauEnnemiPourStage(6, 9);
                e.add(new EnnemiBrain(niv));
                e.add(new EnnemiMage9Tank(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage1DPS(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage2DPS(Variante.CHAPITRE_6, niv));
                e.add(new EnnemiMage3Soigneur(Variante.CHAPITRE_6, niv));
                yield new Stage(9, "Jura vs Brain", 12700, 165, e);
            }
            // Stage 10 — Combat scripte : Natsu Etherion (seul) contre Zero.
            case 10 -> {
                e.add(new EnnemiZero(CourbeChapitres.niveauEnnemiPourStage(6, 10)));
                yield new Stage(10, "Natsu vs la nouvelle forme de Brain", 13200, 172, e);
            }
            default -> new Stage(numero, "???", 0, 0, e);
        };
    }

    public String getTitreStage(int numero) {
        return switch (numero) {
            case 1  -> "Prologue — L'alliance des guildes";
            case 2  -> "Oracions seis vs l'alliance des guildes";
            case 3  -> "Gray et leon vs racer";
            case 4  -> "Combat de constellasioniste";
            case 5  -> "Duel entre Jura et Hoy-eyes";
            case 6  -> "Cobras vs Natsu";
            case 7  -> "Jellal et erza vs midnight";
            case 8  -> "Natsu vs Brain ";
            case 9  -> "Jura vs Brain ";
            case 10 -> "Natsu vs la nouvelle forme de Brain";
            default -> "???";
        };
    }

    public String getNomChapitre() { return "Oracions seis"; }

    public boolean[] getStagesDebloques() { return stagesDebloques; }
    public boolean[] getStagesReussis()   { return stagesReussis; }
    public void setStagesDebloques(boolean[] d) { for (int i = 0; i <= NB_STAGES; i++) stagesDebloques[i] = d[i]; }
    public void setStagesReussis(boolean[] r)   { for (int i = 0; i <= NB_STAGES; i++) stagesReussis[i]   = r[i]; }

    // ── Combats scriptes (stages 1 et 2) ────────────────────────────────────

    /** Stage 1 : equipe fixe (Natsu, Gray, Erza, Lucy, personnage principal), pas la formation reelle. */
    private Stage.ResultatStage lancerStage1EquipeHeros(GameContext ctx, Stage stage, boolean estNouveau) {
        int niv = CourbeChapitres.niveauEnnemiPourStage(6, 1);

        perso_Natsu natsu = Formation.creerInvite(perso_Natsu::new, niv, 3600, 1050, 480, 210);
        EquipementFactory.equiperSetStandard(natsu, EquipementFactory.rareteEnnemiPourNiveau(natsu.getNiveau()));
        perso_Gray gray = Formation.creerInvite(perso_Gray::new, niv, 3700, 1000, 550, 195);
        EquipementFactory.equiperSetStandard(gray, EquipementFactory.rareteEnnemiPourNiveau(gray.getNiveau()));
        // Erza renforcee de 20% pour ce stage precis (equilibrage demande) : uniquement ces
        // stats litterales, pas la version invitee au stage 7 plus loin dans ce fichier.
        perso_Erza erza = Formation.creerInvite(perso_Erza::new, niv, 4200 * 1.2, 950 * 1.2, 650 * 1.2, 190 * 1.2);
        EquipementFactory.equiperSetStandard(erza, EquipementFactory.rareteEnnemiPourNiveau(erza.getNiveau()));
        perso_Lucy lucy = Formation.creerInvite(perso_Lucy::new, niv, 3300, 650, 420, 200);
        EquipementFactory.equiperSetStandard(lucy, EquipementFactory.rareteEnnemiPourNiveau(lucy.getNiveau()));

        ArrayList<PersonnageBase> equipeFixe = new ArrayList<>();
        equipeFixe.add(natsu);
        equipeFixe.add(gray);
        equipeFixe.add(erza);
        equipeFixe.add(lucy);
        equipeFixe.add(ctx.joueur);

        System.out.println(">> Natsu, Gray, Erza, Lucy et vous-meme affrontez les mages allies venus a la rencontre !");
        return stage.lancer(ctx, equipeFixe, estNouveau);
    }

    /** Stage 2 : meme equipe fixe que le stage 1, contre Oracion Seis. */
    private Stage.ResultatStage lancerStage2EquipeHeros(GameContext ctx, Stage stage, boolean estNouveau) {
        int niv = CourbeChapitres.niveauEnnemiPourStage(6, 2);

        perso_Natsu natsu = Formation.creerInvite(perso_Natsu::new, niv, 3600, 1050, 480, 210);
        EquipementFactory.equiperSetStandard(natsu, EquipementFactory.rareteEnnemiPourNiveau(natsu.getNiveau()));
        perso_Gray gray = Formation.creerInvite(perso_Gray::new, niv, 3700, 1000, 550, 195);
        EquipementFactory.equiperSetStandard(gray, EquipementFactory.rareteEnnemiPourNiveau(gray.getNiveau()));
        perso_Erza erza = Formation.creerInvite(perso_Erza::new, niv, 4200, 950, 650, 190);
        EquipementFactory.equiperSetStandard(erza, EquipementFactory.rareteEnnemiPourNiveau(erza.getNiveau()));
        perso_Lucy lucy = Formation.creerInvite(perso_Lucy::new, niv, 3300, 650, 420, 200);
        EquipementFactory.equiperSetStandard(lucy, EquipementFactory.rareteEnnemiPourNiveau(lucy.getNiveau()));

        ArrayList<PersonnageBase> equipeFixe = new ArrayList<>();
        equipeFixe.add(natsu);
        equipeFixe.add(gray);
        equipeFixe.add(erza);
        equipeFixe.add(lucy);
        equipeFixe.add(ctx.joueur);

        System.out.println(">> L'alliance des guildes affronte Oracion Seis !");
        return stage.lancer(ctx, equipeFixe, estNouveau);
    }

    // ── Invites temporaires (stages 3, 4, 6, 7, 8 et 9) ─────────────────────

    private Stage.ResultatStage lancerStageAvecInvite(GameContext ctx, Stage stage, boolean estNouveau, PersonnageBase invite) {
        EquipementFactory.equiperSetStandard(invite, EquipementFactory.rareteEnnemiPourNiveau(invite.getNiveau()));

        ArrayList<PersonnageBase> equipe = ctx.formation.getEquipe();
        Formation.ajouterInviteTemporaire(equipe, invite);

        if (equipe.contains(invite)) {
            System.out.println(">> " + invite.getNom() + " rejoint votre equipe pour ce combat !");
        } else {
            System.out.println(">> " + invite.getNom() + " est deja dans votre equipe, il n'y a pas besoin de l'inviter.");
        }
        return stage.lancer(ctx, equipe, estNouveau);
    }

    private Stage.ResultatStage lancerStageAvecDeuxInvites(GameContext ctx, Stage stage, boolean estNouveau,
                                                            PersonnageBase invite1, PersonnageBase invite2) {
        EquipementFactory.equiperSetStandard(invite1, EquipementFactory.rareteEnnemiPourNiveau(invite1.getNiveau()));
        EquipementFactory.equiperSetStandard(invite2, EquipementFactory.rareteEnnemiPourNiveau(invite2.getNiveau()));

        ArrayList<PersonnageBase> equipe = ctx.formation.getEquipe();
        Formation.ajouterInviteTemporaire(equipe, invite1);
        Formation.ajouterInviteTemporaire(equipe, invite2);

        for (PersonnageBase invite : new PersonnageBase[]{invite1, invite2}) {
            if (equipe.contains(invite)) {
                System.out.println(">> " + invite.getNom() + " rejoint votre equipe pour ce combat !");
            }
        }
        return stage.lancer(ctx, equipe, estNouveau);
    }

    /** Combat scripte en solo : remplace entierement la formation du joueur par un seul personnage calibre. */
    private Stage.ResultatStage lancerStageSolo(GameContext ctx, Stage stage, boolean estNouveau, PersonnageBase solo) {
        EquipementFactory.equiperSetStandard(solo, EquipementFactory.rareteEnnemiPourNiveau(solo.getNiveau()));

        ArrayList<PersonnageBase> equipeFixe = new ArrayList<>();
        equipeFixe.add(solo);

        System.out.println(">> " + solo.getNom() + " affronte seul(e) cet ennemi !");
        return stage.lancer(ctx, equipeFixe, estNouveau);
    }
}
