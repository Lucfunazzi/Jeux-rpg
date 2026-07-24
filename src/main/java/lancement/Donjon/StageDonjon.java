package lancement.Donjon;

import Combat.Combat;
import Effets.BuffTitre;
import Equipement.Equipement;
import Equipement.EquipementFactory;
import Equipement.Inventaire;
import Equipement.ParcheminXP;
import Joueur.Personnage_principale;
import Personnage.PersonnageBase;
import lancement.Gestionnaires.GestionnaireDonjon;
import lancement.Gestionnaires.GestionnaireDonjon.TypeDonjon;
import lancement.Gestionnaires.GestionnaireDonjon.Difficulte;
import lancement.Gestionnaires.GestionnaireTitres;
import java.util.ArrayList;
import java.util.List;

/**
 * Un combat de donjon.
 * Recompenses fixes selon type + difficulte, pas de sauvegarde automatique
 * (la sauvegarde est faite par MenuDonjon apres le run).
 */
public class StageDonjon {

    private final TypeDonjon  type;
    private final Difficulte  difficulte;
    private final ArrayList<PersonnageBase> ennemis;

    // Recompenses
    private final int    recompenseOr;
    private final int    recompensePierres;    // pierres d'affinage (0 si donjon or/xp)
    private final ParcheminXP recompenseParchemin; // null si donjon or/affinage

    // Instantane et evenements du dernier combat lance (pour la relecture visuelle GUI)
    private List<Combat.PersonnageSnapshot> etatInitial;
    private List<Combat.CombatEvent> evenements;

    public StageDonjon(TypeDonjon type, Difficulte difficulte,
                       ArrayList<PersonnageBase> ennemis,
                       int recompenseOr, int recompensePierres,
                       ParcheminXP recompenseParchemin) {
        this.type               = type;
        this.difficulte         = difficulte;
        this.ennemis            = ennemis;
        this.recompenseOr       = recompenseOr;
        this.recompensePierres  = recompensePierres;
        this.recompenseParchemin = recompenseParchemin;

        // Equipement fantome selon la difficulte : Normal = C, Difficile = B, Extreme = A.
        Equipement.Rarete rarete = switch (difficulte) {
            case NORMAL    -> Equipement.Rarete.C;
            case DIFFICILE -> Equipement.Rarete.B;
            case EXTREME   -> Equipement.Rarete.A;
        };
        for (PersonnageBase e : ennemis) {
            EquipementFactory.equiperSetStandard(e, rarete);
        }
    }

    /**
     * Lance le combat et distribue les recompenses si victoire.
     * @return true si victoire
     */
    public boolean lancer(Personnage_principale joueur,
                          ArrayList<PersonnageBase> equipeAlliee,
                          Inventaire inventaire,
                          ArrayList<PersonnageBase> personnagesRecruites,
                          GestionnaireDonjon gestionnaireDonjon,
                          GestionnaireTitres gestionnaireTitres,
                          java.util.Scanner scanner) {

        // Reset equipe alliee avant combat
        for (PersonnageBase perso : equipeAlliee) {
            perso.reinitialiserPourCombat();
            perso.getEffetsActifs().clear();
        }

        System.out.println("\n========================================");
        System.out.println("  DONJON " + getNomType() + " — " + getNomDifficulte());
        System.out.println("========================================");
        System.out.println("Ennemis :");
        for (PersonnageBase e : ennemis)
            System.out.println("  - " + e.getNom()
                    + " [" + e.getRarete() + "] Niv." + e.getNiveau()
                    + " (" + e.getRole() + ")");
        System.out.println();

        // Reset ennemis
        for (PersonnageBase e : ennemis) {
            e.reinitialiserPourCombat();
            e.getEffetsActifs().clear();
        }

        double bonusTitre = gestionnaireTitres.getBonusActif();
        etatInitial = Combat.snapshotEquipes(equipeAlliee, ennemis);
        Combat combat = new Combat(equipeAlliee, ennemis, bonusTitre);
        evenements = combat.lancerCombatEnregistre();

        // Retirer BuffTitre apres combat
        for (PersonnageBase perso : equipeAlliee) {
            perso.getEffetsActifs().removeIf(e -> e instanceof BuffTitre);
        }

        boolean victoire = tousKO(ennemis);

        if (victoire) {
            // Reset equipe apres victoire
            for (PersonnageBase perso : equipeAlliee) {
                perso.reinitialiserPourCombat();
                perso.getEffetsActifs().clear();
            }

            System.out.println("\n>> Victoire ! Recompenses :");

            if (recompenseOr > 0) {
                joueur.ajouterOr(recompenseOr);
                System.out.println("   + " + recompenseOr + " or");
            }
            if (recompensePierres > 0) {
                inventaire.ajouterMateriau("Pierre d'affinage", recompensePierres);
                System.out.println("   + " + recompensePierres + " pierre(s) d'affinage");
            }
            if (recompenseParchemin != null) {
                // Stocker le parchemin comme materiau avec son nom
                inventaire.ajouterMateriau(recompenseParchemin.getNom(), 1);
                System.out.println("   + 1x " + recompenseParchemin);
            }

            // Enregistrer le run
            gestionnaireDonjon.enregistrerRun(type, difficulte);
            System.out.println("\nRuns restants aujourd'hui : "
                    + gestionnaireDonjon.getRunsRestants(type, difficulte) + "/3");

            // Si donjon XP : proposer d'utiliser le parchemin immediatement
            if (recompenseParchemin != null && !personnagesRecruites.isEmpty()) {
                utiliserParchemin(joueur, personnagesRecruites, inventaire,
                        recompenseParchemin, scanner);
            }

        } else {
            System.out.println("\n>> Defaite... Aucune recompense.");
        }

        return victoire;
    }

