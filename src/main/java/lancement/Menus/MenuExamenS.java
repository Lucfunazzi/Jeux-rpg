package lancement.Menus;

import Combat.Combat;
import Equipement.Inventaire;
import Equipement.Pierre;
import Personnage.FairyTail.*;
import Personnage.PersonnageBase;
import Personnage.pnj.Chapitre1.EnnemiMage1DPS;
import Personnage.pnj.Chapitre1.EnnemiMage2DPS;
import Personnage.pnj.Chapitre1.EnnemiMage3Soigneur;
import Personnage.pnj.Chapitre1.EnnemiMage4Buff;
import Personnage.pnj.Chapitre1.EnnemiMage5Tank;
import Personnage.pnj.Chapitre1.EnnemiMage6Debuff;
import Personnage.pnj.Chapitre1.EnnemiMage7DPS;
import Personnage.pnj.Chapitre1.EnnemiMage9Tank;
import lancement.ExamenS.StageExamenS;
import lancement.GameContext;
import lancement.Gestionnaires.GestionnaireExamenS;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 * Examen de Rang S : 10 stages, deblocage sequentiel, 1 tentative par stage et par jour.
 * Recompense : "Boite de pierre Lv.1" (1 pierre de niveau 1 au hasard une fois ouverte).
 * Premiere reussite d'un stage = boite garantie. Reussites suivantes = 30% de chance.
 */
public class MenuExamenS {

    public static final String MATERIAU_BOITE_PIERRE_LV1 = "Boite de pierre Lv.1";

    public void afficher(GameContext ctx, Scanner scanner) {
        if (ctx.joueur.getNiveau() < GestionnaireExamenS.NIVEAU_REQUIS) {
            System.out.println("L'Examen de Rang S se debloque au niveau " + GestionnaireExamenS.NIVEAU_REQUIS + " !");
            return;
        }
        GestionnaireExamenS g = ctx.gestionnaireExamenS;
        g.mettreAJour();

        boolean retour = false;
        while (!retour) {
            System.out.println("\n========================================");
            System.out.println("           EXAMEN DE RANG S");
            System.out.println("========================================");
            System.out.println("1 tentative par stage et par jour (reset a minuit).");
            System.out.println("Premiere reussite d'un stage : boite garantie. Ensuite : 30% de chance.");
            System.out.println();

            for (int i = 1; i <= GestionnaireExamenS.NB_STAGES; i++) {
                String etat;
                if (!g.estDebloque(i))            etat = "[VERROUILLE]";
                else if (g.estFaitAujourdhui(i))  etat = "[FAIT AUJOURD'HUI]";
                else if (g.estDejaReussi(i))      etat = "[30% de chance]";
                else                               etat = "[100% - premiere fois]";
                System.out.println("  " + i + ". Stage " + i + "  " + etat);
            }
            System.out.println();
            System.out.println("R. Ratisser (reclame tous les stages deja reussis, sans combat)");
            System.out.println("0. Retour");
            System.out.print("Votre choix : ");

            String choix = scanner.nextLine().trim();
            if (choix.equalsIgnoreCase("R")) { ratisser(ctx); continue; }
            if (choix.equals("0")) { retour = true; continue; }

            try {
                int stage = Integer.parseInt(choix);
                if (stage < 1 || stage > GestionnaireExamenS.NB_STAGES) {
                    System.out.println("Choix invalide.");
                    continue;
                }
                lancerStage(stage, ctx, scanner);
            } catch (NumberFormatException ex) {
                System.out.println("Choix invalide.");
            }
        }
    }

    /** Resultat d'une tentative, reutilisable par la GUI (relecture du combat). */
    public record ResultatExamenS(boolean lance, boolean victoire, boolean boiteGagnee,
                                   List<Combat.PersonnageSnapshot> etatInitial,
                                   List<Combat.CombatEvent> evenements) {}

    public ResultatExamenS lancerStage(int stage, GameContext ctx, Scanner scanner) {
        GestionnaireExamenS g = ctx.gestionnaireExamenS;
        g.mettreAJour();

        if (!g.estDebloque(stage)) {
            System.out.println("Ce stage est verrouille.");
            return new ResultatExamenS(false, false, false, null, null);
        }
        if (g.estFaitAujourdhui(stage)) {
            System.out.println("Vous avez deja fait ce stage aujourd'hui. Revenez demain !");
            return new ResultatExamenS(false, false, false, null, null);
        }
        if (ctx.formation.getEquipe().isEmpty()) {
            System.out.println("Votre formation est vide ! Ajoutez des personnages d'abord.");
            return new ResultatExamenS(false, false, false, null, null);
        }

        ArrayList<PersonnageBase> ennemis = creerEnnemis(stage, ctx);
        StageExamenS combatStage = new StageExamenS(stage, ennemis);
        double bonusTitre = ctx.gestionnaireTitres.getBonusActif();
        boolean victoire = combatStage.lancer(ctx.formation.getEquipe(), bonusTitre);

        boolean boiteGagnee = false;
        if (victoire) {
            boiteGagnee = g.enregistrerReussite(stage);
            System.out.println("\n>> Victoire !");
            if (boiteGagnee) {
                ctx.inventaire.ajouterMateriau(MATERIAU_BOITE_PIERRE_LV1, 1);
                System.out.println("   + 1x " + MATERIAU_BOITE_PIERRE_LV1);
            } else {
                System.out.println("   Pas de boite cette fois...");
            }
            ctx.sauvegarde.sauvegarder(ctx);
            System.out.println(">> Partie sauvegardee automatiquement.");
        } else {
            System.out.println("\n>> Defaite... Aucune recompense.");
        }

        return new ResultatExamenS(true, victoire, boiteGagnee,
                combatStage.getEtatInitial(), combatStage.getEvenements());
    }

