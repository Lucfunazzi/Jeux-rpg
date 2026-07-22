package lancement.ChapitreElite;

import Personnage.PersonnageBase;
import Personnage.pnj.Chapitre3.EnnemiJubia_4elements;
import Personnage.pnj.Chapitre3.EnnemiTotomaru;
import Personnage.pnj.Chapitre3.EnnemiSol;
import Personnage.pnj.Chapitre3.EnnemiAria;
import Personnage.pnj.Chapitre3.EnnemiJose;
import Personnage.pnj.Chapitre3.EnnemiMage1DPS;
import Personnage.pnj.Chapitre3.EnnemiMage2DPS;
import Personnage.pnj.Chapitre3.EnnemiMage3Soigneur;
import Personnage.pnj.Chapitre3.EnnemiMage5Tank;
import Personnage.pnj.Chapitre3.EnnemiMage8DPS;
import Personnage.pnj.Chapitre3.EnnemiMage9Tank;
import Equipement.CarteOr;
import Equipement.FragmentEquipement;
import Equipement.GestionnaireFragments;
import lancement.GameContext;
import lancement.Stage;
import lancement.Chapitres.Chapitre3;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Chapitre3Elite {

    private static final int    NB_STAGES              = 10;
    private static final double CHANCE_CARTE_OR_REPLAY = 0.30;
    private static final double CHANCE_FRAGMENT        = 0.40;
    private static final double CHANCE_FRAGMENT_BOSS   = 0.70;

    private static final GestionnaireFragments gestionnaireFragments = new GestionnaireFragments();

    private final boolean[] stagesDebloques  = new boolean[NB_STAGES + 1];
    private final boolean[] stagesReussis    = new boolean[NB_STAGES + 1];
    private final boolean[] premiereVictoire = new boolean[NB_STAGES + 1];

    private final Chapitre3      chapitre3;
    private final Chapitre2Elite chapitre2Elite;

    public Chapitre3Elite(Chapitre3 chapitre3, Chapitre2Elite chapitre2Elite) {
        this.chapitre3      = chapitre3;
        this.chapitre2Elite = chapitre2Elite;
        stagesDebloques[1]  = true;
    }

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
            System.out.println("   CHAPITRE 3 ELITE — Phantom Lord Transcendé");
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
            } else {
                lancerStage(ctx, choix);
            }
        }
    }

    /**
     * Verifie les runs/energie, lance le stage donne et applique les recompenses en cas de
     * victoire. Suppose que le stage est deja debloque. Retourne null si le stage n'a pas pu
     * etre lance (runs epuises ou energie insuffisante — message imprime dans ce cas).
     * Reutilisable par la console et l'interface graphique.
     */
    public Stage.ResultatStage lancerStage(GameContext ctx, int numero) {
        if (!ctx.gestionnaireEnergie.peutFaireRunElite(numero)) {
            System.out.println("Limite de runs atteinte pour ce stage aujourd'hui (10/10).");
            return null;
        }
        if (!ctx.gestionnaireEnergie.consommerEnergie(5)) {
            System.out.println("Pas assez d'energie ! (il faut 5, vous avez "
                    + ctx.gestionnaireEnergie.getEnergie() + ")");
            return null;
        }

        ctx.gestionnaireEnergie.enregistrerRunElite(numero);
        Stage stage        = construireStage(numero, ctx);
        boolean estNouveau = !stagesReussis[numero];
        Stage.ResultatStage resultatStage = stage.lancer(ctx, ctx.formation.getEquipe(), estNouveau);

        if (resultatStage.victoire) {
            stagesReussis[numero] = true;

            if (numero < NB_STAGES) {
                stagesDebloques[numero + 1] = true;
                System.out.println(">> Stage " + (numero + 1) + " debloque !");
            } else {
                System.out.println(">> Felicitations ! Vous avez termine le Chapitre 3 Elite !");
            }

            if (estNouveau && !premiereVictoire[numero]) {
                premiereVictoire[numero] = true;
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

            double chanceFragment = (numero == NB_STAGES) ? CHANCE_FRAGMENT_BOSS : CHANCE_FRAGMENT;
            if (Math.random() < chanceFragment) {
                List<FragmentEquipement> catalogue = gestionnaireFragments.getCatalogue();
                FragmentEquipement fragment = catalogue.get((int) (Math.random() * catalogue.size()));
                ctx.inventaire.ajouterMateriau(fragment.getNomFragment(), 1);
                int total = ctx.inventaire.getQuantiteMateriau(fragment.getNomFragment());
                System.out.printf("   ✦ Fragment obtenu : %s (%d/%d)%n",
                        fragment.getNomFragment(), total, FragmentEquipement.QUANTITE_REQUISE);
            }

            ctx.gestionnaireQuetes.notifierOrGagne(stage.getRecompenseOr());
            ctx.gestionnaireQuetes.notifierStageFini(3, numero, true,
                    ctx.joueur, ctx.menuRecrutement, ctx.personnagesRecruites);
            ctx.gestionnaireEtoiles.mettreAJour(3, numero, true,
                    resultatStage.victoire, resultatStage.sansAllieMort, resultatStage.enMoinsDe10Tours);

            ctx.sauvegarde.sauvegarder(ctx);
        }
        return resultatStage;
    }

    private Stage construireStage(int numero, GameContext ctx) {
        ArrayList<PersonnageBase> e = new ArrayList<>();
        switch (numero) {

            // Stage 1 — Avant-garde renforcée (+12 niveaux vs normal)
            case 1 -> {
                e.add(new EnnemiMage1DPS(34));
                e.add(new EnnemiMage8DPS(34));
                e.add(new EnnemiMage2DPS(33));
                e.add(new EnnemiMage9Tank(33));
                e.add(new EnnemiMage3Soigneur(32));
                return new Stage(1, "[ELITE] L'assaut de Phantom Lord Renforcé", 9000, 0, e);
            }

            // Stage 2 — Totomaru élite + escorte
            case 2 -> {
                e.add(new EnnemiTotomaru(38));
                e.add(new EnnemiMage2DPS(36));
                e.add(new EnnemiMage9Tank(35));
                e.add(new EnnemiMage3Soigneur(34));
                e.add(new EnnemiMage3Soigneur(34));
                return new Stage(2, "[ELITE] Totomaru — Sept Flammes d'Élite", 11000, 0, e);
            }

            // Stage 3 — Sol élite + troupe lourde
            case 3 -> {
                e.add(new EnnemiSol(38));
                e.add(new EnnemiMage8DPS(36));
                e.add(new EnnemiMage5Tank(36));
                e.add(new EnnemiMage2DPS(35));
                e.add(new EnnemiMage3Soigneur(35));
                return new Stage(3, "[ELITE] Sol — L'Impénétrable d'Élite", 13500, 0, e);
            }

            // Stage 4 — Totomaru + Sol élite ensemble
            case 4 -> {
                e.add(new EnnemiTotomaru(40));
                e.add(new EnnemiSol(40));
                e.add(new EnnemiMage3Soigneur(37));
                e.add(new EnnemiMage9Tank(36));
                e.add(new EnnemiMage3Soigneur(36));
                return new Stage(4, "[ELITE] L'Élément 4 Renforcé", 16000, 0, e);
            }

            // Stage 5 — Jubia élite + garde rapprochée
            case 5 -> {
                e.add(new EnnemiJubia_4elements(41));
                e.add(new EnnemiMage3Soigneur(39));
                e.add(new EnnemiMage9Tank(38));
                e.add(new EnnemiMage5Tank(38));
                e.add(new EnnemiMage3Soigneur(37));
                return new Stage(5, "[ELITE] Jubia — L'Eau qui Brise d'Élite", 19000, 0, e);
            }

            // Stage 6 — Élément 4 complet élite
            case 6 -> {
                e.add(new EnnemiJubia_4elements(42));
                e.add(new EnnemiTotomaru(41));
                e.add(new EnnemiSol(41));
                e.add(new EnnemiMage3Soigneur(39));
                e.add(new EnnemiMage9Tank(38));
                return new Stage(6, "[ELITE] L'Élément 4 Complet d'Élite", 22000, 0, e);
            }

            // Stage 7 — Aria élite + escorte d'élite
            case 7 -> {
                e.add(new EnnemiAria(43));
                e.add(new EnnemiMage3Soigneur(41));
                e.add(new EnnemiMage9Tank(40));
                e.add(new EnnemiMage5Tank(40));
                e.add(new EnnemiMage3Soigneur(39));
                return new Stage(7, "[ELITE] Aria — Magie du Ciel Vide Transcendée", 26000, 0, e);
            }

            // Stage 8 — Aria + Élément 4 au complet élite
            case 8 -> {
                e.add(new EnnemiAria(44));
                e.add(new EnnemiJubia_4elements(43));
                e.add(new EnnemiTotomaru(42));
                e.add(new EnnemiSol(42));
                e.add(new EnnemiMage3Soigneur(40));
                return new Stage(8, "[ELITE] L'Élément 4 — Ultime Résistance", 30000, 0, e);
            }

            // Stage 9 — José élite + Aria + renforts maximum
            case 9 -> {
                e.add(new EnnemiJose(46));
                e.add(new EnnemiAria(44));
                e.add(new EnnemiMage3Soigneur(41));
                e.add(new EnnemiMage9Tank(40));
                e.add(new EnnemiMage3Soigneur(40));
                return new Stage(9, "[ELITE] José — L'Ombre Transcendée", 35000, 0, e);
            }

            // Stage 10 — José boss + tout l'Élément 4 élite
            case 10 -> {
                e.add(new EnnemiJose(48));
                e.add(new EnnemiAria(45));
                e.add(new EnnemiJubia_4elements(44));
                e.add(new EnnemiTotomaru(43));
                e.add(new EnnemiSol(43));
                return new Stage(10, "[ELITE] José Porla — Forme Spectrali Suprême", 42000, 0, e);
            }

            default -> { return new Stage(numero, "???", 0, 0, e); }
        }
    }

    public String getTitreStage(int numero) {
        return switch (numero) {
            case 1  -> "[ELITE] L'assaut de Phantom Lord Renforcé";
            case 2  -> "[ELITE] Totomaru — Sept Flammes d'Élite";
            case 3  -> "[ELITE] Sol — L'Impénétrable d'Élite";
            case 4  -> "[ELITE] L'Élément 4 Renforcé";
            case 5  -> "[ELITE] Jubia — L'Eau qui Brise d'Élite";
            case 6  -> "[ELITE] L'Élément 4 Complet d'Élite";
            case 7  -> "[ELITE] Aria — Magie du Ciel Vide Transcendée";
            case 8  -> "[ELITE] L'Élément 4 — Ultime Résistance";
            case 9  -> "[ELITE] José — L'Ombre Transcendée";
            case 10 -> "[ELITE] José Porla — Forme Spectrali Suprême";
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