    /**
     * Menu pour utiliser un parchemin XP sur un personnage recrute.
     * L'XP est plafonnee au niveau du joueur principal.
     */
    private void utiliserParchemin(Personnage_principale joueur,
                                    ArrayList<PersonnageBase> personnagesRecruites,
                                    Inventaire inventaire,
                                    ParcheminXP parchemin,
                                    java.util.Scanner scanner) {
        // Verifier si le joueur a des parchemins de ce type en inventaire
        int quantite = inventaire.getQuantiteMateriau(parchemin.getNom());
        if (quantite <= 0) return;

        System.out.println("\nVoulez-vous utiliser un " + parchemin.getNom()
                + " maintenant ? (1 : Oui / 0 : Non)");
        System.out.print("Votre choix : ");
        if (!scanner.nextLine().trim().equals("1")) return;

        // Choisir un personnage recrute
        System.out.println("\nChoisissez un personnage recrute pour lui donner "
                + parchemin.getXP() + " XP :");
        for (int i = 0; i < personnagesRecruites.size(); i++) {
            PersonnageBase p = personnagesRecruites.get(i);
            System.out.println("  " + (i + 1) + ". " + p.getNom()
                    + "  Niv." + p.getNiveau()
                    + "  XP : " + p.getExperience() + "/" + p.getExperienceMax());
        }
        System.out.println("  0. Annuler");
        System.out.print("Votre choix : ");

        try {
            int choix = Integer.parseInt(scanner.nextLine().trim());
            if (choix == 0) return;
            if (choix < 1 || choix > personnagesRecruites.size()) {
                System.out.println("Choix invalide.");
                return;
            }

            PersonnageBase cible = personnagesRecruites.get(choix - 1);

            // Plafond : le personnage ne peut pas depasser le niveau du joueur
            if (cible.getNiveau() >= joueur.getNiveau()) {
                System.out.println(cible.getNom() + " est deja au niveau maximum autorise ("
                        + joueur.getNiveau() + ").");
                return;
            }

            // Calculer XP plafonnee
            int xpADonner = parchemin.getXP();
            inventaire.retirerMateriau(parchemin.getNom(), 1);
            System.out.println(cible.getNom() + " recoit " + xpADonner + " XP !");
            // gagnerExperience gere les montees de niveau automatiquement
            // On surveille le niveau pour ne pas depasser celui du joueur
            int niveauAvant = cible.getNiveau();
            cible.gagnerExperience(xpADonner);
            // Si le perso a depasse le niveau du joueur apres montees, on le plafonne
            if (cible.getNiveau() > joueur.getNiveau()) {
                cible.setNiveau(joueur.getNiveau());
                System.out.println(cible.getNom() + " est plafonné au niveau du joueur ("
                        + joueur.getNiveau() + ").");
            }

        } catch (NumberFormatException e) {
            System.out.println("Entree invalide.");
        }
    }

    private boolean tousKO(ArrayList<PersonnageBase> equipe) {
        for (PersonnageBase p : equipe)
            if (p.estVivant()) return false;
        return true;
    }

    public String getNomType() {
        return switch (type) {
            case OR       -> "de l'Or";
            case AFFINAGE -> "de l'Affinage";
            case XP       -> "de l'Experience";
        };
    }

    public String getNomDifficulte() {
        return switch (difficulte) {
            case NORMAL   -> "Normal";
            case DIFFICILE -> "Difficile";
            case EXTREME  -> "Extreme";
        };
    }

    public List<Combat.PersonnageSnapshot> getEtatInitial() { return etatInitial; }
    public List<Combat.CombatEvent> getEvenements() { return evenements; }
}
