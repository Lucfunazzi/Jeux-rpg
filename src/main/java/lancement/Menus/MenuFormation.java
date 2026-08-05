package lancement.Menus;

import Joueur.Personnage_principale;
import Joueur.ArbreCompetences;
import Personnage.PersonnageBase;
import java.util.ArrayList;
import java.util.Scanner;
import lancement.Formation;
import lancement.GameContext;
import lancement.Menus.MenuAbilite;

public class MenuFormation {

    public void afficher(GameContext ctx, Scanner scanner) {
        Personnage_principale     joueur                = ctx.joueur;
        Formation                 formation             = ctx.formation;
        ArrayList<PersonnageBase> personnagesDisponibles = new ArrayList<>();
        personnagesDisponibles.add(ctx.joueur);
        personnagesDisponibles.addAll(ctx.personnagesRecruites);
        boolean retour = false;
        while (!retour) {
            formation.afficherFormation();

            // Résumé de la compétence spéciale/ultime active
            String nomCompActive   = nomCompetenceActive(joueur);
            String nomUltimeActive = nomUltimeActive(joueur);

            System.out.println("\n========================================");
            System.out.println("           MENU FORMATION");
            System.out.println("========================================");
            System.out.println("Competence speciale active : " + nomCompActive);
            System.out.println("Attaque ultime active       : " + nomUltimeActive);
            System.out.println();
            System.out.println("1. Ajouter un personnage");
            System.out.println("2. Retirer un personnage");
            System.out.println("3. Changer la competence speciale");
            System.out.println("4. Changer l'attaque ultime");
            System.out.println("0. Retour");
            System.out.print("Votre choix : ");

            switch (scanner.nextLine().trim()) {
                case "1" -> ajouterMenu(formation, personnagesDisponibles, scanner);
                case "2" -> retirerMenu(formation, scanner);
                case "3" -> changerCompetenceSpeciale(joueur, scanner);
                case "4" -> changerCompetenceUltime(joueur, scanner);
                case "0" -> retour = true;
                default  -> System.out.println("Choix invalide.");
            }
        }
    }

    private void ajouterMenu(Formation formation,
                              ArrayList<PersonnageBase> disponibles,
                              Scanner scanner) {
        if (formation.estPleine()) {
            System.out.println("La formation est pleine (5/5) !");
            return;
        }

        // Filtrer ceux deja dans l'equipe
        ArrayList<PersonnageBase> equipe = formation.getEquipe();
        ArrayList<PersonnageBase> ajoutables = new ArrayList<>();
        for (PersonnageBase p : disponibles) {
            if (!equipe.contains(p)) ajoutables.add(p);
        }

        if (ajoutables.isEmpty()) {
            System.out.println("Aucun personnage disponible a ajouter.");
            return;
        }

        System.out.println("\nPersonnages disponibles :");
        for (int i = 0; i < ajoutables.size(); i++) {
            PersonnageBase p = ajoutables.get(i);
            System.out.println((i + 1) + ". " + p.getNom()
                    + " [" + p.getRole() + "] Niv." + p.getNiveau());
        }
        System.out.println("0. Annuler");
        System.out.print("Votre choix : ");

        try {
            int choix = Integer.parseInt(scanner.nextLine().trim());
            if (choix == 0) return;
            if (choix < 1 || choix > ajoutables.size()) {
                System.out.println("Choix invalide.");
                return;
            }
            System.out.println(formation.ajouterPersonnage(ajoutables.get(choix - 1)));
        } catch (NumberFormatException e) {
            System.out.println("Entree invalide.");
        }
    }

    private void retirerMenu(Formation formation, Scanner scanner) {
        // Equipe sans le joueur (non retirable)
        ArrayList<PersonnageBase> equipe = formation.getEquipe();
        ArrayList<PersonnageBase> retirables = new ArrayList<>(equipe);
        retirables.remove(0); // index 0 = joueur, toujours en premier

        if (retirables.isEmpty()) {
            System.out.println("Aucun personnage a retirer (vous etes seul dans l'equipe).");
            return;
        }

        System.out.println("\nPersonnages dans la formation :");
        for (int i = 0; i < retirables.size(); i++) {
            PersonnageBase p = retirables.get(i);
            System.out.println((i + 1) + ". " + p.getNom() + " [" + p.getRole() + "]");
        }
        System.out.println("0. Annuler");
        System.out.print("Votre choix : ");

        try {
            int choix = Integer.parseInt(scanner.nextLine().trim());
            if (choix == 0) return;
            if (choix < 1 || choix > retirables.size()) {
                System.out.println("Choix invalide.");
                return;
            }
            System.out.println(formation.retirerPersonnage(retirables.get(choix - 1)));
        } catch (NumberFormatException e) {
            System.out.println("Entree invalide.");
        }
    }
    
    /** Numeros des arbres qui debloquent une speciale alternative (4 et 8 sont des arbres d'ultime). */
    private static final int[] ARBRES_SPECIALE = {1, 2, 3, 5, 6, 7};
    /** Numeros des arbres qui debloquent un ultime alternatif. */
    private static final int[] ARBRES_ULTIME = {4, 8};