    /** Reclame instantanement, sans combat, tous les stages deja reussis mais pas encore faits aujourd'hui. */
    public String ratisser(GameContext ctx) {
        GestionnaireExamenS g = ctx.gestionnaireExamenS;
        g.mettreAJour();

        int stagesReclames = 0;
        int boites = 0;
        for (int i = 1; i <= GestionnaireExamenS.NB_STAGES; i++) {
            if (g.estDejaReussi(i) && !g.estFaitAujourdhui(i)) {
                stagesReclames++;
                if (g.enregistrerReussite(i)) {
                    ctx.inventaire.ajouterMateriau(MATERIAU_BOITE_PIERRE_LV1, 1);
                    boites++;
                }
            }
        }

        String message = stagesReclames == 0
                ? "Aucun stage a ratisser (deja fait aujourd'hui, ou jamais reussi)."
                : stagesReclames + " stage(s) ratisse(s) ! + " + boites + " boite(s) de pierre Lv.1.";

        if (stagesReclames > 0) ctx.sauvegarde.sauvegarder(ctx);
        System.out.println(message);
        return message;
    }

    /** Ouvre 1 Boite de pierre Lv.1 : retire 1 boite, donne 1 pierre de niveau 1 d'un type aleatoire. */
    public static String ouvrirBoite(Inventaire inventaire) {
        int stock = inventaire.getQuantiteMateriau(MATERIAU_BOITE_PIERRE_LV1);
        if (stock <= 0) return "Aucune " + MATERIAU_BOITE_PIERRE_LV1 + " en stock.";

        inventaire.retirerMateriau(MATERIAU_BOITE_PIERRE_LV1, 1);
        Pierre.Type[] types = Pierre.Type.values();
        Pierre.Type type = types[(int) (Math.random() * types.length)];
        inventaire.ajouterPierre(type, 1, 1);
        return "Boite ouverte : vous obtenez " + new Pierre(type, 1) + " !";
    }

    // ── Construction des equipes ennemies (niveau = numero du stage) ───────
    private ArrayList<PersonnageBase> creerEnnemis(int stage, GameContext ctx) {
        ArrayList<PersonnageBase> ennemis = new ArrayList<>();
        switch (stage) {
            case 5 -> {
                for (String nom : List.of("Duc Everlue", "Eligoal", "Cherry", "Tobi", "Bora")) {
                    ennemis.add(monterAuNiveau(ctx.menuRecrutement.creerPersonnage(nom), stage));
                }
            }
            case 10 -> {
                for (String nom : List.of("Yuka", "Leon", "Cherry", "Levy", "Lisanna")) {
                    ennemis.add(monterAuNiveau(ctx.menuRecrutement.creerPersonnage(nom), stage));
                }
            }
            case 1 -> ennemis.add(new EnnemiMage1DPS(stage));
            case 2 -> ennemis.add(new EnnemiMage2DPS(stage));
            case 3 -> { ennemis.add(new EnnemiMage4Buff(stage)); ennemis.add(new EnnemiMage1DPS(stage)); }
            case 4 -> {ennemis.add(new EnnemiMage5Tank(stage));  ennemis.add(new EnnemiMage1DPS(stage));}
            case 6 -> { ennemis.add(new EnnemiMage1DPS(stage)); ennemis.add(new EnnemiMage2DPS(stage)); ennemis.add(new EnnemiMage5Tank(stage)); }
            case 7 -> { ennemis.add(new EnnemiMage4Buff(stage)); ennemis.add(new EnnemiMage5Tank(stage)); ennemis.add(new EnnemiMage2DPS(stage)); }
            case 8 -> { ennemis.add(new EnnemiMage7DPS(stage)); ennemis.add(new EnnemiMage9Tank(stage)); ennemis.add(new EnnemiMage2DPS(stage)); ennemis.add(new EnnemiMage3Soigneur(stage)); }
            case 9 -> { ennemis.add(new EnnemiMage6Debuff(stage)); ennemis.add(new EnnemiMage3Soigneur(stage));ennemis.add(new EnnemiMage2DPS(stage));
            ennemis.add(new EnnemiMage2DPS(stage));ennemis.add(new EnnemiMage5Tank(stage));}
            default -> { }
        }
        return ennemis;
    }

    private PersonnageBase monterAuNiveau(PersonnageBase p, int n) {
        while (p.getNiveau() < n) p.monterDeNiveau();
        return p;
    }
}
