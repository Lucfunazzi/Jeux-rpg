package lancement.Menus;

import Joueur.ArbreCompetences;
import Joueur.NoeudArbre;
import Joueur.Personnage_principale;
import java.util.Scanner;
import lancement.GameContext;

public class MenuAbilite {

    public void afficher(GameContext ctx, Scanner scanner) {
        Personnage_principale joueur = ctx.joueur;
        ArbreCompetences arbre = joueur.getArbreCompetences();
        boolean retour = false;

        while (!retour) {
            System.out.println("\n========================================");
            System.out.println("            MENU ABILITE");
            System.out.println("========================================");
            System.out.println("Points disponibles : " + arbre.getPointsDisponibles());
            System.out.println();
            System.out.println("1. Arbre 1" + (arbre.isNoeud10Debloque() ? " [COMPLETE]" : ""));
            System.out.println("2. Arbre 2"
                    + (!arbre.isArbre2Debloque() ? " [VERROUILLE — terminez le Chapitre 2 Elite]"
                    : arbre.isNoeud10Arbre2Debloque() ? " [COMPLETE]" : ""));
            System.out.println("0. Retour");
            System.out.print("Votre choix : ");

            switch (scanner.nextLine().trim()) {
                case "1" -> afficherArbre(joueur, arbre, 1, scanner);
                case "2" -> {
                    if (!arbre.isArbre2Debloque())
                        System.out.println("L'arbre 2 se debloque en terminant le Chapitre 2 Elite.");
                    else
                        afficherArbre(joueur, arbre, 2, scanner);
                }
                case "0" -> retour = true;
                default  -> System.out.println("Choix invalide.");
            }
        }
    }

    // ── Affichage générique d'un arbre ────────────────────────────────────
    private void afficherArbre(Personnage_principale joueur,
                                ArbreCompetences arbre,
                                int numArbre,
                                Scanner scanner) {
        boolean retour = false;

        while (!retour) {
            System.out.println("\n========================================");
            System.out.println("         ARBRE " + numArbre
                    + (numArbre == 1
                        ? " — Voie du Combattant"
                        : " — Voie du Maitre"));
            System.out.println("========================================");
            System.out.println("Points disponibles : " + arbre.getPointsDisponibles());
            System.out.println();

            for (int i = 1; i <= 10; i++) {
                NoeudArbre n = numArbre == 1
                        ? arbre.getNoeud(i)
                        : arbre.getNoeudArbre2(i);
                String etat = n.isDebloque() ? "[OK]" : "[  ]";
                String cout = n.isDebloque() ? "      " : "(" + n.getCoutPoints() + " pts)";
                System.out.println(etat + " Noeud " + i + " — " + n.getDescription()
                        + "  " + cout);
            }

            System.out.println();
            System.out.println("Entrez le numero du noeud a debloquer (0 pour revenir) :");
            int choix;
            try {
                choix = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Entree invalide.");
                continue;
            }

            if (choix == 0) {
                retour = true;
            } else if (choix < 1 || choix > 10) {
                System.out.println("Noeud invalide.");
            } else {
                NoeudArbre n = numArbre == 1
                        ? arbre.getNoeud(choix)
                        : arbre.getNoeudArbre2(choix);

                if (n.getTypeBonus() == NoeudArbre.TypeBonus.COMPETENCE_SPECIALE) {
                    afficherChoixNoeudSpecial(joueur, arbre, numArbre, choix, scanner);
                } else {
                    String resultat = arbre.tenterDebloquer(numArbre, choix);
                    if (resultat.equals("OK")) {
                        System.out.println(">> Noeud " + choix + " debloque : "
                                + n.getDescription() + " !");
                        System.out.println("   Points restants : "
                                + arbre.getPointsDisponibles());
                    } else {
                        System.out.println(resultat);
                    }
                }
            }
        }
    }

    // ── Gestion du nœud compétence spéciale ──────────────────────────────
    private void afficherChoixNoeudSpecial(Personnage_principale joueur,
                                            ArbreCompetences arbre,
                                            int numArbre,
                                            int indexNoeud,
                                            Scanner scanner) {
        NoeudArbre n = numArbre == 1
                ? arbre.getNoeud(indexNoeud)
                : arbre.getNoeudArbre2(indexNoeud);

        String nomComp = getNomCompetence(joueur.getChoixClasses(), numArbre);

        if (n.isDebloque()) {
            // Nœud déjà débloqué → rediriger vers le menu de sélection de compétence
            // (géré dans MenuFormation, on informe simplement le joueur)
            System.out.println("\n--- Competence speciale (Arbre " + numArbre + ") ---");
            System.out.println("  " + nomComp + " est deja disponible.");
            System.out.println("  Rendez-vous dans le Menu Formation pour changer");
            System.out.println("  votre attaque speciale active.");
        } else {
            // Tenter de débloquer
            String resultat = arbre.tenterDebloquer(numArbre, indexNoeud);
            if (resultat.equals("OK")) {
                System.out.println(">> Noeud " + indexNoeud + " debloque : "
                        + nomComp + " !");
                if (numArbre == 1)
                    System.out.println(">> Arbre 1 complete ! Terminez le Chapitre 2 Elite pour debloquer l'Arbre 2.");
                System.out.println("   Rendez-vous dans le Menu Formation pour");
                System.out.println("   l'activer comme attaque speciale.");
                System.out.println("   Points restants : " + arbre.getPointsDisponibles());
            } else {
                System.out.println(resultat);
            }
        }
    }

    // ── Noms des compétences par classe et arbre ──────────────────────────
    public static String getNomCompetence(String classe, int arbre) {
        return switch (classe) {
            case "Mage" -> arbre == 1 ? "Nova Arcanique"       : "Tempete Cristalline";
            case "Ninja"-> arbre == 1 ? "Substitution Meurtriere" : "Tourbillon de Lames";
            case "Guerrier" -> arbre == 1 ? "Brise-Armure"     : "Frappe Sismique";
            default     -> "Competence speciale " + arbre;
        };
    }
}