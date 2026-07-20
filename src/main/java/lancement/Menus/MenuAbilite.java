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
            System.out.println("            ARBRE DE COMPÉTENCES");
            System.out.println("========================================");
            System.out.println("Points disponibles : " + arbre.getPointsDisponibles());
            System.out.println();

            // Afficher les compétences actives
            String[] noms = joueur.getNomsAttaques();
            System.out.println("  Spéciale active : " + noms[1]);
            System.out.println("  Ultime  active  : " + noms[2]);
            System.out.println();

            System.out.println("1. Arbre 1 — Nouvelle Spéciale"
                    + (arbre.isNoeud10Debloque() ? " [DÉBLOQUÉ : " + getNomCompetence(joueur.getChoixClasses(), 1) + "]" : ""));
            System.out.println("2. Arbre 2 — Nouvel Ultime"
                    + (!arbre.isArbre2Debloque()
                        ? " [VERROUILLÉ — terminez le Chapitre 2 Elite]"
                        : arbre.isNoeud10Arbre2Debloque()
                            ? " [DÉBLOQUÉ : " + getNomCompetence(joueur.getChoixClasses(), 2) + "]"
                            : ""));
            System.out.println("0. Retour");
            System.out.print("Votre choix : ");

            switch (scanner.nextLine().trim()) {
                case "1" -> afficherArbre(joueur, arbre, 1, scanner);
                case "2" -> {
                    if (!arbre.isArbre2Debloque())
                        System.out.println("L'arbre 2 se débloque en terminant le Chapitre 2 Elite.");
                    else
                        afficherArbre(joueur, arbre, 2, scanner);
                }
                case "0" -> retour = true;
                default  -> System.out.println("Choix invalide.");
            }
        }
    }

    private void afficherArbre(Personnage_principale joueur,
                                ArbreCompetences arbre,
                                int numArbre,
                                Scanner scanner) {
        boolean retour = false;

        while (!retour) {
            System.out.println("\n========================================");
            System.out.println("  ARBRE " + numArbre
                    + (numArbre == 1 ? " — Voie du Combattant" : " — Voie du Maître"));
            System.out.println("========================================");
            System.out.println("Points disponibles : " + arbre.getPointsDisponibles());
            System.out.println();

            for (int i = 1; i <= 10; i++) {
                NoeudArbre n = numArbre == 1 ? arbre.getNoeud(i) : arbre.getNoeudArbre2(i);
                String etat = n.isDebloque() ? "[OK]" : "[  ]";
                String cout = n.isDebloque() ? "      " : "(" + n.getCoutPoints() + " pts)";
                System.out.println(etat + " Nœud " + i + " — " + n.getDescription() + "  " + cout);
            }

            System.out.println();
            System.out.println("Entrez le numéro du nœud à débloquer (0 pour revenir) :");
            int choix;
            try {
                choix = Integer.parseInt(scanner.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Entrée invalide.");
                continue;
            }

            if (choix == 0) {
                retour = true;
            } else if (choix < 1 || choix > 10) {
                System.out.println("Nœud invalide.");
            } else {
                NoeudArbre n = numArbre == 1 ? arbre.getNoeud(choix) : arbre.getNoeudArbre2(choix);

                if (n.getTypeBonus() == NoeudArbre.TypeBonus.COMPETENCE_SPECIALE) {
                    debloquerNoeudCompetence(joueur, arbre, numArbre, choix);
                } else {
                    String resultat = arbre.tenterDebloquer(numArbre, choix);
                    if (resultat.equals("OK")) {
                        System.out.println(">> Nœud " + choix + " débloqué : " + n.getDescription() + " !");
                        System.out.println("   Points restants : " + arbre.getPointsDisponibles());
                    } else {
                        System.out.println(resultat);
                    }
                }
            }
        }
    }

    private void debloquerNoeudCompetence(Personnage_principale joueur,
                                           ArbreCompetences arbre,
                                           int numArbre, int indexNoeud) {
        NoeudArbre n = numArbre == 1 ? arbre.getNoeud(indexNoeud) : arbre.getNoeudArbre2(indexNoeud);
        String nomComp = getNomCompetence(joueur.getChoixClasses(), numArbre);

        if (n.isDebloque()) {
            System.out.println("\n  " + nomComp + " est déjà débloquée et active.");
        } else {
            String resultat = arbre.tenterDebloquer(numArbre, indexNoeud);
            if (resultat.equals("OK")) {
                // Activer automatiquement la nouvelle compétence
                if (numArbre == 1) {
                    joueur.activerArbre1();
                    System.out.println(">> Nouvelle spéciale débloquée : " + nomComp + " !");
                    System.out.println("   Votre attaque spéciale est maintenant remplacée par " + nomComp + ".");
                    System.out.println("   Terminez le Chapitre 2 Elite pour débloquer l'Arbre 2.");
                } else {
                    joueur.activerArbre2();
                    System.out.println(">> Nouvel ultime débloqué : " + nomComp + " !");
                    System.out.println("   Votre attaque ultime est maintenant remplacée par " + nomComp + ".");
                }
                System.out.println("   Points restants : " + arbre.getPointsDisponibles());
            } else {
                System.out.println(resultat);
            }
        }
    }

    // ── Noms des compétences d'arbre par classe ───────────────────────────
    public static String getNomCompetence(String classe, int arbre) {
        return Joueur.Personnage_principale.getNomCompetenceArbre(classe, arbre);
    }
}