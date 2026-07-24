package lancement.ChapitreElite;

import Personnage.PersonnageBase;
import Personnage.pnj.Chapitre3.EnnemiJubia_4elements;
import Personnage.pnj.Chapitre3.EnnemiTotomaru;
import Personnage.pnj.Chapitre3.EnnemiSol;
import Personnage.pnj.Chapitre3.EnnemiAria;
import Personnage.pnj.Chapitre3.EnnemiJose;
import Personnage.pnj.Chapitre3.EnnemiGadjeel;
import Personnage.pnj.Chapitre3.EnnemiMage1DPS;
import Personnage.pnj.Chapitre3.EnnemiMage2DPS;
import Personnage.pnj.Chapitre3.EnnemiMage3Soigneur;
import Personnage.pnj.Chapitre3.EnnemiMage5Tank;
import Personnage.pnj.Chapitre3.EnnemiMage8DPS;
import Personnage.pnj.Chapitre3.EnnemiMage9Tank;
import Equipement.CarteOr;
import Equipement.Equipement;
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
                String etoiles  = ctx.gestionnaireEtoiles.getEtoiles(3, i, true).afficher();
                System.out.println(etat + "Stage " + i + " — " + getTitreStage(i)
                        + "  " + etoiles + "  (" + restants + "/10 runs restants)");
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
                // Uniquement des fragments rang A ici : les rangs S/SS/SSS/UR s'obtiennent
                // via la Boutique d'equipement (recyclage), pas en Chapitre 3 Elite.
                List<FragmentEquipement> catalogue = gestionnaireFragments.getCatalogue().stream()
                        .filter(f -> f.getRarete() == Equipement.Rarete.A)
                        .toList();
                FragmentEquipement fragment = catalogue.get((int) (Math.random() * catalogue.size()));
                ctx.inventaire.ajouterMateriau(fragment.getNomFragment(), 1);
                int total = ctx.inventaire.getQuantiteMateriau(fragment.getNomFragment());
                System.out.printf("   ✦ Fragment obtenu : %s (%d/%d)%n",
                        fragment.getNomFragment(), total, fragment.getQuantiteRequise());
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

    /**
     * Reprend exactement les combats du Chapitre 3 normal (Totomaru, Sol, Jubia, Gadjeel,
     * Aria, José), mais sans aucun invité temporaire (Natsu/Elfman/Gray/Erza/Makarov) ni
     * combat scripté : c'est toujours notre propre formation qui affronte les boss, avec
     * des niveaux plus eleves. L'equipement fantome (auto-equipe par Stage a partir du
     * niveau) est deja au rang A pour tous ces niveaux (>= 35), au-dessus du rang B —
     * voir EquipementFactory.rareteEnnemiPourNiveau.
     */
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
                return new Stage(1, "[ELITE] L'assaut de Phantom Lord Renforcé", 4500, 0, e);
            }

            // Stage 2 — Totomaru élite + escorte
            case 2 -> {
                e.add(new EnnemiTotomaru(38));
                e.add(new EnnemiMage2DPS(36));
                e.add(new EnnemiMage9Tank(35));
                e.add(new EnnemiMage3Soigneur(34));
                e.add(new EnnemiMage3Soigneur(34));
                return new Stage(2, "[ELITE] Totomaru — Sept Flammes d'Élite", 5500, 0, e);
            }

            // Stage 3 — Sol élite + troupe lourde
            case 3 -> {
                e.add(new EnnemiSol(39));
                e.add(new EnnemiMage8DPS(36));
                e.add(new EnnemiMage5Tank(36));
                e.add(new EnnemiMage2DPS(35));
                e.add(new EnnemiMage3Soigneur(35));
                return new Stage(3, "[ELITE] Sol — L'Impénétrable d'Élite", 6750, 0, e);
            }

            // Stage 4 — Jubia élite + garde rapprochée
            case 4 -> {
                e.add(new EnnemiJubia_4elements(41));
                e.add(new EnnemiMage3Soigneur(38));
                e.add(new EnnemiMage9Tank(37));
                e.add(new EnnemiMage5Tank(37));
                e.add(new EnnemiMage2DPS(36));
                return new Stage(4, "[ELITE] Jubia — L'Eau qui Brise d'Élite", 8000, 0, e);
            }

            // Stage 5 — Gadjeel élite + escorte
            case 5 -> {
                e.add(new EnnemiGadjeel(43));
                e.add(new EnnemiMage3Soigneur(40));
                e.add(new EnnemiMage9Tank(39));
                e.add(new EnnemiMage5Tank(39));
                e.add(new EnnemiMage2DPS(38));
                return new Stage(5, "[ELITE] Gadjeel — Le Dragon d'Acier d'Élite", 9500, 0, e);
            }

            // Stage 6 — Aria élite + escorte d'élite
            case 6 -> {
                e.add(new EnnemiAria(45));
                e.add(new EnnemiMage3Soigneur(41));
                e.add(new EnnemiMage9Tank(41));
                e.add(new EnnemiMage5Tank(40));
                e.add(new EnnemiMage2DPS(40));
                return new Stage(6, "[ELITE] Aria — Magie du Ciel Vide Transcendée", 11000, 0, e);
            }

            // Stage 7 — José élite + escorte d'élite
            case 7 -> {
                e.add(new EnnemiJose(47));
                e.add(new EnnemiMage3Soigneur(43));
                e.add(new EnnemiMage9Tank(42));
                e.add(new EnnemiMage5Tank(42));
                e.add(new EnnemiMage2DPS(41));
                return new Stage(7, "[ELITE] José — L'Ombre Transcendée", 13000, 0, e);
            }

            // Stage 8 — José seul, sans invite, plus fort
            case 8 -> {
                e.add(new EnnemiJose(50));
                return new Stage(8, "[ELITE] José Pora — Seul face à Phantom Lord", 15000, 0, e);
            }

            // Stage 9 — José seul, encore plus fort (pas de combat scripte en Elite)
            case 9 -> {
                e.add(new EnnemiJose(54));
                return new Stage(9, "[ELITE] José Pora — Puissance Maximale", 17500, 0, e);
            }

            // Stage 10 — Aria seule, dernier rempart d'élite
            case 10 -> {
                e.add(new EnnemiAria(58));
                return new Stage(10, "[ELITE] Aria — Le Dernier Rempart d'Élite", 21000, 0, e);
            }

            default -> { return new Stage(numero, "???", 0, 0, e); }
        }
    }

    public String getTitreStage(int numero) {
        return switch (numero) {
            case 1  -> "[ELITE] L'assaut de Phantom Lord Renforcé";
            case 2  -> "[ELITE] Totomaru — Sept Flammes d'Élite";
            case 3  -> "[ELITE] Sol — L'Impénétrable d'Élite";
            case 4  -> "[ELITE] Jubia — L'Eau qui Brise d'Élite";
            case 5  -> "[ELITE] Gadjeel — Le Dragon d'Acier d'Élite";
            case 6  -> "[ELITE] Aria — Magie du Ciel Vide Transcendée";
            case 7  -> "[ELITE] José — L'Ombre Transcendée";
            case 8  -> "[ELITE] José Pora — Seul face à Phantom Lord";
            case 9  -> "[ELITE] José Pora — Puissance Maximale";
            case 10 -> "[ELITE] Aria — Le Dernier Rempart d'Élite";
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