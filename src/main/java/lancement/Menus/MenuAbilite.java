package lancement.Menus;

import Joueur.ArbreCompetences;
import Joueur.NoeudArbre;
import Joueur.Personnage_principale;
import java.util.Scanner;
import lancement.GameContext;
import lancement.RangJoueur;

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
                    + (arbre.isNoeud10Arbre2Debloque() ? " [DÉBLOQUÉ : " + getNomCompetence(joueur.getChoixClasses(), 2) + "]" : ""));
            System.out.println("3. Arbre 3 — Nouvelle Spéciale"
                    + (!arbre.isArbre3Debloque()
                        ? " [VERROUILLÉ — terminez l'Arbre 2]"
                        : arbre.isNoeud10Arbre3Debloque()
                            ? " [DÉBLOQUÉ : " + getNomCompetence(joueur.getChoixClasses(), 3) + "] — Rang B !"
                            : ""));
            System.out.println("0. Retour");
            System.out.print("Votre choix : ");

            switch (scanner.nextLine().trim()) {
                case "1" -> afficherArbre(ctx, arbre, 1, scanner);
                case "2" -> afficherArbre(ctx, arbre, 2, scanner);
                case "3" -> {
                    if (!arbre.isArbre3Debloque())
                        System.out.println("L'arbre 3 se débloque en terminant l'Arbre 2.");
                    else
                        afficherArbre(ctx, arbre, 3, scanner);
                }
                case "0" -> retour = true;
                default  -> System.out.println("Choix invalide.");
            }
        }
    }

    private void afficherArbre(GameContext ctx,
                                ArbreCompetences arbre,
                                int numArbre,
                                Scanner scanner) {
        Personnage_principale joueur = ctx.joueur;
        boolean retour = false;

        while (!retour) {
            String nomArbre = switch (numArbre) {
                case 1 -> " — Voie du Combattant";
                case 2 -> " — Voie du Maître";
                default -> " — Voie de l'Ascension";
            };
            System.out.println("\n========================================");
            System.out.println("  ARBRE " + numArbre + nomArbre);
            System.out.println("========================================");
            System.out.println("Points disponibles : " + arbre.getPointsDisponibles());
            System.out.println();

            for (int i = 1; i <= 10; i++) {
                NoeudArbre n = getNoeud(arbre, numArbre, i);
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
                NoeudArbre n = getNoeud(arbre, numArbre, choix);

                if (n.getTypeBonus() == NoeudArbre.TypeBonus.COMPETENCE_SPECIALE) {
                    debloquerNoeudCompetence(ctx, arbre, numArbre, choix);
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

    private NoeudArbre getNoeud(ArbreCompetences arbre, int numArbre, int index) {
        return switch (numArbre) {
            case 1 -> arbre.getNoeud(index);
            case 2 -> arbre.getNoeudArbre2(index);
            default -> arbre.getNoeudArbre3(index);
        };
    }

    private void debloquerNoeudCompetence(GameContext ctx,
                                           ArbreCompetences arbre,
                                           int numArbre, int indexNoeud) {
        Personnage_principale joueur = ctx.joueur;
        NoeudArbre n = getNoeud(arbre, numArbre, indexNoeud);
        String nomComp = getNomCompetence(joueur.getChoixClasses(), numArbre);

        if (n.isDebloque()) {
            System.out.println("\n  " + nomComp + " est déjà débloquée et active.");
        } else {
            String resultat = arbre.tenterDebloquer(numArbre, indexNoeud);
            if (resultat.equals("OK")) {
                // Activer automatiquement la nouvelle compétence
                switch (numArbre) {
                    case 1 -> {
                        joueur.activerArbre1();
                        System.out.println(">> Nouvelle spéciale débloquée : " + nomComp + " !");
                        System.out.println("   Votre attaque spéciale est maintenant remplacée par " + nomComp + ".");
                    }
                    case 2 -> {
                        joueur.activerArbre2();
                        System.out.println(">> Nouvelle spéciale débloquée : " + nomComp + " !");
                        System.out.println("   Votre attaque ultime est maintenant remplacée par la spéciale " + nomComp + ".");
                    }
                    default -> {
                        System.out.println(">> Nouvelle spéciale débloquée : " + nomComp + " !");
                        RangJoueur rangJoueur = ctx.rangJoueur;
                        if (rangJoueur.getRang() == RangJoueur.Rang.C) {
                            rangJoueur.setRang(RangJoueur.Rang.B);
                            System.out.println(">> Félicitations ! L'Arbre 3 complété vous fait passer Rang B !");
                            System.out.println("   Multiplicateur de stats : x"
                                    + String.format("%.2f", rangJoueur.getMultiplicateur()));
                        }
                    }
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
