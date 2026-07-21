package lancement.Chapitres;

import Personnage.PersonnageBase;

import Personnage.pnj.Chapitre2.*;
import Personnage.FairyTail.perso_Lucy;
import Personnage.FairyTail.perso_Natsu;
import Personnage.FairyTail.perso_Gray;
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
            System.out.println("   CHAPITRE 2 — L'Île de Galuna");
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
                Stage.ResultatStage resultatStage = switch (choix) {
                    case 3 -> {
                        perso_Lucy invite = new perso_Lucy();
                        invite.setNiveau(15);
                        invite.setVie(900);
                        invite.setVieMax(900);
                        invite.setAttaque(140);
                        invite.setDefense(95);
                        invite.setVitesse(160);
                        yield lancerStageAvecInvite(ctx, stage, estNouveau, invite);
                    }
                    case 4, 5 -> {
                        perso_Natsu invite = new perso_Natsu();
                        invite.setNiveau(18);
                        invite.setVie(1100);
                        invite.setVieMax(1100);
                        invite.setAttaque(220);
                        invite.setDefense(130);
                        invite.setVitesse(140);
                        yield lancerStageAvecInvite(ctx, stage, estNouveau, invite);
                    }
                    case 6, 8 -> {
                        perso_Gray invite = new perso_Gray();
                        invite.setNiveau(19);
                        invite.setVie(950);
                        invite.setVieMax(950);
                        invite.setAttaque(210);
                        invite.setDefense(140);
                        invite.setVitesse(120);
                        yield lancerStageAvecInvite(ctx, stage, estNouveau, invite);
                    }
                    case 9  -> lancerStage9AvecUl(ctx, stage, estNouveau);
                    default -> stage.lancer(ctx, ctx.formation.getEquipe(), estNouveau);
                };
                boolean victoire = resultatStage.victoire;

                if (victoire) {
                    stagesReussis[choix] = true;
                    if (choix < NB_STAGES) {
                        stagesDebloques[choix + 1] = true;
                        System.out.println(">> Stage " + (choix + 1) + " debloque !");
                    } else {
                        System.out.println(">> Félicitations ! Vous avez sauvé l'île de Galuna !");
                        // Cadeau de fin de chapitre 2 — première victoire uniquement
                        if (estNouveau) {
                            ctx.inventaire.ajouterMateriau("Echarpe blanche d'Ignir", 50);
                            System.out.println(">> Cadeau : +50 Echarpe(s) blanche d'Ignir !");
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
            case 1  -> { ennemis.add(new EnnemiMage1DPS()); ennemis.add(new EnnemiMage1DPS());
                         ennemis.add(new EnnemiMage5Tank()); ennemis.add(new EnnemiMage3Soigneur());
                         ennemis.add(new EnnemiMage2DPS());
                         return new Stage(1, "Prologue Chapitre 2", 1500, 0, ennemis); } 
            
            case 2  -> { ennemis.add(new EnnemiMage4Buff()); ennemis.add(new EnnemiMage2DPS());
                        ennemis.add(new EnnemiMage5Tank()); ennemis.add(new EnnemiMage3Soigneur());
                         ennemis.add(new EnnemiMage2DPS());
                         return new Stage(2, "Arrivée a l'ile de galuna", 1800, 0, ennemis); }
            
            case 3  -> { ennemis.add(new EnnemiCherry()); ennemis.add(new EnnemiMage2DPS()); ennemis.add(new EnnemiMage1DPS()); 
                         ennemis.add(new EnnemiMage5Tank()); ennemis.add(new EnnemiMage4Buff());
            
                         return new Stage(3, "Lucy VS Cherry", 2100, 0, ennemis); } // Lucy rejoint l'equipe : voir lancerStage3AvecLucy
            
            case 4  -> { ennemis.add(new EnnemiYuka()); ennemis.add(new EnnemiMage3Soigneur());
                         ennemis.add(new EnnemiMage2DPS()); ennemis.add(new EnnemiMage1DPS());
                         ennemis.add(new EnnemiMage6Debuff());
                         
                         return new Stage(4, "Yuka contre Natsu", 2500, 0, ennemis); } // Natsu rejoint l'equipe : voir lancerStageAvecNatsu
            
            case 5  -> { ennemis.add(new EnnemiTobi()); ennemis.add(new EnnemiMage3Soigneur()); ennemis.add(new EnnemiMage2DPS());
                         ennemis.add(new EnnemiMage9Tank()); ennemis.add(new EnnemiMage1DPS());
            
                         return new Stage(5, "Tobi contre Natsu", 2800, 0, ennemis); }// Natsu rejoint l'equipe : voir lancerStageAvecNatsu
            
            case 6  -> { ennemis.add(new EnnemiLeon()); ennemis.add(new EnnemiMage1DPS());
                         ennemis.add(new EnnemiMage6Debuff()); ennemis.add(new EnnemiMage3Soigneur());
                         ennemis.add(new EnnemiMage9Tank());
                         
                         return new Stage(6, "Gray vs Leon 1", 3200, 0, ennemis); }//Gray rejoint l'equipe : voir lancerStageAvecGray
            
            case 7  -> { ennemis.add(new EnnemiHomme_mysterieux()); ennemis.add(new EnnemiMage1DPS()); ennemis.add(new EnnemiMage2DPS());
                           ennemis.add(new EnnemiMage5Tank()); //combat avec Natsu contre  homme mysterieux ( ultia en gros combat demonstation)
                         return new Stage(7, "Natsu contre l'homme mysterieux", 3800, 0, ennemis); }
            case 8  -> { ennemis.add(new EnnemiLeon()); ennemis.add(new EnnemiMage1DPS()); ennemis.add(new EnnemiMage5Tank()); // Gray rejoint l'equipe : voir lancerStageAvecGray
                        ennemis.add(new EnnemiMage4Buff()); ennemis.add(new EnnemiMage3Soigneur());
                         return new Stage(8, "Gray vs Leon part 2", 4300, 0, ennemis); }
            case 9  -> { ennemis.add(new EnnemiDeliora_passe()); // Combat flashback : Ul seule contre Deliora, voir lancerStage9AvecUl
                         return new Stage(9, "Le passé de Gray", 4800, 0, ennemis); }
            case 10 -> { ennemis.add(new EnnemiDeliora());
                         return new Stage(10, "Deliora le demon", 5500, 0, ennemis); }
            default -> { return new Stage(numero, "???", 0, 0, ennemis); }
        }
    }

    private String getTitreStage(int numero) {
        return switch (numero) {
            case 1  -> "Débarquement sur l'île maudite";
            case 2  -> "Tobi, mage de glace de l'île";
            case 3  -> "Yuka, l'annuleur de magie";
            case 4  -> "Alliance des mages de l'île";
            case 5  -> "Chery, gardienne du sanctuaire";
            case 6  -> "Le trio de l'île réuni";
            case 7  -> "Leon Bastia, chef des mages";
            case 8  -> "La dernière défense de l'île";
            case 9  -> "Equipe 8 — Second Match";
            case 10 -> "Orochimaru — Le Ninja Legendaire !";
            default -> "???";
        };
    }

    public boolean[] getStagesDebloques() { return stagesDebloques; }
    public boolean[] getStagesReussis()   { return stagesReussis; }
    public void setStagesDebloques(boolean[] d) { for (int i = 0; i <= NB_STAGES; i++) stagesDebloques[i] = d[i]; }
    public void setStagesReussis(boolean[] r)   { for (int i = 0; i <= NB_STAGES; i++) stagesReussis[i]   = r[i]; }

    // ── Invites temporaires (stage 3, 4/5, 6/8) ───────────────────────────

    /**
     * Injecte un invite deja configure (niveau/stats) dans l'equipe pour la
     * duree d'un seul combat, puis lance le stage. Le role de remplacement
     * est lu directement sur l'invite (perso_Lucy/perso_Natsu/perso_Gray
     * portent deja le bon role).
     */
    private Stage.ResultatStage lancerStageAvecInvite(GameContext ctx, Stage stage, boolean estNouveau, PersonnageBase invite) {
        ArrayList<PersonnageBase> equipe = ctx.formation.getEquipe();
        ajouterInviteEnRemplacantPlusFaible(equipe, invite, invite.getRole());

        System.out.println(">> " + invite.getNom() + " rejoint votre equipe pour ce combat !");
        Stage.ResultatStage resultat = stage.lancer(ctx, equipe, estNouveau);
        System.out.println(">> " + invite.getNom() + " quitte l'equipe apres le combat.");
        return resultat;
    }

    private Stage.ResultatStage lancerStage9AvecUl(GameContext ctx, Stage stage, boolean estNouveau) {
        // Flashback : le passe de Gray, avant Fairy Tail — l'equipe du joueur
        // n'existait pas encore. Seule Ul affronte Deliora.
        EnnemiUlFlashback ulFlashback = new EnnemiUlFlashback();

        ArrayList<PersonnageBase> equipeFixe = new ArrayList<>();
        equipeFixe.add(ulFlashback);

        System.out.println(">> Flashback : Ul Milkovich affronte seule le demon Deliora !");
        System.out.println("   « Je vais sceller ce monstre, quoi qu'il m'en coute. » — Ul\n");

        Stage.ResultatStage resultat = stage.lancer(ctx, equipeFixe, estNouveau);

        System.out.println("\n>> Ul scelle Deliora dans la glace eternelle... au prix de son propre corps.");

        return resultat;
    }

    /**
     * Ajoute un invite a l'equipe. Si le role vise compte deja 3 membres,
     * remplace le plus faible (niveau le plus bas) de ce role — en ignorant
     * toujours le personnage principal. Sinon, l'invite s'ajoute en plus.
     */
    private void ajouterInviteEnRemplacantPlusFaible(ArrayList<PersonnageBase> equipe, PersonnageBase invite, String role) {
        long nbDuRole = equipe.stream().filter(p -> p.getRole().equals(role)).count();
        if (nbDuRole < 3) {
            equipe.add(invite);
            return;
        }

        PersonnageBase plusFaible = null;
        for (PersonnageBase p : equipe) {
            if (p.getRole().equals(role) && !p.estPersonnagePrincipal()) {
                if (plusFaible == null || p.getNiveau() < plusFaible.getNiveau()) {
                    plusFaible = p;
                }
            }
        }

        if (plusFaible != null) {
            equipe.remove(plusFaible);
        }
        equipe.add(invite);
    }
}