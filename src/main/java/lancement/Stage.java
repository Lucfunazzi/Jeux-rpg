package lancement;

import Combat.Combat;
import Effets.BuffTitre;
import Joueur.Personnage_principale;
import Personnage.PersonnageBase;
import Equipement.CarteOr;
import Equipement.Equipement;
import java.util.ArrayList;
import java.util.List;

public class Stage {

    /** Résultat retourné par Stage.lancer() pour le calcul des étoiles. */
    public static class ResultatStage {
        public final boolean victoire;
        public final boolean sansAllieMort;
        public final boolean enMoinsDe10Tours;
        public final int     toursUtilises;

        public ResultatStage(boolean victoire, boolean sansAllieMort,
                             boolean enMoinsDe10Tours, int toursUtilises) {
            this.victoire         = victoire;
            this.sansAllieMort    = sansAllieMort;
            this.enMoinsDe10Tours = enMoinsDe10Tours;
            this.toursUtilises    = toursUtilises;
        }
    }


    private final int    numero;
    private final String titre;
    private final int    recompenseOr;
    private final int    recompenseXP;
    private final ArrayList<PersonnageBase> ennemis;
    private final Equipement recompenseEquipement;

    public Stage(int numero, String titre, int recompenseOr, int recompenseXP,
                 ArrayList<PersonnageBase> ennemis) {
        this(numero, titre, recompenseOr, recompenseXP, ennemis, null);
    }

    public Stage(int numero, String titre, int recompenseOr, int recompenseXP,
                 ArrayList<PersonnageBase> ennemis, Equipement recompenseEquipement) {
        this.numero               = numero;
        this.titre                = titre;
        this.recompenseOr         = recompenseOr;
        this.recompenseXP         = recompenseXP;
        this.ennemis              = ennemis;
        this.recompenseEquipement = recompenseEquipement;
    }

    /**
     * Lance le combat. Reçoit GameContext + les deux booléens spécifiques au stage.
     * Plus aucun chapitre ou manager en paramètre direct.
     */
    public ResultatStage lancer(GameContext ctx,
                               ArrayList<PersonnageBase> equipeAlliee,
                               boolean estNouveauStage) {

        // Reset avant combat + snapshot des alliés vivants
        for (PersonnageBase perso : equipeAlliee) {
            perso.reinitialiserPourCombat();
        }

        System.out.println("\n========================================");
        System.out.println("  STAGE " + numero + " : " + titre);
        System.out.println("========================================\n");
        System.out.println("Ennemis :");
        for (PersonnageBase e : ennemis)
            System.out.println("  - " + e.getNom() + " (Niv." + e.getNiveau() + ")");
        System.out.println();

        // Combat
        double bonusTitre = ctx.gestionnaireTitres.getBonusActif();
        Combat combat = new Combat(equipeAlliee, ennemis, bonusTitre);

        // Clef Céleste active du joueur
        if (ctx.gestionnaireClefsCelestes != null) {
            lancement.Gestionnaires.ClefCeleste clefActive = ctx.gestionnaireClefsCelestes.getClefActive();
            if (clefActive != null)
                combat.setClefJoueur(clefActive, ctx.gestionnaireClefsCelestes.getNiveauClefActive());
        }

        combat.lancerCombat();

        // Retirer BuffTitre après le combat
        for (PersonnageBase perso : equipeAlliee)
            perso.getEffetsActifs().removeIf(e -> e instanceof BuffTitre);

        boolean victoire       = tousKO(ennemis);
        boolean sansAllieMort  = tousVivants(equipeAlliee);
        int     toursUtilises  = combat.getToursUtilises();
        boolean enMoinsDe10    = toursUtilises <= 10;

        if (victoire) {
            for (PersonnageBase perso : equipeAlliee) {
                perso.reinitialiserPourCombat();
            }

            System.out.println("\n>> Recompenses :");
            System.out.println("   + " + recompenseXP + " XP");
            System.out.println("   + " + recompenseOr + " or");
            ctx.joueur.ajouterOr(recompenseOr);

            if (recompenseEquipement != null) {
                ctx.inventaire.ajouterEquipement(recompenseEquipement);
                System.out.println("   + Equipement obtenu : " + recompenseEquipement);
            }

            // ── Cartes d'or ──────────────────────────────────────────────
            if (estNouveauStage) {
                // Première réussite : 10 Cartes d'Or Lv.1 garanties
                int ajoute = ctx.inventaire.ajouterCartesOr(CarteOr.NIVEAU_1, 10);
                System.out.println("   + " + ajoute + "x " + CarteOr.NIVEAU_1.nom
                        + " (premiere victoire) !");
            } else {
                // Replay : 50% de chance d'obtenir 1 Carte d'Or Lv.1
                if (Math.random() < 0.50) {
                    int ajoute = ctx.inventaire.ajouterCartesOr(CarteOr.NIVEAU_1, 1);
                    if (ajoute > 0)
                        System.out.println("   + 1x " + CarteOr.NIVEAU_1.nom + " !");
                }
            }

            if (estNouveauStage) {
                int pts = titre.contains("[ELITE]") ? 4 : 5;
                ctx.joueur.getArbreCompetences().ajouterPoints(pts);
                System.out.println("   + " + pts + " point(s) d'abilite !"
                        + " (Total : " + ctx.joueur.getArbreCompetences().getPointsDisponibles() + ")");
            }

            // Affichage étoiles
            System.out.println();
            System.out.println(">> Etoiles obtenues :");
            System.out.println("   ★ Victoire");
            System.out.println("   " + (sansAllieMort ? "★" : "☆") + " Aucun allie mort");
            System.out.println("   " + (enMoinsDe10   ? "★" : "☆") + " Termine en " + toursUtilises + " tour(s) (<= 10)");

            for (PersonnageBase p : equipeAlliee)
                p.gagnerExperience(recompenseXP);

            ctx.sauvegarde.sauvegarder(ctx);
            System.out.println(">> Partie sauvegardee automatiquement.");
        }

        return new ResultatStage(victoire, sansAllieMort, enMoinsDe10, toursUtilises);
    }

    private boolean tousKO(ArrayList<PersonnageBase> equipe) {
        for (PersonnageBase p : equipe)
            if (p.estVivant()) return false;
        return true;
    }

    private boolean tousVivants(ArrayList<PersonnageBase> equipe) {
        for (PersonnageBase p : equipe)
            if (!p.estVivant()) return false;
        return true;
    }

    public int    getNumero()       { return numero; }
    public String getTitre()        { return titre; }
    public int    getRecompenseOr() { return recompenseOr; }
}