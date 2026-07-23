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

            // Résumé de la compétence spéciale active
            ArbreCompetences arbre = joueur.getArbreCompetences();
            String nomCompActive   = nomCompetenceActive(joueur);

            System.out.println("\n========================================");
            System.out.println("           MENU FORMATION");
            System.out.println("========================================");
            System.out.println("Competence speciale active : " + nomCompActive);
            System.out.println();
            System.out.println("1. Ajouter un personnage");
            System.out.println("2. Retirer un personnage");
            System.out.println("3. Changer la competence speciale");
            System.out.println("0. Retour");
            System.out.print("Votre choix : ");

            switch (scanner.nextLine().trim()) {
                case "1" -> ajouterMenu(formation, personnagesDisponibles, scanner);
                case "2" -> retirerMenu(formation, scanner);
                case "3" -> changerCompetenceSpeciale(joueur, scanner);
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

        // Construire la liste des options disponibles
        // Option 0 : compétence originale (toujours disponible)
        String nomOriginal = joueur.getCompetencesChoisie() != null
                ? joueur.getCompetencesChoisie()
                         .getNomsCompetences()[joueur.getChoixComp() - 1]
                : "Competence originale";

        System.out.println("1. " + nomOriginal + " (competence originale)"
                + (actuelle == 0 ? "  [ACTIVE]" : ""));

        // Option arbre 1
        boolean arbre1Dispo = arbre.isNoeud10Debloque();
        String nomArbre1    = MenuAbilite.getNomCompetence(classe, 1);
        System.out.println("2. " + nomArbre1 + " (Arbre 1)"
                + (!arbre1Dispo ? "  [VERROUILLE]" : actuelle == 1 ? "  [ACTIVE]" : ""));

        // Option arbre 2
        boolean arbre2Dispo = arbre.isNoeud10Arbre2Debloque();
        String nomArbre2    = MenuAbilite.getNomCompetence(classe, 2);
        System.out.println("3. " + nomArbre2 + " (Arbre 2)"
                + (!arbre2Dispo ? "  [VERROUILLE]" : actuelle == 2 ? "  [ACTIVE]" : ""));

        System.out.println("0. Annuler");
        System.out.print("Votre choix : ");

        switch (scanner.nextLine().trim()) {
            case "1" -> {
                joueur.setCompetenceSpecialeActive(0);
                System.out.println(">> Competence active : " + nomOriginal);
            }
            case "2" -> {
                if (!arbre1Dispo) {
                    System.out.println("Competence verrouillee — completez l'arbre 1 d'abord.");
                } else {
                    joueur.setCompetenceSpecialeActive(1);
                    System.out.println(">> Competence active : " + nomArbre1);
                }
            }
            case "3" -> {
                if (!arbre2Dispo) {
                    System.out.println("Competence verrouillee — completez l'arbre 2 d'abord.");
                } else {
                    joueur.setCompetenceSpecialeActive(2);
                    System.out.println(">> Competence active : " + nomArbre2);
                }
            }
            case "0" -> {}
            default  -> System.out.println("Choix invalide.");
        }
    }

    private String nomCompetenceActive(Personnage_principale joueur) {
        return switch (joueur.getCompetenceSpecialeActive()) {
            case 1  -> MenuAbilite.getNomCompetence(joueur.getChoixClasses(), 1);
            case 2  -> MenuAbilite.getNomCompetence(joueur.getChoixClasses(), 2);
            default -> joueur.getCompetencesChoisie() != null
                    ? joueur.getCompetencesChoisie()
                             .getNomsCompetences()[joueur.getChoixComp() - 1]
                    : "Aucune";
        };
    }

}