    // ── Changer la compétence spéciale active ─────────────────────────────
    private void changerCompetenceSpeciale(Personnage_principale joueur, Scanner scanner) {
        ArbreCompetences arbre  = joueur.getArbreCompetences();
        String classe           = joueur.getChoixClasses();
        int actuelle            = joueur.getCompetenceSpecialeActive();

        System.out.println("\n========================================");
        System.out.println("     CHOISIR LA COMPETENCE SPECIALE");
        System.out.println("========================================");
        System.out.println("Competence active : " + nomCompetenceActive(joueur));
        System.out.println();

        String nomOriginal = joueur.getCompetencesChoisie() != null
                ? joueur.getCompetencesChoisie()
                         .getNomsCompetences()[joueur.getChoixComp() - 1]
                : "Competence originale";
        System.out.println("1. " + nomOriginal + " (competence originale)"
                + (actuelle == 0 ? "  [ACTIVE]" : ""));

        for (int i = 0; i < ARBRES_SPECIALE.length; i++) {
            int numArbre = ARBRES_SPECIALE[i];
            boolean dispo = arbre.getNoeud(numArbre, 10).isDebloque();
            String nom = MenuAbilite.getNomCompetence(classe, numArbre);
            System.out.println((i + 2) + ". " + nom + " (Arbre " + numArbre + ")"
                    + (!dispo ? "  [VERROUILLE]" : actuelle == numArbre ? "  [ACTIVE]" : ""));
        }

        System.out.println("0. Annuler");
        System.out.print("Votre choix : ");

        try {
            int choix = Integer.parseInt(scanner.nextLine().trim());
            if (choix == 0) return;
            if (choix == 1) {
                joueur.setCompetenceSpecialeActive(0);
                System.out.println(">> Competence active : " + nomOriginal);
                return;
            }
            if (choix < 2 || choix > ARBRES_SPECIALE.length + 1) {
                System.out.println("Choix invalide.");
                return;
            }
            int numArbre = ARBRES_SPECIALE[choix - 2];
            if (!arbre.getNoeud(numArbre, 10).isDebloque()) {
                System.out.println("Competence verrouillee — completez l'arbre " + numArbre + " d'abord.");
            } else {
                joueur.setCompetenceSpecialeActive(numArbre);
                System.out.println(">> Competence active : " + MenuAbilite.getNomCompetence(classe, numArbre));
            }
        } catch (NumberFormatException e) {
            System.out.println("Entree invalide.");
        }
    }

    // ── Changer l'attaque ultime active ────────────────────────────────────
    private void changerCompetenceUltime(Personnage_principale joueur, Scanner scanner) {
        ArbreCompetences arbre  = joueur.getArbreCompetences();
        String classe           = joueur.getChoixClasses();
        int actuelle            = joueur.getCompetenceUltimeActive();

        System.out.println("\n========================================");
        System.out.println("       CHOISIR L'ATTAQUE ULTIME");
        System.out.println("========================================");
        System.out.println("Ultime active : " + nomUltimeActive(joueur));
        System.out.println();

        String nomOriginal = joueur.getCompetencesChoisie() != null
                ? joueur.getCompetencesChoisie().getNomsCompetences()[1]
                : "Ultime originale";
        System.out.println("1. " + nomOriginal + " (ultime originale)"
                + (actuelle == 0 ? "  [ACTIVE]" : ""));

        for (int i = 0; i < ARBRES_ULTIME.length; i++) {
            int numArbre = ARBRES_ULTIME[i];
            boolean dispo = arbre.getNoeud(numArbre, 10).isDebloque();
            String nom = Personnage_principale.getNomUltimeArbre(classe, numArbre);
            System.out.println((i + 2) + ". " + nom + " (Arbre " + numArbre + ")"
                    + (!dispo ? "  [VERROUILLE]" : actuelle == numArbre ? "  [ACTIVE]" : ""));
        }

        System.out.println("0. Annuler");
        System.out.print("Votre choix : ");

        try {
            int choix = Integer.parseInt(scanner.nextLine().trim());
            if (choix == 0) return;
            if (choix == 1) {
                joueur.setCompetenceUltimeActive(0);
                System.out.println(">> Ultime active : " + nomOriginal);
                return;
            }
            if (choix < 2 || choix > ARBRES_ULTIME.length + 1) {
                System.out.println("Choix invalide.");
                return;
            }
            int numArbre = ARBRES_ULTIME[choix - 2];
            if (!arbre.getNoeud(numArbre, 10).isDebloque()) {
                System.out.println("Ultime verrouillee — completez l'arbre " + numArbre + " d'abord.");
            } else {
                joueur.setCompetenceUltimeActive(numArbre);
                System.out.println(">> Ultime active : " + Personnage_principale.getNomUltimeArbre(classe, numArbre));
            }
        } catch (NumberFormatException e) {
            System.out.println("Entree invalide.");
        }
    }

    private String nomCompetenceActive(Personnage_principale joueur) {
        int actuelle = joueur.getCompetenceSpecialeActive();
        if (actuelle == 0) {
            return joueur.getCompetencesChoisie() != null
                    ? joueur.getCompetencesChoisie()
                             .getNomsCompetences()[joueur.getChoixComp() - 1]
                    : "Aucune";
        }
        return MenuAbilite.getNomCompetence(joueur.getChoixClasses(), actuelle);
    }

    private String nomUltimeActive(Personnage_principale joueur) {
        int actuelle = joueur.getCompetenceUltimeActive();
        if (actuelle == 0) {
            return joueur.getCompetencesChoisie() != null
                    ? joueur.getCompetencesChoisie().getNomsCompetences()[1]
                    : "Aucune";
        }
        return Personnage_principale.getNomUltimeArbre(joueur.getChoixClasses(), actuelle);
    }

}